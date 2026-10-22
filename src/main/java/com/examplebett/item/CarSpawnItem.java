package com.examplebett.item;

import com.examplebett.AiObjectMod;
import com.examplebett.compat.ModEntity;
import com.examplebett.entity.AiObjectModEntities;
import com.examplebett.entity.CarEntity;
import com.examplebett.sound.AiObjectModSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class CarSpawnItem extends Item {

    private static final float DOOR_LOCK_HIGH_PITCH = 1.2f;

    private final String modelId;

    public CarSpawnItem(String modelId, Properties properties) {
        super(properties);
        this.modelId = modelId;
    }

    public String getModelId() {
        return modelId;
    }

    private void spawnCar(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide()) {
            CarEntity car = new CarEntity(level, ModEntity.getX(player), ModEntity.getY(player), ModEntity.getZ(player), modelId);

            car.setPlacedFromItem(CarEntity.PLACED_FROM_SPAWN_ITEM);
            car.setItemIcon(VehicleItemIcons.iconOfSpawnStack(stack));
            level.addFreshEntity(car);

            SoundEvent lockSound = level.random.nextBoolean() ? AiObjectModSounds.DOOR_LOCK_1 : AiObjectModSounds.DOOR_LOCK_2;
            float pitch = level.random.nextBoolean() ? 1.0f : DOOR_LOCK_HIGH_PITCH;
            level.playSound(null, ModEntity.getX(car), ModEntity.getY(car), ModEntity.getZ(car), lockSound, SoundSource.NEUTRAL, 1.0f, pitch);

            if (!com.examplebett.compat.ModPlayer.hasInstabuild(player)) {
                stack.shrink(1);
            }
        }
    }

    //? if >=1.21.2 {
    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        spawnCar(level, player, hand);
        return InteractionResult.SUCCESS;
    }
    //?} else {
    /*@Override
    public net.minecraft.world.InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        spawnCar(level, player, hand);
        return net.minecraft.world.InteractionResultHolder.success(player.getItemInHand(hand));
    }
    *///?}
}
