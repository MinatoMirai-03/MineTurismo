package com.examplebett.render;

import java.util.LinkedHashMap;
import java.util.Map;

public final class PartTransforms {

    private PartTransforms() {
    }

    public record Transform12(float x, float y, float z, float angle, float angleY, float scale,
                               float stretchXPos, float stretchXNeg, float stretchYPos, float stretchYNeg,
                               float stretchZPos, float stretchZNeg) {
    }

    public record Transform6(float x, float y, float z, float angle, float angleY, float scale) {
    }

    private static Map<String, String> parseEntries(String serialized) {
        Map<String, String> entries = new LinkedHashMap<>();
        if (serialized == null || serialized.isEmpty()) {
            return entries;
        }
        for (String entry : serialized.split(";")) {
            int colon = entry.indexOf(':');
            if (colon < 0) {
                continue;
            }
            entries.put(entry.substring(0, colon), entry.substring(colon + 1));
        }
        return entries;
    }

    private static String serializeEntries(Map<String, String> entries) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> e : entries.entrySet()) {
            if (sb.length() > 0) {
                sb.append(';');
            }
            sb.append(e.getKey()).append(':').append(e.getValue());
        }
        return sb.toString();
    }

    private static float[] parseFloats(String csv, int expected) {
        String[] parts = csv.split(",", -1);
        float[] values = new float[expected];
        for (int i = 0; i < expected && i < parts.length; i++) {
            try {
                values[i] = Float.parseFloat(parts[i]);
            } catch (NumberFormatException ignored) {

            }
        }
        return values;
    }

    public static Transform12 resolve12(String serialized, String partId, Transform12 defaults) {
        String raw = parseEntries(serialized).get(partId);
        if (raw == null) {
            return defaults;
        }
        float[] v = parseFloats(raw, 12);
        return new Transform12(v[0], v[1], v[2], v[3], v[4], v[5], v[6], v[7], v[8], v[9], v[10], v[11]);
    }

    public static String put12(String serialized, String partId, Transform12 t) {
        Map<String, String> entries = parseEntries(serialized);
        entries.put(partId, t.x() + "," + t.y() + "," + t.z() + "," + t.angle() + "," + t.angleY() + "," + t.scale() + ","
                + t.stretchXPos() + "," + t.stretchXNeg() + "," + t.stretchYPos() + "," + t.stretchYNeg() + ","
                + t.stretchZPos() + "," + t.stretchZNeg());
        return serializeEntries(entries);
    }

    public static Transform6 resolve6(String serialized, String partId, Transform6 defaults) {
        String raw = parseEntries(serialized).get(partId);
        if (raw == null) {
            return defaults;
        }
        float[] v = parseFloats(raw, 6);
        return new Transform6(v[0], v[1], v[2], v[3], v[4], v[5]);
    }

    public static String put6(String serialized, String partId, Transform6 t) {
        Map<String, String> entries = parseEntries(serialized);
        entries.put(partId, t.x() + "," + t.y() + "," + t.z() + "," + t.angle() + "," + t.angleY() + "," + t.scale());
        return serializeEntries(entries);
    }
}
