package dev.misei.einfachstonks.neuralservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.misei.einfachstonks.neuralservice.dataenum.Dataset;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

@Service
@AllArgsConstructor
public class NeuralService {

    private final ExecutorService executorService = Executors.newFixedThreadPool(10);
    private final Map<UUID, Network> networkList = new HashMap<>();
    private final ConcurrentMap<UUID, Future<Void>> futureMap = new ConcurrentHashMap<>();

    private ObjectMapper objectMapper;

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

    public boolean isBusy(UUID networkId) {
        if(futureMap.containsKey(networkId) && futureMap.get(networkId).state().equals(Future.State.RUNNING)) {
            System.out.println(networkList.get(networkId).getAccumulatedSyncTrainedEpochs());
            return true;
        }

        return false;
    }

    public void predictAsync(UUID networkId, Dataset innerDataset) {
        computeAsync(networkId, () -> {
            synchronized (innerDataset) {
                networkList.get(networkId).predict(innerDataset);
            }
            return null;
        });
    }

    public void trainAsync(UUID networkId, int epochs) {
        computeAsync(networkId, () -> {
            networkList.get(networkId).train(epochs);
            return null;
        });
    }

    public void trainAsync(UUID networkId, int epochs, int pastWindowTime) {
        computeAsync(networkId, () -> {
            networkList.get(networkId).train(epochs, pastWindowTime);
            return null;
        });
    }

    public void mergeAsync(UUID networkId, Dataset innerDataset, int pastWindowTime, int epochRelationPercent) {
        computeAsync(networkId, () -> {
            synchronized (innerDataset) {
                networkList.get(networkId).merge(innerDataset, pastWindowTime, epochRelationPercent);
            }
            return null;
        });
    }

    private void computeAsync(UUID networkId, Callable<Void> callable) {
        if (isBusy(networkId)) {
            throw new RuntimeException("Network busy");
        } else {
            futureMap.put(networkId, executorService.submit(callable));
        }
    }
}
