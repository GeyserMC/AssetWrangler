package org.geysermc.assetwrangler.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.checkerframework.checker.nullness.qual.Nullable;

public class JsonUtils {
    public static @Nullable JsonElement getElement(JsonObject obj, String name) {
        if (!obj.has(name)) return null;

        return obj.get(name);
    }

    public static @Nullable JsonObject getObject(JsonObject obj, String name) {
        if (!obj.has(name)) return null;

        JsonElement element = obj.get(name);
        if (!element.isJsonObject()) return null;
        return element.getAsJsonObject();
    }

    public static @Nullable Integer getInt(JsonObject obj, String name) {
        if (!obj.has(name)) return null;

        JsonElement element = obj.get(name);
        if (!element.isJsonPrimitive()) return null;
        try {
            return element.getAsInt();
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
