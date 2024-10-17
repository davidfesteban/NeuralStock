package dev.misei.einfachml.neuralservice;

import dev.misei.einfachml.neuralservice.domain.Network;
import dev.misei.einfachml.repository.model.DataPair;
import dev.misei.einfachml.repository.model.PredictedData;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class NetworkFluxOperator {

    //Avoid to have huge collections in memory
    public Flux<PredictedData> computeFlux(Flux<DataPair> dataset, int epochs, Consumer<Network> networkCallback) {
        return Flux.range(status.getAccumulatedEpochs(), epochs)
                .concatMap(epoch -> {
                    status.setCurrentEpochToGoal(epoch - status.getAccumulatedEpochs());

                    //Each 1000 epochs, save the state so we can go in the past
                    if (epoch % 1000 == 0) {
                        networkCallback.accept(this);
                    }

                    return dataset.concatMap(dataPair -> {
                        computeForward(dataPair.getInputs());
                        PredictedData predictedData = new PredictedData(
                                UUID.randomUUID(), Instant.now().toEpochMilli(), dataPair.getNetworkId(),
                                epoch, outboundFeeder.stream().map(connection -> connection.parentActivation).toList(),
                                dataPair.getInputs(), dataPair.getExpected());
                        computeBackward(dataPair.getExpected());

                        if (predictedData.getMseError().isNaN() || predictedData.getMseError().isInfinite() ||
                                predictedData.getPredicted().stream().anyMatch(aDouble -> aDouble.isNaN() || aDouble.isInfinite())) {
                            return Mono.error(new IllegalStateException("NaN | Inf detected in prediction results. Stopping execution."));
                        }

                        return Mono.just(predictedData);
                    });
                })
                .doOnSubscribe(subscription -> {
                    status.setRunning(true);
                    status.setTrainingId(UUID.randomUUID());
                    status.setGoalEpochs(epochs);
                })
                .doOnTerminate(() -> {
                    status.setAccumulatedEpochs(status.getAccumulatedEpochs() + epochs);
                    status.setRunning(false);
                });
    }

    public Flux<PredictedData> predictAsync(List<DataPair> dataset) {
        return Flux.fromIterable(dataset)
                .concatMap(dataPair -> {
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
}
