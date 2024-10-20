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

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;

@AllArgsConstructor
@Component
@Slf4j
public class MetricsPredictionRepositoryOperator {

    private PredictedDataRepositoryPerformance predictedDataRepositoryPerformance;
    private MetricsRepository metricsRepository;
    private final Map<UUID, Boolean> cleanUpRunning = new ConcurrentHashMap<>();

    public Flux<PredictedData> getAllPredictionsByNetwork(UUID networkId, Integer lastEpochAmount, Boolean downsample) {
        //TODO
        return Flux.empty();
    }

    public Flux<MSEData> getMSEData(UUID networkId) {
        return metricsRepository.countByNetworkId(networkId)
                .flux().concatMap(
                        (Function<Long, Flux<MSEData>>) aLong -> metricsRepository.findByNetworkIdOrderByEpochHappenedAsc(networkId)
                                .buffer(aLong.intValue() / 100)
                                .concatMap(mseData -> {
                                    var mseDataAcc = mseData.stream().reduce((BinaryOperator<MSEData>) (mseData1, mseData2) -> {
                                        mseData1.setError(mseData1.getError() + mseData2.getError());
                                        return mseData1;
                                    }).orElse(new MSEData(networkId, 0, 0d));

                                    mseDataAcc.setError(mseDataAcc.getError() / mseData.size());
                                    return Mono.just(mseDataAcc);
                                }));
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

    //TODO: Mostrar el error maximo y average de todas las predicciones.
    // Mostrar un valor como el error average de la ultima epoca y maximo

    private Mono<Void> saveGroupedMSEData(Flux<List<PredictedData>> predictedDataFlux) {
        return predictedDataFlux.concatMap(batch -> {
            //double mseErrorSum = batch.stream().mapToDouble(PredictedData::getMseError).sum();
            double mseErrorSum = batch.stream().mapToDouble(PredictedData::getMseError).max().getAsDouble();

            int count = batch.size();
            //double mseErrorAverage = mseErrorSum / count;
            UUID networkId = batch.get(0).getNetworkId();

            MSEData mseData = new MSEData(networkId, batch.get(0).getEpochHappened(), mseErrorSum);
            return Mono.just(mseData);
        }).as(mseDataFlux -> {
            return metricsRepository.saveAll(mseDataFlux);
        }).then();
    }

    private Mono<Void> saveBatchByNetworkId(Flux<List<PredictedData>> predictedDataFlux) {
        return predictedDataFlux.then();
        /*
        return predictedDataFlux
                .collect(() -> new ArrayDeque<List<PredictedData>>(2), (deque, batch) -> {
                    if (deque.size() == 2) {
                        deque.poll();
                    }
                    deque.offer(batch);
                })
                .flatMap(deque -> {
                    if(deque.isEmpty()) return Mono.empty();

                    UUID networkId = deque.peekFirst().get(0).getNetworkId();
                    return predictedDataRepositoryPerformance.deleteByNetworkId(networkId)
                            .then(
                                    Flux.fromIterable(deque)
                                            .concatMap(batch -> predictedDataRepositoryPerformance.saveBatchByNetworkId(batch.get(0).getNetworkId(), batch))
                                            .then()
                            );
                }); */
    }
}
