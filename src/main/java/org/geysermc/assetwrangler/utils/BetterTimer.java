package org.geysermc.assetwrangler.utils;

import javax.swing.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class BetterTimer {
    private final Thread timer;

    private final AtomicBoolean paused = new AtomicBoolean(false);

    public BetterTimer(Runnable onTick) {
        timer = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    return;
                }

                if (!paused.get()) SwingUtilities.invokeLater(onTick);
            }
        });

        timer.start();
    }

    public void setPaused(boolean paused) {
        this.paused.set(paused);
    }

    public boolean isPaused() {
        return this.paused.get();
    }

    public void close() {
        timer.interrupt();
    }
}
