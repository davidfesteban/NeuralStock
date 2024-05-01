package dev.misei.einfachstonks;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

@Slf4j
public class Network {
    private final List<Layer> layers = new ArrayList<>();
    private final DataSetList dataSetList;
    private final Double learningRate = 0.01;
    private final Double momentum = 0.9;

    public Network(DataSetList dataSetList, int nodesPerHiddenLayer, int hiddenLayerSize) {
        this.dataSetList = dataSetList;

        addLayer(dataSetList.getInputSize(), 0);
        addLayer(dataSetList.getOutputSize(), 1);
        addAllHidden(nodesPerHiddenLayer, hiddenLayerSize);
        addAllConnections();
    }

    public void train(int epochs) {
        while (epochs > 0) {
            log.info(String.format("==== Epoch-Iteration: %d", epochs));

            dataSetList.getDataSets().forEach(dataSet -> {
                injectComputeForward(dataSet);
                injectComputeBackward(dataSet, learningRate, momentum);
                ContextCache.weightedGradient.clear();
            });

            --epochs;
        }
    }

    public void predictComplete(DataSet dataSet) {
        injectComputeForward(dataSet);

        layers.get(layers.size() - 1).getNeurons().forEach(neuron -> System.out.println(neuron.getPredicted()));
        //dataSet.outputs().add()
    }

    private void injectComputeForward(DataSet dataSet) {
        //log.info(dataSet.toString());
        //log.info("===== ComputeForward... ");

        for (int z = 0; z < dataSet.inputs().size(); z++) {
            layers.get(0).getNeurons().get(z).setPredicted(dataSet.inputs().get(z));
        }

        layers.forEach(Layer::computeForward);

        //Log
        //layers.forEach(layer -> log.info(layer.toString()));
    }

    private void injectComputeBackward(DataSet dataSet, Double learningRate, Double momentum) {
        var outputLayer = layers.get(layers.size() - 1);

        for (int i = 0; i < dataSet.outputs().size(); i++) {
            outputLayer.getNeurons().get(i).computeBackward(dataSet.outputs().get(i));
        }

        for (int i = layers.size() - 2; i >= 0 ; i--) {
            layers.get(i).computeBackward();
        }

        for (int i = layers.size() - 1; i >= 0 ; i--) {
            layers.get(i).getNeurons().forEach(new Consumer<Neuron>() {
                @Override
                public void accept(Neuron neuron) {
                    neuron.updateWeight(learningRate, momentum);
                }
            });
        }
    }



    private void addAllConnections() {
        AtomicReference<Layer> prevLayer = new AtomicReference<>(new Layer());
        layers.forEach(layer -> {
            layer.addAllReferences(prevLayer.get());
            prevLayer.set(layer);
        });
    }

    private void addAllHidden(int nodesPerHiddenLayer, int hiddenLayerSize) {
        for (int i = 0; i < hiddenLayerSize; i++) {
            addLayer(nodesPerHiddenLayer, layers.size() - 1);
        }
    }

    private void addLayer(int size, int position) {
        var layer = new Layer();

        for (int i = 0; i < size; i++) {
            var neuron = new Neuron(UUID.randomUUID());
            layer.add(neuron);
        }

        layers.add(position, layer);
    }

}
