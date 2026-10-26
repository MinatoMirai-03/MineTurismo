package com.examplebett.client.gui;
import com.examplebett.compat.ModWidgets;
import com.examplebett.compat.ModText;

import com.examplebett.client.MenuCameraOrbit;
import com.examplebett.compat.ModClientNetworking;
import com.examplebett.entity.CarEntity;
import com.examplebett.entity.VehicleCategory;
import com.examplebett.network.SetCarColorPayload;
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
import net.minecraft.world.item.DyeColor;

public class CarColorScreen extends Screen implements CarCustomizationScreen, CarCameraOrbitScreen {

    private static final int COLUMNS = 3;
    private static final int MAX_SWATCH_SIZE = 22;
    private static final int MIN_SWATCH_SIZE = 12;
    private static final int SPACING = 3;
    private static final int LEFT_MARGIN = 10;
    private static final int BUTTON_HEIGHT = 18;
    private static final int TITLE_TO_GRID_GAP = 8;
    private static final int GRID_TO_NAME_GAP = 6;
    private static final int NAME_HEIGHT = 9;
    private static final int GRID_TO_BACK_GAP = 8;
    private static final int BACK_BUTTON_WIDTH = 80;
    private static final int TITLE_HEIGHT = 9;
    private int swatchSize = MAX_SWATCH_SIZE;
    private boolean cameraDragging = false;

    private java.util.List<net.minecraft.util.FormattedCharSequence> noteLines = java.util.Collections.emptyList();
    private int noteHeight;

    private final CarEntity car;
    private final Screen parent;

    private int titleY;
    private int nameY;
    private final java.util.List<Swatch> swatches = new java.util.ArrayList<>();

    private record Swatch(int x, int y, int rgb, String labelKey) {
    }

    public CarColorScreen(CarEntity car, Screen parent) {
        super(ModText.translatable("gui.mineturismo.color_menu.title"));
        this.car = car;
        this.parent = parent;
    }

    public record PaletteEntry(String labelKey, int rgb) {
    }

    public static java.util.List<PaletteEntry> palette() {
        java.util.List<PaletteEntry> entries = new java.util.ArrayList<>();
        for (DyeColor dyeColor : DyeColor.values()) {
            entries.add(new PaletteEntry("color.minecraft." + dyeColor.getName(),
                    CarColorPalette.dyeRgb(dyeColor.getName(), diffuseRgb(dyeColor))));
        }
        for (int i = 0; i < CarColorPalette.EXTRA_RGB.length; i++) {
            entries.add(new PaletteEntry(CarColorPalette.translationKey(i), CarColorPalette.EXTRA_RGB[i]));
        }
        for (int i = 0; i < CarColorPalette.METAL_RGB.length; i++) {
            entries.add(new PaletteEntry(CarColorPalette.metalTranslationKey(i), CarColorPalette.METAL_RGB[i]));
        }
        return entries;
    }

    public static void drawSwatchFill(GuiGraphics guiGraphics, int x0, int y0, int x1, int y1, int rgb) {
        int metal = CarColorPalette.metalIndex(rgb);
        if (metal < 0) {
            guiGraphics.fill(x0, y0, x1, y1, 0xFF000000 | rgb);
            return;
        }
        int height = Math.max(1, y1 - y0);
        int width = Math.max(1, x1 - x0);
        for (int row = 0; row < height; row++) {
            float t = height == 1 ? 0f : (float) row / (height - 1);

            float shade = t < 0.6f ? 1.25f - t / 0.6f * 0.5f : 0.75f + (t - 0.6f) / 0.4f * 0.2f;
            guiGraphics.fill(x0, y0 + row, x1, y0 + row + 1, 0xFF000000 | scaleRgb(rgb, shade));

            int streak = x0 + (int) ((1f - t) * width * 0.7f) + width / 6;
            if (streak + 2 <= x1) {
                guiGraphics.fill(streak, y0 + row, streak + 2, y0 + row + 1, 0xB0FFFFFF);
            }
        }
    }

    private static int scaleRgb(int rgb, float factor) {
        int r = Math.min(255, (int) (((rgb >> 16) & 0xFF) * factor));
        int g = Math.min(255, (int) (((rgb >> 8) & 0xFF) * factor));
        int b = Math.min(255, (int) ((rgb & 0xFF) * factor));
        return (r << 16) | (g << 8) | b;
    }

