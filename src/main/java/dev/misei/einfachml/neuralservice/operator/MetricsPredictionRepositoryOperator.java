package dev.misei.einfachml.neuralservice.operator;

import dev.misei.einfachml.repository.MetricsRepository;
import dev.misei.einfachml.repository.PredictedDataRepositoryPerformance;
import dev.misei.einfachml.repository.model.MSEData;
import dev.misei.einfachml.repository.model.PredictedData;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@AllArgsConstructor
@Component
@Slf4j
public class MetricsPredictionRepositoryOperator {

    private PredictedDataRepositoryPerformance predictedDataRepositoryPerformance;
    private MetricsRepository metricsRepository;

    public Flux<PredictedData> getAllPredictionsByNetwork(UUID networkId, Integer lastEpochAmount, Boolean downsample) {
        //TODO
        return Flux.empty();
    }

    public Flux<MSEData> getMSEData(UUID networkId) {
        return metricsRepository.findByNetworkIdOrderByEpochHappenedAsc(networkId);
    }

    public Mono<Void> processOrderedEpochPredictions(Flux<PredictedData> predictedDataFlux) {
        AtomicInteger currentEpoch = new AtomicInteger(-1);

        return predictedDataFlux.bufferUntil(predictedData -> {
            int epoch = predictedData.getEpochHappened();
            if (currentEpoch.get() == -1) {
                currentEpoch.set(epoch);
            }

            boolean isNewEpoch = epoch != currentEpoch.get();
            if (isNewEpoch) {
                currentEpoch.set(epoch);
            }
            return isNewEpoch;
        }, true).doOnNext(predictedList -> {
            if (predictedList.stream().anyMatch(predictedData -> predictedData.getEpochHappened() != predictedList.getFirst().getEpochHappened())) {
                log.error("Epochs are in different groups while processing!");
            }
        }).publish(flux -> {
            return Mono.when(saveBatchByNetworkId(flux), saveGroupedMSEData(flux));
        }).then();

    }


    private Mono<Void> saveGroupedMSEData(Flux<List<PredictedData>> predictedDataFlux) {
        return predictedDataFlux.concatMap(batch -> {
            double mseErrorSum = batch.stream().mapToDouble(PredictedData::getMseError).sum();
            int count = batch.size();
            double mseErrorAverage = mseErrorSum / count;
            UUID networkId = batch.get(0).getNetworkId();

            MSEData mseData = new MSEData(networkId, batch.get(0).getEpochHappened(), mseErrorAverage);
            return Mono.just(mseData);
        }).as(mseDataFlux -> {
            return metricsRepository.saveAll(mseDataFlux);
        }).then();
    }

    private Mono<Void> saveBatchByNetworkId(Flux<List<PredictedData>> predictedDataFlux) {
        return predictedDataFlux.concatMap(batch -> {
            return predictedDataRepositoryPerformance.saveBatchByNetworkId(batch.getFirst().getNetworkId(), batch);
        }).then();
    }
}
