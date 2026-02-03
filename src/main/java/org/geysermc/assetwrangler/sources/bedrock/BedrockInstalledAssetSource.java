package org.geysermc.assetwrangler.sources.bedrock;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.geysermc.assetwrangler.Logger;
import org.geysermc.assetwrangler.Main;
import org.geysermc.assetwrangler.sources.AssetSource;

import javax.swing.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class BedrockInstalledAssetSource implements AssetSource {
    public abstract String appId();

    public abstract String missingMessage();

    public Path getInstallLocation() {
        if (Main.IS_WINDOWS) {
            try {
                Process process = Runtime.getRuntime().exec(new String[]{"powershell", "(Get-Item (Get-AppPackage %s).InstallLocation).Target".formatted(appId())});
                process.onExit().join();
                String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
                return Path.of(output.trim());
            } catch (IOException e) {
                Logger.error("Failed to start powershell.", e);
                return null;
            }
        }

        return null;
    }

    @Override
    public void download(Path dataDirectory, JFrame parent, Runnable callback, boolean update) {
        callback.run();
    }

    @Override
    public Path[] getPaths(Path dataDirectory) {
        Path packDirectories = getInstallLocation().resolve("data/resource_packs");

        try {
            Path[] vanillaPaths = Files.list(packDirectories)
                    .filter(p -> p.getFileName().toString().startsWith("vanilla"))
                    .sorted()
                    .toArray(Path[]::new);

            Path[] experimentalPaths = Files.list(packDirectories)
                    .filter(p -> p.getFileName().toString().startsWith("experimental_y_"))
                    .sorted()
                    .toArray(Path[]::new);

            Path[] paths = new Path[vanillaPaths.length + experimentalPaths.length];
            System.arraycopy(vanillaPaths, 0, paths, 0, vanillaPaths.length);
            System.arraycopy(experimentalPaths, 0, paths, vanillaPaths.length, experimentalPaths.length);

            return paths;
        } catch (IOException e) {
            return new Path[]{};
        }
    }

    @Override
    public @Nullable String getDisabledMessage() {
        if (Main.IS_WINDOWS) {
            if (getInstallLocation() != null && Files.exists(getInstallLocation())) {
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
