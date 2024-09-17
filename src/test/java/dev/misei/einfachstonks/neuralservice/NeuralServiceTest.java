package dev.misei.einfachstonks.neuralservice;

import dev.misei.einfachstonks.neuralservice.dataenum.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.shaded.org.checkerframework.checker.units.qual.A;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.DoubleSupplier;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

/**
 *
 */

@SpringBootTest
class NeuralServiceTest {

    @Autowired
    private NeuralService neuralService;

    @Test
    void letsgo() throws InterruptedException {
        var datasetA = paraboleDataset(10, false);
        var datasetB = paraboleDataset(10, false);
        var datasetAPredict = paraboleDataset(10, true);
        var datasetBPredict = paraboleDataset(10, true);

        var networkIdA = neuralService.create(Network.create(
                new Algorithm(2, 1, 0.01, AlgorithmType.LEAKY_RELU, Shape.PERCEPTRON), datasetA));

        var networkIdB = neuralService.create(Network.create(
                new Algorithm(2, 1, 0.01, AlgorithmType.SIGMOID, Shape.PERCEPTRON), datasetB));

        neuralService.trainAsync(networkIdA, 1000);
        neuralService.trainAsync(networkIdB, 1000);

        while(neuralService.isBusy(networkIdA)) {
            System.out.println("Busy");
            Thread.sleep(1000);
        }

        while(neuralService.isBusy(networkIdB)) {
            System.out.println("Busy");
            Thread.sleep(1000);
        }


        neuralService.predictAsync(networkIdA, datasetAPredict);
        neuralService.predictAsync(networkIdB, datasetBPredict);

        while(neuralService.isBusy(networkIdA)) {
            System.out.println("Busy");
            Thread.sleep(1000);
        }

        while(neuralService.isBusy(networkIdB)) {
            System.out.println("Busy");
            Thread.sleep(1000);
        }

        exportToCsv(datasetAPredict.getDataset(), "datasetA.csv");
        exportToCsv(datasetBPredict.getDataset(), "datasetB.csv");

        System.out.println("Finalising");

    }



    public Dataset paraboleDataset(int def, boolean forPredict) {
        List<Datapair> datapairs = new ArrayList<>();

        IntStream.rangeClosed(-def, def)
                .forEach(x ->
                        IntStream.rangeClosed(-def, def)
                                .forEach(y ->
                                        datapairs.add(
                                                new Datapair(List.of(x/(double) def, y/(double) def),
                                                        List.of(-(Math.pow(x/(double) def,2) + Math.pow(y/(double) def,2)))))
                                )
                );

        if(forPredict) {
            return new Dataset(datapairs.stream().map(datapair -> new Datapair(datapair.getInputs(), List.of(0d))).toList());
        }

        return new Dataset(datapairs);
    }

    public Dataset paraboleFlyDataset(int def) {
        List<Datapair> datapairs = new ArrayList<>();

        IntStream.rangeClosed(-def, def)
                .forEach(x ->
                        IntStream.rangeClosed(-def, def)
                                .forEach(y ->
                                        datapairs.add(
                                                new Datapair(List.of(x/(double) def, y/(double) def),
                                                        List.of(Math.pow(x/(double) def,2) * (y/(double) def)))
                                )
                ));

        return new Dataset(datapairs);
    }

    public void exportToCsv(List<Datapair> datapairsList, String filePath) {
        try (FileWriter csvWriter = new FileWriter(filePath)) {
            // Writing header
            csvWriter.append("x,y,z\n");

            // Writing data
            for (Datapair datapair : datapairsList) {
                List<Double> inputs = datapair.getInputs();
                List<Double> outputs = datapair.getOutputs();

                if (inputs.size() == 2 && outputs.size() == 1) {
                    csvWriter.append(inputs.get(0).toString()) // x
                            .append(",")
                            .append(inputs.get(1).toString()) // y
                            .append(",")
                            .append(outputs.get(0).toString()) // z
                            .append("\n");
                }
            }

            // Flushing the writer
            csvWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }






}