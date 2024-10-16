package dev.misei.einfachml.controller;

import dev.misei.einfachml.repository.MetricsRepository;
import dev.misei.einfachml.repository.model.MSEData;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.UUID;

@RestController
@RequestMapping("/api/metrics")
@AllArgsConstructor
@Slf4j
public class MetricsAPI {

    private final MetricsRepository metricsRepository;

    @GetMapping("/mseData")
    public Flux<MSEData> getPredictions(@RequestParam UUID networkId) {
        return metricsRepository.findByNetworkIdOrderByEpochHappenedAsc(networkId);
    }

}
