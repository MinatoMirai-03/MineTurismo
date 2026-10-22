package com.examplebett.client.render;

import com.examplebett.AiObjectMod;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.fabricmc.loader.api.FabricLoader;
//? if >=1.21.11 {
/*import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderSetup;*/
//?} else
import net.minecraft.client.renderer.RenderType;
//? if <1.21.11 {
import net.minecraft.client.renderer.RenderStateShard;
//?}
//? if >=1.21.5 {
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.renderer.RenderPipelines;
//?}
//? if >=26.2 {
/*import com.mojang.blaze3d.PrimitiveTopology;*/
//?}
import net.minecraft.resources.ResourceLocation;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

public final class SmoothShadingPipelines {
    private SmoothShadingPipelines() {
    }

    private static final String MID_TEX_ATTRIBUTE = "mc_midTexCoord";
    private static final String UV0_ATTRIBUTE = "UV0";

    private static final float MID_MARGIN = 0.001f;
    private static final int BUFFER_SIZE = 1536;

    //? if >=1.18.2 {
    private static final boolean SUPPORTED = true;
    //?} else {
    /*private static final boolean SUPPORTED = false;*/
    //?}

    private static Boolean available;

    private static final Map<ResourceLocation, RenderType> CUTOUT_TYPES = new HashMap<>();
    private static final Map<ResourceLocation, RenderType> CUTOUT_NO_CULL_TYPES = new HashMap<>();
    private static final Set<BufferBuilder> MARKED_BUFFERS = Collections.newSetFromMap(new WeakHashMap<>());

    //? if >=1.21.5 {
    private static RenderPipeline cutoutPipeline;
    private static RenderPipeline noCullPipeline;
    //?}

    public static boolean isAvailable() {
        if (available == null) {
            available = SUPPORTED && init();
        }
        return available;
    }

    public static RenderType cutoutType(ResourceLocation texture, boolean noCull) {
        if (!isAvailable()) {
            return null;
        }
        return useNoCull(noCull)
                ? CUTOUT_NO_CULL_TYPES.computeIfAbsent(texture, tex -> createType("mineturismo_entity_cutout_no_cull_triangles", tex, true))
                : CUTOUT_TYPES.computeIfAbsent(texture, tex -> createType("mineturismo_entity_cutout_triangles", tex, false));
    }

    //? if >=26.1 {
    /*private static boolean useNoCull(boolean noCull) {
        return true;
    }*/
    //?} else {
    private static boolean useNoCull(boolean noCull) {
        return noCull;
    }
    //?}

    public static void markBuffer(Object vertexConsumer) {
        if (vertexConsumer instanceof BufferBuilder builder) {
            MARKED_BUFFERS.add(builder);
        }
    }

    public static int legacyBegin(Object vertexConsumer) {
        return -1;
    }

    public static void legacyEnd(Object vertexConsumer, int start, float[] data, Object pose, boolean triangles) {
    }

    public static boolean consumeMark(BufferBuilder builder) {
        return !MARKED_BUFFERS.isEmpty() && MARKED_BUFFERS.remove(builder);
    }

    public static void fixMidTexCoord(ByteBuffer vertexBuffer, VertexFormat format, int vertexCount, boolean triangles) {
        if (!triangles || vertexBuffer == null || format == null) {
            return;
        }
        int midOffset = attributeOffset(format, MID_TEX_ATTRIBUTE);
        int uvOffset = attributeOffset(format, UV0_ATTRIBUTE);
        if (midOffset < 0 || uvOffset < 0) {
            return;
        }
        int stride = format.getVertexSize();
        ByteBuffer buffer = vertexBuffer.duplicate().order(ByteOrder.nativeOrder());
        int base = buffer.position();
        if (base + (long) vertexCount * stride > buffer.limit()) {
            return;
        }
        for (int first = 0; first + 2 < vertexCount; first += 3) {
            float maxU = Float.NEGATIVE_INFINITY;
            float maxV = Float.NEGATIVE_INFINITY;
            for (int k = 0; k < 3; k++) {
                int at = base + (first + k) * stride + uvOffset;
                maxU = Math.max(maxU, buffer.getFloat(at));
                maxV = Math.max(maxV, buffer.getFloat(at + 4));
            }
            float midU = maxU + MID_MARGIN;
            float midV = maxV + MID_MARGIN;
            for (int k = 0; k < 3; k++) {
                int at = base + (first + k) * stride + midOffset;
                buffer.putFloat(at, midU);
                buffer.putFloat(at + 4, midV);
            }
        }
    }

