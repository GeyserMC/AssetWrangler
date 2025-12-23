package org.geysermc.assetwrangler.sources;

import lombok.SneakyThrows;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.geysermc.assetwrangler.Main;
import org.geysermc.assetwrangler.utils.DialogUtils;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Collectors;

public abstract class LocalAssetSource implements AssetSource {
    @Override
    public void download(Path dataDirectory, JFrame parent, Runnable callback, boolean update) throws IOException {
        Path storagePath = dataDirectory.resolve("data/%s.path".formatted(getKey()));
        Files.createDirectories(storagePath.getParent());

        if (Files.exists(storagePath) && update) {
            callback.run();
            return;
        }

        JFileChooser chooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        chooser.setDialogTitle("Select %s Asset Directory".formatted(getType().getName()));
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setMultiSelectionEnabled(true);
        chooser.setVisible(true);
        int chosen = chooser.showOpenDialog(parent);

        if (chosen == JFileChooser.APPROVE_OPTION) {
            Files.writeString(
                    storagePath,
                    Arrays.stream(chooser.getSelectedFiles())
                            .map(File::getAbsolutePath)
                            .collect(Collectors.joining(File.pathSeparator))
            );
        }

        callback.run();
    }

    @SneakyThrows
    @Override
    public Path[] getPaths(Path dataDirectory) {
        Path storagePath = dataDirectory.resolve("data/%s.path".formatted(getKey()));
        Files.createDirectories(storagePath.getParent());

        return Arrays.stream(Files.readString(storagePath).split(File.pathSeparator))
                .map(Path::of).toArray(Path[]::new);
    }

    @Override
    public @Nullable String getDisabledMessage() {
        return null;
    }
}
