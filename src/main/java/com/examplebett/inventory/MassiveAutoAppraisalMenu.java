package com.examplebett.inventory;

import com.examplebett.compat.ModEntity;
import com.examplebett.compat.ModPlayer;
import com.examplebett.compat.ModText;
import com.examplebett.item.BoxedCarItem;
import com.examplebett.item.CarSpawnItem;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class MassiveAutoAppraisalMenu extends AbstractContainerMenu {

    public static final int SELL_BUTTON_ID = 0;
    public static final int SELL_SLOT_X = 88;
    public static final int SELL_SLOT_Y = 58;
    public static final int INVENTORY_X = 16;
    public static final int INVENTORY_Y = 120;
    public static final int HOTBAR_Y = 178;
    private static final int SLOT_SIZE = 18;

    private static final int[] SALE_WEIGHTS = {45, 35, 12, 6, 2};

    private final SimpleContainer sellContainer = new SimpleContainer(1);

    public MassiveAutoAppraisalMenu(int containerId, Inventory playerInventory) {
        super(AiObjectModMenus.MASSIVE_AUTO_APPRAISAL_MENU_TYPE, containerId);
        this.addSlot(new Slot(this.sellContainer, 0, SELL_SLOT_X, SELL_SLOT_Y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return isCar(stack);
            }
        });
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, INVENTORY_X + col * SLOT_SIZE, INVENTORY_Y + row * SLOT_SIZE));
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, INVENTORY_X + col * SLOT_SIZE, HOTBAR_Y));
        }
    }

    public static boolean isCar(ItemStack stack) {
        return stack.getItem() instanceof CarSpawnItem || BoxedCarItem.hasCar(stack);
    }

    public boolean hasCarToSell() {
        return isCar(this.slots.get(0).getItem());
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id != SELL_BUTTON_ID || !(player instanceof ServerPlayer)) {
            return false;
        }
        Slot slot = this.slots.get(0);
        if (!isCar(slot.getItem())) {
            return false;
        }
        slot.remove(1);
        int emeralds = rollEmeralds(player);
        ItemStack reward = new ItemStack(Items.EMERALD, emeralds);

        ModPlayer.getInventory(player).add(reward);
        if (!reward.isEmpty()) {
            player.drop(reward, false);
        }
        ModPlayer.displayActionBar(player, ModText.translatable("gui.mineturismo.massive_auto.sold", emeralds));

        //? if >=1.20 {
        net.minecraft.world.level.Level level = player.level();
        //?} else
        /*net.minecraft.world.level.Level level = player.level;*/
        level.playSound(null, ModEntity.getX(player), ModEntity.getY(player), ModEntity.getZ(player),
                SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.6f, 1.2f);
        this.broadcastChanges();
        return true;
    }

    private static int rollEmeralds(Player player) {
        int total = 0;
        for (int w : SALE_WEIGHTS) {
            total += w;
        }
        int roll = player.getRandom().nextInt(total);
        for (int i = 0; i < SALE_WEIGHTS.length; i++) {
            roll -= SALE_WEIGHTS[i];
            if (roll < 0) {
                return i + 1;
            }
        }
        return 1;
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
            if (index == 0) {
                if (!this.moveItemStackTo(stackInSlot, 1, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!isCar(stackInSlot) || !this.moveItemStackTo(stackInSlot, 0, 1, false)) {
                return ItemStack.EMPTY;
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
            ItemStack stack = this.sellContainer.removeItemNoUpdate(0);
            if (!stack.isEmpty()) {
                ModPlayer.getInventory(player).add(stack);
                if (!stack.isEmpty()) {
                    player.drop(stack, false);
                }
            }
        }
    }
}
