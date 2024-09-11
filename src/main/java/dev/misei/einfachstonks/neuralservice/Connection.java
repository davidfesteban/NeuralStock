package dev.misei.einfachstonks.neuralservice;

import dev.misei.einfachstonks.neuralservice.dataenum.Algorithm;
import dev.misei.einfachstonks.neuralservice.dataenum.AlgorithmType;

public class Connection {

    Double parentActivation;
    Double weight;
    Double gradientNeuron;

    //
    Double manualIOFeed;

    public Connection(Algorithm algorithm) {
        this.parentActivation = 0d;
        this.weight = algorithm.weightInitialiser();
        this.gradientNeuron = 0d;
        this.manualIOFeed = null;
    }

    public Double getOutboundGradientNeuronWeighted() {
        if(manualIOFeed == null) {
            //It is a hidden layer
            return this.weight * this.gradientNeuron;
        }
        return parentActivation - manualIOFeed;
    }
}
