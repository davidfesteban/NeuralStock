package dev.misei.einfachstonks.neuralservice.network;

import dev.misei.einfachstonks.neuralservice.math.AlgorithmType;
import dev.misei.einfachstonks.neuralservice.math.ErrorMeasureType;
import lombok.Data;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * NeuronOutput: NeuronId, PredictedValue
 * WeightedGradient: NeuronId, List-Double-Partial_Gradient
 * LearningRatio: The greater, the bigger steps takes on the curve
 * Momentum: The greater, the more last calculations affects into the future
 * AlgorithmType: Sigmoid, LeakyRelu. In the future, it will be Neuron based and not context
 * ErrorMeasureType: Normal diff or MSE. In the future, it will be Neuron based and not context
 */

@Data
public class Context {

    //NeuronId, PredictedValue
    public final ConcurrentHashMap<UUID, Double> neuronOutput = new ConcurrentHashMap<>();

    //NeuronId, Partial Gradient Map
    public final MultiValueMap<UUID, Double> weightedGradient = new LinkedMultiValueMap<>();

    public final Double learningRatio;
    public final Double momentum;
    public final AlgorithmType algorithmType;
    public final ErrorMeasureType errorMeasureType;
}
