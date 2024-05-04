package dev.misei.einfachstonks.neuralservice.layer;

import dev.misei.einfachstonks.neuralservice.math.Algorithm;
import dev.misei.einfachstonks.neuralservice.math.ErrorMeasure;
import dev.misei.einfachstonks.neuralservice.network.Context;
import dev.misei.einfachstonks.neuralservice.network.NetworkLifecycle;
import dev.misei.einfachstonks.neuralservice.neuron.Axon;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
public abstract class Layer extends NetworkLifecycle {
    private final List<Axon> axons = new ArrayList<>();
    private final UUID id = UUID.randomUUID();

    public abstract void addNeuron(Algorithm algorithm, ErrorMeasure errorMeasure);

    public void connectAll(Layer inboundLayer) {
        axons.forEach(neuron -> {
            neuron.connectAll(inboundLayer);
        });
    }

    public void inject(List<Double> value) {
        for (int i = 0; i < value.size(); i++) {
            this.getAxons().get(i).inject(value.get(i));
        }
    }

    @Override
    public void computeForward(Context context) {
        axons.forEach(axon -> axon.computeForward(context));
    }

    @Override
    public void computeBackward(Context context) {
        axons.forEach(axon -> axon.computeBackward(context));
    }

    @Override
    public String toString() {
        return "Layer: " + this.id + " ====== \n" +
                axons.stream().map(Axon::toString).collect(Collectors.joining("\n"));
    }
}
