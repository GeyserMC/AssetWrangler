package org.geysermc.assetwrangler.sources.java;

public class JavaReleaseAssetSource extends JavaVanillaAssetSource {
    @Override
    public String getKey() {
        return "java_vanilla_release";
    }

    @Override
    public String getName() {
        return "Release Java Assets";
    }

    @Override
    public String getLatestVersionTag() {
        return versionManifest.getLatest().getRelease();
    }
}
