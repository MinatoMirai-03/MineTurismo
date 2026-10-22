package com.examplebett.client.gui;
import com.examplebett.compat.ModWidgets;
import com.examplebett.compat.ModText;

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

public class WaypointControlsScreen extends Screen {

    private static final int LINE_HEIGHT = 14;

    private final Screen parent;

    public WaypointControlsScreen(Screen parent) {
        super(ModText.translatable("gui.mineturismo.waypoint_traffic.controls_title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        this.addRenderableWidget(ModWidgets.button(ModText.translatable("gui.mineturismo.back"), centerX - 100, this.height - 40, 200, 20, button ->
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
        int top = this.height / 2 - 80;

        guiGraphics.drawCenteredString(this.font, this.title, centerX, top - 30, 0xFFFFFFFF);

        Component[] lines = {
                ModText.translatable("gui.mineturismo.waypoint_traffic.controls_line1"),
                ModText.translatable("gui.mineturismo.waypoint_traffic.controls_line2"),
                ModText.translatable("gui.mineturismo.waypoint_traffic.controls_line3"),
                ModText.translatable("gui.mineturismo.waypoint_traffic.controls_line4"),
        };
        for (int i = 0; i < lines.length; i++) {
            guiGraphics.drawCenteredString(this.font, lines[i], centerX, top + i * LINE_HEIGHT, 0xFFFFFFFF);
        }

        int oneWayTop = top + lines.length * LINE_HEIGHT + 12;
        guiGraphics.drawCenteredString(this.font,
                ModText.translatable("gui.mineturismo.waypoint_traffic.controls_one_way_header"),
                centerX, oneWayTop, 0xFFFFFF55);

        Component[] oneWayLines = {
                ModText.translatable("gui.mineturismo.one_way_marker.line1"),
                ModText.translatable("gui.mineturismo.one_way_marker.line2"),
                ModText.translatable("gui.mineturismo.one_way_marker.line3"),
                ModText.translatable("gui.mineturismo.one_way_marker.line4"),
        };
        for (int i = 0; i < oneWayLines.length; i++) {
            guiGraphics.drawCenteredString(this.font, oneWayLines[i], centerX, oneWayTop + 16 + i * LINE_HEIGHT, 0xFFFFFFFF);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
