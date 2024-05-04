package dev.misei.einfachstonks.stonkservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.lang.NonNull;

import java.time.LocalDate;
import java.util.UUID;

@Document
@Getter
@AllArgsConstructor
public class ETFHistory implements Comparable<ETFHistory> {
    UUID internalNameId;
    UUID historyId;
    UUID refEtfComposite;
    LocalDate dayPrecision;
    Double priceOpen;
    Double priceClose;
    Long volume;
    Double priceHigh;
    Double priceLow;

    @Override
    public int compareTo(@NonNull ETFHistory o) {
        return this.dayPrecision.compareTo(o.dayPrecision);
    }
}
