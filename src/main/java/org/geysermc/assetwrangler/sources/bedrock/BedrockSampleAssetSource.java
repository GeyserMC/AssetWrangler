package org.geysermc.assetwrangler.sources.bedrock;

import org.geysermc.assetwrangler.sources.GitHubAssetSource;

import java.nio.file.Path;

public class BedrockSampleAssetSource extends GitHubAssetSource {
    @Override
    public String getKey() {
        return "bedrock_samples_repo";
    }

    @Override
    public String getName() {
        return "Bedrock Sample Assets";
    }

    @Override
    public Type getType() {
        return Type.BEDROCK;
    }

    @Override
    public String repo() {
        return "Mojang/bedrock-samples";
    }

    @Override
    public Path modifyPath(Path path) {
        return path.resolve("resource_pack");
    }
}
