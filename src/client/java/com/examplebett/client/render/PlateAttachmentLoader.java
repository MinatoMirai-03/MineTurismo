package com.examplebett.client.render;

import com.examplebett.AiObjectMod;
import com.examplebett.render.PlateAttachment;
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

public class PlateAttachmentLoader {

    private static final Map<String, Optional<PlateAttachment>> cache = new HashMap<>();

    public static Optional<PlateAttachment> load(ResourceManager resourceManager, String modelId) {
        return cache.computeIfAbsent(modelId, id -> loadUncached(resourceManager, id));
    }

    private static Optional<PlateAttachment> loadUncached(ResourceManager resourceManager, String modelId) {
        ResourceLocation location = AiObjectMod.id("models/" + modelId + ".plates.json");
        Optional<Resource> resource = com.examplebett.compat.ModResources.getResourceOptional(resourceManager, location);
        if (resource.isEmpty()) {
            return Optional.empty();
        }
        try (InputStream stream = com.examplebett.compat.ModResources.open(resource.get())) {
            String text = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            JsonObject json = JsonParser.parseString(text).getAsJsonObject();
            PlateAttachment attachment = new PlateAttachment(
                    json.get("frontX").getAsFloat(), json.get("frontY").getAsFloat(), json.get("frontZ").getAsFloat(),
                    json.get("frontScale").getAsFloat(), json.get("frontYaw").getAsFloat(),
                    json.get("frontPitch").getAsFloat(), json.get("frontRoll").getAsFloat(),
                    json.get("rearX").getAsFloat(), json.get("rearY").getAsFloat(), json.get("rearZ").getAsFloat(),
                    json.get("rearScale").getAsFloat(), json.get("rearYaw").getAsFloat(),
                    json.get("rearPitch").getAsFloat(), json.get("rearRoll").getAsFloat(),

                    json.has("frontPlateId") ? json.get("frontPlateId").getAsString() : "",
                    json.has("rearPlateId") ? json.get("rearPlateId").getAsString() : "");
            return Optional.of(attachment);
        } catch (IOException | RuntimeException e) {
            AiObjectMod.LOGGER.error("Failed to load plate attachment config: " + modelId, e);
            return Optional.empty();
        }
    }

    public static void clearCache() {
        cache.clear();
    }

    private PlateAttachmentLoader() {
    }
}
