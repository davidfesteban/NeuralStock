package dev.misei.einfachml.repository.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class AlgorithmBoard {
    int inputSize;
    int outputSize;
    double learningRatio;
    double complexity;
    boolean tridimensional;
    String algorithmType;
    String shape;
    List<List<Integer>> shapeFigure;
}
