package com.examplebett.client.gui;
import com.examplebett.compat.ModCheckbox;
import com.examplebett.compat.ModWidgets;
import com.examplebett.compat.ModText;

import com.examplebett.compat.ModClientNetworking;
import com.examplebett.network.SetAmbientEngineVolumePayload;
import com.examplebett.network.SetTownAiThrottlePayload;
import com.examplebett.network.SetTownMaxSpawnedCarsPayload;
import com.examplebett.network.SetTownNightLightsPayload;
import com.examplebett.network.SetTownRecallDistantCarsPayload;
import com.examplebett.network.SetTownSpawnIntervalPayload;
import com.examplebett.waypoint.WaypointNetworkSavedData;
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
import net.minecraft.util.Mth;

public class WaypointMiscSettingsScreen extends Screen {

    static boolean cachedRecallDistantCarsEnabled = false;
    static boolean cachedNightLightsEnabled = true;
    static int cachedMaxSpawnedCars = 3;
    static int cachedAiThrottleIntervalTicks = 4;
    static int cachedSpawnIntervalTicks = 150;
    static float cachedAmbientEngineVolumeMultiplier = WaypointNetworkSavedData.DEFAULT_AMBIENT_ENGINE_VOLUME_MULTIPLIER;

    private final Screen parent;
    private int maxCarsValueY;
    private int aiThrottleValueY;
    private int spawnIntervalValueY;
    private int ambientVolumeValueY;

    public WaypointMiscSettingsScreen(Screen parent) {
        super(ModText.translatable("gui.mineturismo.waypoint_traffic.misc_title"));
        this.parent = parent;
    }

    private void sendRecallDistantCarsEnabled(boolean enabled) {
        cachedRecallDistantCarsEnabled = enabled;
        SetTownRecallDistantCarsPayload mineturismo$payload = new SetTownRecallDistantCarsPayload(enabled);
        //? if >=1.20.5 {
        ModClientNetworking.sendToServer(mineturismo$payload);
        //?} else {
        /*ModClientNetworking.sendToServer(SetTownRecallDistantCarsPayload.ID, mineturismo$payload, SetTownRecallDistantCarsPayload::write);*/
        //?}
    }

    private void sendNightLightsEnabled(boolean enabled) {
        cachedNightLightsEnabled = enabled;
        SetTownNightLightsPayload mineturismo$payload = new SetTownNightLightsPayload(enabled);
        //? if >=1.20.5 {
        ModClientNetworking.sendToServer(mineturismo$payload);
        //?} else {
        /*ModClientNetworking.sendToServer(SetTownNightLightsPayload.ID, mineturismo$payload, SetTownNightLightsPayload::write);*/
        //?}
    }

    private void adjustMaxSpawnedCars(int delta) {
        cachedMaxSpawnedCars = Mth.clamp(cachedMaxSpawnedCars + delta,
                WaypointNetworkSavedData.MIN_MAX_SPAWNED_CARS, WaypointNetworkSavedData.MAX_MAX_SPAWNED_CARS);
        SetTownMaxSpawnedCarsPayload mineturismo$payload = new SetTownMaxSpawnedCarsPayload(cachedMaxSpawnedCars);
        //? if >=1.20.5 {
        ModClientNetworking.sendToServer(mineturismo$payload);
        //?} else {
        /*ModClientNetworking.sendToServer(SetTownMaxSpawnedCarsPayload.ID, mineturismo$payload, SetTownMaxSpawnedCarsPayload::write);*/
        //?}
    }

    private void adjustAiThrottle(int delta) {
        cachedAiThrottleIntervalTicks = Mth.clamp(cachedAiThrottleIntervalTicks + delta,
                WaypointNetworkSavedData.MIN_AI_THROTTLE_INTERVAL_TICKS, WaypointNetworkSavedData.MAX_AI_THROTTLE_INTERVAL_TICKS);
        SetTownAiThrottlePayload mineturismo$payload = new SetTownAiThrottlePayload(cachedAiThrottleIntervalTicks);
        //? if >=1.20.5 {
        ModClientNetworking.sendToServer(mineturismo$payload);
        //?} else {
        /*ModClientNetworking.sendToServer(SetTownAiThrottlePayload.ID, mineturismo$payload, SetTownAiThrottlePayload::write);*/
        //?}
    }

