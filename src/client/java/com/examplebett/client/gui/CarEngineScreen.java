package com.examplebett.client.gui;
import com.examplebett.compat.ModWidgets;
import com.examplebett.compat.ModText;

import com.examplebett.compat.ModClientNetworking;
import com.examplebett.entity.CarEntity;
import com.examplebett.entity.EngineSounds;
import com.examplebett.network.SetCarEnginePayload;
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

import java.util.ArrayList;
import java.util.List;

public class CarEngineScreen extends Screen implements CarCustomizationScreen {

    private static final int SOUND_COLUMNS = 2;
    private static final int SOUND_BUTTON_WIDTH = 120;
    private static final int SOUND_BUTTON_HEIGHT = 20;
    private static final int SOUND_COL_GAP = 8;
    private static final int SOUND_ROW_GAP = 4;
    private static final int SOUND_VISIBLE_ROWS = 3;
    private static final int SOUND_ROW_HEIGHT = SOUND_BUTTON_HEIGHT + SOUND_ROW_GAP;

    private final CarEntity car;
    private final Screen parent;

    private final List<SoundButtonEntry> soundButtons = new ArrayList<>();
    private int soundScrollRow = 0;
    private int soundMaxScrollRow = 0;
    private int soundViewportY;

    private record SoundButtonEntry(Button button, int row) {
    }

    public CarEngineScreen(CarEntity car, Screen parent) {
        super(ModText.translatable("gui.mineturismo.engine.title"));
        this.car = car;
        this.parent = parent;
    }

    private void updateSoundButtonPositions() {
        int viewportTop = this.soundViewportY;
        for (SoundButtonEntry entry : this.soundButtons) {
            int visibleRow = entry.row() - this.soundScrollRow;
            boolean isVisible = visibleRow >= 0 && visibleRow < SOUND_VISIBLE_ROWS;
            entry.button().visible = isVisible;
            entry.button().active = isVisible;
            if (isVisible) {
                ModWidgets.setY(entry.button(), viewportTop + visibleRow * SOUND_ROW_HEIGHT);
            }
        }
    }

