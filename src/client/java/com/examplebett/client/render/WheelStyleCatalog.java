package com.examplebett.client.render;

import com.examplebett.AiObjectMod;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
//? if >=1.19.3 {
import net.minecraft.resources.FileToIdConverter;
//?}
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class WheelStyleCatalog {

    //? if >=1.19.3 {
    private static final FileToIdConverter MARKERS = new FileToIdConverter("models", ".wheel_model.json");
    //?}

    private static List<String> cache = null;
    private static final Map<String, Float> defaultWidthScaleCache = new HashMap<>();

    private WheelStyleCatalog() {
    }

    public static float getDefaultWheelWidthScale(ResourceManager resourceManager, String wheelId) {
        return defaultWidthScaleCache.computeIfAbsent(wheelId, id -> loadDefaultWheelWidthScale(resourceManager, id));
    }

    private static float loadDefaultWheelWidthScale(ResourceManager resourceManager, String wheelId) {
        ResourceLocation location = AiObjectMod.id("models/" + wheelId + ".wheel_model.json");
        Optional<Resource> resource = com.examplebett.compat.ModResources.getResourceOptional(resourceManager, location);
        if (resource.isEmpty()) {
            return 1f;
        }
        try (InputStream stream = com.examplebett.compat.ModResources.open(resource.get())) {
            String text = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            JsonObject json = JsonParser.parseString(text).getAsJsonObject();
            return json.has("defaultWheelWidthScale") ? json.get("defaultWheelWidthScale").getAsFloat() : 1f;
        } catch (IOException | RuntimeException e) {
            AiObjectMod.LOGGER.error("Failed to load wheel style defaults: " + wheelId, e);
            return 1f;
        }
    }

    public static List<String> list(ResourceManager resourceManager) {
        if (cache == null) {
            List<ResourceLocation> files = new ArrayList<>();

            //? if >=1.19.3 {
            files.addAll(MARKERS.listMatchingResources(resourceManager).keySet());
            //?}
            //? if >=1.19 && <1.19.3 {
            /*files.addAll(resourceManager.listResources("models",
                    loc -> loc.getPath().endsWith(".wheel_model.json")).keySet());*/
            //?}
            //? if <1.19 {
            /*files.addAll(resourceManager.listResources("models",
                    path -> path.endsWith(".wheel_model.json")));*/
            //?}
            List<String> ids = new ArrayList<>();
            for (ResourceLocation loc : files) {
                String path = loc.getPath();

                int slash = path.lastIndexOf('/');
                String fileName = slash >= 0 ? path.substring(slash + 1) : path;
                ids.add(fileName.substring(0, fileName.length() - ".wheel_model.json".length()));
            }
            ids.sort(String::compareTo);
            cache = ids;
        }
        return cache;
    }

    public static void clearCache() {
        cache = null;
        defaultWidthScaleCache.clear();
    }
}
