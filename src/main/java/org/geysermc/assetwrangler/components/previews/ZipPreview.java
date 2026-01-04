package org.geysermc.assetwrangler.components.previews;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipPreview extends JScrollPane {
    public ZipPreview(File file) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        JLabel filesText = new JLabel("Files:");
        filesText.setFont(filesText.getFont().deriveFont(18f));
        panel.add(filesText, gbc);

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setBackground(new Color(28, 30, 37));
        try {
            ZipFile zipFile = new ZipFile(file);
            String builder = String.join("\n", zipFile.stream().filter(entry -> !entry.isDirectory()).map(ZipEntry::getName).toList());
            textArea.append(builder);
            zipFile.close();
        } catch (IOException e) {
            e.printStackTrace();
            textArea.setText("Error reading file.");
        }
        panel.add(textArea, gbc);

        setViewportView(panel);
        getVerticalScrollBar().setUnitIncrement(20);
    }
}
