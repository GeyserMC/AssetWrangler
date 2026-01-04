package org.geysermc.assetwrangler.treemodels;

import com.google.gson.JsonParser;
import com.twelvemonkeys.image.BufferedImageIcon;
import lombok.Getter;
import org.geysermc.assetwrangler.components.previews.*;
import org.geysermc.assetwrangler.panels.AssetPanel;
import org.geysermc.assetwrangler.utils.AnimationMeta;
import org.geysermc.assetwrangler.utils.Asset;
import org.geysermc.assetwrangler.utils.ColorUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.geysermc.assetwrangler.utils.Asset.getMappedLocation;

public class AssetTreeModel implements TreeModel {
    private final AssetPanel panel;
    private final Asset rootAsset;
    private final String rootName;

    private Entry root = null;

    public AssetTreeModel(AssetPanel panel, Asset rootAsset, String rootName) {
        this.panel = panel;
        this.rootAsset = rootAsset;
        this.rootName = rootName;

        generateRoot();
    }

    public void generateRoot() {
        root = getEntry(rootAsset);
        root.name = rootName;
    }

    public Entry getEntry(Asset asset) {
        List<Asset> visibleChildren = asset.getChildren().stream()
                .filter(a -> a.viewable(panel))
                .toList();

        if (asset.isDirectory() && visibleChildren.isEmpty()) return null;

        String location = panel.getMetaSection().getRelativePath().isBlank() ||
                panel.getMetaSection().getRelativePath().equals(asset.getRelativePath())
                ? asset.getRelativePath() :
                asset.getRelativePath().substring(
                        panel.getMetaSection().getRelativePath().length() + 1
                );

        return new Entry(
                this,
                visibleChildren.stream()
                        .map(this::getEntry)
                        .filter(Objects::nonNull)
                        .toList(),
                asset.getName(),
                asset.getPath().toString(),
                getMappedLocation(panel, location, false),
                location,
                asset.isDirectory()
        );
    }

    @Override
    public Object getRoot() {
        return root;
    }

    @Override
    public Object getChild(Object parent, int index) {
        Entry entry = (Entry) parent;
        return entry.getChildren().get(index);
    }

    @Override
    public int getChildCount(Object parent) {
        Entry entry = (Entry) parent;
        return entry.getChildren().size();
    }

    @Override
    public boolean isLeaf(Object node) {
        Entry entry = (Entry) node;
        return !entry.isDirectory();
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        Entry par = (Entry) parent;
        Entry ch = (Entry) parent;
        return par.getChildren().indexOf(ch);
    }

    public void valueForPathChanged(TreePath path, Object newValue) {}
    public void addTreeModelListener(TreeModelListener l) {}
    public void removeTreeModelListener(TreeModelListener l) {}

    public static class Entry implements Comparable<Entry> {
        private final AssetTreeModel treeModel;

        @Getter
        private final List<Entry> children;
        private final String filePath;
        @Getter
        private final String relativePath;
        @Getter
        private final String unmappedRelativePath;
        @Getter
        private final boolean directory;

        private String name;

        public Entry(AssetTreeModel treeModel, List<Entry> children, String name, String filePath, String relativePath, String unmappedRelativePath, boolean directory) {
            this.treeModel = treeModel;

            List<Entry> directories = children.stream()
                    .filter(Entry::isDirectory)
                    .sorted()
                    .toList();
            List<Entry> files = children.stream()
                    .filter(entry -> !entry.isDirectory())
                    .sorted()
                    .toList();

            children = new ArrayList<>();
            children.addAll(directories);
            children.addAll(files);
            this.children = children;
            this.name = name;
            this.filePath = filePath;
            this.relativePath = relativePath;
            this.unmappedRelativePath = unmappedRelativePath;
            this.directory = directory;
        }

        public boolean canSelect() {
            return children.isEmpty();
        }

        public JComponent getMainComponent() {
            Color c = null;

            AssetPanel panel = treeModel.panel;

            String relativePath = this.relativePath;
            int dotIndex = relativePath.indexOf('.');
            if (dotIndex != -1) relativePath = relativePath.substring(0, dotIndex);

            if (panel.isMapped(relativePath)) {
                c = Color.GREEN;
            } else if (panel.getMetaSection().getMatchingPaths().contains(relativePath)) {
                c = Color.MAGENTA;
            } else if (panel.getMetaSection().getIgnoredPaths().contains(relativePath)) {
                c = Color.RED;
            } else if (panel.getMetaSection().getTransformedPaths().contains(relativePath)) {
                c = Color.CYAN;
            }

            JLabel label = new JLabel(this.name);
            label.setInheritsPopupMenu(true);
            if (c != null) {
                label.setIcon(new BufferedImageIcon(ColorUtils.getSolidImg(c)));
            }
            return label;
        }

        public JComponent getPreviewComponent() {
            File file = new File(filePath);
            if (
                    file.getName().endsWith(".png") ||
                            file.getName().endsWith(".tga")
            ) {
                BufferedImage image;

                try {
                    image = ImageIO.read(file);
                } catch (IOException e) {
                    image = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
                    Graphics g = image.getGraphics();
                    g.drawString("Unable to load image.", 0, 0);
                }

                if (treeModel.panel.isAssetAnimated(filePath)) {
                    AnimationMeta meta = treeModel.panel.getAnimationMeta(image, filePath);
                    if (meta != null) return new AnimatedTexturePreview(image, meta, relativePath);
                }

                return new TexturePreview(image, relativePath);
            } else if (
                    file.getName().endsWith(".ogg") ||
                            file.getName().endsWith(".mp3") ||
                            file.getName().endsWith(".wav") ||
                            file.getName().endsWith(".fsb")
            ) {
                return new SoundPreview(file, relativePath);
            } else if (
                    file.getName().endsWith(".json") ||
                            file.getName().endsWith(".mcmeta")
            ) {
                try {
                    return new JsonPreview(JsonParser.parseReader(
                            new FileReader(filePath)
                    ), relativePath);
                } catch (FileNotFoundException e) {
                    return null;
                }
            } else if (
                    file.getName().endsWith(".lang") ||
                            file.getName().endsWith(".txt") ||
                            file.getName().endsWith(".fsh") ||
                            file.getName().endsWith(".vsh") ||
                            file.getName().endsWith(".glsl") ||
                            file.getName().endsWith(".material")
            ) {
                return new TextPreview(file);
            } else if (file.getName().endsWith(".zip")) {
                return new ZipPreview(file);
            } else {
                return null;
            }
        }

        @Override
        public String toString() {
            return this.name;
        }

        @Override
        public int compareTo(Entry o) {
            return this.name.compareTo(o.name);
        }
    }
}
