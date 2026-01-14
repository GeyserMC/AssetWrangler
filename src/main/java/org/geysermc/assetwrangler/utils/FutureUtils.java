package org.geysermc.assetwrangler.utils;

import org.geysermc.assetwrangler.utils.io.IOConsumer;
import org.geysermc.assetwrangler.utils.io.IOFunction;
import org.geysermc.assetwrangler.utils.io.IORunnable;
import org.geysermc.assetwrangler.utils.io.IOSupplier;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class FutureUtils {
    public static CompletableFuture<Void> run(IORunnable runnable, ExecutorService service) {
        return CompletableFuture.runAsync(runnable, service);
    }

    public static <T> CompletableFuture<T> supplyAsync(IOSupplier<T> supplier, ExecutorService service) {
        return CompletableFuture.supplyAsync(supplier, service);
    }

    public static <T> CompletableFuture<Void> then(CompletableFuture<T> future, IOConsumer<T> consumer) {
        return future.thenAcceptAsync(consumer);
    }

    public static <T, S> CompletableFuture<S> then(CompletableFuture<T> future, IOFunction<T, S> function) {
        return future.thenApplyAsync(function);
    }

    public static void onComplete(IORunnable runnable, List<CompletableFuture<?>> futures) {
        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).thenAccept(v -> {
            runnable.run();
        });
    }
}
