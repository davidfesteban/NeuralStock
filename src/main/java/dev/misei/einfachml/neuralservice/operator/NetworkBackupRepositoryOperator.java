package dev.misei.einfachml.neuralservice.operator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.misei.einfachml.neuralservice.domain.Network;
import dev.misei.einfachml.repository.NetworkBackupRepository;
import dev.misei.einfachml.repository.model.NetworkBackup;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.UUID;

@AllArgsConstructor
@Component
@Slf4j
public class NetworkBackupRepositoryOperator {

    private NetworkBackupRepository networkBackupRepository;
    private ObjectMapper objectMapper;

    public Mono<Void> saveNetworkBackup(Network network) {

        UUID networkId = network.getStatus().getNetworkId();

        //TODO: There is no need of delete. ID is always the same. It will override it!
        return networkBackupRepository.deleteById(networkId)
                .then(Mono.defer(() -> {
                    NetworkBackup newBackup = new NetworkBackup(networkId, null);
                    try {
                        log.info("Start Serialising Backup");
                        newBackup.setNetwork(objectMapper.writeValueAsString(network));
                        log.info("Finished Serialising Backup");
                    } catch (JsonProcessingException e) {
                        return Mono.error(new RuntimeException("Failed to serialize network", e));
                    }

                    return networkBackupRepository.save(newBackup)
                            .doOnNext(a -> log.info("Saved Backup Repository"))
                            .doOnTerminate(() -> log.info("Terminated Backup"))
                            .then();
                }));
    }

    public Mono<Network> retrieveNetworkBackup(UUID networkId) {
        return networkBackupRepository.findById(networkId)
                .flatMap(networkBackup -> {
                    try {
                        Network network = objectMapper.readValue(networkBackup.getNetwork(), Network.class);
                        return Mono.just(network);
                    } catch (JsonProcessingException e) {
                        return Mono.error(new RuntimeException("Failed to parse network backup", e));
                    }
                });
    }

    public Mono<Void> deleteByNetwork(Network network) {
        if (network.getStatus().isRunning()) {
            return Mono.error(new IllegalStateException("Network still running"));
        }

        return networkBackupRepository.deleteById(network.getStatus().getNetworkId());
    }
}
