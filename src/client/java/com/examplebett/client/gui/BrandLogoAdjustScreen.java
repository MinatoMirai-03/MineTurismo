package com.examplebett.client.gui;
import com.examplebett.compat.ModCheckbox;
import com.examplebett.compat.ModWidgets;
import com.examplebett.compat.ModText;

import com.examplebett.compat.ModClientNetworking;
import com.examplebett.AiObjectMod;
import com.examplebett.client.MenuCameraOrbit;
import com.examplebett.client.render.BrandLogoTextures;
import com.examplebett.entity.CarEntity;
import com.examplebett.network.SetCarBrandLogoPayload;
import com.examplebett.render.BrandLogos;
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
import java.util.function.DoubleSupplier;

public class BrandLogoAdjustScreen extends Screen implements CarCustomizationScreen, CarCameraOrbitScreen {

    private static final float POSITION_STEP = 0.05f;
    private static final float ANGLE_STEP = 5f;
    private static final float SCALE_STEP = 0.1f;
    private static final float DEPTH_STEP = 0.01f;
    private static final float FINE_STEP_DIVISOR = 5f;
    private static final int RIGHT_MARGIN = 20;
    private static final int CLUSTER_HALF_WIDTH = 70;
    private static final int AXIS_LABEL_MAX_WIDTH = 56;
    private static final int LEFT_X = 20;
    private static final int LEFT_WIDTH = 142;
    private static final int TOP = 30;
    private static final int ROW = 24;

    private static final int LOGO_COLUMNS = 3;
    private static final int LOGO_CELL_WIDTH = 44;
    private static final int LOGO_CELL_HEIGHT = 28;
    private static final int LOGO_GAP = 5;
    private static final int LOGO_ROW_GAP = 3;
    private static final int LOGO_VISIBLE_ROWS = 3;
    private static final int LOGO_THUMB_PADDING = 3;

    private static final int LOGO_CELL_BACKGROUND = 0xFF808080;

    private static final float PLATE_MOUNT_GAP = 0.002f;

    private final CarEntity car;

    private final Screen customPartsScreen;
    private final int slotIndex;
    private BrandLogos.Slot slot;

    private int clusterCenterX;
    private int logoLabelY;
    private int logoScrollRow = 0;
    private int logoMaxScrollRow = 0;

    private ScrollbarWidget logoScrollbar;
    private String logoScrollbarLayout;
    private int logoGridTop;
    private int logoGridBottom;
    private final List<AxisRow> axisRows = new ArrayList<>();
    private final List<LogoButton> logoButtons = new ArrayList<>();

    private record AxisRow(String label, DoubleSupplier value, int y) {
    }

    private record LogoButton(Button button, BrandLogoTextures.Design design) {
    }

    private interface AxisAdjuster {
        void adjust(float delta);
    }

    public BrandLogoAdjustScreen(CarEntity car, Screen customPartsScreen, int slotIndex) {
        super(ModText.translatable("gui.mineturismo.brand_logo_adjust.title",
                BrandLogoScreen.slotName(slotIndex)));
        this.car = car;
        this.customPartsScreen = customPartsScreen;
        this.slotIndex = slotIndex;
        this.slot = car.getBrandLogo(slotIndex);
    }

    private void apply(BrandLogos.Slot updated) {
        this.slot = updated;
        this.car.setBrandLogo(this.slotIndex, updated);
        SetCarBrandLogoPayload mineturismo$payload = new SetCarBrandLogoPayload(this.car.getId(), BrandLogos.encodeLine(this.slotIndex, updated));
        //? if >=1.20.5 {
        ModClientNetworking.sendToServer(mineturismo$payload);
        //?} else {
        /*ModClientNetworking.sendToServer(SetCarBrandLogoPayload.ID, mineturismo$payload, SetCarBrandLogoPayload::write);*/
        //?}
    }

    private void applyAdjust(BrandLogos.Slot updated) {
        this.apply(updated);
        AiObjectMod.LOGGER.debug("brand logo adjust [{}] slot={} {}", this.car.getModelId(), BrandLogos.SLOT_IDS[this.slotIndex], updated);
    }

    @Override
    public void removed() {
        super.removed();
        if (this.slot != null) {
            AiObjectMod.LOGGER.debug("brand logo state [{}] {}", this.car.getModelId(), BrandLogos.encodeLine(this.slotIndex, this.slot));
        }
    }

