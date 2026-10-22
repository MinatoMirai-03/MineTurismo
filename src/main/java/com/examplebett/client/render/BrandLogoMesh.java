//? if >=1.18 {
//?}
//? if <1.18 {
/*
package com.examplebett.client.render;

import com.examplebett.AiObjectMod;
import com.examplebett.compat.ModResources;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class BrandLogoMesh {

    public record Wall(float x0, float y0, float x1, float y1, int lowLevel, int highLevel,
                       float u0, float v0, float u1, float v1, float nx, float ny) {
    }

    public record Mesh(int width, int height, List<Wall> walls) {
    }

    private static final Map<String, Mesh> CACHE = new HashMap<>();
    private static final Mesh EMPTY = new Mesh(1, 1, List.of());

    private BrandLogoMesh() {
    }

    public static Mesh get(BrandLogoTextures.Design design) {
        return CACHE.computeIfAbsent(design.id(), id -> load(design.heightMap()));
    }

    private static Mesh load(ResourceLocation location) {
        int[][] levels;
        try {
            Optional<net.minecraft.server.packs.resources.Resource> resource =
                    ModResources.getResourceOptional(Minecraft.getInstance().getResourceManager(), location);
            if (!resource.isPresent()) {
                AiObjectMod.LOGGER.warn("brand logo height map not found: {}", location);
                return EMPTY;
            }
            try (InputStream in = ModResources.open(resource.get());
                 BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                String[] size = reader.readLine().trim().split(" ");
                int w = Integer.parseInt(size[0]);
                int h = Integer.parseInt(size[1]);
                levels = new int[h][w];
                for (int y = 0; y < h; y++) {
                    String row = reader.readLine();
                    for (int x = 0; x < w && row != null && x < row.length(); x++) {
                        levels[y][x] = Math.max(0, Math.min(2, row.charAt(x) - '0'));
                    }
                }
            }
        } catch (Exception e) {
            AiObjectMod.LOGGER.warn("failed to load brand logo height map {}", location, e);
            return EMPTY;
        }
        return build(levels);
    }

    private static int level(int[][] levels, int x, int y) {
        if (y < 0 || y >= levels.length || x < 0 || x >= levels[0].length) {
            return 0;
        }
        return levels[y][x];
    }

    private static Mesh build(int[][] levels) {
        int h = levels.length;
        int w = levels[0].length;
        List<Wall> walls = new ArrayList<>();

        for (int y = 0; y <= h; y++) {
            int x = 0;
            while (x < w) {
                int above = level(levels, x, y - 1);
                int below = level(levels, x, y);
                if (above == below) {
                    x++;
                    continue;
                }
                int start = x;
                while (x < w && level(levels, x, y - 1) == above && level(levels, x, y) == below) {
                    x++;
                }

                boolean aboveHigher = above > below;
                float v = ((aboveHigher ? y - 1 : y) + 0.5f) / h;
                walls.add(new Wall(start, y, x, y, Math.min(above, below), Math.max(above, below),
                        (float) start / w, v, (float) x / w, v, 0f, aboveHigher ? 1f : -1f));
            }
        }

        for (int x = 0; x <= w; x++) {
            int y = 0;
            while (y < h) {
                int left = level(levels, x - 1, y);
                int right = level(levels, x, y);
                if (left == right) {
                    y++;
                    continue;
                }
                int start = y;
                while (y < h && level(levels, x - 1, y) == left && level(levels, x, y) == right) {
                    y++;
                }
                boolean leftHigher = left > right;
                float u = ((leftHigher ? x - 1 : x) + 0.5f) / w;
                walls.add(new Wall(x, start, x, y, Math.min(left, right), Math.max(left, right),
                        u, (float) start / h, u, (float) y / h, leftHigher ? 1f : -1f, 0f));
            }
        }
        return new Mesh(w, h, walls);
    }
}
*/
//?}
