package org.geysermc.assetwrangler.panels;

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

    static TooltipItem of(String name, Runnable onClick, MappingsWindow main, KeyStroke keyStroke) {
        InputMap inputMap = main.getLayeredPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);

        inputMap.put(keyStroke, name);
        main.getLayeredPane().getActionMap().put(name, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onClick.run();
            }
        });

        return new TooltipItem() {
            @Override
            public String name() {
                return name;
            }

            @Override
            public void onClick(JMenuItem menuItem) {
                onClick.run();
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
