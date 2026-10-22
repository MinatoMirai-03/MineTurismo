//? if >=1.18 {
//?}
//? if <1.18 && >=1.17 {
/*
package com.examplebett.client.render;

import com.examplebett.AiObjectMod;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.fabricmc.loader.api.FabricLoader;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderStateShard;

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

    private static final boolean SUPPORTED = false;

    private static Boolean available;

    private static final Map<ResourceLocation, RenderType> CUTOUT_TYPES = new HashMap<>();
    private static final Map<ResourceLocation, RenderType> CUTOUT_NO_CULL_TYPES = new HashMap<>();
    private static final Set<BufferBuilder> MARKED_BUFFERS = Collections.newSetFromMap(new WeakHashMap<>());

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

    private static boolean useNoCull(boolean noCull) {
        return noCull;
    }

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

    private static int attributeOffset(VertexFormat format, String name) {
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
    }

    private static boolean init() {
        try {
        } catch (Throwable t) {
            AiObjectMod.LOGGER.error("[smooth-shading] Failed to build TRIANGLES entity pipelines, falling back to vanilla QUADS", t);
            return false;
        }
        return true;
    }

    private static RenderType createType(String name, ResourceLocation texture, boolean noCull) {
        return null;
    }

}
*/
//?}
