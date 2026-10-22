package com.examplebett.entity;

import com.examplebett.AiObjectMod;
import com.examplebett.compat.ModResources;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class WheelAttachmentFootprint {

    public record Footprint(float frontZ, float rearZ, float trackHalfWidth, float axleHeight, float wheelRadius,
                             float bodyHeight) {
    }

    private static final String PATH_PREFIX = "wheel_attachment/";

    private static Map<String, Footprint> cache;

    public static Footprint get(ResourceManager resourceManager, String modelId) {
        ensureLoaded(resourceManager);

        return cache != null ? cache.get(modelId.toLowerCase(Locale.ROOT)) : null;
    }

    private static void ensureLoaded(ResourceManager resourceManager) {
        if (cache != null) return;

        if (resourceManager == null) return;
        Map<String, Footprint> loaded = new HashMap<>();
        Map<ResourceLocation, ModResources.IoSupplier<InputStream>> resources =
                ModResources.listJsonResources(resourceManager, "wheel_attachment");
        for (Map.Entry<ResourceLocation, ModResources.IoSupplier<InputStream>> entry : resources.entrySet()) {
            ResourceLocation location = entry.getKey();
            String path = location.getPath();
            if (!path.startsWith(PATH_PREFIX)) continue;
            String modelId = path.substring(PATH_PREFIX.length(), path.length() - ".json".length());
            try (InputStream stream = entry.getValue().get()) {
                JsonObject json = ModResources.parseJsonObject(
                        new InputStreamReader(stream, StandardCharsets.UTF_8));
                Footprint footprint = new Footprint(
                        json.get("frontZ").getAsFloat(),
                        json.get("rearZ").getAsFloat(),
                        json.get("trackHalfWidth").getAsFloat(),
                        json.get("axleHeight").getAsFloat(),
                        json.get("wheelRadius").getAsFloat(),
                        json.has("bodyHeight") ? json.get("bodyHeight").getAsFloat() : 0f);
                loaded.put(modelId.toLowerCase(Locale.ROOT), footprint);
            } catch (IOException | RuntimeException e) {
                AiObjectMod.LOGGER.error("Failed to load wheel attachment footprint: " + location, e);
            }
        }
        cache = loaded;
    }

    public static void clearCache() {
        cache = null;
    }

    private WheelAttachmentFootprint() {
    }
}
