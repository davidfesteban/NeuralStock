package dev.misei.einfachstonks.stonkservice.bridge.yahoo;

import dev.misei.einfachstonks.stonkservice.dto.FinancialDTO;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class YahooFinanceBridge {

    public static Stream<FinancialDTO> downloadCsvData(String symbol, long period1, long period2, RestTemplate restTemplate) {
        URI uri = UriComponentsBuilder.fromHttpUrl("https://query1.finance.yahoo.com/v7/finance/download/" + symbol)
                .queryParam("period1", period1)
                .queryParam("period2", period2)
                .queryParam("interval", "1d")
                .queryParam("events", "history")
                .queryParam("includeAdjustedClose", true)
                .build()
                .toUri();

        return cleanStream(parseCSV(restTemplate.getForObject(uri, String.class)));
    }

    private static Stream<FinancialDTO> parseCSV(String csv) {
        var csvSplit = new ArrayList<>(Arrays.asList(csv.split("\n")));
        csvSplit.remove(0);
        return csvSplit.stream().map(s -> {
            String[] data = s.split(",");
            var date = data[0].split("-");
            return new FinancialDTO(
                    LocalDate.of(Integer.parseInt(date[0]), Integer.parseInt(date[1]), Integer.parseInt(date[2])),
                    data[1].equalsIgnoreCase("null") ? 0d : Double.parseDouble(data[1]),
                    data[4].equalsIgnoreCase("null") ? 0d : Double.parseDouble(data[4]),
                    data[2].equalsIgnoreCase("null") ? 0d : Double.parseDouble(data[2]),
                    data[3].equalsIgnoreCase("null") ? 0d : Double.parseDouble(data[3]),
                    data[6].equalsIgnoreCase("null") ? 0L : Long.parseLong(data[6])
            );
        });
    }

    private static Stream<FinancialDTO> cleanStream(Stream<FinancialDTO> dirtyRecords) {
        List<FinancialDTO> closeOpenMarketRecords = new ArrayList<>();
        List<FinancialDTO> validRecords = dirtyRecords.filter(FinancialDTO::isValid).sorted().toList();

        FinancialDTO prevDTO = null;

        for (FinancialDTO financialDTO : validRecords) {
            if (financialDTO.isComplete()) {
                if (prevDTO != null) {
                    LocalDate nextDate = prevDTO.date().plusDays(1);
                    while (nextDate.isBefore(financialDTO.date())) {
                        prevDTO = prevDTO.withDate(nextDate)
                                .withHigh(prevDTO.close())
                                .withLow(prevDTO.close())
                                .withOpen(prevDTO.close())
                                .withVolume(0);

                        closeOpenMarketRecords.add(prevDTO);
                        nextDate = prevDTO.date().plusDays(1);
                    }
                }

                prevDTO = financialDTO;
                closeOpenMarketRecords.add(financialDTO);
            } else {
                System.out.println("Dropping not completed record");
            }
        }

        return closeOpenMarketRecords.stream();
    }
}

