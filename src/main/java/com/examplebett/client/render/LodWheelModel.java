//? if >=1.18 {
//?}
//? if <1.18 {
/*
package com.examplebett.client.render;

import com.examplebett.AiObjectMod;
import com.examplebett.render.CompiledCarModel;
import com.examplebett.render.ObjModelLoader;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.IOException;

public final class LodWheelModel {

    public static final String MODEL_ID = "wheel_lod_generic";

    private static CompiledCarModel compiled;

    private static CompiledCarModel compiledMirrored;
    private static boolean loadFailed;

    public static CompiledCarModel get(ResourceManager resourceManager) {
        load(resourceManager);
        return compiled;
    }

    public static CompiledCarModel getMirrored(ResourceManager resourceManager) {
        load(resourceManager);
        return compiledMirrored;
    }

    private static void load(ResourceManager resourceManager) {
        if (compiled == null && !loadFailed) {
            try {
                var model = ObjModelLoader.loadModel(resourceManager, MODEL_ID);
                var materials = ObjModelLoader.loadMaterials(resourceManager, MODEL_ID);
                compiled = CompiledCarModel.compile(model, materials, CompiledCarModel.FALLBACK_TEXTURE, true);
                compiledMirrored = CompiledCarModel.mirrorX(compiled);
            } catch (IOException e) {
                loadFailed = true;
                AiObjectMod.LOGGER.error("Failed to load LOD wheel model", e);
            }
        }
    }

    public static void clearCache() {
        compiled = null;
        compiledMirrored = null;
        loadFailed = false;
    }

    private LodWheelModel() {
    }
}
*/
//?}
