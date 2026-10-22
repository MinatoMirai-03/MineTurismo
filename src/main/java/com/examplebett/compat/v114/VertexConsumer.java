//? if >=1.15 {
//?}
//? if <1.15 {
/*
package com.examplebett.compat.v114;

public final class VertexConsumer {

    static final int STRIDE = 14;

    float[] data = new float[STRIDE * 256];
    int size;

    MultiBufferSource owner;
    RenderType type;
    private final float[] position = new float[3];
    private float x, y, z, r = 1.0f, g = 1.0f, b = 1.0f, a = 1.0f, u, v, nx, ny = 1.0f, nz;
    private int light = 0xF000F0;

    VertexConsumer() {
    }

    public VertexConsumer vertex(Matrix4f matrix, float x, float y, float z) {
        matrix.transformPosition(x, y, z, this.position);
        this.x = this.position[0];
        this.y = this.position[1];
        this.z = this.position[2];
        return this;
    }

    public VertexConsumer vertex(double x, double y, double z) {
        this.x = (float) x;
        this.y = (float) y;
        this.z = (float) z;
        return this;
    }

    public VertexConsumer color(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
        return this;
    }

    public VertexConsumer color(int r, int g, int b, int a) {
        return this.color(r / 255.0f, g / 255.0f, b / 255.0f, a / 255.0f);
    }

    public VertexConsumer uv(float u, float v) {
        this.u = u;
        this.v = v;
        return this;
    }

    public VertexConsumer overlayCoords(int overlay) {
        return this;
    }

    public VertexConsumer uv2(int packedLight) {
        this.light = packedLight;
        return this;
    }

    public VertexConsumer normal(Matrix3f matrix, float x, float y, float z) {
        this.nx = matrix.m00 * x + matrix.m01 * y + matrix.m02 * z;
        this.ny = matrix.m10 * x + matrix.m11 * y + matrix.m12 * z;
        this.nz = matrix.m20 * x + matrix.m21 * y + matrix.m22 * z;
        return this;
    }

    public VertexConsumer normal(float x, float y, float z) {
        this.nx = x;
        this.ny = y;
        this.nz = z;
        return this;
    }

    public void drawGl(Matrix4f matrix, int light, float red, float green, float blue, Runnable draw) {
        if (this.owner != null) {
            this.owner.addGlDraw(this.type, matrix.copy(), light, red, green, blue, draw);
        }
    }

    public void endVertex() {
        if (this.size + STRIDE > this.data.length) {
            this.data = java.util.Arrays.copyOf(this.data, this.data.length * 2);
        }
        float[] d = this.data;
        int o = this.size;
        d[o] = this.x;
        d[o + 1] = this.y;
        d[o + 2] = this.z;
        d[o + 3] = this.r;
        d[o + 4] = this.g;
        d[o + 5] = this.b;
        d[o + 6] = this.a;
        d[o + 7] = this.u;
        d[o + 8] = this.v;
        d[o + 9] = this.light & 0xFFFF;
        d[o + 10] = (this.light >> 16) & 0xFFFF;
        d[o + 11] = this.nx;
        d[o + 12] = this.ny;
        d[o + 13] = this.nz;
        this.size = o + STRIDE;
    }
}
*/
//?}
