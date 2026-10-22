//? if >=1.18 {
//?}
//? if <1.18 {
/*
package com.examplebett.client.render;

import com.examplebett.AiObjectMod;
import com.examplebett.entity.VehicleCategory;
import com.examplebett.entity.VehicleCategoryFootprint;
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
import java.util.Set;

public class VehicleCategoryLoader {

    private static final Map<String, Optional<Set<VehicleCategory>>> cache = new HashMap<>();

    public static Optional<Set<VehicleCategory>> load(ResourceManager resourceManager, String modelId) {
        return cache.computeIfAbsent(modelId, id -> loadUncached(resourceManager, id));
    }

    private static Optional<Set<VehicleCategory>> loadUncached(ResourceManager resourceManager, String modelId) {
        ResourceLocation location = AiObjectMod.id("models/" + modelId + ".category.json");
        Optional<Resource> resource = com.examplebett.compat.ModResources.getResourceOptional(resourceManager, location);
        if (resource.isEmpty()) {
            return Optional.empty();
        }
        try (InputStream stream = com.examplebett.compat.ModResources.open(resource.get())) {
            String text = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            JsonObject json = new JsonParser().parse(text).getAsJsonObject();
            Set<VehicleCategory> categories = VehicleCategoryFootprint.parseCategories(json);
            return categories.isEmpty() ? Optional.empty() : Optional.of(categories);
        } catch (IOException | RuntimeException e) {
            AiObjectMod.LOGGER.error("Failed to load vehicle category config: " + modelId, e);
            return Optional.empty();
        }
    }

    public static void clearCache() {
        cache.clear();
    }

    private VehicleCategoryLoader() {
    }
}
*/
//?}
