package dev.misei.einfachstonks;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.With;

import java.util.UUID;

@Data
@AllArgsConstructor
@With
public class RefNeuron {
    UUID ref;
    Double weight;
    Double weightDelta;

    @Override
    public String toString() {
        return "RefNeuron{" +
                "ref=" + ref +
                ", weight=" + weight +
                '}';
    }
}
