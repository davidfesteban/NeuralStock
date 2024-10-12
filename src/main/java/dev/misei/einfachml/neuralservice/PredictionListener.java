package dev.misei.einfachml.neuralservice;

import dev.misei.einfachml.repository.model.PredictedData;

import java.util.List;

public interface PredictionListener {

    void onPrediction(PredictedData predictedData);
}
