package dev.misei.einfachstonks.neuralservice;

import dev.misei.einfachstonks.neuralservice.dataenum.Algorithm;
import dev.misei.einfachstonks.neuralservice.dataenum.Datapair;
import dev.misei.einfachstonks.neuralservice.dataenum.Dataset;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.IntStream;

public class Network {

    Algorithm algorithm;
    List<List<Neuron>> network;

    private Network(Algorithm algorithm) {
        this.algorithm = algorithm;
    }

    public static Network create(Algorithm algorithm) {
        Network result = new Network(algorithm);

        result.network = algorithm.drawShape().stream().map(
                integer -> IntStream.range(0, integer).mapToObj(i -> new Neuron(algorithm))
                        .toList()).toList();

        result.connectAll(algorithm);

        return result;
    }

    public void compute(Dataset dataset, int epochs, boolean train) {
        for (int i = 0; i < epochs; i++) {
            for (int j = 0; j < dataset.getDataset().size(); j++) {
                Datapair datapair = dataset.getDataset().get(j);
                List<Double> inputs = datapair.getInputs();
                List<Double> outputs = datapair.getOutputs();

                Assert.isTrue(inputs.size() == algorithm.getInputSize(), "Input size does not match expected size");
                Assert.isTrue(outputs.size() == algorithm.getOutputSize(), "Output size does not match expected size");

                computeForward(inputs);

                //Output Predicted Values
                datapair.getPredicted().clear();
                datapair.getPredicted().addAll(network.getLast().stream().map(Neuron::getActivation).toList());

                System.out.println(datapair.getOutputs());
                System.out.println(datapair.getPredicted());
                System.out.println(datapair.computeTotalMSE());

                if (train) {
                    computeBackward(outputs);
                }
            }
            System.out.println("Epoch: " + i);
        }
    }

    private void computeForward(List<Double> inputs) {
        for (int i = 0; i < network.getFirst().size(); i++) {
            network.getFirst().get(i).feedFeatureInput(inputs.get(i));
        }

        for (int i = 1; i < network.size(); i++) {
            network.get(i).forEach(Neuron::computeForward);
        }
    }

    private void computeBackward(List<Double> outputs) {
        for (int i = 0; i < network.getLast().size(); i++) {
            network.getLast().get(i).feedExpectedOutput(outputs.get(i));
        }

        for (int i = network.size() - 1; i > 0; i--) {
            network.get(i).forEach(Neuron::prepareGradient);
        }

        for (int i = network.size() - 1; i > 0; i--) {
            network.get(i).forEach(Neuron::updateWeights);
        }
    }

    private void connectAll(Algorithm algorithm) {
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
    }
}
