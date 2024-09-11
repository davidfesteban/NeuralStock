package dev.misei.einfachstonks.neuralservice;

import dev.misei.einfachstonks.neuralservice.dataenum.AlgorithmType;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
public class Neuron {

    final List<Connection> inboundConnections;
    final List<Connection> outboundConnections;

    final AlgorithmType algorithmType;
    final Double learningRatio;

    //Only for OutputNeurons
    Double expectedOutputFeed;

    Double activation;
    Double bias;

    public Neuron(AlgorithmType algorithmType, double learningRatio) {
        this.inboundConnections = new ArrayList<>();
        this.outboundConnections = new ArrayList<>();
        this.expectedOutputFeed = null;
        this.activation = null;
        this.bias = AlgorithmType.random.nextDouble(-2, 2);
        this.algorithmType = algorithmType;
        this.learningRatio = learningRatio;
    }

    public void computeForward() {
        Double preActivation = inboundConnections.stream()
                .map(inbound -> inbound.parentActivation * inbound.weight)
                .reduce(Double::sum)
                .orElseThrow(() -> new RuntimeException("Cannot reduce preActivation components"));

        this.activation = algorithmType.activate(preActivation + bias);
        outboundConnections.forEach(outbound -> outbound.parentActivation = activation);
    }

    public void prepareGradient() {
        var gradientExpectedToError = computeErrorLoss();
        var derivativeSigmoid = algorithmType.derivative(this.activation);
        var gradientLossAndSigmoidToNeuron = gradientExpectedToError * derivativeSigmoid;

        //GradientNeuronToWeight
        inboundConnections.forEach(inboundConnection -> inboundConnection.gradientNeuron = gradientLossAndSigmoidToNeuron);
    }

    public void updateWeights() {
        inboundConnections.forEach(inbound ->
                inbound.weight = inbound.weight -
                        (learningRatio * inbound.gradientNeuron * inbound.parentActivation));

        bias = bias - (learningRatio * (inboundConnections.getFirst().gradientNeuron));
    }

    private Double computeErrorLoss() {
        //If true, it is implicit that it is an Output Neuron
        if (expectedOutputFeed != null) {
            return this.activation - expectedOutputFeed;
        }

        //If false, then is a hidden layer
        return this.outboundConnections.stream()
                .map(outbound -> outbound.weight * outbound.gradientNeuron)
                .reduce(Double::sum).orElseThrow(() -> new RuntimeException("Cannot calculate total gradient of children loss"));
    }

    public void feedExpectedOutput(Double expected) {
        this.expectedOutputFeed = expected;
    }

    public void feedFeatureInput(Double inputValue) {
        this.activation = inputValue;
        outboundConnections.forEach(outbound -> outbound.parentActivation = activation);
    }
}
