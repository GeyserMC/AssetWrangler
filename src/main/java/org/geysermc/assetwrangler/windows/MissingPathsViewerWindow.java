package org.geysermc.assetwrangler.windows;

import org.geysermc.assetwrangler.Main;
import org.geysermc.assetwrangler.utils.Asset;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MissingPathsViewerWindow extends BaseWindow {
    public MissingPathsViewerWindow(MappingsWindow main) {
        super();
        this.setSize(500, 500);
        this.setResizable(false);
        this.setIconImage(Main.ICON_IMAGE);
        this.setTitle("Missing assets");

        this.setLayout(new LayoutManager() {
            public void addLayoutComponent(String name, Component comp) {}
            public void removeLayoutComponent(Component comp) {}
            public Dimension preferredLayoutSize(Container parent) {return null;}
            public Dimension minimumLayoutSize(Container parent) {return null;}

            @Override
            public void layoutContainer(Container parent) {
                synchronized (parent.getTreeLock()) {
                    for (Component component : parent.getComponents()) {
                        if (component instanceof JScrollPane scrollPane) {
                            scrollPane.setBounds(0, 0, 484, 435);
                        } else if (component instanceof JButton button) {
                            button.setBounds(0, 440, 484, 20);
                        }
                    }
                }
            }
        });

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new GridLayout(0, 1));

        for (String path : main.getJsonMappings().presentJavaPaths()) {
            Asset rootAsset = main.getJavaAssetPanel().getRootAsset();
            String prefix = main.getMappingsMeta().getJava().getRelativePath();
            if (!prefix.isEmpty()) prefix = prefix + "/";

            Asset asset = rootAsset.resolve(prefix + path);
            if (asset == null) { // It's missing, let's handle it
                JButton button = new JButton("Java: %s".formatted(path));

                JPopupMenu menu = new JPopupMenu();

                JMenuItem unmap = new JMenuItem("Delete mapping");
                unmap.addActionListener(e -> {
                    main.getJavaAssetPanel().unmap(path);
                    listPanel.remove(button);
                    listPanel.repaint();
                });
                menu.add(unmap);

                button.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseReleased(MouseEvent e) {
                        menu.show(e.getComponent(), e.getX(), e.getY());
                    }
                });

                listPanel.add(button);
            }
        }

        for (String path : main.getJsonMappings().presentBedrockPaths()) {
            Asset rootAsset = main.getBedrockAssetPanel().getRootAsset();
            String prefix = main.getMappingsMeta().getBedrock().getRelativePath();
            if (!prefix.isEmpty()) prefix = prefix + "/";

            Asset asset = rootAsset.resolve(prefix + path, true);
            if (asset == null) { // It's missing, let's handle it
                JButton button = new JButton("Bedrock: %s".formatted(path));

                JPopupMenu menu = new JPopupMenu();

                JMenuItem unmap = new JMenuItem("Delete mapping");
                unmap.addActionListener(e -> {
                    main.getBedrockAssetPanel().unmap(path);
                    listPanel.remove(button);
                    listPanel.repaint();
                });
                menu.add(unmap);

                button.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseReleased(MouseEvent e) {
                        menu.show(e.getComponent(), e.getX(), e.getY());
                    }
                });

                listPanel.add(button);
            }
        }

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        this.add(scrollPane);

        JButton done = new JButton("Done");
        done.addActionListener(e -> {
            MissingPathsViewerWindow.this.close();
        });
        this.add(done);

        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    @Override
    public void close() {
        this.dispose();
    }
}
