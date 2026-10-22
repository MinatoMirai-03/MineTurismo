//? if >=1.15 {
//?}
//? if <1.15 {
/*
package com.examplebett.compat.v114;

import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public final class RenderType {
    private static final Map<String, RenderType> CACHE = new HashMap<>();

    final ResourceLocation texture;
    final boolean noCull;
    final boolean translucent;

    final boolean emissive;

    private RenderType(ResourceLocation texture, boolean noCull, boolean translucent, boolean emissive) {
        this.texture = texture;
        this.noCull = noCull;
        this.translucent = translucent;
        this.emissive = emissive;
    }

    private static RenderType of(String kind, ResourceLocation texture, boolean noCull, boolean translucent, boolean emissive) {
        return CACHE.computeIfAbsent(kind + ":" + texture, key -> new RenderType(texture, noCull, translucent, emissive));
    }

    public static RenderType entityCutout(ResourceLocation texture) {
        return of("cutout", texture, false, false, false);
    }

    public static RenderType entityCutoutNoCull(ResourceLocation texture) {
        return of("cutout_no_cull", texture, true, false, false);
    }

    public static RenderType entitySolid(ResourceLocation texture) {
        return of("solid", texture, false, false, false);
    }

    public static RenderType entityNoOutline(ResourceLocation texture) {
        return of("no_outline", texture, true, true, false);
    }

    public static RenderType entityTranslucent(ResourceLocation texture) {
        return of("translucent", texture, true, true, false);
    }

    public static RenderType entityTranslucentCull(ResourceLocation texture) {
        return of("translucent_cull", texture, false, true, false);
    }

    public static RenderType itemEntityTranslucentCull(ResourceLocation texture) {
        return entityTranslucentCull(texture);
    }

    public static RenderType entityTranslucentEmissive(ResourceLocation texture) {
        return of("translucent_emissive", texture, true, true, true);
    }

    public static RenderType eyes(ResourceLocation texture) {
        return of("eyes", texture, true, true, true);
    }
}
*/
//?}
