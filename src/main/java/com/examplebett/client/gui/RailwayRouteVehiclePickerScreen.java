//? if >=1.18 {
//?}
//? if <1.18 && >=1.17 {
/*
package com.examplebett.client.gui;
import com.examplebett.compat.ModWidgets;
import com.examplebett.compat.ModText;

import com.examplebett.client.render.VehicleCategoryLoader;
import com.examplebett.compat.ModClientNetworking;
import com.examplebett.entity.VehicleCategory;
import com.examplebett.network.SetRailwayRouteVehiclePayload;
import com.examplebett.util.AiCarPool;

import com.examplebett.compat.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class RailwayRouteVehiclePickerScreen extends Screen {

    private static final int COLUMNS = 2;
    private static final int BUTTON_WIDTH = 150;
    private static final int BUTTON_HEIGHT = 20;
    private static final int COL_GAP = 8;
    private static final int ROW_GAP = 4;
    private static final int VISIBLE_ROWS = 6;
    private static final int ROW_HEIGHT = BUTTON_HEIGHT + ROW_GAP;

    private final Screen parent;
    private final int originNodeId;
    private final boolean railwayOnly;
    private List<String> options = List.of();

    private final List<SlotButtonEntry> slotButtons = new ArrayList<>();
    private int scrollRow = 0;
    private int maxScrollRow = 0;
    private int viewportY;

    private record SlotButtonEntry(Button button, int row) {
    }

    public RailwayRouteVehiclePickerScreen(Screen parent, int originNodeId, boolean railwayOnly) {
        super(ModText.translatable(railwayOnly
                ? "gui.mineturismo.railway_spawn.pick_existing"
                : "gui.mineturismo.railway_spawn.pick_any"));
        this.parent = parent;
        this.originNodeId = originNodeId;
        this.railwayOnly = railwayOnly;
    }

    private void updateButtonPositions() {
        int viewportTop = this.viewportY;
        for (SlotButtonEntry entry : this.slotButtons) {
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
                this.updateButtonPositions();
                return true;
            }
        }
        return super.mouseScrolled(mouseX, mouseY, scrollY);
    }

    private String labelFor(String modelId) {
        Item item = AiCarPool.spawnItemFor(modelId);
        return item != null ? new ItemStack(item).getHoverName().getString() : modelId;
    }

    private void pick(String modelId) {
        SetRailwayRouteVehiclePayload payload = new SetRailwayRouteVehiclePayload(this.originNodeId, modelId);
        ModClientNetworking.sendToServer(SetRailwayRouteVehiclePayload.ID, payload, SetRailwayRouteVehiclePayload::write);
        this.minecraft.setScreen(null);
    }

    @Override
    protected void init() {
        ResourceManager resourceManager = this.minecraft.getResourceManager();
        List<String> options = new ArrayList<>();
        for (String modelId : AiCarPool.allModelIds(id -> false)) {
            if (!this.railwayOnly) {
                options.add(modelId);
                continue;
            }
            VehicleCategoryLoader.load(resourceManager, modelId)
                    .ifPresent(categories -> {
                        if (categories.contains(VehicleCategory.RAILWAY)) {
                            options.add(modelId);
                        }
                    });
        }
        this.options = options;

        this.slotButtons.clear();
        this.scrollRow = 0;

        int centerX = this.width / 2;
        int gridWidth = COLUMNS * BUTTON_WIDTH + (COLUMNS - 1) * COL_GAP;
        int gridX = centerX - gridWidth / 2;
        int gridY = this.height / 2 - 60;
        this.viewportY = gridY;

        int totalRows = (this.options.size() + COLUMNS - 1) / COLUMNS;
        this.maxScrollRow = Math.max(0, totalRows - VISIBLE_ROWS);

        for (int i = 0; i < this.options.size(); i++) {
            String modelId = this.options.get(i);
            int row = i / COLUMNS;
            int col = i % COLUMNS;
            int x = gridX + col * (BUTTON_WIDTH + COL_GAP);
            Button button = ModWidgets.button(ModText.literal(this.labelFor(modelId)), x, gridY + row * ROW_HEIGHT, BUTTON_WIDTH, BUTTON_HEIGHT, b ->
                    this.pick(modelId));
            this.addRenderableWidget(button);
            this.slotButtons.add(new SlotButtonEntry(button, row));
        }

        int viewportHeight = VISIBLE_ROWS * ROW_HEIGHT - ROW_GAP;
        int arrowX = gridX + gridWidth + 8;

        this.addRenderableWidget(new ScrollbarWidget(ScrollbarWidget.xInArrowColumn(arrowX), gridY,
                gridY + viewportHeight - 20 + 20 - (gridY), VISIBLE_ROWS, this.maxScrollRow + VISIBLE_ROWS, () -> this.scrollRow, row -> {
                    this.scrollRow = row;
                    this.updateButtonPositions();
                }));

        this.updateButtonPositions();

        int backWidth = 200;
        this.addRenderableWidget(ModWidgets.button(ModText.translatable("gui.mineturismo.back"), centerX - backWidth / 2, gridY + viewportHeight + 8, backWidth, BUTTON_HEIGHT, b ->
                this.minecraft.setScreen(this.parent)));
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        mineturismo$renderScreen(new GuiGraphics(poseStack), mouseX, mouseY, partialTick);
    }

    private void mineturismo$renderScreen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        super.render(guiGraphics.pose(), mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - 80, 0xFFFFFFFF);
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

import com.examplebett.client.render.VehicleCategoryLoader;
import com.examplebett.compat.ModClientNetworking;
import com.examplebett.entity.VehicleCategory;
import com.examplebett.network.SetRailwayRouteVehiclePayload;
import com.examplebett.util.AiCarPool;

import com.examplebett.compat.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class RailwayRouteVehiclePickerScreen extends Screen {

    private static final int COLUMNS = 2;
    private static final int BUTTON_WIDTH = 150;
    private static final int BUTTON_HEIGHT = 20;
    private static final int COL_GAP = 8;
    private static final int ROW_GAP = 4;
    private static final int VISIBLE_ROWS = 6;
    private static final int ROW_HEIGHT = BUTTON_HEIGHT + ROW_GAP;

    private final Screen parent;
    private final int originNodeId;
    private final boolean railwayOnly;
    private List<String> options = List.of();

    private final List<SlotButtonEntry> slotButtons = new ArrayList<>();
    private int scrollRow = 0;
    private int maxScrollRow = 0;
    private int viewportY;

    private record SlotButtonEntry(Button button, int row) {
    }

    public RailwayRouteVehiclePickerScreen(Screen parent, int originNodeId, boolean railwayOnly) {
        super(ModText.translatable(railwayOnly
                ? "gui.mineturismo.railway_spawn.pick_existing"
                : "gui.mineturismo.railway_spawn.pick_any"));
        this.parent = parent;
        this.originNodeId = originNodeId;
        this.railwayOnly = railwayOnly;
    }

    private void updateButtonPositions() {
        int viewportTop = this.viewportY;
        for (SlotButtonEntry entry : this.slotButtons) {
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
                this.updateButtonPositions();
                return true;
            }
        }
        return super.mouseScrolled(mouseX, mouseY, scrollY);
    }

    private String labelFor(String modelId) {
        Item item = AiCarPool.spawnItemFor(modelId);
        return item != null ? new ItemStack(item).getHoverName().getString() : modelId;
    }

    private void pick(String modelId) {
        SetRailwayRouteVehiclePayload payload = new SetRailwayRouteVehiclePayload(this.originNodeId, modelId);
        ModClientNetworking.sendToServer(SetRailwayRouteVehiclePayload.ID, payload, SetRailwayRouteVehiclePayload::write);
        this.minecraft.setScreen(null);
    }

    @Override
    protected void init() {
        ResourceManager resourceManager = this.minecraft.getResourceManager();
        List<String> options = new ArrayList<>();
        for (String modelId : AiCarPool.allModelIds(id -> false)) {
            if (!this.railwayOnly) {
                options.add(modelId);
                continue;
            }
            VehicleCategoryLoader.load(resourceManager, modelId)
                    .ifPresent(categories -> {
                        if (categories.contains(VehicleCategory.RAILWAY)) {
                            options.add(modelId);
                        }
                    });
        }
        this.options = options;

        this.slotButtons.clear();
        this.scrollRow = 0;

        int centerX = this.width / 2;
        int gridWidth = COLUMNS * BUTTON_WIDTH + (COLUMNS - 1) * COL_GAP;
        int gridX = centerX - gridWidth / 2;
        int gridY = this.height / 2 - 60;
        this.viewportY = gridY;

        int totalRows = (this.options.size() + COLUMNS - 1) / COLUMNS;
        this.maxScrollRow = Math.max(0, totalRows - VISIBLE_ROWS);

        for (int i = 0; i < this.options.size(); i++) {
            String modelId = this.options.get(i);
            int row = i / COLUMNS;
            int col = i % COLUMNS;
            int x = gridX + col * (BUTTON_WIDTH + COL_GAP);
            Button button = ModWidgets.button(ModText.literal(this.labelFor(modelId)), x, gridY + row * ROW_HEIGHT, BUTTON_WIDTH, BUTTON_HEIGHT, b ->
                    this.pick(modelId));
            this.addButton(button);
            this.slotButtons.add(new SlotButtonEntry(button, row));
        }

        int viewportHeight = VISIBLE_ROWS * ROW_HEIGHT - ROW_GAP;
        int arrowX = gridX + gridWidth + 8;

        this.addButton(new ScrollbarWidget(ScrollbarWidget.xInArrowColumn(arrowX), gridY,
                gridY + viewportHeight - 20 + 20 - (gridY), VISIBLE_ROWS, this.maxScrollRow + VISIBLE_ROWS, () -> this.scrollRow, row -> {
                    this.scrollRow = row;
                    this.updateButtonPositions();
                }));

        this.updateButtonPositions();

        int backWidth = 200;
        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.back"), centerX - backWidth / 2, gridY + viewportHeight + 8, backWidth, BUTTON_HEIGHT, b ->
                this.minecraft.setScreen(this.parent)));
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        mineturismo$renderScreen(new GuiGraphics(poseStack), mouseX, mouseY, partialTick);
    }

    private void mineturismo$renderScreen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        super.render(guiGraphics.pose(), mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - 80, 0xFFFFFFFF);
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

import com.examplebett.client.render.VehicleCategoryLoader;
import com.examplebett.compat.ModClientNetworking;
import com.examplebett.entity.VehicleCategory;
import com.examplebett.network.SetRailwayRouteVehiclePayload;
import com.examplebett.util.AiCarPool;

import com.examplebett.compat.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class RailwayRouteVehiclePickerScreen extends Screen {

    private static final int COLUMNS = 2;
    private static final int BUTTON_WIDTH = 150;
    private static final int BUTTON_HEIGHT = 20;
    private static final int COL_GAP = 8;
    private static final int ROW_GAP = 4;
    private static final int VISIBLE_ROWS = 6;
    private static final int ROW_HEIGHT = BUTTON_HEIGHT + ROW_GAP;

    private final Screen parent;
    private final int originNodeId;
    private final boolean railwayOnly;
    private List<String> options = List.of();

    private final List<SlotButtonEntry> slotButtons = new ArrayList<>();
    private int scrollRow = 0;
    private int maxScrollRow = 0;
    private int viewportY;

    private record SlotButtonEntry(Button button, int row) {
    }

    public RailwayRouteVehiclePickerScreen(Screen parent, int originNodeId, boolean railwayOnly) {
        super(ModText.translatable(railwayOnly
                ? "gui.mineturismo.railway_spawn.pick_existing"
                : "gui.mineturismo.railway_spawn.pick_any"));
        this.parent = parent;
        this.originNodeId = originNodeId;
        this.railwayOnly = railwayOnly;
    }

    private void updateButtonPositions() {
        int viewportTop = this.viewportY;
        for (SlotButtonEntry entry : this.slotButtons) {
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
                this.updateButtonPositions();
                return true;
            }
        }
        return super.mouseScrolled(mouseX, mouseY, scrollY);
    }

    private String labelFor(String modelId) {
        Item item = AiCarPool.spawnItemFor(modelId);
        return item != null ? new ItemStack(item).getHoverName().getString() : modelId;
    }

    private void pick(String modelId) {
        SetRailwayRouteVehiclePayload payload = new SetRailwayRouteVehiclePayload(this.originNodeId, modelId);
        ModClientNetworking.sendToServer(SetRailwayRouteVehiclePayload.ID, payload, SetRailwayRouteVehiclePayload::write);
        this.minecraft.setScreen(null);
    }

    @Override
    protected void init() {
        ResourceManager resourceManager = this.minecraft.getResourceManager();
        List<String> options = new ArrayList<>();
        for (String modelId : AiCarPool.allModelIds(id -> false)) {
            if (!this.railwayOnly) {
                options.add(modelId);
                continue;
            }
            VehicleCategoryLoader.load(resourceManager, modelId)
                    .ifPresent(categories -> {
                        if (categories.contains(VehicleCategory.RAILWAY)) {
                            options.add(modelId);
                        }
                    });
        }
        this.options = options;

        this.slotButtons.clear();
        this.scrollRow = 0;

        int centerX = this.width / 2;
        int gridWidth = COLUMNS * BUTTON_WIDTH + (COLUMNS - 1) * COL_GAP;
        int gridX = centerX - gridWidth / 2;
        int gridY = this.height / 2 - 60;
        this.viewportY = gridY;

        int totalRows = (this.options.size() + COLUMNS - 1) / COLUMNS;
        this.maxScrollRow = Math.max(0, totalRows - VISIBLE_ROWS);

        for (int i = 0; i < this.options.size(); i++) {
            String modelId = this.options.get(i);
            int row = i / COLUMNS;
            int col = i % COLUMNS;
            int x = gridX + col * (BUTTON_WIDTH + COL_GAP);
            Button button = ModWidgets.button(ModText.literal(this.labelFor(modelId)), x, gridY + row * ROW_HEIGHT, BUTTON_WIDTH, BUTTON_HEIGHT, b ->
                    this.pick(modelId));
            this.addButton(button);
            this.slotButtons.add(new SlotButtonEntry(button, row));
        }

        int viewportHeight = VISIBLE_ROWS * ROW_HEIGHT - ROW_GAP;
        int arrowX = gridX + gridWidth + 8;

        this.addButton(new ScrollbarWidget(ScrollbarWidget.xInArrowColumn(arrowX), gridY,
                gridY + viewportHeight - 20 + 20 - (gridY), VISIBLE_ROWS, this.maxScrollRow + VISIBLE_ROWS, () -> this.scrollRow, row -> {
                    this.scrollRow = row;
                    this.updateButtonPositions();
                }));

        this.updateButtonPositions();

        int backWidth = 200;
        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.back"), centerX - backWidth / 2, gridY + viewportHeight + 8, backWidth, BUTTON_HEIGHT, b ->
                this.minecraft.setScreen(this.parent)));
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTick) {
        mineturismo$renderScreen(new GuiGraphics(null), mouseX, mouseY, partialTick);
    }

    private void mineturismo$renderScreen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        super.render(mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - 80, 0xFFFFFFFF);
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

import com.examplebett.client.render.VehicleCategoryLoader;
import com.examplebett.compat.ModClientNetworking;
import com.examplebett.entity.VehicleCategory;
import com.examplebett.network.SetRailwayRouteVehiclePayload;
import com.examplebett.util.AiCarPool;

import com.examplebett.compat.GuiGraphics;
import com.examplebett.compat.v114.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class RailwayRouteVehiclePickerScreen extends Screen {

    private static final int COLUMNS = 2;
    private static final int BUTTON_WIDTH = 150;
    private static final int BUTTON_HEIGHT = 20;
    private static final int COL_GAP = 8;
    private static final int ROW_GAP = 4;
    private static final int VISIBLE_ROWS = 6;
    private static final int ROW_HEIGHT = BUTTON_HEIGHT + ROW_GAP;

    private final Screen parent;
    private final int originNodeId;
    private final boolean railwayOnly;
    private List<String> options = List.of();

    private final List<SlotButtonEntry> slotButtons = new ArrayList<>();
    private int scrollRow = 0;
    private int maxScrollRow = 0;
    private int viewportY;

    private record SlotButtonEntry(Button button, int row) {
    }

    public RailwayRouteVehiclePickerScreen(Screen parent, int originNodeId, boolean railwayOnly) {
        super(ModText.translatable(railwayOnly
                ? "gui.mineturismo.railway_spawn.pick_existing"
                : "gui.mineturismo.railway_spawn.pick_any"));
        this.parent = parent;
        this.originNodeId = originNodeId;
        this.railwayOnly = railwayOnly;
    }

    private void updateButtonPositions() {
        int viewportTop = this.viewportY;
        for (SlotButtonEntry entry : this.slotButtons) {
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
                this.updateButtonPositions();
                return true;
            }
        }
        return super.mouseScrolled(mouseX, mouseY, scrollY);
    }

    private String labelFor(String modelId) {
        Item item = AiCarPool.spawnItemFor(modelId);
        return item != null ? new ItemStack(item).getHoverName().getString() : modelId;
    }

    private void pick(String modelId) {
        SetRailwayRouteVehiclePayload payload = new SetRailwayRouteVehiclePayload(this.originNodeId, modelId);
        ModClientNetworking.sendToServer(SetRailwayRouteVehiclePayload.ID, payload, SetRailwayRouteVehiclePayload::write);
        this.minecraft.setScreen(null);
    }

    @Override
    protected void init() {
        ResourceManager resourceManager = this.minecraft.getResourceManager();
        List<String> options = new ArrayList<>();
        for (String modelId : AiCarPool.allModelIds(id -> false)) {
            if (!this.railwayOnly) {
                options.add(modelId);
                continue;
            }
            VehicleCategoryLoader.load(resourceManager, modelId)
                    .ifPresent(categories -> {
                        if (categories.contains(VehicleCategory.RAILWAY)) {
                            options.add(modelId);
                        }
                    });
        }
        this.options = options;

        this.slotButtons.clear();
        this.scrollRow = 0;

        int centerX = this.width / 2;
        int gridWidth = COLUMNS * BUTTON_WIDTH + (COLUMNS - 1) * COL_GAP;
        int gridX = centerX - gridWidth / 2;
        int gridY = this.height / 2 - 60;
        this.viewportY = gridY;

        int totalRows = (this.options.size() + COLUMNS - 1) / COLUMNS;
        this.maxScrollRow = Math.max(0, totalRows - VISIBLE_ROWS);

        for (int i = 0; i < this.options.size(); i++) {
            String modelId = this.options.get(i);
            int row = i / COLUMNS;
            int col = i % COLUMNS;
            int x = gridX + col * (BUTTON_WIDTH + COL_GAP);
            Button button = ModWidgets.button(ModText.literal(this.labelFor(modelId)), x, gridY + row * ROW_HEIGHT, BUTTON_WIDTH, BUTTON_HEIGHT, b ->
                    this.pick(modelId));
            this.addButton(button);
            this.slotButtons.add(new SlotButtonEntry(button, row));
        }

        int viewportHeight = VISIBLE_ROWS * ROW_HEIGHT - ROW_GAP;
        int arrowX = gridX + gridWidth + 8;

        this.addButton(new ScrollbarWidget(ScrollbarWidget.xInArrowColumn(arrowX), gridY,
                gridY + viewportHeight - 20 + 20 - (gridY), VISIBLE_ROWS, this.maxScrollRow + VISIBLE_ROWS, () -> this.scrollRow, row -> {
                    this.scrollRow = row;
                    this.updateButtonPositions();
                }));

        this.updateButtonPositions();

        int backWidth = 200;
        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.back"), centerX - backWidth / 2, gridY + viewportHeight + 8, backWidth, BUTTON_HEIGHT, b ->
                this.minecraft.setScreen(this.parent)));
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTick) {
        mineturismo$renderScreen(new GuiGraphics(null), mouseX, mouseY, partialTick);
    }

    private void mineturismo$renderScreen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        super.render(mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - 80, 0xFFFFFFFF);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
*/
//?}
