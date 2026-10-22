package com.examplebett.client.render;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public final class PaintTintTexture {

    private static final int PAINT_MIN_CHANNEL = 170;
    private static final int PAINT_MAX_SPREAD = 24;

    private record BaseImage(int width, int height, int[] argbPixels, boolean[] paintMask, boolean hasPaint) {
    }

    private static final Map<ResourceLocation, BaseImage> bases = new HashMap<>();
    private static final Map<ResourceLocation, Map<Integer, ResourceLocation>> tintedCache = new HashMap<>();

    public static void clearCache() {
        bases.clear();
        tintedCache.clear();
    }

    private record ExcludedRect(int x0, int y0, int x1, int y1) {
        boolean contains(int x, int y) {
            return x >= x0 && x < x1 && y >= y0 && y < y1;
        }
    }

    private static final java.util.Set<String> FORCE_NON_TINTABLE_TEXTURES = java.util.Set.of(

            "mineturismo:textures/entity/honda_integra_type_r_db8/embedded_2.png",
            "mineturismo:textures/entity/toyota_corolla_ae86_trueno/embedded_1.png",

            "mineturismo:textures/entity/mazda_rx7_fc/embedded_2.png",
            "mineturismo:textures/entity/mazda_rx7_fc/embedded_5.png",
            "mineturismo:textures/entity/mazda_rx7_fd/embedded_3.png",

            "mineturismo:textures/entity/mazda_rx7_fc/embedded_1.png",
            "mineturismo:textures/entity/mazda_rx7_fd/embedded_5.png"
    );

    private static final java.util.Map<String, ExcludedRect> KNOWN_EXCLUDED_PIXEL_RECTS = java.util.Map.of(

            "mineturismo:textures/entity/toyota_corolla_ae86_trueno/embedded_0.png",
            new ExcludedRect(203, 94, 302, 130)
    );

    public static void register(ResourceLocation baseLocation, int[] argbPixels, int width, int height) {
        boolean[] mask = new boolean[argbPixels.length];
        boolean hasPaint = false;
        String key = baseLocation.toString();
        boolean forceNonTintable = FORCE_NON_TINTABLE_TEXTURES.contains(key);
        ExcludedRect excludedRect = KNOWN_EXCLUDED_PIXEL_RECTS.get(key);
        if (!forceNonTintable) {
            for (int i = 0; i < argbPixels.length; i++) {
                int argb = argbPixels[i];
                int alpha = (argb >>> 24) & 0xFF;
                if (alpha == 0) {
                    continue;
                }
                int r = (argb >> 16) & 0xFF;
                int g = (argb >> 8) & 0xFF;
                int b = argb & 0xFF;
                int min = Math.min(r, Math.min(g, b));
                int max = Math.max(r, Math.max(g, b));
                if (min >= PAINT_MIN_CHANNEL && (max - min) <= PAINT_MAX_SPREAD) {
                    if (excludedRect != null && excludedRect.contains(i % width, i / width)) {
                        continue;
                    }
                    mask[i] = true;
                    hasPaint = true;
                }
            }
        }
        bases.put(baseLocation, new BaseImage(width, height, argbPixels, mask, hasPaint));
    }

    public static boolean hasPaint(ResourceLocation baseLocation) {
        BaseImage base = bases.get(baseLocation);
        return base != null && base.hasPaint();
    }

    public static ResourceLocation resolve(ResourceLocation baseLocation, int color) {
        BaseImage base = bases.get(baseLocation);
        if (base == null || !base.hasPaint() || (color & 0xFFFFFF) == 0xFFFFFF) {
            return baseLocation;
        }

        Map<Integer, ResourceLocation> perColor = tintedCache.computeIfAbsent(baseLocation, k -> new HashMap<>());
        ResourceLocation cached = perColor.get(color);
        if (cached != null) {
            return cached;
        }

        String hex = Integer.toHexString(color & 0xFFFFFF);
        //? if >=1.21 {
        ResourceLocation fileCandidate = ResourceLocation.fromNamespaceAndPath(
                baseLocation.getNamespace(), stripPngExtension(baseLocation.getPath()) + "_c" + hex + ".png");
        //?} else
        /*ResourceLocation fileCandidate = new ResourceLocation(
                baseLocation.getNamespace(), stripPngExtension(baseLocation.getPath()) + "_c" + hex + ".png");*/
        if (com.examplebett.compat.ModResources.getResourceOptional(Minecraft.getInstance().getResourceManager(), fileCandidate).isPresent()) {
            perColor.put(color, fileCandidate);
            return fileCandidate;
        }

        int tintR = (color >> 16) & 0xFF;
        int tintG = (color >> 8) & 0xFF;
        int tintB = color & 0xFF;

        int width = base.width();
        int height = base.height();
        int[] source = base.argbPixels();
        boolean[] mask = base.paintMask();

        NativeImage image = new NativeImage(width, height, false);
        for (int y = 0; y < height; y++) {
            int rowOffset = y * width;
            for (int x = 0; x < width; x++) {
                int i = rowOffset + x;
                int argb = source[i];
                if (mask[i]) {
                    int alpha = (argb >>> 24) & 0xFF;
                    int r = (argb >> 16) & 0xFF;
                    int g = (argb >> 8) & 0xFF;
                    int b = argb & 0xFF;
                    r = r * tintR / 255;
                    g = g * tintG / 255;
                    b = b * tintB / 255;
                    argb = (alpha << 24) | (r << 16) | (g << 8) | b;
                }

                //? if >=1.21.2 {
                image.setPixel(x, y, argb);
                //?} else
                /*image.setPixelRGBA(x, y, (argb & 0xFF00FF00) | ((argb >> 16) & 0xFF) | ((argb & 0xFF) << 16));*/
            }
        }

        //? if >=1.21 {
        ResourceLocation location = ResourceLocation.fromNamespaceAndPath(
                baseLocation.getNamespace(), baseLocation.getPath() + "_c" + Integer.toHexString(color & 0xFFFFFF));
        //?} else
        /*ResourceLocation location = new ResourceLocation(
                baseLocation.getNamespace(), baseLocation.getPath() + "_c" + Integer.toHexString(color & 0xFFFFFF));*/

        //? if >=1.21.5 {
        DynamicTexture dynamicTexture = new DynamicTexture(() -> "mineturismo paint tint " + location, image);
        //?} else
        /*DynamicTexture dynamicTexture = new DynamicTexture(image);*/
        Minecraft.getInstance().getTextureManager().register(location, dynamicTexture);
        perColor.put(color, location);
        return location;
    }

    private static String stripPngExtension(String path) {
        return path.endsWith(".png") ? path.substring(0, path.length() - 4) : path;
    }

    private PaintTintTexture() {
    }
}
