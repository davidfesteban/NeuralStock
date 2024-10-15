package dev.misei.einfachml.neuralservice;

import dev.misei.einfachml.repository.DataPairRepository;
import dev.misei.einfachml.repository.model.DataPair;
import dev.misei.einfachml.repository.model.NetworkBoard;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class DataService {

    private DataPairRepository dataPairRepository;

    public Mono<Void> includeDataset(NetworkBoard networkBoard, Flux<DataPair> dataPairList) {
        return dataPairList.flatMapSequential(dataPair -> {
            // Check if inputs are null
            if (dataPair.getInputs() == null) {
                return Mono.error(new IllegalArgumentException("DataPair inputs cannot be null"));
            }

            // Validate the input size
            if (dataPair.getInputs().size() != networkBoard.getAlgorithmBoard().getInputSize()) {
                return Mono.error(new IllegalArgumentException("DataPair input size is not consistent"));
            }

            // Validate the expected size
            if (dataPair.getExpected().size() != networkBoard.getAlgorithmBoard().getOutputSize()) {
                return Mono.error(new IllegalArgumentException("DataPair expected size is not consistent"));
            }

            dataPair.getInputs().forEach(input -> {
                if (input == null) {
                    throw new IllegalArgumentException("DataPair has null values in inputs");
                }
            });

            dataPair.getExpected().forEach(output -> {
                if (output == null) {
                    throw new IllegalArgumentException("DataPair has null values in expected");
                }
            });

            dataPair.setNetworkId(networkBoard.getNetworkId());

            return Mono.just(dataPair);
        }).as(dataPairFlux -> dataPairRepository.saveAll(dataPairFlux)).then();
    }

    public Flux<DataPair> retrieve(UUID networkId, Long createdAtStart, Long createdAtEnd, Integer lastAmount) {
        if (createdAtStart == null || createdAtEnd == null) {
            return dataPairRepository.findByNetworkIdOrderByCreatedAtAsc(networkId);
        } else if (lastAmount != null) {
            return dataPairRepository.findByNetworkIdOrderByCreatedAtAsc(networkId)
                    .sort(Comparator.comparing(DataPair::getCreatedAt).reversed()) //DESC
                    .take(lastAmount)
                    .sort(Comparator.comparing(DataPair::getCreatedAt)); //ASC
        }
        return dataPairRepository.findByNetworkIdAndCreatedAtBetweenOrderByCreatedAtAsc(networkId, createdAtStart, createdAtEnd);
    }

    public Mono<Void> cleanDatapair(UUID networkId) {
        return dataPairRepository.deleteByNetworkId(networkId).then();
    }

    public Mono<Void> deleteDataSet(Flux<UUID> dataSetUUID) {
        return dataSetUUID
                .flatMap(dataPairRepository::deleteById)  // Reactive delete for each UUID
                .then();
    }

    public Flux<DataPair> retrieveAll(UUID networkId) {
        return dataPairRepository.findByNetworkIdOrderByCreatedAtAsc(networkId);
    }

    public Mono<Long> countByNetworkId(UUID networkId) {
        return dataPairRepository.countByNetworkId(networkId);
    }
}
