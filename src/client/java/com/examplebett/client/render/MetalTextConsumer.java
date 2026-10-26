package com.examplebett.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

public final class MetalTextConsumer implements VertexConsumer {

    private static final float[][] STYLE = {
            {1.35f, 0.6f, 0.9f},
            {1.25f, 0.7f, 0.7f},
            {1.15f, 0.8f, 0.45f}
    };

    private final VertexConsumer out;
    private final PoseStack.Pose pose;
    private final float left;
    private final float top;
    private final float width;
    private final float height;
    private final int rgb;
    private final float[] style;
    private final boolean gold;
    private final float phase;
    private float lastX;
    private float lastY;

    public static VertexConsumer create(VertexConsumer out, PoseStack.Pose pose, float left, float top, float width, float height,
                                        int rgb, int metal, float phase) {
        MetalTextConsumer consumer = new MetalTextConsumer(out, pose, left, top, width, height, rgb, metal, phase);
        //? if <1.21.9 {
        /*Class<?> writer = SodiumBridge.writerClass();
        if (writer != null) {
            return SodiumBridge.wrap(consumer, writer);
        }*/
        //?}
        return consumer;
    }

    public MetalTextConsumer(VertexConsumer out, PoseStack.Pose pose, float left, float top, float width, float height,
                             int rgb, int metal, float phase) {
        this.out = out;
        this.pose = pose;
        this.left = left;
        this.top = top;
        this.width = width;
        this.height = height;
        this.rgb = rgb;
        this.style = STYLE[Math.max(0, Math.min(STYLE.length - 1, metal))];
        this.gold = metal == com.examplebett.client.gui.CarColorPalette.METAL_GOLD;
        this.phase = phase;
    }

    public static float phase(PoseStack.Pose pose, float centerX, float centerY) {
        float[] camera = PoseMatrices.transformPosition(PoseMatrices.invert(PoseMatrices.capture(pose)), 0f, 0f, 0f);
        float x = camera[0] - centerX;
        float y = camera[1] - centerY;
        float z = camera[2];
        float length = (float) Math.sqrt(x * x + y * y + z * z);
        if (length < 1e-4f) {
            return Float.NaN;
        }
        return 0.5f + 1.1f * (x / length * 0.8f - y / length * 0.6f);
    }

    private int[] metalColor() {
        float u = Math.max(0f, Math.min(1f, (this.lastX - this.left) / this.width));
        float v = Math.max(0f, Math.min(1f, (this.lastY - this.top) / this.height));
        float shade = this.style[0] + (this.style[1] - this.style[0]) * v;
        float glint = 0f;
        if (!Float.isNaN(this.phase)) {
            float s = (u * 0.8f + (1f - v) * 0.6f) / 1.4f;
            glint = Math.min(1f, glintBand(s, this.phase, 0.14f) + 0.6f * glintBand(s, this.phase - 0.24f, 0.05f)) * this.style[2];
        }

        return new int[]{
                metalChannel((this.rgb >> 16) & 0xFF, shade, glint, 255),
                metalChannel((this.rgb >> 8) & 0xFF, shade, glint, this.gold ? 244 : 255),
                metalChannel(this.rgb & 0xFF, shade, glint, this.gold ? 200 : 255)};
    }

    private static int metalChannel(int base, float shade, float glint, int highlight) {
        float c = Math.min(255f, base * shade);
        return (int) Math.min(255f, c + (highlight - c) * glint);
    }

    public static float glintBand(float s, float center, float halfWidth) {
        float t = 1f - Math.abs(s - center) / halfWidth;
        return t <= 0f ? 0f : t * t;
    }

    //? if >=1.21 {
    @Override
    public VertexConsumer addVertex(float x, float y, float z) {
        this.lastX = x;
        this.lastY = y;
        this.out.addVertex(this.pose, x, y, z);
        return this;
    }

    @Override
    public VertexConsumer setColor(int red, int green, int blue, int alpha) {
        int[] c = this.metalColor();
        this.out.setColor(c[0], c[1], c[2], alpha);
        return this;
    }

    @Override
    public VertexConsumer setUv(float u, float v) {
        this.out.setUv(u, v);
        return this;
    }

    @Override
    public VertexConsumer setUv1(int u, int v) {
        this.out.setUv1(u, v);
        return this;
    }

    @Override
    public VertexConsumer setUv2(int u, int v) {
        this.out.setUv2(u, v);
        return this;
    }

