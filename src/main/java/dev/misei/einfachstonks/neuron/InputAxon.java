package dev.misei.einfachstonks.neuron;

import dev.misei.einfachstonks.layer.Layer;
import dev.misei.einfachstonks.network.Context;

public class InputAxon extends Axon {

    public InputAxon() {
        super();
    }

    @Override
    public void inject(double injectedTarget) {
        this.output = injectedTarget;
    }

    @Override
    public void computeForward(Context context) {
        context.neuronOutput.put(this.id, output);
    }

    @Override
    public void computeBackward(Context context) {
        //Contextual. Done better than perfect.
    }

    @Override
    public void connectAll(Layer inboundLayer) {
        //Contextual. Done better than perfect.
    }

}
