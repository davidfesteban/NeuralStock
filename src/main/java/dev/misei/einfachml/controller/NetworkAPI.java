package dev.misei.einfachml.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.misei.einfachml.controller.mapper.AlgorithmBoardMapper;
import dev.misei.einfachml.controller.mapper.NetworkBoardMapper;
import dev.misei.einfachml.neuralservice.DataService;
import dev.misei.einfachml.neuralservice.NeuralService;
import dev.misei.einfachml.neuralservice.domain.Network;
import dev.misei.einfachml.repository.NetworkBoardRepository;
import dev.misei.einfachml.repository.model.AlgorithmBoard;
import dev.misei.einfachml.util.ResponseUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import static dev.misei.einfachml.util.ResponseUtil.entityResponse;

@RestController
@AllArgsConstructor
@RequestMapping("/api/network")
@Slf4j
public class NetworkAPI {

    private NetworkBoardRepository networkBoardRepository;
    private NeuralService neuralService;
    private DataService dataService;
    private ObjectMapper objectMapper;

    @PostMapping("/create")
    public ResponseEntity<UUID> createNetwork(@RequestBody AlgorithmBoard algorithmBoard) {
        return entityResponse(() -> {
            var algorithm = AlgorithmBoardMapper.from(algorithmBoard);
            Network network = Network.create(UUID.randomUUID(), algorithm);

            UUID networkId = network.getStatus().getNetworkId();
            networkBoardRepository.save(NetworkBoardMapper.from(networkId, AlgorithmBoardMapper.to(algorithm)));
            return neuralService.load(networkId, network);
        });
    }

    @GetMapping("/getAllNetworks")
    public SseEmitter getAllNetworks() {
        SseEmitter emitter = new SseEmitter();

        Disposable disposable = Flux.interval(Duration.ofSeconds(2))
                .share()
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe(aLong -> {
                    try {
                        emitter.send(networkBoardRepository.findAll());
                    } catch (IOException e) {
                        log.error("Get All Networks Error: " + e.getMessage());
                        emitter.completeWithError(e);
                    }
                });

        emitter.onCompletion(disposable::dispose);
        emitter.onTimeout(() -> {
            log.error("Get All Networks Timeout");
            disposable.dispose();
            emitter.complete();
        });

        return emitter;
    }

    @GetMapping("/deleteEntire")
    public CompletableFuture<ResponseEntity<Void>> delete(@RequestParam UUID networkId) {
        return dataService.cleanDatapair(networkId)
                .thenApply(unused -> {
                    neuralService.delete(networkId);
                    return null;
                })
                .thenApplyAsync((Function<Object, ResponseEntity<Void>>) o -> {
                    networkBoardRepository.deleteById(networkId);
                    return ResponseEntity.ok(null);
                })
                .exceptionally(ResponseUtil::responseEntityFailed);
    }

    @PostMapping("/upload")
    public ResponseEntity<UUID> uploadJsonFile(@RequestParam("file") MultipartFile networkJsonFile) throws IOException {
        return entityResponse(() -> {
            if (networkJsonFile.isEmpty()) {
                throw new IllegalArgumentException("Network Json File is empty");
            }

            Network network = null;
            try {
                network = objectMapper.readValue(networkJsonFile.getInputStream(), Network.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            network.reconnectAll();

            UUID networkId = network.getStatus().getNetworkId();
            networkBoardRepository.save(NetworkBoardMapper.from(networkId, AlgorithmBoardMapper.to(network.getAlgorithm())));
            return neuralService.load(networkId, network);
        });
    }

    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> downloadNetwork(@RequestParam UUID networkId) throws IOException {

        Network network = neuralService.getNetworkList().get(networkId);
        File file = new File(String.format("static/models/network_%s.json", networkId));
        objectMapper.writeValue(file, network);

        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=network_" + networkId + ".json");

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @GetMapping("/reloadFromFiles")
    public ResponseEntity<Void> reloadFromFiles() {
        File directory = new File("static/models");
        File[] files = directory.listFiles((dir, name) -> name.startsWith("network_") && name.endsWith(".json"));

        if (files != null && files.length > 0) {
            for (File file : files) {
                try {
                    Network network = objectMapper.readValue(file, Network.class);
                    network.reconnectAll();
                    networkBoardRepository.deleteById(network.getStatus().getNetworkId());
                    networkBoardRepository.save(NetworkBoardMapper.from(network.getStatus().getNetworkId(), AlgorithmBoardMapper.to(network.getAlgorithm())));
                    neuralService.load(network.getStatus().getNetworkId(), network);
                } catch (IOException e) {
                    return ResponseUtil.responseEntityFailed(e);
                }
            }
            return ResponseEntity.ok(null);
        } else {
            return ResponseEntity.status(404).body(null);
        }
    }
}
