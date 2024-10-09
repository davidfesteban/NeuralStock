package dev.misei.einfachstonks.neuralservice;

import dev.misei.einfachstonks.neuralservice.domain.Network;
import dev.misei.einfachstonks.neuralservice.domain.algorithm.Algorithm;
import dev.misei.einfachstonks.neuralservice.domain.algorithm.AlgorithmType;
import dev.misei.einfachstonks.neuralservice.domain.data.Datapair;
import dev.misei.einfachstonks.neuralservice.domain.data.Dataset;
import dev.misei.einfachstonks.neuralservice.domain.shape.StandardShape;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;


/*
@SpringBootTest
class NeuralServiceTest {

    @Autowired
    private NeuralService neuralService;

    @Test
    void exportNetwork() throws Exception {
        var datasetA = createDatasetSum(true);
        var networkId = neuralService.create(UUID.fromString("396b0c37-7710-4894-a491-2896ffe5a75e"), Network.create(new Algorithm(2, 1, 0.01, 1, false, AlgorithmType.LEAKY_RELU, StandardShape.PERCEPTRON), datasetA));

        neuralService.trainAsync(networkId, 10000);

        while (neuralService.isBusy(networkId)) {
            System.out.println("Busy A");
            Thread.sleep(1000);
        }

        var datasetB = createDatasetSum(false);
        neuralService.predictAsync(networkId, datasetB);

        while (neuralService.isBusy(networkId)) {
            System.out.println("Busy A");
            Thread.sleep(1000);
        }

        for (var datapair : datasetB.getDataset()) {
            Assert.isTrue(datapair.computeLastMSE() < 0.1, "The value must be low");
            Assert.isTrue(datapair.getOutputs().getFirst() - datapair.getPredictedHistory().getFirst().getFirst() < 0.1, "The value must be low");
        }

        neuralService.saveNetwork(networkId);
    }

    @Test
    void importNetwork() throws Exception {
        var networkId = UUID.fromString("396b0c37-7710-4894-a491-2896ffe5a75e");
        neuralService.importNetwork(networkId);

        var datasetB = createDatasetSum(false);
        neuralService.predictAsync(networkId, datasetB);

        while (neuralService.isBusy(networkId)) {
            System.out.println("Busy A");
            Thread.sleep(1000);
        }

        for (var datapair : datasetB.getDataset()) {
            Assert.isTrue(datapair.computeLastMSE() < 0.1, "The value must be low");
            Assert.isTrue(datapair.getOutputs().getFirst() - datapair.getPredictedHistory().getFirst().getFirst() < 0.1, "The value must be low");
        }
    }


    @Test
    void exportParabole() {
        var datasetA = paraboleDataset(10).getDataset();
        try (FileWriter csvWriter = new FileWriter("datasetProof.csv")) {
            // Writing header
            csvWriter.append("x,y,z\n");

            // Writing data
            for (Datapair datapair : datasetA) {
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

    @Test
    void letsgo() throws InterruptedException {
        var datasetA = paraboleDataset(10);
        var datasetB = paraboleDataset(10);
        var datasetAPredict = paraboleDataset(10);
        var datasetBPredict = paraboleDataset(10);

        var networkIdA = neuralService.create(Network.create(
                new Algorithm(2, 1, 0.01, 1, false, AlgorithmType.LEAKY_RELU, StandardShape.PERCEPTRON), datasetA));

        var networkIdB = neuralService.create(Network.create(
                new Algorithm(2, 1, 0.01, 1, false, AlgorithmType.LEAKY_RELU, StandardShape.SQUARE), datasetB));

        neuralService.trainAsync(networkIdA, 200000);
        neuralService.trainAsync(networkIdB, 50000);

        while (neuralService.isBusy(networkIdA)) {
            System.out.println("Busy A");
            Thread.sleep(1000);
        }

        while (neuralService.isBusy(networkIdB)) {
            System.out.println("Busy B");
            Thread.sleep(1000);
        }


        neuralService.predictAsync(networkIdA, datasetAPredict);
        neuralService.predictAsync(networkIdB, datasetBPredict);

        while (neuralService.isBusy(networkIdA)) {
            System.out.println("Busy");
            Thread.sleep(1000);
        }

        while (neuralService.isBusy(networkIdB)) {
            System.out.println("Busy");
            Thread.sleep(1000);
        }

        exportToCsv(datasetAPredict.getDataset(), "datasetA.csv");
        exportToCsv(datasetBPredict.getDataset(), "datasetB.csv");

        System.out.println("Finalising");

    }


    private Dataset paraboleDataset(int def) {
        List<Datapair> datapairs = new ArrayList<>();

        IntStream.rangeClosed(-def, def)
                .forEach(x ->
                        IntStream.rangeClosed(-def, def)
                                .forEach(y ->
                                        datapairs.add(
                                                new Datapair(List.of(x / (double) def, y / (double) def),
                                                        List.of(-(Math.pow(x / (double) def, 2) + Math.pow(y / (double) def, 2)))))
                                )
                );

        return new Dataset(datapairs);
    }

    private Dataset paraboleFlyDataset(int def) {
        List<Datapair> datapairs = new ArrayList<>();

        IntStream.rangeClosed(-def, def)
                .forEach(x ->
                        IntStream.rangeClosed(-def, def)
                                .forEach(y ->
                                        datapairs.add(
                                                new Datapair(List.of(x / (double) def, y / (double) def),
                                                        List.of(Math.pow(x / (double) def, 2) * (y / (double) def)))
                                        )
                                ));

        return new Dataset(datapairs);
    }

    private void exportToCsv(List<Datapair> datapairsList, String filePath) {
        try (FileWriter csvWriter = new FileWriter(filePath)) {
            // Writing header
            csvWriter.append("x,y,z\n");

            // Writing data
            for (Datapair datapair : datapairsList) {
                List<Double> inputs = datapair.getInputs();
                List<Double> outputs = datapair.getPredictedHistory().getFirst();

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


} */
