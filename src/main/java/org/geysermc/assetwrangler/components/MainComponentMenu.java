package org.geysermc.assetwrangler.components;

import org.geysermc.assetwrangler.panels.AssetPanel;
import org.geysermc.assetwrangler.treemodels.AssetTreeModel;
import org.geysermc.assetwrangler.utils.JsonMappingsMeta;
import org.geysermc.assetwrangler.windows.MappingsWindow;

import javax.swing.*;
import java.util.List;
import java.util.function.Consumer;

public class MainComponentMenu extends JPopupMenu {
    private final AssetPanel panel;

    public MainComponentMenu(AssetPanel panel, AssetTreeModel.Entry entry) {
        super();

        this.panel = panel;

        JsonMappingsMeta.Section mappingsMeta = panel.getMetaSection();

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

        Consumer<String> doAction = isIncluded ? unset : set;
        Consumer<String> undoAction = isIncluded ? set : unset;
        item.addActionListener(e -> {
            panel.getMain().getActionManager().doAction(
                    () -> {
                        doAction.accept(relativePath);
                        if (panel.getMain() instanceof MappingsWindow window) window.refreshView();
                    },
                    () -> {
                        undoAction.accept(relativePath);
                        if (panel.getMain() instanceof MappingsWindow window) window.refreshView();
                    },
                    true
            );
        });
        this.add(item);
    }
}
