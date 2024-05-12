package dev.misei.einfachstonks.neuralservice.network.layer;

import dev.misei.einfachstonks.neuralservice.network.layer.neuron.InputNeuron;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class InputLayer extends Layer {

    @Override
    public void addNeuron() {
        this.getNeurons().add(new InputNeuron());
    }
}
