package com.examplebett.waypoint;

import com.examplebett.item.AiObjectModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;

public final class WaypointVisualizer {

    private static final int TICK_INTERVAL = 10;
    private static final double VISIBLE_RADIUS = 48.0;
    private static final double LINK_SAMPLE_SPACING = 1.0;
    private static final int SIGNAL_GREEN_COLOR = 0x33FF33;
    private static final int SIGNAL_RED_COLOR = 0xFF3333;
    private static final float SIGNAL_PARTICLE_SCALE = 1.4f;

    private static final int ONE_WAY_TARGET_COLOR = 0xFF3333;
    private static final float ONE_WAY_TARGET_SCALE = 1.6f;

    private static final int RAILWAY_ORIGIN_COLOR = 0xFFD700;
    private static final float RAILWAY_ORIGIN_SCALE = 1.8f;

    private static final int RAILWAY_STOP_COLOR = 0x33CCFF;
    private static final float RAILWAY_STOP_SCALE = 1.6f;

    private WaypointVisualizer() {
    }

    private static DustParticleOptions dustOptions(int packedColor, float scale) {
        //? if >=1.17 {
        //? if >=1.19.3 {
        //? if >=1.21.2 {
        return new DustParticleOptions(packedColor, scale);
        //?} else {
        /*float r = ((packedColor >> 16) & 0xFF) / 255f;
        float g = ((packedColor >> 8) & 0xFF) / 255f;
        float b = (packedColor & 0xFF) / 255f;
        return new DustParticleOptions(new org.joml.Vector3f(r, g, b), scale);*/
        //?}
        //?} else {
        /*float r = ((packedColor >> 16) & 0xFF) / 255f;
        float g = ((packedColor >> 8) & 0xFF) / 255f;
        float b = (packedColor & 0xFF) / 255f;
        return new DustParticleOptions(new com.mojang.math.Vector3f(r, g, b), scale);*/
        //?}
        //?} else
        /*float r = ((packedColor >> 16) & 0xFF) / 255f;
        float g = ((packedColor >> 8) & 0xFF) / 255f;
        float b = (packedColor & 0xFF) / 255f;
        return new DustParticleOptions(r, g, b, scale);*/
    }

    private static <T extends net.minecraft.core.particles.ParticleOptions> void sendParticlesCompat(
            ServerLevel level, ServerPlayer player, T particle,
            double x, double y, double z, int count, double dx, double dy, double dz, double speed) {
        //? if >=1.21.4 {
        level.sendParticles(player, particle, false, false, x, y, z, count, dx, dy, dz, speed);
        //?} else
        /*level.sendParticles(player, particle, false, x, y, z, count, dx, dy, dz, speed);*/
    }

    public static void registerEvents() {
        com.examplebett.compat.ModServerEvents.onEndServerTick(WaypointVisualizer::onServerTick);
    }

    private static void onServerTick(net.minecraft.server.MinecraftServer server) {
        if (server.getTickCount() % TICK_INTERVAL != 0) {
            return;
        }
        for (ServerLevel level : server.getAllLevels()) {
            for (ServerPlayer player : level.players()) {
                if (isHolding(player, AiObjectModItems.WAYPOINT_MARKER) || isHolding(player, AiObjectModItems.ONE_WAY_WAYPOINT_MARKER)) {
                    visualizeNodesFor(level, player);
                }
                if (isHolding(player, AiObjectModItems.TRAFFIC_SIGNAL_MARKER)) {
                    visualizeSignalsFor(level, player);
                }

                if (isHolding(player, AiObjectModItems.RAILWAY_WAYPOINT_MARKER) || isHolding(player, AiObjectModItems.RAILWAY_STOP_MARKER)) {
                    visualizeRailwayNodesFor(level, player);
                    visualizeRailwayStopsFor(level, player);
                }
            }
        }
    }

    private static boolean isHolding(ServerPlayer player, Item item) {
        return player.getItemInHand(InteractionHand.MAIN_HAND).getItem() == item
                || player.getItemInHand(InteractionHand.OFF_HAND).getItem() == item;
    }

    private static void visualizeNodesFor(ServerLevel level, ServerPlayer player) {
        WaypointNetworkSavedData data = WaypointNetworkSavedData.get(level);
        BlockPos playerPos = player.blockPosition();

        for (WaypointNode node : data.findNodesWithinRadius(playerPos, VISIBLE_RADIUS)) {
            spawnPoint(level, player, node.pos().getX() + 0.5, node.pos().getY() + 0.2, node.pos().getZ() + 0.5, ParticleTypes.END_ROD, 3);

            for (int neighborId : node.links()) {
                if (neighborId <= node.id()) {

                    continue;
                }
                WaypointNode neighbor = data.getNodes().get(neighborId);
                if (neighbor != null) {
                    spawnLine(level, player, node.pos(), neighbor.pos());
                }
            }

            for (int oneWayTargetId : node.oneWayLinks()) {
                WaypointNode target = data.getNodes().get(oneWayTargetId);
                if (target != null) {
                    boolean isTerminal = target.oneWayLinks().length == 0;
                    spawnOneWayEdge(level, player, node.pos(), target.pos(), isTerminal);
                }
            }
        }
    }

