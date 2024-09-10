package dev.misei.einfachstonks.neuralservice.network;

import dev.misei.einfachstonks.neuralservice.dataset.DataSet;
import dev.misei.einfachstonks.neuralservice.dataset.DataSetList;
import dev.misei.einfachstonks.neuralservice.network.layer.HiddenLayer;
import dev.misei.einfachstonks.neuralservice.network.layer.InputLayer;
import dev.misei.einfachstonks.neuralservice.network.layer.Layer;
import dev.misei.einfachstonks.neuralservice.network.layer.OutputLayer;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * How does it work? Explanation.
 * - Input and Output Neurons have an injected value used as Input and Expected values as injectedValue.
 * - On Forward Propagation: Each Predicted value is published in the Context by a NeuronId. It is stored in Neuron as output.
 * - On Backward Propagation: Each Gradient and Delta is calculated and published in the Context. Tbh, I still don´t
 * fully understand the algorithm :)
 * Disclaimer: The code is really chunky sometimes but this project is taking away my life. Apologies.
 */

@Slf4j
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Network {

    private final List<NetworkLifecycleComponent> layers = new ArrayList<>();
    private final DataSetList dataSetList;
    private final Context context;

    /**
     * Creates all internal connections. Adds the number of Layers and Internal Nodes shifting the Output Layer.
     * An anti-pattern is to have initialization logic of @this within the constructor.
     */
    public static Network create(DataSetList dataSetList, Context context, /*Shape shape*/ int neuronsPerHiddenLayer, int totalHiddenLayers) {
        Network network = new Network(dataSetList, context);
        network.createLayer(new InputLayer(), dataSetList.getInputSize(), 0);
        network.createLayer(new OutputLayer(), dataSetList.getOutputSize(), 1);
        network.createAllHidden(neuronsPerHiddenLayer, totalHiddenLayers);
        network.connectAll();
        return network;
    }

    void train(int totalEpochs) {
        for (int epoch = 0; epoch < totalEpochs; epoch++) {
            dataSetList.getDataSets().forEach(dataSet -> compute(dataSet, true));
            System.out.println(epoch);
        }
    }

    List<Double> predict(DataSet dataSet, boolean forTraining) {
        if (forTraining) {
            this.dataSetList.accumulateTraining(dataSet);
        }

        return compute(dataSet, forTraining);
    }

    /**
     * Step 1: Inject input values and expected ones
     * Step 2: Forward Propagation
     * Step 3: Return the predicted values from the context by asking the neuron id
     * Step 4: Backward Propagation if it was executed for training purposes
     * Step 5: Clear all gradient calculations as it is a multimap
     */
    private List<Double> compute(DataSet dataSet, boolean forTraining) {
        List<Double> results = new ArrayList<>();

        layers.getFirst().inject(dataSet.inputs());
        layers.getLast().inject(dataSet.outputs());

        layers.forEach(layer -> layer.computeForward(context));

        ((OutputLayer) layers.getLast()).getNeurons().forEach(neuron -> results.add(context.neuronOutput.get(neuron.getId())));

        if (forTraining) {
            layers.reversed().forEach(layer -> layer.computeBackward(context));
        }

        context.weightedGradient.clear();
        return results;
    }

    /**
     * Connects all the hidden layers and add them by shifting the output layer
     */
    private void createAllHidden(int nodesPerHiddenLayer, int hiddenLayerSize) {
        for (int i = 0; i < hiddenLayerSize; i++) {
            createLayer(new HiddenLayer(), nodesPerHiddenLayer, layers.size() - 1);
        }
    }

    private void createLayer(Layer layer, int totalNeurons, int position) {
        for (int i = 0; i < totalNeurons; i++) {
            layer.addNeuron();
        }

        layers.add(position, layer);
    }

    /**
     * Connects all the layer by injecting a reference of N-1 into N.
     */
    private void connectAll() {
        for (int i = 1; i < layers.size(); i++) {
            layers.get(i).connectAll((Layer) layers.get(i - 1));
        }
    }
}
