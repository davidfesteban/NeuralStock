package dev.misei.einfachml.neuralservice.domain.data;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public class Dataset extends ArrayList<Datapair> {

    private final UUID uuid = UUID.randomUUID();
    private final int outputSize;
    private final int inputSize;

    public Dataset(Datapair initial) {
        super();
        this.outputSize = initial.getOutputs().size();
        this.inputSize = initial.getInputs().size();
        this.add(initial);
    }

    public Dataset(List<Datapair> initial) {
        super(initial);
        this.outputSize = initial.getFirst().getOutputs().size();
        this.inputSize = initial.getFirst().getInputs().size();
    }

    //public Double getAverageError() {
    //    return getAverageError(dataset.size());
    //}
//
    //public Double getAverageError(int windowTime) {
    //    var view = dataset.subList(Math.max(0, dataset.size() - windowTime), dataset.size());
    //    return view.stream()
    //            .map(Datapair::computeLastMSE)
    //            .reduce(Double::sum).get() / view.size();
    //}

    public boolean isCompatible(Dataset dataset) {
        return dataset.inputSize == inputSize && dataset.outputSize == outputSize;
    }
}
