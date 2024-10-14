package dev.misei.einfachml.neuralservice;

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

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
@Slf4j
@AllArgsConstructor
@Getter
public class NeuralService {
    private final Map<UUID, Network> networkList = new HashMap<>();
    private final Queue<PredictedData> predictedDataCache = new ConcurrentLinkedQueue<>();

    private PredictedDataRepositoryPerformance predictedDataRepository;

    public UUID load(UUID uuid, Network network) {
        network.reconnectAll();
        networkList.put(uuid, network);
        return uuid;
    }

    @Async
    public CompletableFuture<Network> delete(UUID networkId) {
        Network network = networkList.get(networkId);

        if (network.getStatus().isRunning()) {
            return CompletableFuture.failedFuture(new IllegalStateException("Network still running"));
        }

        networkList.remove(networkId);
        predictedDataRepository.deleteByNetworkId(networkId);

        return CompletableFuture.completedFuture(network);
    }

    public Flux<PredictedData> getAllPredictionsByNetwork(UUID networkId, Integer lastEpochAmount) {
        var totalEpochs = this.networkList.get(networkId).getStatus().getAccumulatedEpochs();
        if (lastEpochAmount == null) {
            return predictedDataRepository.findAllByNetworkId(networkId);
        }

        return predictedDataRepository.findByNetworkIdAndEpochHappenedBetween(networkId,
                Math.max(0, totalEpochs - lastEpochAmount), totalEpochs);
    }

    @Async
    public CompletableFuture<List<Status>> getAllStatus() {
        return CompletableFuture.completedFuture(networkList.values().stream().map(Network::getStatus).toList());
    }

    @Async
    public CompletableFuture<Void> computeElasticAsync(UUID networkId, List<DataPair> dataset, int epochs) {
        Network network = networkList.get(networkId);

        if (network.getStatus().isRunning()) {
            return CompletableFuture.failedFuture(new IllegalStateException("Network still running"));
        }

        networkList.get(networkId)
                .computeFlux(dataset, epochs, predictedDataCache::add);

        return CompletableFuture.completedFuture(null);
    }


    public void scheduleDataSave(int bufferSize) {
        int bufferCount = 0;
        Map<UUID, List<PredictedData>> buffer = new HashMap<>();
        PredictedData predictedData = new PredictedData();

        while (bufferCount < bufferSize && predictedData != null) {
            predictedData = predictedDataCache.poll();
            if (predictedData != null) {
                UUID networkId = predictedData.getNetworkId();
                buffer.computeIfAbsent(networkId, k -> new ArrayList<>());
                buffer.get(networkId).add(predictedData);
                ++bufferCount;
            }
        }

        if (bufferCount != 0 || !buffer.isEmpty()) {
            log.info(String.format("Saving on database %d elements", bufferCount));
            buffer.forEach((uuid, predictedData1) -> predictedDataRepository.saveBatchByNetworkId(uuid, predictedData1));
        }
    }

    @Async
    public CompletableFuture<List<PredictedData>> predictElasticAsync(UUID networkId, List<DataPair> dataSet) {
        Network network = networkList.get(networkId);

        if (network.getStatus().isRunning()) {
            return CompletableFuture.failedFuture(new IllegalStateException("Network still running"));
        }

        return CompletableFuture.completedFuture(network.predictAsync(dataSet));
    }
}
