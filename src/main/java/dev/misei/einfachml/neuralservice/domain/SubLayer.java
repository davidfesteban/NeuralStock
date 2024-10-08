package dev.misei.einfachml.neuralservice.domain;

import java.util.ArrayList;
import java.util.List;

public class SubLayer extends ArrayList<Neuron> {
    public SubLayer() {
        super();
    }

    public SubLayer(List<Neuron> neuronList) {
        super(neuronList);
    }

    void computeForward() {
        this.forEach(Neuron::computeForward);
    }

    void prepareGradient() {
        this.forEach(Neuron::prepareGradient);
    }

    void updateWeights() {
        this.forEach(Neuron::updateWeights);
    }
}
