package org.geysermc.assetwrangler.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

@Getter
public class AnimationMeta {
    private boolean interpolate;
    private int width;
    private int height;
    private int chunkWidth;
    private int chunkHeight;
    private List<Frame> frames;

    @Getter
    public static class Frame {
        private int index;
        private int time;
    }

    public static AnimationMeta fromJavaJson(BufferedImage image, JsonObject object) {
        object = object.getAsJsonObject("animation");

        int frametime = 1;
        if (object.has("frametime")) frametime = object.get("frametime").getAsInt();

        AnimationMeta meta = new AnimationMeta();

        if (object.has("interpolate")) meta.interpolate = object.get("interpolate").getAsBoolean();
        else meta.interpolate = false;

        if (!object.has("width")) {
            if (object.has("height")) meta.width = image.getWidth();
            else meta.width = Math.min(image.getHeight(), image.getWidth());
        } else meta.width = object.get("width").getAsInt();

        meta.chunkWidth = meta.width;

        if (!object.has("height")) {
            if (object.has("width")) meta.height = image.getHeight();
            else meta.height = Math.min(image.getHeight(), image.getWidth());
        } else meta.height = object.get("height").getAsInt();

        meta.chunkHeight = meta.height;

        if (object.has("frames")) {
            List<Frame> frames = new ArrayList<>();
            for (JsonElement element : object.getAsJsonArray("frames")) {
                if (element.isJsonObject()) {
                    JsonObject frameObject = element.getAsJsonObject();
                    Frame frame = new Frame();
                    frame.index = frameObject.get("index").getAsInt();
                    if (frameObject.has("time")) frame.time = frameObject.get("time").getAsInt();
                    else frame.time = frametime;
                    frames.add(frame);
                } else {
                    Frame frame = new Frame();
                    frame.index = element.getAsInt();
                    frame.time = frametime;
                    frames.add(frame);
                }
            }
            meta.frames = frames;
        } else {
            List<Frame> frames = new ArrayList<>();
            int frameCount = image.getHeight() / meta.height;
            for (int i = 0; i < frameCount; i++) {
                Frame frame = new Frame();
                frame.index = i;
                frame.time = frametime;
                frames.add(frame);
            }
            meta.frames = frames;
        }

        return meta;
    }

    public static AnimationMeta fromBedrockJson(BufferedImage image, JsonObject object) {
        int frametime = 1;
        if (object.has("ticks_per_frame")) frametime = object.get("ticks_per_frame").getAsInt();

        AnimationMeta meta = new AnimationMeta();

        if (object.has("blend_frames")) meta.interpolate = object.get("blend_frames").getAsBoolean();
        else meta.interpolate = true;

        meta.chunkWidth = image.getWidth();
        meta.chunkHeight = meta.chunkWidth;

        int replicate = 1;
        if (object.has("replicate")) replicate = object.get("replicate").getAsInt();

        meta.width = meta.chunkWidth / replicate;
        meta.height = meta.chunkHeight / replicate;

        List<Frame> frames = new ArrayList<>();
        if (object.has("frames")) {
            if (object.get("frames").isJsonArray()) {
                for (JsonElement element : object.getAsJsonArray("frames")) {
                    Frame frame = new Frame();
                    frame.index = element.getAsInt();
                    frame.time = frametime;
                    frames.add(frame);
                }
            } else {
                for (int i = 0; i < object.get("frames").getAsInt(); i++) {
                    Frame frame = new Frame();
                    frame.index = i;
                    frame.time = frametime;
                    frames.add(frame);
                }
            }
        } else {
            for (int i = 0; i < image.getHeight() / meta.chunkHeight; i++) {
                Frame frame = new Frame();
                frame.index = i;
                frame.time = frametime;
                frames.add(frame);
            }
        }
        meta.frames = frames;

        return meta;
    }
}