    private static void visualizeRailwayNodesFor(ServerLevel level, ServerPlayer player) {
        RailwayNetworkSavedData data = RailwayNetworkSavedData.get(level);
        BlockPos playerPos = player.blockPosition();

        for (RailwayNode node : data.findNodesWithinRadius(playerPos, VISIBLE_RADIUS)) {
            spawnPoint(level, player, node.pos().getX() + 0.5, node.pos().getY() + 0.2, node.pos().getZ() + 0.5, ParticleTypes.END_ROD, 3);

            if (data.getRouteConfig(node.id()) != null) {
                DustParticleOptions originMarker = dustOptions(RAILWAY_ORIGIN_COLOR, RAILWAY_ORIGIN_SCALE);
                sendParticlesCompat(level, player, originMarker,
                        node.pos().getX() + 0.5, node.pos().getY() + 0.6, node.pos().getZ() + 0.5, 4, 0.05, 0.05, 0.05, 0.0);
            }

            for (int targetId : node.oneWayLinks()) {
                RailwayNode target = data.getNodes().get(targetId);
                if (target != null) {
                    boolean isTerminal = target.oneWayLinks().length == 0;
                    spawnOneWayEdge(level, player, node.pos(), target.pos(), isTerminal);
                }
            }
        }
    }

    private static void visualizeRailwayStopsFor(ServerLevel level, ServerPlayer player) {
        RailwayNetworkSavedData data = RailwayNetworkSavedData.get(level);
        DustParticleOptions options = dustOptions(RAILWAY_STOP_COLOR, RAILWAY_STOP_SCALE);
        for (RailwayStopMarker stop : data.findStopsWithinRadius(player.blockPosition(), VISIBLE_RADIUS)) {
            sendParticlesCompat(level, player, options,
                    stop.pos().getX() + 0.5, stop.pos().getY() + 0.7, stop.pos().getZ() + 0.5,
                    4, 0.1, 0.1, 0.1, 0.0);
        }
    }

    private static void visualizeSignalsFor(ServerLevel level, ServerPlayer player) {
        WaypointNetworkSavedData data = WaypointNetworkSavedData.get(level);
        BlockPos playerPos = player.blockPosition();
        long gameTime = level.getGameTime();

        for (TrafficSignalMarker signal : data.findSignalsWithinRadius(playerPos, VISIBLE_RADIUS)) {
            SignalGroupConfig group = data.getSignalGroup(signal.groupKey());
            boolean isGreen = group == null || group.isGreenAt(gameTime);
            DustParticleOptions options = dustOptions(
                    isGreen ? SIGNAL_GREEN_COLOR : SIGNAL_RED_COLOR, SIGNAL_PARTICLE_SCALE);
            sendParticlesCompat(level, player, options,
                    signal.pos().getX() + 0.5, signal.pos().getY() + 0.7, signal.pos().getZ() + 0.5,
                    4, 0.1, 0.1, 0.1, 0.0);
        }
    }

    private static void spawnOneWayEdge(ServerLevel level, ServerPlayer player, BlockPos from, BlockPos to, boolean isTerminal) {
        spawnLine(level, player, from, to);
        if (isTerminal) {
            DustParticleOptions targetMarker = dustOptions(ONE_WAY_TARGET_COLOR, ONE_WAY_TARGET_SCALE);
            sendParticlesCompat(level, player, targetMarker,
                    to.getX() + 0.5, to.getY() + 0.4, to.getZ() + 0.5, 3, 0.05, 0.05, 0.05, 0.0);
        }
    }

    private static void spawnLine(ServerLevel level, ServerPlayer player, BlockPos from, BlockPos to) {
        double dx = to.getX() - from.getX();
        double dy = to.getY() - from.getY();
        double dz = to.getZ() - from.getZ();
        double length = Math.sqrt(dx * dx + dy * dy + dz * dz);
        int samples = Math.max(1, (int) (length / LINK_SAMPLE_SPACING));
        for (int i = 1; i < samples; i++) {
            double t = (double) i / samples;
            spawnPoint(level, player,
                    from.getX() + 0.5 + dx * t,
                    from.getY() + 0.2 + dy * t,
                    from.getZ() + 0.5 + dz * t,
                    ParticleTypes.CRIT, 1);
        }
    }

    private static void spawnPoint(ServerLevel level, ServerPlayer player, double x, double y, double z,
                                    net.minecraft.core.particles.ParticleOptions particle, int count) {
        sendParticlesCompat(level, player, particle, x, y, z, count, 0.05, 0.05, 0.05, 0.0);
    }
}
