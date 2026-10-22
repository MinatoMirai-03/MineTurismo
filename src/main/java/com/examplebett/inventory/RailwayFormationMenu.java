package com.examplebett.inventory;

import com.examplebett.item.AiObjectModItems;
import com.examplebett.item.BoxedCarItem;
import com.examplebett.waypoint.RailwayNetworkSavedData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
//? if >=1.20.5 {
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
//?}

public class RailwayFormationMenu extends AbstractContainerMenu {

    public static final int SLOT_COUNT = 5;
    private static final int SLOT_SIZE = 18;

    public static int pendingOriginNodeId = -1;

    private final Container formationContainer;
    private final int originNodeId;

    private static final class FormationSlot extends Slot {
        FormationSlot(Container container, int index, int x, int y) {
            super(container, index, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return stack.getItem() instanceof BoxedCarItem;
        }
    }

    public RailwayFormationMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, pendingOriginNodeId);
        pendingOriginNodeId = -1;
    }

    public RailwayFormationMenu(int containerId, Inventory playerInventory, int originNodeId) {
        super(AiObjectModMenus.RAILWAY_FORMATION_MENU_TYPE, containerId);
        this.originNodeId = originNodeId;
        this.formationContainer = new SimpleContainer(SLOT_COUNT);

        if (playerInventory.player instanceof ServerPlayer serverPlayer) {
            this.populateFromExistingRoute(serverLevelOf(serverPlayer), originNodeId);
        }

        for (int i = 0; i < SLOT_COUNT; i++) {
            this.addSlot(new FormationSlot(this.formationContainer, i, 8 + i * SLOT_SIZE, 20));
        }
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * SLOT_SIZE, 51 + row * SLOT_SIZE));
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * SLOT_SIZE, 109));
        }
    }

    public int getOriginNodeId() {
        return this.originNodeId;
    }

    public ItemStack getFormationStack(int index) {
        return this.formationContainer.getItem(index);
    }

    private static ServerLevel serverLevelOf(ServerPlayer player) {
        //? if >=1.20 {
        //? if >=1.21.6 {
        return player.level();
        //?} else
        /*return (ServerLevel) player.level();*/
        //?} else
        /*return (ServerLevel) player.getLevel();*/
    }

    private static int rawIntOr(CompoundTag tag, String key, int fallback) {
        //? if >=1.21.5 {
        return tag.getInt(key).orElse(fallback);
        //?} else
        /*return tag.contains(key) ? tag.getInt(key) : fallback;*/
    }

    private static CompoundTag rawCompoundOr(CompoundTag tag, String key) {
        //? if >=1.21.5 {
        return tag.getCompound(key).orElse(new CompoundTag());
        //?} else
        /*return tag.contains(key) ? tag.getCompound(key) : new CompoundTag();*/
    }

    private void populateFromExistingRoute(ServerLevel serverLevel, int originNodeId) {
        RailwayNetworkSavedData data = RailwayNetworkSavedData.get(serverLevel);
        RailwayNetworkSavedData.RailwayRouteEntry config = data.getRouteConfig(originNodeId);
        if (config == null || !RailwayNetworkSavedData.RailwayRouteEntry.MODE_CUSTOM.equals(config.mode())) {
            return;
        }
        CompoundTag formationTag = config.formationTag();
        if (formationTag.isEmpty()) {
            return;
        }
        int count = rawIntOr(formationTag, "RailwayCarCount", 0);
        CompoundTag headTag = formationTag.copy();
        headTag.remove("RailwayCarCount");
        for (int i = 0; i < count; i++) {
            headTag.remove("RailwayCar" + i);
        }
        this.formationContainer.setItem(0, stackFromTag(headTag));
        for (int i = 0; i < count && i + 1 < SLOT_COUNT; i++) {
            this.formationContainer.setItem(i + 1, stackFromTag(rawCompoundOr(formationTag, "RailwayCar" + i)));
        }
    }

    private static ItemStack stackFromTag(CompoundTag tag) {
        if (tag.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = new ItemStack(AiObjectModItems.BOXED_CAR);
        //? if >=1.20.5 {
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        //?} else
        /*stack.setTag(tag);*/
        return stack;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            result = stackInSlot.copy();
            if (index < SLOT_COUNT) {
                if (!this.moveItemStackTo(stackInSlot, SLOT_COUNT, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.moveItemStackTo(stackInSlot, 0, SLOT_COUNT, false)) {
                    return ItemStack.EMPTY;
                }
            }
            if (stackInSlot.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return result;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        if (player instanceof ServerPlayer) {
            for (int i = 0; i < SLOT_COUNT; i++) {
                ItemStack stack = this.formationContainer.removeItemNoUpdate(i);
                if (!stack.isEmpty()) {

                    com.examplebett.compat.ModPlayer.getInventory(player).add(stack);
                    if (!stack.isEmpty()) {
                        player.drop(stack, false);
                    }
                }
            }
        }
    }
}
