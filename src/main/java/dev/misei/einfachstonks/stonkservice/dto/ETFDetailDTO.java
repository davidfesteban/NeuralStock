package dev.misei.einfachstonks.stonkservice.dto;

import dev.misei.einfachstonks.stonkservice.model.ETFCompositeHistory;
import dev.misei.einfachstonks.stonkservice.model.ETFHistory;

public record ETFDetailDTO(ETFHistory history, ETFCompositeHistory composite) {
}
