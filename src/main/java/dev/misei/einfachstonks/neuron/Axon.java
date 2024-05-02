package dev.misei.einfachstonks.neuron;

import dev.misei.einfachstonks.layer.Layer;
import dev.misei.einfachstonks.network.NetworkLifecycle;
import lombok.Getter;

import java.util.UUID;

@Getter
public abstract class Axon extends NetworkLifecycle {
    final UUID id;
    Double output;

    public Axon() {
        this.id = UUID.randomUUID();
        this.output = 0d;
    }

    public abstract void connectAll(Layer inboundLayer);
    public abstract void inject(double value);
}
