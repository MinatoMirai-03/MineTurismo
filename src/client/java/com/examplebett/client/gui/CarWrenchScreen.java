package com.examplebett.client.gui;
import com.examplebett.compat.ModCheckbox;
import com.examplebett.compat.ModWidgets;
import com.examplebett.compat.ModText;

import com.examplebett.compat.ModClientNetworking;
import com.examplebett.entity.CarEntity;
import com.examplebett.network.SetCarCustomTuningPayload;
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

import java.util.ArrayList;
import java.util.List;

public class CarWrenchScreen extends Screen implements CarCustomizationScreen {

    private static final int MENU_BUTTON_WIDTH = 200;
    private static final int MENU_BUTTON_HEIGHT = 20;
    private static final int MENU_ROW_GAP = 4;
    private static final int MENU_VISIBLE_ROWS = 5;
    private static final int MENU_ROW_HEIGHT = MENU_BUTTON_HEIGHT + MENU_ROW_GAP;

    private final CarEntity car;

    private final List<MenuButtonEntry> menuButtons = new ArrayList<>();
    private int menuScrollRow = 0;
    private int menuMaxScrollRow = 0;
    private int menuViewportY;

    private record MenuButtonEntry(Button button, int row) {
    }

    public CarWrenchScreen(CarEntity car) {
        super(ModText.translatable("gui.mineturismo.wrench_menu.title"));
        this.car = car;
    }

