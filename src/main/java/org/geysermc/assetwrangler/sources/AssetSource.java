package org.geysermc.assetwrangler.sources;

import lombok.Getter;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Path;

public interface AssetSource {
    /**
     * Get the key of this source, used for saving
     * @return The key of this source
     */
    String getKey();

    /**
     * Get the user-friendly name of this source
     * @return The user-friendly name
     */
    String getName();

    default boolean setup(Path dataDirectory, JFrame parent) throws IOException {
        return download(dataDirectory, parent, false);
    }

    /**
     * Returns if a download is required
     * @param dataDirectory The application data directory
     * @return if a download is required
     */
    boolean downloadRequired(Path dataDirectory);

    /**
     * Downloads the source, no checks should be done if the source is downloaded here, {@link #downloadRequired(Path)} checks that
     * @param dataDirectory The application data directory
     * @param parent the frame, downloads may want to add confirmation
     */
    boolean download(Path dataDirectory, JFrame parent, boolean update) throws IOException;

    /**
     * Gets the paths providing this source, if the array is null, assume the user cancelled the operation.
     * @param dataDirectory The application data directory
     * @return Paths to the source
     */
    Path[] getPaths(Path dataDirectory);

    /**
     * If null, assume its not disabled
     * @return Disabled text
     */
    @Nullable String getDisabledMessage();

    Type getType();

    enum Type {
        JAVA("Java"),
        BEDROCK("Bedrock");

        @Getter
        private final String name;

        Type(String name) {
            this.name = name;
        }
    }
}
