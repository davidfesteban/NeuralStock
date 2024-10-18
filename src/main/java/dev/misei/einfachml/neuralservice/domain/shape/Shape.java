package dev.misei.einfachml.neuralservice.domain.shape;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

@JsonDeserialize(using = ShapeDeserializer.class)
public interface Shape {
    List<List<Integer>> draw(int inputSize, int outputSize, double complexity, boolean tridimensional);
    String getName();
}
