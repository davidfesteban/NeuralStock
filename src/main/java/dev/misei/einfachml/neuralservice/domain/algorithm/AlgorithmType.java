package dev.misei.einfachml.neuralservice.domain.algorithm;

public enum AlgorithmType {
    SIGMOID_BOUNDED {
        private final Double threshold = 100.0;  // Example threshold

        @Override
        public Double activate(Double x) {
            // Standard Sigmoid scaled by the threshold
            return threshold * (1d / (1d + Math.exp(-x)));
        }

        // Derivative of the modified sigmoid
        @Override
        public Double derivative(Double x) {
            // The derivative of the scaled sigmoid
            Double sigmoid = activate(x) / threshold;  // Reverse the scaling to get the original sigmoid value
            return threshold * sigmoid * (1d - sigmoid);  // Derivative of the sigmoid with scaling
        }

        @Override
        Double weightInitialiser(int inputs, int outputs) {
            double stdDev = Math.sqrt(1.0 / (inputs + outputs));
            return Algorithm.RANDOM.nextGaussian() * stdDev;
        }
    },
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
    SIN {
        @Override
        public Double activate(Double x) {
            return Math.sin(x);  // Activation function (sin)
        }

        @Override
        public Double derivative(Double x) {
            return Math.cos(x);  // Derivative of sin(x)
        }

        @Override
        Double weightInitialiser(int inputs, int outputs) {
            double variance = 1.0 / (inputs + outputs);  // Xavier initialization formula
            double stdDev = Math.sqrt(variance);
            return Algorithm.RANDOM.nextGaussian() * stdDev;
        }
    },
    BOUNDED_LEAKY_RELU {
        @Override
        public Double activate(Double x) {
            double alpha = 0.01;
            double threshold = 100.0;

            // Softplus transition instead of abrupt ReLU
            if (x > threshold) {
                return Math.log1p(x - threshold) + threshold;
            } else if (x > 0) {
                return Math.log1p(Math.exp(x)) - Math.log1p(Math.exp(0));  // Softplus for positive x
            } else {
                return alpha * x;  // Leaky ReLU for negative x
            }
        }

        @Override
        public Double derivative(Double x) {
            double alpha = 0.01;
            double threshold = 100.0;

            if (x > threshold) {
                // Derivative for the log1p region (x > threshold)
                return 1 / (x - threshold + 1);
            } else if (x > 0) {
                // Derivative of softplus function for 0 < x <= threshold
                return Math.exp(x) / (1 + Math.exp(x));
            } else {
                // Derivative for Leaky ReLU region (x <= 0)
                return alpha;
            }
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