    //? if <1.21 {
    /*private static int attributeOffset(VertexFormat format, String name) {
        List<String> names = format.getElementAttributeNames();
        List<VertexFormatElement> elements = format.getElements();
        int offset = 0;
        for (int i = 0; i < elements.size() && i < names.size(); i++) {
            if (name.equals(names.get(i))) {
                return offset;
            }
            offset += elements.get(i).getByteSize();
        }
        return -1;
    }*/
    //?}
    //? if >=1.21 && <26.2 {
    private static int attributeOffset(VertexFormat format, String name) {
        List<String> names = format.getElementAttributeNames();
        List<VertexFormatElement> elements = format.getElements();
        for (int i = 0; i < elements.size() && i < names.size(); i++) {
            if (name.equals(names.get(i))) {
                return format.getOffset(elements.get(i));
            }
        }
        return -1;
    }
    //?}
    //? if >=26.2 {
    /*private static int attributeOffset(VertexFormat format, String name) {
        if (!format.contains(name)) {
            return -1;
        }
        VertexFormatElement element = format.getElement(name);
        return element != null ? element.offset() : -1;
    }*/
    //?}

    private static boolean init() {
        try {
            //? if >=1.21.5 {
            cutoutPipeline = RenderPipelines.register(buildPipeline("pipeline/entity_cutout_triangles", false));
            noCullPipeline = RenderPipelines.register(buildPipeline("pipeline/entity_cutout_no_cull_triangles", true));
            //?}
        } catch (Throwable t) {
            AiObjectMod.LOGGER.error("[smooth-shading] Failed to build TRIANGLES entity pipelines, falling back to vanilla QUADS", t);
            return false;
        }
        //? if >=1.21.5 {
        if (FabricLoader.getInstance().isModLoaded("iris")) {
            if (!registerWithIris(vanillaPipeline(false), cutoutPipeline)
                    || !registerWithIris(vanillaPipeline(true), noCullPipeline)) {

                AiObjectMod.LOGGER.warn("[smooth-shading] Could not register pipelines with Iris, falling back to vanilla QUADS");
                return false;
            }
        }
        //?}
        return true;
    }

    //? if >=1.18.2 && <1.21.5 {
    /*private static RenderType createType(String name, ResourceLocation texture, boolean noCull) {
        return LegacyAccess.createType(name, texture, noCull);
    }*/
    //?}
    //? if >=1.18.2 && <1.21.2 {
    /*private abstract static class LegacyAccess extends RenderType {
        private LegacyAccess() {
            super("", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.TRIANGLES, 0, false, false, () -> { }, () -> { });
        }

        static RenderType createType(String name, ResourceLocation texture, boolean noCull) {
            CompositeState.CompositeStateBuilder builder = CompositeState.builder()
                    .setShaderState(noCull ? RENDERTYPE_ENTITY_CUTOUT_NO_CULL_SHADER : RENDERTYPE_ENTITY_CUTOUT_SHADER)
                    .setTextureState(new TextureStateShard(texture, false, false))
                    .setTransparencyState(NO_TRANSPARENCY)
                    .setLightmapState(LIGHTMAP)
                    .setOverlayState(OVERLAY);
            if (noCull) {
                builder = builder.setCullState(NO_CULL);
            }
            return RenderType.create(name, DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.TRIANGLES, BUFFER_SIZE, true, false,
                    builder.createCompositeState(true));
        }
    }*/
    //?}
    //? if >=1.21.2 && <1.21.5 {
    /*private abstract static class LegacyAccess extends RenderType {
        private LegacyAccess() {
            super("", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.TRIANGLES, 0, false, false, () -> { }, () -> { });
        }

        static RenderType createType(String name, ResourceLocation texture, boolean noCull) {
            CompositeState.CompositeStateBuilder builder = CompositeState.builder()
                    .setShaderState(noCull ? RENDERTYPE_ENTITY_CUTOUT_NO_CULL_SHADER : RENDERTYPE_ENTITY_CUTOUT_SHADER)
                    .setTextureState(new TextureStateShard(texture, net.minecraft.util.TriState.FALSE, false))
                    .setTransparencyState(NO_TRANSPARENCY)
                    .setLightmapState(LIGHTMAP)
                    .setOverlayState(OVERLAY);
            if (noCull) {
                builder = builder.setCullState(NO_CULL);
            }
            return RenderType.create(name, DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.TRIANGLES, BUFFER_SIZE, true, false,
                    builder.createCompositeState(true));
        }
    }*/
    //?}
    //? if >=1.21.5 && <1.21.11 {
    private static RenderType createType(String name, ResourceLocation texture, boolean noCull) {
        RenderType.CompositeState state = RenderType.CompositeState.builder()
                .setTextureState(textureShard(texture))
                .setLightmapState(RenderStateShard.LIGHTMAP)
                .setOverlayState(RenderStateShard.OVERLAY)
                .createCompositeState(true);
        return RenderType.create(name, BUFFER_SIZE, true, false, noCull ? noCullPipeline : cutoutPipeline, state);
    }
    //?}

    //? if >=1.21.11 {
    /*private static RenderType createType(String name, ResourceLocation texture, boolean noCull) {
        RenderSetup setup = RenderSetup.builder(noCull ? noCullPipeline : cutoutPipeline)
                .withTexture("Sampler0", texture)
                .useLightmap()
                .useOverlay()
                .affectsCrumbling()
                .setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE)
                .createRenderSetup();
        return RenderType .create(name, setup);
    }*/
    //?}
    //? if <1.18.2 {
    /*private static RenderType createType(String name, ResourceLocation texture, boolean noCull) {
        return null;
    }*/
    //?}

