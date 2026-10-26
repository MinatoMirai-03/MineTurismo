package com.examplebett.item;
import com.examplebett.compat.ModPlayer;
import com.examplebett.compat.ModText;

import com.examplebett.waypoint.WaypointNetworkSavedData;
import com.examplebett.waypoint.WaypointNode;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.UUID;
import java.util.function.Consumer;

public class WaypointMarkerItem extends Item {

    private static final double SNAP_RADIUS = 1.5;

    public static Consumer<Player> trafficSettingsMenuOpener;

    public WaypointMarkerItem(Properties properties) {
        super(properties);
    }

    private void openSettingsIfClient(Level level, Player player) {
        if (level.isClientSide() && trafficSettingsMenuOpener != null) {
            trafficSettingsMenuOpener.accept(player);
        }
    }

    //? if >=1.21.2 {
    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        openSettingsIfClient(level, player);
        return InteractionResult.SUCCESS;
    }
    //?} else {
    /*@Override
    public net.minecraft.world.InteractionResultHolder<net.minecraft.world.item.ItemStack> use(Level level, Player player, InteractionHand hand) {
        openSettingsIfClient(level, player);
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
        WaypointNetworkSavedData data = WaypointNetworkSavedData.get(serverLevel);
        UUID playerId = player.getUUID();
        WaypointNode nearby = data.findNear(targetPos, SNAP_RADIUS);

        if (player.isShiftKeyDown()) {
            if (nearby != null) {
                Integer chainId = data.getChainWaypoint(playerId);
                boolean linked = chainId != null && chainId != nearby.id() && data.contains(chainId);
                if (linked) {
                    data.link(chainId, nearby.id());
                }
                data.setChainWaypoint(playerId, nearby.id());
                ModPlayer.displayActionBar(player, ModText.translatable(linked
                        ? "item.mineturismo.waypoint_marker.linked_existing"
                        : "item.mineturismo.waypoint_marker.selected"));
            } else {
                data.clearChainWaypoint(playerId);
                ModPlayer.displayActionBar(player, ModText.translatable("item.mineturismo.waypoint_marker.chain_reset"));
            }
            return InteractionResult.SUCCESS;
        }

        if (nearby != null) {
            data.removeWaypoint(nearby.id());
            data.clearChainWaypointIfMatches(playerId, nearby.id());
            ModPlayer.displayActionBar(player, ModText.translatable("item.mineturismo.waypoint_marker.removed"));
            return InteractionResult.SUCCESS;
        }

        int newId = data.addWaypoint(targetPos, data.getPenSpeedKmh(playerId));
        Integer chainId = data.getChainWaypoint(playerId);
        boolean linked = chainId != null && data.contains(chainId);
        if (linked) {
            data.link(chainId, newId);
        }
        data.setChainWaypoint(playerId, newId);
        ModPlayer.displayActionBar(player, ModText.translatable(linked
                ? "item.mineturismo.waypoint_marker.placed_linked"
                : "item.mineturismo.waypoint_marker.placed"));
        return InteractionResult.SUCCESS;
    }
}
