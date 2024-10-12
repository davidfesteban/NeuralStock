package dev.misei.einfachml.neuralservice.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@AllArgsConstructor
@Data
public class Status {

    private final UUID networkId;
    private boolean running;
    private int accumulatedEpochs;

    private UUID trainingId;
    private int goalEpochs;
    private int currentEpochToGoal;

    void incrementAccEpoch() {
        ++accumulatedEpochs;
    }
}
