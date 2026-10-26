package com.examplebett.client.render;

import com.examplebett.AiObjectMod;
import com.examplebett.render.WheelColorOptions;
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

public class WheelColorLoader {

    private static final Map<String, Optional<WheelColorOptions>> cache = new HashMap<>();

    public static Optional<WheelColorOptions> load(ResourceManager resourceManager, String wheelModelId) {
        return cache.computeIfAbsent(wheelModelId, id -> loadUncached(resourceManager, id));
    }

    private static Optional<WheelColorOptions> loadUncached(ResourceManager resourceManager, String wheelModelId) {
        ResourceLocation location = AiObjectMod.id("models/" + wheelModelId + ".wheel_colors.json");
        Optional<Resource> resource = com.examplebett.compat.ModResources.getResourceOptional(resourceManager, location);
        if (resource.isEmpty()) {
            return Optional.empty();
        }
        try (InputStream stream = com.examplebett.compat.ModResources.open(resource.get())) {
            String text = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            JsonObject json = JsonParser.parseString(text).getAsJsonObject();
            List<String> ids = new ArrayList<>();
            for (var el : json.getAsJsonArray("colors")) {
                ids.add(el.getAsString());
            }
            List<String> labels = new ArrayList<>();
            if (json.has("colorLabels")) {
                JsonArray labelsJson = json.getAsJsonArray("colorLabels");
                for (var el : labelsJson) {
                    labels.add(el.getAsString());
                }
            }
            return Optional.of(WheelColorOptions.of(ids, labels));
        } catch (IOException | RuntimeException e) {
            AiObjectMod.LOGGER.error("Failed to load wheel color config: " + wheelModelId, e);
            return Optional.empty();
        }
    }

    public static void clearCache() {
        cache.clear();
    }

    private WheelColorLoader() {
    }
}
