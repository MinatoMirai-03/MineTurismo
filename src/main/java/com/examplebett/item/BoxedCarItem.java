package com.examplebett.item;
import com.examplebett.compat.ModEntity;
import com.examplebett.compat.ModText;

import com.examplebett.entity.CarEntity;
import com.examplebett.sound.AiObjectModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
//? if >=1.20.5 {
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
//?}
//? if >=1.21.5 {
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;
//?}

import java.util.List;

public class BoxedCarItem extends Item {

    private static final float DOOR_LOCK_HIGH_PITCH = 1.2f;

    public BoxedCarItem(Properties properties) {
        super(properties);
    }

    private static CompoundTag getCustomTag(ItemStack stack) {
        //? if >=1.20.5 {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        //?} else
        /*return stack.getOrCreateTag();*/
    }

    private static String getStringOr(CompoundTag tag, String key, String fallback) {
        //? if >=1.21.5 {
        return tag.getStringOr(key, fallback);
        //?} else
        /*return tag.contains(key) ? tag.getString(key) : fallback;*/
    }

    public static CompoundTag customizationTag(ItemStack stack) {
        return getCustomTag(stack);
    }

    public static boolean hasCar(ItemStack stack) {
        return stack.getItem() instanceof BoxedCarItem && !getStringOr(getCustomTag(stack), "ModelId", "").isEmpty();
    }

    @Override
    //? if >=1.21.2 {
    public net.minecraft.world.InteractionResult use(Level level, Player player, InteractionHand hand) {
    //?} else
    /*public net.minecraft.world.InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {*/
        ItemStack stack = player.getItemInHand(hand);
        CompoundTag tag = getCustomTag(stack);
        String modelId = getStringOr(tag, "ModelId", "");
        if (modelId.isEmpty()) {
            //? if >=1.21.2 {
            return net.minecraft.world.InteractionResult.FAIL;
            //?} else
            /*return net.minecraft.world.InteractionResultHolder.fail(stack);*/
        }

        if (!level.isClientSide()) {
            CarEntity car = new CarEntity(level, ModEntity.getX(player), ModEntity.getY(player), ModEntity.getZ(player), modelId);

            car.importCustomizationTag(tag);

            car.setPlacedFromItem(CarEntity.PLACED_FROM_BOXED_CAR);
            level.addFreshEntity(car);

            SoundEvent lockSound = level.random.nextBoolean() ? AiObjectModSounds.DOOR_LOCK_1 : AiObjectModSounds.DOOR_LOCK_2;
            float pitch = level.random.nextBoolean() ? 1.0f : DOOR_LOCK_HIGH_PITCH;
            level.playSound(null, ModEntity.getX(car), ModEntity.getY(car), ModEntity.getZ(car), lockSound, SoundSource.NEUTRAL, 1.0f, pitch);

            if (!com.examplebett.compat.ModPlayer.hasInstabuild(player)) {
                stack.shrink(1);
            }
        }

        //? if >=1.21.2 {
        return net.minecraft.world.InteractionResult.SUCCESS;
        //?} else
        /*return net.minecraft.world.InteractionResultHolder.success(stack);*/
    }

    //? if >=1.20.5 {
    //? if >=1.21.5 {
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay,
                                 Consumer<Component> tooltipAdder, TooltipFlag flag) {
        CompoundTag tag = getCustomTag(stack);
        String modelId = getStringOr(tag, "ModelId", "");
        if (!modelId.isEmpty()) {
            tooltipAdder.accept(ModText.translatable("item.mineturismo." + modelId + "_spawn")
                    .withStyle(ChatFormatting.GRAY));
        }
    }
    //?} else {
    /*@Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        CompoundTag tag = getCustomTag(stack);
        String modelId = getStringOr(tag, "ModelId", "");
        if (!modelId.isEmpty()) {
            tooltip.add(ModText.translatable("item.mineturismo." + modelId + "_spawn")
                    .withStyle(ChatFormatting.GRAY));
        }
    }*/
    //?}
    //?} else {
    /*@Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        CompoundTag tag = getCustomTag(stack);
        String modelId = getStringOr(tag, "ModelId", "");
        if (!modelId.isEmpty()) {
            tooltip.add(ModText.translatable("item.mineturismo." + modelId + "_spawn")
                    .withStyle(ChatFormatting.GRAY));
        }
    }*/
    //?}
}
