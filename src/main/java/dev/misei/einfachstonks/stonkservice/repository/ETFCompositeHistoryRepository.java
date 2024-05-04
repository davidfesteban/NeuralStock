package dev.misei.einfachstonks.stonkservice.repository;

import dev.misei.einfachstonks.stonkservice.model.ETFCompositeHistory;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface ETFCompositeHistoryRepository extends MongoRepository<ETFCompositeHistory, String> {
    ETFCompositeHistory findByEtfCompositeId(UUID etfCompositeId);
    void deleteByEtfCompositeId(UUID etfCompositeId);
}
