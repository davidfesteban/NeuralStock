package dev.misei.einfachstonks.neuralservice.network;

import dev.misei.einfachstonks.neuralservice.dataset.DataSet;
import dev.misei.einfachstonks.neuralservice.dataset.DataSetList;
import dev.misei.einfachstonks.neuralservice.math.AlgorithmType;
import dev.misei.einfachstonks.neuralservice.math.ErrorMeasureType;
import dev.misei.einfachstonks.neuralservice.math.MathStonk;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class NeuralNetworkService {

    private final ExecutorService executor;

    private final ConcurrentHashMap<UUID, Network> networks;
    private final ConcurrentHashMap<UUID, Double> scoreByNetworks;

    public NeuralNetworkService() {
        executor = Executors.newVirtualThreadPerTaskExecutor();
        networks = new ConcurrentHashMap<>();
        scoreByNetworks = new ConcurrentHashMap<>();
    }

    /**
     * Standard Network for lazy people
     */
    public UUID createNetwork(DataSetList dataSetList) {
        var learningRatio = 0.01;
        var momentum = 0.9;
        var neuronsPerHiddenLayer = MathStonk.factorial(dataSetList.getInputSize());
        int totalHiddenLayers = (dataSetList.getInputSize() / 10) + 2;

        return createNetwork(dataSetList, new Context(learningRatio, momentum, AlgorithmType.LEAKY_RELU, ErrorMeasureType.LINEAR),
                neuronsPerHiddenLayer, totalHiddenLayers);
    }

    public UUID createNetwork(DataSetList dataSetList, Context context, int neuronsPerHiddenLayer, int totalHiddenLayers) {
        var id = UUID.randomUUID();
        networks.put(id, Network.create(dataSetList, context, neuronsPerHiddenLayer, totalHiddenLayers));
        return id;
    }

    public void trainAll(int totalEpochs) throws InterruptedException {
        networks.forEach((uuid, network) ->
                executor.submit(() -> {
                    network.train(totalEpochs);
                }));

        executor.awaitTermination(24, TimeUnit.HOURS);
    }

    public void train(UUID networkId, int totalEpochs) {
        final Network network = networks.get(networkId);
        network.train(totalEpochs);
    }

    public List<Double> predict(UUID networkId, DataSet dataSet, boolean forTraining) throws InterruptedException {
        final Network network = networks.get(networkId);
        return network.predict(dataSet, forTraining);
    }

    public UUID testScoreAll(int snapshotPopulation) throws InterruptedException {
        networks.forEach((uuid, network) -> executor.submit(() -> {
            List<Double> errorCounter = new ArrayList<>();

            var datasetCopy = new ArrayList<>(network.getDataSetList().getDataSets());
            Collections.shuffle(datasetCopy);
            datasetCopy.stream().limit(snapshotPopulation).forEach(dataSet -> {
                var predictedValues = network.predict(dataSet, false);
                var error = ErrorMeasureType.LINEAR.calculate(predictedValues.stream().reduce(Double::sum).orElse(0d),
                        dataSet.outputs().stream().reduce(Double::sum).orElse(0d));

                errorCounter.add(error);
            });
            var score = errorCounter.stream().reduce(Double::sum).orElse(0d) / errorCounter.size();

            log.info(uuid + " " + score);

            scoreByNetworks.put(uuid, score);
        }));


        executor.awaitTermination(24, TimeUnit.HOURS);

        Map.Entry<UUID, Double> bestEntry = scoreByNetworks.entrySet()
                .stream()
                .min(Map.Entry.comparingByValue())
                .orElseThrow(NoSuchElementException::new);

        return bestEntry.getKey();
    }

}
