package org.geysermc.assetwrangler.panels;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.geysermc.assetwrangler.Main;
import org.geysermc.assetwrangler.sources.AssetSource;
import org.geysermc.assetwrangler.utils.AnimationMeta;
import org.geysermc.assetwrangler.utils.JsonMappingsMeta;
import org.geysermc.assetwrangler.utils.JsonUtils;
import org.geysermc.assetwrangler.utils.NinesliceData;
import org.geysermc.assetwrangler.windows.AssetViewerWindow;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class JavaAssetPanel extends AssetPanel {
    public JavaAssetPanel(AssetViewerWindow main, AssetSource assetSource, boolean isForMapping) {
        super(main, "Java Assets", isForMapping, assetSource.getPaths(Main.DATA_FOLDER));
    }

    @Override
    public boolean isMapped(String path) {
        return main.isJavaMapped(path);
    }

    @Override
    public void unmap(String path) {
        List<String> bedrockOutputs = main.getJsonMappings().map(path);

        main.getActionManager().doAction(() -> {
            main.getJsonMappings().removeJava(path);
            main.refreshView();
        }, () -> {
            main.getJsonMappings().map(path, bedrockOutputs);
            main.refreshView();
        }, true);
    }

    @Override
    public boolean canSingleMap() {
        return true;
    }

    @Override
    public List<String> lookupMapping(String key) {
        return main.getJsonMappings().map(key);
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

    @Override
    public boolean isAssetNinesliced(String filePath) {
        File mcMetaFile = new File(filePath + ".mcmeta");
        if (mcMetaFile.exists()) {
            try {
                return JsonParser.parseReader(new FileReader(mcMetaFile)).getAsJsonObject().has("gui");
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return false;
    }

    @Override
    public NinesliceData getNineslicedMeta(BufferedImage img, String filePath) {
        File mcMetaFile = new File(filePath + ".mcmeta");
        try {
            JsonObject scalingData = JsonParser.parseReader(new FileReader(mcMetaFile)).getAsJsonObject().getAsJsonObject("gui").getAsJsonObject("scaling");

            if (scalingData == null) return null;

            if (!scalingData.has("type")) return null;
            JsonElement scalingType = scalingData.get("type");

            if (!scalingType.isJsonPrimitive()) return null;
            if (!scalingType.getAsString().equals("nine_slice")) return null;

            Integer baseWidth = JsonUtils.getInt(scalingData, "width");
            if (baseWidth == null) return null;

            Integer baseHeight = JsonUtils.getInt(scalingData, "height");
            if (baseHeight == null) return null;

            JsonElement nineSliceElement = JsonUtils.getElement(scalingData, "border");
            if (nineSliceElement == null) return null;

            if (nineSliceElement.isJsonObject()) {
                JsonObject nineSlice = nineSliceElement.getAsJsonObject();

                Integer x1 = JsonUtils.getInt(nineSlice, "left");
                if (x1 == null) return null;

                Integer y1 = JsonUtils.getInt(nineSlice, "top");
                if (y1 == null) return null;

                Integer x2 = JsonUtils.getInt(nineSlice, "right");
                if (x2 == null) return null;

                Integer y2 = JsonUtils.getInt(nineSlice, "bottom");
                if (y2 == null) return null;

                return new NinesliceData.Java(img, baseWidth, baseHeight, x1, y1, x2, y2);
            } else if (nineSliceElement.isJsonPrimitive()) {
                try {
                    int nineSlice = nineSliceElement.getAsInt();

                    return new NinesliceData.Java(img, baseWidth, baseHeight, nineSlice, nineSlice, nineSlice, nineSlice);
                } catch (NumberFormatException e) {
                    return null;
                }
            }

            return null;
        } catch (IOException e) {
            return null;
        }
    }
}
