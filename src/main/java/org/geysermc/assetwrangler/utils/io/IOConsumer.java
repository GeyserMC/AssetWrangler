package org.geysermc.assetwrangler.utils.io;

import java.io.IOException;
import java.util.function.Consumer;

public interface IOConsumer<T> extends Consumer<T> {
    void acceptUnchecked(T t) throws IOException;

    @Override
    default void accept(T t) {
        try {
            acceptUnchecked(t);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
