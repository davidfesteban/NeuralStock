package dev.misei.einfachstonks.neuralservice;

import dev.misei.einfachstonks.neuralservice.dataenum.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;

@SpringBootTest
public class NetworkTest {

    @Test
    void letsgo() {
        var datasetA = createDatasetSum(true);
        Network network = Network.create(new Algorithm(2, 1, 0.01, AlgorithmType.LEAKY_RELU, Shape.PERCEPTRON), datasetA);
        network.train(10000);

        var datasetB = createDatasetSum(false);
        network.predict(datasetB);

        for (var datapair : datasetB.getDataset()) {
            Assert.isTrue(datapair.computeLastMSE() < 0.1, "The value must be low");
            Assert.isTrue(datapair.getOutputs().getFirst() - datapair.getPredictedHistory().getFirst().getFirst() < 0.1, "The value must be low");
        }
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
                        output.add((double) (x + y));
                        datapairs.add(new Datapair(input, output));
                    }
                });
            }
        });

        return new Dataset(datapairs);
    }
}
