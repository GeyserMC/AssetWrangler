package org.geysermc.assetwrangler.utils;

import lombok.Getter;
import lombok.ToString;
import org.geysermc.assetwrangler.Main;
import org.geysermc.assetwrangler.panels.AssetPanel;

import java.io.File;
import java.nio.file.Path;
import java.util.*;

@ToString
@Getter
public class Asset {
    private static final Map<String, String> REDIRECTS = Map.of(
            "textures/blocks", "textures/block",
            "textures/items", "textures/item",
            "textures/ui", "textures/gui"
    );

    private final String relativePath;
    private final Path path;
    private final boolean directory;
    private final String name;
    private List<Asset> children;

    public Asset(Path root, Path path, String name, boolean directory) {
        this.relativePath = root.relativize(path).toString().replace(File.separatorChar, '/');
        this.path = path;
        this.name = name;
        this.directory = directory;
        this.children = directory ? Arrays.stream(path.toFile().listFiles())
                .filter(f ->
                        !f.getName().equals("_list.json") && // TODO remove when I add proper java client asset downloading
                                !f.getName().equals("_all.json") &&
                                !f.getName().equals("desktop.ini") &&
                                !f.getName().equals("__brarchive")
                )
                .map(f -> new Asset(root, f.toPath(), f.getName(), f.isDirectory()))
                .toList() : List.of();
    }

    public Asset resolve(String path) {
        if (path.isBlank()) return this;
        Asset currentAsset = this;
        List<String> paths = Arrays.stream(path.split("/")).toList();
        for (String childPath : paths) {
            currentAsset = currentAsset.getChild(childPath);
            if (currentAsset == null) return null;
        }
        return currentAsset;
    }

    public Asset getChild(String path) {
        List<Asset> assets = children.stream().filter(a -> a.name.equals(path)).toList();
        if (assets.isEmpty()) return null;
        return assets.getFirst();
    }

    public void combine(Asset asset) {
        Map<String, Asset> assetMap = new HashMap<>();

        for (Asset child : this.children) {
            assetMap.put(child.name, child);
        }

        for (Asset child : asset.children) {
            if (assetMap.containsKey(child.name)) {
                Asset a = assetMap.get(child.name);
                a.combine(child);
                assetMap.put(child.name, a);
            }
            else assetMap.put(child.name, child);
        }

        this.children = assetMap.values().stream().toList();
    }

    public boolean viewable(AssetPanel panel) {
        if (!panel.isForMapping()) return true;

        if (!Main.CONFIG.showBedrockMetadataFiles()) {
            if (
                    name.endsWith("_mers.tga") ||
                            name.endsWith("_mers.png") ||
                            name.endsWith(".texture_set.json")
            ) return false;
        }

        if (!Main.CONFIG.showJavaMetadataFiles()) {
            if (name.endsWith(".mcmeta")) return false;
        }

        String relativePath = this.relativePath;
        int dotIndex = relativePath.indexOf('.');
        if (dotIndex != -1) relativePath = relativePath.substring(0, dotIndex);

        if (!panel.getMetaSection().getRelativePath().isBlank() &&
                !panel.getMetaSection().getRelativePath().equals(this.getRelativePath())) {
            relativePath = relativePath.substring(
                    panel.getMetaSection().getRelativePath().length() + 1
            );
        }

        if (panel.getMetaSection().getMatchingPaths().contains(relativePath)) {
            if (!Main.CONFIG.showMatchingEntries()) return false;
        }

        if (panel.getMetaSection().getIgnoredPaths().contains(relativePath)) {
            if (!Main.CONFIG.showIgnoredEntries()) return false;
        }

        if (panel.getMetaSection().getTransformedPaths().contains(relativePath)) {
            if (!Main.CONFIG.showTransformedEntries()) return false;
        }

        if (panel.isMapped(getMappedLocation(panel, relativePath, true))) {
            if (!Main.CONFIG.showMappedEntries()) return false;
        }

        return true;
    }

    public static String getMappedLocation(AssetPanel panel, String relativePath, boolean noFileExtension) {
        String location = relativePath;
        for (Map.Entry<String, String> entry : REDIRECTS.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            if (panel != null && !panel.getMetaSection().getRelativePath().isBlank()) {
                if (!panel.getMetaSection().getRelativePath().equals(key)) {
                    key = key.substring(
                            panel.getMetaSection().getRelativePath().length() + 1
                    );
                }
                if (!panel.getMetaSection().getRelativePath().equals(value)) {
                    value = value.substring(
                            panel.getMetaSection().getRelativePath().length() + 1
                    );
                }
            }

            if (location.startsWith(key)) location = location.replaceFirst(key, value);
        }
        if (noFileExtension) {
            int dotIndex = location.indexOf('.');
            if (dotIndex != -1) location = location.substring(0, dotIndex);
        }

        return location;
    }
}
