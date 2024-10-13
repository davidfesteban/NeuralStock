package dev.misei.einfachml.neuralservice;

import dev.misei.einfachml.neuralservice.domain.Network;
import dev.misei.einfachml.neuralservice.domain.Status;
import dev.misei.einfachml.repository.PredictedDataRepository;
import dev.misei.einfachml.repository.model.DataPair;
import dev.misei.einfachml.repository.model.PredictedData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantLock;

@Service
@Slf4j
@AllArgsConstructor
@Getter
public class NeuralService {
    private final Map<UUID, Network> networkList = new HashMap<>();
    private final ReentrantLock lock = new ReentrantLock();
    private final Queue<PredictedData> predictedDataCache = new ConcurrentLinkedQueue<>();

    private PredictedDataRepository predictedDataRepository;

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

    @Async
    public CompletableFuture<List<PredictedData>> getAllPredictionsByNetwork(UUID networkId, Integer lastEpochAmount) {
        var totalEpochs = this.networkList.get(networkId).getStatus().getAccumulatedEpochs();
        return predictedDataRepository.findByNetworkIdAndEpochHappenedBetweenOrderByCreatedAtAsc(networkId,
                lastEpochAmount == null ? 0 : totalEpochs - lastEpochAmount
                , totalEpochs);
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

    @Async
    public void scheduleDataSave(int bufferSize) {
        if (lock.tryLock()) {
            try {
                List<PredictedData> buffer = new ArrayList<>();
                PredictedData predictedData = new PredictedData();

                while (buffer.size() <= bufferSize && predictedData != null) {
                    predictedData = predictedDataCache.poll();
                    if (predictedData != null) {
                        buffer.add(predictedData);
                    }
                }

                log.info(String.format("Saving on database %d elements", buffer.size()));
                predictedDataRepository.saveAll(buffer);
            } finally {
                lock.unlock();
            }
        } else {
            log.info("Task skipped - another instance is already running");
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
