package org.geysermc.assetwrangler.windows;

import lombok.Getter;
import org.geysermc.assetwrangler.BuildConstants;
import org.geysermc.assetwrangler.Main;
import org.geysermc.assetwrangler.actions.ActionManager;
import org.geysermc.assetwrangler.config.Config;
import org.geysermc.assetwrangler.panels.*;
import org.geysermc.assetwrangler.panels.BedrockAssetPanel;
import org.geysermc.assetwrangler.panels.JavaAssetPanel;
import org.geysermc.assetwrangler.sources.AssetSource;
import org.geysermc.assetwrangler.sources.AssetSources;
import org.geysermc.assetwrangler.utils.JButtonUtils;
import org.geysermc.assetwrangler.utils.JsonMappings;
import org.geysermc.assetwrangler.utils.JsonMappingsMeta;
import org.geysermc.assetwrangler.utils.MappingUtils;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

@Getter
public class MappingsWindow extends JFrame implements AssetViewerWindow {
    private final ActionManager actionManager;

    private final PreviewPanel previewPanel;
    private final JavaAssetPanel javaAssetPanel;
    private final BedrockAssetPanel bedrockAssetPanel;

    private YamlConfigurationLoader metaLoader;
    private JsonMappingsMeta mappingsMeta;
    private JsonMappings mappings;
    private boolean savesRequired = false;

