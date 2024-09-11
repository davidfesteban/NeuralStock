package dev.misei.einfachstonks.neuralservice.dataenum;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Random;

@AllArgsConstructor
@Getter
public class Algorithm {
    public static final Random RANDOM = new Random();

    private int inputSize;
    private int outputSize;
    private double learningRatio;
    private AlgorithmType algorithmType;
    private Shape shape;

    public Double weightInitialiser() {
        return algorithmType.weightInitialiser(inputSize, outputSize);
    }

    public List<Integer> drawShape() {
        return shape.draw(inputSize, outputSize);
    }
}
