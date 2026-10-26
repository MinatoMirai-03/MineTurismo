package com.examplebett.client.gui;
import com.examplebett.compat.ModCheckbox;
import com.examplebett.compat.ModWidgets;
import com.examplebett.compat.ModText;

import com.examplebett.compat.ModClientNetworking;
import com.examplebett.entity.CarEntity;
import com.examplebett.network.SetCarDriveModePayload;
import com.examplebett.network.SetChaseModePayload;
import com.examplebett.network.SetChaosModePayload;
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
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class CarDriveModeScreen extends Screen implements CarCustomizationScreen {

    private final CarEntity car;
    private final Screen parent;

    public CarDriveModeScreen(CarEntity car, Screen parent) {
        super(ModText.translatable("gui.mineturismo.drive_mode.title"));
        this.car = car;
        this.parent = parent;
    }

    private void applyMode(CarEntity.DriveMode mode) {
        this.car.setDriveMode(mode);
        SetCarDriveModePayload mineturismo$payload = new SetCarDriveModePayload(this.car.getId(), mode.ordinal());
        //? if >=1.20.5 {
        ModClientNetworking.sendToServer(mineturismo$payload);
        //?} else {
        /*ModClientNetworking.sendToServer(SetCarDriveModePayload.ID, mineturismo$payload, SetCarDriveModePayload::write);*/
        //?}
        this.minecraft.setScreen(new CarDriveModeScreen(this.car, this.parent));
    }

    private void sendChaseMode(boolean enabled) {
        this.car.setChaseModeEnabled(enabled);
        SetChaseModePayload mineturismo$payload = new SetChaseModePayload(this.car.getId(), enabled);
        //? if >=1.20.5 {
        ModClientNetworking.sendToServer(mineturismo$payload);
        //?} else {
        /*ModClientNetworking.sendToServer(SetChaseModePayload.ID, mineturismo$payload, SetChaseModePayload::write);*/
        //?}
    }

    private void sendChaosMode(boolean enabled) {
        this.car.setChaosModeEnabled(enabled);
        SetChaosModePayload mineturismo$payload = new SetChaosModePayload(this.car.getId(), enabled);
        //? if >=1.20.5 {
        ModClientNetworking.sendToServer(mineturismo$payload);
        //?} else {
        /*ModClientNetworking.sendToServer(SetChaosModePayload.ID, mineturismo$payload, SetChaosModePayload::write);*/
        //?}
    }

    private void openCustomTuning() {
        this.car.setDriveMode(CarEntity.DriveMode.CUSTOM);
        SetCarDriveModePayload mineturismo$payload = new SetCarDriveModePayload(this.car.getId(), CarEntity.DriveMode.CUSTOM.ordinal());
        //? if >=1.20.5 {
        ModClientNetworking.sendToServer(mineturismo$payload);
        //?} else {
        /*ModClientNetworking.sendToServer(SetCarDriveModePayload.ID, mineturismo$payload, SetCarDriveModePayload::write);*/
        //?}
        this.minecraft.setScreen(new CarCustomTuningScreen(this.car, this));
    }

    private Component modeLabel(CarEntity.DriveMode mode, String translationKey) {
        Component label = ModText.translatable(translationKey);
        return this.car.getDriveMode() == mode ? ModText.literal("> ").append(label) : label;
    }

    @Override
    protected void init() {
        int buttonWidth = 200;
        int x = this.width / 2 - buttonWidth / 2;
        int y = this.height / 2 - 66;

        this.addRenderableWidget(ModWidgets.button(this.modeLabel(CarEntity.DriveMode.MODERN, "gui.mineturismo.drive_mode.modern"), x, y, buttonWidth, 20, button -> this.applyMode(CarEntity.DriveMode.MODERN)));

        this.addRenderableWidget(ModWidgets.button(this.modeLabel(CarEntity.DriveMode.CLASSIC, "gui.mineturismo.drive_mode.classic"), x, y + 24, buttonWidth, 20, button -> this.applyMode(CarEntity.DriveMode.CLASSIC)));

        this.addRenderableWidget(ModWidgets.button(this.modeLabel(CarEntity.DriveMode.HYPER, "gui.mineturismo.drive_mode.hyper"), x, y + 48, buttonWidth, 20, button -> this.applyMode(CarEntity.DriveMode.HYPER)));

        this.addRenderableWidget(ModWidgets.button(this.modeLabel(CarEntity.DriveMode.DRIFT, "gui.mineturismo.drive_mode.drift"), x, y + 72, buttonWidth, 20, button -> this.applyMode(CarEntity.DriveMode.DRIFT)));

        this.addRenderableWidget(ModWidgets.button(this.modeLabel(CarEntity.DriveMode.CUSTOM, "gui.mineturismo.drive_mode.custom"), x, y + 96, buttonWidth, 20, button -> this.openCustomTuning()));

        int checkboxX = x - 20;
        this.addRenderableWidget(ModCheckbox.builder(ModText.translatable("gui.mineturismo.drive_mode.chase"), this.font)
                .pos(checkboxX, y + 124)
                .selected(this.car.isChaseModeEnabled())
                .onValueChange((checkbox, selected) -> this.sendChaseMode(selected))
                .build());

        this.addRenderableWidget(ModCheckbox.builder(ModText.translatable("gui.mineturismo.drive_mode.chaos"), this.font)
                .pos(checkboxX, y + 148)
                .selected(this.car.isChaosModeEnabled())
                .onValueChange((checkbox, selected) -> this.sendChaosMode(selected))
                .build());

        this.addRenderableWidget(ModWidgets.button(ModText.translatable("gui.mineturismo.back"), x, y + 176, buttonWidth, 20, button ->
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
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - 96, 0xFFFFFFFF);
        String descriptionKey = switch (this.car.getDriveMode()) {
            case MODERN -> "gui.mineturismo.drive_mode.modern_desc";
            case CLASSIC -> "gui.mineturismo.drive_mode.classic_desc";
            case HYPER -> "gui.mineturismo.drive_mode.hyper_desc";
            case DRIFT -> "gui.mineturismo.drive_mode.drift_desc";
            case CUSTOM -> "gui.mineturismo.drive_mode.custom_desc";
        };
        guiGraphics.drawCenteredString(this.font, ModText.translatable(descriptionKey), this.width / 2, this.height / 2 + 156, 0xAAAAAA);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
