package dev.misei.einfachml.neuralservice;

import dev.misei.einfachml.repository.DataPairRepository;
import dev.misei.einfachml.repository.model.DataPair;
import dev.misei.einfachml.repository.model.NetworkBoard;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class DataService {
//pastWindowTime Math.max(0, dataset.size() - pastWindowTime)
//Assume Dataset is sorted with window time applied.
//Assume Dataset is compatible

//public EpochCountDown mergeAsync(UUID networkId, Dataset innerDataset, int pastWindowTime, int epochRelationPercent) {
//    Network network = networkList.get(networkId);
//    int epochs = (int) Math.max(1, Math.ceil(network.getAccumulatedTrainedEpochs() * epochRelationPercent / 100.0));
//    EpochCountDown latch = new EpochCountDown(epochs);
//
//    var flux = network.merge(innerDataset, pastWindowTime, epochs, latch)
//            .buffer(BATCH_SAVE_SIZE);
//
//    subscribe(networkId, network, flux, latch);
//
//    return latch;
//}

    private DataPairRepository dataPairRepository;

    public List<DataPair> includeDataset(NetworkBoard networkBoard, List<DataPair> dataPairList) {
        dataPairList.forEach(dataPair -> {
            if (dataPair.getNetworkId() == null) {
                throw new IllegalArgumentException("DataPair not attached to Network");
            }

            if (dataPair.getInputs().size() != networkBoard.getAlgorithmBoard().getInputSize() ||
                    dataPair.getExpected().size() != networkBoard.getAlgorithmBoard().getOutputSize()) {
                throw new IllegalArgumentException("DataPair is not consistent");
            }

            dataPair.getInputs().forEach(aDouble -> {
                if (aDouble == null) {
                    throw new IllegalArgumentException("DataPair has null values");
                }
            });

            dataPair.getExpected().forEach(aDouble -> {
                if (aDouble == null) {
                    throw new IllegalArgumentException("DataPair has null values");
                }
            });
        });

        return dataPairRepository.saveAll(dataPairList).stream().sorted().toList();
    }

    public List<DataPair> retrieveAll(UUID networkId) {
        return dataPairRepository.findByNetworkId(networkId).stream().sorted().toList();
    }

    public List<DataPair> retrieveWindowed(UUID networkId, long createdAtStart, long createdAtEnd) {
        return dataPairRepository.findByNetworkIdAndCreatedAtBetween(networkId, createdAtStart, createdAtEnd).stream().sorted().toList();
    }

    public List<DataPair> cleanDatapair(UUID networkId) {
        return dataPairRepository.deleteByNetworkId(networkId);
    }


    //NeuralMetrics generateNeuralMetrics(UUID networkId) {
    //    NeuralMetrics neuralMetrics = new NeuralMetrics();
    //    if (!networkList.containsKey(networkId)) {
    //        return neuralMetrics;
    //    }
//
    //    Network network = networkList.get(networkId);
    //    List<PredictedData> predictedData = predictedDataRepository.findByCollectionId(networkId.toString());
//
    //    neuralMetrics.setUuid(networkId.toString());
    //    neuralMetrics.setStatus(countDownList.containsKey(networkId) ? "Busy" : "Ready");
    //    neuralMetrics.setEpochGoal(countDownList.getOrDefault(networkId, new EpochCountDown(0)).getEpochs());
    //    neuralMetrics.setCurrentEpoch((int) countDownList.getOrDefault(networkId, new EpochCountDown(0)).getCount());
    //    neuralMetrics.setComplexityLevel(StandardComplexity.fromValue(network.getAlgorithm().getComplexity()).name());
    //    neuralMetrics.setTrainingRatio(network.getAlgorithm().getLearningRatio());
    //    neuralMetrics.setAlgorithmType(network.getAlgorithm().getAlgorithmType().name());
//
    //    neuralMetrics.setAvgFitnessError(predictedData.stream().map(new Function<PredictedData, Double>() {
    //        @Override
    //        public Double apply(PredictedData predictedData) {
    //            return predictedData.;
    //        }
    //    }));
//
    //    //Network Metrics
    //    double avgFitnessError;
    //    double lastFitnessError;
    //    double lastTrainingTime;
    //    double avgFitnessWithInEpochs; //This one is a ratio to check how quickly it is learning
    //    int epochs;
    //    int totalTrainingTime;
//
    //    List<Double> mseErrors;
//
    //    //Shape
    //    List<List<Integer>> neuralShape;
    //    List<Integer> trainingEpochs;
    //    List<Integer> trainingTimes;
//
    //    List<List<Double>> input;
    //    List<List<Double>> predicted;
    //    List<List<Double>> expected;
//
//
    //}
}
