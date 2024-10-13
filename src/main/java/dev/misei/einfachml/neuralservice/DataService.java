package dev.misei.einfachml.neuralservice;

import dev.misei.einfachml.repository.DataPairRepository;
import dev.misei.einfachml.repository.model.DataPair;
import dev.misei.einfachml.repository.model.NetworkBoard;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@AllArgsConstructor
@Slf4j
public class DataService {

    private DataPairRepository dataPairRepository;

    @Async
    public void includeDataset(NetworkBoard networkBoard, List<DataPair> dataPairList) {
        dataPairList.forEach(dataPair -> {

            if (dataPair.getInputs().size() != networkBoard.getAlgorithmBoard().getInputSize() ||
                    dataPair.getExpected().size() != networkBoard.getAlgorithmBoard().getOutputSize()) {
                throw new IllegalArgumentException("DataPair is not consistent");
            }

            dataPair.getInputs().forEach(aDouble -> {
                if (aDouble == null) {
                    throw new IllegalArgumentException("DataPair has null values");
                }
            });

            dataPair.getExpected().forEach(aDouble -> {
                if (aDouble == null) {
                    throw new IllegalArgumentException("DataPair has null values");
                }
            });

            dataPair.setNetworkId(networkBoard.getNetworkId());
        });

        log.info(String.format("Including DataSet for %s", networkBoard.getNetworkId()));
        dataPairRepository.saveAll(dataPairList);
    }

    @Async
    public CompletableFuture<List<DataPair>> retrieve(UUID networkId, Long createdAtStart, Long createdAtEnd, Integer lastAmount) {
        if(createdAtStart == null || createdAtEnd == null) {
            return CompletableFuture.completedFuture(dataPairRepository.findByNetworkId(networkId));
        } else if (lastAmount != null) {
            //TODO
        }
        return CompletableFuture.completedFuture(dataPairRepository.findByNetworkIdAndCreatedAtBetween(networkId, createdAtStart, createdAtEnd).stream().sorted().toList());
    }

    @Async
    public CompletableFuture<Void> cleanDatapair(UUID networkId) {
        dataPairRepository.deleteByNetworkId(networkId);
        return null;
    }

    @Async
    public void deleteDataSet(List<UUID> dataSetUUID) {
        dataSetUUID.forEach(uuid -> dataPairRepository.deleteById(uuid));
    }

    @Async
    public CompletableFuture<List<DataPair>> retrieveByPage(UUID networkId, Integer page) {
        //TODO
        return CompletableFuture.completedFuture(Collections.emptyList());
    }

}
