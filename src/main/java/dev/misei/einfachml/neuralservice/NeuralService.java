package dev.misei.einfachml.neuralservice;

import dev.misei.einfachml.neuralservice.domain.Network;
import dev.misei.einfachml.neuralservice.domain.Status;
import dev.misei.einfachml.repository.PredictedDataRepository;
import dev.misei.einfachml.repository.model.DataPair;
import dev.misei.einfachml.repository.model.PredictedData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantLock;

//TODO: Load/Unload the Network
@Service
@Slf4j
@AllArgsConstructor
@Getter
public class NeuralService {

    private static final int SAVE_RATE = 10000;
    private static final int BUFFER_SIZE = 10000;
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
        scheduledDataSave(); //When called from the same class, it is not async
        predictedDataRepository.deleteByNetworkId(networkId);

        return CompletableFuture.completedFuture(network);
    }

    @Async
    public CompletableFuture<List<PredictedData>> getAllPredictionsByNetwork(UUID networkId, Integer lastEpochAmount) {
        var totalEpochs = this.networkList.get(networkId).getStatus().getAccumulatedEpochs();
        return predictedDataRepository.findByNetworkIdAndEpochHappenedBetweenOrderByCreatedAtAsc(networkId,
                lastEpochAmount == null? 0 : totalEpochs - lastEpochAmount
                , totalEpochs);
    }

    @Async
    public CompletableFuture<List<Status>> getAllStatus() {
        return CompletableFuture.completedFuture(networkList.values().stream().map(Network::getStatus).toList());
    }

    @Async
    public void computeElasticAsync(UUID networkId, List<DataPair> dataset, int epochs,
                                                       boolean forTraining, SseEmitter sseEmitter) {
        networkList.get(networkId)
                .computeFlux(dataset, epochs, forTraining, predictedDataCache::add, sseEmitter);
    }

    @Async
    @Scheduled(fixedRate = SAVE_RATE)
    void scheduledDataSave() {
        if (lock.tryLock()) {
            try {
                List<PredictedData> buffer = new ArrayList<>();
                PredictedData predictedData = new PredictedData();

                while (buffer.size() <= BUFFER_SIZE && predictedData != null) {
                    predictedData = predictedDataCache.poll();
                    if (predictedData != null) {
                        buffer.add(predictedData);
                    }
                }

                log.info(String.format("Saving on database %d elements", buffer.size()));
                predictedDataRepository.saveAll(buffer);
                log.info("Saved on database");
            } finally {
                lock.unlock();
            }
        } else {
            log.info("Task skipped - another instance is already running");
        }
    }
}