    @Override
    protected void init() {
        this.clusterCenterX = this.width - RIGHT_MARGIN - CLUSTER_HALF_WIDTH;

        int y = TOP;
        this.addRenderableWidget(ModCheckbox.builder(ModText.translatable("gui.mineturismo.text_part_adjust.enabled"), this.font)
                .pos(LEFT_X, y)
                .selected(this.slot.enabled())
                .onValueChange((checkbox, selected) -> this.apply(this.slot.withEnabled(selected)))
                .build());
        y += ROW;

        this.addRenderableWidget(ModCheckbox.builder(ModText.translatable("gui.mineturismo.text_part_adjust.glow"), this.font)
                .pos(LEFT_X, y)
                .selected(this.slot.glow())
                .onValueChange((checkbox, selected) -> this.apply(this.slot.withGlow(selected)))
                .build());
        y += ROW;

        this.logoLabelY = y;
        int listTop = y + 12;
        List<BrandLogoTextures.Design> designs = BrandLogoTextures.designsFor(this.slotIndex);
        int totalRows = (designs.size() + LOGO_COLUMNS - 1) / LOGO_COLUMNS;
        int maxScrollRow = Math.max(0, totalRows - LOGO_VISIBLE_ROWS);
        this.logoMaxScrollRow = maxScrollRow;
        this.logoScrollRow = Math.max(0, Math.min(maxScrollRow, this.logoScrollRow));
        this.logoButtons.clear();
        for (int i = 0; i < designs.size(); i++) {
            int row = i / LOGO_COLUMNS - this.logoScrollRow;
            if (row < 0 || row >= LOGO_VISIBLE_ROWS) {
                continue;
            }
            BrandLogoTextures.Design design = designs.get(i);
            int bx = LEFT_X + (i % LOGO_COLUMNS) * (LOGO_CELL_WIDTH + LOGO_GAP);
            int by = listTop + row * (LOGO_CELL_HEIGHT + LOGO_ROW_GAP);

            Button button = this.addRenderableWidget(ModWidgets.button(ModText.literal(""), bx, by, LOGO_CELL_WIDTH, LOGO_CELL_HEIGHT,
                    b -> this.apply(this.slot.withLogo(design.id()))));
            this.logoButtons.add(new LogoButton(button, design));
        }
        int visibleRows = Math.min(LOGO_VISIBLE_ROWS, totalRows);
        this.logoGridTop = listTop;
        this.logoGridBottom = listTop + visibleRows * (LOGO_CELL_HEIGHT + LOGO_ROW_GAP);
        y = this.logoGridBottom + 1;

        if (maxScrollRow > 0) {
            int barX = LEFT_X + LEFT_WIDTH + 4;
            int barHeight = this.logoGridBottom - LOGO_ROW_GAP - listTop;
            String layout = barX + "," + listTop + "," + barHeight + "," + totalRows;
            if (this.logoScrollbar == null || !layout.equals(this.logoScrollbarLayout)) {
                this.logoScrollbar = new ScrollbarWidget(barX, listTop, barHeight, LOGO_VISIBLE_ROWS, totalRows,
                        () -> this.logoScrollRow, row -> this.scrollLogos(row - this.logoScrollRow));
                this.logoScrollbarLayout = layout;
            }
            this.addRenderableWidget(this.logoScrollbar);
        }
        y += 4;

        this.addRenderableWidget(ModWidgets.button(ModText.translatable("gui.mineturismo.text_part_adjust.move_front_plate"),
                LEFT_X, y, LEFT_WIDTH, 18, b -> this.moveToPlateMount(true)));
        y += 20;
        this.addRenderableWidget(ModWidgets.button(ModText.translatable("gui.mineturismo.text_part_adjust.move_rear_plate"),
                LEFT_X, y, LEFT_WIDTH, 18, b -> this.moveToPlateMount(false)));

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
                d -> this.applyAdjust(this.slot.withRotation(this.slot.pitch(), this.slot.yaw() + d, this.slot.roll())));
        addAxisRow(rowY += rowHeight, "Pitch", () -> this.slot.pitch(), ANGLE_STEP,
                d -> this.applyAdjust(this.slot.withRotation(this.slot.pitch() + d, this.slot.yaw(), this.slot.roll())));
        addAxisRow(rowY += rowHeight, "Roll", () -> this.slot.roll(), ANGLE_STEP,
                d -> this.applyAdjust(this.slot.withRotation(this.slot.pitch(), this.slot.yaw(), this.slot.roll() + d)));
        addAxisRow(rowY += rowHeight, "Scale", () -> this.slot.scale(), SCALE_STEP,
                d -> this.applyAdjust(this.slot.withScale(this.slot.scale() + d)));
        addAxisRow(rowY += rowHeight, "Depth", () -> this.slot.depth(), DEPTH_STEP,
                d -> this.applyAdjust(this.slot.withDepth(this.slot.depth() + d)));
        rowY += rowHeight + 6;

