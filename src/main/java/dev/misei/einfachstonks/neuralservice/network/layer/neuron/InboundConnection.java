package dev.misei.einfachstonks.neuralservice.network.layer.neuron;

import lombok.With;

import java.util.UUID;

@With
public record InboundConnection(UUID originId, Double weight, Double weightDelta) {
}
