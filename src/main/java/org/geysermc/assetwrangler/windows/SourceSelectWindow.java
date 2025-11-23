package org.geysermc.assetwrangler.windows;

import org.geysermc.assetwrangler.BuildConstants;
import org.geysermc.assetwrangler.Main;
import org.geysermc.assetwrangler.components.DelegateStorageButtonModel;
import org.geysermc.assetwrangler.sources.AssetSource;
import org.geysermc.assetwrangler.sources.AssetSources;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class SourceSelectWindow extends JFrame {
    public SourceSelectWindow(Runnable onComplete) {
        this.setLayout(new FlowLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(1, 2, 10, 0));

        ButtonGroup javaGroup = new ButtonGroup();
        ButtonModel selectedJavaModel = null;
        JPanel javaPanel = new JPanel();
        javaPanel.setLayout(new GridLayout(0, 1, 0, 10));
        for (AssetSource assetSource : AssetSources.javaSources()) {
            JRadioButton button = new JRadioButton(assetSource.getName());
            button.setModel(new DelegateStorageButtonModel(button.getModel(), assetSource.getKey()));

            if (Main.CONFIG.javaAssetSourceKey().equals(assetSource.getKey())) selectedJavaModel = button.getModel();

            if (assetSource.getDisabledMessage() != null) {
                button.setEnabled(false);
                button.setToolTipText(assetSource.getDisabledMessage());
            }
            button.addActionListener(e -> {
                Main.CONFIG.javaAssetSourceKey(assetSource.getKey());
                javaGroup.setSelected(button.getModel(), true);
            });
            javaPanel.add(button);
        }
        javaGroup.setSelected(selectedJavaModel, true);

        ButtonGroup bedrockGroup = new ButtonGroup();
        ButtonModel selectedBedrockModel = null;
        JPanel bedrockPanel = new JPanel();
        bedrockPanel.setLayout(new GridLayout(0, 1, 0, 10));
        for (AssetSource assetSource : AssetSources.bedrockSources()) {
            JRadioButton button = new JRadioButton(assetSource.getName());
            button.setModel(new DelegateStorageButtonModel(button.getModel(), assetSource.getKey()));

            if (Main.CONFIG.bedrockAssetSourceKey().equals(assetSource.getKey())) selectedBedrockModel = button.getModel();

            if (assetSource.getDisabledMessage() != null) {
                button.setEnabled(false);
                button.setToolTipText(assetSource.getDisabledMessage());
            }
            button.addActionListener(e -> {
                Main.CONFIG.bedrockAssetSourceKey(assetSource.getKey());
                bedrockGroup.setSelected(button.getModel(), true);
            });
            bedrockPanel.add(button);
        }
        bedrockGroup.setSelected(selectedBedrockModel, true);

        panel.add(javaPanel);
        panel.add(bedrockPanel);

        this.add(panel);

        JButton done = new JButton("Done");
        done.addActionListener(e -> {
            if (javaGroup.getSelection() == null) {
                JOptionPane.showMessageDialog(
                        SourceSelectWindow.this,
                        "Select a java source.",
                        "Uh oh", JOptionPane.WARNING_MESSAGE
                );
            } else if (bedrockGroup.getSelection() == null) {
                JOptionPane.showMessageDialog(
                        SourceSelectWindow.this,
                        "Select a bedrock source.",
                        "Uh oh", JOptionPane.WARNING_MESSAGE
                );
            } else {
                Main.CONFIG.javaAssetSourceKey(((DelegateStorageButtonModel) javaGroup.getSelection()).getStoredId());
                Main.CONFIG.bedrockAssetSourceKey(((DelegateStorageButtonModel) bedrockGroup.getSelection()).getStoredId());

                AssetSource javaSource = AssetSources.getAssetSource(Main.CONFIG.javaAssetSourceKey());
                try {
                    javaSource.setup(Main.DATA_FOLDER, SourceSelectWindow.this);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(
                            this,
                            "Something went wrong while fetching java assets",
                            "Error! Error!",
                            JOptionPane.ERROR_MESSAGE
                    );
                }

                AssetSource bedrockSource = AssetSources.getAssetSource(Main.CONFIG.bedrockAssetSourceKey());
                try {
                    bedrockSource.setup(Main.DATA_FOLDER, SourceSelectWindow.this);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(
                            this,
                            "Something went wrong while fetching bedrock assets",
                            "Error! Error!",
                            JOptionPane.ERROR_MESSAGE
                    );
                }

                onComplete.run();
                this.dispose();
            }
        });
        this.add(done);

        this.setSize(400, 170);
        this.setLocationRelativeTo(null);
        this.setTitle(BuildConstants.getInstance().getName());
        this.setIconImage(Main.ICON_IMAGE);
        this.setVisible(true);
    }
}
