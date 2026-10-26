//? if >=1.18 {
//?}
//? if <1.18 && >=1.17 {
/*
package com.examplebett.client.gui;
import com.examplebett.compat.ModCheckbox;
import com.examplebett.compat.ModWidgets;
import com.examplebett.compat.ModText;

import com.examplebett.compat.ModClientNetworking;
import com.examplebett.entity.CarEntity;
import com.examplebett.network.SetCarCustomTuningPayload;
import com.examplebett.network.SetRailwayConsistPayload;
import com.examplebett.util.AiCarPool;

import com.examplebett.compat.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class RailwayConsistScreen extends Screen implements CarCustomizationScreen {

    private static final int MAX_MIDDLE_CARS = 20;
    private static final int ROW_BUTTON_WIDTH = 220;
    private static final int ROW_BUTTON_HEIGHT = 20;
    private static final int ROW_GAP = 4;
    private static final int VISIBLE_ROWS = 4;
    private static final int ROW_HEIGHT = ROW_BUTTON_HEIGHT + ROW_GAP;

    private final CarEntity car;
    private final Screen parent;

    private List<String> middleModelIds = new ArrayList<>();
    private String tailModelId = "";

    private final List<RowButtonEntry> rowButtons = new ArrayList<>();
    private int scrollRow = 0;
    private int maxScrollRow = 0;
    private int viewportY;

    private record RowButtonEntry(Button button, int row) {
    }

    public RailwayConsistScreen(CarEntity car, Screen parent) {
        super(ModText.translatable("gui.mineturismo.railway_menu.title"));
        this.car = car;
        this.parent = parent;
    }

    private void updateRowButtonPositions() {
        int viewportTop = this.viewportY;
        for (RowButtonEntry entry : this.rowButtons) {
            int visibleRow = entry.row() - this.scrollRow;
            boolean isVisible = visibleRow >= 0 && visibleRow < VISIBLE_ROWS;
            entry.button().visible = isVisible;
            entry.button().active = isVisible;
            if (isVisible) {
                ModWidgets.setY(entry.button(), viewportTop + visibleRow * ROW_HEIGHT);
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
                this.updateRowButtonPositions();
                return true;
            }
        }
        return super.mouseScrolled(mouseX, mouseY, scrollY);
    }

    private static String labelForModelId(String modelId) {
        if (modelId.isEmpty()) {
            return ModText.translatable("gui.mineturismo.railway_menu.tail_none").getString();
        }
        Item item = AiCarPool.spawnItemFor(modelId);
        return item != null ? new ItemStack(item).getHoverName().getString() : modelId;
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
        ModClientNetworking.sendToServer(SetCarCustomTuningPayload.ID, mineturismo$payload, SetCarCustomTuningPayload::write);
    }

    private void sendConsist(List<String> middleModelIds, String tailModelId) {
        String middleCsv = String.join(",", middleModelIds);

        this.car.setRailwayMiddleModelIds(middleCsv);
        this.car.setRailwayTailModelId(tailModelId);
        SetRailwayConsistPayload mineturismo$payload = new SetRailwayConsistPayload(this.car.getId(), middleCsv, tailModelId);
        ModClientNetworking.sendToServer(SetRailwayConsistPayload.ID, mineturismo$payload, SetRailwayConsistPayload::write);
        this.minecraft.setScreen(new RailwayConsistScreen(this.car, this.parent));
    }

    @Override
    protected void init() {
        this.middleModelIds = new ArrayList<>();
        String middleCsv = this.car.getRailwayMiddleModelIds();
        if (!middleCsv.isEmpty()) {
            for (String modelId : middleCsv.split(",")) {
                if (!modelId.isEmpty()) {
                    this.middleModelIds.add(modelId);
                }
            }
        }
        this.tailModelId = this.car.getRailwayTailModelId();

        int centerX = this.width / 2;
        int top = this.height / 2 - 100;

        Component countLabel = ModText.translatable("gui.mineturismo.railway_menu.middle_cars",
                String.valueOf(this.middleModelIds.size()));
        int countWidth = this.font.width(countLabel) + 16;
        int stepperWidth = 20;
        int stepperGap = 4;
        int countX = centerX - countWidth / 2;
        this.addRenderableWidget(ModWidgets.button(ModText.literal("-"),
                countX - stepperWidth - stepperGap, top, stepperWidth, ROW_BUTTON_HEIGHT, b -> {
                    if (!this.middleModelIds.isEmpty()) {
                        List<String> updated = new ArrayList<>(this.middleModelIds);
                        updated.remove(updated.size() - 1);
                        this.sendConsist(updated, this.tailModelId);
                    }
                }));
        this.addRenderableWidget(ModWidgets.button(countLabel, countX, top, countWidth, ROW_BUTTON_HEIGHT, b -> {
        }));
        this.addRenderableWidget(ModWidgets.button(ModText.literal("+"),
                countX + countWidth + stepperGap, top, stepperWidth, ROW_BUTTON_HEIGHT, b -> {
                    if (this.middleModelIds.size() < MAX_MIDDLE_CARS) {
                        List<String> updated = new ArrayList<>(this.middleModelIds);

                        updated.add(this.car.getModelId());
                        this.sendConsist(updated, this.tailModelId);
                    }
                }));

        int listX = centerX - ROW_BUTTON_WIDTH / 2;
        int listY = top + ROW_BUTTON_HEIGHT + 8;
        this.viewportY = listY;
        this.rowButtons.clear();
        this.scrollRow = 0;

        int totalRows = this.middleModelIds.size() + 1;
        this.maxScrollRow = Math.max(0, totalRows - VISIBLE_ROWS);

        for (int i = 0; i < this.middleModelIds.size(); i++) {
            int slotIndex = i;
            Component label = ModText.translatable("gui.mineturismo.railway_menu.middle_car_slot", String.valueOf(i + 1))
                    .append(": " + labelForModelId(this.middleModelIds.get(i)));
            Button button = ModWidgets.button(label, listX, listY + i * ROW_HEIGHT, ROW_BUTTON_WIDTH, ROW_BUTTON_HEIGHT, b ->
                    this.minecraft.setScreen(new RailwayCarPickerScreen(this, false, modelId -> {
                        List<String> updated = new ArrayList<>(this.middleModelIds);
                        updated.set(slotIndex, modelId);
                        this.sendConsist(updated, this.tailModelId);
                    })));
            this.addRenderableWidget(button);
            this.rowButtons.add(new RowButtonEntry(button, i));
        }

        int tailRow = this.middleModelIds.size();
        Component tailLabel = ModText.translatable("gui.mineturismo.railway_menu.tail_car")
                .append(": " + labelForModelId(this.tailModelId));
        Button tailButton = ModWidgets.button(tailLabel, listX, listY + tailRow * ROW_HEIGHT, ROW_BUTTON_WIDTH, ROW_BUTTON_HEIGHT, b ->
                this.minecraft.setScreen(new RailwayCarPickerScreen(this, true, modelId ->
                        this.sendConsist(this.middleModelIds, modelId))));
        this.addRenderableWidget(tailButton);
        this.rowButtons.add(new RowButtonEntry(tailButton, tailRow));

        int viewportHeight = VISIBLE_ROWS * ROW_HEIGHT - ROW_GAP;
        int arrowX = listX + ROW_BUTTON_WIDTH + 8;

        this.addRenderableWidget(new ScrollbarWidget(ScrollbarWidget.xInArrowColumn(arrowX), listY,
                listY + viewportHeight - 20 + 20 - (listY), VISIBLE_ROWS, this.maxScrollRow + VISIBLE_ROWS, () -> this.scrollRow, row -> {
                    this.scrollRow = row;
                    this.updateRowButtonPositions();
                }));

        this.updateRowButtonPositions();

        int footerY = listY + viewportHeight + 8;
        int footerButtonWidth = ROW_BUTTON_WIDTH;

        this.addRenderableWidget(ModCheckbox.builder(ModText.translatable("gui.mineturismo.custom_tuning.despawn_enabled"), this.font)
                .pos(listX, footerY)
                .selected(this.car.isDespawnEnabled())
                .onValueChange((checkbox, selected) -> this.sendDespawnEnabled(selected))
                .build());
        this.addRenderableWidget(ModWidgets.button(ModText.translatable("gui.mineturismo.railway_menu.color"),
                listX, footerY + ROW_HEIGHT, footerButtonWidth, ROW_BUTTON_HEIGHT, b ->
                        this.minecraft.setScreen(new CarColorScreen(this.car, this))));
        this.addRenderableWidget(ModWidgets.button(ModText.translatable("gui.mineturismo.railway_menu.livery"),
                listX, footerY + ROW_HEIGHT * 2, footerButtonWidth, ROW_BUTTON_HEIGHT, b ->
                        this.minecraft.setScreen(new CarLiveryScreen(this.car, this))));
        this.addRenderableWidget(ModWidgets.button(ModText.translatable("gui.mineturismo.railway_menu.get_custom_key"),
                listX, footerY + ROW_HEIGHT * 3, footerButtonWidth, ROW_BUTTON_HEIGHT, b ->
                        this.minecraft.setScreen(new BoxCarScreen(this.car, this))));
        this.addRenderableWidget(ModWidgets.button(ModText.translatable("gui.mineturismo.back"),
                listX, footerY + ROW_HEIGHT * 4, footerButtonWidth, ROW_BUTTON_HEIGHT, b ->
                        this.minecraft.setScreen(this.parent)));
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        mineturismo$renderScreen(new GuiGraphics(poseStack), mouseX, mouseY, partialTick);
    }

    private void mineturismo$renderScreen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        super.render(guiGraphics.pose(), mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - 120, 0xFFFFFFFF);
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
import com.examplebett.entity.CarEntity;
import com.examplebett.network.SetCarCustomTuningPayload;
import com.examplebett.network.SetRailwayConsistPayload;
import com.examplebett.util.AiCarPool;

import com.examplebett.compat.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class RailwayConsistScreen extends Screen implements CarCustomizationScreen {

    private static final int MAX_MIDDLE_CARS = 20;
    private static final int ROW_BUTTON_WIDTH = 220;
    private static final int ROW_BUTTON_HEIGHT = 20;
    private static final int ROW_GAP = 4;
    private static final int VISIBLE_ROWS = 4;
    private static final int ROW_HEIGHT = ROW_BUTTON_HEIGHT + ROW_GAP;

    private final CarEntity car;
    private final Screen parent;

    private List<String> middleModelIds = new ArrayList<>();
    private String tailModelId = "";

    private final List<RowButtonEntry> rowButtons = new ArrayList<>();
    private int scrollRow = 0;
    private int maxScrollRow = 0;
    private int viewportY;

    private record RowButtonEntry(Button button, int row) {
    }

    public RailwayConsistScreen(CarEntity car, Screen parent) {
        super(ModText.translatable("gui.mineturismo.railway_menu.title"));
        this.car = car;
        this.parent = parent;
    }

    private void updateRowButtonPositions() {
        int viewportTop = this.viewportY;
        for (RowButtonEntry entry : this.rowButtons) {
            int visibleRow = entry.row() - this.scrollRow;
            boolean isVisible = visibleRow >= 0 && visibleRow < VISIBLE_ROWS;
            entry.button().visible = isVisible;
            entry.button().active = isVisible;
            if (isVisible) {
                ModWidgets.setY(entry.button(), viewportTop + visibleRow * ROW_HEIGHT);
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
                this.updateRowButtonPositions();
                return true;
            }
        }
        return super.mouseScrolled(mouseX, mouseY, scrollY);
    }

    private static String labelForModelId(String modelId) {
        if (modelId.isEmpty()) {
            return ModText.translatable("gui.mineturismo.railway_menu.tail_none").getString();
        }
        Item item = AiCarPool.spawnItemFor(modelId);
        return item != null ? new ItemStack(item).getHoverName().getString() : modelId;
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
        ModClientNetworking.sendToServer(SetCarCustomTuningPayload.ID, mineturismo$payload, SetCarCustomTuningPayload::write);
    }

    private void sendConsist(List<String> middleModelIds, String tailModelId) {
        String middleCsv = String.join(",", middleModelIds);

        this.car.setRailwayMiddleModelIds(middleCsv);
        this.car.setRailwayTailModelId(tailModelId);
        SetRailwayConsistPayload mineturismo$payload = new SetRailwayConsistPayload(this.car.getId(), middleCsv, tailModelId);
        ModClientNetworking.sendToServer(SetRailwayConsistPayload.ID, mineturismo$payload, SetRailwayConsistPayload::write);
        this.minecraft.setScreen(new RailwayConsistScreen(this.car, this.parent));
    }

    @Override
    protected void init() {
        this.middleModelIds = new ArrayList<>();
        String middleCsv = this.car.getRailwayMiddleModelIds();
        if (!middleCsv.isEmpty()) {
            for (String modelId : middleCsv.split(",")) {
                if (!modelId.isEmpty()) {
                    this.middleModelIds.add(modelId);
                }
            }
        }
        this.tailModelId = this.car.getRailwayTailModelId();

        int centerX = this.width / 2;
        int top = this.height / 2 - 100;

        Component countLabel = ModText.translatable("gui.mineturismo.railway_menu.middle_cars",
                String.valueOf(this.middleModelIds.size()));
        int countWidth = this.font.width(countLabel) + 16;
        int stepperWidth = 20;
        int stepperGap = 4;
        int countX = centerX - countWidth / 2;
        this.addButton(ModWidgets.button(ModText.literal("-"),
                countX - stepperWidth - stepperGap, top, stepperWidth, ROW_BUTTON_HEIGHT, b -> {
                    if (!this.middleModelIds.isEmpty()) {
                        List<String> updated = new ArrayList<>(this.middleModelIds);
                        updated.remove(updated.size() - 1);
                        this.sendConsist(updated, this.tailModelId);
                    }
                }));
        this.addButton(ModWidgets.button(countLabel, countX, top, countWidth, ROW_BUTTON_HEIGHT, b -> {
        }));
        this.addButton(ModWidgets.button(ModText.literal("+"),
                countX + countWidth + stepperGap, top, stepperWidth, ROW_BUTTON_HEIGHT, b -> {
                    if (this.middleModelIds.size() < MAX_MIDDLE_CARS) {
                        List<String> updated = new ArrayList<>(this.middleModelIds);

                        updated.add(this.car.getModelId());
                        this.sendConsist(updated, this.tailModelId);
                    }
                }));

        int listX = centerX - ROW_BUTTON_WIDTH / 2;
        int listY = top + ROW_BUTTON_HEIGHT + 8;
        this.viewportY = listY;
        this.rowButtons.clear();
        this.scrollRow = 0;

        int totalRows = this.middleModelIds.size() + 1;
        this.maxScrollRow = Math.max(0, totalRows - VISIBLE_ROWS);

        for (int i = 0; i < this.middleModelIds.size(); i++) {
            int slotIndex = i;
            Component label = ModText.translatable("gui.mineturismo.railway_menu.middle_car_slot", String.valueOf(i + 1))
                    .append(": " + labelForModelId(this.middleModelIds.get(i)));
            Button button = ModWidgets.button(label, listX, listY + i * ROW_HEIGHT, ROW_BUTTON_WIDTH, ROW_BUTTON_HEIGHT, b ->
                    this.minecraft.setScreen(new RailwayCarPickerScreen(this, false, modelId -> {
                        List<String> updated = new ArrayList<>(this.middleModelIds);
                        updated.set(slotIndex, modelId);
                        this.sendConsist(updated, this.tailModelId);
                    })));
            this.addButton(button);
            this.rowButtons.add(new RowButtonEntry(button, i));
        }

        int tailRow = this.middleModelIds.size();
        Component tailLabel = ModText.translatable("gui.mineturismo.railway_menu.tail_car")
                .append(": " + labelForModelId(this.tailModelId));
        Button tailButton = ModWidgets.button(tailLabel, listX, listY + tailRow * ROW_HEIGHT, ROW_BUTTON_WIDTH, ROW_BUTTON_HEIGHT, b ->
                this.minecraft.setScreen(new RailwayCarPickerScreen(this, true, modelId ->
                        this.sendConsist(this.middleModelIds, modelId))));
        this.addButton(tailButton);
        this.rowButtons.add(new RowButtonEntry(tailButton, tailRow));

        int viewportHeight = VISIBLE_ROWS * ROW_HEIGHT - ROW_GAP;
        int arrowX = listX + ROW_BUTTON_WIDTH + 8;

        this.addButton(new ScrollbarWidget(ScrollbarWidget.xInArrowColumn(arrowX), listY,
                listY + viewportHeight - 20 + 20 - (listY), VISIBLE_ROWS, this.maxScrollRow + VISIBLE_ROWS, () -> this.scrollRow, row -> {
                    this.scrollRow = row;
                    this.updateRowButtonPositions();
                }));

        this.updateRowButtonPositions();

        int footerY = listY + viewportHeight + 8;
        int footerButtonWidth = ROW_BUTTON_WIDTH;

        this.addButton(ModCheckbox.builder(ModText.translatable("gui.mineturismo.custom_tuning.despawn_enabled"), this.font)
                .pos(listX, footerY)
                .selected(this.car.isDespawnEnabled())
                .onValueChange((checkbox, selected) -> this.sendDespawnEnabled(selected))
                .build());
        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.railway_menu.color"),
                listX, footerY + ROW_HEIGHT, footerButtonWidth, ROW_BUTTON_HEIGHT, b ->
                        this.minecraft.setScreen(new CarColorScreen(this.car, this))));
        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.railway_menu.livery"),
                listX, footerY + ROW_HEIGHT * 2, footerButtonWidth, ROW_BUTTON_HEIGHT, b ->
                        this.minecraft.setScreen(new CarLiveryScreen(this.car, this))));
        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.railway_menu.get_custom_key"),
                listX, footerY + ROW_HEIGHT * 3, footerButtonWidth, ROW_BUTTON_HEIGHT, b ->
                        this.minecraft.setScreen(new BoxCarScreen(this.car, this))));
        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.back"),
                listX, footerY + ROW_HEIGHT * 4, footerButtonWidth, ROW_BUTTON_HEIGHT, b ->
                        this.minecraft.setScreen(this.parent)));
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        mineturismo$renderScreen(new GuiGraphics(poseStack), mouseX, mouseY, partialTick);
    }

    private void mineturismo$renderScreen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        super.render(guiGraphics.pose(), mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - 120, 0xFFFFFFFF);
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
import com.examplebett.entity.CarEntity;
import com.examplebett.network.SetCarCustomTuningPayload;
import com.examplebett.network.SetRailwayConsistPayload;
import com.examplebett.util.AiCarPool;

import com.examplebett.compat.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class RailwayConsistScreen extends Screen implements CarCustomizationScreen {

    private static final int MAX_MIDDLE_CARS = 20;
    private static final int ROW_BUTTON_WIDTH = 220;
    private static final int ROW_BUTTON_HEIGHT = 20;
    private static final int ROW_GAP = 4;
    private static final int VISIBLE_ROWS = 4;
    private static final int ROW_HEIGHT = ROW_BUTTON_HEIGHT + ROW_GAP;

    private final CarEntity car;
    private final Screen parent;

    private List<String> middleModelIds = new ArrayList<>();
    private String tailModelId = "";

    private final List<RowButtonEntry> rowButtons = new ArrayList<>();
    private int scrollRow = 0;
    private int maxScrollRow = 0;
    private int viewportY;

    private record RowButtonEntry(Button button, int row) {
    }

    public RailwayConsistScreen(CarEntity car, Screen parent) {
        super(ModText.translatable("gui.mineturismo.railway_menu.title"));
        this.car = car;
        this.parent = parent;
    }

    private void updateRowButtonPositions() {
        int viewportTop = this.viewportY;
        for (RowButtonEntry entry : this.rowButtons) {
            int visibleRow = entry.row() - this.scrollRow;
            boolean isVisible = visibleRow >= 0 && visibleRow < VISIBLE_ROWS;
            entry.button().visible = isVisible;
            entry.button().active = isVisible;
            if (isVisible) {
                ModWidgets.setY(entry.button(), viewportTop + visibleRow * ROW_HEIGHT);
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
                this.updateRowButtonPositions();
                return true;
            }
        }
        return super.mouseScrolled(mouseX, mouseY, scrollY);
    }

    private static String labelForModelId(String modelId) {
        if (modelId.isEmpty()) {
            return ModText.translatable("gui.mineturismo.railway_menu.tail_none").getString();
        }
        Item item = AiCarPool.spawnItemFor(modelId);
        return item != null ? new ItemStack(item).getHoverName().getString() : modelId;
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
        ModClientNetworking.sendToServer(SetCarCustomTuningPayload.ID, mineturismo$payload, SetCarCustomTuningPayload::write);
    }

    private void sendConsist(List<String> middleModelIds, String tailModelId) {
        String middleCsv = String.join(",", middleModelIds);

        this.car.setRailwayMiddleModelIds(middleCsv);
        this.car.setRailwayTailModelId(tailModelId);
        SetRailwayConsistPayload mineturismo$payload = new SetRailwayConsistPayload(this.car.getId(), middleCsv, tailModelId);
        ModClientNetworking.sendToServer(SetRailwayConsistPayload.ID, mineturismo$payload, SetRailwayConsistPayload::write);
        this.minecraft.setScreen(new RailwayConsistScreen(this.car, this.parent));
    }

    @Override
    protected void init() {
        this.middleModelIds = new ArrayList<>();
        String middleCsv = this.car.getRailwayMiddleModelIds();
        if (!middleCsv.isEmpty()) {
            for (String modelId : middleCsv.split(",")) {
                if (!modelId.isEmpty()) {
                    this.middleModelIds.add(modelId);
                }
            }
        }
        this.tailModelId = this.car.getRailwayTailModelId();

        int centerX = this.width / 2;
        int top = this.height / 2 - 100;

        Component countLabel = ModText.translatable("gui.mineturismo.railway_menu.middle_cars",
                String.valueOf(this.middleModelIds.size()));
        int countWidth = com.examplebett.compat.LegacyGui.width(this.font, countLabel) + 16;
        int stepperWidth = 20;
        int stepperGap = 4;
        int countX = centerX - countWidth / 2;
        this.addButton(ModWidgets.button(ModText.literal("-"),
                countX - stepperWidth - stepperGap, top, stepperWidth, ROW_BUTTON_HEIGHT, b -> {
                    if (!this.middleModelIds.isEmpty()) {
                        List<String> updated = new ArrayList<>(this.middleModelIds);
                        updated.remove(updated.size() - 1);
                        this.sendConsist(updated, this.tailModelId);
                    }
                }));
        this.addButton(ModWidgets.button(countLabel, countX, top, countWidth, ROW_BUTTON_HEIGHT, b -> {
        }));
        this.addButton(ModWidgets.button(ModText.literal("+"),
                countX + countWidth + stepperGap, top, stepperWidth, ROW_BUTTON_HEIGHT, b -> {
                    if (this.middleModelIds.size() < MAX_MIDDLE_CARS) {
                        List<String> updated = new ArrayList<>(this.middleModelIds);

                        updated.add(this.car.getModelId());
                        this.sendConsist(updated, this.tailModelId);
                    }
                }));

        int listX = centerX - ROW_BUTTON_WIDTH / 2;
        int listY = top + ROW_BUTTON_HEIGHT + 8;
        this.viewportY = listY;
        this.rowButtons.clear();
        this.scrollRow = 0;

        int totalRows = this.middleModelIds.size() + 1;
        this.maxScrollRow = Math.max(0, totalRows - VISIBLE_ROWS);

        for (int i = 0; i < this.middleModelIds.size(); i++) {
            int slotIndex = i;
            Component label = ModText.translatable("gui.mineturismo.railway_menu.middle_car_slot", String.valueOf(i + 1))
                    .append(": " + labelForModelId(this.middleModelIds.get(i)));
            Button button = ModWidgets.button(label, listX, listY + i * ROW_HEIGHT, ROW_BUTTON_WIDTH, ROW_BUTTON_HEIGHT, b ->
                    this.minecraft.setScreen(new RailwayCarPickerScreen(this, false, modelId -> {
                        List<String> updated = new ArrayList<>(this.middleModelIds);
                        updated.set(slotIndex, modelId);
                        this.sendConsist(updated, this.tailModelId);
                    })));
            this.addButton(button);
            this.rowButtons.add(new RowButtonEntry(button, i));
        }

        int tailRow = this.middleModelIds.size();
        Component tailLabel = ModText.translatable("gui.mineturismo.railway_menu.tail_car")
                .append(": " + labelForModelId(this.tailModelId));
        Button tailButton = ModWidgets.button(tailLabel, listX, listY + tailRow * ROW_HEIGHT, ROW_BUTTON_WIDTH, ROW_BUTTON_HEIGHT, b ->
                this.minecraft.setScreen(new RailwayCarPickerScreen(this, true, modelId ->
                        this.sendConsist(this.middleModelIds, modelId))));
        this.addButton(tailButton);
        this.rowButtons.add(new RowButtonEntry(tailButton, tailRow));

        int viewportHeight = VISIBLE_ROWS * ROW_HEIGHT - ROW_GAP;
        int arrowX = listX + ROW_BUTTON_WIDTH + 8;

        this.addButton(new ScrollbarWidget(ScrollbarWidget.xInArrowColumn(arrowX), listY,
                listY + viewportHeight - 20 + 20 - (listY), VISIBLE_ROWS, this.maxScrollRow + VISIBLE_ROWS, () -> this.scrollRow, row -> {
                    this.scrollRow = row;
                    this.updateRowButtonPositions();
                }));

        this.updateRowButtonPositions();

        int footerY = listY + viewportHeight + 8;
        int footerButtonWidth = ROW_BUTTON_WIDTH;

        this.addButton(ModCheckbox.builder(ModText.translatable("gui.mineturismo.custom_tuning.despawn_enabled"), this.font)
                .pos(listX, footerY)
                .selected(this.car.isDespawnEnabled())
                .onValueChange((checkbox, selected) -> this.sendDespawnEnabled(selected))
                .build());
        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.railway_menu.color"),
                listX, footerY + ROW_HEIGHT, footerButtonWidth, ROW_BUTTON_HEIGHT, b ->
                        this.minecraft.setScreen(new CarColorScreen(this.car, this))));
        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.railway_menu.livery"),
                listX, footerY + ROW_HEIGHT * 2, footerButtonWidth, ROW_BUTTON_HEIGHT, b ->
                        this.minecraft.setScreen(new CarLiveryScreen(this.car, this))));
        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.railway_menu.get_custom_key"),
                listX, footerY + ROW_HEIGHT * 3, footerButtonWidth, ROW_BUTTON_HEIGHT, b ->
                        this.minecraft.setScreen(new BoxCarScreen(this.car, this))));
        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.back"),
                listX, footerY + ROW_HEIGHT * 4, footerButtonWidth, ROW_BUTTON_HEIGHT, b ->
                        this.minecraft.setScreen(this.parent)));
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTick) {
        mineturismo$renderScreen(new GuiGraphics(null), mouseX, mouseY, partialTick);
    }

    private void mineturismo$renderScreen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        super.render(mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - 120, 0xFFFFFFFF);
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
import com.examplebett.entity.CarEntity;
import com.examplebett.network.SetCarCustomTuningPayload;
import com.examplebett.network.SetRailwayConsistPayload;
import com.examplebett.util.AiCarPool;

import com.examplebett.compat.GuiGraphics;
import com.examplebett.compat.v114.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class RailwayConsistScreen extends Screen implements CarCustomizationScreen {

    private static final int MAX_MIDDLE_CARS = 20;
    private static final int ROW_BUTTON_WIDTH = 220;
    private static final int ROW_BUTTON_HEIGHT = 20;
    private static final int ROW_GAP = 4;
    private static final int VISIBLE_ROWS = 4;
    private static final int ROW_HEIGHT = ROW_BUTTON_HEIGHT + ROW_GAP;

    private final CarEntity car;
    private final Screen parent;

    private List<String> middleModelIds = new ArrayList<>();
    private String tailModelId = "";

    private final List<RowButtonEntry> rowButtons = new ArrayList<>();
    private int scrollRow = 0;
    private int maxScrollRow = 0;
    private int viewportY;

    private record RowButtonEntry(Button button, int row) {
    }

    public RailwayConsistScreen(CarEntity car, Screen parent) {
        super(ModText.translatable("gui.mineturismo.railway_menu.title"));
        this.car = car;
        this.parent = parent;
    }

    private void updateRowButtonPositions() {
        int viewportTop = this.viewportY;
        for (RowButtonEntry entry : this.rowButtons) {
            int visibleRow = entry.row() - this.scrollRow;
            boolean isVisible = visibleRow >= 0 && visibleRow < VISIBLE_ROWS;
            entry.button().visible = isVisible;
            entry.button().active = isVisible;
            if (isVisible) {
                ModWidgets.setY(entry.button(), viewportTop + visibleRow * ROW_HEIGHT);
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
                this.updateRowButtonPositions();
                return true;
            }
        }
        return super.mouseScrolled(mouseX, mouseY, scrollY);
    }

    private static String labelForModelId(String modelId) {
        if (modelId.isEmpty()) {
            return ModText.translatable("gui.mineturismo.railway_menu.tail_none").getString();
        }
        Item item = AiCarPool.spawnItemFor(modelId);
        return item != null ? new ItemStack(item).getHoverName().getString() : modelId;
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
        ModClientNetworking.sendToServer(SetCarCustomTuningPayload.ID, mineturismo$payload, SetCarCustomTuningPayload::write);
    }

    private void sendConsist(List<String> middleModelIds, String tailModelId) {
        String middleCsv = String.join(",", middleModelIds);

        this.car.setRailwayMiddleModelIds(middleCsv);
        this.car.setRailwayTailModelId(tailModelId);
        SetRailwayConsistPayload mineturismo$payload = new SetRailwayConsistPayload(this.car.getId(), middleCsv, tailModelId);
        ModClientNetworking.sendToServer(SetRailwayConsistPayload.ID, mineturismo$payload, SetRailwayConsistPayload::write);
        this.minecraft.setScreen(new RailwayConsistScreen(this.car, this.parent));
    }

    @Override
    protected void init() {
        this.middleModelIds = new ArrayList<>();
        String middleCsv = this.car.getRailwayMiddleModelIds();
        if (!middleCsv.isEmpty()) {
            for (String modelId : middleCsv.split(",")) {
                if (!modelId.isEmpty()) {
                    this.middleModelIds.add(modelId);
                }
            }
        }
        this.tailModelId = this.car.getRailwayTailModelId();

        int centerX = this.width / 2;
        int top = this.height / 2 - 100;

        Component countLabel = ModText.translatable("gui.mineturismo.railway_menu.middle_cars",
                String.valueOf(this.middleModelIds.size()));
        int countWidth = com.examplebett.compat.LegacyGui.width(this.font, countLabel) + 16;
        int stepperWidth = 20;
        int stepperGap = 4;
        int countX = centerX - countWidth / 2;
        this.addButton(ModWidgets.button(ModText.literal("-"),
                countX - stepperWidth - stepperGap, top, stepperWidth, ROW_BUTTON_HEIGHT, b -> {
                    if (!this.middleModelIds.isEmpty()) {
                        List<String> updated = new ArrayList<>(this.middleModelIds);
                        updated.remove(updated.size() - 1);
                        this.sendConsist(updated, this.tailModelId);
                    }
                }));
        this.addButton(ModWidgets.button(countLabel, countX, top, countWidth, ROW_BUTTON_HEIGHT, b -> {
        }));
        this.addButton(ModWidgets.button(ModText.literal("+"),
                countX + countWidth + stepperGap, top, stepperWidth, ROW_BUTTON_HEIGHT, b -> {
                    if (this.middleModelIds.size() < MAX_MIDDLE_CARS) {
                        List<String> updated = new ArrayList<>(this.middleModelIds);

                        updated.add(this.car.getModelId());
                        this.sendConsist(updated, this.tailModelId);
                    }
                }));

        int listX = centerX - ROW_BUTTON_WIDTH / 2;
        int listY = top + ROW_BUTTON_HEIGHT + 8;
        this.viewportY = listY;
        this.rowButtons.clear();
        this.scrollRow = 0;

        int totalRows = this.middleModelIds.size() + 1;
        this.maxScrollRow = Math.max(0, totalRows - VISIBLE_ROWS);

        for (int i = 0; i < this.middleModelIds.size(); i++) {
            int slotIndex = i;
            Component label = ModText.translatable("gui.mineturismo.railway_menu.middle_car_slot", String.valueOf(i + 1))
                    .append(": " + labelForModelId(this.middleModelIds.get(i)));
            Button button = ModWidgets.button(label, listX, listY + i * ROW_HEIGHT, ROW_BUTTON_WIDTH, ROW_BUTTON_HEIGHT, b ->
                    this.minecraft.setScreen(new RailwayCarPickerScreen(this, false, modelId -> {
                        List<String> updated = new ArrayList<>(this.middleModelIds);
                        updated.set(slotIndex, modelId);
                        this.sendConsist(updated, this.tailModelId);
                    })));
            this.addButton(button);
            this.rowButtons.add(new RowButtonEntry(button, i));
        }

        int tailRow = this.middleModelIds.size();
        Component tailLabel = ModText.translatable("gui.mineturismo.railway_menu.tail_car")
                .append(": " + labelForModelId(this.tailModelId));
        Button tailButton = ModWidgets.button(tailLabel, listX, listY + tailRow * ROW_HEIGHT, ROW_BUTTON_WIDTH, ROW_BUTTON_HEIGHT, b ->
                this.minecraft.setScreen(new RailwayCarPickerScreen(this, true, modelId ->
                        this.sendConsist(this.middleModelIds, modelId))));
        this.addButton(tailButton);
        this.rowButtons.add(new RowButtonEntry(tailButton, tailRow));

        int viewportHeight = VISIBLE_ROWS * ROW_HEIGHT - ROW_GAP;
        int arrowX = listX + ROW_BUTTON_WIDTH + 8;

        this.addButton(new ScrollbarWidget(ScrollbarWidget.xInArrowColumn(arrowX), listY,
                listY + viewportHeight - 20 + 20 - (listY), VISIBLE_ROWS, this.maxScrollRow + VISIBLE_ROWS, () -> this.scrollRow, row -> {
                    this.scrollRow = row;
                    this.updateRowButtonPositions();
                }));

        this.updateRowButtonPositions();

        int footerY = listY + viewportHeight + 8;
        int footerButtonWidth = ROW_BUTTON_WIDTH;

        this.addButton(ModCheckbox.builder(ModText.translatable("gui.mineturismo.custom_tuning.despawn_enabled"), this.font)
                .pos(listX, footerY)
                .selected(this.car.isDespawnEnabled())
                .onValueChange((checkbox, selected) -> this.sendDespawnEnabled(selected))
                .build());
        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.railway_menu.color"),
                listX, footerY + ROW_HEIGHT, footerButtonWidth, ROW_BUTTON_HEIGHT, b ->
                        this.minecraft.setScreen(new CarColorScreen(this.car, this))));
        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.railway_menu.livery"),
                listX, footerY + ROW_HEIGHT * 2, footerButtonWidth, ROW_BUTTON_HEIGHT, b ->
                        this.minecraft.setScreen(new CarLiveryScreen(this.car, this))));
        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.railway_menu.get_custom_key"),
                listX, footerY + ROW_HEIGHT * 3, footerButtonWidth, ROW_BUTTON_HEIGHT, b ->
                        this.minecraft.setScreen(new BoxCarScreen(this.car, this))));
        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.back"),
                listX, footerY + ROW_HEIGHT * 4, footerButtonWidth, ROW_BUTTON_HEIGHT, b ->
                        this.minecraft.setScreen(this.parent)));
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTick) {
        mineturismo$renderScreen(new GuiGraphics(null), mouseX, mouseY, partialTick);
    }

    private void mineturismo$renderScreen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        super.render(mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - 120, 0xFFFFFFFF);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
*/
//?}
