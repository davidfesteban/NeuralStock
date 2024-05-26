package dev.misei.einfachstonks.neuralservice.math;

import java.util.Random;

public enum AlgorithmType {
    SIGMOID {
        @Override
        public double activate(double x) {
            return 1.0 / (1.0 + Math.exp(-x));
        }

        @Override
        public double derivative(double sigmoid) {
            return sigmoid * (1 - sigmoid);
        }
    },
    RELU {
        @Override
        public double activate(double x) {
            return Math.max(0, x);
        }

        @Override
        public double derivative(double x) {
            return x > 0 ? 1 : 0;
        }
    },
    LEAKY_RELU {
        private final double alpha = 0.01;  // Coefficient for negative inputs

        @Override
        public double activate(double x) {
            //TODO: This converts the number so small that it is impossible for the machine to follow it.
            var result = x > 0 ? x : alpha * x;

            //if(Double.isNaN(result))  {
            //    return 0.00000000001;
            //}

            return result;
        }

        @Override
        public double derivative(double x) {
            return x > 0 ? 1 : alpha;
        }
    };

    public static final Random random = new Random();

    public abstract double activate(double x);

    public abstract double derivative(double x);
}