    @Override
    public VertexConsumer setNormal(float x, float y, float z) {
        this.out.setNormal(this.pose, x, y, z);
        return this;
    }
    //?}
    //? if >=1.21.11 {
    /*@Override
    public VertexConsumer setColor(int argb) {
        return this.setColor((argb >> 16) & 0xFF, (argb >> 8) & 0xFF, argb & 0xFF, (argb >>> 24) & 0xFF);
    }

    @Override
    public VertexConsumer setLineWidth(float width) {
        this.out.setLineWidth(width);
        return this;
    }*/
    //?}
    //? if >=26.3 {
    /*@Override
    public VertexConsumer setUv3(float u, float v) {
        this.out.setUv3(u, v);
        return this;
    }*/
    //?}
    //? if <1.21 {
    /*@Override
    public VertexConsumer vertex(double x, double y, double z) {
        this.lastX = (float) x;
        this.lastY = (float) y;
        this.out.vertex(this.pose.pose(), (float) x, (float) y, (float) z);
        return this;
    }

    @Override
    public VertexConsumer color(int red, int green, int blue, int alpha) {
        int[] c = this.metalColor();
        this.out.color(c[0], c[1], c[2], alpha);
        return this;
    }

    @Override
    public VertexConsumer uv(float u, float v) {
        this.out.uv(u, v);
        return this;
    }

    @Override
    public VertexConsumer overlayCoords(int u, int v) {
        this.out.overlayCoords(u, v);
        return this;
    }

    @Override
    public VertexConsumer uv2(int u, int v) {
        this.out.uv2(u, v);
        return this;
    }

    @Override
    public void endVertex() {
        this.out.endVertex();
    }

    public void defaultColor(int red, int green, int blue, int alpha) {
    }

    public void unsetDefaultColor() {
    }*/
    //?}

    //? if >=1.21 {
    void emitVertex(float x, float y, float z, int red, int green, int blue, int alpha, float u, float v, int lightU, int lightV) {
        this.addVertex(x, y, z);
        this.setColor(red, green, blue, alpha);
        this.setUv(u, v);
        this.setUv2(lightU, lightV);
    }
    //?}
    //? if <1.21 {
    /*void emitVertex(float x, float y, float z, int red, int green, int blue, int alpha, float u, float v, int lightU, int lightV) {
        this.vertex(x, y, z);
        this.color(red, green, blue, alpha);
        this.uv(u, v);
        this.uv2(lightU, lightV);
        this.endVertex();
    }*/
    //?}

