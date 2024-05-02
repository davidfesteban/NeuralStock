package dev.misei.einfachstonks.layer;

import dev.misei.einfachstonks.math.Algorithm;
import dev.misei.einfachstonks.math.ErrorMeasure;
import dev.misei.einfachstonks.neuron.OutputNeuron;

import java.util.List;

public class OutputLayer extends Layer {
    public OutputLayer() {
        super();
    }

    @Override
    public void addNeuron(Algorithm algorithm, ErrorMeasure errorMeasure) {
        this.getAxons().add(new OutputNeuron(algorithm, errorMeasure));
    }
}
