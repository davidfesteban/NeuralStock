package dev.misei.einfachstonks.neuralservice;

import dev.misei.einfachstonks.neuralservice.dataenum.Algorithm;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class Neuron {

    final List<Connection> inboundConnections;
    final List<Connection> outboundConnections;

    final Algorithm algorithm;

    Double activation;
    Double bias;

    public Neuron(Algorithm algorithm) {
        this.inboundConnections = new ArrayList<>();
        this.outboundConnections = new ArrayList<>();
        this.activation = null;
        this.bias = Algorithm.RANDOM.nextDouble(-2, 2);
        this.algorithm = algorithm;
    }

    public void computeForward() {
        Double preActivation = inboundConnections.stream()
                .map(inbound -> inbound.parentActivation * inbound.weight)
                .reduce(Double::sum)
                .orElseThrow(() -> new RuntimeException("Cannot reduce preActivation components"));

        this.activation = algorithm.getAlgorithmType().activate(preActivation + bias);
        outboundConnections.forEach(outbound -> outbound.parentActivation = activation);
    }

    public void prepareGradient() {
        var gradientExpectedToError = computeErrorLoss();
        var derivativeSigmoid = algorithm.getAlgorithmType().derivative(this.activation);
        var gradientLossAndSigmoidToNeuron = gradientExpectedToError * derivativeSigmoid;

        //GradientNeuronToWeight
        inboundConnections.forEach(inboundConnection -> inboundConnection.gradientNeuron = gradientLossAndSigmoidToNeuron);
    }

    public void updateWeights() {
        inboundConnections.forEach(inbound ->
                inbound.weight = inbound.weight -
                        (algorithm.getLearningRatio() * inbound.gradientNeuron * inbound.parentActivation));

        bias = bias - (algorithm.getLearningRatio() * (inboundConnections.getFirst().gradientNeuron));
    }

    private Double computeErrorLoss() {
        return this.outboundConnections.stream()
                .map(Connection::getOutboundGradientNeuronWeighted)
                .reduce(Double::sum).orElseThrow(() -> new RuntimeException("Cannot calculate total gradient of children loss"));
    }
}
