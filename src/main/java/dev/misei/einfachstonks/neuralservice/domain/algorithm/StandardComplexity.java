package dev.misei.einfachstonks.neuralservice.domain.algorithm;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StandardComplexity {
    SOFT(0.5),
    NORMAL(1),
    ADVANCED(1.5),
    HARD(2),
    OMG(3);

    private final double complexityValue;
}
