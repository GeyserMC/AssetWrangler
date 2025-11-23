package org.geysermc.assetwrangler.sources.java;

import org.geysermc.assetwrangler.sources.LocalAssetSource;

public class JavaLocalAssetSource extends LocalAssetSource {
    @Override
    public String getKey() {
        return "java_local";
    }

    @Override
    public String getName() {
        return "Local Java Assets";
    }

    @Override
    public Type getType() {
        return Type.JAVA;
    }
}
