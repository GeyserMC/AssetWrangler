package org.geysermc.assetwrangler.windows;

import lombok.Getter;
import org.geysermc.assetwrangler.BuildConstants;
import org.geysermc.assetwrangler.Main;
import org.geysermc.assetwrangler.actions.ActionManager;
import org.geysermc.assetwrangler.panels.*;
import org.geysermc.assetwrangler.sources.AssetSource;
import org.geysermc.assetwrangler.sources.AssetSources;
import org.geysermc.assetwrangler.utils.JsonMappingsMeta;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class ViewerWindow extends BaseWindow implements AssetViewerWindow {
    @Getter
    private final ActionManager actionManager;

    private final AssetPanel assetPanel;
    private final PreviewPanel previewPanel;

    public ViewerWindow(boolean isJava) {
        super();
        actionManager = new ActionManager(this);

        this.setLayout(new Layout());

        AtomicBoolean waitingTime = new AtomicBoolean(true);

        if (isJava) {
            AssetSource javaSource = AssetSources.getAssetSource(Main.CONFIG.javaAssetSourceKey());
            checkAssetSource(javaSource, () -> {
                waitingTime.set(false);
            });
            // Wait until the final callback is complete, blocking yes, but kinda required
            while (waitingTime.get()) {}
            this.assetPanel = new JavaAssetPanel(this, javaSource, false);
        } else {
            AssetSource bedrockSource = AssetSources.getAssetSource(Main.CONFIG.bedrockAssetSourceKey());
            checkAssetSource(bedrockSource, () -> {
                waitingTime.set(false);
            });
            // Wait until the final callback is complete, blocking yes, but kinda required
            while (waitingTime.get()) {}
            this.assetPanel = new BedrockAssetPanel(this, bedrockSource, false);
        }

        this.add(assetPanel);

        this.previewPanel = new PreviewPanel(isJava ? PreviewPanel.Type.JAVA : PreviewPanel.Type.BEDROCK);
        this.add(previewPanel);

        this.setIconImage(Main.ICON_IMAGE);
        this.setTitle(BuildConstants.getInstance().getName());

        this.setSize(800, 600);
        this.setMinimumSize(new Dimension(400, 300));
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setVisible(true);
    }

    @Override
    public void setJavaPreviewComponent(JComponent component) {
        this.previewPanel.setJavaPreviewComponent(component);
    }

    @Override
    public void setBedrockPreviewComponent(JComponent component) {
        this.previewPanel.setBedrockPreviewComponent(component);
    }

    @Override
    public boolean isJavaMapped(String path) {
        return false;
    }

    @Override
    public boolean isBedrockMapped(String path) {
        return false;
    }

    @Override
    public JsonMappingsMeta.Section getJavaMeta() {
        return new JsonMappingsMeta.Section();
    }

    @Override
    public JsonMappingsMeta.Section getBedrockMeta() {
        return new JsonMappingsMeta.Section();
    }

    @Override
    public boolean isSavesRequired() {
        return false;
    }

    @Override
    public void markSave() {
        // No-op, saves shouldn't happen on a viewer window
    }

    @Override
    public void unmarkSave() {
        // No-op, saves shouldn't happen on a viewer window
    }

    private static class Layout implements LayoutManager {
        private static final int MINIMUM_PREVIEW_PANEL_HEIGHT = 256;

        public void addLayoutComponent(String name, Component comp) {}
        public void removeLayoutComponent(Component comp) {}
        public Dimension preferredLayoutSize(Container parent) {return null;}
        public Dimension minimumLayoutSize(Container parent) {return null;}

        @Override
        public void layoutContainer(Container parent) {
            synchronized (parent.getTreeLock()) {
                int panelsHeight = parent.getHeight() - MINIMUM_PREVIEW_PANEL_HEIGHT;

                for (Component component : parent.getComponents()) {
                    if (component instanceof AssetPanel panel) {
                        panel.setBounds(0, 0, parent.getWidth(), panelsHeight);
                    } else if (component instanceof PreviewPanel panel) {
                        panel.setBounds(0, panelsHeight, parent.getWidth(), MINIMUM_PREVIEW_PANEL_HEIGHT);
                    }
                }
            }
        }
    }
}
