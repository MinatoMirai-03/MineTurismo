package com.examplebett.client.gui;

import com.examplebett.item.SmartDispatchAppItem;
import net.minecraft.client.Minecraft;
//? if >=26 {
/*import com.examplebett.compat.GuiGraphics;*/
//?}
//? if >=1.20 && <26 {
import net.minecraft.client.gui.GuiGraphics;
//?}
//? if <1.20 {
/*import com.examplebett.compat.GuiGraphics;*/
//?}

enum PhoneSkin {

    CLASSIC("popphone_classic", 4, 9, 11),
    POPPHONE_3("popphone_3", 4, 13, 14),
    POPPHONE_6("popphone_6", 3, 13, 14),
    POPPHONE_18("popphone_18", 3, 4, 5);

    static final int PX = 2;

    final String id;
    final int insetX;
    final int insetTop;
    final int insetBottom;

    PhoneSkin(String id, int insetXDots, int insetTopDots, int insetBottomDots) {
        this.id = id;
        this.insetX = insetXDots * PX;
        this.insetTop = insetTopDots * PX;
        this.insetBottom = insetBottomDots * PX;
    }

    static PhoneSkin byId(String id) {
        for (PhoneSkin skin : values()) {
            if (skin.id.equals(id)) {
                return skin;
            }
        }
        return POPPHONE_6;
    }

    static PhoneSkin ofHeldPhone() {
        Minecraft mc = Minecraft.getInstance();
        return mc.player == null ? POPPHONE_6 : byId(SmartDispatchAppItem.phoneModelOf(SmartDispatchAppItem.heldPhone(mc.player)));
    }

    int[] homeRect(int x0, int x1, int sy1, int y1) {
        int w;
        int h;
        if (this == CLASSIC) {
            w = PX * 10;
            h = PX * 4;
        } else if (this == POPPHONE_18) {
            w = PX * 14;
            h = PX * 2;
        } else {
            w = PX * 8;
            h = PX * 8;
        }
        int hx0 = (x0 + x1 - w) / 2 / PX * PX;
        int hy0 = sy1 + (y1 - sy1 - h) / 2 / PX * PX;
        return new int[]{hx0, hy0, hx0 + w, hy0 + h};
    }

