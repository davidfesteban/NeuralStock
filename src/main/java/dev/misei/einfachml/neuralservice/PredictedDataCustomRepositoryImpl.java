package dev.misei.einfachml.neuralservice;

import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
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
