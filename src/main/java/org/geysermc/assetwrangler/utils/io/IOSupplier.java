package org.geysermc.assetwrangler.utils.io;

import java.io.IOException;
import java.util.function.Supplier;

public interface IOSupplier<T> extends Supplier<T> {
    T getUnchecked() throws IOException;

    @Override
    default T get() {
        try {
            return getUnchecked();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
