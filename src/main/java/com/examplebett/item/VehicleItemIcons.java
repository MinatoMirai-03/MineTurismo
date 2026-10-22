package com.examplebett.item;

import com.examplebett.AiObjectMod;
import com.examplebett.compat.ModText;
import com.examplebett.entity.CarEntity;
import com.examplebett.util.AiCarPool;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
//? if >=1.20.5 {
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
//?}
//? if >=1.21.4 {
import net.minecraft.core.registries.BuiltInRegistries;
//?}

public final class VehicleItemIcons {

    public static final String TAG_ICON = "MineturismoItemIcon";

    private VehicleItemIcons() {
    }

    public static void applyToSpawnStack(ItemStack stack, int icon) {
        //? if >=1.21.4 {
        if (icon == CarEntity.ITEM_ICON_KEY) {
            stack.set(DataComponents.ITEM_MODEL, AiObjectMod.id("vehicle_spawn_key"));
        } else if (icon == CarEntity.ITEM_ICON_BOXED) {
            stack.set(DataComponents.ITEM_MODEL, AiObjectMod.id("boxed_car"));
        } else if (icon == CarEntity.ITEM_ICON_SMART_KEY) {
            stack.set(DataComponents.ITEM_MODEL, AiObjectMod.id("vehicle_smart_key"));
        }
        //?}
        //? if <1.21.4 {
        /*if (icon != CarEntity.ITEM_ICON_VEHICLE) {
            CompoundTag tag = customTagOf(stack);
            tag.putInt(TAG_ICON, icon);
            setCustomTag(stack, tag);
        }*/
        //?}
    }

    //? if >=1.20.5 && <1.21.4 {
    /*public static CompoundTag customTagOf(ItemStack stack) {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
    }

    private static void setCustomTag(ItemStack stack, CompoundTag tag) {
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }*/
    //?}
    //? if <1.20.5 {
    /*public static CompoundTag customTagOf(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null ? tag.copy() : new CompoundTag();
    }

    private static void setCustomTag(ItemStack stack, CompoundTag tag) {
        stack.setTag(tag);
    }*/
    //?}

    public static void applyToBoxedStack(ItemStack stack, String modelId, int icon) {
        //? if >=1.21.4 {
        if (icon == CarEntity.ITEM_ICON_KEY) {
            stack.set(DataComponents.ITEM_MODEL, AiObjectMod.id("vehicle_spawn_key"));
        } else if (icon == CarEntity.ITEM_ICON_SMART_KEY) {
            stack.set(DataComponents.ITEM_MODEL, AiObjectMod.id("vehicle_smart_key"));
        } else if (icon == CarEntity.ITEM_ICON_VEHICLE) {
            Item spawnItem = AiCarPool.spawnItemFor(modelId);
            if (spawnItem != null) {
                stack.set(DataComponents.ITEM_MODEL, BuiltInRegistries.ITEM.getKey(spawnItem));
            }
        }
        //?}
    }

    public static int iconOfSpawnStack(ItemStack stack) {
        //? if >=1.21.4 {
        Object model = stack.get(DataComponents.ITEM_MODEL);
        if (AiObjectMod.id("vehicle_spawn_key").equals(model)) {
            return CarEntity.ITEM_ICON_KEY;
        }
        if (AiObjectMod.id("boxed_car").equals(model)) {
            return CarEntity.ITEM_ICON_BOXED;
        }
        if (AiObjectMod.id("vehicle_smart_key").equals(model)) {
            return CarEntity.ITEM_ICON_SMART_KEY;
        }
        //?}
        //? if <1.21.4 {
        /*CompoundTag tag = customTagOf(stack);
        if (tag.contains(TAG_ICON)) {
            return tag.getInt(TAG_ICON);
        }*/
        //?}
        return CarEntity.ITEM_ICON_VEHICLE;
    }

    public static ItemStack createBoxedStack(CarEntity car, String name) {
        ItemStack stack = new ItemStack(AiObjectModItems.BOXED_CAR);
        CompoundTag tag = car.exportCustomizationTag();
        //? if >=1.20.5 {
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        //?} else
        /*stack.setTag(tag);*/
        net.minecraft.network.chat.Component displayName = name == null || name.isEmpty()
                ? ModText.translatable("item.mineturismo." + car.getModelId() + "_spawn")
                : ModText.literal(name);
        //? if >=1.20.5 {
        stack.set(DataComponents.CUSTOM_NAME, displayName);
        //?} else
        /*stack.setHoverName(displayName);*/
        applyToBoxedStack(stack, car.getModelId(), car.getItemIcon());
        return stack;
    }

    public static ItemStack createBoxedStack(CompoundTag tag) {
        //? if >=1.21.5 {
        String modelId = tag.getStringOr("ModelId", "");
        int icon = tag.getIntOr("ItemIcon", CarEntity.ITEM_ICON_VEHICLE);
        //?} else {
        /*String modelId = tag.getString("ModelId");
        int icon = tag.contains("ItemIcon") ? tag.getInt("ItemIcon") : CarEntity.ITEM_ICON_VEHICLE;*/
        //?}
        ItemStack stack = new ItemStack(AiObjectModItems.BOXED_CAR);
        //? if >=1.20.5 {
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        stack.set(DataComponents.CUSTOM_NAME, ModText.translatable("item.mineturismo." + modelId + "_spawn"));
        //?} else {
        /*stack.setTag(tag);
        stack.setHoverName(ModText.translatable("item.mineturismo." + modelId + "_spawn"));*/
        //?}
        applyToBoxedStack(stack, modelId, icon);
        return stack;
    }

    public static ItemStack dropStackFor(CarEntity car) {
        if (car.getPlacedFromItem() == CarEntity.PLACED_FROM_BOXED_CAR) {
            return createBoxedStack(car, null);
        }
        if (car.getPlacedFromItem() == CarEntity.PLACED_FROM_SPAWN_ITEM) {
            Item spawnItem = AiCarPool.spawnItemFor(car.getModelId());
            if (spawnItem == null) {
                return ItemStack.EMPTY;
            }
            ItemStack stack = new ItemStack(spawnItem);
            applyToSpawnStack(stack, car.getItemIcon());
            return stack;
        }
        return ItemStack.EMPTY;
    }
}
