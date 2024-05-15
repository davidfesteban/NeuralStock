package dev.misei.einfachstonks.stonkservice.model;

import dev.misei.einfachstonks.stonkservice.bridge.yahoo.YahooFinanceBridge;
import dev.misei.einfachstonks.stonkservice.dto.FinancialDTO;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.stream.Stream;

public enum ETFBridgeType {
    YAHOO {
        @Override
        public Stream<FinancialDTO> downloadData(String symbol, LocalDate fromDate, LocalDate toDate, RestTemplate restTemplate) {
            return YahooFinanceBridge.downloadCsvData(symbol, fromDate.atStartOfDay(ZoneId.systemDefault()).toEpochSecond(),
                    toDate.atStartOfDay(ZoneId.systemDefault()).toEpochSecond(), restTemplate);
        }
    },
    JUSTETF {
        @Override
        public Stream<FinancialDTO> downloadData(String symbol, LocalDate fromDate, LocalDate toDate, RestTemplate restTemplate) {
            return Stream.of();
        }
    };

    public abstract Stream<FinancialDTO> downloadData(String symbol, LocalDate fromDate, LocalDate toDate, RestTemplate restTemplate);
}
