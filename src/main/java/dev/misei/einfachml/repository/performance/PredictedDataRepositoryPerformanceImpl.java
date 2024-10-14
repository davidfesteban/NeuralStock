package dev.misei.einfachml.repository.performance;

import dev.misei.einfachml.repository.PredictedDataRepositoryPerformance;
import dev.misei.einfachml.repository.model.PredictedData;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Repository
@AllArgsConstructor
public class PredictedDataRepositoryPerformanceImpl implements PredictedDataRepositoryPerformance {

    private ReactiveMongoTemplate mongoTemplate;

    @Override
    public Flux<PredictedData> saveBatchByNetworkId(UUID networkId, List<PredictedData> batch) {
        return mongoTemplate.insert(batch, networkId.toString());
    }

    @Override
    public Flux<PredictedData> findByNetworkIdAndEpochHappenedBetween(UUID networkId, int epochHappenedStart, int epochHappenedEnd) {
        Query query = new Query();
        query.addCriteria(Criteria.where("epochHappened").gte(epochHappenedStart).lte(epochHappenedEnd));
        //query.with(Sort.by(Sort.Direction.ASC, "createdAt")); // Sort by createdAt in ascending order

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
}