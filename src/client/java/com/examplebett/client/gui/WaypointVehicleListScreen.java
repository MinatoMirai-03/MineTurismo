package com.examplebett.client.gui;
import com.examplebett.compat.ModWidgets;
import com.examplebett.compat.ModText;

import com.examplebett.compat.ModClientNetworking;
import com.examplebett.item.BoxedCarItem;
import com.examplebett.network.AddCustomTrafficCarPayload;
import com.examplebett.network.SetTownTrafficDisabledVehiclesPayload;
import com.examplebett.network.UpdateCustomTrafficCarPayload;
import com.examplebett.util.AiCarPool;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
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
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
//? if >=1.21.9 {
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.input.MouseButtonEvent;
//?}
//? if >=1.21.6 {
import net.minecraft.client.renderer.RenderPipelines;
//?}
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
//? if >=1.21.2 {
import net.minecraft.util.ARGB;
//?} else
/*import net.minecraft.util.FastColor;*/

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class WaypointVehicleListScreen extends Screen {

    private static final int COLUMNS = 2;
    private static final int COLUMN_WIDTH = 200;
    private static final int BUTTON_WIDTH = 190;
    private static final int ROW_HEIGHT = 14;
    private static final int BUTTON_HEIGHT = 12;
    private static final int VISIBLE_ROWS = 8;

    //? if >=1.21 {
    private static final ResourceLocation CHECKBOX_SELECTED_HIGHLIGHTED_SPRITE =
            ResourceLocation.withDefaultNamespace("widget/checkbox_selected_highlighted");
    private static final ResourceLocation CHECKBOX_SELECTED_SPRITE =
            ResourceLocation.withDefaultNamespace("widget/checkbox_selected");
    private static final ResourceLocation CHECKBOX_HIGHLIGHTED_SPRITE =
            ResourceLocation.withDefaultNamespace("widget/checkbox_highlighted");
    private static final ResourceLocation CHECKBOX_SPRITE =
            ResourceLocation.withDefaultNamespace("widget/checkbox");
    //?} else {
    /*private static final ResourceLocation CHECKBOX_SELECTED_HIGHLIGHTED_SPRITE =
            new ResourceLocation("widget/checkbox_selected_highlighted");
    private static final ResourceLocation CHECKBOX_SELECTED_SPRITE =
            new ResourceLocation("widget/checkbox_selected");
    private static final ResourceLocation CHECKBOX_HIGHLIGHTED_SPRITE =
            new ResourceLocation("widget/checkbox_highlighted");
    private static final ResourceLocation CHECKBOX_SPRITE =
            new ResourceLocation("widget/checkbox");*/
    //?}
    private static final int CHECKBOX_TEXT_COLOR = 0xFFE0E0E0;
    private static final int RARE_ICON_COLOR_RGB = 0xFFD700;

    static final Set<String> cachedDisabledModelIds = new HashSet<>();
    static final Set<String> cachedRareModelIds = new HashSet<>();

    static boolean hasAppliedSessionDefaultPreset = false;

    private enum Tab {
        CURRENT, PRESETS, CUSTOM
    }

    private record CustomCarEntry(String id, String modelId, boolean rare) {
    }

    static final List<CustomCarEntry> cachedCustomCars = new ArrayList<>();

    static void applySyncedCustomCars(String csv) {
        cachedCustomCars.clear();
        if (!csv.isEmpty()) {
            for (String entry : csv.split(",")) {
                String[] parts = entry.split(":");
                if (parts.length == 3) {
                    cachedCustomCars.add(new CustomCarEntry(parts[0], parts[1], Boolean.parseBoolean(parts[2])));
                }
            }
        }
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.execute(() -> {
            if (minecraft.screen instanceof WaypointVehicleListScreen screen && screen.activeTab == Tab.CUSTOM) {
                minecraft.setScreen(screen);
            }
        });
    }

    private Tab activeTab = Tab.CURRENT;

    private enum SpawnState {
        NORMAL, OFF, RARE;

        SpawnState next() {
            return switch (this) {
                case NORMAL -> OFF;
                case OFF -> RARE;
                case RARE -> NORMAL;
            };
        }
    }

    private static final class SpawnCheckbox extends AbstractButton {
        private final Font font;
        private final Consumer<SpawnState> onStateChange;
        private final boolean allowOff;
        private SpawnState state;

        SpawnCheckbox(int x, int y, int width, int height, Font font, Component label,
                      SpawnState initialState, Consumer<SpawnState> onStateChange) {
            this(x, y, width, height, font, label, initialState, onStateChange, true);
        }

        SpawnCheckbox(int x, int y, int width, int height, Font font, Component label,
                      SpawnState initialState, Consumer<SpawnState> onStateChange, boolean allowOff) {
            super(x, y, width, height, label);
            this.font = font;
            this.state = initialState;
            this.onStateChange = onStateChange;
            this.allowOff = allowOff;
        }

        @Override

        //? if >=1.21.9 {
        public void onPress(InputWithModifiers inputWithModifiers) {
        //?} else
        /*public void onPress() {*/
            this.state = this.state.next();
            if (!this.allowOff && this.state == SpawnState.OFF) {
                this.state = this.state.next();
            }
            this.onStateChange.accept(this.state);
        }

        @Override

        //? if >=26 {
        /*protected void extractContents(net.minecraft.client.gui.GuiGraphicsExtractor extractor, int mouseX, int mouseY, float partialTick) {
            mineturismo$renderCheckboxWidget(new GuiGraphics(extractor), mouseX, mouseY, partialTick);
        }*/
        //?}
        //? if >=1.21.11 && <26 {
        /*protected void renderContents(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            mineturismo$renderCheckboxWidget(guiGraphics, mouseX, mouseY, partialTick);
        }*/
        //?}
        //? if >=1.20 && <1.21.11 {
        public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            mineturismo$renderCheckboxWidget(guiGraphics, mouseX, mouseY, partialTick);
        }
        //?}
        //? if >=1.19.4 && <1.20 {
        /*public void renderWidget(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
            mineturismo$renderCheckboxWidget(new GuiGraphics(poseStack), mouseX, mouseY, partialTick);
        }*/
        //?}
        //? if <1.19.4 {
        /*public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
            mineturismo$renderCheckboxWidget(new GuiGraphics(poseStack), mouseX, mouseY, partialTick);
        }*/
        //?}

        private int mineturismo$x() {
            //? if >=1.19.3 {
            return this.getX();
            //?} else
            /*return this.x;*/
        }

        private int mineturismo$y() {
            //? if >=1.19.3 {
            return this.getY();
            //?} else
            /*return this.y;*/
        }

        private void mineturismo$renderCheckboxWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

            //? if >=1.20.5 {
            int boxSize = Checkbox.getBoxSize(this.font);
            //?} else
            /*int boxSize = 17;*/
            boolean checked = this.state != SpawnState.OFF;
            ResourceLocation sprite;
            if (checked) {
                sprite = this.isFocused() ? CHECKBOX_SELECTED_HIGHLIGHTED_SPRITE : CHECKBOX_SELECTED_SPRITE;
            } else {
                sprite = this.isFocused() ? CHECKBOX_HIGHLIGHTED_SPRITE : CHECKBOX_SPRITE;
            }
            int alpha8 = (int) (this.alpha * 255.0F);

            //? if >=1.21.2 {
            int iconColor = this.state == SpawnState.RARE
                    ? ARGB.color(alpha8, RARE_ICON_COLOR_RGB >> 16 & 0xFF, RARE_ICON_COLOR_RGB >> 8 & 0xFF, RARE_ICON_COLOR_RGB & 0xFF)
                    : ARGB.color(alpha8, 255, 255, 255);
            //?} else
            /*int iconColor = this.state == SpawnState.RARE
                    ? FastColor.ARGB32.color(alpha8, RARE_ICON_COLOR_RGB >> 16 & 0xFF, RARE_ICON_COLOR_RGB >> 8 & 0xFF, RARE_ICON_COLOR_RGB & 0xFF)
                    : FastColor.ARGB32.color(alpha8, 255, 255, 255);*/

            //? if >=1.21.6 && <26 {
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, this.mineturismo$x(), this.mineturismo$y(), boxSize, boxSize, iconColor);
            //?}

            //? if >=26 {
            /*guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, this.mineturismo$x(), this.mineturismo$y(), boxSize, boxSize, iconColor);*/
            //?}
            //? if >=1.21.2 && <1.21.6 {
            /*guiGraphics.blitSprite(net.minecraft.client.renderer.RenderType::guiTextured, sprite, this.mineturismo$x(), this.mineturismo$y(), boxSize, boxSize, iconColor);*/
            //?}
            //? if >=1.20.2 && <1.21.2 {
            /*guiGraphics.blitSprite(sprite, this.mineturismo$x(), this.mineturismo$y(), boxSize, boxSize, iconColor);*/
            //?}
            //? if <1.20.2 {
            /*guiGraphics.fill(this.mineturismo$x(), this.mineturismo$y(), this.mineturismo$x() + boxSize, this.mineturismo$y() + boxSize, iconColor);*/
            //?}

            int textX = this.mineturismo$x() + boxSize + 4;
            int textY = this.mineturismo$y() + boxSize / 2 - this.font.lineHeight / 2;
            guiGraphics.drawString(this.font, this.getMessage(), textX, textY, CHECKBOX_TEXT_COLOR);
        }

        //? if >=1.19.3 {
        @Override
        protected void updateWidgetNarration(NarrationElementOutput output) {
            this.defaultButtonNarrationText(output);
        }
        //?} else {
        /*@Override
        public void updateNarration(NarrationElementOutput output) {
            this.defaultButtonNarrationText(output);
        }*/
        //?}
    }

    private final Screen parent;
    private int gridTop;
    private final List<VehicleCheckboxEntry> checkboxEntries = new ArrayList<>();

    private int scrollRow = 0;
    private int maxScrollRow = 0;
    private final DragScrollbar scrollbar = new DragScrollbar(row -> {
        this.scrollRow = Math.max(0, Math.min(this.maxScrollRow, row));
        this.updateCheckboxPositions();
    });

    private record VehicleCheckboxEntry(SpawnCheckbox checkbox, int row) {
    }

    private static final int PRESET_ROW_HEIGHT = 24;
    private final List<PresetButtonEntry> presetEntries = new ArrayList<>();
    private int presetVisibleRows = 0;

    private record PresetButtonEntry(Button button, int row) {
    }

    private static final int CUSTOM_ROW_HEIGHT = 20;
    private static final int CUSTOM_LIST_WIDTH = 330;
    private static final int CUSTOM_DELETE_WIDTH = 44;
    private static final int SLOT_SIZE = 18;
    private static final int DROP_SLOT_SIZE = 24;
    private final List<CustomRowEntry> customEntries = new ArrayList<>();
    private int customVisibleRows = 0;
    private int customListLeft;
    private int customListTop;
    private int dropSlotX;
    private int dropSlotY;
    private int inventoryLeft;
    private int inventoryTop;

    private int draggingSlot = -1;

    private record CustomRowEntry(CustomCarEntry car, SpawnCheckbox checkbox, Button deleteButton, int row) {
    }

    public WaypointVehicleListScreen(Screen parent) {
        super(ModText.translatable("gui.mineturismo.waypoint_traffic.vehicles_title"));
        this.parent = parent;
    }

    private static SpawnState stateOf(String modelId) {
        if (cachedDisabledModelIds.contains(modelId)) {
            return SpawnState.OFF;
        }
        if (cachedRareModelIds.contains(modelId)) {
            return SpawnState.RARE;
        }
        return SpawnState.NORMAL;
    }

    private static void applyState(String modelId, SpawnState state) {
        cachedDisabledModelIds.remove(modelId);
        cachedRareModelIds.remove(modelId);
        switch (state) {
            case OFF -> cachedDisabledModelIds.add(modelId);
            case RARE -> cachedRareModelIds.add(modelId);
            case NORMAL -> {
            }
        }
    }

    private void sendVehicleSettings() {
        SetTownTrafficDisabledVehiclesPayload mineturismo$payload = new SetTownTrafficDisabledVehiclesPayload(
                String.join(",", cachedDisabledModelIds),
                String.join(",", cachedRareModelIds));
        //? if >=1.20.5 {
        ModClientNetworking.sendToServer(mineturismo$payload);
        //?} else {
        /*ModClientNetworking.sendToServer(SetTownTrafficDisabledVehiclesPayload.ID, mineturismo$payload, SetTownTrafficDisabledVehiclesPayload::write);*/
        //?}
    }

    private static void sendCustomCarUpdate(String id, int action) {
        UpdateCustomTrafficCarPayload mineturismo$payload = new UpdateCustomTrafficCarPayload(id, action);
        //? if >=1.20.5 {
        ModClientNetworking.sendToServer(mineturismo$payload);
        //?} else {
        /*ModClientNetworking.sendToServer(UpdateCustomTrafficCarPayload.ID, mineturismo$payload, UpdateCustomTrafficCarPayload::write);*/
        //?}
    }

    private static void sendAddCustomCar(int inventorySlot) {
        AddCustomTrafficCarPayload mineturismo$payload = new AddCustomTrafficCarPayload(inventorySlot);
        //? if >=1.20.5 {
        ModClientNetworking.sendToServer(mineturismo$payload);
        //?} else {
        /*ModClientNetworking.sendToServer(AddCustomTrafficCarPayload.ID, mineturismo$payload, AddCustomTrafficCarPayload::write);*/
        //?}
    }

    private void updateCheckboxPositions() {
        for (VehicleCheckboxEntry entry : this.checkboxEntries) {
            int visibleRow = entry.row() - this.scrollRow;
            boolean isVisible = visibleRow >= 0 && visibleRow < VISIBLE_ROWS;
            entry.checkbox().visible = isVisible;
            entry.checkbox().active = isVisible;
            if (isVisible) {
                ModWidgets.setY(entry.checkbox(), this.gridTop + visibleRow * ROW_HEIGHT);
            }
        }
        for (PresetButtonEntry entry : this.presetEntries) {
            int visibleRow = entry.row() - this.scrollRow;
            boolean isVisible = visibleRow >= 0 && visibleRow < this.presetVisibleRows;
            entry.button().visible = isVisible;
            entry.button().active = isVisible;
            if (isVisible) {
                ModWidgets.setY(entry.button(), this.gridTop + visibleRow * PRESET_ROW_HEIGHT);
            }
        }
        for (CustomRowEntry entry : this.customEntries) {
            int visibleRow = entry.row() - this.scrollRow;
            boolean isVisible = visibleRow >= 0 && visibleRow < this.customVisibleRows;
            entry.checkbox().visible = isVisible;
            entry.checkbox().active = isVisible;
            entry.deleteButton().visible = isVisible;
            entry.deleteButton().active = isVisible;
            if (isVisible) {
                int rowY = this.customListTop + visibleRow * CUSTOM_ROW_HEIGHT;
                ModWidgets.setY(entry.checkbox(), rowY + 4);
                ModWidgets.setY(entry.deleteButton(), rowY + 2);
            }
        }
    }

    private void switchTab(Tab tab) {
        this.activeTab = tab;
        this.scrollRow = 0;
        this.minecraft.setScreen(this);
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        this.gridTop = 62;
        this.checkboxEntries.clear();
        this.presetEntries.clear();
        this.customEntries.clear();
        this.maxScrollRow = 0;
        this.draggingSlot = -1;
        this.scrollbar.setBounds(0, 0, 0, 0, 0);

        net.minecraft.server.packs.resources.ResourceManager resourceManager = this.minecraft.getResourceManager();

        if (!hasAppliedSessionDefaultPreset && cachedDisabledModelIds.isEmpty() && cachedRareModelIds.isEmpty()) {
            com.examplebett.entity.TownTrafficPresetPool.getDefault(resourceManager).ifPresent(preset -> {
                cachedDisabledModelIds.addAll(com.examplebett.entity.TownTrafficPresetPool.computeDisabledIds(preset));
                cachedRareModelIds.addAll(preset.rareModelIds());
            });
            hasAppliedSessionDefaultPreset = true;
        }

        int tabWidth = 110;
        int tabY = 6;
        this.addRenderableWidget(ModWidgets.button(ModText.translatable("gui.mineturismo.waypoint_traffic.tab_current"),
                centerX - tabWidth * 3 / 2, tabY, tabWidth, 16, b -> this.switchTab(Tab.CURRENT)));
        this.addRenderableWidget(ModWidgets.button(ModText.translatable("gui.mineturismo.waypoint_traffic.tab_presets"),
                centerX - tabWidth / 2, tabY, tabWidth, 16, b -> this.switchTab(Tab.PRESETS)));
        this.addRenderableWidget(ModWidgets.button(ModText.translatable("gui.mineturismo.waypoint_traffic.tab_custom"),
                centerX + tabWidth / 2, tabY, tabWidth, 16, b -> this.switchTab(Tab.CUSTOM)));

        if (this.activeTab == Tab.PRESETS) {
            this.initPresetsTab(centerX, resourceManager);
            return;
        }
        if (this.activeTab == Tab.CUSTOM) {
            this.initCustomTab(centerX);
            return;
        }

        List<String> modelIds = AiCarPool.allModelIds(modelId ->
                com.examplebett.client.render.VehicleCategoryLoader.load(resourceManager, modelId)
                        .map(categories -> categories.contains(com.examplebett.entity.VehicleCategory.HELICOPTER)
                                || categories.contains(com.examplebett.entity.VehicleCategory.TRAILER))
                        .orElse(false));
        int gridLeft = centerX - (COLUMNS * COLUMN_WIDTH) / 2;
        int totalRows = (modelIds.size() + COLUMNS - 1) / COLUMNS;
        this.maxScrollRow = Math.max(0, totalRows - VISIBLE_ROWS);
        this.scrollRow = Math.min(this.scrollRow, this.maxScrollRow);

        for (int i = 0; i < modelIds.size(); i++) {
            String modelId = modelIds.get(i);
            int column = i % COLUMNS;
            int row = i / COLUMNS;
            int x = gridLeft + column * COLUMN_WIDTH;
            SpawnCheckbox checkbox = new SpawnCheckbox(x, this.gridTop + row * ROW_HEIGHT, BUTTON_WIDTH, BUTTON_HEIGHT,
                    this.font, vehicleName(modelId), stateOf(modelId), newState -> {
                        applyState(modelId, newState);
                        sendVehicleSettings();
                    });
            this.addRenderableWidget(checkbox);
            this.checkboxEntries.add(new VehicleCheckboxEntry(checkbox, row));
        }

        int viewportHeight = VISIBLE_ROWS * ROW_HEIGHT;
        this.scrollbar.setBounds(gridLeft + COLUMNS * COLUMN_WIDTH + 8, this.gridTop, viewportHeight, VISIBLE_ROWS, totalRows);
        this.updateCheckboxPositions();

        int backY = this.gridTop + viewportHeight + 20;
        this.addRenderableWidget(ModWidgets.button(ModText.translatable("gui.mineturismo.back"), centerX - 100, backY, 200, 20, button ->
                this.minecraft.setScreen(this.parent)));
    }

    private static Component vehicleName(String modelId) {
        Item spawnItem = AiCarPool.spawnItemFor(modelId);
        return spawnItem != null
                ? ModText.translatable(spawnItem.getDescriptionId())
                : ModText.translatable("item.mineturismo." + modelId + "_spawn");
    }

    private void initPresetsTab(int centerX, net.minecraft.server.packs.resources.ResourceManager resourceManager) {
        List<com.examplebett.entity.TownTrafficPresetPool.Preset> presets =
                com.examplebett.entity.TownTrafficPresetPool.list(resourceManager);
        int buttonWidth = 200;

        int fitRows = Math.max(2, (this.height - this.gridTop - 44) / PRESET_ROW_HEIGHT);
        this.presetVisibleRows = Math.min(presets.size(), fitRows);
        this.maxScrollRow = Math.max(0, presets.size() - this.presetVisibleRows);
        this.scrollRow = Math.min(this.scrollRow, this.maxScrollRow);
        for (int i = 0; i < presets.size(); i++) {
            com.examplebett.entity.TownTrafficPresetPool.Preset preset = presets.get(i);
            Button button = this.addRenderableWidget(ModWidgets.button(ModText.literal(preset.label()),
                    centerX - buttonWidth / 2, this.gridTop + i * PRESET_ROW_HEIGHT, buttonWidth, 20, b -> this.applyPreset(preset)));
            this.presetEntries.add(new PresetButtonEntry(button, i));
        }

        int viewportHeight = this.presetVisibleRows * PRESET_ROW_HEIGHT;
        this.scrollbar.setBounds(centerX + buttonWidth / 2 + 8, this.gridTop, viewportHeight - 4, this.presetVisibleRows, presets.size());
        this.updateCheckboxPositions();

        int backY = this.gridTop + viewportHeight + 20;
        this.addRenderableWidget(ModWidgets.button(ModText.translatable("gui.mineturismo.back"), centerX - 100, backY, 200, 20, button ->
                this.minecraft.setScreen(this.parent)));
    }

    private void initCustomTab(int centerX) {
        int backY = this.height - 28;
        int inventoryHeight = SLOT_SIZE * 4 + 4;
        this.inventoryTop = backY - 8 - inventoryHeight;
        this.inventoryLeft = centerX - SLOT_SIZE * 9 / 2;
        this.customListLeft = centerX - CUSTOM_LIST_WIDTH / 2;
        this.dropSlotX = this.customListLeft;
        this.dropSlotY = this.gridTop - 4;
        this.customListTop = this.dropSlotY + DROP_SLOT_SIZE + 6;
        this.customVisibleRows = Math.max(1, (this.inventoryTop - 8 - this.customListTop) / CUSTOM_ROW_HEIGHT);
        this.maxScrollRow = Math.max(0, cachedCustomCars.size() - this.customVisibleRows);
        this.scrollRow = Math.min(this.scrollRow, this.maxScrollRow);

        int checkboxX = this.customListLeft + 20;
        int checkboxWidth = CUSTOM_LIST_WIDTH - 20 - CUSTOM_DELETE_WIDTH - 6;
        for (int i = 0; i < cachedCustomCars.size(); i++) {
            CustomCarEntry car = cachedCustomCars.get(i);
            Component label = ModText.literal("#" + (i + 1) + " ").append(vehicleName(car.modelId()));
            SpawnCheckbox checkbox = new SpawnCheckbox(checkboxX, 0, checkboxWidth, BUTTON_HEIGHT, this.font, label,
                    car.rare() ? SpawnState.RARE : SpawnState.NORMAL,
                    newState -> sendCustomCarUpdate(car.id(), newState == SpawnState.RARE
                            ? UpdateCustomTrafficCarPayload.ACTION_SET_RARE : UpdateCustomTrafficCarPayload.ACTION_SET_NORMAL),
                    false);
            this.addRenderableWidget(checkbox);
            Button deleteButton = this.addRenderableWidget(ModWidgets.button(
                    ModText.translatable("gui.mineturismo.waypoint_traffic.custom_delete"),
                    this.customListLeft + CUSTOM_LIST_WIDTH - CUSTOM_DELETE_WIDTH, 0, CUSTOM_DELETE_WIDTH, 16,
                    b -> sendCustomCarUpdate(car.id(), UpdateCustomTrafficCarPayload.ACTION_REMOVE)));
            this.customEntries.add(new CustomRowEntry(car, checkbox, deleteButton, i));
        }
        this.scrollbar.setBounds(this.customListLeft + CUSTOM_LIST_WIDTH + 6, this.customListTop,
                this.customVisibleRows * CUSTOM_ROW_HEIGHT, this.customVisibleRows, cachedCustomCars.size());
        this.updateCheckboxPositions();

        this.addRenderableWidget(ModWidgets.button(ModText.translatable("gui.mineturismo.back"), centerX - 100, backY, 200, 20, button ->
                this.minecraft.setScreen(this.parent)));
    }

    private void applyPreset(com.examplebett.entity.TownTrafficPresetPool.Preset preset) {
        cachedDisabledModelIds.clear();
        cachedRareModelIds.clear();
        cachedDisabledModelIds.addAll(com.examplebett.entity.TownTrafficPresetPool.computeDisabledIds(preset));
        cachedRareModelIds.addAll(preset.rareModelIds());
        sendVehicleSettings();
        this.switchTab(Tab.CURRENT);
    }

    private int slotX(int slot) {
        return this.inventoryLeft + (slot % 9) * SLOT_SIZE;
    }

    private int slotY(int slot) {
        return slot < 9 ? this.inventoryTop + SLOT_SIZE * 3 + 4 : this.inventoryTop + (slot / 9 - 1) * SLOT_SIZE;
    }

    private int slotAt(double mouseX, double mouseY) {
        for (int slot = 0; slot < 36; slot++) {
            int x = this.slotX(slot);
            int y = this.slotY(slot);
            if (mouseX >= x && mouseX < x + SLOT_SIZE && mouseY >= y && mouseY < y + SLOT_SIZE) {
                return slot;
            }
        }
        return -1;
    }

    private ItemStack inventoryItem(int slot) {
        return this.minecraft.player == null ? ItemStack.EMPTY : this.minecraft.player.getInventory().getItem(slot);
    }

    private boolean overDropSlot(double mouseX, double mouseY) {
        return mouseX >= this.dropSlotX && mouseX < this.dropSlotX + DROP_SLOT_SIZE
                && mouseY >= this.dropSlotY && mouseY < this.dropSlotY + DROP_SLOT_SIZE;
    }

    private boolean mineturismo$mousePressed(double mouseX, double mouseY, int button) {
        if (button != 0) {
            return false;
        }
        if (this.scrollbar.mouseClicked(mouseX, mouseY, this.scrollRow)) {
            return true;
        }
        if (this.activeTab == Tab.CUSTOM) {
            int slot = this.slotAt(mouseX, mouseY);
            if (slot >= 0 && BoxedCarItem.hasCar(this.inventoryItem(slot))) {
                this.draggingSlot = slot;
                return true;
            }
        }
        return false;
    }

    private boolean mineturismo$mouseDragged(double mouseY) {
        return this.scrollbar.mouseDragged(mouseY) || this.draggingSlot >= 0;
    }

    private boolean mineturismo$mouseReleased(double mouseX, double mouseY, int button) {
        if (button != 0) {
            return false;
        }
        this.scrollbar.mouseReleased();
        if (this.draggingSlot >= 0) {
            int slot = this.draggingSlot;
            this.draggingSlot = -1;
            if (this.overDropSlot(mouseX, mouseY) && BoxedCarItem.hasCar(this.inventoryItem(slot))) {
                sendAddCustomCar(slot);
            }
            return true;
        }
        return false;
    }

    @Override
    //? if >=1.21.9 {
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (this.mineturismo$mousePressed(event.x(), event.y(), event.button())) {
            return true;
        }
        return super.mouseClicked(event, doubleClick);
    }
    //?} else {
    /*public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.mineturismo$mousePressed(mouseX, mouseY, button)) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }*/
    //?}

    @Override
    //? if >=1.21.9 {
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        if (event.button() == 0 && this.mineturismo$mouseDragged(event.y())) {
            return true;
        }
        return super.mouseDragged(event, dragX, dragY);
    }
    //?} else {
    /*public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (button == 0 && this.mineturismo$mouseDragged(mouseY)) {
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }*/
    //?}

    @Override
    //? if >=1.21.9 {
    public boolean mouseReleased(MouseButtonEvent event) {
        if (this.mineturismo$mouseReleased(event.x(), event.y(), event.button())) {
            return true;
        }
        return super.mouseReleased(event);
    }
    //?} else {
    /*public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (this.mineturismo$mouseReleased(mouseX, mouseY, button)) {
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }*/
    //?}

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
                this.updateCheckboxPositions();
                return true;
            }
        }
        //? if >=1.20.2 {
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        //?} else {
        /*return super.mouseScrolled(mouseX, mouseY, scrollY);*/
        //?}
    }

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
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, this.gridTop - 32, 0xFFFFFFFF);
        if (this.activeTab == Tab.CURRENT) {
            guiGraphics.drawCenteredString(this.font,
                    ModText.translatable("gui.mineturismo.waypoint_traffic.vehicles_hint"),
                    this.width / 2, this.gridTop - 20, 0xFFAAAAAA);
        }
        if (this.activeTab == Tab.CUSTOM) {
            this.renderCustomTab(guiGraphics, mouseX, mouseY);
        }
        this.scrollbar.render(guiGraphics, mouseX, mouseY, this.scrollRow);
    }

    private void renderCustomTab(GuiGraphics g, int mouseX, int mouseY) {
        g.drawCenteredString(this.font, ModText.translatable("gui.mineturismo.waypoint_traffic.custom_hint"),
                this.width / 2, this.gridTop - 20, 0xFFAAAAAA);

        boolean dropHover = this.draggingSlot >= 0 && this.overDropSlot(mouseX, mouseY);
        g.fill(this.dropSlotX, this.dropSlotY, this.dropSlotX + DROP_SLOT_SIZE, this.dropSlotY + DROP_SLOT_SIZE,
                dropHover ? 0xFFFFD700 : (this.draggingSlot >= 0 ? 0xFFB0B0B0 : 0xFF8B8B8B));
        g.fill(this.dropSlotX + 1, this.dropSlotY + 1, this.dropSlotX + DROP_SLOT_SIZE - 1, this.dropSlotY + DROP_SLOT_SIZE - 1, 0xFF2B2B2B);
        g.drawString(this.font, "+", this.dropSlotX + DROP_SLOT_SIZE / 2 - this.font.width("+") / 2,
                this.dropSlotY + (DROP_SLOT_SIZE - 8) / 2, 0xFF8B8B8B);
        g.drawString(this.font, ModText.translatable("gui.mineturismo.waypoint_traffic.custom_drop"),
                this.dropSlotX + DROP_SLOT_SIZE + 6, this.dropSlotY + 3, 0xFFFFFFFF);
        g.drawString(this.font, ModText.translatable("gui.mineturismo.waypoint_traffic.custom_count",
                        cachedCustomCars.size(), com.examplebett.waypoint.WaypointNetworkSavedData.MAX_CUSTOM_TRAFFIC_CARS),
                this.dropSlotX + DROP_SLOT_SIZE + 6, this.dropSlotY + 14, 0xFFAAAAAA);

        if (cachedCustomCars.isEmpty()) {
            g.drawString(this.font, ModText.translatable("gui.mineturismo.waypoint_traffic.custom_empty"),
                    this.customListLeft, this.customListTop + 6, 0xFF808080);
        }
        for (CustomRowEntry entry : this.customEntries) {
            int visibleRow = entry.row() - this.scrollRow;
            if (visibleRow < 0 || visibleRow >= this.customVisibleRows) {
                continue;
            }
            Item spawnItem = AiCarPool.spawnItemFor(entry.car().modelId());
            if (spawnItem != null) {
                this.renderItemIcon(g, new ItemStack(spawnItem), this.customListLeft, this.customListTop + visibleRow * CUSTOM_ROW_HEIGHT + 2);
            }
        }

        g.drawString(this.font, ModText.translatable("gui.mineturismo.waypoint_traffic.custom_inventory"),
                this.inventoryLeft, this.inventoryTop - 10, 0xFFAAAAAA);
        for (int slot = 0; slot < 36; slot++) {
            int x = this.slotX(slot);
            int y = this.slotY(slot);
            g.fill(x, y, x + SLOT_SIZE, y + SLOT_SIZE, 0xFF8B8B8B);
            g.fill(x + 1, y + 1, x + SLOT_SIZE - 1, y + SLOT_SIZE - 1, 0xFF373737);
            ItemStack stack = this.inventoryItem(slot);
            if (stack.isEmpty() || slot == this.draggingSlot) {
                continue;
            }
            this.renderItemIcon(g, stack, x + 1, y + 1);
            if (!BoxedCarItem.hasCar(stack)) {
                g.fill(x + 1, y + 1, x + SLOT_SIZE - 1, y + SLOT_SIZE - 1, 0xA0373737);
            } else if (mouseX >= x && mouseX < x + SLOT_SIZE && mouseY >= y && mouseY < y + SLOT_SIZE && this.draggingSlot < 0) {
                g.fill(x + 1, y + 1, x + SLOT_SIZE - 1, y + SLOT_SIZE - 1, 0x60FFFFFF);
            }
        }

        if (this.draggingSlot >= 0) {
            this.renderItemIcon(g, this.inventoryItem(this.draggingSlot), mouseX - 8, mouseY - 8);
        }
    }

    private void renderItemIcon(GuiGraphics g, ItemStack stack, int x, int y) {
        g.renderItem(stack, x, y);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
