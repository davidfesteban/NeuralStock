package dev.misei.einfachstonks.network;

import dev.misei.einfachstonks.dataset.DataSetList;
import dev.misei.einfachstonks.math.Algorithm;
import dev.misei.einfachstonks.math.ErrorMeasure;
import lombok.Getter;

@Getter
public class NetworkFactory {

    public NetworkFactory() {
    }

    public static Network create(DataSetList dataSetList, Context context, int neuronsPerHiddenLayer, int totalHiddenLayers, Algorithm algorithm, ErrorMeasure errorMeasure) throws CloneNotSupportedException {
        return new Network(dataSetList, context, algorithm, errorMeasure).init(neuronsPerHiddenLayer, totalHiddenLayers);
    }
}
