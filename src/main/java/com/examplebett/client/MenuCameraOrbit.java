//? if >=1.18 {
//?}
//? if <1.18 {
/*
package com.examplebett.client;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;

public final class MenuCameraOrbit {

    private static boolean active = false;
    private static float yaw;
    private static float pitch;

    private MenuCameraOrbit() {
    }

    public static boolean isActive() {
        return active;
    }

    public static float yaw() {
        return yaw;
    }

    public static float pitch() {
        return pitch;
    }

    public static void begin(float initialYaw, float initialPitch) {
        active = true;
        yaw = initialYaw;
        pitch = initialPitch;
    }

    public static void end() {
        active = false;
    }

    private static final double SPEED_MULTIPLIER = 1.5;

    public static void drag(double dragX, double dragY) {

        double sensitivity = Minecraft.getInstance().options.sensitivity * 0.6 + 0.2;
        double factor = sensitivity * sensitivity * sensitivity * 8.0 * 0.15 * SPEED_MULTIPLIER;
        yaw = Mth.wrapDegrees(yaw + (float) (dragX * factor));
        pitch = Mth.clamp(pitch + (float) (dragY * factor), -90.0f, 90.0f);
    }
}
*/
//?}
