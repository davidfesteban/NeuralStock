package dev.misei.einfachstonks;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ContextCache {

    //Node, Value
    public static final ConcurrentHashMap<UUID, Double> predictedValues = new ConcurrentHashMap<>();

    //Neuron, parcial Predicted / Neuron
    public static final MultiValueMap<UUID, Double> weightedGradient = new LinkedMultiValueMap<>();
}
