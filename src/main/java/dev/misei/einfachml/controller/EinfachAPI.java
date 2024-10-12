package dev.misei.einfachml.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.misei.einfachml.controller.mapper.AlgorithmBoardMapper;
import dev.misei.einfachml.controller.mapper.NetworkBoardMapper;
import dev.misei.einfachml.neuralservice.DataService;
import dev.misei.einfachml.neuralservice.NeuralService;
import dev.misei.einfachml.neuralservice.domain.Network;
import dev.misei.einfachml.repository.NetworkBoardRepository;
import dev.misei.einfachml.repository.model.AlgorithmBoard;
import dev.misei.einfachml.repository.model.DataPair;
import dev.misei.einfachml.repository.model.NetworkBoard;
import dev.misei.einfachml.repository.model.PredictedData;
import dev.misei.einfachml.util.EpochCountDown;
import lombok.AllArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

//TODO: @GetMapping("/save & upload DataSet")
//TODO: Backup Scheduled for Networks & load
//TODO: Clean up Network static
@RestController
@AllArgsConstructor
public class EinfachAPI {

    private NeuralService neuralService;
    private DataService dataService;
    private NetworkBoardRepository networkBoardRepository;

    private ObjectMapper objectMapper;

    @PostMapping("/createNetwork")
    public UUID createNetwork(@RequestBody AlgorithmBoard algorithmBoard) {
        Network network = Network.create(UUID.randomUUID(), AlgorithmBoardMapper.from(algorithmBoard));

        UUID networkId = network.getStatus().getNetworkId();
        networkBoardRepository.save(NetworkBoardMapper.from(networkId, algorithmBoard, network));
        return neuralService.load(networkId, network);
    }

    //@PostMapping("/uploadNetwork")
    //public UUID uploadJsonFile(@RequestParam("file") MultipartFile networkJsonFile) throws IOException {
    //    if (networkJsonFile.isEmpty()) {
    //        throw new IllegalArgumentException("Network Json File is empty");
    //    }
//
    //    Network network = objectMapper.readValue(networkJsonFile.getInputStream(), Network.class);
    //    network.reconnectAll();
//
    //    UUID networkId = network.getStatus().getNetworkId();
    //    networkBoardRepository.save(NetworkBoardMapper.from(networkId, AlgorithmBoardMapper.to(network.getAlgorithm()), network));
    //    return neuralService.load(networkId, network);
    //}
//
    //@GetMapping("/saveDownloadNetwork")
    //public ResponseEntity<InputStreamResource> downloadNetwork(@RequestParam UUID networkId) throws IOException {
//
    //    Network network = neuralService.getNetworkList().get(networkId);
    //    File file = new File(String.format("static/models/network_%s.json", networkId));
    //    objectMapper.writeValue(file, network);
//
    //    InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
//
    //    HttpHeaders headers = new HttpHeaders();
    //    headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=network_" + networkId + ".json");
//
    //    return ResponseEntity.ok()
    //            .headers(headers)
    //            .contentLength(file.length())
    //            .contentType(MediaType.APPLICATION_OCTET_STREAM)
    //            .body(resource);
    //}


    //@GetMapping("/deleteAll")
    //public Network deleteNetwork(@RequestParam UUID networkId) {
    //    networkBoardRepository.deleteById(networkId);
    //    dataService.cleanDatapair(networkId);
    //    return neuralService.delete(networkId);
    //}

    @GetMapping("/getAllNetworks")
    public List<NetworkBoard> getAllNetworks() {
        return networkBoardRepository.findAll();
    }

    @PostMapping("/includeDataSet")
    public UUID includeDataSet(@RequestParam UUID networkId, @RequestBody List<DataPair> dataSet) throws Throwable {
        var networkBoard = networkBoardRepository.findById(networkId)
                .orElseThrow((Supplier<Throwable>) () -> new IllegalArgumentException("Network UUID not found"));

        dataService.includeDataset(networkBoard, dataSet);

        return networkId;
    }

    @GetMapping("/compute")
    public CompletableFuture<Void> compute(@RequestParam UUID networkId, @RequestParam int epochs, @RequestParam(required = false) Long createdAtStart,
                                     @RequestParam(required = false) Long createdAtEnd) {

        List<DataPair> dataset = null;
        if(createdAtStart != null && createdAtEnd != null) {
            dataset = dataService.retrieveWindowed(networkId, createdAtStart, createdAtEnd);
        } else {
            dataset = dataService.retrieveAll(networkId);
        }

        var countDown = neuralService.computeElasticAsync(networkId, dataset, epochs, true);

        return Flux.interval(Duration.ofSeconds(1))
                .subscribeOn(Schedulers.boundedElastic())
                .map(sequence -> String.format("Epoch: %d/%d", countDown.getEpochs() - countDown.getCount(), countDown.getEpochs()))
                .takeUntil(sequence -> countDown.getCount() == 0);
    }

    @PostMapping("/predict")
    public Flux<String> predict(@RequestParam UUID networkId, @RequestBody List<DataPair> dataSet) throws Throwable {
        var countDown = neuralService.computeElasticAsync(networkId, dataSet, 1, false);

        return Flux.interval(Duration.ofSeconds(1))
                .subscribeOn(Schedulers.boundedElastic())
                .map(sequence -> String.format("Epoch: %d/%d", countDown.getEpochs() - countDown.getCount(), countDown.getEpochs()))
                .takeUntil(sequence -> countDown.getCount() == 0);
    }

    @GetMapping("/reloadBoard")
    public NetworkBoard reloadBoard(UUID networkId) {
        Network network = neuralService.getNetwork(networkId);
        System.out.println(network);
        EpochCountDown epochCountDown = neuralService.getEpochCountDown(networkId);
        NetworkBoard networkBoard = networkBoardRepository.findById(networkId).orElseThrow(() -> new IllegalArgumentException("Network not found"));
        List<PredictedData> predictedDataList = neuralService.getAllPredictionsByNetwork(networkId);

        NetworkBoardMapper.update(networkBoard, network, epochCountDown, predictedDataList);
        networkBoardRepository.deleteById(networkId);
        return networkBoardRepository.save(networkBoard);
    }

    @GetMapping("/getAllPredictions")
    public List<PredictedData> getAllPredictions(@RequestParam UUID networkId) {
        return neuralService.getAllPredictionsByNetwork(networkId);
    }

    //@GetMapping("/getDataSet")
    //public NetworkBoard reloadBoard(UUID networkId) {
//
    //}
}
