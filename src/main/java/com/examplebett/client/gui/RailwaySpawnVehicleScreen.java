//? if >=1.18 {
//?}
//? if <1.18 && >=1.17 {
/*
package com.examplebett.client.gui;
import com.examplebett.compat.ModWidgets;
import com.examplebett.compat.ModText;

import com.examplebett.compat.ModClientNetworking;
import com.examplebett.network.OpenRailwayFormationEditorPayload;

import com.examplebett.compat.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;
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
                    ModClientNetworking.sendToServer(OpenRailwayFormationEditorPayload.ID, payload, OpenRailwayFormationEditorPayload::write);
                    this.minecraft.setScreen(null);
                }));
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        mineturismo$renderScreen(new GuiGraphics(poseStack), mouseX, mouseY, partialTick);
    }

    private void mineturismo$renderScreen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        super.render(guiGraphics.pose(), mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - 70, 0xFFFFFFFF);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
*/
//?}
//? if <1.17 && >=1.16 {
/*
package com.examplebett.client.gui;
import com.examplebett.compat.ModWidgets;
import com.examplebett.compat.ModText;

import com.examplebett.compat.ModClientNetworking;
import com.examplebett.network.OpenRailwayFormationEditorPayload;

import com.examplebett.compat.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;
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

        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.railway_spawn.pick_existing"),
                centerX - BUTTON_WIDTH / 2, top, BUTTON_WIDTH, BUTTON_HEIGHT, b ->
                        this.minecraft.setScreen(new RailwayRouteVehiclePickerScreen(this, this.originNodeId, true))));

        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.railway_spawn.pick_any"),
                centerX - BUTTON_WIDTH / 2, top + BUTTON_HEIGHT + ROW_GAP, BUTTON_WIDTH, BUTTON_HEIGHT, b ->
                        this.minecraft.setScreen(new RailwayRouteVehiclePickerScreen(this, this.originNodeId, false))));

        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.railway_spawn.pick_custom"),
                centerX - BUTTON_WIDTH / 2, top + (BUTTON_HEIGHT + ROW_GAP) * 2, BUTTON_WIDTH, BUTTON_HEIGHT, b -> {
                    OpenRailwayFormationEditorPayload payload = new OpenRailwayFormationEditorPayload(this.originNodeId);
                    ModClientNetworking.sendToServer(OpenRailwayFormationEditorPayload.ID, payload, OpenRailwayFormationEditorPayload::write);
                    this.minecraft.setScreen(null);
                }));
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        mineturismo$renderScreen(new GuiGraphics(poseStack), mouseX, mouseY, partialTick);
    }

    private void mineturismo$renderScreen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        super.render(guiGraphics.pose(), mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - 70, 0xFFFFFFFF);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
*/
//?}
//? if <1.16 && >=1.15 {
/*
package com.examplebett.client.gui;
import com.examplebett.compat.ModWidgets;
import com.examplebett.compat.ModText;

import com.examplebett.compat.ModClientNetworking;
import com.examplebett.network.OpenRailwayFormationEditorPayload;

import com.examplebett.compat.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;
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

        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.railway_spawn.pick_existing"),
                centerX - BUTTON_WIDTH / 2, top, BUTTON_WIDTH, BUTTON_HEIGHT, b ->
                        this.minecraft.setScreen(new RailwayRouteVehiclePickerScreen(this, this.originNodeId, true))));

        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.railway_spawn.pick_any"),
                centerX - BUTTON_WIDTH / 2, top + BUTTON_HEIGHT + ROW_GAP, BUTTON_WIDTH, BUTTON_HEIGHT, b ->
                        this.minecraft.setScreen(new RailwayRouteVehiclePickerScreen(this, this.originNodeId, false))));

        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.railway_spawn.pick_custom"),
                centerX - BUTTON_WIDTH / 2, top + (BUTTON_HEIGHT + ROW_GAP) * 2, BUTTON_WIDTH, BUTTON_HEIGHT, b -> {
                    OpenRailwayFormationEditorPayload payload = new OpenRailwayFormationEditorPayload(this.originNodeId);
                    ModClientNetworking.sendToServer(OpenRailwayFormationEditorPayload.ID, payload, OpenRailwayFormationEditorPayload::write);
                    this.minecraft.setScreen(null);
                }));
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTick) {
        mineturismo$renderScreen(new GuiGraphics(null), mouseX, mouseY, partialTick);
    }

    private void mineturismo$renderScreen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        super.render(mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - 70, 0xFFFFFFFF);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
*/
//?}
//? if <1.15 {
/*
package com.examplebett.client.gui;
import com.examplebett.compat.ModWidgets;
import com.examplebett.compat.ModText;

import com.examplebett.compat.ModClientNetworking;
import com.examplebett.network.OpenRailwayFormationEditorPayload;

import com.examplebett.compat.GuiGraphics;
import com.examplebett.compat.v114.PoseStack;
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

        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.railway_spawn.pick_existing"),
                centerX - BUTTON_WIDTH / 2, top, BUTTON_WIDTH, BUTTON_HEIGHT, b ->
                        this.minecraft.setScreen(new RailwayRouteVehiclePickerScreen(this, this.originNodeId, true))));

        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.railway_spawn.pick_any"),
                centerX - BUTTON_WIDTH / 2, top + BUTTON_HEIGHT + ROW_GAP, BUTTON_WIDTH, BUTTON_HEIGHT, b ->
                        this.minecraft.setScreen(new RailwayRouteVehiclePickerScreen(this, this.originNodeId, false))));

        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.railway_spawn.pick_custom"),
                centerX - BUTTON_WIDTH / 2, top + (BUTTON_HEIGHT + ROW_GAP) * 2, BUTTON_WIDTH, BUTTON_HEIGHT, b -> {
                    OpenRailwayFormationEditorPayload payload = new OpenRailwayFormationEditorPayload(this.originNodeId);
                    ModClientNetworking.sendToServer(OpenRailwayFormationEditorPayload.ID, payload, OpenRailwayFormationEditorPayload::write);
                    this.minecraft.setScreen(null);
                }));
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTick) {
        mineturismo$renderScreen(new GuiGraphics(null), mouseX, mouseY, partialTick);
    }

    private void mineturismo$renderScreen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        super.render(mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - 70, 0xFFFFFFFF);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
*/
//?}
