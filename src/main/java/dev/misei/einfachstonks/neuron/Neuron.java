package dev.misei.einfachstonks.neuron;

import dev.misei.einfachstonks.layer.Layer;
import dev.misei.einfachstonks.math.Algorithm;
import dev.misei.einfachstonks.math.ErrorMeasure;
import dev.misei.einfachstonks.network.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class Neuron extends Axon {
    final Algorithm algorithm;
    final ErrorMeasure errorMeasure;
    private List<InboundConnection> inboundConnections;
    private Double gradient;
    private Double bias;

    public Neuron(Algorithm algorithm, ErrorMeasure errorMeasure) {
        super();
        this.algorithm = algorithm;
        this.errorMeasure = errorMeasure;
        this.inboundConnections = new ArrayList<>();
        this.gradient = 0d;
        this.bias = 1d;
    }

    @Override
    public void computeForward(Context context) {
        double preActivation = 0d;

        for (InboundConnection inboundConnection : inboundConnections) {
            var output = context.neuronOutput.get(inboundConnection.originId());
            preActivation += output * inboundConnection.weight();
        }

        this.output = algorithm.activate(preActivation + bias);
        context.neuronOutput.put(this.id, this.output);
    }

    @Override
    public void computeBackward(Context context) {
        this.gradient = calculateGradient(context);
        inboundConnections.forEach(inboundConnection ->
                context.weightedGradient.add(inboundConnection.originId(), gradient * inboundConnection.weight()));
        updateWeights(context);
    }

    private void updateWeights(Context context) {
        inboundConnections = inboundConnections.stream().map(inboundConnection -> {
            double oldWeightDelta = inboundConnection.weightDelta();
            var weightDelta = context.learningRatio * gradient * context.neuronOutput.get(inboundConnection.originId());
            return inboundConnection.withWeightDelta(weightDelta)
                    .withWeight(inboundConnection.weight() + weightDelta + (context.momentum * oldWeightDelta));
        }).toList();
    }

    protected abstract Double calculateGradient(Context context);

    @Override
    public void connectAll(Layer inboundLayer) {
        inboundConnections.addAll(inboundLayer.getAxons()
                .stream().map(axon ->
                        new InboundConnection(axon.id, Algorithm.random.nextDouble(-2, 2), 0d))
                .toList());
    }

    @Override
    public String toString() {
        return "\nNeuron " + id + " =====\n" + String.format("Bias %s", bias) + " : " + String.format("Predicted %s", output)
                + "\n" + inboundConnections.stream().map(InboundConnection::toString).collect(Collectors.joining(" ||\n"));
    }
}
