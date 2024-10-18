package dev.misei.einfachml.neuralservice.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.misei.einfachml.neuralservice.domain.algorithm.Algorithm;
import dev.misei.einfachml.repository.model.PredictedData;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

@Slf4j
@AllArgsConstructor
@RequiredArgsConstructor
@Setter
@Getter
public class Network extends ArrayList<Layer> {

    private Algorithm algorithm;
    private List<Connection> inboundFeeder;
    private List<Connection> outboundFeeder;
    private Status status;

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
                                subLayerSize -> IntStream.range(0, subLayerSize).mapToObj(i -> new Neuron(algorithm)).toList()).map(SubLayer::new)
                        .toList())
                .map(Layer::new)
                .toList());

        result.connectAll(algorithm);

        return result;
    }


    public PredictedData compute(List<Double> input, List<Double> expect) {
        computeForward(input);
        PredictedData predictedData = new PredictedData(
                UUID.randomUUID(), Instant.now().toEpochMilli(), status.getNetworkId(),
                status.getAccumulatedEpochs(), outboundFeeder.stream().map(connection -> connection.parentActivation).toList(),
                input, expect);
        computeBackward(expect);

        return predictedData;
    }

    public PredictedData predict(List<Double> input, List<Double> expect) {
        computeForward(input);
        return new PredictedData(
                UUID.randomUUID(), Instant.now().toEpochMilli(), status.getNetworkId(),
                status.getAccumulatedEpochs(), outboundFeeder.stream().map(connection -> connection.parentActivation).toList(),
                input, expect);
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

    public synchronized Status getStatus() {
        return status;
    }
}
