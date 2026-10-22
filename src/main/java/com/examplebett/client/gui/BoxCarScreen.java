//? if >=1.18 {
//?}
//? if <1.18 && >=1.17 {
/*
package com.examplebett.client.gui;
import com.examplebett.compat.ModWidgets;
import com.examplebett.compat.ModText;

import com.examplebett.compat.ModClientNetworking;
import com.examplebett.entity.CarEntity;
import com.examplebett.network.BoxCarPayload;

import com.examplebett.compat.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class BoxCarScreen extends Screen implements CarCustomizationScreen {

    private static final int FIELD_WIDTH = 200;
    private static final int ROW_HEIGHT = 20;
    private static final int ROW_GAP = 4;

    private final CarEntity car;
    private final Screen parent;
    private EditBox nameBox;

    public BoxCarScreen(CarEntity car, Screen parent) {
        super(ModText.translatable("gui.mineturismo.box_car.title"));
        this.car = car;
        this.parent = parent;
    }

    @Override
    protected void init() {
        int x = this.width / 2 - FIELD_WIDTH / 2;
        int y = this.height / 2 - 40;

        this.nameBox = new EditBox(this.font, x, y, FIELD_WIDTH, ROW_HEIGHT,
                ModText.translatable("gui.mineturismo.box_car.name_hint"));
        this.nameBox.setMaxLength(64);
        ModWidgets.setHint(this.nameBox, ModText.translatable("gui.mineturismo.box_car.name_hint"));
        this.addRenderableWidget(this.nameBox);
        this.setInitialFocus(this.nameBox);

        this.addRenderableWidget(ModWidgets.button(ModText.translatable("gui.mineturismo.box_car.confirm"), x, y + ROW_HEIGHT + ROW_GAP, FIELD_WIDTH, ROW_HEIGHT, button -> {
            BoxCarPayload payload = new BoxCarPayload(this.car.getId(), this.nameBox.getValue());
            ModClientNetworking.sendToServer(BoxCarPayload.ID, payload, BoxCarPayload::write);

            this.minecraft.setScreen(null);
        }));

        this.addRenderableWidget(ModWidgets.button(ModText.translatable("gui.mineturismo.back"), x, y + (ROW_HEIGHT + ROW_GAP) * 2, FIELD_WIDTH, ROW_HEIGHT, button ->
                this.minecraft.setScreen(this.parent)));
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        mineturismo$renderScreen(new GuiGraphics(poseStack), mouseX, mouseY, partialTick);
    }

    private void mineturismo$renderScreen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        super.render(guiGraphics.pose(), mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - 70, 0xFFFFFFFF);
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
import com.examplebett.entity.CarEntity;
import com.examplebett.network.BoxCarPayload;

import com.examplebett.compat.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class BoxCarScreen extends Screen implements CarCustomizationScreen {

    private static final int FIELD_WIDTH = 200;
    private static final int ROW_HEIGHT = 20;
    private static final int ROW_GAP = 4;

    private final CarEntity car;
    private final Screen parent;
    private EditBox nameBox;

    public BoxCarScreen(CarEntity car, Screen parent) {
        super(ModText.translatable("gui.mineturismo.box_car.title"));
        this.car = car;
        this.parent = parent;
    }

    @Override
    protected void init() {
        int x = this.width / 2 - FIELD_WIDTH / 2;
        int y = this.height / 2 - 40;

        this.nameBox = new EditBox(this.font, x, y, FIELD_WIDTH, ROW_HEIGHT,
                ModText.translatable("gui.mineturismo.box_car.name_hint"));
        this.nameBox.setMaxLength(64);
        ModWidgets.setHint(this.nameBox, ModText.translatable("gui.mineturismo.box_car.name_hint"));
        this.addButton(this.nameBox);
        this.setInitialFocus(this.nameBox);

        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.box_car.confirm"), x, y + ROW_HEIGHT + ROW_GAP, FIELD_WIDTH, ROW_HEIGHT, button -> {
            BoxCarPayload payload = new BoxCarPayload(this.car.getId(), this.nameBox.getValue());
            ModClientNetworking.sendToServer(BoxCarPayload.ID, payload, BoxCarPayload::write);

            this.minecraft.setScreen(null);
        }));

        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.back"), x, y + (ROW_HEIGHT + ROW_GAP) * 2, FIELD_WIDTH, ROW_HEIGHT, button ->
                this.minecraft.setScreen(this.parent)));
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        mineturismo$renderScreen(new GuiGraphics(poseStack), mouseX, mouseY, partialTick);
    }

    private void mineturismo$renderScreen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        super.render(guiGraphics.pose(), mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - 70, 0xFFFFFFFF);
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
import com.examplebett.network.BoxCarPayload;

import com.examplebett.compat.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class BoxCarScreen extends Screen implements CarCustomizationScreen {

    private static final int FIELD_WIDTH = 200;
    private static final int ROW_HEIGHT = 20;
    private static final int ROW_GAP = 4;

    private final CarEntity car;
    private final Screen parent;
    private EditBox nameBox;

    public BoxCarScreen(CarEntity car, Screen parent) {
        super(ModText.translatable("gui.mineturismo.box_car.title"));
        this.car = car;
        this.parent = parent;
    }

    @Override
    protected void init() {
        int x = this.width / 2 - FIELD_WIDTH / 2;
        int y = this.height / 2 - 40;

        this.nameBox = new EditBox(this.font, x, y, FIELD_WIDTH, ROW_HEIGHT,
                com.examplebett.compat.LegacyGui.text(ModText.translatable("gui.mineturismo.box_car.name_hint")));
        this.nameBox.setMaxLength(64);
        ModWidgets.setHint(this.nameBox, ModText.translatable("gui.mineturismo.box_car.name_hint"));
        this.addButton(this.nameBox);
        this.setInitialFocus(this.nameBox);

        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.box_car.confirm"), x, y + ROW_HEIGHT + ROW_GAP, FIELD_WIDTH, ROW_HEIGHT, button -> {
            BoxCarPayload payload = new BoxCarPayload(this.car.getId(), this.nameBox.getValue());
            ModClientNetworking.sendToServer(BoxCarPayload.ID, payload, BoxCarPayload::write);

            this.minecraft.setScreen(null);
        }));

        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.back"), x, y + (ROW_HEIGHT + ROW_GAP) * 2, FIELD_WIDTH, ROW_HEIGHT, button ->
                this.minecraft.setScreen(this.parent)));
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTick) {
        mineturismo$renderScreen(new GuiGraphics(null), mouseX, mouseY, partialTick);
    }

    private void mineturismo$renderScreen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        super.render(mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - 70, 0xFFFFFFFF);
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
import com.examplebett.network.BoxCarPayload;

import com.examplebett.compat.GuiGraphics;
import com.examplebett.compat.v114.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class BoxCarScreen extends Screen implements CarCustomizationScreen {

    private static final int FIELD_WIDTH = 200;
    private static final int ROW_HEIGHT = 20;
    private static final int ROW_GAP = 4;

    private final CarEntity car;
    private final Screen parent;
    private EditBox nameBox;

    public BoxCarScreen(CarEntity car, Screen parent) {
        super(ModText.translatable("gui.mineturismo.box_car.title"));
        this.car = car;
        this.parent = parent;
    }

    @Override
    protected void init() {
        int x = this.width / 2 - FIELD_WIDTH / 2;
        int y = this.height / 2 - 40;

        this.nameBox = new EditBox(this.font, x, y, FIELD_WIDTH, ROW_HEIGHT,
                com.examplebett.compat.LegacyGui.text(ModText.translatable("gui.mineturismo.box_car.name_hint")));
        this.nameBox.setMaxLength(64);
        ModWidgets.setHint(this.nameBox, ModText.translatable("gui.mineturismo.box_car.name_hint"));
        this.addButton(this.nameBox);
        this.setInitialFocus(this.nameBox);

        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.box_car.confirm"), x, y + ROW_HEIGHT + ROW_GAP, FIELD_WIDTH, ROW_HEIGHT, button -> {
            BoxCarPayload payload = new BoxCarPayload(this.car.getId(), this.nameBox.getValue());
            ModClientNetworking.sendToServer(BoxCarPayload.ID, payload, BoxCarPayload::write);

            this.minecraft.setScreen(null);
        }));

        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.back"), x, y + (ROW_HEIGHT + ROW_GAP) * 2, FIELD_WIDTH, ROW_HEIGHT, button ->
                this.minecraft.setScreen(this.parent)));
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTick) {
        mineturismo$renderScreen(new GuiGraphics(null), mouseX, mouseY, partialTick);
    }

    private void mineturismo$renderScreen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        super.render(mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - 70, 0xFFFFFFFF);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
*/
//?}
