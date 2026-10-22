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
import com.examplebett.network.DeleteGhostLapPayload;
import com.examplebett.network.PlayGhostLapPayload;
import com.examplebett.network.SetGhostRecordingPayload;

import com.examplebett.compat.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class CarGhostLapScreen extends Screen implements CarCustomizationScreen {

    private final CarEntity car;
    private final Screen parent;
    private boolean loop;

    public CarGhostLapScreen(CarEntity car, Screen parent) {
        this(car, parent, false);
    }

    private CarGhostLapScreen(CarEntity car, Screen parent, boolean loop) {
        super(ModText.translatable("gui.mineturismo.ghost_lap.title"));
        this.car = car;
        this.parent = parent;
        this.loop = loop;
    }

    @Override
    protected void init() {
        int buttonWidth = 200;
        int x = this.width / 2 - buttonWidth / 2;
        int y = this.height / 2 - 50;

        boolean recording = this.car.isRecording();
        Component recordLabel = ModText.translatable(recording
                ? "gui.mineturismo.ghost_lap.stop_recording"
                : "gui.mineturismo.ghost_lap.record");
        this.addRenderableWidget(ModWidgets.button(recordLabel, x, y, buttonWidth, 20, button -> {
            boolean nextState = !this.car.isRecording();
            if (nextState) {
                this.car.startRecording();
            } else {
                this.car.stopRecording();
            }
            SetGhostRecordingPayload mineturismo$payload = new SetGhostRecordingPayload(this.car.getId(), nextState);
            ModClientNetworking.sendToServer(SetGhostRecordingPayload.ID, mineturismo$payload, SetGhostRecordingPayload::write);
            this.minecraft.setScreen(new CarGhostLapScreen(this.car, this.parent, this.loop));
        }));

        this.addRenderableWidget(ModCheckbox.builder(ModText.translatable("gui.mineturismo.ghost_lap.loop"), this.font)
                .pos(x, y + 28)
                .selected(this.loop)
                .onValueChange((checkbox, selected) -> this.loop = selected)
                .build());

        this.addRenderableWidget(ModWidgets.button(ModText.translatable("gui.mineturismo.ghost_lap.play"), x, y + 56, buttonWidth, 20, button -> {
            PlayGhostLapPayload mineturismo$payload = new PlayGhostLapPayload(this.car.getId(), this.loop);
            ModClientNetworking.sendToServer(PlayGhostLapPayload.ID, mineturismo$payload, PlayGhostLapPayload::write);
        }));

        this.addRenderableWidget(ModWidgets.button(ModText.translatable("gui.mineturismo.ghost_lap.delete"), x, y + 80, buttonWidth, 20, button -> {
            DeleteGhostLapPayload mineturismo$payload = new DeleteGhostLapPayload(this.car.getId());
            ModClientNetworking.sendToServer(DeleteGhostLapPayload.ID, mineturismo$payload, DeleteGhostLapPayload::write);
        }));

        this.addRenderableWidget(ModWidgets.button(ModText.translatable("gui.mineturismo.back"), x, y + 108, buttonWidth, 20, button ->
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
import com.examplebett.compat.ModCheckbox;
import com.examplebett.compat.ModWidgets;
import com.examplebett.compat.ModText;

import com.examplebett.compat.ModClientNetworking;
import com.examplebett.entity.CarEntity;
import com.examplebett.network.DeleteGhostLapPayload;
import com.examplebett.network.PlayGhostLapPayload;
import com.examplebett.network.SetGhostRecordingPayload;

import com.examplebett.compat.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class CarGhostLapScreen extends Screen implements CarCustomizationScreen {

    private final CarEntity car;
    private final Screen parent;
    private boolean loop;

    public CarGhostLapScreen(CarEntity car, Screen parent) {
        this(car, parent, false);
    }

    private CarGhostLapScreen(CarEntity car, Screen parent, boolean loop) {
        super(ModText.translatable("gui.mineturismo.ghost_lap.title"));
        this.car = car;
        this.parent = parent;
        this.loop = loop;
    }

    @Override
    protected void init() {
        int buttonWidth = 200;
        int x = this.width / 2 - buttonWidth / 2;
        int y = this.height / 2 - 50;

        boolean recording = this.car.isRecording();
        Component recordLabel = ModText.translatable(recording
                ? "gui.mineturismo.ghost_lap.stop_recording"
                : "gui.mineturismo.ghost_lap.record");
        this.addButton(ModWidgets.button(recordLabel, x, y, buttonWidth, 20, button -> {
            boolean nextState = !this.car.isRecording();
            if (nextState) {
                this.car.startRecording();
            } else {
                this.car.stopRecording();
            }
            SetGhostRecordingPayload mineturismo$payload = new SetGhostRecordingPayload(this.car.getId(), nextState);
            ModClientNetworking.sendToServer(SetGhostRecordingPayload.ID, mineturismo$payload, SetGhostRecordingPayload::write);
            this.minecraft.setScreen(new CarGhostLapScreen(this.car, this.parent, this.loop));
        }));

        this.addButton(ModCheckbox.builder(ModText.translatable("gui.mineturismo.ghost_lap.loop"), this.font)
                .pos(x, y + 28)
                .selected(this.loop)
                .onValueChange((checkbox, selected) -> this.loop = selected)
                .build());

        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.ghost_lap.play"), x, y + 56, buttonWidth, 20, button -> {
            PlayGhostLapPayload mineturismo$payload = new PlayGhostLapPayload(this.car.getId(), this.loop);
            ModClientNetworking.sendToServer(PlayGhostLapPayload.ID, mineturismo$payload, PlayGhostLapPayload::write);
        }));

        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.ghost_lap.delete"), x, y + 80, buttonWidth, 20, button -> {
            DeleteGhostLapPayload mineturismo$payload = new DeleteGhostLapPayload(this.car.getId());
            ModClientNetworking.sendToServer(DeleteGhostLapPayload.ID, mineturismo$payload, DeleteGhostLapPayload::write);
        }));

        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.back"), x, y + 108, buttonWidth, 20, button ->
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
import com.examplebett.compat.ModCheckbox;
import com.examplebett.compat.ModWidgets;
import com.examplebett.compat.ModText;

