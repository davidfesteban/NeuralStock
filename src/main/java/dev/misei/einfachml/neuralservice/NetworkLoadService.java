package dev.misei.einfachml.neuralservice;

import dev.misei.einfachml.neuralservice.domain.Network;
import dev.misei.einfachml.neuralservice.operator.NetworkBackupRepositoryOperator;
import dev.misei.einfachml.neuralservice.operator.NetworkSummaryOperator;
import dev.misei.einfachml.repository.model.NetworkBoard;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class NetworkLoadService {
    private final Map<UUID, Network> networkList = new HashMap<>();

    public NetworkBackupRepositoryOperator networkBackupRepositoryOperator;
    public NetworkSummaryOperator networkSummaryOperator;

    @EventListener(ApplicationReadyEvent.class)
    public void reload() {
        networkSummaryOperator.getAllSummaries()
                .collectList()
                .flatMapMany(Flux::fromIterable)
                .flatMap(networkBoard -> restore(networkBoard.getNetworkId(), null))
                .blockLast();
    }

    public Mono<UUID> load(UUID uuid, Network network, boolean reconnect) {
        return Mono.defer(() -> {
            if (reconnect) network.reconnectAll();

            networkList.put(uuid, network);
            return Mono.when(networkSummaryOperator.saveSummary(network),
                            networkBackupRepositoryOperator.saveNetworkBackup(network))
                    .then(Mono.just(uuid));
        });
    }

    public Mono<Network> delete(UUID networkId) {
        Network network = networkList.get(networkId);

        if (network.getStatus().isRunning()) {
            return Mono.error(new IllegalStateException("Network still running"));
        }

        return Mono.when(networkSummaryOperator.deleteSummary(networkId),
                        networkBackupRepositoryOperator.deleteByNetwork(network))
                .then(Mono.just(networkList.remove(networkId)));
    }

    public Mono<Network> get(UUID networkId) {
        return Mono.just(networkList.get(networkId));
    }

    public Flux<NetworkBoard> getAllSummariesEnriched() {
        return networkSummaryOperator.getAllSummaries().handle((networkBoard, sink) -> {
            Network network = networkList.get(networkBoard.getNetworkId());
            networkBoard.setStatus(network.getStatus());
            sink.next(networkBoard);
        });
    }

    public Mono<Void> saveNetworkBackup(Network networkBackup) {
        return Mono.when(networkBackupRepositoryOperator.saveNetworkBackup(networkBackup),
                networkSummaryOperator.saveSummary(networkBackup));
    }

    public Mono<Void> restore(UUID networkId, Throwable throwable) {
        var restore = networkBackupRepositoryOperator.retrieveNetworkBackup(networkId)
                .flatMap(network -> {
                    network.reconnectAll();
                    network.getStatus().setRunning(false);
                    networkList.put(networkId, network);
                    return networkSummaryOperator.saveSummary(network);
                });

        if (throwable != null) {
            return restore.then(Mono.error(throwable));
        }

        return restore.then();
    }
}
