package dev.misei.einfachml.neuralservice;

import dev.misei.einfachml.neuralservice.domain.algorithm.AlgorithmType;
import dev.misei.einfachml.neuralservice.domain.algorithm.StandardComplexity;
import dev.misei.einfachml.neuralservice.domain.shape.StandardShape;
import dev.misei.einfachml.repository.model.AlgorithmBoard;
import dev.misei.einfachml.repository.model.DataPair;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Flux;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EinfachAPITest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    public void testCreateNetwork() {

        AlgorithmBoard algorithmBoardA = new AlgorithmBoard(2, 1, 0.01, StandardComplexity.NORMAL.getComplexityValue(), false,
                AlgorithmType.LEAKY_RELU.name(), StandardShape.PERCEPTRON.name(), null);

        AlgorithmBoard algorithmBoardB = new AlgorithmBoard(2, 1, 0.1, StandardComplexity.NORMAL.getComplexityValue(), false,
                AlgorithmType.LEAKY_RELU.name(), StandardShape.PERCEPTRON.name(), null);

        AlgorithmBoard algorithmBoardC = new AlgorithmBoard(2, 1, 0.01, StandardComplexity.HARD.getComplexityValue(), false,
                AlgorithmType.LEAKY_RELU.name(), StandardShape.PERCEPTRON.name(), null);


        UUID networkA = testRestTemplate.postForEntity(
                "http://localhost:8080/createNetwork",
                algorithmBoardA,
                UUID.class
        ).getBody();

        UUID networkB = testRestTemplate.postForEntity(
                "http://localhost:8080/createNetwork",
                algorithmBoardB,
                UUID.class
        ).getBody();

        UUID networkC = testRestTemplate.postForEntity(
                "http://localhost:8080/createNetwork",
                algorithmBoardC,
                UUID.class
        ).getBody();

        testRestTemplate.postForEntity(
                "http://localhost:8080/includeDataSet?networkId=" + networkA,
                createDatasetSum(networkA),
                UUID.class
        );

        testRestTemplate.postForEntity(
                "http://localhost:8080/includeDataSet?networkId=" + networkB,
                createDatasetSum(networkB),
                UUID.class
        );

        testRestTemplate.postForEntity(
                "http://localhost:8080/includeDataSet?networkId=" + networkC,
                createDatasetSum(networkC),
                UUID.class
        );

        System.out.println(networkA);
        System.out.println(networkB);
        System.out.println(networkC);
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
                        datapairs.add(new DataPair(UUID.randomUUID(), Instant.now().toEpochMilli(), networkId, input, output));
                    }
                });
            }
        });

        return datapairs;
    }
}
