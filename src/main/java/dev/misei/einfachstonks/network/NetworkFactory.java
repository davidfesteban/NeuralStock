package dev.misei.einfachstonks.network;

import dev.misei.einfachstonks.math.Algorithm;
import dev.misei.einfachstonks.dataset.DataSetList;
import dev.misei.einfachstonks.math.ErrorMeasure;

public class NetworkFactory {
    private final DataSetList dataSetList;
    private final Context context;
    private final Algorithm algorithm;
    private final ErrorMeasure errorMeasure;

    public NetworkFactory(DataSetList dataSetList, Context context, Algorithm algorithm, ErrorMeasure errorMeasure) {
        this.dataSetList = dataSetList;
        this.context = context;
        this.algorithm = algorithm;
        this.errorMeasure = errorMeasure;
    }

    public Network create(int neuronsPerHiddenLayer, int totalHiddenLayers) {
        return new Network(dataSetList, context, algorithm, errorMeasure).init(neuronsPerHiddenLayer, totalHiddenLayers);
    }
}
