//? if >=1.18 {
//?}
//? if <1.18 && >=1.16.2 {
/*
package com.examplebett.client;

import com.examplebett.compat.ModClientNetworking;
import com.examplebett.entity.CarEntity;
import com.examplebett.network.SetGttPayload;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.PauseScreen;

import com.examplebett.compat.GuiGraphics;

public final class GttClient {

    public static final float CAMERA_HEIGHT = 28f;

    public static final float CAMERA_YAW = 180f;
    public static final float CAMERA_PITCH = 90f;

    private static final int START_TIMEOUT_TICKS = 40;

    private static boolean active;
    private static int pendingStartTicks;
    private static CameraType previousCameraType;

    private static int score;

    private GttClient() {
    }

    public static boolean isActive() {
        return active;
    }

    public static void requestStart() {
        pendingStartTicks = START_TIMEOUT_TICKS;
        score = 0;
    }

    public static void setScore(int value) {
        score = value;
    }

    private static boolean isDriving(Minecraft mc) {
        return mc.player != null && mc.player.getVehicle() instanceof CarEntity car && car.getControllingPassenger() == mc.player;
    }

    public static void tick(Minecraft mc) {
        if (mc.player == null || mc.level == null) {

            if (active) {
                end(mc, false);
            }
            pendingStartTicks = 0;
            return;
        }
        boolean driving = isDriving(mc);
        if (pendingStartTicks > 0) {
            pendingStartTicks--;
            if (driving) {
                pendingStartTicks = 0;
                begin(mc);
            }
        }
        if (!active) {
            return;
        }
        if (!driving) {
            end(mc, true);
        } else if (mc.screen instanceof PauseScreen) {

            end(mc, true);
            mc.setScreen(null);
        }
    }

    private static void begin(Minecraft mc) {
        active = true;
        previousCameraType = mc.options.getCameraType();

        mc.options.setCameraType(CameraType.THIRD_PERSON_BACK);
    }

    private static void end(Minecraft mc, boolean notifyServer) {
        active = false;
        if (previousCameraType != null) {
            mc.options.setCameraType(previousCameraType);
            previousCameraType = null;
        }
        if (notifyServer) {
            SetGttPayload payload = new SetGttPayload(false);
            ModClientNetworking.sendToServer(SetGttPayload.ID, payload, SetGttPayload::write);
        }
    }

    private static final float FRAME_WIDTH_RATIO = 0.13f;
    private static final int FRAME_PIXEL = 2;
    private static final int FRAME_COLOR = 0xFF1B1C24;
    private static final int FRAME_COLOR_LIGHT = 0xFF2C2E3A;
    private static final int FRAME_EDGE_COLOR = 0xFF4A4D5C;
    private static final int FRAME_EDGE_SHADOW = 0xFF0C0C10;
    private static final int SCORE_COLOR = 0xFFFFD21E;
    private static final float SCORE_SCALE = 3.0f;
    private static final int STAR_COUNT = 6;
    private static final int STAR_DOT = 2;

    private static final String[] STAR_DOTS = {
            "....X....",
            "....X....",
            "...XXX...",
            "XXXXXXXXX",
            ".XXXXXXX.",
            "..XXXXX..",
            "..XXXXX..",
            ".XX...XX.",
            "X.......X",
    };

    public static void renderHud(GuiGraphics g) {
        Minecraft mc = Minecraft.getInstance();
        if (!active || mc.options.hideGui) {
            return;
        }
        int width = g.guiWidth();
        int height = g.guiHeight();
        int frameW = Math.round(width * FRAME_WIDTH_RATIO) / FRAME_PIXEL * FRAME_PIXEL;
        drawFrame(g, 0, frameW, height, false);
        drawFrame(g, width - frameW, width, height, true);

        int right = width - frameW - 8;
        String text = Integer.toString(score);
        int textWidth = Math.round(mc.font.width(text) * SCORE_SCALE);
        g.pose().pushPose();
        g.pose().scale(SCORE_SCALE, SCORE_SCALE, 1.0f);
        g.drawString(mc.font, text, Math.round((right - textWidth) / SCORE_SCALE), Math.round(8 / SCORE_SCALE), SCORE_COLOR, true);
        g.pose().popPose();

        int starSize = STAR_DOTS.length * STAR_DOT;
        int gap = 3;
        int starsTop = 8 + Math.round(mc.font.lineHeight * SCORE_SCALE) + 4;
        int starsLeft = right - (STAR_COUNT * starSize + (STAR_COUNT - 1) * gap);
        for (int i = 0; i < STAR_COUNT; i++) {
            drawStar(g, starsLeft + i * (starSize + gap), starsTop);
        }
    }

    private static void drawFrame(GuiGraphics g, int x0, int x1, int height, boolean rightSide) {
        int p = FRAME_PIXEL;
        g.fill(x0, 0, x1, height, FRAME_COLOR);
        int inner = rightSide ? x0 : x1;
        int bandX0 = rightSide ? inner + p : inner - p * 5;
        for (int y = 0; y < height; y += p) {
            for (int col = 0; col < 4; col++) {
                if (((y / p) + col) % 2 == 0) {
                    int x = bandX0 + col * p;
                    g.fill(x, y, x + p, y + p, FRAME_COLOR_LIGHT);
                }
            }
        }

        int edgeX = rightSide ? inner : inner - p;
        int shadowX = rightSide ? inner - p : inner;
        g.fill(edgeX, 0, edgeX + p, height, FRAME_EDGE_COLOR);
        g.fill(shadowX, 0, shadowX + p, height, FRAME_EDGE_SHADOW);

        int boltX = (x0 + x1) / 2 / p * p;
        for (int boltY : new int[]{p * 4, height - p * 6}) {
            g.fill(boltX - p, boltY, boltX + p, boltY + p * 2, FRAME_EDGE_SHADOW);
            g.fill(boltX - p, boltY, boltX, boltY + p, FRAME_EDGE_COLOR);
        }
    }

    private static void drawStar(GuiGraphics g, int x, int y) {

        for (int pass = 0; pass < 2; pass++) {
            int offset = pass == 0 ? 1 : 0;
            int color = pass == 0 ? 0x88000000 : 0xFFFFFFFF;
            for (int row = 0; row < STAR_DOTS.length; row++) {
                String line = STAR_DOTS[row];
                for (int col = 0; col < line.length(); col++) {
                    if (line.charAt(col) == 'X') {
                        int px = x + col * STAR_DOT + offset;
                        int py = y + row * STAR_DOT + offset;
                        g.fill(px, py, px + STAR_DOT, py + STAR_DOT, color);
                    }
                }
            }
        }
    }
}
*/
//?}
//? if <1.16.2 && >=1.16 {
/*
package com.examplebett.client;

import com.examplebett.compat.ModClientNetworking;
import com.examplebett.entity.CarEntity;
import com.examplebett.network.SetGttPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.PauseScreen;

import com.examplebett.compat.GuiGraphics;

public final class GttClient {

    public static final float CAMERA_HEIGHT = 28f;

    public static final float CAMERA_YAW = 180f;
    public static final float CAMERA_PITCH = 90f;

    private static final int START_TIMEOUT_TICKS = 40;

    private static boolean active;
    private static int pendingStartTicks;
    private static Integer previousCameraType;

    private static int score;

    private GttClient() {
    }

    public static boolean isActive() {
        return active;
    }

    public static void requestStart() {
        pendingStartTicks = START_TIMEOUT_TICKS;
        score = 0;
    }

    public static void setScore(int value) {
        score = value;
    }

    private static boolean isDriving(Minecraft mc) {
        return mc.player != null && mc.player.getVehicle() instanceof CarEntity car && car.getControllingPassenger() == mc.player;
    }

    public static void tick(Minecraft mc) {
        if (mc.player == null || mc.level == null) {

            if (active) {
                end(mc, false);
            }
            pendingStartTicks = 0;
            return;
        }
        boolean driving = isDriving(mc);
        if (pendingStartTicks > 0) {
            pendingStartTicks--;
            if (driving) {
                pendingStartTicks = 0;
                begin(mc);
            }
        }
        if (!active) {
            return;
        }
        if (!driving) {
            end(mc, true);
        } else if (mc.screen instanceof PauseScreen) {

            end(mc, true);
            mc.setScreen(null);
        }
    }

    private static void begin(Minecraft mc) {
        active = true;
        previousCameraType = mc.options.thirdPersonView;

        mc.options.thirdPersonView = 1;
    }

    private static void end(Minecraft mc, boolean notifyServer) {
        active = false;
        if (previousCameraType != null) {
            mc.options.thirdPersonView = previousCameraType;
            previousCameraType = null;
        }
        if (notifyServer) {
            SetGttPayload payload = new SetGttPayload(false);
            ModClientNetworking.sendToServer(SetGttPayload.ID, payload, SetGttPayload::write);
        }
    }

    private static final float FRAME_WIDTH_RATIO = 0.13f;
    private static final int FRAME_PIXEL = 2;
    private static final int FRAME_COLOR = 0xFF1B1C24;
    private static final int FRAME_COLOR_LIGHT = 0xFF2C2E3A;
    private static final int FRAME_EDGE_COLOR = 0xFF4A4D5C;
    private static final int FRAME_EDGE_SHADOW = 0xFF0C0C10;
    private static final int SCORE_COLOR = 0xFFFFD21E;
    private static final float SCORE_SCALE = 3.0f;
    private static final int STAR_COUNT = 6;
    private static final int STAR_DOT = 2;

    private static final String[] STAR_DOTS = {
            "....X....",
            "....X....",
            "...XXX...",
            "XXXXXXXXX",
            ".XXXXXXX.",
            "..XXXXX..",
            "..XXXXX..",
            ".XX...XX.",
            "X.......X",
    };

    public static void renderHud(GuiGraphics g) {
        Minecraft mc = Minecraft.getInstance();
        if (!active || mc.options.hideGui) {
            return;
        }
        int width = g.guiWidth();
        int height = g.guiHeight();
        int frameW = Math.round(width * FRAME_WIDTH_RATIO) / FRAME_PIXEL * FRAME_PIXEL;
        drawFrame(g, 0, frameW, height, false);
        drawFrame(g, width - frameW, width, height, true);

        int right = width - frameW - 8;
        String text = Integer.toString(score);
        int textWidth = Math.round(mc.font.width(text) * SCORE_SCALE);
        g.pose().pushPose();
        g.pose().scale(SCORE_SCALE, SCORE_SCALE, 1.0f);
        g.drawString(mc.font, text, Math.round((right - textWidth) / SCORE_SCALE), Math.round(8 / SCORE_SCALE), SCORE_COLOR, true);
        g.pose().popPose();

        int starSize = STAR_DOTS.length * STAR_DOT;
        int gap = 3;
        int starsTop = 8 + Math.round(mc.font.lineHeight * SCORE_SCALE) + 4;
        int starsLeft = right - (STAR_COUNT * starSize + (STAR_COUNT - 1) * gap);
        for (int i = 0; i < STAR_COUNT; i++) {
            drawStar(g, starsLeft + i * (starSize + gap), starsTop);
        }
    }

    private static void drawFrame(GuiGraphics g, int x0, int x1, int height, boolean rightSide) {
        int p = FRAME_PIXEL;
        g.fill(x0, 0, x1, height, FRAME_COLOR);
        int inner = rightSide ? x0 : x1;
        int bandX0 = rightSide ? inner + p : inner - p * 5;
        for (int y = 0; y < height; y += p) {
            for (int col = 0; col < 4; col++) {
                if (((y / p) + col) % 2 == 0) {
                    int x = bandX0 + col * p;
                    g.fill(x, y, x + p, y + p, FRAME_COLOR_LIGHT);
                }
            }
        }

        int edgeX = rightSide ? inner : inner - p;
        int shadowX = rightSide ? inner - p : inner;
        g.fill(edgeX, 0, edgeX + p, height, FRAME_EDGE_COLOR);
        g.fill(shadowX, 0, shadowX + p, height, FRAME_EDGE_SHADOW);

        int boltX = (x0 + x1) / 2 / p * p;
        for (int boltY : new int[]{p * 4, height - p * 6}) {
            g.fill(boltX - p, boltY, boltX + p, boltY + p * 2, FRAME_EDGE_SHADOW);
            g.fill(boltX - p, boltY, boltX, boltY + p, FRAME_EDGE_COLOR);
        }
    }

    private static void drawStar(GuiGraphics g, int x, int y) {

        for (int pass = 0; pass < 2; pass++) {
            int offset = pass == 0 ? 1 : 0;
            int color = pass == 0 ? 0x88000000 : 0xFFFFFFFF;
            for (int row = 0; row < STAR_DOTS.length; row++) {
                String line = STAR_DOTS[row];
                for (int col = 0; col < line.length(); col++) {
                    if (line.charAt(col) == 'X') {
                        int px = x + col * STAR_DOT + offset;
                        int py = y + row * STAR_DOT + offset;
                        g.fill(px, py, px + STAR_DOT, py + STAR_DOT, color);
                    }
                }
            }
        }
    }
}
*/
//?}
//? if <1.16 {
/*
package com.examplebett.client;

import com.examplebett.compat.ModClientNetworking;
import com.examplebett.entity.CarEntity;
import com.examplebett.network.SetGttPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.PauseScreen;

import com.examplebett.compat.GuiGraphics;

public final class GttClient {

    public static final float CAMERA_HEIGHT = 28f;

    public static final float CAMERA_YAW = 180f;
    public static final float CAMERA_PITCH = 90f;

    private static final int START_TIMEOUT_TICKS = 40;

    private static boolean active;
    private static int pendingStartTicks;
    private static Integer previousCameraType;

    private static int score;

    private GttClient() {
    }

    public static boolean isActive() {
        return active;
    }

    public static void requestStart() {
        pendingStartTicks = START_TIMEOUT_TICKS;
        score = 0;
    }

    public static void setScore(int value) {
        score = value;
    }

    private static boolean isDriving(Minecraft mc) {
        return mc.player != null && mc.player.getVehicle() instanceof CarEntity car && car.getControllingPassenger() == mc.player;
    }

    public static void tick(Minecraft mc) {
        if (mc.player == null || mc.level == null) {

            if (active) {
                end(mc, false);
            }
            pendingStartTicks = 0;
            return;
        }
        boolean driving = isDriving(mc);
        if (pendingStartTicks > 0) {
            pendingStartTicks--;
            if (driving) {
                pendingStartTicks = 0;
                begin(mc);
            }
        }
        if (!active) {
            return;
        }
        if (!driving) {
            end(mc, true);
        } else if (mc.screen instanceof PauseScreen) {

            end(mc, true);
            mc.setScreen(null);
        }
    }

    private static void begin(Minecraft mc) {
        active = true;
        previousCameraType = mc.options.thirdPersonView;

        mc.options.thirdPersonView = 1;
    }

    private static void end(Minecraft mc, boolean notifyServer) {
        active = false;
        if (previousCameraType != null) {
            mc.options.thirdPersonView = previousCameraType;
            previousCameraType = null;
        }
        if (notifyServer) {
            SetGttPayload payload = new SetGttPayload(false);
            ModClientNetworking.sendToServer(SetGttPayload.ID, payload, SetGttPayload::write);
        }
    }

    private static final float FRAME_WIDTH_RATIO = 0.13f;
    private static final int FRAME_PIXEL = 2;
    private static final int FRAME_COLOR = 0xFF1B1C24;
    private static final int FRAME_COLOR_LIGHT = 0xFF2C2E3A;
    private static final int FRAME_EDGE_COLOR = 0xFF4A4D5C;
    private static final int FRAME_EDGE_SHADOW = 0xFF0C0C10;
    private static final int SCORE_COLOR = 0xFFFFD21E;
    private static final float SCORE_SCALE = 3.0f;
    private static final int STAR_COUNT = 6;
    private static final int STAR_DOT = 2;

    private static final String[] STAR_DOTS = {
            "....X....",
            "....X....",
            "...XXX...",
            "XXXXXXXXX",
            ".XXXXXXX.",
            "..XXXXX..",
            "..XXXXX..",
            ".XX...XX.",
            "X.......X",
    };

    public static void renderHud(GuiGraphics g) {
        Minecraft mc = Minecraft.getInstance();
        if (!active || mc.options.hideGui) {
            return;
        }
        int width = g.guiWidth();
        int height = g.guiHeight();
        int frameW = Math.round(width * FRAME_WIDTH_RATIO) / FRAME_PIXEL * FRAME_PIXEL;
        drawFrame(g, 0, frameW, height, false);
        drawFrame(g, width - frameW, width, height, true);

        int right = width - frameW - 8;
        String text = Integer.toString(score);
        int textWidth = Math.round(com.examplebett.compat.LegacyGui.width(mc.font, text) * SCORE_SCALE);
        g.pose().pushPose();
        g.pose().scale(SCORE_SCALE, SCORE_SCALE, 1.0f);
        g.drawString(mc.font, text, Math.round((right - textWidth) / SCORE_SCALE), Math.round(8 / SCORE_SCALE), SCORE_COLOR, true);
        g.pose().popPose();

        int starSize = STAR_DOTS.length * STAR_DOT;
        int gap = 3;
        int starsTop = 8 + Math.round(mc.font.lineHeight * SCORE_SCALE) + 4;
        int starsLeft = right - (STAR_COUNT * starSize + (STAR_COUNT - 1) * gap);
        for (int i = 0; i < STAR_COUNT; i++) {
            drawStar(g, starsLeft + i * (starSize + gap), starsTop);
        }
    }

    private static void drawFrame(GuiGraphics g, int x0, int x1, int height, boolean rightSide) {
        int p = FRAME_PIXEL;
        g.fill(x0, 0, x1, height, FRAME_COLOR);
        int inner = rightSide ? x0 : x1;
        int bandX0 = rightSide ? inner + p : inner - p * 5;
        for (int y = 0; y < height; y += p) {
            for (int col = 0; col < 4; col++) {
                if (((y / p) + col) % 2 == 0) {
                    int x = bandX0 + col * p;
                    g.fill(x, y, x + p, y + p, FRAME_COLOR_LIGHT);
                }
            }
        }

        int edgeX = rightSide ? inner : inner - p;
        int shadowX = rightSide ? inner - p : inner;
        g.fill(edgeX, 0, edgeX + p, height, FRAME_EDGE_COLOR);
        g.fill(shadowX, 0, shadowX + p, height, FRAME_EDGE_SHADOW);

        int boltX = (x0 + x1) / 2 / p * p;
        for (int boltY : new int[]{p * 4, height - p * 6}) {
            g.fill(boltX - p, boltY, boltX + p, boltY + p * 2, FRAME_EDGE_SHADOW);
            g.fill(boltX - p, boltY, boltX, boltY + p, FRAME_EDGE_COLOR);
        }
    }

    private static void drawStar(GuiGraphics g, int x, int y) {

        for (int pass = 0; pass < 2; pass++) {
            int offset = pass == 0 ? 1 : 0;
            int color = pass == 0 ? 0x88000000 : 0xFFFFFFFF;
            for (int row = 0; row < STAR_DOTS.length; row++) {
                String line = STAR_DOTS[row];
                for (int col = 0; col < line.length(); col++) {
                    if (line.charAt(col) == 'X') {
                        int px = x + col * STAR_DOT + offset;
                        int py = y + row * STAR_DOT + offset;
                        g.fill(px, py, px + STAR_DOT, py + STAR_DOT, color);
                    }
                }
            }
        }
    }
}
*/
//?}
