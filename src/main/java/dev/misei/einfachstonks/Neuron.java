package dev.misei.einfachstonks;

import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static dev.misei.einfachstonks.Algorithm.sigmoidDerivative;

@Getter
@Setter
public class Neuron {

    private final Random random;
    private final UUID id;

    // Neuron, Weight
    private List<RefNeuron> prevLayerNeuron = new ArrayList<>();

    private Double bias;
    private Double predicted;
    private Double learningRate;


    public Neuron(UUID uuid) {
        this.random = new Random();
        this.predicted = 0d;
        this.bias = random.nextDouble(-1, 1);
        this.id = uuid;
        this.learningRate = 0.05;
    }


    public void addAllPrev(List<RefNeuron> refNeurons) {
        prevLayerNeuron = refNeurons;
    }

    public void computeForward() {
        AtomicDouble preActivation = new AtomicDouble(0d);

        if (!prevLayerNeuron.isEmpty()) {
            prevLayerNeuron.forEach(refNeuron -> {
                var predicted = ContextCache.predictedValues.get(refNeuron.getRef());
                preActivation.increment(predicted * refNeuron.weight);
            });
        } else {
            preActivation.set(predicted);
        }

        predicted = Algorithm.sigmoid(preActivation.get() + bias);
        ContextCache.predictedValues.put(this.id, predicted);
    }

    public void computeBackward() {
        // Step 1: Calculate error between expected output and predicted value
        double expectedOutput = ContextCache.backwardTarget.get(this.id);
        double outputError = expectedOutput - predicted;

        // Step 2: Calculate gradient using the derivative of the sigmoid function
        double outputGradient = outputError * Algorithm.sigmoidDerivative(predicted);

        // Step 3: Update the weights for connections from previous layer neurons
        prevLayerNeuron = prevLayerNeuron.stream()
                .map(refNeuron -> {
                    // Get current weight
                    double currentWeight = refNeuron.getWeight();
                    // Calculate the new weight
                    double newWeight = currentWeight + (learningRate * outputGradient * ContextCache.predictedValues.get(refNeuron.ref));
                    // Return a new RefNeuron with the updated weight
                    return refNeuron.withWeight(newWeight);
                })
                .toList();

        // Step 4: Update the bias for this neuron
        bias += learningRate * outputGradient;

        // Step 5: Check if this neuron impacts other neurons and propagate error
        prevLayerNeuron.forEach(new Consumer<RefNeuron>() {
            @Override
            public void accept(RefNeuron refNeuron) {
                ContextCache.backwardTarget.put(refNeuron.getRef(), outputGradient * refNeuron.getWeight());
            }
        });
    }


    @Override
    public String toString() {
        return "\nNeuron " + id + " =====\n" + String.format("Bias %s", bias) + " : " + String.format("Predicted %s", predicted)
                + "\n" + prevLayerNeuron.stream().map(RefNeuron::toString).collect(Collectors.joining(" ||\n"));
    }
}