    @Override
    protected void init() {
        java.util.List<PaletteEntry> palette = palette();
        int colorCount = palette.size();
        String[] labelKeys = new String[colorCount];
        int[] rgbs = new int[colorCount];
        for (int i = 0; i < colorCount; i++) {
            labelKeys[i] = palette.get(i).labelKey();
            rgbs[i] = palette.get(i).rgb();
        }

        int rows = (colorCount + COLUMNS - 1) / COLUMNS;

        this.noteLines = this.font.split(ModText.translatable("gui.mineturismo.color_menu.livery_note"), Math.min(this.width - 20, 330));
        this.noteHeight = this.noteLines.size() * 10 + 3;
        this.titleY = 6;
        int areaTop = this.titleY + TITLE_HEIGHT + this.noteHeight + TITLE_TO_GRID_GAP;
        int belowGrid = GRID_TO_NAME_GAP + NAME_HEIGHT + GRID_TO_BACK_GAP + BUTTON_HEIGHT;

        int fitSize = (this.height - 4 - areaTop - belowGrid - (rows - 1) * SPACING) / rows;
        this.swatchSize = Math.max(MIN_SWATCH_SIZE, Math.min(MAX_SWATCH_SIZE, fitSize));
        int gridHeight = rows * this.swatchSize + (rows - 1) * SPACING;
        int blockHeight = gridHeight + belowGrid;
        int startX = LEFT_MARGIN;
        int startY = Math.max(areaTop, (this.height - blockHeight) / 2);
        this.nameY = startY + gridHeight + GRID_TO_NAME_GAP;
        this.swatches.clear();

        for (int i = 0; i < colorCount; i++) {
            int rgb = rgbs[i];
            int col = i % COLUMNS;
            int row = i / COLUMNS;
            int x = startX + col * (this.swatchSize + SPACING);
            int y = startY + row * (this.swatchSize + SPACING);
            this.swatches.add(new Swatch(x, y, rgb, labelKeys[i]));

            Button swatchButton = ModWidgets.button(ModText.literal(""), x, y, this.swatchSize, this.swatchSize, button -> {
                SetCarColorPayload mineturismo$payload = new SetCarColorPayload(this.car.getId(), rgb);
                //? if >=1.20.5 {
                ModClientNetworking.sendToServer(mineturismo$payload);
                //?} else {
                /*ModClientNetworking.sendToServer(SetCarColorPayload.ID, mineturismo$payload, SetCarColorPayload::write);*/
                //?}
                this.car.setColor(rgb);
                this.mineturismo$cascadeToRailwayCoupledCars(rgb);

            });
            this.addRenderableWidget(swatchButton);
        }

        this.addRenderableWidget(ModWidgets.button(ModText.translatable("gui.mineturismo.back"), LEFT_MARGIN, this.nameY + NAME_HEIGHT + GRID_TO_BACK_GAP, BACK_BUTTON_WIDTH, BUTTON_HEIGHT, button ->
                this.minecraft.setScreen(this.parent)));
    }

    private void mineturismo$cascadeToRailwayCoupledCars(int rgb) {
        if (!this.car.hasVehicleCategory(VehicleCategory.RAILWAY)) {
            return;
        }
        if (this.minecraft.level == null) {
            return;
        }
        for (int coupledId : this.car.getRailwayCoupledEntityIds()) {
            if (this.minecraft.level.getEntity(coupledId) instanceof CarEntity coupled) {
                SetCarColorPayload mineturismo$coupledPayload = new SetCarColorPayload(coupledId, rgb);
                //? if >=1.20.5 {
                ModClientNetworking.sendToServer(mineturismo$coupledPayload);
                //?} else {
                /*ModClientNetworking.sendToServer(SetCarColorPayload.ID, mineturismo$coupledPayload, SetCarColorPayload::write);*/
                //?}
                coupled.setColor(rgb);
            }
        }
    }

    private static int diffuseRgb(DyeColor color) {
        //? if >=1.21 {
        return color.getTextureDiffuseColor();
        //?} else {
        /*float[] rgb = color.getTextureDiffuseColors();
        return ((int) (rgb[0] * 255) << 16) | ((int) (rgb[1] * 255) << 8) | (int) (rgb[2] * 255);
        *///?}
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
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, this.titleY, 0xFFFFFFFF);
        for (int i = 0; i < this.noteLines.size(); i++) {
            net.minecraft.util.FormattedCharSequence line = this.noteLines.get(i);
            guiGraphics.drawCenteredString(this.font, line, this.width / 2, this.titleY + TITLE_HEIGHT + 3 + i * 10, 0xFFFFFF55);
        }

        int currentRgb = this.car.getColor() & 0xFFFFFF;
        Swatch hovered = null;
        Swatch current = null;
        for (Swatch swatch : this.swatches) {
            int size = this.swatchSize;
            drawSwatchFill(guiGraphics, swatch.x() + 2, swatch.y() + 2, swatch.x() + size - 2, swatch.y() + size - 2, swatch.rgb());
            if (swatch.rgb() == currentRgb) {
                current = swatch;
                int x0 = swatch.x() - 1;
                int y0 = swatch.y() - 1;
                int x1 = swatch.x() + size + 1;
                int y1 = swatch.y() + size + 1;
                guiGraphics.fill(x0, y0, x1, y0 + 1, 0xFFFFFFFF);
                guiGraphics.fill(x0, y1 - 1, x1, y1, 0xFFFFFFFF);
                guiGraphics.fill(x0, y0, x0 + 1, y1, 0xFFFFFFFF);
                guiGraphics.fill(x1 - 1, y0, x1, y1, 0xFFFFFFFF);
            }
            if (mouseX >= swatch.x() && mouseX < swatch.x() + size
                    && mouseY >= swatch.y() && mouseY < swatch.y() + size) {
                hovered = swatch;
            }
        }
        Swatch named = hovered != null ? hovered : current;
        if (named != null) {
            guiGraphics.drawString(this.font, ModText.translatable(named.labelKey()), LEFT_MARGIN, this.nameY, 0xFFFFFFFF);
        }
    }

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

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
