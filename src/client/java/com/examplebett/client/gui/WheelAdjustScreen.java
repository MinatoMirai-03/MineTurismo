package com.examplebett.client.gui;
import com.examplebett.compat.ModWidgets;
import com.examplebett.compat.ModText;

import com.examplebett.compat.ModClientNetworking;
import com.examplebett.AiObjectMod;
import com.examplebett.client.MenuCameraOrbit;
import com.examplebett.entity.CarEntity;
import com.examplebett.network.SetCarWheelAdjustPayload;
import com.examplebett.network.SetCarWheelColorPayload;
import com.examplebett.network.SetCarWheelWidthPayload;
import com.examplebett.render.WheelAttachment;
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
//? if >=1.21.9 {
import net.minecraft.client.input.MouseButtonEvent;
//?}
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class WheelAdjustScreen extends Screen implements CarCustomizationScreen, CarCameraOrbitScreen {

    private static final float POSITION_STEP = 0.05f;
    private static final float CAMBER_STEP = 0.5f;
    private static final float SCALE_STEP = 0.05f;

    private static final int RIGHT_MARGIN = 20;
    private static final int CLUSTER_HALF_WIDTH = 70;

    private static final int LEFT_MARGIN = 20;

    private static final int AXIS_LABEL_MAX_WIDTH = 56;

    private static final int BUTTON_HORIZONTAL_PADDING = 16;
    private static final int MIN_BUTTON_WIDTH = 60;

    private final CarEntity car;

    private final Screen parent;
    private int clusterCenterX;
    private int contentTop;

    public WheelAdjustScreen(CarEntity car) {
        this(car, null);
    }

    public WheelAdjustScreen(CarEntity car, Screen parent) {
        super(ModText.translatable("gui.mineturismo.wheel_adjust.title"));
        this.car = car;
        this.parent = parent;
    }

    private int fitWidth(Component label) {
        return Math.max(MIN_BUTTON_WIDTH, this.font.width(label) + BUTTON_HORIZONTAL_PADDING);
    }

    private void applyAdjust(float frontZ, float rearZ, float trackHalfWidth, float axleHeight,
                              float bodyHeight, float camberDegrees, float wheelScale, String wheelModelIdOverride) {
        this.car.setWheelAdjust(frontZ, rearZ, trackHalfWidth, axleHeight, bodyHeight,
                camberDegrees, wheelScale, wheelModelIdOverride);
        SetCarWheelAdjustPayload mineturismo$payload = new SetCarWheelAdjustPayload(
                this.car.getId(), frontZ, rearZ, trackHalfWidth, axleHeight, bodyHeight,
                camberDegrees, wheelScale, wheelModelIdOverride);
        //? if >=1.20.5 {
        ModClientNetworking.sendToServer(mineturismo$payload);
        //?} else {
        /*ModClientNetworking.sendToServer(SetCarWheelAdjustPayload.ID, mineturismo$payload, SetCarWheelAdjustPayload::write);*/
        //?}

        AiObjectMod.LOGGER.debug(
                "wheel adjust [{}] frontZ={} rearZ={} trackHalfWidth={} axleHeight={} bodyHeight={} camberDegrees={} wheelScale={} style={}",
                this.car.getModelId(), frontZ, rearZ, trackHalfWidth, axleHeight, bodyHeight,
                camberDegrees, wheelScale, wheelModelIdOverride);
        this.minecraft.setScreen(new WheelAdjustScreen(this.car, this.parent));
    }

    static void applyWheelStyle(CarEntity car, String style) {
        car.setWheelColor("");
        SetCarWheelColorPayload mineturismo$colorPayload = new SetCarWheelColorPayload(car.getId(), "");
        //? if >=1.20.5 {
        ModClientNetworking.sendToServer(mineturismo$colorPayload);
        //?} else {
        /*ModClientNetworking.sendToServer(SetCarWheelColorPayload.ID, mineturismo$colorPayload, SetCarWheelColorPayload::write);*/
        //?}
        float frontZ = car.getWheelFrontZOffset();
        float rearZ = car.getWheelRearZOffset();
        float trackHalfWidth = car.getWheelTrackHalfWidthOffset();
        float axleHeight = car.getWheelAxleHeightOffset();
        float bodyHeight = car.getBodyHeightAdjust();
        float camberDegrees = car.getWheelCamberDegrees();
        float wheelScale = car.getWheelScaleAdjust();
        car.setWheelAdjust(frontZ, rearZ, trackHalfWidth, axleHeight, bodyHeight, camberDegrees, wheelScale, style);
        SetCarWheelAdjustPayload mineturismo$payload = new SetCarWheelAdjustPayload(
                car.getId(), frontZ, rearZ, trackHalfWidth, axleHeight, bodyHeight, camberDegrees, wheelScale, style);
        //? if >=1.20.5 {
        ModClientNetworking.sendToServer(mineturismo$payload);
        //?} else {
        /*ModClientNetworking.sendToServer(SetCarWheelAdjustPayload.ID, mineturismo$payload, SetCarWheelAdjustPayload::write);*/
        //?}
        AiObjectMod.LOGGER.debug(
                "wheel adjust [{}] frontZ={} rearZ={} trackHalfWidth={} axleHeight={} bodyHeight={} camberDegrees={} wheelScale={} style={}",
                car.getModelId(), frontZ, rearZ, trackHalfWidth, axleHeight, bodyHeight, camberDegrees, wheelScale, style);
    }

    private void applyColor(String wheelColorOverride) {
        this.car.setWheelColor(wheelColorOverride);
        SetCarWheelColorPayload mineturismo$payload = new SetCarWheelColorPayload(this.car.getId(), wheelColorOverride);
        //? if >=1.20.5 {
        ModClientNetworking.sendToServer(mineturismo$payload);
        //?} else {
        /*ModClientNetworking.sendToServer(SetCarWheelColorPayload.ID, mineturismo$payload, SetCarWheelColorPayload::write);*/
        //?}
        this.minecraft.setScreen(new WheelAdjustScreen(this.car, this.parent));
    }

    private void applyWheelWidth(String wheelStyleId, float wheelWidthScaleAdjust) {
        this.car.setWheelWidthScale(wheelWidthScaleAdjust);
        SetCarWheelWidthPayload mineturismo$payload = new SetCarWheelWidthPayload(this.car.getId(), wheelWidthScaleAdjust);
        //? if >=1.20.5 {
        ModClientNetworking.sendToServer(mineturismo$payload);
        //?} else {
        /*ModClientNetworking.sendToServer(SetCarWheelWidthPayload.ID, mineturismo$payload, SetCarWheelWidthPayload::write);*/
        //?}

        if (wheelStyleId != null && !wheelStyleId.isEmpty()
                && !com.examplebett.render.WheelAttachment.NONE_STYLE.equals(wheelStyleId)) {
            AiObjectMod.LOGGER.debug("wheel width adjust [{}] wheelWidthScale={}", wheelStyleId, wheelWidthScaleAdjust);
        }
        this.minecraft.setScreen(new WheelAdjustScreen(this.car, this.parent));
    }

    @Override
    protected void init() {
        this.clusterCenterX = this.width - RIGHT_MARGIN - CLUSTER_HALF_WIDTH;
        int top = this.height / 2 - 170;

        top = Math.max(10, top);
        this.contentTop = top;

        WheelAttachment attachment = this.car.getWheelAttachment();
        float frontZ = this.car.getWheelFrontZOffset();
        float rearZ = this.car.getWheelRearZOffset();
        float trackHalfWidth = this.car.getWheelTrackHalfWidthOffset();
        float axleHeight = this.car.getWheelAxleHeightOffset();
        float bodyHeight = this.car.getBodyHeightAdjust();
        float camberDegrees = this.car.getWheelCamberDegrees();
        float wheelScale = this.car.getWheelScaleAdjust();
        float wheelWidthScale = this.car.getWheelWidthScaleAdjust();
        String override = this.car.getWheelModelIdOverride();

        String currentStyle = (override != null && !override.isEmpty())
                ? override : (attachment != null ? attachment.wheelModelId() : null);

        int rowHeight = 20;
        int rowY = top + 20;

        if (attachment != null || this.car.hasWheelPositions()) {
            addAxisRow(rowY, "Front Z", frontZ, POSITION_STEP,
                    delta -> applyAdjust(frontZ + delta, rearZ, trackHalfWidth, axleHeight, bodyHeight, camberDegrees, wheelScale, override));
            addAxisRow(rowY + rowHeight, "Rear Z", rearZ, POSITION_STEP,
                    delta -> applyAdjust(frontZ, rearZ + delta, trackHalfWidth, axleHeight, bodyHeight, camberDegrees, wheelScale, override));
            addAxisRow(rowY + rowHeight * 2, "Track", trackHalfWidth, POSITION_STEP,
                    delta -> applyAdjust(frontZ, rearZ, trackHalfWidth + delta, axleHeight, bodyHeight, camberDegrees, wheelScale, override));
            addAxisRow(rowY + rowHeight * 3, "Axle Y", axleHeight, POSITION_STEP,
                    delta -> applyAdjust(frontZ, rearZ, trackHalfWidth, axleHeight + delta, bodyHeight, camberDegrees, wheelScale, override));

            addAxisRow(rowY + rowHeight * 4, "Camber", camberDegrees, CAMBER_STEP,
                    delta -> applyAdjust(frontZ, rearZ, trackHalfWidth, axleHeight, bodyHeight, camberDegrees + delta, wheelScale, override));
            addAxisRow(rowY + rowHeight * 5, "Wheel Size", wheelScale, SCALE_STEP,
                    delta -> applyAdjust(frontZ, rearZ, trackHalfWidth, axleHeight, bodyHeight, camberDegrees, wheelScale + delta, override));
            addAxisRow(rowY + rowHeight * 6, "Tread Width", wheelWidthScale, SCALE_STEP,
                    delta -> applyWheelWidth(currentStyle, wheelWidthScale + delta));
        }

        int bodyHeightRowY = rowY + rowHeight * 7;
        addAxisRow(bodyHeightRowY, "Ride Height", bodyHeight, POSITION_STEP,
                delta -> applyAdjust(frontZ, rearZ, trackHalfWidth, axleHeight, bodyHeight + delta, camberDegrees, wheelScale, override));

        int rightEdge = this.width - RIGHT_MARGIN;
        int nextY = bodyHeightRowY + rowHeight;

        if (attachment != null && attachment.wheelStyles().size() > 1) {
            Component label = ModText.translatable("gui.mineturismo.wheel_adjust.change_wheel");
            int buttonWidth = fitWidth(label);
            this.addRenderableWidget(ModWidgets.button(label, rightEdge - buttonWidth, nextY, buttonWidth, 20, button ->
                    this.minecraft.setScreen(new WheelStyleSelectScreen(this.car, this.parent))));
            nextY += rowHeight + 8;
        }

        if (currentStyle != null && !com.examplebett.render.WheelAttachment.NONE_STYLE.equals(currentStyle)) {
            com.examplebett.render.WheelColorOptions colorOptions = com.examplebett.client.render.WheelColorLoader
                    .load(this.minecraft.getResourceManager(), currentStyle)
                    .orElse(null);
            if (colorOptions != null && colorOptions.ids().size() > 1) {
                int colorRowHeight = 20;
                int colorSpacing = 4;
                int colorTop = top + 20;
                for (int i = 0; i < colorOptions.ids().size(); i++) {
                    String colorId = colorOptions.ids().get(i);
                    String colorLabel = colorOptions.labels().get(i);
                    int y = colorTop + i * (colorRowHeight + colorSpacing);
                    Component label = ModText.literal(colorLabel);
                    int buttonWidth = fitWidth(label);
                    this.addRenderableWidget(ModWidgets.button(label, LEFT_MARGIN, y, buttonWidth, colorRowHeight, button ->
                            applyColor(colorId)));
                }
            }
        }

        Component backLabel = ModText.translatable("gui.mineturismo.back");
        int backWidth = fitWidth(backLabel);
        this.addRenderableWidget(ModWidgets.button(backLabel, rightEdge - backWidth, nextY, backWidth, 20, button ->
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
    //? if >=1.21.9 {
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (super.mouseClicked(event, doubleClick)) {
            return true;
        }
        if (event.button() == 0) {
            this.cameraDragging = true;
            return true;
        }
        return false;
    }
    //?} else {
    /*public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (button == 0) {
            this.cameraDragging = true;
            return true;
        }
        return false;
    }*/
    //?}

    @Override
    //? if >=1.21.9 {
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        if (this.cameraDragging && event.button() == 0) {
            MenuCameraOrbit.drag(dragX, dragY);
            return true;
        }
        return super.mouseDragged(event, dragX, dragY);
    }
    //?} else {
    /*public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.cameraDragging && button == 0) {
            MenuCameraOrbit.drag(dragX, dragY);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }*/
    //?}

    @Override
    //? if >=1.21.9 {
    public boolean mouseReleased(MouseButtonEvent event) {
        if (event.button() == 0) {
            this.cameraDragging = false;
        }
        return super.mouseReleased(event);
    }
    //?} else {
    /*public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            this.cameraDragging = false;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }*/
    //?}

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
        //? if >=1.21.6 {
        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().scale(scale, scale);
        //?} else {
        /*guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(scale, scale, 1.0f);*/
        //?}
        int drawX = Math.round(centerX / scale - textWidth / 2f);
        int drawY = Math.round(topY / scale);
        guiGraphics.drawString(this.font, text, drawX, drawY, 0xFFFFFFFF);
        //? if >=1.21.6 {
        guiGraphics.pose().popMatrix();
        //?} else
        /*guiGraphics.pose().popPose();*/
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
