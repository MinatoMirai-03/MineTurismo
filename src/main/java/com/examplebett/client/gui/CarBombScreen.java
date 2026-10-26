//? if >=1.18 {
//?}
//? if <1.18 && >=1.17 {
/*
package com.examplebett.client.gui;
import com.examplebett.compat.ModWidgets;
import com.examplebett.compat.ModText;

import com.examplebett.compat.ModClientNetworking;
import com.examplebett.entity.CarEntity;
import com.examplebett.network.SetCarBombPayload;

import com.examplebett.compat.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public class CarBombScreen extends Screen implements CarCustomizationScreen {

    private static final int TEXT_WIDTH = 240;

    private final CarEntity car;
    private final Screen parent;
    private int statusY;
    private int descY;

    public CarBombScreen(CarEntity car, Screen parent) {
        super(ModText.translatable("gui.mineturismo.bomb.title"));
        this.car = car;
        this.parent = parent;
    }

    private void apply(boolean installed) {

        this.car.setBombInstalled(installed, this.minecraft.player != null ? this.minecraft.player.getUUID() : null);
        SetCarBombPayload mineturismo$payload = new SetCarBombPayload(this.car.getId(), installed);
        ModClientNetworking.sendToServer(SetCarBombPayload.ID, mineturismo$payload, SetCarBombPayload::write);
        this.minecraft.setScreen(new CarBombScreen(this.car, this.parent));
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int top = this.height / 2 - 60;
        this.statusY = top;
        boolean installed = this.car.isBombInstalled();
        int buttonWidth = 200;
        Component toggle = ModText.translatable(installed ? "gui.mineturismo.bomb.remove" : "gui.mineturismo.bomb.install");
        this.addRenderableWidget(ModWidgets.button(toggle, centerX - buttonWidth / 2, top + 16, buttonWidth, 20, b -> this.apply(!installed)));
        this.descY = top + 46;
        this.addRenderableWidget(ModWidgets.button(ModText.translatable("gui.mineturismo.back"), centerX - buttonWidth / 2, top + 100, buttonWidth, 20, button ->
                this.minecraft.setScreen(this.parent)));
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        mineturismo$renderScreen(new GuiGraphics(poseStack), mouseX, mouseY, partialTick);
    }

    private void mineturismo$renderScreen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        super.render(guiGraphics.pose(), mouseX, mouseY, partialTick);
        int centerX = this.width / 2;
        guiGraphics.drawCenteredString(this.font, this.title, centerX, this.statusY - 24, 0xFFFFFFFF);
        boolean installed = this.car.isBombInstalled();
        guiGraphics.drawCenteredString(this.font,
                ModText.translatable(installed ? "gui.mineturismo.bomb.status_installed" : "gui.mineturismo.bomb.status_none"),
                centerX, this.statusY, installed ? 0xFFFF5555 : 0xFFAAAAAA);
        List<FormattedCharSequence> lines = this.font.split(ModText.translatable("gui.mineturismo.bomb.desc"), TEXT_WIDTH);
        for (int i = 0; i < lines.size(); i++) {
            guiGraphics.drawCenteredString(this.font, lines.get(i), centerX, this.descY + i * 10, 0xFFDDDDDD);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
*/
//?}
//? if <1.17 && >=1.16.2 {
/*
package com.examplebett.client.gui;
import com.examplebett.compat.ModWidgets;
import com.examplebett.compat.ModText;

import com.examplebett.compat.ModClientNetworking;
import com.examplebett.entity.CarEntity;
import com.examplebett.network.SetCarBombPayload;

import com.examplebett.compat.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public class CarBombScreen extends Screen implements CarCustomizationScreen {

    private static final int TEXT_WIDTH = 240;

    private final CarEntity car;
    private final Screen parent;
    private int statusY;
    private int descY;

    public CarBombScreen(CarEntity car, Screen parent) {
        super(ModText.translatable("gui.mineturismo.bomb.title"));
        this.car = car;
        this.parent = parent;
    }

    private void apply(boolean installed) {

        this.car.setBombInstalled(installed, this.minecraft.player != null ? this.minecraft.player.getUUID() : null);
        SetCarBombPayload mineturismo$payload = new SetCarBombPayload(this.car.getId(), installed);
        ModClientNetworking.sendToServer(SetCarBombPayload.ID, mineturismo$payload, SetCarBombPayload::write);
        this.minecraft.setScreen(new CarBombScreen(this.car, this.parent));
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int top = this.height / 2 - 60;
        this.statusY = top;
        boolean installed = this.car.isBombInstalled();
        int buttonWidth = 200;
        Component toggle = ModText.translatable(installed ? "gui.mineturismo.bomb.remove" : "gui.mineturismo.bomb.install");
        this.addButton(ModWidgets.button(toggle, centerX - buttonWidth / 2, top + 16, buttonWidth, 20, b -> this.apply(!installed)));
        this.descY = top + 46;
        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.back"), centerX - buttonWidth / 2, top + 100, buttonWidth, 20, button ->
                this.minecraft.setScreen(this.parent)));
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        mineturismo$renderScreen(new GuiGraphics(poseStack), mouseX, mouseY, partialTick);
    }

    private void mineturismo$renderScreen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        super.render(guiGraphics.pose(), mouseX, mouseY, partialTick);
        int centerX = this.width / 2;
        guiGraphics.drawCenteredString(this.font, this.title, centerX, this.statusY - 24, 0xFFFFFFFF);
        boolean installed = this.car.isBombInstalled();
        guiGraphics.drawCenteredString(this.font,
                ModText.translatable(installed ? "gui.mineturismo.bomb.status_installed" : "gui.mineturismo.bomb.status_none"),
                centerX, this.statusY, installed ? 0xFFFF5555 : 0xFFAAAAAA);
        List<FormattedCharSequence> lines = this.font.split(ModText.translatable("gui.mineturismo.bomb.desc"), TEXT_WIDTH);
        for (int i = 0; i < lines.size(); i++) {
            guiGraphics.drawCenteredString(this.font, lines.get(i), centerX, this.descY + i * 10, 0xFFDDDDDD);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
*/
//?}
//? if <1.16.2 && >=1.16 {
/*
package com.examplebett.client.gui;
import com.examplebett.compat.ModWidgets;
import com.examplebett.compat.ModText;

import com.examplebett.compat.ModClientNetworking;
import com.examplebett.entity.CarEntity;
import com.examplebett.network.SetCarBombPayload;

import com.examplebett.compat.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;

import java.util.List;

public class CarBombScreen extends Screen implements CarCustomizationScreen {

    private static final int TEXT_WIDTH = 240;

    private final CarEntity car;
    private final Screen parent;
    private int statusY;
    private int descY;

    public CarBombScreen(CarEntity car, Screen parent) {
        super(ModText.translatable("gui.mineturismo.bomb.title"));
        this.car = car;
        this.parent = parent;
    }

    private void apply(boolean installed) {

        this.car.setBombInstalled(installed, this.minecraft.player != null ? this.minecraft.player.getUUID() : null);
        SetCarBombPayload mineturismo$payload = new SetCarBombPayload(this.car.getId(), installed);
        ModClientNetworking.sendToServer(SetCarBombPayload.ID, mineturismo$payload, SetCarBombPayload::write);
        this.minecraft.setScreen(new CarBombScreen(this.car, this.parent));
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int top = this.height / 2 - 60;
        this.statusY = top;
        boolean installed = this.car.isBombInstalled();
        int buttonWidth = 200;
        Component toggle = ModText.translatable(installed ? "gui.mineturismo.bomb.remove" : "gui.mineturismo.bomb.install");
        this.addButton(ModWidgets.button(toggle, centerX - buttonWidth / 2, top + 16, buttonWidth, 20, b -> this.apply(!installed)));
        this.descY = top + 46;
        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.back"), centerX - buttonWidth / 2, top + 100, buttonWidth, 20, button ->
                this.minecraft.setScreen(this.parent)));
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        mineturismo$renderScreen(new GuiGraphics(poseStack), mouseX, mouseY, partialTick);
    }

    private void mineturismo$renderScreen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        super.render(guiGraphics.pose(), mouseX, mouseY, partialTick);
        int centerX = this.width / 2;
        guiGraphics.drawCenteredString(this.font, this.title, centerX, this.statusY - 24, 0xFFFFFFFF);
        boolean installed = this.car.isBombInstalled();
        guiGraphics.drawCenteredString(this.font,
                ModText.translatable(installed ? "gui.mineturismo.bomb.status_installed" : "gui.mineturismo.bomb.status_none"),
                centerX, this.statusY, installed ? 0xFFFF5555 : 0xFFAAAAAA);
        List<FormattedText> lines = this.font.split(ModText.translatable("gui.mineturismo.bomb.desc"), TEXT_WIDTH);
        for (int i = 0; i < lines.size(); i++) {
            guiGraphics.drawCenteredString(this.font, lines.get(i), centerX, this.descY + i * 10, 0xFFDDDDDD);
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
import com.examplebett.entity.CarEntity;
import com.examplebett.network.SetCarBombPayload;

import com.examplebett.compat.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

public class CarBombScreen extends Screen implements CarCustomizationScreen {

    private static final int TEXT_WIDTH = 240;

    private final CarEntity car;
    private final Screen parent;
    private int statusY;
    private int descY;

    public CarBombScreen(CarEntity car, Screen parent) {
        super(ModText.translatable("gui.mineturismo.bomb.title"));
        this.car = car;
        this.parent = parent;
    }

    private void apply(boolean installed) {

        this.car.setBombInstalled(installed, this.minecraft.player != null ? this.minecraft.player.getUUID() : null);
        SetCarBombPayload mineturismo$payload = new SetCarBombPayload(this.car.getId(), installed);
        ModClientNetworking.sendToServer(SetCarBombPayload.ID, mineturismo$payload, SetCarBombPayload::write);
        this.minecraft.setScreen(new CarBombScreen(this.car, this.parent));
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int top = this.height / 2 - 60;
        this.statusY = top;
        boolean installed = this.car.isBombInstalled();
        int buttonWidth = 200;
        Component toggle = ModText.translatable(installed ? "gui.mineturismo.bomb.remove" : "gui.mineturismo.bomb.install");
        this.addButton(ModWidgets.button(toggle, centerX - buttonWidth / 2, top + 16, buttonWidth, 20, b -> this.apply(!installed)));
        this.descY = top + 46;
        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.back"), centerX - buttonWidth / 2, top + 100, buttonWidth, 20, button ->
                this.minecraft.setScreen(this.parent)));
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTick) {
        mineturismo$renderScreen(new GuiGraphics(null), mouseX, mouseY, partialTick);
    }

    private void mineturismo$renderScreen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        super.render(mouseX, mouseY, partialTick);
        int centerX = this.width / 2;
        guiGraphics.drawCenteredString(this.font, this.title, centerX, this.statusY - 24, 0xFFFFFFFF);
        boolean installed = this.car.isBombInstalled();
        guiGraphics.drawCenteredString(this.font,
                ModText.translatable(installed ? "gui.mineturismo.bomb.status_installed" : "gui.mineturismo.bomb.status_none"),
                centerX, this.statusY, installed ? 0xFFFF5555 : 0xFFAAAAAA);
        List<String> lines = com.examplebett.compat.LegacyGui.split(this.font, ModText.translatable("gui.mineturismo.bomb.desc"), TEXT_WIDTH);
        for (int i = 0; i < lines.size(); i++) {
            guiGraphics.drawCenteredString(this.font, lines.get(i), centerX, this.descY + i * 10, 0xFFDDDDDD);
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
import com.examplebett.entity.CarEntity;
import com.examplebett.network.SetCarBombPayload;

import com.examplebett.compat.GuiGraphics;
import com.examplebett.compat.v114.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

public class CarBombScreen extends Screen implements CarCustomizationScreen {

    private static final int TEXT_WIDTH = 240;

    private final CarEntity car;
    private final Screen parent;
    private int statusY;
    private int descY;

    public CarBombScreen(CarEntity car, Screen parent) {
        super(ModText.translatable("gui.mineturismo.bomb.title"));
        this.car = car;
        this.parent = parent;
    }

    private void apply(boolean installed) {

        this.car.setBombInstalled(installed, this.minecraft.player != null ? this.minecraft.player.getUUID() : null);
        SetCarBombPayload mineturismo$payload = new SetCarBombPayload(this.car.getId(), installed);
        ModClientNetworking.sendToServer(SetCarBombPayload.ID, mineturismo$payload, SetCarBombPayload::write);
        this.minecraft.setScreen(new CarBombScreen(this.car, this.parent));
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int top = this.height / 2 - 60;
        this.statusY = top;
        boolean installed = this.car.isBombInstalled();
        int buttonWidth = 200;
        Component toggle = ModText.translatable(installed ? "gui.mineturismo.bomb.remove" : "gui.mineturismo.bomb.install");
        this.addButton(ModWidgets.button(toggle, centerX - buttonWidth / 2, top + 16, buttonWidth, 20, b -> this.apply(!installed)));
        this.descY = top + 46;
        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.back"), centerX - buttonWidth / 2, top + 100, buttonWidth, 20, button ->
                this.minecraft.setScreen(this.parent)));
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTick) {
        mineturismo$renderScreen(new GuiGraphics(null), mouseX, mouseY, partialTick);
    }

    private void mineturismo$renderScreen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        super.render(mouseX, mouseY, partialTick);
        int centerX = this.width / 2;
        guiGraphics.drawCenteredString(this.font, this.title, centerX, this.statusY - 24, 0xFFFFFFFF);
        boolean installed = this.car.isBombInstalled();
        guiGraphics.drawCenteredString(this.font,
                ModText.translatable(installed ? "gui.mineturismo.bomb.status_installed" : "gui.mineturismo.bomb.status_none"),
                centerX, this.statusY, installed ? 0xFFFF5555 : 0xFFAAAAAA);
        List<String> lines = com.examplebett.compat.LegacyGui.split(this.font, ModText.translatable("gui.mineturismo.bomb.desc"), TEXT_WIDTH);
        for (int i = 0; i < lines.size(); i++) {
            guiGraphics.drawCenteredString(this.font, lines.get(i), centerX, this.descY + i * 10, 0xFFDDDDDD);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
*/
//?}
