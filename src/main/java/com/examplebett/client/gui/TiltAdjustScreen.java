//? if >=1.18 {
//?}
//? if <1.18 && >=1.17 {
/*
package com.examplebett.client.gui;
import com.examplebett.compat.ModWidgets;
import com.examplebett.compat.ModText;

import com.examplebett.compat.ModClientNetworking;
import com.examplebett.AiObjectMod;
import com.examplebett.client.MenuCameraOrbit;
import com.examplebett.entity.CarEntity;
import com.examplebett.network.SetCarTiltPayload;

import com.examplebett.compat.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class TiltAdjustScreen extends Screen implements CarCustomizationScreen, CarCameraOrbitScreen {

    private static final float ANGLE_STEP_DEGREES = 15f;
    private static final float HEIGHT_STEP_CM = 10f;
    private static final int RIGHT_MARGIN = 20;
    private static final int CLUSTER_HALF_WIDTH = 70;
    private static final int BUTTON_HORIZONTAL_PADDING = 16;
    private static final int MIN_BUTTON_WIDTH = 60;
    private static final int AXIS_LABEL_MAX_WIDTH = 56;

    private final CarEntity car;
    private final Screen parent;
    private int clusterCenterX;
    private int contentTop;

    public TiltAdjustScreen(CarEntity car, Screen parent) {
        super(ModText.translatable("gui.mineturismo.tilt_adjust.title"));
        this.car = car;
        this.parent = parent;
    }

    private int fitWidth(Component label) {
        return Math.max(MIN_BUTTON_WIDTH, this.font.width(label) + BUTTON_HORIZONTAL_PADDING);
    }

    private void applyTiltAdjust(float rollDegrees, float pitchDegrees, float heightCm) {
        this.car.setTiltAdjust(rollDegrees, pitchDegrees, heightCm);
        SetCarTiltPayload mineturismo$payload = new SetCarTiltPayload(this.car.getId(), rollDegrees, pitchDegrees, heightCm);
        ModClientNetworking.sendToServer(SetCarTiltPayload.ID, mineturismo$payload, SetCarTiltPayload::write);
        AiObjectMod.LOGGER.debug(
                "tilt adjust [{}] rollDegrees={} pitchDegrees={} heightCm={}",
                this.car.getModelId(), rollDegrees, pitchDegrees, heightCm);
        this.minecraft.setScreen(new TiltAdjustScreen(this.car, this.parent));
    }

    @Override
    protected void init() {
        this.clusterCenterX = this.width - RIGHT_MARGIN - CLUSTER_HALF_WIDTH;
        int top = Math.max(10, this.height / 2 - 60);
        this.contentTop = top;

        float rollDegrees = this.car.getTiltRollDegrees();
        float pitchDegrees = this.car.getTiltPitchDegrees();
        float heightCm = this.car.getTiltHeightCm();

        int rowHeight = 20;
        int rowY = top + 20;

        addAxisRow(rowY, "Roll (deg)", rollDegrees, ANGLE_STEP_DEGREES,
                delta -> applyTiltAdjust(rollDegrees + delta, pitchDegrees, heightCm));
        addAxisRow(rowY + rowHeight, "Pitch (deg)", pitchDegrees, ANGLE_STEP_DEGREES,
                delta -> applyTiltAdjust(rollDegrees, pitchDegrees + delta, heightCm));
        addAxisRow(rowY + rowHeight * 2, "Height (cm)", heightCm, HEIGHT_STEP_CM,
                delta -> applyTiltAdjust(rollDegrees, pitchDegrees, heightCm + delta));

        int rightEdge = this.width - RIGHT_MARGIN;
        int backY = rowY + rowHeight * 3 + 8;
        Component backLabel = ModText.translatable("gui.mineturismo.back");
        int backWidth = fitWidth(backLabel);
        this.addRenderableWidget(ModWidgets.button(backLabel, rightEdge - backWidth, backY, backWidth, 20, button ->
                this.minecraft.setScreen(this.parent)));
    }

    private interface AxisAdjuster {
        void adjust(float delta);
    }

    private static final float FINE_STEP_DIVISOR = 5f;

    private void addAxisRow(int y, String axisLabel, double currentValue, float step, AxisAdjuster adjuster) {
        int centerX = this.clusterCenterX;

        float fineStep = Math.abs(step / FINE_STEP_DIVISOR - 0.01f) < 1e-6f ? 0.002f : step / FINE_STEP_DIVISOR;
        this.addRenderableWidget(ModWidgets.button(ModText.literal("-"), centerX - 70, y, 20, 20, button -> adjuster.adjust(-step)));
        this.addRenderableWidget(ModWidgets.button(ModText.literal("-"), centerX - 48, y, 20, 20, button -> adjuster.adjust(-fineStep)));
        this.addRenderableWidget(ModWidgets.button(ModText.literal("+"), centerX + 28, y, 20, 20, button -> adjuster.adjust(fineStep)));
        this.addRenderableWidget(ModWidgets.button(ModText.literal("+"), centerX + 50, y, 20, 20, button -> adjuster.adjust(step)));
        this.axisLabels.add(new AxisLabel(axisLabel, currentValue, y));
    }

    private final List<AxisLabel> axisLabels = new ArrayList<>();

    private record AxisLabel(String axis, double value, int y) {
    }

    private boolean cameraDragging = false;

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (button == 0) {
            this.cameraDragging = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.cameraDragging && button == 0) {
            MenuCameraOrbit.drag(dragX, dragY);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            this.cameraDragging = false;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        mineturismo$renderScreen(new GuiGraphics(poseStack), mouseX, mouseY, partialTick);
    }

    private void mineturismo$renderScreen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        super.render(guiGraphics.pose(), mouseX, mouseY, partialTick);
        this.renderAiObjectNotice(guiGraphics, this.font, this.width);
        guiGraphics.drawCenteredString(this.font, this.title, this.clusterCenterX, this.contentTop - 20, 0xFFFFFFFF);
        for (AxisLabel label : this.axisLabels) {
            String text = String.format("%s: %s", label.axis(), AxisValueFormat.format(label.value()));
            drawFittedAxisLabel(guiGraphics, text, this.clusterCenterX, label.y() + 6);
        }
    }

    private void drawFittedAxisLabel(GuiGraphics guiGraphics, String text, int centerX, int topY) {
        int textWidth = this.font.width(text);
        if (textWidth <= AXIS_LABEL_MAX_WIDTH) {
            guiGraphics.drawCenteredString(this.font, text, centerX, topY, 0xFFFFFFFF);
            return;
        }
        float scale = (float) AXIS_LABEL_MAX_WIDTH / textWidth;
        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(scale, scale, 1.0f);
        int drawX = Math.round(centerX / scale - textWidth / 2f);
        int drawY = Math.round(topY / scale);
        guiGraphics.drawString(this.font, text, drawX, drawY, 0xFFFFFFFF);
        guiGraphics.pose().popPose();
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
import com.examplebett.AiObjectMod;
import com.examplebett.client.MenuCameraOrbit;
import com.examplebett.entity.CarEntity;
import com.examplebett.network.SetCarTiltPayload;

import com.examplebett.compat.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class TiltAdjustScreen extends Screen implements CarCustomizationScreen, CarCameraOrbitScreen {

    private static final float ANGLE_STEP_DEGREES = 15f;
    private static final float HEIGHT_STEP_CM = 10f;
    private static final int RIGHT_MARGIN = 20;
    private static final int CLUSTER_HALF_WIDTH = 70;
    private static final int BUTTON_HORIZONTAL_PADDING = 16;
    private static final int MIN_BUTTON_WIDTH = 60;
    private static final int AXIS_LABEL_MAX_WIDTH = 56;

    private final CarEntity car;
    private final Screen parent;
    private int clusterCenterX;
    private int contentTop;

    public TiltAdjustScreen(CarEntity car, Screen parent) {
        super(ModText.translatable("gui.mineturismo.tilt_adjust.title"));
        this.car = car;
        this.parent = parent;
    }

    private int fitWidth(Component label) {
        return Math.max(MIN_BUTTON_WIDTH, this.font.width(label) + BUTTON_HORIZONTAL_PADDING);
    }

    private void applyTiltAdjust(float rollDegrees, float pitchDegrees, float heightCm) {
        this.car.setTiltAdjust(rollDegrees, pitchDegrees, heightCm);
        SetCarTiltPayload mineturismo$payload = new SetCarTiltPayload(this.car.getId(), rollDegrees, pitchDegrees, heightCm);
        ModClientNetworking.sendToServer(SetCarTiltPayload.ID, mineturismo$payload, SetCarTiltPayload::write);
        AiObjectMod.LOGGER.debug(
                "tilt adjust [{}] rollDegrees={} pitchDegrees={} heightCm={}",
                this.car.getModelId(), rollDegrees, pitchDegrees, heightCm);
        this.minecraft.setScreen(new TiltAdjustScreen(this.car, this.parent));
    }

    @Override
    protected void init() {
        this.clusterCenterX = this.width - RIGHT_MARGIN - CLUSTER_HALF_WIDTH;
        int top = Math.max(10, this.height / 2 - 60);
        this.contentTop = top;

        float rollDegrees = this.car.getTiltRollDegrees();
        float pitchDegrees = this.car.getTiltPitchDegrees();
        float heightCm = this.car.getTiltHeightCm();

        int rowHeight = 20;
        int rowY = top + 20;

        addAxisRow(rowY, "Roll (deg)", rollDegrees, ANGLE_STEP_DEGREES,
                delta -> applyTiltAdjust(rollDegrees + delta, pitchDegrees, heightCm));
        addAxisRow(rowY + rowHeight, "Pitch (deg)", pitchDegrees, ANGLE_STEP_DEGREES,
                delta -> applyTiltAdjust(rollDegrees, pitchDegrees + delta, heightCm));
        addAxisRow(rowY + rowHeight * 2, "Height (cm)", heightCm, HEIGHT_STEP_CM,
                delta -> applyTiltAdjust(rollDegrees, pitchDegrees, heightCm + delta));

        int rightEdge = this.width - RIGHT_MARGIN;
        int backY = rowY + rowHeight * 3 + 8;
        Component backLabel = ModText.translatable("gui.mineturismo.back");
        int backWidth = fitWidth(backLabel);
        this.addButton(ModWidgets.button(backLabel, rightEdge - backWidth, backY, backWidth, 20, button ->
                this.minecraft.setScreen(this.parent)));
    }

    private interface AxisAdjuster {
        void adjust(float delta);
    }

    private static final float FINE_STEP_DIVISOR = 5f;

    private void addAxisRow(int y, String axisLabel, double currentValue, float step, AxisAdjuster adjuster) {
        int centerX = this.clusterCenterX;

        float fineStep = Math.abs(step / FINE_STEP_DIVISOR - 0.01f) < 1e-6f ? 0.002f : step / FINE_STEP_DIVISOR;
        this.addButton(ModWidgets.button(ModText.literal("-"), centerX - 70, y, 20, 20, button -> adjuster.adjust(-step)));
        this.addButton(ModWidgets.button(ModText.literal("-"), centerX - 48, y, 20, 20, button -> adjuster.adjust(-fineStep)));
        this.addButton(ModWidgets.button(ModText.literal("+"), centerX + 28, y, 20, 20, button -> adjuster.adjust(fineStep)));
        this.addButton(ModWidgets.button(ModText.literal("+"), centerX + 50, y, 20, 20, button -> adjuster.adjust(step)));
        this.axisLabels.add(new AxisLabel(axisLabel, currentValue, y));
    }

    private final List<AxisLabel> axisLabels = new ArrayList<>();

    private record AxisLabel(String axis, double value, int y) {
    }

    private boolean cameraDragging = false;

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (button == 0) {
            this.cameraDragging = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.cameraDragging && button == 0) {
            MenuCameraOrbit.drag(dragX, dragY);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            this.cameraDragging = false;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        mineturismo$renderScreen(new GuiGraphics(poseStack), mouseX, mouseY, partialTick);
    }

    private void mineturismo$renderScreen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        super.render(guiGraphics.pose(), mouseX, mouseY, partialTick);
        this.renderAiObjectNotice(guiGraphics, this.font, this.width);
        guiGraphics.drawCenteredString(this.font, this.title, this.clusterCenterX, this.contentTop - 20, 0xFFFFFFFF);
        for (AxisLabel label : this.axisLabels) {
            String text = String.format("%s: %s", label.axis(), AxisValueFormat.format(label.value()));
            drawFittedAxisLabel(guiGraphics, text, this.clusterCenterX, label.y() + 6);
        }
    }

    private void drawFittedAxisLabel(GuiGraphics guiGraphics, String text, int centerX, int topY) {
        int textWidth = this.font.width(text);
        if (textWidth <= AXIS_LABEL_MAX_WIDTH) {
            guiGraphics.drawCenteredString(this.font, text, centerX, topY, 0xFFFFFFFF);
            return;
        }
        float scale = (float) AXIS_LABEL_MAX_WIDTH / textWidth;
        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(scale, scale, 1.0f);
        int drawX = Math.round(centerX / scale - textWidth / 2f);
        int drawY = Math.round(topY / scale);
        guiGraphics.drawString(this.font, text, drawX, drawY, 0xFFFFFFFF);
        guiGraphics.pose().popPose();
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
import com.examplebett.AiObjectMod;
import com.examplebett.client.MenuCameraOrbit;
import com.examplebett.entity.CarEntity;
import com.examplebett.network.SetCarTiltPayload;

import com.examplebett.compat.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class TiltAdjustScreen extends Screen implements CarCustomizationScreen, CarCameraOrbitScreen {

    private static final float ANGLE_STEP_DEGREES = 15f;
    private static final float HEIGHT_STEP_CM = 10f;
    private static final int RIGHT_MARGIN = 20;
    private static final int CLUSTER_HALF_WIDTH = 70;
    private static final int BUTTON_HORIZONTAL_PADDING = 16;
    private static final int MIN_BUTTON_WIDTH = 60;
    private static final int AXIS_LABEL_MAX_WIDTH = 56;

    private final CarEntity car;
    private final Screen parent;
    private int clusterCenterX;
    private int contentTop;

    public TiltAdjustScreen(CarEntity car, Screen parent) {
        super(ModText.translatable("gui.mineturismo.tilt_adjust.title"));
        this.car = car;
        this.parent = parent;
    }

    private int fitWidth(Component label) {
        return Math.max(MIN_BUTTON_WIDTH, com.examplebett.compat.LegacyGui.width(this.font, label) + BUTTON_HORIZONTAL_PADDING);
    }

    private void applyTiltAdjust(float rollDegrees, float pitchDegrees, float heightCm) {
        this.car.setTiltAdjust(rollDegrees, pitchDegrees, heightCm);
        SetCarTiltPayload mineturismo$payload = new SetCarTiltPayload(this.car.getId(), rollDegrees, pitchDegrees, heightCm);
        ModClientNetworking.sendToServer(SetCarTiltPayload.ID, mineturismo$payload, SetCarTiltPayload::write);
        AiObjectMod.LOGGER.debug(
                "tilt adjust [{}] rollDegrees={} pitchDegrees={} heightCm={}",
                this.car.getModelId(), rollDegrees, pitchDegrees, heightCm);
        this.minecraft.setScreen(new TiltAdjustScreen(this.car, this.parent));
    }

    @Override
    protected void init() {
        this.clusterCenterX = this.width - RIGHT_MARGIN - CLUSTER_HALF_WIDTH;
        int top = Math.max(10, this.height / 2 - 60);
        this.contentTop = top;

        float rollDegrees = this.car.getTiltRollDegrees();
        float pitchDegrees = this.car.getTiltPitchDegrees();
        float heightCm = this.car.getTiltHeightCm();

        int rowHeight = 20;
        int rowY = top + 20;

        addAxisRow(rowY, "Roll (deg)", rollDegrees, ANGLE_STEP_DEGREES,
                delta -> applyTiltAdjust(rollDegrees + delta, pitchDegrees, heightCm));
        addAxisRow(rowY + rowHeight, "Pitch (deg)", pitchDegrees, ANGLE_STEP_DEGREES,
                delta -> applyTiltAdjust(rollDegrees, pitchDegrees + delta, heightCm));
        addAxisRow(rowY + rowHeight * 2, "Height (cm)", heightCm, HEIGHT_STEP_CM,
                delta -> applyTiltAdjust(rollDegrees, pitchDegrees, heightCm + delta));

        int rightEdge = this.width - RIGHT_MARGIN;
        int backY = rowY + rowHeight * 3 + 8;
        Component backLabel = ModText.translatable("gui.mineturismo.back");
        int backWidth = fitWidth(backLabel);
        this.addButton(ModWidgets.button(backLabel, rightEdge - backWidth, backY, backWidth, 20, button ->
                this.minecraft.setScreen(this.parent)));
    }

    private interface AxisAdjuster {
        void adjust(float delta);
    }

    private static final float FINE_STEP_DIVISOR = 5f;

    private void addAxisRow(int y, String axisLabel, double currentValue, float step, AxisAdjuster adjuster) {
        int centerX = this.clusterCenterX;

        float fineStep = Math.abs(step / FINE_STEP_DIVISOR - 0.01f) < 1e-6f ? 0.002f : step / FINE_STEP_DIVISOR;
        this.addButton(ModWidgets.button(ModText.literal("-"), centerX - 70, y, 20, 20, button -> adjuster.adjust(-step)));
        this.addButton(ModWidgets.button(ModText.literal("-"), centerX - 48, y, 20, 20, button -> adjuster.adjust(-fineStep)));
        this.addButton(ModWidgets.button(ModText.literal("+"), centerX + 28, y, 20, 20, button -> adjuster.adjust(fineStep)));
        this.addButton(ModWidgets.button(ModText.literal("+"), centerX + 50, y, 20, 20, button -> adjuster.adjust(step)));
        this.axisLabels.add(new AxisLabel(axisLabel, currentValue, y));
    }

    private final List<AxisLabel> axisLabels = new ArrayList<>();

    private record AxisLabel(String axis, double value, int y) {
    }

    private boolean cameraDragging = false;

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (button == 0) {
            this.cameraDragging = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.cameraDragging && button == 0) {
            MenuCameraOrbit.drag(dragX, dragY);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            this.cameraDragging = false;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTick) {
        mineturismo$renderScreen(new GuiGraphics(null), mouseX, mouseY, partialTick);
    }

    private void mineturismo$renderScreen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        super.render(mouseX, mouseY, partialTick);
        this.renderAiObjectNotice(guiGraphics, this.font, this.width);
        guiGraphics.drawCenteredString(this.font, this.title, this.clusterCenterX, this.contentTop - 20, 0xFFFFFFFF);
        for (AxisLabel label : this.axisLabels) {
            String text = String.format("%s: %s", label.axis(), AxisValueFormat.format(label.value()));
            drawFittedAxisLabel(guiGraphics, text, this.clusterCenterX, label.y() + 6);
        }
    }

    private void drawFittedAxisLabel(GuiGraphics guiGraphics, String text, int centerX, int topY) {
        int textWidth = com.examplebett.compat.LegacyGui.width(this.font, text);
        if (textWidth <= AXIS_LABEL_MAX_WIDTH) {
            guiGraphics.drawCenteredString(this.font, text, centerX, topY, 0xFFFFFFFF);
            return;
        }
        float scale = (float) AXIS_LABEL_MAX_WIDTH / textWidth;
        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(scale, scale, 1.0f);
        int drawX = Math.round(centerX / scale - textWidth / 2f);
        int drawY = Math.round(topY / scale);
        guiGraphics.drawString(this.font, text, drawX, drawY, 0xFFFFFFFF);
        guiGraphics.pose().popPose();
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
import com.examplebett.AiObjectMod;
import com.examplebett.client.MenuCameraOrbit;
import com.examplebett.entity.CarEntity;
import com.examplebett.network.SetCarTiltPayload;

import com.examplebett.compat.GuiGraphics;
import com.examplebett.compat.v114.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class TiltAdjustScreen extends Screen implements CarCustomizationScreen, CarCameraOrbitScreen {

    private static final float ANGLE_STEP_DEGREES = 15f;
    private static final float HEIGHT_STEP_CM = 10f;
    private static final int RIGHT_MARGIN = 20;
    private static final int CLUSTER_HALF_WIDTH = 70;
    private static final int BUTTON_HORIZONTAL_PADDING = 16;
    private static final int MIN_BUTTON_WIDTH = 60;
    private static final int AXIS_LABEL_MAX_WIDTH = 56;

    private final CarEntity car;
    private final Screen parent;
    private int clusterCenterX;
    private int contentTop;

    public TiltAdjustScreen(CarEntity car, Screen parent) {
        super(ModText.translatable("gui.mineturismo.tilt_adjust.title"));
        this.car = car;
        this.parent = parent;
    }

    private int fitWidth(Component label) {
        return Math.max(MIN_BUTTON_WIDTH, com.examplebett.compat.LegacyGui.width(this.font, label) + BUTTON_HORIZONTAL_PADDING);
    }

    private void applyTiltAdjust(float rollDegrees, float pitchDegrees, float heightCm) {
        this.car.setTiltAdjust(rollDegrees, pitchDegrees, heightCm);
        SetCarTiltPayload mineturismo$payload = new SetCarTiltPayload(this.car.getId(), rollDegrees, pitchDegrees, heightCm);
        ModClientNetworking.sendToServer(SetCarTiltPayload.ID, mineturismo$payload, SetCarTiltPayload::write);
        AiObjectMod.LOGGER.debug(
                "tilt adjust [{}] rollDegrees={} pitchDegrees={} heightCm={}",
                this.car.getModelId(), rollDegrees, pitchDegrees, heightCm);
        this.minecraft.setScreen(new TiltAdjustScreen(this.car, this.parent));
    }

    @Override
    protected void init() {
        this.clusterCenterX = this.width - RIGHT_MARGIN - CLUSTER_HALF_WIDTH;
        int top = Math.max(10, this.height / 2 - 60);
        this.contentTop = top;

        float rollDegrees = this.car.getTiltRollDegrees();
        float pitchDegrees = this.car.getTiltPitchDegrees();
        float heightCm = this.car.getTiltHeightCm();

        int rowHeight = 20;
        int rowY = top + 20;

        addAxisRow(rowY, "Roll (deg)", rollDegrees, ANGLE_STEP_DEGREES,
                delta -> applyTiltAdjust(rollDegrees + delta, pitchDegrees, heightCm));
        addAxisRow(rowY + rowHeight, "Pitch (deg)", pitchDegrees, ANGLE_STEP_DEGREES,
                delta -> applyTiltAdjust(rollDegrees, pitchDegrees + delta, heightCm));
        addAxisRow(rowY + rowHeight * 2, "Height (cm)", heightCm, HEIGHT_STEP_CM,
                delta -> applyTiltAdjust(rollDegrees, pitchDegrees, heightCm + delta));

        int rightEdge = this.width - RIGHT_MARGIN;
        int backY = rowY + rowHeight * 3 + 8;
        Component backLabel = ModText.translatable("gui.mineturismo.back");
        int backWidth = fitWidth(backLabel);
        this.addButton(ModWidgets.button(backLabel, rightEdge - backWidth, backY, backWidth, 20, button ->
                this.minecraft.setScreen(this.parent)));
    }

    private interface AxisAdjuster {
        void adjust(float delta);
    }

    private static final float FINE_STEP_DIVISOR = 5f;

    private void addAxisRow(int y, String axisLabel, double currentValue, float step, AxisAdjuster adjuster) {
        int centerX = this.clusterCenterX;

        float fineStep = Math.abs(step / FINE_STEP_DIVISOR - 0.01f) < 1e-6f ? 0.002f : step / FINE_STEP_DIVISOR;
        this.addButton(ModWidgets.button(ModText.literal("-"), centerX - 70, y, 20, 20, button -> adjuster.adjust(-step)));
        this.addButton(ModWidgets.button(ModText.literal("-"), centerX - 48, y, 20, 20, button -> adjuster.adjust(-fineStep)));
        this.addButton(ModWidgets.button(ModText.literal("+"), centerX + 28, y, 20, 20, button -> adjuster.adjust(fineStep)));
        this.addButton(ModWidgets.button(ModText.literal("+"), centerX + 50, y, 20, 20, button -> adjuster.adjust(step)));
        this.axisLabels.add(new AxisLabel(axisLabel, currentValue, y));
    }

    private final List<AxisLabel> axisLabels = new ArrayList<>();

    private record AxisLabel(String axis, double value, int y) {
    }

    private boolean cameraDragging = false;

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (button == 0) {
            this.cameraDragging = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.cameraDragging && button == 0) {
            MenuCameraOrbit.drag(dragX, dragY);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            this.cameraDragging = false;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTick) {
        mineturismo$renderScreen(new GuiGraphics(null), mouseX, mouseY, partialTick);
    }

    private void mineturismo$renderScreen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        super.render(mouseX, mouseY, partialTick);
        this.renderAiObjectNotice(guiGraphics, this.font, this.width);
        guiGraphics.drawCenteredString(this.font, this.title, this.clusterCenterX, this.contentTop - 20, 0xFFFFFFFF);
        for (AxisLabel label : this.axisLabels) {
            String text = String.format("%s: %s", label.axis(), AxisValueFormat.format(label.value()));
            drawFittedAxisLabel(guiGraphics, text, this.clusterCenterX, label.y() + 6);
        }
    }

    private void drawFittedAxisLabel(GuiGraphics guiGraphics, String text, int centerX, int topY) {
        int textWidth = com.examplebett.compat.LegacyGui.width(this.font, text);
        if (textWidth <= AXIS_LABEL_MAX_WIDTH) {
            guiGraphics.drawCenteredString(this.font, text, centerX, topY, 0xFFFFFFFF);
            return;
        }
        float scale = (float) AXIS_LABEL_MAX_WIDTH / textWidth;
        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(scale, scale, 1.0f);
        int drawX = Math.round(centerX / scale - textWidth / 2f);
        int drawY = Math.round(topY / scale);
        guiGraphics.drawString(this.font, text, drawX, drawY, 0xFFFFFFFF);
        guiGraphics.pose().popPose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
*/
//?}
