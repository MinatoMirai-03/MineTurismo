//? if >=1.16 {
//?}
//? if <1.16 {
/*
package com.examplebett.compat;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;

import java.lang.reflect.Field;
import java.util.List;

public final class LegacyGui {
    private static Field heightField;

    private LegacyGui() {
    }

    public static int width(Font font, String text) {
        return font.width(text);
    }

    public static int width(Font font, Component text) {
        return font.width(text.getString());
    }

    public static List<String> split(Font font, Component text, int width) {
        return font.split(text.getString(), width);
    }

    public static List<String> split(Font font, String text, int width) {
        return font.split(text, width);
    }

    public static String text(Component text) {
        return text.getString();
    }

    public static String text(String text) {
        return text;
    }

    public static void setMessage(AbstractWidget widget, Component message) {
        widget.setMessage(message.getString());
    }

    public static void setMessage(AbstractWidget widget, String message) {
        widget.setMessage(message);
    }

    public static int getHeight(AbstractWidget widget) {
        try {
            if (heightField == null) {

                heightField = ModReflection.findField(AbstractWidget.class, "height", "height");
            }
            return heightField.getInt(widget);
        } catch (Exception e) {
            return 20;
        }
    }

    public static int argb(int alpha, int red, int green, int blue) {
        return alpha << 24 | red << 16 | green << 8 | blue;
    }

    private static Font currentFont;

    public static Font currentFont() {
        return currentFont != null ? currentFont : net.minecraft.client.Minecraft.getInstance().font;
    }

    public static void setCurrentFont(Font font) {
        currentFont = font;
    }

    public static Font fontFor(net.minecraft.resources.ResourceLocation id) {
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        if (id == null) {
            return mc.font;
        }
        Font font = mc.getFontManager().get(id);
        return font != null ? font : mc.font;
    }

    public static Font textPartFont(String fontName) {
        if (com.examplebett.render.TextParts.FONT_DEFAULT.equals(fontName)) {
            return net.minecraft.client.Minecraft.getInstance().font;
        }
        return fontFor(com.examplebett.AiObjectMod.id("text_" + fontName));
    }
}
*/
//?}
