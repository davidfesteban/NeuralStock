package dev.misei.einfachstonks.layer;

import dev.misei.einfachstonks.math.Algorithm;
import dev.misei.einfachstonks.math.ErrorMeasure;
import dev.misei.einfachstonks.neuron.HiddenNeuron;

public class HiddenLayer extends Layer {
    public HiddenLayer() {
        super();
    }

    @Override
    public void addNeuron(Algorithm algorithm, ErrorMeasure errorMeasure) {
        this.getAxons().add(new HiddenNeuron(algorithm, errorMeasure));
    }
}
