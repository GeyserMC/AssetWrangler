package org.geysermc.assetwrangler.windows;

import org.geysermc.assetwrangler.Logger;
import org.geysermc.assetwrangler.Main;
import org.geysermc.assetwrangler.actions.ActionManager;
import org.geysermc.assetwrangler.sources.AssetSource;
import org.geysermc.assetwrangler.utils.JsonMappingsMeta;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public interface AssetViewerWindow {
    void setJavaPreviewComponent(JComponent component);
    void setBedrockPreviewComponent(JComponent component);

    boolean isJavaMapped(String path);
    boolean isBedrockMapped(String path);

    JsonMappingsMeta.Section getJavaMeta();
    JsonMappingsMeta.Section getBedrockMeta();

    ActionManager getActionManager();

    boolean isSavesRequired();
    void markSave();
    void unmarkSave();

    default void checkAssetSource(AssetSource assetSource, Runnable callback) {
        try {
            assetSource.download(Main.DATA_FOLDER, (JFrame) this, callback, true);
        } catch (IOException e) {
            Logger.errorWithDialog(
                    "Something went wrong while fetching %s (%s) assets"
                            .formatted(assetSource.getName(), assetSource.getKey()),
                    e, (Component) this
            );
        }
    }
}
