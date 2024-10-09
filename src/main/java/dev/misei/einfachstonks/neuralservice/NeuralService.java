package dev.misei.einfachstonks.neuralservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.misei.einfachstonks.neuralservice.domain.Network;
import dev.misei.einfachstonks.neuralservice.domain.data.Dataset;
import dev.misei.einfachstonks.neuralservice.model.PredictedData;
import dev.misei.einfachstonks.neuralservice.model.PredictedPoint;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Service
@AllArgsConstructor
public class NeuralService {

    static final int BATCH_SAVE_SIZE = 1000;
    private final Map<UUID, Network> networkList = new HashMap<>();

    private ObjectMapper objectMapper;

    private PredictedDataRepository predictedDataRepository;

    public void saveNetwork(UUID networkId) throws IOException {
        objectMapper.writeValue(new File(String.format("network_%s.json", networkId.toString())), networkList.get(networkId));
    }

    public void importNetwork(UUID networkId) throws IOException {
        var network = objectMapper.readValue(new File(String.format("network_%s.json", networkId.toString())), Network.class);
        network.reconnectAll();
        networkList.put(networkId, network);
    }

    public UUID create(Network network) {
        return create(UUID.randomUUID(), network);
    }

    public UUID create(UUID uuid, Network network) {
        networkList.put(uuid, network);
        return uuid;
    }

    public EpochCountDown predictAsync(UUID networkId, Dataset innerDataset) {
        EpochCountDown latch = new EpochCountDown(1);
        Network network = networkList.get(networkId);
        var flux = network.predict(innerDataset, latch)
                .buffer(1);

        subscribe(networkId, network, flux, latch);

        return latch;
    }

    public EpochCountDown trainElasticAsync(UUID networkId, int epochs) {
        EpochCountDown latch = new EpochCountDown(epochs);
        Network network = networkList.get(networkId);
        var flux = network.train(epochs, latch)
                .buffer(BATCH_SAVE_SIZE);

        subscribe(networkId, network, flux, latch);

        return latch;
    }

    public EpochCountDown trainElasticAsync(UUID networkId, int epochs, int pastWindowTime) {
        EpochCountDown latch = new EpochCountDown(epochs);
        Network network = networkList.get(networkId);
        var flux = network.train(epochs, pastWindowTime, latch)
                .buffer(BATCH_SAVE_SIZE);

        subscribe(networkId, network, flux, latch);

        return latch;
    }

    public EpochCountDown mergeAsync(UUID networkId, Dataset innerDataset, int pastWindowTime, int epochRelationPercent) {
        Network network = networkList.get(networkId);
        int epochs = (int) Math.max(1, Math.ceil(network.getAccumulatedTrainedEpochs() * epochRelationPercent / 100.0));
        EpochCountDown latch = new EpochCountDown(epochs);

        var flux = network.merge(innerDataset, pastWindowTime, epochs, latch)
                .buffer(BATCH_SAVE_SIZE);

        subscribe(networkId, network, flux, latch);

        return latch;
    }

    private void subscribe(UUID networkId, Network network, Flux<List<PredictedPoint>> flux, EpochCountDown epochCountDown) {
        int initialEpoch = network.getAccumulatedTrainedEpochs();

        AtomicReference<PredictedData> currentPredictedData = new AtomicReference<>(
                new PredictedData(UUID.randomUUID(), networkId, new ArrayList<>(), initialEpoch)
        );
        AtomicInteger currentEpoch = new AtomicInteger(initialEpoch);
        AtomicReference<List<PredictedData>> predictedCache = new AtomicReference<>(new ArrayList<>());


        flux
                .subscribe(batch -> {
                    System.out.println("Magic_" + networkId.toString());
                    batch.forEach(predictedPoint -> {
                        if (predictedPoint.getEpochHappened() != currentEpoch.get()) {
                            // Save the current predicted data to cache
                            var cache = predictedCache.get();
                            cache.add(currentPredictedData.get());
                            predictedCache.set(cache);

                            // Update to the new epoch
                            currentEpoch.set(predictedPoint.getEpochHappened());

                            // Start tracking a new PredictedData for the new epoch
                            currentPredictedData.set(
                                    new PredictedData(UUID.randomUUID(), networkId, new ArrayList<>(), predictedPoint.getEpochHappened())
                            );
                        }

                        // Add the predicted point to the current epoch's predicted data
                        var predictedData = currentPredictedData.get();
                        predictedData.getPredictedPointByEpoch().add(predictedPoint);
                        currentPredictedData.set(predictedData);

                        // If the cache size exceeds the batch size, save it
                        if (predictedCache.get().size() > BATCH_SAVE_SIZE) {
                            predictedDataRepository.saveAllOnCollectionName(predictedCache.get(), networkId.toString());
                            predictedCache.set(new ArrayList<>()); // Clear the cache after saving
                        }
                    });
                }, throwable -> {
                    System.out.println("Error during training: " + throwable.getMessage());
                    epochCountDown.terminate();
                }, () -> {
                    var cache = predictedCache.get();
                    cache.add(currentPredictedData.get());
                    predictedDataRepository.saveAllOnCollectionName(cache, networkId.toString());
                    System.out.println("Training completed for network: " + networkId);
                    epochCountDown.terminate();
                });
    }
}
