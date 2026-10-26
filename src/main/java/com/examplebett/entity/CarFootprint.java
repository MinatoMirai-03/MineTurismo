package com.examplebett.entity;

import com.examplebett.AiObjectMod;
import com.examplebett.compat.ModResources;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class CarFootprint {

    private static final float DEFAULT_WIDTH = 2.0f;
    private static final float DEFAULT_LENGTH = 4.5f;

    private static final float DEFAULT_HEIGHT = 1.5f;

    private static final float DEFAULT_CAR_SCALE = 1.0f;
    private static final String PATH_PREFIX = "car_dimensions/";

    private static Map<String, float[]> cache;

    public static float getWidth(ResourceManager resourceManager, String modelId) {
        return lookup(resourceManager, modelId)[0];
    }

    public static float getLength(ResourceManager resourceManager, String modelId) {
        return lookup(resourceManager, modelId)[1];
    }

    public static float getHeight(ResourceManager resourceManager, String modelId) {
        return lookup(resourceManager, modelId)[2];
    }

    public static float getCarScale(ResourceManager resourceManager, String modelId) {
        return lookup(resourceManager, modelId)[3];
    }

    private static float[] lookup(ResourceManager resourceManager, String modelId) {
        ensureLoaded(resourceManager);

        float[] whhs = cache != null ? cache.get(modelId.toLowerCase(Locale.ROOT)) : null;
        return whhs != null ? whhs : new float[]{DEFAULT_WIDTH, DEFAULT_LENGTH, DEFAULT_HEIGHT, DEFAULT_CAR_SCALE};
    }

    private static void ensureLoaded(ResourceManager resourceManager) {
        if (cache != null) return;

        if (resourceManager == null) return;
        Map<String, float[]> loaded = new HashMap<>();
        //? if >=1.19 {
        Map<ResourceLocation, Resource> resources = resourceManager.listResources(
                "car_dimensions", location -> location.getPath().endsWith(".json"));
        for (Map.Entry<ResourceLocation, Resource> entry : resources.entrySet()) {
            ResourceLocation location = entry.getKey();
            String path = location.getPath();
            if (!path.startsWith(PATH_PREFIX)) continue;
            String modelId = path.substring(PATH_PREFIX.length(), path.length() - ".json".length());
            try (InputStream stream = entry.getValue().open()) {
                JsonObject json = ModResources.parseJsonObject(
                        new InputStreamReader(stream, StandardCharsets.UTF_8));
                float width = json.get("width").getAsFloat();
                float length = json.get("length").getAsFloat();
                float height = json.has("height") ? json.get("height").getAsFloat() : DEFAULT_HEIGHT;
                float carScale = json.has("carScale") ? json.get("carScale").getAsFloat() : DEFAULT_CAR_SCALE;
                loaded.put(modelId.toLowerCase(Locale.ROOT), new float[]{width, length, height, carScale});
            } catch (IOException | RuntimeException e) {
                AiObjectMod.LOGGER.error("Failed to load car dimensions: " + location, e);
            }
        }
        //?} else {
        /*
        java.util.Collection<ResourceLocation> resources = resourceManager.listResources(
                "car_dimensions", path -> path.endsWith(".json"));
        for (ResourceLocation location : resources) {
            String path = location.getPath();
            if (!path.startsWith(PATH_PREFIX)) continue;
            String modelId = path.substring(PATH_PREFIX.length(), path.length() - ".json".length());
            try (InputStream stream = resourceManager.getResource(location).getInputStream()) {
                JsonObject json = ModResources.parseJsonObject(
                        new InputStreamReader(stream, StandardCharsets.UTF_8));
                float width = json.get("width").getAsFloat();
                float length = json.get("length").getAsFloat();
                float height = json.has("height") ? json.get("height").getAsFloat() : DEFAULT_HEIGHT;
                float carScale = json.has("carScale") ? json.get("carScale").getAsFloat() : DEFAULT_CAR_SCALE;
                loaded.put(modelId.toLowerCase(Locale.ROOT), new float[]{width, length, height, carScale});
            } catch (IOException | RuntimeException e) {
                AiObjectMod.LOGGER.error("Failed to load car dimensions: " + location, e);
            }
        }
        */
        //?}
        cache = loaded;
    }

    public static void clearCache() {
        cache = null;
    }

    private CarFootprint() {
    }
}
