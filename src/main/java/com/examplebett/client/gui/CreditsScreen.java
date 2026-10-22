//? if >=1.18 {
//?}
//? if <1.18 && >=1.17 {
/*
package com.examplebett.client.gui;

import com.examplebett.AiObjectMod;
import com.examplebett.compat.ModText;
import com.examplebett.compat.ModWidgets;

import com.examplebett.compat.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class CreditsScreen extends Screen {

    private static final Path FLAG_FILE = FabricLoader.getInstance().getConfigDir().resolve("mineturismo_credits_shown.flag");

    private static final Component[] LINES = {
            ModText.translatable("gui.mineturismo.credits.thanks"),
            ModText.literal(""),
            ModText.translatable("gui.mineturismo.credits.models_title"),
            ModText.literal("Lexyc16"),
            ModText.literal("Fenrate"),
            ModText.literal("iSteven"),
            ModText.literal(""),
            ModText.translatable("gui.mineturismo.credits.ai_generated"),
            ModText.literal(""),
            ModText.translatable("gui.mineturismo.credits.modified_note"),
    };

    private static final int LINE_HEIGHT = 11;
    private static final int BUTTON_WIDTH = 120;
    private static final int BUTTON_HEIGHT = 20;

    private static final String[] DISCLAIMER_KEYS = {
            "gui.mineturismo.credits.disclaimer_fictional",
            "gui.mineturismo.credits.disclaimer_endorsement",
    };

    private final boolean disclaimer;

    public CreditsScreen() {
        this(false);
    }

    private CreditsScreen(boolean disclaimer) {
        super(ModText.literal("Credits"));
        this.disclaimer = disclaimer;
    }

    private void mineturismo$onOk() {
        if (this.disclaimer) {
            this.onClose();
        } else {
            Minecraft.getInstance().setScreen(new CreditsScreen(true));
        }
    }

    public static void showOnceIfNeeded() {
        if (Files.exists(FLAG_FILE)) {
            return;
        }
        try {
            Files.createDirectories(FLAG_FILE.getParent());
            Files.createFile(FLAG_FILE);
        } catch (IOException e) {

            AiObjectMod.LOGGER.warn("Failed to write credits-shown flag", e);
        }
        Minecraft.getInstance().setScreen(new CreditsScreen());
    }

    @Override
    protected void init() {
        this.addRenderableWidget(ModWidgets.button(ModText.translatable("gui.mineturismo.back"),
                this.width / 2 - BUTTON_WIDTH / 2, this.height / 2 + 70, BUTTON_WIDTH, BUTTON_HEIGHT,
                button -> this.mineturismo$onOk()));
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        mineturismo$renderScreen(new GuiGraphics(poseStack), mouseX, mouseY, partialTick);
    }

    private void mineturismo$renderScreen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        super.render(guiGraphics.pose(), mouseX, mouseY, partialTick);
        int centerX = this.width / 2;
        if (this.disclaimer) {

            int maxWidth = Math.min(this.width - 40, 360);
            int y = this.height / 2 - 70;
            guiGraphics.drawCenteredString(this.font, ModText.translatable("gui.mineturismo.credits.disclaimer_title"), centerX, y, 0xFFFFFF55);
            y += LINE_HEIGHT * 2;
            for (String key : DISCLAIMER_KEYS) {
                for (net.minecraft.util.FormattedCharSequence line : this.font.split(ModText.translatable(key), maxWidth)) {
                    guiGraphics.drawCenteredString(this.font, line, centerX, y, 0xFFFFFFFF);
                    y += LINE_HEIGHT;
                }
                y += LINE_HEIGHT;
            }
            return;
        }
        int top = this.height / 2 - 70;
        for (int i = 0; i < LINES.length; i++) {
            guiGraphics.drawCenteredString(this.font, LINES[i], centerX, top + i * LINE_HEIGHT, 0xFFFFFFFF);
        }
    }
}
*/
//?}
//? if <1.17 && >=1.16.2 {
/*
package com.examplebett.client.gui;

import com.examplebett.AiObjectMod;
import com.examplebett.compat.ModText;
import com.examplebett.compat.ModWidgets;

import com.examplebett.compat.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class CreditsScreen extends Screen {

    private static final Path FLAG_FILE = FabricLoader.getInstance().getConfigDir().resolve("mineturismo_credits_shown.flag");

    private static final Component[] LINES = {
            ModText.translatable("gui.mineturismo.credits.thanks"),
            ModText.literal(""),
            ModText.translatable("gui.mineturismo.credits.models_title"),
            ModText.literal("Lexyc16"),
            ModText.literal("Fenrate"),
            ModText.literal("iSteven"),
            ModText.literal(""),
            ModText.translatable("gui.mineturismo.credits.ai_generated"),
            ModText.literal(""),
            ModText.translatable("gui.mineturismo.credits.modified_note"),
    };

    private static final int LINE_HEIGHT = 11;
    private static final int BUTTON_WIDTH = 120;
    private static final int BUTTON_HEIGHT = 20;

    private static final String[] DISCLAIMER_KEYS = {
            "gui.mineturismo.credits.disclaimer_fictional",
            "gui.mineturismo.credits.disclaimer_endorsement",
    };

    private final boolean disclaimer;

    public CreditsScreen() {
        this(false);
    }

    private CreditsScreen(boolean disclaimer) {
        super(ModText.literal("Credits"));
        this.disclaimer = disclaimer;
    }

    private void mineturismo$onOk() {
        if (this.disclaimer) {
            this.onClose();
        } else {
            Minecraft.getInstance().setScreen(new CreditsScreen(true));
        }
    }

    public static void showOnceIfNeeded() {
        if (Files.exists(FLAG_FILE)) {
            return;
        }
        try {
            Files.createDirectories(FLAG_FILE.getParent());
            Files.createFile(FLAG_FILE);
        } catch (IOException e) {

            AiObjectMod.LOGGER.warn("Failed to write credits-shown flag", e);
        }
        Minecraft.getInstance().setScreen(new CreditsScreen());
    }

    @Override
    protected void init() {
        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.back"),
                this.width / 2 - BUTTON_WIDTH / 2, this.height / 2 + 70, BUTTON_WIDTH, BUTTON_HEIGHT,
                button -> this.mineturismo$onOk()));
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        mineturismo$renderScreen(new GuiGraphics(poseStack), mouseX, mouseY, partialTick);
    }

    private void mineturismo$renderScreen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        super.render(guiGraphics.pose(), mouseX, mouseY, partialTick);
        int centerX = this.width / 2;
        if (this.disclaimer) {

            int maxWidth = Math.min(this.width - 40, 360);
            int y = this.height / 2 - 70;
            guiGraphics.drawCenteredString(this.font, ModText.translatable("gui.mineturismo.credits.disclaimer_title"), centerX, y, 0xFFFFFF55);
            y += LINE_HEIGHT * 2;
            for (String key : DISCLAIMER_KEYS) {
                for (net.minecraft.util.FormattedCharSequence line : this.font.split(ModText.translatable(key), maxWidth)) {
                    guiGraphics.drawCenteredString(this.font, line, centerX, y, 0xFFFFFFFF);
                    y += LINE_HEIGHT;
                }
                y += LINE_HEIGHT;
            }
            return;
        }
        int top = this.height / 2 - 70;
        for (int i = 0; i < LINES.length; i++) {
            guiGraphics.drawCenteredString(this.font, LINES[i], centerX, top + i * LINE_HEIGHT, 0xFFFFFFFF);
        }
    }
}
*/
//?}
//? if <1.16.2 && >=1.16 {
/*
package com.examplebett.client.gui;

import com.examplebett.AiObjectMod;
import com.examplebett.compat.ModText;
import com.examplebett.compat.ModWidgets;

import com.examplebett.compat.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class CreditsScreen extends Screen {

    private static final Path FLAG_FILE = FabricLoader.getInstance().getConfigDir().resolve("mineturismo_credits_shown.flag");

    private static final Component[] LINES = {
            ModText.translatable("gui.mineturismo.credits.thanks"),
            ModText.literal(""),
            ModText.translatable("gui.mineturismo.credits.models_title"),
            ModText.literal("Lexyc16"),
            ModText.literal("Fenrate"),
            ModText.literal("iSteven"),
            ModText.literal(""),
            ModText.translatable("gui.mineturismo.credits.ai_generated"),
            ModText.literal(""),
            ModText.translatable("gui.mineturismo.credits.modified_note"),
    };

    private static final int LINE_HEIGHT = 11;
    private static final int BUTTON_WIDTH = 120;
    private static final int BUTTON_HEIGHT = 20;

    private static final String[] DISCLAIMER_KEYS = {
            "gui.mineturismo.credits.disclaimer_fictional",
            "gui.mineturismo.credits.disclaimer_endorsement",
    };

    private final boolean disclaimer;

    public CreditsScreen() {
        this(false);
    }

    private CreditsScreen(boolean disclaimer) {
        super(ModText.literal("Credits"));
        this.disclaimer = disclaimer;
    }

    private void mineturismo$onOk() {
        if (this.disclaimer) {
            this.onClose();
        } else {
            Minecraft.getInstance().setScreen(new CreditsScreen(true));
        }
    }

    public static void showOnceIfNeeded() {
        if (Files.exists(FLAG_FILE)) {
            return;
        }
        try {
            Files.createDirectories(FLAG_FILE.getParent());
            Files.createFile(FLAG_FILE);
        } catch (IOException e) {

            AiObjectMod.LOGGER.warn("Failed to write credits-shown flag", e);
        }
        Minecraft.getInstance().setScreen(new CreditsScreen());
    }

    @Override
    protected void init() {
        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.back"),
                this.width / 2 - BUTTON_WIDTH / 2, this.height / 2 + 70, BUTTON_WIDTH, BUTTON_HEIGHT,
                button -> this.mineturismo$onOk()));
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        mineturismo$renderScreen(new GuiGraphics(poseStack), mouseX, mouseY, partialTick);
    }

    private void mineturismo$renderScreen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        super.render(guiGraphics.pose(), mouseX, mouseY, partialTick);
        int centerX = this.width / 2;
        if (this.disclaimer) {

            int maxWidth = Math.min(this.width - 40, 360);
            int y = this.height / 2 - 70;
            guiGraphics.drawCenteredString(this.font, ModText.translatable("gui.mineturismo.credits.disclaimer_title"), centerX, y, 0xFFFFFF55);
            y += LINE_HEIGHT * 2;
            for (String key : DISCLAIMER_KEYS) {
                for (net.minecraft.network.chat.FormattedText line : this.font.split(ModText.translatable(key), maxWidth)) {
                    guiGraphics.drawCenteredString(this.font, line, centerX, y, 0xFFFFFFFF);
                    y += LINE_HEIGHT;
                }
                y += LINE_HEIGHT;
            }
            return;
        }
        int top = this.height / 2 - 70;
        for (int i = 0; i < LINES.length; i++) {
            guiGraphics.drawCenteredString(this.font, LINES[i], centerX, top + i * LINE_HEIGHT, 0xFFFFFFFF);
        }
    }
}
*/
//?}
//? if <1.16 && >=1.15 {
/*
package com.examplebett.client.gui;

import com.examplebett.AiObjectMod;
import com.examplebett.compat.ModText;
import com.examplebett.compat.ModWidgets;

import com.examplebett.compat.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class CreditsScreen extends Screen {

    private static final Path FLAG_FILE = FabricLoader.getInstance().getConfigDir().resolve("mineturismo_credits_shown.flag");

    private static final Component[] LINES = {
            ModText.translatable("gui.mineturismo.credits.thanks"),
            ModText.literal(""),
            ModText.translatable("gui.mineturismo.credits.models_title"),
            ModText.literal("Lexyc16"),
            ModText.literal("Fenrate"),
            ModText.literal("iSteven"),
            ModText.literal(""),
            ModText.translatable("gui.mineturismo.credits.ai_generated"),
            ModText.literal(""),
            ModText.translatable("gui.mineturismo.credits.modified_note"),
    };

    private static final int LINE_HEIGHT = 11;
    private static final int BUTTON_WIDTH = 120;
    private static final int BUTTON_HEIGHT = 20;

    private static final String[] DISCLAIMER_KEYS = {
            "gui.mineturismo.credits.disclaimer_fictional",
            "gui.mineturismo.credits.disclaimer_endorsement",
    };

    private final boolean disclaimer;

    public CreditsScreen() {
        this(false);
    }

    private CreditsScreen(boolean disclaimer) {
        super(ModText.literal("Credits"));
        this.disclaimer = disclaimer;
    }

    private void mineturismo$onOk() {
        if (this.disclaimer) {
            this.onClose();
        } else {
            Minecraft.getInstance().setScreen(new CreditsScreen(true));
        }
    }

    public static void showOnceIfNeeded() {
        if (Files.exists(FLAG_FILE)) {
            return;
        }
        try {
            Files.createDirectories(FLAG_FILE.getParent());
            Files.createFile(FLAG_FILE);
        } catch (IOException e) {

            AiObjectMod.LOGGER.warn("Failed to write credits-shown flag", e);
        }
        Minecraft.getInstance().setScreen(new CreditsScreen());
    }

    @Override
    protected void init() {
        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.back"),
                this.width / 2 - BUTTON_WIDTH / 2, this.height / 2 + 70, BUTTON_WIDTH, BUTTON_HEIGHT,
                button -> this.mineturismo$onOk()));
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTick) {
        mineturismo$renderScreen(new GuiGraphics(null), mouseX, mouseY, partialTick);
    }

    private void mineturismo$renderScreen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        super.render(mouseX, mouseY, partialTick);
        int centerX = this.width / 2;
        if (this.disclaimer) {

            int maxWidth = Math.min(this.width - 40, 360);
            int y = this.height / 2 - 70;
            guiGraphics.drawCenteredString(this.font, ModText.translatable("gui.mineturismo.credits.disclaimer_title"), centerX, y, 0xFFFFFF55);
            y += LINE_HEIGHT * 2;
            for (String key : DISCLAIMER_KEYS) {
                for (String line : com.examplebett.compat.LegacyGui.split(this.font, ModText.translatable(key), maxWidth)) {
                    guiGraphics.drawCenteredString(this.font, line, centerX, y, 0xFFFFFFFF);
                    y += LINE_HEIGHT;
                }
                y += LINE_HEIGHT;
            }
            return;
        }
        int top = this.height / 2 - 70;
        for (int i = 0; i < LINES.length; i++) {
            guiGraphics.drawCenteredString(this.font, LINES[i], centerX, top + i * LINE_HEIGHT, 0xFFFFFFFF);
        }
    }
}
*/
//?}
//? if <1.15 {
/*
package com.examplebett.client.gui;

import com.examplebett.AiObjectMod;
import com.examplebett.compat.ModText;
import com.examplebett.compat.ModWidgets;

import com.examplebett.compat.GuiGraphics;
import com.examplebett.compat.v114.PoseStack;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class CreditsScreen extends Screen {

    private static final Path FLAG_FILE = FabricLoader.getInstance().getConfigDir().resolve("mineturismo_credits_shown.flag");

    private static final Component[] LINES = {
            ModText.translatable("gui.mineturismo.credits.thanks"),
            ModText.literal(""),
            ModText.translatable("gui.mineturismo.credits.models_title"),
            ModText.literal("Lexyc16"),
            ModText.literal("Fenrate"),
            ModText.literal("iSteven"),
            ModText.literal(""),
            ModText.translatable("gui.mineturismo.credits.ai_generated"),
            ModText.literal(""),
            ModText.translatable("gui.mineturismo.credits.modified_note"),
    };

    private static final int LINE_HEIGHT = 11;
    private static final int BUTTON_WIDTH = 120;
    private static final int BUTTON_HEIGHT = 20;

    private static final String[] DISCLAIMER_KEYS = {
            "gui.mineturismo.credits.disclaimer_fictional",
            "gui.mineturismo.credits.disclaimer_endorsement",
    };

    private final boolean disclaimer;

    public CreditsScreen() {
        this(false);
    }

    private CreditsScreen(boolean disclaimer) {
        super(ModText.literal("Credits"));
        this.disclaimer = disclaimer;
    }

    private void mineturismo$onOk() {
        if (this.disclaimer) {
            this.onClose();
        } else {
            Minecraft.getInstance().setScreen(new CreditsScreen(true));
        }
    }

    public static void showOnceIfNeeded() {
        if (Files.exists(FLAG_FILE)) {
            return;
        }
        try {
            Files.createDirectories(FLAG_FILE.getParent());
            Files.createFile(FLAG_FILE);
        } catch (IOException e) {

            AiObjectMod.LOGGER.warn("Failed to write credits-shown flag", e);
        }
        Minecraft.getInstance().setScreen(new CreditsScreen());
    }

    @Override
    protected void init() {
        this.addButton(ModWidgets.button(ModText.translatable("gui.mineturismo.back"),
                this.width / 2 - BUTTON_WIDTH / 2, this.height / 2 + 70, BUTTON_WIDTH, BUTTON_HEIGHT,
                button -> this.mineturismo$onOk()));
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTick) {
        mineturismo$renderScreen(new GuiGraphics(null), mouseX, mouseY, partialTick);
    }

    private void mineturismo$renderScreen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        super.render(mouseX, mouseY, partialTick);
        int centerX = this.width / 2;
        if (this.disclaimer) {

            int maxWidth = Math.min(this.width - 40, 360);
            int y = this.height / 2 - 70;
            guiGraphics.drawCenteredString(this.font, ModText.translatable("gui.mineturismo.credits.disclaimer_title"), centerX, y, 0xFFFFFF55);
            y += LINE_HEIGHT * 2;
            for (String key : DISCLAIMER_KEYS) {
                for (String line : com.examplebett.compat.LegacyGui.split(this.font, ModText.translatable(key), maxWidth)) {
                    guiGraphics.drawCenteredString(this.font, line, centerX, y, 0xFFFFFFFF);
                    y += LINE_HEIGHT;
                }
                y += LINE_HEIGHT;
            }
            return;
        }
        int top = this.height / 2 - 70;
        for (int i = 0; i < LINES.length; i++) {
            guiGraphics.drawCenteredString(this.font, LINES[i], centerX, top + i * LINE_HEIGHT, 0xFFFFFFFF);
        }
    }
}
*/
//?}
