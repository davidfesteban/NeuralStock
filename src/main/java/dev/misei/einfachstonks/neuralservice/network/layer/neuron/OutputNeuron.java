package dev.misei.einfachstonks.neuralservice.network.layer.neuron;

import dev.misei.einfachstonks.neuralservice.network.Context;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
public class OutputNeuron extends Neuron {
    private Double injectedTarget;

    @Override
    protected Double calculateGradient(Context context) {
        var result = context.errorMeasureType.calculate(injectedTarget, this.output) * context.algorithmType.derivative(this.output);

        //if(Double.isNaN(result)) {
            //System.out.println(result + "Gradient Neuron");
        //}

        return result;
    }

    @Override
    public void inject(List<Double> injectedTargets) {
        this.injectedTarget = injectedTargets.getFirst();
    }

}
