package com.examplebett.client.gui;
import com.examplebett.compat.ModWidgets;
import com.examplebett.compat.ModText;

import com.examplebett.compat.ModClientNetworking;
import com.examplebett.AiObjectMod;
import com.examplebett.entity.CarEntity;
import com.examplebett.network.MountForCameraAdjustPayload;
import com.examplebett.network.SetCarScalePayload;
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

public class DebugWrenchScreen extends Screen implements CarCustomizationScreen {

    private static final float CAR_SCALE_STEP = 0.1f;
    private static final float CAR_SCALE_FINE_STEP = CAR_SCALE_STEP / 5f;

    private static final int MENU_BUTTON_HEIGHT = 20;
    private static final int MENU_ROW_GAP = 4;
    private static final int MENU_VISIBLE_ROWS = 6;
    private static final int MENU_ROW_HEIGHT = MENU_BUTTON_HEIGHT + MENU_ROW_GAP;

    private final CarEntity car;

    private float carScaleValue;
    private int carScaleLabelY;

    private final List<MenuButtonEntry> menuButtons = new ArrayList<>();
    private int menuScrollRow = 0;
    private int menuMaxScrollRow = 0;
    private int menuViewportY;

    private record MenuButtonEntry(Button button, int row) {
    }

    public DebugWrenchScreen(CarEntity car) {
        super(ModText.translatable("gui.mineturismo.debug_wrench_menu.title"));
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

    private void applyCarScale(float delta) {
        float carScaleAdjust = this.car.getCarScaleAdjust() + delta;
        this.car.setCarScale(carScaleAdjust);
        SetCarScalePayload mineturismo$payload = new SetCarScalePayload(this.car.getId(), carScaleAdjust);
        //? if >=1.20.5 {
        ModClientNetworking.sendToServer(mineturismo$payload);
        //?} else {
        /*ModClientNetworking.sendToServer(SetCarScalePayload.ID, mineturismo$payload, SetCarScalePayload::write);*/
        //?}

        AiObjectMod.LOGGER.debug("car scale adjust [{}] carScaleAdjust={} resolved={}",
                this.car.getModelId(), carScaleAdjust, this.car.getCarScale());
        this.minecraft.setScreen(new DebugWrenchScreen(this.car));
    }

    @Override
    protected void init() {
        int buttonWidth = 200;
        int x = this.width / 2 - buttonWidth / 2;
        int y = this.height / 2 - 70;

        int carScaleRowY = y;
        int centerX = x + buttonWidth / 2;
        this.addRenderableWidget(ModWidgets.button(ModText.literal("-"), centerX - 70, carScaleRowY, 20, 20, button -> applyCarScale(-CAR_SCALE_STEP)));
        this.addRenderableWidget(ModWidgets.button(ModText.literal("-"), centerX - 48, carScaleRowY, 20, 20, button -> applyCarScale(-CAR_SCALE_FINE_STEP)));
        this.addRenderableWidget(ModWidgets.button(ModText.literal("+"), centerX + 28, carScaleRowY, 20, 20, button -> applyCarScale(CAR_SCALE_FINE_STEP)));
        this.addRenderableWidget(ModWidgets.button(ModText.literal("+"), centerX + 50, carScaleRowY, 20, 20, button -> applyCarScale(CAR_SCALE_STEP)));
        this.carScaleValue = this.car.getCarScaleAdjust();
        this.carScaleLabelY = carScaleRowY + 6;

        int menuY = carScaleRowY + 28;
        this.menuViewportY = menuY;
        this.menuButtons.clear();
        this.menuScrollRow = 0;

        record MenuAction(String labelKey, java.util.function.Consumer<DebugWrenchScreen> open) {
        }
        List<MenuAction> actions = List.of(
                new MenuAction("gui.mineturismo.debug_wrench_menu.light", s ->
                        s.minecraft.setScreen(new LightAdjustScreen(s.car, s))),
                new MenuAction("gui.mineturismo.debug_wrench_menu.camera", s -> {

                    MountForCameraAdjustPayload mineturismo$payload = new MountForCameraAdjustPayload(s.car.getId());
                    //? if >=1.20.5 {
                    ModClientNetworking.sendToServer(mineturismo$payload);
                    //?} else {
                    /*ModClientNetworking.sendToServer(MountForCameraAdjustPayload.ID, mineturismo$payload, MountForCameraAdjustPayload::write);*/
                    //?}
                    s.minecraft.setScreen(new CarCameraScreen(s.car, s));
                }),
                new MenuAction("gui.mineturismo.debug_wrench_menu.action", s ->
                        s.minecraft.setScreen(new ActionScreen(s.car, s))),
                new MenuAction("gui.mineturismo.debug_wrench_menu.width", s ->
                        s.minecraft.setScreen(new CarWidthScreen(s.car, s))),
                new MenuAction("gui.mineturismo.debug_wrench_menu.tilt", s ->
                        s.minecraft.setScreen(new TiltAdjustScreen(s.car, s))),
                new MenuAction("gui.mineturismo.debug_wrench_menu.passenger_position", s ->
                        s.minecraft.setScreen(new PassengerPositionAdjustScreen(s.car, s))),
                new MenuAction("gui.mineturismo.debug_wrench_menu.other", s ->
                        s.minecraft.setScreen(new OtherScreen(s.car, s)))
        );

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
        guiGraphics.drawCenteredString(this.font, String.format("Car Size: %.2f", this.carScaleValue),
                this.width / 2, this.carScaleLabelY, 0xFFFFFFFF);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
