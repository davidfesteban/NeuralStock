package dev.misei.einfachml.repository;

import dev.misei.einfachml.repository.model.PredictedData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PredictedDataRepository extends MongoRepository<PredictedData, UUID> {
    List<PredictedData> findByNetworkId(UUID networkId);
    void deleteByNetworkId(UUID networkId);
}
