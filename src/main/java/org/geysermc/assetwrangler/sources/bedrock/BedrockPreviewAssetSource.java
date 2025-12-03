package org.geysermc.assetwrangler.sources.bedrock;

import java.nio.file.Path;

public class BedrockPreviewAssetSource extends BedrockInstalledAssetSource {
    @Override
    public Path getInstallLocation() {
        return Path.of("C:\\XboxGames\\Minecraft Preview for Windows\\");
    }

    @Override
    public String missingMessage() {
        return "You must have Minecraft: Bedrock Edition Preview version 1.21.120+ installed to use this option.";
    }

    @Override
    public String getKey() {
        return "bedrock_installed_preview";
    }

    @Override
    public String getName() {
        return "Bedrock Preview Assets";
    }
}
