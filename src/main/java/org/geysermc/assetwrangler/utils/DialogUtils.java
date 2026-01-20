package org.geysermc.assetwrangler.utils;

import org.geysermc.assetwrangler.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.CompletableFuture;

public class DialogUtils {
    public static boolean yesOrNo(JFrame parent, String title, String message) {
        return JOptionPane.showOptionDialog(
                parent,
                message,
                title,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                Main.ICON, null, null
        ) == JOptionPane.YES_OPTION;
    }

    public static CompletableFuture<String> getString(JFrame parent, String title, String message) {
        CompletableFuture<String> future = new CompletableFuture<>();

        JDialog dialog = new JDialog();
        dialog.setTitle(title);
        dialog.setIconImage(Main.ICON_IMAGE);
        dialog.setLayout(new FlowLayout());

        dialog.add(new JLabel(message));

        JTextField textField = new JTextField();
        dialog.add(textField);

        JButton button = new JButton("Done");
        button.addActionListener(e -> {
            dialog.dispose();
            future.complete(textField.getText());
        });
        dialog.add(button);

        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                future.complete(null);
            }
        });
        dialog.setVisible(true);

        return future;
    }
}
