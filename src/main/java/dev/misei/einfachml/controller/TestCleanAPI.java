package dev.misei.einfachml.controller;

import dev.misei.einfachml.repository.PredictedDataRepositoryPerformance;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@AllArgsConstructor
@RequestMapping("/api/config")
@Slf4j
public class TestCleanAPI {

    private PredictedDataRepositoryPerformance predictedDataRepositoryPerformance;

    @GetMapping("/boom")
    public Mono<Void> boom() {
        return predictedDataRepositoryPerformance.deleteAll();
    }
}