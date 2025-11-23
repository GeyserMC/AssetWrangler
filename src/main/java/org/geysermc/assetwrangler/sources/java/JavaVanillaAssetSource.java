package org.geysermc.assetwrangler.sources.java;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.geysermc.assetwrangler.sources.AssetSource;

import javax.swing.*;
import java.nio.file.Path;

public class JavaVanillaAssetSource implements AssetSource {
    @Override
    public String getKey() {
        return "java_vanilla";
    }

    @Override
    public String getName() {
        return "Vanilla Java Assets";
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
        return "WIP";
    }

    @Override
    public Type getType() {
        return Type.JAVA;
    }
}
