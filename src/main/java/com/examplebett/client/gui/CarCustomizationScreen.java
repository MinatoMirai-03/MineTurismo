//? if >=1.18 {
//?}
//? if <1.18 && >=1.16 {
/*
package com.examplebett.client.gui;
import com.examplebett.compat.ModText;

import com.examplebett.AiObjectMod;
import net.minecraft.client.gui.Font;

import com.examplebett.compat.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.List;

public interface CarCustomizationScreen {

    Component AI_OBJECT_NOTICE = ModText.translatable("gui.mineturismo.wrench_menu.ai_object_notice");

    default void renderAiObjectNotice(GuiGraphics guiGraphics, Font font, int screenWidth) {
        int textWidth = font.width(AI_OBJECT_NOTICE);
        int paddingX = 8;
        int paddingY = 3;
        int boxLeft = screenWidth / 2 - textWidth / 2 - paddingX;
        int boxRight = screenWidth / 2 + textWidth / 2 + paddingX;
        int boxTop = 6;
        int boxBottom = boxTop + font.lineHeight + paddingY * 2;
        guiGraphics.fill(boxLeft, boxTop, boxRight, boxBottom, 0xC0000000);
        guiGraphics.drawCenteredString(font, AI_OBJECT_NOTICE, screenWidth / 2, boxTop + paddingY, 0xFFFFD700);
    }

    Component CUSTOM_PART_MODEL_MISSING_NOTICE = ModText.translatable("gui.mineturismo.custom_parts.model_missing_notice");

    default boolean isAnyCustomPartModelMissing(ResourceManager resourceManager, List<String> modelIds) {
        for (String modelId : modelIds) {
            if (com.examplebett.compat.ModResources.getResourceOptional(resourceManager, AiObjectMod.id("models/" + modelId + ".obj")).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    default void renderCustomPartModelMissingNotice(GuiGraphics guiGraphics, Font font, int screenWidth) {
        int textWidth = font.width(CUSTOM_PART_MODEL_MISSING_NOTICE);
        int paddingX = 8;
        int paddingY = 3;
        int boxLeft = screenWidth / 2 - textWidth / 2 - paddingX;
        int boxRight = screenWidth / 2 + textWidth / 2 + paddingX;
        int boxTop = 6;
        int boxBottom = boxTop + font.lineHeight + paddingY * 2;
        guiGraphics.fill(boxLeft, boxTop, boxRight, boxBottom, 0xC0000000);
        guiGraphics.drawCenteredString(font, CUSTOM_PART_MODEL_MISSING_NOTICE, screenWidth / 2, boxTop + paddingY, 0xFFFF5555);
    }
}
*/
//?}
//? if <1.16 {
/*
package com.examplebett.client.gui;
import com.examplebett.compat.ModText;

import com.examplebett.AiObjectMod;
import net.minecraft.client.gui.Font;

import com.examplebett.compat.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.List;

public interface CarCustomizationScreen {

    Component AI_OBJECT_NOTICE = ModText.translatable("gui.mineturismo.wrench_menu.ai_object_notice");

    default void renderAiObjectNotice(GuiGraphics guiGraphics, Font font, int screenWidth) {
        int textWidth = com.examplebett.compat.LegacyGui.width(font, AI_OBJECT_NOTICE);
        int paddingX = 8;
        int paddingY = 3;
        int boxLeft = screenWidth / 2 - textWidth / 2 - paddingX;
        int boxRight = screenWidth / 2 + textWidth / 2 + paddingX;
        int boxTop = 6;
        int boxBottom = boxTop + font.lineHeight + paddingY * 2;
        guiGraphics.fill(boxLeft, boxTop, boxRight, boxBottom, 0xC0000000);
        guiGraphics.drawCenteredString(font, AI_OBJECT_NOTICE, screenWidth / 2, boxTop + paddingY, 0xFFFFD700);
    }

    Component CUSTOM_PART_MODEL_MISSING_NOTICE = ModText.translatable("gui.mineturismo.custom_parts.model_missing_notice");

    default boolean isAnyCustomPartModelMissing(ResourceManager resourceManager, List<String> modelIds) {
        for (String modelId : modelIds) {
            if (com.examplebett.compat.ModResources.getResourceOptional(resourceManager, AiObjectMod.id("models/" + modelId + ".obj")).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    default void renderCustomPartModelMissingNotice(GuiGraphics guiGraphics, Font font, int screenWidth) {
        int textWidth = com.examplebett.compat.LegacyGui.width(font, CUSTOM_PART_MODEL_MISSING_NOTICE);
        int paddingX = 8;
        int paddingY = 3;
        int boxLeft = screenWidth / 2 - textWidth / 2 - paddingX;
        int boxRight = screenWidth / 2 + textWidth / 2 + paddingX;
        int boxTop = 6;
        int boxBottom = boxTop + font.lineHeight + paddingY * 2;
        guiGraphics.fill(boxLeft, boxTop, boxRight, boxBottom, 0xC0000000);
        guiGraphics.drawCenteredString(font, CUSTOM_PART_MODEL_MISSING_NOTICE, screenWidth / 2, boxTop + paddingY, 0xFFFF5555);
    }
}
*/
//?}