    public MappingsWindow() {
        Main.registerForFrame(this);

        actionManager = new ActionManager(this);

        metaLoader = YamlConfigurationLoader.builder()
                .path(Path.of(Main.mappingFile.toString() + ".asset_mapper_meta.yml"))
                .indent(4)
                .nodeStyle(NodeStyle.BLOCK)
                .build();

        try {
            CommentedConfigurationNode node = metaLoader.load();
            mappingsMeta = node.get(JsonMappingsMeta.class);
            if (node.isNull()) {
                CommentedConfigurationNode newRoot = CommentedConfigurationNode.root(metaLoader.defaultOptions());
                newRoot.set(new Config());
                metaLoader.save(newRoot);
                mappingsMeta = newRoot.get(JsonMappingsMeta.class);
            }
        } catch (ConfigurateException e) {
            e.printStackTrace();
            previewPanel = null;
            javaAssetPanel = null;
            bedrockAssetPanel = null;
            JOptionPane.showMessageDialog(
                    null, "Something went wrong while reading the mappings meta :(",
                    "Error! Error!", JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        AssetSource javaSource = AssetSources.getAssetSource(Main.CONFIG.javaAssetSourceKey());
        AssetSource bedrockSource = AssetSources.getAssetSource(Main.CONFIG.bedrockAssetSourceKey());

        AtomicBoolean waitingTime = new AtomicBoolean(true);

        checkAssetSource(javaSource, () -> {
            checkAssetSource(bedrockSource, () -> {
                waitingTime.set(false);
            });
        });

        // Wait until the final callback is complete, blocking yes, but kinda required
        while (waitingTime.get()) {}

        mappings = MappingUtils.getMappings(Main.mappingFile.toFile());

        Layout layoutManager = new Layout();

        this.setLayout(layoutManager);

        this.add(new AssetMapperToolBar(this));
        this.add(javaAssetPanel = new JavaAssetPanel(this, javaSource, true));
        this.add(new SpacerPanel());
        this.add(bedrockAssetPanel = new BedrockAssetPanel(this, bedrockSource, true));
        this.add(new ActionPanel(this));
        this.add(this.previewPanel = new PreviewPanel(PreviewPanel.Type.BOTH));

        this.doLayout();
        this.setTitle(BuildConstants.getInstance().getName());
        this.setIconImage(Main.ICON_IMAGE);

        this.setSize(layoutManager.preferredLayoutSize(this));
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.setMinimumSize(layoutManager.minimumLayoutSize(this));

        this.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {
                MappingsWindow.this.offerSaveIfRequired(b -> {
                    if (b != null) {
                        if (b) Main.saveConfig();

                        System.exit(0);
                    }
                });
            }

            @Override
            public void windowClosed(WindowEvent e) {

            }

            @Override
            public void windowIconified(WindowEvent e) {

            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowActivated(WindowEvent e) {

            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }
        });
    }

    public boolean isJavaMapped(String path) {
        return mappings.isJavaInMappings(path);
    }

    public boolean isBedrockMapped(String path) {
        return mappings.isBedrockInMappings(path);
    }

    @Override
    public JsonMappingsMeta.Section getJavaMeta() {
        return mappingsMeta.getJava();
    }

    @Override
    public JsonMappingsMeta.Section getBedrockMeta() {
        return mappingsMeta.getBedrock();
    }

    public void offerSaveIfRequired(Consumer<Boolean> afterAction) {
        if (isSavesRequired()) {
            JDialog dialog = new JDialog(MappingsWindow.this, "Save?");
            Main.registerForFrame(dialog);
            dialog.setLayout(new FlowLayout());

            dialog.add(new JLabel("You have unsaved changes! Would you like to save?"));

            dialog.add(JButtonUtils.createButton("Save", (button, event) -> {
                afterAction.accept(true);
            }));
            dialog.add(JButtonUtils.createButton("Don't save", (button, event) -> {
                afterAction.accept(false);
            }));
            dialog.add(JButtonUtils.createButton("Cancel", (button, event) -> {
                afterAction.accept(null);
            }));

            dialog.setSize(325, 100);
            dialog.setResizable(false);
            dialog.setLocationRelativeTo(MappingsWindow.this);
            dialog.setVisible(true);
        } else {
            afterAction.accept(true);
        }
    }

    public void map() {
        List<String> javaAssets = javaAssetPanel.getSelectedPaths();
        List<String> bedrockAssets = bedrockAssetPanel.getSelectedPaths();

        if (javaAssets.isEmpty()) {
            JOptionPane.showMessageDialog(this, "You must select a java asset.", "Uh oh.", JOptionPane.WARNING_MESSAGE);
            return;
        } else if (bedrockAssets.isEmpty()) {
            JOptionPane.showMessageDialog(this, "You must select one or more bedrock assets.", "Uh oh.", JOptionPane.WARNING_MESSAGE);
            return;
        } else if (javaAssets.size() > 1) {
            JOptionPane.showMessageDialog(this, "You can only select one java asset.", "Uh oh.", JOptionPane.WARNING_MESSAGE);
            return;
        }

        actionManager.doAction(() -> {
            mappings.map(
                    javaAssets.getFirst(),
                    bedrockAssets
            );
            refreshView();
        }, () -> {
            mappings.remove(javaAssets.getFirst());
            refreshView();
        }, true);

    }

    public void match() {
        List<String> javaAssets = javaAssetPanel.getUnmappedSelectedPaths();
        List<String> bedrockAssets = bedrockAssetPanel.getUnmappedSelectedPaths();

        actionManager.doAction(() -> {
            javaAssets.forEach(mappingsMeta.getJava()::matchPath);
            bedrockAssets.forEach(mappingsMeta.getBedrock()::matchPath);
            refreshView();
        }, () -> {
            javaAssets.forEach(mappingsMeta.getJava()::unmatchPath);
            bedrockAssets.forEach(mappingsMeta.getBedrock()::unmatchPath);
            refreshView();
        }, true);
    }

    public void ignore() {
        List<String> javaAssets = javaAssetPanel.getUnmappedSelectedPaths();
        List<String> bedrockAssets = bedrockAssetPanel.getUnmappedSelectedPaths();

        actionManager.doAction(() -> {
            javaAssets.forEach(mappingsMeta.getJava()::ignorePath);
            bedrockAssets.forEach(mappingsMeta.getBedrock()::ignorePath);
            refreshView();
        }, () -> {
            javaAssets.forEach(mappingsMeta.getJava()::unignorePath);
            bedrockAssets.forEach(mappingsMeta.getBedrock()::unignorePath);
            refreshView();
        }, true);
    }

    public void transformed() {
        List<String> javaAssets = javaAssetPanel.getUnmappedSelectedPaths();
        List<String> bedrockAssets = bedrockAssetPanel.getUnmappedSelectedPaths();

        actionManager.doAction(() -> {
            javaAssets.forEach(mappingsMeta.getJava()::transformPath);
            bedrockAssets.forEach(mappingsMeta.getBedrock()::transformPath);
            refreshView();
        }, () -> {
            javaAssets.forEach(mappingsMeta.getJava()::untransformPath);
            bedrockAssets.forEach(mappingsMeta.getBedrock()::untransformPath);
            refreshView();
        }, true);
    }

    public void unmarkSave() {
        this.savesRequired = false;
        this.setTitle(BuildConstants.getInstance().getName());
    }

    public void markSave() {
        this.savesRequired = true;
        this.setTitle(BuildConstants.getInstance().getName() + " *");
    }

    public void save() {
        this.save(Main.mappingFile);
    }

    public void refreshView() {
        this.revalidate();
        this.repaint();
        this.javaAssetPanel.redraw();
        this.bedrockAssetPanel.redraw();

        if (this.javaAssetPanel.getUnmappedSelectedPaths().isEmpty()) {
            this.previewPanel.setJavaPreviewComponent(null);
        }

        if (this.bedrockAssetPanel.getUnmappedSelectedPaths().isEmpty()) {
            this.previewPanel.setBedrockPreviewComponent(null);
        }
    }

    private void save(Path path) {
        this.savesRequired = false;
        this.setTitle(BuildConstants.getInstance().getName());
        Main.mappingFile = path;
        MappingUtils.saveMappings(Main.mappingFile.toFile(), mappings);
        Path metaPath = Path.of(Main.mappingFile.toString() + ".asset_mapper_meta.yml");
        metaLoader = YamlConfigurationLoader.builder()
                .path(metaPath)
                .indent(4)
                .nodeStyle(NodeStyle.BLOCK)
                .build();
        CommentedConfigurationNode metaRoot = CommentedConfigurationNode.root(metaLoader.defaultOptions());
        try {
            metaRoot.set(mappingsMeta);
            metaLoader.save(metaRoot);
        } catch (ConfigurateException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occured while saving the metadata file of the mapping.", "Error! Error!", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void saveAs() {
        FileDialog chooser = new FileDialog(this);
        chooser.setTitle("Save Mapping");
        chooser.setMultipleMode(false);
        chooser.setMode(FileDialog.SAVE);
        chooser.setVisible(true);

        if (chooser.getFile() == null) return;

        this.save(Path.of(chooser.getDirectory(), chooser.getFile()));
    }

    public void setJavaPreviewComponent(JComponent component) {
        this.previewPanel.setJavaPreviewComponent(component);
    }

    public void setBedrockPreviewComponent(JComponent component) {
        this.previewPanel.setBedrockPreviewComponent(component);
    }

    public void setPreviewPanelVisbility(boolean val) {
        this.previewPanel.setVisible(val);
    }

    private class Layout implements LayoutManager {
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
}