import com.examplebett.compat.ModClientNetworking;
import com.examplebett.entity.CarEntity;
import com.examplebett.network.DeleteGhostLapPayload;
import com.examplebett.network.PlayGhostLapPayload;
import com.examplebett.network.SetGhostRecordingPayload;

import com.examplebett.compat.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class CarGhostLapScreen extends Screen implements CarCustomizationScreen {

    private final CarEntity car;
    private final Screen parent;
    private boolean loop;

    public CarGhostLapScreen(CarEntity car, Screen parent) {
        this(car, parent, false);
    }

    private CarGhostLapScreen(CarEntity car, Screen parent, boolean loop) {
        super(ModText.translatable("gui.mineturismo.ghost_lap.title"));
        this.car = car;
        this.parent = parent;
        this.loop = loop;
    }

    @Override
    protected void init() {
        int buttonWidth = 200;
        int x = this.width / 2 - buttonWidth / 2;
        int y = this.height / 2 - 50;

        boolean recording = this.car.isRecording();
        Component recordLabel = ModText.translatable(recording
                ? "gui.mineturismo.ghost_lap.stop_recording"
                : "gui.mineturismo.ghost_lap.record");
        this.addButton(ModWidgets.button(recordLabel, x, y, buttonWidth, 20, button -> {
            boolean nextState = !this.car.isRecording();
            if (nextState) {
                this.car.startRecording();
            } else {
                this.car.stopRecording();
            }
            SetGhostRecordingPayload mineturismo$payload = new SetGhostRecordingPayload(this.car.getId(), nextState);
            ModClientNetworking.sendToServer(SetGhostRecordingPayload.ID, mineturismo$payload, SetGhostRecordingPayload::write);
            this.minecraft.setScreen(new CarGhostLapScreen(this.car, this.parent, this.loop));
        }));

        this.addButton(ModCheckbox.builder(ModText.translatable("gui.mineturismo.ghost_lap.loop"), this.font)
                .pos(x, y + 28)
                .selected(this.loop)
                .onValueChange((checkbox, selected) -> this.loop = selected)
                .build());

        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.ghost_lap.play"), x, y + 56, buttonWidth, 20, button -> {
            PlayGhostLapPayload mineturismo$payload = new PlayGhostLapPayload(this.car.getId(), this.loop);
            ModClientNetworking.sendToServer(PlayGhostLapPayload.ID, mineturismo$payload, PlayGhostLapPayload::write);
        }));

        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.ghost_lap.delete"), x, y + 80, buttonWidth, 20, button -> {
            DeleteGhostLapPayload mineturismo$payload = new DeleteGhostLapPayload(this.car.getId());
            ModClientNetworking.sendToServer(DeleteGhostLapPayload.ID, mineturismo$payload, DeleteGhostLapPayload::write);
        }));

        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.back"), x, y + 108, buttonWidth, 20, button ->
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
import com.examplebett.compat.ModCheckbox;
import com.examplebett.compat.ModWidgets;
import com.examplebett.compat.ModText;

