//? if >=1.18 {
//?}
//? if <1.18 && >=1.17 {
/*
package com.examplebett.client.gui;
import com.examplebett.compat.ModCheckbox;
import com.examplebett.compat.ModWidgets;
import com.examplebett.compat.ModText;

import com.examplebett.compat.ModClientNetworking;
import com.examplebett.AiObjectMod;
import com.examplebett.client.MenuCameraOrbit;
import com.examplebett.entity.CarEntity;
import com.examplebett.network.SetCarUnderNeonPayload;
import com.examplebett.render.UnderNeon;

import com.examplebett.compat.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleSupplier;

public class UnderNeonAdjustScreen extends Screen implements CarCustomizationScreen, CarCameraOrbitScreen {

    private static final float POSITION_STEP = 0.05f;
    private static final float ANGLE_STEP = 5f;
    private static final float SIZE_STEP = 0.1f;
    private static final float BRIGHTNESS_STEP = 0.1f;
    private static final float FINE_STEP_DIVISOR = 5f;
    private static final int RIGHT_MARGIN = 20;
    private static final int CLUSTER_HALF_WIDTH = 70;
    private static final int AXIS_LABEL_MAX_WIDTH = 56;
    private static final int LEFT_X = 20;
    private static final int TOP = 30;
    private static final int ROW = 24;

    private static final int SWATCH_SIZE = 14;
    private static final int SWATCH_GAP = 2;
    private static final int SWATCH_COLUMNS = 9;

    private final CarEntity car;

    private final Screen wrenchScreen;
    private final int slotIndex;
    private UnderNeon.Slot slot;

    private int clusterCenterX;
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

    public UnderNeonAdjustScreen(CarEntity car, Screen wrenchScreen, int slotIndex) {
        super(ModText.translatable("gui.mineturismo.under_neon_adjust.title",
                ModText.translatable("gui.mineturismo.under_neon.slot_" + UnderNeon.SLOT_IDS[slotIndex])));
        this.car = car;
        this.wrenchScreen = wrenchScreen;
        this.slotIndex = slotIndex;
        this.slot = car.getUnderNeon(slotIndex);
    }

    private void apply(UnderNeon.Slot updated) {
        this.slot = updated;
        this.car.setUnderNeon(this.slotIndex, updated);
        SetCarUnderNeonPayload mineturismo$payload = new SetCarUnderNeonPayload(this.car.getId(), UnderNeon.encodeLine(this.slotIndex, updated));
        ModClientNetworking.sendToServer(SetCarUnderNeonPayload.ID, mineturismo$payload, SetCarUnderNeonPayload::write);
    }

    private void applyAdjust(UnderNeon.Slot updated) {
        this.apply(updated);
        AiObjectMod.LOGGER.debug("under neon adjust [{}] slot={} {}", this.car.getModelId(), UnderNeon.SLOT_IDS[this.slotIndex], updated);
    }

    @Override
    protected void init() {
        this.clusterCenterX = this.width - RIGHT_MARGIN - CLUSTER_HALF_WIDTH;
        this.palette = CarColorScreen.palette();

        int y = TOP;
        this.addRenderableWidget(ModCheckbox.builder(ModText.translatable("gui.mineturismo.under_neon_adjust.enabled"), this.font)
                .pos(LEFT_X, y)
                .selected(this.slot.enabled())
                .onValueChange((checkbox, selected) -> this.apply(this.slot.withEnabled(selected)))
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

        this.axisRows.clear();
        int rowY = TOP;
        int rowHeight = 20;
        addAxisRow(rowY, "X", () -> this.slot.x(), POSITION_STEP,
                d -> this.applyAdjust(this.slot.withPosition(this.slot.x() + d, this.slot.y(), this.slot.z())));
        addAxisRow(rowY += rowHeight, "Y", () -> this.slot.y(), POSITION_STEP,
                d -> this.applyAdjust(this.slot.withPosition(this.slot.x(), this.slot.y() + d, this.slot.z())));
        addAxisRow(rowY += rowHeight, "Z", () -> this.slot.z(), POSITION_STEP,
                d -> this.applyAdjust(this.slot.withPosition(this.slot.x(), this.slot.y(), this.slot.z() + d)));
        addAxisRow(rowY += rowHeight, "Yaw", () -> this.slot.yaw(), ANGLE_STEP,
                d -> this.applyAdjust(this.slot.withYaw(this.slot.yaw() + d)));
        addAxisRow(rowY += rowHeight, "Width", () -> this.slot.width(), SIZE_STEP,
                d -> this.applyAdjust(this.slot.withSize(this.slot.width() + d, this.slot.length())));
        addAxisRow(rowY += rowHeight, "Length", () -> this.slot.length(), SIZE_STEP,
                d -> this.applyAdjust(this.slot.withSize(this.slot.width(), this.slot.length() + d)));
        addAxisRow(rowY += rowHeight, "Bright", () -> this.slot.brightness(), BRIGHTNESS_STEP,
                d -> this.applyAdjust(this.slot.withBrightness(this.slot.brightness() + d)));
        rowY += rowHeight + 6;

        int rightEdge = this.width - RIGHT_MARGIN;
        int halfWidth = (CLUSTER_HALF_WIDTH * 2 - 4) / 2;
        this.addRenderableWidget(ModWidgets.button(ModText.translatable("gui.mineturismo.under_neon_adjust.reset_transform"),
                rightEdge - CLUSTER_HALF_WIDTH * 2, rowY, halfWidth, 20, b -> this.resetTransform()));
        this.addRenderableWidget(ModWidgets.button(ModText.translatable("gui.mineturismo.back"),
                rightEdge - halfWidth, rowY, halfWidth, 20, b ->
                        this.minecraft.setScreen(new UnderNeonScreen(this.car, this.wrenchScreen))));
    }

    private void resetTransform() {
        UnderNeon.Slot defaults = this.car.getUnderNeonDefault(this.slotIndex);
        this.applyAdjust(this.slot.withPosition(defaults.x(), defaults.y(), defaults.z())
                .withYaw(defaults.yaw())
                .withSize(defaults.width(), defaults.length())
                .withBrightness(defaults.brightness()));
    }

    private void addAxisRow(int y, String label, DoubleSupplier value, float step, AxisAdjuster adjuster) {
        int centerX = this.clusterCenterX;

        float fineStep = Math.abs(step / FINE_STEP_DIVISOR - 0.01f) < 1e-6f ? 0.002f : step / FINE_STEP_DIVISOR;
        this.addRenderableWidget(ModWidgets.button(ModText.literal("-"), centerX - 70, y, 20, 20, button -> adjuster.adjust(-step)));
        this.addRenderableWidget(ModWidgets.button(ModText.literal("-"), centerX - 48, y, 20, 20, button -> adjuster.adjust(-fineStep)));
        this.addRenderableWidget(ModWidgets.button(ModText.literal("+"), centerX + 28, y, 20, 20, button -> adjuster.adjust(fineStep)));
        this.addRenderableWidget(ModWidgets.button(ModText.literal("+"), centerX + 50, y, 20, 20, button -> adjuster.adjust(step)));
        this.axisRows.add(new AxisRow(label, value, y));
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
        guiGraphics.drawString(this.font, this.title, LEFT_X, TOP - 20, 0xFFFFFFFF);
        guiGraphics.drawString(this.font, ModText.translatable("gui.mineturismo.under_neon_adjust.color", this.colorName()),
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
            drawFittedAxisLabel(guiGraphics, String.format("%s: %s", row.label(), AxisValueFormat.format(row.value().getAsDouble())), this.clusterCenterX, row.y() + 6);
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
import com.examplebett.compat.ModCheckbox;
import com.examplebett.compat.ModWidgets;
import com.examplebett.compat.ModText;

import com.examplebett.compat.ModClientNetworking;
import com.examplebett.AiObjectMod;
import com.examplebett.client.MenuCameraOrbit;
import com.examplebett.entity.CarEntity;
import com.examplebett.network.SetCarUnderNeonPayload;
import com.examplebett.render.UnderNeon;

import com.examplebett.compat.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleSupplier;

public class UnderNeonAdjustScreen extends Screen implements CarCustomizationScreen, CarCameraOrbitScreen {

    private static final float POSITION_STEP = 0.05f;
    private static final float ANGLE_STEP = 5f;
    private static final float SIZE_STEP = 0.1f;
    private static final float BRIGHTNESS_STEP = 0.1f;
    private static final float FINE_STEP_DIVISOR = 5f;
    private static final int RIGHT_MARGIN = 20;
    private static final int CLUSTER_HALF_WIDTH = 70;
    private static final int AXIS_LABEL_MAX_WIDTH = 56;
    private static final int LEFT_X = 20;
    private static final int TOP = 30;
    private static final int ROW = 24;

    private static final int SWATCH_SIZE = 14;
    private static final int SWATCH_GAP = 2;
    private static final int SWATCH_COLUMNS = 9;

    private final CarEntity car;

    private final Screen wrenchScreen;
    private final int slotIndex;
    private UnderNeon.Slot slot;

    private int clusterCenterX;
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

    public UnderNeonAdjustScreen(CarEntity car, Screen wrenchScreen, int slotIndex) {
        super(ModText.translatable("gui.mineturismo.under_neon_adjust.title",
                ModText.translatable("gui.mineturismo.under_neon.slot_" + UnderNeon.SLOT_IDS[slotIndex])));
        this.car = car;
        this.wrenchScreen = wrenchScreen;
        this.slotIndex = slotIndex;
        this.slot = car.getUnderNeon(slotIndex);
    }

    private void apply(UnderNeon.Slot updated) {
        this.slot = updated;
        this.car.setUnderNeon(this.slotIndex, updated);
        SetCarUnderNeonPayload mineturismo$payload = new SetCarUnderNeonPayload(this.car.getId(), UnderNeon.encodeLine(this.slotIndex, updated));
        ModClientNetworking.sendToServer(SetCarUnderNeonPayload.ID, mineturismo$payload, SetCarUnderNeonPayload::write);
    }

    private void applyAdjust(UnderNeon.Slot updated) {
        this.apply(updated);
        AiObjectMod.LOGGER.debug("under neon adjust [{}] slot={} {}", this.car.getModelId(), UnderNeon.SLOT_IDS[this.slotIndex], updated);
    }

    @Override
    protected void init() {
        this.clusterCenterX = this.width - RIGHT_MARGIN - CLUSTER_HALF_WIDTH;
        this.palette = CarColorScreen.palette();

        int y = TOP;
        this.addButton(ModCheckbox.builder(ModText.translatable("gui.mineturismo.under_neon_adjust.enabled"), this.font)
                .pos(LEFT_X, y)
                .selected(this.slot.enabled())
                .onValueChange((checkbox, selected) -> this.apply(this.slot.withEnabled(selected)))
                .build());
        y += ROW;

        this.colorLabelY = y;
        int swatchTop = y + 12;
        this.swatches.clear();
        for (int i = 0; i < this.palette.size(); i++) {
            int rgb = this.palette.get(i).rgb();
            int sx = LEFT_X + (i % SWATCH_COLUMNS) * (SWATCH_SIZE + SWATCH_GAP);
            int sy = swatchTop + (i / SWATCH_COLUMNS) * (SWATCH_SIZE + SWATCH_GAP);
            this.addButton(ModWidgets.button(ModText.literal(""), sx, sy, SWATCH_SIZE, SWATCH_SIZE, b -> this.apply(this.slot.withColor(rgb))));
            this.swatches.add(new Swatch(sx, sy, rgb));
        }

        this.axisRows.clear();
        int rowY = TOP;
        int rowHeight = 20;
        addAxisRow(rowY, "X", () -> this.slot.x(), POSITION_STEP,
                d -> this.applyAdjust(this.slot.withPosition(this.slot.x() + d, this.slot.y(), this.slot.z())));
        addAxisRow(rowY += rowHeight, "Y", () -> this.slot.y(), POSITION_STEP,
                d -> this.applyAdjust(this.slot.withPosition(this.slot.x(), this.slot.y() + d, this.slot.z())));
        addAxisRow(rowY += rowHeight, "Z", () -> this.slot.z(), POSITION_STEP,
                d -> this.applyAdjust(this.slot.withPosition(this.slot.x(), this.slot.y(), this.slot.z() + d)));
        addAxisRow(rowY += rowHeight, "Yaw", () -> this.slot.yaw(), ANGLE_STEP,
                d -> this.applyAdjust(this.slot.withYaw(this.slot.yaw() + d)));
        addAxisRow(rowY += rowHeight, "Width", () -> this.slot.width(), SIZE_STEP,
                d -> this.applyAdjust(this.slot.withSize(this.slot.width() + d, this.slot.length())));
        addAxisRow(rowY += rowHeight, "Length", () -> this.slot.length(), SIZE_STEP,
                d -> this.applyAdjust(this.slot.withSize(this.slot.width(), this.slot.length() + d)));
        addAxisRow(rowY += rowHeight, "Bright", () -> this.slot.brightness(), BRIGHTNESS_STEP,
                d -> this.applyAdjust(this.slot.withBrightness(this.slot.brightness() + d)));
        rowY += rowHeight + 6;

        int rightEdge = this.width - RIGHT_MARGIN;
        int halfWidth = (CLUSTER_HALF_WIDTH * 2 - 4) / 2;
        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.under_neon_adjust.reset_transform"),
                rightEdge - CLUSTER_HALF_WIDTH * 2, rowY, halfWidth, 20, b -> this.resetTransform()));
        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.back"),
                rightEdge - halfWidth, rowY, halfWidth, 20, b ->
                        this.minecraft.setScreen(new UnderNeonScreen(this.car, this.wrenchScreen))));
    }

    private void resetTransform() {
        UnderNeon.Slot defaults = this.car.getUnderNeonDefault(this.slotIndex);
        this.applyAdjust(this.slot.withPosition(defaults.x(), defaults.y(), defaults.z())
                .withYaw(defaults.yaw())
                .withSize(defaults.width(), defaults.length())
                .withBrightness(defaults.brightness()));
    }

    private void addAxisRow(int y, String label, DoubleSupplier value, float step, AxisAdjuster adjuster) {
        int centerX = this.clusterCenterX;

        float fineStep = Math.abs(step / FINE_STEP_DIVISOR - 0.01f) < 1e-6f ? 0.002f : step / FINE_STEP_DIVISOR;
        this.addButton(ModWidgets.button(ModText.literal("-"), centerX - 70, y, 20, 20, button -> adjuster.adjust(-step)));
        this.addButton(ModWidgets.button(ModText.literal("-"), centerX - 48, y, 20, 20, button -> adjuster.adjust(-fineStep)));
        this.addButton(ModWidgets.button(ModText.literal("+"), centerX + 28, y, 20, 20, button -> adjuster.adjust(fineStep)));
        this.addButton(ModWidgets.button(ModText.literal("+"), centerX + 50, y, 20, 20, button -> adjuster.adjust(step)));
        this.axisRows.add(new AxisRow(label, value, y));
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
        guiGraphics.drawString(this.font, this.title, LEFT_X, TOP - 20, 0xFFFFFFFF);
        guiGraphics.drawString(this.font, ModText.translatable("gui.mineturismo.under_neon_adjust.color", this.colorName()),
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
            drawFittedAxisLabel(guiGraphics, String.format("%s: %s", row.label(), AxisValueFormat.format(row.value().getAsDouble())), this.clusterCenterX, row.y() + 6);
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
import com.examplebett.compat.ModCheckbox;
import com.examplebett.compat.ModWidgets;
import com.examplebett.compat.ModText;

import com.examplebett.compat.ModClientNetworking;
import com.examplebett.AiObjectMod;
import com.examplebett.client.MenuCameraOrbit;
import com.examplebett.entity.CarEntity;
import com.examplebett.network.SetCarUnderNeonPayload;
import com.examplebett.render.UnderNeon;

import com.examplebett.compat.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleSupplier;

public class UnderNeonAdjustScreen extends Screen implements CarCustomizationScreen, CarCameraOrbitScreen {

    private static final float POSITION_STEP = 0.05f;
    private static final float ANGLE_STEP = 5f;
    private static final float SIZE_STEP = 0.1f;
    private static final float BRIGHTNESS_STEP = 0.1f;
    private static final float FINE_STEP_DIVISOR = 5f;
    private static final int RIGHT_MARGIN = 20;
    private static final int CLUSTER_HALF_WIDTH = 70;
    private static final int AXIS_LABEL_MAX_WIDTH = 56;
    private static final int LEFT_X = 20;
    private static final int TOP = 30;
    private static final int ROW = 24;

    private static final int SWATCH_SIZE = 14;
    private static final int SWATCH_GAP = 2;
    private static final int SWATCH_COLUMNS = 9;

    private final CarEntity car;

    private final Screen wrenchScreen;
    private final int slotIndex;
    private UnderNeon.Slot slot;

    private int clusterCenterX;
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

    public UnderNeonAdjustScreen(CarEntity car, Screen wrenchScreen, int slotIndex) {
        super(ModText.translatable("gui.mineturismo.under_neon_adjust.title",
                ModText.translatable("gui.mineturismo.under_neon.slot_" + UnderNeon.SLOT_IDS[slotIndex])));
        this.car = car;
        this.wrenchScreen = wrenchScreen;
        this.slotIndex = slotIndex;
        this.slot = car.getUnderNeon(slotIndex);
    }

    private void apply(UnderNeon.Slot updated) {
        this.slot = updated;
        this.car.setUnderNeon(this.slotIndex, updated);
        SetCarUnderNeonPayload mineturismo$payload = new SetCarUnderNeonPayload(this.car.getId(), UnderNeon.encodeLine(this.slotIndex, updated));
        ModClientNetworking.sendToServer(SetCarUnderNeonPayload.ID, mineturismo$payload, SetCarUnderNeonPayload::write);
    }

    private void applyAdjust(UnderNeon.Slot updated) {
        this.apply(updated);
        AiObjectMod.LOGGER.debug("under neon adjust [{}] slot={} {}", this.car.getModelId(), UnderNeon.SLOT_IDS[this.slotIndex], updated);
    }

    @Override
    protected void init() {
        this.clusterCenterX = this.width - RIGHT_MARGIN - CLUSTER_HALF_WIDTH;
        this.palette = CarColorScreen.palette();

        int y = TOP;
        this.addButton(ModCheckbox.builder(ModText.translatable("gui.mineturismo.under_neon_adjust.enabled"), this.font)
                .pos(LEFT_X, y)
                .selected(this.slot.enabled())
                .onValueChange((checkbox, selected) -> this.apply(this.slot.withEnabled(selected)))
                .build());
        y += ROW;

        this.colorLabelY = y;
        int swatchTop = y + 12;
        this.swatches.clear();
        for (int i = 0; i < this.palette.size(); i++) {
            int rgb = this.palette.get(i).rgb();
            int sx = LEFT_X + (i % SWATCH_COLUMNS) * (SWATCH_SIZE + SWATCH_GAP);
            int sy = swatchTop + (i / SWATCH_COLUMNS) * (SWATCH_SIZE + SWATCH_GAP);
            this.addButton(ModWidgets.button(ModText.literal(""), sx, sy, SWATCH_SIZE, SWATCH_SIZE, b -> this.apply(this.slot.withColor(rgb))));
            this.swatches.add(new Swatch(sx, sy, rgb));
        }

        this.axisRows.clear();
        int rowY = TOP;
        int rowHeight = 20;
        addAxisRow(rowY, "X", () -> this.slot.x(), POSITION_STEP,
                d -> this.applyAdjust(this.slot.withPosition(this.slot.x() + d, this.slot.y(), this.slot.z())));
        addAxisRow(rowY += rowHeight, "Y", () -> this.slot.y(), POSITION_STEP,
                d -> this.applyAdjust(this.slot.withPosition(this.slot.x(), this.slot.y() + d, this.slot.z())));
        addAxisRow(rowY += rowHeight, "Z", () -> this.slot.z(), POSITION_STEP,
                d -> this.applyAdjust(this.slot.withPosition(this.slot.x(), this.slot.y(), this.slot.z() + d)));
        addAxisRow(rowY += rowHeight, "Yaw", () -> this.slot.yaw(), ANGLE_STEP,
                d -> this.applyAdjust(this.slot.withYaw(this.slot.yaw() + d)));
        addAxisRow(rowY += rowHeight, "Width", () -> this.slot.width(), SIZE_STEP,
                d -> this.applyAdjust(this.slot.withSize(this.slot.width() + d, this.slot.length())));
        addAxisRow(rowY += rowHeight, "Length", () -> this.slot.length(), SIZE_STEP,
                d -> this.applyAdjust(this.slot.withSize(this.slot.width(), this.slot.length() + d)));
        addAxisRow(rowY += rowHeight, "Bright", () -> this.slot.brightness(), BRIGHTNESS_STEP,
                d -> this.applyAdjust(this.slot.withBrightness(this.slot.brightness() + d)));
        rowY += rowHeight + 6;

        int rightEdge = this.width - RIGHT_MARGIN;
        int halfWidth = (CLUSTER_HALF_WIDTH * 2 - 4) / 2;
        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.under_neon_adjust.reset_transform"),
                rightEdge - CLUSTER_HALF_WIDTH * 2, rowY, halfWidth, 20, b -> this.resetTransform()));
        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.back"),
                rightEdge - halfWidth, rowY, halfWidth, 20, b ->
                        this.minecraft.setScreen(new UnderNeonScreen(this.car, this.wrenchScreen))));
    }

    private void resetTransform() {
        UnderNeon.Slot defaults = this.car.getUnderNeonDefault(this.slotIndex);
        this.applyAdjust(this.slot.withPosition(defaults.x(), defaults.y(), defaults.z())
                .withYaw(defaults.yaw())
                .withSize(defaults.width(), defaults.length())
                .withBrightness(defaults.brightness()));
    }

    private void addAxisRow(int y, String label, DoubleSupplier value, float step, AxisAdjuster adjuster) {
        int centerX = this.clusterCenterX;

        float fineStep = Math.abs(step / FINE_STEP_DIVISOR - 0.01f) < 1e-6f ? 0.002f : step / FINE_STEP_DIVISOR;
        this.addButton(ModWidgets.button(ModText.literal("-"), centerX - 70, y, 20, 20, button -> adjuster.adjust(-step)));
        this.addButton(ModWidgets.button(ModText.literal("-"), centerX - 48, y, 20, 20, button -> adjuster.adjust(-fineStep)));
        this.addButton(ModWidgets.button(ModText.literal("+"), centerX + 28, y, 20, 20, button -> adjuster.adjust(fineStep)));
        this.addButton(ModWidgets.button(ModText.literal("+"), centerX + 50, y, 20, 20, button -> adjuster.adjust(step)));
        this.axisRows.add(new AxisRow(label, value, y));
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
        guiGraphics.drawString(this.font, this.title, LEFT_X, TOP - 20, 0xFFFFFFFF);
        guiGraphics.drawString(this.font, ModText.translatable("gui.mineturismo.under_neon_adjust.color", this.colorName()),
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
            drawFittedAxisLabel(guiGraphics, String.format("%s: %s", row.label(), AxisValueFormat.format(row.value().getAsDouble())), this.clusterCenterX, row.y() + 6);
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
import com.examplebett.compat.ModCheckbox;
import com.examplebett.compat.ModWidgets;
import com.examplebett.compat.ModText;

import com.examplebett.compat.ModClientNetworking;
import com.examplebett.AiObjectMod;
import com.examplebett.client.MenuCameraOrbit;
import com.examplebett.entity.CarEntity;
import com.examplebett.network.SetCarUnderNeonPayload;
import com.examplebett.render.UnderNeon;

import com.examplebett.compat.GuiGraphics;
import com.examplebett.compat.v114.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleSupplier;

public class UnderNeonAdjustScreen extends Screen implements CarCustomizationScreen, CarCameraOrbitScreen {

    private static final float POSITION_STEP = 0.05f;
    private static final float ANGLE_STEP = 5f;
    private static final float SIZE_STEP = 0.1f;
    private static final float BRIGHTNESS_STEP = 0.1f;
    private static final float FINE_STEP_DIVISOR = 5f;
    private static final int RIGHT_MARGIN = 20;
    private static final int CLUSTER_HALF_WIDTH = 70;
    private static final int AXIS_LABEL_MAX_WIDTH = 56;
    private static final int LEFT_X = 20;
    private static final int TOP = 30;
    private static final int ROW = 24;

    private static final int SWATCH_SIZE = 14;
    private static final int SWATCH_GAP = 2;
    private static final int SWATCH_COLUMNS = 9;

    private final CarEntity car;

    private final Screen wrenchScreen;
    private final int slotIndex;
    private UnderNeon.Slot slot;

    private int clusterCenterX;
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

    public UnderNeonAdjustScreen(CarEntity car, Screen wrenchScreen, int slotIndex) {
        super(ModText.translatable("gui.mineturismo.under_neon_adjust.title",
                ModText.translatable("gui.mineturismo.under_neon.slot_" + UnderNeon.SLOT_IDS[slotIndex])));
        this.car = car;
        this.wrenchScreen = wrenchScreen;
        this.slotIndex = slotIndex;
        this.slot = car.getUnderNeon(slotIndex);
    }

    private void apply(UnderNeon.Slot updated) {
        this.slot = updated;
        this.car.setUnderNeon(this.slotIndex, updated);
        SetCarUnderNeonPayload mineturismo$payload = new SetCarUnderNeonPayload(this.car.getId(), UnderNeon.encodeLine(this.slotIndex, updated));
        ModClientNetworking.sendToServer(SetCarUnderNeonPayload.ID, mineturismo$payload, SetCarUnderNeonPayload::write);
    }

    private void applyAdjust(UnderNeon.Slot updated) {
        this.apply(updated);
        AiObjectMod.LOGGER.debug("under neon adjust [{}] slot={} {}", this.car.getModelId(), UnderNeon.SLOT_IDS[this.slotIndex], updated);
    }

    @Override
    protected void init() {
        this.clusterCenterX = this.width - RIGHT_MARGIN - CLUSTER_HALF_WIDTH;
        this.palette = CarColorScreen.palette();

        int y = TOP;
        this.addButton(ModCheckbox.builder(ModText.translatable("gui.mineturismo.under_neon_adjust.enabled"), this.font)
                .pos(LEFT_X, y)
                .selected(this.slot.enabled())
                .onValueChange((checkbox, selected) -> this.apply(this.slot.withEnabled(selected)))
                .build());
        y += ROW;

        this.colorLabelY = y;
        int swatchTop = y + 12;
        this.swatches.clear();
        for (int i = 0; i < this.palette.size(); i++) {
            int rgb = this.palette.get(i).rgb();
            int sx = LEFT_X + (i % SWATCH_COLUMNS) * (SWATCH_SIZE + SWATCH_GAP);
            int sy = swatchTop + (i / SWATCH_COLUMNS) * (SWATCH_SIZE + SWATCH_GAP);
            this.addButton(ModWidgets.button(ModText.literal(""), sx, sy, SWATCH_SIZE, SWATCH_SIZE, b -> this.apply(this.slot.withColor(rgb))));
            this.swatches.add(new Swatch(sx, sy, rgb));
        }

        this.axisRows.clear();
        int rowY = TOP;
        int rowHeight = 20;
        addAxisRow(rowY, "X", () -> this.slot.x(), POSITION_STEP,
                d -> this.applyAdjust(this.slot.withPosition(this.slot.x() + d, this.slot.y(), this.slot.z())));
        addAxisRow(rowY += rowHeight, "Y", () -> this.slot.y(), POSITION_STEP,
                d -> this.applyAdjust(this.slot.withPosition(this.slot.x(), this.slot.y() + d, this.slot.z())));
        addAxisRow(rowY += rowHeight, "Z", () -> this.slot.z(), POSITION_STEP,
                d -> this.applyAdjust(this.slot.withPosition(this.slot.x(), this.slot.y(), this.slot.z() + d)));
        addAxisRow(rowY += rowHeight, "Yaw", () -> this.slot.yaw(), ANGLE_STEP,
                d -> this.applyAdjust(this.slot.withYaw(this.slot.yaw() + d)));
        addAxisRow(rowY += rowHeight, "Width", () -> this.slot.width(), SIZE_STEP,
                d -> this.applyAdjust(this.slot.withSize(this.slot.width() + d, this.slot.length())));
        addAxisRow(rowY += rowHeight, "Length", () -> this.slot.length(), SIZE_STEP,
                d -> this.applyAdjust(this.slot.withSize(this.slot.width(), this.slot.length() + d)));
        addAxisRow(rowY += rowHeight, "Bright", () -> this.slot.brightness(), BRIGHTNESS_STEP,
                d -> this.applyAdjust(this.slot.withBrightness(this.slot.brightness() + d)));
        rowY += rowHeight + 6;

        int rightEdge = this.width - RIGHT_MARGIN;
        int halfWidth = (CLUSTER_HALF_WIDTH * 2 - 4) / 2;
        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.under_neon_adjust.reset_transform"),
                rightEdge - CLUSTER_HALF_WIDTH * 2, rowY, halfWidth, 20, b -> this.resetTransform()));
        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.back"),
                rightEdge - halfWidth, rowY, halfWidth, 20, b ->
                        this.minecraft.setScreen(new UnderNeonScreen(this.car, this.wrenchScreen))));
    }

    private void resetTransform() {
        UnderNeon.Slot defaults = this.car.getUnderNeonDefault(this.slotIndex);
        this.applyAdjust(this.slot.withPosition(defaults.x(), defaults.y(), defaults.z())
                .withYaw(defaults.yaw())
                .withSize(defaults.width(), defaults.length())
                .withBrightness(defaults.brightness()));
    }

    private void addAxisRow(int y, String label, DoubleSupplier value, float step, AxisAdjuster adjuster) {
        int centerX = this.clusterCenterX;

        float fineStep = Math.abs(step / FINE_STEP_DIVISOR - 0.01f) < 1e-6f ? 0.002f : step / FINE_STEP_DIVISOR;
        this.addButton(ModWidgets.button(ModText.literal("-"), centerX - 70, y, 20, 20, button -> adjuster.adjust(-step)));
        this.addButton(ModWidgets.button(ModText.literal("-"), centerX - 48, y, 20, 20, button -> adjuster.adjust(-fineStep)));
        this.addButton(ModWidgets.button(ModText.literal("+"), centerX + 28, y, 20, 20, button -> adjuster.adjust(fineStep)));
        this.addButton(ModWidgets.button(ModText.literal("+"), centerX + 50, y, 20, 20, button -> adjuster.adjust(step)));
        this.axisRows.add(new AxisRow(label, value, y));
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
        guiGraphics.drawString(this.font, this.title, LEFT_X, TOP - 20, 0xFFFFFFFF);
        guiGraphics.drawString(this.font, ModText.translatable("gui.mineturismo.under_neon_adjust.color", this.colorName()),
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
            drawFittedAxisLabel(guiGraphics, String.format("%s: %s", row.label(), AxisValueFormat.format(row.value().getAsDouble())), this.clusterCenterX, row.y() + 6);
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
