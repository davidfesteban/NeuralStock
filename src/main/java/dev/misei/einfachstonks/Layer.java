package dev.misei.einfachstonks;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
public class Layer {
    private final List<Neuron> neurons = new ArrayList<>();
    private final UUID id = UUID.randomUUID();

    public void add(Neuron neuron) {
        this.neurons.add(neuron);
    }

    public void addAllReferences(Layer prevLayer) {
        neurons.forEach(neuron -> {
            var prevLayerRef = prevLayer.getNeurons().stream().map(
                            prevNeuron -> new RefNeuron(prevNeuron.getId(), Math.floor(Algorithm.random.nextDouble(-2, 2)*100)/100, 0d))
                    .toList();
            neuron.addAllPrev(prevLayerRef);
        });
    }

    public void computeForward() {
        neurons.forEach(Neuron::computeForward);
    }

    public void computeBackward() {
        neurons.forEach(Neuron::computeBackward);
    }

    @Override
    public String toString() {
        String result = "Layer " + this.id + " ====== \n" +
                neurons.stream().map(Neuron::toString).collect(Collectors.joining("\n"));

        return result;
    }
}
