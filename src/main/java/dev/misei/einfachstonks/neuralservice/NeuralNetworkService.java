package dev.misei.einfachstonks.neuralservice;

import dev.misei.einfachstonks.neuralservice.dataset.DataSet;
import dev.misei.einfachstonks.neuralservice.dataset.DataSetList;
import dev.misei.einfachstonks.neuralservice.math.Algorithm;
import dev.misei.einfachstonks.neuralservice.math.ErrorMeasure;
import dev.misei.einfachstonks.neuralservice.math.MathStonk;
import dev.misei.einfachstonks.neuralservice.network.Context;
import dev.misei.einfachstonks.neuralservice.network.Network;
import dev.misei.einfachstonks.neuralservice.network.NetworkFactory;
import dev.misei.einfachstonks.neuralservice.network.NetworkService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class NeuralNetworkService implements NetworkService {

    private final ExecutorService executor;

    private final List<NetworkService> networks;
    private final List<Future<Double>> scores;
    private NetworkService networkService;
    private Double score;

    public NeuralNetworkService() {
        executor = Executors.newVirtualThreadPerTaskExecutor();
        networks = new ArrayList<>();
        scores = new ArrayList<>();
        score = null;
    }

    public void createNetworks(DataSetList dataSetList) {
        networks.clear();
        var learningRatio = 0.01;
        var momentum = 0.9;
        var neuronsPerHiddenLayer = dataSetList.getInputSize();
        int totalHiddenLayers = (dataSetList.getInputSize() / 10) + 1;

        networks.add(NetworkFactory.create(dataSetList, new Context(learningRatio, momentum),
                MathStonk.factorial(neuronsPerHiddenLayer), totalHiddenLayers, Algorithm.LEAKY_RELU, ErrorMeasure.LINEAR));

        networks.add(NetworkFactory.create(dataSetList, new Context(learningRatio, momentum),
                MathStonk.factorial(neuronsPerHiddenLayer), totalHiddenLayers * 2, Algorithm.LEAKY_RELU, ErrorMeasure.LINEAR));

        networks.add(NetworkFactory.create(dataSetList, new Context(learningRatio, momentum),
                MathStonk.mean(dataSetList.getInputSize(), dataSetList.getOutputSize()), totalHiddenLayers, Algorithm.LEAKY_RELU, ErrorMeasure.LINEAR));
    }

    @Override
    public void train(int totalEpochs) {
        networks.forEach(network ->
                executor.submit(() -> network.train(totalEpochs)));

        try {
            executor.awaitTermination(10, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        networks.forEach(network ->
                scores.add(
                        executor.submit(() -> {
                            AtomicReference<Double> errorResult = new AtomicReference<>(0d);
                            var snapshotDataSetList = ((Network) network).getDataSetList().getDataSets().stream().limit(5).toList();

                            snapshotDataSetList.forEach(dataSet -> {
                                var predicted = network.predict(dataSet.inputs());

                                for (int i = 0; i < predicted.size(); i++) {
                                    errorResult.set(errorResult.get() + Math.abs(predicted.stream().reduce(Double::sum).orElse(0d) -
                                            dataSet.outputs().stream().reduce(Double::sum).orElse(0d)));
                                }
                            });

                            return errorResult.get();
                        })));

        try {
            executor.awaitTermination(10, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        try {
            for (int i = 0; i < scores.size(); i++) {
                var futureScore = scores.get(i).get();
                if (score == null || score > futureScore) {
                    score = futureScore;
                    networkService = networks.get(i);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public List<Double> predict(List<Double> inputs) {
        return networkService.predict(inputs);
    }

    @Override
    public void accumulateDataset(DataSet dataSet) {
        networkService.accumulateDataset(dataSet);
    }

}
