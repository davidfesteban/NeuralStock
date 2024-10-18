package dev.misei.einfachml.controller;

import dev.misei.einfachml.controller.dto.TopicResponse;
import dev.misei.einfachml.neuralservice.DataService;
import dev.misei.einfachml.repository.model.DataPair;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/dataset")
@Slf4j
public class DataSetAPI {

    private DataService dataService;

    @PostMapping("/add")
    public Mono<Void> includeDataSet(@RequestBody Flux<DataPair> dataSet) {
        return dataService.includeDataset(dataSet);
    }

    @PostMapping("/removeList")
    public Mono<Void> removeDataSet(@RequestBody Flux<UUID> dataSetUUID) {
        return dataService.deleteDataSetByUUID(dataSetUUID);
    }

    @PostMapping("/removeTopic")
    public Mono<Void> removeDataSet(@RequestParam String topic) {
        return dataService.deleteDataSetByTopic(topic);
    }

    @GetMapping("/getAllTopics")
    public Flux<TopicResponse> getAllTopics() {
        return dataService.retrieveTopics();
    }

}
