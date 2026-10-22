//? if >=1.15 {
//?}
//? if <1.15 {
/*
package com.examplebett.compat.v114;

import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;

public final class Matrix3f {
    public float m00, m01, m02;
    public float m10, m11, m12;
    public float m20, m21, m22;

    public Matrix3f() {
    }

    public Matrix3f(Matrix3f o) {
        this.load(o);
    }

    public Matrix3f(Quaternion q) {
        Matrix4f m = new Matrix4f(q);
        this.m00 = m.m00; this.m01 = m.m01; this.m02 = m.m02;
        this.m10 = m.m10; this.m11 = m.m11; this.m12 = m.m12;
        this.m20 = m.m20; this.m21 = m.m21; this.m22 = m.m22;
    }

    public void load(Matrix3f o) {
        this.m00 = o.m00; this.m01 = o.m01; this.m02 = o.m02;
        this.m10 = o.m10; this.m11 = o.m11; this.m12 = o.m12;
        this.m20 = o.m20; this.m21 = o.m21; this.m22 = o.m22;
    }

    public Matrix3f copy() {
        return new Matrix3f(this);
    }

    public void setIdentity() {
        this.m00 = 1.0f; this.m01 = 0.0f; this.m02 = 0.0f;
        this.m10 = 0.0f; this.m11 = 1.0f; this.m12 = 0.0f;
        this.m20 = 0.0f; this.m21 = 0.0f; this.m22 = 1.0f;
    }

    public static Matrix3f createScaleMatrix(float x, float y, float z) {
        Matrix3f m = new Matrix3f();
        m.m00 = x;
        m.m11 = y;
        m.m22 = z;
        return m;
    }

    public void mul(Matrix3f o) {
        float a00 = m00 * o.m00 + m01 * o.m10 + m02 * o.m20;
        float a01 = m00 * o.m01 + m01 * o.m11 + m02 * o.m21;
        float a02 = m00 * o.m02 + m01 * o.m12 + m02 * o.m22;
        float a10 = m10 * o.m00 + m11 * o.m10 + m12 * o.m20;
        float a11 = m10 * o.m01 + m11 * o.m11 + m12 * o.m21;
        float a12 = m10 * o.m02 + m11 * o.m12 + m12 * o.m22;
        float a20 = m20 * o.m00 + m21 * o.m10 + m22 * o.m20;
        float a21 = m20 * o.m01 + m21 * o.m11 + m22 * o.m21;
        float a22 = m20 * o.m02 + m21 * o.m12 + m22 * o.m22;
        m00 = a00; m01 = a01; m02 = a02;
        m10 = a10; m11 = a11; m12 = a12;
        m20 = a20; m21 = a21; m22 = a22;
    }

    public void mul(Quaternion q) {
        this.mul(new Matrix3f(q));
    }

    public void mul(float s) {
        m00 *= s; m01 *= s; m02 *= s;
        m10 *= s; m11 *= s; m12 *= s;
        m20 *= s; m21 *= s; m22 *= s;
    }

    public void transform(Vector3f v) {
        float x = v.x();
        float y = v.y();
        float z = v.z();
        v.set(m00 * x + m01 * y + m02 * z, m10 * x + m11 * y + m12 * z, m20 * x + m21 * y + m22 * z);
    }
}
*/
//?}
