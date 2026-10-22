package com.examplebett.compat;

public final class ModMatrix4f {

    private final float[] m = new float[16];

    public ModMatrix4f() {
        identity();
    }

    public void identity() {
        for (int i = 0; i < 16; i++) m[i] = 0f;
        m[0] = 1f;
        m[5] = 1f;
        m[10] = 1f;
        m[15] = 1f;
    }

    public ModMatrix4f copy() {
        ModMatrix4f c = new ModMatrix4f();
        System.arraycopy(this.m, 0, c.m, 0, 16);
        return c;
    }

    float get(int row, int col) {
        return m[row * 4 + col];
    }

    private void set(int row, int col, float v) {
        m[row * 4 + col] = v;
    }

    public void multiply(ModMatrix4f other) {
        float[] result = new float[16];
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                float sum = 0f;
                for (int k = 0; k < 4; k++) {
                    sum += this.m[row * 4 + k] * other.m[k * 4 + col];
                }
                result[row * 4 + col] = sum;
            }
        }
        System.arraycopy(result, 0, this.m, 0, 16);
    }

    public static ModMatrix4f createTranslateMatrix(float x, float y, float z) {
        ModMatrix4f r = new ModMatrix4f();
        r.set(0, 3, x);
        r.set(1, 3, y);
        r.set(2, 3, z);
        return r;
    }

    public static ModMatrix4f createScaleMatrix(float x, float y, float z) {
        ModMatrix4f r = new ModMatrix4f();
        r.set(0, 0, x);
        r.set(1, 1, y);
        r.set(2, 2, z);
        return r;
    }

    public static ModMatrix4f fromQuaternion(float x, float y, float z, float w) {
        float len = (float) Math.sqrt(x * x + y * y + z * z + w * w);
        if (len > 1e-8f) {
            x /= len;
            y /= len;
            z /= len;
            w /= len;
        }
        float xx = x * x, yy = y * y, zz = z * z;
        float xy = x * y, xz = x * z, yz = y * z;
        float wx = w * x, wy = w * y, wz = w * z;

        ModMatrix4f r = new ModMatrix4f();
        r.set(0, 0, 1f - 2f * (yy + zz));
        r.set(0, 1, 2f * (xy - wz));
        r.set(0, 2, 2f * (xz + wy));
        r.set(1, 0, 2f * (xy + wz));
        r.set(1, 1, 1f - 2f * (xx + zz));
        r.set(1, 2, 2f * (yz - wx));
        r.set(2, 0, 2f * (xz - wy));
        r.set(2, 1, 2f * (yz + wx));
        r.set(2, 2, 1f - 2f * (xx + yy));
        r.set(3, 3, 1f);
        return r;
    }

    public static ModMatrix4f fromColumnMajor16(float[] v) {
        ModMatrix4f r = new ModMatrix4f();
        for (int col = 0; col < 4; col++) {
            for (int row = 0; row < 4; row++) {
                r.set(row, col, v[col * 4 + row]);
            }
        }
        return r;
    }

    public float determinant() {
        float a00 = m[0], a01 = m[1], a02 = m[2], a03 = m[3];
        float a10 = m[4], a11 = m[5], a12 = m[6], a13 = m[7];
        float a20 = m[8], a21 = m[9], a22 = m[10], a23 = m[11];
        float a30 = m[12], a31 = m[13], a32 = m[14], a33 = m[15];

        float b00 = a00 * a11 - a01 * a10;
        float b01 = a00 * a12 - a02 * a10;
        float b02 = a00 * a13 - a03 * a10;
        float b03 = a01 * a12 - a02 * a11;
        float b04 = a01 * a13 - a03 * a11;
        float b05 = a02 * a13 - a03 * a12;
        float b06 = a20 * a31 - a21 * a30;
        float b07 = a20 * a32 - a22 * a30;
        float b08 = a20 * a33 - a23 * a30;
        float b09 = a21 * a32 - a22 * a31;
        float b10 = a21 * a33 - a23 * a31;
        float b11 = a22 * a33 - a23 * a32;

        return b00 * b11 - b01 * b10 + b02 * b09 + b03 * b08 - b04 * b07 + b05 * b06;
    }

    public float[] transformPosition(float x, float y, float z) {
        float rx = m[0] * x + m[1] * y + m[2] * z + m[3];
        float ry = m[4] * x + m[5] * y + m[6] * z + m[7];
        float rz = m[8] * x + m[9] * y + m[10] * z + m[11];
        return new float[]{rx, ry, rz};
    }

}
