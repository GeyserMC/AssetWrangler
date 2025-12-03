package org.geysermc.assetwrangler.windows;

import org.geysermc.assetwrangler.BuildConstants;
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

public class SourceSelectWindow extends JFrame {
    public SourceSelectWindow(Runnable onComplete) {
        Main.registerForFrame(this);

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
                JOptionPane.showMessageDialog(
                        SourceSelectWindow.this,
                        "Select a java source.",
                        "Uh oh", JOptionPane.WARNING_MESSAGE
                );
            } else if (bedrockComboBox.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(
                        SourceSelectWindow.this,
                        "Select a bedrock source.",
                        "Uh oh", JOptionPane.WARNING_MESSAGE
                );
            } else {
                String javaId = ((SourceLabel) javaComboBox.getSelectedItem()).getId();
                String bedrockId = ((SourceLabel) bedrockComboBox.getSelectedItem()).getId();

                AssetSource javaSource = AssetSources.getAssetSource(javaId);
                AssetSource bedrockSource = AssetSources.getAssetSource(bedrockId);

                if (javaSource.getDisabledMessage() != null) {
                    JOptionPane.showMessageDialog(
                            SourceSelectWindow.this,
                            javaSource.getDisabledMessage(),
                            "Uh oh", JOptionPane.WARNING_MESSAGE
                    );
                } else if (bedrockSource.getDisabledMessage() != null) {
                    JOptionPane.showMessageDialog(
                            SourceSelectWindow.this,
                            bedrockSource.getDisabledMessage(),
                            "Uh oh", JOptionPane.WARNING_MESSAGE
                    );
                } else {
                    Main.CONFIG.javaAssetSourceKey(javaId);
                    Main.CONFIG.bedrockAssetSourceKey(bedrockId);

                    try {
                        javaSource.setup(Main.DATA_FOLDER, SourceSelectWindow.this, () -> {
                            try {
                                bedrockSource.setup(Main.DATA_FOLDER, SourceSelectWindow.this, () -> {
                                    onComplete.run();
                                    this.dispose();
                                });
                            } catch (IOException ex) {
                                ex.printStackTrace();
                                JOptionPane.showMessageDialog(
                                        this,
                                        "Something went wrong while fetching bedrock assets",
                                        "Error! Error!",
                                        JOptionPane.ERROR_MESSAGE
                                );
                            }
                        });
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(
                                this,
                                "Something went wrong while fetching java assets",
                                "Error! Error!",
                                JOptionPane.ERROR_MESSAGE
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
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setVisible(true);
    }
}
