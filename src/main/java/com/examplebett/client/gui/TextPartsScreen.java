//? if >=1.18 {
//?}
//? if <1.18 && >=1.17 {
/*
package com.examplebett.client.gui;
import com.examplebett.compat.ModWidgets;
import com.examplebett.compat.ModText;

import com.examplebett.entity.CarEntity;
import com.examplebett.render.TextParts;

import com.examplebett.compat.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class TextPartsScreen extends Screen implements CarCustomizationScreen {

    private static final int MENU_BUTTON_WIDTH = 200;
    private static final int MENU_BUTTON_HEIGHT = 20;
    private static final int MENU_ROW_GAP = 4;
    private static final int MENU_VISIBLE_ROWS = 5;
    private static final int MENU_ROW_HEIGHT = MENU_BUTTON_HEIGHT + MENU_ROW_GAP;

    private static final int PREVIEW_MAX_WIDTH = MENU_BUTTON_WIDTH - 90;

    private final CarEntity car;
    private final Screen parent;
    private final int initialScrollRow;

    private final java.util.List<MenuButtonEntry> menuButtons = new java.util.ArrayList<>();
    private int menuScrollRow = 0;
    private int menuMaxScrollRow = 0;
    private int menuViewportY;

    private record MenuButtonEntry(Button button, int row) {
    }

    public TextPartsScreen(CarEntity car, Screen parent) {
        this(car, parent, 0);
    }

    public TextPartsScreen(CarEntity car, Screen parent, int initialScrollRow) {
        super(ModText.translatable("gui.mineturismo.text_parts.title"));
        this.car = car;
        this.parent = parent;
        this.initialScrollRow = initialScrollRow;
    }

    private void updateMenuButtonPositions() {
        for (MenuButtonEntry entry : this.menuButtons) {
            int visibleRow = entry.row() - this.menuScrollRow;
            boolean isVisible = visibleRow >= 0 && visibleRow < MENU_VISIBLE_ROWS;
            entry.button().visible = isVisible;
            entry.button().active = isVisible;
            if (isVisible) {
                ModWidgets.setY(entry.button(), this.menuViewportY + visibleRow * MENU_ROW_HEIGHT);
            }
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollY) {
        return mineturismo$mouseScrolled(mouseX, mouseY, 0, scrollY);
    }

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
        return super.mouseScrolled(mouseX, mouseY, scrollY);
    }

    private Component slotLabel(int slot) {
        TextParts.Slot value = this.car.getTextPart(slot);
        String number = String.valueOf(slot + 1);
        if (value.text().trim().isEmpty()) {
            return ModText.translatable("gui.mineturismo.text_parts.slot_empty", number);
        }
        String preview = value.text();
        if (this.font.width(preview) > PREVIEW_MAX_WIDTH) {
            preview = this.font.plainSubstrByWidth(preview, PREVIEW_MAX_WIDTH - this.font.width("...")) + "...";
        }
        return ModText.translatable(value.enabled() ? "gui.mineturismo.text_parts.slot" : "gui.mineturismo.text_parts.slot_hidden",
                number, preview);
    }

    @Override
    protected void init() {
        int buttonWidth = MENU_BUTTON_WIDTH;
        int x = this.width / 2 - buttonWidth / 2;
        int y = this.height / 2 - 64;
        this.menuViewportY = y;
        this.menuButtons.clear();
        this.menuMaxScrollRow = Math.max(0, TextParts.MAX_SLOTS - MENU_VISIBLE_ROWS);
        this.menuScrollRow = Math.max(0, Math.min(this.menuMaxScrollRow, this.initialScrollRow));

        for (int i = 0; i < TextParts.MAX_SLOTS; i++) {
            int slot = i;
            Button button = ModWidgets.button(this.slotLabel(slot), x, y + i * MENU_ROW_HEIGHT, buttonWidth, MENU_BUTTON_HEIGHT, b ->
                    this.minecraft.setScreen(new TextPartAdjustScreen(this.car, this, slot, this.menuScrollRow)));
            this.addRenderableWidget(button);
            this.menuButtons.add(new MenuButtonEntry(button, i));
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

    Screen parent() {
        return this.parent;
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

import com.examplebett.entity.CarEntity;
import com.examplebett.render.TextParts;

import com.examplebett.compat.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class TextPartsScreen extends Screen implements CarCustomizationScreen {

    private static final int MENU_BUTTON_WIDTH = 200;
    private static final int MENU_BUTTON_HEIGHT = 20;
    private static final int MENU_ROW_GAP = 4;
    private static final int MENU_VISIBLE_ROWS = 5;
    private static final int MENU_ROW_HEIGHT = MENU_BUTTON_HEIGHT + MENU_ROW_GAP;

    private static final int PREVIEW_MAX_WIDTH = MENU_BUTTON_WIDTH - 90;

    private final CarEntity car;
    private final Screen parent;
    private final int initialScrollRow;

    private final java.util.List<MenuButtonEntry> menuButtons = new java.util.ArrayList<>();
    private int menuScrollRow = 0;
    private int menuMaxScrollRow = 0;
    private int menuViewportY;

    private record MenuButtonEntry(Button button, int row) {
    }

    public TextPartsScreen(CarEntity car, Screen parent) {
        this(car, parent, 0);
    }

    public TextPartsScreen(CarEntity car, Screen parent, int initialScrollRow) {
        super(ModText.translatable("gui.mineturismo.text_parts.title"));
        this.car = car;
        this.parent = parent;
        this.initialScrollRow = initialScrollRow;
    }

    private void updateMenuButtonPositions() {
        for (MenuButtonEntry entry : this.menuButtons) {
            int visibleRow = entry.row() - this.menuScrollRow;
            boolean isVisible = visibleRow >= 0 && visibleRow < MENU_VISIBLE_ROWS;
            entry.button().visible = isVisible;
            entry.button().active = isVisible;
            if (isVisible) {
                ModWidgets.setY(entry.button(), this.menuViewportY + visibleRow * MENU_ROW_HEIGHT);
            }
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollY) {
        return mineturismo$mouseScrolled(mouseX, mouseY, 0, scrollY);
    }

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
        return super.mouseScrolled(mouseX, mouseY, scrollY);
    }

    private Component slotLabel(int slot) {
        TextParts.Slot value = this.car.getTextPart(slot);
        String number = String.valueOf(slot + 1);
        if (value.text().trim().isEmpty()) {
            return ModText.translatable("gui.mineturismo.text_parts.slot_empty", number);
        }
        String preview = value.text();
        if (this.font.width(preview) > PREVIEW_MAX_WIDTH) {
            preview = this.font.plainSubstrByWidth(preview, PREVIEW_MAX_WIDTH - this.font.width("...")) + "...";
        }
        return ModText.translatable(value.enabled() ? "gui.mineturismo.text_parts.slot" : "gui.mineturismo.text_parts.slot_hidden",
                number, preview);
    }

    @Override
    protected void init() {
        int buttonWidth = MENU_BUTTON_WIDTH;
        int x = this.width / 2 - buttonWidth / 2;
        int y = this.height / 2 - 64;
        this.menuViewportY = y;
        this.menuButtons.clear();
        this.menuMaxScrollRow = Math.max(0, TextParts.MAX_SLOTS - MENU_VISIBLE_ROWS);
        this.menuScrollRow = Math.max(0, Math.min(this.menuMaxScrollRow, this.initialScrollRow));

        for (int i = 0; i < TextParts.MAX_SLOTS; i++) {
            int slot = i;
            Button button = ModWidgets.button(this.slotLabel(slot), x, y + i * MENU_ROW_HEIGHT, buttonWidth, MENU_BUTTON_HEIGHT, b ->
                    this.minecraft.setScreen(new TextPartAdjustScreen(this.car, this, slot, this.menuScrollRow)));
            this.addButton(button);
            this.menuButtons.add(new MenuButtonEntry(button, i));
        }

        int viewportHeight = MENU_VISIBLE_ROWS * MENU_ROW_HEIGHT - MENU_ROW_GAP;
        int arrowX = x + buttonWidth + 8;

        this.addButton(new ScrollbarWidget(ScrollbarWidget.xInArrowColumn(arrowX), y,
                y + viewportHeight - 20 + 20 - (y), MENU_VISIBLE_ROWS, this.menuMaxScrollRow + MENU_VISIBLE_ROWS, () -> this.menuScrollRow, row -> {
                    this.menuScrollRow = row;
                    this.updateMenuButtonPositions();
                }));
        this.updateMenuButtonPositions();

        Component backLabel = ModText.translatable("gui.mineturismo.back");
        this.addButton(ModWidgets.button(backLabel, x, y + viewportHeight + MENU_ROW_GAP + 8, buttonWidth, 20, button ->
                this.minecraft.setScreen(this.parent)));
    }

    Screen parent() {
        return this.parent;
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

import com.examplebett.entity.CarEntity;
import com.examplebett.render.TextParts;

import com.examplebett.compat.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class TextPartsScreen extends Screen implements CarCustomizationScreen {

    private static final int MENU_BUTTON_WIDTH = 200;
    private static final int MENU_BUTTON_HEIGHT = 20;
    private static final int MENU_ROW_GAP = 4;
    private static final int MENU_VISIBLE_ROWS = 5;
    private static final int MENU_ROW_HEIGHT = MENU_BUTTON_HEIGHT + MENU_ROW_GAP;

    private static final int PREVIEW_MAX_WIDTH = MENU_BUTTON_WIDTH - 90;

    private final CarEntity car;
    private final Screen parent;
    private final int initialScrollRow;

    private final java.util.List<MenuButtonEntry> menuButtons = new java.util.ArrayList<>();
    private int menuScrollRow = 0;
    private int menuMaxScrollRow = 0;
    private int menuViewportY;

    private record MenuButtonEntry(Button button, int row) {
    }

    public TextPartsScreen(CarEntity car, Screen parent) {
        this(car, parent, 0);
    }

    public TextPartsScreen(CarEntity car, Screen parent, int initialScrollRow) {
        super(ModText.translatable("gui.mineturismo.text_parts.title"));
        this.car = car;
        this.parent = parent;
        this.initialScrollRow = initialScrollRow;
    }

    private void updateMenuButtonPositions() {
        for (MenuButtonEntry entry : this.menuButtons) {
            int visibleRow = entry.row() - this.menuScrollRow;
            boolean isVisible = visibleRow >= 0 && visibleRow < MENU_VISIBLE_ROWS;
            entry.button().visible = isVisible;
            entry.button().active = isVisible;
            if (isVisible) {
                ModWidgets.setY(entry.button(), this.menuViewportY + visibleRow * MENU_ROW_HEIGHT);
            }
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollY) {
        return mineturismo$mouseScrolled(mouseX, mouseY, 0, scrollY);
    }

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
        return super.mouseScrolled(mouseX, mouseY, scrollY);
    }

    private Component slotLabel(int slot) {
        TextParts.Slot value = this.car.getTextPart(slot);
        String number = String.valueOf(slot + 1);
        if (value.text().trim().isEmpty()) {
            return ModText.translatable("gui.mineturismo.text_parts.slot_empty", number);
        }
        String preview = value.text();
        if (com.examplebett.compat.LegacyGui.width(this.font, preview) > PREVIEW_MAX_WIDTH) {
            preview = this.font.substrByWidth(preview, PREVIEW_MAX_WIDTH - com.examplebett.compat.LegacyGui.width(this.font, "...")) + "...";
        }
        return ModText.translatable(value.enabled() ? "gui.mineturismo.text_parts.slot" : "gui.mineturismo.text_parts.slot_hidden",
                number, preview);
    }

    @Override
    protected void init() {
        int buttonWidth = MENU_BUTTON_WIDTH;
        int x = this.width / 2 - buttonWidth / 2;
        int y = this.height / 2 - 64;
        this.menuViewportY = y;
        this.menuButtons.clear();
        this.menuMaxScrollRow = Math.max(0, TextParts.MAX_SLOTS - MENU_VISIBLE_ROWS);
        this.menuScrollRow = Math.max(0, Math.min(this.menuMaxScrollRow, this.initialScrollRow));

        for (int i = 0; i < TextParts.MAX_SLOTS; i++) {
            int slot = i;
            Button button = ModWidgets.button(this.slotLabel(slot), x, y + i * MENU_ROW_HEIGHT, buttonWidth, MENU_BUTTON_HEIGHT, b ->
                    this.minecraft.setScreen(new TextPartAdjustScreen(this.car, this, slot, this.menuScrollRow)));
            this.addButton(button);
            this.menuButtons.add(new MenuButtonEntry(button, i));
        }

        int viewportHeight = MENU_VISIBLE_ROWS * MENU_ROW_HEIGHT - MENU_ROW_GAP;
        int arrowX = x + buttonWidth + 8;

        this.addButton(new ScrollbarWidget(ScrollbarWidget.xInArrowColumn(arrowX), y,
                y + viewportHeight - 20 + 20 - (y), MENU_VISIBLE_ROWS, this.menuMaxScrollRow + MENU_VISIBLE_ROWS, () -> this.menuScrollRow, row -> {
                    this.menuScrollRow = row;
                    this.updateMenuButtonPositions();
                }));
        this.updateMenuButtonPositions();

        Component backLabel = ModText.translatable("gui.mineturismo.back");
        this.addButton(ModWidgets.button(backLabel, x, y + viewportHeight + MENU_ROW_GAP + 8, buttonWidth, 20, button ->
                this.minecraft.setScreen(this.parent)));
    }

    Screen parent() {
        return this.parent;
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

import com.examplebett.entity.CarEntity;
import com.examplebett.render.TextParts;

import com.examplebett.compat.GuiGraphics;
import com.examplebett.compat.v114.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class TextPartsScreen extends Screen implements CarCustomizationScreen {

    private static final int MENU_BUTTON_WIDTH = 200;
    private static final int MENU_BUTTON_HEIGHT = 20;
    private static final int MENU_ROW_GAP = 4;
    private static final int MENU_VISIBLE_ROWS = 5;
    private static final int MENU_ROW_HEIGHT = MENU_BUTTON_HEIGHT + MENU_ROW_GAP;

    private static final int PREVIEW_MAX_WIDTH = MENU_BUTTON_WIDTH - 90;

    private final CarEntity car;
    private final Screen parent;
    private final int initialScrollRow;

    private final java.util.List<MenuButtonEntry> menuButtons = new java.util.ArrayList<>();
    private int menuScrollRow = 0;
    private int menuMaxScrollRow = 0;
    private int menuViewportY;

    private record MenuButtonEntry(Button button, int row) {
    }

    public TextPartsScreen(CarEntity car, Screen parent) {
        this(car, parent, 0);
    }

    public TextPartsScreen(CarEntity car, Screen parent, int initialScrollRow) {
        super(ModText.translatable("gui.mineturismo.text_parts.title"));
        this.car = car;
        this.parent = parent;
        this.initialScrollRow = initialScrollRow;
    }

    private void updateMenuButtonPositions() {
        for (MenuButtonEntry entry : this.menuButtons) {
            int visibleRow = entry.row() - this.menuScrollRow;
            boolean isVisible = visibleRow >= 0 && visibleRow < MENU_VISIBLE_ROWS;
            entry.button().visible = isVisible;
            entry.button().active = isVisible;
            if (isVisible) {
                ModWidgets.setY(entry.button(), this.menuViewportY + visibleRow * MENU_ROW_HEIGHT);
            }
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollY) {
        return mineturismo$mouseScrolled(mouseX, mouseY, 0, scrollY);
    }

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
        return super.mouseScrolled(mouseX, mouseY, scrollY);
    }

    private Component slotLabel(int slot) {
        TextParts.Slot value = this.car.getTextPart(slot);
        String number = String.valueOf(slot + 1);
        if (value.text().trim().isEmpty()) {
            return ModText.translatable("gui.mineturismo.text_parts.slot_empty", number);
        }
        String preview = value.text();
        if (com.examplebett.compat.LegacyGui.width(this.font, preview) > PREVIEW_MAX_WIDTH) {
            preview = this.font.substrByWidth(preview, PREVIEW_MAX_WIDTH - com.examplebett.compat.LegacyGui.width(this.font, "...")) + "...";
        }
        return ModText.translatable(value.enabled() ? "gui.mineturismo.text_parts.slot" : "gui.mineturismo.text_parts.slot_hidden",
                number, preview);
    }

    @Override
    protected void init() {
        int buttonWidth = MENU_BUTTON_WIDTH;
        int x = this.width / 2 - buttonWidth / 2;
        int y = this.height / 2 - 64;
        this.menuViewportY = y;
        this.menuButtons.clear();
        this.menuMaxScrollRow = Math.max(0, TextParts.MAX_SLOTS - MENU_VISIBLE_ROWS);
        this.menuScrollRow = Math.max(0, Math.min(this.menuMaxScrollRow, this.initialScrollRow));

        for (int i = 0; i < TextParts.MAX_SLOTS; i++) {
            int slot = i;
            Button button = ModWidgets.button(this.slotLabel(slot), x, y + i * MENU_ROW_HEIGHT, buttonWidth, MENU_BUTTON_HEIGHT, b ->
                    this.minecraft.setScreen(new TextPartAdjustScreen(this.car, this, slot, this.menuScrollRow)));
            this.addButton(button);
            this.menuButtons.add(new MenuButtonEntry(button, i));
        }

        int viewportHeight = MENU_VISIBLE_ROWS * MENU_ROW_HEIGHT - MENU_ROW_GAP;
        int arrowX = x + buttonWidth + 8;

        this.addButton(new ScrollbarWidget(ScrollbarWidget.xInArrowColumn(arrowX), y,
                y + viewportHeight - 20 + 20 - (y), MENU_VISIBLE_ROWS, this.menuMaxScrollRow + MENU_VISIBLE_ROWS, () -> this.menuScrollRow, row -> {
                    this.menuScrollRow = row;
                    this.updateMenuButtonPositions();
                }));
        this.updateMenuButtonPositions();

        Component backLabel = ModText.translatable("gui.mineturismo.back");
        this.addButton(ModWidgets.button(backLabel, x, y + viewportHeight + MENU_ROW_GAP + 8, buttonWidth, 20, button ->
                this.minecraft.setScreen(this.parent)));
    }

    Screen parent() {
        return this.parent;
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
