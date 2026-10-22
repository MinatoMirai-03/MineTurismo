//? if >=1.18 {
//?}
//? if <1.18 && >=1.17 {
/*
package com.examplebett.client.render;

import com.examplebett.AiObjectMod;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public final class LegacyShaderFix {
    private static final float MID_TEX_MARGIN = 0.001f;
    private static java.lang.reflect.Field bufferBuilderBuffer;
    private static java.lang.reflect.Field bufferBuilderNextElementByte;
    private static java.lang.reflect.Field bufferBuilderFormat;
    private static boolean bufferBuilderReflectionFailed;

    private LegacyShaderFix() {
    }

    public static boolean isAvailable() {
        return false;
    }

    public static RenderType cutoutType(ResourceLocation texture, boolean noCull) {
        return null;
    }

    public static void markBuffer(Object vertexConsumer) {
    }

    private static boolean bufferBuilderFieldsReady() {
        if (bufferBuilderReflectionFailed) {
            return false;
        }
        if (bufferBuilderBuffer == null) {
            try {

                Class<?> owner = BufferBuilder.class;
                bufferBuilderBuffer = com.examplebett.compat.ModReflection.findField(owner, "buffer", "field_1555");
                bufferBuilderNextElementByte = com.examplebett.compat.ModReflection.findField(owner, "nextElementByte", "field_20884");
                bufferBuilderFormat = com.examplebett.compat.ModReflection.findField(owner, "format", "field_1565");
            } catch (ReflectiveOperationException e) {
                bufferBuilderReflectionFailed = true;
                AiObjectMod.LOGGER.warn("Could not access BufferBuilder internals; shader vertex correction is disabled", e);
                return false;
            }
        }
        return true;
    }

    public static int legacyBegin(Object consumer) {
        if (!(consumer instanceof BufferBuilder) || !bufferBuilderFieldsReady()) {
            return -1;
        }
        try {
            return bufferBuilderNextElementByte.getInt(consumer);
        } catch (IllegalAccessException e) {
            return -1;
        }
    }

    private static byte packNormalComponent(float value) {
        return (byte) ((int) (Math.max(-1.0f, Math.min(1.0f, value)) * 127.0f) & 0xFF);
    }

    public static void legacyEnd(Object consumer, int start, float[] data, Object poseObject, boolean triangles) {
        if (start < 0 || triangles) {
            return;
        }
        PoseStack.Pose pose = (PoseStack.Pose) poseObject;
        try {
            VertexFormat format = (VertexFormat) bufferBuilderFormat.get(consumer);
            ByteBuffer raw = (ByteBuffer) bufferBuilderBuffer.get(consumer);
            int end = bufferBuilderNextElementByte.getInt(consumer);
            if (format == null || raw == null) {
                return;
            }
            int midOffset = -1;
            int uvOffset = -1;
            int normalOffset = -1;
            int offset = 0;
            for (VertexFormatElement element : format.getElements()) {
                boolean float2 = element.getType() == VertexFormatElement.Type.FLOAT && element.getByteSize() == 8;
                if (float2 && element.getUsage() == VertexFormatElement.Usage.UV && element.getIndex() == 0 && uvOffset < 0) {
                    uvOffset = offset;
                }
                if (float2 && element.getUsage() == VertexFormatElement.Usage.GENERIC) {
                    midOffset = offset;
                }
                if (element.getUsage() == VertexFormatElement.Usage.NORMAL && normalOffset < 0) {
                    normalOffset = offset;
                }
                offset += element.getByteSize();
            }
            if (midOffset < 0 || uvOffset < 0 || normalOffset < 0) {
                return;
            }
            int stride = format.getVertexSize();
            if (end <= start || end > raw.capacity()) {
                return;
            }
            ByteBuffer buffer = raw.duplicate().order(ByteOrder.nativeOrder());

            int quads = Math.min((end - start) / stride / 4, data.length / 36);
            for (int q = 0; q < quads; q++) {
                float maxU = Float.NEGATIVE_INFINITY;
                float maxV = Float.NEGATIVE_INFINITY;
                for (int k = 0; k < 3; k++) {
                    int at = start + (q * 4 + k) * stride + uvOffset;
                    maxU = Math.max(maxU, buffer.getFloat(at));
                    maxV = Math.max(maxV, buffer.getFloat(at + 4));
                }
                for (int k = 0; k < 4; k++) {
                    int at = start + (q * 4 + k) * stride;
                    buffer.putFloat(at + midOffset, maxU + MID_TEX_MARGIN);
                    buffer.putFloat(at + midOffset + 4, maxV + MID_TEX_MARGIN);
                    int dataOffset = q * 36 + Math.min(k, 2) * 12;
                    Vector3f normal = new Vector3f(data[dataOffset + 9], data[dataOffset + 10], data[dataOffset + 11]);
                    normal.transform(pose.normal());
                    buffer.put(at + normalOffset, packNormalComponent(normal.x()));
                    buffer.put(at + normalOffset + 1, packNormalComponent(normal.y()));
                    buffer.put(at + normalOffset + 2, packNormalComponent(normal.z()));
                }
            }
        } catch (IllegalAccessException e) {

        }
    }
}
*/
//?}
//? if <1.17 && >=1.16 {
/*
package com.examplebett.client.render;

import com.examplebett.AiObjectMod;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;

public final class LegacyShaderFix {
    private static final int GL_TRIANGLES = 4;
    private static final float MID_TEX_MARGIN = 0.001f;
    private static final int IRIS_MID_TEXTURE_ELEMENT_INDEX = 12;
    private static final Map<ResourceLocation, RenderType> CUTOUT_TYPES = new HashMap<>();
    private static final Map<ResourceLocation, RenderType> CUTOUT_NO_CULL_TYPES = new HashMap<>();
    private static java.lang.reflect.Field bufferBuilderBuffer;
    private static java.lang.reflect.Field bufferBuilderNextElementByte;
    private static java.lang.reflect.Field bufferBuilderFormat;
    private static boolean bufferBuilderReflectionFailed;

    private LegacyShaderFix() {
    }

    public static boolean isAvailable() {
        return true;
    }

    public static void markBuffer(Object vertexConsumer) {
    }

    private static String intermediaryShardName(String fieldName) {
        switch (fieldName) {
            case "NO_TRANSPARENCY": return "field_21364";
            case "DEFAULT_ALPHA": return "field_21372";
            case "LIGHTMAP": return "field_21383";
            case "OVERLAY": return "field_21385";
            case "NO_CULL": return "field_21345";
            default: return fieldName;
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T vanillaShard(String fieldName) {
        try {
            java.lang.reflect.Field f = com.examplebett.compat.ModReflection.findField(RenderStateShard.class,
                    fieldName, intermediaryShardName(fieldName));
            return (T) f.get(null);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to access RenderStateShard." + fieldName, e);
        }
    }

    public static RenderType cutoutType(ResourceLocation texture, boolean noCull) {
        return noCull
                ? CUTOUT_NO_CULL_TYPES.computeIfAbsent(texture, tex -> createType("mineturismo_entity_cutout_no_cull_smooth_triangles", tex, true))
                : CUTOUT_TYPES.computeIfAbsent(texture, tex -> createType("mineturismo_entity_cutout_smooth_triangles", tex, false));
    }

    private static RenderType createType(String name, ResourceLocation texture, boolean noCull) {
        RenderType.CompositeState.CompositeStateBuilder builder = RenderType.CompositeState.builder()
                .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
                .setTransparencyState(LegacyShaderFix.<RenderStateShard.TransparencyStateShard>vanillaShard("NO_TRANSPARENCY"))
                .setDiffuseLightingState(new RenderStateShard.DiffuseLightingStateShard(true))
                .setShadeModelState(new RenderStateShard.ShadeModelStateShard(true))
                .setAlphaState(LegacyShaderFix.<RenderStateShard.AlphaStateShard>vanillaShard("DEFAULT_ALPHA"))
                .setLightmapState(LegacyShaderFix.<RenderStateShard.LightmapStateShard>vanillaShard("LIGHTMAP"))
                .setOverlayState(LegacyShaderFix.<RenderStateShard.OverlayStateShard>vanillaShard("OVERLAY"));
        if (noCull) {
            builder.setCullState(LegacyShaderFix.<RenderStateShard.CullStateShard>vanillaShard("NO_CULL"));
        }
        return RenderType.create(name, DefaultVertexFormat.NEW_ENTITY, GL_TRIANGLES, 256, true, false, builder.createCompositeState(true));
    }

    private static boolean bufferBuilderFieldsReady() {
        if (bufferBuilderReflectionFailed) {
            return false;
        }
        if (bufferBuilderBuffer == null) {
            try {

                Class<?> owner = BufferBuilder.class;
                bufferBuilderBuffer = com.examplebett.compat.ModReflection.findField(owner, "buffer", "field_1555");
                bufferBuilderNextElementByte = com.examplebett.compat.ModReflection.findField(owner, "nextElementByte", "field_20884");
                bufferBuilderFormat = com.examplebett.compat.ModReflection.findField(owner, "format", "field_1565");
            } catch (ReflectiveOperationException e) {
                bufferBuilderReflectionFailed = true;
                AiObjectMod.LOGGER.warn("Could not access BufferBuilder internals; mc_midTexCoord rewriting is disabled", e);
                return false;
            }
        }
        return true;
    }

    public static int legacyBegin(Object consumer) {
        if (!(consumer instanceof BufferBuilder) || !bufferBuilderFieldsReady()) {
            return -1;
        }
        try {
            return bufferBuilderNextElementByte.getInt(consumer);
        } catch (IllegalAccessException e) {
            return -1;
        }
    }

    private static byte packNormalComponent(float value) {
        return (byte) ((int) (Math.max(-1.0f, Math.min(1.0f, value)) * 127.0f) & 0xFF);
    }

    public static void legacyEnd(Object consumer, int start, float[] data, Object poseObject, boolean triangles) {
        if (start < 0 || !triangles) {
            return;
        }
        PoseStack.Pose pose = (PoseStack.Pose) poseObject;
        try {
            VertexFormat format = (VertexFormat) bufferBuilderFormat.get(consumer);
            ByteBuffer raw = (ByteBuffer) bufferBuilderBuffer.get(consumer);
            int end = bufferBuilderNextElementByte.getInt(consumer);
            if (format == null || raw == null) {
                return;
            }
            int midOffset = -1;
            int uvOffset = -1;
            int normalOffset = -1;
            int offset = 0;
            for (VertexFormatElement element : format.getElements()) {
                boolean isFloat = element.getType() == VertexFormatElement.Type.FLOAT;
                if (isFloat && element.getUsage() == VertexFormatElement.Usage.UV && element.getIndex() == 0 && uvOffset < 0) {
                    uvOffset = offset;
                }
                if (isFloat && element.getUsage() == VertexFormatElement.Usage.GENERIC
                        && element.getIndex() == IRIS_MID_TEXTURE_ELEMENT_INDEX) {
                    midOffset = offset;
                }
                if (element.getUsage() == VertexFormatElement.Usage.NORMAL && normalOffset < 0) {
                    normalOffset = offset;
                }
                offset += element.getByteSize();
            }
            if (midOffset < 0 || uvOffset < 0) {
                return;
            }
            int stride = format.getVertexSize();
            if (end <= start || end > raw.capacity()) {
                return;
            }
            ByteBuffer buffer = raw.duplicate().order(ByteOrder.nativeOrder());
            int vertexCount = (end - start) / stride;
            for (int first = 0; first + 2 < vertexCount; first += 3) {
                float maxU = Float.NEGATIVE_INFINITY;
                float maxV = Float.NEGATIVE_INFINITY;
                for (int k = 0; k < 3; k++) {
                    int at = start + (first + k) * stride + uvOffset;
                    maxU = Math.max(maxU, buffer.getFloat(at));
                    maxV = Math.max(maxV, buffer.getFloat(at + 4));
                }
                for (int k = 0; k < 3; k++) {
                    int at = start + (first + k) * stride + midOffset;
                    buffer.putFloat(at, maxU + MID_TEX_MARGIN);
                    buffer.putFloat(at + 4, maxV + MID_TEX_MARGIN);
                }
            }
            if (normalOffset >= 0) {

                int count = Math.min(vertexCount, data.length / 12);
                for (int v = 0; v < count; v++) {
                    Vector3f normal = new Vector3f(data[v * 12 + 9], data[v * 12 + 10], data[v * 12 + 11]);
                    normal.transform(pose.normal());
                    int at = start + v * stride + normalOffset;
                    buffer.put(at, packNormalComponent(normal.x()));
                    buffer.put(at + 1, packNormalComponent(normal.y()));
                    buffer.put(at + 2, packNormalComponent(normal.z()));
                }
            }
        } catch (IllegalAccessException e) {

        }
    }
}
*/
//?}
//? if <1.16 && >=1.15 {
/*
package com.examplebett.client.render;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public final class LegacyShaderFix {
    private LegacyShaderFix() {
    }

    public static boolean isAvailable() {
        return false;
    }

    public static RenderType cutoutType(ResourceLocation texture, boolean noCull) {
        return null;
    }

    public static void markBuffer(Object vertexConsumer) {
    }

    public static int legacyBegin(Object consumer) {
        return -1;
    }

    public static void legacyEnd(Object consumer, int start, float[] data, Object poseObject, boolean triangles) {
    }
}
*/
//?}
//? if <1.15 {
/*
package com.examplebett.client.render;

import com.examplebett.compat.v114.RenderType;
import net.minecraft.resources.ResourceLocation;

public final class LegacyShaderFix {
    private LegacyShaderFix() {
    }

    public static boolean isAvailable() {
        return false;
    }

    public static RenderType cutoutType(ResourceLocation texture, boolean noCull) {
        return null;
    }

    public static void markBuffer(Object vertexConsumer) {
    }

    public static int legacyBegin(Object consumer) {
        return -1;
    }

    public static void legacyEnd(Object consumer, int start, float[] data, Object poseObject, boolean triangles) {
    }
}
*/
//?}
