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
import com.examplebett.network.SetCarRearBrakeCaliperPayload;
import com.examplebett.render.PartTransforms;
import com.examplebett.render.RearBrakeCaliperParts;

import com.examplebett.compat.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class RearBrakeCaliperAdjustScreen extends Screen implements CarCustomizationScreen, CarCameraOrbitScreen {

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

    public RearBrakeCaliperAdjustScreen(CarEntity car, Screen parent) {
        this(car, parent, 0);
    }

    public RearBrakeCaliperAdjustScreen(CarEntity car, Screen parent, int initialScrollRow) {
        super(ModText.translatable("gui.mineturismo.rear_brake_caliper_adjust.title"));
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

    private void applyAdjust(String enabledIds, String partId, PartTransforms.Transform6 t) {
        this.car.setRearBrakeCaliperEnabledIds(enabledIds);
        this.car.setRearBrakeCaliperTransform(partId, t);
        SetCarRearBrakeCaliperPayload mineturismo$payload = new SetCarRearBrakeCaliperPayload(this.car.getId(), enabledIds, partId, t.x(), t.y(), t.z(), t.angle(), t.angleY(), t.scale());
        ModClientNetworking.sendToServer(SetCarRearBrakeCaliperPayload.ID, mineturismo$payload, SetCarRearBrakeCaliperPayload::write);
        AiObjectMod.LOGGER.debug("rear brake caliper adjust [{}] enabledIds={} partId={} {}", this.car.getModelId(), enabledIds, partId, t);
        this.minecraft.setScreen(new RearBrakeCaliperAdjustScreen(this.car, this.parent, this.partScrollRow));
    }

    private void applyToggle(String id, boolean selected) {
        Set<String> ids = parseIds(this.car.getRearBrakeCaliperEnabledIds());
        if (selected) {
            ids.add(id);
        } else {
            ids.remove(id);
        }
        applyAdjust(String.join(",", ids), id, this.car.getRearBrakeCaliperTransform(id));
    }

    private final List<PartCheckboxEntry> partCheckboxes = new ArrayList<>();
    private int partScrollRow = 0;
    private int partMaxScrollRow = 0;
    private int partViewportY;

    private record PartCheckboxEntry(ModCheckbox checkbox, int row) {
    }

    private void updatePartCheckboxPositions() {
        int viewportTop = this.partViewportY;
        for (PartCheckboxEntry entry : this.partCheckboxes) {
            int visibleRow = entry.row() - this.partScrollRow;
            boolean isVisible = visibleRow >= 0 && visibleRow < PART_VISIBLE_ROWS;
            entry.checkbox().visible = isVisible;
            entry.checkbox().active = isVisible;
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

        List<RearBrakeCaliperParts.Option> parts = RearBrakeCaliperParts.list(this.minecraft.getResourceManager());
        this.modelMissing = this.isAnyCustomPartModelMissing(this.minecraft.getResourceManager(),
                parts.stream().map(RearBrakeCaliperParts.Option::modelId).toList());

        Set<String> enabledIds = parseIds(this.car.getRearBrakeCaliperEnabledIds());

        this.partCheckboxes.clear();
        this.partMaxScrollRow = Math.max(0, parts.size() - PART_VISIBLE_ROWS);
        this.partScrollRow = Math.max(0, Math.min(this.partMaxScrollRow, this.initialScrollRow));

        int arrowX = 20;

        int checklistTop = top + 24;
        this.partViewportY = checklistTop;
        for (int i = 0; i < parts.size(); i++) {
            RearBrakeCaliperParts.Option part = parts.get(i);
            boolean selected = enabledIds.contains(part.id());
            ModCheckbox checkbox = ModCheckbox.builder(part.displayName(), this.font)
                    .pos(arrowX, checklistTop + i * PART_ROW_HEIGHT)
                    .selected(selected)
                    .onValueChange((cb, newSelected) -> applyToggle(part.id(), newSelected))
                    .build();
            this.addRenderableWidget(checkbox);
            this.partCheckboxes.add(new PartCheckboxEntry(checkbox, i));
        }

        int visibleRows = Math.min(parts.size(), PART_VISIBLE_ROWS);
        int viewportHeight = visibleRows * PART_ROW_HEIGHT;
        int downArrowY = checklistTop + viewportHeight + 4;

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

        List<RearBrakeCaliperParts.Option> enabledParts = parts.stream().filter(p -> enabledIds.contains(p.id())).toList();
        for (RearBrakeCaliperParts.Option part : enabledParts) {
            PartTransforms.Transform6 t = this.car.getRearBrakeCaliperTransform(part.id());
            this.groupHeaders.add(new GroupHeader(part.displayName().getString(), rowY));
            int groupTop = rowY + 16;
            String id = part.id();
            addAxisRow(groupTop, "X", t.x(), POSITION_STEP,
                    delta -> applyAdjust(String.join(",", enabledIds), id, new PartTransforms.Transform6(t.x() + delta, t.y(), t.z(), t.angle(), t.angleY(), t.scale())));
            addAxisRow(groupTop + rowHeight, "Y", t.y(), POSITION_STEP,
                    delta -> applyAdjust(String.join(",", enabledIds), id, new PartTransforms.Transform6(t.x(), t.y() + delta, t.z(), t.angle(), t.angleY(), t.scale())));
            addAxisRow(groupTop + rowHeight * 2, "Z", t.z(), POSITION_STEP,
                    delta -> applyAdjust(String.join(",", enabledIds), id, new PartTransforms.Transform6(t.x(), t.y(), t.z() + delta, t.angle(), t.angleY(), t.scale())));
            addAxisRow(groupTop + rowHeight * 3, "Angle", t.angle(), ANGLE_STEP,
                    delta -> applyAdjust(String.join(",", enabledIds), id, new PartTransforms.Transform6(t.x(), t.y(), t.z(), t.angle() + delta, t.angleY(), t.scale())));
            addAxisRow(groupTop + rowHeight * 4, "Angle Y", t.angleY(), ANGLE_STEP,
                    delta -> applyAdjust(String.join(",", enabledIds), id, new PartTransforms.Transform6(t.x(), t.y(), t.z(), t.angle(), t.angleY() + delta, t.scale())));
            addAxisRow(groupTop + rowHeight * 5, "Scale", t.scale(), SCALE_STEP,
                    delta -> applyAdjust(String.join(",", enabledIds), id, new PartTransforms.Transform6(t.x(), t.y(), t.z(), t.angle(), t.angleY(), t.scale() + delta)));
            rowY = groupTop + rowHeight * 6 + 12;
        }

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
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollY) {
        return mineturismo$mouseScrolled(mouseX, mouseY, 0, scrollY);
    }

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
        return super.mouseScrolled(mouseX, mouseY, scrollY);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        mineturismo$renderScreen(new GuiGraphics(poseStack), mouseX, mouseY, partialTick);
    }

    private void mineturismo$renderScreen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        super.render(guiGraphics.pose(), mouseX, mouseY, partialTick);
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
import com.examplebett.network.SetCarRearBrakeCaliperPayload;
import com.examplebett.render.PartTransforms;
import com.examplebett.render.RearBrakeCaliperParts;

import com.examplebett.compat.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class RearBrakeCaliperAdjustScreen extends Screen implements CarCustomizationScreen, CarCameraOrbitScreen {

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

    public RearBrakeCaliperAdjustScreen(CarEntity car, Screen parent) {
        this(car, parent, 0);
    }

    public RearBrakeCaliperAdjustScreen(CarEntity car, Screen parent, int initialScrollRow) {
        super(ModText.translatable("gui.mineturismo.rear_brake_caliper_adjust.title"));
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

    private void applyAdjust(String enabledIds, String partId, PartTransforms.Transform6 t) {
        this.car.setRearBrakeCaliperEnabledIds(enabledIds);
        this.car.setRearBrakeCaliperTransform(partId, t);
        SetCarRearBrakeCaliperPayload mineturismo$payload = new SetCarRearBrakeCaliperPayload(this.car.getId(), enabledIds, partId, t.x(), t.y(), t.z(), t.angle(), t.angleY(), t.scale());
        ModClientNetworking.sendToServer(SetCarRearBrakeCaliperPayload.ID, mineturismo$payload, SetCarRearBrakeCaliperPayload::write);
        AiObjectMod.LOGGER.debug("rear brake caliper adjust [{}] enabledIds={} partId={} {}", this.car.getModelId(), enabledIds, partId, t);
        this.minecraft.setScreen(new RearBrakeCaliperAdjustScreen(this.car, this.parent, this.partScrollRow));
    }

    private void applyToggle(String id, boolean selected) {
        Set<String> ids = parseIds(this.car.getRearBrakeCaliperEnabledIds());
        if (selected) {
            ids.add(id);
        } else {
            ids.remove(id);
        }
        applyAdjust(String.join(",", ids), id, this.car.getRearBrakeCaliperTransform(id));
    }

    private final List<PartCheckboxEntry> partCheckboxes = new ArrayList<>();
    private int partScrollRow = 0;
    private int partMaxScrollRow = 0;
    private int partViewportY;

    private record PartCheckboxEntry(ModCheckbox checkbox, int row) {
    }

    private void updatePartCheckboxPositions() {
        int viewportTop = this.partViewportY;
        for (PartCheckboxEntry entry : this.partCheckboxes) {
            int visibleRow = entry.row() - this.partScrollRow;
            boolean isVisible = visibleRow >= 0 && visibleRow < PART_VISIBLE_ROWS;
            entry.checkbox().visible = isVisible;
            entry.checkbox().active = isVisible;
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

        List<RearBrakeCaliperParts.Option> parts = RearBrakeCaliperParts.list(this.minecraft.getResourceManager());
        this.modelMissing = this.isAnyCustomPartModelMissing(this.minecraft.getResourceManager(),
                parts.stream().map(RearBrakeCaliperParts.Option::modelId).toList());

        Set<String> enabledIds = parseIds(this.car.getRearBrakeCaliperEnabledIds());

        this.partCheckboxes.clear();
        this.partMaxScrollRow = Math.max(0, parts.size() - PART_VISIBLE_ROWS);
        this.partScrollRow = Math.max(0, Math.min(this.partMaxScrollRow, this.initialScrollRow));

        int arrowX = 20;

        int checklistTop = top + 24;
        this.partViewportY = checklistTop;
        for (int i = 0; i < parts.size(); i++) {
            RearBrakeCaliperParts.Option part = parts.get(i);
            boolean selected = enabledIds.contains(part.id());
            ModCheckbox checkbox = ModCheckbox.builder(part.displayName(), this.font)
                    .pos(arrowX, checklistTop + i * PART_ROW_HEIGHT)
                    .selected(selected)
                    .onValueChange((cb, newSelected) -> applyToggle(part.id(), newSelected))
                    .build();
            this.addButton(checkbox);
            this.partCheckboxes.add(new PartCheckboxEntry(checkbox, i));
        }

        int visibleRows = Math.min(parts.size(), PART_VISIBLE_ROWS);
        int viewportHeight = visibleRows * PART_ROW_HEIGHT;
        int downArrowY = checklistTop + viewportHeight + 4;

        this.addButton(new ScrollbarWidget(arrowX - 12, checklistTop,
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

        List<RearBrakeCaliperParts.Option> enabledParts = parts.stream().filter(p -> enabledIds.contains(p.id())).toList();
        for (RearBrakeCaliperParts.Option part : enabledParts) {
            PartTransforms.Transform6 t = this.car.getRearBrakeCaliperTransform(part.id());
            this.groupHeaders.add(new GroupHeader(part.displayName().getString(), rowY));
            int groupTop = rowY + 16;
            String id = part.id();
            addAxisRow(groupTop, "X", t.x(), POSITION_STEP,
                    delta -> applyAdjust(String.join(",", enabledIds), id, new PartTransforms.Transform6(t.x() + delta, t.y(), t.z(), t.angle(), t.angleY(), t.scale())));
            addAxisRow(groupTop + rowHeight, "Y", t.y(), POSITION_STEP,
                    delta -> applyAdjust(String.join(",", enabledIds), id, new PartTransforms.Transform6(t.x(), t.y() + delta, t.z(), t.angle(), t.angleY(), t.scale())));
            addAxisRow(groupTop + rowHeight * 2, "Z", t.z(), POSITION_STEP,
                    delta -> applyAdjust(String.join(",", enabledIds), id, new PartTransforms.Transform6(t.x(), t.y(), t.z() + delta, t.angle(), t.angleY(), t.scale())));
            addAxisRow(groupTop + rowHeight * 3, "Angle", t.angle(), ANGLE_STEP,
                    delta -> applyAdjust(String.join(",", enabledIds), id, new PartTransforms.Transform6(t.x(), t.y(), t.z(), t.angle() + delta, t.angleY(), t.scale())));
            addAxisRow(groupTop + rowHeight * 4, "Angle Y", t.angleY(), ANGLE_STEP,
                    delta -> applyAdjust(String.join(",", enabledIds), id, new PartTransforms.Transform6(t.x(), t.y(), t.z(), t.angle(), t.angleY() + delta, t.scale())));
            addAxisRow(groupTop + rowHeight * 5, "Scale", t.scale(), SCALE_STEP,
                    delta -> applyAdjust(String.join(",", enabledIds), id, new PartTransforms.Transform6(t.x(), t.y(), t.z(), t.angle(), t.angleY(), t.scale() + delta)));
            rowY = groupTop + rowHeight * 6 + 12;
        }

        int rightEdge = this.width - RIGHT_MARGIN;

        int backY = Math.max(leftColumnEndY, rowY) + 8;
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

    private final List<GroupHeader> groupHeaders = new ArrayList<>();

    private record GroupHeader(String label, int y) {
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
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollY) {
        return mineturismo$mouseScrolled(mouseX, mouseY, 0, scrollY);
    }

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
        return super.mouseScrolled(mouseX, mouseY, scrollY);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        mineturismo$renderScreen(new GuiGraphics(poseStack), mouseX, mouseY, partialTick);
    }

    private void mineturismo$renderScreen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        super.render(guiGraphics.pose(), mouseX, mouseY, partialTick);
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
import com.examplebett.network.SetCarRearBrakeCaliperPayload;
import com.examplebett.render.PartTransforms;
import com.examplebett.render.RearBrakeCaliperParts;

import com.examplebett.compat.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class RearBrakeCaliperAdjustScreen extends Screen implements CarCustomizationScreen, CarCameraOrbitScreen {

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

    public RearBrakeCaliperAdjustScreen(CarEntity car, Screen parent) {
        this(car, parent, 0);
    }

    public RearBrakeCaliperAdjustScreen(CarEntity car, Screen parent, int initialScrollRow) {
        super(ModText.translatable("gui.mineturismo.rear_brake_caliper_adjust.title"));
        this.car = car;
        this.parent = parent;
        this.initialScrollRow = initialScrollRow;
    }

    private int fitWidth(Component label) {
        return Math.max(MIN_BUTTON_WIDTH, com.examplebett.compat.LegacyGui.width(this.font, label) + BUTTON_HORIZONTAL_PADDING);
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

    private void applyAdjust(String enabledIds, String partId, PartTransforms.Transform6 t) {
        this.car.setRearBrakeCaliperEnabledIds(enabledIds);
        this.car.setRearBrakeCaliperTransform(partId, t);
        SetCarRearBrakeCaliperPayload mineturismo$payload = new SetCarRearBrakeCaliperPayload(this.car.getId(), enabledIds, partId, t.x(), t.y(), t.z(), t.angle(), t.angleY(), t.scale());
        ModClientNetworking.sendToServer(SetCarRearBrakeCaliperPayload.ID, mineturismo$payload, SetCarRearBrakeCaliperPayload::write);
        AiObjectMod.LOGGER.debug("rear brake caliper adjust [{}] enabledIds={} partId={} {}", this.car.getModelId(), enabledIds, partId, t);
        this.minecraft.setScreen(new RearBrakeCaliperAdjustScreen(this.car, this.parent, this.partScrollRow));
    }

    private void applyToggle(String id, boolean selected) {
        Set<String> ids = parseIds(this.car.getRearBrakeCaliperEnabledIds());
        if (selected) {
            ids.add(id);
        } else {
            ids.remove(id);
        }
        applyAdjust(String.join(",", ids), id, this.car.getRearBrakeCaliperTransform(id));
    }

    private final List<PartCheckboxEntry> partCheckboxes = new ArrayList<>();
    private int partScrollRow = 0;
    private int partMaxScrollRow = 0;
    private int partViewportY;

    private record PartCheckboxEntry(ModCheckbox checkbox, int row) {
    }

    private void updatePartCheckboxPositions() {
        int viewportTop = this.partViewportY;
        for (PartCheckboxEntry entry : this.partCheckboxes) {
            int visibleRow = entry.row() - this.partScrollRow;
            boolean isVisible = visibleRow >= 0 && visibleRow < PART_VISIBLE_ROWS;
            entry.checkbox().visible = isVisible;
            entry.checkbox().active = isVisible;
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

        List<RearBrakeCaliperParts.Option> parts = RearBrakeCaliperParts.list(this.minecraft.getResourceManager());
        this.modelMissing = this.isAnyCustomPartModelMissing(this.minecraft.getResourceManager(),
                parts.stream().map(RearBrakeCaliperParts.Option::modelId).toList());

        Set<String> enabledIds = parseIds(this.car.getRearBrakeCaliperEnabledIds());

        this.partCheckboxes.clear();
        this.partMaxScrollRow = Math.max(0, parts.size() - PART_VISIBLE_ROWS);
        this.partScrollRow = Math.max(0, Math.min(this.partMaxScrollRow, this.initialScrollRow));

        int arrowX = 20;

        int checklistTop = top + 24;
        this.partViewportY = checklistTop;
        for (int i = 0; i < parts.size(); i++) {
            RearBrakeCaliperParts.Option part = parts.get(i);
            boolean selected = enabledIds.contains(part.id());
            ModCheckbox checkbox = ModCheckbox.builder(part.displayName(), this.font)
                    .pos(arrowX, checklistTop + i * PART_ROW_HEIGHT)
                    .selected(selected)
                    .onValueChange((cb, newSelected) -> applyToggle(part.id(), newSelected))
                    .build();
            this.addButton(checkbox);
            this.partCheckboxes.add(new PartCheckboxEntry(checkbox, i));
        }

        int visibleRows = Math.min(parts.size(), PART_VISIBLE_ROWS);
        int viewportHeight = visibleRows * PART_ROW_HEIGHT;
        int downArrowY = checklistTop + viewportHeight + 4;

        this.addButton(new ScrollbarWidget(arrowX - 12, checklistTop,
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

        List<RearBrakeCaliperParts.Option> enabledParts = parts.stream().filter(p -> enabledIds.contains(p.id())).toList();
        for (RearBrakeCaliperParts.Option part : enabledParts) {
            PartTransforms.Transform6 t = this.car.getRearBrakeCaliperTransform(part.id());
            this.groupHeaders.add(new GroupHeader(part.displayName().getString(), rowY));
            int groupTop = rowY + 16;
            String id = part.id();
            addAxisRow(groupTop, "X", t.x(), POSITION_STEP,
                    delta -> applyAdjust(String.join(",", enabledIds), id, new PartTransforms.Transform6(t.x() + delta, t.y(), t.z(), t.angle(), t.angleY(), t.scale())));
            addAxisRow(groupTop + rowHeight, "Y", t.y(), POSITION_STEP,
                    delta -> applyAdjust(String.join(",", enabledIds), id, new PartTransforms.Transform6(t.x(), t.y() + delta, t.z(), t.angle(), t.angleY(), t.scale())));
            addAxisRow(groupTop + rowHeight * 2, "Z", t.z(), POSITION_STEP,
                    delta -> applyAdjust(String.join(",", enabledIds), id, new PartTransforms.Transform6(t.x(), t.y(), t.z() + delta, t.angle(), t.angleY(), t.scale())));
            addAxisRow(groupTop + rowHeight * 3, "Angle", t.angle(), ANGLE_STEP,
                    delta -> applyAdjust(String.join(",", enabledIds), id, new PartTransforms.Transform6(t.x(), t.y(), t.z(), t.angle() + delta, t.angleY(), t.scale())));
            addAxisRow(groupTop + rowHeight * 4, "Angle Y", t.angleY(), ANGLE_STEP,
                    delta -> applyAdjust(String.join(",", enabledIds), id, new PartTransforms.Transform6(t.x(), t.y(), t.z(), t.angle(), t.angleY() + delta, t.scale())));
            addAxisRow(groupTop + rowHeight * 5, "Scale", t.scale(), SCALE_STEP,
                    delta -> applyAdjust(String.join(",", enabledIds), id, new PartTransforms.Transform6(t.x(), t.y(), t.z(), t.angle(), t.angleY(), t.scale() + delta)));
            rowY = groupTop + rowHeight * 6 + 12;
        }

        int rightEdge = this.width - RIGHT_MARGIN;

        int backY = Math.max(leftColumnEndY, rowY) + 8;
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

    private final List<GroupHeader> groupHeaders = new ArrayList<>();

    private record GroupHeader(String label, int y) {
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
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollY) {
        return mineturismo$mouseScrolled(mouseX, mouseY, 0, scrollY);
    }

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
        return super.mouseScrolled(mouseX, mouseY, scrollY);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTick) {
        mineturismo$renderScreen(new GuiGraphics(null), mouseX, mouseY, partialTick);
    }

    private void mineturismo$renderScreen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        super.render(mouseX, mouseY, partialTick);
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
import com.examplebett.network.SetCarRearBrakeCaliperPayload;
import com.examplebett.render.PartTransforms;
import com.examplebett.render.RearBrakeCaliperParts;

import com.examplebett.compat.GuiGraphics;
import com.examplebett.compat.v114.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class RearBrakeCaliperAdjustScreen extends Screen implements CarCustomizationScreen, CarCameraOrbitScreen {

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

    public RearBrakeCaliperAdjustScreen(CarEntity car, Screen parent) {
        this(car, parent, 0);
    }

    public RearBrakeCaliperAdjustScreen(CarEntity car, Screen parent, int initialScrollRow) {
        super(ModText.translatable("gui.mineturismo.rear_brake_caliper_adjust.title"));
        this.car = car;
        this.parent = parent;
        this.initialScrollRow = initialScrollRow;
    }

    private int fitWidth(Component label) {
        return Math.max(MIN_BUTTON_WIDTH, com.examplebett.compat.LegacyGui.width(this.font, label) + BUTTON_HORIZONTAL_PADDING);
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

    private void applyAdjust(String enabledIds, String partId, PartTransforms.Transform6 t) {
        this.car.setRearBrakeCaliperEnabledIds(enabledIds);
        this.car.setRearBrakeCaliperTransform(partId, t);
        SetCarRearBrakeCaliperPayload mineturismo$payload = new SetCarRearBrakeCaliperPayload(this.car.getId(), enabledIds, partId, t.x(), t.y(), t.z(), t.angle(), t.angleY(), t.scale());
        ModClientNetworking.sendToServer(SetCarRearBrakeCaliperPayload.ID, mineturismo$payload, SetCarRearBrakeCaliperPayload::write);
        AiObjectMod.LOGGER.debug("rear brake caliper adjust [{}] enabledIds={} partId={} {}", this.car.getModelId(), enabledIds, partId, t);
        this.minecraft.setScreen(new RearBrakeCaliperAdjustScreen(this.car, this.parent, this.partScrollRow));
    }

    private void applyToggle(String id, boolean selected) {
        Set<String> ids = parseIds(this.car.getRearBrakeCaliperEnabledIds());
        if (selected) {
            ids.add(id);
        } else {
            ids.remove(id);
        }
        applyAdjust(String.join(",", ids), id, this.car.getRearBrakeCaliperTransform(id));
    }

    private final List<PartCheckboxEntry> partCheckboxes = new ArrayList<>();
    private int partScrollRow = 0;
    private int partMaxScrollRow = 0;
    private int partViewportY;

    private record PartCheckboxEntry(ModCheckbox checkbox, int row) {
    }

    private void updatePartCheckboxPositions() {
        int viewportTop = this.partViewportY;
        for (PartCheckboxEntry entry : this.partCheckboxes) {
            int visibleRow = entry.row() - this.partScrollRow;
            boolean isVisible = visibleRow >= 0 && visibleRow < PART_VISIBLE_ROWS;
            entry.checkbox().visible = isVisible;
            entry.checkbox().active = isVisible;
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

        List<RearBrakeCaliperParts.Option> parts = RearBrakeCaliperParts.list(this.minecraft.getResourceManager());
        this.modelMissing = this.isAnyCustomPartModelMissing(this.minecraft.getResourceManager(),
                parts.stream().map(RearBrakeCaliperParts.Option::modelId).toList());

        Set<String> enabledIds = parseIds(this.car.getRearBrakeCaliperEnabledIds());

        this.partCheckboxes.clear();
        this.partMaxScrollRow = Math.max(0, parts.size() - PART_VISIBLE_ROWS);
        this.partScrollRow = Math.max(0, Math.min(this.partMaxScrollRow, this.initialScrollRow));

        int arrowX = 20;

        int checklistTop = top + 24;
        this.partViewportY = checklistTop;
        for (int i = 0; i < parts.size(); i++) {
            RearBrakeCaliperParts.Option part = parts.get(i);
            boolean selected = enabledIds.contains(part.id());
            ModCheckbox checkbox = ModCheckbox.builder(part.displayName(), this.font)
                    .pos(arrowX, checklistTop + i * PART_ROW_HEIGHT)
                    .selected(selected)
                    .onValueChange((cb, newSelected) -> applyToggle(part.id(), newSelected))
                    .build();
            this.addButton(checkbox);
            this.partCheckboxes.add(new PartCheckboxEntry(checkbox, i));
        }

        int visibleRows = Math.min(parts.size(), PART_VISIBLE_ROWS);
        int viewportHeight = visibleRows * PART_ROW_HEIGHT;
        int downArrowY = checklistTop + viewportHeight + 4;

        this.addButton(new ScrollbarWidget(arrowX - 12, checklistTop,
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

        List<RearBrakeCaliperParts.Option> enabledParts = parts.stream().filter(p -> enabledIds.contains(p.id())).toList();
        for (RearBrakeCaliperParts.Option part : enabledParts) {
            PartTransforms.Transform6 t = this.car.getRearBrakeCaliperTransform(part.id());
            this.groupHeaders.add(new GroupHeader(part.displayName().getString(), rowY));
            int groupTop = rowY + 16;
            String id = part.id();
            addAxisRow(groupTop, "X", t.x(), POSITION_STEP,
                    delta -> applyAdjust(String.join(",", enabledIds), id, new PartTransforms.Transform6(t.x() + delta, t.y(), t.z(), t.angle(), t.angleY(), t.scale())));
            addAxisRow(groupTop + rowHeight, "Y", t.y(), POSITION_STEP,
                    delta -> applyAdjust(String.join(",", enabledIds), id, new PartTransforms.Transform6(t.x(), t.y() + delta, t.z(), t.angle(), t.angleY(), t.scale())));
            addAxisRow(groupTop + rowHeight * 2, "Z", t.z(), POSITION_STEP,
                    delta -> applyAdjust(String.join(",", enabledIds), id, new PartTransforms.Transform6(t.x(), t.y(), t.z() + delta, t.angle(), t.angleY(), t.scale())));
            addAxisRow(groupTop + rowHeight * 3, "Angle", t.angle(), ANGLE_STEP,
                    delta -> applyAdjust(String.join(",", enabledIds), id, new PartTransforms.Transform6(t.x(), t.y(), t.z(), t.angle() + delta, t.angleY(), t.scale())));
            addAxisRow(groupTop + rowHeight * 4, "Angle Y", t.angleY(), ANGLE_STEP,
                    delta -> applyAdjust(String.join(",", enabledIds), id, new PartTransforms.Transform6(t.x(), t.y(), t.z(), t.angle(), t.angleY() + delta, t.scale())));
            addAxisRow(groupTop + rowHeight * 5, "Scale", t.scale(), SCALE_STEP,
                    delta -> applyAdjust(String.join(",", enabledIds), id, new PartTransforms.Transform6(t.x(), t.y(), t.z(), t.angle(), t.angleY(), t.scale() + delta)));
            rowY = groupTop + rowHeight * 6 + 12;
        }

        int rightEdge = this.width - RIGHT_MARGIN;

        int backY = Math.max(leftColumnEndY, rowY) + 8;
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

    private final List<GroupHeader> groupHeaders = new ArrayList<>();

    private record GroupHeader(String label, int y) {
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
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollY) {
        return mineturismo$mouseScrolled(mouseX, mouseY, 0, scrollY);
    }

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
        return super.mouseScrolled(mouseX, mouseY, scrollY);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTick) {
        mineturismo$renderScreen(new GuiGraphics(null), mouseX, mouseY, partialTick);
    }

    private void mineturismo$renderScreen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        super.render(mouseX, mouseY, partialTick);
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
