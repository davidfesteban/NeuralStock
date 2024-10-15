package dev.misei.einfachml.repository.performance;

import dev.misei.einfachml.repository.PredictedDataRepositoryPerformance;
import dev.misei.einfachml.repository.model.PredictedData;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.function.Function;

@Repository
@AllArgsConstructor
@Slf4j
public class PredictedDataRepositoryPerformanceImpl implements PredictedDataRepositoryPerformance {

    private ReactiveMongoTemplate mongoTemplate;

    @Override
    public Mono<Void> saveBatchByNetworkId(UUID networkId, Flux<PredictedData> batch) {
        return mongoTemplate.<PredictedData>insertAll(batch.collectList(), networkId.toString()).then();
    }

    @Override
    public Flux<PredictedData> findByNetworkIdAndEpochHappenedBetween(UUID networkId, int epochHappenedStart, int epochHappenedEnd) {
        Query query = new Query();
        query.addCriteria(Criteria.where("epochHappened").gte(epochHappenedStart).lte(epochHappenedEnd));
        query.with(Sort.by(Sort.Direction.ASC, "createdAt"));

        return mongoTemplate.find(query, PredictedData.class, networkId.toString());
    }

    @Override
    public Flux<PredictedData> findAllByNetworkId(UUID networkId) {
        return mongoTemplate.findAll(PredictedData.class, networkId.toString());
    }

    @Override
    public Mono<Long> countByNetworkId(UUID networkId) {
        return mongoTemplate.estimatedCount(networkId.toString());
    }

    @Override
    public Mono<Void> deleteByNetworkId(UUID networkId) {
        return mongoTemplate.dropCollection(networkId.toString());
    }

    @Override
    public Mono<Void> deleteAll() {
        return mongoTemplate.getCollectionNames().flatMap((Function<String, Publisher<?>>) s -> mongoTemplate.dropCollection(s)).then();
    }
}