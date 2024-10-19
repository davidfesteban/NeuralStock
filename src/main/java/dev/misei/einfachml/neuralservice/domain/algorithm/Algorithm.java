package dev.misei.einfachml.neuralservice.domain.algorithm;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import dev.misei.einfachml.neuralservice.domain.shape.Shape;
import dev.misei.einfachml.neuralservice.serial.ShapeDeserializer;
import dev.misei.einfachml.neuralservice.serial.ShapeSerializer;
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

    @JsonSerialize(using = ShapeSerializer.class)
    @JsonDeserialize(using = ShapeDeserializer.class)
    private Shape shape;

    public Double weightInitialiser() {
        return algorithmType.weightInitialiser(inputSize, outputSize);
    }

    public List<List<Integer>> drawShape() {
        return shape.draw(inputSize, outputSize, complexity, tridimensional);
    }
}
