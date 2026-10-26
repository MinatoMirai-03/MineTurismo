package com.examplebett.entity;

import com.examplebett.waypoint.WaypointNetworkSavedData;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public final class TownTrafficManager {

    private TownTrafficManager() {
    }

    public static void registerEvents() {
        com.examplebett.compat.ModServerEvents.onEndServerTick(TownTrafficManager::onServerTick);
    }

    private static void onServerTick(MinecraftServer server) {
        for (ServerLevel level : server.getAllLevels()) {
            WaypointNetworkSavedData data = WaypointNetworkSavedData.get(level);
            if (!data.isTownTrafficEnabled()) {
                continue;
            }
            for (ServerPlayer player : level.players()) {
                int cooldown = data.getSpawnCooldown(player.getUUID());
                if (cooldown > 0) {
                    data.setSpawnCooldown(player.getUUID(), cooldown - 1);
                    continue;
                }
                data.setSpawnCooldown(player.getUUID(), data.getSpawnIntervalTicks());
                CarEntity.trySpawnTownTrafficNear(level, data, player.blockPosition());
            }
        }
    }
}
