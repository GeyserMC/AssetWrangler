package org.geysermc.assetwrangler.sources;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.SneakyThrows;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.geysermc.assetwrangler.Main;
import org.geysermc.assetwrangler.utils.NetUtils;

import javax.net.ssl.HttpsURLConnection;
import javax.swing.*;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public abstract class GitHubAssetSource implements AssetSource {
    @Override
    public boolean downloadRequired(Path dataDirectory) {
        Path directory = dataDirectory.resolve("data/github/%s/%s".formatted(branch(), repo()));
        Path hashFile = dataDirectory.resolve("data/github/%s/%s.hash".formatted(branch(), repo()));
        try {
            Files.createDirectories(directory.getParent());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (Files.exists(directory)) {
            try {
                String latestHash = getLatestHash();

                return !latestHash.equals(Files.readString(hashFile));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return true;
    }

    @Override
    public boolean download(Path dataDirectory, JFrame parentFrame, Runnable callback, boolean update) throws IOException {
        if (update) {
            int choice = JOptionPane.showOptionDialog(
                    parentFrame,
                    "Would you like to update the source `%s`?".formatted(getKey()),
                    "Download Confirmation",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    Main.ICON, null, null
            );

            if (choice != JOptionPane.YES_OPTION) return false;
        }

        Path tempDownload = dataDirectory.resolve("tmp/github/%s/%s.zip".formatted(branch(), repo()));
        Path directory = dataDirectory.resolve("data/github/%s/%s".formatted(branch(), repo()));
        Path hashFile = dataDirectory.resolve("data/github/%s/%s.hash".formatted(branch(), repo()));
        Files.createDirectories(directory.getParent());
        Files.createDirectories(tempDownload.getParent());

        URL url = NetUtils.asUrl("https://github.com/%s/archive/refs/heads/%s.zip".formatted(repo(), branch()));

        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestProperty("User-Agent", NetUtils.USER_AGENT);
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(10000);

        connection.connect();

        Files.write(tempDownload, connection.getInputStream().readAllBytes());

        connection.getInputStream().close();

        Files.writeString(hashFile, getLatestHash());

        byte[] buffer = new byte[1024];
        ZipInputStream zis = new ZipInputStream(new FileInputStream(tempDownload.toFile()));
        ZipEntry zipEntry = zis.getNextEntry();
        while (zipEntry != null) {
            Path destFile = directory.resolve(zipEntry.getName());

            String destDirPath = directory.toAbsolutePath().toString();
            String destFilePath = destFile.toAbsolutePath().toString();

            if (!destFilePath.startsWith(destDirPath + File.separator)) {
                throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
            }

            if (zipEntry.isDirectory()) {
                if (!destFile.toFile().isDirectory() && !destFile.toFile().mkdirs()) {
                    throw new IOException("Failed to create directory " + destFile);
                }
            } else {
                Path parent = destFile.getParent();
                if (!parent.toFile().isDirectory() && !parent.toFile().mkdirs()) {
                    throw new IOException("Failed to create directory " + parent);
                }

                FileOutputStream fos = new FileOutputStream(destFile.toFile());
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
            }
            zipEntry = zis.getNextEntry();
        }

        zis.closeEntry();
        zis.close();

        callback.run();

        return true;
    }

    private String getLatestHash() throws IOException {
        URL url = NetUtils.asUrl("https://api.github.com/repos/%s/commits?sha=%s".formatted(repo(), branch()));

        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestProperty("Accept", "application/vnd.github+json");
        connection.setRequestProperty("X-GitHub-Api-Version", "2022-11-28");
        connection.setRequestProperty("User-Agent", NetUtils.USER_AGENT);
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);

        connection.connect();

        JsonArray arr = JsonParser.parseReader(new InputStreamReader(connection.getInputStream())).getAsJsonArray();

        connection.getInputStream().close();

        JsonObject latestCommitData = arr.get(0).getAsJsonObject();

        return latestCommitData.get("sha").getAsString();
    }

    @SneakyThrows
    @Override
    public Path[] getPaths(Path dataDirectory) {
        Path directory = dataDirectory.resolve("data/github/%s/%s/%s-%s".formatted(branch(), repo(), repo().substring(repo().indexOf('/')), branch()));
        Files.createDirectories(directory.getParent());
        return new Path[]{ modifyPath(directory) };
    }

    @Override
    public @Nullable String getDisabledMessage() {
        return null;
    }

    public abstract String repo();
    public Path modifyPath(Path path) { return path; }
    public String branch() { return "main"; }
}
