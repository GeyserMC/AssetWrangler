package org.geysermc.assetwrangler.components.previews;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class TextPreview extends JScrollPane {
    public TextPreview(File file) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        try {
            textArea.setText(Files.readString(file.toPath()));
        } catch (IOException e) {
            e.printStackTrace();
            textArea.setText("Error reading file.");
        }
        panel.add(textArea, gbc);

        setViewportView(panel);
        getVerticalScrollBar().setUnitIncrement(20);
    }
}
