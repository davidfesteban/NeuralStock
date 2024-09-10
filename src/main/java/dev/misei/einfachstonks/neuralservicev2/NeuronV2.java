package dev.misei.einfachstonks.neuralservicev2;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
public class NeuronV2 {

    List<Connection> inboundConnections;
    List<Connection> outboundConnections;

    //Only for OutputNeurons
    BigDecimal expectedOutputFeed;

    BigDecimal activation;
    BigDecimal bias;

    AlgorithmTypeV2 algorithmType;

    public void computeForward() {
        BigDecimal preActivation = inboundConnections.stream()
                .map(connection -> connection.parentActivation.multiply(connection.weight))
                .reduce(BigDecimal::add)
                .orElseThrow(() -> new RuntimeException("Cannot reduce preActivation components"));

        this.activation = algorithmType.activate(preActivation.add(bias));
        outboundConnections.forEach(outbound -> outbound.parentActivation = activation);
    }

    public void prepareGradient() {
        var gradientExpectedToError = computeErrorLoss();
        var derivativeSigmoid = AlgorithmTypeV2.SIGMOID.derivative(this.activation);
        var gradientLossAndSigmoidToNeuron = gradientExpectedToError.multiply(derivativeSigmoid);

        //GradientNeuronToWeight
        inboundConnections.forEach(inboundConnection -> inboundConnection.gradientNeuron = gradientLossAndSigmoidToNeuron);
    }

    public void updateWeights(BigDecimal learningRatio) {
        inboundConnections.forEach(inboundConnection ->
                inboundConnection.weight = inboundConnection.weight.subtract(
                        learningRatio.multiply(inboundConnection.gradientNeuron.multiply(inboundConnection.parentActivation))));

        bias = bias.subtract(learningRatio.multiply(inboundConnections.getFirst().gradientNeuron));
    }

    private BigDecimal computeErrorLoss() {
        //If true, it is implicit that it is an Output Neuron
        if (expectedOutputFeed != null) {
            return this.activation.subtract(expectedOutputFeed);
        }

        //If false, then is a hidden layer
        return this.outboundConnections.stream()
                .map(outbound -> outbound.weight.multiply(outbound.gradientNeuron))
                .reduce(BigDecimal::add).orElseThrow(() -> new RuntimeException("Cannot calculate total gradient of children loss"));
    }

    public NeuronV2 feedExpectedOutput(BigDecimal expected) {
        this.expectedOutputFeed = expected;
        return this;
    }
}
