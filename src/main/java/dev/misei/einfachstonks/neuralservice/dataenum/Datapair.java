package dev.misei.einfachstonks.neuralservice.dataenum;

import lombok.Getter;

import java.util.*;

@Getter
public class Datapair {

    private final UUID uuid = UUID.randomUUID();
    private List<Double> inputs;
    private List<Double> outputs;

    private List<List<Double>> predictedHistory;

    public Datapair(List<Double> inputs, List<Double> outputs) {
        this.inputs = inputs;
        this.outputs = outputs;
        this.predictedHistory = new ArrayList<>();
    }

    public Double computeLastMSE() {
        var lastPredicted = predictedHistory.getLast();
        Double totalLoss = 0d;

        for (int i = 0; i < lastPredicted.size(); i++) {
            var output = lastPredicted.get(i);
            var expected = outputs.get(i);

            var error = output - expected;
            totalLoss += Math.pow(error, 2);
        }

        // Return the mean of the squared errors
        return totalLoss / lastPredicted.size();
    }
}
