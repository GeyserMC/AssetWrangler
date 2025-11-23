package org.geysermc.assetwrangler.panels;

import org.geysermc.assetwrangler.Main;
import org.geysermc.assetwrangler.windows.AssetDirectorySelectorWindow;
import org.geysermc.assetwrangler.windows.MappingsWindow;
import org.geysermc.assetwrangler.config.Config;
import org.geysermc.assetwrangler.windows.InfoWindow;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class AssetMapperToolBar extends JMenuBar {
    public AssetMapperToolBar(MappingsWindow main) {
        Config config = Main.CONFIG;

        createPopupMenu(
                "File",
                TooltipItem.of("Create", (item) -> {
                    main.offerSaveIfRequired(b -> {
                        if (b != null) {
                            FileDialog chooser = new FileDialog(main);
                            chooser.setTitle("Create Mapping");
                            chooser.setMultipleMode(false);
                            chooser.setMode(FileDialog.SAVE);
                            chooser.setVisible(true);

                            if (chooser.getFile() == null) return;

                            Main.mappingFile = Path.of(chooser.getFile());

                            try {
                                Files.writeString(Main.mappingFile, "{}");
                            } catch (IOException ex) {
                                ex.printStackTrace();
                                JOptionPane.showMessageDialog(main, "Unable to create mapping file!", "Error! Error!", JOptionPane.ERROR_MESSAGE);
                            }

                            main.setVisible(false);
                            main.invalidate();

                            new MappingsWindow();
                        }
                    });
                }),
                TooltipItem.of("Open", (item) -> {
                    main.offerSaveIfRequired(b -> {
                        if (b != null) {
                            FileDialog chooser = new FileDialog(main);
                            chooser.setTitle("Load Mapping");
                            chooser.setMultipleMode(false);
                            chooser.setMode(FileDialog.LOAD);
                            chooser.setVisible(true);

                            if (chooser.getFile() == null) return;

                            Main.mappingFile = Path.of(chooser.getDirectory(), chooser.getFile());

                            main.setVisible(false);
                            main.invalidate();

                            new MappingsWindow();
                        }
                    });
                }),
                TooltipItem.of("Save", (item) -> main.save()),
                TooltipItem.of("Save As", (item) -> main.saveAs())
        );
        createPopupMenu(
                "View",
                TooltipItem.optionToggle("Show Preview Pane", config::showPreviewPane, config::showPreviewPane, (item) -> {
                    main.setPreviewPanelVisbility(config.showPreviewPane());
                    main.refreshView();
                }),
                TooltipItem.optionToggle("Show Mapped Entries", config::showMappedEntries, config::showMappedEntries, (item) -> {
                    main.refreshView();
                }),
                TooltipItem.optionToggle("Show Ignored Entries", config::showIgnoredEntries, config::showIgnoredEntries, (item) -> {
                    main.refreshView();
                }),
                TooltipItem.optionToggle("Show Matching Entries", config::showMatchingEntries, config::showMatchingEntries, (item) -> {
                    main.refreshView();
                }),
                TooltipItem.optionToggle("Show Transformed Entries", config::showTransformedEntries, config::showTransformedEntries, (item) -> {
                    main.refreshView();
                }),
                TooltipItem.optionToggle("Show Java Metadata Files", config::showJavaMetadataFiles, config::showJavaMetadataFiles, (item) -> {
                    main.refreshView();
                }),
                TooltipItem.optionToggle("Show Bedrock Metadata Files", config::showBedrockMetadataFiles, config::showBedrockMetadataFiles, (item) -> {
                    main.refreshView();
                })
        );
        createPopupMenu(
                "Tools",
                TooltipItem.of("Set Java Relative Path", (item) -> {
                    new AssetDirectorySelectorWindow(main.getJavaAssetPanel(), () -> {
                        main.getJavaAssetPanel().redraw();
                        main.markSave();
                    });
                }),
                TooltipItem.of("Set Bedrock Relative Path", (item) -> {
                    new AssetDirectorySelectorWindow(main.getBedrockAssetPanel(), () -> {
                        main.getBedrockAssetPanel().redraw();
                        main.markSave();
                    });
                })
        );
        createPopupMenu(
                "Info",
                TooltipItem.of("Version", (item) -> {
                    new InfoWindow(main);
                })
        );
    }

    public void createPopupMenu(String name, TooltipItem... items) {
        JMenu menu = new JMenu(name);

        for (TooltipItem item : items) {
            JMenuItem menuItem = new JMenuItem(item.name());
            menuItem.addActionListener(e -> item.onClick(menuItem));
            menu.add(menuItem);
        }

        this.add(menu);
    }
}
