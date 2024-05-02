package dev.misei.einfachstonks.layer;

import dev.misei.einfachstonks.math.Algorithm;
import dev.misei.einfachstonks.math.ErrorMeasure;
import dev.misei.einfachstonks.neuron.InputAxon;

public class InputLayer extends Layer {
    public InputLayer() {
        super();
    }
    @Override
    public void addNeuron(Algorithm algorithm, ErrorMeasure errorMeasure) {
        this.getAxons().add(new InputAxon());
    }
}
