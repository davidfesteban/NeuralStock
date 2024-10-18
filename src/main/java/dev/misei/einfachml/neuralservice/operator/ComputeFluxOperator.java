package dev.misei.einfachml.neuralservice.operator;

import dev.misei.einfachml.neuralservice.domain.Network;
import dev.misei.einfachml.repository.model.DataPair;
import dev.misei.einfachml.repository.model.PredictedData;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.StreamCorruptedException;
import java.util.UUID;
import java.util.function.Consumer;

@Component
public class ComputeFluxOperator {

    public Flux<PredictedData> compute(Network network, Flux<DataPair> dataset, int epochs, Consumer<Network> callbackBackup) {
        if (network.getStatus().isRunning()) {
            return Flux.error(new IllegalStateException("Network is already on use"));
        }

        return Flux.range(network.getStatus().getAccumulatedEpochs(), epochs)
                .concatMap(epoch -> {

                    //Each 1000 epochs, save the state so we can go in the past
                    if (epoch % 1000 == 0) {
                        callbackBackup.accept(network);
                    }

                    var flux = dataset.concatMap(dataPair -> {
                        var predictedData = network.compute(dataPair.getInputs(), dataPair.getExpected());

                        Mono<PredictedData> error = checkForInfiniteAndNaN(predictedData);

                        return error == null ? Mono.just(predictedData) : error;
                    });

                    network.getStatus().incrementAccEpoch();

                    return flux;
                })
                .doOnSubscribe(subscription -> {
                    network.getStatus().setRunning(true);
                    network.getStatus().setTrainingId(UUID.randomUUID());
                    network.getStatus().setGoalEpochs(epochs);
                })
                .doOnComplete(() -> {
                    callbackBackup.accept(network);
                })
                .doOnTerminate(() -> {
                    network.getStatus().setRunning(false);
                });
    }

    public Flux<PredictedData> predict(Network network, Flux<DataPair> dataset) {
        if (network.getStatus().isRunning()) {
            return Flux.error(new IllegalStateException("Network is already on use"));
        }

        return dataset.
                concatMap(dataPair -> {
                    return Mono.just(network.predict(dataPair.getInputs(), dataPair.getExpected()));
                }).doOnSubscribe(subscription -> {
                    network.getStatus().setRunning(true);
                })
                .doOnTerminate(() -> {
                    network.getStatus().setRunning(false);
                });
    }

    private Mono<PredictedData> checkForInfiniteAndNaN(PredictedData predictedData) {
        if (predictedData.getMseError().isNaN() || predictedData.getMseError().isInfinite() ||
                predictedData.getPredicted().stream().anyMatch(aDouble -> aDouble.isNaN() || aDouble.isInfinite())) {
            return Mono.error(new StreamCorruptedException("NaN | Inf detected in prediction results. Stopping execution."));
        }
        return null;
    }
}
