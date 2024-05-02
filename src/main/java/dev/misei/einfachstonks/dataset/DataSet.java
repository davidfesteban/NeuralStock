package dev.misei.einfachstonks.dataset;

import java.util.List;
import java.util.stream.Collectors;

public record DataSet(List<Double> inputs, List<Double> outputs) {
    @Override
    public String toString() {
        String result = "====== Inputs: " + inputs.stream().map(Object::toString).collect(Collectors.joining(", "));
        result += "\n====== Outputs: " + outputs.stream().map(Object::toString).collect(Collectors.joining(", "));
        return result;
    }
}