    private void updateMenuButtonPositions() {
        int viewportTop = this.menuViewportY;
        for (MenuButtonEntry entry : this.menuButtons) {
            int visibleRow = entry.row() - this.menuScrollRow;
            boolean isVisible = visibleRow >= 0 && visibleRow < MENU_VISIBLE_ROWS;
            entry.button().visible = isVisible;
            entry.button().active = isVisible;
            if (isVisible) {
                ModWidgets.setY(entry.button(), viewportTop + visibleRow * MENU_ROW_HEIGHT);
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

    private void sendDespawnEnabled(boolean enabled) {
        this.car.setDespawnEnabled(enabled);
        SetCarCustomTuningPayload mineturismo$payload = new SetCarCustomTuningPayload(
                this.car.getId(),
                this.car.getCustomTopSpeedKmh(),
                this.car.getCustomAccelFraction(),
                this.car.getCustomBrakeFraction(),
                this.car.getCustomHandlingFraction(),
                this.car.isCustomTireSmokeAlways(),
                enabled);
        //? if >=1.20.5 {
        ModClientNetworking.sendToServer(mineturismo$payload);
        //?} else {
        /*ModClientNetworking.sendToServer(SetCarCustomTuningPayload.ID, mineturismo$payload, SetCarCustomTuningPayload::write);*/
        //?}
    }

    @Override
    protected void init() {
        int buttonWidth = MENU_BUTTON_WIDTH;
        int x = this.width / 2 - buttonWidth / 2;
        int y = this.height / 2 - 70;

        this.addRenderableWidget(ModCheckbox.builder(ModText.translatable("gui.mineturismo.custom_tuning.despawn_enabled"), this.font)
                .pos(x, y)
                .selected(this.car.isDespawnEnabled())
                .onValueChange((checkbox, selected) -> this.sendDespawnEnabled(selected))
                .build());

        Component itemizeLabel = ModText.translatable("gui.mineturismo.wrench_menu.itemize_custom");
        int itemizeWidth = Math.max(MENU_BUTTON_WIDTH / 2, this.font.width(itemizeLabel) + 16);
        int itemizeX = x - 8 - itemizeWidth;
        int itemizeY = y + 24 + MENU_ROW_HEIGHT;
        this.addRenderableWidget(ModWidgets.button(itemizeLabel, itemizeX, itemizeY, itemizeWidth, MENU_BUTTON_HEIGHT, b ->
                this.minecraft.setScreen(new BoxCarScreen(this.car, this))));

        int menuY = y + 24;
        this.menuViewportY = menuY;
        this.menuButtons.clear();
        this.menuScrollRow = 0;

        record MenuAction(String labelKey, java.util.function.Consumer<CarWrenchScreen> open) {
        }
        List<MenuAction> actions = new ArrayList<>(List.of(
                new MenuAction("gui.mineturismo.wrench_menu.color", s -> s.minecraft.setScreen(new CarColorScreen(s.car, s))),
                new MenuAction("gui.mineturismo.wrench_menu.license_plate", s -> s.minecraft.setScreen(new CarPlateScreen(s.car, s))),
                new MenuAction("gui.mineturismo.wrench_menu.drive_mode", s -> s.minecraft.setScreen(new CarDriveModeScreen(s.car, s))),
                new MenuAction("gui.mineturismo.wrench_menu.engine", s -> s.minecraft.setScreen(new CarEngineScreen(s.car, s))),
                new MenuAction("gui.mineturismo.wrench_menu.livery", s -> s.minecraft.setScreen(new CarLiveryScreen(s.car, s))),
                new MenuAction("gui.mineturismo.wrench_menu.wheel", s -> s.minecraft.setScreen(new WheelAdjustScreen(s.car, s))),
                new MenuAction("gui.mineturismo.wrench_menu.custom_parts", s -> s.minecraft.setScreen(new CustomPartsScreen(s.car, s))),
                new MenuAction("gui.mineturismo.wrench_menu.text_parts", s -> s.minecraft.setScreen(new TextPartsScreen(s.car, s))),
                new MenuAction("gui.mineturismo.wrench_menu.item_icon", s -> s.minecraft.setScreen(new CarItemIconScreen(s.car, s))),
                new MenuAction("gui.mineturismo.wrench_menu.horn_siren", s -> s.minecraft.setScreen(new CarHornSirenScreen(s.car, s))),
                new MenuAction("gui.mineturismo.wrench_menu.bomb", s -> s.minecraft.setScreen(new CarBombScreen(s.car, s))),
                new MenuAction("gui.mineturismo.wrench_menu.under_spoiler", s -> s.minecraft.setScreen(new CarUnderSpoilerScreen(s.car, s))),
                new MenuAction("gui.mineturismo.wrench_menu.limousine", s -> s.minecraft.setScreen(new CarLimousineScreen(s.car, s)))
        ));

        if (this.car.hasVehicleCategory(com.examplebett.entity.VehicleCategory.RAILWAY)) {
            actions.add(new MenuAction("gui.mineturismo.wrench_menu.railway_consist",
                    s -> s.minecraft.setScreen(new RailwayConsistScreen(s.car, s))));
        }

        actions.add(new MenuAction("gui.mineturismo.wrench_menu.tire_smoke", s -> s.minecraft.setScreen(new CarTireSmokeEditScreen(s.car, s))));
        actions.add(new MenuAction("gui.mineturismo.wrench_menu.ghost_lap", s -> s.minecraft.setScreen(new CarGhostLapScreen(s.car, s))));

        int totalRows = actions.size();
        this.menuMaxScrollRow = Math.max(0, totalRows - MENU_VISIBLE_ROWS);

        for (int i = 0; i < actions.size(); i++) {
            MenuAction action = actions.get(i);
            Button button = ModWidgets.button(ModText.translatable(action.labelKey()), x, menuY + i * MENU_ROW_HEIGHT, buttonWidth, MENU_BUTTON_HEIGHT, b -> action.open().accept(this));
            this.addRenderableWidget(button);
            this.menuButtons.add(new MenuButtonEntry(button, i));
        }

        int viewportHeight = MENU_VISIBLE_ROWS * MENU_ROW_HEIGHT - MENU_ROW_GAP;
        int arrowX = x + buttonWidth + 8;

        this.addRenderableWidget(new ScrollbarWidget(ScrollbarWidget.xInArrowColumn(arrowX), menuY,
                menuY + viewportHeight - 20 + 20 - (menuY), MENU_VISIBLE_ROWS, this.menuMaxScrollRow + MENU_VISIBLE_ROWS, () -> this.menuScrollRow, row -> {
                    this.menuScrollRow = row;
                    this.updateMenuButtonPositions();
                }));

        this.updateMenuButtonPositions();
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
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - 100, 0xFFFFFFFF);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
