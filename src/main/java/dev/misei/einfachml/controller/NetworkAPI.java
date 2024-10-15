package dev.misei.einfachml.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.misei.einfachml.controller.mapper.AlgorithmBoardMapper;
import dev.misei.einfachml.controller.mapper.NetworkBoardMapper;
import dev.misei.einfachml.neuralservice.DataService;
import dev.misei.einfachml.neuralservice.NeuralService;
import dev.misei.einfachml.neuralservice.domain.Network;
import dev.misei.einfachml.repository.NetworkBoardRepository;
import dev.misei.einfachml.repository.model.AlgorithmBoard;
import dev.misei.einfachml.repository.model.NetworkBoard;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

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
    public Mono<UUID> createNetwork(@RequestBody AlgorithmBoard algorithmBoard) {
        var algorithm = AlgorithmBoardMapper.from(algorithmBoard);

        Network network = Network.create(UUID.randomUUID(), algorithm);
        UUID networkId = network.getStatus().getNetworkId();

        return networkBoardRepository.save(NetworkBoardMapper.from(networkId, AlgorithmBoardMapper.to(algorithm)))
                .then(neuralService.load(networkId, network))
                .thenReturn(networkId);
    }

    @GetMapping("/getAllNetworks")
    public Flux<NetworkBoard> getAllNetworks() {
        return neuralService.getAllStatus()
                .flatMap(status -> {
                    return networkBoardRepository.findById(status.getNetworkId())
                            .flatMap(networkBoard -> {
                                networkBoard.setStatus(status);

                                return dataService.countByNetworkId(networkBoard.getNetworkId())
                                        .flatMap(datasetSize -> {
                                            networkBoard.setDatasetSize(datasetSize);
                                            return neuralService.countByNetworkId(networkBoard.getNetworkId())
                                                    .map(predictionsSize -> {
                                                        networkBoard.setPredictionsSize(predictionsSize);
                                                        return networkBoard;
                                                    });
                                        });
                            });
                })
                .flatMap(networkBoard -> networkBoardRepository.save(networkBoard));
    }

    @GetMapping("/deleteEntire")
    public Mono<Void> delete(@RequestParam UUID networkId) {
        return dataService.cleanDatapair(networkId)
                .then(neuralService.delete(networkId))
                .then(networkBoardRepository.deleteById(networkId))
                .then();
    }

    /*

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
    } */
}