        int rightEdge = this.width - RIGHT_MARGIN;
        this.addRenderableWidget(ModWidgets.button(ModText.translatable("gui.mineturismo.back"),
                rightEdge - CLUSTER_HALF_WIDTH * 2, rowY, CLUSTER_HALF_WIDTH * 2, 20, b ->
                        this.minecraft.setScreen(new BrandLogoScreen(this.car, this.customPartsScreen))));
    }

    private void scrollLogos(int delta) {
        int next = Math.max(0, Math.min(this.logoMaxScrollRow, this.logoScrollRow + delta));
        if (next == this.logoScrollRow) {
            return;
        }
        this.logoScrollRow = next;
        this.clearWidgets();
        this.init();
    }

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
        if (scrollY != 0 && this.logoMaxScrollRow > 0
                && mouseX >= LEFT_X && mouseX < LEFT_X + LEFT_WIDTH
                && mouseY >= this.logoGridTop && mouseY < this.logoGridBottom) {
            this.scrollLogos(scrollY > 0 ? -1 : 1);
            return true;
        }
        return false;
    }

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
        this.addRenderableWidget(ModWidgets.button(ModText.literal("-"), centerX - 70, y, 20, 20, button -> adjuster.adjust(-step)));
        this.addRenderableWidget(ModWidgets.button(ModText.literal("-"), centerX - 48, y, 20, 20, button -> adjuster.adjust(-fineStep)));
        this.addRenderableWidget(ModWidgets.button(ModText.literal("+"), centerX + 28, y, 20, 20, button -> adjuster.adjust(fineStep)));
        this.addRenderableWidget(ModWidgets.button(ModText.literal("+"), centerX + 50, y, 20, 20, button -> adjuster.adjust(step)));
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
        guiGraphics.drawString(this.font, this.title, LEFT_X, TOP - 20, 0xFFFFFFFF);

        BrandLogoTextures.Design hovered = null;
        for (LogoButton entry : this.logoButtons) {
            Button b = entry.button();
            boolean over = mouseX >= ModWidgets.getX(b) && mouseX < ModWidgets.getX(b) + b.getWidth()
                    && mouseY >= ModWidgets.getY(b) && mouseY < ModWidgets.getY(b) + b.getHeight();
            if (over) {
                hovered = entry.design();
            }
            this.drawLogoCell(guiGraphics, b, entry.design(), entry.design().id().equals(this.slot.logo()), over);
        }
        Component logoName = (hovered != null ? hovered : BrandLogoTextures.design(this.slot.logo())).displayName();
        guiGraphics.drawString(this.font, ModText.translatable("gui.mineturismo.brand_logo_adjust.logo", logoName),
                LEFT_X, this.logoLabelY, 0xFFFFFFFF);
        for (AxisRow row : this.axisRows) {
            drawFittedAxisLabel(guiGraphics, String.format("%s: %s", row.label(), AxisValueFormat.format(row.value().getAsDouble())), this.clusterCenterX, row.y() + 6);
        }
    }

    private void drawLogoCell(GuiGraphics guiGraphics, Button b, BrandLogoTextures.Design design, boolean selected, boolean hovered) {
        int x0 = ModWidgets.getX(b);
        int y0 = ModWidgets.getY(b);
        int x1 = x0 + b.getWidth();
        int y1 = y0 + b.getHeight();
        guiGraphics.fill(x0, y0, x1, y1, LOGO_CELL_BACKGROUND);
        int maxW = b.getWidth() - LOGO_THUMB_PADDING * 2;
        int maxH = b.getHeight() - LOGO_THUMB_PADDING * 2;
        float fit = Math.min((float) maxW / design.width(), (float) maxH / design.height());
        int w = Math.max(1, Math.round(design.width() * fit));
        int h = Math.max(1, Math.round(design.height() * fit));
        int tx = x0 + (b.getWidth() - w) / 2;
        int ty = y0 + (b.getHeight() - h) / 2;
        com.examplebett.compat.GuiTextures.blit(guiGraphics, design.texture(), tx, ty, 0f, 0f, w, h,
                design.width(), design.height(), design.width(), design.height());
        int frame = selected ? 0xFFFFFFFF : hovered ? 0xFFC0C0C0 : 0;
        if (frame != 0) {
            guiGraphics.fill(x0 - 1, y0 - 1, x1 + 1, y0, frame);
            guiGraphics.fill(x0 - 1, y1, x1 + 1, y1 + 1, frame);
            guiGraphics.fill(x0 - 1, y0, x0, y1, frame);
            guiGraphics.fill(x1, y0, x1 + 1, y1, frame);
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
