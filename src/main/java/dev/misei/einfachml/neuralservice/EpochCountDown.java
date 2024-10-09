package dev.misei.einfachml.neuralservice;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

public class EpochCountDown extends CountDownLatch {

    private final AtomicBoolean isCanceled;

    public EpochCountDown(int count) {
        super(count);
        this.isCanceled = new AtomicBoolean(false);
    }

    public void terminate() {
        isCanceled.set(true);
        while (this.getCount() > 0) {
            this.countDown();
        }
    }
}
