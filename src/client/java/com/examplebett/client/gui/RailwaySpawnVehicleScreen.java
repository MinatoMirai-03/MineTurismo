package com.examplebett.client.gui;
import com.examplebett.compat.ModWidgets;
import com.examplebett.compat.ModText;

import com.examplebett.compat.ModClientNetworking;
import com.examplebett.network.OpenRailwayFormationEditorPayload;
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

public class RailwaySpawnVehicleScreen extends Screen {

    private static final int BUTTON_WIDTH = 240;
    private static final int BUTTON_HEIGHT = 20;
    private static final int ROW_GAP = 8;

    private final int originNodeId;

    public RailwaySpawnVehicleScreen(int originNodeId) {
        super(ModText.translatable("gui.mineturismo.railway_spawn.title"));
        this.originNodeId = originNodeId;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int top = this.height / 2 - 50;

        this.addRenderableWidget(ModWidgets.button(ModText.translatable("gui.mineturismo.railway_spawn.pick_existing"),
                centerX - BUTTON_WIDTH / 2, top, BUTTON_WIDTH, BUTTON_HEIGHT, b ->
                        this.minecraft.setScreen(new RailwayRouteVehiclePickerScreen(this, this.originNodeId, true))));

        this.addRenderableWidget(ModWidgets.button(ModText.translatable("gui.mineturismo.railway_spawn.pick_any"),
                centerX - BUTTON_WIDTH / 2, top + BUTTON_HEIGHT + ROW_GAP, BUTTON_WIDTH, BUTTON_HEIGHT, b ->
                        this.minecraft.setScreen(new RailwayRouteVehiclePickerScreen(this, this.originNodeId, false))));

        this.addRenderableWidget(ModWidgets.button(ModText.translatable("gui.mineturismo.railway_spawn.pick_custom"),
                centerX - BUTTON_WIDTH / 2, top + (BUTTON_HEIGHT + ROW_GAP) * 2, BUTTON_WIDTH, BUTTON_HEIGHT, b -> {
                    OpenRailwayFormationEditorPayload payload = new OpenRailwayFormationEditorPayload(this.originNodeId);
                    //? if >=1.20.5 {
                    ModClientNetworking.sendToServer(payload);
                    //?} else
                    /*ModClientNetworking.sendToServer(OpenRailwayFormationEditorPayload.ID, payload, OpenRailwayFormationEditorPayload::write);*/
                    this.minecraft.setScreen(null);
                }));
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
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - 70, 0xFFFFFFFF);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
