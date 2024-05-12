package dev.misei.einfachstonks.neuralservice.network.layer;

import dev.misei.einfachstonks.neuralservice.network.layer.neuron.OutputNeuron;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class OutputLayer extends Layer {

    @Override
    public void addNeuron() {
        this.getNeurons().add(new OutputNeuron());
    }

}
