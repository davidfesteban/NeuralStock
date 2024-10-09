package dev.misei.einfachstonks.neuralservice.domain.data;

import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
public class Datapair {

    private final UUID uuid = UUID.randomUUID();
    private List<Double> inputs;
    private List<Double> outputs;

    public Datapair(List<Double> inputs, List<Double> outputs) {
        this.inputs = inputs;
        this.outputs = outputs;
    }

    //public Double computeLastMSE() {
    //    var lastPredicted = predictedHistory.getLast();
    //    Double totalLoss = 0d;
//
    //    for (int i = 0; i < lastPredicted.size(); i++) {
    //        var output = lastPredicted.get(i);
    //        var expected = outputs.get(i);
//
    //        var error = output - expected;
    //        totalLoss += Math.pow(error, 2);
    //    }
//
    //    return totalLoss / lastPredicted.size();
    //}
}
