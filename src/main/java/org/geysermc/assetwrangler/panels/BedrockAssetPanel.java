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
import org.geysermc.assetwrangler.utils.NinesliceData;
import org.geysermc.assetwrangler.windows.AssetViewerWindow;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class BedrockAssetPanel extends AssetPanel {
    private final Map<String, AnimationMeta> ANIMATED_TEXTURES = new HashMap<>();

    public BedrockAssetPanel(AssetViewerWindow main, AssetSource assetSource, boolean isForMapping) {
        super(main, "Bedrock Assets", isForMapping, assetSource.getPaths(Main.DATA_FOLDER));

        Asset rootAsset = this.getRootAsset();

        Asset animatedAsset = rootAsset.resolve("textures/flipbook_textures.json");

        if (animatedAsset == null) return; // Nothing animated here!

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
    public AnimationMeta getAnimationMeta(BufferedImage img, String filePath) {
        return ANIMATED_TEXTURES.get(filePath);
    }

    @Override
    public boolean isAssetNinesliced(String filePath) {
        String filePathButJson = filePath.replace(".png", ".json").replace(".tga", ".json");

        Path path = Path.of(filePathButJson);

        return Files.exists(path);
    }

    @Override
    public NinesliceData getNineslicedMeta(BufferedImage img, String filePath) {
        String filePathButJson = filePath.replace(".png", ".json").replace(".tga", ".json");

        Path path = Path.of(filePathButJson);

        try {
            JsonObject object = JsonParser.parseReader(new FileReader(path.toFile())).getAsJsonObject();

            if (!object.has("nineslice_size")) return null;

            JsonElement nineSliceSize = object.get("nineslice_size");

            int[] ninesliceSizeArray = new int[4];

            if (nineSliceSize.isJsonArray()) {
                JsonArray array = nineSliceSize.getAsJsonArray();
                if (array.size() != 4) return null;

                JsonElement val1 = array.get(0);
                if (!val1.isJsonPrimitive()) return null;
                JsonElement val2 = array.get(1);
                if (!val2.isJsonPrimitive()) return null;
                JsonElement val3 = array.get(2);
                if (!val3.isJsonPrimitive()) return null;
                JsonElement val4 = array.get(3);
                if (!val4.isJsonPrimitive()) return null;

                ninesliceSizeArray[0] = val1.getAsInt();
                ninesliceSizeArray[1] = val2.getAsInt();
                ninesliceSizeArray[2] = val3.getAsInt();
                ninesliceSizeArray[3] = val4.getAsInt();
            } else if (nineSliceSize.isJsonPrimitive()) {
                int val = nineSliceSize.getAsInt();
                ninesliceSizeArray[0] = val;
                ninesliceSizeArray[1] = val;
                ninesliceSizeArray[2] = val;
                ninesliceSizeArray[3] = val;
            } else return null;

            if (!object.has("base_size")) return null;

            JsonElement baseSize = object.get("base_size");

            int baseWidth;
            int baseHeight;

            if (baseSize.isJsonArray()) {
                JsonArray array = baseSize.getAsJsonArray();
                if (array.size() != 2) return null;

                JsonElement val1 = array.get(0);
                if (!val1.isJsonPrimitive()) return null;
                JsonElement val2 = array.get(1);
                if (!val2.isJsonPrimitive()) return null;

                baseWidth = val1.getAsInt();
                baseHeight = val2.getAsInt();
            } else return null;

            return new NinesliceData.Bedrock(ninesliceSizeArray, img, baseWidth, baseHeight);
        } catch (FileNotFoundException e) {
            return null;
        }
    }
}
