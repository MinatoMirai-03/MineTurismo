package com.examplebett.client.gui;
import com.examplebett.compat.ModWidgets;
import com.examplebett.compat.ModText;

import com.examplebett.compat.ModClientNetworking;
import com.examplebett.entity.CarEntity;
import com.examplebett.network.SetCarBombPayload;
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
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public class CarBombScreen extends Screen implements CarCustomizationScreen {

    private static final int TEXT_WIDTH = 240;

    private final CarEntity car;
    private final Screen parent;
    private int statusY;
    private int descY;

    public CarBombScreen(CarEntity car, Screen parent) {
        super(ModText.translatable("gui.mineturismo.bomb.title"));
        this.car = car;
        this.parent = parent;
    }

    private void apply(boolean installed) {

        this.car.setBombInstalled(installed, this.minecraft.player != null ? this.minecraft.player.getUUID() : null);
        SetCarBombPayload mineturismo$payload = new SetCarBombPayload(this.car.getId(), installed);
        //? if >=1.20.5 {
        ModClientNetworking.sendToServer(mineturismo$payload);
        //?} else {
        /*ModClientNetworking.sendToServer(SetCarBombPayload.ID, mineturismo$payload, SetCarBombPayload::write);*/
        //?}
        this.minecraft.setScreen(new CarBombScreen(this.car, this.parent));
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int top = this.height / 2 - 60;
        this.statusY = top;
        boolean installed = this.car.isBombInstalled();
        int buttonWidth = 200;
        Component toggle = ModText.translatable(installed ? "gui.mineturismo.bomb.remove" : "gui.mineturismo.bomb.install");
        this.addRenderableWidget(ModWidgets.button(toggle, centerX - buttonWidth / 2, top + 16, buttonWidth, 20, b -> this.apply(!installed)));
        this.descY = top + 46;
        this.addRenderableWidget(ModWidgets.button(ModText.translatable("gui.mineturismo.back"), centerX - buttonWidth / 2, top + 100, buttonWidth, 20, button ->
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
        int centerX = this.width / 2;
        guiGraphics.drawCenteredString(this.font, this.title, centerX, this.statusY - 24, 0xFFFFFFFF);
        boolean installed = this.car.isBombInstalled();
        guiGraphics.drawCenteredString(this.font,
                ModText.translatable(installed ? "gui.mineturismo.bomb.status_installed" : "gui.mineturismo.bomb.status_none"),
                centerX, this.statusY, installed ? 0xFFFF5555 : 0xFFAAAAAA);
        List<FormattedCharSequence> lines = this.font.split(ModText.translatable("gui.mineturismo.bomb.desc"), TEXT_WIDTH);
        for (int i = 0; i < lines.size(); i++) {
            guiGraphics.drawCenteredString(this.font, lines.get(i), centerX, this.descY + i * 10, 0xFFDDDDDD);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
