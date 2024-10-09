package dev.misei.einfachstonks.neuralservice.model;

import dev.misei.einfachstonks.neuralservice.domain.data.Datapair;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class PredictedPoint {
    Datapair datapair;
    List<Double> predicted;
    int epochHappened;
}
