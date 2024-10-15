package dev.misei.einfachml.neuralservice.domain;

import dev.misei.einfachml.neuralservice.PredictionListener;
import dev.misei.einfachml.neuralservice.domain.algorithm.Algorithm;
import dev.misei.einfachml.repository.model.DataPair;
import dev.misei.einfachml.repository.model.PredictedData;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
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

    private Network(UUID networkId, Algorithm algorithm) {
        this.algorithm = algorithm;
        this.inboundFeeder = new ArrayList<>();
        this.outboundFeeder = new ArrayList<>();
        this.status = new Status(networkId, false, 0, UUID.randomUUID(), 0, 0);
    }

    public static Network create(UUID networkId, Algorithm algorithm) {
        Network result = new Network(networkId, algorithm);

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

    //Avoid to have huge collections in memory
    public Flux<PredictedData> computeFlux(Flux<DataPair> dataset, int epochs) {
        return Flux.range(0, epochs)
                .doOnNext(epoch -> {
                    status.setCurrentEpochToGoal(epoch);
                    status.incrementAccEpoch();
                })
                .flatMapSequential(epoch -> dataset
                        .map(dataPair -> {
                            computeForward(dataPair.getInputs());
                            PredictedData predictedData = new PredictedData(
                                    UUID.randomUUID(), Instant.now().toEpochMilli(), dataPair.getNetworkId(),
                                    status.getAccumulatedEpochs(), outboundFeeder.stream().map(connection -> connection.parentActivation).toList(),
                                    dataPair.getInputs(), dataPair.getExpected());
                            computeBackward(dataPair.getExpected());
                            return predictedData;
                        })
                )
                .doOnSubscribe(subscription -> {
                    status.setRunning(true);
                    status.setTrainingId(UUID.randomUUID());
                    status.setGoalEpochs(epochs);
                })
                .doOnTerminate(() -> status.setRunning(false));
    }

    public Flux<PredictedData> predictAsync(List<DataPair> dataset) {
        return Flux.fromIterable(dataset)
                .flatMapSequential(dataPair -> {
                    computeForward(dataPair.getInputs());
                    PredictedData predictedData = new PredictedData(
                            UUID.randomUUID(),
                            Instant.now().toEpochMilli(),
                            dataPair.getNetworkId(),
                            status.getAccumulatedEpochs(),
                            outboundFeeder.stream().map(connection -> connection.parentActivation).toList(),
                            dataPair.getInputs(),
                            dataPair.getExpected()
                    );
                    return Mono.just(predictedData);
                })
                .doOnSubscribe(subscription -> {
                    status.setRunning(true);
                })
                .doOnTerminate(() -> {
                    status.setRunning(false);
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
        status.setRunning(false);
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

    public synchronized Status getStatus() {
        return status;
    }
}
