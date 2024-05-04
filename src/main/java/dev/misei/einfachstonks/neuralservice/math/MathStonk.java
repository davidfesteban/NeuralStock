package dev.misei.einfachstonks.neuralservice.math;

import java.util.Arrays;
import java.util.stream.IntStream;

public class MathStonk {

    public static int factorial(int number) {
        return IntStream.rangeClosed(1, number)
                .reduce(1, (x, y) -> x * y);
    }

    public static int mean(int... number) {
        return Arrays.stream(number).sum()/number.length;
    }
}
