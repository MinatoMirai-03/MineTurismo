package com.examplebett.entity;

import com.examplebett.AiObjectMod;
import com.examplebett.compat.ModResources;
import com.examplebett.render.BrandLogos;
import com.examplebett.render.TextParts;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class CarPartPresets {

    public record Preset(String textPartsRaw, String brandLogosRaw) {
    }

    private static final String PATH_PREFIX = "part_presets/";

    private static Map<String, Preset> cache;

    public static Preset get(ResourceManager resourceManager, String modelId) {
        ensureLoaded(resourceManager);
        return cache != null ? cache.get(modelId.toLowerCase(Locale.ROOT)) : null;
    }

    private static void ensureLoaded(ResourceManager resourceManager) {
        if (cache != null) return;

        if (resourceManager == null) return;
        Map<String, Preset> loaded = new HashMap<>();
        Map<ResourceLocation, ModResources.IoSupplier<InputStream>> resources =
                ModResources.listJsonResources(resourceManager, "part_presets");
        for (Map.Entry<ResourceLocation, ModResources.IoSupplier<InputStream>> entry : resources.entrySet()) {
            ResourceLocation location = entry.getKey();
            String path = location.getPath();
            if (!path.startsWith(PATH_PREFIX)) continue;
            String modelId = path.substring(PATH_PREFIX.length(), path.length() - ".json".length());
            try (InputStream stream = entry.getValue().get()) {
                JsonObject json = ModResources.parseJsonObject(new InputStreamReader(stream, StandardCharsets.UTF_8));
                loaded.put(modelId.toLowerCase(Locale.ROOT), new Preset(parseTextParts(json), parseBrandLogos(json)));
            } catch (IOException | RuntimeException e) {
                AiObjectMod.LOGGER.error("Failed to load part preset: " + location, e);
            }
        }
        cache = loaded;
    }

    private static String parseTextParts(JsonObject json) {
        if (!json.has("textParts")) {
            return "";
        }
        TextParts.Slot[] slots = new TextParts.Slot[TextParts.MAX_SLOTS];
        for (JsonElement element : json.getAsJsonArray("textParts")) {
            JsonObject o = element.getAsJsonObject();
            int slot = o.get("slot").getAsInt();
            if (slot < 0 || slot >= TextParts.MAX_SLOTS) {
                continue;
            }
            TextParts.Slot raw = new TextParts.Slot(bool(o, "enabled", true), str(o, "text", ""), str(o, "font", "gothic"),
                    Integer.parseInt(str(o, "color", "FFFFFF"), 16) & 0xFFFFFF, bool(o, "glow", false), num(o, "depth", 0f),
                    num(o, "x", 0f), num(o, "y", 0f), num(o, "z", 0f),
                    num(o, "pitch", 0f), num(o, "yaw", 0f), num(o, "roll", 0f), num(o, "scale", 1f),
                    num(o, "width", 1f), num(o, "height", 1f));
            TextParts.Line line = TextParts.decodeLine(TextParts.encodeLine(slot, raw));
            if (line != null) {
                slots[line.slot()] = line.value();
            }
        }
        return TextParts.serialize(slots);
    }

    private static String parseBrandLogos(JsonObject json) {
        if (!json.has("brandLogos")) {
            return "";
        }
        BrandLogos.Slot[] slots = new BrandLogos.Slot[BrandLogos.MAX_SLOTS];
        JsonArray array = json.getAsJsonArray("brandLogos");
        for (JsonElement element : array) {
            JsonObject o = element.getAsJsonObject();
            int slot = java.util.Arrays.asList(BrandLogos.SLOT_IDS).indexOf(str(o, "slot", ""));
            if (slot < 0) {
                continue;
            }
            BrandLogos.Slot raw = new BrandLogos.Slot(bool(o, "enabled", true), str(o, "logo", BrandLogos.DEFAULT_LOGO),
                    num(o, "x", 0f), num(o, "y", 0f), num(o, "z", 0f),
                    num(o, "pitch", 0f), num(o, "yaw", 0f), num(o, "roll", 0f),
                    num(o, "scale", 1f), num(o, "depth", BrandLogos.DEFAULT_DEPTH), bool(o, "glow", false));
            BrandLogos.Line line = BrandLogos.decodeLine(BrandLogos.encodeLine(slot, raw));
            if (line != null) {
                slots[line.slot()] = line.value();
            }
        }
        return BrandLogos.serialize(slots);
    }

    private static String str(JsonObject o, String key, String fallback) {
        return o.has(key) ? o.get(key).getAsString() : fallback;
    }

    private static float num(JsonObject o, String key, float fallback) {
        return o.has(key) ? o.get(key).getAsFloat() : fallback;
    }

    private static boolean bool(JsonObject o, String key, boolean fallback) {
        return o.has(key) ? o.get(key).getAsBoolean() : fallback;
    }

    public static void clearCache() {
        cache = null;
    }

    private CarPartPresets() {
    }
}
