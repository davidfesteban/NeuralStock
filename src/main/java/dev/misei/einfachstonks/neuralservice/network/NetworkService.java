package dev.misei.einfachstonks.neuralservice.network;

import dev.misei.einfachstonks.neuralservice.dataset.DataSet;

import java.util.List;

public interface NetworkService {
    void train(int totalEpochs);
    List<Double> predict(List<Double> inputs);
    void accumulateDataset(DataSet dataSet);
}
