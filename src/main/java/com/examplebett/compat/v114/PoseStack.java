//? if >=1.15 {
//?}
//? if <1.15 {
/*
package com.examplebett.compat.v114;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;

import java.nio.FloatBuffer;
import java.util.ArrayDeque;
import java.util.Deque;

public class PoseStack {
    private static final FloatBuffer GL_MATRIX = org.lwjgl.BufferUtils.createFloatBuffer(16);

    private final Deque<Pose> stack = new ArrayDeque<>();
    private final boolean gl;

    public PoseStack() {
        this(false);
    }

    private PoseStack(boolean gl) {
        this.gl = gl;
        Matrix4f pose = new Matrix4f();
        pose.setIdentity();
        Matrix3f normal = new Matrix3f();
        normal.setIdentity();
        this.stack.addLast(new Pose(pose, normal));
    }

    public static PoseStack gl() {
        return new PoseStack(true);
    }

    public void translate(double x, double y, double z) {
        this.last().pose.multiply(Matrix4f.createTranslateMatrix((float) x, (float) y, (float) z));
        if (this.gl) {
            GlStateManager.translated(x, y, z);
        }
    }

    public void scale(float x, float y, float z) {
        Pose pose = this.last();
        pose.pose.multiply(Matrix4f.createScaleMatrix(x, y, z));
        if (x == y && y == z) {
            if (x < 0.0f) {
                pose.normal.mul(-1.0f);
            }
        } else {
            float ix = 1.0f / x;
            float iy = 1.0f / y;
            float iz = 1.0f / z;
            float n = (float) (1.0 / Math.cbrt(ix * iy * iz));
            pose.normal.mul(Matrix3f.createScaleMatrix(n * ix, n * iy, n * iz));
        }
        if (this.gl) {
            GlStateManager.scalef(x, y, z);
        }
    }

    public void mulPose(Quaternion rotation) {
        Pose pose = this.last();
        Matrix4f m = new Matrix4f(rotation);
        pose.pose.multiply(m);
        pose.normal.mul(rotation);
        if (this.gl) {
            GL_MATRIX.clear();
            m.store(GL_MATRIX);
            GL_MATRIX.flip();
            GlStateManager.multMatrix(GL_MATRIX);
        }
    }

    public void pushPose() {
        Pose pose = this.last();
        this.stack.addLast(new Pose(pose.pose.copy(), pose.normal.copy()));
        if (this.gl) {
            GlStateManager.pushMatrix();
        }
    }

    public void popPose() {
        this.stack.removeLast();
        if (this.gl) {
            GlStateManager.popMatrix();
        }
    }

    public Pose last() {
        return this.stack.getLast();
    }

    public boolean clear() {
        return this.stack.size() == 1;
    }

    public static Quaternion xRotation(float degrees) {
        return new Quaternion(new Vector3f(1.0f, 0.0f, 0.0f), degrees, true);
    }

    public static Quaternion yRotation(float degrees) {
        return new Quaternion(new Vector3f(0.0f, 1.0f, 0.0f), degrees, true);
    }

    public static Quaternion zRotation(float degrees) {
        return new Quaternion(new Vector3f(0.0f, 0.0f, 1.0f), degrees, true);
    }

    public static Quaternion xRotationRadians(float radians) {
        return new Quaternion(new Vector3f(1.0f, 0.0f, 0.0f), radians, false);
    }

    public static Quaternion yRotationRadians(float radians) {
        return new Quaternion(new Vector3f(0.0f, 1.0f, 0.0f), radians, false);
    }

    public static Quaternion zRotationRadians(float radians) {
        return new Quaternion(new Vector3f(0.0f, 0.0f, 1.0f), radians, false);
    }

    public static final class Pose {
        private final Matrix4f pose;
        private final Matrix3f normal;

        private Pose(Matrix4f pose, Matrix3f normal) {
            this.pose = pose;
            this.normal = normal;
        }

        public Matrix4f pose() {
            return this.pose;
        }

        public Matrix3f normal() {
            return this.normal;
        }
    }
}
*/
//?}
