package com.examplebett.client.render;

import com.examplebett.AiObjectMod;
import com.examplebett.render.WheelAttachment;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class WheelAttachmentLoader {

    private static final Map<String, Optional<WheelAttachment>> cache = new HashMap<>();

    public static Optional<WheelAttachment> load(ResourceManager resourceManager, String modelId) {
        return cache.computeIfAbsent(modelId, id -> loadUncached(resourceManager, id));
    }

    private static Optional<WheelAttachment> loadUncached(ResourceManager resourceManager, String modelId) {
        ResourceLocation location = AiObjectMod.id("models/" + modelId + ".wheels.json");
        Optional<Resource> resource = com.examplebett.compat.ModResources.getResourceOptional(resourceManager, location);
        if (resource.isEmpty()) {
            return Optional.empty();
        }
        try (InputStream stream = com.examplebett.compat.ModResources.open(resource.get())) {
            String text = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            JsonObject json = JsonParser.parseString(text).getAsJsonObject();
            String wheelModelId = json.get("wheelModelId").getAsString();
            List<String> wheelStyles = new ArrayList<>();
            if (json.has("wheelStyles")) {
                JsonArray stylesJson = json.getAsJsonArray("wheelStyles");
                for (var el : stylesJson) {
                    wheelStyles.add(el.getAsString());
                }
            }
            if (wheelStyles.isEmpty()) {

                wheelStyles.addAll(WheelStyleCatalog.list(resourceManager));
                if (!wheelStyles.contains(wheelModelId)) {
                    wheelStyles.add(0, wheelModelId);
                }
                if (!wheelStyles.contains(WheelAttachment.NONE_STYLE)) {
                    wheelStyles.add(WheelAttachment.NONE_STYLE);
                }
            }
            WheelAttachment attachment = new WheelAttachment(
                    wheelModelId,
                    wheelStyles,
                    json.get("frontZ").getAsFloat(),
                    json.get("rearZ").getAsFloat(),
                    json.get("trackHalfWidth").getAsFloat(),
                    json.get("axleHeight").getAsFloat(),
                    json.get("wheelRadius").getAsFloat(),

                    json.has("bodyHeight") ? json.get("bodyHeight").getAsFloat() : 0f,

                    json.has("rearZ2") ? json.get("rearZ2").getAsFloat() : Float.NaN,

                    json.has("wheelScale") ? json.get("wheelScale").getAsFloat() : 1f,

                    json.has("wheelWidthScale") ? json.get("wheelWidthScale").getAsFloat() : 1f,

                    json.has("carScale") ? json.get("carScale").getAsFloat() : 1f,

                    json.has("widthStretchCm") ? json.get("widthStretchCm").getAsFloat() : 0f,
                    json.has("widthZoneCm") ? json.get("widthZoneCm").getAsFloat()
                            : com.examplebett.entity.CarEntity.DEFAULT_WIDTH_ZONE_CM,
                    json.has("widthCenterOffsetCm") ? json.get("widthCenterOffsetCm").getAsFloat() : 0f,

                    json.has("limoStretchCm") ? json.get("limoStretchCm").getAsFloat() : 0f,
                    json.has("limoZoneCm") ? json.get("limoZoneCm").getAsFloat()
                            : com.examplebett.entity.CarEntity.DEFAULT_LIMO_ZONE_CM,
                    json.has("limoCenterOffsetCm") ? json.get("limoCenterOffsetCm").getAsFloat() : 0f,

                    json.has("passengerX") ? json.get("passengerX").getAsFloat() : 0f,
                    json.has("passengerY") ? json.get("passengerY").getAsFloat() : 0f,
                    json.has("passengerZ") ? json.get("passengerZ").getAsFloat() : 0f,

                    json.has("singleWheelPerAxle") && json.get("singleWheelPerAxle").getAsBoolean());
            return Optional.of(attachment);
        } catch (IOException | RuntimeException e) {
            AiObjectMod.LOGGER.error("Failed to load wheel attachment config: " + modelId, e);
            return Optional.empty();
        }
    }

    public static void clearCache() {
        cache.clear();
    }

    private WheelAttachmentLoader() {
    }
}
