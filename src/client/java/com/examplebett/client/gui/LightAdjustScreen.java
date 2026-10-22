package com.examplebett.client.gui;
import com.examplebett.compat.ModCheckbox;
import com.examplebett.compat.ModWidgets;
import com.examplebett.compat.ModText;

import com.examplebett.compat.ModClientNetworking;
import com.examplebett.AiObjectMod;
import com.examplebett.entity.CarEntity;
import com.examplebett.entity.HeadlightColors;
import com.examplebett.network.SetCarLightBlinkPayload;
import com.examplebett.network.SetCarLightPayload;
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

import java.util.ArrayList;
import java.util.List;

public class LightAdjustScreen extends Screen implements CarCustomizationScreen {

    private static final float POSITION_STEP = 0.05f;
    private static final int RIGHT_MARGIN = 20;
    private static final int CLUSTER_HALF_WIDTH = 70;
    private static final int BUTTON_HORIZONTAL_PADDING = 16;
    private static final int MIN_BUTTON_WIDTH = 60;

    private final CarEntity car;
    private final Screen parent;
    private int clusterCenterX;

    public LightAdjustScreen(CarEntity car, Screen parent) {
        super(ModText.translatable("gui.mineturismo.light_adjust.title"));
        this.car = car;
        this.parent = parent;
    }

    private int fitWidth(Component label) {
        return Math.max(MIN_BUTTON_WIDTH, this.font.width(label) + BUTTON_HORIZONTAL_PADDING);
    }

    private void applyAdjust(float headlightX, float headlightY, float headlightZ,
                              float taillightX, float taillightY, float taillightZ,
                              CarEntity.LightMode mode, String headlightColorId) {
        this.car.setLightAdjust(headlightX, headlightY, headlightZ, taillightX, taillightY, taillightZ, mode, headlightColorId);
        SetCarLightPayload mineturismo$payload = new SetCarLightPayload(
                this.car.getId(), headlightX, headlightY, headlightZ,
                taillightX, taillightY, taillightZ, mode.ordinal(), headlightColorId);
        //? if >=1.20.5 {
        ModClientNetworking.sendToServer(mineturismo$payload);
        //?} else {
        /*ModClientNetworking.sendToServer(SetCarLightPayload.ID, mineturismo$payload, SetCarLightPayload::write);*/
        //?}

        var headlightResolved = this.car.getHeadlightOffset();
        var taillightResolved = this.car.getTaillightOffset();
        AiObjectMod.LOGGER.debug(
                "light adjust [{}] headlight=({}, {}, {}) taillight=({}, {}, {}) mode={} headlightColor={}",
                this.car.getModelId(),
                headlightResolved.x, headlightResolved.y, headlightResolved.z,
                taillightResolved.x, taillightResolved.y, taillightResolved.z,
                mode, headlightColorId);
        this.minecraft.setScreen(new LightAdjustScreen(this.car, this.parent));
    }

