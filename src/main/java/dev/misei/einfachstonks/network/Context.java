package dev.misei.einfachstonks.network;

import lombok.AllArgsConstructor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@AllArgsConstructor
public class Context {

    //Node, Value
    public final ConcurrentHashMap<UUID, Double> neuronOutput = new ConcurrentHashMap<>();

    //Neuron, parcial Predicted / Neuron
    public final MultiValueMap<UUID, Double> weightedGradient = new LinkedMultiValueMap<>();

    public final Double learningRatio;
    public final Double momentum;
}
