package dev.misei.einfachml.neuralservice;

import dev.misei.einfachml.neuralservice.operator.ComputeFluxOperator;
import dev.misei.einfachml.neuralservice.operator.MetricsPredictionRepositoryOperator;
import dev.misei.einfachml.repository.model.DataPair;
import dev.misei.einfachml.repository.model.PredictedData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@Slf4j
@AllArgsConstructor
@Getter
public class ComputeService {

    private NetworkLoadService networkLoadService;
    private ComputeFluxOperator networkFluxOperator;
    private MetricsPredictionRepositoryOperator metricsPredictionRepositoryOperator;

    public Mono<Void> computeElasticAsync(UUID networkId, Flux<DataPair> dataset, int epochs) {
        return networkLoadService.get(networkId)
                .flatMap(network -> {
                    return networkFluxOperator.compute(network, dataset, epochs, networkBackup -> {
                                networkLoadService.saveNetworkBackup(networkBackup).subscribe();
                            })
                            .publish(flux -> {
                                return metricsPredictionRepositoryOperator.processOrderedEpochPredictions(flux);
                            })
                            .then(networkLoadService.get(networkId).flatMap(networkLoad -> {
                                return networkLoadService.saveNetworkBackup(networkLoad);
                            }))
                            .onErrorResume(error -> {
                                log.error("Error occurred during execution: " + error.getMessage());
                                return networkLoadService.restore(networkId, error);
                            })
                            .then();
                });
    }


    public Flux<PredictedData> predictElasticAsync(UUID networkId, Flux<DataPair> dataSet) {
        return networkLoadService.get(networkId).flatMapMany(network -> {
            return networkFluxOperator.predict(network, dataSet);
        });
    }
}
