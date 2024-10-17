package dev.misei.einfachml.neuralservice;


import dev.misei.einfachml.neuralservice.domain.Connection;
import dev.misei.einfachml.neuralservice.domain.Network;
import dev.misei.einfachml.neuralservice.domain.algorithm.Algorithm;
import dev.misei.einfachml.neuralservice.domain.algorithm.AlgorithmType;
import dev.misei.einfachml.neuralservice.domain.shape.StandardShape;
import dev.misei.einfachml.repository.model.DataPair;
import dev.misei.einfachml.repository.model.PredictedData;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import java.util.stream.IntStream;

public class NetworkTest {

    @Test
    void letsgo() {
        UUID networkId = UUID.randomUUID();
        Network networkA = Network.create(networkId, new Algorithm(3, 1, 0.0001, 1.5, true,
                AlgorithmType.LEAKY_RELU, StandardShape.PERCEPTRON));
        List<DataPair> dataSet = createDataset(networkId);
        List<PredictedData> predictedDataList = new ArrayList<>();
        IntStream.range(0, 200000).forEach(new IntConsumer() {
            @Override
            public void accept(int value) {
                dataSet.forEach(new Consumer<DataPair>() {
                    @Override
                    public void accept(DataPair dataPair) {
                        networkA.computeForward(dataPair.getInputs());
                        networkA.computeBackward(dataPair.getExpected());
                    }
                });
            }
        });

        dataSet.forEach(new Consumer<DataPair>() {
            @Override
            public void accept(DataPair dataPair) {
                networkA.computeForward(dataPair.getInputs());
                PredictedData predictedData = new PredictedData(
                        UUID.randomUUID(), Instant.now().toEpochMilli(), dataPair.getNetworkId(),
                        200000, networkA.getOutboundFeeder().stream().map(Connection::getParentActivation).toList(),
                        dataPair.getInputs(), dataPair.getExpected());
                predictedDataList.add(predictedData);
                networkA.computeBackward(dataPair.getExpected());
            }
        });

        var he = predictedDataList.reversed();
        var ha = he.stream().filter(new Predicate<PredictedData>() {
            @Override
            public boolean test(PredictedData predictedData) {
                return !predictedData.getPredicted().getFirst().isNaN();
            }
        }).toList();

        var av =  he.stream().filter(new Predicate<PredictedData>() {
            @Override
            public boolean test(PredictedData predictedData) {
                return he.getFirst().getEpochHappened() == predictedData.getEpochHappened();
            }
        }).mapToDouble(new ToDoubleFunction<PredictedData>() {
            @Override
            public double applyAsDouble(PredictedData value) {
                return value.getMseError();
            }
        }).average().getAsDouble();
        System.out.println("Hola");
    }

    @Test
    //void letsgo3d() {
    //    var datasetA = createDatasetSum(true);
    //    Network network = Network.create(new Algorithm(2, 1, 0.01,
    //            StandardComplexity.NORMAL.getComplexityValue(), true, AlgorithmType.LEAKY_RELU, StandardShape.PERCEPTRON), datasetA);
//
    //    network.train(10000);
//
    //    var datasetB = createDatasetSum(false);
    //    network.predict(datasetB);
//
    //    for (var datapair : datasetB.getDataset()) {
    //        Assert.isTrue(datapair.computeLastMSE() < 0.1, "The value must be low");
    //        Assert.isTrue(datapair.getOutputs().getFirst() - datapair.getPredictedHistory().getFirst().getFirst() < 0.1, "The value must be low");
    //    }
    //}
//
    //@Test
    //void print3d() {
    //    var datasetA = createDatasetSum(true);
    //    Network networkA = Network.create(new Algorithm(2, 1, 0.01,
    //            StandardComplexity.NORMAL.getComplexityValue(), true, AlgorithmType.LEAKY_RELU, StandardShape.PERCEPTRON), datasetA);
//
    //    Network networkB = Network.create(new Algorithm(2, 1, 0.01,
    //            StandardComplexity.HARD.getComplexityValue(), true, AlgorithmType.LEAKY_RELU, StandardShape.PERCEPTRON), datasetA);
//
    //    Network networkC = Network.create(new Algorithm(2, 1, 0.01,
    //            StandardComplexity.OMG.getComplexityValue(), true, AlgorithmType.LEAKY_RELU, StandardShape.PERCEPTRON), datasetA);
//
    //    Network networkD = Network.create(new Algorithm(2, 1, 0.01,
    //            StandardComplexity.NORMAL.getComplexityValue(), true, AlgorithmType.LEAKY_RELU, StandardShape.TRIANGLE), datasetA);
//
    //    Network networkE = Network.create(new Algorithm(2, 1, 0.01,
    //            StandardComplexity.HARD.getComplexityValue(), true, AlgorithmType.LEAKY_RELU, StandardShape.TRIANGLE), datasetA);
//
    //    exportToCsv(networkA, "networkA");
    //    exportToCsv(networkB, "networkB");
    //    exportToCsv(networkC, "networkC");
    //    exportToCsv(networkD, "networkD");
    //    exportToCsv(networkE, "networkE");
    //}


    private List<DataPair> createDataset(UUID networkId) {
        List<DataPair> datapairs = new ArrayList<>();

        IntStream.range(0, 10).forEach(new IntConsumer() {
            @Override
            public void accept(int x) {
                IntStream.range(0, 10).forEach(new IntConsumer() {
                    @Override
                    public void accept(int y) {
                        datapairs.add(new DataPair(
                                networkId,
                                UUID.randomUUID(),
                                Instant.now().toEpochMilli(),
                                List.of((double) x, (double) y, 0d),
                                List.of((double) (x + y))
                        ));

                        datapairs.add(new DataPair(
                                networkId,
                                UUID.randomUUID(),
                                Instant.now().toEpochMilli(),
                                List.of((double) x, (double) y, 1d),
                                List.of((double) (x - y))
                        ));

                        datapairs.add(new DataPair(
                                networkId,
                                UUID.randomUUID(),
                                Instant.now().toEpochMilli(),
                                List.of((double) x, (double) y, 2d),
                                List.of((double) (x * y))
                        ));
                    }
                });
            }
        });

        return datapairs;
    }

    //private void exportToCsv(Network network, String name) {
    //    try (FileWriter csvWriter = new FileWriter(name + ".csv")) {
    //        // Writing header
    //        csvWriter.append("x,y,z\n");
//
    //        for (int x = 0; x < network.size(); x++) {
    //            for (int y = 0; y < network.get(x).size(); y++) {
    //                for (int z = 0; z < network.get(x).get(y).size(); z++) {
    //                    csvWriter.append(Integer.toString(x)) // x
    //                            .append(",")
    //                            .append(Integer.toString(y)) // y
    //                            .append(",")
    //                            .append(Integer.toString(z)) // z
    //                            .append("\n");
    //                }
    //            }
    //        }
//
    //        // Flushing the writer
    //        csvWriter.flush();
    //    } catch (IOException e) {
    //        e.printStackTrace();
    //    }
    //}
}