    private void adjustSpawnInterval(int delta) {
        cachedSpawnIntervalTicks = Mth.clamp(cachedSpawnIntervalTicks + delta,
                WaypointNetworkSavedData.MIN_SPAWN_INTERVAL_TICKS, WaypointNetworkSavedData.MAX_SPAWN_INTERVAL_TICKS);
        SetTownSpawnIntervalPayload mineturismo$payload = new SetTownSpawnIntervalPayload(cachedSpawnIntervalTicks);
        //? if >=1.20.5 {
        ModClientNetworking.sendToServer(mineturismo$payload);
        //?} else {
        /*ModClientNetworking.sendToServer(SetTownSpawnIntervalPayload.ID, mineturismo$payload, SetTownSpawnIntervalPayload::write);*/
        //?}
    }

    private void adjustAmbientEngineVolume(float delta) {
        cachedAmbientEngineVolumeMultiplier = Mth.clamp(cachedAmbientEngineVolumeMultiplier + delta,
                WaypointNetworkSavedData.MIN_AMBIENT_ENGINE_VOLUME_MULTIPLIER, WaypointNetworkSavedData.MAX_AMBIENT_ENGINE_VOLUME_MULTIPLIER);
        SetAmbientEngineVolumePayload mineturismo$payload = new SetAmbientEngineVolumePayload(cachedAmbientEngineVolumeMultiplier);
        //? if >=1.20.5 {
        ModClientNetworking.sendToServer(mineturismo$payload);
        //?} else {
        /*ModClientNetworking.sendToServer(SetAmbientEngineVolumePayload.ID, mineturismo$payload, SetAmbientEngineVolumePayload::write);*/
        //?}
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int checkboxY = this.height / 2 - 130;
        int nightLightsCheckboxY = checkboxY + 24;
        int row1Y = this.height / 2 - 70;
        int row2Y = row1Y + 64;
        int row3Y = row2Y + 64;
        int row4Y = row3Y + 64;
        this.maxCarsValueY = row1Y + 24;
        this.aiThrottleValueY = row2Y + 24;
        this.spawnIntervalValueY = row3Y + 24;
        this.ambientVolumeValueY = row4Y + 24;

        this.addRenderableWidget(ModCheckbox.builder(ModText.translatable("gui.mineturismo.waypoint_traffic.recall_distant_cars"), this.font)
                .pos(centerX - 100, checkboxY)
                .selected(cachedRecallDistantCarsEnabled)
                .onValueChange((checkbox, selected) -> this.sendRecallDistantCarsEnabled(selected))
                .build());

        this.addRenderableWidget(ModCheckbox.builder(ModText.translatable("gui.mineturismo.waypoint_traffic.night_lights"), this.font)
                .pos(centerX - 100, nightLightsCheckboxY)
                .selected(cachedNightLightsEnabled)
                .onValueChange((checkbox, selected) -> this.sendNightLightsEnabled(selected))
                .build());

        this.addRenderableWidget(ModWidgets.button(ModText.literal("-10"), centerX - 130, row1Y, 40, 20, button -> adjustMaxSpawnedCars(-10)));
        this.addRenderableWidget(ModWidgets.button(ModText.literal("-1"), centerX - 85, row1Y, 40, 20, button -> adjustMaxSpawnedCars(-1)));
        this.addRenderableWidget(ModWidgets.button(ModText.literal("+1"), centerX + 45, row1Y, 40, 20, button -> adjustMaxSpawnedCars(1)));
        this.addRenderableWidget(ModWidgets.button(ModText.literal("+10"), centerX + 90, row1Y, 40, 20, button -> adjustMaxSpawnedCars(10)));

        this.addRenderableWidget(ModWidgets.button(ModText.literal("-1"), centerX - 85, row2Y, 40, 20, button -> adjustAiThrottle(-1)));
        this.addRenderableWidget(ModWidgets.button(ModText.literal("+1"), centerX + 45, row2Y, 40, 20, button -> adjustAiThrottle(1)));

        this.addRenderableWidget(ModWidgets.button(ModText.literal("-100"), centerX - 130, row3Y, 40, 20, button -> adjustSpawnInterval(-100)));
        this.addRenderableWidget(ModWidgets.button(ModText.literal("-20"), centerX - 85, row3Y, 40, 20, button -> adjustSpawnInterval(-20)));
        this.addRenderableWidget(ModWidgets.button(ModText.literal("+20"), centerX + 45, row3Y, 40, 20, button -> adjustSpawnInterval(20)));
        this.addRenderableWidget(ModWidgets.button(ModText.literal("+100"), centerX + 90, row3Y, 40, 20, button -> adjustSpawnInterval(100)));

        this.addRenderableWidget(ModWidgets.button(ModText.literal("-1.0"), centerX - 130, row4Y, 40, 20, button -> adjustAmbientEngineVolume(-1.0f)));
        this.addRenderableWidget(ModWidgets.button(ModText.literal("-0.2"), centerX - 85, row4Y, 40, 20, button -> adjustAmbientEngineVolume(-0.2f)));
        this.addRenderableWidget(ModWidgets.button(ModText.literal("+0.2"), centerX + 45, row4Y, 40, 20, button -> adjustAmbientEngineVolume(0.2f)));
        this.addRenderableWidget(ModWidgets.button(ModText.literal("+1.0"), centerX + 90, row4Y, 40, 20, button -> adjustAmbientEngineVolume(1.0f)));

        this.addRenderableWidget(ModWidgets.button(ModText.translatable("gui.mineturismo.back"), centerX - 100, row4Y + 50, 200, 20, button ->
                this.minecraft.setScreen(this.parent)));
    }

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
        int checkboxY = this.height / 2 - 130;
        int row1Y = this.height / 2 - 70;
        int row2Y = row1Y + 64;
        int row3Y = row2Y + 64;
        int row4Y = row3Y + 64;

