package dev.misei.einfachml.neuralservice;

import com.mongodb.client.MongoIterable;
import dev.misei.einfachml.neuralservice.domain.Network;
import dev.misei.einfachml.neuralservice.domain.Status;
import dev.misei.einfachml.repository.PredictedDataRepositoryPerformance;
import dev.misei.einfachml.repository.model.DataPair;
import dev.misei.einfachml.repository.model.PredictedData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
@Slf4j
@AllArgsConstructor
@Getter
public class NeuralService {
    private final Map<UUID, Network> networkList = new HashMap<>();

    private PredictedDataRepositoryPerformance predictedDataRepository;

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

    public Flux<PredictedData> getAllPredictionsByNetwork(UUID networkId, Integer lastEpochAmount) {
        var totalEpochs = this.networkList.get(networkId).getStatus().getAccumulatedEpochs();
        if (lastEpochAmount == null) {
            return predictedDataRepository.findAllByNetworkId(networkId);
        }

        return predictedDataRepository.findByNetworkIdAndEpochHappenedBetween(networkId,
                Math.max(0, totalEpochs - lastEpochAmount), totalEpochs);
    }

    public Flux<Status> getAllStatus() {
        return Flux.fromStream(networkList.values().stream().map(Network::getStatus));
    }

    public Mono<Void> computeElasticAsync(UUID networkId, Flux<DataPair> dataset, int epochs) {
        Network network = networkList.get(networkId);
        if (network.getStatus().isRunning()) {
            return Mono.error(new IllegalStateException("Network still running"));
        }

        return network.computeFlux(dataset, epochs)
                .buffer(1000)
                .flatMapSequential(batch -> predictedDataRepository.saveBatchByNetworkId(networkId, Flux.fromIterable(batch)))
                .then();
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
