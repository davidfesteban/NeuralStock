package dev.misei.einfachstonks.neuralservice;

import java.util.Collection;

public interface PredictedDataCustomRepository {
    public <T> Collection<T> saveAllOnCollectionName(Collection<? extends T> batchToSave, String collectionName);
}
