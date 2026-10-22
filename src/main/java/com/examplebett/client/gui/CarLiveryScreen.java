//? if >=1.18 {
//?}
//? if <1.18 && >=1.17 {
/*
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

import com.examplebett.compat.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
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
                        ModClientNetworking.sendToServer(SetCarLiveryPayload.ID, mineturismo$coupledPayload, SetCarLiveryPayload::write);
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
                ModClientNetworking.sendToServer(SetCarLiveryPayload.ID, mineturismo$payload, SetCarLiveryPayload::write);
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

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollY) {
        return mineturismo$mouseScrolled(mouseX, mouseY, 0, scrollY);
    }

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
        return super.mouseScrolled(mouseX, mouseY, scrollY);
    }

    private int titleX;

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
*/
//?}
//? if <1.17 && >=1.16 {
/*
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

import com.examplebett.compat.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
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
                        ModClientNetworking.sendToServer(SetCarLiveryPayload.ID, mineturismo$coupledPayload, SetCarLiveryPayload::write);
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
            this.addButton(ModWidgets.button(backLabel, rightEdge - buttonWidth, y, buttonWidth, BUTTON_HEIGHT, button ->
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
            this.liveryButtons.add(this.addButton(ModWidgets.button(ModText.literal(label), rightEdge - buttonWidth, y, buttonWidth, BUTTON_HEIGHT, button -> {
                SetCarLiveryPayload mineturismo$payload = new SetCarLiveryPayload(this.car.getId(), liveryId);
                ModClientNetworking.sendToServer(SetCarLiveryPayload.ID, mineturismo$payload, SetCarLiveryPayload::write);
                this.car.setLivery(liveryId);
                this.mineturismo$cascadeToRailwayCoupledCars(label);

            })));
        }

        int backWidth = fitWidth(backLabel);
        int bottomY = startY + totalHeight + SPACING * 2 + 10;

        this.addButton(ModWidgets.button(backLabel, rightEdge - backWidth, bottomY, backWidth, BUTTON_HEIGHT, button ->
                this.minecraft.setScreen(this.parent)));

        if (this.maxScrollRow > 0) {

            int arrowX = rightEdge - maxWidth - 8 - 20;
            this.addButton(new ScrollbarWidget(ScrollbarWidget.xInArrowColumn(arrowX), startY, totalHeight,
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

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollY) {
        return mineturismo$mouseScrolled(mouseX, mouseY, 0, scrollY);
    }

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
        return super.mouseScrolled(mouseX, mouseY, scrollY);
    }

    private int titleX;

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
*/
//?}
//? if <1.16 && >=1.15 {
/*
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

import com.examplebett.compat.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
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
        return Math.max(MIN_BUTTON_WIDTH, com.examplebett.compat.LegacyGui.width(this.font, label) + BUTTON_HORIZONTAL_PADDING);
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
                        ModClientNetworking.sendToServer(SetCarLiveryPayload.ID, mineturismo$coupledPayload, SetCarLiveryPayload::write);
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
            this.addButton(ModWidgets.button(backLabel, rightEdge - buttonWidth, y, buttonWidth, BUTTON_HEIGHT, button ->
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
            this.liveryButtons.add(this.addButton(ModWidgets.button(ModText.literal(label), rightEdge - buttonWidth, y, buttonWidth, BUTTON_HEIGHT, button -> {
                SetCarLiveryPayload mineturismo$payload = new SetCarLiveryPayload(this.car.getId(), liveryId);
                ModClientNetworking.sendToServer(SetCarLiveryPayload.ID, mineturismo$payload, SetCarLiveryPayload::write);
                this.car.setLivery(liveryId);
                this.mineturismo$cascadeToRailwayCoupledCars(label);

            })));
        }

        int backWidth = fitWidth(backLabel);
        int bottomY = startY + totalHeight + SPACING * 2 + 10;

        this.addButton(ModWidgets.button(backLabel, rightEdge - backWidth, bottomY, backWidth, BUTTON_HEIGHT, button ->
                this.minecraft.setScreen(this.parent)));

        if (this.maxScrollRow > 0) {

            int arrowX = rightEdge - maxWidth - 8 - 20;
            this.addButton(new ScrollbarWidget(ScrollbarWidget.xInArrowColumn(arrowX), startY, totalHeight,
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

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollY) {
        return mineturismo$mouseScrolled(mouseX, mouseY, 0, scrollY);
    }

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
        return super.mouseScrolled(mouseX, mouseY, scrollY);
    }

    private int titleX;

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
*/
//?}
//? if <1.15 {
/*
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

import com.examplebett.compat.GuiGraphics;
import com.examplebett.compat.v114.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
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
        return Math.max(MIN_BUTTON_WIDTH, com.examplebett.compat.LegacyGui.width(this.font, label) + BUTTON_HORIZONTAL_PADDING);
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
                        ModClientNetworking.sendToServer(SetCarLiveryPayload.ID, mineturismo$coupledPayload, SetCarLiveryPayload::write);
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
            this.addButton(ModWidgets.button(backLabel, rightEdge - buttonWidth, y, buttonWidth, BUTTON_HEIGHT, button ->
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
            this.liveryButtons.add(this.addButton(ModWidgets.button(ModText.literal(label), rightEdge - buttonWidth, y, buttonWidth, BUTTON_HEIGHT, button -> {
                SetCarLiveryPayload mineturismo$payload = new SetCarLiveryPayload(this.car.getId(), liveryId);
                ModClientNetworking.sendToServer(SetCarLiveryPayload.ID, mineturismo$payload, SetCarLiveryPayload::write);
                this.car.setLivery(liveryId);
                this.mineturismo$cascadeToRailwayCoupledCars(label);

            })));
        }

        int backWidth = fitWidth(backLabel);
        int bottomY = startY + totalHeight + SPACING * 2 + 10;

        this.addButton(ModWidgets.button(backLabel, rightEdge - backWidth, bottomY, backWidth, BUTTON_HEIGHT, button ->
                this.minecraft.setScreen(this.parent)));

        if (this.maxScrollRow > 0) {

            int arrowX = rightEdge - maxWidth - 8 - 20;
            this.addButton(new ScrollbarWidget(ScrollbarWidget.xInArrowColumn(arrowX), startY, totalHeight,
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

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollY) {
        return mineturismo$mouseScrolled(mouseX, mouseY, 0, scrollY);
    }

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
        return super.mouseScrolled(mouseX, mouseY, scrollY);
    }

    private int titleX;

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
*/
//?}
