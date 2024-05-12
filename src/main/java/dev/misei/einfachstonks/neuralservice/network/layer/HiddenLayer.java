package dev.misei.einfachstonks.neuralservice.network.layer;

import dev.misei.einfachstonks.neuralservice.network.layer.neuron.HiddenNeuron;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class HiddenLayer extends Layer {

    @Override
    public void addNeuron() {
        this.getNeurons().add(new HiddenNeuron());
    }
}
