package dev.misei.einfachstonks;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.misei.einfachstonks.neuralservice.dataset.DataSet;
import dev.misei.einfachstonks.neuralservice.dataset.DataSetList;
import dev.misei.einfachstonks.neuralservice.math.AlgorithmType;
import dev.misei.einfachstonks.neuralservice.math.ErrorMeasureType;
import dev.misei.einfachstonks.neuralservice.network.Context;
import dev.misei.einfachstonks.neuralservice.network.NeuralNetworkService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

@SpringBootTest
class StonksV2ApplicationTests {

    @Autowired
    NeuralNetworkService neuralNetworkService;

    @Test
    void givenDatasetDouble_whenSum_thenProbabilitySum() throws InterruptedException, ExecutionException, JsonProcessingException {
        neuralNetworkService.createNetwork(generateAndAnd_And_Door());
        neuralNetworkService.createNetwork(generateAndAnd_And_Door(),
                new Context(0.01, 0.9, AlgorithmType.LEAKY_RELU, ErrorMeasureType.LINEAR),
                6, 1);
        neuralNetworkService.createNetwork(generateAndAnd_And_Door(),
                new Context(0.01, 0.9, AlgorithmType.LEAKY_RELU, ErrorMeasureType.LINEAR),
                16, 2);
        //neuralNetworkService.createNetwork(generateAndAnd_And_Door(),
        //        new Context(0.01, 0.9, AlgorithmType.LEAKY_RELU, ErrorMeasureType.LINEAR),
        //        10, 6);
        neuralNetworkService.createNetwork(generateAndAnd_And_Door(),
                new Context(0.01, 0.9, AlgorithmType.LEAKY_RELU, ErrorMeasureType.LINEAR),
                3, 4);
        neuralNetworkService.trainAll(40000);
        var bestUUID = neuralNetworkService.testScoreAll(3);
        System.out.println(bestUUID);
        System.out.println(new ObjectMapper().writeValueAsString(neuralNetworkService));
        System.out.println(new ObjectMapper().writeValueAsString(
                neuralNetworkService.predict(bestUUID, new DataSet(List.of(1d, 1d, 1d,1d,1d), List.of()), false)));
        System.out.println(new ObjectMapper().writeValueAsString(
                neuralNetworkService.predict(bestUUID, new DataSet(List.of(0d, 0d, 1d,1d,1d), List.of()), false)));

    }

    @Test
    void givenDatasetLogic_whenAND_thenANDBool() {
        //var networkFactory = new NetworkFactory(generateAndAnd_And_Door(), new Context(0.01, 0.9), Algorithm.SIGMOID, ErrorMeasure.LINEAR);
        //network = networkFactory.create(20, 1);
        //network.train(10000);
//
        //network.predict(new DataSet(List.of(0.0, 0.0, 0.0), new ArrayList<>()));
        //network.predict(new DataSet(List.of(0.0, 1.0, 0.0), new ArrayList<>()));
        //network.predict(new DataSet(List.of(1.0, 1.0, 1.0), new ArrayList<>()));
    }

    private DataSetList generateAndAnd_And_Door() {
        DataSetList dataSetList = new DataSetList();

        // Generate all combinations for 10-input AND gate
        int totalCombinations = (int) Math.pow(2, 5); // 1024 combinations
        for (int i = 0; i < totalCombinations; i++) {
            List<Double> inputs = new ArrayList<>();
            boolean allOnes = true;

            // Convert the integer to binary and check each bit
            for (int j = 0; j < 5; j++) {
                if ((i & (1 << j)) != 0) {
                    inputs.add(0, 1.0); // Push to the beginning to reverse order
                } else {
                    inputs.add(0, 0.0); // Push to the beginning to reverse order
                    allOnes = false;
                }
            }

            // Determine output based on whether all inputs are 1.0
            double output = allOnes ? 1.0 : 0.0;
            dataSetList.accumulateTraining(new DataSet(inputs, List.of(output)));
        }

        return dataSetList;
    }

    private DataSet threeRandomDoubleNumber() {
        var random = new Random();

        List<Double> testData = List.of(Math.floor(random.nextDouble(1) * 10) / 10, Math.floor(random.nextDouble(1) * 10) / 10);
        List<Double> resultData = List.of(testData.stream().reduce(Double::sum).get());

        return new DataSet(testData, resultData);
    }

    private DataSetList generateRandomDataByBatch(int amount) {
        DataSetList data = new DataSetList();

        for (int i = 0; i < amount; i++) {
            data.accumulateTraining(threeRandomDoubleNumber());
        }

        return data;
    }

}
