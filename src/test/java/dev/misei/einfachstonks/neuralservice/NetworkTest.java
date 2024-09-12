package dev.misei.einfachstonks.neuralservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.misei.einfachstonks.neuralservice.dataenum.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class NetworkTest {

    @Autowired
    private NeuralService neuralService;

    @Test
    void testFileCreation() throws IOException {
        // Act
        neuralService.exporter();
    }

    @Test
    void happyPath() {

        Network network = Network.create(new Algorithm(3, 1, 0.01, AlgorithmType.LEAKY_RELU, Shape.PERCEPTRON));
        network.compute(allMatchEqually(100, 3), 10000, true);

        var uniqueRandom = allMatchEqually(1, 3);
        network.compute(uniqueRandom, 1, false);

        uniqueRandom.getAverageError();
    }

    @Test
    void happyPath2() throws IOException {

        //Network network = Network.create(new Algorithm(3, 1, 0.01, AlgorithmType.LEAKY_RELU, Shape.PERCEPTRON));
        //network.compute(randomNumberSumTestData(100, 3), 10000, true);
//
        //var uniqueRandom = randomNumberSumTestData(1, 3);
        //network.compute(uniqueRandom, 1, false);
//
        //objectMapper.writeValue(new File("network.json"), network);
//
        //System.out.println(uniqueRandom.getAverageError());
//
        //var untrainedDataset = randomNumberSumTestData(1, 3);
        //network = Network.create(new Algorithm(3, 1, 0.01, AlgorithmType.LEAKY_RELU, Shape.PERCEPTRON));
        //network.compute(untrainedDataset, 1, true);
//
        //System.out.println(untrainedDataset.getAverageError());
//
        //network = objectMapper.readValue(new File("network.json"), objectMapper.getTypeFactory().constructCollectionType(List.class, List.class));
        //network.compute(untrainedDataset, 1, true);
//
        //System.out.println(untrainedDataset.getAverageError());
    }

    private Dataset randomNumberSumTestData(int size, int feature) {

        List<Datapair> datapairs = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            List<Double> inputs = new ArrayList<>();
            for (int j = 0; j < feature; j++) {
                inputs.add(Math.random()*10);
            }

            // Calculate the sum of inputs as the output
            double sum = inputs.stream().mapToDouble(Double::doubleValue).sum();

            // Create a Datapair with inputs and the output (sum)
            List<Double> outputs = new ArrayList<>();
            outputs.add(sum);  // The output is the sum of the inputs

            datapairs.add(new Datapair(inputs, outputs));
        }

        // Create the dataset and add all datapairs
        Dataset dataset = new Dataset(datapairs);

        return dataset;
    }

    private Dataset allMatchEqually(int size, int feature) {

        List<Datapair> datapairs = new ArrayList<>();

        // Loop to create datapairs
        for (int i = 0; i < size; i++) {
            // Generate random inputs (0 or 1)
            List<Double> inputs = new ArrayList<>();
            for (int j = 0; j < feature; j++) {
                inputs.add(Math.random() >= 0.5 ? 1.0 : 0.0);  // Randomly choose 0 or 1
            }

            // Calculate the AND gate output (1 if all inputs are 1, otherwise 0)
            double andResult = inputs.stream().allMatch(input -> input == 1.0) ? 1.0 : inputs.stream().allMatch(input -> input == 0.0) ? 1.0 : 0.0;

            // Create a Datapair with inputs and the output (AND result)
            List<Double> outputs = new ArrayList<>();
            outputs.add(andResult);  // The output is the AND of the inputs

            datapairs.add(new Datapair(inputs, outputs));
        }

        // Create the dataset and add all datapairs
        Dataset dataset = new Dataset(datapairs);

        return dataset;
    }


}