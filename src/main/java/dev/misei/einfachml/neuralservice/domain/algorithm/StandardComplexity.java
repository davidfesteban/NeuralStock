package dev.misei.einfachml.neuralservice.domain.algorithm;

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

    public static StandardComplexity fromValue(double value) {
        for (StandardComplexity complexity : StandardComplexity.values()) {
            if (complexity.getComplexityValue() == value) {
                return complexity;
            }
        }
        throw new IllegalArgumentException("No StandardComplexity found for value: " + value);
    }
}
