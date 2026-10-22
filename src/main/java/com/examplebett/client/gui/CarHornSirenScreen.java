//? if >=1.18 {
//?}
//? if <1.18 && >=1.17 {
/*
package com.examplebett.client.gui;
import com.examplebett.compat.ModWidgets;
import com.examplebett.compat.ModText;

import com.examplebett.compat.ModClientNetworking;
import com.examplebett.entity.CarEntity;
import com.examplebett.entity.HornSirenSounds;
import com.examplebett.network.SetCarHornSirenPayload;

import com.examplebett.compat.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;

public class CarHornSirenScreen extends Screen implements CarCustomizationScreen {

    private static final int COLUMNS = 2;
    private static final int BUTTON_WIDTH = 120;
    private static final int BUTTON_HEIGHT = 20;
    private static final int COL_GAP = 8;
    private static final int ROW_HEIGHT = BUTTON_HEIGHT + 4;
    private static final int LABEL_HEIGHT = 12;
    private static final int SECTION_GAP = 10;

    private static SoundInstance previewSound = null;

    private final CarEntity car;
    private final Screen parent;
    private int hornLabelY;
    private int sirenLabelY;
    private int hintY;
    private boolean reopening;

    public CarHornSirenScreen(CarEntity car, Screen parent) {
        super(ModText.translatable("gui.mineturismo.horn_siren.title"));
        this.car = car;
        this.parent = parent;
    }

    private void sendSettings() {
        SetCarHornSirenPayload mineturismo$payload = new SetCarHornSirenPayload(
                this.car.getId(), this.car.getHornSoundId(), this.car.getSirenSoundId());
        ModClientNetworking.sendToServer(SetCarHornSirenPayload.ID, mineturismo$payload, SetCarHornSirenPayload::write);
    }

    private static void playPreview(SoundEvent sound) {
        stopPreview();

        if (sound == null) {
            return;
        }
        previewSound = SimpleSoundInstance.forUI(sound, 1.0f);
        Minecraft.getInstance().getSoundManager().play(previewSound);
    }

    private static void stopPreview() {
        if (previewSound != null) {
            Minecraft.getInstance().getSoundManager().stop(previewSound);
            previewSound = null;
        }
    }

    private void applyHorn(HornSirenSounds.HornOption option) {
        this.car.setHornSoundId(option.id());
        this.sendSettings();
        playPreview(option.sound());
        this.reopen();
    }

    private void applySiren(HornSirenSounds.SirenOption option) {
        this.car.setSirenSoundId(option.id());
        this.sendSettings();
        playPreview(option.sound());
        this.reopen();
    }

    private void reopen() {
        this.reopening = true;
        this.minecraft.setScreen(new CarHornSirenScreen(this.car, this.parent));
    }

    private static Component markSelected(Component name, boolean selected) {
        return selected ? ModText.literal("> ").append(name) : name;
    }

    private int addGridButton(int index, int gridX, int gridY, Component label, Runnable onPress) {
        int row = index / COLUMNS;
        int col = index % COLUMNS;
        int x = gridX + col * (BUTTON_WIDTH + COL_GAP);
        int y = gridY + row * ROW_HEIGHT;
        this.addRenderableWidget(ModWidgets.button(label, x, y, BUTTON_WIDTH, BUTTON_HEIGHT, b -> onPress.run()));
        return y + BUTTON_HEIGHT;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int gridWidth = COLUMNS * BUTTON_WIDTH + (COLUMNS - 1) * COL_GAP;
        int gridX = centerX - gridWidth / 2;
        int y = this.height / 2 - 90;

        this.hornLabelY = y;
        y += LABEL_HEIGHT;
        int bottom = y;
        String currentHorn = this.car.getHornSoundId();
        for (int i = 0; i < HornSirenSounds.HORNS.size(); i++) {
            HornSirenSounds.HornOption option = HornSirenSounds.HORNS.get(i);
            bottom = this.addGridButton(i, gridX, y, markSelected(option.displayName(), option.id().equals(currentHorn)),
                    () -> this.applyHorn(option));
        }

        y = bottom + SECTION_GAP;
        this.sirenLabelY = y;
        y += LABEL_HEIGHT;
        String currentSiren = this.car.getSirenSoundId();
        for (int i = 0; i < HornSirenSounds.SIRENS.size(); i++) {
            HornSirenSounds.SirenOption option = HornSirenSounds.SIRENS.get(i);
            bottom = this.addGridButton(i, gridX, y, markSelected(option.displayName(), option.id().equals(currentSiren)),
                    () -> this.applySiren(option));
        }

        this.hintY = bottom + 6;
        int buttonWidth = 200;
        this.addRenderableWidget(ModWidgets.button(ModText.translatable("gui.mineturismo.back"), centerX - buttonWidth / 2, this.hintY + 16, buttonWidth, 20, button ->
                this.minecraft.setScreen(this.parent)));
    }

    @Override
    public void removed() {

        if (!this.reopening) {
            stopPreview();
        }
        super.removed();
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        mineturismo$renderScreen(new GuiGraphics(poseStack), mouseX, mouseY, partialTick);
    }

    private void mineturismo$renderScreen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        super.render(guiGraphics.pose(), mouseX, mouseY, partialTick);
        int centerX = this.width / 2;
        guiGraphics.drawCenteredString(this.font, this.title, centerX, this.height / 2 - 110, 0xFFFFFFFF);
        guiGraphics.drawCenteredString(this.font, ModText.translatable("gui.mineturismo.horn_siren.horn_label"), centerX, this.hornLabelY, 0xFFFFFFFF);
        guiGraphics.drawCenteredString(this.font, ModText.translatable("gui.mineturismo.horn_siren.siren_label"), centerX, this.sirenLabelY, 0xFFFFFFFF);
        guiGraphics.drawCenteredString(this.font, ModText.translatable("gui.mineturismo.horn_siren.siren_hint"), centerX, this.hintY, 0xFFAAAAAA);
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
import com.examplebett.entity.HornSirenSounds;
import com.examplebett.network.SetCarHornSirenPayload;

import com.examplebett.compat.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;

public class CarHornSirenScreen extends Screen implements CarCustomizationScreen {

    private static final int COLUMNS = 2;
    private static final int BUTTON_WIDTH = 120;
    private static final int BUTTON_HEIGHT = 20;
    private static final int COL_GAP = 8;
    private static final int ROW_HEIGHT = BUTTON_HEIGHT + 4;
    private static final int LABEL_HEIGHT = 12;
    private static final int SECTION_GAP = 10;

    private static SoundInstance previewSound = null;

    private final CarEntity car;
    private final Screen parent;
    private int hornLabelY;
    private int sirenLabelY;
    private int hintY;
    private boolean reopening;

    public CarHornSirenScreen(CarEntity car, Screen parent) {
        super(ModText.translatable("gui.mineturismo.horn_siren.title"));
        this.car = car;
        this.parent = parent;
    }

    private void sendSettings() {
        SetCarHornSirenPayload mineturismo$payload = new SetCarHornSirenPayload(
                this.car.getId(), this.car.getHornSoundId(), this.car.getSirenSoundId());
        ModClientNetworking.sendToServer(SetCarHornSirenPayload.ID, mineturismo$payload, SetCarHornSirenPayload::write);
    }

    private static void playPreview(SoundEvent sound) {
        stopPreview();

        if (sound == null) {
            return;
        }
        previewSound = SimpleSoundInstance.forUI(sound, 1.0f);
        Minecraft.getInstance().getSoundManager().play(previewSound);
    }

    private static void stopPreview() {
        if (previewSound != null) {
            Minecraft.getInstance().getSoundManager().stop(previewSound);
            previewSound = null;
        }
    }

    private void applyHorn(HornSirenSounds.HornOption option) {
        this.car.setHornSoundId(option.id());
        this.sendSettings();
        playPreview(option.sound());
        this.reopen();
    }

    private void applySiren(HornSirenSounds.SirenOption option) {
        this.car.setSirenSoundId(option.id());
        this.sendSettings();
        playPreview(option.sound());
        this.reopen();
    }

    private void reopen() {
        this.reopening = true;
        this.minecraft.setScreen(new CarHornSirenScreen(this.car, this.parent));
    }

    private static Component markSelected(Component name, boolean selected) {
        return selected ? ModText.literal("> ").append(name) : name;
    }

    private int addGridButton(int index, int gridX, int gridY, Component label, Runnable onPress) {
        int row = index / COLUMNS;
        int col = index % COLUMNS;
        int x = gridX + col * (BUTTON_WIDTH + COL_GAP);
        int y = gridY + row * ROW_HEIGHT;
        this.addButton(ModWidgets.button(label, x, y, BUTTON_WIDTH, BUTTON_HEIGHT, b -> onPress.run()));
        return y + BUTTON_HEIGHT;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int gridWidth = COLUMNS * BUTTON_WIDTH + (COLUMNS - 1) * COL_GAP;
        int gridX = centerX - gridWidth / 2;
        int y = this.height / 2 - 90;

        this.hornLabelY = y;
        y += LABEL_HEIGHT;
        int bottom = y;
        String currentHorn = this.car.getHornSoundId();
        for (int i = 0; i < HornSirenSounds.HORNS.size(); i++) {
            HornSirenSounds.HornOption option = HornSirenSounds.HORNS.get(i);
            bottom = this.addGridButton(i, gridX, y, markSelected(option.displayName(), option.id().equals(currentHorn)),
                    () -> this.applyHorn(option));
        }

        y = bottom + SECTION_GAP;
        this.sirenLabelY = y;
        y += LABEL_HEIGHT;
        String currentSiren = this.car.getSirenSoundId();
        for (int i = 0; i < HornSirenSounds.SIRENS.size(); i++) {
            HornSirenSounds.SirenOption option = HornSirenSounds.SIRENS.get(i);
            bottom = this.addGridButton(i, gridX, y, markSelected(option.displayName(), option.id().equals(currentSiren)),
                    () -> this.applySiren(option));
        }

        this.hintY = bottom + 6;
        int buttonWidth = 200;
        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.back"), centerX - buttonWidth / 2, this.hintY + 16, buttonWidth, 20, button ->
                this.minecraft.setScreen(this.parent)));
    }

    @Override
    public void removed() {

        if (!this.reopening) {
            stopPreview();
        }
        super.removed();
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        mineturismo$renderScreen(new GuiGraphics(poseStack), mouseX, mouseY, partialTick);
    }

    private void mineturismo$renderScreen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        super.render(guiGraphics.pose(), mouseX, mouseY, partialTick);
        int centerX = this.width / 2;
        guiGraphics.drawCenteredString(this.font, this.title, centerX, this.height / 2 - 110, 0xFFFFFFFF);
        guiGraphics.drawCenteredString(this.font, ModText.translatable("gui.mineturismo.horn_siren.horn_label"), centerX, this.hornLabelY, 0xFFFFFFFF);
        guiGraphics.drawCenteredString(this.font, ModText.translatable("gui.mineturismo.horn_siren.siren_label"), centerX, this.sirenLabelY, 0xFFFFFFFF);
        guiGraphics.drawCenteredString(this.font, ModText.translatable("gui.mineturismo.horn_siren.siren_hint"), centerX, this.hintY, 0xFFAAAAAA);
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
import com.examplebett.entity.HornSirenSounds;
import com.examplebett.network.SetCarHornSirenPayload;

import com.examplebett.compat.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;

public class CarHornSirenScreen extends Screen implements CarCustomizationScreen {

    private static final int COLUMNS = 2;
    private static final int BUTTON_WIDTH = 120;
    private static final int BUTTON_HEIGHT = 20;
    private static final int COL_GAP = 8;
    private static final int ROW_HEIGHT = BUTTON_HEIGHT + 4;
    private static final int LABEL_HEIGHT = 12;
    private static final int SECTION_GAP = 10;

    private static SoundInstance previewSound = null;

    private final CarEntity car;
    private final Screen parent;
    private int hornLabelY;
    private int sirenLabelY;
    private int hintY;
    private boolean reopening;

    public CarHornSirenScreen(CarEntity car, Screen parent) {
        super(ModText.translatable("gui.mineturismo.horn_siren.title"));
        this.car = car;
        this.parent = parent;
    }

    private void sendSettings() {
        SetCarHornSirenPayload mineturismo$payload = new SetCarHornSirenPayload(
                this.car.getId(), this.car.getHornSoundId(), this.car.getSirenSoundId());
        ModClientNetworking.sendToServer(SetCarHornSirenPayload.ID, mineturismo$payload, SetCarHornSirenPayload::write);
    }

    private static void playPreview(SoundEvent sound) {
        stopPreview();

        if (sound == null) {
            return;
        }
        previewSound = SimpleSoundInstance.forUI(sound, 1.0f);
        Minecraft.getInstance().getSoundManager().play(previewSound);
    }

    private static void stopPreview() {
        if (previewSound != null) {
            Minecraft.getInstance().getSoundManager().stop(previewSound);
            previewSound = null;
        }
    }

    private void applyHorn(HornSirenSounds.HornOption option) {
        this.car.setHornSoundId(option.id());
        this.sendSettings();
        playPreview(option.sound());
        this.reopen();
    }

    private void applySiren(HornSirenSounds.SirenOption option) {
        this.car.setSirenSoundId(option.id());
        this.sendSettings();
        playPreview(option.sound());
        this.reopen();
    }

    private void reopen() {
        this.reopening = true;
        this.minecraft.setScreen(new CarHornSirenScreen(this.car, this.parent));
    }

    private static Component markSelected(Component name, boolean selected) {
        return selected ? ModText.literal("> ").append(name) : name;
    }

    private int addGridButton(int index, int gridX, int gridY, Component label, Runnable onPress) {
        int row = index / COLUMNS;
        int col = index % COLUMNS;
        int x = gridX + col * (BUTTON_WIDTH + COL_GAP);
        int y = gridY + row * ROW_HEIGHT;
        this.addButton(ModWidgets.button(label, x, y, BUTTON_WIDTH, BUTTON_HEIGHT, b -> onPress.run()));
        return y + BUTTON_HEIGHT;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int gridWidth = COLUMNS * BUTTON_WIDTH + (COLUMNS - 1) * COL_GAP;
        int gridX = centerX - gridWidth / 2;
        int y = this.height / 2 - 90;

        this.hornLabelY = y;
        y += LABEL_HEIGHT;
        int bottom = y;
        String currentHorn = this.car.getHornSoundId();
        for (int i = 0; i < HornSirenSounds.HORNS.size(); i++) {
            HornSirenSounds.HornOption option = HornSirenSounds.HORNS.get(i);
            bottom = this.addGridButton(i, gridX, y, markSelected(option.displayName(), option.id().equals(currentHorn)),
                    () -> this.applyHorn(option));
        }

        y = bottom + SECTION_GAP;
        this.sirenLabelY = y;
        y += LABEL_HEIGHT;
        String currentSiren = this.car.getSirenSoundId();
        for (int i = 0; i < HornSirenSounds.SIRENS.size(); i++) {
            HornSirenSounds.SirenOption option = HornSirenSounds.SIRENS.get(i);
            bottom = this.addGridButton(i, gridX, y, markSelected(option.displayName(), option.id().equals(currentSiren)),
                    () -> this.applySiren(option));
        }

        this.hintY = bottom + 6;
        int buttonWidth = 200;
        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.back"), centerX - buttonWidth / 2, this.hintY + 16, buttonWidth, 20, button ->
                this.minecraft.setScreen(this.parent)));
    }

    @Override
    public void removed() {

        if (!this.reopening) {
            stopPreview();
        }
        super.removed();
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTick) {
        mineturismo$renderScreen(new GuiGraphics(null), mouseX, mouseY, partialTick);
    }

    private void mineturismo$renderScreen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        super.render(mouseX, mouseY, partialTick);
        int centerX = this.width / 2;
        guiGraphics.drawCenteredString(this.font, this.title, centerX, this.height / 2 - 110, 0xFFFFFFFF);
        guiGraphics.drawCenteredString(this.font, ModText.translatable("gui.mineturismo.horn_siren.horn_label"), centerX, this.hornLabelY, 0xFFFFFFFF);
        guiGraphics.drawCenteredString(this.font, ModText.translatable("gui.mineturismo.horn_siren.siren_label"), centerX, this.sirenLabelY, 0xFFFFFFFF);
        guiGraphics.drawCenteredString(this.font, ModText.translatable("gui.mineturismo.horn_siren.siren_hint"), centerX, this.hintY, 0xFFAAAAAA);
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
import com.examplebett.entity.HornSirenSounds;
import com.examplebett.network.SetCarHornSirenPayload;

import com.examplebett.compat.GuiGraphics;
import com.examplebett.compat.v114.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;

public class CarHornSirenScreen extends Screen implements CarCustomizationScreen {

    private static final int COLUMNS = 2;
    private static final int BUTTON_WIDTH = 120;
    private static final int BUTTON_HEIGHT = 20;
    private static final int COL_GAP = 8;
    private static final int ROW_HEIGHT = BUTTON_HEIGHT + 4;
    private static final int LABEL_HEIGHT = 12;
    private static final int SECTION_GAP = 10;

    private static SoundInstance previewSound = null;

    private final CarEntity car;
    private final Screen parent;
    private int hornLabelY;
    private int sirenLabelY;
    private int hintY;
    private boolean reopening;

    public CarHornSirenScreen(CarEntity car, Screen parent) {
        super(ModText.translatable("gui.mineturismo.horn_siren.title"));
        this.car = car;
        this.parent = parent;
    }

    private void sendSettings() {
        SetCarHornSirenPayload mineturismo$payload = new SetCarHornSirenPayload(
                this.car.getId(), this.car.getHornSoundId(), this.car.getSirenSoundId());
        ModClientNetworking.sendToServer(SetCarHornSirenPayload.ID, mineturismo$payload, SetCarHornSirenPayload::write);
    }

    private static void playPreview(SoundEvent sound) {
        stopPreview();

        if (sound == null) {
            return;
        }
        previewSound = SimpleSoundInstance.forUI(sound, 1.0f);
        Minecraft.getInstance().getSoundManager().play(previewSound);
    }

    private static void stopPreview() {
        if (previewSound != null) {
            Minecraft.getInstance().getSoundManager().stop(previewSound);
            previewSound = null;
        }
    }

    private void applyHorn(HornSirenSounds.HornOption option) {
        this.car.setHornSoundId(option.id());
        this.sendSettings();
        playPreview(option.sound());
        this.reopen();
    }

    private void applySiren(HornSirenSounds.SirenOption option) {
        this.car.setSirenSoundId(option.id());
        this.sendSettings();
        playPreview(option.sound());
        this.reopen();
    }

    private void reopen() {
        this.reopening = true;
        this.minecraft.setScreen(new CarHornSirenScreen(this.car, this.parent));
    }

    private static Component markSelected(Component name, boolean selected) {
        return selected ? ModText.literal("> ").append(name) : name;
    }

    private int addGridButton(int index, int gridX, int gridY, Component label, Runnable onPress) {
        int row = index / COLUMNS;
        int col = index % COLUMNS;
        int x = gridX + col * (BUTTON_WIDTH + COL_GAP);
        int y = gridY + row * ROW_HEIGHT;
        this.addButton(ModWidgets.button(label, x, y, BUTTON_WIDTH, BUTTON_HEIGHT, b -> onPress.run()));
        return y + BUTTON_HEIGHT;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int gridWidth = COLUMNS * BUTTON_WIDTH + (COLUMNS - 1) * COL_GAP;
        int gridX = centerX - gridWidth / 2;
        int y = this.height / 2 - 90;

        this.hornLabelY = y;
        y += LABEL_HEIGHT;
        int bottom = y;
        String currentHorn = this.car.getHornSoundId();
        for (int i = 0; i < HornSirenSounds.HORNS.size(); i++) {
            HornSirenSounds.HornOption option = HornSirenSounds.HORNS.get(i);
            bottom = this.addGridButton(i, gridX, y, markSelected(option.displayName(), option.id().equals(currentHorn)),
                    () -> this.applyHorn(option));
        }

        y = bottom + SECTION_GAP;
        this.sirenLabelY = y;
        y += LABEL_HEIGHT;
        String currentSiren = this.car.getSirenSoundId();
        for (int i = 0; i < HornSirenSounds.SIRENS.size(); i++) {
            HornSirenSounds.SirenOption option = HornSirenSounds.SIRENS.get(i);
            bottom = this.addGridButton(i, gridX, y, markSelected(option.displayName(), option.id().equals(currentSiren)),
                    () -> this.applySiren(option));
        }

        this.hintY = bottom + 6;
        int buttonWidth = 200;
        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.back"), centerX - buttonWidth / 2, this.hintY + 16, buttonWidth, 20, button ->
                this.minecraft.setScreen(this.parent)));
    }

    @Override
    public void removed() {

        if (!this.reopening) {
            stopPreview();
        }
        super.removed();
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTick) {
        mineturismo$renderScreen(new GuiGraphics(null), mouseX, mouseY, partialTick);
    }

    private void mineturismo$renderScreen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        super.render(mouseX, mouseY, partialTick);
        int centerX = this.width / 2;
        guiGraphics.drawCenteredString(this.font, this.title, centerX, this.height / 2 - 110, 0xFFFFFFFF);
        guiGraphics.drawCenteredString(this.font, ModText.translatable("gui.mineturismo.horn_siren.horn_label"), centerX, this.hornLabelY, 0xFFFFFFFF);
        guiGraphics.drawCenteredString(this.font, ModText.translatable("gui.mineturismo.horn_siren.siren_label"), centerX, this.sirenLabelY, 0xFFFFFFFF);
        guiGraphics.drawCenteredString(this.font, ModText.translatable("gui.mineturismo.horn_siren.siren_hint"), centerX, this.hintY, 0xFFAAAAAA);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
*/
//?}
