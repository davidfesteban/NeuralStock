package dev.misei.einfachml.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.misei.einfachml.neuralservice.NeuralService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/scheduler")
@Slf4j
public class ScheduledAPI {
    private static final int SAVE_RATE = 2000;
    private final NeuralService neuralService;
    private final ObjectMapper objectMapper;

    //ReIndex Mongo sometimes

    //@Async
    //@Scheduled(fixedRate = 1000 * 60 * 5)
    void saveNetworkBackups() {
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
