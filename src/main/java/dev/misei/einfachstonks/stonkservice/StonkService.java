package dev.misei.einfachstonks.stonkservice;

import dev.misei.einfachstonks.stonkservice.dto.ETFDetailDTO;
import dev.misei.einfachstonks.stonkservice.model.*;
import dev.misei.einfachstonks.stonkservice.repository.ETFCompositeHistoryRepository;
import dev.misei.einfachstonks.stonkservice.repository.ETFHistoryRepository;
import dev.misei.einfachstonks.stonkservice.repository.ETFIdentityRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

import static dev.misei.einfachstonks.stonkservice.model.HistoryToCompositeUtil.*;

@Service
@AllArgsConstructor
public class StonkService {

    private ETFHistoryRepository etfHistoryRepository;
    private ETFCompositeHistoryRepository etfCompositeHistoryRepository;
    private ETFIdentityRepository etfIdentityRepository;
    private RestTemplate restTemplate;

    public UUID createETFTracker(String etfName,
                                 ETFBridgeType etfBridgeType,
                                 String ticker,
                                 ETFType etfType) {

        var identity = new ETFIdentity(UUID.randomUUID(), etfName, etfBridgeType, ticker, etfType, null);
        if (!etfIdentityRepository.existsByEtfBridgeTypeAndTicker(identity.getEtfBridgeType(), identity.getTicker())) {
            etfIdentityRepository.save(identity);
        }

        return identity.getInternalNameId();
    }

    public UUID track(UUID internalNameId, boolean override) {
        var identity = etfIdentityRepository.findByInternalNameId(internalNameId);
        if (override) {
            identity.setLastUpdate(LocalDate.of(1970, 1, 1));
        }

        var fromDate = Optional.ofNullable(identity.getLastUpdate()).orElse(LocalDate.of(1970, 1, 1));
        var nowDate = LocalDate.now();
        var financialData = identity.getEtfBridgeType().downloadData(identity.getTicker(), fromDate, nowDate, restTemplate);

        //Clean History and Composite (all composites must be recalculated, that is why you delete from old to NOW)
        var deleted = etfHistoryRepository.deleteByInternalNameIdAndDayPrecisionBetween(internalNameId, fromDate, nowDate);
        deleted.forEach(etfHistory -> etfCompositeHistoryRepository.deleteByEtfCompositeId(etfHistory.getRefEtfComposite()));

        //Remap to ETF
        var etfHistoryList = financialData.map(financialDTO ->
                        new ETFHistory(internalNameId, UUID.randomUUID(), UUID.randomUUID(), financialDTO.date(),
                                financialDTO.open(), financialDTO.close(), financialDTO.volume(), financialDTO.high(), financialDTO.low()))
                .sorted().toList();

        //Update real last update, Save each history
        etfHistoryList.stream().sorted().forEachOrdered(new Consumer<ETFHistory>() {
            @Override
            public void accept(ETFHistory etfHistory) {
                if (identity.getLastUpdate() == null || identity.getLastUpdate().isBefore(etfHistory.getDayPrecision())) {
                    identity.setLastUpdate(etfHistory.getDayPrecision());
                }
            }
        });

        etfHistoryRepository.saveAll(etfHistoryList);

        //Save composite history
        etfCompositeHistoryRepository.saveAll(etfHistoryList.stream().sorted().map(new Function<ETFHistory, ETFCompositeHistory>() {
            @Override
            public ETFCompositeHistory apply(ETFHistory etfHistory) {
                return calculateCompositeFromHistory(internalNameId, etfHistory,
                        etfHistoryList.stream().filter(originHistory -> originHistory.getDayPrecision().isBefore(etfHistory.getDayPrecision()) ||
                                originHistory.getDayPrecision().isEqual(etfHistory.getDayPrecision())).toList(),
                        etfHistoryList.stream().filter(originHistory -> originHistory.getDayPrecision().isEqual(etfHistory.getDayPrecision().plusDays(1))).findFirst(),
                        etfHistoryList.stream().filter(originHistory -> originHistory.getDayPrecision().isEqual(etfHistory.getDayPrecision().plusWeeks(1))).findFirst(),
                        etfHistoryList.stream().filter(originHistory -> originHistory.getDayPrecision().isEqual(etfHistory.getDayPrecision().plusMonths(1))).findFirst());
            }
        }).toList());

        //Update the identity
        etfIdentityRepository.deleteByInternalNameId(internalNameId);
        return etfIdentityRepository.save(identity).getInternalNameId();
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
                future1Day.map(ETFHistory::getPriceClose).orElse(null),
                future1Week.map(ETFHistory::getPriceClose).orElse(null),
                future1Month.map(ETFHistory::getPriceClose).orElse(null));
    }

    public MultiValueMap<ETFType, ETFDetailDTO> returnAll() {
        MultiValueMap<ETFType, ETFDetailDTO> result = new LinkedMultiValueMap<>();

        etfIdentityRepository.findAll().forEach(etfIdentity -> {
            var key = etfIdentity.getInternalNameId();
            etfHistoryRepository.findByInternalNameId(key).stream().sorted().forEachOrdered(new Consumer<ETFHistory>() {
                @Override
                public void accept(ETFHistory etfHistory) {
                    result.add(etfIdentity.getEtfType(), new ETFDetailDTO(etfHistory, etfCompositeHistoryRepository.findByEtfCompositeId(etfHistory.getRefEtfComposite())));
                }
            });
        });

        return result;
    }


    public MultiValueMap<ETFType, ETFDetailDTO> singleSnapshot(LocalDate when) {
        MultiValueMap<ETFType, ETFDetailDTO> result = new LinkedMultiValueMap<>();

        etfIdentityRepository.findAll().forEach(new Consumer<ETFIdentity>() {
            @Override
            public void accept(ETFIdentity etfIdentity) {
                var key = etfIdentity.getInternalNameId();
                var etfHistory = etfHistoryRepository.findByInternalNameIdAndDayPrecision(key, when);
                result.add(etfIdentity.getEtfType(), new ETFDetailDTO(etfHistory, etfCompositeHistoryRepository.findByEtfCompositeId(etfHistory.getRefEtfComposite())));
            }
        });

        return result;
    }

}
