package dev.misei.einfachml.controller;

import dev.misei.einfachml.neuralservice.DataService;
import dev.misei.einfachml.repository.NetworkBoardRepository;
import dev.misei.einfachml.repository.model.DataPair;
import dev.misei.einfachml.repository.model.NetworkBoard;
import dev.misei.einfachml.util.ResponseUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import static dev.misei.einfachml.util.ResponseUtil.entityResponse;

@RestController
@AllArgsConstructor
@RequestMapping("/api/dataset")
public class DataSetAPI {

    private DataService dataService;
    private NetworkBoardRepository networkBoardRepository;

    @PostMapping("/add")
    public Mono<Void> includeDataSet(@RequestParam UUID networkId, @RequestBody List<DataPair> dataSet) {
        return networkBoardRepository.findById(networkId)
                .flatMap(networkBoard -> dataService.includeDataset(networkBoard, Flux.fromIterable(dataSet)))
                .then();
    }

    @PostMapping("/remove")
    public Mono<Void> removeDataSet(@RequestBody List<UUID> dataSetUUID) {
        return dataService.deleteDataSet(Flux.fromIterable(dataSetUUID));
    }

    @GetMapping("/getAll")
    public Flux<DataPair> getAll(@RequestParam UUID networkId) {
        return dataService.retrieveByPage(networkId);
    }

}
