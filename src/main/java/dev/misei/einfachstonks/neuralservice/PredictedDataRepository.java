package dev.misei.einfachstonks.neuralservice;

import dev.misei.einfachstonks.neuralservice.model.PredictedData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PredictedDataRepository extends MongoRepository<PredictedData, UUID>, PredictedDataCustomRepository {
}
