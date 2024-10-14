package dev.misei.einfachml.repository;

import dev.misei.einfachml.repository.model.PredictedData;

import java.util.List;
import java.util.UUID;

public interface PredictedDataRepositoryPerformance {
    void saveBatchByNetworkId(UUID networkId, List<PredictedData> batch);
    List<PredictedData> findByNetworkIdAndEpochHappenedBetween(UUID networkId, int epochHappenedStart, int epochHappenedEnd);
    List<PredictedData> findAllByNetworkId(UUID networkId);
    long countByNetworkId(UUID networkId);
    void deleteByNetworkId(UUID networkId);
}
