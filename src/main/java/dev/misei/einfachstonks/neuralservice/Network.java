package dev.misei.einfachstonks.neuralservice;

import dev.misei.einfachstonks.neuralservice.dataenum.Algorithm;
import dev.misei.einfachstonks.neuralservice.dataenum.Datapair;
import dev.misei.einfachstonks.neuralservice.dataenum.Dataset;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.IntStream;

@Data
public class Network {

    private final Algorithm algorithm;
    private final List<Connection> inboundFeeder;
    private final List<Connection> outboundFeeder;
    private final Dataset dataset;
    List<List<Neuron>> network;
    private int accumulatedTrainedEpochs;

    private Network(Algorithm algorithm, Dataset dataset) {
        this.algorithm = algorithm;
        this.inboundFeeder = new ArrayList<>();
        this.outboundFeeder = new ArrayList<>();
        this.dataset = dataset;
        this.accumulatedTrainedEpochs = 0;
    }

    public static Network create(Algorithm algorithm, Dataset dataset) {
        Network result = new Network(algorithm, dataset);

        result.network = algorithm.drawShape().stream().map(
                integer -> IntStream.range(0, integer).mapToObj(i -> new Neuron(algorithm))
                        .toList()).toList();

        result.connectAll(algorithm);

        return result;
    }

    public void predict(Dataset innerDataset) {
        compute(innerDataset, 0, false);
    }

    public void train(int epochs) {
        train(epochs, dataset.getDataset().size());
    }

    public void train(int epochs, int pastWindowTime) {
        IntStream.range(0, epochs).forEach(value ->
                compute(dataset, Math.max(0, dataset.getDataset().size() - pastWindowTime), true));
    }

    /**
     * When merging a new dataset, you need to train equally the network and normalise it
     */
    public void merge(Dataset innerDataset, int pastWindowTime, int epochRelationPercent) {
        if (!innerDataset.isCompatible(this.dataset)) {
            throw new IllegalArgumentException("Datasets are not compatible on merge");
        }

        //Prepare
        var currentDataset = this.dataset.getDataset();

        //Merge
        var pastDatapairs = new ArrayList<>(currentDataset
                .subList(Math.max(0, currentDataset.size() - pastWindowTime), currentDataset.size()));
        innerDataset.getDataset().addAll(0, pastDatapairs);

        //Train
        IntStream.range(0, Math.max(1, (int) Math.ceil(accumulatedTrainedEpochs * epochRelationPercent / 100.0)))
                .forEach(value -> compute(innerDataset, 0, true));

        //Clean
        innerDataset.getDataset().removeAll(pastDatapairs);

        //Add
        currentDataset.addAll(innerDataset.getDataset());
    }

    private void compute(Dataset innerDataset, int indexStart, boolean train) {
        for (int j = indexStart; j < innerDataset.getDataset().size(); j++) {
            Datapair datapair = innerDataset.getDataset().get(j);
            List<Double> inputs = datapair.getInputs();
            List<Double> outputs = datapair.getOutputs();

            Assert.isTrue(inputs.size() == algorithm.getInputSize(), "Input size does not match expected size");
            Assert.isTrue(outputs.size() == algorithm.getOutputSize(), "Output size does not match expected size");

            computeForward(inputs);

            //Output Predicted Values
            datapair.getPredictedHistory().add(outboundFeeder.stream().map(connection -> connection.parentActivation).toList());

            if (train) {
                computeBackward(outputs);
            }
        }

        if (train) {
            ++accumulatedTrainedEpochs;
        }
    }

    private void computeForward(List<Double> inputs) {
        IntStream.range(0, inboundFeeder.size()).forEach(i -> inboundFeeder.get(i).parentActivation = inputs.get(i));
        network.forEach(layer -> layer.forEach(Neuron::computeForward));
    }

    private void computeBackward(List<Double> outputs) {
        IntStream.range(0, outboundFeeder.size()).forEach(i -> outboundFeeder.get(i).manualOutputFeed = outputs.get(i));

        network.reversed().forEach(neurons -> neurons.forEach(Neuron::prepareGradient));
        network.reversed().forEach(neurons -> neurons.forEach(Neuron::updateWeights));
    }

    private void connectAll(Algorithm algorithm) {
        network.getFirst().forEach(neuron -> {
            Connection connection = new Connection(algorithm);
            neuron.inboundConnections.add(connection);
            inboundFeeder.add(connection);
        });

        for (int layerIndex = 0; layerIndex < network.size() - 1; layerIndex++) {
            List<Neuron> currentLayer = network.get(layerIndex);
            List<Neuron> nextLayer = network.get(layerIndex + 1);

            for (Neuron currentNeuron : currentLayer) {
                for (Neuron nextNeuron : nextLayer) {
                    Connection connection = new Connection(algorithm);
                    currentNeuron.outboundConnections.add(connection);
                    nextNeuron.inboundConnections.add(connection);
                }
            }
        }

        network.getLast().forEach(neuron -> {
            Connection connection = new Connection(algorithm);
            neuron.outboundConnections.add(connection);
            outboundFeeder.add(connection);
        });
    }

    void reconnectAll() {
        network.forEach(neurons -> neurons.forEach(neuron -> neuron.inboundConnections.clear()));

        for (int i = 0; i < network.getFirst().size(); i++) {
            network.getFirst().get(i).inboundConnections.add(inboundFeeder.get(i));
        }

        for (int layerIndex = 0; layerIndex < network.size() - 1; layerIndex++) {
            List<Neuron> currentLayer = network.get(layerIndex);
            List<Neuron> nextLayer = network.get(layerIndex + 1);

            for (Neuron neuron : currentLayer) {
                for (int j = 0; j < nextLayer.size(); j++) {
                    nextLayer.get(j).inboundConnections.add(neuron.getOutboundConnections().get(j));
                }
            }
        }

        for (int i = 0; i < network.getLast().size(); i++) {
            network.getLast().get(i).outboundConnections.add(outboundFeeder.get(i));
        }
    }

    public synchronized int getAccumulatedSyncTrainedEpochs() {
        return accumulatedTrainedEpochs;
    }
}
