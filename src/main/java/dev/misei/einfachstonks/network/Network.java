package dev.misei.einfachstonks.network;

import dev.misei.einfachstonks.dataset.DataSet;
import dev.misei.einfachstonks.dataset.DataSetList;
import dev.misei.einfachstonks.layer.HiddenLayer;
import dev.misei.einfachstonks.layer.InputLayer;
import dev.misei.einfachstonks.layer.Layer;
import dev.misei.einfachstonks.layer.OutputLayer;
import dev.misei.einfachstonks.math.Algorithm;
import dev.misei.einfachstonks.math.ErrorMeasure;
import dev.misei.einfachstonks.neuron.Axon;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Slf4j
public class Network extends NetworkLifecycle {

    private final List<Layer> layers;
    private final DataSetList dataSetList;
    private final Context context;
    private final Algorithm algorithm;
    private final ErrorMeasure errorMeasure;

    Network(DataSetList dataSetList, Context context, Algorithm algorithm, ErrorMeasure errorMeasure) {
        this.layers = new ArrayList<>();
        this.dataSetList = dataSetList;
        this.context = context;
        this.algorithm = algorithm;
        this.errorMeasure = errorMeasure;
    }

    Network init(int neuronsPerHiddenLayer, int totalHiddenLayers) {
        addLayer(new InputLayer(), dataSetList.getInputSize(), 0);
        addLayer(new OutputLayer(), dataSetList.getOutputSize(), 1);
        addAllHidden(neuronsPerHiddenLayer, totalHiddenLayers);
        connectAll();
        return this;
    }

    public void train(int totalEpochs) {
        for (int epoch = 0; epoch < totalEpochs; epoch++) {
            log.info(String.format("==== Epoch-Iteration: %d", epoch));

            dataSetList.getDataSets().forEach(this::compute);
        }
    }

    public List<Double> predict(DataSet dataSet) {
        layers.getFirst().inject(dataSet.inputs());
        computeForward(context);
        layers.getLast().getAxons().forEach(new Consumer<Axon>() {
            @Override
            public void accept(Axon axon) {
                System.out.println(context.neuronOutput.get(axon.getId()));
            }
        });
        return null;
    }

    public void compute(DataSet dataSet) {
        layers.getFirst().inject(dataSet.inputs());
        layers.getLast().inject(dataSet.outputs());
        compute(context);
        context.weightedGradient.clear();
    }

    @Override
    public void computeForward(Context context) {
        layers.forEach(layer -> layer.computeForward(context));
    }

    @Override
    public void computeBackward(Context context) {
        layers.reversed().forEach(layer -> layer.computeBackward(context));
    }


    private void addLayer(Layer layer, int totalNeurons, int position) {
        for (int i = 0; i < totalNeurons; i++) {
            layer.addNeuron(algorithm, errorMeasure);
        }

        layers.add(position, layer);
    }

    private void addAllHidden(int nodesPerHiddenLayer, int hiddenLayerSize) {
        for (int i = 0; i < hiddenLayerSize; i++) {
            addLayer(new HiddenLayer(), nodesPerHiddenLayer, layers.size() - 1);
        }
    }

    private void connectAll() {
        for (int i = 1; i < layers.size(); i++) {
            layers.get(i).connectAll(layers.get(i - 1));
        }
    }


}
