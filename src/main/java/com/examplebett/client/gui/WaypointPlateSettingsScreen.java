//? if >=1.18 {
//?}
//? if <1.18 && >=1.17 {
/*
package com.examplebett.client.gui;
import com.examplebett.compat.ModWidgets;
import com.examplebett.compat.ModText;

import com.examplebett.compat.ModClientNetworking;
import com.examplebett.client.render.PlateTextures;
import com.examplebett.network.SetTownTrafficPlateIdsPayload;
import net.minecraft.client.gui.Font;

import com.examplebett.compat.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class WaypointPlateSettingsScreen extends Screen {

    private static final int COLUMNS = 2;
    private static final int COLUMN_WIDTH = 200;
    private static final int BUTTON_WIDTH = 190;
    private static final int ROW_HEIGHT = 14;
    private static final int BUTTON_HEIGHT = 12;
    private static final int VISIBLE_ROWS = 8;

    private static final ResourceLocation CHECKBOX_SELECTED_HIGHLIGHTED_SPRITE =
            new ResourceLocation("widget/checkbox_selected_highlighted");
    private static final ResourceLocation CHECKBOX_SELECTED_SPRITE =
            new ResourceLocation("widget/checkbox_selected");
    private static final ResourceLocation CHECKBOX_HIGHLIGHTED_SPRITE =
            new ResourceLocation("widget/checkbox_highlighted");
    private static final ResourceLocation CHECKBOX_SPRITE =
            new ResourceLocation("widget/checkbox");
    private static final int CHECKBOX_TEXT_COLOR = 0xFFE0E0E0;
    private static final int EXCLUSIVE_ICON_COLOR_RGB = 0xFFD700;

    static final Set<String> cachedTrafficPlateIds = new HashSet<>();
    static final Set<String> cachedExclusivePlateIds = new HashSet<>();

    private enum PlateState {
        OFF, POOL, EXCLUSIVE;

        PlateState next() {
            return switch (this) {
                case OFF -> POOL;
                case POOL -> EXCLUSIVE;
                case EXCLUSIVE -> OFF;
            };
        }
    }

    private static final class PlateCheckbox extends AbstractButton {
        private final Font font;
        private final Consumer<PlateState> onStateChange;
        private PlateState state;

        PlateCheckbox(int x, int y, int width, int height, Font font, Component label,
                      PlateState initialState, Consumer<PlateState> onStateChange) {
            super(x, y, width, height, label);
            this.font = font;
            this.state = initialState;
            this.onStateChange = onStateChange;
        }

        @Override
        public void onPress() {
            this.state = this.state.next();
            this.onStateChange.accept(this.state);
        }

        @Override

        public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
            mineturismo$renderCheckboxWidget(new GuiGraphics(poseStack), mouseX, mouseY, partialTick);
        }

        private int mineturismo$x() {
            return this.x;
        }

        private int mineturismo$y() {
            return this.y;
        }

        private void mineturismo$renderCheckboxWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            int boxSize = 17;
            boolean checked = this.state != PlateState.OFF;
            ResourceLocation sprite;
            if (checked) {
                sprite = this.isFocused() ? CHECKBOX_SELECTED_HIGHLIGHTED_SPRITE : CHECKBOX_SELECTED_SPRITE;
            } else {
                sprite = this.isFocused() ? CHECKBOX_HIGHLIGHTED_SPRITE : CHECKBOX_SPRITE;
            }
            int alpha8 = (int) (this.alpha * 255.0F);
            int iconColor = this.state == PlateState.EXCLUSIVE
                    ? FastColor.ARGB32.color(alpha8, EXCLUSIVE_ICON_COLOR_RGB >> 16 & 0xFF, EXCLUSIVE_ICON_COLOR_RGB >> 8 & 0xFF, EXCLUSIVE_ICON_COLOR_RGB & 0xFF)
                    : FastColor.ARGB32.color(alpha8, 255, 255, 255);

            WaypointVehicleListScreen.drawPlainCheckbox(guiGraphics, this.mineturismo$x(), this.mineturismo$y(), boxSize, checked, this.isFocused(), iconColor);

            int textX = this.mineturismo$x() + boxSize + 4;
            int textY = this.mineturismo$y() + boxSize / 2 - this.font.lineHeight / 2;
            guiGraphics.drawString(this.font, this.getMessage(), textX, textY, CHECKBOX_TEXT_COLOR);
        }

        @Override
        public void updateNarration(NarrationElementOutput output) {
            this.defaultButtonNarrationText(output);
        }
    }

    private final Screen parent;
    private int gridTop;
    private final List<PlateCheckboxEntry> checkboxEntries = new ArrayList<>();
    private int scrollRow = 0;
    private int maxScrollRow = 0;

    private record PlateCheckboxEntry(PlateCheckbox checkbox, int row) {
    }

    public WaypointPlateSettingsScreen(Screen parent) {
        super(ModText.translatable("gui.mineturismo.waypoint_traffic.plate_title"));
        this.parent = parent;
    }

    private static PlateState stateOf(String plateId) {
        if (cachedExclusivePlateIds.contains(plateId)) {
            return PlateState.EXCLUSIVE;
        }
        if (cachedTrafficPlateIds.contains(plateId)) {
            return PlateState.POOL;
        }
        return PlateState.OFF;
    }

    private static void applyState(String plateId, PlateState state) {
        cachedTrafficPlateIds.remove(plateId);
        cachedExclusivePlateIds.remove(plateId);
        switch (state) {
            case POOL -> cachedTrafficPlateIds.add(plateId);
            case EXCLUSIVE -> cachedExclusivePlateIds.add(plateId);
            case OFF -> {
            }
        }
    }

    private void sendPlateIds() {
        SetTownTrafficPlateIdsPayload mineturismo$payload = new SetTownTrafficPlateIdsPayload(
                String.join(",", cachedTrafficPlateIds), String.join(",", cachedExclusivePlateIds));
        ModClientNetworking.sendToServer(SetTownTrafficPlateIdsPayload.ID, mineturismo$payload, SetTownTrafficPlateIdsPayload::write);
    }

    private void updateCheckboxPositions() {
        for (PlateCheckboxEntry entry : this.checkboxEntries) {
            int visibleRow = entry.row() - this.scrollRow;
            boolean isVisible = visibleRow >= 0 && visibleRow < VISIBLE_ROWS;
            entry.checkbox().visible = isVisible;
            entry.checkbox().active = isVisible;
            if (isVisible) {
                ModWidgets.setY(entry.checkbox(), this.gridTop + visibleRow * ROW_HEIGHT);
            }
        }
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        this.gridTop = 50;
        this.checkboxEntries.clear();
        this.scrollRow = 0;

        List<PlateTextures.Design> designs = PlateTextures.DESIGNS;
        int gridLeft = centerX - (COLUMNS * COLUMN_WIDTH) / 2;
        int totalRows = (designs.size() + COLUMNS - 1) / COLUMNS;
        this.maxScrollRow = Math.max(0, totalRows - VISIBLE_ROWS);

        for (int i = 0; i < designs.size(); i++) {
            PlateTextures.Design design = designs.get(i);
            String plateId = design.id();
            int column = i % COLUMNS;
            int row = i / COLUMNS;
            int x = gridLeft + column * COLUMN_WIDTH;
            PlateCheckbox checkbox = new PlateCheckbox(x, this.gridTop + row * ROW_HEIGHT, BUTTON_WIDTH, BUTTON_HEIGHT,
                    this.font, design.displayName(), stateOf(plateId), newState -> {
                        applyState(plateId, newState);
                        sendPlateIds();
                    });
            this.addRenderableWidget(checkbox);
            this.checkboxEntries.add(new PlateCheckboxEntry(checkbox, row));
        }

        int viewportHeight = VISIBLE_ROWS * ROW_HEIGHT;
        int arrowX = gridLeft + COLUMNS * COLUMN_WIDTH + 8;

        this.addRenderableWidget(new ScrollbarWidget(ScrollbarWidget.xInArrowColumn(arrowX), this.gridTop,
                this.gridTop + viewportHeight - 20 + 20 - (this.gridTop), VISIBLE_ROWS, this.maxScrollRow + VISIBLE_ROWS, () -> this.scrollRow, row -> {
                    this.scrollRow = row;
                    this.updateCheckboxPositions();
                }));

        this.updateCheckboxPositions();

        int backY = this.gridTop + viewportHeight + 20;
        this.addRenderableWidget(ModWidgets.button(ModText.translatable("gui.mineturismo.back"), centerX - 100, backY, 200, 20, button ->
                this.minecraft.setScreen(this.parent)));
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
                this.updateCheckboxPositions();
                return true;
            }
        }
        return super.mouseScrolled(mouseX, mouseY, scrollY);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        mineturismo$renderScreen(new GuiGraphics(poseStack), mouseX, mouseY, partialTick);
    }

    private void mineturismo$renderScreen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        super.render(guiGraphics.pose(), mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, this.gridTop - 32, 0xFFFFFFFF);
        guiGraphics.drawCenteredString(this.font,
                ModText.translatable("gui.mineturismo.waypoint_traffic.plate_hint"),
                this.width / 2, this.gridTop - 20, 0xFFAAAAAA);
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
import com.examplebett.client.render.PlateTextures;
import com.examplebett.network.SetTownTrafficPlateIdsPayload;
import net.minecraft.client.gui.Font;

import com.examplebett.compat.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class WaypointPlateSettingsScreen extends Screen {

    private static final int COLUMNS = 2;
    private static final int COLUMN_WIDTH = 200;
    private static final int BUTTON_WIDTH = 190;
    private static final int ROW_HEIGHT = 14;
    private static final int BUTTON_HEIGHT = 12;
    private static final int VISIBLE_ROWS = 8;

    private static final ResourceLocation CHECKBOX_SELECTED_HIGHLIGHTED_SPRITE =
            new ResourceLocation("widget/checkbox_selected_highlighted");
    private static final ResourceLocation CHECKBOX_SELECTED_SPRITE =
            new ResourceLocation("widget/checkbox_selected");
    private static final ResourceLocation CHECKBOX_HIGHLIGHTED_SPRITE =
            new ResourceLocation("widget/checkbox_highlighted");
    private static final ResourceLocation CHECKBOX_SPRITE =
            new ResourceLocation("widget/checkbox");
    private static final int CHECKBOX_TEXT_COLOR = 0xFFE0E0E0;
    private static final int EXCLUSIVE_ICON_COLOR_RGB = 0xFFD700;

    static final Set<String> cachedTrafficPlateIds = new HashSet<>();
    static final Set<String> cachedExclusivePlateIds = new HashSet<>();

    private enum PlateState {
        OFF, POOL, EXCLUSIVE;

        PlateState next() {
            return switch (this) {
                case OFF -> POOL;
                case POOL -> EXCLUSIVE;
                case EXCLUSIVE -> OFF;
            };
        }
    }

    private static final class PlateCheckbox extends AbstractButton {
        private final Font font;
        private final Consumer<PlateState> onStateChange;
        private PlateState state;

        PlateCheckbox(int x, int y, int width, int height, Font font, Component label,
                      PlateState initialState, Consumer<PlateState> onStateChange) {
            super(x, y, width, height, label);
            this.font = font;
            this.state = initialState;
            this.onStateChange = onStateChange;
        }

        @Override
        public void onPress() {
            this.state = this.state.next();
            this.onStateChange.accept(this.state);
        }

        @Override

        public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
            mineturismo$renderCheckboxWidget(new GuiGraphics(poseStack), mouseX, mouseY, partialTick);
        }

        private int mineturismo$x() {
            return this.x;
        }

        private int mineturismo$y() {
            return this.y;
        }

        private void mineturismo$renderCheckboxWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            int boxSize = 17;
            boolean checked = this.state != PlateState.OFF;
            ResourceLocation sprite;
            if (checked) {
                sprite = this.isFocused() ? CHECKBOX_SELECTED_HIGHLIGHTED_SPRITE : CHECKBOX_SELECTED_SPRITE;
            } else {
                sprite = this.isFocused() ? CHECKBOX_HIGHLIGHTED_SPRITE : CHECKBOX_SPRITE;
            }
            int alpha8 = (int) (this.alpha * 255.0F);
            int iconColor = this.state == PlateState.EXCLUSIVE
                    ? FastColor.ARGB32.color(alpha8, EXCLUSIVE_ICON_COLOR_RGB >> 16 & 0xFF, EXCLUSIVE_ICON_COLOR_RGB >> 8 & 0xFF, EXCLUSIVE_ICON_COLOR_RGB & 0xFF)
                    : FastColor.ARGB32.color(alpha8, 255, 255, 255);

            WaypointVehicleListScreen.drawPlainCheckbox(guiGraphics, this.mineturismo$x(), this.mineturismo$y(), boxSize, checked, this.isFocused(), iconColor);

            int textX = this.mineturismo$x() + boxSize + 4;
            int textY = this.mineturismo$y() + boxSize / 2 - this.font.lineHeight / 2;
            guiGraphics.drawString(this.font, this.getMessage(), textX, textY, CHECKBOX_TEXT_COLOR);
        }

    }

    private final Screen parent;
    private int gridTop;
    private final List<PlateCheckboxEntry> checkboxEntries = new ArrayList<>();
    private int scrollRow = 0;
    private int maxScrollRow = 0;

    private record PlateCheckboxEntry(PlateCheckbox checkbox, int row) {
    }

    public WaypointPlateSettingsScreen(Screen parent) {
        super(ModText.translatable("gui.mineturismo.waypoint_traffic.plate_title"));
        this.parent = parent;
    }

    private static PlateState stateOf(String plateId) {
        if (cachedExclusivePlateIds.contains(plateId)) {
            return PlateState.EXCLUSIVE;
        }
        if (cachedTrafficPlateIds.contains(plateId)) {
            return PlateState.POOL;
        }
        return PlateState.OFF;
    }

    private static void applyState(String plateId, PlateState state) {
        cachedTrafficPlateIds.remove(plateId);
        cachedExclusivePlateIds.remove(plateId);
        switch (state) {
            case POOL -> cachedTrafficPlateIds.add(plateId);
            case EXCLUSIVE -> cachedExclusivePlateIds.add(plateId);
            case OFF -> {
            }
        }
    }

    private void sendPlateIds() {
        SetTownTrafficPlateIdsPayload mineturismo$payload = new SetTownTrafficPlateIdsPayload(
                String.join(",", cachedTrafficPlateIds), String.join(",", cachedExclusivePlateIds));
        ModClientNetworking.sendToServer(SetTownTrafficPlateIdsPayload.ID, mineturismo$payload, SetTownTrafficPlateIdsPayload::write);
    }

    private void updateCheckboxPositions() {
        for (PlateCheckboxEntry entry : this.checkboxEntries) {
            int visibleRow = entry.row() - this.scrollRow;
            boolean isVisible = visibleRow >= 0 && visibleRow < VISIBLE_ROWS;
            entry.checkbox().visible = isVisible;
            entry.checkbox().active = isVisible;
            if (isVisible) {
                ModWidgets.setY(entry.checkbox(), this.gridTop + visibleRow * ROW_HEIGHT);
            }
        }
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        this.gridTop = 50;
        this.checkboxEntries.clear();
        this.scrollRow = 0;

        List<PlateTextures.Design> designs = PlateTextures.DESIGNS;
        int gridLeft = centerX - (COLUMNS * COLUMN_WIDTH) / 2;
        int totalRows = (designs.size() + COLUMNS - 1) / COLUMNS;
        this.maxScrollRow = Math.max(0, totalRows - VISIBLE_ROWS);

        for (int i = 0; i < designs.size(); i++) {
            PlateTextures.Design design = designs.get(i);
            String plateId = design.id();
            int column = i % COLUMNS;
            int row = i / COLUMNS;
            int x = gridLeft + column * COLUMN_WIDTH;
            PlateCheckbox checkbox = new PlateCheckbox(x, this.gridTop + row * ROW_HEIGHT, BUTTON_WIDTH, BUTTON_HEIGHT,
                    this.font, design.displayName(), stateOf(plateId), newState -> {
                        applyState(plateId, newState);
                        sendPlateIds();
                    });
            this.addButton(checkbox);
            this.checkboxEntries.add(new PlateCheckboxEntry(checkbox, row));
        }

        int viewportHeight = VISIBLE_ROWS * ROW_HEIGHT;
        int arrowX = gridLeft + COLUMNS * COLUMN_WIDTH + 8;

        this.addButton(new ScrollbarWidget(ScrollbarWidget.xInArrowColumn(arrowX), this.gridTop,
                this.gridTop + viewportHeight - 20 + 20 - (this.gridTop), VISIBLE_ROWS, this.maxScrollRow + VISIBLE_ROWS, () -> this.scrollRow, row -> {
                    this.scrollRow = row;
                    this.updateCheckboxPositions();
                }));

        this.updateCheckboxPositions();

        int backY = this.gridTop + viewportHeight + 20;
        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.back"), centerX - 100, backY, 200, 20, button ->
                this.minecraft.setScreen(this.parent)));
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
                this.updateCheckboxPositions();
                return true;
            }
        }
        return super.mouseScrolled(mouseX, mouseY, scrollY);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        mineturismo$renderScreen(new GuiGraphics(poseStack), mouseX, mouseY, partialTick);
    }

    private void mineturismo$renderScreen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        super.render(guiGraphics.pose(), mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, this.gridTop - 32, 0xFFFFFFFF);
        guiGraphics.drawCenteredString(this.font,
                ModText.translatable("gui.mineturismo.waypoint_traffic.plate_hint"),
                this.width / 2, this.gridTop - 20, 0xFFAAAAAA);
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
import com.examplebett.client.render.PlateTextures;
import com.examplebett.network.SetTownTrafficPlateIdsPayload;
import net.minecraft.client.gui.Font;

import com.examplebett.compat.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class WaypointPlateSettingsScreen extends Screen {

    private static final int COLUMNS = 2;
    private static final int COLUMN_WIDTH = 200;
    private static final int BUTTON_WIDTH = 190;
    private static final int ROW_HEIGHT = 14;
    private static final int BUTTON_HEIGHT = 12;
    private static final int VISIBLE_ROWS = 8;

    private static final ResourceLocation CHECKBOX_SELECTED_HIGHLIGHTED_SPRITE =
            new ResourceLocation("widget/checkbox_selected_highlighted");
    private static final ResourceLocation CHECKBOX_SELECTED_SPRITE =
            new ResourceLocation("widget/checkbox_selected");
    private static final ResourceLocation CHECKBOX_HIGHLIGHTED_SPRITE =
            new ResourceLocation("widget/checkbox_highlighted");
    private static final ResourceLocation CHECKBOX_SPRITE =
            new ResourceLocation("widget/checkbox");
    private static final int CHECKBOX_TEXT_COLOR = 0xFFE0E0E0;
    private static final int EXCLUSIVE_ICON_COLOR_RGB = 0xFFD700;

    static final Set<String> cachedTrafficPlateIds = new HashSet<>();
    static final Set<String> cachedExclusivePlateIds = new HashSet<>();

    private enum PlateState {
        OFF, POOL, EXCLUSIVE;

        PlateState next() {
            return switch (this) {
                case OFF -> POOL;
                case POOL -> EXCLUSIVE;
                case EXCLUSIVE -> OFF;
            };
        }
    }

    private static final class PlateCheckbox extends AbstractButton {
        private final Font font;
        private final Consumer<PlateState> onStateChange;
        private PlateState state;

        PlateCheckbox(int x, int y, int width, int height, Font font, Component label,
                      PlateState initialState, Consumer<PlateState> onStateChange) {
            super(x, y, width, height, com.examplebett.compat.LegacyGui.text(label));
            this.font = font;
            this.state = initialState;
            this.onStateChange = onStateChange;
        }

        @Override
        public void onPress() {
            this.state = this.state.next();
            this.onStateChange.accept(this.state);
        }

        @Override

        public void renderButton(int mouseX, int mouseY, float partialTick) {
            mineturismo$renderCheckboxWidget(new GuiGraphics(null), mouseX, mouseY, partialTick);
        }

        private int mineturismo$x() {
            return this.x;
        }

        private int mineturismo$y() {
            return this.y;
        }

        private void mineturismo$renderCheckboxWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            int boxSize = 17;
            boolean checked = this.state != PlateState.OFF;
            ResourceLocation sprite;
            if (checked) {
                sprite = this.isFocused() ? CHECKBOX_SELECTED_HIGHLIGHTED_SPRITE : CHECKBOX_SELECTED_SPRITE;
            } else {
                sprite = this.isFocused() ? CHECKBOX_HIGHLIGHTED_SPRITE : CHECKBOX_SPRITE;
            }
            int alpha8 = (int) (this.alpha * 255.0F);
            int iconColor = this.state == PlateState.EXCLUSIVE
                    ? com.examplebett.compat.LegacyGui.argb(alpha8, EXCLUSIVE_ICON_COLOR_RGB >> 16 & 0xFF, EXCLUSIVE_ICON_COLOR_RGB >> 8 & 0xFF, EXCLUSIVE_ICON_COLOR_RGB & 0xFF)
                    : com.examplebett.compat.LegacyGui.argb(alpha8, 255, 255, 255);

            WaypointVehicleListScreen.drawPlainCheckbox(guiGraphics, this.mineturismo$x(), this.mineturismo$y(), boxSize, checked, this.isFocused(), iconColor);

            int textX = this.mineturismo$x() + boxSize + 4;
            int textY = this.mineturismo$y() + boxSize / 2 - this.font.lineHeight / 2;
            guiGraphics.drawString(this.font, this.getMessage(), textX, textY, CHECKBOX_TEXT_COLOR);
        }

    }

    private final Screen parent;
    private int gridTop;
    private final List<PlateCheckboxEntry> checkboxEntries = new ArrayList<>();
    private int scrollRow = 0;
    private int maxScrollRow = 0;

    private record PlateCheckboxEntry(PlateCheckbox checkbox, int row) {
    }

    public WaypointPlateSettingsScreen(Screen parent) {
        super(ModText.translatable("gui.mineturismo.waypoint_traffic.plate_title"));
        this.parent = parent;
    }

    private static PlateState stateOf(String plateId) {
        if (cachedExclusivePlateIds.contains(plateId)) {
            return PlateState.EXCLUSIVE;
        }
        if (cachedTrafficPlateIds.contains(plateId)) {
            return PlateState.POOL;
        }
        return PlateState.OFF;
    }

    private static void applyState(String plateId, PlateState state) {
        cachedTrafficPlateIds.remove(plateId);
        cachedExclusivePlateIds.remove(plateId);
        switch (state) {
            case POOL -> cachedTrafficPlateIds.add(plateId);
            case EXCLUSIVE -> cachedExclusivePlateIds.add(plateId);
            case OFF -> {
            }
        }
    }

    private void sendPlateIds() {
        SetTownTrafficPlateIdsPayload mineturismo$payload = new SetTownTrafficPlateIdsPayload(
                String.join(",", cachedTrafficPlateIds), String.join(",", cachedExclusivePlateIds));
        ModClientNetworking.sendToServer(SetTownTrafficPlateIdsPayload.ID, mineturismo$payload, SetTownTrafficPlateIdsPayload::write);
    }

    private void updateCheckboxPositions() {
        for (PlateCheckboxEntry entry : this.checkboxEntries) {
            int visibleRow = entry.row() - this.scrollRow;
            boolean isVisible = visibleRow >= 0 && visibleRow < VISIBLE_ROWS;
            entry.checkbox().visible = isVisible;
            entry.checkbox().active = isVisible;
            if (isVisible) {
                ModWidgets.setY(entry.checkbox(), this.gridTop + visibleRow * ROW_HEIGHT);
            }
        }
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        this.gridTop = 50;
        this.checkboxEntries.clear();
        this.scrollRow = 0;

        List<PlateTextures.Design> designs = PlateTextures.DESIGNS;
        int gridLeft = centerX - (COLUMNS * COLUMN_WIDTH) / 2;
        int totalRows = (designs.size() + COLUMNS - 1) / COLUMNS;
        this.maxScrollRow = Math.max(0, totalRows - VISIBLE_ROWS);

        for (int i = 0; i < designs.size(); i++) {
            PlateTextures.Design design = designs.get(i);
            String plateId = design.id();
            int column = i % COLUMNS;
            int row = i / COLUMNS;
            int x = gridLeft + column * COLUMN_WIDTH;
            PlateCheckbox checkbox = new PlateCheckbox(x, this.gridTop + row * ROW_HEIGHT, BUTTON_WIDTH, BUTTON_HEIGHT,
                    this.font, design.displayName(), stateOf(plateId), newState -> {
                        applyState(plateId, newState);
                        sendPlateIds();
                    });
            this.addButton(checkbox);
            this.checkboxEntries.add(new PlateCheckboxEntry(checkbox, row));
        }

        int viewportHeight = VISIBLE_ROWS * ROW_HEIGHT;
        int arrowX = gridLeft + COLUMNS * COLUMN_WIDTH + 8;

        this.addButton(new ScrollbarWidget(ScrollbarWidget.xInArrowColumn(arrowX), this.gridTop,
                this.gridTop + viewportHeight - 20 + 20 - (this.gridTop), VISIBLE_ROWS, this.maxScrollRow + VISIBLE_ROWS, () -> this.scrollRow, row -> {
                    this.scrollRow = row;
                    this.updateCheckboxPositions();
                }));

        this.updateCheckboxPositions();

        int backY = this.gridTop + viewportHeight + 20;
        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.back"), centerX - 100, backY, 200, 20, button ->
                this.minecraft.setScreen(this.parent)));
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
                this.updateCheckboxPositions();
                return true;
            }
        }
        return super.mouseScrolled(mouseX, mouseY, scrollY);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTick) {
        mineturismo$renderScreen(new GuiGraphics(null), mouseX, mouseY, partialTick);
    }

    private void mineturismo$renderScreen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        super.render(mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, this.gridTop - 32, 0xFFFFFFFF);
        guiGraphics.drawCenteredString(this.font,
                ModText.translatable("gui.mineturismo.waypoint_traffic.plate_hint"),
                this.width / 2, this.gridTop - 20, 0xFFAAAAAA);
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
import com.examplebett.client.render.PlateTextures;
import com.examplebett.network.SetTownTrafficPlateIdsPayload;
import net.minecraft.client.gui.Font;

import com.examplebett.compat.GuiGraphics;
import com.examplebett.compat.v114.PoseStack;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class WaypointPlateSettingsScreen extends Screen {

    private static final int COLUMNS = 2;
    private static final int COLUMN_WIDTH = 200;
    private static final int BUTTON_WIDTH = 190;
    private static final int ROW_HEIGHT = 14;
    private static final int BUTTON_HEIGHT = 12;
    private static final int VISIBLE_ROWS = 8;

    private static final ResourceLocation CHECKBOX_SELECTED_HIGHLIGHTED_SPRITE =
            new ResourceLocation("widget/checkbox_selected_highlighted");
    private static final ResourceLocation CHECKBOX_SELECTED_SPRITE =
            new ResourceLocation("widget/checkbox_selected");
    private static final ResourceLocation CHECKBOX_HIGHLIGHTED_SPRITE =
            new ResourceLocation("widget/checkbox_highlighted");
    private static final ResourceLocation CHECKBOX_SPRITE =
            new ResourceLocation("widget/checkbox");
    private static final int CHECKBOX_TEXT_COLOR = 0xFFE0E0E0;
    private static final int EXCLUSIVE_ICON_COLOR_RGB = 0xFFD700;

    static final Set<String> cachedTrafficPlateIds = new HashSet<>();
    static final Set<String> cachedExclusivePlateIds = new HashSet<>();

    private enum PlateState {
        OFF, POOL, EXCLUSIVE;

        PlateState next() {
            return switch (this) {
                case OFF -> POOL;
                case POOL -> EXCLUSIVE;
                case EXCLUSIVE -> OFF;
            };
        }
    }

    private static final class PlateCheckbox extends AbstractButton {
        private final Font font;
        private final Consumer<PlateState> onStateChange;
        private PlateState state;

        PlateCheckbox(int x, int y, int width, int height, Font font, Component label,
                      PlateState initialState, Consumer<PlateState> onStateChange) {
            super(x, y, width, height, com.examplebett.compat.LegacyGui.text(label));
            this.font = font;
            this.state = initialState;
            this.onStateChange = onStateChange;
        }

        @Override
        public void onPress() {
            this.state = this.state.next();
            this.onStateChange.accept(this.state);
        }

        @Override

        public void renderButton(int mouseX, int mouseY, float partialTick) {
            mineturismo$renderCheckboxWidget(new GuiGraphics(null), mouseX, mouseY, partialTick);
        }

        private int mineturismo$x() {
            return this.x;
        }

        private int mineturismo$y() {
            return this.y;
        }

        private void mineturismo$renderCheckboxWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            int boxSize = 17;
            boolean checked = this.state != PlateState.OFF;
            ResourceLocation sprite;
            if (checked) {
                sprite = this.isFocused() ? CHECKBOX_SELECTED_HIGHLIGHTED_SPRITE : CHECKBOX_SELECTED_SPRITE;
            } else {
                sprite = this.isFocused() ? CHECKBOX_HIGHLIGHTED_SPRITE : CHECKBOX_SPRITE;
            }
            int alpha8 = (int) (this.alpha * 255.0F);
            int iconColor = this.state == PlateState.EXCLUSIVE
                    ? com.examplebett.compat.LegacyGui.argb(alpha8, EXCLUSIVE_ICON_COLOR_RGB >> 16 & 0xFF, EXCLUSIVE_ICON_COLOR_RGB >> 8 & 0xFF, EXCLUSIVE_ICON_COLOR_RGB & 0xFF)
                    : com.examplebett.compat.LegacyGui.argb(alpha8, 255, 255, 255);

            WaypointVehicleListScreen.drawPlainCheckbox(guiGraphics, this.mineturismo$x(), this.mineturismo$y(), boxSize, checked, this.isFocused(), iconColor);

            int textX = this.mineturismo$x() + boxSize + 4;
            int textY = this.mineturismo$y() + boxSize / 2 - this.font.lineHeight / 2;
            guiGraphics.drawString(this.font, this.getMessage(), textX, textY, CHECKBOX_TEXT_COLOR);
        }

    }

    private final Screen parent;
    private int gridTop;
    private final List<PlateCheckboxEntry> checkboxEntries = new ArrayList<>();
    private int scrollRow = 0;
    private int maxScrollRow = 0;

    private record PlateCheckboxEntry(PlateCheckbox checkbox, int row) {
    }

    public WaypointPlateSettingsScreen(Screen parent) {
        super(ModText.translatable("gui.mineturismo.waypoint_traffic.plate_title"));
        this.parent = parent;
    }

    private static PlateState stateOf(String plateId) {
        if (cachedExclusivePlateIds.contains(plateId)) {
            return PlateState.EXCLUSIVE;
        }
        if (cachedTrafficPlateIds.contains(plateId)) {
            return PlateState.POOL;
        }
        return PlateState.OFF;
    }

    private static void applyState(String plateId, PlateState state) {
        cachedTrafficPlateIds.remove(plateId);
        cachedExclusivePlateIds.remove(plateId);
        switch (state) {
            case POOL -> cachedTrafficPlateIds.add(plateId);
            case EXCLUSIVE -> cachedExclusivePlateIds.add(plateId);
            case OFF -> {
            }
        }
    }

    private void sendPlateIds() {
        SetTownTrafficPlateIdsPayload mineturismo$payload = new SetTownTrafficPlateIdsPayload(
                String.join(",", cachedTrafficPlateIds), String.join(",", cachedExclusivePlateIds));
        ModClientNetworking.sendToServer(SetTownTrafficPlateIdsPayload.ID, mineturismo$payload, SetTownTrafficPlateIdsPayload::write);
    }

    private void updateCheckboxPositions() {
        for (PlateCheckboxEntry entry : this.checkboxEntries) {
            int visibleRow = entry.row() - this.scrollRow;
            boolean isVisible = visibleRow >= 0 && visibleRow < VISIBLE_ROWS;
            entry.checkbox().visible = isVisible;
            entry.checkbox().active = isVisible;
            if (isVisible) {
                ModWidgets.setY(entry.checkbox(), this.gridTop + visibleRow * ROW_HEIGHT);
            }
        }
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        this.gridTop = 50;
        this.checkboxEntries.clear();
        this.scrollRow = 0;

        List<PlateTextures.Design> designs = PlateTextures.DESIGNS;
        int gridLeft = centerX - (COLUMNS * COLUMN_WIDTH) / 2;
        int totalRows = (designs.size() + COLUMNS - 1) / COLUMNS;
        this.maxScrollRow = Math.max(0, totalRows - VISIBLE_ROWS);

        for (int i = 0; i < designs.size(); i++) {
            PlateTextures.Design design = designs.get(i);
            String plateId = design.id();
            int column = i % COLUMNS;
            int row = i / COLUMNS;
            int x = gridLeft + column * COLUMN_WIDTH;
            PlateCheckbox checkbox = new PlateCheckbox(x, this.gridTop + row * ROW_HEIGHT, BUTTON_WIDTH, BUTTON_HEIGHT,
                    this.font, design.displayName(), stateOf(plateId), newState -> {
                        applyState(plateId, newState);
                        sendPlateIds();
                    });
            this.addButton(checkbox);
            this.checkboxEntries.add(new PlateCheckboxEntry(checkbox, row));
        }

        int viewportHeight = VISIBLE_ROWS * ROW_HEIGHT;
        int arrowX = gridLeft + COLUMNS * COLUMN_WIDTH + 8;

        this.addButton(new ScrollbarWidget(ScrollbarWidget.xInArrowColumn(arrowX), this.gridTop,
                this.gridTop + viewportHeight - 20 + 20 - (this.gridTop), VISIBLE_ROWS, this.maxScrollRow + VISIBLE_ROWS, () -> this.scrollRow, row -> {
                    this.scrollRow = row;
                    this.updateCheckboxPositions();
                }));

        this.updateCheckboxPositions();

        int backY = this.gridTop + viewportHeight + 20;
        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.back"), centerX - 100, backY, 200, 20, button ->
                this.minecraft.setScreen(this.parent)));
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
                this.updateCheckboxPositions();
                return true;
            }
        }
        return super.mouseScrolled(mouseX, mouseY, scrollY);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTick) {
        mineturismo$renderScreen(new GuiGraphics(null), mouseX, mouseY, partialTick);
    }

    private void mineturismo$renderScreen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        super.render(mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, this.gridTop - 32, 0xFFFFFFFF);
        guiGraphics.drawCenteredString(this.font,
                ModText.translatable("gui.mineturismo.waypoint_traffic.plate_hint"),
                this.width / 2, this.gridTop - 20, 0xFFAAAAAA);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
*/
//?}
