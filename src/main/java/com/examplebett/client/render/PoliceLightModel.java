//? if >=1.18 {
//?}
//? if <1.18 {
/*
package com.examplebett.client.render;

import com.examplebett.AiObjectMod;
import com.examplebett.render.CompiledCarModel;
import com.examplebett.render.ObjModelLoader;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class PoliceLightModel {

    private static final Map<String, CompiledCarModel> cache = new HashMap<>();
    private static final Set<String> loadFailedIds = new HashSet<>();

    public static CompiledCarModel get(ResourceManager resourceManager, String modelId) {
        CompiledCarModel cached = cache.get(modelId);
        if (cached != null) {
            return cached;
        }
        if (loadFailedIds.contains(modelId)) {
            return null;
        }
        try {
            var model = ObjModelLoader.loadModel(resourceManager, modelId);
            var materials = ObjModelLoader.loadMaterials(resourceManager, modelId);

            CompiledCarModel compiled = CompiledCarModel.compile(model, materials, CompiledCarModel.FALLBACK_TEXTURE, true);
            cache.put(modelId, compiled);
            return compiled;
        } catch (Exception e) {

            loadFailedIds.add(modelId);
            AiObjectMod.LOGGER.error("Failed to load police light part model: " + modelId, e);
            return null;
        }
    }

    public static void clearCache() {
        cache.clear();
        loadFailedIds.clear();
    }

    private PoliceLightModel() {
    }
}
*/
//?}
