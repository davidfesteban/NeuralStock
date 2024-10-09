package dev.misei.einfachml.neuralservice.domain;

import dev.misei.einfachml.neuralservice.domain.algorithm.Algorithm;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Layer extends ArrayList<SubLayer> {

    public Layer() {
        super();
    }

    public Layer(List<SubLayer> subLayers) {
        super(subLayers);
    }

    void computeForward() {
        this.forEach(SubLayer::computeForward);
    }

    void prepareGradient() {
        this.forEach(SubLayer::prepareGradient);
    }

    void updateWeights() {
        this.forEach(SubLayer::updateWeights);
    }

    void connectToAll(Layer nextLayer, Algorithm algorithm) {
        this.forEach(neurons -> {
            neurons.forEach(neuron -> {
                    List<Connection> outbound = IntStream.range(0, nextLayer.getTotalNeurons())
                            .mapToObj(value -> new Connection(algorithm)).toList();
                    neuron.outboundConnections.addAll(outbound);
                    nextLayer.connectFrom(outbound);
            });
        });
    }

    void connectFrom(List<Connection> totalIncomingOutbound) {
        var flattenNeurons = this.stream().flatMap((Function<SubLayer, Stream<Neuron>>) Collection::stream).toList();
        Assert.isTrue(flattenNeurons.size() == totalIncomingOutbound.size(), "Must be equal connections in both sides");

        for (int i = 0; i < flattenNeurons.size(); i++) {
            flattenNeurons.get(i).inboundConnections.add(totalIncomingOutbound.get(i));
        }
    }

    Integer getTotalNeurons() {
        return this.stream().map((Function<SubLayer, Integer>) ArrayList::size).reduce(Integer::sum).get();
    }


}
