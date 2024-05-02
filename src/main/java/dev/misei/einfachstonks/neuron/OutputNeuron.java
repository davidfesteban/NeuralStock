package dev.misei.einfachstonks.neuron;

import dev.misei.einfachstonks.math.Algorithm;
import dev.misei.einfachstonks.math.ErrorMeasure;
import dev.misei.einfachstonks.network.Context;

public class OutputNeuron extends Neuron {
    private Double injectedTarget;

    public OutputNeuron(Algorithm algorithm, ErrorMeasure errorMeasure) {
        super(algorithm, errorMeasure);
    }

    @Override
    protected Double calculateGradient(Context context) {
        return this.errorMeasure.calculate(injectedTarget, this.output) * algorithm.derivative(this.output);
    }

    @Override
    public void inject(double injectedTarget) {
        this.injectedTarget = injectedTarget;
    }

}
