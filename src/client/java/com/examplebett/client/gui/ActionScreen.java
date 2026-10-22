package com.examplebett.client.gui;
import com.examplebett.compat.ModCheckbox;
import com.examplebett.compat.ModWidgets;
import com.examplebett.compat.ModText;

import com.examplebett.compat.ModClientNetworking;
import com.examplebett.entity.CarEntity;
import com.examplebett.network.SetActionMultipliersPayload;
import com.examplebett.network.SetForceRotatePayload;
import com.examplebett.network.SetSecurityAlarmPayload;
import com.examplebett.network.SetSparkPositionOffsetPayload;
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
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
//? if >=1.21.9 {
import net.minecraft.client.input.MouseButtonEvent;
//?}
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.function.DoubleConsumer;
import java.util.function.DoubleFunction;

public class ActionScreen extends Screen implements CarCustomizationScreen {

    private final CarEntity car;
    private final Screen parent;

    public ActionScreen(CarEntity car, Screen parent) {
        super(ModText.translatable("gui.mineturismo.action_menu.title"));
        this.car = car;
        this.parent = parent;
    }

    private void sendSecurityAlarm(boolean enabled) {
        CarEntity.setSecurityAlarmEnabled(enabled);
        SetSecurityAlarmPayload mineturismo$payload = new SetSecurityAlarmPayload(enabled);
        //? if >=1.20.5 {
        ModClientNetworking.sendToServer(mineturismo$payload);
        //?} else {
        /*ModClientNetworking.sendToServer(SetSecurityAlarmPayload.ID, mineturismo$payload, SetSecurityAlarmPayload::write);*/
        //?}
    }

    private void sendForceRotate() {
        SetForceRotatePayload mineturismo$payload = new SetForceRotatePayload(
                CarEntity.isForceRotateSideEnabled(), CarEntity.isForceRotateFrontEnabled());
        //? if >=1.20.5 {
        ModClientNetworking.sendToServer(mineturismo$payload);
        //?} else {
        /*ModClientNetworking.sendToServer(SetForceRotatePayload.ID, mineturismo$payload, SetForceRotatePayload::write);*/
        //?}
    }

    private void sendSparkPositionOffset(boolean enabled) {
        CarEntity.setSparkPositionOffsetEnabled(enabled);
        SetSparkPositionOffsetPayload mineturismo$payload = new SetSparkPositionOffsetPayload(enabled);
        //? if >=1.20.5 {
        ModClientNetworking.sendToServer(mineturismo$payload);
        //?} else {
        /*ModClientNetworking.sendToServer(SetSparkPositionOffsetPayload.ID, mineturismo$payload, SetSparkPositionOffsetPayload::write);*/
        //?}
    }

    private void sendActionMultipliers() {
        SetActionMultipliersPayload mineturismo$payload = new SetActionMultipliersPayload(
                CarEntity.getTumbleHeightMultiplier(),
                CarEntity.getTumbleRotationMultiplier(),
                CarEntity.getPushDistanceMultiplier(),
                CarEntity.getSuspensionIntensityMultiplier());
        //? if >=1.20.5 {
        ModClientNetworking.sendToServer(mineturismo$payload);
        //?} else {
        /*ModClientNetworking.sendToServer(SetActionMultipliersPayload.ID, mineturismo$payload, SetActionMultipliersPayload::write);*/
        //?}
    }

