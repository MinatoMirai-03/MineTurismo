package com.examplebett.client.render;

import com.examplebett.AiObjectMod;
import com.examplebett.render.MtlMaterial;
import com.examplebett.render.ObjModel;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
//? if >=1.19.3 {
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
//?}

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class GlbModelLoader {

    private static final int GLB_MAGIC = 0x46546C67;
    private static final int CHUNK_TYPE_JSON = 0x4E4F534A;
    private static final int CHUNK_TYPE_BIN = 0x004E4942;

    public record GlbResult(ObjModel model, Map<String, MtlMaterial> materials) {
    }

    private static final Map<String, GlbResult> cache = new HashMap<>();
    private static final Map<String, TextureResolution> textureCache = new HashMap<>();

    private record TextureResolution(ResourceLocation location, boolean tintable) {
    }

    public static GlbResult load(ResourceManager resourceManager, String modelPath) throws IOException {
        ResourceLocation modelLocation = AiObjectMod.id("models/" + modelPath + ".glb");
        Resource resource = com.examplebett.compat.ModResources.getResourceOptional(resourceManager, modelLocation).orElseThrow();

        byte[] fileBytes;

        //? if >=1.19 {
        try (InputStream stream = resource.open()) {
            fileBytes = stream.readAllBytes();
        }
        //?} else {
        /*try (InputStream stream = resource.getInputStream()) {
            fileBytes = stream.readAllBytes();
        }*/
        //?}

        return loadFromBytes(fileBytes, modelPath);
    }

    public static GlbResult loadFromBytes(byte[] fileBytes, String cacheKey) throws IOException {
        GlbResult cached = cache.get(cacheKey);
        if (cached != null) {
            return cached;
        }

        ByteBuffer buf = ByteBuffer.wrap(fileBytes).order(ByteOrder.LITTLE_ENDIAN);
        if (buf.remaining() < 12 || buf.getInt() != GLB_MAGIC) {
            throw new IOException("Not a valid GLB file: " + cacheKey);
        }
        buf.getInt();
        buf.getInt();

        byte[] jsonChunk = null;
        byte[] binChunk = null;
        while (buf.remaining() >= 8) {
            int chunkLength = buf.getInt();
            int chunkType = buf.getInt();
            byte[] chunkData = new byte[chunkLength];
            buf.get(chunkData);
            if (chunkType == CHUNK_TYPE_JSON) {
                jsonChunk = chunkData;
            } else if (chunkType == CHUNK_TYPE_BIN) {
                binChunk = chunkData;
            }
        }
        if (jsonChunk == null) {
            throw new IOException("GLB has no JSON chunk: " + cacheKey);
        }
        if (binChunk == null) {
            binChunk = new byte[0];
        }

        JsonObject root = JsonParser.parseString(new String(jsonChunk, StandardCharsets.UTF_8)).getAsJsonObject();

        Map<String, MtlMaterial> materials = buildMaterials(root, binChunk, cacheKey);

        ObjModel model = buildModel(root, binChunk, materials, cacheKey);

        GlbResult result = new GlbResult(model, materials);
        cache.put(cacheKey, result);
        return result;
    }

    public static void clearCache() {
        cache.clear();
        textureCache.clear();
        PaintTintTexture.clearCache();
    }

    //? if >=1.19.3 {
    private static ObjModel buildModel(JsonObject root, byte[] binChunk, Map<String, MtlMaterial> materials, String modelId) {
        ObjModel model = new ObjModel();
        if (!root.has("nodes") || !root.has("meshes")) {
            return model;
        }

        JsonArray nodes = root.getAsJsonArray("nodes");
        JsonArray rootNodeIndices = resolveRootNodes(root, nodes);

        int nodeCount = nodes.size();
        Matrix4f[] worldTransforms = new Matrix4f[nodeCount];
        boolean[] wheelCandidateByNode = new boolean[nodeCount];
        boolean[] rotorCandidateByNode = new boolean[nodeCount];
        int[] parentNodeIndex = new int[nodeCount];
        Arrays.fill(parentNodeIndex, -1);
        for (JsonElement el : rootNodeIndices) {
            collectNodeTransforms(nodes, el.getAsInt(), new Matrix4f(), false, false, -1,
                    worldTransforms, wheelCandidateByNode, rotorCandidateByNode, parentNodeIndex);
        }

        for (int i = 0; i < nodeCount; i++) {
            if (worldTransforms[i] == null) continue;
            JsonObject node = nodes.get(i).getAsJsonObject();
            if (!node.has("mesh")) continue;
            SkinContext skinContext = node.has("skin")
                    ? buildSkinContext(root, binChunk, node.get("skin").getAsInt(),
                            worldTransforms, wheelCandidateByNode, rotorCandidateByNode, parentNodeIndex)
                    : null;
            appendMesh(root, binChunk, node.get("mesh").getAsInt(), worldTransforms[i],
                    wheelCandidateByNode[i], rotorCandidateByNode[i], i, model, skinContext);
        }

        ObjModel.resolveWheelGroups(model);
        ObjModel.resolveRotorGroups(model);

        List<ObjModel.Triangle> opaqueTriangles = new ArrayList<>();
        List<ObjModel.Triangle> translucentTriangles = new ArrayList<>();
        for (ObjModel.Triangle tri : model.triangles) {
            MtlMaterial material = materials.get(tri.materialName);
            if (material != null && material.translucent) {
                ObjModel.Vertex tmp = tri.v2;
                tri.v2 = tri.v3;
                tri.v3 = tmp;
                translucentTriangles.add(tri);
            } else {
                opaqueTriangles.add(tri);
            }
        }

        ObjModel.removeOverlappingDuplicates(opaqueTriangles);
        ObjModel.fixInconsistentWinding(opaqueTriangles, modelId);
        model.triangles.clear();
        model.triangles.addAll(opaqueTriangles);
        model.triangles.addAll(translucentTriangles);

        return model;
    }

    private static JsonArray resolveRootNodes(JsonObject root, JsonArray nodes) {
        int sceneIndex = root.has("scene") ? root.get("scene").getAsInt() : 0;
        if (root.has("scenes") && root.getAsJsonArray("scenes").size() > sceneIndex) {
            JsonObject scene = root.getAsJsonArray("scenes").get(sceneIndex).getAsJsonObject();
            if (scene.has("nodes")) {
                return scene.getAsJsonArray("nodes");
            }
        }

        JsonArray all = new JsonArray();
        for (int i = 0; i < nodes.size(); i++) all.add(i);
        return all;
    }

    private static void collectNodeTransforms(JsonArray nodes, int nodeIndex,
                                               Matrix4f parentTransform, boolean wheelCandidateAncestor,
                                               boolean rotorCandidateAncestor, int parentIndex,
                                               Matrix4f[] worldTransforms, boolean[] wheelCandidateByNode,
                                               boolean[] rotorCandidateByNode, int[] parentNodeIndex) {
        JsonObject node = nodes.get(nodeIndex).getAsJsonObject();
        Matrix4f world = new Matrix4f(parentTransform).mul(readNodeTransform(node));
        String nodeName = node.has("name") ? node.get("name").getAsString() : null;
        boolean wheelCandidate = wheelCandidateAncestor || ObjModel.isWheelCandidateName(nodeName);
        boolean rotorCandidate = rotorCandidateAncestor || ObjModel.isRotorCandidateName(nodeName);

        worldTransforms[nodeIndex] = world;
        wheelCandidateByNode[nodeIndex] = wheelCandidate;
        rotorCandidateByNode[nodeIndex] = rotorCandidate;
        parentNodeIndex[nodeIndex] = parentIndex;

        if (node.has("children")) {
            for (JsonElement child : node.getAsJsonArray("children")) {
                collectNodeTransforms(nodes, child.getAsInt(), world, wheelCandidate, rotorCandidate, nodeIndex,
                        worldTransforms, wheelCandidateByNode, rotorCandidateByNode, parentNodeIndex);
            }
        }
    }

    private static Matrix4f readNodeTransform(JsonObject node) {
        if (node.has("matrix")) {
            JsonArray m = node.getAsJsonArray("matrix");
            float[] values = new float[16];
            for (int i = 0; i < 16; i++) values[i] = m.get(i).getAsFloat();
            return new Matrix4f().set(values);
        }
        Vector3f translation = readVec3(node, "translation", 0f, 0f, 0f);
        Quaternionf rotation = readQuaternion(node);
        Vector3f scale = readVec3(node, "scale", 1f, 1f, 1f);
        return new Matrix4f().translationRotateScale(translation, rotation, scale);
    }

    private static Vector3f readVec3(JsonObject node, String key, float dx, float dy, float dz) {
        if (!node.has(key)) return new Vector3f(dx, dy, dz);
        JsonArray a = node.getAsJsonArray(key);
        return new Vector3f(a.get(0).getAsFloat(), a.get(1).getAsFloat(), a.get(2).getAsFloat());
    }

    private static Quaternionf readQuaternion(JsonObject node) {
        if (!node.has("rotation")) return new Quaternionf();
        JsonArray a = node.getAsJsonArray("rotation");
        return new Quaternionf(a.get(0).getAsFloat(), a.get(1).getAsFloat(), a.get(2).getAsFloat(), a.get(3).getAsFloat());
    }

    private record SkinContext(List<Integer> jointNodeIndices, Matrix4f[] skinMatrices,
                                boolean[] wheelCandidateByNode, boolean[] rotorCandidateByNode) {
    }

    private static SkinContext buildSkinContext(JsonObject root, byte[] binChunk, int skinIndex,
                                                  Matrix4f[] worldTransforms, boolean[] wheelCandidateByNode,
                                                  boolean[] rotorCandidateByNode, int[] parentNodeIndex) {
        JsonObject skin = root.getAsJsonArray("skins").get(skinIndex).getAsJsonObject();
        JsonArray jointsJson = skin.getAsJsonArray("joints");
        List<Integer> jointNodeIndices = new ArrayList<>(jointsJson.size());
        for (JsonElement el : jointsJson) jointNodeIndices.add(el.getAsInt());

        Matrix4f[] skinMatrices = new Matrix4f[jointNodeIndices.size()];
        float[][] inverseBindMatrices = skin.has("inverseBindMatrices")
                ? readAccessorVectors(root, binChunk, skin.get("inverseBindMatrices").getAsInt())
                : null;
        for (int j = 0; j < jointNodeIndices.size(); j++) {
            int jointNode = jointNodeIndices.get(j);
            Matrix4f jointWorld = worldTransforms[jointNode] != null ? worldTransforms[jointNode] : new Matrix4f();
            Matrix4f inverseBind = inverseBindMatrices != null
                    ? new Matrix4f().set(inverseBindMatrices[j])
                    : new Matrix4f();
            skinMatrices[j] = new Matrix4f(jointWorld).mul(inverseBind);
        }

        if (skinMatrices.length > 0) {
            int rootJointNode = jointNodeIndices.get(0);
            int enclosingAncestor = parentNodeIndex[rootJointNode];
            if (enclosingAncestor >= 0 && worldTransforms[enclosingAncestor] != null) {
                Matrix4f ancestorWorld = worldTransforms[enclosingAncestor];
                Quaternionf ancestorRotation = ancestorWorld.getNormalizedRotation(new Quaternionf());
                Vector3f ancestorScale = ancestorWorld.getScale(new Vector3f());
                float scale = (ancestorScale.x + ancestorScale.y + ancestorScale.z) / 3f;
                boolean needsRotation = ancestorRotation.angle() > (float) Math.toRadians(1.0);
                boolean needsScale = Math.abs(scale - 1f) > 0.001f;
                if (needsRotation || needsScale) {

                    Matrix4f correction = new Matrix4f()
                            .rotation(ancestorRotation.invert(new Quaternionf()))
                            .scale(scale);
                    for (int j = 0; j < skinMatrices.length; j++) {
                        skinMatrices[j] = correction.mul(skinMatrices[j], new Matrix4f());
                    }
                }
            }
        }
        return new SkinContext(jointNodeIndices, skinMatrices, wheelCandidateByNode, rotorCandidateByNode);
    }

    private static void appendMesh(JsonObject root, byte[] binChunk, int meshIndex, Matrix4f world,
                                    boolean wheelCandidate, boolean rotorCandidate, int meshOwnerNodeIndex,
                                    ObjModel model, SkinContext skinContext) {
        JsonObject mesh = root.getAsJsonArray("meshes").get(meshIndex).getAsJsonObject();
        Matrix3f normalMatrix = world.normal(new Matrix3f());

        boolean mirrored = skinContext == null && world.determinant() < 0f;

        for (JsonElement primEl : mesh.getAsJsonArray("primitives")) {
            JsonObject primitive = primEl.getAsJsonObject();
            int mode = primitive.has("mode") ? primitive.get("mode").getAsInt() : 4;
            if (mode != 4) {
                AiObjectMod.LOGGER.warn("Skipping GLB primitive with unsupported mode " + mode);
                continue;
            }

            JsonObject attributes = primitive.getAsJsonObject("attributes");
            if (attributes == null || !attributes.has("POSITION")) continue;

            float[][] positions = readAccessorVectors(root, binChunk, attributes.get("POSITION").getAsInt());
            float[][] normals = attributes.has("NORMAL")
                    ? readAccessorVectors(root, binChunk, attributes.get("NORMAL").getAsInt())
                    : null;
            float[][] uvs = attributes.has("TEXCOORD_0")
                    ? readAccessorVectors(root, binChunk, attributes.get("TEXCOORD_0").getAsInt())
                    : null;
            int[] indices = primitive.has("indices")
                    ? readAccessorIndices(root, binChunk, primitive.get("indices").getAsInt())
                    : identityIndices(positions.length);

            String materialName = primitive.has("material") ? "material_" + primitive.get("material").getAsInt() : null;

            if (uvs != null && primitive.has("material")) {
                applyTextureTransform(uvs, readTextureTransform(root, primitive.get("material").getAsInt()));
            }

            float[][] joints0 = null;
            float[][] weights0 = null;
            if (skinContext != null && attributes.has("JOINTS_0") && attributes.has("WEIGHTS_0")) {
                joints0 = readAccessorVectors(root, binChunk, attributes.get("JOINTS_0").getAsInt());
                weights0 = readAccessorVectors(root, binChunk, attributes.get("WEIGHTS_0").getAsInt());
            }
            boolean skinned = joints0 != null;

            for (int i = 0; i + 2 < indices.length; i += 3) {
                int rawI1 = indices[i];
                int rawI2 = indices[i + 1];
                int rawI3 = indices[i + 2];

                ObjModel.Triangle tri = new ObjModel.Triangle();
                tri.materialName = materialName;

                if (skinned) {

                    int dominantLocalJoint = dominantJoint(joints0[rawI1], weights0[rawI1]);
                    Matrix4f skinMatrix = skinContext.skinMatrices()[dominantLocalJoint];
                    int jointNodeIndex = skinContext.jointNodeIndices().get(dominantLocalJoint);
                    boolean triMirrored = skinMatrix.determinant() < 0f;
                    int i2 = triMirrored ? rawI3 : rawI2;
                    int i3 = triMirrored ? rawI2 : rawI3;
                    tri.v1 = buildSkinnedVertex(positions, uvs, rawI1, joints0, weights0, skinContext);
                    tri.v2 = buildSkinnedVertex(positions, uvs, i2, joints0, weights0, skinContext);
                    tri.v3 = buildSkinnedVertex(positions, uvs, i3, joints0, weights0, skinContext);
                    tri.wheelCandidate = wheelCandidate || skinContext.wheelCandidateByNode()[jointNodeIndex];
                    tri.rotorCandidate = rotorCandidate || skinContext.rotorCandidateByNode()[jointNodeIndex];
                    tri.rotorSourceNode = jointNodeIndex;
                } else {
                    int i2 = mirrored ? rawI3 : rawI2;
                    int i3 = mirrored ? rawI2 : rawI3;
                    tri.v1 = buildVertex(positions, normals, uvs, rawI1, world, normalMatrix);
                    tri.v2 = buildVertex(positions, normals, uvs, i2, world, normalMatrix);
                    tri.v3 = buildVertex(positions, normals, uvs, i3, world, normalMatrix);
                    tri.wheelCandidate = wheelCandidate;
                    tri.rotorCandidate = rotorCandidate;

                    tri.rotorSourceNode = meshOwnerNodeIndex;
                }
                model.triangles.add(tri);
            }
        }
    }

    private static int dominantJoint(float[] joints0, float[] weights0) {
        int best = 0;
        float bestWeight = -1f;
        for (int i = 0; i < 4; i++) {
            if (weights0[i] > bestWeight) {
                bestWeight = weights0[i];
                best = i;
            }
        }
        return Math.round(joints0[best]);
    }

    private static ObjModel.Vertex buildSkinnedVertex(float[][] positions, float[][] uvs, int idx,
                                                        float[][] joints0, float[][] weights0, SkinContext skinContext) {
        ObjModel.Vertex v = new ObjModel.Vertex();

        float x = 0f, y = 0f, z = 0f;
        float[] localPos = positions[idx];
        float[] j = joints0[idx];
        float[] w = weights0[idx];
        for (int i = 0; i < 4; i++) {
            if (w[i] <= 0f) continue;
            int jointLocal = Math.round(j[i]);
            Vector3f p = new Vector3f(localPos[0], localPos[1], localPos[2]);
            skinContext.skinMatrices()[jointLocal].transformPosition(p);
            x += p.x * w[i];
            y += p.y * w[i];
            z += p.z * w[i];
        }
        v.x = x;
        v.y = y;
        v.z = z;

        if (uvs != null) {
            v.u = uvs[idx][0];
            v.v = uvs[idx][1];
        }

        return v;
    }

    private static float[] readTextureTransform(JsonObject root, int materialIndex) {
        float[] identity = {0f, 0f, 1f, 1f, 0f};
        JsonObject material = root.getAsJsonArray("materials").get(materialIndex).getAsJsonObject();
        if (!material.has("pbrMetallicRoughness")) return identity;
        JsonObject pbr = material.getAsJsonObject("pbrMetallicRoughness");
        if (!pbr.has("baseColorTexture")) return identity;
        JsonObject texInfo = pbr.getAsJsonObject("baseColorTexture");
        if (!texInfo.has("extensions")) return identity;
        JsonObject extensions = texInfo.getAsJsonObject("extensions");
        if (!extensions.has("KHR_texture_transform")) return identity;

        JsonObject transform = extensions.getAsJsonObject("KHR_texture_transform");
        float offsetX = 0f, offsetY = 0f, scaleX = 1f, scaleY = 1f, rotation = 0f;
        if (transform.has("offset")) {
            JsonArray o = transform.getAsJsonArray("offset");
            offsetX = o.get(0).getAsFloat();
            offsetY = o.get(1).getAsFloat();
        }
        if (transform.has("scale")) {
            JsonArray s = transform.getAsJsonArray("scale");
            scaleX = s.get(0).getAsFloat();
            scaleY = s.get(1).getAsFloat();
        }
        if (transform.has("rotation")) {
            rotation = transform.get("rotation").getAsFloat();
        }
        return new float[]{offsetX, offsetY, scaleX, scaleY, rotation};
    }

    private static void applyTextureTransform(float[][] uvs, float[] t) {
        float offsetX = t[0], offsetY = t[1], scaleX = t[2], scaleY = t[3], rotation = t[4];
        if (offsetX == 0f && offsetY == 0f && scaleX == 1f && scaleY == 1f && rotation == 0f) {
            return;
        }

        float cos = (float) Math.cos(rotation);
        float sin = (float) Math.sin(rotation);
        for (float[] uv : uvs) {
            float u = uv[0] * scaleX;
            float v = uv[1] * scaleY;

            uv[0] = wrapUnit(cos * u - sin * v + offsetX);
            uv[1] = wrapUnit(sin * u + cos * v + offsetY);
        }
    }

    private static float wrapUnit(float value) {
        float wrapped = value % 1f;
        return wrapped < 0f ? wrapped + 1f : wrapped;
    }

    private static int[] identityIndices(int count) {
        int[] out = new int[count];
        for (int i = 0; i < count; i++) out[i] = i;
        return out;
    }

    private static ObjModel.Vertex buildVertex(float[][] positions, float[][] normals, float[][] uvs, int idx,
                                                 Matrix4f world, Matrix3f normalMatrix) {
        ObjModel.Vertex v = new ObjModel.Vertex();

        Vector3f pos = new Vector3f(positions[idx][0], positions[idx][1], positions[idx][2]);
        world.transformPosition(pos);
        v.x = pos.x;
        v.y = pos.y;
        v.z = pos.z;

        if (normals != null) {
            Vector3f n = new Vector3f(normals[idx][0], normals[idx][1], normals[idx][2]);
            normalMatrix.transform(n).normalize();
            v.nx = n.x;
            v.ny = n.y;
            v.nz = n.z;
        }

        if (uvs != null) {

            v.u = uvs[idx][0];
            v.v = uvs[idx][1];
        }

        return v;
    }
    //?} else {
    /*private static ObjModel buildModel(JsonObject root, byte[] binChunk, Map<String, MtlMaterial> materials, String modelId) {
        ObjModel model = new ObjModel();
        if (!root.has("nodes") || !root.has("meshes")) {
            return model;
        }

        JsonArray nodes = root.getAsJsonArray("nodes");
        JsonArray rootNodeIndices = resolveRootNodes(root, nodes);

        int nodeCount = nodes.size();
        com.mojang.math.Matrix4f[] worldTransforms = new com.mojang.math.Matrix4f[nodeCount];
        boolean[] wheelCandidateByNode = new boolean[nodeCount];
        boolean[] rotorCandidateByNode = new boolean[nodeCount];
        int[] parentNodeIndex = new int[nodeCount];
        Arrays.fill(parentNodeIndex, -1);
        com.mojang.math.Matrix4f identity = new com.mojang.math.Matrix4f();
        identity.setIdentity();
        for (JsonElement el : rootNodeIndices) {
            collectNodeTransforms(nodes, el.getAsInt(), identity, false, false, -1,
                    worldTransforms, wheelCandidateByNode, rotorCandidateByNode, parentNodeIndex);
        }

        for (int i = 0; i < nodeCount; i++) {
            if (worldTransforms[i] == null) continue;
            JsonObject node = nodes.get(i).getAsJsonObject();
            if (!node.has("mesh")) continue;
            SkinContext skinContext = node.has("skin")
                    ? buildSkinContext(root, binChunk, node.get("skin").getAsInt(),
                            worldTransforms, wheelCandidateByNode, rotorCandidateByNode, parentNodeIndex)
                    : null;
            appendMesh(root, binChunk, node.get("mesh").getAsInt(), worldTransforms[i],
                    wheelCandidateByNode[i], rotorCandidateByNode[i], i, model, skinContext);
        }

        ObjModel.resolveWheelGroups(model);
        ObjModel.resolveRotorGroups(model);

        List<ObjModel.Triangle> opaqueTriangles = new ArrayList<>();
        List<ObjModel.Triangle> translucentTriangles = new ArrayList<>();
        for (ObjModel.Triangle tri : model.triangles) {
            MtlMaterial material = materials.get(tri.materialName);
            if (material != null && material.translucent) {
                ObjModel.Vertex tmp = tri.v2;
                tri.v2 = tri.v3;
                tri.v3 = tmp;
                translucentTriangles.add(tri);
            } else {
                opaqueTriangles.add(tri);
            }
        }
        ObjModel.removeOverlappingDuplicates(opaqueTriangles);
        ObjModel.fixInconsistentWinding(opaqueTriangles, modelId);
        model.triangles.clear();
        model.triangles.addAll(opaqueTriangles);
        model.triangles.addAll(translucentTriangles);

        return model;
    }

    private static JsonArray resolveRootNodes(JsonObject root, JsonArray nodes) {
        int sceneIndex = root.has("scene") ? root.get("scene").getAsInt() : 0;
        if (root.has("scenes") && root.getAsJsonArray("scenes").size() > sceneIndex) {
            JsonObject scene = root.getAsJsonArray("scenes").get(sceneIndex).getAsJsonObject();
            if (scene.has("nodes")) {
                return scene.getAsJsonArray("nodes");
            }
        }
        JsonArray all = new JsonArray();
        for (int i = 0; i < nodes.size(); i++) all.add(i);
        return all;
    }

    private static void collectNodeTransforms(JsonArray nodes, int nodeIndex,
                                               com.mojang.math.Matrix4f parentTransform, boolean wheelCandidateAncestor,
                                               boolean rotorCandidateAncestor, int parentIndex,
                                               com.mojang.math.Matrix4f[] worldTransforms, boolean[] wheelCandidateByNode,
                                               boolean[] rotorCandidateByNode, int[] parentNodeIndex) {
        JsonObject node = nodes.get(nodeIndex).getAsJsonObject();
        com.mojang.math.Matrix4f world = parentTransform.copy();
        world.multiply(readNodeTransform(node));
        String nodeName = node.has("name") ? node.get("name").getAsString() : null;
        boolean wheelCandidate = wheelCandidateAncestor || ObjModel.isWheelCandidateName(nodeName);
        boolean rotorCandidate = rotorCandidateAncestor || ObjModel.isRotorCandidateName(nodeName);

        worldTransforms[nodeIndex] = world;
        wheelCandidateByNode[nodeIndex] = wheelCandidate;
        rotorCandidateByNode[nodeIndex] = rotorCandidate;
        parentNodeIndex[nodeIndex] = parentIndex;

        if (node.has("children")) {
            for (JsonElement child : node.getAsJsonArray("children")) {
                collectNodeTransforms(nodes, child.getAsInt(), world, wheelCandidate, rotorCandidate, nodeIndex,
                        worldTransforms, wheelCandidateByNode, rotorCandidateByNode, parentNodeIndex);
            }
        }
    }

    private static com.mojang.math.Matrix4f readNodeTransform(JsonObject node) {
        if (node.has("matrix")) {
            JsonArray m = node.getAsJsonArray("matrix");
            float[] values = new float[16];
            for (int i = 0; i < 16; i++) values[i] = m.get(i).getAsFloat();
            return mineturismo$matrix4fFromColumnMajor(values);
        }
        com.mojang.math.Vector3f translation = readVec3(node, "translation", 0f, 0f, 0f);
        com.mojang.math.Quaternion rotation = readQuaternion(node);
        com.mojang.math.Vector3f scale = readVec3(node, "scale", 1f, 1f, 1f);
        com.mojang.math.Matrix4f result = new com.mojang.math.Matrix4f();
        result.setIdentity();

        result.multiply(com.mojang.math.Matrix4f.createTranslateMatrix(translation.x(), translation.y(), translation.z()));
        result.multiply(rotation);
        result.multiply(com.mojang.math.Matrix4f.createScaleMatrix(scale.x(), scale.y(), scale.z()));
        return result;
    }

    private static com.mojang.math.Vector3f readVec3(JsonObject node, String key, float dx, float dy, float dz) {
        if (!node.has(key)) return new com.mojang.math.Vector3f(dx, dy, dz);
        JsonArray a = node.getAsJsonArray(key);
        return new com.mojang.math.Vector3f(a.get(0).getAsFloat(), a.get(1).getAsFloat(), a.get(2).getAsFloat());
    }

    private static com.mojang.math.Quaternion readQuaternion(JsonObject node) {
        if (!node.has("rotation")) return new com.mojang.math.Quaternion(0f, 0f, 0f, 1f);
        JsonArray a = node.getAsJsonArray("rotation");
        return new com.mojang.math.Quaternion(a.get(0).getAsFloat(), a.get(1).getAsFloat(), a.get(2).getAsFloat(), a.get(3).getAsFloat());
    }

    private record SkinContext(List<Integer> jointNodeIndices, com.mojang.math.Matrix4f[] skinMatrices,
                                boolean[] wheelCandidateByNode, boolean[] rotorCandidateByNode) {
    }

    private static SkinContext buildSkinContext(JsonObject root, byte[] binChunk, int skinIndex,
                                                  com.mojang.math.Matrix4f[] worldTransforms, boolean[] wheelCandidateByNode,
                                                  boolean[] rotorCandidateByNode, int[] parentNodeIndex) {
        JsonObject skin = root.getAsJsonArray("skins").get(skinIndex).getAsJsonObject();
        JsonArray jointsJson = skin.getAsJsonArray("joints");
        List<Integer> jointNodeIndices = new ArrayList<>(jointsJson.size());
        for (JsonElement el : jointsJson) jointNodeIndices.add(el.getAsInt());

        com.mojang.math.Matrix4f[] skinMatrices = new com.mojang.math.Matrix4f[jointNodeIndices.size()];
        float[][] inverseBindMatrices = skin.has("inverseBindMatrices")
                ? readAccessorVectors(root, binChunk, skin.get("inverseBindMatrices").getAsInt())
                : null;
        for (int j = 0; j < jointNodeIndices.size(); j++) {
            int jointNode = jointNodeIndices.get(j);
            com.mojang.math.Matrix4f jointWorld = worldTransforms[jointNode] != null ? worldTransforms[jointNode] : new com.mojang.math.Matrix4f();
            com.mojang.math.Matrix4f inverseBind;
            if (inverseBindMatrices != null) {
                inverseBind = mineturismo$matrix4fFromColumnMajor(inverseBindMatrices[j]);
            } else {
                inverseBind = new com.mojang.math.Matrix4f();
                inverseBind.setIdentity();
            }
            com.mojang.math.Matrix4f skinMatrix = jointWorld.copy();
            skinMatrix.multiply(inverseBind);
            skinMatrices[j] = skinMatrix;
        }

        if (skinMatrices.length > 0) {
            int rootJointNode = jointNodeIndices.get(0);
            int enclosingAncestor = parentNodeIndex[rootJointNode];
            if (enclosingAncestor >= 0 && worldTransforms[enclosingAncestor] != null) {
                com.mojang.math.Matrix4f ancestorWorld = worldTransforms[enclosingAncestor];
                com.mojang.math.Matrix3f upper3x3 = new com.mojang.math.Matrix3f(ancestorWorld);
                org.apache.commons.lang3.tuple.Triple<com.mojang.math.Quaternion, com.mojang.math.Vector3f, com.mojang.math.Quaternion> svd =
                        upper3x3.svdDecompose();
                com.mojang.math.Quaternion ancestorRotation = svd.getLeft().copy();
                ancestorRotation.mul(svd.getRight());
                com.mojang.math.Vector3f ancestorScale = svd.getMiddle();
                float scale = (ancestorScale.x() + ancestorScale.y() + ancestorScale.z()) / 3f;
                com.mojang.math.Quaternion invRotation = ancestorRotation.copy();
                invRotation.conj();
                com.mojang.math.Matrix4f correction = new com.mojang.math.Matrix4f(invRotation);
                correction.multiply(com.mojang.math.Matrix4f.createScaleMatrix(scale, scale, scale));
                for (int j = 0; j < skinMatrices.length; j++) {
                    com.mojang.math.Matrix4f updated = correction.copy();
                    updated.multiply(skinMatrices[j]);
                    skinMatrices[j] = updated;
                }
            }
        }
        return new SkinContext(jointNodeIndices, skinMatrices, wheelCandidateByNode, rotorCandidateByNode);
    }

    private static void appendMesh(JsonObject root, byte[] binChunk, int meshIndex, com.mojang.math.Matrix4f world,
                                    boolean wheelCandidate, boolean rotorCandidate, int meshOwnerNodeIndex,
                                    ObjModel model, SkinContext skinContext) {
        JsonObject mesh = root.getAsJsonArray("meshes").get(meshIndex).getAsJsonObject();
        com.mojang.math.Matrix3f normalMatrix = new com.mojang.math.Matrix3f(world);
        normalMatrix.invert();
        normalMatrix.transpose();
        boolean mirrored = skinContext == null && mineturismo$isMirrored(world);

        for (JsonElement primEl : mesh.getAsJsonArray("primitives")) {
            JsonObject primitive = primEl.getAsJsonObject();
            int mode = primitive.has("mode") ? primitive.get("mode").getAsInt() : 4;
            if (mode != 4) {
                AiObjectMod.LOGGER.warn("Skipping GLB primitive with unsupported mode " + mode);
                continue;
            }

            JsonObject attributes = primitive.getAsJsonObject("attributes");
            if (attributes == null || !attributes.has("POSITION")) continue;

            float[][] positions = readAccessorVectors(root, binChunk, attributes.get("POSITION").getAsInt());
            float[][] normals = attributes.has("NORMAL")
                    ? readAccessorVectors(root, binChunk, attributes.get("NORMAL").getAsInt())
                    : null;
            float[][] uvs = attributes.has("TEXCOORD_0")
                    ? readAccessorVectors(root, binChunk, attributes.get("TEXCOORD_0").getAsInt())
                    : null;
            int[] indices = primitive.has("indices")
                    ? readAccessorIndices(root, binChunk, primitive.get("indices").getAsInt())
                    : identityIndices(positions.length);

            String materialName = primitive.has("material") ? "material_" + primitive.get("material").getAsInt() : null;

            if (uvs != null && primitive.has("material")) {
                applyTextureTransform(uvs, readTextureTransform(root, primitive.get("material").getAsInt()));
            }

            float[][] joints0 = null;
            float[][] weights0 = null;
            if (skinContext != null && attributes.has("JOINTS_0") && attributes.has("WEIGHTS_0")) {
                joints0 = readAccessorVectors(root, binChunk, attributes.get("JOINTS_0").getAsInt());
                weights0 = readAccessorVectors(root, binChunk, attributes.get("WEIGHTS_0").getAsInt());
            }
            boolean skinned = joints0 != null;

            for (int i = 0; i + 2 < indices.length; i += 3) {
                int rawI1 = indices[i];
                int rawI2 = indices[i + 1];
                int rawI3 = indices[i + 2];

                ObjModel.Triangle tri = new ObjModel.Triangle();
                tri.materialName = materialName;

                if (skinned) {
                    int dominantLocalJoint = dominantJoint(joints0[rawI1], weights0[rawI1]);
                    com.mojang.math.Matrix4f skinMatrix = skinContext.skinMatrices()[dominantLocalJoint];
                    int jointNodeIndex = skinContext.jointNodeIndices().get(dominantLocalJoint);
                    boolean triMirrored = mineturismo$isMirrored(skinMatrix);
                    int i2 = triMirrored ? rawI3 : rawI2;
                    int i3 = triMirrored ? rawI2 : rawI3;
                    tri.v1 = buildSkinnedVertex(positions, uvs, rawI1, joints0, weights0, skinContext);
                    tri.v2 = buildSkinnedVertex(positions, uvs, i2, joints0, weights0, skinContext);
                    tri.v3 = buildSkinnedVertex(positions, uvs, i3, joints0, weights0, skinContext);
                    tri.wheelCandidate = wheelCandidate || skinContext.wheelCandidateByNode()[jointNodeIndex];
                    tri.rotorCandidate = rotorCandidate || skinContext.rotorCandidateByNode()[jointNodeIndex];
                    tri.rotorSourceNode = jointNodeIndex;
                } else {
                    int i2 = mirrored ? rawI3 : rawI2;
                    int i3 = mirrored ? rawI2 : rawI3;
                    tri.v1 = buildVertex(positions, normals, uvs, rawI1, world, normalMatrix);
                    tri.v2 = buildVertex(positions, normals, uvs, i2, world, normalMatrix);
                    tri.v3 = buildVertex(positions, normals, uvs, i3, world, normalMatrix);
                    tri.wheelCandidate = wheelCandidate;
                    tri.rotorCandidate = rotorCandidate;
                    tri.rotorSourceNode = meshOwnerNodeIndex;
                }
                model.triangles.add(tri);
            }
        }
    }

    private static int dominantJoint(float[] joints0, float[] weights0) {
        int best = 0;
        float bestWeight = -1f;
        for (int i = 0; i < 4; i++) {
            if (weights0[i] > bestWeight) {
                bestWeight = weights0[i];
                best = i;
            }
        }
        return Math.round(joints0[best]);
    }

    private static ObjModel.Vertex buildSkinnedVertex(float[][] positions, float[][] uvs, int idx,
                                                        float[][] joints0, float[][] weights0, SkinContext skinContext) {
        ObjModel.Vertex v = new ObjModel.Vertex();

        float x = 0f, y = 0f, z = 0f;
        float[] localPos = positions[idx];
        float[] j = joints0[idx];
        float[] w = weights0[idx];
        for (int i = 0; i < 4; i++) {
            if (w[i] <= 0f) continue;
            int jointLocal = Math.round(j[i]);
            com.mojang.math.Vector4f p = new com.mojang.math.Vector4f(localPos[0], localPos[1], localPos[2], 1f);
            p.transform(skinContext.skinMatrices()[jointLocal]);
            x += p.x() * w[i];
            y += p.y() * w[i];
            z += p.z() * w[i];
        }
        v.x = x;
        v.y = y;
        v.z = z;

        if (uvs != null) {
            v.u = uvs[idx][0];
            v.v = uvs[idx][1];
        }

        return v;
    }

    private static float[] readTextureTransform(JsonObject root, int materialIndex) {
        float[] identity = {0f, 0f, 1f, 1f, 0f};
        JsonObject material = root.getAsJsonArray("materials").get(materialIndex).getAsJsonObject();
        if (!material.has("pbrMetallicRoughness")) return identity;
        JsonObject pbr = material.getAsJsonObject("pbrMetallicRoughness");
        if (!pbr.has("baseColorTexture")) return identity;
        JsonObject texInfo = pbr.getAsJsonObject("baseColorTexture");
        if (!texInfo.has("extensions")) return identity;
        JsonObject extensions = texInfo.getAsJsonObject("extensions");
        if (!extensions.has("KHR_texture_transform")) return identity;

        JsonObject transform = extensions.getAsJsonObject("KHR_texture_transform");
        float offsetX = 0f, offsetY = 0f, scaleX = 1f, scaleY = 1f, rotation = 0f;
        if (transform.has("offset")) {
            JsonArray o = transform.getAsJsonArray("offset");
            offsetX = o.get(0).getAsFloat();
            offsetY = o.get(1).getAsFloat();
        }
        if (transform.has("scale")) {
            JsonArray s = transform.getAsJsonArray("scale");
            scaleX = s.get(0).getAsFloat();
            scaleY = s.get(1).getAsFloat();
        }
        if (transform.has("rotation")) {
            rotation = transform.get("rotation").getAsFloat();
        }
        return new float[]{offsetX, offsetY, scaleX, scaleY, rotation};
    }

    private static void applyTextureTransform(float[][] uvs, float[] t) {
        float offsetX = t[0], offsetY = t[1], scaleX = t[2], scaleY = t[3], rotation = t[4];
        if (offsetX == 0f && offsetY == 0f && scaleX == 1f && scaleY == 1f && rotation == 0f) {
            return;
        }
        float cos = (float) Math.cos(rotation);
        float sin = (float) Math.sin(rotation);
        for (float[] uv : uvs) {
            float u = uv[0] * scaleX;
            float v = uv[1] * scaleY;
            uv[0] = wrapUnit(cos * u - sin * v + offsetX);
            uv[1] = wrapUnit(sin * u + cos * v + offsetY);
        }
    }

    private static float wrapUnit(float value) {
        float wrapped = value % 1f;
        return wrapped < 0f ? wrapped + 1f : wrapped;
    }

    private static int[] identityIndices(int count) {
        int[] out = new int[count];
        for (int i = 0; i < count; i++) out[i] = i;
        return out;
    }

    private static ObjModel.Vertex buildVertex(float[][] positions, float[][] normals, float[][] uvs, int idx,
                                                 com.mojang.math.Matrix4f world, com.mojang.math.Matrix3f normalMatrix) {
        ObjModel.Vertex v = new ObjModel.Vertex();

        com.mojang.math.Vector4f posH = new com.mojang.math.Vector4f(positions[idx][0], positions[idx][1], positions[idx][2], 1f);
        posH.transform(world);
        v.x = posH.x();
        v.y = posH.y();
        v.z = posH.z();

        if (normals != null) {
            com.mojang.math.Vector3f n = new com.mojang.math.Vector3f(normals[idx][0], normals[idx][1], normals[idx][2]);
            n.transform(normalMatrix);
            n.normalize();
            v.nx = n.x();
            v.ny = n.y();
            v.nz = n.z();
        }

        if (uvs != null) {
            v.u = uvs[idx][0];
            v.v = uvs[idx][1];
        }

        return v;
    }*/
    //?}

    //? if >=1.19.3 {
    //?}
    //? if >=1.17 && <1.19.3 {
    /*private static com.mojang.math.Matrix4f mineturismo$matrix4fFromColumnMajor(float[] values) {
        com.mojang.math.Matrix4f result = new com.mojang.math.Matrix4f();
        result.load(java.nio.FloatBuffer.wrap(values));
        return result;
    }

    private static boolean mineturismo$isMirrored(com.mojang.math.Matrix4f matrix) {
        return matrix.determinant() < 0f;
    }*/
    //?}
    //? if <1.17 {
    /*private static com.mojang.math.Matrix4f mineturismo$matrix4fFromColumnMajor(float[] values) {
        AiObjectMod.LOGGER.warn("GLB node uses an explicit matrix; falling back to identity on 1.16.5 (unsupported, no .glb assets ship for this version)");
        return new com.mojang.math.Matrix4f();
    }

    private static boolean mineturismo$isMirrored(com.mojang.math.Matrix4f matrix) {
        return false;
    }*/
    //?}

    private static int componentSize(int componentType) {
        return switch (componentType) {
            case 5120, 5121 -> 1;
            case 5122, 5123 -> 2;
            case 5125, 5126 -> 4;
            default -> throw new IllegalStateException("Unsupported glTF componentType " + componentType);
        };
    }

    private static int numComponents(String type) {
        return switch (type) {
            case "SCALAR" -> 1;
            case "VEC2" -> 2;
            case "VEC3" -> 3;
            case "VEC4" -> 4;
            case "MAT4" -> 16;
            default -> throw new IllegalStateException("Unsupported glTF accessor type " + type);
        };
    }

    private static float[][] readAccessorVectors(JsonObject root, byte[] binChunk, int accessorIndex) {
        JsonObject accessor = root.getAsJsonArray("accessors").get(accessorIndex).getAsJsonObject();
        int count = accessor.get("count").getAsInt();
        int comps = numComponents(accessor.get("type").getAsString());
        float[][] out = new float[count][comps];

        if (!accessor.has("bufferView")) {
            return out;
        }

        int componentType = accessor.get("componentType").getAsInt();
        boolean normalized = accessor.has("normalized") && accessor.get("normalized").getAsBoolean();
        int accessorByteOffset = accessor.has("byteOffset") ? accessor.get("byteOffset").getAsInt() : 0;

        JsonObject bv = root.getAsJsonArray("bufferViews").get(accessor.get("bufferView").getAsInt()).getAsJsonObject();
        int bvByteOffset = bv.has("byteOffset") ? bv.get("byteOffset").getAsInt() : 0;
        int compSize = componentSize(componentType);
        int byteStride = bv.has("byteStride") ? bv.get("byteStride").getAsInt() : comps * compSize;

        ByteBuffer buf = ByteBuffer.wrap(binChunk).order(ByteOrder.LITTLE_ENDIAN);
        int base = bvByteOffset + accessorByteOffset;
        for (int i = 0; i < count; i++) {
            int elementOffset = base + i * byteStride;
            for (int c = 0; c < comps; c++) {
                int off = elementOffset + c * compSize;
                out[i][c] = readComponent(buf, off, componentType, normalized);
            }
        }
        return out;
    }

    private static float readComponent(ByteBuffer buf, int off, int componentType, boolean normalized) {
        return switch (componentType) {
            case 5126 -> buf.getFloat(off);
            case 5121 -> {
                int v = buf.get(off) & 0xFF;
                yield normalized ? v / 255f : v;
            }
            case 5123 -> {
                int v = buf.getShort(off) & 0xFFFF;
                yield normalized ? v / 65535f : v;
            }
            case 5120 -> {
                int v = buf.get(off);
                yield normalized ? Math.max(v / 127f, -1f) : v;
            }
            case 5122 -> {
                int v = buf.getShort(off);
                yield normalized ? Math.max(v / 32767f, -1f) : v;
            }
            default -> throw new IllegalStateException("Unsupported glTF componentType " + componentType);
        };
    }

    private static int[] readAccessorIndices(JsonObject root, byte[] binChunk, int accessorIndex) {
        JsonObject accessor = root.getAsJsonArray("accessors").get(accessorIndex).getAsJsonObject();
        int count = accessor.get("count").getAsInt();
        int componentType = accessor.get("componentType").getAsInt();
        int accessorByteOffset = accessor.has("byteOffset") ? accessor.get("byteOffset").getAsInt() : 0;

        JsonObject bv = root.getAsJsonArray("bufferViews").get(accessor.get("bufferView").getAsInt()).getAsJsonObject();
        int bvByteOffset = bv.has("byteOffset") ? bv.get("byteOffset").getAsInt() : 0;
        int compSize = componentSize(componentType);
        int byteStride = bv.has("byteStride") ? bv.get("byteStride").getAsInt() : compSize;

        ByteBuffer buf = ByteBuffer.wrap(binChunk).order(ByteOrder.LITTLE_ENDIAN);
        int base = bvByteOffset + accessorByteOffset;
        int[] out = new int[count];
        for (int i = 0; i < count; i++) {
            int off = base + i * byteStride;
            out[i] = switch (componentType) {
                case 5121 -> buf.get(off) & 0xFF;
                case 5123 -> buf.getShort(off) & 0xFFFF;
                case 5125 -> buf.getInt(off);
                default -> throw new IllegalStateException("Unsupported glTF index componentType " + componentType);
            };
        }
        return out;
    }

    private static byte[] readBufferViewBytes(JsonObject root, byte[] binChunk, int bufferViewIndex) {
        JsonObject bv = root.getAsJsonArray("bufferViews").get(bufferViewIndex).getAsJsonObject();
        int byteOffset = bv.has("byteOffset") ? bv.get("byteOffset").getAsInt() : 0;
        int byteLength = bv.get("byteLength").getAsInt();
        byte[] out = new byte[byteLength];
        System.arraycopy(binChunk, byteOffset, out, 0, byteLength);
        return out;
    }

    private static Map<String, MtlMaterial> buildMaterials(JsonObject root, byte[] binChunk, String modelPath) {
        Map<String, MtlMaterial> materials = new HashMap<>();
        if (!root.has("materials")) return materials;

        JsonArray materialsJson = root.getAsJsonArray("materials");
        for (int i = 0; i < materialsJson.size(); i++) {
            JsonObject materialJson = materialsJson.get(i).getAsJsonObject();
            MtlMaterial material = new MtlMaterial();

            if (materialJson.has("pbrMetallicRoughness")) {
                JsonObject pbr = materialJson.getAsJsonObject("pbrMetallicRoughness");
                if (pbr.has("baseColorFactor")) {
                    JsonArray c = pbr.getAsJsonArray("baseColorFactor");
                    material.r = c.get(0).getAsFloat();
                    material.g = c.get(1).getAsFloat();
                    material.b = c.get(2).getAsFloat();
                    if (c.size() > 3) {
                        material.a = c.get(3).getAsFloat();
                    }
                }
                if (pbr.has("baseColorTexture")) {
                    int textureIndex = pbr.getAsJsonObject("baseColorTexture").get("index").getAsInt();
                    TextureResolution resolution = resolveTexture(root, binChunk, textureIndex, modelPath);
                    if (resolution != null) {
                        material.texture = resolution.location();
                        material.tintable = resolution.tintable();
                    }
                }
            }

            if (material.tintable && materialJson.has("name")
                    && ObjModel.isPlateCandidateName(materialJson.get("name").getAsString())) {
                material.tintable = false;
            }

            material.translucent = materialJson.has("alphaMode")
                    && "BLEND".equals(materialJson.get("alphaMode").getAsString());

            material.doubleSided = materialJson.has("doubleSided")
                    && materialJson.get("doubleSided").getAsBoolean();

            materials.put("material_" + i, material);
        }
        return materials;
    }

    private static TextureResolution resolveTexture(JsonObject root, byte[] binChunk, int textureIndex, String modelPath) {
        JsonObject texture = root.getAsJsonArray("textures").get(textureIndex).getAsJsonObject();
        if (!texture.has("source")) return null;
        int imageIndex = texture.get("source").getAsInt();

        String cacheKey = modelPath + "#" + imageIndex;
        TextureResolution cached = textureCache.get(cacheKey);
        if (cached != null) return cached;

        TextureResolution fileBacked = resolveExtractedFileTexture(modelPath, imageIndex, cacheKey);
        if (fileBacked != null) {
            return fileBacked;
        }

        JsonObject image = root.getAsJsonArray("images").get(imageIndex).getAsJsonObject();
        byte[] imageBytes;
        if (image.has("bufferView")) {
            imageBytes = readBufferViewBytes(root, binChunk, image.get("bufferView").getAsInt());
        } else if (image.has("uri") && image.get("uri").getAsString().startsWith("data:")) {
            imageBytes = decodeDataUri(image.get("uri").getAsString());
        } else {
            AiObjectMod.LOGGER.warn("GLB image uses an unsupported external URI, texture skipped: " + modelPath);
            return null;
        }

        try {
            DecodedImage decoded = decodeImage(imageBytes);

            //? if >=1.21.5 {
            DynamicTexture dynamicTexture = new DynamicTexture(() -> "mineturismo glb texture " + modelPath, decoded.nativeImage());
            //?} else
            /*DynamicTexture dynamicTexture = new DynamicTexture(decoded.nativeImage());*/
            ResourceLocation location = AiObjectMod.id("dynamic/" + modelPath.toLowerCase(Locale.ROOT) + "_tex" + imageIndex);
            Minecraft.getInstance().getTextureManager().register(location, dynamicTexture);
            PaintTintTexture.register(location, decoded.argbPixels(), decoded.width(), decoded.height());
            boolean tintable = PaintTintTexture.hasPaint(location) && !isPlateShapedTexture(decoded.width(), decoded.height());
            TextureResolution resolution = new TextureResolution(location, tintable);
            textureCache.put(cacheKey, resolution);
            return resolution;
        } catch (IOException e) {
            AiObjectMod.LOGGER.error("Failed to decode embedded GLB texture for " + modelPath, e);
            return null;
        }
    }

    private static final float PLATE_ASPECT_RATIO_THRESHOLD = 3.0f;

    private static boolean isPlateShapedTexture(int width, int height) {
        if (width <= 0 || height <= 0) return false;
        float ratio = (float) Math.max(width, height) / Math.min(width, height);
        return ratio >= PLATE_ASPECT_RATIO_THRESHOLD;
    }

    private static TextureResolution resolveExtractedFileTexture(String modelPath, int imageIndex, String cacheKey) {
        ResourceLocation fileLocation = AiObjectMod.id(
                "textures/entity/" + modelPath.toLowerCase(Locale.ROOT) + "/embedded_" + imageIndex + ".png");
        ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
        Optional<Resource> resource = com.examplebett.compat.ModResources.getResourceOptional(resourceManager, fileLocation);
        if (resource.isEmpty()) {
            return null;
        }
        try {
            byte[] fileBytes;
            try (InputStream in = com.examplebett.compat.ModResources.open(resource.get())) {
                fileBytes = in.readAllBytes();
            }
            DecodedImage decoded = decodeImage(fileBytes);
            decoded.nativeImage().close();
            PaintTintTexture.register(fileLocation, decoded.argbPixels(), decoded.width(), decoded.height());
            boolean tintable = PaintTintTexture.hasPaint(fileLocation) && !isPlateShapedTexture(decoded.width(), decoded.height());
            TextureResolution resolution = new TextureResolution(fileLocation, tintable);
            textureCache.put(cacheKey, resolution);
            return resolution;
        } catch (IOException e) {
            AiObjectMod.LOGGER.error("Failed to read extracted GLB texture file, falling back to embedded: " + fileLocation, e);
            return null;
        }
    }

    private static byte[] decodeDataUri(String uri) {
        int comma = uri.indexOf(',');
        return Base64.getDecoder().decode(uri.substring(comma + 1));
    }

    private static final int MAX_TEXTURE_DIMENSION = 512;

    private record DecodedImage(NativeImage nativeImage, int[] argbPixels, int width, int height) {
    }

    private static DecodedImage decodeImage(byte[] imageBytes) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imageBytes));
        if (bufferedImage == null) {
            throw new IOException("Unsupported or corrupt image data");
        }

        bufferedImage = downscaleIfNeeded(bufferedImage);

        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        int[] argbPixels = bufferedImage.getRGB(0, 0, width, height, null, 0, width);

        NativeImage nativeImage = new NativeImage(width, height, false);
        for (int y = 0; y < height; y++) {
            int rowOffset = y * width;
            for (int x = 0; x < width; x++) {

                //? if >=1.21.2 {
                nativeImage.setPixel(x, y, argbPixels[rowOffset + x]);
                //?} else {
                /*int argb = argbPixels[rowOffset + x];
                int abgr = (argb & 0xFF00FF00) | ((argb >> 16) & 0xFF) | ((argb & 0xFF) << 16);
                nativeImage.setPixelRGBA(x, y, abgr);*/
                //?}
            }
        }
        return new DecodedImage(nativeImage, argbPixels, width, height);
    }

    private static BufferedImage downscaleIfNeeded(BufferedImage source) {
        int width = source.getWidth();
        int height = source.getHeight();
        int longestSide = Math.max(width, height);
        if (longestSide <= MAX_TEXTURE_DIMENSION) {
            return source;
        }

        double scale = (double) MAX_TEXTURE_DIMENSION / longestSide;
        int scaledWidth = Math.max(1, (int) Math.round(width * scale));
        int scaledHeight = Math.max(1, (int) Math.round(height * scale));

        BufferedImage scaled = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = scaled.createGraphics();
        try {
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.drawImage(source, 0, 0, scaledWidth, scaledHeight, null);
        } finally {
            graphics.dispose();
        }
        return scaled;
    }

    private GlbModelLoader() {
    }
}
