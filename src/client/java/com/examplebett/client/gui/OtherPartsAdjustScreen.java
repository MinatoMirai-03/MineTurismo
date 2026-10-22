package com.examplebett.client.gui;
import com.examplebett.compat.ModCheckbox;
import com.examplebett.compat.ModWidgets;
import com.examplebett.compat.ModText;

import com.examplebett.compat.ModClientNetworking;
import com.examplebett.AiObjectMod;
import com.examplebett.client.MenuCameraOrbit;
import com.examplebett.entity.CarEntity;
import com.examplebett.network.SetCarExtraRearAxlePayload;
import com.examplebett.network.SetCarOtherPartPayload;
import com.examplebett.render.OtherParts;
import com.examplebett.render.PartTransforms;
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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class OtherPartsAdjustScreen extends Screen implements CarCustomizationScreen, CarCameraOrbitScreen {

    private static final float POSITION_STEP = 0.05f;
    private static final float ANGLE_STEP = 1f;
    private static final float SCALE_STEP = 0.05f;
    private static final int RIGHT_MARGIN = 20;
    private static final int CLUSTER_HALF_WIDTH = 70;
    private static final int AXIS_LABEL_MAX_WIDTH = 56;
    private static final int BUTTON_HORIZONTAL_PADDING = 16;
    private static final int MIN_BUTTON_WIDTH = 60;

    private static final int PART_ROW_HEIGHT = 24;
    private static final int PART_VISIBLE_ROWS = 5;

    private final CarEntity car;
    private final Screen parent;
    private final int initialScrollRow;
    private int clusterCenterX;
    private int contentTop;

    private boolean modelMissing;

    public OtherPartsAdjustScreen(CarEntity car, Screen parent) {
        this(car, parent, 0);
    }

    public OtherPartsAdjustScreen(CarEntity car, Screen parent, int initialScrollRow) {
        super(ModText.translatable("gui.mineturismo.other_parts_adjust.title"));
        this.car = car;
        this.parent = parent;
        this.initialScrollRow = initialScrollRow;
    }

    private int fitWidth(Component label) {
        return Math.max(MIN_BUTTON_WIDTH, this.font.width(label) + BUTTON_HORIZONTAL_PADDING);
    }

    private static Set<String> parseIds(String csv) {
        Set<String> ids = new LinkedHashSet<>();
        if (!csv.isEmpty()) {
            for (String id : csv.split(",")) {
                ids.add(id);
            }
        }
        return ids;
    }

    private void applyAdjust(String enabledIds, String partId, PartTransforms.Transform12 t) {
        this.car.setOtherPartEnabledIds(enabledIds);
        this.car.setOtherPartTransform(partId, t);
        SetCarOtherPartPayload mineturismo$payload = new SetCarOtherPartPayload(this.car.getId(), enabledIds, partId, t.x(), t.y(), t.z(), t.angle(), t.angleY(), t.scale(),
                t.stretchXPos(), t.stretchXNeg(), t.stretchYPos(), t.stretchYNeg(), t.stretchZPos(), t.stretchZNeg());
        //? if >=1.20.5 {
        ModClientNetworking.sendToServer(mineturismo$payload);
        //?} else {
        /*ModClientNetworking.sendToServer(SetCarOtherPartPayload.ID, mineturismo$payload, SetCarOtherPartPayload::write);*/
        //?}

        AiObjectMod.LOGGER.debug("other part adjust [{}] enabledIds={} partId={} {}", this.car.getModelId(), enabledIds, partId, t);
        this.minecraft.setScreen(new OtherPartsAdjustScreen(this.car, this.parent, this.partScrollRow));
    }

    private void applyExtraRearAxle(int axle, boolean enabled, float zOffset) {
        if (axle == 2) {
            this.car.setExtraRearAxle2(enabled, zOffset);
        } else {
            this.car.setExtraRearAxle(enabled, zOffset);
        }
        SetCarExtraRearAxlePayload mineturismo$payload = new SetCarExtraRearAxlePayload(this.car.getId(), axle, enabled, zOffset);
        //? if >=1.20.5 {
        ModClientNetworking.sendToServer(mineturismo$payload);
        //?} else {
        /*ModClientNetworking.sendToServer(SetCarExtraRearAxlePayload.ID, mineturismo$payload, SetCarExtraRearAxlePayload::write);*/
        //?}

        AiObjectMod.LOGGER.debug("extra rear axle adjust [{}] axle={} enabled={} zOffset={}",
                this.car.getModelId(), axle, enabled, zOffset);
        this.minecraft.setScreen(new OtherPartsAdjustScreen(this.car, this.parent, this.partScrollRow));
    }

    private void applyToggle(String id, boolean selected) {
        Set<String> ids = parseIds(this.car.getOtherPartEnabledIds());
        if (selected) {
            ids.add(id);
        } else {
            ids.remove(id);
        }
        applyAdjust(String.join(",", ids), id, this.car.getOtherPartTransform(id));
    }

    private final List<PartCheckboxEntry> partCheckboxes = new ArrayList<>();
    private int partScrollRow = 0;
    private int partMaxScrollRow = 0;
    private int partViewportY;

    private record PartCheckboxEntry(ModCheckbox checkbox, int row, boolean usable) {
    }

    private void updatePartCheckboxPositions() {
        int viewportTop = this.partViewportY;
        for (PartCheckboxEntry entry : this.partCheckboxes) {
            int visibleRow = entry.row() - this.partScrollRow;
            boolean isVisible = visibleRow >= 0 && visibleRow < PART_VISIBLE_ROWS;
            entry.checkbox().visible = isVisible;
            entry.checkbox().active = isVisible && entry.usable();
            if (isVisible) {
                ModWidgets.setY(entry.checkbox(), viewportTop + visibleRow * PART_ROW_HEIGHT);
            }
        }
    }

    @Override
    protected void init() {
        this.clusterCenterX = this.width - RIGHT_MARGIN - CLUSTER_HALF_WIDTH;

        int top = 30;
        this.contentTop = top;

        List<OtherParts.Option> parts = OtherParts.list(this.minecraft.getResourceManager());

        this.modelMissing = this.isAnyCustomPartModelMissing(this.minecraft.getResourceManager(),
                parts.stream().filter(OtherParts::usesObjModel).map(OtherParts.Option::modelId).toList());

        Set<String> enabledIds = parseIds(this.car.getOtherPartEnabledIds());

        this.partCheckboxes.clear();
        this.partMaxScrollRow = Math.max(0, parts.size() - PART_VISIBLE_ROWS);
        this.partScrollRow = Math.max(0, Math.min(this.partMaxScrollRow, this.initialScrollRow));

        int arrowX = 20;

        int checklistTop = top + 24;
        this.partViewportY = checklistTop;
        for (int i = 0; i < parts.size(); i++) {
            OtherParts.Option part = parts.get(i);
            boolean selected = enabledIds.contains(part.id());
            ModCheckbox checkbox = ModCheckbox.builder(part.displayName(), this.font)
                    .pos(arrowX, checklistTop + i * PART_ROW_HEIGHT)
                    .selected(selected)
                    .onValueChange((cb, newSelected) -> applyToggle(part.id(), newSelected))
                    .build();
            this.addRenderableWidget(checkbox);

            boolean usable = !OtherParts.SPARE_TIRE_ID.equals(part.id()) || this.car.getWheelAttachment() != null;
            this.partCheckboxes.add(new PartCheckboxEntry(checkbox, i, usable));
        }

        int visibleRows = Math.min(parts.size(), PART_VISIBLE_ROWS);
        int viewportHeight = visibleRows * PART_ROW_HEIGHT;

        int extraAxleRowY = checklistTop + viewportHeight;
        boolean extraRearAxleEnabled = this.car.isExtraRearAxleEnabled();
        float extraRearAxleZOffset = this.car.getExtraRearAxleZOffset();

        boolean wheelPositionsKnown = this.car.hasWheelPositions();
        ModCheckbox extraAxleCheckbox = this.addRenderableWidget(ModCheckbox.builder(ModText.translatable("gui.mineturismo.other_parts_adjust.extra_rear_axle"), this.font)
                .pos(arrowX, extraAxleRowY)
                .selected(extraRearAxleEnabled)
                .onValueChange((checkbox, newSelected) -> applyExtraRearAxle(1, newSelected, extraRearAxleZOffset))
                .build());
        extraAxleCheckbox.active = wheelPositionsKnown;

        int extraAxle2RowY = extraAxleRowY + PART_ROW_HEIGHT;
        boolean extraRearAxle2Enabled = this.car.isExtraRearAxle2Enabled();
        float extraRearAxle2ZOffset = this.car.getExtraRearAxle2ZOffset();
        ModCheckbox extraAxle2Checkbox = this.addRenderableWidget(ModCheckbox.builder(ModText.translatable("gui.mineturismo.other_parts_adjust.extra_rear_axle_2"), this.font)
                .pos(arrowX, extraAxle2RowY)
                .selected(extraRearAxle2Enabled)
                .onValueChange((checkbox, newSelected) -> applyExtraRearAxle(2, newSelected, extraRearAxle2ZOffset))
                .build());
        extraAxle2Checkbox.active = wheelPositionsKnown;

        int downArrowY = extraAxle2RowY + PART_ROW_HEIGHT + 4;

        this.addRenderableWidget(new ScrollbarWidget(arrowX - 12, checklistTop,
                viewportHeight, PART_VISIBLE_ROWS, this.partMaxScrollRow + PART_VISIBLE_ROWS, () -> this.partScrollRow, row -> {
                    this.partScrollRow = row;
                    this.updatePartCheckboxPositions();
                }));
        this.updatePartCheckboxPositions();
        int leftColumnEndY = downArrowY + 20;

        this.axisLabels.clear();
        this.groupHeaders.clear();
        int rowHeight = 20;

        int rowY = top;

        List<OtherParts.Option> enabledParts = parts.stream().filter(p -> enabledIds.contains(p.id())).toList();
        for (OtherParts.Option part : enabledParts) {
            PartTransforms.Transform12 t = this.car.getOtherPartTransform(part.id());
            this.groupHeaders.add(new GroupHeader(part.displayName().getString(), rowY));
            int groupTop = rowY + 16;
            String id = part.id();
            addAxisRow(groupTop, "X", t.x(), POSITION_STEP,
                    delta -> applyAdjust(String.join(",", enabledIds), id, new PartTransforms.Transform12(t.x() + delta, t.y(), t.z(), t.angle(), t.angleY(), t.scale(),
                            t.stretchXPos(), t.stretchXNeg(), t.stretchYPos(), t.stretchYNeg(), t.stretchZPos(), t.stretchZNeg())));
            addAxisRow(groupTop + rowHeight, "Y", t.y(), POSITION_STEP,
                    delta -> applyAdjust(String.join(",", enabledIds), id, new PartTransforms.Transform12(t.x(), t.y() + delta, t.z(), t.angle(), t.angleY(), t.scale(),
                            t.stretchXPos(), t.stretchXNeg(), t.stretchYPos(), t.stretchYNeg(), t.stretchZPos(), t.stretchZNeg())));
            addAxisRow(groupTop + rowHeight * 2, "Z", t.z(), POSITION_STEP,
                    delta -> applyAdjust(String.join(",", enabledIds), id, new PartTransforms.Transform12(t.x(), t.y(), t.z() + delta, t.angle(), t.angleY(), t.scale(),
                            t.stretchXPos(), t.stretchXNeg(), t.stretchYPos(), t.stretchYNeg(), t.stretchZPos(), t.stretchZNeg())));
            addAxisRow(groupTop + rowHeight * 3, "Angle", t.angle(), ANGLE_STEP,
                    delta -> applyAdjust(String.join(",", enabledIds), id, new PartTransforms.Transform12(t.x(), t.y(), t.z(), t.angle() + delta, t.angleY(), t.scale(),
                            t.stretchXPos(), t.stretchXNeg(), t.stretchYPos(), t.stretchYNeg(), t.stretchZPos(), t.stretchZNeg())));
            addAxisRow(groupTop + rowHeight * 4, "Angle Y", t.angleY(), ANGLE_STEP,
                    delta -> applyAdjust(String.join(",", enabledIds), id, new PartTransforms.Transform12(t.x(), t.y(), t.z(), t.angle(), t.angleY() + delta, t.scale(),
                            t.stretchXPos(), t.stretchXNeg(), t.stretchYPos(), t.stretchYNeg(), t.stretchZPos(), t.stretchZNeg())));
            addAxisRow(groupTop + rowHeight * 5, "Scale", t.scale(), SCALE_STEP,
                    delta -> applyAdjust(String.join(",", enabledIds), id, new PartTransforms.Transform12(t.x(), t.y(), t.z(), t.angle(), t.angleY(), t.scale() + delta,
                            t.stretchXPos(), t.stretchXNeg(), t.stretchYPos(), t.stretchYNeg(), t.stretchZPos(), t.stretchZNeg())));

            addAxisRow(groupTop + rowHeight * 6, "Stretch X", t.stretchXPos(), SCALE_STEP,
                    delta -> applyAdjust(String.join(",", enabledIds), id, new PartTransforms.Transform12(t.x(), t.y(), t.z(), t.angle(), t.angleY(), t.scale(),
                            t.stretchXPos() + delta, t.stretchXNeg() + delta, t.stretchYPos(), t.stretchYNeg(), t.stretchZPos(), t.stretchZNeg())));
            addAxisRow(groupTop + rowHeight * 7, "Stretch Y", t.stretchYPos(), SCALE_STEP,
                    delta -> applyAdjust(String.join(",", enabledIds), id, new PartTransforms.Transform12(t.x(), t.y(), t.z(), t.angle(), t.angleY(), t.scale(),
                            t.stretchXPos(), t.stretchXNeg(), t.stretchYPos() + delta, t.stretchYNeg() + delta, t.stretchZPos(), t.stretchZNeg())));
            addAxisRow(groupTop + rowHeight * 8, "Stretch Z", t.stretchZPos(), SCALE_STEP,
                    delta -> applyAdjust(String.join(",", enabledIds), id, new PartTransforms.Transform12(t.x(), t.y(), t.z(), t.angle(), t.angleY(), t.scale(),
                            t.stretchXPos(), t.stretchXNeg(), t.stretchYPos(), t.stretchYNeg(), t.stretchZPos() + delta, t.stretchZNeg() + delta)));
            rowY = groupTop + rowHeight * 9 + 12;
        }

        addAxisRow(rowY, "Axle Z (5-6)", extraRearAxleZOffset, POSITION_STEP,
                delta -> applyExtraRearAxle(1, extraRearAxleEnabled, extraRearAxleZOffset + delta));
        rowY += rowHeight;
        addAxisRow(rowY, "Axle Z (7-8)", extraRearAxle2ZOffset, POSITION_STEP,
                delta -> applyExtraRearAxle(2, extraRearAxle2Enabled, extraRearAxle2ZOffset + delta));
        rowY += rowHeight;

        int rightEdge = this.width - RIGHT_MARGIN;

        int backY = Math.max(leftColumnEndY, rowY) + 8;
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

    private final List<GroupHeader> groupHeaders = new ArrayList<>();

    private record GroupHeader(String label, int y) {
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
        if (this.partMaxScrollRow > 0 && scrollY != 0) {
            int delta = scrollY > 0 ? -1 : 1;
            int newScrollRow = Math.max(0, Math.min(this.partMaxScrollRow, this.partScrollRow + delta));
            if (newScrollRow != this.partScrollRow) {
                this.partScrollRow = newScrollRow;
                this.updatePartCheckboxPositions();
                return true;
            }
        }
        //? if >=1.20.2 {
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        //?} else {
        /*return super.mouseScrolled(mouseX, mouseY, scrollY);*/
        //?}
    }

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
        if (this.modelMissing) {
            this.renderCustomPartModelMissingNotice(guiGraphics, this.font, this.width);
        }
        guiGraphics.drawCenteredString(this.font, this.title, this.clusterCenterX, this.contentTop - 20, 0xFFFFFFFF);
        for (GroupHeader header : this.groupHeaders) {
            guiGraphics.drawCenteredString(this.font, header.label(), this.clusterCenterX, header.y(), 0xFFFFFFFF);
        }
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
