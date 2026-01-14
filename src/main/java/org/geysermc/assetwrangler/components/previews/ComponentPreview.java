package org.geysermc.assetwrangler.components.previews;

import org.geysermc.assetwrangler.Logger;
import org.geysermc.assetwrangler.components.ComponentTextArea;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ComponentPreview extends JScrollPane {
    public ComponentPreview(File file) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        String val;
        try {
            val = Files.readString(file.toPath());
        } catch (IOException e) {
            Logger.error("Error loading text file (%s)".formatted(file.toString()), e);
            val = "Error reading file.";
        }
        ComponentTextArea textArea = new ComponentTextArea();
        textArea.appendLegacyText(val);
        textArea.setBackground(new Color(1, 1, 1, 102));
        textArea.setOpaque(false);

        panel.add(textArea, gbc);

        setViewportView(panel);
        getVerticalScrollBar().setUnitIncrement(20);
    }
}
