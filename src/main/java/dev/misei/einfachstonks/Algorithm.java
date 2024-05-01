package dev.misei.einfachstonks;

import java.util.Random;

public class Algorithm {
    public static double sigmoid(double in){
        return 1 / (1 + Math.exp(-in));
    }

    public static double sigmoidDerivative(double x) {
        //x already a value from sigmoid
        return x * (1 - x);
    }

    public static final Random random = new Random();
}
