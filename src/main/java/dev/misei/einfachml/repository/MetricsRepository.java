package dev.misei.einfachml.repository;

import dev.misei.einfachml.repository.model.MSEData;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface MetricsRepository extends ReactiveMongoRepository<MSEData, String> {
    Mono<Long> countByNetworkId(UUID networkId);
    Flux<MSEData> findByNetworkIdOrderByEpochHappenedAsc(UUID networkId);

}
