package dev.misei.einfachstonks.stonkservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Document
@AllArgsConstructor
@Getter
@Setter
public class ETFIdentity {
    UUID internalNameId;
    String etfName;
    ETFBridgeType etfBridgeType;
    String ticker;
    ETFType etfType;
    LocalDate lastUpdate;

    List<Double> asList() {
        return List.of((double) etfType.ordinal());
    }
}
