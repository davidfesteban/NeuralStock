package dev.misei.einfachml.controller;

import dev.misei.einfachml.controller.dto.PlotBoard;
import dev.misei.einfachml.controller.mapper.AlgorithmBoardMapper;
import dev.misei.einfachml.controller.mapper.NetworkBoardMapper;
import dev.misei.einfachml.neuralservice.DataService;
import dev.misei.einfachml.neuralservice.NeuralService;
import dev.misei.einfachml.neuralservice.domain.Network;
import dev.misei.einfachml.repository.NetworkBoardRepository;
import dev.misei.einfachml.repository.model.AlgorithmBoard;
import dev.misei.einfachml.repository.model.DataPair;
import dev.misei.einfachml.repository.model.PredictedData;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Collectors;

//TODO: @GetMapping("/save & upload DataSet")
//TODO: Backup Scheduled for Networks & load
//TODO: Clean up Network static
@RestController
@AllArgsConstructor
public class EinfachAPI {

    private NeuralService neuralService;
    private DataService dataService;
    private NetworkBoardRepository networkBoardRepository;

    @PostMapping("/createNetwork")
    public UUID createNetwork(@RequestBody AlgorithmBoard algorithmBoard) {
        var algorithm = AlgorithmBoardMapper.from(algorithmBoard);
        Network network = Network.create(UUID.randomUUID(), algorithm);

        UUID networkId = network.getStatus().getNetworkId();
        networkBoardRepository.save(NetworkBoardMapper.from(networkId, AlgorithmBoardMapper.to(algorithm)));
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
    public SseEmitter getAllNetworks() {
        SseEmitter emitter = new SseEmitter();

        Disposable disposable = Flux.interval(Duration.ofSeconds(2))
                .share()
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe(aLong -> {
                    try {
                        emitter.send(networkBoardRepository.findAll());
                    } catch (IOException e) {
                        System.out.println("GET ALL NETWORKS");
                        emitter.completeWithError(e);
                    }
                });

        emitter.onCompletion(disposable::dispose);
        emitter.onTimeout(() -> {
            disposable.dispose();
            emitter.complete();
        });

        return emitter;
    }

    @PostMapping("/includeDataSet")
    public UUID includeDataSet(@RequestParam UUID networkId, @RequestBody List<DataPair> dataSet) throws Throwable {
        var networkBoard = networkBoardRepository.findById(networkId)
                .orElseThrow((Supplier<Throwable>) () -> new IllegalArgumentException("Network UUID not found"));

        dataService.includeDataset(networkBoard, dataSet);

        return networkId;
    }

    @GetMapping("/compute")
    public SseEmitter compute(@RequestParam UUID networkId, @RequestParam int epochs, @RequestParam(required = false) Long createdAtStart,
                              @RequestParam(required = false) Long createdAtEnd) {
        SseEmitter sseEmitter = new SseEmitter(600000L);

        dataService.retrieve(networkId, createdAtStart, createdAtEnd).thenAccept(dataPairList ->
                neuralService.computeElasticAsync(networkId, dataPairList, epochs, true, sseEmitter));

        return sseEmitter;
    }

    @PostMapping("/predict")
    public SseEmitter predict(@RequestParam UUID networkId, @RequestBody List<DataPair> dataSet) {
        SseEmitter sseEmitter = new SseEmitter();
        neuralService.computeElasticAsync(networkId, dataSet, 1, false, sseEmitter);
        return sseEmitter;
    }

    @Async
    @GetMapping("/fetchPlotWithDefinition")
    public CompletableFuture<PlotBoard> fetchPlot(@RequestParam UUID networkId, @RequestParam(required = false) Integer lastEpochAmount) {
        return neuralService.getAllPredictionsByNetwork(networkId, lastEpochAmount).thenApply(predictedData -> {

            PlotBoard neuralBoard = new PlotBoard();
            int lastEpoch = predictedData.getLast().getEpochHappened();

            neuralBoard.setLastEpochPredicted(predictedData.stream()
                    .filter(predictedData1 -> predictedData1.getEpochHappened() == lastEpoch).toList());

            neuralBoard.setMseErrors(predictedData.stream()
                    .collect(Collectors.groupingBy(PredictedData::getEpochHappened)).values().stream().map(
                            predictedData12 -> predictedData12.stream().mapToDouble(
                                            PredictedData::calculateMseForPredictedData)
                                    .average().orElse(0d)).toList());

            return neuralBoard;
        });
    }

    @GetMapping("/getPredictionsWithDefinition")
    public CompletableFuture<List<PredictedData>> getPredictions(@RequestParam UUID networkId, @RequestParam(required = false) Integer lastEpochAmount) {
        return neuralService.getAllPredictionsByNetwork(networkId, lastEpochAmount);
    }

    @Async
    @Scheduled(fixedRate = 10000)
    void updateNetworkBoard() {
        System.out.println("NetworkBoard update");
        neuralService.getAllStatus().thenAccept(statuses -> statuses.forEach(status -> {
            var networkBoard = networkBoardRepository.findById(status.getNetworkId()).get();
            networkBoard.setStatus(status);
            networkBoard.setDatasetSize(0);
            networkBoard.setPredictionsSize(0);
            networkBoardRepository.save(networkBoard);
        }));
    }

    //@GetMapping("/getDataSet")
    //public NetworkBoard reloadBoard(UUID networkId) {
//
    //}
}
