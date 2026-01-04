package org.geysermc.assetwrangler.panels;

import com.google.gson.JsonParser;
import org.geysermc.assetwrangler.Main;
import org.geysermc.assetwrangler.sources.AssetSource;
import org.geysermc.assetwrangler.utils.AnimationMeta;
import org.geysermc.assetwrangler.utils.JsonMappingsMeta;
import org.geysermc.assetwrangler.windows.AssetViewerWindow;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class JavaAssetPanel extends AssetPanel {
    public JavaAssetPanel(AssetViewerWindow main, AssetSource assetSource, boolean isForMapping) {
        super(main, "Java Assets", isForMapping, assetSource.getPaths(Main.DATA_FOLDER));
    }

    @Override
    public boolean isMapped(String path) {
        return main.isJavaMapped(path);
    }

    @Override
    public void setPreviewComponent(AssetViewerWindow main, JComponent component) {
        main.setJavaPreviewComponent(component);
    }

    @Override
    public JsonMappingsMeta.Section getMetaSection() {
        return main.getJavaMeta();
    }

    @Override
    public boolean isAssetAnimated(String filePath) {
        File mcMetaFile = new File(filePath + ".mcmeta");
        if (mcMetaFile.exists()) {
            try {
                return JsonParser.parseReader(new FileReader(mcMetaFile)).getAsJsonObject().has("animation");
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return false;
    }

    @Override
    public AnimationMeta getAnimationMeta(BufferedImage img, String filePath) {
        try {
            return AnimationMeta.fromJavaJson(img, JsonParser.parseReader(
                    new FileReader(filePath + ".mcmeta")
            ).getAsJsonObject());
        } catch (IOException e) {
            return null;
        }
    }
}
