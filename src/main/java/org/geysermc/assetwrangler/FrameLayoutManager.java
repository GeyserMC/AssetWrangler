package org.geysermc.assetwrangler;

import org.geysermc.assetwrangler.panels.*;
import org.geysermc.assetwrangler.panels.BedrockAssetPanel;
import org.geysermc.assetwrangler.panels.JavaAssetPanel;
import org.geysermc.assetwrangler.windows.MappingsWindow;

import java.awt.*;

public class FrameLayoutManager implements LayoutManager {
    private final MappingsWindow window;

    public FrameLayoutManager(MappingsWindow window) {
        this.window = window;
    }

    public void addLayoutComponent(String name, Component comp) {}
    public void removeLayoutComponent(Component comp) {}

    @Override
    public Dimension preferredLayoutSize(Container parent) {
        return new Dimension(1200, 800);
    }

    @Override
    public Dimension minimumLayoutSize(Container parent) {
        return new Dimension(800, 600);
    }

    @Override
    public void layoutContainer(Container parent) {
        synchronized (parent.getTreeLock()) {
            int x0 = 0;
            int y0 = 5;

            int widthMinus = parent.getWidth() - 5;
            int heightMinus = parent.getHeight() - 65;

            int panelsWidth = widthMinus / 2;
            int panelsHeight = (int) (heightMinus * (Main.CONFIG.showPreviewPane() ? 0.6 : 1));

            int x2 = panelsWidth + 5;
            int y2 = (int) (heightMinus * 0.6) + 65;

            for (Component component : parent.getComponents()) {
                if (component instanceof AssetMapperToolBar toolBar) {
                    toolBar.setBounds(x0, y0, parent.getWidth(), 30);
                } else if (component instanceof JavaAssetPanel panel) {
                    panel.setBounds(x0, y0 + 30, panelsWidth, panelsHeight);
                } else if (component instanceof SpacerPanel panel) {
                    panel.setBounds(panelsWidth, y0 + 30, 5, panelsHeight);
                } else if (component instanceof BedrockAssetPanel panel) {
                    panel.setBounds(x2, y0 + 30, panelsWidth, panelsHeight);
                } else if (component instanceof ActionPanel panel) {
                    panel.setBounds(x0, panelsHeight + 30, parent.getWidth(), 35);
                } else if (component instanceof PreviewPanel panel) {
                    panel.setBounds(x0, y2, Main.CONFIG.showPreviewPane() ? parent.getWidth() : 0, Main.CONFIG.showPreviewPane() ? (int) (heightMinus * 0.4) : 0);
                    panel.setVisible(Main.CONFIG.showPreviewPane());
                }
            }
        }
    }
}
