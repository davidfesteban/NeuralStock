package dev.misei.einfachstonks.neuralservice.network.layer.neuron;

import dev.misei.einfachstonks.neuralservice.network.Context;

import javax.sound.midi.Soundbank;

public class HiddenNeuron extends Neuron {

    @Override
    protected Double calculateGradient(Context context) {
        //TODO: reduce(sum) of really small Doubles provokes NAN
        var result = context.weightedGradient.get(this.id).stream().reduce(Double::sum).orElse(0d)
                * context.algorithmType.derivative(this.output);

        if(Double.isNaN(result)) {
            //result = 0d;
        }

        return result;
    }
}
