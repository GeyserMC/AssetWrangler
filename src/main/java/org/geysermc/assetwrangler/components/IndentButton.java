package org.geysermc.assetwrangler.components;

import javax.swing.*;
import java.awt.*;

public class IndentButton extends JButton {
    private final int indent;

    public IndentButton(Icon icon, String text, int indent) {
        super(text, icon);
        this.indent = indent;
    }

    public IndentButton(String text, int indent) {
        super(text);
        this.indent = indent;
    }

    @Override
    public void paint(Graphics g) {
        g.translate(indent * 10, 0);
        super.paint(g);
    }
}
