package dev.misei.einfachml.neuralservice.domain;

import dev.misei.einfachml.neuralservice.domain.algorithm.Algorithm;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Connection {

    Double parentActivation;
    Double weight;
    Double gradientNeuron;

    //
    Double manualOutputFeed;

    //
    Double adjustedLearningRate;

    public Connection(Algorithm algorithm) {
        this.parentActivation = 0d;
        this.weight = algorithm.weightInitialiser();
        this.gradientNeuron = 0d;
        this.manualOutputFeed = null;
        this.adjustedLearningRate = null;
    }

    public Double getOutboundGradientNeuronWeighted() {
        if(manualOutputFeed == null) {
            //It is a hidden layer
            return this.weight * this.gradientNeuron;
        }
        return parentActivation - manualOutputFeed;
    }
}
