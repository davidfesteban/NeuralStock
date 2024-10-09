package dev.misei.einfachstonks.neuralservice;

import dev.misei.einfachstonks.neuralservice.domain.Network;
import dev.misei.einfachstonks.neuralservice.domain.algorithm.Algorithm;
import dev.misei.einfachstonks.neuralservice.domain.algorithm.AlgorithmType;
import dev.misei.einfachstonks.neuralservice.domain.algorithm.StandardComplexity;
import dev.misei.einfachstonks.neuralservice.domain.data.Datapair;
import dev.misei.einfachstonks.neuralservice.domain.data.Dataset;
import dev.misei.einfachstonks.neuralservice.domain.shape.StandardShape;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;

@SpringBootTest
public class NeuralServiceSpringTest {

    @Autowired
    NeuralService neuralService;

    @Test
    void letsgo() throws ExecutionException, InterruptedException {
        var uuid = neuralService.create(Network.create(new Algorithm(2, 1, 0.01,
                StandardComplexity.NORMAL.getComplexityValue(), false, AlgorithmType.LEAKY_RELU, StandardShape.PERCEPTRON), createDatasetSum()));

        var uuid2 = neuralService.create(Network.create(new Algorithm(2, 1, 0.01,
                StandardComplexity.ADVANCED.getComplexityValue(), false, AlgorithmType.LEAKY_RELU, StandardShape.PERCEPTRON), createDatasetSum()));


        var latch = neuralService.trainElasticAsync(uuid, 10000);
        //neuralService.predictAsync(uuid, 10000); Busy case
        var latch2 = neuralService.trainElasticAsync(uuid2, 10000);

        latch.await();
        latch2.await();
    }

    private Dataset createDatasetSum() {
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
