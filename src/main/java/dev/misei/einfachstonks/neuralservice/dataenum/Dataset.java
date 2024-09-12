package dev.misei.einfachstonks.neuralservice.dataenum;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class Dataset {

    private final UUID uuid = UUID.randomUUID();
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
        return getAverageError(dataset.size());
    }

    public Double getAverageError(int windowTime) {
        var view = dataset.subList(Math.max(0, dataset.size() - windowTime), dataset.size());
        return view.stream()
                .map(Datapair::computeLastMSE)
                .reduce(Double::sum).get() / view.size();
    }

    public boolean isCompatible(Dataset dataset) {
        return dataset.inputSize == inputSize && dataset.outputSize == outputSize;
    }
}
