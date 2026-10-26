package com.examplebett.compat;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

public final class ModPlayer {

    public static Inventory getInventory(Player player) {
        //? if >=1.17 {
        return player.getInventory();
        //?} else
        /*return player.inventory;*/
    }

    public static boolean hasInstabuild(Player player) {
        //? if >=1.17 {
        return player.getAbilities().instabuild;
        //?} else
        /*return player.abilities.instabuild;*/
    }

    public static void displayActionBar(Player player, Component message) {

        //? if >=26.1 {
        /*player.sendOverlayMessage(message);*/
        //?} else
        player.displayClientMessage(message, true);
    }

    public static boolean hasGamemasterPermission(Player player) {
        //? if >=1.21.11 {
        /*return player.permissions().hasPermission(net.minecraft.server.permissions.Permissions.COMMANDS_GAMEMASTER);*/
        //?} else
        return player.hasPermissions(2);
    }

    private ModPlayer() {
    }
}
