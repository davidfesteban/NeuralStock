package dev.misei.einfachstonks.neuralservice;

import dev.misei.einfachstonks.neuralservice.model.PredictedData;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface PredictedDataRepository extends MongoRepository<PredictedData, UUID> {
}
