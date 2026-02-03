package org.geysermc.assetwrangler.sources.bedrock;

public class BedrockReleaseAssetSource extends BedrockInstalledAssetSource {
    @Override
    public String appId() {
        return "Microsoft.MinecraftWindows";
    }

    @Override
    public String missingMessage() {
        return "You must have Minecraft: Bedrock Edition version 1.21.120+ installed to use this option.";
    }

    @Override
    public String getKey() {
        return "bedrock_installed_release";
    }

    @Override
    public String getName() {
        return "Bedrock Release Assets";
    }
}
