package org.geysermc.assetwrangler.windows;

import org.geysermc.assetwrangler.utils.JsonMappingsMeta;

import javax.swing.*;

public interface AssetViewerWindow {
    void setJavaPreviewComponent(JComponent component);
    void setBedrockPreviewComponent(JComponent component);

    boolean isJavaMapped(String path);
    boolean isBedrockMapped(String path);

    JsonMappingsMeta.Section getJavaMeta();
    JsonMappingsMeta.Section getBedrockMeta();
}
