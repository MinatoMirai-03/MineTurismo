package com.examplebett.client.render;

import com.examplebett.AiObjectMod;
import com.examplebett.render.LiveryOptions;
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

public class LiveryLoader {

    private static final Map<String, Optional<LiveryOptions>> cache = new HashMap<>();

    public static Optional<LiveryOptions> load(ResourceManager resourceManager, String modelId) {
        return cache.computeIfAbsent(modelId, id -> loadUncached(resourceManager, id));
    }

    private static Optional<LiveryOptions> loadUncached(ResourceManager resourceManager, String modelId) {
        ResourceLocation location = AiObjectMod.id("models/" + modelId + ".liveries.json");
        Optional<Resource> resource = com.examplebett.compat.ModResources.getResourceOptional(resourceManager, location);
        if (resource.isEmpty()) {
            return Optional.empty();
        }
        try (InputStream stream = com.examplebett.compat.ModResources.open(resource.get())) {
            String text = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            JsonObject json = JsonParser.parseString(text).getAsJsonObject();
            List<String> ids = new ArrayList<>();
            for (var el : json.getAsJsonArray("liveries")) {
                ids.add(el.getAsString());
            }
            List<String> labels = new ArrayList<>();
            if (json.has("liveryLabels")) {
                JsonArray labelsJson = json.getAsJsonArray("liveryLabels");
                for (var el : labelsJson) {
                    labels.add(el.getAsString());
                }
            }
            List<String> decalIds = new ArrayList<>();
            if (json.has("decalLiveries")) {
                JsonArray decalJson = json.getAsJsonArray("decalLiveries");
                for (var el : decalJson) {
                    decalIds.add(el.getAsString());
                }
            }
            return Optional.of(LiveryOptions.of(ids, labels, decalIds));
        } catch (IOException | RuntimeException e) {
            AiObjectMod.LOGGER.error("Failed to load livery config: " + modelId, e);
            return Optional.empty();
        }
    }

    public static void clearCache() {
        cache.clear();
    }

    private LiveryLoader() {
    }
}
