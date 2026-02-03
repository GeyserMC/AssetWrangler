package org.geysermc.assetwrangler.windows;

import org.geysermc.assetwrangler.BuildConstants;
import org.geysermc.assetwrangler.Logger;
import org.geysermc.assetwrangler.Main;
import org.geysermc.assetwrangler.utils.JButtonUtils;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;

public class InfoWindow extends JDialog {
    private static final List<String> RNG_STRINGS;
    private static final LinkedHashMap<String, String> URL_BUTTONS = new LinkedHashMap<>();
    private static final Random RANDOM = new Random();

    static {
        String rngMessages;
        try {
            InputStream stream = InfoWindow.class.getResourceAsStream("/messages.txt");
            if (stream == null) throw new IOException("messages.txt was not found.");
            rngMessages = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            stream.close();
        } catch (IOException e) {
            Logger.error("Unable to load funny messages :(", e);
            rngMessages = "Unable to load these funny messages... Please report this sad occasion.";
        }
        RNG_STRINGS = Arrays.stream(rngMessages.split("\n"))
                .filter(str -> !str.startsWith("//"))
                .filter(str -> !str.isBlank())
                .toList();

        URL_BUTTONS.put("Discord Support Server", "https://discord.gg/GeyserMC/");
        URL_BUTTONS.put("GeyserMC Site", "https://geysermc.org/");
    }

    public InfoWindow(JFrame parent) {
        super(parent);
        Main.registerForFrame(this);
        this.setLayout(new Layout());

        this.add(new JLabel(Main.ICON));

        JTextArea informationArea = new JTextArea();
        informationArea.setBackground(new Color(0, 0, 0, 0));
        if (Main.isDarkMode()) {
            informationArea.setDisabledTextColor(Color.WHITE);
        } else {
            informationArea.setDisabledTextColor(Color.BLACK);
        }
        Main.registerDarkModeHook(this, (darkMode) -> {
            if (darkMode) {
                informationArea.setDisabledTextColor(Color.WHITE);
            } else {
                informationArea.setDisabledTextColor(Color.BLACK);
            }
        });
        informationArea.setOpaque(false);
        informationArea.setEnabled(false);
        informationArea.setLineWrap(true);
        informationArea.setWrapStyleWord(true);
        informationArea.append(BuildConstants.getInstance().getName());
        informationArea.append(" (");
        informationArea.append(BuildConstants.getInstance().getVersion());
        informationArea.append(")\nAuthors: ");
        informationArea.append(BuildConstants.getInstance().getAuthors());
        informationArea.append("\n\n");
        informationArea.append(RNG_STRINGS.get(RANDOM.nextInt(RNG_STRINGS.size())));
        this.add(informationArea);

        for (Map.Entry<String, String> entry : URL_BUTTONS.entrySet()) {
            this.add(JButtonUtils.linkButton(entry.getKey(), entry.getValue()));
        }

        this.setSize(550, 280);
        this.setResizable(false);
        this.setLocationRelativeTo(parent);
        this.setVisible(true);
        this.setTitle(BuildConstants.getInstance().getName() + " Information");
    }

    private static class Layout implements LayoutManager {
        @Override public void addLayoutComponent(String name, Component comp) {}
        @Override public void removeLayoutComponent(Component comp) {}
        @Override public Dimension preferredLayoutSize(Container parent) {return null;}
        @Override public Dimension minimumLayoutSize(Container parent) {return null;}

        @Override
        public void layoutContainer(Container parent) {
            synchronized (parent.getTreeLock()) {
                int panelsHeight = parent.getHeight() - 30;
                int textAreaWidth = parent.getWidth() - panelsHeight;
                int buttonWidth = parent.getWidth() / URL_BUTTONS.size();

                for (Component component : parent.getComponents()) {
                    if (component instanceof JLabel icon) {
                        icon.setBounds(0, 0, panelsHeight, panelsHeight);
                    } else if (component instanceof JTextArea area) {
                        area.setBounds(panelsHeight, 0, textAreaWidth, panelsHeight);
                    } else if (component instanceof JButton link) {
                        int index = URL_BUTTONS.sequencedKeySet().stream().toList().indexOf(link.getText());
                        link.setBounds(index * buttonWidth, panelsHeight, buttonWidth, 30);
                    }
                }
            }
        }
    }
}
