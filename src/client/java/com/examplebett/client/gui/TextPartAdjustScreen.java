package com.examplebett.client.gui;
import com.examplebett.compat.ModCheckbox;
import com.examplebett.compat.ModWidgets;
import com.examplebett.compat.ModText;

import com.examplebett.compat.ModClientNetworking;
import com.examplebett.AiObjectMod;
import com.examplebett.client.MenuCameraOrbit;
import com.examplebett.entity.CarEntity;
import com.examplebett.network.SetCarTextPartPayload;
import com.examplebett.render.TextParts;
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
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
//? if >=1.21.9 {
import net.minecraft.client.input.MouseButtonEvent;
//?}
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleSupplier;

public class TextPartAdjustScreen extends Screen implements CarCustomizationScreen, CarCameraOrbitScreen {

    private static final float POSITION_STEP = 0.05f;
    private static final float ANGLE_STEP = 5f;
    private static final float SCALE_STEP = 0.1f;
    private static final float DEPTH_STEP = 0.01f;
    private static final float FINE_STEP_DIVISOR = 5f;
    private static final int RIGHT_MARGIN = 20;
    private static final int CLUSTER_HALF_WIDTH = 70;
    private static final int AXIS_LABEL_MAX_WIDTH = 56;
    private static final int LEFT_X = 20;
    private static final int LEFT_WIDTH = 150;
    private static final int TOP = 30;
    private static final int ROW = 24;

    private static final int SWATCH_SIZE = 14;
    private static final int SWATCH_GAP = 2;
    private static final int SWATCH_COLUMNS = 9;

    private final CarEntity car;
    private final TextPartsScreen listScreen;
    private final int slotIndex;
    private final int listScrollRow;
    private TextParts.Slot slot;

    private static final int AXIS_ROW_COUNT = 10;

    private int clusterCenterX;
    private int rowHeight = 20;
    private Button fontButton;
    private int colorLabelY;
    private final List<AxisRow> axisRows = new ArrayList<>();
    private final List<Swatch> swatches = new ArrayList<>();
    private List<CarColorScreen.PaletteEntry> palette = List.of();

    private record AxisRow(String label, DoubleSupplier value, int y) {
    }

    private record Swatch(int x, int y, int rgb) {
    }

    private interface AxisAdjuster {
        void adjust(float delta);
    }

    public TextPartAdjustScreen(CarEntity car, TextPartsScreen listScreen, int slotIndex, int listScrollRow) {
        super(ModText.translatable("gui.mineturismo.text_part_adjust.title", String.valueOf(slotIndex + 1)));
        this.car = car;
        this.listScreen = listScreen;
        this.slotIndex = slotIndex;
        this.listScrollRow = listScrollRow;
        this.slot = car.getTextPart(slotIndex);
    }

    private void apply(TextParts.Slot updated) {
        this.slot = updated;
        this.car.setTextPart(this.slotIndex, updated);
        SetCarTextPartPayload mineturismo$payload = new SetCarTextPartPayload(this.car.getId(), TextParts.encodeLine(this.slotIndex, updated));
        //? if >=1.20.5 {
        ModClientNetworking.sendToServer(mineturismo$payload);
        //?} else {
        /*ModClientNetworking.sendToServer(SetCarTextPartPayload.ID, mineturismo$payload, SetCarTextPartPayload::write);*/
        //?}
    }

    private void applyAdjust(TextParts.Slot updated) {
        this.apply(updated);
        AiObjectMod.LOGGER.debug("text part adjust [{}] slot={} {}", this.car.getModelId(), this.slotIndex, updated);
    }

    @Override
    public void removed() {
        super.removed();
        if (this.slot != null) {
            AiObjectMod.LOGGER.debug("text part state [{}] {}", this.car.getModelId(), TextParts.encodeLine(this.slotIndex, this.slot));
        }
    }

    private Component fontLabel() {
        return ModText.translatable("gui.mineturismo.text_part_adjust.font",
                ModText.translatable("gui.mineturismo.text_part_adjust.font_" + this.slot.font()));
    }

    private void cycleFont() {
        int next = (TextParts.fontIndex(this.slot.font()) + 1) % TextParts.FONT_IDS.length;
        this.apply(this.slot.withFont(TextParts.FONT_IDS[next]));
        this.fontButton.setMessage(this.fontLabel());
    }