    //? if >=1.21.5 && <1.21.6 {
    /*private static RenderStateShard.TextureStateShard textureShard(ResourceLocation texture) {
        return new RenderStateShard.TextureStateShard(texture, net.minecraft.util.TriState.FALSE, false);
    }*/
    //?}
    //? if >=1.21.6 && <1.21.11 {
    private static RenderStateShard.TextureStateShard textureShard(ResourceLocation texture) {
        return new RenderStateShard.TextureStateShard(texture, false);
    }
    //?}

    //? if >=1.21.5 && <1.21.9 {
    /*private static RenderPipeline buildPipeline(String path, boolean noCull) {
        RenderPipeline.Builder builder = RenderPipeline.builder(RenderPipelines.ENTITY_SNIPPET)
                .withLocation(AiObjectMod.id(path))
                .withShaderDefine("ALPHA_CUTOUT", 0.1f)
                .withSampler("Sampler1")
                .withVertexFormat(DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.TRIANGLES);
        if (noCull) {
            builder = builder.withCull(false);
        }
        return builder.build();
    }*/
    //?}
    //? if >=1.21.9 && <26.2 {
    private static RenderPipeline buildPipeline(String path, boolean noCull) {
        RenderPipeline.Builder builder = RenderPipeline.builder(RenderPipelines.ENTITY_SNIPPET)
                .withLocation(AiObjectMod.id(path))
                .withShaderDefine("ALPHA_CUTOUT", 0.1f);
        if (noCull) {
            builder = builder.withShaderDefine("PER_FACE_LIGHTING");
        }
        builder = builder.withSampler("Sampler1");

        //? if <26.1 {
        builder = builder.withVertexFormat(DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.TRIANGLES);
        //?} else
        /*builder = builder.withVertexFormat(DefaultVertexFormat.ENTITY, VertexFormat.Mode.TRIANGLES);*/
        if (noCull) {
            builder = builder.withCull(false);
        }
        return builder.build();
    }
    //?}

    //? if >=26.2 {
    /*private static RenderPipeline buildPipeline(String path, boolean noCull) {
        RenderPipeline.Builder builder = RenderPipeline.builder(RenderPipelines.ENTITY_SNIPPET)
                .withLocation(AiObjectMod.id(path))
                .withShaderDefine("ALPHA_CUTOUT", 0.1f);
        if (noCull) {
            builder = builder.withShaderDefine("PER_FACE_LIGHTING").withCull(false);
        }

        return builder.withBindGroupLayout(net.minecraft.client.renderer.BindGroupLayouts.SAMPLER1)
                .withPrimitiveTopology(PrimitiveTopology.TRIANGLES).build();
    }*/
    //?}

    //? if >=1.21.5 && <26.1 {
    private static RenderPipeline vanillaPipeline(boolean noCull) {
        return noCull ? RenderPipelines.ENTITY_CUTOUT_NO_CULL : RenderPipelines.ENTITY_CUTOUT;
    }
    //?}
    //? if >=26.1 {
    /*private static RenderPipeline vanillaPipeline(boolean noCull) {
        return noCull ? RenderPipelines.ENTITY_CUTOUT : RenderPipelines.ENTITY_CUTOUT_CULL;
    }*/
    //?}

    //? if >=1.21.5 {
    private static boolean registerWithIris(RenderPipeline vanilla, RenderPipeline ours) {
        try {
            Class<?> pipelines = Class.forName("net.irisshaders.iris.pipeline.IrisPipelines");
            pipelines.getMethod("copyPipeline", RenderPipeline.class, RenderPipeline.class).invoke(null, vanilla, ours);
            AiObjectMod.LOGGER.info("[smooth-shading] Registered {} with Iris as a copy of {}", ours.getLocation(), vanilla.getLocation());
            return true;
        } catch (Throwable t) {
            AiObjectMod.LOGGER.warn("[smooth-shading] IrisPipelines.copyPipeline unavailable, trying IrisApi.assignPipeline", t);
        }
        try {
            Class<?> api = Class.forName("net.irisshaders.iris.api.v0.IrisApi");
            Object instance = api.getMethod("getInstance").invoke(null);
            Class<?> program = Class.forName("net.irisshaders.iris.api.v0.IrisProgram");
            Object entities = program.getField("ENTITIES").get(null);
            api.getMethod("assignPipeline", RenderPipeline.class, program).invoke(instance, ours, entities);
            AiObjectMod.LOGGER.info("[smooth-shading] Registered {} with Iris via IrisApi (main pass only)", ours.getLocation());
            return true;
        } catch (Throwable t) {
            AiObjectMod.LOGGER.warn("[smooth-shading] IrisApi.assignPipeline failed", t);
            return false;
        }
    }
    //?}
}
