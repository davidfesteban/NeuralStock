package dev.misei.einfachstonks.neuron;

import dev.misei.einfachstonks.math.Algorithm;
import dev.misei.einfachstonks.math.ErrorMeasure;
import dev.misei.einfachstonks.network.Context;

public class HiddenNeuron extends Neuron {
    public HiddenNeuron(Algorithm algorithm, ErrorMeasure errorMeasure) {
        super(algorithm, errorMeasure);
    }

    @Override
    protected Double calculateGradient(Context context) {
        return context.weightedGradient.get(this.id).stream().reduce(Double::sum).orElse(0d)
                * algorithm.derivative(this.output);
    }

    @Override
    public void inject(double value) {
        //Contextual
    }
}
