package com.examplebett.client.gui;
import com.examplebett.compat.ModWidgets;
import com.examplebett.compat.ModText;

import com.examplebett.compat.ModClientNetworking;
import com.examplebett.client.MenuCameraOrbit;
import com.examplebett.client.render.LiveryLoader;
import com.examplebett.entity.CarEntity;
import com.examplebett.entity.VehicleCategory;
import com.examplebett.network.SetCarLiveryPayload;
import com.examplebett.render.LiveryOptions;
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

public class CarLiveryScreen extends Screen implements CarCustomizationScreen, CarCameraOrbitScreen {

    private static final int BUTTON_HEIGHT = 20;
    private static final int SPACING = 4;

    private static final int RIGHT_MARGIN = 20;

    private static final int BUTTON_HORIZONTAL_PADDING = 16;
    private static final int MIN_BUTTON_WIDTH = 60;

    private static final int ROW_HEIGHT = BUTTON_HEIGHT + SPACING;

    private static final int LIST_TOP_OFFSET = 76;
    private static final int BOTTOM_MARGIN = 10;

    private static final int BACK_AREA_HEIGHT = SPACING * 2 + 10 + BUTTON_HEIGHT;

    private final CarEntity car;
    private final Screen parent;

    private final java.util.List<Button> liveryButtons = new java.util.ArrayList<>();
    private int scrollRow = 0;
    private int maxScrollRow = 0;
    private int visibleRows = 0;
    private int listStartY;

    public CarLiveryScreen(CarEntity car, Screen parent) {
        super(ModText.translatable("gui.mineturismo.livery_menu.title"));
        this.car = car;
        this.parent = parent;
    }

    private int fitWidth(Component label) {
        return Math.max(MIN_BUTTON_WIDTH, this.font.width(label) + BUTTON_HORIZONTAL_PADDING);
    }

    private void mineturismo$cascadeToRailwayCoupledCars(String label) {
        if (!this.car.hasVehicleCategory(VehicleCategory.RAILWAY) || this.minecraft.level == null) {
            return;
        }
        for (int coupledId : this.car.getRailwayCoupledEntityIds()) {
            if (!(this.minecraft.level.getEntity(coupledId) instanceof CarEntity coupled)) {
                continue;
            }
            LiveryLoader.load(this.minecraft.getResourceManager(), coupled.getModelId())
                    .ifPresent(options -> {
                        int matchIndex = options.labels().indexOf(label);
                        if (matchIndex < 0) {
                            return;
                        }
                        String matchedLiveryId = options.ids().get(matchIndex);
                        coupled.setLiveryOptions(options);
                        SetCarLiveryPayload mineturismo$coupledPayload = new SetCarLiveryPayload(coupledId, matchedLiveryId);
                        //? if >=1.20.5 {
                        ModClientNetworking.sendToServer(mineturismo$coupledPayload);
                        //?} else {
                        /*ModClientNetworking.sendToServer(SetCarLiveryPayload.ID, mineturismo$coupledPayload, SetCarLiveryPayload::write);*/
                        //?}
                        coupled.setLivery(matchedLiveryId);
                    });
        }
    }