import com.examplebett.compat.ModClientNetworking;
import com.examplebett.entity.CarEntity;
import com.examplebett.network.DeleteGhostLapPayload;
import com.examplebett.network.PlayGhostLapPayload;
import com.examplebett.network.SetGhostRecordingPayload;

import com.examplebett.compat.GuiGraphics;
import com.examplebett.compat.v114.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class CarGhostLapScreen extends Screen implements CarCustomizationScreen {

    private final CarEntity car;
    private final Screen parent;
    private boolean loop;

    public CarGhostLapScreen(CarEntity car, Screen parent) {
        this(car, parent, false);
    }

    private CarGhostLapScreen(CarEntity car, Screen parent, boolean loop) {
        super(ModText.translatable("gui.mineturismo.ghost_lap.title"));
        this.car = car;
        this.parent = parent;
        this.loop = loop;
    }

    @Override
    protected void init() {
        int buttonWidth = 200;
        int x = this.width / 2 - buttonWidth / 2;
        int y = this.height / 2 - 50;

        boolean recording = this.car.isRecording();
        Component recordLabel = ModText.translatable(recording
                ? "gui.mineturismo.ghost_lap.stop_recording"
                : "gui.mineturismo.ghost_lap.record");
        this.addButton(ModWidgets.button(recordLabel, x, y, buttonWidth, 20, button -> {
            boolean nextState = !this.car.isRecording();
            if (nextState) {
                this.car.startRecording();
            } else {
                this.car.stopRecording();
            }
            SetGhostRecordingPayload mineturismo$payload = new SetGhostRecordingPayload(this.car.getId(), nextState);
            ModClientNetworking.sendToServer(SetGhostRecordingPayload.ID, mineturismo$payload, SetGhostRecordingPayload::write);
            this.minecraft.setScreen(new CarGhostLapScreen(this.car, this.parent, this.loop));
        }));

        this.addButton(ModCheckbox.builder(ModText.translatable("gui.mineturismo.ghost_lap.loop"), this.font)
                .pos(x, y + 28)
                .selected(this.loop)
                .onValueChange((checkbox, selected) -> this.loop = selected)
                .build());

        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.ghost_lap.play"), x, y + 56, buttonWidth, 20, button -> {
            PlayGhostLapPayload mineturismo$payload = new PlayGhostLapPayload(this.car.getId(), this.loop);
            ModClientNetworking.sendToServer(PlayGhostLapPayload.ID, mineturismo$payload, PlayGhostLapPayload::write);
        }));

        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.ghost_lap.delete"), x, y + 80, buttonWidth, 20, button -> {
            DeleteGhostLapPayload mineturismo$payload = new DeleteGhostLapPayload(this.car.getId());
            ModClientNetworking.sendToServer(DeleteGhostLapPayload.ID, mineturismo$payload, DeleteGhostLapPayload::write);
        }));

        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.back"), x, y + 108, buttonWidth, 20, button ->
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
