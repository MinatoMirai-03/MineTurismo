package com.examplebett.client.gui;
import com.examplebett.compat.ModWidgets;
import com.examplebett.compat.ModText;

import com.examplebett.compat.ModClientNetworking;
import com.examplebett.client.MenuCameraOrbit;
import com.examplebett.network.SetCarWheelColorPayload;
import com.examplebett.entity.CarEntity;
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

public class WheelStyleSelectScreen extends Screen implements CarCustomizationScreen, CarCameraOrbitScreen {

    private static final int RIGHT_MARGIN = 20;
    private static final int LIST_WIDTH = 120;
    private static final int ARROW_SIZE = 20;
    private static final int ARROW_GAP = 4;
    private static final int ROW_HEIGHT = 20;
    private static final int ROW_GAP = 2;
    private static final int ROW_PITCH = ROW_HEIGHT + ROW_GAP;
    private static final int TOP = 30;
    private static final int COLOR_WIDTH = 80;

    private final CarEntity car;

    private final Screen adjustParent;
    private final int initialScrollRow;

    private final List<RowEntry> rows = new ArrayList<>();
    private int scrollRow = 0;
    private int maxScrollRow = 0;
    private int visibleRows = 1;
    private int listTop;
    private int listX;

    private int colorLabelX = -1;
    private record RowEntry(Button button, int row) {
    }

    public WheelStyleSelectScreen(CarEntity car, Screen adjustParent) {
        this(car, adjustParent, -1);
    }

    private WheelStyleSelectScreen(CarEntity car, Screen adjustParent, int initialScrollRow) {
        super(ModText.translatable("gui.mineturismo.wheel_select.title"));
        this.car = car;
        this.adjustParent = adjustParent;
        this.initialScrollRow = initialScrollRow;
    }

    private String currentStyle(WheelAttachment attachment) {
        String override = this.car.getWheelModelIdOverride();
        return (override != null && !override.isEmpty()) ? override : attachment.wheelModelId();
    }

    private static Component styleName(String id) {
        String key = "gui.mineturismo.wheel_style." + id;
        return net.minecraft.locale.Language.getInstance().has(key) ? ModText.translatable(key) : ModText.literal(id);
    }

    private void updateRowPositions() {
        for (RowEntry entry : this.rows) {
            int visibleRow = entry.row() - this.scrollRow;
            boolean isVisible = visibleRow >= 0 && visibleRow < this.visibleRows;
            entry.button().visible = isVisible;
            entry.button().active = isVisible;
            if (isVisible) {
                ModWidgets.setY(entry.button(), this.listTop + visibleRow * ROW_PITCH);
            }
        }
    }

    private void applyColor(String wheelColorOverride) {
        this.car.setWheelColor(wheelColorOverride);
        SetCarWheelColorPayload mineturismo$payload = new SetCarWheelColorPayload(this.car.getId(), wheelColorOverride);
        //? if >=1.20.5 {
        ModClientNetworking.sendToServer(mineturismo$payload);
        //?} else {
        /*ModClientNetworking.sendToServer(SetCarWheelColorPayload.ID, mineturismo$payload, SetCarWheelColorPayload::write);*/
        //?}
        this.minecraft.setScreen(new WheelStyleSelectScreen(this.car, this.adjustParent, this.scrollRow));
    }

    private void scrollBy(int delta) {
        this.scrollRow = Math.max(0, Math.min(this.maxScrollRow, this.scrollRow + delta));
        this.updateRowPositions();
    }

