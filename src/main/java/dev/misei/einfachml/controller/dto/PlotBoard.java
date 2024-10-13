package dev.misei.einfachml.controller.dto;

import dev.misei.einfachml.repository.model.PredictedData;
import lombok.Data;

import java.util.List;

@Data
public class PlotBoard {

    List<PredictedData> lastEpochPredicted;
    List<Double> mseErrors;
}
