package dev.misei.einfachstonks.neuralservice.network;

import dev.misei.einfachstonks.neuralservice.dataset.DataSetList;
import dev.misei.einfachstonks.neuralservice.math.Algorithm;
import dev.misei.einfachstonks.neuralservice.math.ErrorMeasure;
import lombok.Getter;

@Getter
public class NetworkFactory {

    private NetworkFactory() {
    }

    public static Network create(DataSetList dataSetList, Context context, int neuronsPerHiddenLayer, int totalHiddenLayers, Algorithm algorithm, ErrorMeasure errorMeasure) {
        return new Network(dataSetList, context, algorithm, errorMeasure).init(neuronsPerHiddenLayer, totalHiddenLayers);
    }
}
