package dev.misei.einfachml.controller;

import dev.misei.einfachml.controller.dto.UUIDResponse;
import dev.misei.einfachml.controller.mapper.AlgorithmBoardMapper;
import dev.misei.einfachml.neuralservice.DataService;
import dev.misei.einfachml.neuralservice.ComputeService;
import dev.misei.einfachml.neuralservice.NetworkLoadService;
import dev.misei.einfachml.neuralservice.domain.Network;
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

    private ComputeService neuralService;
    private DataService dataService;
    private NetworkLoadService networkLoadService;

    @PostMapping("/create")
    public Mono<UUIDResponse> createNetwork(@RequestBody AlgorithmBoard algorithmBoard) {
        var algorithm = AlgorithmBoardMapper.from(algorithmBoard);
        UUID networkId = UUID.randomUUID();
        Network network = Network.create(networkId, algorithm);

        return networkLoadService.load(networkId, network, false).map(UUIDResponse::new);
    }

    @GetMapping("/getAll")
    public Flux<NetworkBoard> getAllNetworks() {
        return networkLoadService.getAllSummariesEnriched();
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
