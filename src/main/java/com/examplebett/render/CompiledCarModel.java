package com.examplebett.render;

import com.examplebett.AiObjectMod;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompiledCarModel {

    public static final ResourceLocation FALLBACK_TEXTURE =
            AiObjectMod.id("textures/entity/white.png");

    private record GroupKey(ResourceLocation texture, boolean noCull, boolean tintable, boolean translucent, int wheelGroup, int rotorGroup) {
    }

    private record Resolution(ResourceLocation texture, boolean tintable, boolean translucent) {
    }

    public static class TextureGroup {
        public final ResourceLocation texture;

        public final boolean noCull;

        public final boolean tintable;

        public final boolean translucent;

        public final int wheelGroup;

        public final int rotorGroup;

        public final float[] vertexData;

        public TextureGroup(ResourceLocation texture, boolean noCull, boolean tintable, boolean translucent, int wheelGroup, int rotorGroup, float[] vertexData) {
            this.texture = texture;
            this.noCull = noCull;
            this.tintable = tintable;
            this.translucent = translucent;
            this.wheelGroup = wheelGroup;
            this.rotorGroup = rotorGroup;
            this.vertexData = vertexData;
        }
    }

    public final List<TextureGroup> groups;

    public final List<ObjModel.WheelPivot> wheelPivots;

    public final List<ObjModel.RotorPivot> rotorPivots;

    public final float bodyMinY;

    public final float bodyCenterZ;

    public final float bodyCenterX;

    private CompiledCarModel(List<TextureGroup> groups, List<ObjModel.WheelPivot> wheelPivots,
                              List<ObjModel.RotorPivot> rotorPivots, float bodyMinY, float bodyCenterZ, float bodyCenterX) {
        this.groups = groups;
        this.wheelPivots = wheelPivots;
        this.rotorPivots = rotorPivots;
        this.bodyMinY = bodyMinY;
        this.bodyCenterZ = bodyCenterZ;
        this.bodyCenterX = bodyCenterX;
    }

    public float averageWheelRadius() {
        if (wheelPivots.isEmpty()) {
            return 0f;
        }
        float sum = 0f;
        for (ObjModel.WheelPivot pivot : wheelPivots) {
            sum += pivot.radius;
        }
        return sum / wheelPivots.size();
    }

    public static CompiledCarModel compile(ObjModel model, Map<String, MtlMaterial> materials, ResourceLocation fallbackTexture) {
        return compile(model, materials, fallbackTexture, false, null);
    }

    public static CompiledCarModel compile(ObjModel model, Map<String, MtlMaterial> materials, ResourceLocation fallbackTexture, boolean forceDoubleSided) {
        return compile(model, materials, fallbackTexture, forceDoubleSided, null);
    }

    public static CompiledCarModel compile(ObjModel model, Map<String, MtlMaterial> materials, ResourceLocation fallbackTexture, boolean forceDoubleSided, String modelId) {
        List<ObjModel.Triangle> triangles = model.triangles;

        float[][] cornerNormals = computeSmoothNormals(triangles);

        GroupKey[] keys = new GroupKey[triangles.size()];
        Map<GroupKey, Integer> counts = new HashMap<>();
        for (int i = 0; i < triangles.size(); i++) {
            ObjModel.Triangle tri = triangles.get(i);
            Resolution resolution = resolve(tri, materials, fallbackTexture);
            MtlMaterial triMaterial = materials != null ? materials.get(tri.materialName) : null;

            boolean noCull = tri.uncertainWinding || forceDoubleSided || tri.wheelGroup >= 0 || tri.rotorGroup >= 0
                    || (triMaterial != null && triMaterial.doubleSided);
            GroupKey key = new GroupKey(resolution.texture(), noCull, resolution.tintable(), resolution.translucent(), tri.wheelGroup, tri.rotorGroup);
            keys[i] = key;
            counts.merge(key, 3, Integer::sum);
        }

        Map<GroupKey, float[]> buffers = new HashMap<>();
        Map<GroupKey, int[]> cursors = new HashMap<>();
        for (Map.Entry<GroupKey, Integer> entry : counts.entrySet()) {
            buffers.put(entry.getKey(), new float[entry.getValue() * 12]);
            cursors.put(entry.getKey(), new int[]{0});
        }

        for (int i = 0; i < triangles.size(); i++) {
            ObjModel.Triangle tri = triangles.get(i);
            GroupKey key = keys[i];

            MtlMaterial material = materials != null ? materials.get(tri.materialName) : null;
            float r = 1f, g = 1f, b = 1f;
            if (key.texture().equals(fallbackTexture) && material != null) {
                r = material.r;
                g = material.g;
                b = material.b;
            }

            float a = material != null ? material.a : 1f;

            float[] buffer = buffers.get(key);
            int[] cursor = cursors.get(key);
            cursor[0] = writeVertex(buffer, cursor[0], tri.v1, r, g, b, a, cornerNormals[i * 3]);
            cursor[0] = writeVertex(buffer, cursor[0], tri.v2, r, g, b, a, cornerNormals[i * 3 + 1]);
            cursor[0] = writeVertex(buffer, cursor[0], tri.v3, r, g, b, a, cornerNormals[i * 3 + 2]);
        }

        List<TextureGroup> groups = new ArrayList<>(buffers.size());
        for (Map.Entry<GroupKey, float[]> entry : buffers.entrySet()) {
            GroupKey key = entry.getKey();
            TextureGroup g = new TextureGroup(key.texture(), key.noCull(), key.tintable(), key.translucent(), key.wheelGroup(), key.rotorGroup(), entry.getValue());
            groups.add(g);
        }

        groups.sort(Comparator.comparing(g -> g.translucent));

        float bodyMinY = Float.NaN;
        float bodyMinZ = Float.NaN, bodyMaxZ = Float.NaN;
        float bodyMinX = Float.NaN, bodyMaxX = Float.NaN;
        for (ObjModel.Triangle tri : triangles) {
            if (tri.wheelGroup >= 0 || tri.rotorGroup >= 0) continue;
            float triMinY = Math.min(tri.v1.y, Math.min(tri.v2.y, tri.v3.y));
            bodyMinY = Float.isNaN(bodyMinY) ? triMinY : Math.min(bodyMinY, triMinY);
            float triMinZ = Math.min(tri.v1.z, Math.min(tri.v2.z, tri.v3.z));
            float triMaxZ = Math.max(tri.v1.z, Math.max(tri.v2.z, tri.v3.z));
            bodyMinZ = Float.isNaN(bodyMinZ) ? triMinZ : Math.min(bodyMinZ, triMinZ);
            bodyMaxZ = Float.isNaN(bodyMaxZ) ? triMaxZ : Math.max(bodyMaxZ, triMaxZ);
            float triMinX = Math.min(tri.v1.x, Math.min(tri.v2.x, tri.v3.x));
            float triMaxX = Math.max(tri.v1.x, Math.max(tri.v2.x, tri.v3.x));
            bodyMinX = Float.isNaN(bodyMinX) ? triMinX : Math.min(bodyMinX, triMinX);
            bodyMaxX = Float.isNaN(bodyMaxX) ? triMaxX : Math.max(bodyMaxX, triMaxX);
        }
        float bodyCenterZ = Float.isNaN(bodyMinZ) ? Float.NaN : (bodyMinZ + bodyMaxZ) / 2f;
        float bodyCenterX = Float.isNaN(bodyMinX) ? Float.NaN : (bodyMinX + bodyMaxX) / 2f;
        return new CompiledCarModel(groups, model.wheelPivots, model.rotorPivots, bodyMinY, bodyCenterZ, bodyCenterX);
    }

    private static final float DECAL_NORMAL_OFFSET = 0.001f;

    public static CompiledCarModel compileBodyAndDecal(ObjModel model, Map<String, MtlMaterial> decalMaterials,
                                                          ResourceLocation fallbackTexture, boolean forceDoubleSided) {
        CompiledCarModel body = compile(model, null, fallbackTexture, forceDoubleSided);
        CompiledCarModel decal = compile(model, decalMaterials, fallbackTexture, forceDoubleSided);
        List<TextureGroup> combined = new ArrayList<>(body.groups.size() + decal.groups.size());
        combined.addAll(body.groups);
        for (TextureGroup group : decal.groups) {
            combined.add(new TextureGroup(group.texture, group.noCull, group.tintable, group.translucent,
                    group.wheelGroup, group.rotorGroup, offsetAlongNormal(group.vertexData, DECAL_NORMAL_OFFSET)));
        }

        combined.sort(Comparator.comparing(g -> g.translucent));
        return new CompiledCarModel(combined, model.wheelPivots, model.rotorPivots, decal.bodyMinY, decal.bodyCenterZ, decal.bodyCenterX);
    }

    private static float[] offsetAlongNormal(float[] src, float amount) {
        float[] dst = src.clone();
        for (int i = 0; i < dst.length; i += 12) {
            float nx = dst[i + 9], ny = dst[i + 10], nz = dst[i + 11];
            dst[i] += nx * amount;
            dst[i + 1] += ny * amount;
            dst[i + 2] += nz * amount;
        }
        return dst;
    }

    public static CompiledCarModel mirrorX(CompiledCarModel source) {
        List<TextureGroup> mirroredGroups = new ArrayList<>(source.groups.size());
        for (TextureGroup group : source.groups) {
            float[] src = group.vertexData;
            float[] dst = new float[src.length];
            for (int tri = 0; tri < src.length; tri += 36) {
                copyMirroredVertex(src, tri, dst, tri);
                copyMirroredVertex(src, tri + 24, dst, tri + 12);
                copyMirroredVertex(src, tri + 12, dst, tri + 24);
            }
            mirroredGroups.add(new TextureGroup(group.texture, group.noCull, group.tintable, group.translucent,
                    group.wheelGroup, group.rotorGroup, dst));
        }

        return new CompiledCarModel(mirroredGroups, source.wheelPivots, source.rotorPivots, source.bodyMinY, source.bodyCenterZ, source.bodyCenterX);
    }

    private static void copyMirroredVertex(float[] src, int srcOffset, float[] dst, int dstOffset) {
        dst[dstOffset] = -src[srcOffset];
        dst[dstOffset + 1] = src[srcOffset + 1];
        dst[dstOffset + 2] = src[srcOffset + 2];
        dst[dstOffset + 3] = src[srcOffset + 3];
        dst[dstOffset + 4] = src[srcOffset + 4];
        dst[dstOffset + 5] = src[srcOffset + 5];
        dst[dstOffset + 6] = src[srcOffset + 6];
        dst[dstOffset + 7] = src[srcOffset + 7];
        dst[dstOffset + 8] = src[srcOffset + 8];
        dst[dstOffset + 9] = -src[srcOffset + 9];
        dst[dstOffset + 10] = src[srcOffset + 10];
        dst[dstOffset + 11] = src[srcOffset + 11];
    }

    private static final float WHITE_EPSILON = 0.02f;

    private static boolean isWhite(MtlMaterial material) {
        return Math.abs(material.r - 1f) < WHITE_EPSILON
                && Math.abs(material.g - 1f) < WHITE_EPSILON
                && Math.abs(material.b - 1f) < WHITE_EPSILON;
    }

    private static Resolution resolve(ObjModel.Triangle tri, Map<String, MtlMaterial> materials, ResourceLocation fallbackTexture) {
        MtlMaterial material = materials != null ? materials.get(tri.materialName) : null;
        if (material != null && material.texture != null) {

            return new Resolution(material.texture, false, material.translucent);
        }
        boolean translucent = material != null && material.translucent;
        boolean tintable = material == null || isWhite(material);
        return new Resolution(fallbackTexture, tintable, translucent);
    }

    private static int writeVertex(float[] buffer, int cursor, ObjModel.Vertex vertex, float r, float g, float b, float a, float[] normal) {
        buffer[cursor++] = vertex.x;
        buffer[cursor++] = vertex.y;
        buffer[cursor++] = vertex.z;
        buffer[cursor++] = vertex.u;
        buffer[cursor++] = vertex.v;
        buffer[cursor++] = r;
        buffer[cursor++] = g;
        buffer[cursor++] = b;
        buffer[cursor++] = a;
        buffer[cursor++] = normal[0];
        buffer[cursor++] = normal[1];
        buffer[cursor++] = normal[2];
        return cursor;
    }

    private static final float SMOOTH_NORMAL_COS_THRESHOLD = 0.5f;

    private static float[][] computeFaceNormals(List<ObjModel.Triangle> triangles, boolean[] degenerateOut) {
        int n = triangles.size();
        float[][] normals = new float[n][];
        for (int i = 0; i < n; i++) {
            ObjModel.Triangle t = triangles.get(i);
            float ax = t.v2.x - t.v1.x, ay = t.v2.y - t.v1.y, az = t.v2.z - t.v1.z;
            float bx = t.v3.x - t.v1.x, by = t.v3.y - t.v1.y, bz = t.v3.z - t.v1.z;
            float nx = ay * bz - az * by;
            float ny = az * bx - ax * bz;
            float nz = ax * by - ay * bx;
            float length = (float) Math.sqrt(nx * nx + ny * ny + nz * nz);
            if (length < 1e-8f) {

                degenerateOut[i] = true;
                normals[i] = new float[]{0f, 1f, 0f};
            } else {
                normals[i] = new float[]{nx / length, ny / length, nz / length};
            }
        }
        return normals;
    }

    private static float[][] computeSmoothNormals(List<ObjModel.Triangle> triangles) {
        int n = triangles.size();
        boolean[] degenerate = new boolean[n];
        float[][] faceNormals = computeFaceNormals(triangles, degenerate);

        Map<String, List<int[]>> byPosition = new HashMap<>();
        for (int i = 0; i < n; i++) {
            ObjModel.Triangle t = triangles.get(i);
            String tag = "#g" + t.wheelGroup + "r" + t.rotorGroup;
            addCorner(byPosition, t.v1, tag, i, 0);
            addCorner(byPosition, t.v2, tag, i, 1);
            addCorner(byPosition, t.v3, tag, i, 2);
        }

        float[][] cornerNormals = new float[n * 3][];
        for (List<int[]> bucket : byPosition.values()) {
            for (int[] entry : bucket) {
                int triIndex = entry[0];
                int corner = entry[1];
                float[] base = faceNormals[triIndex];
                if (degenerate[triIndex]) {
                    cornerNormals[triIndex * 3 + corner] = base;
                    continue;
                }
                float sx = 0f, sy = 0f, sz = 0f;
                for (int[] other : bucket) {
                    int otherIndex = other[0];
                    if (degenerate[otherIndex]) continue;
                    float[] otherNormal = faceNormals[otherIndex];
                    float dot = base[0] * otherNormal[0] + base[1] * otherNormal[1] + base[2] * otherNormal[2];
                    if (dot >= SMOOTH_NORMAL_COS_THRESHOLD) {
                        sx += otherNormal[0];
                        sy += otherNormal[1];
                        sz += otherNormal[2];
                    }
                }
                float length = (float) Math.sqrt(sx * sx + sy * sy + sz * sz);
                cornerNormals[triIndex * 3 + corner] = length < 1e-8f
                        ? base
                        : new float[]{sx / length, sy / length, sz / length};
            }
        }
        return cornerNormals;
    }

    private static void addCorner(Map<String, List<int[]>> byPosition, ObjModel.Vertex v, String tag, int triIndex, int corner) {
        String key = quantizeVertex(v) + tag;
        byPosition.computeIfAbsent(key, k -> new ArrayList<>()).add(new int[]{triIndex, corner});
    }

    private static final float POSITION_EPSILON = 0.00002f;

    private static String quantizeVertex(ObjModel.Vertex v) {
        long qx = Math.round(v.x / POSITION_EPSILON);
        long qy = Math.round(v.y / POSITION_EPSILON);
        long qz = Math.round(v.z / POSITION_EPSILON);
        return qx + "," + qy + "," + qz;
    }
}
