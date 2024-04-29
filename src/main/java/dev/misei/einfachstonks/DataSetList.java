package dev.misei.einfachstonks;

import lombok.Getter;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

@Getter
public class DataSetList {

    private final List<DataSet> dataSets = new ArrayList<>();
    private int inputSize;
    private int outputSize;

    void accumulateTraining(DataSet dataSet) {
        if (!dataSets.isEmpty()) {
            Assert.isTrue(inputSize == dataSet.inputs().size(), "Size must remain equals");
            Assert.isTrue(outputSize == dataSet.outputs().size(), "Size must remain equals");
        }

        dataSets.add(dataSet);
        inputSize = dataSet.inputs().size();
        outputSize = dataSet.outputs().size();
    }

}
