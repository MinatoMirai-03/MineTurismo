package com.examplebett.render;
import com.examplebett.compat.ModText;

import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.ArrayList;
import java.util.List;

public final class FrontBrakeCaliperParts {

    public record Option(String id, String modelId, Component displayName) {
    }

    private static final String MARKER_SUFFIX = ".front_brake_caliper_part.json";

    private static final List<Option> BUILT_IN = List.of(
            new Option("red", "brake_caliper_red", ModText.translatable("gui.mineturismo.front_brake_caliper_adjust.part_red")),

            new Option("red_reversed", "brake_caliper_red", ModText.translatable("gui.mineturismo.front_brake_caliper_adjust.part_red_reversed")),

            new Option("black", "brake_caliper_black", ModText.translatable("gui.mineturismo.front_brake_caliper_adjust.part_black")),
            new Option("black_reversed", "brake_caliper_black", ModText.translatable("gui.mineturismo.front_brake_caliper_adjust.part_black_reversed")),
            new Option("gray", "brake_caliper_gray", ModText.translatable("gui.mineturismo.front_brake_caliper_adjust.part_gray")),
            new Option("gray_reversed", "brake_caliper_gray", ModText.translatable("gui.mineturismo.front_brake_caliper_adjust.part_gray_reversed")),
            new Option("white", "brake_caliper_white", ModText.translatable("gui.mineturismo.front_brake_caliper_adjust.part_white")),
            new Option("white_reversed", "brake_caliper_white", ModText.translatable("gui.mineturismo.front_brake_caliper_adjust.part_white_reversed")),
            new Option("yellow", "brake_caliper_yellow", ModText.translatable("gui.mineturismo.front_brake_caliper_adjust.part_yellow")),
            new Option("yellow_reversed", "brake_caliper_yellow", ModText.translatable("gui.mineturismo.front_brake_caliper_adjust.part_yellow_reversed")),
            new Option("orange", "brake_caliper_orange", ModText.translatable("gui.mineturismo.front_brake_caliper_adjust.part_orange")),
            new Option("orange_reversed", "brake_caliper_orange", ModText.translatable("gui.mineturismo.front_brake_caliper_adjust.part_orange_reversed")),
            new Option("blue", "brake_caliper_blue", ModText.translatable("gui.mineturismo.front_brake_caliper_adjust.part_blue")),
            new Option("blue_reversed", "brake_caliper_blue", ModText.translatable("gui.mineturismo.front_brake_caliper_adjust.part_blue_reversed"))
    );

    private static List<Option> cached;

    public static List<Option> list(ResourceManager resourceManager) {
        if (cached != null) {
            return cached;
        }
        List<Option> result = new ArrayList<>(BUILT_IN);
        for (String id : CustomPartCatalog.list(resourceManager, MARKER_SUFFIX)) {
            result.add(new Option(id, id, ModText.translatable("gui.mineturismo.front_brake_caliper_adjust.part_" + id)));
        }
        cached = result;
        return cached;
    }

    public static void clearCache() {
        cached = null;
    }

    private FrontBrakeCaliperParts() {
    }
}
