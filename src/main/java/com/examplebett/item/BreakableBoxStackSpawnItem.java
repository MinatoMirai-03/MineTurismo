package com.examplebett.item;

import com.examplebett.compat.ModEntity;
import com.examplebett.entity.BreakableBoxEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class BreakableBoxStackSpawnItem extends Item {

    private static final double BOX_HEIGHT = 0.7434;
    private static final int STACK_COUNT = 5;

    private final EntityType<BreakableBoxEntity> entityType;
    private final String modelId;

    public BreakableBoxStackSpawnItem(EntityType<BreakableBoxEntity> entityType, String modelId, Properties properties) {
        super(properties);
        this.entityType = entityType;
        this.modelId = modelId;
    }

    private void spawnStack(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide()) {
            for (int i = 0; i < STACK_COUNT; i++) {
                BreakableBoxEntity entity = new BreakableBoxEntity(this.entityType, level, this.modelId);
                entity.setPos(ModEntity.getX(player), ModEntity.getY(player) + i * BOX_HEIGHT, ModEntity.getZ(player));
                entity.setYRot(BreakableTireStackSpawnItem.placementYRot(player));
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
        spawnStack(level, player, hand);
        return InteractionResult.SUCCESS;
    }
    //?} else {
    /*@Override
    public net.minecraft.world.InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        spawnStack(level, player, hand);
        return net.minecraft.world.InteractionResultHolder.success(player.getItemInHand(hand));
    }
    *///?}
}
