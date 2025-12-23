package org.geysermc.assetwrangler.sources.java;

import com.google.gson.*;
import lombok.Getter;
import lombok.SneakyThrows;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.geysermc.assetwrangler.Main;
import org.geysermc.assetwrangler.sources.AssetSource;
import org.geysermc.assetwrangler.utils.DialogUtils;
import org.geysermc.assetwrangler.utils.WebUtils;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.List;
import java.util.zip.ZipFile;

public abstract class JavaVanillaAssetSource implements AssetSource {
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting().create();

    private static final String RESOLVED_DATA_PATH = "data/java/%s";
    private static final String RESOLVED_TMP_DIRECTORY = "tmp/java/%s.jar";

    protected VersionManifest versionManifest = null;

    public abstract String getLatestVersionTag();

    private Path getStoredVersionPath(Path dataDirectory) throws IOException {
        Path dataPath = dataDirectory.resolve("data/%s.version".formatted(getKey()));

        if (Files.notExists(dataPath)) {
            Files.writeString(dataPath, getLatestVersionTag());
        }

        return dataPath;
    }

    @Override
    public void download(Path dataDirectory, JFrame parent, Runnable callback, boolean update) throws IOException {
        boolean shouldDownload = false;

        fetchVersionManifestIfRequired();
        Path storedVersion = getStoredVersionPath(dataDirectory);
        if (Files.exists(storedVersion)) {
            String currentVersion = Files.readString(storedVersion);

            if (!currentVersion.equals(getLatestVersionTag())) {
                shouldDownload = DialogUtils.yesOrNo(
                        null, "Options, options...",
                        "A new version of java assets can be downloaded! Would you like to get the new version?"
                );
            }
        } else {
            shouldDownload = true;
            Files.writeString(storedVersion, getLatestVersionTag());
        }

        if (!shouldDownload || Files.notExists(storedVersion)) return;

        JDialog dialog = new JDialog(parent);
        Main.registerForFrame(dialog);

        JPanel panel = new JPanel();
        JScrollPane scrollPane = new JScrollPane(panel);
        JTextArea textArea = new JTextArea();
        textArea.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        textArea.setText("Beginning download...");
        textArea.setEditable(false);
        textArea.setCaretColor(new Color(0, 0, 0, 0));
        textArea.setAutoscrolls(true);
        panel.add(textArea);

        dialog.add(scrollPane);

        dialog.setTitle("Downloading %s...".formatted(getLatestVersionTag()));
        dialog.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        dialog.setSize(600, 200);
        dialog.setLocationRelativeTo(parent);

        dialog.setVisible(true);

        Thread t = new Thread(() -> {
            try {
                SwingUtilities.invokeLater(() -> {
                    textArea.setText(textArea.getText() + "\nFetching version...");
                    textArea.setCaretPosition(textArea.getDocument().getLength());
                });
                String currentVersion = Files.readString(storedVersion);
                Path rootPath = dataDirectory.resolve(RESOLVED_DATA_PATH.formatted(currentVersion));
                Path tmpPath = dataDirectory.resolve(RESOLVED_TMP_DIRECTORY.formatted(currentVersion));

                String latestInfoURL = "";
                for (Version version : versionManifest.getVersions()) {
                    if (version.getId().equals(currentVersion)) {
                        latestInfoURL = version.getUrl();
                        break;
                    }
                }

                if (latestInfoURL.isEmpty()) {
                    throw new IOException("Unable to find a valid version!");
                }

                // Get the individual version manifest
                VersionInfo versionInfo = GSON.fromJson(WebUtils.getBody(latestInfoURL), VersionInfo.class);

                // Get the client jar for use when downloading the en_us locale
                VersionDownload clientJarInfo = versionInfo.getDownloads().get("client");

                JsonObject assets = JsonParser.parseString(WebUtils.getBody(versionInfo.getAssetIndex().getUrl())).getAsJsonObject().get("objects").getAsJsonObject();

                if (rootPath.getParent() != null) Files.createDirectories(rootPath.getParent());

                if (Files.exists(tmpPath)) Files.delete(tmpPath);
                Files.createDirectories(tmpPath.getParent());
                SwingUtilities.invokeLater(() -> {
                    textArea.setText(textArea.getText() + "\nDownloading client jar...");
                    textArea.setCaretPosition(textArea.getDocument().getLength());
                });
                Files.copy(new URI(clientJarInfo.url).toURL().openStream(), tmpPath);

                ZipFile file = new ZipFile(tmpPath.toFile());

                SwingUtilities.invokeLater(() -> {
                    textArea.setText(textArea.getText() + "\nExtracting client jar assets...");
                    textArea.setCaretPosition(textArea.getDocument().getLength());
                });
                file.stream().filter(entry -> !entry.isDirectory() && entry.getName().startsWith("assets"))
                        .forEach(entry -> {
                            try {
                                Path destPath = rootPath.resolve(entry.getName());
                                Files.createDirectories(destPath.getParent());
                                if (Files.exists(destPath)) Files.delete(destPath);
                                Files.copy(file.getInputStream(entry), destPath);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });
                file.close();

                SwingUtilities.invokeLater(() -> {
                    textArea.setText(textArea.getText() + "\nDownloading extra assets...");
                    textArea.setCaretPosition(textArea.getDocument().getLength());
                });

                for (Map.Entry<String, JsonElement> entry : assets.entrySet()) {
                    Asset asset = GSON.fromJson(entry.getValue(), Asset.class);

                    String bytes = asset.hash.substring(0, 2);

                    Path destPath = rootPath.resolve("assets/" + entry.getKey());

                    Files.createDirectories(destPath.getParent());

                    if (Files.exists(destPath)) Files.delete(destPath); // We prefer these over existing ones

                    Files.copy(
                            new URI("https://resources.download.minecraft.net/%s/%s"
                                    .formatted(bytes, asset.hash)).toURL().openStream(), destPath
                    );
                }

                SwingUtilities.invokeLater(() -> {
                    dialog.setVisible(false);
                    dialog.dispose();
                    callback.run();
                });
            } catch (IOException | URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });

        t.start();
    }

    private void fetchVersionManifestIfRequired() {
        if (versionManifest != null) return;

        try {
            versionManifest = GSON.fromJson(
                    WebUtils.getBody("https://launchermeta.mojang.com/mc/game/version_manifest.json"),
                    VersionManifest.class
            );
        } catch (IOException e) {
            versionManifest = new VersionManifest();
        }
    }

    @SneakyThrows
    @Override
    public Path[] getPaths(Path dataDirectory) {
        Path storedVersion = getStoredVersionPath(dataDirectory);
        if (Files.notExists(storedVersion)) return new Path[0];

        String currentVersion = Files.readString(storedVersion);
        Path rootPath = dataDirectory.resolve(RESOLVED_DATA_PATH.formatted(currentVersion));

        return new Path[]{rootPath.resolve("assets/minecraft")};
    }

    @Override
    public @Nullable String getDisabledMessage() {
        return null;
    }

    @Override
    public Type getType() {
        return Type.JAVA;
    }

    @Getter
    public static class VersionManifest {
        private LatestVersion latest;

        private List<Version> versions;
    }

    @Getter
    public static class LatestVersion {
        private String release;

        private String snapshot;
    }

    @Getter
    static class Version {
        private String id;

        private String type;

        private String url;

        private String time;

        private String releaseTime;
    }

    @Getter
    static class VersionInfo {
        private String id;

        private String type;

        private String time;

        private String releaseTime;

        private AssetIndex assetIndex;

        private Map<String, VersionDownload> downloads;
    }

    @Getter
    static class VersionDownload {
        private String sha1;

        private int size;

        private String url;
    }

    @Getter
    static class AssetIndex {
        private String id;

        private String sha1;

        private int size;

        private int totalSize;

        private String url;
    }

    @Getter
    public static class Asset {
        private String hash;

        private int size;
    }
}
