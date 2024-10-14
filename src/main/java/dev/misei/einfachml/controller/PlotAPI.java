package dev.misei.einfachml.controller;

import dev.misei.einfachml.controller.dto.PlotBoard;
import dev.misei.einfachml.neuralservice.NeuralService;
import dev.misei.einfachml.repository.model.PredictedData;
import dev.misei.einfachml.util.ResponseUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping("/api/plot")
public class PlotAPI {

    private NeuralService neuralService;

    @GetMapping("/fetchPlotWithDefinition")
    public CompletableFuture<ResponseEntity<PlotBoard>> fetchPlot(@RequestParam UUID networkId,
                                                                  @RequestParam(required = false) Integer lastEpochAmount) {
        return neuralService.getAllPredictionsByNetwork(networkId, lastEpochAmount)
                .thenApply(predictedData -> {
                    return ResponseUtil.entityResponse(() -> {
                        PlotBoard neuralBoard = new PlotBoard();
                        //TODO: LAST EPOCH IS +1! Fix or empty!
                        int lastEpoch = predictedData.getLast().getEpochHappened();

                        neuralBoard.setLastEpochPredicted(predictedData.stream()
                                .filter(predictedData1 -> predictedData1.getEpochHappened() == lastEpoch).toList());

                        neuralBoard.setMseErrors(predictedData.stream()
                                .collect(Collectors.groupingBy(PredictedData::getEpochHappened)).values().stream().map(
                                        predictedData12 -> predictedData12.stream().mapToDouble(PredictedData::getMseError)
                                                .average().orElse(0d)).toList());

                        return neuralBoard;
                    });
                })
                .exceptionally(ResponseUtil::responseEntityFailed);
    }
}
