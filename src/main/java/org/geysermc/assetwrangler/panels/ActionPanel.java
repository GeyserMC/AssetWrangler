package org.geysermc.assetwrangler.panels;

import org.geysermc.assetwrangler.windows.MappingsWindow;

import javax.swing.*;

public class ActionPanel extends BasePanel {
    public ActionPanel(MappingsWindow main) {
        super();

        JButton mapButton = new JButton("Map");
        mapButton.addActionListener(e -> main.map());
        this.add(mapButton);

        JButton matchButton = new JButton("Match");
        matchButton.addActionListener(e -> main.match());
        this.add(matchButton);

        JButton ignoreButton = new JButton("Ignore");
        ignoreButton.addActionListener(e -> main.ignore());
        this.add(ignoreButton);

        JButton transformedButton = new JButton("Transformed");
        transformedButton.addActionListener(e -> main.transformed());
        this.add(transformedButton);
    }
}
