package com.examplebett.compat;

public final class ModMatrix3f {

    private final float[] m = new float[9];

    public ModMatrix3f(ModMatrix4f source) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                m[row * 3 + col] = source.get(row, col);
            }
        }
    }

    private ModMatrix3f() {
    }

    private static ModMatrix3f identity() {
        ModMatrix3f r = new ModMatrix3f();
        r.m[0] = 1f;
        r.m[4] = 1f;
        r.m[8] = 1f;
        return r;
    }

    private ModMatrix3f copyInternal() {
        ModMatrix3f c = new ModMatrix3f();
        System.arraycopy(this.m, 0, c.m, 0, 9);
        return c;
    }

    private float determinant() {
        float a = m[0], b = m[1], c = m[2];
        float d = m[3], e = m[4], f = m[5];
        float g = m[6], h = m[7], i = m[8];
        return a * (e * i - f * h) - b * (d * i - f * g) + c * (d * h - e * g);
    }

    public void invert() {
        float det = determinant();
        if (Math.abs(det) < 1e-12f) {

            float[] id = {1f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f};
            System.arraycopy(id, 0, m, 0, 9);
            return;
        }
        float invDet = 1f / det;
        float a = m[0], b = m[1], c = m[2];
        float d = m[3], e = m[4], f = m[5];
        float g = m[6], h = m[7], i = m[8];

        float[] adj = new float[9];
        adj[0] = (e * i - f * h) * invDet;
        adj[1] = (c * h - b * i) * invDet;
        adj[2] = (b * f - c * e) * invDet;
        adj[3] = (f * g - d * i) * invDet;
        adj[4] = (a * i - c * g) * invDet;
        adj[5] = (c * d - a * f) * invDet;
        adj[6] = (d * h - e * g) * invDet;
        adj[7] = (b * g - a * h) * invDet;
        adj[8] = (a * e - b * d) * invDet;
        System.arraycopy(adj, 0, m, 0, 9);
    }

    public void transpose() {
        float t;
        t = m[1];
        m[1] = m[3];
        m[3] = t;
        t = m[2];
        m[2] = m[6];
        m[6] = t;
        t = m[5];
        m[5] = m[7];
        m[7] = t;
    }

    public float[] transformNormal(float x, float y, float z) {
        float rx = m[0] * x + m[1] * y + m[2] * z;
        float ry = m[3] * x + m[4] * y + m[5] * z;
        float rz = m[6] * x + m[7] * y + m[8] * z;
        return new float[]{rx, ry, rz};
    }

    public record RotationScale(float rotationX, float rotationY, float rotationZ, float rotationW, float averageScale) {
    }

    public RotationScale extractRotationAndAverageScale() {
        ModMatrix3f r = this.copyInternal();
        for (int iter = 0; iter < 16; iter++) {
            ModMatrix3f invT = r.copyInternal();
            invT.invert();
            invT.transpose();
            ModMatrix3f next = new ModMatrix3f();
            for (int i = 0; i < 9; i++) {
                next.m[i] = 0.5f * (r.m[i] + invT.m[i]);
            }
            float diff = 0f;
            for (int i = 0; i < 9; i++) {
                diff += Math.abs(next.m[i] - r.m[i]);
            }
            r = next;
            if (diff < 1e-6f) {
                break;
            }
        }

        ModMatrix3f rT = r.copyInternal();
        rT.transpose();
        float s00 = rT.m[0] * this.m[0] + rT.m[1] * this.m[3] + rT.m[2] * this.m[6];
        float s11 = rT.m[3] * this.m[1] + rT.m[4] * this.m[4] + rT.m[5] * this.m[7];
        float s22 = rT.m[6] * this.m[2] + rT.m[7] * this.m[5] + rT.m[8] * this.m[8];
        float averageScale = (s00 + s11 + s22) / 3f;

        float m00 = r.m[0], m01 = r.m[1], m02 = r.m[2];
        float m10 = r.m[3], m11 = r.m[4], m12 = r.m[5];
        float m20 = r.m[6], m21 = r.m[7], m22 = r.m[8];
        float trace = m00 + m11 + m22;
        float qx, qy, qz, qw;
        if (trace > 0f) {
            float s = 0.5f / (float) Math.sqrt(trace + 1f);
            qw = 0.25f / s;
            qx = (m21 - m12) * s;
            qy = (m02 - m20) * s;
            qz = (m10 - m01) * s;
        } else if (m00 > m11 && m00 > m22) {
            float s = 2f * (float) Math.sqrt(1f + m00 - m11 - m22);
            qw = (m21 - m12) / s;
            qx = 0.25f * s;
            qy = (m01 + m10) / s;
            qz = (m02 + m20) / s;
        } else if (m11 > m22) {
            float s = 2f * (float) Math.sqrt(1f + m11 - m00 - m22);
            qw = (m02 - m20) / s;
            qx = (m01 + m10) / s;
            qy = 0.25f * s;
            qz = (m12 + m21) / s;
        } else {
            float s = 2f * (float) Math.sqrt(1f + m22 - m00 - m11);
            qw = (m10 - m01) / s;
            qx = (m02 + m20) / s;
            qy = (m12 + m21) / s;
            qz = 0.25f * s;
        }
        return new RotationScale(qx, qy, qz, qw, averageScale);
    }
}
