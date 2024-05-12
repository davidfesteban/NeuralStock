package dev.misei.einfachstonks.neuralservice.network.layer.neuron;

import dev.misei.einfachstonks.neuralservice.network.Context;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class InputNeuron extends Neuron {

    @Override
    public void computeForward(Context context) {
        context.neuronOutput.put(this.id, output);
    }

}
