package dev.misei.einfachml.controller;

import dev.misei.einfachml.neuralservice.DataService;
import dev.misei.einfachml.neuralservice.ComputeService;
import dev.misei.einfachml.repository.model.DataPair;
import dev.misei.einfachml.repository.model.PredictedData;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/prediction")
@Slf4j
public class PredictionAPI {

    private DataService dataService;
    private ComputeService neuralService;

    @GetMapping("/compute")
    public Mono<Void> compute(@RequestParam String topic, @RequestParam UUID networkId, @RequestParam int epochs, @RequestParam(required = false) Long createdAtStart,
                              @RequestParam(required = false) Long createdAtEnd, @RequestParam(required = false) Integer lastAmount) {
        return dataService.retrieve(topic, createdAtStart, createdAtEnd, lastAmount)
                .as(dataPairList -> neuralService.computeElasticAsync(networkId, dataPairList, epochs));
    }

    @PostMapping("/predict")
    public Flux<PredictedData> predict(@RequestParam UUID networkId, @RequestBody Flux<DataPair> dataSet) {
        return neuralService.predictElasticAsync(networkId, dataSet);
    }
}
