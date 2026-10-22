//? if >=1.15 {
//?}
//? if <1.15 {
/*
package com.examplebett.compat.v114;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import org.lwjgl.opengl.GL11;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class MultiBufferSource {

    private static final VertexFormat FORMAT = new VertexFormat()
            .addElement(DefaultVertexFormat.ELEMENT_POSITION)
            .addElement(DefaultVertexFormat.ELEMENT_COLOR)
            .addElement(DefaultVertexFormat.ELEMENT_UV0)
            .addElement(DefaultVertexFormat.ELEMENT_UV1)
            .addElement(DefaultVertexFormat.ELEMENT_NORMAL);
    private static final FloatBuffer GL_MATRIX = org.lwjgl.BufferUtils.createFloatBuffer(16);

    private final Map<RenderType, VertexConsumer> buffers = new LinkedHashMap<>();
    private final List<Text> texts = new ArrayList<>();
    private final List<GlDraw> glDraws = new ArrayList<>();

    private static final class GlDraw {
        final RenderType type;
        final Matrix4f matrix;
        final int light;
        final float red;
        final float green;
        final float blue;
        final Runnable draw;

        GlDraw(RenderType type, Matrix4f matrix, int light, float red, float green, float blue, Runnable draw) {
            this.type = type;
            this.matrix = matrix;
            this.light = light;
            this.red = red;
            this.green = green;
            this.blue = blue;
            this.draw = draw;
        }
    }

    private static final class Text {
        final Font font;
        final String text;
        final float x;
        final float y;
        final int argb;
        final Matrix4f matrix;

        Text(Font font, String text, float x, float y, int argb, Matrix4f matrix) {
            this.font = font;
            this.text = text;
            this.x = x;
            this.y = y;
            this.argb = argb;
            this.matrix = matrix;
        }
    }

    public VertexConsumer getBuffer(RenderType type) {
        return this.buffers.computeIfAbsent(type, t -> {
            VertexConsumer consumer = new VertexConsumer();
            consumer.owner = this;
            consumer.type = t;
            return consumer;
        });
    }

    void addGlDraw(RenderType type, Matrix4f matrix, int light, float red, float green, float blue, Runnable draw) {
        this.glDraws.add(new GlDraw(type, matrix, light, red, green, blue, draw));
    }

    public void drawText(Font font, String text, float x, float y, int argb, Matrix4f matrix) {
        this.texts.add(new Text(font, text, x, y, argb, matrix.copy()));
    }

    public void endBatch() {
        Minecraft mc = Minecraft.getInstance();
        GlStateManager.disableLighting();
        mc.gameRenderer.turnOnLightLayer();
        GlStateManager.enableAlphaTest();
        for (Map.Entry<RenderType, VertexConsumer> e : this.buffers.entrySet()) {
            if (!e.getKey().translucent) {
                draw(mc, e.getKey(), e.getValue());
            }
        }
        for (Map.Entry<RenderType, VertexConsumer> e : this.buffers.entrySet()) {
            if (e.getKey().translucent) {
                draw(mc, e.getKey(), e.getValue());
            }
        }
        this.buffers.clear();

        for (GlDraw d : this.glDraws) {
            GlStateManager.activeTexture(com.mojang.blaze3d.platform.GLX.GL_TEXTURE0);
            mc.getTextureManager().bind(d.type.texture);
            if (d.type.noCull) {
                GlStateManager.disableCull();
            } else {
                GlStateManager.enableCull();
            }
            if (d.type.translucent) {
                GlStateManager.enableBlend();
                GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                        GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            } else {
                GlStateManager.disableBlend();
            }
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1f);
            com.mojang.blaze3d.platform.GLX.glMultiTexCoord2f(com.mojang.blaze3d.platform.GLX.GL_TEXTURE1,
                    d.light & 0xFFFF, (d.light >> 16) & 0xFFFF);
            GlStateManager.color4f(d.red, d.green, d.blue, 1.0f);
            GlStateManager.pushMatrix();
            GL_MATRIX.clear();
            d.matrix.store(GL_MATRIX);
            GL_MATRIX.flip();
            GlStateManager.multMatrix(GL_MATRIX);
            d.draw.run();
            GlStateManager.popMatrix();
        }
        this.glDraws.clear();
        if (!this.texts.isEmpty()) {
            GlStateManager.enableBlend();
            GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                    GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.disableCull();
            GlStateManager.enablePolygonOffset();
            GlStateManager.polygonOffset(-1.0f, -10.0f);
            for (Text t : this.texts) {
                GlStateManager.pushMatrix();
                GL_MATRIX.clear();
                t.matrix.store(GL_MATRIX);
                GL_MATRIX.flip();
                GlStateManager.multMatrix(GL_MATRIX);
                t.font.draw(t.text, t.x, t.y, t.argb);
                GlStateManager.popMatrix();
            }
            GlStateManager.polygonOffset(0.0f, 0.0f);
            GlStateManager.disablePolygonOffset();
            this.texts.clear();
        }
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1f);
        GlStateManager.enableLighting();
    }

    private static void draw(Minecraft mc, RenderType type, VertexConsumer consumer) {
        if (consumer.size == 0) {
            return;
        }
        GlStateManager.activeTexture(com.mojang.blaze3d.platform.GLX.GL_TEXTURE0);
        mc.getTextureManager().bind(type.texture);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        if (type.noCull) {
            GlStateManager.disableCull();
        } else {
            GlStateManager.enableCull();
        }
        if (type.translucent) {
            GlStateManager.enableBlend();
            GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                    GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.003921569f);
        } else {
            GlStateManager.disableBlend();
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1f);
        }
        GlStateManager.depthMask(!type.emissive);
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder builder = tesselator.getBuilder();
        builder.begin(GL11.GL_QUADS, FORMAT);
        float[] d = consumer.data;
        int quadEnd = consumer.size - consumer.size % (VertexConsumer.STRIDE * 4);
        for (int o = 0; o < quadEnd; o += VertexConsumer.STRIDE) {
            builder.vertex(d[o], d[o + 1], d[o + 2])
                    .color(d[o + 3], d[o + 4], d[o + 5], d[o + 6])
                    .uv(d[o + 7], d[o + 8])
                    .uv2((int) d[o + 9], (int) d[o + 10])
                    .normal(d[o + 11], d[o + 12], d[o + 13])
                    .endVertex();
        }
        tesselator.end();
        consumer.size = 0;
    }
}
*/
//?}
