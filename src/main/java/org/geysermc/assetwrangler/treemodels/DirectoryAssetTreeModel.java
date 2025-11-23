package org.geysermc.assetwrangler.treemodels;

import org.geysermc.assetwrangler.panels.AssetPanel;
import org.geysermc.assetwrangler.utils.Asset;

import java.util.List;
import java.util.Objects;

import static org.geysermc.assetwrangler.utils.Asset.getMappedLocation;

public class DirectoryAssetTreeModel extends AssetTreeModel {
    private final AssetPanel panel;

    public DirectoryAssetTreeModel(AssetPanel panel, Asset rootAsset, String rootName) {
        super(panel, rootAsset, rootName);
        this.panel = panel;
    }

    @Override
    public Entry getEntry(Asset asset) {
        List<Asset> visibleChildren = asset.getChildren().stream()
                .filter(Asset::isDirectory)
                .toList();

        return new Entry(
                this,
                visibleChildren.stream()
                        .map(this::getEntry)
                        .toList(),
                asset.getName(),
                asset.getPath().toString(),
                getMappedLocation(panel, asset.getRelativePath(), false),
                asset.getRelativePath(),
                asset.isDirectory()
        );
    }
}
