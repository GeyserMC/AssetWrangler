package org.geysermc.assetwrangler.sources.bedrock;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.geysermc.assetwrangler.Main;
import org.geysermc.assetwrangler.sources.AssetSource;

import javax.swing.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class BedrockInstalledAssetSource implements AssetSource {
    @Override
    public String getKey() {
        return "bedrock_installed";
    }

    @Override
    public String getName() {
        return "Installed Bedrock Assets";
    }

    @Override
    public boolean downloadRequired(Path dataDirectory) {
        return false;
    }

    @Override
    public boolean download(Path dataDirectory, JFrame parent, boolean update) {
        return false;
    }

    @Override
    public Path[] getPaths(Path dataDirectory) {
        return new Path[0];
    }

    @Override
    public @Nullable String getDisabledMessage() {
        if (Main.IS_WINDOWS) {
            // TODO Better check for game, specifically checking for assets
            if (Files.exists(Path.of("C:\\XboxGames\\Minecraft for Windows\\"))) {
                return null;
            } else {
                return "You must have Minecraft: Bedrock Edition version 1.21.120+ installed to use this option.";
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
