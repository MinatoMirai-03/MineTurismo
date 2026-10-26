package com.examplebett.client.gui;
import com.examplebett.compat.ModWidgets;
import com.examplebett.compat.ModText;

import com.examplebett.compat.ModClientNetworking;
import com.examplebett.network.SetTownTrafficMovementModePayload;
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

public class WaypointMovementModeScreen extends Screen {

    private static final int BUTTON_WIDTH = 220;
    private static final int BUTTON_HEIGHT = 20;
    private static final int ROW_GAP = 8;

    static boolean cachedUseLegacySteering = false;

    private final Screen parent;

    public WaypointMovementModeScreen(Screen parent) {
        super(ModText.translatable("gui.mineturismo.waypoint_traffic.movement_mode_title"));
        this.parent = parent;
    }

    private void select(boolean useLegacySteering) {
        cachedUseLegacySteering = useLegacySteering;
        SetTownTrafficMovementModePayload mineturismo$payload = new SetTownTrafficMovementModePayload(useLegacySteering);
        //? if >=1.20.5 {
        ModClientNetworking.sendToServer(mineturismo$payload);
        //?} else {
        /*ModClientNetworking.sendToServer(SetTownTrafficMovementModePayload.ID, mineturismo$payload, SetTownTrafficMovementModePayload::write);*/
        //?}
        this.minecraft.setScreen(new WaypointMovementModeScreen(this.parent));
    }

    private Component optionLabel(boolean useLegacySteering, String translationKey) {
        Component label = ModText.translatable(translationKey);
        return cachedUseLegacySteering == useLegacySteering ? ModText.literal("> ").append(label) : label;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int top = this.height / 2 - 40;

        this.addRenderableWidget(ModWidgets.button(this.optionLabel(false, "gui.mineturismo.waypoint_traffic.movement_mode_line"), centerX - BUTTON_WIDTH / 2, top, BUTTON_WIDTH, BUTTON_HEIGHT, button -> this.select(false)));

        this.addRenderableWidget(ModWidgets.button(this.optionLabel(true, "gui.mineturismo.waypoint_traffic.movement_mode_legacy"), centerX - BUTTON_WIDTH / 2, top + (BUTTON_HEIGHT + ROW_GAP), BUTTON_WIDTH, BUTTON_HEIGHT, button -> this.select(true)));

        this.addRenderableWidget(ModWidgets.button(ModText.translatable("gui.mineturismo.back"), centerX - BUTTON_WIDTH / 2, top + (BUTTON_HEIGHT + ROW_GAP) * 2 + ROW_GAP, BUTTON_WIDTH, BUTTON_HEIGHT, button ->
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
        int top = this.height / 2 - 40;
        guiGraphics.drawCenteredString(this.font, this.title, centerX, top - 30, 0xFFFFFFFF);
        String descriptionKey = cachedUseLegacySteering
                ? "gui.mineturismo.waypoint_traffic.movement_mode_legacy_desc"
                : "gui.mineturismo.waypoint_traffic.movement_mode_line_desc";
        guiGraphics.drawCenteredString(this.font, ModText.translatable(descriptionKey), centerX, top + 70, 0xAAAAAAAA);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
