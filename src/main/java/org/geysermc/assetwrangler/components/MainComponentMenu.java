package org.geysermc.assetwrangler.components;

import org.geysermc.assetwrangler.treemodels.AssetTreeModel;
import org.geysermc.assetwrangler.utils.JsonMappingsMeta;

import javax.swing.*;
import java.util.List;
import java.util.function.Consumer;

public class MainComponentMenu extends JPopupMenu {
    public MainComponentMenu(JsonMappingsMeta.Section mappingsMeta, AssetTreeModel.Entry entry) {
        super();

        String relativePath = entry.getRelativePath();
        int dotIndex = relativePath.indexOf('.');
        if (dotIndex != -1) relativePath = relativePath.substring(0, dotIndex);

        addComponent(
                relativePath,
                mappingsMeta.getMatchingPaths(),
                mappingsMeta::matchPath,
                mappingsMeta::unmatchPath,
                "matching"
        );
        addComponent(
                relativePath,
                mappingsMeta.getIgnoredPaths(),
                mappingsMeta::ignorePath,
                mappingsMeta::unignorePath,
                "ignored"
        );
        addComponent(
                relativePath,
                mappingsMeta.getTransformedPaths(),
                mappingsMeta::transformPath,
                mappingsMeta::untransformPath,
                "transformed"
        );
    }

    public void addComponent(String relativePath, List<String> currentPaths, Consumer<String> set, Consumer<String> unset, String name) {
        boolean isIncluded = currentPaths.contains(relativePath);
        JMenuItem item = new JMenuItem(
                isIncluded ?
                        "Unmark as %s".formatted(name) :
                        "Mark as %s".formatted(name)
        );
        item.addActionListener(e -> {
            if (isIncluded) {
                unset.accept(relativePath);
            } else {
                set.accept(relativePath);
            }
        });
        this.add(item);
    }
}
