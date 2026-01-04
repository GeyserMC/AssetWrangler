package org.geysermc.assetwrangler.components.previews;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class NoPlaySoundPreview extends JPanel {
    public NoPlaySoundPreview(File file, String relativePath) {
        this.setLayout(new FlowLayout());

        JButton button = new JButton("Open");
        button.addActionListener(e -> {
            try {
                Desktop.getDesktop().open(file);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            } catch (UnsupportedOperationException ignored) {}
        });
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
            button.setEnabled(false);
            button.setToolTipText("Unable to open files on your Operating System.");
        }
        this.add(button);

        this.add(new JLabel("Path: " + relativePath));
    }
}
