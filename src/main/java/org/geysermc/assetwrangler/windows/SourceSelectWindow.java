package org.geysermc.assetwrangler.windows;

import org.geysermc.assetwrangler.BuildConstants;
import org.geysermc.assetwrangler.Logger;
import org.geysermc.assetwrangler.Main;
import org.geysermc.assetwrangler.components.DelegateStorageButtonModel;
import org.geysermc.assetwrangler.components.SourceLabel;
import org.geysermc.assetwrangler.sources.AssetSource;
import org.geysermc.assetwrangler.sources.AssetSources;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class SourceSelectWindow extends BaseWindow {
    public SourceSelectWindow(Runnable onComplete) {
        super();
        this.setLayout(new FlowLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(1, 2, 10, 0));

        JPanel javaPanel = new JPanel();
        javaPanel.setLayout(new FlowLayout());
        JComboBox<SourceLabel> javaComboBox = new JComboBox<>();
        for (AssetSource assetSource : AssetSources.javaSources()) {
            SourceLabel label = new SourceLabel(assetSource.getKey(), assetSource.getName());
            javaComboBox.addItem(label);

            if (Main.CONFIG.javaAssetSourceKey().equals(assetSource.getKey()))
                javaComboBox.setSelectedItem(label);
        }
        javaPanel.add(new JLabel("Java Sources"));
        javaPanel.add(javaComboBox);

        JPanel bedrockPanel = new JPanel();
        bedrockPanel.setLayout(new FlowLayout());
        JComboBox<SourceLabel> bedrockComboBox = new JComboBox<>();
        for (AssetSource assetSource : AssetSources.bedrockSources()) {
            SourceLabel label = new SourceLabel(assetSource.getKey(), assetSource.getName());
            bedrockComboBox.addItem(label);

            if (Main.CONFIG.bedrockAssetSourceKey().equals(assetSource.getKey()))
                bedrockComboBox.setSelectedItem(label);
        }
        bedrockPanel.add(new JLabel("Bedrock Sources"));
        bedrockPanel.add(bedrockComboBox);

        panel.add(javaPanel);
        panel.add(bedrockPanel);

        this.add(panel);

        JButton done = new JButton("Done");
        done.addActionListener(e -> {
            if (javaComboBox.getSelectedItem() == null) {
                Logger.warnWithDialog("Select a java source.", null, SourceSelectWindow.this);
            } else if (bedrockComboBox.getSelectedItem() == null) {
                Logger.warnWithDialog("Select a bedrock source.", null, SourceSelectWindow.this);
            } else {
                String javaId = ((SourceLabel) javaComboBox.getSelectedItem()).getId();
                String bedrockId = ((SourceLabel) bedrockComboBox.getSelectedItem()).getId();

                AssetSource javaSource = AssetSources.getAssetSource(javaId);
                AssetSource bedrockSource = AssetSources.getAssetSource(bedrockId);

                if (javaSource.getDisabledMessage() != null) {
                    Logger.warnWithDialog(javaSource.getDisabledMessage(), null, SourceSelectWindow.this);
                } else if (bedrockSource.getDisabledMessage() != null) {
                    Logger.warnWithDialog(bedrockSource.getDisabledMessage(), null, SourceSelectWindow.this);
                } else {
                    Main.CONFIG.javaAssetSourceKey(javaId);
                    Main.CONFIG.bedrockAssetSourceKey(bedrockId);

                    try {
                        javaSource.download(Main.DATA_FOLDER, SourceSelectWindow.this, () -> {
                            try {
                                bedrockSource.download(Main.DATA_FOLDER, SourceSelectWindow.this, () -> {
                                    onComplete.run();
                                    this.dispose();
                                }, false);
                            } catch (IOException ex) {
                                Logger.errorWithDialog(
                                        "Something went wrong while fetching bedrock assets", ex, this
                                );
                            }
                        }, false);
                    } catch (IOException ex) {
                        Logger.errorWithDialog(
                                "Something went wrong while fetching java assets", ex, this
                        );
                    }
                }
            }
        });
        this.add(done);

        this.setSize(600, 125);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setTitle(BuildConstants.getInstance().getName());
        this.setIconImage(Main.ICON_IMAGE);
        this.setVisible(true);
    }
}
