package dev.misei.einfachstonks.neuralservice.neuron;

import dev.misei.einfachstonks.neuralservice.math.Algorithm;
import dev.misei.einfachstonks.neuralservice.math.ErrorMeasure;
import dev.misei.einfachstonks.neuralservice.network.Context;

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
