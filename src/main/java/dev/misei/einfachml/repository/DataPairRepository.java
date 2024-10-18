package dev.misei.einfachml.repository;

import dev.misei.einfachml.repository.model.DataPair;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface DataPairRepository extends ReactiveMongoRepository<DataPair, UUID> {
    void deleteByTopicIgnoreCase(String topic);
    Flux<DataPair> findByTopicIgnoreCaseAndCreatedAtBetweenOrderByCreatedAtAsc(String topic, long createdAtStart, long createdAtEnd);
    Flux<DataPair> findByTopicIgnoreCaseOrderByCreatedAtAsc(String topic);
    Flux<DataPair> findByNetworkIdAndCreatedAtBetweenOrderByCreatedAtAsc(UUID networkId, long createdAtStart, long createdAtEnd);

    Flux<DataPair> findByNetworkIdOrderByCreatedAtAsc(UUID networkId);
    Flux<DataPair> findByNetworkIdOrderByCreatedAtDesc(UUID networkId);

    Mono<Long> countByNetworkId(UUID networkId);

    Flux<DataPair> deleteByNetworkId(UUID networkId);

    Flux<DataPair> findByNetworkIdAndCreatedAtBetween(UUID networkId, long createdAtStart, long createdAtEnd);

    Flux<DataPair> findByNetworkId(UUID networkId);

    Flux<DataPair> findByTopicIgnoreCaseOrderByCreatedAtDesc(String topic);
}
