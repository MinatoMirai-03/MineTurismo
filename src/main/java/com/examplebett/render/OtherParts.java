package com.examplebett.render;
import com.examplebett.compat.ModText;

import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.ArrayList;
import java.util.List;

public final class OtherParts {

    public record Option(String id, String modelId, Component displayName) {
    }

    private static final String MARKER_SUFFIX = ".other_part.json";

    public static final String SPARE_TIRE_ID = "spare_tire";

    public static final String R34_ID = "nissan_skyline_r34_gtr";

    private static final String CARDBOARD_BOX_ID_PREFIX = "cardboard_box_";
    private static final String CARDBOARD_BOX_MODEL_ID = "breakable_cardboard_box";

    public static final float CARDBOARD_BOX_HEIGHT = 0.7434f;

    public static final String SITTING_STEVE_ID = "sitting_steve";
    public static final String SITTING_ALEX_ID = "sitting_alex";

    public static final String SITTING_WOLF_ID = "sitting_wolf";

    private static final List<Option> BUILT_IN = List.of(
            new Option("black_square", "black_square", ModText.translatable("gui.mineturismo.other_parts_adjust.part_black_square")),
            new Option(SPARE_TIRE_ID, null, ModText.translatable("gui.mineturismo.other_parts_adjust.part_spare_tire")),
            new Option(R34_ID, R34_ID, ModText.translatable("gui.mineturismo.other_parts_adjust.part_r34")),
            new Option(CARDBOARD_BOX_ID_PREFIX + 1, CARDBOARD_BOX_MODEL_ID, ModText.translatable("gui.mineturismo.other_parts_adjust.part_cardboard_box_1")),
            new Option(CARDBOARD_BOX_ID_PREFIX + 2, CARDBOARD_BOX_MODEL_ID, ModText.translatable("gui.mineturismo.other_parts_adjust.part_cardboard_box_2")),
            new Option(CARDBOARD_BOX_ID_PREFIX + 3, CARDBOARD_BOX_MODEL_ID, ModText.translatable("gui.mineturismo.other_parts_adjust.part_cardboard_box_3")),
            new Option(SITTING_STEVE_ID, null, ModText.translatable("gui.mineturismo.other_parts_adjust.part_sitting_steve")),
            new Option(SITTING_ALEX_ID, null, ModText.translatable("gui.mineturismo.other_parts_adjust.part_sitting_alex")),
            new Option(SITTING_WOLF_ID, null, ModText.translatable("gui.mineturismo.other_parts_adjust.part_sitting_wolf"))
    );

    public static boolean isSeatedPlayer(String id) {
        return SITTING_STEVE_ID.equals(id) || SITTING_ALEX_ID.equals(id);
    }

    public static boolean isSeatedCreature(String id) {
        return isSeatedPlayer(id) || SITTING_WOLF_ID.equals(id);
    }

    public static int cardboardBoxIndex(String id) {
        if (id != null && id.startsWith(CARDBOARD_BOX_ID_PREFIX)) {
            try {
                return Integer.parseInt(id.substring(CARDBOARD_BOX_ID_PREFIX.length())) - 1;
            } catch (NumberFormatException e) {
                return -1;
            }
        }
        return -1;
    }

    public static boolean usesObjModel(Option option) {
        return option.modelId() != null && !R34_ID.equals(option.modelId());
    }

    private static List<Option> cached;

    public static List<Option> list(ResourceManager resourceManager) {
        if (cached != null) {
            return cached;
        }
        List<Option> result = new ArrayList<>(BUILT_IN);
        for (String id : CustomPartCatalog.list(resourceManager, MARKER_SUFFIX)) {
            result.add(new Option(id, id, ModText.translatable("gui.mineturismo.other_parts_adjust.part_" + id)));
        }
        cached = result;
        return cached;
    }

    public static void clearCache() {
        cached = null;
    }

    private OtherParts() {
    }
}