    @Override
    protected void init() {
        this.clusterCenterX = this.width - RIGHT_MARGIN - CLUSTER_HALF_WIDTH;
        this.palette = CarColorScreen.palette();

        int y = TOP;
        this.addRenderableWidget(ModCheckbox.builder(ModText.translatable("gui.mineturismo.text_part_adjust.enabled"), this.font)
                .pos(LEFT_X, y)
                .selected(this.slot.enabled())
                .onValueChange((checkbox, selected) -> this.apply(this.slot.withEnabled(selected)))
                .build());
        y += ROW;

        Component hint = ModText.translatable("gui.mineturismo.text_part_adjust.text_hint");
        EditBox textBox = new EditBox(this.font, LEFT_X, y, LEFT_WIDTH, 20, hint);
        textBox.setMaxLength(TextParts.MAX_TEXT_LENGTH);
        textBox.setValue(this.slot.text());
        ModWidgets.setHint(textBox, hint);
        textBox.setResponder(value -> {
            if (!value.equals(this.slot.text())) {
                this.apply(this.slot.withText(value));
            }
        });
        this.addRenderableWidget(textBox);
        y += ROW;

        this.fontButton = this.addRenderableWidget(ModWidgets.button(this.fontLabel(), LEFT_X, y, LEFT_WIDTH, 20, b -> this.cycleFont()));
        y += ROW;

        this.addRenderableWidget(ModCheckbox.builder(ModText.translatable("gui.mineturismo.text_part_adjust.glow"), this.font)
                .pos(LEFT_X, y)
                .selected(this.slot.glow())
                .onValueChange((checkbox, selected) -> this.apply(this.slot.withGlow(selected)))
                .build());
        y += ROW;

        this.colorLabelY = y;
        int swatchTop = y + 12;
        this.swatches.clear();
        for (int i = 0; i < this.palette.size(); i++) {
            int rgb = this.palette.get(i).rgb();
            int sx = LEFT_X + (i % SWATCH_COLUMNS) * (SWATCH_SIZE + SWATCH_GAP);
            int sy = swatchTop + (i / SWATCH_COLUMNS) * (SWATCH_SIZE + SWATCH_GAP);
            this.addRenderableWidget(ModWidgets.button(ModText.literal(""), sx, sy, SWATCH_SIZE, SWATCH_SIZE, b -> this.apply(this.slot.withColor(rgb))));
            this.swatches.add(new Swatch(sx, sy, rgb));
        }
        int swatchRows = (this.palette.size() + SWATCH_COLUMNS - 1) / SWATCH_COLUMNS;
        y = swatchTop + swatchRows * (SWATCH_SIZE + SWATCH_GAP) + 4;

        int mountWidth = SWATCH_COLUMNS * (SWATCH_SIZE + SWATCH_GAP) - SWATCH_GAP;
        this.addRenderableWidget(ModWidgets.button(ModText.translatable("gui.mineturismo.text_part_adjust.move_front_plate"),
                LEFT_X, y, mountWidth, 18, b -> this.moveToPlateMount(true)));
        y += 20;
        this.addRenderableWidget(ModWidgets.button(ModText.translatable("gui.mineturismo.text_part_adjust.move_rear_plate"),
                LEFT_X, y, mountWidth, 18, b -> this.moveToPlateMount(false)));

        this.axisRows.clear();
        int rowY = TOP;

        this.rowHeight = Math.max(16, Math.min(20, (this.height - TOP - 6 - 20 - 4) / AXIS_ROW_COUNT));
        int rowHeight = this.rowHeight;
        addAxisRow(rowY, "X", () -> this.slot.x(), POSITION_STEP,
                d -> this.applyAdjust(this.slot.withPosition(this.slot.x() + d, this.slot.y(), this.slot.z())));
        addAxisRow(rowY += rowHeight, "Y", () -> this.slot.y(), POSITION_STEP,
                d -> this.applyAdjust(this.slot.withPosition(this.slot.x(), this.slot.y() + d, this.slot.z())));
        addAxisRow(rowY += rowHeight, "Z", () -> this.slot.z(), POSITION_STEP,
                d -> this.applyAdjust(this.slot.withPosition(this.slot.x(), this.slot.y(), this.slot.z() + d)));
        addAxisRow(rowY += rowHeight, "Yaw", () -> this.slot.yaw(), ANGLE_STEP,
                d -> this.applyAdjust(this.slot.withRotation(this.slot.pitch(), this.slot.yaw() + d, this.slot.roll())));
        addAxisRow(rowY += rowHeight, "Pitch", () -> this.slot.pitch(), ANGLE_STEP,
                d -> this.applyAdjust(this.slot.withRotation(this.slot.pitch() + d, this.slot.yaw(), this.slot.roll())));
        addAxisRow(rowY += rowHeight, "Roll", () -> this.slot.roll(), ANGLE_STEP,
                d -> this.applyAdjust(this.slot.withRotation(this.slot.pitch(), this.slot.yaw(), this.slot.roll() + d)));
        addAxisRow(rowY += rowHeight, "Scale", () -> this.slot.scale(), SCALE_STEP,
                d -> this.applyAdjust(this.slot.withScale(this.slot.scale() + d)));
        addAxisRow(rowY += rowHeight, "Width", () -> this.slot.width(), SCALE_STEP,
                d -> this.applyAdjust(this.slot.withStretch(this.slot.width() + d, this.slot.height())));
        addAxisRow(rowY += rowHeight, "Height", () -> this.slot.height(), SCALE_STEP,
                d -> this.applyAdjust(this.slot.withStretch(this.slot.width(), this.slot.height() + d)));
        addAxisRow(rowY += rowHeight, "Depth", () -> this.slot.depth(), DEPTH_STEP,
                d -> this.applyAdjust(this.slot.withDepth(this.slot.depth() + d)));
        rowY += rowHeight + 6;

        int rightEdge = this.width - RIGHT_MARGIN;
        this.addRenderableWidget(ModWidgets.button(ModText.translatable("gui.mineturismo.back"),
                rightEdge - CLUSTER_HALF_WIDTH * 2, rowY, CLUSTER_HALF_WIDTH * 2, 20, b ->
                        this.minecraft.setScreen(new TextPartsScreen(this.car, this.listScreen.parent(), this.listScrollRow))));
    }

