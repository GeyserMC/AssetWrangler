package org.geysermc.assetwrangler.sources.java;

public class JavaSnapshotAssetSource extends JavaVanillaAssetSource {
    @Override
    public String getKey() {
        return "java_vanilla_snapshot";
    }

    @Override
    public String getName() {
        return "Snapshot Java Assets";
    }

    @Override
    public String getLatestVersionTag() {
        return versionManifest.getLatest().getSnapshot();
    }
}
