package dev.misei.einfachstonks.neuralservice.network;

public abstract class NetworkLifecycle {

    public void compute(Context context) {
        computeForward(context);
        computeBackward(context);
    }

    public abstract void computeForward(Context context);

    public abstract void computeBackward(Context context);
}
