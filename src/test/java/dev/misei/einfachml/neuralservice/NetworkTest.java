package dev.misei.einfachml.neuralservice;

/*
@SpringBootTest

public class NetworkTest {

    @Test
    void letsgo() {
        var datasetA = createDatasetSum(true);
        Network network = Network.create(new Algorithm(2, 1, 0.01,
                StandardComplexity.NORMAL.getComplexityValue(), false, AlgorithmType.LEAKY_RELU, StandardShape.PERCEPTRON), datasetA);

        network.train(10000);

        var datasetB = createDatasetSum(false);
        network.predict(datasetB);

        for (var datapair : datasetB.getDataset()) {
            Assert.isTrue(datapair.computeLastMSE() < 0.1, "The value must be low");
            Assert.isTrue(datapair.getOutputs().getFirst() - datapair.getPredictedHistory().getFirst().getFirst() < 0.1, "The value must be low");
        }
    }

    @Test
    void letsgo3d() {
        var datasetA = createDatasetSum(true);
        Network network = Network.create(new Algorithm(2, 1, 0.01,
                StandardComplexity.NORMAL.getComplexityValue(), true, AlgorithmType.LEAKY_RELU, StandardShape.PERCEPTRON), datasetA);

        network.train(10000);

        var datasetB = createDatasetSum(false);
        network.predict(datasetB);

        for (var datapair : datasetB.getDataset()) {
            Assert.isTrue(datapair.computeLastMSE() < 0.1, "The value must be low");
            Assert.isTrue(datapair.getOutputs().getFirst() - datapair.getPredictedHistory().getFirst().getFirst() < 0.1, "The value must be low");
        }
    }

    @Test
    void print3d() {
        var datasetA = createDatasetSum(true);
        Network networkA = Network.create(new Algorithm(2, 1, 0.01,
                StandardComplexity.NORMAL.getComplexityValue(), true, AlgorithmType.LEAKY_RELU, StandardShape.PERCEPTRON), datasetA);

        Network networkB = Network.create(new Algorithm(2, 1, 0.01,
                StandardComplexity.HARD.getComplexityValue(), true, AlgorithmType.LEAKY_RELU, StandardShape.PERCEPTRON), datasetA);

        Network networkC = Network.create(new Algorithm(2, 1, 0.01,
                StandardComplexity.OMG.getComplexityValue(), true, AlgorithmType.LEAKY_RELU, StandardShape.PERCEPTRON), datasetA);

        Network networkD = Network.create(new Algorithm(2, 1, 0.01,
                StandardComplexity.NORMAL.getComplexityValue(), true, AlgorithmType.LEAKY_RELU, StandardShape.TRIANGLE), datasetA);

        Network networkE = Network.create(new Algorithm(2, 1, 0.01,
                StandardComplexity.HARD.getComplexityValue(), true, AlgorithmType.LEAKY_RELU, StandardShape.TRIANGLE), datasetA);

        exportToCsv(networkA, "networkA");
        exportToCsv(networkB, "networkB");
        exportToCsv(networkC, "networkC");
        exportToCsv(networkD, "networkD");
        exportToCsv(networkE, "networkE");
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

    private void exportToCsv(Network network, String name) {
        try (FileWriter csvWriter = new FileWriter(name + ".csv")) {
            // Writing header
            csvWriter.append("x,y,z\n");

            for (int x = 0; x < network.size(); x++) {
                for (int y = 0; y < network.get(x).size(); y++) {
                    for (int z = 0; z < network.get(x).get(y).size(); z++) {
                        csvWriter.append(Integer.toString(x)) // x
                                .append(",")
                                .append(Integer.toString(y)) // y
                                .append(",")
                                .append(Integer.toString(z)) // z
                                .append("\n");
                    }
                }
            }

            // Flushing the writer
            csvWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
} */
