package dev.misei.einfachstonks.stonkservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.UUID;

@Document
@Getter
@AllArgsConstructor
public class ETFCompositeHistory {
    UUID internalNameId;
    UUID etfCompositeId;
    UUID refEtfHistory;
    Double week52Low;
    Double week52High;
    Double averageVolume;
    Double etfVolatility;
    Double future1DayClosePrice;
    Double future1WeekClosePrice;
    Double future1MonthClosePrice;

    public List<Double> asList() {
        return List.of(week52Low, week52High, averageVolume, etfVolatility);
    }

    public boolean canBeUsed() {
        return future1DayClosePrice != null;
    }

    public List<Double> resultsAsList() {
        return List.of(future1DayClosePrice);
    }
}
