package com.examplebett.client.gui;
import com.examplebett.compat.ModWidgets;
import com.examplebett.compat.ModText;

import com.examplebett.compat.ModClientNetworking;
import com.examplebett.AiObjectMod;
import com.examplebett.client.MenuCameraOrbit;
import com.examplebett.client.render.PlateTextures;
import com.examplebett.entity.CarEntity;
import com.examplebett.network.SetCarPlateTransformPayload;
import com.examplebett.network.SetCarPlateTexturePayload;
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
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class CarPlateEditScreen extends Screen implements CarCustomizationScreen, CarCameraOrbitScreen {

    private static final float POSITION_STEP = 0.05f;
    private static final float SCALE_STEP = 0.05f;
    private static final float ANGLE_STEP = 5f;

    private static final int RIGHT_MARGIN = 20;
    private static final int CLUSTER_HALF_WIDTH = 70;

    private static final int AXIS_LABEL_MAX_WIDTH = 56;

    private final CarEntity car;
    private final boolean front;
    private final Screen parent;
    private final int initialScrollRow;

    public CarPlateEditScreen(CarEntity car, boolean front, Screen parent) {
        this(car, front, parent, 0);
    }

    public CarPlateEditScreen(CarEntity car, boolean front, Screen parent, int initialScrollRow) {
        super(ModText.translatable(front
                ? "gui.mineturismo.plate_edit.title_front"
                : "gui.mineturismo.plate_edit.title_rear"));
        this.car = car;
        this.front = front;
        this.parent = parent;
        this.initialScrollRow = initialScrollRow;
    }

    private Vec3 currentOffsetAdjust() {
        return this.front
                ? new Vec3(this.car.getFrontPlateXAdjust(), this.car.getFrontPlateYAdjust(), this.car.getFrontPlateZAdjust())
                : new Vec3(this.car.getRearPlateXAdjust(), this.car.getRearPlateYAdjust(), this.car.getRearPlateZAdjust());
    }

    private float currentScaleAdjust() {
        return this.front ? this.car.getFrontPlateScaleAdjust() : this.car.getRearPlateScaleAdjust();
    }

    private float currentYawAdjust() {
        return this.front ? this.car.getFrontPlateYawAdjust() : this.car.getRearPlateYawAdjust();
    }

    private float currentPitchAdjust() {
        return this.front ? this.car.getFrontPlatePitchAdjust() : this.car.getRearPlatePitchAdjust();
    }

    private float currentRollAdjust() {
        return this.front ? this.car.getFrontPlateRollAdjust() : this.car.getRearPlateRollAdjust();
    }

    private float currentBaseScale() {
        return this.front
                ? this.car.getFrontPlateScale() - this.car.getFrontPlateScaleAdjust()
                : this.car.getRearPlateScale() - this.car.getRearPlateScaleAdjust();
    }

    private void applyTexture(String textureId) {
        if (this.front) {
            this.car.setFrontPlate(textureId);
        } else {
            this.car.setRearPlate(textureId);
        }
        SetCarPlateTexturePayload mineturismo$payload = new SetCarPlateTexturePayload(this.car.getId(), this.front, textureId);
        //? if >=1.20.5 {
        ModClientNetworking.sendToServer(mineturismo$payload);
        //?} else {
        /*ModClientNetworking.sendToServer(SetCarPlateTexturePayload.ID, mineturismo$payload, SetCarPlateTexturePayload::write);*/
        //?}

        PlateTextures.Design design = PlateTextures.DESIGNS.stream()
                .filter(d -> d.id().equals(textureId))
                .findFirst().orElse(null);
        if (design != null) {

            float targetAbsoluteScale = "italy_glossy".equals(textureId) ? CarEntity.ITALY_FRONT_PLATE_SCALE
                    : design.european() ? CarEntity.EUROPEAN_DEFAULT_PLATE_SCALE : CarEntity.FALLBACK_PLATE_SCALE;
            float newScaleAdjust = targetAbsoluteScale - this.currentBaseScale();
            Vec3 offset = this.currentOffsetAdjust();
            float yaw = this.currentYawAdjust();
            float pitch = this.currentPitchAdjust();
            float roll = this.currentRollAdjust();
            if (this.front) {
                this.car.setFrontPlateScale(newScaleAdjust);
            } else {
                this.car.setRearPlateScale(newScaleAdjust);
            }
            SetCarPlateTransformPayload mineturismo$transformPayload = new SetCarPlateTransformPayload(
                    this.car.getId(), this.front, (float) offset.x, (float) offset.y, (float) offset.z, newScaleAdjust, yaw, pitch, roll);
            //? if >=1.20.5 {
            ModClientNetworking.sendToServer(mineturismo$transformPayload);
            //?} else {
            /*ModClientNetworking.sendToServer(SetCarPlateTransformPayload.ID, mineturismo$transformPayload, SetCarPlateTransformPayload::write);*/
            //?}
        }

        this.minecraft.setScreen(new CarPlateEditScreen(this.car, this.front, this.parent, this.designScrollRow));
    }

    private void applyTransform(Vec3 offsetAdjust, float scaleAdjust, float yawAdjust, float pitchAdjust, float rollAdjust) {
        if (this.front) {
            this.car.setFrontPlateOffset((float) offsetAdjust.x, (float) offsetAdjust.y, (float) offsetAdjust.z);
            this.car.setFrontPlateScale(scaleAdjust);
            this.car.setFrontPlateRotation(yawAdjust, pitchAdjust, rollAdjust);
        } else {
            this.car.setRearPlateOffset((float) offsetAdjust.x, (float) offsetAdjust.y, (float) offsetAdjust.z);
            this.car.setRearPlateScale(scaleAdjust);
            this.car.setRearPlateRotation(yawAdjust, pitchAdjust, rollAdjust);
        }
        SetCarPlateTransformPayload mineturismo$payload = new SetCarPlateTransformPayload(
                this.car.getId(), this.front, (float) offsetAdjust.x, (float) offsetAdjust.y, (float) offsetAdjust.z,
                scaleAdjust, yawAdjust, pitchAdjust, rollAdjust);
        //? if >=1.20.5 {
        ModClientNetworking.sendToServer(mineturismo$payload);
        //?} else {
        /*ModClientNetworking.sendToServer(SetCarPlateTransformPayload.ID, mineturismo$payload, SetCarPlateTransformPayload::write);*/
        //?}

        AiObjectMod.LOGGER.debug(
                "plate adjust [{}] front={} x={} y={} z={} scale={} yaw={} pitch={} roll={}",
                this.car.getModelId(), this.front, offsetAdjust.x, offsetAdjust.y, offsetAdjust.z,
                scaleAdjust, yawAdjust, pitchAdjust, rollAdjust);
        this.minecraft.setScreen(new CarPlateEditScreen(this.car, this.front, this.parent, this.designScrollRow));
    }

    private static final int DESIGN_COLUMNS = 3;
    private static final int DESIGN_BUTTON_WIDTH = 90;
    private static final int DESIGN_BUTTON_HEIGHT = 20;
    private static final int DESIGN_COL_GAP = 4;
    private static final int DESIGN_ROW_GAP = 4;
    private static final int DESIGN_VISIBLE_ROWS = 3;
    private static final int DESIGN_ROW_HEIGHT = DESIGN_BUTTON_HEIGHT + DESIGN_ROW_GAP;

    private final List<DesignButtonEntry> designButtons = new ArrayList<>();
    private int designScrollRow = 0;
    private int designMaxScrollRow = 0;
    private int designViewportY;

    private record DesignButtonEntry(Button button, int row) {
    }

    private void updateDesignButtonPositions() {
        int viewportTop = this.designViewportY;
        for (DesignButtonEntry entry : this.designButtons) {
            int visibleRow = entry.row() - this.designScrollRow;
            boolean isVisible = visibleRow >= 0 && visibleRow < DESIGN_VISIBLE_ROWS;
            entry.button().visible = isVisible;
            entry.button().active = isVisible;
            if (isVisible) {
                ModWidgets.setY(entry.button(), viewportTop + visibleRow * DESIGN_ROW_HEIGHT);
            }
        }
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

    //? if >=1.20.2 {
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        return mineturismo$mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }
    //?} else {
    /*@Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollY) {
        return mineturismo$mouseScrolled(mouseX, mouseY, 0, scrollY);
    }*/
    //?}

    private boolean mineturismo$mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (this.designMaxScrollRow > 0 && scrollY != 0) {
            int delta = scrollY > 0 ? -1 : 1;
            int newScrollRow = Math.max(0, Math.min(this.designMaxScrollRow, this.designScrollRow + delta));
            if (newScrollRow != this.designScrollRow) {
                this.designScrollRow = newScrollRow;
                this.updateDesignButtonPositions();
                return true;
            }
        }
        //? if >=1.20.2 {
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        //?} else {
        /*return super.mouseScrolled(mouseX, mouseY, scrollY);*/
        //?}
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int top = this.height / 2 - 150;

        this.designButtons.clear();

        int gridWidth = DESIGN_COLUMNS * DESIGN_BUTTON_WIDTH + (DESIGN_COLUMNS - 1) * DESIGN_COL_GAP;
        int designX0 = centerX - gridWidth / 2;
        int designY = top + 20;
        this.designViewportY = designY;

        List<Component> itemLabels = new ArrayList<>();
        List<String> itemIds = new ArrayList<>();
        itemLabels.add(ModText.translatable("gui.mineturismo.plate_edit.remove"));
        itemIds.add("");
        for (PlateTextures.Design design : PlateTextures.DESIGNS) {
            itemLabels.add(design.displayName());
            itemIds.add(design.id());
        }

        int totalRows = (itemIds.size() + DESIGN_COLUMNS - 1) / DESIGN_COLUMNS;
        this.designMaxScrollRow = Math.max(0, totalRows - DESIGN_VISIBLE_ROWS);
        this.designScrollRow = Math.max(0, Math.min(this.designMaxScrollRow, this.initialScrollRow));

        for (int i = 0; i < itemIds.size(); i++) {
            int row = i / DESIGN_COLUMNS;
            int col = i % DESIGN_COLUMNS;
            String textureId = itemIds.get(i);
            int x = designX0 + col * (DESIGN_BUTTON_WIDTH + DESIGN_COL_GAP);
            Button button = ModWidgets.button(itemLabels.get(i), x, designY + row * DESIGN_ROW_HEIGHT, DESIGN_BUTTON_WIDTH, DESIGN_BUTTON_HEIGHT, b -> this.applyTexture(textureId));
            this.addRenderableWidget(button);
            this.designButtons.add(new DesignButtonEntry(button, row));
        }

        int viewportHeight = DESIGN_VISIBLE_ROWS * DESIGN_ROW_HEIGHT - DESIGN_ROW_GAP;
        int arrowX = designX0 + gridWidth + 8;

        this.addRenderableWidget(new ScrollbarWidget(ScrollbarWidget.xInArrowColumn(arrowX), designY,
                designY + viewportHeight - 20 + 20 - (designY), DESIGN_VISIBLE_ROWS, this.designMaxScrollRow + DESIGN_VISIBLE_ROWS, () -> this.designScrollRow, row -> {
                    this.designScrollRow = row;
                    this.updateDesignButtonPositions();
                }));

        this.updateDesignButtonPositions();

        Vec3 offset = this.currentOffsetAdjust();
        float scale = this.currentScaleAdjust();
        float yaw = this.currentYawAdjust();
        float pitch = this.currentPitchAdjust();
        float roll = this.currentRollAdjust();

        int rowHeight = 20;
        int rowY = designY + viewportHeight + 12;
        addAxisRow(rowY, "X", offset.x, POSITION_STEP,
                delta -> applyTransform(new Vec3(offset.x + delta, offset.y, offset.z), scale, yaw, pitch, roll));
        addAxisRow(rowY + rowHeight, "Y", offset.y, POSITION_STEP,
                delta -> applyTransform(new Vec3(offset.x, offset.y + delta, offset.z), scale, yaw, pitch, roll));
        addAxisRow(rowY + rowHeight * 2, "Z", offset.z, POSITION_STEP,
                delta -> applyTransform(new Vec3(offset.x, offset.y, offset.z + delta), scale, yaw, pitch, roll));
        addAxisRow(rowY + rowHeight * 3, "Scale", scale, SCALE_STEP,
                delta -> applyTransform(offset, scale + delta, yaw, pitch, roll));
        addAxisRow(rowY + rowHeight * 4, "Yaw", yaw, ANGLE_STEP,
                delta -> applyTransform(offset, scale, yaw + delta, pitch, roll));
        addAxisRow(rowY + rowHeight * 5, "Pitch", pitch, ANGLE_STEP,
                delta -> applyTransform(offset, scale, yaw, pitch + delta, roll));
        addAxisRow(rowY + rowHeight * 6, "Roll", roll, ANGLE_STEP,
                delta -> applyTransform(offset, scale, yaw, pitch, roll + delta));

        int buttonWidth = 200;
        this.addRenderableWidget(ModWidgets.button(ModText.translatable("gui.mineturismo.back"), centerX - buttonWidth / 2, rowY + rowHeight * 7 + 8, buttonWidth, 20, button ->
                this.minecraft.setScreen(this.parent)));
    }

    private interface AxisAdjuster {
        void adjust(float delta);
    }

    private static final float FINE_STEP_DIVISOR = 5f;

    private void addAxisRow(int y, String axisLabel, double currentValue, float step, AxisAdjuster adjuster) {
        int centerX = this.width - RIGHT_MARGIN - CLUSTER_HALF_WIDTH;

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

    private static final java.util.Set<String> ANGLE_AXES = java.util.Set.of("Yaw", "Pitch", "Roll");

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
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - 170, 0xFFFFFFFF);
        int axisLabelX = this.width - RIGHT_MARGIN - CLUSTER_HALF_WIDTH;
        for (AxisLabel label : this.axisLabels) {
            String text = ANGLE_AXES.contains(label.axis())
                    ? String.format("%s: %.0f°", label.axis(), label.value())
                    : String.format("%s: %s", label.axis(), AxisValueFormat.format(label.value()));
            drawFittedAxisLabel(guiGraphics, text, axisLabelX, label.y() + 6);
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
