package com.examplebett.render;

import net.minecraft.resources.ResourceLocation;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class TextureTintAnalyzer {

    private static final int WHITE_MIN_CHANNEL = 200;

    private static final int WHITE_MAX_SPREAD = 20;

    private static final Map<ResourceLocation, Boolean> cache = new HashMap<>();

    public static void clearCache() {
        cache.clear();
    }

    public static boolean isWhiteTexture(ResourceLocation key, byte[] imageBytes) {
        Boolean cached = cache.get(key);
        if (cached != null) {
            return cached;
        }
        boolean result = analyze(imageBytes);
        cache.put(key, result);
        return result;
    }

    private static boolean analyze(byte[] imageBytes) {
        BufferedImage image;
        try {
            image = ImageIO.read(new ByteArrayInputStream(imageBytes));
        } catch (IOException e) {
            return false;
        }
        if (image == null) {
            return false;
        }

        int width = image.getWidth();
        int height = image.getHeight();
        int[] pixels = image.getRGB(0, 0, width, height, null, 0, width);

        long sumR = 0, sumG = 0, sumB = 0, count = 0;
        for (int argb : pixels) {
            int alpha = (argb >>> 24) & 0xFF;
            if (alpha == 0) {
                continue;
            }
            sumR += (argb >> 16) & 0xFF;
            sumG += (argb >> 8) & 0xFF;
            sumB += argb & 0xFF;
            count++;
        }
        if (count == 0) {
            return false;
        }

        double avgR = (double) sumR / count;
        double avgG = (double) sumG / count;
        double avgB = (double) sumB / count;
        double min = Math.min(avgR, Math.min(avgG, avgB));
        double max = Math.max(avgR, Math.max(avgG, avgB));
        return min >= WHITE_MIN_CHANNEL && (max - min) <= WHITE_MAX_SPREAD;
    }

    private TextureTintAnalyzer() {
    }
}
