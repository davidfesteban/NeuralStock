package dev.misei.einfachstonks.neuralservice.dataenum;

import java.util.Random;

public enum AlgorithmType {
    SIGMOID {
        @Override
        public Double activate(Double x) {
            return 1d / (1d + Math.exp(-x));
        }

        @Override
        public Double derivative(Double x) {
            return x * (1d - x);
        }

        @Override
        public Double weightInitialiser() {
            double stdDev = Math.sqrt(2.0 / (inputs + outputs));
            return random.nextGaussian() * stdDev;
        }
    },
    RELU {
        @Override
        public Double activate(Double x) {
            return Math.max(0d, x);
        }

        @Override
        public Double derivative(Double x) {
            return x > 0 ? 1d : 0d;
        }

        @Override
        public Double weightInitialiser() {
            double stdDev = Math.sqrt(2.0 / inputs);
            return random.nextGaussian() * stdDev;
        }
    },
    LEAKY_RELU {
        private final Double alpha = 0.01d;

        @Override
        public Double activate(Double x) {

            return x > 0 ? x : alpha * x;
        }

        @Override
        public Double derivative(Double x) {
            return x > 0 ? 1 : alpha;
        }

        @Override
        public Double weightInitialiser() {
            double stdDev = Math.sqrt(2.0 / inputs);
            return random.nextGaussian() * stdDev;
        }
    };

    public static final Random random = new Random();
    protected double inputs;
    protected double outputs;

    public abstract Double activate(Double x);

    public abstract Double derivative(Double x);

    public void setInputsAndOutputs(double inputs, double outputs) {
        this.inputs = inputs;
        this.outputs = outputs;
    }

    public abstract Double weightInitialiser();
}
