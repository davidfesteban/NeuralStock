package dev.misei.einfachstonks.neuralservice;

import dev.misei.einfachstonks.neuralservice.dataenum.AlgorithmType;
import dev.misei.einfachstonks.neuralservice.dataenum.Datapair;
import dev.misei.einfachstonks.neuralservice.dataenum.Dataset;
import dev.misei.einfachstonks.neuralservice.dataenum.Shape;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class NetworkTest {

    @Test
    void happyPath() {

        Network network = Network.create(2, 1, Shape.PERCEPTRON, 0.01, AlgorithmType.RELU);
        network.compute(allMatchEqually(100, 2), 10000, true);

        var uniqueRandom = allMatchEqually(1, 3);
        network.compute(uniqueRandom, 1, false);

        uniqueRandom.getAverageError();

    }

    private Dataset randomNumberSumTestData(int size, int feature) {

        List<Datapair> datapairs = new ArrayList<>();

        // Loop to create 10 datapairs
        for (int i = 0; i < size; i++) {
            // Generate 10 random inputs
            List<Double> inputs = new ArrayList<>();
            for (int j = 0; j < feature; j++) {
                inputs.add(Math.round((Math.random()/10.0) * 100000)/100000.0);
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