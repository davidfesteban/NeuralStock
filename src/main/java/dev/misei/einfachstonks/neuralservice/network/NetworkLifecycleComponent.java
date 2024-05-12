package dev.misei.einfachstonks.neuralservice.network;

import dev.misei.einfachstonks.neuralservice.network.layer.Layer;

import java.util.List;

public abstract class NetworkLifecycleComponent {

    public abstract void computeForward(Context context);

    public abstract void computeBackward(Context context);

    public abstract void connectAll(Layer inboundLayer);

    public abstract void inject(List<Double> injectedTargets);
    protected Double calculateGradient(Context context) {
        return 0d;
    }
}
