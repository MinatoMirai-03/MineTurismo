package com.examplebett.render;

import com.examplebett.AiObjectMod;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class AddonPlateDesigns {

    private static final String REGISTRY_PATH = "assets/" + AiObjectMod.MOD_ID + "/plates/plates.json";
    public static final float DEFAULT_ASPECT = 2f;

    public record Entry(String id, boolean european, float aspect) {
    }

    private static List<Entry> entries;

    private AddonPlateDesigns() {
    }

    public static synchronized List<Entry> entries() {
        if (entries == null) {
            entries = Collections.unmodifiableList(load());
        }
        return entries;
    }

    public static boolean isEuropean(String id) {
        for (Entry entry : entries()) {
            if (entry.id().equals(id)) {
                return entry.european();
            }
        }
        return false;
    }

    private static List<Entry> load() {
        List<Entry> result = new ArrayList<>();
        Set<String> seen = new LinkedHashSet<>();
        try {
            Enumeration<URL> urls = AddonPlateDesigns.class.getClassLoader().getResources(REGISTRY_PATH);
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                try (InputStream in = url.openStream()) {
                    JsonObject json = com.examplebett.compat.ModResources.parseJsonObject(new InputStreamReader(in, StandardCharsets.UTF_8));
                    for (JsonElement element : json.getAsJsonArray("plates")) {
                        JsonObject o = element.getAsJsonObject();
                        String id = o.get("id").getAsString();
                        if (!isValidId(id) || !seen.add(id)) {
                            continue;
                        }
                        float aspect = o.has("aspect") ? o.get("aspect").getAsFloat() : DEFAULT_ASPECT;
                        result.add(new Entry(id, o.has("european") && o.get("european").getAsBoolean(),
                                Float.isFinite(aspect) && aspect > 0.1f ? aspect : DEFAULT_ASPECT));
                    }
                } catch (Exception e) {
                    AiObjectMod.LOGGER.warn("failed to read plate registry {}", url, e);
                }
            }
        } catch (Exception e) {
            AiObjectMod.LOGGER.warn("failed to list plate registries {}", REGISTRY_PATH, e);
        }
        return result;
    }

    private static boolean isValidId(String id) {
        if (id == null || id.isEmpty() || id.length() > 64) {
            return false;
        }
        for (int i = 0; i < id.length(); i++) {
            char c = id.charAt(i);
            if (!((c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || c == '_')) {
                return false;
            }
        }
        return true;
    }
}
