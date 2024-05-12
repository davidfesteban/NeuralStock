package dev.misei.einfachstonks.stonkservice;

import dev.misei.einfachstonks.stonkservice.bridge.yahoo.YahooFinanceBridge;
import dev.misei.einfachstonks.stonkservice.dto.EtfDto;
import dev.misei.einfachstonks.stonkservice.dto.FinancialDTO;
import dev.misei.einfachstonks.stonkservice.model.ETFCompositeHistory;
import dev.misei.einfachstonks.stonkservice.model.ETFHistory;
import dev.misei.einfachstonks.stonkservice.model.ETFIdentity;
import dev.misei.einfachstonks.stonkservice.model.ETFType;
import dev.misei.einfachstonks.stonkservice.repository.ETFCompositeHistoryRepository;
import dev.misei.einfachstonks.stonkservice.repository.ETFHistoryRepository;
import dev.misei.einfachstonks.stonkservice.repository.ETFIdentityRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;

import static dev.misei.einfachstonks.stonkservice.model.HistoryToCompositeUtil.*;

@Service
@AllArgsConstructor
public class StonkService {

    private YahooFinanceBridge yahooFinanceBridge;

    private ETFHistoryRepository etfHistoryRepository;
    private ETFCompositeHistoryRepository etfCompositeHistoryRepository;
    private ETFIdentityRepository etfIdentityRepository;

    public UUID createETFTracker(String etfName,
                                 String isinJustEtf,
                                 String wknNameJustEtf,
                                 String ticketYahoo,
                                 ETFType etfType) {
        Assert.isTrue((isinJustEtf != null) || (wknNameJustEtf != null) || (ticketYahoo != null), "One reference must be non null");
        if (isinJustEtf != null && etfIdentityRepository.existsByIsinJustEtfIgnoreCase(isinJustEtf)) {
            return null;
        }

        if (wknNameJustEtf != null && etfIdentityRepository.existsByWknNameJustEtfIgnoreCase(wknNameJustEtf)) {
            return null;
        }

        if (ticketYahoo != null && etfIdentityRepository.existsByTicketYahooIgnoreCase(ticketYahoo)) {
            return null;
        }

        var identity = new ETFIdentity(UUID.randomUUID(), etfName, isinJustEtf, wknNameJustEtf, ticketYahoo, etfType.name(), null);
        etfIdentityRepository.save(identity);
        return identity.getInternalNameId();
    }

    public void trackETF(UUID internalNameId, LocalDate fromDate) {
        var now = LocalDate.now();
        List<FinancialDTO> financialData = null;
        var identity = etfIdentityRepository.findByInternalNameId(internalNameId);
        //TODO: Refactor to injection Interface
        //TODO: Refactor to async
        if (identity.getTicketYahoo() != null) {
            financialData = yahooFinanceBridge.downloadCsvData(identity.getTicketYahoo(), fromDate.atStartOfDay(ZoneId.systemDefault()).toEpochSecond(), LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toEpochSecond());
        }

        var deleted = etfHistoryRepository.deleteByInternalNameIdAndDayPrecisionBetween(internalNameId, fromDate, now);
        //If date is not today, we need to reupdate all the composites that are about of LocalDate.toDate(). Therefor, we will always update until today
        deleted.forEach(etfHistory -> etfCompositeHistoryRepository.deleteByEtfCompositeId(etfHistory.getRefEtfComposite()));

        AtomicReference<LocalDate> lastUpdate = new AtomicReference<>(LocalDate.of(1600, 1, 1));
        financialData.stream().map(new Function<FinancialDTO, ETFHistory>() {
            @Override
            public ETFHistory apply(FinancialDTO financialDTO) {
                return new ETFHistory(internalNameId, UUID.randomUUID(), UUID.randomUUID(), financialDTO.date(),
                        financialDTO.open(), financialDTO.close(), financialDTO.volume(), financialDTO.high(), financialDTO.low());
            }
        }).sorted().forEachOrdered(new Consumer<ETFHistory>() {
            @Override
            public void accept(ETFHistory etfHistory) {
                if (lastUpdate.get().isBefore(etfHistory.getDayPrecision())) {
                    lastUpdate.set(etfHistory.getDayPrecision());
                }
                etfHistoryRepository.save(etfHistory);
                etfCompositeHistoryRepository.save(calculateCompositeFromHistory(internalNameId, etfHistory,
                        etfHistoryRepository.findByInternalNameIdAndDayPrecisionLessThanEqual(internalNameId, etfHistory.getDayPrecision()),
                        etfHistoryRepository.findByInternalNameIdAndDayPrecision(internalNameId, etfHistory.getDayPrecision().plusDays(1)),
                        etfHistoryRepository.findByInternalNameIdAndDayPrecision(internalNameId, etfHistory.getDayPrecision().plusWeeks(1)),
                        etfHistoryRepository.findByInternalNameIdAndDayPrecision(internalNameId, etfHistory.getDayPrecision().plusMonths(1))))
                ;
            }
        });

        var etfIdentity = etfIdentityRepository.findByInternalNameId(internalNameId);
        etfIdentity.setLastUpdate(lastUpdate.get());
        etfIdentityRepository.deleteByInternalNameId(etfIdentity.getInternalNameId());
        etfIdentityRepository.save(etfIdentity);
    }

    private ETFCompositeHistory calculateCompositeFromHistory(UUID internalNameId, ETFHistory etfHistory, List<ETFHistory> pastEtfHistories,
                                                              Optional<ETFHistory> future1Day,
                                                              Optional<ETFHistory> future1Week,
                                                              Optional<ETFHistory> future1Month
    ) {
        return new ETFCompositeHistory(internalNameId,
                etfHistory.getRefEtfComposite(),
                etfHistory.getHistoryId(),
                calculate52WeekLow(pastEtfHistories),
                calculate52WeekHigh(pastEtfHistories),
                calculateAverageVolume(pastEtfHistories),
                calculateETFVolatility(pastEtfHistories),
                future1Day.orElse(new ETFHistory()).getPriceClose(),
                future1Week.orElse(new ETFHistory()).getPriceClose(),
                future1Month.orElse(new ETFHistory()).getPriceClose());
    }

    public void refreshETFSinceLast(UUID internalNameId) {
        trackETF(internalNameId, etfIdentityRepository.findByInternalNameId(internalNameId).getLastUpdate().plusDays(1));
    }

    public MultiValueMap<ETFType, EtfDto> snapshot(LocalDate from, LocalDate to) {
        MultiValueMap<ETFType, EtfDto> result = new LinkedMultiValueMap<>();

        etfIdentityRepository.findAll().forEach(new Consumer<ETFIdentity>() {
            @Override
            public void accept(ETFIdentity etfIdentity) {
                var key = etfIdentity.getInternalNameId();
                etfHistoryRepository.findByInternalNameId(key).stream().sorted().forEachOrdered(new Consumer<ETFHistory>() {
                    @Override
                    public void accept(ETFHistory etfHistory) {
                        result.add(ETFType.valueOf(etfIdentity.getEtfType()), new EtfDto(etfHistory, etfCompositeHistoryRepository.findByEtfCompositeId(etfHistory.getRefEtfComposite())));
                    }
                });
            }
        });

        return result;
    }

}
