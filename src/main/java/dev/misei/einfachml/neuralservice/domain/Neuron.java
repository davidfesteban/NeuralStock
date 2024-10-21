package dev.misei.einfachml.neuralservice.domain;

import dev.misei.einfachml.neuralservice.domain.algorithm.Algorithm;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;

@Data
@AllArgsConstructor
public class Neuron {
    private static final double GRADIENT_CLIP_THRESHOLD = 7.0;
    private static final double MIN_LEARNING_RATE = 1e-9;
    private static final double MAX_LEARNING_RATE = 1e-2;
    private static final double SMALL_GRADIENT_THRESHOLD = 1e-7; // Below this, increase learning rate
    private static final double LARGE_GRADIENT_THRESHOLD = 1.0;  // Above this, decrease learning rate


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

        inboundConnections.forEach(inboundConnection -> {
            double rawGradient = gradientLossAndSigmoidToNeuron;

            //TODO: Add into algorithm as optional. Add learning Ratio standards. 1e-3 1e-2 1e-5 1e-9 1e-12
            // Apply gradient clipping if necessary
            if (Math.abs(rawGradient) > GRADIENT_CLIP_THRESHOLD) {
                rawGradient = Math.signum(rawGradient) * GRADIENT_CLIP_THRESHOLD;
            }

            inboundConnection.gradientNeuron = rawGradient;
        });

        //GradientNeuronToWeight V1
        //inboundConnections.forEach(inboundConnection -> inboundConnection.gradientNeuron = gradientLossAndSigmoidToNeuron);
    }

    public void updateWeights() {
        inboundConnections.forEach(inbound ->
                inbound.weight = inbound.weight -
                        (algorithm.getLearningRatio() * inbound.gradientNeuron * inbound.parentActivation));

        //GradientNeuron is the total gradient
        bias = bias - (algorithm.getLearningRatio() * inboundConnections.getFirst().gradientNeuron);
    }

    private Double computeErrorLoss() {
        return this.outboundConnections.stream()
                .map(Connection::getOutboundGradientNeuronWeighted)
                .reduce(Double::sum).orElseThrow(() -> new RuntimeException("Cannot calculate total gradient of children loss"));
    }

    //TODO: Include and review better
    // Function to adjust the learning rate dynamically based on the gradient's magnitude
    private double adjustLearningRate(Connection inbound) {
        double baseLearningRate = algorithm.getLearningRatio();
        double currentLearningRate = Optional.ofNullable(inbound.getAdjustedLearningRate()).orElse(baseLearningRate);

        if (Math.abs(inbound.gradientNeuron) < SMALL_GRADIENT_THRESHOLD) {
            currentLearningRate = Math.min(MAX_LEARNING_RATE, currentLearningRate * 1.01);  // Increase by 10%
        } else if (Math.abs(inbound.gradientNeuron) > LARGE_GRADIENT_THRESHOLD) {
            // If the gradient is large, decrease the learning rate
            currentLearningRate = Math.max(MIN_LEARNING_RATE, currentLearningRate * 0.99);  // Decrease by 10%
        }

        inbound.setAdjustedLearningRate(currentLearningRate);

        return currentLearningRate;
    }
}
