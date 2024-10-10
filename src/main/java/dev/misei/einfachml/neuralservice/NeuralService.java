package dev.misei.einfachml.neuralservice;

import dev.misei.einfachml.neuralservice.domain.Network;
import dev.misei.einfachml.repository.PredictedDataRepository;
import dev.misei.einfachml.repository.model.DataPair;
import dev.misei.einfachml.repository.model.PredictedData;
import dev.misei.einfachml.util.EpochCountDown;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

//TODO: Load/Unload the Network
@Service
@Slf4j
@AllArgsConstructor
@Getter
public class NeuralService {

    private final Map<UUID, Network> networkList = new HashMap<>();
    private final Map<UUID, EpochCountDown> countDownList = new ConcurrentHashMap<>();

    private PredictedDataRepository predictedDataRepository;

    public UUID load(UUID uuid, Network network) {
        network.reconnectAll();
        networkList.put(uuid, network);
        return uuid;
    }

    public List<PredictedData> getAllPredictions(UUID networkId) {
        return predictedDataRepository.findByNetworkId(networkId).stream().sorted().toList();
    }

    public Network getNetwork(UUID networkId) {
        return networkList.get(networkId);
    }

    public EpochCountDown getEpochCountDown(UUID networkId) {
        return countDownList.getOrDefault(networkId, new EpochCountDown(0));
    }

    public Network delete(UUID networkId) {

        if (countDownList.containsKey(networkId) && countDownList.get(networkId).isCanceled()) {
            countDownList.remove(networkId);
        } else if (countDownList.containsKey(networkId)) {
            return null;
        }

        Network network = networkList.remove(networkId);
        predictedDataRepository.deleteByNetworkId(networkId);

        return network;
    }

    public EpochCountDown computeElasticAsync(UUID networkId, List<DataPair> dataset, int epochs, boolean forTraining) {
        EpochCountDown latch = new EpochCountDown(epochs);
        var bufferSize = Math.max(1, (dataset.size() / 10) + (dataset.get(0).getInputs().size() / 10) + (epochs / 10));
        log.info(String.format("Buffer size for %s: %d", networkId, bufferSize));
        var flux = networkList.get(networkId).computeFlux(dataset, epochs, forTraining, latch)
                .buffer(bufferSize);

        subscribe(networkId, flux, latch);

        return latch;
    }

    private void subscribe(UUID networkId, Flux<List<PredictedData>> flux, EpochCountDown epochCountDown) {
        countDownList.put(networkId, epochCountDown);
        log.info(String.format("Subscribed to: %s", networkId));
        flux
                .subscribe(batch -> {
                    log.info(String.format("Processing next batch: %d, on %s", batch.size(), networkId));
                    predictedDataRepository.saveAll(batch);
                }, throwable -> {
                    terminate(epochCountDown, networkId, String.format("Error during training: %s", throwable.getMessage()));
                }, () -> {
                    terminate(epochCountDown, networkId, String.format("Training completed on Network: %s", networkId));
                });
    }

    private void terminate(EpochCountDown epochCountDown, UUID networkId, String message) {
        log.info(message);
        epochCountDown.terminate();
        countDownList.remove(networkId);
    }
}
