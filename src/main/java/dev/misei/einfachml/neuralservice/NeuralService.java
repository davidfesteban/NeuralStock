package dev.misei.einfachml.neuralservice;

import dev.misei.einfachml.neuralservice.domain.Network;
import dev.misei.einfachml.neuralservice.domain.Status;
import dev.misei.einfachml.repository.MetricsRepository;
import dev.misei.einfachml.repository.PredictedDataRepositoryPerformance;
import dev.misei.einfachml.repository.model.DataPair;
import dev.misei.einfachml.repository.model.MSEData;
import dev.misei.einfachml.repository.model.PredictedData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

@Service
@Slf4j
@AllArgsConstructor
@Getter
public class NeuralService {
    private final Map<UUID, Network> networkList = new HashMap<>();

    private PredictedDataRepositoryPerformance predictedDataRepository;
    private MetricsRepository metricsRepository;

    public Mono<UUID> load(UUID uuid, Network network) {
        return Mono.defer(() -> {
            network.reconnectAll();
            networkList.put(uuid, network);
            return Mono.just(uuid);
        });
    }

    public Mono<Void> delete(UUID networkId) {
        Network network = networkList.get(networkId);

        if (network.getStatus().isRunning()) {
            return Mono.error(new IllegalStateException("Network still running"));
        }

        return predictedDataRepository.deleteByNetworkId(networkId)
                .then(Mono.fromRunnable(() -> networkList.remove(networkId)))
                .then();
    }

    public Flux<PredictedData> getAllPredictionsByNetwork(UUID networkId, Integer lastEpochAmount, Boolean downsample) {
        log.info("Sending Predictions");
        var totalEpochs = this.networkList.get(networkId).getStatus().getAccumulatedEpochs();
        Flux<PredictedData> flux;
        if (lastEpochAmount == null) {
            flux = predictedDataRepository.findAllByNetworkId(networkId);
        } else {
            flux = predictedDataRepository.findByNetworkIdAndEpochHappenedBetween(networkId,
                    Math.max(0, totalEpochs - lastEpochAmount), totalEpochs);
        }

        if (downsample == null || !downsample) {
            return flux;
        }

        return flux; //flux.count()
        // .flatMapMany(totalCount -> {
        //     log.info(totalCount.toString());
        //     int bufferSize = (int) Math.floor((double) totalCount / 100);
        //     return flux
        //             .buffer(bufferSize)
        //             .flatMap(batch -> {
        //                 PredictedData maxValue = batch.stream()
        //                         .max(Comparator.comparingDouble(PredictedData::getMseError))
        //                         .orElse(null);
        //                 log.info("Batch size " + batch.size());
        //                 return maxValue != null ? Flux.just(maxValue) : Flux.empty();
        //             });
        // });


    }

    public Flux<Status> getAllStatus() {
        return Flux.fromStream(networkList.values().stream().map(Network::getStatus));
    }

    public Mono<Void> computeElasticAsync(UUID networkId, Flux<DataPair> dataset, int epochs) {
        Network network = networkList.get(networkId);
        if (network.getStatus().isRunning()) {
            return Mono.error(new IllegalStateException("Network still running"));
        }

        //return network.computeFlux(dataset, epochs)
        //        .as(predictedDataFlux -> predictedDataRepository.saveBatchByNetworkId(networkId, predictedDataFlux))
        //        .as(saveToMSEError)
        //        .then();
        return network.computeFlux(dataset, epochs)
                .publish(flux -> {
                    Mono<Void> saveToPredictions = flux.buffer(3000)
                            .flatMap(batch -> predictedDataRepository.saveBatchByNetworkId(networkId, Flux.fromIterable(batch)), 2)
                            .then();

                    Mono<Void> saveToMetrics = flux.buffer(3000)
                            .flatMap(batch -> saveGroupedMSEData(Flux.fromIterable(batch)), 2)
                            .then();

                    return Mono.when(saveToPredictions, saveToMetrics);
                }).then();

    }

    public Mono<Void> saveGroupedMSEData(Flux<PredictedData> predictedDataFlux) {
        return predictedDataFlux
                .groupBy(predictedData -> new AbstractMap.SimpleEntry<>(predictedData.getNetworkId(), predictedData.getEpochHappened()))
                .flatMap(groupedFlux -> {
                    UUID networkId = groupedFlux.key().getKey();
                    int epochHappened = groupedFlux.key().getValue();

                    return groupedFlux
                            .reduce(new double[2], (acc, predictedData) -> {
                                acc[0] += predictedData.getMseError();
                                acc[1]++;
                                return acc;
                            })
                            .map(acc -> {
                                double mseErrorAverage = acc[0] / acc[1];
                                return new MSEData(networkId, epochHappened, mseErrorAverage);
                            });
                })
                .as(flux -> metricsRepository.saveAll(flux)).then();
    }

    public Flux<PredictedData> predictElasticAsync(UUID networkId, List<DataPair> dataSet) {
        Network network = networkList.get(networkId);
        if (network.getStatus().isRunning()) {
            return Flux.error(new IllegalStateException("Network still running"));
        }

        return network.predictAsync(dataSet);
    }

    public Mono<Long> countByNetworkId(UUID networkId) {
        return predictedDataRepository.countByNetworkId(networkId);
    }
}
