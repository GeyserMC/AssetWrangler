package org.geysermc.assetwrangler.windows;

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
            if (assetSource.downloadRequired(Main.DATA_FOLDER)) {
                assetSource.download(Main.DATA_FOLDER, (JFrame) this, callback, true);
            } else callback.run();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    (Component) this,
                    "Something went wrong while fetching " + assetSource.getName() + " assets",
                    "Error! Error!",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
