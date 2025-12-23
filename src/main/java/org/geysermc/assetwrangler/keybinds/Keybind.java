package org.geysermc.assetwrangler.keybinds;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public record Keybind(int keycode, int modifers) {
    public static Keybind ctrl(int keycode) {
        return new Keybind(
                keycode,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()
        );
    }

    public static Keybind ctrlShift(int keycode) {
        return new Keybind(
                keycode,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx() |
                    KeyEvent.SHIFT_DOWN_MASK
        );
    }

    public KeyStroke getKeyStroke() {
        return KeyStroke.getKeyStroke(this.keycode, this.modifers);
    }
}
