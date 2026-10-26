package com.examplebett.entity;

import com.examplebett.AiObjectMod;
import com.examplebett.waypoint.RailwayNetworkSavedData;
import com.examplebett.waypoint.RailwayNode;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class RailwayTrafficManager {

    private static final int CHECK_INTERVAL_TICKS = 100;
    private static final double SPAWN_SEARCH_RADIUS = 64.0;

    private static final int MAX_PATH_LENGTH = 4096;

    private RailwayTrafficManager() {
    }

    public static void registerEvents() {
        com.examplebett.compat.ModServerEvents.onEndServerTick(RailwayTrafficManager::onServerTick);
    }

    private static void onServerTick(MinecraftServer server) {
        for (ServerLevel level : server.getAllLevels()) {
            RailwayNetworkSavedData data = RailwayNetworkSavedData.get(level);
            for (int originId : new ArrayList<>(data.getRouteOriginIds())) {
                int cooldown = data.getRouteCooldown(originId);
                if (cooldown > 0) {
                    data.setRouteCooldown(originId, cooldown - 1);
                    continue;
                }
                data.setRouteCooldown(originId, CHECK_INTERVAL_TICKS);

                Integer activeEntityId = data.getActiveTrainEntityId(originId);
                if (activeEntityId != null) {
                    if (level.getEntity(activeEntityId) != null) {
                        AiObjectMod.LOGGER.debug("[railway] route {}: skip (already active, entity {})", originId, activeEntityId);
                        continue;
                    }
                    data.clearActiveTrainIfMatches(originId, activeEntityId);
                }

                RailwayNetworkSavedData.RailwayRouteEntry config = data.getRouteConfig(originId);
                if (config == null) {
                    AiObjectMod.LOGGER.debug("[railway] route {}: skip (no route config)", originId);
                    continue;
                }
                List<RailwayNode> path = computeRoutePath(data, originId);
                if (path.size() < 2) {
                    AiObjectMod.LOGGER.debug("[railway] route {}: skip (path too short: {} node(s))", originId, path.size());
                    continue;
                }

                int spawnIndex = findNearestPlayerPathIndex(level, path);
                if (spawnIndex < 0) {
                    AiObjectMod.LOGGER.debug("[railway] route {}: skip (no player within {} blocks of {} path node(s))", originId, SPAWN_SEARCH_RADIUS, path.size());
                    continue;
                }

                if (spawnIndex >= path.size() - 1) {
                    spawnIndex = path.size() - 2;
                }
                AiObjectMod.LOGGER.debug("[railway] route {}: spawning (mode={}, modelId={}, path={} node(s), spawnIndex={})",
                        originId, config.mode(), config.modelId(), path.size(), spawnIndex);
                trySpawnTrain(level, data, originId, config, path, spawnIndex);
            }
        }
    }

    private static int findNearestPlayerPathIndex(ServerLevel level, List<RailwayNode> path) {
        double radiusSq = SPAWN_SEARCH_RADIUS * SPAWN_SEARCH_RADIUS;
        int bestIndex = -1;
        double bestDistSq = Double.MAX_VALUE;
        for (ServerPlayer player : level.players()) {
            BlockPos playerPos = player.blockPosition();
            for (int i = 0; i < path.size(); i++) {
                double distSq = playerPos.distSqr(path.get(i).pos());
                if (distSq <= radiusSq && distSq < bestDistSq) {
                    bestDistSq = distSq;
                    bestIndex = i;
                }
            }
        }
        return bestIndex;
    }

    static List<RailwayNode> computeRoutePath(RailwayNetworkSavedData data, int originId) {
        List<RailwayNode> path = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();
        int currentId = originId;
        while (visited.add(currentId) && path.size() < MAX_PATH_LENGTH) {
            RailwayNode node = data.getNodes().get(currentId);
            if (node == null) {
                break;
            }
            path.add(node);
            if (node.oneWayLinks().length == 0) {
                break;
            }
            currentId = node.oneWayLinks()[0];
        }
        return path;
    }

    private static String rawStringOr(CompoundTag tag, String key, String fallback) {
        //? if >=1.21.5 {
        return tag.getStringOr(key, fallback);
        //?} else
        /*return tag.contains(key) ? tag.getString(key) : fallback;*/
    }

    private static void trySpawnTrain(ServerLevel level, RailwayNetworkSavedData data, int originId,
                                       RailwayNetworkSavedData.RailwayRouteEntry config, List<RailwayNode> path, int spawnIndex) {
        String modelId;
        CompoundTag formationTag = null;
        if (RailwayNetworkSavedData.RailwayRouteEntry.MODE_CUSTOM.equals(config.mode())) {
            formationTag = config.formationTag();
            modelId = rawStringOr(formationTag, "ModelId", "");
        } else {
            modelId = config.modelId();
        }
        if (modelId.isEmpty()) {
            AiObjectMod.LOGGER.debug("[railway] route {}: abort spawn (empty modelId, formationTag empty={})",
                    originId, formationTag == null || formationTag.isEmpty());
            return;
        }

        BlockPos spawnPos = path.get(spawnIndex).pos();
        CarEntity car = new CarEntity(level, spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, modelId);

        car.faceRailwayHeading(spawnPos, path.get(spawnIndex + 1).pos());
        if (formationTag != null) {
            car.importCustomizationTag(formationTag);
        }
        level.addFreshEntity(car);
        car.startRailwayRoute(data, originId, path, spawnIndex);
        AiObjectMod.LOGGER.debug("[railway] route {}: spawned entity {} at {}", originId, car.getId(), spawnPos);
    }
}
