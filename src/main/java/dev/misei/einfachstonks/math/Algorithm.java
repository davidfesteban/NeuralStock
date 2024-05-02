package dev.misei.einfachstonks.math;

import java.util.Random;

public enum Algorithm {
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
            return x > 0 ? x : alpha * x;
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
