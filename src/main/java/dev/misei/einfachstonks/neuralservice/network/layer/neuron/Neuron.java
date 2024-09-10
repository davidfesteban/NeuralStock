package dev.misei.einfachstonks.neuralservice.network.layer.neuron;

import dev.misei.einfachstonks.neuralservice.math.AlgorithmType;
import dev.misei.einfachstonks.neuralservice.network.Context;
import dev.misei.einfachstonks.neuralservice.network.NetworkLifecycleComponent;
import dev.misei.einfachstonks.neuralservice.network.layer.Layer;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public abstract class Neuron extends NetworkLifecycleComponent {
    protected final UUID id;
    protected Double output;
    private List<InboundConnection> inboundConnections;
    private Double gradient;
    private Double bias;

    //TODO: Have in mind bias and use it on calculations
    public Neuron() {
        this.inboundConnections = new ArrayList<>();
        this.gradient = 0d;
        this.bias = 1d;
        this.id = UUID.randomUUID();
        this.output = 0d;
    }

    @Override
    public void computeForward(Context context) {
        double preActivation = 0d;

        for (InboundConnection inboundConnection : inboundConnections) {
            var output = context.neuronOutput.get(inboundConnection.originId());
            preActivation += output * inboundConnection.weight();
        }

        this.output = context.algorithmType.activate(preActivation + bias);
        context.neuronOutput.put(this.id, this.output);
    }

    @Override
    public void computeBackward(Context context) {
        this.gradient = calculateGradient(context);
        inboundConnections.forEach(inboundConnection ->
                context.weightedGradient.add(inboundConnection.originId(), gradient * inboundConnection.weight()));
        updateWeights(context);
    }

    //TODO: Better initialization
    @Override
    public void connectAll(Layer inboundLayer) {
        inboundConnections.addAll(inboundLayer.getNeurons()
                .stream().map(neuron ->
                        new InboundConnection(neuron.getId(), AlgorithmType.random.nextDouble(-2, 2), 0d))
                .toList());
    }

    @Override
    public void inject(List<Double> injectedTargets) {
        this.output = injectedTargets.getFirst();
    }

    private void updateWeights(Context context) {
        inboundConnections = inboundConnections.stream().map(inboundConnection -> {
            double oldWeightDelta = inboundConnection.weightDelta();
            var weightDelta = context.learningRatio * gradient * context.neuronOutput.get(inboundConnection.originId());
            var newInbound = inboundConnection.withWeightDelta(weightDelta)
                    .withWeight(inboundConnection.weight() + weightDelta + (context.momentum * oldWeightDelta));

            return newInbound;
        }).toList();
    }
}
