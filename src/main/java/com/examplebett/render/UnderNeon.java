package com.examplebett.render;

public final class UnderNeon {

    public static final int SLOT_FRONT = 0;
    public static final int SLOT_REAR = 1;
    public static final int SLOT_LEFT = 2;
    public static final int SLOT_RIGHT = 3;
    public static final int MAX_SLOTS = 4;

    public static final String[] SLOT_IDS = {"front", "rear", "left", "right"};

    public static final float MIN_SIZE = 0.1f;
    public static final float MAX_SIZE = 10f;
    public static final float MIN_BRIGHTNESS = 0.1f;
    public static final float MAX_BRIGHTNESS = 1f;

    public static final int DEFAULT_COLOR = 0x3AB3DA;

    public record Slot(boolean enabled, int color, float brightness,
                       float x, float y, float z, float yaw, float width, float length) {

        public Slot withEnabled(boolean v) {
            return new Slot(v, color, brightness, x, y, z, yaw, width, length);
        }

        public Slot withColor(int v) {
            return new Slot(enabled, v & 0xFFFFFF, brightness, x, y, z, yaw, width, length);
        }

        public Slot withBrightness(float v) {
            return new Slot(enabled, color, clamp(v, MIN_BRIGHTNESS, MAX_BRIGHTNESS), x, y, z, yaw, width, length);
        }

        public Slot withPosition(float nx, float ny, float nz) {
            return new Slot(enabled, color, brightness, nx, ny, nz, yaw, width, length);
        }

        public Slot withYaw(float v) {
            return new Slot(enabled, color, brightness, x, y, z, v, width, length);
        }

        public Slot withSize(float nWidth, float nLength) {
            return new Slot(enabled, color, brightness, x, y, z, yaw,
                    clamp(nWidth, MIN_SIZE, MAX_SIZE), clamp(nLength, MIN_SIZE, MAX_SIZE));
        }
    }

    private UnderNeon() {
    }

    private static float clamp(float v, float min, float max) {
        if (Float.isNaN(v)) {
            return min;
        }
        return Math.max(min, Math.min(max, v));
    }

    public static String encodeLine(int slot, Slot s) {
        return slot + "\t" + (s.enabled() ? 1 : 0) + "\t" + Integer.toHexString(s.color() & 0xFFFFFF)
                + "\t" + s.brightness() + "\t" + s.x() + "\t" + s.y() + "\t" + s.z()
                + "\t" + s.yaw() + "\t" + s.width() + "\t" + s.length();
    }

    public record Line(int slot, Slot value) {
    }

    public static Line decodeLine(String line) {
        if (line == null) {
            return null;
        }
        String[] f = line.split("\t");
        if (f.length < 10) {
            return null;
        }
        try {
            int slot = Integer.parseInt(f[0]);
            if (slot < 0 || slot >= MAX_SLOTS) {
                return null;
            }
            Slot s = new Slot("1".equals(f[1]), Integer.parseInt(f[2], 16) & 0xFFFFFF,
                    clamp(parseFloat(f[3]), MIN_BRIGHTNESS, MAX_BRIGHTNESS),
                    parseFloat(f[4]), parseFloat(f[5]), parseFloat(f[6]), parseFloat(f[7]),
                    clamp(parseFloat(f[8]), MIN_SIZE, MAX_SIZE), clamp(parseFloat(f[9]), MIN_SIZE, MAX_SIZE));
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
