package dev.misei.einfachml.controller;

import dev.misei.einfachml.neuralservice.DataService;
import dev.misei.einfachml.repository.NetworkBoardRepository;
import dev.misei.einfachml.repository.model.DataPair;
import dev.misei.einfachml.util.ResponseUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static dev.misei.einfachml.util.ResponseUtil.entityResponse;

@RestController
@AllArgsConstructor
@RequestMapping("/api/dataset")
public class DataSetAPI {

    private DataService dataService;
    private NetworkBoardRepository networkBoardRepository;

    @PostMapping("/add")
    public ResponseEntity<Void> includeDataSet(@RequestParam UUID networkId, @RequestBody List<DataPair> dataSet) {
        return entityResponse(() -> {
            var networkBoard = networkBoardRepository.findById(networkId)
                    .orElseThrow(() -> new IllegalArgumentException("Network UUID not found"));

            dataService.includeDataset(networkBoard, dataSet);

            return null;
        });
    }

    @PostMapping("/remove")
    public ResponseEntity<Void> removeDataSet(@RequestBody List<UUID> dataSetUUID) {
        return entityResponse(() -> {
            dataService.deleteDataSet(dataSetUUID);
            return null;
        });
    }

    /**
     * @return If Page is null, it will return the entire thing. That is why it is async.
     */
    @GetMapping("/getAll")
    public CompletableFuture<ResponseEntity<List<DataPair>>> getAll(@RequestParam UUID networkId, @RequestParam(required = false) Integer page) {
        return dataService.retrieveByPage(networkId, page)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ResponseUtil::responseEntityFailed);
    }

}
