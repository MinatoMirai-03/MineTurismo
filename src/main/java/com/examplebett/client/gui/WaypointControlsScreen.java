//? if >=1.18 {
//?}
//? if <1.18 && >=1.17 {
/*
package com.examplebett.client.gui;
import com.examplebett.compat.ModWidgets;
import com.examplebett.compat.ModText;

import com.examplebett.compat.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;
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

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        mineturismo$renderScreen(new GuiGraphics(poseStack), mouseX, mouseY, partialTick);
    }

    private void mineturismo$renderScreen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        super.render(guiGraphics.pose(), mouseX, mouseY, partialTick);
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
*/
//?}
//? if <1.17 && >=1.16 {
/*
package com.examplebett.client.gui;
import com.examplebett.compat.ModWidgets;
import com.examplebett.compat.ModText;

import com.examplebett.compat.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;
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
        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.back"), centerX - 100, this.height - 40, 200, 20, button ->
                this.minecraft.setScreen(this.parent)));
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        mineturismo$renderScreen(new GuiGraphics(poseStack), mouseX, mouseY, partialTick);
    }

    private void mineturismo$renderScreen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        super.render(guiGraphics.pose(), mouseX, mouseY, partialTick);
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
*/
//?}
//? if <1.16 && >=1.15 {
/*
package com.examplebett.client.gui;
import com.examplebett.compat.ModWidgets;
import com.examplebett.compat.ModText;

import com.examplebett.compat.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;
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
        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.back"), centerX - 100, this.height - 40, 200, 20, button ->
                this.minecraft.setScreen(this.parent)));
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTick) {
        mineturismo$renderScreen(new GuiGraphics(null), mouseX, mouseY, partialTick);
    }

    private void mineturismo$renderScreen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        super.render(mouseX, mouseY, partialTick);
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
*/
//?}
//? if <1.15 {
/*
package com.examplebett.client.gui;
import com.examplebett.compat.ModWidgets;
import com.examplebett.compat.ModText;

import com.examplebett.compat.GuiGraphics;
import com.examplebett.compat.v114.PoseStack;
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
        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.back"), centerX - 100, this.height - 40, 200, 20, button ->
                this.minecraft.setScreen(this.parent)));
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTick) {
        mineturismo$renderScreen(new GuiGraphics(null), mouseX, mouseY, partialTick);
    }

    private void mineturismo$renderScreen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        super.render(mouseX, mouseY, partialTick);
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
*/
//?}
