package dev.misei.einfachstonks.stonkservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Document;

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
}
