package dev.misei.einfachml.repository;

import dev.misei.einfachml.repository.model.PredictedData;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Repository
public interface PredictedDataRepository extends MongoRepository<PredictedData, UUID> {
    long countByNetworkId(UUID networkId);
    @Async
    CompletableFuture<List<PredictedData>> findByNetworkIdAndEpochHappenedBetweenOrderByCreatedAtAsc(UUID networkId, int epochHappenedStart, int epochHappenedEnd);
    List<PredictedData> findByNetworkIdAndEpochHappenedBetween(UUID networkId, int epochHappenedStart, int epochHappenedEnd);
    List<PredictedData> findByEpochHappened(int epochHappened, Pageable pageable);
    List<PredictedData> findByNetworkId(UUID networkId);
    void deleteByNetworkId(UUID networkId);
}
