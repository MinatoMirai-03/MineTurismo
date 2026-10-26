package com.examplebett.item;
import com.examplebett.compat.ModPlayer;
import com.examplebett.compat.ModText;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class TowRopeItem extends Item {

    public TowRopeItem(Properties properties) {
        super(properties);
    }

    private void showHintIfClient(Level level, Player player) {
        if (level.isClientSide()) {
            ModPlayer.displayActionBar(player, ModText.translatable("item.mineturismo.tow_rope.hint"));
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
}
