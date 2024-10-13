package dev.misei.einfachml.controller;

import dev.misei.einfachml.neuralservice.DataService;
import dev.misei.einfachml.neuralservice.NeuralService;
import dev.misei.einfachml.repository.model.DataPair;
import dev.misei.einfachml.repository.model.PredictedData;
import dev.misei.einfachml.util.ResponseUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@AllArgsConstructor
@RequestMapping("/api/prediction")
public class PredictionAPI {

    private DataService dataService;
    private NeuralService neuralService;

    @GetMapping("/compute")
    public CompletableFuture<Void> compute(@RequestParam UUID networkId, @RequestParam int epochs, @RequestParam(required = false) Long createdAtStart,
                                           @RequestParam(required = false) Long createdAtEnd, @RequestParam(required = false) Integer lastAmount) {
        return dataService.retrieve(networkId, createdAtStart, createdAtEnd, lastAmount)
                .thenAccept(dataPairList -> neuralService.computeElasticAsync(networkId, dataPairList, epochs));
    }

    @PostMapping("/predict")
    public CompletableFuture<ResponseEntity<List<PredictedData>>> predict(@RequestParam UUID networkId, @RequestBody List<DataPair> dataSet) {
        return neuralService.predictElasticAsync(networkId, dataSet)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ResponseUtil::responseEntityFailed);
    }

    @GetMapping("/getPredictionsWithDefinition")
    public CompletableFuture<ResponseEntity<List<PredictedData>>> getPredictions(@RequestParam UUID networkId, @RequestParam(required = false) Integer lastEpochAmount) {
        return neuralService.getAllPredictionsByNetwork(networkId, lastEpochAmount)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ResponseUtil::responseEntityFailed);
    }
}
