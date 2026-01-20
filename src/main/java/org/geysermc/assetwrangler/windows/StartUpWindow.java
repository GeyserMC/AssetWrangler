package org.geysermc.assetwrangler.windows;

import org.geysermc.assetwrangler.BuildConstants;
import org.geysermc.assetwrangler.Logger;
import org.geysermc.assetwrangler.Main;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class StartUpWindow extends BaseWindow {
    public StartUpWindow() {
        super();
        this.setLayout(new FlowLayout());
        this.add(new JLabel("Welcome to %s! Select an option to begin.".formatted(BuildConstants.getInstance().getName())));

        JButton load = new JButton("Load PC Mapping");
        load.setToolTipText("Load a PackConverter mapping file.");
        load.addActionListener(e -> {
            FileDialog chooser = new FileDialog(this);
            chooser.setTitle("Load Mapping");
            chooser.setMultipleMode(false);
            chooser.setMode(FileDialog.LOAD);
            chooser.setVisible(true);

            if (chooser.getFile() == null) return;

            Main.mappingFile = Path.of(chooser.getDirectory(), chooser.getFile());

            StartUpWindow.this.setVisible(false);
            StartUpWindow.this.invalidate();
            StartUpWindow.this.dispose();

            new MappingsWindow();
        });
        this.add(load);

        JButton create = new JButton("Create PC Mapping");
        create.setToolTipText("Create a PackConverter mapping file.");
        create.addActionListener(e -> {
            FileDialog chooser = new FileDialog(this);
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
                        "Unable to create mapping file!", ex, StartUpWindow.this
                );
            }

            StartUpWindow.this.setVisible(false);
            StartUpWindow.this.invalidate();
            StartUpWindow.this.dispose();

            new MappingsWindow();
        });
        this.add(create);

        JButton javaAssets = new JButton("View Java Assets");
        javaAssets.setToolTipText("View the Java Assets from your selected source.");
        javaAssets.addActionListener(e -> {
            StartUpWindow.this.setVisible(false);
            StartUpWindow.this.invalidate();
            StartUpWindow.this.dispose();

            new ViewerWindow(true);
        });
        this.add(javaAssets);
        JButton bedrockAssets = new JButton("View Bedrock Assets");
        bedrockAssets.setToolTipText("View the Bedrock Assets from your selected source.");
        bedrockAssets.addActionListener(e -> {
            StartUpWindow.this.setVisible(false);
            StartUpWindow.this.invalidate();
            StartUpWindow.this.dispose();

            new ViewerWindow(false);
        });
        this.add(bedrockAssets);

        JButton setSources = new JButton("Set Source Locations");
        setSources.setToolTipText("Set the sources of your assets.");
        setSources.addActionListener(e -> {
            StartUpWindow.this.setVisible(false);
            StartUpWindow.this.invalidate();
            StartUpWindow.this.dispose();

            new SourceSelectWindow(StartUpWindow::new);
        });
        this.add(setSources);

        this.setTitle(BuildConstants.getInstance().getName());
        this.setIconImage(Main.ICON_IMAGE);

        this.setSize(375, 150);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setVisible(true);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
}
