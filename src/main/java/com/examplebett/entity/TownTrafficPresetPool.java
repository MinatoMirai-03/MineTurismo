package com.examplebett.entity;

import com.examplebett.AiObjectMod;
import com.examplebett.compat.ModResources;
import com.examplebett.util.AiCarPool;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public final class TownTrafficPresetPool {

    private static final String PATH_PREFIX = "town_traffic_presets";

    public record Preset(String id, String label, List<String> normalModelIds, List<String> rareModelIds,
                          boolean isDefault) {
    }

    private static List<Preset> cache;

    private TownTrafficPresetPool() {
    }

    public static List<Preset> list(ResourceManager resourceManager) {
        ensureLoaded(resourceManager);

        return cache != null ? cache : List.of();
    }

    public static Optional<Preset> getDefault(ResourceManager resourceManager) {
        ensureLoaded(resourceManager);
        if (cache == null) {
            return Optional.empty();
        }
        return cache.stream().filter(Preset::isDefault).findFirst();
    }

    public static Set<String> computeDisabledIds(Preset preset) {
        Set<String> disabled = new HashSet<>(AiCarPool.allModelIds(modelId -> false));
        disabled.removeAll(preset.normalModelIds());
        disabled.removeAll(preset.rareModelIds());
        return disabled;
    }

    private static void ensureLoaded(ResourceManager resourceManager) {
        if (cache != null) {
            return;
        }

        if (resourceManager == null) {
            return;
        }
        List<Preset> presets = new ArrayList<>();
        Map<ResourceLocation, ModResources.IoSupplier<InputStream>> resources =
                ModResources.listJsonResources(resourceManager, PATH_PREFIX);
        for (Map.Entry<ResourceLocation, ModResources.IoSupplier<InputStream>> entry : resources.entrySet()) {
            String path = entry.getKey().getPath();
            int slash = path.lastIndexOf('/');
            String fileName = slash >= 0 ? path.substring(slash + 1) : path;
            if (!fileName.endsWith(".json")) {
                continue;
            }
            String id = fileName.substring(0, fileName.length() - ".json".length());
            try (InputStream stream = entry.getValue().get();
                 InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
                JsonObject json = ModResources.parseJsonObject(reader);
                String label = json.has("label") ? json.get("label").getAsString() : id;
                List<String> normal = new ArrayList<>();
                if (json.has("normalModelIds")) {
                    for (var el : json.getAsJsonArray("normalModelIds")) {
                        normal.add(el.getAsString());
                    }
                }
                List<String> rare = new ArrayList<>();
                if (json.has("rareModelIds")) {
                    for (var el : json.getAsJsonArray("rareModelIds")) {
                        rare.add(el.getAsString());
                    }
                }
                boolean isDefault = json.has("default") && json.get("default").getAsBoolean();
                presets.add(new Preset(id, label, normal, rare, isDefault));
            } catch (IOException | RuntimeException e) {
                AiObjectMod.LOGGER.error("Failed to load town traffic preset: " + id, e);
            }
        }
        presets.sort((a, b) -> a.id().compareTo(b.id()));
        cache = presets;
    }

    public static void clearCache() {
        cache = null;
    }
}
