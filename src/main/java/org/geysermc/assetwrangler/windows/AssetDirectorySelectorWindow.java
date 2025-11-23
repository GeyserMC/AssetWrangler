package org.geysermc.assetwrangler.windows;

import org.geysermc.assetwrangler.panels.AssetPanel;
import org.geysermc.assetwrangler.treemodels.AssetTreeModel;
import org.geysermc.assetwrangler.treemodels.DirectoryAssetTreeModel;

import javax.swing.*;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;

public class AssetDirectorySelectorWindow extends JFrame {
    private String selectedPath = "";

    public AssetDirectorySelectorWindow(AssetPanel panel, Runnable onComplete) {
        this.setSize(500, 500);
        this.setLayout(new LayoutManager() {
            public void addLayoutComponent(String name, Component comp) {}
            public void removeLayoutComponent(Component comp) {}
            public Dimension preferredLayoutSize(Container parent) {return null;}
            public Dimension minimumLayoutSize(Container parent) {return null;}

            @Override
            public void layoutContainer(Container parent) {
                synchronized (parent.getTreeLock()) {
                    for (Component component : parent.getComponents()) {
                        if (component instanceof JTree tree) {
                            tree.setBounds(0, 0, 484, 435);
                        } else if (component instanceof JScrollPane scrollPane) {
                            scrollPane.setBounds(0, 0, 484, 435);
                        } else if (component instanceof JButton button) {
                            button.setBounds(0, 440, 484, 20);
                        }
                    }
                }
            }
        });
        JTree tree = new JTree();
        tree.setModel(new DirectoryAssetTreeModel(panel, panel.getRootAsset(), panel.getRootName()));
        TreeSelectionModel selectionModel = new DefaultTreeSelectionModel();
        selectionModel.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setSelectionModel(selectionModel);
        tree.setCellRenderer((tree1, value, selected, expanded, leaf, row, hasFocus) -> {
            AssetTreeModel.Entry entry = (AssetTreeModel.Entry) value;

            return entry.getMainComponent();
        });

        tree.addTreeSelectionListener(e -> {
            selectedPath = ((AssetTreeModel.Entry) e.getPath().getLastPathComponent()).getRelativePath();
        });

        JScrollPane pane = new JScrollPane(tree);
        this.add(pane);

        JButton done = new JButton("Done");
        done.addActionListener(e -> {
            panel.getMetaSection().setRelativePath(selectedPath);
            this.dispose();
            onComplete.run();
        });
        this.add(done);

        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
}
