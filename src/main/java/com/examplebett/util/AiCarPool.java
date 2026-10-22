package com.examplebett.util;

import com.examplebett.compat.ModRegistries;
import com.examplebett.item.CarSpawnItem;
//? if >=1.19 {
import net.minecraft.util.RandomSource;
//?}
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class AiCarPool {

    private static List<String> allModelIds;
    private static java.util.Map<String, Item> spawnItemsByModelId;

    private static List<String> allModelIds() {
        if (allModelIds == null) {
            List<String> ids = new ArrayList<>();
            for (Item item : ModRegistries.ITEM) {
                if (item instanceof CarSpawnItem carSpawnItem) {
                    ids.add(carSpawnItem.getModelId());
                }
            }
            allModelIds = List.copyOf(ids);
        }
        return allModelIds;
    }

    public static Item spawnItemFor(String modelId) {
        if (spawnItemsByModelId == null) {
            java.util.Map<String, Item> map = new java.util.LinkedHashMap<>();
            for (Item item : ModRegistries.ITEM) {
                if (item instanceof CarSpawnItem carSpawnItem) {
                    map.put(carSpawnItem.getModelId(), item);
                }
            }
            spawnItemsByModelId = java.util.Map.copyOf(map);
        }
        return spawnItemsByModelId.get(modelId);
    }

    private static List<String> groundCarModelIds(Predicate<String> excludeModelId) {
        List<String> ids = new ArrayList<>();
        for (String modelId : allModelIds()) {
            if (!excludeModelId.test(modelId)) {
                ids.add(modelId);
            }
        }
        return ids;
    }

    //? if >=1.19 {
    public static String pickRandom(Predicate<String> excludeModelId, RandomSource random) {
    //?} else
    /*public static String pickRandom(Predicate<String> excludeModelId, java.util.Random random) {*/
        List<String> ids = groundCarModelIds(excludeModelId);
        return ids.get(random.nextInt(ids.size()));
    }

    public static List<String> allModelIds(Predicate<String> excludeModelId) {
        return groundCarModelIds(excludeModelId);
    }

    public static final double RARE_SPAWN_WEIGHT = 0.05;

    public static double allowedTotalWeight(Predicate<String> excludeModelId, Set<String> disabledModelIds, Set<String> rareModelIds) {
        double total = 0.0;
        for (String id : groundCarModelIds(excludeModelId)) {
            if (!disabledModelIds.contains(id)) {
                total += rareModelIds.contains(id) ? RARE_SPAWN_WEIGHT : 1.0;
            }
        }
        return total;
    }

    //? if >=1.19 {
    public static String pickRandomAllowed(Predicate<String> excludeModelId, RandomSource random, Set<String> disabledModelIds, Set<String> rareModelIds) {
    //?} else
    /*public static String pickRandomAllowed(Predicate<String> excludeModelId, java.util.Random random, Set<String> disabledModelIds, Set<String> rareModelIds) {*/
        List<String> ids = groundCarModelIds(excludeModelId);
        List<String> allowed = new ArrayList<>(ids.size());
        double totalWeight = 0.0;
        for (String id : ids) {
            if (!disabledModelIds.contains(id)) {
                allowed.add(id);
                totalWeight += rareModelIds.contains(id) ? RARE_SPAWN_WEIGHT : 1.0;
            }
        }
        if (allowed.isEmpty()) {
            return null;
        }
        double roll = random.nextDouble() * totalWeight;
        double cumulative = 0.0;
        for (String id : allowed) {
            cumulative += rareModelIds.contains(id) ? RARE_SPAWN_WEIGHT : 1.0;
            if (roll < cumulative) {
                return id;
            }
        }
        return allowed.get(allowed.size() - 1);
    }
}
