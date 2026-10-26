//? if >=1.18 {
//?}
//? if <1.18 && >=1.15 {
/*
package com.examplebett.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

public final class MetalTextConsumer implements VertexConsumer {

    private static final float[][] STYLE = {
            {1.35f, 0.6f, 0.9f},
            {1.25f, 0.7f, 0.7f},
            {1.15f, 0.8f, 0.45f}
    };

    private final VertexConsumer out;
    private final PoseStack.Pose pose;
    private final float left;
    private final float top;
    private final float width;
    private final float height;
    private final int rgb;
    private final float[] style;
    private final boolean gold;
    private final float phase;
    private float lastX;
    private float lastY;

    public MetalTextConsumer(VertexConsumer out, PoseStack.Pose pose, float left, float top, float width, float height,
                             int rgb, int metal, float phase) {
        this.out = out;
        this.pose = pose;
        this.left = left;
        this.top = top;
        this.width = width;
        this.height = height;
        this.rgb = rgb;
        this.style = STYLE[Math.max(0, Math.min(STYLE.length - 1, metal))];
        this.gold = metal == com.examplebett.client.gui.CarColorPalette.METAL_GOLD;
        this.phase = phase;
    }

    public static float phase(PoseStack.Pose pose, float centerX, float centerY) {
        float[] camera = PoseMatrices.transformPosition(PoseMatrices.invert(PoseMatrices.capture(pose)), 0f, 0f, 0f);
        float x = camera[0] - centerX;
        float y = camera[1] - centerY;
        float z = camera[2];
        float length = (float) Math.sqrt(x * x + y * y + z * z);
        if (length < 1e-4f) {
            return Float.NaN;
        }
        return 0.5f + 1.1f * (x / length * 0.8f - y / length * 0.6f);
    }

    private int[] metalColor() {
        float u = Math.max(0f, Math.min(1f, (this.lastX - this.left) / this.width));
        float v = Math.max(0f, Math.min(1f, (this.lastY - this.top) / this.height));
        float shade = this.style[0] + (this.style[1] - this.style[0]) * v;
        float glint = 0f;
        if (!Float.isNaN(this.phase)) {
            float s = (u * 0.8f + (1f - v) * 0.6f) / 1.4f;
            glint = Math.min(1f, glintBand(s, this.phase, 0.14f) + 0.6f * glintBand(s, this.phase - 0.24f, 0.05f)) * this.style[2];
        }

        return new int[]{
                metalChannel((this.rgb >> 16) & 0xFF, shade, glint, 255),
                metalChannel((this.rgb >> 8) & 0xFF, shade, glint, this.gold ? 244 : 255),
                metalChannel(this.rgb & 0xFF, shade, glint, this.gold ? 200 : 255)};
    }

    private static int metalChannel(int base, float shade, float glint, int highlight) {
        float c = Math.min(255f, base * shade);
        return (int) Math.min(255f, c + (highlight - c) * glint);
    }

    public static float glintBand(float s, float center, float halfWidth) {
        float t = 1f - Math.abs(s - center) / halfWidth;
        return t <= 0f ? 0f : t * t;
    }

    @Override
    public VertexConsumer vertex(double x, double y, double z) {
        this.lastX = (float) x;
        this.lastY = (float) y;
        this.out.vertex(this.pose.pose(), (float) x, (float) y, (float) z);
        return this;
    }

    @Override
    public VertexConsumer color(int red, int green, int blue, int alpha) {
        int[] c = this.metalColor();
        this.out.color(c[0], c[1], c[2], alpha);
        return this;
    }

    @Override
    public VertexConsumer uv(float u, float v) {
        this.out.uv(u, v);
        return this;
    }

    @Override
    public VertexConsumer overlayCoords(int u, int v) {
        this.out.overlayCoords(u, v);
        return this;
    }

    @Override
    public VertexConsumer uv2(int u, int v) {
        this.out.uv2(u, v);
        return this;
    }

    @Override
    public void endVertex() {
        this.out.endVertex();
    }

    public void defaultColor(int red, int green, int blue, int alpha) {
    }

    public void unsetDefaultColor() {
    }

    @Override
    public VertexConsumer normal(float x, float y, float z) {
        this.out.normal(this.pose.normal(), x, y, z);
        return this;
    }
}
*/
//?}
//? if <1.15 {
/*
package com.examplebett.client.render;

public final class MetalTextConsumer {
    private MetalTextConsumer() {
    }

    public static float glintBand(float s, float center, float halfWidth) {
        float t = 1f - Math.abs(s - center) / halfWidth;
        return t <= 0f ? 0f : t * t;
    }
}
*/
//?}
