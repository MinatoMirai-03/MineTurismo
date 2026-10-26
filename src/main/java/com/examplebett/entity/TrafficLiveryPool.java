package com.examplebett.entity;

import com.examplebett.AiObjectMod;
import com.examplebett.compat.ModResources;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
//? if >=1.19 {
import net.minecraft.util.RandomSource;
//?}

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class TrafficLiveryPool {

    private static final String PATH_PREFIX = "traffic_liveries/";

    private static final double RARE_WEIGHT = 0.17;

    private record Entry(List<String> common, List<String> rare) {
    }

    private static Map<String, Entry> cache;

    //? if >=1.19 {
    public static String pickRandom(ResourceManager resourceManager, RandomSource random, String modelId) {
    //?} else
    /*public static String pickRandom(ResourceManager resourceManager, java.util.Random random, String modelId) {*/
        ensureLoaded(resourceManager);

        Entry entry = cache != null ? cache.get(modelId.toLowerCase(Locale.ROOT)) : null;
        if (entry == null) {
            return null;
        }
        int commonCount = entry.common().size();
        int rareCount = entry.rare().size();
        if (commonCount == 0 && rareCount == 0) {
            return null;
        }
        double totalWeight = commonCount * 1.0 + rareCount * RARE_WEIGHT;
        double roll = random.nextDouble() * totalWeight;
        double cumulative = 0.0;
        for (String livery : entry.common()) {
            cumulative += 1.0;
            if (roll < cumulative) {
                return livery;
            }
        }
        for (String livery : entry.rare()) {
            cumulative += RARE_WEIGHT;
            if (roll < cumulative) {
                return livery;
            }
        }

        return !entry.common().isEmpty() ? entry.common().get(0) : entry.rare().get(0);
    }

    private static void ensureLoaded(ResourceManager resourceManager) {
        if (cache != null) return;

        if (resourceManager == null) return;
        Map<String, Entry> loaded = new HashMap<>();
        Map<ResourceLocation, ModResources.IoSupplier<InputStream>> resources =
                ModResources.listJsonResources(resourceManager, "traffic_liveries");
        for (Map.Entry<ResourceLocation, ModResources.IoSupplier<InputStream>> resourceEntry : resources.entrySet()) {
            ResourceLocation location = resourceEntry.getKey();
            String path = location.getPath();
            if (!path.startsWith(PATH_PREFIX)) continue;
            String modelId = path.substring(PATH_PREFIX.length(), path.length() - ".json".length());
            try (InputStream stream = resourceEntry.getValue().get()) {
                JsonObject json = ModResources.parseJsonObject(
                        new InputStreamReader(stream, StandardCharsets.UTF_8));
                List<String> common = readArray(json, "common");
                List<String> rare = readArray(json, "rare");
                loaded.put(modelId.toLowerCase(Locale.ROOT), new Entry(common, rare));
            } catch (IOException | RuntimeException e) {
                AiObjectMod.LOGGER.error("Failed to load traffic liveries: " + location, e);
            }
        }
        cache = loaded;
    }

    private static List<String> readArray(JsonObject json, String key) {
        List<String> result = new ArrayList<>();
        if (json.has(key)) {
            JsonArray array = json.getAsJsonArray(key);
            for (var element : array) {
                result.add(element.getAsString());
            }
        }
        return result;
    }

    public static void clearCache() {
        cache = null;
    }

    private TrafficLiveryPool() {
    }
}
