package com.examplebett.client.gui;

public final class CarColorPalette {

    private static final String BROWN_DYE_NAME = "brown";
    private static final int BROWN_RGB = 0x5E402C;

    public static final String[] EXTRA_IDS = {
            "dark_gray", "silver", "cream", "bronze",
            "dark_red", "dark_blue", "dark_brown", "dark_green"
    };
    public static final int[] EXTRA_RGB = {
            0x32363A, 0x4D4D4D, 0xEFE3C2, 0xA0703F,
            0x582423, 0x1B2D5A, 0x372B25, 0x37431D
    };

    public static final String[] METAL_IDS = {"chrome", "gold", "metallic_silver"};
    public static final int[] METAL_RGB = {0xD8DCE0, 0xD4AF37, 0xB4B8BC};
    public static final int METAL_CHROME = 0;
    public static final int METAL_GOLD = 1;
    public static final int METAL_METALLIC_SILVER = 2;

    public static final int EXTRA_BRONZE = 3;

    public static String translationKey(int extraIndex) {
        return "color.mineturismo." + EXTRA_IDS[extraIndex];
    }

    public static String metalTranslationKey(int metalIndex) {
        return "color.mineturismo." + METAL_IDS[metalIndex];
    }

    public static int metalIndex(int rgb) {
        int masked = rgb & 0xFFFFFF;
        for (int i = 0; i < METAL_RGB.length; i++) {
            if (METAL_RGB[i] == masked) {
                return i;
            }
        }
        return -1;
    }

    public static int dyeRgb(String dyeName, int vanillaRgb) {
        return BROWN_DYE_NAME.equals(dyeName) ? BROWN_RGB : vanillaRgb;
    }

    private CarColorPalette() {
    }
}
