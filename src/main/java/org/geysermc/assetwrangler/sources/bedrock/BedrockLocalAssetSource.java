package org.geysermc.assetwrangler.sources.bedrock;

import org.geysermc.assetwrangler.sources.LocalAssetSource;

public class BedrockLocalAssetSource extends LocalAssetSource {
    @Override
    public String getKey() {
        return "bedrock_local";
    }

    @Override
    public String getName() {
        return "Local Bedrock Assets";
    }

    @Override
    public Type getType() {
        return Type.BEDROCK;
    }
}
