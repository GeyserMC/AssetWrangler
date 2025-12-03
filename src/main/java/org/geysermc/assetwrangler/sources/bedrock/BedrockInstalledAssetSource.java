package org.geysermc.assetwrangler.sources.bedrock;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.geysermc.assetwrangler.Main;
import org.geysermc.assetwrangler.sources.AssetSource;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class BedrockInstalledAssetSource implements AssetSource {
    public abstract Path getInstallLocation();

    public abstract String missingMessage();

    @Override
    public boolean downloadRequired(Path dataDirectory) {
        return false;
    }

    @Override
    public boolean download(Path dataDirectory, JFrame parent, Runnable callback, boolean update) {
        return false;
    }

    @Override
    public Path[] getPaths(Path dataDirectory) {
        Path packDirectories = getInstallLocation().resolve("Content/data/resource_packs");

        try {
            return Files.list(packDirectories)
                    .filter(p -> p.getFileName().toString().startsWith("vanilla"))
                    .sorted()
                    .toArray(Path[]::new);
        } catch (IOException e) {
            return new Path[]{};
        }
    }

    @Override
    public @Nullable String getDisabledMessage() {
        if (Main.IS_WINDOWS) {
            // TODO Better check for game, specifically checking for assets
            if (Files.exists(getInstallLocation())) {
                return null;
            } else {
                return missingMessage();
            }
        } else {
            return "This option only works on Windows.";
        }
    }

    @Override
    public Type getType() {
        return Type.BEDROCK;
    }
}
