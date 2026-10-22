package com.examplebett.item;

import com.examplebett.compat.ModEntity;
import com.examplebett.entity.BreakableTireStackEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.function.Function;

public class BreakableTireStackSpawnItem extends Item {

    private final Function<Level, BreakableTireStackEntity> factory;

    private final int count;
    private final double stepHeight;

    public BreakableTireStackSpawnItem(Function<Level, BreakableTireStackEntity> factory, Properties properties) {
        this(factory, 1, 0.0, properties);
    }

    public BreakableTireStackSpawnItem(Function<Level, BreakableTireStackEntity> factory, int count, double stepHeight, Properties properties) {
        super(properties);
        this.factory = factory;
        this.count = count;
        this.stepHeight = stepHeight;
    }

    static float placementYRot(Player player) {

        //? if >=1.17 {
        float playerYRot = player.getYRot();
        //?} else
        /*float playerYRot = player.yRot;*/
        return -playerYRot;
    }

    private void spawnEntity(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide()) {
            for (int i = 0; i < this.count; i++) {
                BreakableTireStackEntity entity = this.factory.apply(level);
                entity.setPos(ModEntity.getX(player), ModEntity.getY(player) + i * this.stepHeight, ModEntity.getZ(player));
                entity.setYRot(placementYRot(player));
                level.addFreshEntity(entity);
            }

            if (!com.examplebett.compat.ModPlayer.hasInstabuild(player)) {
                stack.shrink(1);
            }
        }
    }

    //? if >=1.21.2 {
    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        spawnEntity(level, player, hand);
        return InteractionResult.SUCCESS;
    }
    //?} else {
    /*@Override
    public net.minecraft.world.InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        spawnEntity(level, player, hand);
        return net.minecraft.world.InteractionResultHolder.success(player.getItemInHand(hand));
    }
    *///?}
}
