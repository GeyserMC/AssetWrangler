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

    /**
     * Downloads the source
     * @param dataDirectory The application data directory
     * @param parent the frame, downloads may want to add confirmation
     */
    void download(Path dataDirectory, JFrame parent, Runnable callback, boolean update) throws IOException;

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