    @Override
    protected void init() {
        LiveryOptions options = this.car.getLiveryOptions();
        Component backLabel = ModText.translatable("gui.mineturismo.back");
        int rightEdge = this.width - RIGHT_MARGIN;

        if (options == null || options.ids().isEmpty()) {
            int buttonWidth = fitWidth(backLabel);
            this.titleX = rightEdge - buttonWidth / 2;
            int y = this.height / 2;
            this.addRenderableWidget(ModWidgets.button(backLabel, rightEdge - buttonWidth, y, buttonWidth, BUTTON_HEIGHT, button ->
                    this.minecraft.setScreen(this.parent)));
            return;
        }

        int maxWidth = fitWidth(backLabel);
        for (String label : options.labels()) {
            maxWidth = Math.max(maxWidth, fitWidth(ModText.literal(label)));
        }
        this.titleX = rightEdge - maxWidth / 2;

        int rows = options.ids().size();
        int listTop = this.height / 2 - LIST_TOP_OFFSET;
        int available = this.height - BOTTOM_MARGIN - listTop - BACK_AREA_HEIGHT;
        int maxVisibleRows = Math.max(1, (available + SPACING) / ROW_HEIGHT);
        this.visibleRows = Math.min(rows, maxVisibleRows);
        this.maxScrollRow = rows - this.visibleRows;

        this.scrollRow = Math.max(0, Math.min(this.maxScrollRow, this.scrollRow));

        int totalHeight = this.visibleRows * ROW_HEIGHT - SPACING;
        int startY = Math.max(listTop, this.height / 2 - totalHeight / 2);
        this.listStartY = startY;
        this.liveryButtons.clear();

        for (int i = 0; i < options.ids().size(); i++) {
            String liveryId = options.ids().get(i);
            String label = options.labels().get(i);
            int y = startY + i * ROW_HEIGHT;
            int buttonWidth = fitWidth(ModText.literal(label));
            this.liveryButtons.add(this.addRenderableWidget(ModWidgets.button(ModText.literal(label), rightEdge - buttonWidth, y, buttonWidth, BUTTON_HEIGHT, button -> {
                SetCarLiveryPayload mineturismo$payload = new SetCarLiveryPayload(this.car.getId(), liveryId);
                //? if >=1.20.5 {
                ModClientNetworking.sendToServer(mineturismo$payload);
                //?} else {
                /*ModClientNetworking.sendToServer(SetCarLiveryPayload.ID, mineturismo$payload, SetCarLiveryPayload::write);*/
                //?}
                this.car.setLivery(liveryId);
                this.mineturismo$cascadeToRailwayCoupledCars(label);

            })));
        }

        int backWidth = fitWidth(backLabel);
        int bottomY = startY + totalHeight + SPACING * 2 + 10;

        this.addRenderableWidget(ModWidgets.button(backLabel, rightEdge - backWidth, bottomY, backWidth, BUTTON_HEIGHT, button ->
                this.minecraft.setScreen(this.parent)));

        if (this.maxScrollRow > 0) {

            int arrowX = rightEdge - maxWidth - 8 - 20;
            this.addRenderableWidget(new ScrollbarWidget(ScrollbarWidget.xInArrowColumn(arrowX), startY, totalHeight,
                    this.visibleRows, rows, () -> this.scrollRow, row -> {
                        this.scrollRow = row;
                        this.updateLiveryButtonPositions();
                    }));
        }
        this.updateLiveryButtonPositions();
    }

    private void updateLiveryButtonPositions() {
        for (int i = 0; i < this.liveryButtons.size(); i++) {
            Button button = this.liveryButtons.get(i);
            int visibleRow = i - this.scrollRow;
            boolean isVisible = visibleRow >= 0 && visibleRow < this.visibleRows;
            button.visible = isVisible;
            button.active = isVisible;
            if (isVisible) {
                ModWidgets.setY(button, this.listStartY + visibleRow * ROW_HEIGHT);
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
        if (this.maxScrollRow > 0 && scrollY != 0) {
            int delta = scrollY > 0 ? -1 : 1;
            int newScrollRow = Math.max(0, Math.min(this.maxScrollRow, this.scrollRow + delta));
            if (newScrollRow != this.scrollRow) {
                this.scrollRow = newScrollRow;
                this.updateLiveryButtonPositions();
                return true;
            }
        }
        //? if >=1.20.2 {
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        //?} else {
        /*return super.mouseScrolled(mouseX, mouseY, scrollY);*/
        //?}
    }

    private int titleX;

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
        this.renderAiObjectNotice(guiGraphics, this.font, this.width);
        guiGraphics.drawCenteredString(this.font, this.title, this.titleX, this.height / 2 - 90, 0xFFFFFFFF);
        if (this.car.getLiveryOptions() == null || this.car.getLiveryOptions().ids().isEmpty()) {
            guiGraphics.drawCenteredString(this.font,
                    ModText.translatable("gui.mineturismo.livery_menu.none"),
                    this.titleX, this.height / 2 - 12, 0xFFFFFFFF);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
