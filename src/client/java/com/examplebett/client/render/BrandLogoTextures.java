package com.examplebett.client.render;
import com.examplebett.compat.ModResources;
import com.examplebett.compat.ModText;

import com.examplebett.AiObjectMod;
import com.examplebett.render.BrandLogos;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class BrandLogoTextures {

    private static final String REGISTRY_PATH = "/assets/" + AiObjectMod.MOD_ID + "/brand_logos/logos.json";

    public record Design(String id, String name, boolean sticker, boolean flat, int width, int height) {
        public Component displayName() {
            return ModText.literal(name);
        }

        public ResourceLocation texture() {
            return AiObjectMod.id("textures/entity/brand_logos/" + id + ".png");
        }

        public ResourceLocation raisedTexture() {
            return AiObjectMod.id("textures/entity/brand_logos/" + id + "_raised.png");
        }

        public ResourceLocation glintTexture() {
            return AiObjectMod.id("textures/entity/brand_logos/" + id + "_glint.png");
        }

        public ResourceLocation heightMap() {
            return AiObjectMod.id("brand_logos/" + id + ".txt");
        }
    }

    public static final List<Design> DESIGNS;

    public static final List<Design> OTHER_DESIGNS;

    static {
        List<Design> emblems = new ArrayList<>();
        List<Design> stickers = new ArrayList<>();
        for (Design design : load()) {
            (design.sticker() ? stickers : emblems).add(design);
        }

        if (emblems.isEmpty()) {
            emblems.add(new Design(BrandLogos.DEFAULT_LOGO, "Atlantis", false, false, 150, 160));
        }
        if (stickers.isEmpty()) {
            stickers.add(emblems.get(0));
        }
        DESIGNS = Collections.unmodifiableList(emblems);
        OTHER_DESIGNS = Collections.unmodifiableList(stickers);
    }

    private static List<Design> load() {
        List<List<Design>> files = new ArrayList<>();
        try {
            java.util.Enumeration<java.net.URL> urls = BrandLogoTextures.class.getClassLoader().getResources(REGISTRY_PATH.substring(1));
            while (urls.hasMoreElements()) {
                java.net.URL url = urls.nextElement();
                try (InputStream in = url.openStream()) {
                    files.add(parse(ModResources.parseJsonObject(new InputStreamReader(in, StandardCharsets.UTF_8))));
                } catch (Exception e) {
                    AiObjectMod.LOGGER.warn("failed to load brand logo registry {}", url, e);
                }
            }
        } catch (Exception e) {
            AiObjectMod.LOGGER.warn("failed to list brand logo registries {}", REGISTRY_PATH, e);
        }
        if (files.isEmpty()) {
            AiObjectMod.LOGGER.warn("brand logo registry {} not found", REGISTRY_PATH);
        }
        files.sort(java.util.Comparator.comparingInt(f -> f.stream().anyMatch(d -> d.id().equals(BrandLogos.DEFAULT_LOGO)) ? 0 : 1));
        List<Design> result = new ArrayList<>();
        java.util.Set<String> seen = new java.util.HashSet<>();
        for (List<Design> file : files) {
            for (Design design : file) {
                if (seen.add(design.id())) {
                    result.add(design);
                }
            }
        }
        return result;
    }

    private static List<Design> parse(JsonObject json) {
        List<Design> result = new ArrayList<>();
        for (JsonElement element : json.getAsJsonArray("logos")) {
            JsonObject o = element.getAsJsonObject();
            String id = o.get("id").getAsString();
            if (!BrandLogos.sanitizeLogo(id).equals(id)) {
                AiObjectMod.LOGGER.warn("brand logo registry: invalid id '{}' skipped", id);
                continue;
            }
            result.add(new Design(id,
                    o.has("name") ? o.get("name").getAsString() : id,
                    o.has("category") && "sticker".equals(o.get("category").getAsString()),
                    o.has("flat") && o.get("flat").getAsBoolean(),
                    Math.max(1, o.has("width") ? o.get("width").getAsInt() : 1),
                    Math.max(1, o.has("height") ? o.get("height").getAsInt() : 1)));
        }
        return result;
    }

    public static List<Design> designsFor(int slot) {
        return BrandLogos.isOtherSlot(slot) ? OTHER_DESIGNS : DESIGNS;
    }

    public static Design design(String id) {
        for (Design design : DESIGNS) {
            if (design.id().equals(id)) {
                return design;
            }
        }
        for (Design design : OTHER_DESIGNS) {
            if (design.id().equals(id)) {
                return design;
            }
        }
        return DESIGNS.get(0);
    }

    private BrandLogoTextures() {
    }
}