    @Override
    protected void init() {
        int width = 220;
        int x = this.width / 2 - width / 2;
        int y = this.height / 2 - 110;
        int rowHeight = 24;

        this.addRenderableWidget(ModCheckbox.builder(ModText.translatable("gui.mineturismo.action_menu.security_alarm"), this.font)
                .pos(x, y)
                .selected(CarEntity.isSecurityAlarmEnabled())
                .onValueChange((checkbox, selected) -> this.sendSecurityAlarm(selected))
                .build());

        this.addRenderableWidget(ModCheckbox.builder(ModText.translatable("gui.mineturismo.action_menu.force_rotate_side"), this.font)
                .pos(x, y + rowHeight)
                .selected(CarEntity.isForceRotateSideEnabled())
                .onValueChange((checkbox, selected) -> {
                    CarEntity.setForceRotateSideEnabled(selected);
                    this.sendForceRotate();
                })
                .build());

        this.addRenderableWidget(ModCheckbox.builder(ModText.translatable("gui.mineturismo.action_menu.force_rotate_front"), this.font)
                .pos(x, y + rowHeight * 2)
                .selected(CarEntity.isForceRotateFrontEnabled())
                .onValueChange((checkbox, selected) -> {
                    CarEntity.setForceRotateFrontEnabled(selected);
                    this.sendForceRotate();
                })
                .build());

        this.addRenderableWidget(ModCheckbox.builder(ModText.translatable("gui.mineturismo.action_menu.spark_position_offset"), this.font)
                .pos(x, y + rowHeight * 3)
                .selected(CarEntity.isSparkPositionOffsetEnabled())
                .onValueChange((checkbox, selected) -> this.sendSparkPositionOffset(selected))
                .build());

        int slidersY = y + rowHeight * 4 + 8;

        double heightInitial = (CarEntity.getTumbleHeightMultiplier() - CarEntity.ACTION_MULTIPLIER_MIN)
                / (CarEntity.ACTION_MULTIPLIER_MAX - CarEntity.ACTION_MULTIPLIER_MIN);
        this.addRenderableWidget(new ActionSlider(x, slidersY, width, 20, heightInitial,
                "gui.mineturismo.action_menu.tumble_height",
                value -> String.format("x%.2f", Mth.lerp((float) value, CarEntity.ACTION_MULTIPLIER_MIN, CarEntity.ACTION_MULTIPLIER_MAX)),
                value -> CarEntity.setTumbleHeightMultiplier(Mth.lerp((float) value, CarEntity.ACTION_MULTIPLIER_MIN, CarEntity.ACTION_MULTIPLIER_MAX))));

        double rotationInitial = (CarEntity.getTumbleRotationMultiplier() - CarEntity.ACTION_MULTIPLIER_MIN)
                / (CarEntity.ACTION_MULTIPLIER_MAX - CarEntity.ACTION_MULTIPLIER_MIN);
        this.addRenderableWidget(new ActionSlider(x, slidersY + rowHeight, width, 20, rotationInitial,
                "gui.mineturismo.action_menu.tumble_rotation",
                value -> String.format("x%.2f", Mth.lerp((float) value, CarEntity.ACTION_MULTIPLIER_MIN, CarEntity.ACTION_MULTIPLIER_MAX)),
                value -> CarEntity.setTumbleRotationMultiplier(Mth.lerp((float) value, CarEntity.ACTION_MULTIPLIER_MIN, CarEntity.ACTION_MULTIPLIER_MAX))));

        double pushDistanceInitial = (CarEntity.getPushDistanceMultiplier() - CarEntity.PUSH_DISTANCE_MULTIPLIER_MIN)
                / (CarEntity.PUSH_DISTANCE_MULTIPLIER_MAX - CarEntity.PUSH_DISTANCE_MULTIPLIER_MIN);
        this.addRenderableWidget(new ActionSlider(x, slidersY + rowHeight * 2, width, 20, pushDistanceInitial,
                "gui.mineturismo.action_menu.push_distance",
                value -> String.format("x%.2f", Mth.lerp((float) value, CarEntity.PUSH_DISTANCE_MULTIPLIER_MIN, CarEntity.PUSH_DISTANCE_MULTIPLIER_MAX)),
                value -> CarEntity.setPushDistanceMultiplier(Mth.lerp((float) value, CarEntity.PUSH_DISTANCE_MULTIPLIER_MIN, CarEntity.PUSH_DISTANCE_MULTIPLIER_MAX))));

        double suspensionIntensityInitial = (CarEntity.getSuspensionIntensityMultiplier() - CarEntity.SUSPENSION_INTENSITY_MULTIPLIER_MIN)
                / (CarEntity.SUSPENSION_INTENSITY_MULTIPLIER_MAX - CarEntity.SUSPENSION_INTENSITY_MULTIPLIER_MIN);
        this.addRenderableWidget(new ActionSlider(x, slidersY + rowHeight * 3, width, 20, suspensionIntensityInitial,
                "gui.mineturismo.action_menu.suspension_intensity",
                value -> String.format("x%.2f", Mth.lerp((float) value, CarEntity.SUSPENSION_INTENSITY_MULTIPLIER_MIN, CarEntity.SUSPENSION_INTENSITY_MULTIPLIER_MAX)),
                value -> CarEntity.setSuspensionIntensityMultiplier(Mth.lerp((float) value, CarEntity.SUSPENSION_INTENSITY_MULTIPLIER_MIN, CarEntity.SUSPENSION_INTENSITY_MULTIPLIER_MAX))));

        this.addRenderableWidget(ModWidgets.button(ModText.translatable("gui.mineturismo.back"), x, slidersY + rowHeight * 4 + 8, width, 20, button ->
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
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - 140, 0xFFFFFFFF);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private class ActionSlider extends AbstractSliderButton {
        private final String labelKey;
        private final DoubleFunction<String> formatter;
        private final DoubleConsumer onChange;

        ActionSlider(int x, int y, int width, int height, double initialValue,
                     String labelKey, DoubleFunction<String> formatter, DoubleConsumer onChange) {
            super(x, y, width, height, ModText.literal(""), Mth.clamp(initialValue, 0.0, 1.0));
            this.labelKey = labelKey;
            this.formatter = formatter;
            this.onChange = onChange;
            this.updateMessage();
        }

        @Override
        protected void updateMessage() {
            this.setMessage(ModText.translatable(this.labelKey, this.formatter.apply(this.value)));
        }

        @Override
        protected void applyValue() {
            this.onChange.accept(this.value);
        }

        @Override

        //? if >=1.21.9 {
        public void onRelease(MouseButtonEvent event) {
            super.onRelease(event);
        //?} else
        /*public void onRelease(double mouseX, double mouseY) {
            super.onRelease(mouseX, mouseY);*/
            ActionScreen.this.sendActionMultipliers();
        }
    }
}
