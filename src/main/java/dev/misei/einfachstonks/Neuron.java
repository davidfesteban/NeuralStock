package dev.misei.einfachstonks;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@Setter
public class Neuron {

    private final UUID id;

    // Neuron, Weight
    private List<RefNeuron> prevLayerNeuron = new ArrayList<>();

    private Double bias;
    private double gradient;
    private Double predicted;


    public Neuron(UUID uuid) {
        this.predicted = 0d;
        this.bias = 1d;
        this.id = uuid;
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
            predicted = Algorithm.sigmoid(preActivation.get() + bias);
        } else {
            //Input layer
        }


        ContextCache.predictedValues.put(this.id, predicted);
    }

    public double error(double target) {
        return target - predicted;
    }

    public void computeBackward(double target) {
        this.gradient = error(target) * Algorithm.sigmoidDerivative(predicted);
        prevLayerNeuron.forEach(new Consumer<RefNeuron>() {
            @Override
            public void accept(RefNeuron refNeuron) {
                ContextCache.weightedGradient.add(refNeuron.getRef(), gradient * refNeuron.getWeight());
            }
        });
    }

    public void computeBackward() {
        this.gradient = ContextCache.weightedGradient.get(this.id).stream().reduce(Double::sum).orElse(0d)
                * Algorithm.sigmoidDerivative(predicted);

        prevLayerNeuron.forEach(new Consumer<RefNeuron>() {
            @Override
            public void accept(RefNeuron refNeuron) {
                ContextCache.weightedGradient.add(refNeuron.getRef(), gradient * refNeuron.getWeight());
            }
        });
    }

    public void updateWeight(double lr, double mu) {
        prevLayerNeuron = prevLayerNeuron.stream().map(new Function<RefNeuron, RefNeuron>() {
            @Override
            public RefNeuron apply(RefNeuron refNeuron) {
                double prevDelta = refNeuron.getWeightDelta();
                var weightDelta = lr * gradient * ContextCache.predictedValues.get(refNeuron.ref);
                return refNeuron.withWeightDelta(weightDelta).withWeight(refNeuron.getWeight() + weightDelta + mu * prevDelta);
            }
        }).toList();
    }


    @Override
    public String toString() {
        return "\nNeuron " + id + " =====\n" + String.format("Bias %s", bias) + " : " + String.format("Predicted %s", predicted)
                + "\n" + prevLayerNeuron.stream().map(RefNeuron::toString).collect(Collectors.joining(" ||\n"));
    }
}
