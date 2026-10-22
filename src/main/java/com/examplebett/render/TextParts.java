package com.examplebett.render;

public final class TextParts {

    public static final int MAX_SLOTS = 15;
    public static final int MAX_TEXT_LENGTH = 32;

    public static final String FONT_DEFAULT = "default";
    public static final String[] FONT_IDS = {FONT_DEFAULT, "racing", "script", "block", "gothic"};

    public static final float MIN_SCALE = 0.05f;
    public static final float MAX_SCALE = 20f;
    public static final float MAX_DEPTH = 0.2f;

    public static final float MIN_STRETCH = 0.1f;
    public static final float MAX_STRETCH = 5f;

    public static final int DEFAULT_COLOR = 0xF9FFFE;

    public record Slot(boolean enabled, String text, String font, int color, boolean glow, float depth,
                       float x, float y, float z, float pitch, float yaw, float roll, float scale,
                       float width, float height) {

        public Slot withEnabled(boolean v) {
            return new Slot(v, text, font, color, glow, depth, x, y, z, pitch, yaw, roll, scale, width, height);
        }

        public Slot withText(String v) {
            return new Slot(enabled, sanitizeText(v), font, color, glow, depth, x, y, z, pitch, yaw, roll, scale, width, height);
        }

        public Slot withFont(String v) {
            return new Slot(enabled, text, sanitizeFont(v), color, glow, depth, x, y, z, pitch, yaw, roll, scale, width, height);
        }

        public Slot withColor(int v) {
            return new Slot(enabled, text, font, v & 0xFFFFFF, glow, depth, x, y, z, pitch, yaw, roll, scale, width, height);
        }

        public Slot withGlow(boolean v) {
            return new Slot(enabled, text, font, color, v, depth, x, y, z, pitch, yaw, roll, scale, width, height);
        }

        public Slot withDepth(float v) {
            return new Slot(enabled, text, font, color, glow, clamp(v, 0f, MAX_DEPTH), x, y, z, pitch, yaw, roll, scale, width, height);
        }

        public Slot withPosition(float nx, float ny, float nz) {
            return new Slot(enabled, text, font, color, glow, depth, nx, ny, nz, pitch, yaw, roll, scale, width, height);
        }

        public Slot withRotation(float nPitch, float nYaw, float nRoll) {
            return new Slot(enabled, text, font, color, glow, depth, x, y, z, nPitch, nYaw, nRoll, scale, width, height);
        }

        public Slot withScale(float v) {
            return new Slot(enabled, text, font, color, glow, depth, x, y, z, pitch, yaw, roll, clamp(v, MIN_SCALE, MAX_SCALE), width, height);
        }

        public Slot withStretch(float nWidth, float nHeight) {
            return new Slot(enabled, text, font, color, glow, depth, x, y, z, pitch, yaw, roll, scale,
                    clamp(nWidth, MIN_STRETCH, MAX_STRETCH), clamp(nHeight, MIN_STRETCH, MAX_STRETCH));
        }

        public boolean visible() {
            return enabled && !text.trim().isEmpty();
        }
    }

    private TextParts() {
    }

    private static float clamp(float v, float min, float max) {
        if (Float.isNaN(v)) {
            return min;
        }
        return Math.max(min, Math.min(max, v));
    }

    public static String sanitizeText(String text) {
        if (text == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder(Math.min(text.length(), MAX_TEXT_LENGTH));
        for (int i = 0; i < text.length() && sb.length() < MAX_TEXT_LENGTH; i++) {
            char c = text.charAt(i);
            if (c < 0x20 || c == 0x7F || c == '§') {
                continue;
            }
            sb.append(c);
        }

        int len = sb.length();
        if (len > 0 && Character.isHighSurrogate(sb.charAt(len - 1))) {
            sb.setLength(len - 1);
        }
        return sb.toString();
    }

    public static String sanitizeFont(String font) {
        for (String id : FONT_IDS) {
            if (id.equals(font)) {
                return id;
            }
        }
        return FONT_DEFAULT;
    }

    public static int fontIndex(String font) {
        for (int i = 0; i < FONT_IDS.length; i++) {
            if (FONT_IDS[i].equals(font)) {
                return i;
            }
        }
        return 0;
    }

    public static String encodeLine(int slot, Slot s) {
        return slot + "\t" + (s.enabled() ? 1 : 0) + "\t" + s.font() + "\t" + Integer.toHexString(s.color() & 0xFFFFFF)
                + "\t" + (s.glow() ? 1 : 0) + "\t" + s.depth() + "\t" + s.x() + "\t" + s.y() + "\t" + s.z()
                + "\t" + s.pitch() + "\t" + s.yaw() + "\t" + s.roll() + "\t" + s.scale() + "\t" + s.width() + "\t" + s.height() + "\t" + sanitizeText(s.text());
    }

    public record Line(int slot, Slot value) {
    }

    public static Line decodeLine(String line) {
        if (line == null) {
            return null;
        }

        String[] f = line.split("\t", 16);
        if (f.length != 14 && f.length != 16) {
            return null;
        }
        boolean stretched = f.length == 16;
        try {
            int slot = Integer.parseInt(f[0]);
            if (slot < 0 || slot >= MAX_SLOTS) {
                return null;
            }
            Slot s = new Slot("1".equals(f[1]), sanitizeText(f[f.length - 1]), sanitizeFont(f[2]),
                    Integer.parseInt(f[3], 16) & 0xFFFFFF, "1".equals(f[4]), clamp(parseFloat(f[5]), 0f, MAX_DEPTH),
                    parseFloat(f[6]), parseFloat(f[7]), parseFloat(f[8]),
                    parseFloat(f[9]), parseFloat(f[10]), parseFloat(f[11]),
                    clamp(parseFloat(f[12]), MIN_SCALE, MAX_SCALE),
                    stretched ? clamp(parseFloat(f[13]), MIN_STRETCH, MAX_STRETCH) : 1f,
                    stretched ? clamp(parseFloat(f[14]), MIN_STRETCH, MAX_STRETCH) : 1f);
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
