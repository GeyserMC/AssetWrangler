package org.geysermc.assetwrangler;

import com.formdev.flatlaf.intellijthemes.FlatArcDarkIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatArcIJTheme;
import com.jthemedetecor.OsThemeDetector;
import org.geysermc.assetwrangler.config.Config;
import org.geysermc.assetwrangler.sources.AssetSources;
import org.geysermc.assetwrangler.sources.bedrock.*;
import org.geysermc.assetwrangler.sources.java.JavaLocalAssetSource;
import org.geysermc.assetwrangler.sources.java.JavaReleaseAssetSource;
import org.geysermc.assetwrangler.sources.java.JavaSnapshotAssetSource;
import org.geysermc.assetwrangler.windows.InfoWindow;
import org.geysermc.assetwrangler.windows.SourceSelectWindow;
import org.geysermc.assetwrangler.windows.StartUpWindow;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Main {
    private static final YamlConfigurationLoader CONFIG_LOADER;
    public static final Config CONFIG;
    public static final Path DATA_FOLDER;
    public static final BufferedImage ICON_IMAGE;
    public static final Icon ICON;
    public static final boolean IS_WINDOWS = System.getProperty("os.name").toLowerCase().contains("windows");
    public static Path mappingFile;

    private static final List<Runnable> DARK_MODE_HOOKS = new CopyOnWriteArrayList<>();

    public static void registerForFrame(Window frame) {
        Runnable runnable = () -> {
            SwingUtilities.updateComponentTreeUI(frame);
        };
        DARK_MODE_HOOKS.add(runnable);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                DARK_MODE_HOOKS.remove(runnable);
            }
        });
    }

    static {
        if (Main.IS_WINDOWS) {
            DATA_FOLDER = Path.of(System.getenv("APPDATA")).resolve("AssetMapper");
        } else {
            DATA_FOLDER = Path.of(System.getProperty("user.home")).resolve("AssetMapper");
        }

        try {
            Files.createDirectories(DATA_FOLDER);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        CONFIG_LOADER = YamlConfigurationLoader.builder()
                .path(DATA_FOLDER.resolve("config.yml"))
                .indent(4)
                .nodeStyle(NodeStyle.BLOCK)
                .build();

        Config config1;
        try {
            CommentedConfigurationNode node = CONFIG_LOADER.load();
            if (node.isNull()) {
                CommentedConfigurationNode newRoot = CommentedConfigurationNode.root(CONFIG_LOADER.defaultOptions());
                newRoot.set(new Config());
                CONFIG_LOADER.save(newRoot);
                config1 = newRoot.get(Config.class);
            } else {
                config1 = node.get(Config.class);
            }
        } catch (ConfigurateException e) {
            config1 = new Config();
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    null, "The default config will be in use since an error occured when reading the default config.",
                    "Error! Error!", JOptionPane.ERROR_MESSAGE
            );
        }
        CONFIG = config1;
    }

    public static void saveConfig() {
        try {
            CommentedConfigurationNode newRoot = CommentedConfigurationNode.root(CONFIG_LOADER.defaultOptions());
            newRoot.set(CONFIG);
            CONFIG_LOADER.save(newRoot);
        } catch (ConfigurateException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    null, "An error occured while saving the config.",
                    "Error! Error!", JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public static void main(String[] args) {
        AssetSources.registerSource(new JavaLocalAssetSource());
        AssetSources.registerSource(new JavaReleaseAssetSource());
        AssetSources.registerSource(new JavaSnapshotAssetSource());

        AssetSources.registerSource(new BedrockReleaseAssetSource());
        AssetSources.registerSource(new BedrockPreviewAssetSource());
        AssetSources.registerSource(new BedrockLocalAssetSource());
        AssetSources.registerSource(new BedrockSampleAssetSource());

        final OsThemeDetector detector = OsThemeDetector.getDetector();
        detector.registerListener(isDark -> {
            SwingUtilities.invokeLater(() -> {
                setDarkMode(isDark);
            });
        });
        setDarkMode(detector.isDark());

        if (
                CONFIG.bedrockAssetSourceKey().isBlank() ||
                        CONFIG.javaAssetSourceKey().isBlank()
        ) {
            new SourceSelectWindow(StartUpWindow::new);
        } else {
            new StartUpWindow();
        }
    }

    private static void setDarkMode(boolean isDark) {
        if (isDark) {
            FlatArcDarkIJTheme.setup();
        } else {
            FlatArcIJTheme.setup();
        }

        DARK_MODE_HOOKS.forEach(Runnable::run);
    }

    static {
        try {
            ICON_IMAGE = ImageIO.read(InfoWindow.class.getResourceAsStream("/GeyserLogo.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ICON = new ImageIcon(ICON_IMAGE);
    }
}
