package dev.misei.einfachml.util;

import lombok.Getter;

import java.util.concurrent.CountDownLatch;

@Getter
public class EpochCountDown {

    private final int epochs;
    private boolean isCanceled;

    public EpochCountDown(int count) {
        this.epochs = count;
        this.isCanceled = count == 0;
    }

    public void terminate() {
        isCanceled = true;
        while (this.getCount() > 0) {
            this.countDown();
        }
    }
}
