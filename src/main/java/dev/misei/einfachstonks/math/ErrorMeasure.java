package dev.misei.einfachstonks.math;

public enum ErrorMeasure {
    LINEAR {
        @Override
        public double calculate(double expected, double actual) {
            return expected - actual;
        }
    },
    MSE {
        @Override
        public double calculate(double expected, double actual) {
            return Math.pow(expected - actual, 2) / 2;
        }
    };

    public abstract double calculate(double expected, double actual);
}
