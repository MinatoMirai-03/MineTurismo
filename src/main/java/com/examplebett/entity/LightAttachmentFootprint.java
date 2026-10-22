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

public final class LightAttachmentFootprint {

    public record Footprint(float headlightX, float headlightY, float headlightZ,
                             float taillightX, float taillightY, float taillightZ) {
    }

    private static final String PATH_PREFIX = "light_attachment/";

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
                ModResources.listJsonResources(resourceManager, "light_attachment");
        for (Map.Entry<ResourceLocation, ModResources.IoSupplier<InputStream>> entry : resources.entrySet()) {
            ResourceLocation location = entry.getKey();
            String path = location.getPath();
            if (!path.startsWith(PATH_PREFIX)) continue;
            String modelId = path.substring(PATH_PREFIX.length(), path.length() - ".json".length());
            try (InputStream stream = entry.getValue().get()) {
                JsonObject json = ModResources.parseJsonObject(
                        new InputStreamReader(stream, StandardCharsets.UTF_8));
                Footprint footprint = new Footprint(
                        json.get("headlightX").getAsFloat(),
                        json.get("headlightY").getAsFloat(),
                        json.get("headlightZ").getAsFloat(),
                        json.get("taillightX").getAsFloat(),
                        json.get("taillightY").getAsFloat(),
                        json.get("taillightZ").getAsFloat());
                loaded.put(modelId.toLowerCase(Locale.ROOT), footprint);
            } catch (IOException | RuntimeException e) {
                AiObjectMod.LOGGER.error("Failed to load light attachment footprint: " + location, e);
            }
        }
        cache = loaded;
    }

    public static void clearCache() {
        cache = null;
    }

    private LightAttachmentFootprint() {
    }
}
