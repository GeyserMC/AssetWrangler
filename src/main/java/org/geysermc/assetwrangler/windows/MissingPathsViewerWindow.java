package org.geysermc.assetwrangler.windows;

import org.geysermc.assetwrangler.Logger;
import org.geysermc.assetwrangler.Main;
import org.geysermc.assetwrangler.utils.Asset;
import org.geysermc.assetwrangler.utils.JsonMappingsMeta;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MissingPathsViewerWindow extends BaseWindow {
    public MissingPathsViewerWindow(MappingsWindow main) {
        super();
        this.setSize(500, 500);
        this.setResizable(false);
        this.setIconImage(Main.ICON_IMAGE);
        this.setTitle("Missing assets");

        this.setLayout(new LayoutManager() {
            public void addLayoutComponent(String name, Component comp) {}
            public void removeLayoutComponent(Component comp) {}
            public Dimension preferredLayoutSize(Container parent) {return null;}
            public Dimension minimumLayoutSize(Container parent) {return null;}

            @Override
            public void layoutContainer(Container parent) {
                synchronized (parent.getTreeLock()) {
                    for (Component component : parent.getComponents()) {
                        if (component instanceof JScrollPane scrollPane) {
                            scrollPane.setBounds(0, 0, 484, 435);
                        } else if (component instanceof JButton button) {
                            button.setBounds(0, 440, 484, 20);
                        }
                    }
                }
            }
        });

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new GridLayout(0, 1));

        addItems(
                "Mapped Java Path", listPanel, main.getJsonMappings().presentJavaPaths(),
                main.getJavaAssetPanel().getRootAsset(), main.getJavaMeta(),
                main.getJavaAssetPanel()::unmap, false
        );
        addItems(
                "Matched Java Path", listPanel, main.getJavaMeta().getMatchingPaths(),
                main.getJavaAssetPanel().getRootAsset(), main.getJavaMeta(),
                main.getJavaMeta()::unmatchPath, false
        );
        addItems(
                "Ignored Java Path", listPanel, main.getJavaMeta().getIgnoredPaths(),
                main.getJavaAssetPanel().getRootAsset(), main.getJavaMeta(),
                main.getJavaMeta()::unignorePath, false
        );
        addItems(
                "Transformed Java Path", listPanel, main.getJavaMeta().getTransformedPaths(),
                main.getJavaAssetPanel().getRootAsset(), main.getJavaMeta(),
                main.getJavaMeta()::untransformPath, false
        );

        addItems(
                "Mapped Bedrock Path", listPanel, main.getJsonMappings().presentBedrockPaths(),
                main.getBedrockAssetPanel().getRootAsset(), main.getBedrockMeta(),
                main.getJavaAssetPanel()::unmap, true
        );
        addItems(
                "Matched Bedrock Path", listPanel, main.getBedrockMeta().getMatchingPaths(),
                main.getBedrockAssetPanel().getRootAsset(), main.getBedrockMeta(),
                main.getBedrockMeta()::unmatchPath, false
        );
        addItems(
                "Ignored Bedrock Path", listPanel, main.getBedrockMeta().getIgnoredPaths(),
                main.getBedrockAssetPanel().getRootAsset(), main.getBedrockMeta(),
                main.getBedrockMeta()::unignorePath, false
        );
        addItems(
                "Transformed Bedrock Path", listPanel, main.getBedrockMeta().getTransformedPaths(),
                main.getBedrockAssetPanel().getRootAsset(), main.getBedrockMeta(),
                main.getBedrockMeta()::untransformPath, false
        );

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        this.add(scrollPane);

        JButton done = new JButton("Done");
        done.addActionListener(e -> {
            MissingPathsViewerWindow.this.close();
        });
        this.add(done);

        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    @Override
    public void close() {
        this.dispose();
    }

    public void addItems(
            String name, JPanel listPanel, List<String> paths, Asset rootAsset,
            JsonMappingsMeta.Section section, Consumer<String> action, boolean handleRedirects
    ) {
        for (String path : paths) {
            String prefix = section.getRelativePath();
            if (!prefix.isEmpty()) prefix = prefix + "/";
            String absolutePath = prefix + path;

            Asset asset = rootAsset.resolve(absolutePath, handleRedirects);
            boolean addMissing = false;
            if (asset == null) addMissing = true; // It's not present, handle it
            else if (asset.isDirectory()) { // There might be file with the same name (excluding file extensions)
                // Should only happen with these 3
                Asset pngAsset = rootAsset.resolve(absolutePath + ".png", handleRedirects);
                Asset tgaAsset = rootAsset.resolve(absolutePath + ".tga", handleRedirects);
                Asset jsonAsset = rootAsset.resolve(absolutePath + ".json", handleRedirects);
                if (pngAsset == null && tgaAsset == null && jsonAsset == null) addMissing = true;
            }

            if (addMissing) {
                JButton button = new JButton("%s: %s".formatted(name, path));

                JPopupMenu menu = new JPopupMenu();

                JMenuItem unmap = new JMenuItem("Remove");
                unmap.addActionListener(e -> {
                    action.accept(path);
                    listPanel.remove(button);
                    listPanel.updateUI();
                    listPanel.repaint();
                });
                menu.add(unmap);

                button.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseReleased(MouseEvent e) {
                        menu.show(e.getComponent(), e.getX(), e.getY());
                    }
                });

                listPanel.add(button);
            }
        }
    }
}
