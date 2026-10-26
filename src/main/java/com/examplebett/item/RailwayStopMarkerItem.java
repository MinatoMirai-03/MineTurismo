package com.examplebett.item;
import com.examplebett.compat.ModPlayer;
import com.examplebett.compat.ModText;

import com.examplebett.waypoint.RailwayNetworkSavedData;
import com.examplebett.waypoint.RailwayNode;
import com.examplebett.waypoint.RailwayStopMarker;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.HashSet;
import java.util.Set;

public class RailwayStopMarkerItem extends Item {

    private static final double STOP_REMOVE_RADIUS = 1.5;

    private static final double MAX_EDGE_SNAP_DISTANCE = 2.5;

    private static final double NEARBY_NODE_SEARCH_RADIUS = 48.0;

    public RailwayStopMarkerItem(Properties properties) {
        super(properties);
    }

    private void showHintIfClient(Level level, Player player) {
        if (level.isClientSide()) {
            ModPlayer.displayActionBar(player, ModText.translatable("item.mineturismo.railway_stop_marker.hint"));
        }
    }

    //? if >=1.21.2 {
    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        showHintIfClient(level, player);
        return InteractionResult.SUCCESS;
    }
    //?} else {
    /*@Override
    public net.minecraft.world.InteractionResultHolder<net.minecraft.world.item.ItemStack> use(Level level, Player player, InteractionHand hand) {
        showHintIfClient(level, player);
        return net.minecraft.world.InteractionResultHolder.success(player.getItemInHand(hand));
    }
    *///?}

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
        RailwayNetworkSavedData data = RailwayNetworkSavedData.get(serverLevel);

        RailwayStopMarker nearby = data.findStopNear(targetPos, STOP_REMOVE_RADIUS);
        if (nearby != null) {
            data.removeStop(nearby.id());
            ModPlayer.displayActionBar(player, ModText.translatable("item.mineturismo.railway_stop_marker.removed"));
            return InteractionResult.SUCCESS;
        }

        EdgeMatch match = findNearestRailwayEdge(data, targetPos);
        if (match == null) {
            ModPlayer.displayActionBar(player, ModText.translatable("item.mineturismo.railway_stop_marker.no_nearby_railway"));
            return InteractionResult.SUCCESS;
        }

        data.addStop(targetPos, match.fromId, match.toId, match.stopDistanceFromEnd);
        ModPlayer.displayActionBar(player, ModText.translatable("item.mineturismo.railway_stop_marker.placed"));
        return InteractionResult.SUCCESS;
    }

    private record EdgeMatch(int fromId, int toId, double stopDistanceFromEnd, double matchDistance) {
    }

    private static EdgeMatch findNearestRailwayEdge(RailwayNetworkSavedData data, BlockPos targetPos) {
        EdgeMatch best = null;
        double bestDist = MAX_EDGE_SNAP_DISTANCE;
        Set<Long> checkedEdges = new HashSet<>();

        for (RailwayNode nearby : data.findNodesWithinRadius(targetPos, NEARBY_NODE_SEARCH_RADIUS)) {
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

    private static EdgeMatch checkEdge(RailwayNetworkSavedData data, BlockPos targetPos, int fromId, int toId,
                                        double bestDist, Set<Long> checkedEdges) {
        long key = ((long) fromId << 32) | (toId & 0xFFFFFFFFL);
        if (!checkedEdges.add(key)) {
            return null;
        }
        RailwayNode from = data.getNodes().get(fromId);
        RailwayNode to = data.getNodes().get(toId);
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
