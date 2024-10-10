package dev.misei.einfachml.controller.mapper;

import dev.misei.einfachml.neuralservice.domain.algorithm.Algorithm;
import dev.misei.einfachml.neuralservice.domain.algorithm.AlgorithmType;
import dev.misei.einfachml.neuralservice.domain.shape.StandardShape;
import dev.misei.einfachml.repository.model.AlgorithmBoard;

public class AlgorithmBoardMapper {
    public static Algorithm from(AlgorithmBoard algorithmBoard) {
        return new Algorithm(algorithmBoard.getInputSize(),
                algorithmBoard.getOutputSize(),
                algorithmBoard.getLearningRatio(),
                algorithmBoard.getComplexity(),
                algorithmBoard.isTridimensional(),
                AlgorithmType.valueOf(algorithmBoard.getAlgorithmType()),
                StandardShape.valueOf(algorithmBoard.getShape()));
    }

    public static AlgorithmBoard to(Algorithm algorithm) {
        return new AlgorithmBoard(
                algorithm.getInputSize(),
                algorithm.getOutputSize(),
                algorithm.getLearningRatio(),
                algorithm.getComplexity(),
                algorithm.isTridimensional(),
                algorithm.getAlgorithmType().name(),
                algorithm.getShape().getName(),
                algorithm.drawShape());
    }
}
