package org.geysermc.assetwrangler.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URI;
import java.util.function.BiConsumer;

public class JButtonUtils {
    private JButtonUtils() {
        throw new AssertionError("Whoops.");
    }

    public static JButton createButton(String name, BiConsumer<JButton, ActionEvent> handler) {
        JButton button = new JButton(name);
        button.addActionListener(e -> {
            handler.accept(button, e);
        });
        return button;
    }

    public static JButton linkButton(String name, String url) {
        JButton button = new JButton(name);
        button.addActionListener(e -> {
            try {
                Desktop.getDesktop().browse(URI.create(url));
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(
                        null, "Something went wrong when opening link!",
                        "Error! Error!", JOptionPane.ERROR_MESSAGE
                );
            }
        });
        return button;
    }
}
