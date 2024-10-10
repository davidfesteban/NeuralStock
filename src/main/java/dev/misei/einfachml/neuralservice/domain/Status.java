package dev.misei.einfachml.neuralservice.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Status {
    private boolean running;
    private int accumulatedEpochs;

    void incrementAccEpoch() {
        ++accumulatedEpochs;
    }
}