    private static final float PLATE_MOUNT_GAP = 0.002f;

    private void moveToPlateMount(boolean front) {
        net.minecraft.world.phys.Vec3 offset = front ? this.car.getFrontPlateOffset() : this.car.getRearPlateOffset();
        float yaw = front ? this.car.getFrontPlateYaw() : net.minecraft.util.Mth.wrapDegrees(this.car.getRearPlateYaw() + 180f);
        float pitch = front ? this.car.getFrontPlatePitch() : this.car.getRearPlatePitch();
        float roll = front ? this.car.getFrontPlateRoll() : this.car.getRearPlateRoll();

        double yawRad = Math.toRadians(yaw);
        double pitchRad = Math.toRadians(pitch);
        double nx = Math.cos(pitchRad) * Math.sin(yawRad);
        double ny = -Math.sin(pitchRad);
        double nz = Math.cos(pitchRad) * Math.cos(yawRad);
        this.applyAdjust(this.slot.withRotation(pitch, yaw, roll)
                .withPosition((float) (offset.x + nx * PLATE_MOUNT_GAP), (float) (offset.y + ny * PLATE_MOUNT_GAP),
                        (float) (offset.z + nz * PLATE_MOUNT_GAP)));
    }

    private void addAxisRow(int y, String label, DoubleSupplier value, float step, AxisAdjuster adjuster) {
        int centerX = this.clusterCenterX;

        float fineStep = Math.abs(step / FINE_STEP_DIVISOR - 0.01f) < 1e-6f ? 0.002f : step / FINE_STEP_DIVISOR;
        int h = this.rowHeight;
        this.addRenderableWidget(ModWidgets.button(ModText.literal("-"), centerX - 70, y, 20, h, button -> adjuster.adjust(-step)));
        this.addRenderableWidget(ModWidgets.button(ModText.literal("-"), centerX - 48, y, 20, h, button -> adjuster.adjust(-fineStep)));
        this.addRenderableWidget(ModWidgets.button(ModText.literal("+"), centerX + 28, y, 20, h, button -> adjuster.adjust(fineStep)));
        this.addRenderableWidget(ModWidgets.button(ModText.literal("+"), centerX + 50, y, 20, h, button -> adjuster.adjust(step)));
        this.axisRows.add(new AxisRow(label, value, y));
    }

    private boolean cameraDragging = false;

    @Override
    //? if >=1.21.9 {
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (super.mouseClicked(event, doubleClick)) {
            return true;
        }
        if (event.button() == 0) {

            this.setFocused(null);
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
            this.setFocused(null);
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
        guiGraphics.drawString(this.font, this.title, LEFT_X, TOP - 20, 0xFFFFFFFF);
        guiGraphics.drawString(this.font, ModText.translatable("gui.mineturismo.text_part_adjust.color", this.colorName()),
                LEFT_X, this.colorLabelY, 0xFFFFFFFF);

        for (Swatch swatch : this.swatches) {
            CarColorScreen.drawSwatchFill(guiGraphics, swatch.x() + 2, swatch.y() + 2, swatch.x() + SWATCH_SIZE - 2, swatch.y() + SWATCH_SIZE - 2, swatch.rgb());
            if (swatch.rgb() == this.slot.color()) {
                int x0 = swatch.x() - 1;
                int y0 = swatch.y() - 1;
                int x1 = swatch.x() + SWATCH_SIZE + 1;
                int y1 = swatch.y() + SWATCH_SIZE + 1;
                guiGraphics.fill(x0, y0, x1, y0 + 1, 0xFFFFFFFF);
                guiGraphics.fill(x0, y1 - 1, x1, y1, 0xFFFFFFFF);
                guiGraphics.fill(x0, y0, x0 + 1, y1, 0xFFFFFFFF);
                guiGraphics.fill(x1 - 1, y0, x1, y1, 0xFFFFFFFF);
            }
        }
        for (AxisRow row : this.axisRows) {
            drawFittedAxisLabel(guiGraphics, String.format("%s: %s", row.label(), AxisValueFormat.format(row.value().getAsDouble())), this.clusterCenterX, row.y() + (this.rowHeight - 8) / 2);
        }
    }

    private Component colorName() {
        for (CarColorScreen.PaletteEntry entry : this.palette) {
            if (entry.rgb() == this.slot.color()) {
                return ModText.translatable(entry.labelKey());
            }
        }
        return ModText.literal(String.format("#%06X", this.slot.color()));
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
