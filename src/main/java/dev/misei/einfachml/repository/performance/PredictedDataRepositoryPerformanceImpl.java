package dev.misei.einfachml.repository.performance;

import dev.misei.einfachml.repository.PredictedDataRepositoryPerformance;
import dev.misei.einfachml.repository.model.PredictedData;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@AllArgsConstructor
public class PredictedDataRepositoryPerformanceImpl implements PredictedDataRepositoryPerformance {

    private MongoTemplate mongoTemplate;

    @Override
    public void saveBatchByNetworkId(UUID networkId, List<PredictedData> batch) {
        mongoTemplate.insert(batch, networkId.toString());
    }

    @Override
    public List<PredictedData> findByNetworkIdAndEpochHappenedBetween(UUID networkId, int epochHappenedStart, int epochHappenedEnd) {
        Query query = new Query();
        query.addCriteria(Criteria.where("epochHappened").gte(epochHappenedStart).lte(epochHappenedEnd));
        //query.with(Sort.by(Sort.Direction.ASC, "createdAt")); // Sort by createdAt in ascending order

        return mongoTemplate.find(query, PredictedData.class, networkId.toString());
    }
/*
    public void findInBatches(UUID networkId) {
        MongoCursor<PredictedData> cursor = mongoTemplate.getCollection(networkId.toString())
                .find()
                .batchSize(100) // Fetch data in batches
                .iterator();

        while (cursor.hasNext()) {
            PredictedData data = cursor.next();
            // Process each document here
            System.out.println(data);
        }
    }
*/
    @Override
    public List<PredictedData> findAllByNetworkId(UUID networkId) {
        return mongoTemplate.findAll(PredictedData.class, networkId.toString());
    }

    @Override
    public long countByNetworkId(UUID networkId) {
        return mongoTemplate.estimatedCount(networkId.toString());
    }

    @Override
    public void deleteByNetworkId(UUID networkId) {
        mongoTemplate.getCollection(networkId.toString()).drop();
    }
}