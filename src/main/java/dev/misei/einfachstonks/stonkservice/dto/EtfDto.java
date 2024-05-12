package dev.misei.einfachstonks.stonkservice.dto;

import dev.misei.einfachstonks.stonkservice.model.ETFCompositeHistory;
import dev.misei.einfachstonks.stonkservice.model.ETFHistory;

public record EtfDto (ETFHistory history, ETFCompositeHistory composite) {
}
