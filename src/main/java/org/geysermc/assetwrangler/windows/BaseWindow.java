package org.geysermc.assetwrangler.windows;

import org.geysermc.assetwrangler.Main;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class BaseWindow extends JFrame {
    public BaseWindow() {
        Main.registerForFrame(this);
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                BaseWindow.this.close();
            }
        });
    }

    public void close() {
        Main.saveConfig();
        this.dispose();
        System.exit(0);
    }
}
