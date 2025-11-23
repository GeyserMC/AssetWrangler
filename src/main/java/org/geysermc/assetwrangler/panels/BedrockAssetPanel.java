package org.geysermc.assetwrangler.panels;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.geysermc.assetwrangler.Main;
import org.geysermc.assetwrangler.sources.AssetSource;
import org.geysermc.assetwrangler.utils.AnimationMeta;
import org.geysermc.assetwrangler.utils.Asset;
import org.geysermc.assetwrangler.utils.JsonMappingsMeta;
import org.geysermc.assetwrangler.windows.AssetViewerWindow;
import org.geysermc.assetwrangler.windows.MappingsWindow;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class BedrockAssetPanel extends AssetPanel {
    private final Map<String, AnimationMeta> ANIMATED_TEXTURES = new HashMap<>();

    public BedrockAssetPanel(AssetViewerWindow main, AssetSource assetSource, boolean isForMapping) {
        super(main, "Bedrock Assets", isForMapping, assetSource.getPaths(Main.DATA_FOLDER));

        Asset rootAsset = this.getRootAsset();

        Asset animatedAsset = rootAsset.resolve("textures/flipbook_textures.json");

        try {
            JsonArray flipbookArray = JsonParser.parseReader(new FileReader(animatedAsset.getPath().toFile())).getAsJsonArray();

            for (JsonElement flipbookElement : flipbookArray) {
                JsonObject flipbookObject = flipbookElement.getAsJsonObject();
                String texturePath = flipbookObject.get("flipbook_texture").getAsString();

                Asset asset = rootAsset.resolve(texturePath + ".png");
                if (asset == null) asset = rootAsset.resolve(texturePath + ".tga");

                ANIMATED_TEXTURES.put(asset.getPath().toString(), AnimationMeta.fromBedrockJson(
                        ImageIO.read(asset.getPath().toFile()), flipbookObject
                ));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public boolean isMapped(String path) {
        return main.isBedrockMapped(path);
    }

    @Override
    public void setPreviewComponent(AssetViewerWindow main, JComponent component) {
        main.setBedrockPreviewComponent(component);
    }

    @Override
    public JsonMappingsMeta.Section getMetaSection() {
        return main.getBedrockMeta();
    }

    @Override
    public boolean isAssetAnimated(String filePath) {
        return ANIMATED_TEXTURES.containsKey(filePath);
    }

    @Override
    public AnimationMeta getAnimationMeta(BufferedImage img, String filePath) throws IOException {
        return ANIMATED_TEXTURES.get(filePath);
    }
}
