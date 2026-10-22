//? if >=1.18 {
//?}
//? if <1.18 && >=1.17 {
/*
package com.examplebett.client.gui;
import com.examplebett.compat.ModCheckbox;
import com.examplebett.compat.ModWidgets;
import com.examplebett.compat.ModText;

import com.examplebett.compat.ModClientNetworking;
import com.examplebett.client.AiObjectModClient;
import com.examplebett.entity.CarEntity;
import com.examplebett.network.SetBreakVanillaBlocksPayload;
import com.examplebett.network.SetHitboxDisabledPayload;
import com.examplebett.network.SetMobDamagePayload;

import com.examplebett.compat.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class OtherScreen extends Screen implements CarCustomizationScreen {

    private final CarEntity car;
    private final Screen parent;
    private int contentTop;

    public OtherScreen(CarEntity car, Screen parent) {
        super(ModText.translatable("gui.mineturismo.other_menu.title"));
        this.car = car;
        this.parent = parent;
    }

    private void sendHitboxDisabled(boolean disabled) {
        CarEntity.setHitboxDisabled(disabled);
        SetHitboxDisabledPayload mineturismo$payload = new SetHitboxDisabledPayload(disabled);
        ModClientNetworking.sendToServer(SetHitboxDisabledPayload.ID, mineturismo$payload, SetHitboxDisabledPayload::write);
    }

    private void sendBreakVanillaBlocks(boolean enabled) {
        CarEntity.setBreakVanillaBlocksEnabled(enabled);
        SetBreakVanillaBlocksPayload mineturismo$payload = new SetBreakVanillaBlocksPayload(enabled);
        ModClientNetworking.sendToServer(SetBreakVanillaBlocksPayload.ID, mineturismo$payload, SetBreakVanillaBlocksPayload::write);
    }

    private void sendMobDamage(boolean enabled) {
        CarEntity.setMobDamageEnabled(enabled);
        SetMobDamagePayload mineturismo$payload = new SetMobDamagePayload(enabled);
        ModClientNetworking.sendToServer(SetMobDamagePayload.ID, mineturismo$payload, SetMobDamagePayload::write);
    }

    private Component speedDisplayLabel() {
        String stateKey = switch (AiObjectModClient.getSpeedDisplayMode()) {
            case NONE -> "gui.mineturismo.other_menu.speed_display_none";
            case KMH -> "gui.mineturismo.other_menu.speed_display_kmh";
            case MPH -> "gui.mineturismo.other_menu.speed_display_mph";
        };
        return ModText.translatable("gui.mineturismo.other_menu.speed_display", ModText.translatable(stateKey));
    }

    @Override
    protected void init() {
        int buttonWidth = 200;
        int x = this.width / 2 - buttonWidth / 2;
        int y = this.height / 2 - 60;
        this.contentTop = y;

        this.addRenderableWidget(ModCheckbox.builder(ModText.translatable("gui.mineturismo.debug_wrench_menu.disable_hitbox"), this.font)
                .pos(x, y)
                .selected(CarEntity.isHitboxDisabled())
                .onValueChange((checkbox, selected) -> this.sendHitboxDisabled(selected))
                .build());

        this.addRenderableWidget(ModCheckbox.builder(ModText.translatable("gui.mineturismo.debug_wrench_menu.break_vanilla_blocks"), this.font)
                .pos(x, y + 24)
                .selected(CarEntity.isBreakVanillaBlocksEnabled())
                .onValueChange((checkbox, selected) -> this.sendBreakVanillaBlocks(selected))
                .build());

        this.addRenderableWidget(ModCheckbox.builder(ModText.translatable("gui.mineturismo.debug_wrench_menu.mob_damage"), this.font)
                .pos(x, y + 48)
                .selected(CarEntity.isMobDamageEnabled())
                .onValueChange((checkbox, selected) -> this.sendMobDamage(selected))
                .build());

        this.addRenderableWidget(ModCheckbox.builder(ModText.translatable("gui.mineturismo.debug_wrench_menu.custom_font"), this.font)
                .pos(x, y + 72)
                .selected(AiObjectModClient.isMountToastCustomFontEnabled())
                .onValueChange((checkbox, selected) -> AiObjectModClient.setMountToastCustomFontEnabled(selected))
                .build());

        this.addRenderableWidget(ModCheckbox.builder(ModText.translatable("gui.mineturismo.debug_wrench_menu.fake_reflection"), this.font)
                .pos(x, y + 96)
                .selected(AiObjectModClient.isFakeReflectionEnabled())
                .onValueChange((checkbox, selected) -> AiObjectModClient.setFakeReflectionEnabled(selected))
                .build());

        this.addRenderableWidget(ModCheckbox.builder(ModText.translatable("gui.mineturismo.debug_wrench_menu.hide_hints"), this.font)
                .pos(x, y + 120)
                .selected(!AiObjectModClient.isHintsEnabled())
                .onValueChange((checkbox, selected) -> AiObjectModClient.setHintsEnabled(!selected))
                .build());

        this.addRenderableWidget(ModCheckbox.builder(ModText.translatable("gui.mineturismo.debug_wrench_menu.fps_log"), this.font)
                .pos(x, y + 144)
                .selected(AiObjectModClient.isFpsLogEnabled())
                .onValueChange((checkbox, selected) -> AiObjectModClient.setFpsLogEnabled(selected))
                .build());

        int speedRowY = y + 168;
        this.addRenderableWidget(ModWidgets.button(this.speedDisplayLabel(), x, speedRowY, buttonWidth, 20, button -> {
            AiObjectModClient.setSpeedDisplayMode(AiObjectModClient.getSpeedDisplayMode().next());
            button.setMessage(this.speedDisplayLabel());
        }));

        Component backLabel = ModText.translatable("gui.mineturismo.back");
        int backWidth = Math.max(60, this.font.width(backLabel) + 16);
        this.addRenderableWidget(ModWidgets.button(backLabel, x + buttonWidth / 2 - backWidth / 2, speedRowY + 28, backWidth, 20, button ->
                this.minecraft.setScreen(this.parent)));
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        mineturismo$renderScreen(new GuiGraphics(poseStack), mouseX, mouseY, partialTick);
    }

    private void mineturismo$renderScreen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        super.render(guiGraphics.pose(), mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, this.contentTop - 16, 0xFFFFFFFF);
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
import com.examplebett.client.AiObjectModClient;
import com.examplebett.entity.CarEntity;
import com.examplebett.network.SetBreakVanillaBlocksPayload;
import com.examplebett.network.SetHitboxDisabledPayload;
import com.examplebett.network.SetMobDamagePayload;

import com.examplebett.compat.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class OtherScreen extends Screen implements CarCustomizationScreen {

    private final CarEntity car;
    private final Screen parent;
    private int contentTop;

    public OtherScreen(CarEntity car, Screen parent) {
        super(ModText.translatable("gui.mineturismo.other_menu.title"));
        this.car = car;
        this.parent = parent;
    }

    private void sendHitboxDisabled(boolean disabled) {
        CarEntity.setHitboxDisabled(disabled);
        SetHitboxDisabledPayload mineturismo$payload = new SetHitboxDisabledPayload(disabled);
        ModClientNetworking.sendToServer(SetHitboxDisabledPayload.ID, mineturismo$payload, SetHitboxDisabledPayload::write);
    }

    private void sendBreakVanillaBlocks(boolean enabled) {
        CarEntity.setBreakVanillaBlocksEnabled(enabled);
        SetBreakVanillaBlocksPayload mineturismo$payload = new SetBreakVanillaBlocksPayload(enabled);
        ModClientNetworking.sendToServer(SetBreakVanillaBlocksPayload.ID, mineturismo$payload, SetBreakVanillaBlocksPayload::write);
    }

    private void sendMobDamage(boolean enabled) {
        CarEntity.setMobDamageEnabled(enabled);
        SetMobDamagePayload mineturismo$payload = new SetMobDamagePayload(enabled);
        ModClientNetworking.sendToServer(SetMobDamagePayload.ID, mineturismo$payload, SetMobDamagePayload::write);
    }

    private Component speedDisplayLabel() {
        String stateKey = switch (AiObjectModClient.getSpeedDisplayMode()) {
            case NONE -> "gui.mineturismo.other_menu.speed_display_none";
            case KMH -> "gui.mineturismo.other_menu.speed_display_kmh";
            case MPH -> "gui.mineturismo.other_menu.speed_display_mph";
        };
        return ModText.translatable("gui.mineturismo.other_menu.speed_display", ModText.translatable(stateKey));
    }

    @Override
    protected void init() {
        int buttonWidth = 200;
        int x = this.width / 2 - buttonWidth / 2;
        int y = this.height / 2 - 60;
        this.contentTop = y;

        this.addButton(ModCheckbox.builder(ModText.translatable("gui.mineturismo.debug_wrench_menu.disable_hitbox"), this.font)
                .pos(x, y)
                .selected(CarEntity.isHitboxDisabled())
                .onValueChange((checkbox, selected) -> this.sendHitboxDisabled(selected))
                .build());

        this.addButton(ModCheckbox.builder(ModText.translatable("gui.mineturismo.debug_wrench_menu.break_vanilla_blocks"), this.font)
                .pos(x, y + 24)
                .selected(CarEntity.isBreakVanillaBlocksEnabled())
                .onValueChange((checkbox, selected) -> this.sendBreakVanillaBlocks(selected))
                .build());

        this.addButton(ModCheckbox.builder(ModText.translatable("gui.mineturismo.debug_wrench_menu.mob_damage"), this.font)
                .pos(x, y + 48)
                .selected(CarEntity.isMobDamageEnabled())
                .onValueChange((checkbox, selected) -> this.sendMobDamage(selected))
                .build());

        this.addButton(ModCheckbox.builder(ModText.translatable("gui.mineturismo.debug_wrench_menu.custom_font"), this.font)
                .pos(x, y + 72)
                .selected(AiObjectModClient.isMountToastCustomFontEnabled())
                .onValueChange((checkbox, selected) -> AiObjectModClient.setMountToastCustomFontEnabled(selected))
                .build());

        this.addButton(ModCheckbox.builder(ModText.translatable("gui.mineturismo.debug_wrench_menu.fake_reflection"), this.font)
                .pos(x, y + 96)
                .selected(AiObjectModClient.isFakeReflectionEnabled())
                .onValueChange((checkbox, selected) -> AiObjectModClient.setFakeReflectionEnabled(selected))
                .build());

        this.addButton(ModCheckbox.builder(ModText.translatable("gui.mineturismo.debug_wrench_menu.hide_hints"), this.font)
                .pos(x, y + 120)
                .selected(!AiObjectModClient.isHintsEnabled())
                .onValueChange((checkbox, selected) -> AiObjectModClient.setHintsEnabled(!selected))
                .build());

        this.addButton(ModCheckbox.builder(ModText.translatable("gui.mineturismo.debug_wrench_menu.fps_log"), this.font)
                .pos(x, y + 144)
                .selected(AiObjectModClient.isFpsLogEnabled())
                .onValueChange((checkbox, selected) -> AiObjectModClient.setFpsLogEnabled(selected))
                .build());

        int speedRowY = y + 168;
        this.addButton(ModWidgets.button(this.speedDisplayLabel(), x, speedRowY, buttonWidth, 20, button -> {
            AiObjectModClient.setSpeedDisplayMode(AiObjectModClient.getSpeedDisplayMode().next());
            button.setMessage(this.speedDisplayLabel());
        }));

        Component backLabel = ModText.translatable("gui.mineturismo.back");
        int backWidth = Math.max(60, this.font.width(backLabel) + 16);
        this.addButton(ModWidgets.button(backLabel, x + buttonWidth / 2 - backWidth / 2, speedRowY + 28, backWidth, 20, button ->
                this.minecraft.setScreen(this.parent)));
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        mineturismo$renderScreen(new GuiGraphics(poseStack), mouseX, mouseY, partialTick);
    }

    private void mineturismo$renderScreen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        super.render(guiGraphics.pose(), mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, this.contentTop - 16, 0xFFFFFFFF);
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
import com.examplebett.client.AiObjectModClient;
import com.examplebett.entity.CarEntity;
import com.examplebett.network.SetBreakVanillaBlocksPayload;
import com.examplebett.network.SetHitboxDisabledPayload;
import com.examplebett.network.SetMobDamagePayload;

import com.examplebett.compat.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class OtherScreen extends Screen implements CarCustomizationScreen {

    private final CarEntity car;
    private final Screen parent;
    private int contentTop;

    public OtherScreen(CarEntity car, Screen parent) {
        super(ModText.translatable("gui.mineturismo.other_menu.title"));
        this.car = car;
        this.parent = parent;
    }

    private void sendHitboxDisabled(boolean disabled) {
        CarEntity.setHitboxDisabled(disabled);
        SetHitboxDisabledPayload mineturismo$payload = new SetHitboxDisabledPayload(disabled);
        ModClientNetworking.sendToServer(SetHitboxDisabledPayload.ID, mineturismo$payload, SetHitboxDisabledPayload::write);
    }

    private void sendBreakVanillaBlocks(boolean enabled) {
        CarEntity.setBreakVanillaBlocksEnabled(enabled);
        SetBreakVanillaBlocksPayload mineturismo$payload = new SetBreakVanillaBlocksPayload(enabled);
        ModClientNetworking.sendToServer(SetBreakVanillaBlocksPayload.ID, mineturismo$payload, SetBreakVanillaBlocksPayload::write);
    }

    private void sendMobDamage(boolean enabled) {
        CarEntity.setMobDamageEnabled(enabled);
        SetMobDamagePayload mineturismo$payload = new SetMobDamagePayload(enabled);
        ModClientNetworking.sendToServer(SetMobDamagePayload.ID, mineturismo$payload, SetMobDamagePayload::write);
    }

    private Component speedDisplayLabel() {
        String stateKey = switch (AiObjectModClient.getSpeedDisplayMode()) {
            case NONE -> "gui.mineturismo.other_menu.speed_display_none";
            case KMH -> "gui.mineturismo.other_menu.speed_display_kmh";
            case MPH -> "gui.mineturismo.other_menu.speed_display_mph";
        };
        return ModText.translatable("gui.mineturismo.other_menu.speed_display", ModText.translatable(stateKey));
    }

    @Override
    protected void init() {
        int buttonWidth = 200;
        int x = this.width / 2 - buttonWidth / 2;
        int y = this.height / 2 - 60;
        this.contentTop = y;

        this.addButton(ModCheckbox.builder(ModText.translatable("gui.mineturismo.debug_wrench_menu.disable_hitbox"), this.font)
                .pos(x, y)
                .selected(CarEntity.isHitboxDisabled())
                .onValueChange((checkbox, selected) -> this.sendHitboxDisabled(selected))
                .build());

        this.addButton(ModCheckbox.builder(ModText.translatable("gui.mineturismo.debug_wrench_menu.break_vanilla_blocks"), this.font)
                .pos(x, y + 24)
                .selected(CarEntity.isBreakVanillaBlocksEnabled())
                .onValueChange((checkbox, selected) -> this.sendBreakVanillaBlocks(selected))
                .build());

        this.addButton(ModCheckbox.builder(ModText.translatable("gui.mineturismo.debug_wrench_menu.mob_damage"), this.font)
                .pos(x, y + 48)
                .selected(CarEntity.isMobDamageEnabled())
                .onValueChange((checkbox, selected) -> this.sendMobDamage(selected))
                .build());

        this.addButton(ModCheckbox.builder(ModText.translatable("gui.mineturismo.debug_wrench_menu.custom_font"), this.font)
                .pos(x, y + 72)
                .selected(AiObjectModClient.isMountToastCustomFontEnabled())
                .onValueChange((checkbox, selected) -> AiObjectModClient.setMountToastCustomFontEnabled(selected))
                .build());

        this.addButton(ModCheckbox.builder(ModText.translatable("gui.mineturismo.debug_wrench_menu.fake_reflection"), this.font)
                .pos(x, y + 96)
                .selected(AiObjectModClient.isFakeReflectionEnabled())
                .onValueChange((checkbox, selected) -> AiObjectModClient.setFakeReflectionEnabled(selected))
                .build());

        this.addButton(ModCheckbox.builder(ModText.translatable("gui.mineturismo.debug_wrench_menu.hide_hints"), this.font)
                .pos(x, y + 120)
                .selected(!AiObjectModClient.isHintsEnabled())
                .onValueChange((checkbox, selected) -> AiObjectModClient.setHintsEnabled(!selected))
                .build());

        this.addButton(ModCheckbox.builder(ModText.translatable("gui.mineturismo.debug_wrench_menu.fps_log"), this.font)
                .pos(x, y + 144)
                .selected(AiObjectModClient.isFpsLogEnabled())
                .onValueChange((checkbox, selected) -> AiObjectModClient.setFpsLogEnabled(selected))
                .build());

        int speedRowY = y + 168;
        this.addButton(ModWidgets.button(this.speedDisplayLabel(), x, speedRowY, buttonWidth, 20, button -> {
            AiObjectModClient.setSpeedDisplayMode(AiObjectModClient.getSpeedDisplayMode().next());
            com.examplebett.compat.LegacyGui.setMessage(button, this.speedDisplayLabel());
        }));

        Component backLabel = ModText.translatable("gui.mineturismo.back");
        int backWidth = Math.max(60, com.examplebett.compat.LegacyGui.width(this.font, backLabel) + 16);
        this.addButton(ModWidgets.button(backLabel, x + buttonWidth / 2 - backWidth / 2, speedRowY + 28, backWidth, 20, button ->
                this.minecraft.setScreen(this.parent)));
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTick) {
        mineturismo$renderScreen(new GuiGraphics(null), mouseX, mouseY, partialTick);
    }

    private void mineturismo$renderScreen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        super.render(mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, this.contentTop - 16, 0xFFFFFFFF);
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
import com.examplebett.client.AiObjectModClient;
import com.examplebett.entity.CarEntity;
import com.examplebett.network.SetBreakVanillaBlocksPayload;
import com.examplebett.network.SetHitboxDisabledPayload;
import com.examplebett.network.SetMobDamagePayload;

import com.examplebett.compat.GuiGraphics;
import com.examplebett.compat.v114.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class OtherScreen extends Screen implements CarCustomizationScreen {

    private final CarEntity car;
    private final Screen parent;
    private int contentTop;

    public OtherScreen(CarEntity car, Screen parent) {
        super(ModText.translatable("gui.mineturismo.other_menu.title"));
        this.car = car;
        this.parent = parent;
    }

    private void sendHitboxDisabled(boolean disabled) {
        CarEntity.setHitboxDisabled(disabled);
        SetHitboxDisabledPayload mineturismo$payload = new SetHitboxDisabledPayload(disabled);
        ModClientNetworking.sendToServer(SetHitboxDisabledPayload.ID, mineturismo$payload, SetHitboxDisabledPayload::write);
    }

    private void sendBreakVanillaBlocks(boolean enabled) {
        CarEntity.setBreakVanillaBlocksEnabled(enabled);
        SetBreakVanillaBlocksPayload mineturismo$payload = new SetBreakVanillaBlocksPayload(enabled);
        ModClientNetworking.sendToServer(SetBreakVanillaBlocksPayload.ID, mineturismo$payload, SetBreakVanillaBlocksPayload::write);
    }

    private void sendMobDamage(boolean enabled) {
        CarEntity.setMobDamageEnabled(enabled);
        SetMobDamagePayload mineturismo$payload = new SetMobDamagePayload(enabled);
        ModClientNetworking.sendToServer(SetMobDamagePayload.ID, mineturismo$payload, SetMobDamagePayload::write);
    }

    private Component speedDisplayLabel() {
        String stateKey = switch (AiObjectModClient.getSpeedDisplayMode()) {
            case NONE -> "gui.mineturismo.other_menu.speed_display_none";
            case KMH -> "gui.mineturismo.other_menu.speed_display_kmh";
            case MPH -> "gui.mineturismo.other_menu.speed_display_mph";
        };
        return ModText.translatable("gui.mineturismo.other_menu.speed_display", ModText.translatable(stateKey));
    }

    @Override
    protected void init() {
        int buttonWidth = 200;
        int x = this.width / 2 - buttonWidth / 2;
        int y = this.height / 2 - 60;
        this.contentTop = y;

        this.addButton(ModCheckbox.builder(ModText.translatable("gui.mineturismo.debug_wrench_menu.disable_hitbox"), this.font)
                .pos(x, y)
                .selected(CarEntity.isHitboxDisabled())
                .onValueChange((checkbox, selected) -> this.sendHitboxDisabled(selected))
                .build());

        this.addButton(ModCheckbox.builder(ModText.translatable("gui.mineturismo.debug_wrench_menu.break_vanilla_blocks"), this.font)
                .pos(x, y + 24)
                .selected(CarEntity.isBreakVanillaBlocksEnabled())
                .onValueChange((checkbox, selected) -> this.sendBreakVanillaBlocks(selected))
                .build());

        this.addButton(ModCheckbox.builder(ModText.translatable("gui.mineturismo.debug_wrench_menu.mob_damage"), this.font)
                .pos(x, y + 48)
                .selected(CarEntity.isMobDamageEnabled())
                .onValueChange((checkbox, selected) -> this.sendMobDamage(selected))
                .build());

        this.addButton(ModCheckbox.builder(ModText.translatable("gui.mineturismo.debug_wrench_menu.custom_font"), this.font)
                .pos(x, y + 72)
                .selected(AiObjectModClient.isMountToastCustomFontEnabled())
                .onValueChange((checkbox, selected) -> AiObjectModClient.setMountToastCustomFontEnabled(selected))
                .build());

        this.addButton(ModCheckbox.builder(ModText.translatable("gui.mineturismo.debug_wrench_menu.fake_reflection"), this.font)
                .pos(x, y + 96)
                .selected(AiObjectModClient.isFakeReflectionEnabled())
                .onValueChange((checkbox, selected) -> AiObjectModClient.setFakeReflectionEnabled(selected))
                .build());

        this.addButton(ModCheckbox.builder(ModText.translatable("gui.mineturismo.debug_wrench_menu.hide_hints"), this.font)
                .pos(x, y + 120)
                .selected(!AiObjectModClient.isHintsEnabled())
                .onValueChange((checkbox, selected) -> AiObjectModClient.setHintsEnabled(!selected))
                .build());

        this.addButton(ModCheckbox.builder(ModText.translatable("gui.mineturismo.debug_wrench_menu.fps_log"), this.font)
                .pos(x, y + 144)
                .selected(AiObjectModClient.isFpsLogEnabled())
                .onValueChange((checkbox, selected) -> AiObjectModClient.setFpsLogEnabled(selected))
                .build());

        int speedRowY = y + 168;
        this.addButton(ModWidgets.button(this.speedDisplayLabel(), x, speedRowY, buttonWidth, 20, button -> {
            AiObjectModClient.setSpeedDisplayMode(AiObjectModClient.getSpeedDisplayMode().next());
            com.examplebett.compat.LegacyGui.setMessage(button, this.speedDisplayLabel());
        }));

        Component backLabel = ModText.translatable("gui.mineturismo.back");
        int backWidth = Math.max(60, com.examplebett.compat.LegacyGui.width(this.font, backLabel) + 16);
        this.addButton(ModWidgets.button(backLabel, x + buttonWidth / 2 - backWidth / 2, speedRowY + 28, backWidth, 20, button ->
                this.minecraft.setScreen(this.parent)));
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTick) {
        mineturismo$renderScreen(new GuiGraphics(null), mouseX, mouseY, partialTick);
    }

    private void mineturismo$renderScreen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        super.render(mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, this.contentTop - 16, 0xFFFFFFFF);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
*/
//?}
