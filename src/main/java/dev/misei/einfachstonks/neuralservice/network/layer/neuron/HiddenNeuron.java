package dev.misei.einfachstonks.neuralservice.network.layer.neuron;

import dev.misei.einfachstonks.neuralservice.network.Context;

public class HiddenNeuron extends Neuron {

    @Override
    protected Double calculateGradient(Context context) {
        return context.weightedGradient.get(this.id).stream().reduce(Double::sum).orElse(0d)
                * context.algorithmType.derivative(this.output);
    }
}
