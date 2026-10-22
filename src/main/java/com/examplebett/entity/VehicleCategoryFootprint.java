package com.examplebett.entity;

import com.examplebett.AiObjectMod;
import com.examplebett.compat.ModResources;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public final class VehicleCategoryFootprint {

    private static final String PATH_PREFIX = "vehicle_category/";
    private static final Set<VehicleCategory> EMPTY = Set.of();

    private static Map<String, Set<VehicleCategory>> cache;

    public static Set<VehicleCategory> get(ResourceManager resourceManager, String modelId) {
        ensureLoaded(resourceManager);

        return cache != null ? cache.getOrDefault(modelId.toLowerCase(Locale.ROOT), EMPTY) : EMPTY;
    }

    private static void ensureLoaded(ResourceManager resourceManager) {
        if (cache != null) return;

        if (resourceManager == null) return;
        Map<String, Set<VehicleCategory>> loaded = new HashMap<>();
        Map<ResourceLocation, ModResources.IoSupplier<InputStream>> resources =
                ModResources.listJsonResources(resourceManager, "vehicle_category");
        for (Map.Entry<ResourceLocation, ModResources.IoSupplier<InputStream>> entry : resources.entrySet()) {
            ResourceLocation location = entry.getKey();
            String path = location.getPath();
            if (!path.startsWith(PATH_PREFIX)) continue;
            String modelId = path.substring(PATH_PREFIX.length(), path.length() - ".json".length());
            try (InputStream stream = entry.getValue().get()) {
                JsonObject json = ModResources.parseJsonObject(
                        new InputStreamReader(stream, StandardCharsets.UTF_8));
                Set<VehicleCategory> categories = parseCategories(json);
                if (!categories.isEmpty()) {
                    loaded.put(modelId.toLowerCase(Locale.ROOT), categories);
                }
            } catch (IOException | RuntimeException e) {
                AiObjectMod.LOGGER.error("Failed to load vehicle category: " + location, e);
            }
        }
        cache = loaded;
    }

    public static Set<VehicleCategory> parseCategories(JsonObject json) {
        Set<VehicleCategory> categories = EnumSet.noneOf(VehicleCategory.class);
        if (json.has("categories")) {
            JsonArray array = json.getAsJsonArray("categories");
            for (var element : array) {
                VehicleCategory category = VehicleCategory.byId(element.getAsString());
                if (category != null) {
                    categories.add(category);
                }
            }
        } else if (json.has("category")) {
            VehicleCategory category = VehicleCategory.byId(json.get("category").getAsString());
            if (category != null) {
                categories.add(category);
            }
        }
        return categories;
    }

    public static void clearCache() {
        cache = null;
    }

    private VehicleCategoryFootprint() {
    }
}
