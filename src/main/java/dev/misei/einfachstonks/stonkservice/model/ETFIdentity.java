package dev.misei.einfachstonks.stonkservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.UUID;

@Document
@AllArgsConstructor
@Getter
@Setter
public class ETFIdentity {
    UUID internalNameId;
    String etfName;
    String isinJustEtf;
    String wknNameJustEtf;
    String ticketYahoo;
    String etfType;
    LocalDate lastUpdate;
}
