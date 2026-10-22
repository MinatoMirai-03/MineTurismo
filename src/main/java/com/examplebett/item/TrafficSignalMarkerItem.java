package com.examplebett.item;
import com.examplebett.compat.ModPlayer;
import com.examplebett.compat.ModText;

import com.examplebett.compat.ModNetworking;
import com.examplebett.network.OpenSignalSettingsPayload;
import com.examplebett.waypoint.SignalGroupConfig;
import com.examplebett.waypoint.TrafficSignalMarker;
import com.examplebett.waypoint.WaypointNetworkSavedData;
import com.examplebett.waypoint.WaypointNode;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;

import java.util.HashSet;
import java.util.Set;

public class TrafficSignalMarkerItem extends Item {

    private static final double SIGNAL_REMOVE_RADIUS = 1.5;

    private static final double MAX_EDGE_SNAP_DISTANCE = 2.5;

    private static final double NEARBY_NODE_SEARCH_RADIUS = 48.0;

    public TrafficSignalMarkerItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null) {
            return InteractionResult.PASS;
        }
        if (!(context.getLevel() instanceof ServerLevel serverLevel)) {
            return InteractionResult.SUCCESS;
        }

        BlockPos targetPos = context.getClickedPos().relative(context.getClickedFace());
        WaypointNetworkSavedData data = WaypointNetworkSavedData.get(serverLevel);

        TrafficSignalMarker nearby = data.findSignalNear(targetPos, SIGNAL_REMOVE_RADIUS);
        if (nearby != null) {
            if (player.isShiftKeyDown()) {
                this.openExistingSignalSettings(data, player, nearby);
            } else {
                data.removeSignal(nearby.id());
                Integer editingSignalId = data.getEditingSignalId(player.getUUID());
                if (editingSignalId != null && editingSignalId == nearby.id()) {
                    data.clearEditingSignalId(player.getUUID());
                }
                ModPlayer.displayActionBar(player, ModText.translatable("item.mineturismo.traffic_signal_marker.removed"));
            }
            return InteractionResult.SUCCESS;
        }

        EdgeMatch match = findNearestOneWayEdge(data, targetPos);
        if (match == null) {
            ModPlayer.displayActionBar(player, ModText.translatable("item.mineturismo.traffic_signal_marker.no_nearby_one_way"));
            return InteractionResult.SUCCESS;
        }

        WaypointNetworkSavedData.PendingSignalPen pen = data.getPendingSignalPen(player.getUUID());
        int newId = data.addSignal(targetPos, match.fromId, match.toId, match.stopDistanceFromEnd, pen);
        data.setEditingSignalId(player.getUUID(), newId);
        ModPlayer.displayActionBar(player, pen.intersectionName().isEmpty()
                ? ModText.translatable("item.mineturismo.traffic_signal_marker.placed_unnamed")
                : ModText.translatable("item.mineturismo.traffic_signal_marker.placed",
                        pen.intersectionName(), String.valueOf(pen.direction())));
        return InteractionResult.SUCCESS;
    }

    private void openExistingSignalSettings(WaypointNetworkSavedData data, Player player, TrafficSignalMarker nearby) {
        SignalGroupConfig group = data.getSignalGroup(nearby.groupKey());
        if (group == null) {
            return;
        }
        data.setEditingSignalId(player.getUUID(), nearby.id());
        data.setPendingSignalPen(player.getUUID(), new WaypointNetworkSavedData.PendingSignalPen(
                group.intersectionName(), group.direction(), group.greenTicks(), group.redTicks(), group.phaseOffsetTicks()));
        if (player instanceof ServerPlayer serverPlayer) {
            OpenSignalSettingsPayload openPayload = new OpenSignalSettingsPayload(nearby.id(),
                    group.intersectionName(), String.valueOf(group.direction()),
                    group.greenTicks(), group.redTicks(), group.phaseOffsetTicks());
            //? if >=1.20.5 {
            ModNetworking.sendToClient(serverPlayer, openPayload);
            //?} else
            /*ModNetworking.sendToClient(serverPlayer, OpenSignalSettingsPayload.ID, openPayload, OpenSignalSettingsPayload::write);*/
        }
    }

    private record EdgeMatch(int fromId, int toId, double stopDistanceFromEnd, double matchDistance) {
    }

    private static EdgeMatch findNearestOneWayEdge(WaypointNetworkSavedData data, BlockPos targetPos) {
        EdgeMatch best = null;
        double bestDist = MAX_EDGE_SNAP_DISTANCE;
        Set<Long> checkedEdges = new HashSet<>();

        for (WaypointNode nearby : data.findNodesWithinRadius(targetPos, NEARBY_NODE_SEARCH_RADIUS)) {
            for (int toId : nearby.oneWayLinks()) {
                EdgeMatch candidate = checkEdge(data, targetPos, nearby.id(), toId, bestDist, checkedEdges);
                if (candidate != null) {
                    best = candidate;
                    bestDist = candidate.matchDistance();
                }
            }
            for (int fromId : data.getIncomingOneWayLinks(nearby.id())) {
                EdgeMatch candidate = checkEdge(data, targetPos, fromId, nearby.id(), bestDist, checkedEdges);
                if (candidate != null) {
                    best = candidate;
                    bestDist = candidate.matchDistance();
                }
            }
        }
        return best;
    }

    private static EdgeMatch checkEdge(WaypointNetworkSavedData data, BlockPos targetPos, int fromId, int toId,
                                        double bestDist, Set<Long> checkedEdges) {
        long key = ((long) fromId << 32) | (toId & 0xFFFFFFFFL);
        if (!checkedEdges.add(key)) {
            return null;
        }
        WaypointNode from = data.getNodes().get(fromId);
        WaypointNode to = data.getNodes().get(toId);
        if (from == null || to == null) {
            return null;
        }

        double targetX = targetPos.getX() + 0.5;
        double targetZ = targetPos.getZ() + 0.5;
        double lineStartX = from.pos().getX() + 0.5;
        double lineStartZ = from.pos().getZ() + 0.5;
        double lineEndX = to.pos().getX() + 0.5;
        double lineEndZ = to.pos().getZ() + 0.5;

        double segX = lineEndX - lineStartX;
        double segZ = lineEndZ - lineStartZ;
        double segLenSq = segX * segX + segZ * segZ;
        if (segLenSq < 1.0E-6) {
            return null;
        }
        double segLen = Math.sqrt(segLenSq);
        double relX = targetX - lineStartX;
        double relZ = targetZ - lineStartZ;
        double projT = Math.max(0.0, Math.min(1.0, (relX * segX + relZ * segZ) / segLenSq));
        double closestX = lineStartX + segX * projT;
        double closestZ = lineStartZ + segZ * projT;
        double dx = targetX - closestX;
        double dz = targetZ - closestZ;
        double dist = Math.sqrt(dx * dx + dz * dz);

        if (dist >= bestDist) {
            return null;
        }
        double stopDistanceFromEnd = segLen * (1.0 - projT);
        return new EdgeMatch(fromId, toId, stopDistanceFromEnd, dist);
    }
}
