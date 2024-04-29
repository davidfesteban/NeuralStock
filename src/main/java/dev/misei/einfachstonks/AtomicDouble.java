package dev.misei.einfachstonks;

import java.util.concurrent.atomic.AtomicLong;

public class AtomicDouble {
    private AtomicLong bits;

    public AtomicDouble(double initialValue) {
        this.bits = new AtomicLong(Double.doubleToRawLongBits(initialValue));
    }

    public double get() {
        return Double.longBitsToDouble(bits.get());
    }

    public void set(double newValue) {
        bits.set(Double.doubleToRawLongBits(newValue));
    }

    public double getAndSet(double newValue) {
        return Double.longBitsToDouble(bits.getAndSet(Double.doubleToRawLongBits(newValue)));
    }

    public boolean compareAndSet(double expect, double update) {
        return bits.compareAndSet(
                Double.doubleToRawLongBits(expect),
                Double.doubleToRawLongBits(update)
        );
    }

    public void increment(double value) {
        this.set(this.get() + value);
    }
}

