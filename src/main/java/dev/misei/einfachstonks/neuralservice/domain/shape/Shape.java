package dev.misei.einfachstonks.neuralservice.domain.shape;

import java.util.List;

public interface Shape {

    List<List<Integer>> draw(int inputSize, int outputSize, double complexity, boolean tridimensional);
}
