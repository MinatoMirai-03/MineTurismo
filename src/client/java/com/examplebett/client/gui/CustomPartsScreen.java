package com.examplebett.client.gui;
import com.examplebett.compat.ModWidgets;
import com.examplebett.compat.ModText;

import com.examplebett.entity.CarEntity;
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
import net.minecraft.network.chat.Component;

public class CustomPartsScreen extends Screen implements CarCustomizationScreen {

    private static final int MENU_BUTTON_WIDTH = 200;
    private static final int MENU_BUTTON_HEIGHT = 20;
    private static final int MENU_ROW_GAP = 4;
    private static final int MENU_VISIBLE_ROWS = 5;
    private static final int MENU_ROW_HEIGHT = MENU_BUTTON_HEIGHT + MENU_ROW_GAP;

    private final CarEntity car;
    private final Screen parent;

    private final java.util.List<MenuButtonEntry> menuButtons = new java.util.ArrayList<>();
    private int menuScrollRow = 0;
    private int menuMaxScrollRow = 0;
    private int menuViewportY;

    private record MenuButtonEntry(Button button, int row, boolean enabled) {
    }

    private record MenuAction(String labelKey, java.util.function.Consumer<CustomPartsScreen> open, boolean enabled) {
    }

    public CustomPartsScreen(CarEntity car, Screen parent) {
        super(ModText.translatable("gui.mineturismo.custom_parts.title"));
        this.car = car;
        this.parent = parent;
    }

    private void updateMenuButtonPositions() {
        for (MenuButtonEntry entry : this.menuButtons) {
            int visibleRow = entry.row() - this.menuScrollRow;
            boolean isVisible = visibleRow >= 0 && visibleRow < MENU_VISIBLE_ROWS;
            entry.button().visible = isVisible;
            entry.button().active = isVisible && entry.enabled();
            if (isVisible) {
                ModWidgets.setY(entry.button(), this.menuViewportY + visibleRow * MENU_ROW_HEIGHT);
            }
        }
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
        if (this.menuMaxScrollRow > 0 && scrollY != 0) {
            int delta = scrollY > 0 ? -1 : 1;
            int newScrollRow = Math.max(0, Math.min(this.menuMaxScrollRow, this.menuScrollRow + delta));
            if (newScrollRow != this.menuScrollRow) {
                this.menuScrollRow = newScrollRow;
                this.updateMenuButtonPositions();
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
        int buttonWidth = MENU_BUTTON_WIDTH;
        int x = this.width / 2 - buttonWidth / 2;
        int y = this.height / 2 - 64;
        this.menuViewportY = y;
        this.menuButtons.clear();
        this.menuScrollRow = 0;

        boolean wheelPositionsKnown = this.car.hasWheelPositions();
        java.util.List<MenuAction> actions = java.util.List.of(
                new MenuAction("gui.mineturismo.custom_parts.rear_wing", s -> s.minecraft.setScreen(new RearWingAdjustScreen(s.car, s)), true),
                new MenuAction("gui.mineturismo.custom_parts.police_vehicle", s -> s.minecraft.setScreen(new PoliceLightAdjustScreen(s.car, s)), true),
                new MenuAction("gui.mineturismo.custom_parts.taxi", s -> s.minecraft.setScreen(new TaxiSignAdjustScreen(s.car, s)), true),
                new MenuAction("gui.mineturismo.custom_parts.other", s -> s.minecraft.setScreen(new OtherPartsAdjustScreen(s.car, s)), true),

                new MenuAction("gui.mineturismo.custom_parts.exhaust", s -> s.minecraft.setScreen(new ExhaustAdjustScreen(s.car, s)), true),
                new MenuAction("gui.mineturismo.custom_parts.front_brake_caliper", s -> s.minecraft.setScreen(new FrontBrakeCaliperAdjustScreen(s.car, s)), wheelPositionsKnown),
                new MenuAction("gui.mineturismo.custom_parts.rear_brake_caliper", s -> s.minecraft.setScreen(new RearBrakeCaliperAdjustScreen(s.car, s)), wheelPositionsKnown),
                new MenuAction("gui.mineturismo.custom_parts.under_neon", s -> s.minecraft.setScreen(new UnderNeonScreen(s.car, s)), true),
                new MenuAction("gui.mineturismo.custom_parts.brand_logo", s -> s.minecraft.setScreen(new BrandLogoScreen(s.car, s)), true)
        );
        this.menuMaxScrollRow = Math.max(0, actions.size() - MENU_VISIBLE_ROWS);
        for (int i = 0; i < actions.size(); i++) {
            MenuAction action = actions.get(i);
            Button button = ModWidgets.button(ModText.translatable(action.labelKey()), x, y + i * MENU_ROW_HEIGHT, buttonWidth, MENU_BUTTON_HEIGHT, b -> action.open().accept(this));
            this.addRenderableWidget(button);
            this.menuButtons.add(new MenuButtonEntry(button, i, action.enabled()));
        }

        int viewportHeight = MENU_VISIBLE_ROWS * MENU_ROW_HEIGHT - MENU_ROW_GAP;
        int arrowX = x + buttonWidth + 8;

        this.addRenderableWidget(new ScrollbarWidget(ScrollbarWidget.xInArrowColumn(arrowX), y,
                y + viewportHeight - 20 + 20 - (y), MENU_VISIBLE_ROWS, this.menuMaxScrollRow + MENU_VISIBLE_ROWS, () -> this.menuScrollRow, row -> {
                    this.menuScrollRow = row;
                    this.updateMenuButtonPositions();
                }));
        this.updateMenuButtonPositions();

        Component backLabel = ModText.translatable("gui.mineturismo.back");
        this.addRenderableWidget(ModWidgets.button(backLabel, x, y + viewportHeight + MENU_ROW_GAP + 8, buttonWidth, 20, button ->
                this.minecraft.setScreen(this.parent)));
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
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - 70, 0xFFFFFFFF);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
