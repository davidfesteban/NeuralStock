package dev.misei.einfachml.repository;

import dev.misei.einfachml.repository.model.DataPair;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface DataPairRepository extends ReactiveMongoRepository<DataPair, UUID> {
    Mono<Void> deleteByTopicIgnoreCase(String topic);

    Flux<DataPair> findByTopicIgnoreCaseAndCreatedAtBetweenOrderByCreatedAtAsc(String topic, long createdAtStart, long createdAtEnd);

    Flux<DataPair> findByTopicIgnoreCaseOrderByCreatedAtAsc(String topic);

    Flux<DataPair> findByTopicIgnoreCaseOrderByCreatedAtDesc(String topic);
}
