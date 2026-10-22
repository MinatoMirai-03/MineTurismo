package com.examplebett.client.gui;
import com.examplebett.compat.ModWidgets;
import com.examplebett.compat.ModText;

import com.examplebett.compat.ModClientNetworking;
import com.examplebett.network.SetWaypointPenSpeedPayload;
import com.examplebett.waypoint.WaypointNode;
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

public class WaypointSpeedLimitScreen extends Screen {

    private static final float MIN_SPEED_KMH = 10f;
    private static final float MAX_SPEED_KMH = 150f;
    private static final float SPEED_STEP = 5f;

    private static float cachedSpeedKmh = WaypointNode.DEFAULT_SPEED_KMH;

    private final Screen parent;
    private int valueY;

    public WaypointSpeedLimitScreen(Screen parent) {
        super(ModText.translatable("gui.mineturismo.waypoint_traffic.speed_limit_title"));
        this.parent = parent;
    }

    private void adjustSpeed(float delta) {
        cachedSpeedKmh = Math.max(MIN_SPEED_KMH, Math.min(MAX_SPEED_KMH, cachedSpeedKmh + delta));
        SetWaypointPenSpeedPayload mineturismo$payload = new SetWaypointPenSpeedPayload(cachedSpeedKmh);
        //? if >=1.20.5 {
        ModClientNetworking.sendToServer(mineturismo$payload);
        //?} else {
        /*ModClientNetworking.sendToServer(SetWaypointPenSpeedPayload.ID, mineturismo$payload, SetWaypointPenSpeedPayload::write);*/
        //?}
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int buttonY = this.height / 2 - 10;
        this.valueY = buttonY + 6;

        this.addRenderableWidget(ModWidgets.button(ModText.literal("-5"), centerX - 80, buttonY, 40, 20, button -> adjustSpeed(-SPEED_STEP)));
        this.addRenderableWidget(ModWidgets.button(ModText.literal("+5"), centerX + 40, buttonY, 40, 20, button -> adjustSpeed(SPEED_STEP)));

        this.addRenderableWidget(ModWidgets.button(ModText.translatable("gui.mineturismo.back"), centerX - 100, buttonY + 60, 200, 20, button ->
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
        guiGraphics.drawCenteredString(this.font, this.title, centerX, this.height / 2 - 60, 0xFFFFFFFF);
        guiGraphics.drawCenteredString(this.font,
                ModText.translatable("gui.mineturismo.waypoint_traffic.speed_limit_desc"),
                centerX, this.height / 2 - 40, 0xFFFFFFFF);
        guiGraphics.drawCenteredString(this.font, String.format("%.0f km/h", cachedSpeedKmh),
                centerX, this.valueY, 0xFFFFFFFF);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