    void drawBody(GuiGraphics g, int x0, int y0, int x1, int y1, int sx0, int sy0, int sx1, int sy1, int[] home) {
        int cx = (x0 + x1) / 2 / PX * PX;
        switch (this) {
            case POPPHONE_3 -> {

                sideButtons(g, x0, y0, 0xFF8E929A);
                fillRoundedLayers(g, x0, y0, x1, y1, PX * 6, 0xFF8E929A, 0xFFD3D6DC, 0xFF151517);
                g.fill(x0 + PX * 7, y0 + PX * 3, x0 + PX * 14, y0 + PX * 4, 0xFF26262A);
                g.fill(x0 + PX * 3, y0 + PX * 7, x0 + PX * 4, y0 + PX * 14, 0xFF26262A);

                int ey = y0 + PX * 6;
                SmartDispatchAppScreen.fillPixelRounded(g, cx - PX * 6, ey - PX, cx + PX * 6, ey + PX * 2, 1, 0xFF0A0A0B);
                g.fill(cx - PX * 5, ey, cx + PX * 5, ey + PX, 0xFF46464C);
                g.fill(sx0 - PX, sy0 - PX, sx1 + PX, sy1 + PX, 0xFF0A0A0B);

                int hcx = (home[0] + home[2]) / 2;
                int hcy = (home[1] + home[3]) / 2;
                int r = (home[2] - home[0]) / 2;
                SmartDispatchAppScreen.fillPixelCircle(g, hcx, hcy, r, 0xFF34343A);
                SmartDispatchAppScreen.fillPixelCircle(g, hcx, hcy, r - PX, 0xFF0D0D0F);
                int q = PX * 2;
                g.fill(hcx - q, hcy - q, hcx + q, hcy - q + 1, 0xFF9A9AA2);
                g.fill(hcx - q, hcy + q - 1, hcx + q, hcy + q, 0xFF9A9AA2);
                g.fill(hcx - q, hcy - q, hcx - q + 1, hcy + q, 0xFF9A9AA2);
                g.fill(hcx + q - 1, hcy - q, hcx + q, hcy + q, 0xFF9A9AA2);
            }
            case POPPHONE_6 -> {

                sideButtons(g, x0, y0, 0xFFD9A79C);
                fillRoundedLayers(g, x0, y0, x1, y1, PX * 6, 0xFFD9A79C, 0xFFF8F8FB);
                int ey = y0 + PX * 6;
                g.fill(cx - PX, y0 + PX * 3, cx, y0 + PX * 4, 0xFF3A3F55);
                g.fill(cx - PX * 10, ey - PX, cx - PX * 8, ey + PX, 0xFF3A3F55);
                SmartDispatchAppScreen.fillPixelRounded(g, cx - PX * 5, ey - 1, cx + PX * 5, ey + PX, 1, 0xFFC9CAD2);
                g.fill(sx0 - 1, sy0 - 1, sx1 + 1, sy1 + 1, 0xFF1A1A1E);

                int hcx = (home[0] + home[2]) / 2;
                int hcy = (home[1] + home[3]) / 2;
                int r = (home[2] - home[0]) / 2;
                SmartDispatchAppScreen.fillPixelCircle(g, hcx, hcy, r, 0xFFD9A79C);
                SmartDispatchAppScreen.fillPixelCircle(g, hcx, hcy, r - PX, 0xFFF8F8FB);
            }
            case POPPHONE_18 -> {

                sideButtons(g, x0, y0, 0xFF8E98AA);
                fillRoundedLayers(g, x0, y0, x1, y1, PX * 7, 0xFF7C8698, 0xFFBFC7D4, 0xFF060607);
                SmartDispatchAppScreen.fillPixelRounded(g, home[0], home[1], home[2], home[3], 1, 0xFFB4B9C4);
            }
            default -> {
                SmartDispatchAppScreen.fillPixelRounded(g, x0, y0, x1, y1, PX * 2, 0xFF121216);
                SmartDispatchAppScreen.fillPixelRounded(g, x0 + PX, y0 + PX, x1 - PX, y1 - PX, PX * 2, 0xFF34363E);

                g.fill(x0 + PX * 3, y0 + PX * 2, x0 + PX * 8, y0 + PX * 3, 0xFF4A4D58);
                g.fill(x0 + PX * 2, y0 + PX * 3, x0 + PX * 3, y0 + PX * 8, 0xFF4A4D58);

                int speakerY = y0 + PX * 4;
                g.fill(cx - PX * 6, speakerY, cx + PX * 6, speakerY + PX, 0xFF1C1D22);
                g.fill(cx + PX * 9, speakerY, cx + PX * 10, speakerY + PX, 0xFF1C1D22);
                g.fill(sx0 - PX, sy0 - PX, sx1 + PX, sy1 + PX, 0xFF121216);
                SmartDispatchAppScreen.fillPixelRounded(g, home[0], home[1], home[2], home[3], PX, 0xFF787C88);
            }
        }
    }

    void drawScreenOverlay(GuiGraphics g, int sx0, int sy0, int sx1) {
        if (this == POPPHONE_18) {
            int cx = (sx0 + sx1) / 2 / PX * PX;
            SmartDispatchAppScreen.fillPixelRounded(g, cx - PX * 7, sy0 + 1, cx + PX * 7, sy0 + 1 + PX * 4, 1, 0xFF000000);
        }
    }

    static void fillRoundedLayers(GuiGraphics g, int x0, int y0, int x1, int y1, int radius, int... colors) {
        int rows = (y1 - y0 + PX - 1) / PX;
        int[] prev = null;
        for (int k = 0; k < colors.length; k++) {
            int off = k * PX;
            int r = Math.max(PX, radius - off);
            int[] inset = new int[rows];
            java.util.Arrays.fill(inset, Integer.MAX_VALUE);
            for (int row = k; row < rows - k; row++) {
                int y = y0 + row * PX;
                double cy = y + PX / 2.0;
                double d = Math.min(cy - (y0 + off), (y1 - off) - cy);
                int corner = 0;
                if (d < r) {
                    double t = r - d;
                    corner = (int) Math.round((r - Math.sqrt(Math.max(0.0, (double) r * r - t * t))) / PX) * PX;
                }
                int value = off + corner;
                if (prev != null) {
                    for (int n = row - 1; n <= row + 1; n++) {
                        if (n >= 0 && n < rows && prev[n] != Integer.MAX_VALUE) {
                            value = Math.max(value, prev[n] + PX);
                        }
                    }
                }
                inset[row] = value;
                if (x0 + value < x1 - value) {
                    g.fill(x0 + value, y, x1 - value, Math.min(y + PX, y1 - off), colors[k]);
                }
            }
            prev = inset;
        }
    }

    private static void sideButtons(GuiGraphics g, int x0, int y0, int color) {
        g.fill(x0 - PX, y0 + PX * 18, x0, y0 + PX * 24, color);
        g.fill(x0 - PX, y0 + PX * 27, x0, y0 + PX * 33, color);
    }
}
