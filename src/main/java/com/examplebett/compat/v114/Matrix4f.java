//? if >=1.15 {
//?}
//? if <1.15 {
/*
package com.examplebett.compat.v114;

import com.mojang.math.Quaternion;

public final class Matrix4f {
    public float m00, m01, m02, m03;
    public float m10, m11, m12, m13;
    public float m20, m21, m22, m23;
    public float m30, m31, m32, m33;

    public Matrix4f() {
    }

    public Matrix4f(Matrix4f o) {
        this.load(o);
    }

    public Matrix4f(Quaternion q) {
        float i = q.i();
        float j = q.j();
        float k = q.k();
        float r = q.r();
        float ii = 2.0f * i * i;
        float jj = 2.0f * j * j;
        float kk = 2.0f * k * k;
        this.m00 = 1.0f - jj - kk;
        this.m11 = 1.0f - kk - ii;
        this.m22 = 1.0f - ii - jj;
        this.m33 = 1.0f;
        float ij = i * j;
        float jk = j * k;
        float ki = k * i;
        float ir = i * r;
        float jr = j * r;
        float kr = k * r;
        this.m10 = 2.0f * (ij + kr);
        this.m01 = 2.0f * (ij - kr);
        this.m20 = 2.0f * (ki - jr);
        this.m02 = 2.0f * (ki + jr);
        this.m21 = 2.0f * (jk + ir);
        this.m12 = 2.0f * (jk - ir);
    }

    public void load(Matrix4f o) {
        this.m00 = o.m00; this.m01 = o.m01; this.m02 = o.m02; this.m03 = o.m03;
        this.m10 = o.m10; this.m11 = o.m11; this.m12 = o.m12; this.m13 = o.m13;
        this.m20 = o.m20; this.m21 = o.m21; this.m22 = o.m22; this.m23 = o.m23;
        this.m30 = o.m30; this.m31 = o.m31; this.m32 = o.m32; this.m33 = o.m33;
    }

    public Matrix4f copy() {
        return new Matrix4f(this);
    }

    public void setIdentity() {
        this.m00 = 1.0f; this.m01 = 0.0f; this.m02 = 0.0f; this.m03 = 0.0f;
        this.m10 = 0.0f; this.m11 = 1.0f; this.m12 = 0.0f; this.m13 = 0.0f;
        this.m20 = 0.0f; this.m21 = 0.0f; this.m22 = 1.0f; this.m23 = 0.0f;
        this.m30 = 0.0f; this.m31 = 0.0f; this.m32 = 0.0f; this.m33 = 1.0f;
    }

    public void multiply(Matrix4f o) {
        float a00 = m00 * o.m00 + m01 * o.m10 + m02 * o.m20 + m03 * o.m30;
        float a01 = m00 * o.m01 + m01 * o.m11 + m02 * o.m21 + m03 * o.m31;
        float a02 = m00 * o.m02 + m01 * o.m12 + m02 * o.m22 + m03 * o.m32;
        float a03 = m00 * o.m03 + m01 * o.m13 + m02 * o.m23 + m03 * o.m33;
        float a10 = m10 * o.m00 + m11 * o.m10 + m12 * o.m20 + m13 * o.m30;
        float a11 = m10 * o.m01 + m11 * o.m11 + m12 * o.m21 + m13 * o.m31;
        float a12 = m10 * o.m02 + m11 * o.m12 + m12 * o.m22 + m13 * o.m32;
        float a13 = m10 * o.m03 + m11 * o.m13 + m12 * o.m23 + m13 * o.m33;
        float a20 = m20 * o.m00 + m21 * o.m10 + m22 * o.m20 + m23 * o.m30;
        float a21 = m20 * o.m01 + m21 * o.m11 + m22 * o.m21 + m23 * o.m31;
        float a22 = m20 * o.m02 + m21 * o.m12 + m22 * o.m22 + m23 * o.m32;
        float a23 = m20 * o.m03 + m21 * o.m13 + m22 * o.m23 + m23 * o.m33;
        float a30 = m30 * o.m00 + m31 * o.m10 + m32 * o.m20 + m33 * o.m30;
        float a31 = m30 * o.m01 + m31 * o.m11 + m32 * o.m21 + m33 * o.m31;
        float a32 = m30 * o.m02 + m31 * o.m12 + m32 * o.m22 + m33 * o.m32;
        float a33 = m30 * o.m03 + m31 * o.m13 + m32 * o.m23 + m33 * o.m33;
        m00 = a00; m01 = a01; m02 = a02; m03 = a03;
        m10 = a10; m11 = a11; m12 = a12; m13 = a13;
        m20 = a20; m21 = a21; m22 = a22; m23 = a23;
        m30 = a30; m31 = a31; m32 = a32; m33 = a33;
    }

    public void multiply(Quaternion q) {
        this.multiply(new Matrix4f(q));
    }

    public void multiply(float s) {
        m00 *= s; m01 *= s; m02 *= s; m03 *= s;
        m10 *= s; m11 *= s; m12 *= s; m13 *= s;
        m20 *= s; m21 *= s; m22 *= s; m23 *= s;
        m30 *= s; m31 *= s; m32 *= s; m33 *= s;
    }

    public static Matrix4f createScaleMatrix(float x, float y, float z) {
        Matrix4f m = new Matrix4f();
        m.m00 = x;
        m.m11 = y;
        m.m22 = z;
        m.m33 = 1.0f;
        return m;
    }

    public static Matrix4f createTranslateMatrix(float x, float y, float z) {
        Matrix4f m = new Matrix4f();
        m.setIdentity();
        m.m03 = x;
        m.m13 = y;
        m.m23 = z;
        return m;
    }

    public void translate(float x, float y, float z) {
        this.multiply(createTranslateMatrix(x, y, z));
    }

    public float determinant() {
        float f = m00 * m11 - m01 * m10;
        float g = m00 * m12 - m02 * m10;
        float h = m00 * m13 - m03 * m10;
        float i = m01 * m12 - m02 * m11;
        float j = m01 * m13 - m03 * m11;
        float k = m02 * m13 - m03 * m12;
        float l = m20 * m31 - m21 * m30;
        float m = m20 * m32 - m22 * m30;
        float n = m20 * m33 - m23 * m30;
        float o = m21 * m32 - m22 * m31;
        float p = m21 * m33 - m23 * m31;
        float q = m22 * m33 - m23 * m32;
        return f * q - g * p + h * o + i * n - j * m + k * l;
    }

    public void transformPosition(float x, float y, float z, float[] out) {
        out[0] = m00 * x + m01 * y + m02 * z + m03;
        out[1] = m10 * x + m11 * y + m12 * z + m13;
        out[2] = m20 * x + m21 * y + m22 * z + m23;
    }

    public void transform(com.mojang.math.Vector4f v) {
        float x = v.x();
        float y = v.y();
        float z = v.z();
        float w = v.w();
        v.set(m00 * x + m01 * y + m02 * z + m03 * w, m10 * x + m11 * y + m12 * z + m13 * w,
                m20 * x + m21 * y + m22 * z + m23 * w, m30 * x + m31 * y + m32 * z + m33 * w);
    }

    public void store(java.nio.FloatBuffer buffer) {
        buffer.put(m00).put(m10).put(m20).put(m30);
        buffer.put(m01).put(m11).put(m21).put(m31);
        buffer.put(m02).put(m12).put(m22).put(m32);
        buffer.put(m03).put(m13).put(m23).put(m33);
    }
}
*/
//?}
