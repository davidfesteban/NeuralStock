package dev.misei.einfachml.controller.mapper;

import dev.misei.einfachml.neuralservice.domain.Network;
import dev.misei.einfachml.repository.model.AlgorithmBoard;
import dev.misei.einfachml.repository.model.NetworkBoard;
import dev.misei.einfachml.repository.model.PredictedData;
import dev.misei.einfachml.util.EpochCountDown;

import java.util.List;
import java.util.UUID;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;

public class NetworkBoardMapper {
    public static NetworkBoard from(UUID uuid, AlgorithmBoard algorithmBoard, Network network) {
        return new NetworkBoard(uuid,
                0,
                0L,
                algorithmBoard,
                "Ready",
                0,
                0,
                0,
                0,
                0,
                0,
                null);
    }

    public static void update(NetworkBoard networkBoard, Network network, EpochCountDown epochCountDown, List<PredictedData> predictedDataList) {
        List<Double> mseErrors = predictedDataList.stream().collect(Collectors.groupingBy(PredictedData::getEpochHappened))
                .values().stream().map(predictedData -> predictedData.stream()
                        .mapToDouble(PredictedData::calculateMseForPredictedData)
                        .average()
                        .orElse(0d)).toList();

        networkBoard.setTotalEpochs(network.getStatus().getAccumulatedEpochs());
        networkBoard.setTotalTrainingTime(0L);
        //networkBoard.setAlgorithmBoard();
        networkBoard.setStatus(epochCountDown.isCanceled()? "Ready": "Busy");
        networkBoard.setEpochGoal(epochCountDown.getEpochs());
        networkBoard.setCurrentEpoch((int) epochCountDown.getCount());
        networkBoard.setAvgFitnessError(mseErrors.stream().mapToDouble(value -> value).average().orElse(0d));
        networkBoard.setLastFitnessError(mseErrors.getLast());
        networkBoard.setLastTrainingTime(0);
        networkBoard.setAvgFitnessWithInEpochs(0d);
        networkBoard.setMseErrors(mseErrors);
    }
}
