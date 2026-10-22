package com.examplebett.client.render;

import com.examplebett.entity.CarEntity;
import com.examplebett.item.AiObjectModItems;
import com.examplebett.item.CarSpawnItem;
import com.examplebett.item.SmartDispatchAppItem;
import com.examplebett.item.VehicleItemIcons;
import com.examplebett.util.AiCarPool;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class LegacyItemIconModels {

    public static final int CMD_KEY = 1;
    public static final int CMD_SMART_KEY = 3;

    public static final int CMD_PHONE_BASE = 1001;

    private LegacyItemIconModels() {
    }

    public static ItemStack proxyStack(ItemStack stack) {
        //? if <1.21.4 {
        /*if (stack.isEmpty()) {
            return null;
        }
        Item item = stack.getItem();
        if (item == AiObjectModItems.SMART_DISPATCH_APP) {
            String model = SmartDispatchAppItem.phoneModelOf(stack);
            int index = java.util.Arrays.asList(SmartDispatchAppItem.PHONE_MODELS).indexOf(model);
            if (index < 0 || SmartDispatchAppItem.DEFAULT_PHONE_MODEL.equals(model)) {
                return null;
            }
            return withCustomModelData(new ItemStack(AiObjectModItems.SMART_DISPATCH_APP), CMD_PHONE_BASE + index);
        }
        if (item == AiObjectModItems.BOXED_CAR) {
            net.minecraft.nbt.CompoundTag tag = VehicleItemIcons.customTagOf(stack);
            int icon = tag.contains("ItemIcon") ? tag.getInt("ItemIcon") : CarEntity.ITEM_ICON_BOXED;
            if (icon == CarEntity.ITEM_ICON_KEY) {
                return withCustomModelData(new ItemStack(AiObjectModItems.BOXED_CAR), CMD_KEY);
            }
            if (icon == CarEntity.ITEM_ICON_SMART_KEY) {
                return withCustomModelData(new ItemStack(AiObjectModItems.BOXED_CAR), CMD_SMART_KEY);
            }
            if (icon == CarEntity.ITEM_ICON_VEHICLE) {
                Item spawnItem = AiCarPool.spawnItemFor(tag.getString("ModelId"));
                return spawnItem != null ? new ItemStack(spawnItem) : null;
            }
            return null;
        }
        if (item instanceof CarSpawnItem) {
            int icon = VehicleItemIcons.iconOfSpawnStack(stack);
            if (icon == CarEntity.ITEM_ICON_KEY) {
                return withCustomModelData(new ItemStack(AiObjectModItems.BOXED_CAR), CMD_KEY);
            }
            if (icon == CarEntity.ITEM_ICON_SMART_KEY) {
                return withCustomModelData(new ItemStack(AiObjectModItems.BOXED_CAR), CMD_SMART_KEY);
            }
            if (icon == CarEntity.ITEM_ICON_BOXED) {
                return new ItemStack(AiObjectModItems.BOXED_CAR);
            }
        }*/
        //?}
        return null;
    }

    //? if >=1.20.5 && <1.21.4 {
    /*private static ItemStack withCustomModelData(ItemStack stack, int value) {
        stack.set(net.minecraft.core.component.DataComponents.CUSTOM_MODEL_DATA,
                new net.minecraft.world.item.component.CustomModelData(value));
        return stack;
    }*/
    //?}
    //? if <1.20.5 {
    /*private static ItemStack withCustomModelData(ItemStack stack, int value) {
        stack.getOrCreateTag().putInt("CustomModelData", value);
        return stack;
    }*/
    //?}
}
