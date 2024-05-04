package dev.misei.einfachstonks.stonkservice;

import dev.misei.einfachstonks.stonkservice.bridge.yahoo.YahooFinanceBridge;
import dev.misei.einfachstonks.stonkservice.repository.ETFCompositeHistoryRepository;
import dev.misei.einfachstonks.stonkservice.repository.ETFHistoryRepository;
import dev.misei.einfachstonks.stonkservice.repository.ETFIdentityRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StonkController {



    @GetMapping
    public ResponseEntity<String> foo() {
        //yahooFinanceBridge.downloadCsvData("AAAU",1683057410, 1714679810, "1d", "history", true)
        return null;
    }

}
