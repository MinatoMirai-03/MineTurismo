package com.examplebett.item;
import com.examplebett.compat.ModPlayer;
import com.examplebett.compat.ModText;

import com.examplebett.compat.ModNetworking;
import com.examplebett.network.OpenRailwaySpawnSetupPayload;
import com.examplebett.waypoint.RailwayNetworkSavedData;
import com.examplebett.waypoint.RailwayNode;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.UUID;

public class RailwayWaypointMarkerItem extends Item {

    private static final double SNAP_RADIUS = 1.5;

    private void showHintIfClient(Level level, Player player) {
        if (level.isClientSide()) {
            ModPlayer.displayActionBar(player, ModText.translatable("item.mineturismo.railway_marker.hint"));
        }
    }

    public RailwayWaypointMarkerItem(Properties properties) {
        super(properties);
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
        UUID playerId = player.getUUID();
        RailwayNode nearby = data.findNear(targetPos, SNAP_RADIUS);

        if (player.isShiftKeyDown()) {
            if (nearby != null) {
                Integer chainId = data.getChainWaypoint(playerId);
                boolean linked = chainId != null && chainId != nearby.id() && data.contains(chainId);
                if (linked) {
                    data.linkOneWay(chainId, nearby.id());
                }
                data.setChainWaypoint(playerId, nearby.id());
                ModPlayer.displayActionBar(player, ModText.translatable(linked
                        ? "item.mineturismo.railway_marker.linked_existing"
                        : "item.mineturismo.railway_marker.selected"));
            } else {
                data.clearChainWaypoint(playerId);
                ModPlayer.displayActionBar(player, ModText.translatable("item.mineturismo.railway_marker.chain_reset"));
            }
            return InteractionResult.SUCCESS;
        }

        if (nearby != null) {
            data.removeWaypoint(nearby.id());
            data.clearChainWaypointIfMatches(playerId, nearby.id());
            ModPlayer.displayActionBar(player, ModText.translatable("item.mineturismo.railway_marker.removed"));
            return InteractionResult.SUCCESS;
        }

        Integer chainId = data.getChainWaypoint(playerId);
        boolean isRouteStart = chainId == null || !data.contains(chainId);

        int newId = data.addWaypoint(targetPos, RailwayNode.DEFAULT_SPEED_KMH);
        boolean linked = !isRouteStart;
        if (linked) {
            data.linkOneWay(chainId, newId);
        }
        data.setChainWaypoint(playerId, newId);
        ModPlayer.displayActionBar(player, ModText.translatable(linked
                ? "item.mineturismo.railway_marker.placed_linked"
                : "item.mineturismo.railway_marker.placed"));

        if (isRouteStart && player instanceof ServerPlayer serverPlayer) {
            OpenRailwaySpawnSetupPayload openPayload = new OpenRailwaySpawnSetupPayload(newId);
            //? if >=1.20.5 {
            ModNetworking.sendToClient(serverPlayer, openPayload);
            //?} else
            /*ModNetworking.sendToClient(serverPlayer, OpenRailwaySpawnSetupPayload.ID, openPayload, OpenRailwaySpawnSetupPayload::write);*/
        }
        return InteractionResult.SUCCESS;
    }
}
