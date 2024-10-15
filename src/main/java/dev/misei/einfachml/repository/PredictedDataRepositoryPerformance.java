package dev.misei.einfachml.repository;

import dev.misei.einfachml.repository.model.PredictedData;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface PredictedDataRepositoryPerformance {
    Mono<Void> saveBatchByNetworkId(UUID networkId, Flux<PredictedData> batch);
    Flux<PredictedData> findByNetworkIdAndEpochHappenedBetween(UUID networkId, int epochHappenedStart, int epochHappenedEnd);
    Flux<PredictedData> findAllByNetworkId(UUID networkId);
    Mono<Long> countByNetworkId(UUID networkId);
    Mono<Void> deleteByNetworkId(UUID networkId);
}
