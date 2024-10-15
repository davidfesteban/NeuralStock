package dev.misei.einfachml.controller;

import dev.misei.einfachml.neuralservice.DataService;
import dev.misei.einfachml.repository.DataPairRepository;
import dev.misei.einfachml.repository.NetworkBoardRepository;
import dev.misei.einfachml.repository.PredictedDataRepositoryPerformance;
import dev.misei.einfachml.repository.model.DataPair;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;

@RestController
@AllArgsConstructor
@RequestMapping("/api/config")
@Slf4j
public class TestCleanAPI {

    private NetworkBoardRepository networkBoardRepository;
    private DataPairRepository dataPairRepository;
    private PredictedDataRepositoryPerformance predictedDataRepositoryPerformance;

    @GetMapping("/boom")
    public Mono<Void> boom() {
        return predictedDataRepositoryPerformance.deleteAll();
    }
}