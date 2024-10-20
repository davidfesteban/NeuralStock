package dev.misei.einfachml.repository.impl;

import dev.misei.einfachml.repository.PredictedDataRepositoryPerformance;
import dev.misei.einfachml.repository.model.PredictedData;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Repository
@AllArgsConstructor
@Slf4j
public class PredictedDataRepositoryPerformanceImpl implements PredictedDataRepositoryPerformance {

    private ReactiveMongoTemplate mongoTemplate;

    @Override
    public Mono<Void> saveBatchByNetworkId(UUID networkId, List<PredictedData> batch) {
        return mongoTemplate.insert(batch, networkId.toString()).then();
    }

    @Override
    public Mono<Long> countDistinctEpochHappened(UUID networkId) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.group("epochHappened")
        );

        return mongoTemplate.aggregate(aggregation, networkId.toString(), Object.class).count();
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
        return mongoTemplate.findAll(PredictedData.class, networkId.toString()).sort();
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
    public Mono<Void> deleteOlderEpochsKeep2ByNetworkId(UUID networkId) {
        Query query = new Query()
                .with(Sort.by(Sort.Direction.DESC, "epochHappened"))
                .skip(2);

        return mongoTemplate.remove(query, networkId.toString()).then();
    }
}