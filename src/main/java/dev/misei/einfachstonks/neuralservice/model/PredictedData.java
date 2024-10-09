package dev.misei.einfachstonks.neuralservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.UUID;

@Document
@Data
@AllArgsConstructor
public class PredictedData {
    @Id
    UUID uuid;

    UUID networkUUID;
    List<PredictedPoint> predictedPointByEpoch;

    int epoch;
}
