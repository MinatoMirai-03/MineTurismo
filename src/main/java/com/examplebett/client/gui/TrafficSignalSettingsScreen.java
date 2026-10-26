//? if >=1.18 {
//?}
//? if <1.18 && >=1.17 {
/*
package com.examplebett.client.gui;
import com.examplebett.compat.ModWidgets;
import com.examplebett.compat.ModText;

import com.examplebett.compat.ModClientNetworking;
import com.examplebett.network.SetSignalDirectionPayload;
import com.examplebett.network.SetSignalNamePayload;
import com.examplebett.network.SetSignalTimingPayload;
import com.examplebett.waypoint.SignalGroupConfig;

import com.examplebett.compat.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class TrafficSignalSettingsScreen extends Screen {

    private static final int FIELD_WIDTH = 200;
    private static final char[] DIRECTIONS = {'N', 'E', 'S', 'W'};

    private static String cachedName = "";
    private static char cachedDirection = 'N';
    private static int cachedGreenTicks = SignalGroupConfig.DEFAULT_GREEN_TICKS;
    private static int cachedRedTicks = SignalGroupConfig.DEFAULT_RED_TICKS;
    private static int cachedPhaseOffsetTicks = 0;

    private static int editingSignalId = -1;

    private EditBox nameBox;
    private Button directionButton;
    private int greenValueY;
    private int redValueY;
    private int offsetValueY;

    public TrafficSignalSettingsScreen() {
        super(editingSignalId >= 0
                ? ModText.translatable("gui.mineturismo.traffic_signal.title_editing", editingSignalId)
                : ModText.translatable("gui.mineturismo.traffic_signal.title_new"));
    }

    public static void applyExternalConfig(int signalId, String intersectionName, char direction, int greenTicks, int redTicks, int phaseOffsetTicks) {
        editingSignalId = signalId;
        cachedName = intersectionName;
        cachedDirection = direction;
        cachedGreenTicks = greenTicks;
        cachedRedTicks = redTicks;
        cachedPhaseOffsetTicks = phaseOffsetTicks;
    }

    private void sendTiming() {
        SetSignalTimingPayload mineturismo$payload = new SetSignalTimingPayload(cachedGreenTicks, cachedRedTicks, cachedPhaseOffsetTicks);
        ModClientNetworking.sendToServer(SetSignalTimingPayload.ID, mineturismo$payload, SetSignalTimingPayload::write);
    }

    private void adjustGreen(int delta) {
        cachedGreenTicks = Mth.clamp(cachedGreenTicks + delta, SignalGroupConfig.MIN_PHASE_TICKS, SignalGroupConfig.MAX_PHASE_TICKS);
        this.sendTiming();
    }

    private void adjustRed(int delta) {
        cachedRedTicks = Mth.clamp(cachedRedTicks + delta, SignalGroupConfig.MIN_PHASE_TICKS, SignalGroupConfig.MAX_PHASE_TICKS);
        this.sendTiming();
    }

    private void adjustOffset(int delta) {
        cachedPhaseOffsetTicks = Mth.clamp(cachedPhaseOffsetTicks + delta, -SignalGroupConfig.MAX_PHASE_TICKS, SignalGroupConfig.MAX_PHASE_TICKS);
        this.sendTiming();
    }

    private void cycleDirection() {
        int index = 0;
        for (int i = 0; i < DIRECTIONS.length; i++) {
            if (DIRECTIONS[i] == cachedDirection) {
                index = i;
                break;
            }
        }
        cachedDirection = DIRECTIONS[(index + 1) % DIRECTIONS.length];
        this.directionButton.setMessage(ModText.literal(String.valueOf(cachedDirection)));

        cachedPhaseOffsetTicks = (cachedDirection == 'E' || cachedDirection == 'W')
                ? (cachedGreenTicks + cachedRedTicks) / 2
                : 0;
        SetSignalDirectionPayload mineturismo$payload = new SetSignalDirectionPayload(String.valueOf(cachedDirection));
        ModClientNetworking.sendToServer(SetSignalDirectionPayload.ID, mineturismo$payload, SetSignalDirectionPayload::write);
        this.sendTiming();
    }

    @Override
    protected void init() {
        int x = this.width / 2 - FIELD_WIDTH / 2;
        int top = this.height / 2 - 110;

        this.nameBox = new EditBox(this.font, x, top, FIELD_WIDTH, 20,
                ModText.translatable("gui.mineturismo.traffic_signal.name_hint"));
        this.nameBox.setMaxLength(32);
        this.nameBox.setValue(cachedName);
        ModWidgets.setHint(this.nameBox, ModText.translatable("gui.mineturismo.traffic_signal.name_hint"));
        this.nameBox.setResponder(value -> {
            cachedName = value;
            SetSignalNamePayload mineturismo$payload = new SetSignalNamePayload(value);
            ModClientNetworking.sendToServer(SetSignalNamePayload.ID, mineturismo$payload, SetSignalNamePayload::write);
        });
        this.addRenderableWidget(this.nameBox);
        this.setInitialFocus(this.nameBox);

        int directionY = top + 30;
        this.directionButton = ModWidgets.button(ModText.literal(String.valueOf(cachedDirection)), x, directionY, FIELD_WIDTH, 20, button -> this.cycleDirection());
        this.addRenderableWidget(this.directionButton);

        int greenY = directionY + 40;
        int redY = greenY + 44;
        int offsetY = redY + 44;
        this.greenValueY = greenY + 22;
        this.redValueY = redY + 22;
        this.offsetValueY = offsetY + 22;

        this.addRenderableWidget(ModWidgets.button(ModText.literal("-20"), x, greenY, 40, 20, button -> adjustGreen(-20)));
        this.addRenderableWidget(ModWidgets.button(ModText.literal("-100"), x + 44, greenY, 40, 20, button -> adjustGreen(-100)));
        this.addRenderableWidget(ModWidgets.button(ModText.literal("+100"), x + FIELD_WIDTH - 84, greenY, 40, 20, button -> adjustGreen(100)));
        this.addRenderableWidget(ModWidgets.button(ModText.literal("+20"), x + FIELD_WIDTH - 40, greenY, 40, 20, button -> adjustGreen(20)));

        this.addRenderableWidget(ModWidgets.button(ModText.literal("-20"), x, redY, 40, 20, button -> adjustRed(-20)));
        this.addRenderableWidget(ModWidgets.button(ModText.literal("-100"), x + 44, redY, 40, 20, button -> adjustRed(-100)));
        this.addRenderableWidget(ModWidgets.button(ModText.literal("+100"), x + FIELD_WIDTH - 84, redY, 40, 20, button -> adjustRed(100)));
        this.addRenderableWidget(ModWidgets.button(ModText.literal("+20"), x + FIELD_WIDTH - 40, redY, 40, 20, button -> adjustRed(20)));

        this.addRenderableWidget(ModWidgets.button(ModText.literal("-20"), x, offsetY, 40, 20, button -> adjustOffset(-20)));
        this.addRenderableWidget(ModWidgets.button(ModText.literal("-100"), x + 44, offsetY, 40, 20, button -> adjustOffset(-100)));
        this.addRenderableWidget(ModWidgets.button(ModText.literal("+100"), x + FIELD_WIDTH - 84, offsetY, 40, 20, button -> adjustOffset(100)));
        this.addRenderableWidget(ModWidgets.button(ModText.literal("+20"), x + FIELD_WIDTH - 40, offsetY, 40, 20, button -> adjustOffset(20)));

        this.addRenderableWidget(ModWidgets.button(ModText.translatable("gui.mineturismo.back"), x, offsetY + 40, FIELD_WIDTH, 20, button ->
                this.minecraft.setScreen(null)));
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        mineturismo$renderScreen(new GuiGraphics(poseStack), mouseX, mouseY, partialTick);
    }

    private void mineturismo$renderScreen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        super.render(guiGraphics.pose(), mouseX, mouseY, partialTick);
        int centerX = this.width / 2;
        int top = this.height / 2 - 110;

        guiGraphics.drawCenteredString(this.font, this.title, centerX, top - 30, 0xFFFFFFFF);

        String groupKey = cachedName.isEmpty() ? "?" : cachedName + "_" + cachedDirection;
        guiGraphics.drawCenteredString(this.font,
                ModText.translatable("gui.mineturismo.traffic_signal.next_group", groupKey),
                centerX, top + 24, 0xFFFFFF55);
        guiGraphics.drawCenteredString(this.font,
                ModText.translatable("gui.mineturismo.traffic_signal.group_desc"),
                centerX, top + 44, 0xFFAAAAAA);

        guiGraphics.drawCenteredString(this.font,
                ModText.translatable("gui.mineturismo.traffic_signal.green_title"),
                centerX, this.greenValueY - 26, 0xFFFFFFFF);
        guiGraphics.drawCenteredString(this.font, String.format("%.1fs (%d ticks)", cachedGreenTicks / 20.0, cachedGreenTicks),
                centerX, this.greenValueY, 0xFF55FF55);

        guiGraphics.drawCenteredString(this.font,
                ModText.translatable("gui.mineturismo.traffic_signal.red_title"),
                centerX, this.redValueY - 26, 0xFFFFFFFF);
        guiGraphics.drawCenteredString(this.font, String.format("%.1fs (%d ticks)", cachedRedTicks / 20.0, cachedRedTicks),
                centerX, this.redValueY, 0xFFFF5555);

        guiGraphics.drawCenteredString(this.font,
                ModText.translatable("gui.mineturismo.traffic_signal.offset_title"),
                centerX, this.offsetValueY - 26, 0xFFFFFFFF);
        guiGraphics.drawCenteredString(this.font, String.format("%.1fs (%d ticks)", cachedPhaseOffsetTicks / 20.0, cachedPhaseOffsetTicks),
                centerX, this.offsetValueY, 0xFFFFFFFF);
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
import com.examplebett.network.SetSignalDirectionPayload;
import com.examplebett.network.SetSignalNamePayload;
import com.examplebett.network.SetSignalTimingPayload;
import com.examplebett.waypoint.SignalGroupConfig;

import com.examplebett.compat.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class TrafficSignalSettingsScreen extends Screen {

    private static final int FIELD_WIDTH = 200;
    private static final char[] DIRECTIONS = {'N', 'E', 'S', 'W'};

    private static String cachedName = "";
    private static char cachedDirection = 'N';
    private static int cachedGreenTicks = SignalGroupConfig.DEFAULT_GREEN_TICKS;
    private static int cachedRedTicks = SignalGroupConfig.DEFAULT_RED_TICKS;
    private static int cachedPhaseOffsetTicks = 0;

    private static int editingSignalId = -1;

    private EditBox nameBox;
    private Button directionButton;
    private int greenValueY;
    private int redValueY;
    private int offsetValueY;

    public TrafficSignalSettingsScreen() {
        super(editingSignalId >= 0
                ? ModText.translatable("gui.mineturismo.traffic_signal.title_editing", editingSignalId)
                : ModText.translatable("gui.mineturismo.traffic_signal.title_new"));
    }

    public static void applyExternalConfig(int signalId, String intersectionName, char direction, int greenTicks, int redTicks, int phaseOffsetTicks) {
        editingSignalId = signalId;
        cachedName = intersectionName;
        cachedDirection = direction;
        cachedGreenTicks = greenTicks;
        cachedRedTicks = redTicks;
        cachedPhaseOffsetTicks = phaseOffsetTicks;
    }

    private void sendTiming() {
        SetSignalTimingPayload mineturismo$payload = new SetSignalTimingPayload(cachedGreenTicks, cachedRedTicks, cachedPhaseOffsetTicks);
        ModClientNetworking.sendToServer(SetSignalTimingPayload.ID, mineturismo$payload, SetSignalTimingPayload::write);
    }

    private void adjustGreen(int delta) {
        cachedGreenTicks = Mth.clamp(cachedGreenTicks + delta, SignalGroupConfig.MIN_PHASE_TICKS, SignalGroupConfig.MAX_PHASE_TICKS);
        this.sendTiming();
    }

    private void adjustRed(int delta) {
        cachedRedTicks = Mth.clamp(cachedRedTicks + delta, SignalGroupConfig.MIN_PHASE_TICKS, SignalGroupConfig.MAX_PHASE_TICKS);
        this.sendTiming();
    }

    private void adjustOffset(int delta) {
        cachedPhaseOffsetTicks = Mth.clamp(cachedPhaseOffsetTicks + delta, -SignalGroupConfig.MAX_PHASE_TICKS, SignalGroupConfig.MAX_PHASE_TICKS);
        this.sendTiming();
    }

    private void cycleDirection() {
        int index = 0;
        for (int i = 0; i < DIRECTIONS.length; i++) {
            if (DIRECTIONS[i] == cachedDirection) {
                index = i;
                break;
            }
        }
        cachedDirection = DIRECTIONS[(index + 1) % DIRECTIONS.length];
        this.directionButton.setMessage(ModText.literal(String.valueOf(cachedDirection)));

        cachedPhaseOffsetTicks = (cachedDirection == 'E' || cachedDirection == 'W')
                ? (cachedGreenTicks + cachedRedTicks) / 2
                : 0;
        SetSignalDirectionPayload mineturismo$payload = new SetSignalDirectionPayload(String.valueOf(cachedDirection));
        ModClientNetworking.sendToServer(SetSignalDirectionPayload.ID, mineturismo$payload, SetSignalDirectionPayload::write);
        this.sendTiming();
    }

    @Override
    protected void init() {
        int x = this.width / 2 - FIELD_WIDTH / 2;
        int top = this.height / 2 - 110;

        this.nameBox = new EditBox(this.font, x, top, FIELD_WIDTH, 20,
                ModText.translatable("gui.mineturismo.traffic_signal.name_hint"));
        this.nameBox.setMaxLength(32);
        this.nameBox.setValue(cachedName);
        ModWidgets.setHint(this.nameBox, ModText.translatable("gui.mineturismo.traffic_signal.name_hint"));
        this.nameBox.setResponder(value -> {
            cachedName = value;
            SetSignalNamePayload mineturismo$payload = new SetSignalNamePayload(value);
            ModClientNetworking.sendToServer(SetSignalNamePayload.ID, mineturismo$payload, SetSignalNamePayload::write);
        });
        this.addButton(this.nameBox);
        this.setInitialFocus(this.nameBox);

        int directionY = top + 30;
        this.directionButton = ModWidgets.button(ModText.literal(String.valueOf(cachedDirection)), x, directionY, FIELD_WIDTH, 20, button -> this.cycleDirection());
        this.addButton(this.directionButton);

        int greenY = directionY + 40;
        int redY = greenY + 44;
        int offsetY = redY + 44;
        this.greenValueY = greenY + 22;
        this.redValueY = redY + 22;
        this.offsetValueY = offsetY + 22;

        this.addButton(ModWidgets.button(ModText.literal("-20"), x, greenY, 40, 20, button -> adjustGreen(-20)));
        this.addButton(ModWidgets.button(ModText.literal("-100"), x + 44, greenY, 40, 20, button -> adjustGreen(-100)));
        this.addButton(ModWidgets.button(ModText.literal("+100"), x + FIELD_WIDTH - 84, greenY, 40, 20, button -> adjustGreen(100)));
        this.addButton(ModWidgets.button(ModText.literal("+20"), x + FIELD_WIDTH - 40, greenY, 40, 20, button -> adjustGreen(20)));

        this.addButton(ModWidgets.button(ModText.literal("-20"), x, redY, 40, 20, button -> adjustRed(-20)));
        this.addButton(ModWidgets.button(ModText.literal("-100"), x + 44, redY, 40, 20, button -> adjustRed(-100)));
        this.addButton(ModWidgets.button(ModText.literal("+100"), x + FIELD_WIDTH - 84, redY, 40, 20, button -> adjustRed(100)));
        this.addButton(ModWidgets.button(ModText.literal("+20"), x + FIELD_WIDTH - 40, redY, 40, 20, button -> adjustRed(20)));

        this.addButton(ModWidgets.button(ModText.literal("-20"), x, offsetY, 40, 20, button -> adjustOffset(-20)));
        this.addButton(ModWidgets.button(ModText.literal("-100"), x + 44, offsetY, 40, 20, button -> adjustOffset(-100)));
        this.addButton(ModWidgets.button(ModText.literal("+100"), x + FIELD_WIDTH - 84, offsetY, 40, 20, button -> adjustOffset(100)));
        this.addButton(ModWidgets.button(ModText.literal("+20"), x + FIELD_WIDTH - 40, offsetY, 40, 20, button -> adjustOffset(20)));

        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.back"), x, offsetY + 40, FIELD_WIDTH, 20, button ->
                this.minecraft.setScreen(null)));
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        mineturismo$renderScreen(new GuiGraphics(poseStack), mouseX, mouseY, partialTick);
    }

    private void mineturismo$renderScreen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        super.render(guiGraphics.pose(), mouseX, mouseY, partialTick);
        int centerX = this.width / 2;
        int top = this.height / 2 - 110;

        guiGraphics.drawCenteredString(this.font, this.title, centerX, top - 30, 0xFFFFFFFF);

        String groupKey = cachedName.isEmpty() ? "?" : cachedName + "_" + cachedDirection;
        guiGraphics.drawCenteredString(this.font,
                ModText.translatable("gui.mineturismo.traffic_signal.next_group", groupKey),
                centerX, top + 24, 0xFFFFFF55);
        guiGraphics.drawCenteredString(this.font,
                ModText.translatable("gui.mineturismo.traffic_signal.group_desc"),
                centerX, top + 44, 0xFFAAAAAA);

        guiGraphics.drawCenteredString(this.font,
                ModText.translatable("gui.mineturismo.traffic_signal.green_title"),
                centerX, this.greenValueY - 26, 0xFFFFFFFF);
        guiGraphics.drawCenteredString(this.font, String.format("%.1fs (%d ticks)", cachedGreenTicks / 20.0, cachedGreenTicks),
                centerX, this.greenValueY, 0xFF55FF55);

        guiGraphics.drawCenteredString(this.font,
                ModText.translatable("gui.mineturismo.traffic_signal.red_title"),
                centerX, this.redValueY - 26, 0xFFFFFFFF);
        guiGraphics.drawCenteredString(this.font, String.format("%.1fs (%d ticks)", cachedRedTicks / 20.0, cachedRedTicks),
                centerX, this.redValueY, 0xFFFF5555);

        guiGraphics.drawCenteredString(this.font,
                ModText.translatable("gui.mineturismo.traffic_signal.offset_title"),
                centerX, this.offsetValueY - 26, 0xFFFFFFFF);
        guiGraphics.drawCenteredString(this.font, String.format("%.1fs (%d ticks)", cachedPhaseOffsetTicks / 20.0, cachedPhaseOffsetTicks),
                centerX, this.offsetValueY, 0xFFFFFFFF);
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
import com.examplebett.network.SetSignalDirectionPayload;
import com.examplebett.network.SetSignalNamePayload;
import com.examplebett.network.SetSignalTimingPayload;
import com.examplebett.waypoint.SignalGroupConfig;

import com.examplebett.compat.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class TrafficSignalSettingsScreen extends Screen {

    private static final int FIELD_WIDTH = 200;
    private static final char[] DIRECTIONS = {'N', 'E', 'S', 'W'};

    private static String cachedName = "";
    private static char cachedDirection = 'N';
    private static int cachedGreenTicks = SignalGroupConfig.DEFAULT_GREEN_TICKS;
    private static int cachedRedTicks = SignalGroupConfig.DEFAULT_RED_TICKS;
    private static int cachedPhaseOffsetTicks = 0;

    private static int editingSignalId = -1;

    private EditBox nameBox;
    private Button directionButton;
    private int greenValueY;
    private int redValueY;
    private int offsetValueY;

    public TrafficSignalSettingsScreen() {
        super(editingSignalId >= 0
                ? ModText.translatable("gui.mineturismo.traffic_signal.title_editing", editingSignalId)
                : ModText.translatable("gui.mineturismo.traffic_signal.title_new"));
    }

    public static void applyExternalConfig(int signalId, String intersectionName, char direction, int greenTicks, int redTicks, int phaseOffsetTicks) {
        editingSignalId = signalId;
        cachedName = intersectionName;
        cachedDirection = direction;
        cachedGreenTicks = greenTicks;
        cachedRedTicks = redTicks;
        cachedPhaseOffsetTicks = phaseOffsetTicks;
    }

    private void sendTiming() {
        SetSignalTimingPayload mineturismo$payload = new SetSignalTimingPayload(cachedGreenTicks, cachedRedTicks, cachedPhaseOffsetTicks);
        ModClientNetworking.sendToServer(SetSignalTimingPayload.ID, mineturismo$payload, SetSignalTimingPayload::write);
    }

    private void adjustGreen(int delta) {
        cachedGreenTicks = Mth.clamp(cachedGreenTicks + delta, SignalGroupConfig.MIN_PHASE_TICKS, SignalGroupConfig.MAX_PHASE_TICKS);
        this.sendTiming();
    }

    private void adjustRed(int delta) {
        cachedRedTicks = Mth.clamp(cachedRedTicks + delta, SignalGroupConfig.MIN_PHASE_TICKS, SignalGroupConfig.MAX_PHASE_TICKS);
        this.sendTiming();
    }

    private void adjustOffset(int delta) {
        cachedPhaseOffsetTicks = Mth.clamp(cachedPhaseOffsetTicks + delta, -SignalGroupConfig.MAX_PHASE_TICKS, SignalGroupConfig.MAX_PHASE_TICKS);
        this.sendTiming();
    }

    private void cycleDirection() {
        int index = 0;
        for (int i = 0; i < DIRECTIONS.length; i++) {
            if (DIRECTIONS[i] == cachedDirection) {
                index = i;
                break;
            }
        }
        cachedDirection = DIRECTIONS[(index + 1) % DIRECTIONS.length];
        com.examplebett.compat.LegacyGui.setMessage(this.directionButton, ModText.literal(String.valueOf(cachedDirection)));

        cachedPhaseOffsetTicks = (cachedDirection == 'E' || cachedDirection == 'W')
                ? (cachedGreenTicks + cachedRedTicks) / 2
                : 0;
        SetSignalDirectionPayload mineturismo$payload = new SetSignalDirectionPayload(String.valueOf(cachedDirection));
        ModClientNetworking.sendToServer(SetSignalDirectionPayload.ID, mineturismo$payload, SetSignalDirectionPayload::write);
        this.sendTiming();
    }

    @Override
    protected void init() {
        int x = this.width / 2 - FIELD_WIDTH / 2;
        int top = this.height / 2 - 110;

        this.nameBox = new EditBox(this.font, x, top, FIELD_WIDTH, 20,
                com.examplebett.compat.LegacyGui.text(ModText.translatable("gui.mineturismo.traffic_signal.name_hint")));
        this.nameBox.setMaxLength(32);
        this.nameBox.setValue(cachedName);
        ModWidgets.setHint(this.nameBox, ModText.translatable("gui.mineturismo.traffic_signal.name_hint"));
        this.nameBox.setResponder(value -> {
            cachedName = value;
            SetSignalNamePayload mineturismo$payload = new SetSignalNamePayload(value);
            ModClientNetworking.sendToServer(SetSignalNamePayload.ID, mineturismo$payload, SetSignalNamePayload::write);
        });
        this.addButton(this.nameBox);
        this.setInitialFocus(this.nameBox);

        int directionY = top + 30;
        this.directionButton = ModWidgets.button(ModText.literal(String.valueOf(cachedDirection)), x, directionY, FIELD_WIDTH, 20, button -> this.cycleDirection());
        this.addButton(this.directionButton);

        int greenY = directionY + 40;
        int redY = greenY + 44;
        int offsetY = redY + 44;
        this.greenValueY = greenY + 22;
        this.redValueY = redY + 22;
        this.offsetValueY = offsetY + 22;

        this.addButton(ModWidgets.button(ModText.literal("-20"), x, greenY, 40, 20, button -> adjustGreen(-20)));
        this.addButton(ModWidgets.button(ModText.literal("-100"), x + 44, greenY, 40, 20, button -> adjustGreen(-100)));
        this.addButton(ModWidgets.button(ModText.literal("+100"), x + FIELD_WIDTH - 84, greenY, 40, 20, button -> adjustGreen(100)));
        this.addButton(ModWidgets.button(ModText.literal("+20"), x + FIELD_WIDTH - 40, greenY, 40, 20, button -> adjustGreen(20)));

        this.addButton(ModWidgets.button(ModText.literal("-20"), x, redY, 40, 20, button -> adjustRed(-20)));
        this.addButton(ModWidgets.button(ModText.literal("-100"), x + 44, redY, 40, 20, button -> adjustRed(-100)));
        this.addButton(ModWidgets.button(ModText.literal("+100"), x + FIELD_WIDTH - 84, redY, 40, 20, button -> adjustRed(100)));
        this.addButton(ModWidgets.button(ModText.literal("+20"), x + FIELD_WIDTH - 40, redY, 40, 20, button -> adjustRed(20)));

        this.addButton(ModWidgets.button(ModText.literal("-20"), x, offsetY, 40, 20, button -> adjustOffset(-20)));
        this.addButton(ModWidgets.button(ModText.literal("-100"), x + 44, offsetY, 40, 20, button -> adjustOffset(-100)));
        this.addButton(ModWidgets.button(ModText.literal("+100"), x + FIELD_WIDTH - 84, offsetY, 40, 20, button -> adjustOffset(100)));
        this.addButton(ModWidgets.button(ModText.literal("+20"), x + FIELD_WIDTH - 40, offsetY, 40, 20, button -> adjustOffset(20)));

        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.back"), x, offsetY + 40, FIELD_WIDTH, 20, button ->
                this.minecraft.setScreen(null)));
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTick) {
        mineturismo$renderScreen(new GuiGraphics(null), mouseX, mouseY, partialTick);
    }

    private void mineturismo$renderScreen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        super.render(mouseX, mouseY, partialTick);
        int centerX = this.width / 2;
        int top = this.height / 2 - 110;

        guiGraphics.drawCenteredString(this.font, this.title, centerX, top - 30, 0xFFFFFFFF);

        String groupKey = cachedName.isEmpty() ? "?" : cachedName + "_" + cachedDirection;
        guiGraphics.drawCenteredString(this.font,
                ModText.translatable("gui.mineturismo.traffic_signal.next_group", groupKey),
                centerX, top + 24, 0xFFFFFF55);
        guiGraphics.drawCenteredString(this.font,
                ModText.translatable("gui.mineturismo.traffic_signal.group_desc"),
                centerX, top + 44, 0xFFAAAAAA);

        guiGraphics.drawCenteredString(this.font,
                ModText.translatable("gui.mineturismo.traffic_signal.green_title"),
                centerX, this.greenValueY - 26, 0xFFFFFFFF);
        guiGraphics.drawCenteredString(this.font, String.format("%.1fs (%d ticks)", cachedGreenTicks / 20.0, cachedGreenTicks),
                centerX, this.greenValueY, 0xFF55FF55);

        guiGraphics.drawCenteredString(this.font,
                ModText.translatable("gui.mineturismo.traffic_signal.red_title"),
                centerX, this.redValueY - 26, 0xFFFFFFFF);
        guiGraphics.drawCenteredString(this.font, String.format("%.1fs (%d ticks)", cachedRedTicks / 20.0, cachedRedTicks),
                centerX, this.redValueY, 0xFFFF5555);

        guiGraphics.drawCenteredString(this.font,
                ModText.translatable("gui.mineturismo.traffic_signal.offset_title"),
                centerX, this.offsetValueY - 26, 0xFFFFFFFF);
        guiGraphics.drawCenteredString(this.font, String.format("%.1fs (%d ticks)", cachedPhaseOffsetTicks / 20.0, cachedPhaseOffsetTicks),
                centerX, this.offsetValueY, 0xFFFFFFFF);
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
import com.examplebett.network.SetSignalDirectionPayload;
import com.examplebett.network.SetSignalNamePayload;
import com.examplebett.network.SetSignalTimingPayload;
import com.examplebett.waypoint.SignalGroupConfig;

import com.examplebett.compat.GuiGraphics;
import com.examplebett.compat.v114.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class TrafficSignalSettingsScreen extends Screen {

    private static final int FIELD_WIDTH = 200;
    private static final char[] DIRECTIONS = {'N', 'E', 'S', 'W'};

    private static String cachedName = "";
    private static char cachedDirection = 'N';
    private static int cachedGreenTicks = SignalGroupConfig.DEFAULT_GREEN_TICKS;
    private static int cachedRedTicks = SignalGroupConfig.DEFAULT_RED_TICKS;
    private static int cachedPhaseOffsetTicks = 0;

    private static int editingSignalId = -1;

    private EditBox nameBox;
    private Button directionButton;
    private int greenValueY;
    private int redValueY;
    private int offsetValueY;

    public TrafficSignalSettingsScreen() {
        super(editingSignalId >= 0
                ? ModText.translatable("gui.mineturismo.traffic_signal.title_editing", editingSignalId)
                : ModText.translatable("gui.mineturismo.traffic_signal.title_new"));
    }

    public static void applyExternalConfig(int signalId, String intersectionName, char direction, int greenTicks, int redTicks, int phaseOffsetTicks) {
        editingSignalId = signalId;
        cachedName = intersectionName;
        cachedDirection = direction;
        cachedGreenTicks = greenTicks;
        cachedRedTicks = redTicks;
        cachedPhaseOffsetTicks = phaseOffsetTicks;
    }

    private void sendTiming() {
        SetSignalTimingPayload mineturismo$payload = new SetSignalTimingPayload(cachedGreenTicks, cachedRedTicks, cachedPhaseOffsetTicks);
        ModClientNetworking.sendToServer(SetSignalTimingPayload.ID, mineturismo$payload, SetSignalTimingPayload::write);
    }

    private void adjustGreen(int delta) {
        cachedGreenTicks = Mth.clamp(cachedGreenTicks + delta, SignalGroupConfig.MIN_PHASE_TICKS, SignalGroupConfig.MAX_PHASE_TICKS);
        this.sendTiming();
    }

    private void adjustRed(int delta) {
        cachedRedTicks = Mth.clamp(cachedRedTicks + delta, SignalGroupConfig.MIN_PHASE_TICKS, SignalGroupConfig.MAX_PHASE_TICKS);
        this.sendTiming();
    }

    private void adjustOffset(int delta) {
        cachedPhaseOffsetTicks = Mth.clamp(cachedPhaseOffsetTicks + delta, -SignalGroupConfig.MAX_PHASE_TICKS, SignalGroupConfig.MAX_PHASE_TICKS);
        this.sendTiming();
    }

    private void cycleDirection() {
        int index = 0;
        for (int i = 0; i < DIRECTIONS.length; i++) {
            if (DIRECTIONS[i] == cachedDirection) {
                index = i;
                break;
            }
        }
        cachedDirection = DIRECTIONS[(index + 1) % DIRECTIONS.length];
        com.examplebett.compat.LegacyGui.setMessage(this.directionButton, ModText.literal(String.valueOf(cachedDirection)));

        cachedPhaseOffsetTicks = (cachedDirection == 'E' || cachedDirection == 'W')
                ? (cachedGreenTicks + cachedRedTicks) / 2
                : 0;
        SetSignalDirectionPayload mineturismo$payload = new SetSignalDirectionPayload(String.valueOf(cachedDirection));
        ModClientNetworking.sendToServer(SetSignalDirectionPayload.ID, mineturismo$payload, SetSignalDirectionPayload::write);
        this.sendTiming();
    }

    @Override
    protected void init() {
        int x = this.width / 2 - FIELD_WIDTH / 2;
        int top = this.height / 2 - 110;

        this.nameBox = new EditBox(this.font, x, top, FIELD_WIDTH, 20,
                com.examplebett.compat.LegacyGui.text(ModText.translatable("gui.mineturismo.traffic_signal.name_hint")));
        this.nameBox.setMaxLength(32);
        this.nameBox.setValue(cachedName);
        ModWidgets.setHint(this.nameBox, ModText.translatable("gui.mineturismo.traffic_signal.name_hint"));
        this.nameBox.setResponder(value -> {
            cachedName = value;
            SetSignalNamePayload mineturismo$payload = new SetSignalNamePayload(value);
            ModClientNetworking.sendToServer(SetSignalNamePayload.ID, mineturismo$payload, SetSignalNamePayload::write);
        });
        this.addButton(this.nameBox);
        this.setInitialFocus(this.nameBox);

        int directionY = top + 30;
        this.directionButton = ModWidgets.button(ModText.literal(String.valueOf(cachedDirection)), x, directionY, FIELD_WIDTH, 20, button -> this.cycleDirection());
        this.addButton(this.directionButton);

        int greenY = directionY + 40;
        int redY = greenY + 44;
        int offsetY = redY + 44;
        this.greenValueY = greenY + 22;
        this.redValueY = redY + 22;
        this.offsetValueY = offsetY + 22;

        this.addButton(ModWidgets.button(ModText.literal("-20"), x, greenY, 40, 20, button -> adjustGreen(-20)));
        this.addButton(ModWidgets.button(ModText.literal("-100"), x + 44, greenY, 40, 20, button -> adjustGreen(-100)));
        this.addButton(ModWidgets.button(ModText.literal("+100"), x + FIELD_WIDTH - 84, greenY, 40, 20, button -> adjustGreen(100)));
        this.addButton(ModWidgets.button(ModText.literal("+20"), x + FIELD_WIDTH - 40, greenY, 40, 20, button -> adjustGreen(20)));

        this.addButton(ModWidgets.button(ModText.literal("-20"), x, redY, 40, 20, button -> adjustRed(-20)));
        this.addButton(ModWidgets.button(ModText.literal("-100"), x + 44, redY, 40, 20, button -> adjustRed(-100)));
        this.addButton(ModWidgets.button(ModText.literal("+100"), x + FIELD_WIDTH - 84, redY, 40, 20, button -> adjustRed(100)));
        this.addButton(ModWidgets.button(ModText.literal("+20"), x + FIELD_WIDTH - 40, redY, 40, 20, button -> adjustRed(20)));

        this.addButton(ModWidgets.button(ModText.literal("-20"), x, offsetY, 40, 20, button -> adjustOffset(-20)));
        this.addButton(ModWidgets.button(ModText.literal("-100"), x + 44, offsetY, 40, 20, button -> adjustOffset(-100)));
        this.addButton(ModWidgets.button(ModText.literal("+100"), x + FIELD_WIDTH - 84, offsetY, 40, 20, button -> adjustOffset(100)));
        this.addButton(ModWidgets.button(ModText.literal("+20"), x + FIELD_WIDTH - 40, offsetY, 40, 20, button -> adjustOffset(20)));

        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.back"), x, offsetY + 40, FIELD_WIDTH, 20, button ->
                this.minecraft.setScreen(null)));
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTick) {
        mineturismo$renderScreen(new GuiGraphics(null), mouseX, mouseY, partialTick);
    }

    private void mineturismo$renderScreen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        super.render(mouseX, mouseY, partialTick);
        int centerX = this.width / 2;
        int top = this.height / 2 - 110;

        guiGraphics.drawCenteredString(this.font, this.title, centerX, top - 30, 0xFFFFFFFF);

        String groupKey = cachedName.isEmpty() ? "?" : cachedName + "_" + cachedDirection;
        guiGraphics.drawCenteredString(this.font,
                ModText.translatable("gui.mineturismo.traffic_signal.next_group", groupKey),
                centerX, top + 24, 0xFFFFFF55);
        guiGraphics.drawCenteredString(this.font,
                ModText.translatable("gui.mineturismo.traffic_signal.group_desc"),
                centerX, top + 44, 0xFFAAAAAA);

        guiGraphics.drawCenteredString(this.font,
                ModText.translatable("gui.mineturismo.traffic_signal.green_title"),
                centerX, this.greenValueY - 26, 0xFFFFFFFF);
        guiGraphics.drawCenteredString(this.font, String.format("%.1fs (%d ticks)", cachedGreenTicks / 20.0, cachedGreenTicks),
                centerX, this.greenValueY, 0xFF55FF55);

        guiGraphics.drawCenteredString(this.font,
                ModText.translatable("gui.mineturismo.traffic_signal.red_title"),
                centerX, this.redValueY - 26, 0xFFFFFFFF);
        guiGraphics.drawCenteredString(this.font, String.format("%.1fs (%d ticks)", cachedRedTicks / 20.0, cachedRedTicks),
                centerX, this.redValueY, 0xFFFF5555);

        guiGraphics.drawCenteredString(this.font,
                ModText.translatable("gui.mineturismo.traffic_signal.offset_title"),
                centerX, this.offsetValueY - 26, 0xFFFFFFFF);
        guiGraphics.drawCenteredString(this.font, String.format("%.1fs (%d ticks)", cachedPhaseOffsetTicks / 20.0, cachedPhaseOffsetTicks),
                centerX, this.offsetValueY, 0xFFFFFFFF);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
*/
//?}
