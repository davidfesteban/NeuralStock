package dev.misei.einfachstonks.neuralservice.dataenum;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
public class Dataset {

    private final List<Datapair> dataset = new ArrayList<>();
    private final int outputSize;
    private final int inputSize;

    public Dataset(Datapair initial) {
        this.outputSize = initial.getOutputs().size();
        this.inputSize = initial.getInputs().size();
        dataset.add(initial);
    }

    public Dataset(List<Datapair> initial) {
        this.outputSize = initial.getFirst().getOutputs().size();
        this.inputSize = initial.getFirst().getInputs().size();
        dataset.addAll(initial);
    }

    public Double getAverageError() {
        return dataset.stream()
                .map(Datapair::computeTotalMSE)
                .reduce(Double::sum).get() / dataset.size();
    }
}
