package com.examplebett.render;
import com.examplebett.compat.ModText;

import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.ArrayList;
import java.util.List;

public final class RearWingParts {

    public record Option(String id, String modelId, Component displayName) {
    }

    private static final String MARKER_SUFFIX = ".rear_wing_part.json";

    private static final List<Option> BUILT_IN = List.of(
            new Option("rear_wing_gt", "rear_wing_gt", ModText.translatable("gui.mineturismo.rear_wing_adjust.part_gt"))
    );

    private static List<Option> cached;

    public static List<Option> list(ResourceManager resourceManager) {
        if (cached != null) {
            return cached;
        }
        List<Option> result = new ArrayList<>(BUILT_IN);
        for (String id : CustomPartCatalog.list(resourceManager, MARKER_SUFFIX)) {
            result.add(new Option(id, id, ModText.translatable("gui.mineturismo.rear_wing_adjust.part_" + id)));
        }
        cached = result;
        return cached;
    }

    public static void clearCache() {
        cached = null;
    }

    private RearWingParts() {
    }
}
