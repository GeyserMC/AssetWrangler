package org.geysermc.assetwrangler.utils;

import org.geysermc.assetwrangler.Main;

import javax.swing.*;

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

    public static void warning(JFrame parent, String title, String message) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.WARNING_MESSAGE);
    }
}
