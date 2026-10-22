//? if >=1.18 {
//?}
//? if <1.18 && >=1.17 {
/*
package com.examplebett.client.gui;
import com.examplebett.compat.ModWidgets;
import com.examplebett.compat.ModText;

import com.examplebett.entity.CarEntity;
import com.examplebett.render.UnderNeon;

import com.examplebett.compat.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class UnderNeonScreen extends Screen implements CarCustomizationScreen {

    private static final int MENU_BUTTON_WIDTH = 200;
    private static final int MENU_BUTTON_HEIGHT = 20;
    private static final int MENU_ROW_HEIGHT = MENU_BUTTON_HEIGHT + 4;

    private final CarEntity car;
    private final Screen parent;

    public UnderNeonScreen(CarEntity car, Screen parent) {
        super(ModText.translatable("gui.mineturismo.under_neon.title"));
        this.car = car;
        this.parent = parent;
    }

    private Component slotLabel(int slot) {
        Component position = ModText.translatable("gui.mineturismo.under_neon.slot_" + UnderNeon.SLOT_IDS[slot]);
        return ModText.translatable(this.car.getUnderNeon(slot).enabled()
                ? "gui.mineturismo.under_neon.slot_on" : "gui.mineturismo.under_neon.slot_off", position);
    }

    @Override
    protected void init() {
        int x = this.width / 2 - MENU_BUTTON_WIDTH / 2;
        int y = this.height / 2 - 64;
        for (int i = 0; i < UnderNeon.MAX_SLOTS; i++) {
            int slot = i;
            this.addRenderableWidget(ModWidgets.button(this.slotLabel(slot), x, y + i * MENU_ROW_HEIGHT, MENU_BUTTON_WIDTH, MENU_BUTTON_HEIGHT, b ->
                    this.minecraft.setScreen(new UnderNeonAdjustScreen(this.car, this.parent, slot))));
        }
        this.addRenderableWidget(ModWidgets.button(ModText.translatable("gui.mineturismo.back"),
                x, y + UnderNeon.MAX_SLOTS * MENU_ROW_HEIGHT + 8, MENU_BUTTON_WIDTH, 20, button ->
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

import com.examplebett.entity.CarEntity;
import com.examplebett.render.UnderNeon;

import com.examplebett.compat.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class UnderNeonScreen extends Screen implements CarCustomizationScreen {

    private static final int MENU_BUTTON_WIDTH = 200;
    private static final int MENU_BUTTON_HEIGHT = 20;
    private static final int MENU_ROW_HEIGHT = MENU_BUTTON_HEIGHT + 4;

    private final CarEntity car;
    private final Screen parent;

    public UnderNeonScreen(CarEntity car, Screen parent) {
        super(ModText.translatable("gui.mineturismo.under_neon.title"));
        this.car = car;
        this.parent = parent;
    }

    private Component slotLabel(int slot) {
        Component position = ModText.translatable("gui.mineturismo.under_neon.slot_" + UnderNeon.SLOT_IDS[slot]);
        return ModText.translatable(this.car.getUnderNeon(slot).enabled()
                ? "gui.mineturismo.under_neon.slot_on" : "gui.mineturismo.under_neon.slot_off", position);
    }

    @Override
    protected void init() {
        int x = this.width / 2 - MENU_BUTTON_WIDTH / 2;
        int y = this.height / 2 - 64;
        for (int i = 0; i < UnderNeon.MAX_SLOTS; i++) {
            int slot = i;
            this.addButton(ModWidgets.button(this.slotLabel(slot), x, y + i * MENU_ROW_HEIGHT, MENU_BUTTON_WIDTH, MENU_BUTTON_HEIGHT, b ->
                    this.minecraft.setScreen(new UnderNeonAdjustScreen(this.car, this.parent, slot))));
        }
        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.back"),
                x, y + UnderNeon.MAX_SLOTS * MENU_ROW_HEIGHT + 8, MENU_BUTTON_WIDTH, 20, button ->
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

import com.examplebett.entity.CarEntity;
import com.examplebett.render.UnderNeon;

import com.examplebett.compat.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class UnderNeonScreen extends Screen implements CarCustomizationScreen {

    private static final int MENU_BUTTON_WIDTH = 200;
    private static final int MENU_BUTTON_HEIGHT = 20;
    private static final int MENU_ROW_HEIGHT = MENU_BUTTON_HEIGHT + 4;

    private final CarEntity car;
    private final Screen parent;

    public UnderNeonScreen(CarEntity car, Screen parent) {
        super(ModText.translatable("gui.mineturismo.under_neon.title"));
        this.car = car;
        this.parent = parent;
    }

    private Component slotLabel(int slot) {
        Component position = ModText.translatable("gui.mineturismo.under_neon.slot_" + UnderNeon.SLOT_IDS[slot]);
        return ModText.translatable(this.car.getUnderNeon(slot).enabled()
                ? "gui.mineturismo.under_neon.slot_on" : "gui.mineturismo.under_neon.slot_off", position);
    }

    @Override
    protected void init() {
        int x = this.width / 2 - MENU_BUTTON_WIDTH / 2;
        int y = this.height / 2 - 64;
        for (int i = 0; i < UnderNeon.MAX_SLOTS; i++) {
            int slot = i;
            this.addButton(ModWidgets.button(this.slotLabel(slot), x, y + i * MENU_ROW_HEIGHT, MENU_BUTTON_WIDTH, MENU_BUTTON_HEIGHT, b ->
                    this.minecraft.setScreen(new UnderNeonAdjustScreen(this.car, this.parent, slot))));
        }
        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.back"),
                x, y + UnderNeon.MAX_SLOTS * MENU_ROW_HEIGHT + 8, MENU_BUTTON_WIDTH, 20, button ->
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

import com.examplebett.entity.CarEntity;
import com.examplebett.render.UnderNeon;

import com.examplebett.compat.GuiGraphics;
import com.examplebett.compat.v114.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class UnderNeonScreen extends Screen implements CarCustomizationScreen {

    private static final int MENU_BUTTON_WIDTH = 200;
    private static final int MENU_BUTTON_HEIGHT = 20;
    private static final int MENU_ROW_HEIGHT = MENU_BUTTON_HEIGHT + 4;

    private final CarEntity car;
    private final Screen parent;

    public UnderNeonScreen(CarEntity car, Screen parent) {
        super(ModText.translatable("gui.mineturismo.under_neon.title"));
        this.car = car;
        this.parent = parent;
    }

    private Component slotLabel(int slot) {
        Component position = ModText.translatable("gui.mineturismo.under_neon.slot_" + UnderNeon.SLOT_IDS[slot]);
        return ModText.translatable(this.car.getUnderNeon(slot).enabled()
                ? "gui.mineturismo.under_neon.slot_on" : "gui.mineturismo.under_neon.slot_off", position);
    }

    @Override
    protected void init() {
        int x = this.width / 2 - MENU_BUTTON_WIDTH / 2;
        int y = this.height / 2 - 64;
        for (int i = 0; i < UnderNeon.MAX_SLOTS; i++) {
            int slot = i;
            this.addButton(ModWidgets.button(this.slotLabel(slot), x, y + i * MENU_ROW_HEIGHT, MENU_BUTTON_WIDTH, MENU_BUTTON_HEIGHT, b ->
                    this.minecraft.setScreen(new UnderNeonAdjustScreen(this.car, this.parent, slot))));
        }
        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.back"),
                x, y + UnderNeon.MAX_SLOTS * MENU_ROW_HEIGHT + 8, MENU_BUTTON_WIDTH, 20, button ->
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