    @Override
    protected void init() {
        this.clusterCenterX = this.width - RIGHT_MARGIN - CLUSTER_HALF_WIDTH;
        int top = this.height / 2 - 170;

        float headlightX = this.car.getHeadlightXAdjust();
        float headlightY = this.car.getHeadlightYAdjust();
        float headlightZ = this.car.getHeadlightZAdjust();
        float taillightX = this.car.getTaillightXAdjust();
        float taillightY = this.car.getTaillightYAdjust();
        float taillightZ = this.car.getTaillightZAdjust();
        CarEntity.LightMode mode = this.car.getLightMode();

        String colorId = this.car.resolveHeadlightColorId();

        int rowHeight = 20;
        int rowY = top + 20;

        addAxisRow(rowY, "gui.mineturismo.light_adjust.headlight_x", headlightX, POSITION_STEP,
                delta -> applyAdjust(headlightX + delta, headlightY, headlightZ, taillightX, taillightY, taillightZ, mode, colorId));
        addAxisRow(rowY + rowHeight, "gui.mineturismo.light_adjust.headlight_y", headlightY, POSITION_STEP,
                delta -> applyAdjust(headlightX, headlightY + delta, headlightZ, taillightX, taillightY, taillightZ, mode, colorId));
        addAxisRow(rowY + rowHeight * 2, "gui.mineturismo.light_adjust.headlight_z", headlightZ, POSITION_STEP,
                delta -> applyAdjust(headlightX, headlightY, headlightZ + delta, taillightX, taillightY, taillightZ, mode, colorId));
        addAxisRow(rowY + rowHeight * 3, "gui.mineturismo.light_adjust.taillight_x", taillightX, POSITION_STEP,
                delta -> applyAdjust(headlightX, headlightY, headlightZ, taillightX + delta, taillightY, taillightZ, mode, colorId));
        addAxisRow(rowY + rowHeight * 4, "gui.mineturismo.light_adjust.taillight_y", taillightY, POSITION_STEP,
                delta -> applyAdjust(headlightX, headlightY, headlightZ, taillightX, taillightY + delta, taillightZ, mode, colorId));
        addAxisRow(rowY + rowHeight * 5, "gui.mineturismo.light_adjust.taillight_z", taillightZ, POSITION_STEP,
                delta -> applyAdjust(headlightX, headlightY, headlightZ, taillightX, taillightY, taillightZ + delta, mode, colorId));

        int rightEdge = this.width - RIGHT_MARGIN;
        int nextY = rowY + rowHeight * 6 + 8;

        CarEntity.LightMode nextMode = switch (mode) {
            case AUTO -> CarEntity.LightMode.FORCE_ON;
            case FORCE_ON -> CarEntity.LightMode.FORCE_OFF;
            case FORCE_OFF -> CarEntity.LightMode.AUTO;
        };
        Component modeLabel = ModText.translatable("gui.mineturismo.light_adjust.mode",
                ModText.translatable(modeTranslationKey(mode)));
        int modeButtonWidth = fitWidth(modeLabel);
        this.addRenderableWidget(ModWidgets.button(modeLabel, rightEdge - modeButtonWidth, nextY, modeButtonWidth, 20, button ->
                applyAdjust(headlightX, headlightY, headlightZ, taillightX, taillightY, taillightZ, nextMode, colorId)));
        nextY += rowHeight + 8;

        List<HeadlightColors.Option> colorOptions = HeadlightColors.OPTIONS;
        int colorIndex = indexOfColor(colorOptions, colorId);
        String nextColorId = colorOptions.get((colorIndex + 1) % colorOptions.size()).id();
        Component colorLabel = ModText.translatable("gui.mineturismo.light_adjust.headlight_color",
                colorOptions.get(colorIndex).displayName());
        int colorButtonWidth = fitWidth(colorLabel);
        this.addRenderableWidget(ModWidgets.button(colorLabel, rightEdge - colorButtonWidth, nextY, colorButtonWidth, 20, button ->
                applyAdjust(headlightX, headlightY, headlightZ, taillightX, taillightY, taillightZ, mode, nextColorId)));
        nextY += rowHeight + 8;

        Component blinkLabel = ModText.translatable("gui.mineturismo.light_adjust.blink");
        int blinkWidth = fitWidth(blinkLabel) + 20;
        this.addRenderableWidget(ModCheckbox.builder(blinkLabel, this.font)
                .pos(rightEdge - blinkWidth, nextY)
                .selected(this.car.isLightBlinkEnabled())
                .onValueChange((checkbox, selected) -> {
                    this.car.setLightBlinkEnabled(selected);
                    SetCarLightBlinkPayload mineturismo$payload = new SetCarLightBlinkPayload(this.car.getId(), selected);
                    //? if >=1.20.5 {
                    ModClientNetworking.sendToServer(mineturismo$payload);
                    //?} else {
                    /*ModClientNetworking.sendToServer(SetCarLightBlinkPayload.ID, mineturismo$payload, SetCarLightBlinkPayload::write);*/
                    //?}
                })
                .build());
        nextY += rowHeight + 8;

        Component backLabel = ModText.translatable("gui.mineturismo.back");
        int backWidth = fitWidth(backLabel);
        this.addRenderableWidget(ModWidgets.button(backLabel, rightEdge - backWidth, nextY, backWidth, 20, button ->
                this.minecraft.setScreen(this.parent)));
    }

    private static int indexOfColor(List<HeadlightColors.Option> options, String colorId) {
        for (int i = 0; i < options.size(); i++) {
            if (options.get(i).id().equals(colorId)) {
                return i;
            }
        }
        return 0;
    }

    private static String modeTranslationKey(CarEntity.LightMode mode) {
        return switch (mode) {
            case AUTO -> "gui.mineturismo.light_adjust.mode_auto";
            case FORCE_ON -> "gui.mineturismo.light_adjust.mode_force_on";
            case FORCE_OFF -> "gui.mineturismo.light_adjust.mode_force_off";
        };
    }

    private interface AxisAdjuster {
        void adjust(float delta);
    }

    private void addAxisRow(int y, String labelKey, double currentValue, float step, AxisAdjuster adjuster) {
        int centerX = this.clusterCenterX;
        this.addRenderableWidget(ModWidgets.button(ModText.literal("-"), centerX - CLUSTER_HALF_WIDTH, y, 20, 20, button -> adjuster.adjust(-step)));
        this.addRenderableWidget(ModWidgets.button(ModText.literal("+"), centerX + CLUSTER_HALF_WIDTH - 20, y, 20, 20, button -> adjuster.adjust(step)));
        this.axisLabels.add(new AxisLabel(labelKey, currentValue, y));
    }

    private final List<AxisLabel> axisLabels = new ArrayList<>();

    private record AxisLabel(String labelKey, double value, int y) {
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
        guiGraphics.drawCenteredString(this.font, this.title, this.clusterCenterX, this.height / 2 - 190, 0xFFFFFFFF);
        for (AxisLabel label : this.axisLabels) {
            String text = ModText.translatable(label.labelKey()).getString() + String.format(": %.2f", label.value());
            guiGraphics.drawCenteredString(this.font, text, this.clusterCenterX, label.y() + 6, 0xFFFFFFFF);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
