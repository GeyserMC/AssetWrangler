package org.geysermc.assetwrangler.utils.io;

import java.io.IOException;

public interface IORunnable extends Runnable {
    void runUnchecked() throws IOException;

    @Override
    default void run() {
        try {
            runUnchecked();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
