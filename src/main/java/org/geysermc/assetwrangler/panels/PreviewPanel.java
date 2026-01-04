package org.geysermc.assetwrangler.panels;

import lombok.Getter;
import org.geysermc.assetwrangler.components.ClosableComponent;

import javax.swing.*;
import java.awt.*;

public class PreviewPanel extends BasePanel {
    private final Type type;

    private JComponent previousJavaComponent;
    private JComponent previousBedrockComponent;

    public PreviewPanel(Type type) {
        super();
        this.type = type;
        this.setLayout(new GridLayout(1, type.isSingular() ? 1 : 2));
        if (type.equals(Type.BOTH) || type.equals(Type.JAVA)) this.add(this.previousJavaComponent = new JLabel("Nothing to preview."));
        if (type.equals(Type.BOTH) || type.equals(Type.BEDROCK)) this.add(this.previousBedrockComponent = new JLabel("Nothing to preview."));
    }

    public void setJavaPreviewComponent(JComponent component) {
        if (component == null) component = new JLabel("Nothing to preview.");

        this.remove(previousJavaComponent);
        if (previousJavaComponent instanceof ClosableComponent c) c.close();
        previousJavaComponent.invalidate();
        this.add(component, 0);
        previousJavaComponent = component;
        this.revalidate();
        this.repaint();
    }

    public void setBedrockPreviewComponent(JComponent component) {
        if (component == null) component = new JLabel("Nothing to preview.");

        this.remove(previousBedrockComponent);
        if (previousBedrockComponent instanceof ClosableComponent c) c.close();
        previousBedrockComponent.invalidate();
        this.add(component, type.isSingular() ? 0 : 1);
        previousBedrockComponent = component;
        this.revalidate();
        this.repaint();
    }

    public enum Type {
        BOTH(false), JAVA(true), BEDROCK(true);

        @Getter
        private final boolean singular;

        Type(boolean singular) {
            this.singular = singular;
        }
    }
}
