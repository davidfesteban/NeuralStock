package dev.misei.einfachstonks.neuralservice.layer;

import dev.misei.einfachstonks.neuralservice.math.Algorithm;
import dev.misei.einfachstonks.neuralservice.math.ErrorMeasure;
import dev.misei.einfachstonks.neuralservice.neuron.HiddenNeuron;

public class HiddenLayer extends Layer {
    public HiddenLayer() {
        super();
    }

    @Override
    public void addNeuron(Algorithm algorithm, ErrorMeasure errorMeasure) {
        this.getAxons().add(new HiddenNeuron(algorithm, errorMeasure));
    }
}
