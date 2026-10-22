package com.examplebett.client.gui;
import com.examplebett.compat.ModWidgets;
import com.examplebett.compat.ModText;

import com.examplebett.compat.ModClientNetworking;
import com.examplebett.AiObjectMod;
import com.examplebett.client.MenuCameraOrbit;
import com.examplebett.entity.CarEntity;
import com.examplebett.network.SetCarLimoAdjustPayload;
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

public class CarLimousineScreen extends Screen implements CarCustomizationScreen, CarCameraOrbitScreen {

    private static final float STRETCH_STEP_CM = 10f;
    private static final float ZONE_STEP_CM = 10f;
    private static final int RIGHT_MARGIN = 20;
    private static final int CLUSTER_HALF_WIDTH = 70;
    private static final int BUTTON_HORIZONTAL_PADDING = 16;
    private static final int MIN_BUTTON_WIDTH = 60;

    private static final int AXIS_LABEL_MAX_WIDTH = 56;

    private final CarEntity car;
    private final Screen parent;
    private int clusterCenterX;
    private int contentTop;

    public CarLimousineScreen(CarEntity car, Screen parent) {
        super(ModText.translatable("gui.mineturismo.limousine.title"));
        this.car = car;
        this.parent = parent;
    }

    private int fitWidth(Component label) {
        return Math.max(MIN_BUTTON_WIDTH, this.font.width(label) + BUTTON_HORIZONTAL_PADDING);
    }

    private void applyLimoAdjust(float limoStretchCm, float limoZoneCm, float limoCenterOffsetCm) {
        limoZoneCm = Math.max(1f, limoZoneCm);
        this.car.setLimoAdjust(limoStretchCm, limoZoneCm, limoCenterOffsetCm);
        SetCarLimoAdjustPayload mineturismo$payload = new SetCarLimoAdjustPayload(this.car.getId(), limoStretchCm, limoZoneCm, limoCenterOffsetCm);
        //? if >=1.20.5 {
        ModClientNetworking.sendToServer(mineturismo$payload);
        //?} else {
        /*ModClientNetworking.sendToServer(SetCarLimoAdjustPayload.ID, mineturismo$payload, SetCarLimoAdjustPayload::write);*/
        //?}

        AiObjectMod.LOGGER.debug(
                "limo adjust [{}] limoStretchCm={} limoZoneCm={} limoCenterOffsetCm={}",
                this.car.getModelId(), limoStretchCm, limoZoneCm, limoCenterOffsetCm);
        this.minecraft.setScreen(new CarLimousineScreen(this.car, this.parent));
    }

    @Override
    protected void init() {
        this.clusterCenterX = this.width - RIGHT_MARGIN - CLUSTER_HALF_WIDTH;
        int top = Math.max(10, this.height / 2 - 60);
        this.contentTop = top;

        float stretchCm = this.car.getLimoStretchAdjust();
        float zoneCm = this.car.getLimoZoneAdjust();
        float centerOffsetCm = this.car.getLimoCenterOffsetAdjust();

        int rowHeight = 20;
        int rowY = top + 20;

        addAxisRow(rowY, "Stretch Length (cm)", stretchCm, STRETCH_STEP_CM,
                delta -> applyLimoAdjust(stretchCm + delta, zoneCm, centerOffsetCm));
        addAxisRow(rowY + rowHeight, "Ramp Length (cm)", zoneCm, ZONE_STEP_CM,
                delta -> applyLimoAdjust(stretchCm, zoneCm + delta, centerOffsetCm));
        addAxisRow(rowY + rowHeight * 2, "Anchor Offset (cm)", centerOffsetCm, ZONE_STEP_CM,
                delta -> applyLimoAdjust(stretchCm, zoneCm, centerOffsetCm + delta));

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
