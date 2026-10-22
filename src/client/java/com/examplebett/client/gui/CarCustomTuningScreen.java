package com.examplebett.client.gui;
import com.examplebett.compat.ModCheckbox;
import com.examplebett.compat.ModWidgets;
import com.examplebett.compat.ModText;

import com.examplebett.compat.ModClientNetworking;
import com.examplebett.entity.CarEntity;
import com.examplebett.network.SetCarCustomTuningPayload;
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

public class CarCustomTuningScreen extends Screen implements CarCustomizationScreen {

    private final CarEntity car;
    private final Screen parent;

    public CarCustomTuningScreen(CarEntity car, Screen parent) {
        super(ModText.translatable("gui.mineturismo.custom_tuning.title"));
        this.car = car;
        this.parent = parent;
    }

    private void sendTuning() {
        SetCarCustomTuningPayload mineturismo$payload = new SetCarCustomTuningPayload(
                this.car.getId(),
                this.car.getCustomTopSpeedKmh(),
                this.car.getCustomAccelFraction(),
                this.car.getCustomBrakeFraction(),
                this.car.getCustomHandlingFraction(),
                this.car.isCustomTireSmokeAlways(),
                this.car.isDespawnEnabled());
        //? if >=1.20.5 {
        ModClientNetworking.sendToServer(mineturismo$payload);
        //?} else {
        /*ModClientNetworking.sendToServer(SetCarCustomTuningPayload.ID, mineturismo$payload, SetCarCustomTuningPayload::write);*/
        //?}
    }

    private static final float TOP_SPEED_LOW_KMH = 45.0F;
    private static final double TOP_SPEED_LOW_SPLIT = 0.25;

    private static float sliderToTopSpeed(double value) {
        float kmh;
        if (value <= TOP_SPEED_LOW_SPLIT) {
            kmh = Mth.lerp((float) (value / TOP_SPEED_LOW_SPLIT), CarEntity.CUSTOM_TOP_SPEED_KMH_MIN, TOP_SPEED_LOW_KMH);
        } else {
            kmh = Mth.lerp((float) ((value - TOP_SPEED_LOW_SPLIT) / (1.0 - TOP_SPEED_LOW_SPLIT)), TOP_SPEED_LOW_KMH, CarEntity.CUSTOM_TOP_SPEED_KMH_MAX);
        }

        return Mth.clamp(Math.round(kmh * 2.0F) / 2.0F, CarEntity.CUSTOM_TOP_SPEED_KMH_MIN, CarEntity.CUSTOM_TOP_SPEED_KMH_MAX);
    }

    private static double topSpeedToSlider(float kmh) {
        if (kmh <= TOP_SPEED_LOW_KMH) {
            return (kmh - CarEntity.CUSTOM_TOP_SPEED_KMH_MIN) / (TOP_SPEED_LOW_KMH - CarEntity.CUSTOM_TOP_SPEED_KMH_MIN) * TOP_SPEED_LOW_SPLIT;
        }
        return TOP_SPEED_LOW_SPLIT + (kmh - TOP_SPEED_LOW_KMH) / (CarEntity.CUSTOM_TOP_SPEED_KMH_MAX - TOP_SPEED_LOW_KMH) * (1.0 - TOP_SPEED_LOW_SPLIT);
    }

    private static String formatTopSpeed(float kmh) {
        return kmh == (float) Math.floor(kmh) ? String.format("%.0f km/h", kmh) : String.format("%.1f km/h", kmh);
    }

    @Override
    protected void init() {
        int sliderWidth = 220;
        int x = this.width / 2 - sliderWidth / 2;
        int y = this.height / 2 - 70;
        int rowHeight = 24;

        this.addRenderableWidget(ModCheckbox.builder(ModText.translatable("gui.mineturismo.custom_tuning.despawn_enabled"), this.font)
                .pos(x, y)
                .selected(this.car.isDespawnEnabled())
                .onValueChange((checkbox, selected) -> {
                    this.car.setDespawnEnabled(selected);
                    this.sendTuning();
                })
                .build());

        int slidersY = y + rowHeight;

        this.addRenderableWidget(new TuningSlider(x, slidersY, sliderWidth, 20, topSpeedToSlider(this.car.getCustomTopSpeedKmh()),
                "gui.mineturismo.custom_tuning.top_speed",
                value -> formatTopSpeed(sliderToTopSpeed(value)),
                value -> this.car.setCustomTopSpeedKmh(sliderToTopSpeed(value))));

        this.addRenderableWidget(new TuningSlider(x, slidersY + rowHeight, sliderWidth, 20, this.car.getCustomAccelFraction(),
                "gui.mineturismo.custom_tuning.acceleration",
                value -> String.format("%.0f%%", value * 100.0),
                value -> this.car.setCustomAccelFraction((float) value)));

        this.addRenderableWidget(new TuningSlider(x, slidersY + rowHeight * 2, sliderWidth, 20, this.car.getCustomBrakeFraction(),
                "gui.mineturismo.custom_tuning.braking",
                value -> String.format("%.0f%%", value * 100.0),
                value -> this.car.setCustomBrakeFraction((float) value)));

        this.addRenderableWidget(new TuningSlider(x, slidersY + rowHeight * 3, sliderWidth, 20, this.car.getCustomHandlingFraction(),
                "gui.mineturismo.custom_tuning.handling",
                value -> String.format("%.0f%%", value * 100.0),
                value -> this.car.setCustomHandlingFraction((float) value)));

        this.addRenderableWidget(ModCheckbox.builder(ModText.translatable("gui.mineturismo.custom_tuning.tire_smoke_always"), this.font)
                .pos(x, slidersY + rowHeight * 4 + 8)
                .selected(this.car.isCustomTireSmokeAlways())
                .onValueChange((checkbox, selected) -> {
                    this.car.setCustomTireSmokeAlways(selected);
                    this.sendTuning();
                })
                .build());

        this.addRenderableWidget(ModWidgets.button(ModText.translatable("gui.mineturismo.back"), x, slidersY + rowHeight * 4 + 38, sliderWidth, 20, button ->
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
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - 100, 0xFFFFFFFF);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private class TuningSlider extends AbstractSliderButton {
        private final String labelKey;
        private final DoubleFunction<String> formatter;
        private final DoubleConsumer onChange;

        TuningSlider(int x, int y, int width, int height, double initialValue,
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
            CarCustomTuningScreen.this.sendTuning();
        }
    }
}
