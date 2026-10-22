package com.examplebett.client.gui;
import com.examplebett.compat.ModWidgets;
import com.examplebett.compat.ModText;

import com.examplebett.entity.CarEntity;
import com.examplebett.render.BrandLogos;
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
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class BrandLogoScreen extends Screen implements CarCustomizationScreen {

    private static final int MENU_BUTTON_WIDTH = 200;
    private static final int MENU_BUTTON_HEIGHT = 20;
    private static final int MENU_ROW_HEIGHT = MENU_BUTTON_HEIGHT + 4;

    private final CarEntity car;
    private final Screen parent;

    public BrandLogoScreen(CarEntity car, Screen parent) {
        super(ModText.translatable("gui.mineturismo.brand_logo.title"));
        this.car = car;
        this.parent = parent;
    }

    static Component slotName(int slot) {
        if (BrandLogos.isOtherSlot(slot)) {
            return ModText.translatable("gui.mineturismo.brand_logo.slot_other", slot - BrandLogos.FIRST_OTHER_SLOT + 1);
        }
        return ModText.translatable("gui.mineturismo.under_neon.slot_" + BrandLogos.SLOT_IDS[slot]);
    }

    private Component slotLabel(int slot) {
        return ModText.translatable(this.car.getBrandLogo(slot).enabled()
                ? "gui.mineturismo.under_neon.slot_on" : "gui.mineturismo.under_neon.slot_off", slotName(slot));
    }

    private int menuTop() {
        int menuHeight = BrandLogos.MAX_SLOTS * MENU_ROW_HEIGHT + 8 + 20;
        return Math.max(28, this.height / 2 - menuHeight / 2);
    }

    @Override
    protected void init() {
        int x = this.width / 2 - MENU_BUTTON_WIDTH / 2;
        int y = this.menuTop();
        for (int i = 0; i < BrandLogos.MAX_SLOTS; i++) {
            int slot = i;
            this.addRenderableWidget(ModWidgets.button(this.slotLabel(slot), x, y + i * MENU_ROW_HEIGHT, MENU_BUTTON_WIDTH, MENU_BUTTON_HEIGHT, b ->
                    this.minecraft.setScreen(new BrandLogoAdjustScreen(this.car, this.parent, slot))));
        }
        this.addRenderableWidget(ModWidgets.button(ModText.translatable("gui.mineturismo.back"),
                x, y + BrandLogos.MAX_SLOTS * MENU_ROW_HEIGHT + 8, MENU_BUTTON_WIDTH, 20, button ->
                        this.minecraft.setScreen(this.parent)));
    }

    //? if >=26 {
    /*@Override
    protected void extractBlurredBackground(net.minecraft.client.gui.GuiGraphicsExtractor extractor) {

    }*/
    //?}
    //? if >=1.20.5 && <26 {
    //? if >=1.21.2 {
    //? if >=1.21.6 {
    @Override
    protected void renderBlurredBackground(GuiGraphics guiGraphics) {

    }
    //?} else {
    /*@Override
    protected void renderBlurredBackground() {

    }*/
    //?}
    //?} else {
    /*@Override
    protected void renderBlurredBackground(float partialTick) {

    }*/
    //?}
    //?}

    //? if >=26 {
    /*@Override
    protected void extractMenuBackground(net.minecraft.client.gui.GuiGraphicsExtractor extractor) {

    }*/
    //?}
    //? if >=1.20.5 && <26 {
    @Override
    protected void renderMenuBackground(GuiGraphics guiGraphics) {

    }
    //?}

    //? if >=26 {
    /*@Override
    public void extractRenderState(net.minecraft.client.gui.GuiGraphicsExtractor extractor, int mouseX, int mouseY, float partialTick) {
        mineturismo$renderScreen(new GuiGraphics(extractor), mouseX, mouseY, partialTick);
    }*/
    //?}
    //? if >=1.20 && <26 {
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        mineturismo$renderScreen(guiGraphics, mouseX, mouseY, partialTick);
    }
    //?}
    //? if <1.20 {
    /*@Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        mineturismo$renderScreen(new GuiGraphics(poseStack), mouseX, mouseY, partialTick);
    }*/
    //?}

    private void mineturismo$renderScreen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        //? if >=26 {
        /*super.extractRenderState(guiGraphics.extractor(), mouseX, mouseY, partialTick);*/
        //?}
        //? if >=1.20 && <26 {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        //?}
        //? if <1.20 {
        /*super.render(guiGraphics.pose(), mouseX, mouseY, partialTick);*/
        //?}
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, this.menuTop() - 16, 0xFFFFFFFF);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