    //? if >=1.20.2 {
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        return mineturismo$mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }
    //?} else {
    /*@Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollY) {
        return mineturismo$mouseScrolled(mouseX, mouseY, 0, scrollY);
    }*/
    //?}

    private boolean mineturismo$mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (this.soundMaxScrollRow > 0 && scrollY != 0) {
            int delta = scrollY > 0 ? -1 : 1;
            int newScrollRow = Math.max(0, Math.min(this.soundMaxScrollRow, this.soundScrollRow + delta));
            if (newScrollRow != this.soundScrollRow) {
                this.soundScrollRow = newScrollRow;
                this.updateSoundButtonPositions();
                return true;
            }
        }
        //? if >=1.20.2 {
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        //?} else {
        /*return super.mouseScrolled(mouseX, mouseY, scrollY);*/
        //?}
    }

    private void sendEngineSettings() {
        SetCarEnginePayload mineturismo$payload = new SetCarEnginePayload(
                this.car.getId(), this.car.getEngineSoundId(),
                this.car.getEnginePitchIntensity(), this.car.getEngineTopSpeedPitch(),
                this.car.getEngineLoopUseFraction());
        //? if >=1.20.5 {
        ModClientNetworking.sendToServer(mineturismo$payload);
        //?} else {
        /*ModClientNetworking.sendToServer(SetCarEnginePayload.ID, mineturismo$payload, SetCarEnginePayload::write);*/
        //?}
    }

    private void applySound(String soundId) {
        this.car.setEngineSoundId(soundId);
        this.sendEngineSettings();
        this.minecraft.setScreen(new CarEngineScreen(this.car, this.parent));
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int top = this.height / 2 - 90;

        int sliderWidth = 220;
        int sliderX = centerX - sliderWidth / 2;
        this.addRenderableWidget(new PitchSlider(sliderX, top, sliderWidth, 20, this.car.getEnginePitchIntensity()));

        double topSpeedPitchInitial = (this.car.getEngineTopSpeedPitch() - CarEntity.ENGINE_TOP_SPEED_PITCH_MIN)
                / (CarEntity.ENGINE_TOP_SPEED_PITCH_MAX - CarEntity.ENGINE_TOP_SPEED_PITCH_MIN);
        this.addRenderableWidget(new TopSpeedPitchSlider(sliderX, top + 24, sliderWidth, 20, topSpeedPitchInitial));

        double loopLengthInitial = (this.car.getEngineLoopUseFraction() - CarEntity.ENGINE_LOOP_USE_FRACTION_MIN)
                / (1.0f - CarEntity.ENGINE_LOOP_USE_FRACTION_MIN);
        this.addRenderableWidget(new LoopLengthSlider(sliderX, top + 48, sliderWidth, 20, loopLengthInitial));

        this.soundButtons.clear();
        this.soundScrollRow = 0;

        int gridWidth = SOUND_COLUMNS * SOUND_BUTTON_WIDTH + (SOUND_COLUMNS - 1) * SOUND_COL_GAP;
        int soundX = centerX - gridWidth / 2;
        int soundY = top + 80;
        this.soundViewportY = soundY;
        String currentSoundId = this.car.getEngineSoundId();

        int totalRows = (EngineSounds.OPTIONS.size() + SOUND_COLUMNS - 1) / SOUND_COLUMNS;
        this.soundMaxScrollRow = Math.max(0, totalRows - SOUND_VISIBLE_ROWS);

        for (int i = 0; i < EngineSounds.OPTIONS.size(); i++) {
            EngineSounds.Option option = EngineSounds.OPTIONS.get(i);
            int row = i / SOUND_COLUMNS;
            int col = i % SOUND_COLUMNS;
            Component label = option.id().equals(currentSoundId)
                    ? ModText.literal("> ").append(option.displayName())
                    : option.displayName();
            int x = soundX + col * (SOUND_BUTTON_WIDTH + SOUND_COL_GAP);
            Button button = ModWidgets.button(label, x, soundY + row * SOUND_ROW_HEIGHT, SOUND_BUTTON_WIDTH, SOUND_BUTTON_HEIGHT, button2 -> this.applySound(option.id()));
            this.addRenderableWidget(button);
            this.soundButtons.add(new SoundButtonEntry(button, row));
        }

        int viewportHeight = SOUND_VISIBLE_ROWS * SOUND_ROW_HEIGHT - SOUND_ROW_GAP;
        int arrowX = soundX + gridWidth + 8;

        this.addRenderableWidget(new ScrollbarWidget(ScrollbarWidget.xInArrowColumn(arrowX), soundY,
                soundY + viewportHeight - 20 + 20 - (soundY), SOUND_VISIBLE_ROWS, this.soundMaxScrollRow + SOUND_VISIBLE_ROWS, () -> this.soundScrollRow, row -> {
                    this.soundScrollRow = row;
                    this.updateSoundButtonPositions();
                }));

        this.updateSoundButtonPositions();

        int soundBottom = soundY + viewportHeight;
        int buttonWidth = 200;
        this.addRenderableWidget(ModWidgets.button(ModText.translatable("gui.mineturismo.back"), centerX - buttonWidth / 2, soundBottom + 8, buttonWidth, 20, button ->
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
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - 110, 0xFFFFFFFF);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private class PitchSlider extends AbstractSliderButton {
        PitchSlider(int x, int y, int width, int height, double initialValue) {
            super(x, y, width, height, ModText.literal(""), Mth.clamp(initialValue, 0.0, 1.0));
            this.updateMessage();
        }

        @Override
        protected void updateMessage() {
            this.setMessage(ModText.translatable("gui.mineturismo.engine.pitch_variation", String.format("%.0f%%", this.value * 100.0)));
        }

        @Override
        protected void applyValue() {
            CarEngineScreen.this.car.setEnginePitchIntensity((float) this.value);
        }

        @Override
        //? if >=1.21.9 {
        public void onRelease(MouseButtonEvent event) {
            super.onRelease(event);
        //?} else
        /*public void onRelease(double mouseX, double mouseY) {
            super.onRelease(mouseX, mouseY);*/
            CarEngineScreen.this.sendEngineSettings();
        }
    }

    private class TopSpeedPitchSlider extends AbstractSliderButton {
        TopSpeedPitchSlider(int x, int y, int width, int height, double initialValue) {
            super(x, y, width, height, ModText.literal(""), Mth.clamp(initialValue, 0.0, 1.0));
            this.updateMessage();
        }

        private float toTopSpeedPitch(double sliderValue) {
            return Mth.lerp((float) sliderValue, CarEntity.ENGINE_TOP_SPEED_PITCH_MIN, CarEntity.ENGINE_TOP_SPEED_PITCH_MAX);
        }

        @Override
        protected void updateMessage() {
            this.setMessage(ModText.translatable("gui.mineturismo.engine.top_speed_pitch", String.format("%.2f", this.toTopSpeedPitch(this.value))));
        }

        @Override
        protected void applyValue() {
            CarEngineScreen.this.car.setEngineTopSpeedPitch(this.toTopSpeedPitch(this.value));
        }

        @Override
        //? if >=1.21.9 {
        public void onRelease(MouseButtonEvent event) {
            super.onRelease(event);
        //?} else
        /*public void onRelease(double mouseX, double mouseY) {
            super.onRelease(mouseX, mouseY);*/
            CarEngineScreen.this.sendEngineSettings();
        }
    }

    private class LoopLengthSlider extends AbstractSliderButton {
        LoopLengthSlider(int x, int y, int width, int height, double initialValue) {
            super(x, y, width, height, ModText.literal(""), Mth.clamp(initialValue, 0.0, 1.0));
            this.updateMessage();
        }

        private float toFraction(double sliderValue) {
            return Mth.lerp((float) sliderValue, CarEntity.ENGINE_LOOP_USE_FRACTION_MIN, 1.0f);
        }

        @Override
        protected void updateMessage() {
            float fullSeconds = EngineSounds.byId(CarEngineScreen.this.car.getEngineSoundId()).loopDurationTicks() / 20.0f;
            float useSeconds = fullSeconds * this.toFraction(this.value);
            this.setMessage(ModText.translatable("gui.mineturismo.engine.loop_length",
                    String.format("%.1fs / %.1fs", useSeconds, fullSeconds)));
        }

        @Override
        protected void applyValue() {
            CarEngineScreen.this.car.setEngineLoopUseFraction(this.toFraction(this.value));
        }

        @Override
        //? if >=1.21.9 {
        public void onRelease(MouseButtonEvent event) {
            super.onRelease(event);
        //?} else
        /*public void onRelease(double mouseX, double mouseY) {
            super.onRelease(mouseX, mouseY);*/
            CarEngineScreen.this.sendEngineSettings();
        }
    }
}
