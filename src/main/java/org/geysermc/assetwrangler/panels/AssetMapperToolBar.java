package org.geysermc.assetwrangler.panels;

import org.geysermc.assetwrangler.Logger;
import org.geysermc.assetwrangler.Main;
import org.geysermc.assetwrangler.keybinds.Keybind;
import org.geysermc.assetwrangler.windows.*;
import org.geysermc.assetwrangler.config.Config;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class AssetMapperToolBar extends JMenuBar {
    public AssetMapperToolBar(MappingsWindow main) {
        Config config = Main.CONFIG;

        createPopupMenu(
                "File",
                TooltipItem.of("Create", () -> {
                    main.offerSaveIfRequired(b -> {
                        if (b != null) {
                            FileDialog chooser = new FileDialog(main);
                            chooser.setTitle("Create Mapping");
                            chooser.setMultipleMode(false);
                            chooser.setMode(FileDialog.SAVE);
                            chooser.setVisible(true);

                            if (chooser.getFile() == null) return;

                            Main.mappingFile = Path.of(chooser.getDirectory(), chooser.getFile());

                            try {
                                Files.writeString(Main.mappingFile, "{}");
                            } catch (IOException ex) {
                                Logger.errorWithDialog(
                                        "Unable to create mapping file!", ex, main
                                );
                            }

                            main.reload();
                        }
                    });
                }, Keybind.ctrl(KeyEvent.VK_N)),
                TooltipItem.of("Open", () -> {
                    main.offerSaveIfRequired(b -> {
                        if (b != null) {
                            FileDialog chooser = new FileDialog(main);
                            chooser.setTitle("Load Mapping");
                            chooser.setMultipleMode(false);
                            chooser.setMode(FileDialog.LOAD);
                            chooser.setVisible(true);

                            if (chooser.getFile() == null) return;

                            Main.mappingFile = Path.of(chooser.getDirectory(), chooser.getFile());

                            main.reload();
                        }
                    });
                }, Keybind.ctrl(KeyEvent.VK_O)),
                TooltipItem.of(
                        "Save", main::save,
                        Keybind.ctrl(KeyEvent.VK_S)
                ),
                TooltipItem.of(
                        "Save As", main::saveAs,
                        Keybind.ctrlShift(KeyEvent.VK_S)
                )
        );
        createPopupMenu(
                "Edit",
                TooltipItem.of("Undo", () -> {
                    if (main.getActionManager().canUndo()) {
                        main.getActionManager().undo();
                    } else {
                        Logger.warnWithDialog("Nothing to undo!", null, main);
                    }
                }, Keybind.ctrl(KeyEvent.VK_Z)),
                TooltipItem.of("Redo", () -> {
                    if (main.getActionManager().canRedo()) {
                        main.getActionManager().redo();
                    } else {
                        Logger.warnWithDialog("Nothing to redo!", null, main);
                    }
                }, Keybind.ctrl(KeyEvent.VK_Y))
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
                TooltipItem.optionToggle("Show Texture Metadata Files", config::showTextureMetadata, config::showTextureMetadata, (item) -> {
                    main.refreshView();
                }),
                TooltipItem.optionToggle("Disable Animation Interpolation", config::disableAnimationInterpolation, config::disableAnimationInterpolation)
        );
        createPopupMenu(
                "Tools",
                TooltipItem.of("Set Java Relative Path", () -> {
                    new AssetDirectorySelectorWindow(main.getJavaAssetPanel());
                }),
                TooltipItem.of("Set Bedrock Relative Path", () -> {
                    new AssetDirectorySelectorWindow(main.getBedrockAssetPanel());
                }),
                TooltipItem.of("View Missing Files", () -> {
                    new MissingPathsViewerWindow(main);
                })
        );
        createPopupMenu(
                "Info",
                TooltipItem.of("Version", () -> {
                    new InfoWindow(main);
                })
        );
    }

    public void createPopupMenu(String name, TooltipItem... items) {
        JMenu menu = new JMenu(name);

        for (TooltipItem item : items) {
            menu.add(item.getMenuItem());
        }

        this.add(menu);
    }
}
