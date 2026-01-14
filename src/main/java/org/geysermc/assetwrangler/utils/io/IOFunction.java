package org.geysermc.assetwrangler.utils.io;

import java.io.IOException;
import java.util.function.Function;

public interface IOFunction<T, R> extends Function<T, R> {
    R applyUnchecked(T t) throws IOException;

    @Override
    default R apply(T t) {
        try {
            return applyUnchecked(t);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
