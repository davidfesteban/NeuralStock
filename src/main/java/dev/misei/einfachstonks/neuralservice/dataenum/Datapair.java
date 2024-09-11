package dev.misei.einfachstonks.neuralservice.dataenum;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
public class Datapair {
    private List<Double> inputs;
    private List<Double> outputs;

    private List<Double> predicted;

    public Datapair(List<Double> inputs, List<Double> outputs) {
        this.inputs = inputs;
        this.outputs = outputs;
        this.predicted = new ArrayList<>();
    }

    public Double computeTotalMSE() {
        Double totalLoss = 0d;

        for (int i = 0; i < predicted.size(); i++) {
            var output = predicted.get(i);
            var expected = outputs.get(i);

            var error = output - expected;
            totalLoss += Math.pow(error, 2);
        }

        // Return the mean of the squared errors
        return totalLoss / predicted.size();
    }
}
