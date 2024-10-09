package dev.misei.einfachstonks.neuralservice.domain.algorithm;

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
        Double weightInitialiser(int inputs, int outputs) {
            double stdDev = Math.sqrt(2.0 / (inputs + outputs));
            return Algorithm.RANDOM.nextGaussian() * stdDev;
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
        Double weightInitialiser(int inputs, int outputs) {
            double stdDev = Math.sqrt(2.0 / inputs);
            return Algorithm.RANDOM.nextGaussian() * stdDev;
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
        Double weightInitialiser(int inputs, int outputs) {
            double stdDev = Math.sqrt(2.0 / inputs);
            return Algorithm.RANDOM.nextGaussian() * stdDev;
        }
    };

    public abstract Double activate(Double x);

    public abstract Double derivative(Double x);

    abstract Double weightInitialiser(int inputs, int outputs);
}
