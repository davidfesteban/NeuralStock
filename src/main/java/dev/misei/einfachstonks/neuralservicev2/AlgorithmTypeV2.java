package dev.misei.einfachstonks.neuralservicev2;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Random;

public enum AlgorithmTypeV2 {
    SIGMOID {
        @Override
        public BigDecimal activate(BigDecimal x) {
            return BigDecimal.ONE.divide(BigDecimal.ONE.add(exp(x.negate())));
        }

        @Override
        public BigDecimal derivative(BigDecimal x) {
            return x.multiply(BigDecimal.ONE.subtract(x));
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

    public abstract BigDecimal activate(BigDecimal x);

    public abstract BigDecimal derivative(BigDecimal x);

    // Set precision for the calculation
    private static final MathContext mc = new MathContext(50); // 50 digits of precision

    public static BigDecimal exp(BigDecimal x) {
        BigDecimal result = BigDecimal.ONE;
        BigDecimal term = BigDecimal.ONE;
        BigDecimal n = BigDecimal.ONE;

        // Add more terms to the series until the term is small enough
        for (int i = 1; i < 1000; i++) {

            term = term.multiply(x.negate()).divide(n, mc);
            result = result.add(term, mc);

            n = n.add(BigDecimal.ONE);

            // Break if the term is very small and doesn't affect the result anymore
            if (term.abs().compareTo(BigDecimal.ZERO) == 0) {
                break;
            }
        }
        return result;
    }
}
