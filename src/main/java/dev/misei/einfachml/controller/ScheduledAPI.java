package dev.misei.einfachml.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.misei.einfachml.neuralservice.NeuralService;
import dev.misei.einfachml.repository.DataPairRepository;
import dev.misei.einfachml.repository.NetworkBoardRepository;
import dev.misei.einfachml.repository.PredictedDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;

import static dev.misei.einfachml.util.ResponseUtil.entityResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/scheduler")
@Slf4j
public class ScheduledAPI {
    private static final int SAVE_RATE = 7000;
    private final NeuralService neuralService;
    private final ObjectMapper objectMapper;
    private final NetworkBoardRepository networkBoardRepository;
    private final DataPairRepository dataPairRepository;
    private final PredictedDataRepository predictedDataRepository;
    private int bufferSize = 100000;

    @GetMapping("/buffer")
    public ResponseEntity<Integer> modifyBuffer(@RequestParam Integer size) {
        return entityResponse(() -> {
            return bufferSize = size;
        });
    }

    @Async
    @Scheduled(fixedRate = SAVE_RATE)
    void updateNetworkBoard() {
        neuralService.getAllStatus().thenAccept(statuses -> statuses.forEach(status -> {
            var networkBoard = networkBoardRepository.findById(status.getNetworkId()).get();
            networkBoard.setStatus(status);
            networkBoard.setDatasetSize(dataPairRepository.countByNetworkId(status.getNetworkId()));
            networkBoard.setPredictionsSize(predictedDataRepository.countByNetworkId(status.getNetworkId()));
            networkBoardRepository.save(networkBoard);
        }));
    }

    @Async
    @Scheduled(fixedRate = SAVE_RATE)
    void savePredictions() {
        neuralService.scheduleDataSave(bufferSize);
    }

    @Async
    @Scheduled(fixedRate = 3600 * 3)
    void saveNetwork() {
        neuralService.getNetworkList().values().forEach(network -> {
            File file = new File(String.format("static/models/network_%s.json", network.getStatus().getNetworkId()));
            try {
                objectMapper.writeValue(file, network);
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        });
    }
}
