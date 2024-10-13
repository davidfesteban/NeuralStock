package dev.misei.einfachml.neuralservice;

import dev.misei.einfachml.neuralservice.domain.algorithm.AlgorithmType;
import dev.misei.einfachml.neuralservice.domain.algorithm.StandardComplexity;
import dev.misei.einfachml.neuralservice.domain.shape.StandardShape;
import dev.misei.einfachml.repository.model.AlgorithmBoard;
import dev.misei.einfachml.repository.model.DataPair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;

import static com.mongodb.assertions.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EinfachAPITest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    public void testCreateNetwork() throws Exception {

        AlgorithmBoard algorithmBoardA = new AlgorithmBoard(2, 1, 0.01, StandardComplexity.NORMAL.getComplexityValue(), false,
                AlgorithmType.LEAKY_RELU.name(), StandardShape.PERCEPTRON.name(), null);

        AlgorithmBoard algorithmBoardB = new AlgorithmBoard(2, 1, 0.1, StandardComplexity.NORMAL.getComplexityValue(), false,
                AlgorithmType.LEAKY_RELU.name(), StandardShape.PERCEPTRON.name(), null);

        AlgorithmBoard algorithmBoardC = new AlgorithmBoard(2, 1, 0.01, StandardComplexity.HARD.getComplexityValue(), false,
                AlgorithmType.LEAKY_RELU.name(), StandardShape.PERCEPTRON.name(), null);

        // Create network A, B, C
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

        // Assert network creation
        assertNotNull(networkA);
        assertNotNull(networkB);
        assertNotNull(networkC);

        // Include datasets
        ResponseEntity<UUID> includeDataSetA = testRestTemplate.postForEntity(
                "http://localhost:8080/includeDataSet?networkId=" + networkA,
                createDatasetSum(networkA),
                UUID.class
        );
        Assertions.assertEquals(HttpStatus.OK, includeDataSetA.getStatusCode());

        ResponseEntity<UUID> includeDataSetB = testRestTemplate.postForEntity(
                "http://localhost:8080/includeDataSet?networkId=" + networkB,
                createDatasetSum(networkB),
                UUID.class
        );
        Assertions.assertEquals(HttpStatus.OK, includeDataSetB.getStatusCode());

        ResponseEntity<UUID> includeDataSetC = testRestTemplate.postForEntity(
                "http://localhost:8080/includeDataSet?networkId=" + networkC,
                createDatasetSum(networkC),
                UUID.class
        );
        Assertions.assertEquals(HttpStatus.OK, includeDataSetC.getStatusCode());

        callCompute(networkA, 1000, null, null);
        //callCompute(networkB, 4000, null, null);
        //callCompute(networkC, 4000, null, null);

        // Keep the test alive for a while to let the SSE stream continue
        Thread.sleep(300000); // 30 seconds (adjust as needed)

        // Print network UUIDs for verification
        System.out.println(networkA);
        System.out.println(networkB);
        System.out.println(networkC);

        // Optionally, test other endpoints like prediction or computation using async handling
    }

    private void callCompute(UUID networkId, int epochs, Long creAt, Long creEnd) throws InterruptedException {
        WebClient webClient = WebClient.create("http://localhost:8080");

        webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/compute")
                        .queryParam("networkId", networkId)
                        .queryParam("epochs", epochs)
                        .queryParam("createdAtStart", creAt)
                        .queryParam("createdAtEnd", creEnd)
                        .build())
                .retrieve()
                .bodyToFlux(String.class).subscribe(
                event -> {
                    System.out.println("Received event: " + event);
                },
                error -> {
                    System.err.println("Error receiving SSE: " + error.getMessage());
                },
                () -> {
                    System.out.println("SSE stream completed");
                }
        );
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
                        datapairs.add(new DataPair(networkId, UUID.randomUUID(), Instant.now().toEpochMilli(), input, output));
                    }
                });
            }
        });

        return datapairs;
    }
}
