package dev.misei.einfachml.controller.dto;

import dev.misei.einfachml.neuralservice.domain.Network;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class NeuralMetrics {

    //Network Definition
    String uuid;
    String status;
    int epochGoal;
    int currentEpoch;
    String complexityLevel;
    double trainingRatio;
    String algorithmType;

    //Network Metrics
    double avgFitnessError;
    double lastFitnessError;
    double lastTrainingTime;
    double avgFitnessWithInEpochs; //This one is a ratio to check how quickly it is learning
    int epochs;
    int totalTrainingTime;

    List<Double> mseErrors;

    //Shape
    List<List<Integer>> neuralShape;
    List<Integer> trainingEpochs;
    List<Integer> trainingTimes;

    List<List<Double>> input;
    List<List<Double>> predicted;
    List<List<Double>> expected;

    //Special case for 3D representation figures!!
}
