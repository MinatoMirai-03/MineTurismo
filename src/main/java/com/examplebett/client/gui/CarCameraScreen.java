//? if >=1.18 {
//?}
//? if <1.18 && >=1.17 {
/*
package com.examplebett.client.gui;
import com.examplebett.compat.ModWidgets;
import com.examplebett.compat.ModText;

import com.examplebett.compat.ModClientNetworking;
import com.examplebett.AiObjectMod;
import com.examplebett.entity.CarEntity;
import com.examplebett.network.SetCarCameraPayload;

import com.examplebett.compat.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class CarCameraScreen extends Screen implements CarCustomizationScreen {

    private static final float DISTANCE_STEP = 0.25f;
    private static final float HEIGHT_STEP = 0.25f;
    private static final int RIGHT_MARGIN = 20;
    private static final int CLUSTER_HALF_WIDTH = 70;
    private static final int BUTTON_HORIZONTAL_PADDING = 16;
    private static final int MIN_BUTTON_WIDTH = 60;

    private final CarEntity car;
    private final Screen parent;
    private int clusterCenterX;
    private String distanceLabelKey;
    private double distanceLabelValue;
    private int distanceLabelY;
    private String heightLabelKey;
    private double heightLabelValue;
    private int heightLabelY;

    public CarCameraScreen(CarEntity car, Screen parent) {
        super(ModText.translatable("gui.mineturismo.camera_adjust.title"));
        this.car = car;
        this.parent = parent;
    }

    private int fitWidth(Component label) {
        return Math.max(MIN_BUTTON_WIDTH, this.font.width(label) + BUTTON_HORIZONTAL_PADDING);
    }

    private void applyAdjust(float distanceAdjust, float heightAdjust) {
        this.car.setCameraDistanceAdjust(distanceAdjust);
        this.car.setCameraHeightAdjust(heightAdjust);
        SetCarCameraPayload mineturismo$payload = new SetCarCameraPayload(this.car.getId(), distanceAdjust, heightAdjust);
        ModClientNetworking.sendToServer(SetCarCameraPayload.ID, mineturismo$payload, SetCarCameraPayload::write);

        AiObjectMod.LOGGER.debug("camera adjust [{}] distanceAdjust={} resolvedDistance={} heightAdjust={} resolvedHeight={}",
                this.car.getModelId(), distanceAdjust, this.car.getCameraDistance(), heightAdjust, this.car.getCameraHeight());
        this.minecraft.setScreen(new CarCameraScreen(this.car, this.parent));
    }

    @Override
    protected void init() {
        this.clusterCenterX = this.width - RIGHT_MARGIN - CLUSTER_HALF_WIDTH;
        int top = this.height / 2 - 40;

        float distanceAdjust = this.car.getCameraDistanceAdjust();
        float heightAdjust = this.car.getCameraHeightAdjust();
        int rowY = top + 20;
        int centerX = this.clusterCenterX;

        this.distanceLabelKey = "gui.mineturismo.camera_adjust.distance";
        this.distanceLabelValue = distanceAdjust;
        this.distanceLabelY = rowY;
        this.addRenderableWidget(ModWidgets.button(ModText.literal("-"), centerX - CLUSTER_HALF_WIDTH, rowY, 20, 20, button -> applyAdjust(distanceAdjust - DISTANCE_STEP, heightAdjust)));
        this.addRenderableWidget(ModWidgets.button(ModText.literal("+"), centerX + CLUSTER_HALF_WIDTH - 20, rowY, 20, 20, button -> applyAdjust(distanceAdjust + DISTANCE_STEP, heightAdjust)));

        int heightRowY = rowY + 24;
        this.heightLabelKey = "gui.mineturismo.camera_adjust.height";
        this.heightLabelValue = heightAdjust;
        this.heightLabelY = heightRowY;
        this.addRenderableWidget(ModWidgets.button(ModText.literal("-"), centerX - CLUSTER_HALF_WIDTH, heightRowY, 20, 20, button -> applyAdjust(distanceAdjust, heightAdjust - HEIGHT_STEP)));
        this.addRenderableWidget(ModWidgets.button(ModText.literal("+"), centerX + CLUSTER_HALF_WIDTH - 20, heightRowY, 20, 20, button -> applyAdjust(distanceAdjust, heightAdjust + HEIGHT_STEP)));

        int rightEdge = this.width - RIGHT_MARGIN;
        int nextY = heightRowY + 20 + 8;

        Component backLabel = ModText.translatable("gui.mineturismo.back");
        int backWidth = fitWidth(backLabel);
        this.addRenderableWidget(ModWidgets.button(backLabel, rightEdge - backWidth, nextY, backWidth, 20, button ->
                this.minecraft.setScreen(this.parent)));
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        mineturismo$renderScreen(new GuiGraphics(poseStack), mouseX, mouseY, partialTick);
    }

    private void mineturismo$renderScreen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        super.render(guiGraphics.pose(), mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(this.font, this.title, this.clusterCenterX, this.height / 2 - 60, 0xFFFFFFFF);
        String distanceText = ModText.translatable(this.distanceLabelKey).getString() + String.format(": %.2f", this.distanceLabelValue);
        guiGraphics.drawCenteredString(this.font, distanceText, this.clusterCenterX, this.distanceLabelY + 6, 0xFFFFFFFF);
        String heightText = ModText.translatable(this.heightLabelKey).getString() + String.format(": %.2f", this.heightLabelValue);
        guiGraphics.drawCenteredString(this.font, heightText, this.clusterCenterX, this.heightLabelY + 6, 0xFFFFFFFF);
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
import com.examplebett.entity.CarEntity;
import com.examplebett.network.SetCarCameraPayload;

import com.examplebett.compat.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class CarCameraScreen extends Screen implements CarCustomizationScreen {

    private static final float DISTANCE_STEP = 0.25f;
    private static final float HEIGHT_STEP = 0.25f;
    private static final int RIGHT_MARGIN = 20;
    private static final int CLUSTER_HALF_WIDTH = 70;
    private static final int BUTTON_HORIZONTAL_PADDING = 16;
    private static final int MIN_BUTTON_WIDTH = 60;

    private final CarEntity car;
    private final Screen parent;
    private int clusterCenterX;
    private String distanceLabelKey;
    private double distanceLabelValue;
    private int distanceLabelY;
    private String heightLabelKey;
    private double heightLabelValue;
    private int heightLabelY;

    public CarCameraScreen(CarEntity car, Screen parent) {
        super(ModText.translatable("gui.mineturismo.camera_adjust.title"));
        this.car = car;
        this.parent = parent;
    }

    private int fitWidth(Component label) {
        return Math.max(MIN_BUTTON_WIDTH, this.font.width(label) + BUTTON_HORIZONTAL_PADDING);
    }

    private void applyAdjust(float distanceAdjust, float heightAdjust) {
        this.car.setCameraDistanceAdjust(distanceAdjust);
        this.car.setCameraHeightAdjust(heightAdjust);
        SetCarCameraPayload mineturismo$payload = new SetCarCameraPayload(this.car.getId(), distanceAdjust, heightAdjust);
        ModClientNetworking.sendToServer(SetCarCameraPayload.ID, mineturismo$payload, SetCarCameraPayload::write);

        AiObjectMod.LOGGER.debug("camera adjust [{}] distanceAdjust={} resolvedDistance={} heightAdjust={} resolvedHeight={}",
                this.car.getModelId(), distanceAdjust, this.car.getCameraDistance(), heightAdjust, this.car.getCameraHeight());
        this.minecraft.setScreen(new CarCameraScreen(this.car, this.parent));
    }

    @Override
    protected void init() {
        this.clusterCenterX = this.width - RIGHT_MARGIN - CLUSTER_HALF_WIDTH;
        int top = this.height / 2 - 40;

        float distanceAdjust = this.car.getCameraDistanceAdjust();
        float heightAdjust = this.car.getCameraHeightAdjust();
        int rowY = top + 20;
        int centerX = this.clusterCenterX;

        this.distanceLabelKey = "gui.mineturismo.camera_adjust.distance";
        this.distanceLabelValue = distanceAdjust;
        this.distanceLabelY = rowY;
        this.addButton(ModWidgets.button(ModText.literal("-"), centerX - CLUSTER_HALF_WIDTH, rowY, 20, 20, button -> applyAdjust(distanceAdjust - DISTANCE_STEP, heightAdjust)));
        this.addButton(ModWidgets.button(ModText.literal("+"), centerX + CLUSTER_HALF_WIDTH - 20, rowY, 20, 20, button -> applyAdjust(distanceAdjust + DISTANCE_STEP, heightAdjust)));

        int heightRowY = rowY + 24;
        this.heightLabelKey = "gui.mineturismo.camera_adjust.height";
        this.heightLabelValue = heightAdjust;
        this.heightLabelY = heightRowY;
        this.addButton(ModWidgets.button(ModText.literal("-"), centerX - CLUSTER_HALF_WIDTH, heightRowY, 20, 20, button -> applyAdjust(distanceAdjust, heightAdjust - HEIGHT_STEP)));
        this.addButton(ModWidgets.button(ModText.literal("+"), centerX + CLUSTER_HALF_WIDTH - 20, heightRowY, 20, 20, button -> applyAdjust(distanceAdjust, heightAdjust + HEIGHT_STEP)));

        int rightEdge = this.width - RIGHT_MARGIN;
        int nextY = heightRowY + 20 + 8;

        Component backLabel = ModText.translatable("gui.mineturismo.back");
        int backWidth = fitWidth(backLabel);
        this.addButton(ModWidgets.button(backLabel, rightEdge - backWidth, nextY, backWidth, 20, button ->
                this.minecraft.setScreen(this.parent)));
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        mineturismo$renderScreen(new GuiGraphics(poseStack), mouseX, mouseY, partialTick);
    }

    private void mineturismo$renderScreen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        super.render(guiGraphics.pose(), mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(this.font, this.title, this.clusterCenterX, this.height / 2 - 60, 0xFFFFFFFF);
        String distanceText = ModText.translatable(this.distanceLabelKey).getString() + String.format(": %.2f", this.distanceLabelValue);
        guiGraphics.drawCenteredString(this.font, distanceText, this.clusterCenterX, this.distanceLabelY + 6, 0xFFFFFFFF);
        String heightText = ModText.translatable(this.heightLabelKey).getString() + String.format(": %.2f", this.heightLabelValue);
        guiGraphics.drawCenteredString(this.font, heightText, this.clusterCenterX, this.heightLabelY + 6, 0xFFFFFFFF);
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
import com.examplebett.entity.CarEntity;
import com.examplebett.network.SetCarCameraPayload;

import com.examplebett.compat.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class CarCameraScreen extends Screen implements CarCustomizationScreen {

    private static final float DISTANCE_STEP = 0.25f;
    private static final float HEIGHT_STEP = 0.25f;
    private static final int RIGHT_MARGIN = 20;
    private static final int CLUSTER_HALF_WIDTH = 70;
    private static final int BUTTON_HORIZONTAL_PADDING = 16;
    private static final int MIN_BUTTON_WIDTH = 60;

    private final CarEntity car;
    private final Screen parent;
    private int clusterCenterX;
    private String distanceLabelKey;
    private double distanceLabelValue;
    private int distanceLabelY;
    private String heightLabelKey;
    private double heightLabelValue;
    private int heightLabelY;

    public CarCameraScreen(CarEntity car, Screen parent) {
        super(ModText.translatable("gui.mineturismo.camera_adjust.title"));
        this.car = car;
        this.parent = parent;
    }

    private int fitWidth(Component label) {
        return Math.max(MIN_BUTTON_WIDTH, com.examplebett.compat.LegacyGui.width(this.font, label) + BUTTON_HORIZONTAL_PADDING);
    }

    private void applyAdjust(float distanceAdjust, float heightAdjust) {
        this.car.setCameraDistanceAdjust(distanceAdjust);
        this.car.setCameraHeightAdjust(heightAdjust);
        SetCarCameraPayload mineturismo$payload = new SetCarCameraPayload(this.car.getId(), distanceAdjust, heightAdjust);
        ModClientNetworking.sendToServer(SetCarCameraPayload.ID, mineturismo$payload, SetCarCameraPayload::write);

        AiObjectMod.LOGGER.debug("camera adjust [{}] distanceAdjust={} resolvedDistance={} heightAdjust={} resolvedHeight={}",
                this.car.getModelId(), distanceAdjust, this.car.getCameraDistance(), heightAdjust, this.car.getCameraHeight());
        this.minecraft.setScreen(new CarCameraScreen(this.car, this.parent));
    }

    @Override
    protected void init() {
        this.clusterCenterX = this.width - RIGHT_MARGIN - CLUSTER_HALF_WIDTH;
        int top = this.height / 2 - 40;

        float distanceAdjust = this.car.getCameraDistanceAdjust();
        float heightAdjust = this.car.getCameraHeightAdjust();
        int rowY = top + 20;
        int centerX = this.clusterCenterX;

        this.distanceLabelKey = "gui.mineturismo.camera_adjust.distance";
        this.distanceLabelValue = distanceAdjust;
        this.distanceLabelY = rowY;
        this.addButton(ModWidgets.button(ModText.literal("-"), centerX - CLUSTER_HALF_WIDTH, rowY, 20, 20, button -> applyAdjust(distanceAdjust - DISTANCE_STEP, heightAdjust)));
        this.addButton(ModWidgets.button(ModText.literal("+"), centerX + CLUSTER_HALF_WIDTH - 20, rowY, 20, 20, button -> applyAdjust(distanceAdjust + DISTANCE_STEP, heightAdjust)));

        int heightRowY = rowY + 24;
        this.heightLabelKey = "gui.mineturismo.camera_adjust.height";
        this.heightLabelValue = heightAdjust;
        this.heightLabelY = heightRowY;
        this.addButton(ModWidgets.button(ModText.literal("-"), centerX - CLUSTER_HALF_WIDTH, heightRowY, 20, 20, button -> applyAdjust(distanceAdjust, heightAdjust - HEIGHT_STEP)));
        this.addButton(ModWidgets.button(ModText.literal("+"), centerX + CLUSTER_HALF_WIDTH - 20, heightRowY, 20, 20, button -> applyAdjust(distanceAdjust, heightAdjust + HEIGHT_STEP)));

        int rightEdge = this.width - RIGHT_MARGIN;
        int nextY = heightRowY + 20 + 8;

        Component backLabel = ModText.translatable("gui.mineturismo.back");
        int backWidth = fitWidth(backLabel);
        this.addButton(ModWidgets.button(backLabel, rightEdge - backWidth, nextY, backWidth, 20, button ->
                this.minecraft.setScreen(this.parent)));
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTick) {
        mineturismo$renderScreen(new GuiGraphics(null), mouseX, mouseY, partialTick);
    }

    private void mineturismo$renderScreen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        super.render(mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(this.font, this.title, this.clusterCenterX, this.height / 2 - 60, 0xFFFFFFFF);
        String distanceText = ModText.translatable(this.distanceLabelKey).getString() + String.format(": %.2f", this.distanceLabelValue);
        guiGraphics.drawCenteredString(this.font, distanceText, this.clusterCenterX, this.distanceLabelY + 6, 0xFFFFFFFF);
        String heightText = ModText.translatable(this.heightLabelKey).getString() + String.format(": %.2f", this.heightLabelValue);
        guiGraphics.drawCenteredString(this.font, heightText, this.clusterCenterX, this.heightLabelY + 6, 0xFFFFFFFF);
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
import com.examplebett.entity.CarEntity;
import com.examplebett.network.SetCarCameraPayload;

import com.examplebett.compat.GuiGraphics;
import com.examplebett.compat.v114.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class CarCameraScreen extends Screen implements CarCustomizationScreen {

    private static final float DISTANCE_STEP = 0.25f;
    private static final float HEIGHT_STEP = 0.25f;
    private static final int RIGHT_MARGIN = 20;
    private static final int CLUSTER_HALF_WIDTH = 70;
    private static final int BUTTON_HORIZONTAL_PADDING = 16;
    private static final int MIN_BUTTON_WIDTH = 60;

    private final CarEntity car;
    private final Screen parent;
    private int clusterCenterX;
    private String distanceLabelKey;
    private double distanceLabelValue;
    private int distanceLabelY;
    private String heightLabelKey;
    private double heightLabelValue;
    private int heightLabelY;

    public CarCameraScreen(CarEntity car, Screen parent) {
        super(ModText.translatable("gui.mineturismo.camera_adjust.title"));
        this.car = car;
        this.parent = parent;
    }

    private int fitWidth(Component label) {
        return Math.max(MIN_BUTTON_WIDTH, com.examplebett.compat.LegacyGui.width(this.font, label) + BUTTON_HORIZONTAL_PADDING);
    }

    private void applyAdjust(float distanceAdjust, float heightAdjust) {
        this.car.setCameraDistanceAdjust(distanceAdjust);
        this.car.setCameraHeightAdjust(heightAdjust);
        SetCarCameraPayload mineturismo$payload = new SetCarCameraPayload(this.car.getId(), distanceAdjust, heightAdjust);
        ModClientNetworking.sendToServer(SetCarCameraPayload.ID, mineturismo$payload, SetCarCameraPayload::write);

        AiObjectMod.LOGGER.debug("camera adjust [{}] distanceAdjust={} resolvedDistance={} heightAdjust={} resolvedHeight={}",
                this.car.getModelId(), distanceAdjust, this.car.getCameraDistance(), heightAdjust, this.car.getCameraHeight());
        this.minecraft.setScreen(new CarCameraScreen(this.car, this.parent));
    }

    @Override
    protected void init() {
        this.clusterCenterX = this.width - RIGHT_MARGIN - CLUSTER_HALF_WIDTH;
        int top = this.height / 2 - 40;

        float distanceAdjust = this.car.getCameraDistanceAdjust();
        float heightAdjust = this.car.getCameraHeightAdjust();
        int rowY = top + 20;
        int centerX = this.clusterCenterX;

        this.distanceLabelKey = "gui.mineturismo.camera_adjust.distance";
        this.distanceLabelValue = distanceAdjust;
        this.distanceLabelY = rowY;
        this.addButton(ModWidgets.button(ModText.literal("-"), centerX - CLUSTER_HALF_WIDTH, rowY, 20, 20, button -> applyAdjust(distanceAdjust - DISTANCE_STEP, heightAdjust)));
        this.addButton(ModWidgets.button(ModText.literal("+"), centerX + CLUSTER_HALF_WIDTH - 20, rowY, 20, 20, button -> applyAdjust(distanceAdjust + DISTANCE_STEP, heightAdjust)));

        int heightRowY = rowY + 24;
        this.heightLabelKey = "gui.mineturismo.camera_adjust.height";
        this.heightLabelValue = heightAdjust;
        this.heightLabelY = heightRowY;
        this.addButton(ModWidgets.button(ModText.literal("-"), centerX - CLUSTER_HALF_WIDTH, heightRowY, 20, 20, button -> applyAdjust(distanceAdjust, heightAdjust - HEIGHT_STEP)));
        this.addButton(ModWidgets.button(ModText.literal("+"), centerX + CLUSTER_HALF_WIDTH - 20, heightRowY, 20, 20, button -> applyAdjust(distanceAdjust, heightAdjust + HEIGHT_STEP)));

        int rightEdge = this.width - RIGHT_MARGIN;
        int nextY = heightRowY + 20 + 8;

        Component backLabel = ModText.translatable("gui.mineturismo.back");
        int backWidth = fitWidth(backLabel);
        this.addButton(ModWidgets.button(backLabel, rightEdge - backWidth, nextY, backWidth, 20, button ->
                this.minecraft.setScreen(this.parent)));
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTick) {
        mineturismo$renderScreen(new GuiGraphics(null), mouseX, mouseY, partialTick);
    }

    private void mineturismo$renderScreen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        super.render(mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(this.font, this.title, this.clusterCenterX, this.height / 2 - 60, 0xFFFFFFFF);
        String distanceText = ModText.translatable(this.distanceLabelKey).getString() + String.format(": %.2f", this.distanceLabelValue);
        guiGraphics.drawCenteredString(this.font, distanceText, this.clusterCenterX, this.distanceLabelY + 6, 0xFFFFFFFF);
        String heightText = ModText.translatable(this.heightLabelKey).getString() + String.format(": %.2f", this.heightLabelValue);
        guiGraphics.drawCenteredString(this.font, heightText, this.clusterCenterX, this.heightLabelY + 6, 0xFFFFFFFF);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
*/
//?}
