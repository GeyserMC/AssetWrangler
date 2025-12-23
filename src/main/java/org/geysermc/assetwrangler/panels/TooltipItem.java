package org.geysermc.assetwrangler.panels;

import org.geysermc.assetwrangler.keybinds.Keybind;
import org.geysermc.assetwrangler.windows.MappingsWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface TooltipItem {
    String name();

    void onClick(JMenuItem item);

    default Keybind keybind() {
        return null;
    }

    static TooltipItem of(String name, Consumer<JMenuItem> onClick) {
        return new TooltipItem() {
            @Override
            public String name() {
                return name;
            }

            @Override
            public void onClick(JMenuItem menuItem) {
                onClick.accept(menuItem);
            }
        };
    }

    static TooltipItem of(String name, Runnable onClick) {
        return of(name, (item) -> onClick.run());
    }

    static TooltipItem of(String name, Runnable onClick, Keybind keybind) {
        return new TooltipItem() {
            @Override
            public String name() {
                return name;
            }

            @Override
            public void onClick(JMenuItem menuItem) {
                onClick.run();
            }

            @Override
            public Keybind keybind() {
                return keybind;
            }
        };
    }

    static TooltipItem optionToggle(String name, Consumer<Boolean> set, Supplier<Boolean> get) {
        return optionToggle(name, set, get, (item) -> {});
    }

    static TooltipItem optionToggle(String name, Consumer<Boolean> set, Supplier<Boolean> get, Consumer<JMenuItem> onClick) {
        return new TooltipItem() {
            private static final String ON = " ✓";
            private static final String OFF = "";

            @Override
            public String name() {
                return name + (get.get() ? ON : OFF);
            }

            @Override
            public void onClick(JMenuItem item) {
                set.accept(!get.get());
                item.setText(name());
                onClick.accept(item);
            }
        };
    }
}
