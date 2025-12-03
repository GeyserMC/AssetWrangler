package org.geysermc.assetwrangler.panels;

import lombok.Getter;
import lombok.ToString;
import org.geysermc.assetwrangler.Main;
import org.geysermc.assetwrangler.components.AnimatedLabel;
import org.geysermc.assetwrangler.components.MainComponentMenu;
import org.geysermc.assetwrangler.components.SoundPreview;
import org.geysermc.assetwrangler.treemodels.AssetTreeModel;
import org.geysermc.assetwrangler.utils.Asset;
import org.geysermc.assetwrangler.utils.JsonMappingsMeta;
import org.geysermc.assetwrangler.utils.AnimationMeta;
import org.geysermc.assetwrangler.windows.AssetViewerWindow;
import org.geysermc.assetwrangler.windows.MappingsWindow;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeModelListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public abstract class AssetPanel extends BasePanel {
    @Getter
    protected final AssetViewerWindow main;
    private final JTree tree;
    @Getter
    private final Asset rootAsset;
    @Getter
    private final String rootName;
    @Getter
    private final boolean isForMapping;

    public AssetPanel(AssetViewerWindow main, String rootName, boolean isForMapping, Path... inputs) {
        super();
        this.main = main;
        this.rootName = rootName;
        this.isForMapping = isForMapping;

        Asset a = null;
        for (Path path : inputs) {
            Asset asset = new Asset(path, path, path.getFileName().toString(), path.toFile().isDirectory());
            if (a == null) a = asset;
            else a.combine(asset);
        }
        this.rootAsset = a;

        this.setLayout(new BorderLayout());
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        tree = new JTree(new AssetTreeModel(this, rootAsset.resolve(getMetaSection().getRelativePath()), rootName));
        tree.setBorder(new EmptyBorder(0, 0, 0, 0));
        TreeSelectionModel selectionModel = new DefaultTreeSelectionModel();
        selectionModel.setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        tree.setSelectionModel(selectionModel);
        tree.setCellRenderer((tree1, value, selected, expanded, leaf, row, hasFocus) -> {
            AssetTreeModel.Entry entry = (AssetTreeModel.Entry) value;

            return entry.getMainComponent();
        });

        if (isForMapping) {
            tree.addMouseListener(new MouseAdapter() {
                public void mouseReleased(MouseEvent e) {
                    if (e.isPopupTrigger()) {
                        TreePath treePath = tree.getClosestPathForLocation(e.getX(), e.getY());
                        if (treePath != null) {
                            tree.setSelectionPath(treePath);
                            AssetTreeModel.Entry entry = (AssetTreeModel.Entry) treePath.getLastPathComponent();

                            if (entry.canSelect()) {
                                MainComponentMenu menu = new MainComponentMenu(getMetaSection(), entry);
                                menu.show(e.getComponent(), e.getX(), e.getY());
                            }
                        }
                    }
                }
            });
        }

        tree.addTreeSelectionListener(e -> {
            for (TreePath treePath : e.getPaths()) {
                AssetTreeModel.Entry entry = ((AssetTreeModel.Entry) treePath.getLastPathComponent());
                if (!entry.canSelect()) {
                    tree.removeSelectionPath(treePath);
                }
            }
            if (tree.getSelectionCount() > 1) {
                JLabel label = new JLabel("%d items selected".formatted(tree.getSelectionCount()));
                label.setFont(label.getFont().deriveFont(28f));
                setPreviewComponent(main, label);
            } else setPreviewComponent(main, ((AssetTreeModel.Entry) e.getPath().getLastPathComponent()).getPreviewComponent());
        });
        panel.add(tree);

        this.add(scrollPane);
    }

    public void redraw() {
        List<String> expandedPaths = new ArrayList<>();
        for (int i = 0; i < tree.getRowCount(); i++){
            if (tree.isExpanded(i)) {
                expandedPaths.add(((AssetTreeModel.Entry) tree.getPathForRow(i).getLastPathComponent()).getRelativePath());
            }
        }

        tree.setModel(new AssetTreeModel(this, this.rootAsset.resolve(getMetaSection().getRelativePath()), this.rootName));

        for (int i = 0; i < tree.getRowCount(); i++){
            if (expandedPaths.contains(((AssetTreeModel.Entry) tree.getPathForRow(i).getLastPathComponent()).getRelativePath())) {
                tree.expandRow(i);
            }
        }
    }

    public List<String> getSelectedPaths() {
        TreePath[] paths = tree.getSelectionPaths();
        if (paths == null) return List.of();

        return Arrays.stream(paths)
                .map(path -> (AssetTreeModel.Entry) path.getLastPathComponent())
                .map(AssetTreeModel.Entry::getRelativePath)
                .map(str -> {
                    int dotIndex = str.indexOf('.');
                    if (dotIndex != -1) return str.substring(0, dotIndex);
                    return str;
                })
                .toList();
    }

    public List<String> getUnmappedSelectedPaths() {
        TreePath[] paths = tree.getSelectionPaths();
        if (paths == null) return List.of();

        return Arrays.stream(paths)
                .map(path -> (AssetTreeModel.Entry) path.getLastPathComponent())
                .map(AssetTreeModel.Entry::getUnmappedRelativePath)
                .map(str -> {
                    int dotIndex = str.indexOf('.');
                    if (dotIndex != -1) return str.substring(0, dotIndex);
                    return str;
                })
                .toList();
    }

    public abstract boolean isMapped(String path);
    public abstract void setPreviewComponent(AssetViewerWindow main, JComponent component);
    public abstract JsonMappingsMeta.Section getMetaSection();
    public abstract boolean isAssetAnimated(String filePath);
    public abstract AnimationMeta getAnimationMeta(BufferedImage img, String filePath) throws IOException;
}
