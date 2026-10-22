package com.examplebett.client.render;

import com.examplebett.AiObjectMod;
import com.examplebett.render.CameraAttachment;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CameraAttachmentLoader {

    private static final Map<String, Optional<CameraAttachment>> cache = new HashMap<>();

    public static Optional<CameraAttachment> load(ResourceManager resourceManager, String modelId) {
        return cache.computeIfAbsent(modelId, id -> loadUncached(resourceManager, id));
    }

    private static Optional<CameraAttachment> loadUncached(ResourceManager resourceManager, String modelId) {
        ResourceLocation location = AiObjectMod.id("models/" + modelId + ".camera.json");
        Optional<Resource> resource = com.examplebett.compat.ModResources.getResourceOptional(resourceManager, location);
        if (resource.isEmpty()) {
            return Optional.empty();
        }
        try (InputStream stream = com.examplebett.compat.ModResources.open(resource.get())) {
            String text = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            JsonObject json = JsonParser.parseString(text).getAsJsonObject();
            CameraAttachment attachment = new CameraAttachment(
                    json.get("distance").getAsFloat(),

                    json.has("height") ? json.get("height").getAsFloat() : 0f);
            return Optional.of(attachment);
        } catch (IOException | RuntimeException e) {
            AiObjectMod.LOGGER.error("Failed to load camera attachment config: " + modelId, e);
            return Optional.empty();
        }
    }

    public static void clearCache() {
        cache.clear();
    }

    private CameraAttachmentLoader() {
    }
}
