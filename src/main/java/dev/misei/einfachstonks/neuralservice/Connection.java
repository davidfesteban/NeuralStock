package dev.misei.einfachstonks.neuralservice;

import dev.misei.einfachstonks.neuralservice.dataenum.Algorithm;
import dev.misei.einfachstonks.neuralservice.dataenum.AlgorithmType;

public class Connection {
    Double parentActivation;
    Double weight;
    Double gradientNeuron;

    public Connection(Algorithm algorithm) {
        this.parentActivation = 0d;
        this.weight = algorithm.weightInitialiser();
        this.gradientNeuron = 0d;
    }
}
