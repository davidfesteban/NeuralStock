package dev.misei.einfachml.repository;

import dev.misei.einfachml.repository.model.DataPair;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DataPairRepository extends MongoRepository<DataPair, UUID> {
    List<DataPair> findByNetworkIdOrderByCreatedAtAsc(UUID networkId, Pageable pageable);
    long countByNetworkId(UUID networkId);
    List<DataPair> deleteByNetworkId(UUID networkId);
    List<DataPair> findByNetworkIdAndCreatedAtBetween(UUID networkId, long createdAtStart, long createdAtEnd);
    List<DataPair> findByNetworkId(UUID networkId);
}
