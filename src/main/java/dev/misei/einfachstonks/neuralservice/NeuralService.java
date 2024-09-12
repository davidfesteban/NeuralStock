package dev.misei.einfachstonks.neuralservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@AllArgsConstructor
public class NeuralService {

    private final Map<UUID, Network> networkList = new HashMap<>();
    private final Map<UUID, CompletableFuture<Void>> futureMap = new HashMap<>();

    private ObjectMapper objectMapper;

    public void save(UUID networkId) throws IOException {
        objectMapper.writeValue(new File("networks.json"), networkList);
    }

    // Asynchronously compute the network and store the CompletableFuture in a map
    @Async
    public CompletableFuture<Void> computeNetworkAsync(UUID networkId) {
        Network network = networkList.get(networkId);
        if (network != null) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                //network.compute();
            });

            // Store the future in the map to track it
            futureMap.put(networkId, future);

            return future;
        } else {
            throw new IllegalArgumentException("Network not found for ID: " + networkId);
        }
    }

    // Check if the computation is still running
    public boolean isComputationRunning(UUID networkId) {
        CompletableFuture<Void> future = futureMap.get(networkId);
        return future != null && !future.isDone();
    }


    public void cancelComputation(UUID networkId) {
        CompletableFuture<Void> future = futureMap.get(networkId);
        if (future != null && !future.isDone()) {
            future.cancel(true); // Cancel the running task
        }
    }
}
