package dev.misei.einfachstonks.neuralservice;

import dev.misei.einfachstonks.neuralservice.dataenum.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.stream.IntStream;

@SpringBootTest
public class NetworkTest {

    @Test
    void letsgo() {
        var datasetA = createDatasetSum(true);
        Network network = Network.create(new Algorithm(2, 1, 0.01, AlgorithmType.SIGMOID, Shape.PERCEPTRON), datasetA);
        network.train(10);

        var datasetB = createDatasetSum(false);
        network.predict(datasetB);

        System.out.println("Funciona?");
    }

    private Dataset createDatasetSum(boolean forTraining) {
        List<Datapair> datapairs = new ArrayList<>();

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
                        output.add(forTraining? (double) (x + y) : 0);
                        datapairs.add(new Datapair(input, output));
                    }
                });
            }
        });

        return new Dataset(datapairs);
    }
}
