//? if >=1.18 {
//?}
//? if <1.18 && >=1.17 {
/*
package com.examplebett.client.gui;
import com.examplebett.compat.ModCheckbox;
import com.examplebett.compat.ModWidgets;
import com.examplebett.compat.ModText;

import com.examplebett.compat.ModClientNetworking;
import com.examplebett.network.SetTownTrafficEnabledPayload;

import com.examplebett.compat.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class WaypointTrafficSettingsScreen extends Screen {

    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 20;
    private static final int ROW_GAP = 8;

    static boolean cachedTownTrafficEnabled = true;

    public WaypointTrafficSettingsScreen() {
        super(ModText.translatable("gui.mineturismo.waypoint_traffic.title"));
    }

    private void sendTownTrafficEnabled(boolean enabled) {
        cachedTownTrafficEnabled = enabled;
        SetTownTrafficEnabledPayload mineturismo$payload = new SetTownTrafficEnabledPayload(enabled);
        ModClientNetworking.sendToServer(SetTownTrafficEnabledPayload.ID, mineturismo$payload, SetTownTrafficEnabledPayload::write);
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int top = this.height / 2 - 70;

        this.addRenderableWidget(ModCheckbox.builder(ModText.translatable("gui.mineturismo.waypoint_traffic.enabled"), this.font)
                .pos(centerX - BUTTON_WIDTH / 2, top)
                .selected(cachedTownTrafficEnabled)
                .onValueChange((checkbox, selected) -> this.sendTownTrafficEnabled(selected))
                .build());

        int buttonsTop = top + BUTTON_HEIGHT + ROW_GAP * 2;

        this.addRenderableWidget(ModWidgets.button(ModText.translatable("gui.mineturismo.waypoint_traffic.controls_title"), centerX - BUTTON_WIDTH / 2, buttonsTop, BUTTON_WIDTH, BUTTON_HEIGHT, button -> this.minecraft.setScreen(new WaypointControlsScreen(this))));

        this.addRenderableWidget(ModWidgets.button(ModText.translatable("gui.mineturismo.waypoint_traffic.speed_limit_title"), centerX - BUTTON_WIDTH / 2, buttonsTop + (BUTTON_HEIGHT + ROW_GAP), BUTTON_WIDTH, BUTTON_HEIGHT, button -> this.minecraft.setScreen(new WaypointSpeedLimitScreen(this))));

        this.addRenderableWidget(ModWidgets.button(ModText.translatable("gui.mineturismo.waypoint_traffic.vehicles_title"), centerX - BUTTON_WIDTH / 2, buttonsTop + (BUTTON_HEIGHT + ROW_GAP) * 2, BUTTON_WIDTH, BUTTON_HEIGHT, button -> this.minecraft.setScreen(new WaypointVehicleListScreen(this))));

        this.addRenderableWidget(ModWidgets.button(ModText.translatable("gui.mineturismo.waypoint_traffic.plate_title"), centerX - BUTTON_WIDTH / 2, buttonsTop + (BUTTON_HEIGHT + ROW_GAP) * 3, BUTTON_WIDTH, BUTTON_HEIGHT, button -> this.minecraft.setScreen(new WaypointPlateSettingsScreen(this))));

        this.addRenderableWidget(ModWidgets.button(ModText.translatable("gui.mineturismo.waypoint_traffic.movement_mode_title"), centerX - BUTTON_WIDTH / 2, buttonsTop + (BUTTON_HEIGHT + ROW_GAP) * 4, BUTTON_WIDTH, BUTTON_HEIGHT, button -> this.minecraft.setScreen(new WaypointMovementModeScreen(this))));

        this.addRenderableWidget(ModWidgets.button(ModText.translatable("gui.mineturismo.waypoint_traffic.misc_title"), centerX - BUTTON_WIDTH / 2, buttonsTop + (BUTTON_HEIGHT + ROW_GAP) * 5, BUTTON_WIDTH, BUTTON_HEIGHT, button -> this.minecraft.setScreen(new WaypointMiscSettingsScreen(this))));

        Component closeLabel = ModText.translatable("gui.mineturismo.back");
        this.addRenderableWidget(ModWidgets.button(closeLabel, centerX - BUTTON_WIDTH / 2, buttonsTop + (BUTTON_HEIGHT + ROW_GAP) * 6 + ROW_GAP, BUTTON_WIDTH, BUTTON_HEIGHT, button -> this.onClose()));
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        mineturismo$renderScreen(new GuiGraphics(poseStack), mouseX, mouseY, partialTick);
    }

    private void mineturismo$renderScreen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        super.render(guiGraphics.pose(), mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - 110, 0xFFFFFFFF);
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
import com.examplebett.compat.ModCheckbox;
import com.examplebett.compat.ModWidgets;
import com.examplebett.compat.ModText;

import com.examplebett.compat.ModClientNetworking;
import com.examplebett.network.SetTownTrafficEnabledPayload;

import com.examplebett.compat.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class WaypointTrafficSettingsScreen extends Screen {

    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 20;
    private static final int ROW_GAP = 8;

    static boolean cachedTownTrafficEnabled = true;

    public WaypointTrafficSettingsScreen() {
        super(ModText.translatable("gui.mineturismo.waypoint_traffic.title"));
    }

    private void sendTownTrafficEnabled(boolean enabled) {
        cachedTownTrafficEnabled = enabled;
        SetTownTrafficEnabledPayload mineturismo$payload = new SetTownTrafficEnabledPayload(enabled);
        ModClientNetworking.sendToServer(SetTownTrafficEnabledPayload.ID, mineturismo$payload, SetTownTrafficEnabledPayload::write);
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int top = this.height / 2 - 70;

        this.addButton(ModCheckbox.builder(ModText.translatable("gui.mineturismo.waypoint_traffic.enabled"), this.font)
                .pos(centerX - BUTTON_WIDTH / 2, top)
                .selected(cachedTownTrafficEnabled)
                .onValueChange((checkbox, selected) -> this.sendTownTrafficEnabled(selected))
                .build());

        int buttonsTop = top + BUTTON_HEIGHT + ROW_GAP * 2;

        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.waypoint_traffic.controls_title"), centerX - BUTTON_WIDTH / 2, buttonsTop, BUTTON_WIDTH, BUTTON_HEIGHT, button -> this.minecraft.setScreen(new WaypointControlsScreen(this))));

        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.waypoint_traffic.speed_limit_title"), centerX - BUTTON_WIDTH / 2, buttonsTop + (BUTTON_HEIGHT + ROW_GAP), BUTTON_WIDTH, BUTTON_HEIGHT, button -> this.minecraft.setScreen(new WaypointSpeedLimitScreen(this))));

        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.waypoint_traffic.vehicles_title"), centerX - BUTTON_WIDTH / 2, buttonsTop + (BUTTON_HEIGHT + ROW_GAP) * 2, BUTTON_WIDTH, BUTTON_HEIGHT, button -> this.minecraft.setScreen(new WaypointVehicleListScreen(this))));

        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.waypoint_traffic.plate_title"), centerX - BUTTON_WIDTH / 2, buttonsTop + (BUTTON_HEIGHT + ROW_GAP) * 3, BUTTON_WIDTH, BUTTON_HEIGHT, button -> this.minecraft.setScreen(new WaypointPlateSettingsScreen(this))));

        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.waypoint_traffic.movement_mode_title"), centerX - BUTTON_WIDTH / 2, buttonsTop + (BUTTON_HEIGHT + ROW_GAP) * 4, BUTTON_WIDTH, BUTTON_HEIGHT, button -> this.minecraft.setScreen(new WaypointMovementModeScreen(this))));

        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.waypoint_traffic.misc_title"), centerX - BUTTON_WIDTH / 2, buttonsTop + (BUTTON_HEIGHT + ROW_GAP) * 5, BUTTON_WIDTH, BUTTON_HEIGHT, button -> this.minecraft.setScreen(new WaypointMiscSettingsScreen(this))));

        Component closeLabel = ModText.translatable("gui.mineturismo.back");
        this.addButton(ModWidgets.button(closeLabel, centerX - BUTTON_WIDTH / 2, buttonsTop + (BUTTON_HEIGHT + ROW_GAP) * 6 + ROW_GAP, BUTTON_WIDTH, BUTTON_HEIGHT, button -> this.onClose()));
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        mineturismo$renderScreen(new GuiGraphics(poseStack), mouseX, mouseY, partialTick);
    }

    private void mineturismo$renderScreen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        super.render(guiGraphics.pose(), mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - 110, 0xFFFFFFFF);
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
import com.examplebett.compat.ModCheckbox;
import com.examplebett.compat.ModWidgets;
import com.examplebett.compat.ModText;

import com.examplebett.compat.ModClientNetworking;
import com.examplebett.network.SetTownTrafficEnabledPayload;

import com.examplebett.compat.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class WaypointTrafficSettingsScreen extends Screen {

    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 20;
    private static final int ROW_GAP = 8;

    static boolean cachedTownTrafficEnabled = true;

    public WaypointTrafficSettingsScreen() {
        super(ModText.translatable("gui.mineturismo.waypoint_traffic.title"));
    }

    private void sendTownTrafficEnabled(boolean enabled) {
        cachedTownTrafficEnabled = enabled;
        SetTownTrafficEnabledPayload mineturismo$payload = new SetTownTrafficEnabledPayload(enabled);
        ModClientNetworking.sendToServer(SetTownTrafficEnabledPayload.ID, mineturismo$payload, SetTownTrafficEnabledPayload::write);
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int top = this.height / 2 - 70;

        this.addButton(ModCheckbox.builder(ModText.translatable("gui.mineturismo.waypoint_traffic.enabled"), this.font)
                .pos(centerX - BUTTON_WIDTH / 2, top)
                .selected(cachedTownTrafficEnabled)
                .onValueChange((checkbox, selected) -> this.sendTownTrafficEnabled(selected))
                .build());

        int buttonsTop = top + BUTTON_HEIGHT + ROW_GAP * 2;

        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.waypoint_traffic.controls_title"), centerX - BUTTON_WIDTH / 2, buttonsTop, BUTTON_WIDTH, BUTTON_HEIGHT, button -> this.minecraft.setScreen(new WaypointControlsScreen(this))));

        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.waypoint_traffic.speed_limit_title"), centerX - BUTTON_WIDTH / 2, buttonsTop + (BUTTON_HEIGHT + ROW_GAP), BUTTON_WIDTH, BUTTON_HEIGHT, button -> this.minecraft.setScreen(new WaypointSpeedLimitScreen(this))));

        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.waypoint_traffic.vehicles_title"), centerX - BUTTON_WIDTH / 2, buttonsTop + (BUTTON_HEIGHT + ROW_GAP) * 2, BUTTON_WIDTH, BUTTON_HEIGHT, button -> this.minecraft.setScreen(new WaypointVehicleListScreen(this))));

        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.waypoint_traffic.plate_title"), centerX - BUTTON_WIDTH / 2, buttonsTop + (BUTTON_HEIGHT + ROW_GAP) * 3, BUTTON_WIDTH, BUTTON_HEIGHT, button -> this.minecraft.setScreen(new WaypointPlateSettingsScreen(this))));

        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.waypoint_traffic.movement_mode_title"), centerX - BUTTON_WIDTH / 2, buttonsTop + (BUTTON_HEIGHT + ROW_GAP) * 4, BUTTON_WIDTH, BUTTON_HEIGHT, button -> this.minecraft.setScreen(new WaypointMovementModeScreen(this))));

        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.waypoint_traffic.misc_title"), centerX - BUTTON_WIDTH / 2, buttonsTop + (BUTTON_HEIGHT + ROW_GAP) * 5, BUTTON_WIDTH, BUTTON_HEIGHT, button -> this.minecraft.setScreen(new WaypointMiscSettingsScreen(this))));

        Component closeLabel = ModText.translatable("gui.mineturismo.back");
        this.addButton(ModWidgets.button(closeLabel, centerX - BUTTON_WIDTH / 2, buttonsTop + (BUTTON_HEIGHT + ROW_GAP) * 6 + ROW_GAP, BUTTON_WIDTH, BUTTON_HEIGHT, button -> this.onClose()));
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTick) {
        mineturismo$renderScreen(new GuiGraphics(null), mouseX, mouseY, partialTick);
    }

    private void mineturismo$renderScreen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        super.render(mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - 110, 0xFFFFFFFF);
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
import com.examplebett.compat.ModCheckbox;
import com.examplebett.compat.ModWidgets;
import com.examplebett.compat.ModText;

import com.examplebett.compat.ModClientNetworking;
import com.examplebett.network.SetTownTrafficEnabledPayload;

import com.examplebett.compat.GuiGraphics;
import com.examplebett.compat.v114.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class WaypointTrafficSettingsScreen extends Screen {

    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 20;
    private static final int ROW_GAP = 8;

    static boolean cachedTownTrafficEnabled = true;

    public WaypointTrafficSettingsScreen() {
        super(ModText.translatable("gui.mineturismo.waypoint_traffic.title"));
    }

    private void sendTownTrafficEnabled(boolean enabled) {
        cachedTownTrafficEnabled = enabled;
        SetTownTrafficEnabledPayload mineturismo$payload = new SetTownTrafficEnabledPayload(enabled);
        ModClientNetworking.sendToServer(SetTownTrafficEnabledPayload.ID, mineturismo$payload, SetTownTrafficEnabledPayload::write);
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int top = this.height / 2 - 70;

        this.addButton(ModCheckbox.builder(ModText.translatable("gui.mineturismo.waypoint_traffic.enabled"), this.font)
                .pos(centerX - BUTTON_WIDTH / 2, top)
                .selected(cachedTownTrafficEnabled)
                .onValueChange((checkbox, selected) -> this.sendTownTrafficEnabled(selected))
                .build());

        int buttonsTop = top + BUTTON_HEIGHT + ROW_GAP * 2;

        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.waypoint_traffic.controls_title"), centerX - BUTTON_WIDTH / 2, buttonsTop, BUTTON_WIDTH, BUTTON_HEIGHT, button -> this.minecraft.setScreen(new WaypointControlsScreen(this))));

        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.waypoint_traffic.speed_limit_title"), centerX - BUTTON_WIDTH / 2, buttonsTop + (BUTTON_HEIGHT + ROW_GAP), BUTTON_WIDTH, BUTTON_HEIGHT, button -> this.minecraft.setScreen(new WaypointSpeedLimitScreen(this))));

        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.waypoint_traffic.vehicles_title"), centerX - BUTTON_WIDTH / 2, buttonsTop + (BUTTON_HEIGHT + ROW_GAP) * 2, BUTTON_WIDTH, BUTTON_HEIGHT, button -> this.minecraft.setScreen(new WaypointVehicleListScreen(this))));

        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.waypoint_traffic.plate_title"), centerX - BUTTON_WIDTH / 2, buttonsTop + (BUTTON_HEIGHT + ROW_GAP) * 3, BUTTON_WIDTH, BUTTON_HEIGHT, button -> this.minecraft.setScreen(new WaypointPlateSettingsScreen(this))));

        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.waypoint_traffic.movement_mode_title"), centerX - BUTTON_WIDTH / 2, buttonsTop + (BUTTON_HEIGHT + ROW_GAP) * 4, BUTTON_WIDTH, BUTTON_HEIGHT, button -> this.minecraft.setScreen(new WaypointMovementModeScreen(this))));

        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.waypoint_traffic.misc_title"), centerX - BUTTON_WIDTH / 2, buttonsTop + (BUTTON_HEIGHT + ROW_GAP) * 5, BUTTON_WIDTH, BUTTON_HEIGHT, button -> this.minecraft.setScreen(new WaypointMiscSettingsScreen(this))));

        Component closeLabel = ModText.translatable("gui.mineturismo.back");
        this.addButton(ModWidgets.button(closeLabel, centerX - BUTTON_WIDTH / 2, buttonsTop + (BUTTON_HEIGHT + ROW_GAP) * 6 + ROW_GAP, BUTTON_WIDTH, BUTTON_HEIGHT, button -> this.onClose()));
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTick) {
        mineturismo$renderScreen(new GuiGraphics(null), mouseX, mouseY, partialTick);
    }

    private void mineturismo$renderScreen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        super.render(mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - 110, 0xFFFFFFFF);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
*/
//?}
