package com.examplebett.entity;
import com.examplebett.compat.ModText;

import net.minecraft.network.chat.Component;

import java.util.List;

public final class HeadlightColors {

    public record Option(String id, float r, float g, float b, Component displayName) {
    }

    public static final List<Option> OPTIONS = List.of(
            new Option("current", 255 / 255f, 240 / 255f, 180 / 255f, ModText.translatable("gui.mineturismo.light_adjust.color_current")),
            new Option("yellow", 255 / 255f, 214 / 255f, 120 / 255f, ModText.translatable("gui.mineturismo.light_adjust.color_yellow")),
            new Option("yellow_vivid", 255 / 255f, 225 / 255f, 50 / 255f, ModText.translatable("gui.mineturismo.light_adjust.color_yellow_vivid")),
            new Option("white", 255 / 255f, 253 / 255f, 250 / 255f, ModText.translatable("gui.mineturismo.light_adjust.color_white")),
            new Option("blue", 215 / 255f, 230 / 255f, 255 / 255f, ModText.translatable("gui.mineturismo.light_adjust.color_blue")),
            new Option("blue_vivid", 140 / 255f, 185 / 255f, 255 / 255f, ModText.translatable("gui.mineturismo.light_adjust.color_blue_vivid"))
    );

    public static final String DEFAULT_ID = "current";

    public static Option byId(String id) {
        for (Option option : OPTIONS) {
            if (option.id().equals(id)) {
                return option;
            }
        }
        return OPTIONS.get(0);
    }

    private HeadlightColors() {
    }
}
