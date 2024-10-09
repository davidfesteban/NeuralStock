package dev.misei.einfachml.neuralservice;

import dev.misei.einfachml.neuralservice.model.PredictedData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PredictedDataRepository extends MongoRepository<PredictedData, UUID>, PredictedDataCustomRepository {
}
