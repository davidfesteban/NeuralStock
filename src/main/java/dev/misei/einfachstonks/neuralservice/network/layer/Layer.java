package dev.misei.einfachstonks.neuralservice.network.layer;

import dev.misei.einfachstonks.neuralservice.network.Context;
import dev.misei.einfachstonks.neuralservice.network.NetworkLifecycleComponent;
import dev.misei.einfachstonks.neuralservice.network.layer.neuron.Neuron;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public abstract class Layer extends NetworkLifecycleComponent {
    private final List<Neuron> neurons = new ArrayList<>();
    private final UUID id = UUID.randomUUID();

    public abstract void addNeuron();

    @Override
    public void connectAll(Layer inboundLayer) {
        neurons.forEach(neuron -> {
            neuron.connectAll(inboundLayer);
        });
    }

    @Override
    public void inject(List<Double> injectedTargets) {
        for (int i = 0; i < injectedTargets.size(); i++) {
            this.getNeurons().get(i).inject(List.of(injectedTargets.get(i)));
        }
    }

    @Override
    public void computeForward(Context context) {
        neurons.forEach(neuron -> neuron.computeForward(context));
    }

    @Override
    public void computeBackward(Context context) {
        neurons.forEach(neuron -> neuron.computeBackward(context));
    }
}
