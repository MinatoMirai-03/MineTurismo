package com.examplebett.entity;

//? if >=1.19 {
import net.minecraft.util.RandomSource;
//?}
import net.minecraft.world.item.DyeColor;

import java.util.ArrayList;
import java.util.List;

public final class TrafficColorPool {

    private static final DyeColor[] COMMON = {
            DyeColor.WHITE, DyeColor.LIGHT_GRAY, DyeColor.GRAY, DyeColor.BLACK
    };

    private static final DyeColor[] RARE = buildRare();

    private static final double RARE_WEIGHT = 0.13;

    private static DyeColor[] buildRare() {
        List<DyeColor> rare = new ArrayList<>();
        for (DyeColor color : DyeColor.values()) {
            boolean isCommon = false;
            for (DyeColor common : COMMON) {
                if (common == color) {
                    isCommon = true;
                    break;
                }
            }
            if (!isCommon) {
                rare.add(color);
            }
        }
        return rare.toArray(new DyeColor[0]);
    }

    //? if >=1.19 {
    public static int pickRandomRgb(RandomSource random) {
    //?} else
    /*public static int pickRandomRgb(java.util.Random random) {*/
        double totalWeight = COMMON.length * 1.0 + RARE.length * RARE_WEIGHT;
        double roll = random.nextDouble() * totalWeight;
        double cumulative = 0.0;
        for (DyeColor color : COMMON) {
            cumulative += 1.0;
            if (roll < cumulative) {
                return diffuseRgb(color);
            }
        }
        for (DyeColor color : RARE) {
            cumulative += RARE_WEIGHT;
            if (roll < cumulative) {
                return diffuseRgb(color);
            }
        }
        return diffuseRgb(DyeColor.WHITE);
    }

    private static int diffuseRgb(DyeColor color) {
        return com.examplebett.client.gui.CarColorPalette.dyeRgb(color.getName(), vanillaDiffuseRgb(color));
    }

    private static int vanillaDiffuseRgb(DyeColor color) {
        //? if >=1.21 {
        return color.getTextureDiffuseColor();
        //?} else {
        /*float[] rgb = color.getTextureDiffuseColors();
        return ((int) (rgb[0] * 255) << 16) | ((int) (rgb[1] * 255) << 8) | (int) (rgb[2] * 255);
        *///?}
    }

    private TrafficColorPool() {
    }
}
