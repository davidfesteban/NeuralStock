package dev.misei.einfachstonks.neuralservice;

import dev.misei.einfachstonks.neuralservice.dataenum.Algorithm;
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

    public Connection(Algorithm algorithm) {
        this.parentActivation = 0d;
        this.weight = algorithm.weightInitialiser();
        this.gradientNeuron = 0d;
        this.manualOutputFeed = null;
    }

    public Double getOutboundGradientNeuronWeighted() {
        if(manualOutputFeed == null) {
            //It is a hidden layer
            return this.weight * this.gradientNeuron;
        }
        return parentActivation - manualOutputFeed;
    }
}
