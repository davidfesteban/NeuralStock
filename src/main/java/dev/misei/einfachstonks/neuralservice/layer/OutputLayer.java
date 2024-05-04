package dev.misei.einfachstonks.neuralservice.layer;

import dev.misei.einfachstonks.neuralservice.math.Algorithm;
import dev.misei.einfachstonks.neuralservice.math.ErrorMeasure;
import dev.misei.einfachstonks.neuralservice.neuron.OutputNeuron;

public class OutputLayer extends Layer {
    public OutputLayer() {
        super();
    }

    @Override
    public void addNeuron(Algorithm algorithm, ErrorMeasure errorMeasure) {
        this.getAxons().add(new OutputNeuron(algorithm, errorMeasure));
    }
}
