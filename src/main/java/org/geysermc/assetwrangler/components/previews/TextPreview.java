package org.geysermc.assetwrangler.components.previews;

import org.geysermc.assetwrangler.Logger;
import org.geysermc.assetwrangler.Main;
import org.geysermc.assetwrangler.components.ClosableComponent;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.function.Consumer;

public class TextPreview extends JScrollPane implements ClosableComponent {
    private final Consumer<Boolean> darkModeHook;

    public TextPreview(File file) {
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
        JTextArea textArea = new JTextArea();
        textArea.setEnabled(false);
        darkModeHook = (darkMode) -> {
            if (darkMode) {
                textArea.setDisabledTextColor(Color.WHITE);
            } else {
                textArea.setDisabledTextColor(Color.BLACK);
            }
        };

        darkModeHook.accept(Main.isDarkMode());
        Main.registerDarkModeHook(darkModeHook);
        textArea.setText(val);
        textArea.setCaretPosition(0);

        panel.add(textArea, gbc);

        setViewportView(panel);
        getVerticalScrollBar().setUnitIncrement(20);
    }

    @Override
    public void close() {
        Main.unregisterDarkModeHook(darkModeHook);
    }
}
