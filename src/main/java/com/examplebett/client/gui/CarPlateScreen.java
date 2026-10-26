//? if >=1.18 {
//?}
//? if <1.18 && >=1.17 {
/*
package com.examplebett.client.gui;
import com.examplebett.compat.ModWidgets;
import com.examplebett.compat.ModText;

import com.examplebett.entity.CarEntity;

import com.examplebett.compat.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class CarPlateScreen extends Screen implements CarCustomizationScreen {

    private final CarEntity car;
    private final Screen parent;

    public CarPlateScreen(CarEntity car, Screen parent) {
        super(ModText.translatable("gui.mineturismo.plate_menu.title"));
        this.car = car;
        this.parent = parent;
    }

    @Override
    protected void init() {
        int buttonWidth = 200;
        int x = this.width / 2 - buttonWidth / 2;
        int y = this.height / 2 - 30;

        this.addRenderableWidget(ModWidgets.button(ModText.translatable("gui.mineturismo.plate_menu.front"), x, y, buttonWidth, 20, button ->
                this.minecraft.setScreen(new CarPlateEditScreen(this.car, true, this))));

        this.addRenderableWidget(ModWidgets.button(ModText.translatable("gui.mineturismo.plate_menu.rear"), x, y + 24, buttonWidth, 20, button ->
                this.minecraft.setScreen(new CarPlateEditScreen(this.car, false, this))));

        this.addRenderableWidget(ModWidgets.button(ModText.translatable("gui.mineturismo.back"), x, y + 56, buttonWidth, 20, button ->
                this.minecraft.setScreen(this.parent)));
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        mineturismo$renderScreen(new GuiGraphics(poseStack), mouseX, mouseY, partialTick);
    }

    private void mineturismo$renderScreen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        super.render(guiGraphics.pose(), mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - 60, 0xFFFFFFFF);
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

import com.examplebett.compat.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class CarPlateScreen extends Screen implements CarCustomizationScreen {

    private final CarEntity car;
    private final Screen parent;

    public CarPlateScreen(CarEntity car, Screen parent) {
        super(ModText.translatable("gui.mineturismo.plate_menu.title"));
        this.car = car;
        this.parent = parent;
    }

    @Override
    protected void init() {
        int buttonWidth = 200;
        int x = this.width / 2 - buttonWidth / 2;
        int y = this.height / 2 - 30;

        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.plate_menu.front"), x, y, buttonWidth, 20, button ->
                this.minecraft.setScreen(new CarPlateEditScreen(this.car, true, this))));

        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.plate_menu.rear"), x, y + 24, buttonWidth, 20, button ->
                this.minecraft.setScreen(new CarPlateEditScreen(this.car, false, this))));

        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.back"), x, y + 56, buttonWidth, 20, button ->
                this.minecraft.setScreen(this.parent)));
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        mineturismo$renderScreen(new GuiGraphics(poseStack), mouseX, mouseY, partialTick);
    }

    private void mineturismo$renderScreen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        super.render(guiGraphics.pose(), mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - 60, 0xFFFFFFFF);
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

import com.examplebett.compat.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class CarPlateScreen extends Screen implements CarCustomizationScreen {

    private final CarEntity car;
    private final Screen parent;

    public CarPlateScreen(CarEntity car, Screen parent) {
        super(ModText.translatable("gui.mineturismo.plate_menu.title"));
        this.car = car;
        this.parent = parent;
    }

    @Override
    protected void init() {
        int buttonWidth = 200;
        int x = this.width / 2 - buttonWidth / 2;
        int y = this.height / 2 - 30;

        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.plate_menu.front"), x, y, buttonWidth, 20, button ->
                this.minecraft.setScreen(new CarPlateEditScreen(this.car, true, this))));

        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.plate_menu.rear"), x, y + 24, buttonWidth, 20, button ->
                this.minecraft.setScreen(new CarPlateEditScreen(this.car, false, this))));

        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.back"), x, y + 56, buttonWidth, 20, button ->
                this.minecraft.setScreen(this.parent)));
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTick) {
        mineturismo$renderScreen(new GuiGraphics(null), mouseX, mouseY, partialTick);
    }

    private void mineturismo$renderScreen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        super.render(mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - 60, 0xFFFFFFFF);
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

import com.examplebett.compat.GuiGraphics;
import com.examplebett.compat.v114.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class CarPlateScreen extends Screen implements CarCustomizationScreen {

    private final CarEntity car;
    private final Screen parent;

    public CarPlateScreen(CarEntity car, Screen parent) {
        super(ModText.translatable("gui.mineturismo.plate_menu.title"));
        this.car = car;
        this.parent = parent;
    }

    @Override
    protected void init() {
        int buttonWidth = 200;
        int x = this.width / 2 - buttonWidth / 2;
        int y = this.height / 2 - 30;

        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.plate_menu.front"), x, y, buttonWidth, 20, button ->
                this.minecraft.setScreen(new CarPlateEditScreen(this.car, true, this))));

        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.plate_menu.rear"), x, y + 24, buttonWidth, 20, button ->
                this.minecraft.setScreen(new CarPlateEditScreen(this.car, false, this))));

        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.back"), x, y + 56, buttonWidth, 20, button ->
                this.minecraft.setScreen(this.parent)));
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTick) {
        mineturismo$renderScreen(new GuiGraphics(null), mouseX, mouseY, partialTick);
    }

    private void mineturismo$renderScreen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        super.render(mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - 60, 0xFFFFFFFF);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
*/
//?}
