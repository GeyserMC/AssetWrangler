package org.geysermc.assetwrangler.sources;

import java.util.HashMap;
import java.util.Map;

public class AssetSources {
    private AssetSources() {
        throw new AssertionError("oh no. anyway");
    }

    private static final Map<String, AssetSource> JAVA_SOURCES = new HashMap<>();
    private static final Map<String, AssetSource> BEDROCK_SOURCES = new HashMap<>();

    public static void registerSource(AssetSource assetSource) {
        if (assetSource.getType().equals(AssetSource.Type.JAVA)) {
            JAVA_SOURCES.put(assetSource.getKey(), assetSource);
        } else {
            BEDROCK_SOURCES.put(assetSource.getKey(), assetSource);
        }
    }

    public static AssetSource getAssetSource(String key) {
        return JAVA_SOURCES.getOrDefault(key, BEDROCK_SOURCES.get(key));
    }

    public static Iterable<AssetSource> javaSources() {
        return JAVA_SOURCES.values();
    }

    public static Iterable<AssetSource> bedrockSources() {
        return BEDROCK_SOURCES.values();
    }
}
