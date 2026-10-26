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
import com.examplebett.entity.TireSmokeParticles;
import com.examplebett.network.SetCarTireSmokeOffsetPayload;
import com.examplebett.network.SetCarTireSmokeParticlePayload;
import com.examplebett.network.SetCarTireSmokePreviewPayload;

import com.examplebett.compat.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class CarTireSmokeEditScreen extends Screen implements CarCustomizationScreen {

    private static final float POSITION_STEP = 0.05f;

    private final CarEntity car;
    private final Screen parent;

    public CarTireSmokeEditScreen(CarEntity car, Screen parent) {
        super(ModText.translatable("gui.mineturismo.tire_smoke_edit.title"));
        this.car = car;
        this.parent = parent;
    }

    private void applyOffset(Vec3 offset) {
        this.car.setTireSmokeOffset((float) offset.x, (float) offset.y, (float) offset.z);
        SetCarTireSmokeOffsetPayload mineturismo$payload = new SetCarTireSmokeOffsetPayload(
                this.car.getId(), (float) offset.x, (float) offset.y, (float) offset.z);
        ModClientNetworking.sendToServer(SetCarTireSmokeOffsetPayload.ID, mineturismo$payload, SetCarTireSmokeOffsetPayload::write);
        this.minecraft.setScreen(new CarTireSmokeEditScreen(this.car, this.parent));
    }

    private void applyParticle(String particleId) {
        this.car.setTireSmokeParticle(particleId);
        SetCarTireSmokeParticlePayload mineturismo$payload = new SetCarTireSmokeParticlePayload(this.car.getId(), particleId);
        ModClientNetworking.sendToServer(SetCarTireSmokeParticlePayload.ID, mineturismo$payload, SetCarTireSmokeParticlePayload::write);
        this.minecraft.setScreen(new CarTireSmokeEditScreen(this.car, this.parent));
    }

    private void applyPreview(boolean enabled) {
        this.car.setTireSmokePreviewEnabled(enabled);
        SetCarTireSmokePreviewPayload mineturismo$payload = new SetCarTireSmokePreviewPayload(this.car.getId(), enabled);
        ModClientNetworking.sendToServer(SetCarTireSmokePreviewPayload.ID, mineturismo$payload, SetCarTireSmokePreviewPayload::write);
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int top = this.height / 2 - 150;

        int particleButtonWidth = 100;
        int particleColumns = 3;
        int particleX = centerX - (particleButtonWidth * particleColumns + 8 * (particleColumns - 1)) / 2;
        int particleY = top + 20;
        String currentParticle = this.car.getTireSmokeParticle();
        int column = 0;
        for (TireSmokeParticles.Option option : TireSmokeParticles.OPTIONS) {
            Component label = option.id().equals(currentParticle)
                    ? ModText.literal("> ").append(option.displayName())
                    : option.displayName();
            this.addRenderableWidget(ModWidgets.button(label, particleX + column * (particleButtonWidth + 8), particleY, particleButtonWidth, 20, button -> this.applyParticle(option.id())));
            column++;
            if (column >= particleColumns) {
                column = 0;
                particleY += 24;
            }
        }
        if (column != 0) {
            particleY += 24;
        }

        Vec3 offset = this.car.getTireSmokeOffset();

        int rowHeight = 20;
        int rowY = particleY + 12;
        addAxisRow(rowY, "X", offset.x, POSITION_STEP,
                delta -> applyOffset(new Vec3(offset.x + delta, offset.y, offset.z)));
        addAxisRow(rowY + rowHeight, "Y", offset.y, POSITION_STEP,
                delta -> applyOffset(new Vec3(offset.x, offset.y + delta, offset.z)));
        addAxisRow(rowY + rowHeight * 2, "Z", offset.z, POSITION_STEP,
                delta -> applyOffset(new Vec3(offset.x, offset.y, offset.z + delta)));

        int buttonWidth = 200;
        int previewY = rowY + rowHeight * 3 + 8;

        this.addRenderableWidget(ModCheckbox.builder(ModText.translatable("gui.mineturismo.tire_smoke_edit.always_smoke"), this.font)
                .pos(centerX - buttonWidth / 2, previewY)
                .selected(this.car.isTireSmokePreviewEnabled())
                .onValueChange((checkbox, selected) -> this.applyPreview(selected))
                .build());

        this.addRenderableWidget(ModWidgets.button(ModText.translatable("gui.mineturismo.back"), centerX - buttonWidth / 2, previewY + 24, buttonWidth, 20, button ->
                this.minecraft.setScreen(this.parent)));
    }

    private interface AxisAdjuster {
        void adjust(float delta);
    }

    private void addAxisRow(int y, String axisLabel, double currentValue, float step, AxisAdjuster adjuster) {
        int centerX = this.width / 2;
        this.addRenderableWidget(ModWidgets.button(ModText.literal("-"), centerX - 100, y, 20, 20, button -> adjuster.adjust(-step)));
        this.addRenderableWidget(ModWidgets.button(ModText.literal("+"), centerX + 80, y, 20, 20, button -> adjuster.adjust(step)));

        this.axisLabels.add(new AxisLabel(axisLabel, currentValue, y));
    }

    private final List<AxisLabel> axisLabels = new ArrayList<>();

    private record AxisLabel(String axis, double value, int y) {
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        mineturismo$renderScreen(new GuiGraphics(poseStack), mouseX, mouseY, partialTick);
    }

    private void mineturismo$renderScreen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        super.render(guiGraphics.pose(), mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - 170, 0xFFFFFFFF);
        for (AxisLabel label : this.axisLabels) {
            String text = String.format("%s: %.2f", label.axis(), label.value());
            guiGraphics.drawCenteredString(this.font, text, this.width / 2, label.y() + 6, 0xFFFFFFFF);
        }
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
import com.examplebett.entity.TireSmokeParticles;
import com.examplebett.network.SetCarTireSmokeOffsetPayload;
import com.examplebett.network.SetCarTireSmokeParticlePayload;
import com.examplebett.network.SetCarTireSmokePreviewPayload;

import com.examplebett.compat.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class CarTireSmokeEditScreen extends Screen implements CarCustomizationScreen {

    private static final float POSITION_STEP = 0.05f;

    private final CarEntity car;
    private final Screen parent;

    public CarTireSmokeEditScreen(CarEntity car, Screen parent) {
        super(ModText.translatable("gui.mineturismo.tire_smoke_edit.title"));
        this.car = car;
        this.parent = parent;
    }

    private void applyOffset(Vec3 offset) {
        this.car.setTireSmokeOffset((float) offset.x, (float) offset.y, (float) offset.z);
        SetCarTireSmokeOffsetPayload mineturismo$payload = new SetCarTireSmokeOffsetPayload(
                this.car.getId(), (float) offset.x, (float) offset.y, (float) offset.z);
        ModClientNetworking.sendToServer(SetCarTireSmokeOffsetPayload.ID, mineturismo$payload, SetCarTireSmokeOffsetPayload::write);
        this.minecraft.setScreen(new CarTireSmokeEditScreen(this.car, this.parent));
    }

    private void applyParticle(String particleId) {
        this.car.setTireSmokeParticle(particleId);
        SetCarTireSmokeParticlePayload mineturismo$payload = new SetCarTireSmokeParticlePayload(this.car.getId(), particleId);
        ModClientNetworking.sendToServer(SetCarTireSmokeParticlePayload.ID, mineturismo$payload, SetCarTireSmokeParticlePayload::write);
        this.minecraft.setScreen(new CarTireSmokeEditScreen(this.car, this.parent));
    }

    private void applyPreview(boolean enabled) {
        this.car.setTireSmokePreviewEnabled(enabled);
        SetCarTireSmokePreviewPayload mineturismo$payload = new SetCarTireSmokePreviewPayload(this.car.getId(), enabled);
        ModClientNetworking.sendToServer(SetCarTireSmokePreviewPayload.ID, mineturismo$payload, SetCarTireSmokePreviewPayload::write);
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int top = this.height / 2 - 150;

        int particleButtonWidth = 100;
        int particleColumns = 3;
        int particleX = centerX - (particleButtonWidth * particleColumns + 8 * (particleColumns - 1)) / 2;
        int particleY = top + 20;
        String currentParticle = this.car.getTireSmokeParticle();
        int column = 0;
        for (TireSmokeParticles.Option option : TireSmokeParticles.OPTIONS) {
            Component label = option.id().equals(currentParticle)
                    ? ModText.literal("> ").append(option.displayName())
                    : option.displayName();
            this.addButton(ModWidgets.button(label, particleX + column * (particleButtonWidth + 8), particleY, particleButtonWidth, 20, button -> this.applyParticle(option.id())));
            column++;
            if (column >= particleColumns) {
                column = 0;
                particleY += 24;
            }
        }
        if (column != 0) {
            particleY += 24;
        }

        Vec3 offset = this.car.getTireSmokeOffset();

        int rowHeight = 20;
        int rowY = particleY + 12;
        addAxisRow(rowY, "X", offset.x, POSITION_STEP,
                delta -> applyOffset(new Vec3(offset.x + delta, offset.y, offset.z)));
        addAxisRow(rowY + rowHeight, "Y", offset.y, POSITION_STEP,
                delta -> applyOffset(new Vec3(offset.x, offset.y + delta, offset.z)));
        addAxisRow(rowY + rowHeight * 2, "Z", offset.z, POSITION_STEP,
                delta -> applyOffset(new Vec3(offset.x, offset.y, offset.z + delta)));

        int buttonWidth = 200;
        int previewY = rowY + rowHeight * 3 + 8;

        this.addButton(ModCheckbox.builder(ModText.translatable("gui.mineturismo.tire_smoke_edit.always_smoke"), this.font)
                .pos(centerX - buttonWidth / 2, previewY)
                .selected(this.car.isTireSmokePreviewEnabled())
                .onValueChange((checkbox, selected) -> this.applyPreview(selected))
                .build());

        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.back"), centerX - buttonWidth / 2, previewY + 24, buttonWidth, 20, button ->
                this.minecraft.setScreen(this.parent)));
    }

    private interface AxisAdjuster {
        void adjust(float delta);
    }

    private void addAxisRow(int y, String axisLabel, double currentValue, float step, AxisAdjuster adjuster) {
        int centerX = this.width / 2;
        this.addButton(ModWidgets.button(ModText.literal("-"), centerX - 100, y, 20, 20, button -> adjuster.adjust(-step)));
        this.addButton(ModWidgets.button(ModText.literal("+"), centerX + 80, y, 20, 20, button -> adjuster.adjust(step)));

        this.axisLabels.add(new AxisLabel(axisLabel, currentValue, y));
    }

    private final List<AxisLabel> axisLabels = new ArrayList<>();

    private record AxisLabel(String axis, double value, int y) {
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        mineturismo$renderScreen(new GuiGraphics(poseStack), mouseX, mouseY, partialTick);
    }

    private void mineturismo$renderScreen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        super.render(guiGraphics.pose(), mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - 170, 0xFFFFFFFF);
        for (AxisLabel label : this.axisLabels) {
            String text = String.format("%s: %.2f", label.axis(), label.value());
            guiGraphics.drawCenteredString(this.font, text, this.width / 2, label.y() + 6, 0xFFFFFFFF);
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
import com.examplebett.compat.ModCheckbox;
import com.examplebett.compat.ModWidgets;
import com.examplebett.compat.ModText;

import com.examplebett.compat.ModClientNetworking;
import com.examplebett.entity.CarEntity;
import com.examplebett.entity.TireSmokeParticles;
import com.examplebett.network.SetCarTireSmokeOffsetPayload;
import com.examplebett.network.SetCarTireSmokeParticlePayload;
import com.examplebett.network.SetCarTireSmokePreviewPayload;

import com.examplebett.compat.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class CarTireSmokeEditScreen extends Screen implements CarCustomizationScreen {

    private static final float POSITION_STEP = 0.05f;

    private final CarEntity car;
    private final Screen parent;

    public CarTireSmokeEditScreen(CarEntity car, Screen parent) {
        super(ModText.translatable("gui.mineturismo.tire_smoke_edit.title"));
        this.car = car;
        this.parent = parent;
    }

    private void applyOffset(Vec3 offset) {
        this.car.setTireSmokeOffset((float) offset.x, (float) offset.y, (float) offset.z);
        SetCarTireSmokeOffsetPayload mineturismo$payload = new SetCarTireSmokeOffsetPayload(
                this.car.getId(), (float) offset.x, (float) offset.y, (float) offset.z);
        ModClientNetworking.sendToServer(SetCarTireSmokeOffsetPayload.ID, mineturismo$payload, SetCarTireSmokeOffsetPayload::write);
        this.minecraft.setScreen(new CarTireSmokeEditScreen(this.car, this.parent));
    }

    private void applyParticle(String particleId) {
        this.car.setTireSmokeParticle(particleId);
        SetCarTireSmokeParticlePayload mineturismo$payload = new SetCarTireSmokeParticlePayload(this.car.getId(), particleId);
        ModClientNetworking.sendToServer(SetCarTireSmokeParticlePayload.ID, mineturismo$payload, SetCarTireSmokeParticlePayload::write);
        this.minecraft.setScreen(new CarTireSmokeEditScreen(this.car, this.parent));
    }

    private void applyPreview(boolean enabled) {
        this.car.setTireSmokePreviewEnabled(enabled);
        SetCarTireSmokePreviewPayload mineturismo$payload = new SetCarTireSmokePreviewPayload(this.car.getId(), enabled);
        ModClientNetworking.sendToServer(SetCarTireSmokePreviewPayload.ID, mineturismo$payload, SetCarTireSmokePreviewPayload::write);
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int top = this.height / 2 - 150;

        int particleButtonWidth = 100;
        int particleColumns = 3;
        int particleX = centerX - (particleButtonWidth * particleColumns + 8 * (particleColumns - 1)) / 2;
        int particleY = top + 20;
        String currentParticle = this.car.getTireSmokeParticle();
        int column = 0;
        for (TireSmokeParticles.Option option : TireSmokeParticles.OPTIONS) {
            Component label = option.id().equals(currentParticle)
                    ? ModText.literal("> ").append(option.displayName())
                    : option.displayName();
            this.addButton(ModWidgets.button(label, particleX + column * (particleButtonWidth + 8), particleY, particleButtonWidth, 20, button -> this.applyParticle(option.id())));
            column++;
            if (column >= particleColumns) {
                column = 0;
                particleY += 24;
            }
        }
        if (column != 0) {
            particleY += 24;
        }

        Vec3 offset = this.car.getTireSmokeOffset();

        int rowHeight = 20;
        int rowY = particleY + 12;
        addAxisRow(rowY, "X", offset.x, POSITION_STEP,
                delta -> applyOffset(new Vec3(offset.x + delta, offset.y, offset.z)));
        addAxisRow(rowY + rowHeight, "Y", offset.y, POSITION_STEP,
                delta -> applyOffset(new Vec3(offset.x, offset.y + delta, offset.z)));
        addAxisRow(rowY + rowHeight * 2, "Z", offset.z, POSITION_STEP,
                delta -> applyOffset(new Vec3(offset.x, offset.y, offset.z + delta)));

        int buttonWidth = 200;
        int previewY = rowY + rowHeight * 3 + 8;

        this.addButton(ModCheckbox.builder(ModText.translatable("gui.mineturismo.tire_smoke_edit.always_smoke"), this.font)
                .pos(centerX - buttonWidth / 2, previewY)
                .selected(this.car.isTireSmokePreviewEnabled())
                .onValueChange((checkbox, selected) -> this.applyPreview(selected))
                .build());

        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.back"), centerX - buttonWidth / 2, previewY + 24, buttonWidth, 20, button ->
                this.minecraft.setScreen(this.parent)));
    }

    private interface AxisAdjuster {
        void adjust(float delta);
    }

    private void addAxisRow(int y, String axisLabel, double currentValue, float step, AxisAdjuster adjuster) {
        int centerX = this.width / 2;
        this.addButton(ModWidgets.button(ModText.literal("-"), centerX - 100, y, 20, 20, button -> adjuster.adjust(-step)));
        this.addButton(ModWidgets.button(ModText.literal("+"), centerX + 80, y, 20, 20, button -> adjuster.adjust(step)));

        this.axisLabels.add(new AxisLabel(axisLabel, currentValue, y));
    }

    private final List<AxisLabel> axisLabels = new ArrayList<>();

    private record AxisLabel(String axis, double value, int y) {
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTick) {
        mineturismo$renderScreen(new GuiGraphics(null), mouseX, mouseY, partialTick);
    }

    private void mineturismo$renderScreen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        super.render(mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - 170, 0xFFFFFFFF);
        for (AxisLabel label : this.axisLabels) {
            String text = String.format("%s: %.2f", label.axis(), label.value());
            guiGraphics.drawCenteredString(this.font, text, this.width / 2, label.y() + 6, 0xFFFFFFFF);
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
import com.examplebett.compat.ModCheckbox;
import com.examplebett.compat.ModWidgets;
import com.examplebett.compat.ModText;

import com.examplebett.compat.ModClientNetworking;
import com.examplebett.entity.CarEntity;
import com.examplebett.entity.TireSmokeParticles;
import com.examplebett.network.SetCarTireSmokeOffsetPayload;
import com.examplebett.network.SetCarTireSmokeParticlePayload;
import com.examplebett.network.SetCarTireSmokePreviewPayload;

import com.examplebett.compat.GuiGraphics;
import com.examplebett.compat.v114.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class CarTireSmokeEditScreen extends Screen implements CarCustomizationScreen {

    private static final float POSITION_STEP = 0.05f;

    private final CarEntity car;
    private final Screen parent;

    public CarTireSmokeEditScreen(CarEntity car, Screen parent) {
        super(ModText.translatable("gui.mineturismo.tire_smoke_edit.title"));
        this.car = car;
        this.parent = parent;
    }

    private void applyOffset(Vec3 offset) {
        this.car.setTireSmokeOffset((float) offset.x, (float) offset.y, (float) offset.z);
        SetCarTireSmokeOffsetPayload mineturismo$payload = new SetCarTireSmokeOffsetPayload(
                this.car.getId(), (float) offset.x, (float) offset.y, (float) offset.z);
        ModClientNetworking.sendToServer(SetCarTireSmokeOffsetPayload.ID, mineturismo$payload, SetCarTireSmokeOffsetPayload::write);
        this.minecraft.setScreen(new CarTireSmokeEditScreen(this.car, this.parent));
    }

    private void applyParticle(String particleId) {
        this.car.setTireSmokeParticle(particleId);
        SetCarTireSmokeParticlePayload mineturismo$payload = new SetCarTireSmokeParticlePayload(this.car.getId(), particleId);
        ModClientNetworking.sendToServer(SetCarTireSmokeParticlePayload.ID, mineturismo$payload, SetCarTireSmokeParticlePayload::write);
        this.minecraft.setScreen(new CarTireSmokeEditScreen(this.car, this.parent));
    }

    private void applyPreview(boolean enabled) {
        this.car.setTireSmokePreviewEnabled(enabled);
        SetCarTireSmokePreviewPayload mineturismo$payload = new SetCarTireSmokePreviewPayload(this.car.getId(), enabled);
        ModClientNetworking.sendToServer(SetCarTireSmokePreviewPayload.ID, mineturismo$payload, SetCarTireSmokePreviewPayload::write);
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int top = this.height / 2 - 150;

        int particleButtonWidth = 100;
        int particleColumns = 3;
        int particleX = centerX - (particleButtonWidth * particleColumns + 8 * (particleColumns - 1)) / 2;
        int particleY = top + 20;
        String currentParticle = this.car.getTireSmokeParticle();
        int column = 0;
        for (TireSmokeParticles.Option option : TireSmokeParticles.OPTIONS) {
            Component label = option.id().equals(currentParticle)
                    ? ModText.literal("> ").append(option.displayName())
                    : option.displayName();
            this.addButton(ModWidgets.button(label, particleX + column * (particleButtonWidth + 8), particleY, particleButtonWidth, 20, button -> this.applyParticle(option.id())));
            column++;
            if (column >= particleColumns) {
                column = 0;
                particleY += 24;
            }
        }
        if (column != 0) {
            particleY += 24;
        }

        Vec3 offset = this.car.getTireSmokeOffset();

        int rowHeight = 20;
        int rowY = particleY + 12;
        addAxisRow(rowY, "X", offset.x, POSITION_STEP,
                delta -> applyOffset(new Vec3(offset.x + delta, offset.y, offset.z)));
        addAxisRow(rowY + rowHeight, "Y", offset.y, POSITION_STEP,
                delta -> applyOffset(new Vec3(offset.x, offset.y + delta, offset.z)));
        addAxisRow(rowY + rowHeight * 2, "Z", offset.z, POSITION_STEP,
                delta -> applyOffset(new Vec3(offset.x, offset.y, offset.z + delta)));

        int buttonWidth = 200;
        int previewY = rowY + rowHeight * 3 + 8;

        this.addButton(ModCheckbox.builder(ModText.translatable("gui.mineturismo.tire_smoke_edit.always_smoke"), this.font)
                .pos(centerX - buttonWidth / 2, previewY)
                .selected(this.car.isTireSmokePreviewEnabled())
                .onValueChange((checkbox, selected) -> this.applyPreview(selected))
                .build());

        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.back"), centerX - buttonWidth / 2, previewY + 24, buttonWidth, 20, button ->
                this.minecraft.setScreen(this.parent)));
    }

    private interface AxisAdjuster {
        void adjust(float delta);
    }

    private void addAxisRow(int y, String axisLabel, double currentValue, float step, AxisAdjuster adjuster) {
        int centerX = this.width / 2;
        this.addButton(ModWidgets.button(ModText.literal("-"), centerX - 100, y, 20, 20, button -> adjuster.adjust(-step)));
        this.addButton(ModWidgets.button(ModText.literal("+"), centerX + 80, y, 20, 20, button -> adjuster.adjust(step)));

        this.axisLabels.add(new AxisLabel(axisLabel, currentValue, y));
    }

    private final List<AxisLabel> axisLabels = new ArrayList<>();

    private record AxisLabel(String axis, double value, int y) {
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTick) {
        mineturismo$renderScreen(new GuiGraphics(null), mouseX, mouseY, partialTick);
    }

    private void mineturismo$renderScreen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        super.render(mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - 170, 0xFFFFFFFF);
        for (AxisLabel label : this.axisLabels) {
            String text = String.format("%s: %.2f", label.axis(), label.value());
            guiGraphics.drawCenteredString(this.font, text, this.width / 2, label.y() + 6, 0xFFFFFFFF);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
*/
//?}
