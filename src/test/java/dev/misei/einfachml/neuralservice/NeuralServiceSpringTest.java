package dev.misei.einfachml.neuralservice;

import dev.misei.einfachml.neuralservice.domain.algorithm.AlgorithmType;
import dev.misei.einfachml.neuralservice.domain.algorithm.StandardComplexity;
import dev.misei.einfachml.neuralservice.domain.shape.StandardShape;
import dev.misei.einfachml.repository.model.AlgorithmBoard;
import dev.misei.einfachml.repository.model.DataPair;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;

@SpringBootTest
public class NeuralServiceSpringTest {

    @Autowired
    EinfachAPI einfachAPI;

    @Test
    void letsgo() throws Throwable {
        var networkA = einfachAPI.createNetwork(new AlgorithmBoard(2, 1, 0.01, StandardComplexity.NORMAL.getComplexityValue(), false,
                AlgorithmType.LEAKY_RELU.name(), StandardShape.PERCEPTRON.name(), null));

        var networkB = einfachAPI.createNetwork(new AlgorithmBoard(2, 1, 0.1, StandardComplexity.NORMAL.getComplexityValue(), false,
                AlgorithmType.LEAKY_RELU.name(), StandardShape.PERCEPTRON.name(), null));

        var networkC = einfachAPI.createNetwork(new AlgorithmBoard(2, 1, 0.01, StandardComplexity.HARD.getComplexityValue(), false,
                AlgorithmType.LEAKY_RELU.name(), StandardShape.PERCEPTRON.name(), null));

        einfachAPI.includeDataSet(networkA, createDatasetSum(networkA));
        einfachAPI.includeDataSet(networkB, createDatasetSum(networkB));
        einfachAPI.includeDataSet(networkC, createDatasetSum(networkC));

        var fluxA = einfachAPI.compute(networkA, 1000, null, null);
        var fluxB = einfachAPI.compute(networkB, 1000, null, null);
        var fluxC = einfachAPI.compute(networkC, 1000, null, null);
    }

    @Test
    void letsgao() throws Throwable {
        var networkA = einfachAPI.createNetwork(new AlgorithmBoard(2, 1, 0.01, StandardComplexity.NORMAL.getComplexityValue(), false,
                AlgorithmType.LEAKY_RELU.name(), StandardShape.PERCEPTRON.name(), null));
    }

    private List<DataPair> createDatasetSum(UUID networkId) {
        List<DataPair> datapairs = new ArrayList<>();

        IntStream.range(0, 10).forEach(new IntConsumer() {
            @Override
            public void accept(int x) {
                IntStream.range(0, 10).forEach(new IntConsumer() {
                    @Override
                    public void accept(int y) {
                        List<Double> input = new ArrayList<>();
                        List<Double> output = new ArrayList<>();
                        input.add((double) x);
                        input.add((double) y);
                        output.add((double) (x + y));
                        //datapairs.add(new DataPair(UUID.randomUUID(),Instant.now().toEpochMilli(), networkId, input, output));
                    }
                });
            }
        });

        return datapairs;
    }
}