    //? if <1.21.9 {
    /*private static final class SodiumBridge {
        private static final String WRITER_CLASS = "net.caffeinemc.mods.sodium.api.vertex.buffer.VertexBufferWriter";
        private static Class<?> writer;
        private static boolean looked;
        private static boolean warned;

        static Class<?> writerClass() {
            if (!looked) {
                looked = true;
                try {
                    writer = Class.forName(WRITER_CLASS, false, VertexConsumer.class.getClassLoader());
                } catch (Throwable t) {
                    writer = null;
                }
            }
            return writer;
        }

        static VertexConsumer wrap(MetalTextConsumer target, Class<?> writer) {
            try {
                return (VertexConsumer) java.lang.reflect.Proxy.newProxyInstance(VertexConsumer.class.getClassLoader(),
                        new Class<?>[]{VertexConsumer.class, writer}, (proxy, method, args) -> {
                            String name = method.getName();
                            if (method.getDeclaringClass() == writer) {
                                if ("push".equals(name) && args != null && args.length == 4) {
                                    push(target, (Long) args[1], (Integer) args[2], args[3]);
                                    return null;
                                }
                                if ("canUseIntrinsics".equals(name)) {
                                    return true;
                                }
                            }
                            if (method.getDeclaringClass() == Object.class) {
                                if ("equals".equals(name)) {
                                    return proxy == args[0];
                                }
                                if ("hashCode".equals(name)) {
                                    return System.identityHashCode(proxy);
                                }
                                return "MetalTextConsumer$Sodium";
                            }
                            try {
                                return method.invoke(target, args);
                            } catch (java.lang.reflect.InvocationTargetException e) {
                                throw e.getCause();
                            }
                        });
            } catch (Throwable t) {
                return target;
            }
        }

        private static void push(MetalTextConsumer target, long ptr, int count, Object format) {
            int[] layout = layout(format);
            if (layout == null) {
                if (!warned) {
                    warned = true;
                    com.examplebett.AiObjectMod.LOGGER.warn("[metal-text] Unknown Sodium vertex format {}, metal text skipped", format);
                }
                return;
            }
            int stride = layout[0];
            for (int i = 0; i < count; i++) {
                long at = ptr + (long) i * stride;
                float x = org.lwjgl.system.MemoryUtil.memGetFloat(at + layout[1]);
                float y = org.lwjgl.system.MemoryUtil.memGetFloat(at + layout[1] + 4);
                float z = org.lwjgl.system.MemoryUtil.memGetFloat(at + layout[1] + 8);
                int abgr = org.lwjgl.system.MemoryUtil.memGetInt(at + layout[2]);
                float u = org.lwjgl.system.MemoryUtil.memGetFloat(at + layout[3]);
                float v = org.lwjgl.system.MemoryUtil.memGetFloat(at + layout[3] + 4);
                int light = org.lwjgl.system.MemoryUtil.memGetInt(at + layout[4]);
                target.emitVertex(x, y, z, abgr & 0xFF, (abgr >> 8) & 0xFF, (abgr >> 16) & 0xFF, (abgr >>> 24) & 0xFF,
                        u, v, light & 0xFFFF, (light >>> 16) & 0xFFFF);
            }
        }

        private static int[] layout(Object format) {
            int[] vanilla = vanillaLayout(format);
            if (vanilla != null) {
                return vanilla;
            }
            try {
                java.lang.reflect.Method offset = null;
                for (java.lang.reflect.Method m : format.getClass().getMethods()) {
                    if ("getElementOffset".equals(m.getName()) && m.getParameterCount() == 1 && m.getParameterTypes()[0].isEnum()) {
                        offset = m;
                        break;
                    }
                }
                if (offset == null) {
                    return null;
                }
                offset.setAccessible(true);
                java.lang.reflect.Method strideMethod = format.getClass().getMethod("stride");
                strideMethod.setAccessible(true);
                Class<?> attribute = offset.getParameterTypes()[0];
                int[] result = {(Integer) strideMethod.invoke(format),
                        (Integer) offset.invoke(format, enumConstant(attribute, "POSITION")),
                        (Integer) offset.invoke(format, enumConstant(attribute, "COLOR")),
                        (Integer) offset.invoke(format, enumConstant(attribute, "TEXTURE")),
                        (Integer) offset.invoke(format, enumConstant(attribute, "LIGHT"))};
                for (int value : result) {
                    if (value < 0) {
                        return null;
                    }
                }
                return result;
            } catch (Throwable t) {
                return null;
            }
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        private static Object enumConstant(Class<?> type, String name) {
            return Enum.valueOf((Class) type, name);
        }
    }*/
    //?}
    //? if >=1.21 && <1.21.9 {
    /*private static int[] vanillaLayout(Object format) {
        if (!(format instanceof com.mojang.blaze3d.vertex.VertexFormat)) {
            return null;
        }
        com.mojang.blaze3d.vertex.VertexFormat vf = (com.mojang.blaze3d.vertex.VertexFormat) format;
        if (!vf.contains(com.mojang.blaze3d.vertex.VertexFormatElement.POSITION) || !vf.contains(com.mojang.blaze3d.vertex.VertexFormatElement.COLOR)
                || !vf.contains(com.mojang.blaze3d.vertex.VertexFormatElement.UV0) || !vf.contains(com.mojang.blaze3d.vertex.VertexFormatElement.UV2)) {
            return null;
        }
        return new int[]{vf.getVertexSize(), vf.getOffset(com.mojang.blaze3d.vertex.VertexFormatElement.POSITION),
                vf.getOffset(com.mojang.blaze3d.vertex.VertexFormatElement.COLOR), vf.getOffset(com.mojang.blaze3d.vertex.VertexFormatElement.UV0),
                vf.getOffset(com.mojang.blaze3d.vertex.VertexFormatElement.UV2)};
    }*/
    //?}
    //? if <1.21 {
    /*private static int[] vanillaLayout(Object format) {
        return null;
    }*/
    //?}

    //? if >=1.20.5 && <1.21 {
    /*@Override
    public VertexConsumer normal(float x, float y, float z) {
        this.out.normal(this.pose, x, y, z);
        return this;
    }*/
    //?}
    //? if <1.20.5 {
    /*@Override
    public VertexConsumer normal(float x, float y, float z) {
        this.out.normal(this.pose.normal(), x, y, z);
        return this;
    }*/
    //?}
}