    @Override
    protected void init() {
        this.rows.clear();
        int rightEdge = this.width - RIGHT_MARGIN;
        this.listX = rightEdge - ARROW_SIZE - ARROW_GAP - LIST_WIDTH;
        this.listTop = TOP;

        this.visibleRows = Math.max(1, (this.height - TOP - ROW_HEIGHT - 8 - 10 + ROW_GAP) / ROW_PITCH);

        WheelAttachment attachment = this.car.getWheelAttachment();
        List<String> styles = attachment != null ? attachment.wheelStyles() : List.of();
        String current = attachment != null ? this.currentStyle(attachment) : null;
        this.visibleRows = Math.min(this.visibleRows, Math.max(1, styles.size()));
        this.maxScrollRow = Math.max(0, styles.size() - this.visibleRows);

        int currentIndex = Math.max(0, styles.indexOf(current));
        int start = this.initialScrollRow >= 0 ? this.initialScrollRow : currentIndex - this.visibleRows / 2;
        this.scrollRow = Math.max(0, Math.min(this.maxScrollRow, start));

        for (int i = 0; i < styles.size(); i++) {
            String style = styles.get(i);
            boolean selected = style.equals(current);
            Component name = styleName(style);

            Component label = selected ? ModText.literal("▶ ").append(name).append(" ◀") : name;
            Button button = ModWidgets.button(label, this.listX, this.listTop + i * ROW_PITCH, LIST_WIDTH, ROW_HEIGHT, b -> {
                if (!selected) {
                    WheelAdjustScreen.applyWheelStyle(this.car, style);
                    this.minecraft.setScreen(new WheelStyleSelectScreen(this.car, this.adjustParent, this.scrollRow));
                }
            });
            this.addRenderableWidget(button);
            this.rows.add(new RowEntry(button, i));
        }

        int viewportHeight = this.visibleRows * ROW_PITCH - ROW_GAP;
        int arrowX = this.listX + LIST_WIDTH + ARROW_GAP;
        if (this.maxScrollRow > 0) {

            this.addRenderableWidget(new ScrollbarWidget(arrowX + (ARROW_SIZE - DragScrollbar.WIDTH) / 2, this.listTop,
                    viewportHeight, this.visibleRows, styles.size(), () -> this.scrollRow,
                    row -> this.scrollBy(row - this.scrollRow)));
        }
        this.updateRowPositions();

        this.colorLabelX = -1;
        if (current != null && !WheelAttachment.NONE_STYLE.equals(current)) {
            com.examplebett.render.WheelColorOptions colors = com.examplebett.client.render.WheelColorLoader
                    .load(this.minecraft.getResourceManager(), current).orElse(null);
            if (colors != null && colors.ids().size() > 1) {
                String override = this.car.getWheelColorOverride();
                String currentColor = (override != null && !override.isEmpty()) ? override : colors.ids().get(0);
                int colorX = this.listX - ARROW_GAP * 2 - COLOR_WIDTH;
                this.colorLabelX = colorX + COLOR_WIDTH / 2;
                for (int i = 0; i < colors.ids().size(); i++) {
                    String colorId = colors.ids().get(i);
                    boolean selected = colorId.equals(currentColor);
                    Component name = ModText.literal(colors.labels().get(i));
                    Component label = selected ? ModText.literal("▶ ").append(name).append(" ◀") : name;
                    this.addRenderableWidget(ModWidgets.button(label, colorX, this.listTop + i * ROW_PITCH, COLOR_WIDTH, ROW_HEIGHT, b -> {
                        if (!selected) {
                            this.applyColor(colorId);
                        }
                    }));
                }
            }
        }

        this.addRenderableWidget(ModWidgets.button(ModText.translatable("gui.mineturismo.back"),
                this.listX, this.listTop + viewportHeight + 8, LIST_WIDTH, ROW_HEIGHT, b ->
                        this.minecraft.setScreen(new WheelAdjustScreen(this.car, this.adjustParent))));
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
        if (this.maxScrollRow > 0 && scrollY != 0) {
            int before = this.scrollRow;
            this.scrollBy(scrollY > 0 ? -1 : 1);
            if (this.scrollRow != before) {
                return true;
            }
        }
        //? if >=1.20.2 {
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        //?} else {
        /*return super.mouseScrolled(mouseX, mouseY, scrollY);*/
        //?}
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
        guiGraphics.drawCenteredString(this.font, this.title, this.listX + LIST_WIDTH / 2, TOP - 16, 0xFFFFFFFF);
        if (this.colorLabelX >= 0) {
            guiGraphics.drawCenteredString(this.font, ModText.translatable("gui.mineturismo.wheel_select.color"),
                    this.colorLabelX, TOP - 16, 0xFFFFFFFF);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
