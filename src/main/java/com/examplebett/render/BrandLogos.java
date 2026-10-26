package com.examplebett.render;

public final class BrandLogos {

    public static final int SLOT_FRONT = 0;
    public static final int SLOT_REAR = 1;

    public static final int FIRST_OTHER_SLOT = 2;
    public static final int OTHER_SLOT_COUNT = 5;
    public static final int MAX_SLOTS = FIRST_OTHER_SLOT + OTHER_SLOT_COUNT;

    public static final String[] SLOT_IDS = {"front", "rear", "other_1", "other_2", "other_3", "other_4", "other_5"};

    public static final String DEFAULT_LOGO = "atlantis";
    public static final String DEFAULT_OTHER_LOGO = "mines_car_share";

    public static boolean isOtherSlot(int slot) {
        return slot >= FIRST_OTHER_SLOT;
    }
    public static final int MAX_LOGO_ID_LENGTH = 32;
    public static final float MIN_SCALE = 0.1f;
    public static final float MAX_SCALE = 10f;
    public static final float MIN_DEPTH = 0.002f;
    public static final float MAX_DEPTH = 0.2f;
    public static final float DEFAULT_DEPTH = 0.01f;

    public record Slot(boolean enabled, String logo, float x, float y, float z,
                       float pitch, float yaw, float roll, float scale, float depth, boolean glow) {

        public Slot withEnabled(boolean v) {
            return new Slot(v, logo, x, y, z, pitch, yaw, roll, scale, depth, glow);
        }

        public Slot withLogo(String v) {
            return new Slot(enabled, sanitizeLogo(v), x, y, z, pitch, yaw, roll, scale, depth, glow);
        }

        public Slot withPosition(float nx, float ny, float nz) {
            return new Slot(enabled, logo, nx, ny, nz, pitch, yaw, roll, scale, depth, glow);
        }

        public Slot withRotation(float nPitch, float nYaw, float nRoll) {
            return new Slot(enabled, logo, x, y, z, nPitch, nYaw, nRoll, scale, depth, glow);
        }

        public Slot withScale(float v) {
            return new Slot(enabled, logo, x, y, z, pitch, yaw, roll, clamp(v, MIN_SCALE, MAX_SCALE), depth, glow);
        }

        public Slot withGlow(boolean v) {
            return new Slot(enabled, logo, x, y, z, pitch, yaw, roll, scale, depth, v);
        }

        public Slot withDepth(float v) {
            return new Slot(enabled, logo, x, y, z, pitch, yaw, roll, scale, clamp(v, MIN_DEPTH, MAX_DEPTH), glow);
        }
    }

    private BrandLogos() {
    }

    private static float clamp(float v, float min, float max) {
        if (Float.isNaN(v)) {
            return min;
        }
        return Math.max(min, Math.min(max, v));
    }

    public static String sanitizeLogo(String id) {
        if (id == null || id.isEmpty() || id.length() > MAX_LOGO_ID_LENGTH) {
            return DEFAULT_LOGO;
        }
        for (int i = 0; i < id.length(); i++) {
            char c = id.charAt(i);
            if (!((c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || c == '_')) {
                return DEFAULT_LOGO;
            }
        }
        return id;
    }

    public static String encodeLine(int slot, Slot s) {
        return slot + "\t" + (s.enabled() ? 1 : 0) + "\t" + s.logo()
                + "\t" + s.x() + "\t" + s.y() + "\t" + s.z()
                + "\t" + s.pitch() + "\t" + s.yaw() + "\t" + s.roll()
                + "\t" + s.scale() + "\t" + s.depth() + "\t" + (s.glow() ? 1 : 0);
    }

    public record Line(int slot, Slot value) {
    }

    public static Line decodeLine(String line) {
        if (line == null) {
            return null;
        }
        String[] f = line.split("\t");
        if (f.length < 11) {
            return null;
        }
        try {
            int slot = Integer.parseInt(f[0]);
            if (slot < 0 || slot >= MAX_SLOTS) {
                return null;
            }
            Slot s = new Slot("1".equals(f[1]), sanitizeLogo(f[2]),
                    parseFloat(f[3]), parseFloat(f[4]), parseFloat(f[5]),
                    parseFloat(f[6]), parseFloat(f[7]), parseFloat(f[8]),
                    clamp(parseFloat(f[9]), MIN_SCALE, MAX_SCALE), clamp(parseFloat(f[10]), MIN_DEPTH, MAX_DEPTH),

                    f.length > 11 && "1".equals(f[11]));
            return new Line(slot, s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static float parseFloat(String s) {
        float v = Float.parseFloat(s);
        return Float.isFinite(v) ? v : 0f;
    }

    public static Slot[] parse(String serialized) {
        Slot[] slots = new Slot[MAX_SLOTS];
        if (serialized == null || serialized.isEmpty()) {
            return slots;
        }
        for (String line : serialized.split("\n")) {
            Line parsed = decodeLine(line);
            if (parsed != null) {
                slots[parsed.slot()] = parsed.value();
            }
        }
        return slots;
    }

    public static String serialize(Slot[] slots) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < slots.length && i < MAX_SLOTS; i++) {
            if (slots[i] == null) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append('\n');
            }
            sb.append(encodeLine(i, slots[i]));
        }
        return sb.toString();
    }

    public static String put(String serialized, int slot, Slot value) {
        Slot[] slots = parse(serialized);
        slots[slot] = value;
        return serialize(slots);
    }
}
