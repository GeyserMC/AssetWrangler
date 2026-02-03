package org.geysermc.assetwrangler.sources.bedrock;

public class BedrockPreviewAssetSource extends BedrockInstalledAssetSource {
    @Override
    public String appId() {
        return "Microsoft.MinecraftWindowsBeta";
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
