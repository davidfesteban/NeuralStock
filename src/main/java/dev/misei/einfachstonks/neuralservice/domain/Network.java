package dev.misei.einfachstonks.neuralservice.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.misei.einfachstonks.neuralservice.EpochCountDown;
import dev.misei.einfachstonks.neuralservice.domain.algorithm.Algorithm;
import dev.misei.einfachstonks.neuralservice.domain.data.Dataset;
import dev.misei.einfachstonks.neuralservice.model.PredictedPoint;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;

//TODO: Refactor antipattern
public class Network extends ArrayList<Layer> {

    private final Algorithm algorithm;
    private final List<Connection> inboundFeeder;
    private final List<Connection> outboundFeeder;
    private final AtomicBoolean operationInProgress = new AtomicBoolean(false);
    private final Dataset dataset;
    private int accumulatedTrainedEpochs;

    private Network(Algorithm algorithm, Dataset dataset) {
        this.algorithm = algorithm;
        this.inboundFeeder = new ArrayList<>();
        this.outboundFeeder = new ArrayList<>();
        this.dataset = dataset;
        this.accumulatedTrainedEpochs = 0;
    }

    private static <T> T deepCopy(T incoming) {
        try {
            var objectMapper = new ObjectMapper();
            String result = objectMapper.writeValueAsString(incoming);
            return objectMapper.readValue(result, new TypeReference<T>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    //TODO: Lock to avoid multiple ops

    public static Network create(Algorithm incomingAlgorithm, Dataset incomingDataset) {
        Algorithm algorithm = incomingAlgorithm;
        Dataset dataset = incomingDataset;

        //TODO: This must go out
        //Assert.isTrue(inputs.size() == algorithm.getInputSize(), "Input size does not match expected size");
        //Assert.isTrue(outputs.size() == algorithm.getOutputSize(), "Output size does not match expected size");

        Network result = new Network(algorithm, dataset);

        // 3D structure: layer -> sub-layer -> neurons
        result.addAll(algorithm.drawShape().stream()
                .map(layer -> layer.stream().map(
                                subLayerSize -> IntStream.range(0, subLayerSize).mapToObj(i -> new Neuron(algorithm)).toList()).map(SubLayer::new) // Create Neurons for each sublayer
                        .toList())
                .map(Layer::new)
                .toList());

        result.connectAll(algorithm);

        return result;
    }

    public Flux<PredictedPoint> predict(Dataset innerDataset, EpochCountDown latch) {
        if (operationInProgress.get()) {
            return Flux.error(() -> new IllegalStateException("On Going Ops"));
        }
        return Flux.<PredictedPoint>create(sink -> {
                    operationInProgress.set(true);
                    compute(deepCopy(innerDataset), 0, false, sink);
                    latch.countDown();
                    sink.complete();
                })
                .subscribeOn(Schedulers.boundedElastic())
                .doOnTerminate(() -> operationInProgress.set(false))
                .doOnComplete(() -> System.out.println("Prediction completed"));
    }

    public Flux<PredictedPoint> train(int epochs, EpochCountDown latch) {
        return train(epochs, dataset.size(), latch);
    }

    public Flux<PredictedPoint> train(int epochs, int pastWindowTime, EpochCountDown latch) {
        if (operationInProgress.get()) {
            return Flux.error(() -> new IllegalStateException("On Going Ops"));
        }

        return Flux.<PredictedPoint>create(sink -> {
                    operationInProgress.set(true);
                    IntStream.range(0, epochs).forEach(value -> {
                        compute(dataset, Math.max(0, dataset.size() - pastWindowTime), true, sink);
                        latch.countDown();
                    });

                    sink.complete();
                })
                .subscribeOn(Schedulers.boundedElastic())
                .doOnTerminate(() -> {
                    operationInProgress.set(false);
                })
                .doOnComplete(() -> System.out.println("Training completed"));
    }

    public Flux<PredictedPoint> merge(Dataset innerDataset, int pastWindowTime, int epochs, EpochCountDown latch) {
        if (!innerDataset.isCompatible(this.dataset)) {
            throw new IllegalArgumentException("Datasets are not compatible on merge");
        }

        this.dataset.addAll(innerDataset);
        return train(epochs, pastWindowTime, latch);
    }

    private void compute(Dataset innerDataset, int indexStart, boolean train, FluxSink<PredictedPoint> sinkPoint) {
        for (int j = indexStart; j < innerDataset.size(); j++) {

            computeForward(innerDataset.get(j).getInputs());

            sinkPoint.next(new PredictedPoint(innerDataset.get(j),
                    outboundFeeder.stream().map(connection -> connection.parentActivation).toList(),
                    accumulatedTrainedEpochs));

            if (train) {
                computeBackward(innerDataset.get(j).getOutputs());
            }
        }

        if (train) {
            ++accumulatedTrainedEpochs;
        }
    }

    private void computeForward(List<Double> inputs) {
        IntStream.range(0, inboundFeeder.size()).forEach(i -> inboundFeeder.get(i).parentActivation = inputs.get(i));
        this.forEach(Layer::computeForward);
    }

    private void computeBackward(List<Double> outputs) {
        IntStream.range(0, outboundFeeder.size()).forEach(i -> outboundFeeder.get(i).manualOutputFeed = outputs.get(i));

        this.reversed().forEach(Layer::prepareGradient);
        this.reversed().forEach(Layer::updateWeights);
    }

    private void connectAll(Algorithm algorithm) {
        //There is only one sublayer
        this.getFirst().getFirst().forEach(neuron -> {
            Connection connection = new Connection(algorithm);
            neuron.inboundConnections.add(connection);
            inboundFeeder.add(connection);
        });


        //Connect the rest
        for (int layerIndex = 0; layerIndex < this.size() - 1; layerIndex++) {
            Layer currentLayer = this.get(layerIndex);
            Layer nextLayer = this.get(layerIndex + 1);

            currentLayer.connectToAll(nextLayer, algorithm);
        }

        //Connect outputs. Only one sublayer
        this.getLast().getFirst().forEach(neuron -> {
            Connection connection = new Connection(algorithm);
            neuron.outboundConnections.add(connection);
            outboundFeeder.add(connection);
        });
    }

    public void reconnectAll() {
        this.forEach(layer -> layer.forEach(subLayer -> subLayer.forEach(neuron -> neuron.inboundConnections.clear())));

        this.getFirst().connectFrom(inboundFeeder);

        for (int layerIndex = 0; layerIndex < this.size() - 1; layerIndex++) {
            Layer currentLayer = this.get(layerIndex);
            Layer nextLayer = this.get(layerIndex + 1);

            currentLayer.forEach(subLayer -> subLayer.forEach(neuron -> nextLayer.connectFrom(neuron.outboundConnections)));
        }

        outboundFeeder.clear();
        this.getLast().getFirst().forEach(neuron -> outboundFeeder.addAll(neuron.outboundConnections));
    }

    public synchronized int getAccumulatedTrainedEpochs() {
        return accumulatedTrainedEpochs;
    }
}
