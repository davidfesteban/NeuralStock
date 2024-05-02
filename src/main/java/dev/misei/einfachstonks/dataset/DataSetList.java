package dev.misei.einfachstonks.dataset;

import lombok.Getter;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

@Getter
public class DataSetList {

    private final List<DataSet> dataSets = new ArrayList<>();
    private int inputSize;
    private int outputSize;

    public void accumulateTraining(DataSet dataSet) {
        Assert.isTrue(!dataSet.inputs().isEmpty() && !dataSet.outputs().isEmpty(), "DataSet cannot be empty");
        Assert.isTrue(dataSets.isEmpty() || (inputSize == dataSet.inputs().size() && outputSize == dataSet.outputs().size()),
                "Size must remain equals");

        dataSets.add(dataSet);
        inputSize = dataSet.inputs().size();
        outputSize = dataSet.outputs().size();
    }

}
