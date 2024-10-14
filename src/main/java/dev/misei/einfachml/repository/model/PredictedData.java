package dev.misei.einfachml.repository.model;

import lombok.Data;
import lombok.NonNull;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Document
@ToString
@Data
public class PredictedData implements Comparable<PredictedData> {

    @Id
    private UUID uuid = UUID.randomUUID();
    private long createdAt = Instant.now().toEpochMilli();
    private UUID networkId;

    private int epochHappened;

    //It is not the history per-se but just the multiple values of 1 single prediction. (Multiple neurons, output)
    private List<Double> predicted;

    //Copy of DataSet to reduce cross dependencies
    private List<Double> inputs;
    private List<Double> expected;

    private Double mseError;

    public PredictedData(UUID uuid, long createdAt, UUID networkId, int epochHappened, List<Double> predicted, List<Double> inputs, List<Double> expected) {
        this.uuid = uuid;
        this.createdAt = createdAt;
        this.networkId = networkId;
        this.epochHappened = epochHappened;
        this.predicted = predicted;
        this.inputs = inputs;
        this.expected = expected;
        this.mseError = calculateMseForPredictedData();
    }

    public PredictedData(UUID uuid, long createdAt, UUID networkId, int epochHappened, List<Double> predicted, List<Double> inputs, List<Double> expected, Double mseError) {
        this.uuid = uuid;
        this.createdAt = createdAt;
        this.networkId = networkId;
        this.epochHappened = epochHappened;
        this.predicted = predicted;
        this.inputs = inputs;
        this.expected = expected;
        this.mseError = mseError == null ? calculateMseForPredictedData() : mseError;
    }

    public PredictedData() {
        networkId = UUID.randomUUID();
    }

    @Override
    public int compareTo(@NonNull PredictedData o) {
        return Long.compare(createdAt, o.createdAt);
    }

    public double calculateMseForPredictedData() {
        if (predicted.size() != expected.size()) {
            throw new IllegalArgumentException("Predicted and Expected lists must have the same size.");
        }

        // Calculate the sum of squared differences
        double sumSquaredErrors = 0.0;
        for (int i = 0; i < predicted.size(); i++) {
            double error = expected.get(i) - predicted.get(i);
            sumSquaredErrors += error * error;
        }

        // Calculate the MSE (mean squared error)
        return sumSquaredErrors / predicted.size();
    }
}
