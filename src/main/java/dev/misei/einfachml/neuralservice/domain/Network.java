package dev.misei.einfachml.neuralservice.domain;

import dev.misei.einfachml.neuralservice.domain.algorithm.Algorithm;
import dev.misei.einfachml.repository.model.DataPair;
import dev.misei.einfachml.repository.model.PredictedData;
import dev.misei.einfachml.util.EpochCountDown;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.scheduler.Schedulers;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

//TODO: Refactor antipattern
//TODO: Lock to avoid multiple ops
//TODO: Inmmutable Algorithm
@Slf4j
@Getter
public class Network extends ArrayList<Layer> {

    private final Algorithm algorithm;
    private final List<Connection> inboundFeeder;
    private final List<Connection> outboundFeeder;
    private final Status status;

    private Network(Algorithm algorithm) {
        this.algorithm = algorithm;
        this.inboundFeeder = new ArrayList<>();
        this.outboundFeeder = new ArrayList<>();
        this.status = new Status(false, 0);
    }

    public static Network create(Algorithm algorithm) {
        Network result = new Network(algorithm);

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

    public Flux<PredictedData> computeFlux(List<DataPair> dataset, int epochs, boolean forTraining, EpochCountDown latch) {
        if (status.isRunning()) {
            return Flux.error(() -> new IllegalStateException("On Going Ops"));
        }

        return Flux.<PredictedData>create(sink -> {
                    status.setRunning(true);
                    IntStream.range(0, epochs).forEach(value -> {
                        compute(dataset, forTraining, sink);

                        if (forTraining) {
                            status.incrementAccEpoch();
                        }

                        latch.countDown();
                    });

                    sink.complete();
                })
                .subscribeOn(Schedulers.boundedElastic())
                .doOnTerminate(() -> status.setRunning(false))
                .doOnComplete(() -> System.out.println("Training completed"));
    }

    private void compute(List<DataPair> dataset, boolean forTraining, FluxSink<PredictedData> sinkPoint) {
        dataset.forEach(dataPair -> {
            computeForward(dataPair.getInputs());

            sinkPoint.next(new PredictedData(UUID.randomUUID(), Instant.now().toEpochMilli(), dataPair.getNetworkId(), status.getAccumulatedEpochs(),
                    outboundFeeder.stream().map(connection -> connection.parentActivation).toList(), dataPair.getInputs(), dataPair.getExpected()));

            if (forTraining) {
                computeBackward(dataPair.getExpected());
            }
        });
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

    public synchronized Algorithm getAlgorithm() {
        return algorithm;
    }
}
