package org.geysermc.assetwrangler.panels;

import org.geysermc.assetwrangler.keybinds.Keybind;
import org.geysermc.assetwrangler.windows.MappingsWindow;

import javax.swing.*;
import javax.swing.plaf.basic.BasicCheckBoxUI;
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

    default JMenuItem getMenuItem() {
        JMenuItem menuItem = new JMenuItem(this.name());
        if (this.keybind() != null) menuItem.setAccelerator(this.keybind().getKeyStroke());
        menuItem.addActionListener(e -> this.onClick(menuItem));
        return menuItem;
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
            @Override
            public String name() {
                return name;
            }

            @Override
            public void onClick(JMenuItem item) {
                set.accept(!get.get());
                item.setSelected(get.get());
                onClick.accept(item);
            }

            @Override
            public JMenuItem getMenuItem() {
                JMenuItem menuItem = TooltipItem.super.getMenuItem();
                menuItem.setLayout(new BoxLayout(menuItem, BoxLayout.X_AXIS));
                menuItem.setBorder(BorderFactory.createCompoundBorder(
                        menuItem.getBorder(),
                        BorderFactory.createEmptyBorder(0, 10, 0, 10)
                ));
                menuItem.setAlignmentX(Component.LEFT_ALIGNMENT);
                menuItem.setUI(new BasicCheckBoxUI());
                menuItem.setSelected(get.get());
                menuItem.setHorizontalAlignment(SwingConstants.LEFT);
                return menuItem;
            }
        };
    }
}
