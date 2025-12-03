package org.geysermc.assetwrangler.treemodels;

import lombok.Getter;
import org.geysermc.assetwrangler.components.AnimatedLabel;
import org.geysermc.assetwrangler.components.SoundPreview;
import org.geysermc.assetwrangler.panels.AssetPanel;
import org.geysermc.assetwrangler.utils.Asset;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.TreeModelListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

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
            JLabel label = new JLabel(this.name);
            label.setInheritsPopupMenu(true);
            return label;
        }

        public JComponent getPreviewComponent() {
            File file = new File(filePath);
            if (
                    file.getName().endsWith(".png") ||
                            file.getName().endsWith(".tga")
            ) {
                if (treeModel.panel.isAssetAnimated(filePath)) {
                    try {
                        BufferedImage image = ImageIO.read(file);
                        return new AnimatedLabel(
                                image, treeModel.panel.getAnimationMeta(image, filePath),
                                relativePath
                        );
                    } catch (IOException e) {
                        e.printStackTrace();
                        return new JLabel("Error reading image.");
                    }
                }
                StringBuilder label = new StringBuilder("<html>");
                label.append("Path: ");
                label.append(relativePath);
                Icon icon;
                try {
                    BufferedImage image = ImageIO.read(file);

                    float scaleX = 256f / image.getWidth();
                    float scaleY = 256f / image.getHeight();

                    float scale;
                    int xOffset = 0, yOffset = 0;
                    if (scaleX > scaleY) {
                        scale = scaleY;
                        xOffset = (256 - image.getWidth()) / 2;
                    } else if (scaleX < scaleY) {
                        scale = scaleX;
                        yOffset = (256 - image.getHeight()) / 2;
                    } else {
                        scale = scaleX;
                    }

                    label.append("<br/>Resolution: %dx%d".formatted(image.getWidth(), image.getHeight()));
                    BufferedImage scaledImage = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
                    Graphics2D graphics = (Graphics2D) scaledImage.getGraphics();
                    graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
                    graphics.drawImage(image, xOffset, yOffset, (int) (image.getWidth() * scale), (int) (image.getHeight() * scale), null);
                    icon = new ImageIcon(scaledImage);
                } catch (IOException e) {
                    e.printStackTrace();
                    icon = FileSystemView.getFileSystemView().getSystemIcon(file);
                }



                label.append("</html>");
                JLabel jLabel = new JLabel(label.toString(), icon, SwingConstants.LEFT);
                jLabel.setHorizontalTextPosition(JLabel.RIGHT);
                jLabel.setVerticalTextPosition(JLabel.TOP);
                return jLabel;
            } else if (
                    file.getName().endsWith(".ogg") ||
                            file.getName().endsWith(".mp3") ||
                            file.getName().endsWith(".wav") ||
                            file.getName().endsWith(".fsb")
            ) {
                return new SoundPreview(file, relativePath);
            } else if (
                    file.getName().endsWith(".json") ||
                            file.getName().endsWith(".lang") ||
                            file.getName().endsWith(".txt") ||
                            file.getName().endsWith(".fsh") ||
                            file.getName().endsWith(".vsh") ||
                            file.getName().endsWith(".glsl") ||
                            file.getName().endsWith(".material") ||
                            file.getName().endsWith(".mcmeta")
            ) {
                JPanel panel = new JPanel();
                panel.setLayout(new GridBagLayout());
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.weightx = 1;
                gbc.fill = GridBagConstraints.BOTH;
                gbc.gridwidth = GridBagConstraints.REMAINDER;
                JTextArea textArea = new JTextArea();
                textArea.setEditable(false);
                try {
                    textArea.setText(Files.readString(file.toPath()));
                } catch (IOException e) {
                    e.printStackTrace();
                    textArea.setText("Error reading file.");
                }
                panel.add(textArea, gbc);
                JScrollPane scrollPane = new JScrollPane(panel);
                scrollPane.getVerticalScrollBar().setUnitIncrement(20);
                return scrollPane;
            } else if (file.getName().endsWith(".zip")) {
                JPanel panel = new JPanel();
                panel.setLayout(new GridBagLayout());
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.weightx = 1;
                gbc.fill = GridBagConstraints.BOTH;
                gbc.gridwidth = GridBagConstraints.REMAINDER;

                JLabel filesText = new JLabel("Files:");
                filesText.setFont(filesText.getFont().deriveFont(18f));
                panel.add(filesText, gbc);

                JTextArea textArea = new JTextArea();
                textArea.setEditable(false);
                textArea.setBackground(new Color(28, 30, 37));
                try {
                    ZipFile zipFile = new ZipFile(file);
                    String builder = String.join("\n", zipFile.stream().filter(entry -> !entry.isDirectory()).map(ZipEntry::getName).toList());
                    textArea.append(builder);
                    zipFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    textArea.setText("Error reading file.");
                }
                panel.add(textArea, gbc);
                JScrollPane scrollPane = new JScrollPane(panel);
                scrollPane.getVerticalScrollBar().setUnitIncrement(20);
                return scrollPane;
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
