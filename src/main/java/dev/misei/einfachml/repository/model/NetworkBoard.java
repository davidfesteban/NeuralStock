package dev.misei.einfachml.repository.model;

import dev.misei.einfachml.neuralservice.domain.algorithm.Algorithm;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.UUID;

@Document
@AllArgsConstructor
@Data
public class NetworkBoard {

    @Id
    UUID networkId;

    //Total Training
    Integer totalEpochs;
    Long totalTrainingTime;

    AlgorithmBoard algorithmBoard;

    //Current Status
    String status;
    Integer epochGoal;
    int currentEpoch;

    //Network Metrics
    double avgFitnessError;
    double lastFitnessError;
    double lastTrainingTime;
    double avgFitnessWithInEpochs; //This one is a ratio to check how quickly it is learning

    List<Double> mseErrors;

}