        guiGraphics.drawCenteredString(this.font, this.title, centerX, checkboxY - 30, 0xFFFFFFFF);

        guiGraphics.drawCenteredString(this.font,
                ModText.translatable("gui.mineturismo.waypoint_traffic.max_spawned_cars_title"),
                centerX, row1Y - 14, 0xFFFFFFFF);
        guiGraphics.drawCenteredString(this.font, String.valueOf(cachedMaxSpawnedCars),
                centerX, this.maxCarsValueY, 0xFFFFFFFF);
        guiGraphics.drawCenteredString(this.font,
                ModText.translatable("gui.mineturismo.waypoint_traffic.max_spawned_cars_desc"),
                centerX, this.maxCarsValueY + 14, 0xFFAAAAAA);

        guiGraphics.drawCenteredString(this.font,
                ModText.translatable("gui.mineturismo.waypoint_traffic.ai_throttle_title"),
                centerX, row2Y - 14, 0xFFFFFFFF);
        Component aiThrottleValue = cachedAiThrottleIntervalTicks <= 0
                ? ModText.translatable("gui.mineturismo.waypoint_traffic.ai_throttle_disabled")
                : ModText.translatable("gui.mineturismo.waypoint_traffic.ai_throttle_ticks", cachedAiThrottleIntervalTicks);
        guiGraphics.drawCenteredString(this.font, aiThrottleValue, centerX, this.aiThrottleValueY, 0xFFFFFFFF);
        guiGraphics.drawCenteredString(this.font,
                ModText.translatable("gui.mineturismo.waypoint_traffic.ai_throttle_desc"),
                centerX, this.aiThrottleValueY + 14, 0xFFAAAAAA);

        guiGraphics.drawCenteredString(this.font,
                ModText.translatable("gui.mineturismo.waypoint_traffic.spawn_interval_title"),
                centerX, row3Y - 14, 0xFFFFFFFF);
        guiGraphics.drawCenteredString(this.font,
                String.format("%.1fs (%d ticks)", cachedSpawnIntervalTicks / 20.0, cachedSpawnIntervalTicks),
                centerX, this.spawnIntervalValueY, 0xFFFFFFFF);
        guiGraphics.drawCenteredString(this.font,
                ModText.translatable("gui.mineturismo.waypoint_traffic.spawn_interval_desc"),
                centerX, this.spawnIntervalValueY + 14, 0xFFAAAAAA);

        guiGraphics.drawCenteredString(this.font,
                ModText.translatable("gui.mineturismo.waypoint_traffic.ambient_volume_title"),
                centerX, row4Y - 14, 0xFFFFFFFF);
        guiGraphics.drawCenteredString(this.font, String.format("%.1fx", cachedAmbientEngineVolumeMultiplier),
                centerX, this.ambientVolumeValueY, 0xFFFFFFFF);
        guiGraphics.drawCenteredString(this.font,
                ModText.translatable("gui.mineturismo.waypoint_traffic.ambient_volume_desc"),
                centerX, this.ambientVolumeValueY + 14, 0xFFAAAAAA);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
