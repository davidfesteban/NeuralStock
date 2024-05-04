package dev.misei.einfachstonks.neuralservice.layer;

import dev.misei.einfachstonks.neuralservice.math.Algorithm;
import dev.misei.einfachstonks.neuralservice.math.ErrorMeasure;
import dev.misei.einfachstonks.neuralservice.neuron.InputAxon;

public class InputLayer extends Layer {
    public InputLayer() {
        super();
    }
    @Override
    public void addNeuron(Algorithm algorithm, ErrorMeasure errorMeasure) {
        this.getAxons().add(new InputAxon());
    }
}
