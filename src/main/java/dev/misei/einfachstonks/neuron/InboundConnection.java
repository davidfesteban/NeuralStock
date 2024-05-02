package dev.misei.einfachstonks.neuron;

import lombok.With;

import java.util.UUID;

@With
public record InboundConnection(UUID originId, Double weight, Double weightDelta) {
    @Override
    public String toString() {
        return "RefNeuron{" +
                "originId=" + originId +
                ", weight=" + weight +
                '}';
    }
}
