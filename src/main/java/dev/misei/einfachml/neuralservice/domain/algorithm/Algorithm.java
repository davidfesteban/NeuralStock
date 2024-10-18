package dev.misei.einfachml.neuralservice.domain.algorithm;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import dev.misei.einfachml.neuralservice.domain.shape.Shape;
import dev.misei.einfachml.neuralservice.domain.shape.ShapeDeserializer;
import dev.misei.einfachml.neuralservice.domain.shape.StandardShape;
import lombok.AllArgsConstructor;
import lombok.Data;

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

    @JsonDeserialize(using = ShapeDeserializer.class)
    private Shape shape;

    public Double weightInitialiser() {
        return algorithmType.weightInitialiser(inputSize, outputSize);
    }

    public List<List<Integer>> drawShape() {
        return shape.draw(inputSize, outputSize, complexity, tridimensional);
    }
}
