package dev.misei.einfachstonks.neuralservice;

import dev.misei.einfachstonks.neuralservice.dataenum.AlgorithmType;

public class Connection {
    Double parentActivation;
    Double weight;

    Double gradientNeuron;

    public Connection(AlgorithmType algorithmType) {
        this.parentActivation = 0d;
        this.weight = algorithmType.weightInitialiser();
        this.gradientNeuron = 0d;
    }
}
