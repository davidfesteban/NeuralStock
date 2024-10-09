package dev.misei.einfachstonks.neuralservice;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
@AllArgsConstructor
public class PredictedDataCustomRepositoryImpl implements PredictedDataCustomRepository {

    MongoTemplate mongoTemplate;
    @Override
    public <T> Collection<T> saveAllOnCollectionName(Collection<? extends T> batchToSave, String collectionName) {
        return mongoTemplate.insert(batchToSave, collectionName);
    }
}
