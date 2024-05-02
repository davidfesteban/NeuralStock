package dev.misei.einfachstonks.network;

import dev.misei.einfachstonks.neuron.InboundConnection;

public abstract class NetworkLifecycle {

    public void compute(Context context) {
        computeForward(context);
        computeBackward(context);
    }

    public abstract void computeForward(Context context);

    public abstract void computeBackward(Context context);
}
