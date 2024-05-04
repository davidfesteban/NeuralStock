package dev.misei.einfachstonks.stonkservice.bridge.yahoo;

import dev.misei.einfachstonks.stonkservice.dto.FinancialDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

@Component
@AllArgsConstructor
public class YahooFinanceBridge {

    private RestTemplate restTemplate;

    public List<FinancialDTO> downloadCsvData(String symbol, long period1, long period2) {
        URI uri = UriComponentsBuilder.fromHttpUrl("https://query1.finance.yahoo.com/v7/finance/download/" + symbol)
                .queryParam("period1", period1)
                .queryParam("period2", period2)
                .queryParam("interval", "1d")
                .queryParam("events", "history")
                .queryParam("includeAdjustedClose", true)
                .build()
                .toUri();

        try {
            return parseCSV(restTemplate.getForObject(uri, String.class));
        } catch (RestClientException e) {
            throw new IllegalArgumentException("Rest failed: ", e);
        }
    }

    private List<FinancialDTO> parseCSV(String csv) {
        var csvSplit = new ArrayList<>(Arrays.asList(csv.split("\n")));
        csvSplit.remove(0);
        return csvSplit.stream().map(new Function<String, FinancialDTO>() {
            @Override
            public FinancialDTO apply(String s) {
                String[] data = s.split(",");
                var date = data[0].split("-");
                return new FinancialDTO(
                        LocalDate.of(Integer.parseInt(date[0]), Integer.parseInt(date[1]), Integer.parseInt(date[2])),
                        Double.parseDouble(data[1]),
                        Double.parseDouble(data[4]),
                        Double.parseDouble(data[2]),
                        Double.parseDouble(data[3]),
                        Long.parseLong(data[6])
                );
            }
        }).toList();
    }
}

