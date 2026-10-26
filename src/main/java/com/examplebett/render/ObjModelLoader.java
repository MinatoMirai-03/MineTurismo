package com.examplebett.render;

import com.examplebett.AiObjectMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class ObjModelLoader {

    private record TextureResolution(ResourceLocation location, boolean tintable) {
    }

    private static final Map<String, ObjModel> modelCache = new HashMap<>();
    private static final Map<String, Map<String, MtlMaterial>> materialCache = new HashMap<>();

    private static final Map<String, Map<String, MtlMaterial>> liveryMaterialCache = new HashMap<>();

    public static ObjModel loadModel(ResourceManager resourceManager, String modelPath) throws IOException {
        if (modelCache.containsKey(modelPath)) {
            return modelCache.get(modelPath);
        }

        ResourceLocation modelLocation = AiObjectMod.id("models/" + modelPath + ".obj");

        //? if >=1.19 {
        Resource resource = resourceManager.getResource(modelLocation).orElseThrow();
        try (InputStream stream = resource.open()) {
        //?} else
        /*Resource resource = resourceManager.getResource(modelLocation);
        try (InputStream stream = resource.getInputStream()) {*/
            ObjModel model = ObjModel.load(stream);
            modelCache.put(modelPath, model);
            return model;
        }
    }

    public static Map<String, MtlMaterial> loadMaterials(ResourceManager resourceManager, String materialPath) throws IOException {
        if (materialCache.containsKey(materialPath)) {
            return materialCache.get(materialPath);
        }

        ResourceLocation materialLocation = AiObjectMod.id("models/" + materialPath + ".mtl");
        //? if >=1.19 {
        Resource resource = resourceManager.getResource(materialLocation).orElseThrow();
        try (InputStream stream = resource.open()) {
        //?} else
        /*Resource resource = resourceManager.getResource(materialLocation);
        try (InputStream stream = resource.getInputStream()) {*/
            Map<String, MtlMaterial> materials = MtlMaterial.load(stream);
            for (MtlMaterial material : materials.values()) {
                if (material.texturePath != null) {

                    String fileName = material.texturePath.replace('\\', '/');
                    int slash = fileName.lastIndexOf('/');
                    if (slash >= 0) fileName = fileName.substring(slash + 1);
                    int dot = fileName.lastIndexOf('.');
                    String baseName = dot >= 0 ? fileName.substring(0, dot) : fileName;
                    TextureResolution resolution = resolveTexture(resourceManager, materialPath, baseName);
                    if (resolution != null) {
                        material.texture = resolution.location();
                        material.tintable = resolution.tintable();
                    }
                }
            }
            materialCache.put(materialPath, materials);
            return materials;
        }
    }

    public static Map<String, MtlMaterial> loadLiveryMaterials(ResourceManager resourceManager, String modelId,
                                                                  Map<String, MtlMaterial> baseMaterials,
                                                                  String liveryBaseName) {
        String cacheKey = modelId + "#" + liveryBaseName;
        Map<String, MtlMaterial> cached = liveryMaterialCache.get(cacheKey);
        if (cached != null) {
            return cached;
        }

        TextureResolution resolution = resolveTexture(resourceManager, modelId, liveryBaseName);
        Map<String, MtlMaterial> overridden = new HashMap<>();
        for (Map.Entry<String, MtlMaterial> entry : baseMaterials.entrySet()) {
            MtlMaterial base = entry.getValue();
            MtlMaterial copy = new MtlMaterial();
            copy.r = base.r;
            copy.g = base.g;
            copy.b = base.b;
            copy.a = base.a;
            copy.texturePath = base.texturePath;
            copy.translucent = base.translucent;
            copy.texture = base.texture;
            copy.tintable = base.tintable;
            if (base.texture != null && resolution != null) {
                copy.texture = resolution.location();
                copy.tintable = resolution.tintable();
            }
            overridden.put(entry.getKey(), copy);
        }
        liveryMaterialCache.put(cacheKey, overridden);
        return overridden;
    }

    private static TextureResolution resolveTexture(ResourceManager resourceManager, String modelPath, String baseName) {
        ResourceLocation candidate = AiObjectMod.id(
                "textures/entity/" + modelPath + "/" + baseName.toLowerCase(Locale.ROOT) + ".png");

        try {
            byte[] textureBytes;
            //? if >=1.19 {
            Optional<Resource> resource = resourceManager.getResource(candidate);
            if (resource.isEmpty()) {
                return null;
            }
            try (InputStream textureStream = resource.get().open()) {
                textureBytes = textureStream.readAllBytes();
            }
            //?} else {
            /*Resource resource = resourceManager.getResource(candidate);
            try (InputStream textureStream = resource.getInputStream()) {
                textureBytes = textureStream.readAllBytes();
            }*/
            //?}
            boolean tintable = TextureTintAnalyzer.isWhiteTexture(candidate, textureBytes);
            return new TextureResolution(candidate, tintable);
        } catch (IOException e) {
            return null;
        }
    }

    public static void clearCache() {
        modelCache.clear();
        materialCache.clear();
        liveryMaterialCache.clear();
        TextureTintAnalyzer.clearCache();
    }
}
