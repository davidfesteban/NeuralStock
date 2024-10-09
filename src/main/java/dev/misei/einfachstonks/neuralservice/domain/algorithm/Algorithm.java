package dev.misei.einfachstonks.neuralservice.domain.algorithm;

import dev.misei.einfachstonks.neuralservice.domain.shape.Shape;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.List;
import java.util.Random;

@AllArgsConstructor
@Data
public class Algorithm {
    public static final Random RANDOM = new Random();

    private int inputSize;
    private int outputSize;
    private double learningRatio;
    private double complexity;
    private boolean tridimensional;
    private AlgorithmType algorithmType;
    private Shape shape;

    public Double weightInitialiser() {
        return algorithmType.weightInitialiser(inputSize, outputSize);
    }

    public List<List<Integer>> drawShape() {
        return shape.draw(inputSize, outputSize, complexity, tridimensional);
    }
}
