package com.examplebett.render;
import com.examplebett.compat.ModText;

import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.ArrayList;
import java.util.List;

public final class PoliceLightParts {

    public record Option(String id, String modelId, Component displayName) {
    }

    public static final String EMERGENCY_BEACON_ID = "police_light";

    public static final String JP_LIGHT_BAR_ID = "police_light_bar_jp";

    public static final String US_LIGHT_BAR_ID = "police_light_bar_us";

    public static final String EU_LIGHT_BAR_ID = "police_light_bar_eu";

    public static final String EMERGENCY_BEACON_2_ID = "police_light_2";
    public static final String EU_LIGHT_BAR_2_ID = "police_light_bar_eu_2";

    public static boolean isEmergencyBeacon(String id) {
        return EMERGENCY_BEACON_ID.equals(id) || EMERGENCY_BEACON_2_ID.equals(id);
    }

    public static boolean isEuLightBar(String id) {
        return EU_LIGHT_BAR_ID.equals(id) || EU_LIGHT_BAR_2_ID.equals(id);
    }

    public static final String US_PATTERN_FRONT_ID = "police_light_pattern_us_front";
    public static final String US_PATTERN_REAR_ID = "police_light_pattern_us_rear";

    public static boolean hasUsLightBarLights(String id) {
        return US_LIGHT_BAR_ID.equals(id) || US_PATTERN_FRONT_ID.equals(id) || US_PATTERN_REAR_ID.equals(id);
    }

    public static final String EU_PATTERN_1_ID = "police_light_pattern_eu_1";
    public static final String EU_PATTERN_2_ID = "police_light_pattern_eu_2";
    public static final String EU_PATTERN_3_ID = "police_light_pattern_eu_3";

    public static boolean isEuPattern(String id) {
        return EU_PATTERN_1_ID.equals(id) || EU_PATTERN_2_ID.equals(id) || EU_PATTERN_3_ID.equals(id);
    }

    public static boolean isEuPatternLeftFirst(String id) {
        return EU_PATTERN_2_ID.equals(id) || EU_PATTERN_3_ID.equals(id);
    }

    public static final String JP_PATTERN_ID = "police_light_pattern_jp";

    private static final String MARKER_SUFFIX = ".police_light_part.json";

    private static final List<Option> BUILT_IN = List.of(
            new Option(EMERGENCY_BEACON_ID, "police_light", ModText.translatable("gui.mineturismo.police_light_adjust.part_beacon")),
            new Option(EMERGENCY_BEACON_2_ID, "police_light", ModText.translatable("gui.mineturismo.police_light_adjust.part_beacon").append(" 2")),
            new Option(JP_LIGHT_BAR_ID, "police_light_bar_jp", ModText.translatable("gui.mineturismo.police_light_adjust.part_jp_light_bar")),
            new Option(US_LIGHT_BAR_ID, "police_light_bar_us", ModText.translatable("gui.mineturismo.police_light_adjust.part_us_light_bar")),
            new Option(EU_LIGHT_BAR_ID, "police_light_bar_eu", ModText.translatable("gui.mineturismo.police_light_adjust.part_eu_light_bar")),
            new Option(EU_LIGHT_BAR_2_ID, "police_light_bar_eu", ModText.translatable("gui.mineturismo.police_light_adjust.part_eu_light_bar").append(" 2")),
            new Option(US_PATTERN_FRONT_ID, null, ModText.translatable("gui.mineturismo.police_light_adjust.part_us_light_pattern_front")),
            new Option(US_PATTERN_REAR_ID, null, ModText.translatable("gui.mineturismo.police_light_adjust.part_us_light_pattern_rear")),
            new Option(EU_PATTERN_1_ID, null, ModText.translatable("gui.mineturismo.police_light_adjust.part_eu_light_pattern_1")),
            new Option(EU_PATTERN_2_ID, null, ModText.translatable("gui.mineturismo.police_light_adjust.part_eu_light_pattern_2")),
            new Option(EU_PATTERN_3_ID, null, ModText.translatable("gui.mineturismo.police_light_adjust.part_eu_light_pattern_3")),
            new Option(JP_PATTERN_ID, null, ModText.translatable("gui.mineturismo.police_light_adjust.part_jp_light_pattern"))
    );

    private static List<Option> cached;

    public static List<Option> list(ResourceManager resourceManager) {
        if (cached != null) {
            return cached;
        }
        List<Option> result = new ArrayList<>(BUILT_IN);
        for (String id : CustomPartCatalog.list(resourceManager, MARKER_SUFFIX)) {
            result.add(new Option(id, id, ModText.translatable("gui.mineturismo.police_light_adjust.part_" + id)));
        }
        cached = result;
        return cached;
    }

    public static void clearCache() {
        cached = null;
    }

    private PoliceLightParts() {
    }
}
