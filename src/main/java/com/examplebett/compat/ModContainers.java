package com.examplebett.compat;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

public final class ModContainers {
    private ModContainers() {
    }

    public static void load(CompoundTag tag, String key, SimpleContainer container, Entity entity) {
        container.clearContent();
        if (!tag.contains(key)) {
            return;
        }
        NonNullList<ItemStack> items = NonNullList.withSize(container.getContainerSize(), ItemStack.EMPTY);

        //? if >=1.21.5 && <1.21.6 {
        /*ContainerHelper.loadAllItems(tag.getCompoundOrEmpty(key), items, entity.registryAccess());*/
        //?}
        //? if >=1.20.5 && <1.21.5 {
        /*ContainerHelper.loadAllItems(tag.getCompound(key), items, entity.registryAccess());*/
        //?}
        //? if <1.20.5 {
        /*ContainerHelper.loadAllItems(tag.getCompound(key), items);*/
        //?}
        for (int i = 0; i < items.size(); i++) {
            container.setItem(i, items.get(i));
        }
    }

    public static void save(CompoundTag tag, String key, SimpleContainer container, Entity entity) {
        if (container.isEmpty()) {
            return;
        }
        NonNullList<ItemStack> items = NonNullList.withSize(container.getContainerSize(), ItemStack.EMPTY);
        for (int i = 0; i < items.size(); i++) {
            items.set(i, container.getItem(i));
        }
        CompoundTag child = new CompoundTag();
        //? if >=1.20.5 && <1.21.6 {
        /*ContainerHelper.saveAllItems(child, items, entity.registryAccess());*/
        //?}
        //? if <1.20.5 {
        /*ContainerHelper.saveAllItems(child, items);*/
        //?}
        tag.put(key, child);
    }
}
