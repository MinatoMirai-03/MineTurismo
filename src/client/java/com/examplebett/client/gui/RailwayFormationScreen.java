package com.examplebett.client.gui;
import com.examplebett.compat.ModWidgets;
import com.examplebett.compat.ModText;

import com.examplebett.compat.ModClientNetworking;
import com.examplebett.inventory.RailwayFormationMenu;
import com.examplebett.item.BoxedCarItem;
import com.examplebett.network.SetRailwayRouteCustomFormationPayload;
import net.minecraft.nbt.CompoundTag;
//? if >=26 {
/*import com.examplebett.compat.GuiGraphics;*/
//?}
//? if >=1.20 && <26 {
import net.minecraft.client.gui.GuiGraphics;
//?}
//? if <1.20 {
/*import com.examplebett.compat.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;*/
//?}
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
//? if >=1.20.5 {
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
//?}

public class RailwayFormationScreen extends AbstractContainerScreen<RailwayFormationMenu> {

    private static final int PANEL_COLOR = 0xC0101010;
    private static final int SLOT_BG_COLOR = 0x80000000;
    private static final int CONFIRM_BUTTON_WIDTH = 160;
    private static final int CONFIRM_BUTTON_HEIGHT = 20;
    private static final int CONFIRM_BUTTON_Y = 132;

    public RailwayFormationScreen(RailwayFormationMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.addRenderableWidget(ModWidgets.button(ModText.translatable("gui.mineturismo.railway_formation.confirm"),
                this.leftPos + (this.imageWidth - CONFIRM_BUTTON_WIDTH) / 2, this.topPos + CONFIRM_BUTTON_Y,
                CONFIRM_BUTTON_WIDTH, CONFIRM_BUTTON_HEIGHT, b -> this.confirm()));
    }

    private static CompoundTag getCustomTag(ItemStack stack) {
        //? if >=1.20.5 {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        //?} else
        /*return stack.getOrCreateTag();*/
    }

    private void confirm() {
        ItemStack head = this.menu.getFormationStack(0);
        if (head.isEmpty() || !(head.getItem() instanceof BoxedCarItem)) {
            return;
        }
        CompoundTag headTag = getCustomTag(head).copy();
        int count = 0;
        for (int i = 1; i < RailwayFormationMenu.SLOT_COUNT; i++) {
            ItemStack stack = this.menu.getFormationStack(i);
            if (stack.isEmpty() || !(stack.getItem() instanceof BoxedCarItem)) {
                continue;
            }
            headTag.put("RailwayCar" + count, getCustomTag(stack).copy());
            count++;
        }
        if (count > 0) {
            headTag.putInt("RailwayCarCount", count);
        }
        SetRailwayRouteCustomFormationPayload payload = new SetRailwayRouteCustomFormationPayload(this.menu.getOriginNodeId(), headTag);
        //? if >=1.20.5 {
        ModClientNetworking.sendToServer(payload);
        //?} else
        /*ModClientNetworking.sendToServer(SetRailwayRouteCustomFormationPayload.ID, payload, SetRailwayRouteCustomFormationPayload::write);*/
        this.minecraft.setScreen(null);
    }

    private void renderSlotBackgrounds(GuiGraphics guiGraphics) {
        guiGraphics.fill(this.leftPos, this.topPos, this.leftPos + this.imageWidth, this.topPos + this.imageHeight, PANEL_COLOR);
        for (Slot slot : this.menu.slots) {
            guiGraphics.fill(this.leftPos + slot.x - 1, this.topPos + slot.y - 1,
                    this.leftPos + slot.x + 17, this.topPos + slot.y + 17, SLOT_BG_COLOR);
        }
    }

    //? if >=26 {
    /*@Override
    public void extractBackground(net.minecraft.client.gui.GuiGraphicsExtractor extractor, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(extractor, mouseX, mouseY, partialTick);
        this.renderSlotBackgrounds(new GuiGraphics(extractor));
    }*/
    //?}
    //? if >=1.20 && <26 {
    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        this.renderSlotBackgrounds(guiGraphics);
    }
    //?}
    //? if <1.20 {
    /*@Override
    protected void renderBg(PoseStack poseStack, float partialTick, int mouseX, int mouseY) {
        this.renderSlotBackgrounds(new GuiGraphics(poseStack));
    }*/
    //?}

    //? if >=1.20 && <26 {
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
    //?}
    //? if <1.20 {
    /*@Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        super.render(poseStack, mouseX, mouseY, partialTick);
        this.renderTooltip(poseStack, mouseX, mouseY);
    }*/
    //?}

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
