package com.examplebett.render;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ObjModel {

    public static class Vertex {
        public float x, y, z;
        public float u, v;
        public float nx, ny, nz;
    }

    public static class Triangle {
        public Vertex v1, v2, v3;
        public String materialName;

        public boolean uncertainWinding;

        public boolean wheelCandidate;

        public int wheelGroup = -1;

        public boolean rotorCandidate;

        public int rotorGroup = -1;

        public int rotorSourceNode = -1;
    }

    public static class WheelPivot {
        public final float centerX, centerY, centerZ;
        public final float axisX, axisY, axisZ;
        public final float radius;

        public final boolean front;

        public WheelPivot(float centerX, float centerY, float centerZ,
                           float axisX, float axisY, float axisZ, float radius, boolean front) {
            this.centerX = centerX;
            this.centerY = centerY;
            this.centerZ = centerZ;
            this.axisX = axisX;
            this.axisY = axisY;
            this.axisZ = axisZ;
            this.radius = radius;
            this.front = front;
        }
    }

    public static class RotorPivot {
        public final float centerX, centerY, centerZ;
        public final float axisX, axisY, axisZ;

        public RotorPivot(float centerX, float centerY, float centerZ,
                           float axisX, float axisY, float axisZ) {
            this.centerX = centerX;
            this.centerY = centerY;
            this.centerZ = centerZ;
            this.axisX = axisX;
            this.axisY = axisY;
            this.axisZ = axisZ;
        }
    }

    public final List<Triangle> triangles = new ArrayList<>();
    public final List<WheelPivot> wheelPivots = new ArrayList<>();
    public final List<RotorPivot> rotorPivots = new ArrayList<>();

    public static ObjModel load(InputStream stream) throws IOException {
        List<float[]> positions = new ArrayList<>();
        List<float[]> uvs = new ArrayList<>();
        List<float[]> normals = new ArrayList<>();

        List<int[][]> faceVertexGroups = new ArrayList<>();
        List<String> faceMaterials = new ArrayList<>();

        List<String> faceGroupNames = new ArrayList<>();

        String currentMaterial = null;
        String currentGroupName = null;

        float unitScale = 1.0f;

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(stream, StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                if (line.startsWith("#")) {
                    if (line.toLowerCase(Locale.ROOT).contains("file units")) {
                        unitScale = parseUnitScale(line);
                    }
                    continue;
                }

                String[] parts = line.split("\\s+");
                switch (parts[0]) {
                    case "v" -> positions.add(new float[]{
                            Float.parseFloat(parts[1]) * unitScale,
                            Float.parseFloat(parts[2]) * unitScale,
                            Float.parseFloat(parts[3]) * unitScale
                    });
                    case "vt" -> uvs.add(new float[]{
                            Float.parseFloat(parts[1]),
                            Float.parseFloat(parts[2])
                    });
                    case "vn" -> normals.add(new float[]{
                            Float.parseFloat(parts[1]),
                            Float.parseFloat(parts[2]),
                            Float.parseFloat(parts[3])
                    });
                    case "usemtl" -> currentMaterial = parts[1];
                    case "o", "g" -> currentGroupName = parts.length > 1 ? parts[1] : null;
                    case "f" -> {
                        int vertexCount = parts.length - 1;
                        int[][] faceVerts = new int[vertexCount][3];
                        for (int i = 0; i < vertexCount; i++) {
                            faceVerts[i] = parseFaceVertex(parts[i + 1]);
                        }
                        for (int i = 1; i < vertexCount - 1; i++) {
                            faceVertexGroups.add(new int[][]{faceVerts[0], faceVerts[i], faceVerts[i + 1]});
                            faceMaterials.add(currentMaterial);
                            faceGroupNames.add(currentGroupName);
                        }
                    }
                }
            }
        }

        ObjModel model = new ObjModel();

        Map<String, Integer> groupNodeIds = new HashMap<>();
        for (int i = 0; i < faceVertexGroups.size(); i++) {
            int[][] face = faceVertexGroups.get(i);
            Triangle tri = new Triangle();
            tri.v1 = buildVertex(face[0], positions, uvs, normals);
            tri.v2 = buildVertex(face[1], positions, uvs, normals);
            tri.v3 = buildVertex(face[2], positions, uvs, normals);
            tri.materialName = faceMaterials.get(i);
            String groupName = faceGroupNames.get(i);
            tri.wheelCandidate = isWheelCandidateName(groupName);
            tri.rotorCandidate = isRotorCandidateName(groupName);
            if (groupName != null) {
                tri.rotorSourceNode = groupNodeIds.computeIfAbsent(groupName, k -> groupNodeIds.size());
            }
            model.triangles.add(tri);
        }

        resolveWheelGroups(model);
        resolveRotorGroups(model);

        removeOverlappingDuplicates(model.triangles);
        fixInconsistentWinding(model.triangles);
        return model;
    }

    private static final java.util.Set<String> KNOWN_BACKWARDS_COMPONENT_MODELS =
            java.util.Set.of("bmw_m3_e46", "mercedes_benz_gclass");

    public static void fixInconsistentWinding(List<Triangle> triangles) {
        fixInconsistentWinding(triangles, null);
    }

    public static void fixInconsistentWinding(List<Triangle> triangles, String modelId) {
        final float epsilon = 0.00002f;
        int n = triangles.size();

        String[] ka = new String[n];
        String[] kb = new String[n];
        String[] kc = new String[n];
        for (int i = 0; i < n; i++) {
            Triangle t = triangles.get(i);

            String groupTag = "#g" + t.wheelGroup + "r" + t.rotorGroup;
            ka[i] = quantizeVertex(t.v1, epsilon) + groupTag;
            kb[i] = quantizeVertex(t.v2, epsilon) + groupTag;
            kc[i] = quantizeVertex(t.v3, epsilon) + groupTag;
        }

        Map<String, List<int[]>> edgeMap = new HashMap<>();
        for (int i = 0; i < n; i++) {
            addEdgeOccurrence(edgeMap, ka[i], kb[i], i);
            addEdgeOccurrence(edgeMap, kb[i], kc[i], i);
            addEdgeOccurrence(edgeMap, kc[i], ka[i], i);
        }

        for (int i = 0; i < n; i++) {
            Triangle t = triangles.get(i);
            if (t.wheelGroup >= 0 || t.rotorGroup >= 0) {
                continue;
            }
            if (edgeOccurrenceCount(edgeMap, ka[i], kb[i]) != 2
                    || edgeOccurrenceCount(edgeMap, kb[i], kc[i]) != 2
                    || edgeOccurrenceCount(edgeMap, kc[i], ka[i]) != 2) {
                t.uncertainWinding = true;
            }
        }

        boolean[] visited = new boolean[n];
        boolean[] flipped = new boolean[n];
        ArrayDeque<Integer> queue = new ArrayDeque<>();

        int[] componentId = modelId != null && KNOWN_BACKWARDS_COMPONENT_MODELS.contains(modelId) ? new int[n] : null;
        if (componentId != null) {
            Arrays.fill(componentId, -1);
        }
        int componentCount = 0;

        for (int start = 0; start < n; start++) {
            if (visited[start] || triangles.get(start).wheelGroup >= 0 || triangles.get(start).rotorGroup >= 0) continue;
            visited[start] = true;
            queue.add(start);
            if (componentId != null) componentId[start] = componentCount;
            while (!queue.isEmpty()) {
                int i = queue.poll();
                propagateEdge(edgeMap, ka[i], kb[i], i, visited, flipped, queue);
                propagateEdge(edgeMap, kb[i], kc[i], i, visited, flipped, queue);
                propagateEdge(edgeMap, kc[i], ka[i], i, visited, flipped, queue);
                if (componentId != null) {
                    for (int qi : queue) {
                        if (componentId[qi] == -1) componentId[qi] = componentCount;
                    }
                }
            }
            componentCount++;
        }

        if (componentId != null) {
            correctKnownBackwardsComponents(triangles, flipped, componentId, componentCount);
        }

        for (int i = 0; i < n; i++) {
            if (flipped[i]) {
                Triangle t = triangles.get(i);
                Vertex tmp = t.v2;
                t.v2 = t.v3;
                t.v3 = tmp;
            }
        }
    }

    private static final float BACKWARDS_COMPONENT_OUTWARD_DOT_THRESHOLD = -0.6f;

    private static void correctKnownBackwardsComponents(List<Triangle> triangles, boolean[] flipped,
                                                          int[] componentId, int componentCount) {
        int n = triangles.size();
        float modelCx = 0, modelCy = 0, modelCz = 0;
        for (Triangle t : triangles) {
            modelCx += (t.v1.x + t.v2.x + t.v3.x) / 3f;
            modelCy += (t.v1.y + t.v2.y + t.v3.y) / 3f;
            modelCz += (t.v1.z + t.v2.z + t.v3.z) / 3f;
        }
        modelCx /= n;
        modelCy /= n;
        modelCz /= n;

        for (int c = 0; c < componentCount; c++) {
            float cx = 0, cy = 0, cz = 0;
            float nx = 0, ny = 0, nz = 0;
            int size = 0;
            for (int i = 0; i < n; i++) {
                if (componentId[i] != c) continue;
                size++;
                Triangle t = triangles.get(i);
                cx += (t.v1.x + t.v2.x + t.v3.x) / 3f;
                cy += (t.v1.y + t.v2.y + t.v3.y) / 3f;
                cz += (t.v1.z + t.v2.z + t.v3.z) / 3f;

                Vertex v1 = t.v1, v2 = flipped[i] ? t.v3 : t.v2, v3 = flipped[i] ? t.v2 : t.v3;
                float ax = v2.x - v1.x, ay = v2.y - v1.y, az = v2.z - v1.z;
                float bx = v3.x - v1.x, by = v3.y - v1.y, bz = v3.z - v1.z;
                float fnx = ay * bz - az * by;
                float fny = az * bx - ax * bz;
                float fnz = ax * by - ay * bx;
                float flen = (float) Math.sqrt(fnx * fnx + fny * fny + fnz * fnz);
                if (flen > 1e-8f) {
                    nx += fnx / flen;
                    ny += fny / flen;
                    nz += fnz / flen;
                }
            }

            if (size < 20) continue;
            cx /= size;
            cy /= size;
            cz /= size;
            float nlen = (float) Math.sqrt(nx * nx + ny * ny + nz * nz);
            if (nlen < 1e-8f) continue;
            nx /= nlen;
            ny /= nlen;
            nz /= nlen;
            float rx = cx - modelCx, ry = cy - modelCy, rz = cz - modelCz;
            float rlen = (float) Math.sqrt(rx * rx + ry * ry + rz * rz);
            if (rlen < 1e-8f) continue;
            rx /= rlen;
            ry /= rlen;
            rz /= rlen;
            float outwardDot = nx * rx + ny * ry + nz * rz;
            if (outwardDot < BACKWARDS_COMPONENT_OUTWARD_DOT_THRESHOLD) {
                for (int i = 0; i < n; i++) {
                    if (componentId[i] == c) {
                        flipped[i] = !flipped[i];
                    }
                }
            }
        }
    }

    private static int edgeOccurrenceCount(Map<String, List<int[]>> edgeMap, String from, String to) {
        List<int[]> occurrences = edgeMap.get(edgeKey(from, to));
        return occurrences == null ? 0 : occurrences.size();
    }

    private static String edgeKey(String from, String to) {
        return from.compareTo(to) <= 0 ? (from + "|" + to) : (to + "|" + from);
    }

    private static void addEdgeOccurrence(Map<String, List<int[]>> edgeMap, String from, String to, int triangleIndex) {
        boolean ascending = from.compareTo(to) <= 0;
        edgeMap.computeIfAbsent(edgeKey(from, to), k -> new ArrayList<>()).add(new int[]{triangleIndex, ascending ? 1 : 0});
    }

    private static void propagateEdge(Map<String, List<int[]>> edgeMap, String from, String to, int triangleIndex,
                                       boolean[] visited, boolean[] flipped, ArrayDeque<Integer> queue) {
        boolean ascending = from.compareTo(to) <= 0;
        List<int[]> occurrences = edgeMap.get(edgeKey(from, to));

        if (occurrences == null || occurrences.size() != 2) return;

        int[] other = occurrences.get(0)[0] == triangleIndex ? occurrences.get(1) : occurrences.get(0);
        int neighborIndex = other[0];
        if (neighborIndex == triangleIndex || visited[neighborIndex]) return;

        boolean currentAscendingOriginal = ascending;
        boolean currentEffectiveAscending = currentAscendingOriginal ^ flipped[triangleIndex];
        boolean neighborAscendingOriginal = other[1] == 1;

        boolean neighborNeedsFlip = neighborAscendingOriginal == currentEffectiveAscending;

        flipped[neighborIndex] = neighborNeedsFlip;
        visited[neighborIndex] = true;
        queue.add(neighborIndex);
    }

    public static void removeOverlappingDuplicates(List<Triangle> triangles) {
        final float epsilon = 0.00002f;

        Map<String, List<Integer>> buckets = new HashMap<>();
        for (int i = 0; i < triangles.size(); i++) {
            Triangle t = triangles.get(i);
            String[] vKeys = {
                    quantizeVertex(t.v1, epsilon),
                    quantizeVertex(t.v2, epsilon),
                    quantizeVertex(t.v3, epsilon)
            };
            Arrays.sort(vKeys);
            String key = vKeys[0] + "/" + vKeys[1] + "/" + vKeys[2];
            buckets.computeIfAbsent(key, k -> new ArrayList<>()).add(i);
        }

        boolean[] removed = new boolean[triangles.size()];
        for (List<Integer> bucket : buckets.values()) {

            for (int a = 1; a < bucket.size(); a++) {
                removed[bucket.get(a)] = true;
            }
        }

        List<Triangle> kept = new ArrayList<>(triangles.size());
        for (int i = 0; i < triangles.size(); i++) {
            if (!removed[i]) kept.add(triangles.get(i));
        }
        triangles.clear();
        triangles.addAll(kept);
    }

    private static String quantizeVertex(Vertex v, float epsilon) {
        long qx = Math.round(v.x / epsilon);
        long qy = Math.round(v.y / epsilon);
        long qz = Math.round(v.z / epsilon);
        return qx + "," + qy + "," + qz;
    }

    private static float parseUnitScale(String comment) {
        String lower = comment.toLowerCase(Locale.ROOT);
        if (lower.contains("millimeter")) return 0.001f;
        if (lower.contains("centimeter")) return 0.01f;
        if (lower.contains("meter")) return 1.0f;
        if (lower.contains("inch")) return 0.0254f;
        if (lower.contains("feet") || lower.contains("foot")) return 0.3048f;
        return 1.0f;
    }

    private static int[] parseFaceVertex(String token) {
        String[] idx = token.split("/");
        int pos = Integer.parseInt(idx[0]) - 1;
        int uv = (idx.length > 1 && !idx[1].isEmpty()) ? Integer.parseInt(idx[1]) - 1 : -1;
        int normal = (idx.length > 2 && !idx[2].isEmpty()) ? Integer.parseInt(idx[2]) - 1 : -1;
        return new int[]{pos, uv, normal};
    }

    private static Vertex buildVertex(int[] idx, List<float[]> positions, List<float[]> uvs, List<float[]> normals) {
        Vertex vert = new Vertex();
        float[] p = positions.get(idx[0]);
        vert.x = p[0];
        vert.y = p[1];
        vert.z = p[2];

        if (idx[1] >= 0 && !uvs.isEmpty()) {
            float[] uv = uvs.get(idx[1]);
            vert.u = uv[0];
            vert.v = 1.0f - uv[1];
        }

        if (idx[2] >= 0 && !normals.isEmpty()) {
            float[] n = normals.get(idx[2]);
            vert.nx = n[0];
            vert.ny = n[1];
            vert.nz = n[2];
        }

        return vert;
    }

    private static final float WHEEL_CLUSTER_DISTANCE = 0.5f;

    private static final int MIN_WHEEL_CLUSTER_TRIANGLES = 4;
    private static final int EXPECTED_WHEEL_COUNT = 4;

    private static final float MIN_WHEEL_RADIUS = 0.15f;
    private static final float MAX_WHEEL_RADIUS = 0.6f;

    private static final float MIN_CLUSTER_SIZE_RATIO = 0.15f;

    public static void resolveWheelGroups(ObjModel model) {
        List<Triangle> triangles = model.triangles;
        List<Integer> candidates = new ArrayList<>();
        for (int i = 0; i < triangles.size(); i++) {
            if (triangles.get(i).wheelCandidate) {
                candidates.add(i);
            }
        }
        if (candidates.isEmpty()) {
            return;
        }

        int n = candidates.size();
        float[] cx = new float[n];
        float[] cy = new float[n];
        float[] cz = new float[n];
        for (int i = 0; i < n; i++) {
            Triangle t = triangles.get(candidates.get(i));
            cx[i] = (t.v1.x + t.v2.x + t.v3.x) / 3f;
            cy[i] = (t.v1.y + t.v2.y + t.v3.y) / 3f;
            cz[i] = (t.v1.z + t.v2.z + t.v3.z) / 3f;
        }

        int[] parent = new int[n];
        for (int i = 0; i < n; i++) parent[i] = i;

        Map<Long, List<Integer>> grid = new HashMap<>();
        for (int i = 0; i < n; i++) {
            grid.computeIfAbsent(voxelKey(cx[i], cy[i], cz[i]), k -> new ArrayList<>()).add(i);
        }
        for (int i = 0; i < n; i++) {
            int vx = voxelCoord(cx[i]);
            int vy = voxelCoord(cy[i]);
            int vz = voxelCoord(cz[i]);
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        List<Integer> bucket = grid.get(packVoxel(vx + dx, vy + dy, vz + dz));
                        if (bucket == null) continue;
                        for (int j : bucket) {
                            if (j <= i) continue;
                            float ddx = cx[i] - cx[j];
                            float ddy = cy[i] - cy[j];
                            float ddz = cz[i] - cz[j];
                            if (ddx * ddx + ddy * ddy + ddz * ddz <= WHEEL_CLUSTER_DISTANCE * WHEEL_CLUSTER_DISTANCE) {
                                union(parent, i, j);
                            }
                        }
                    }
                }
            }
        }

        Map<Integer, List<Integer>> clusters = new HashMap<>();
        for (int i = 0; i < n; i++) {
            clusters.computeIfAbsent(find(parent, i), k -> new ArrayList<>()).add(i);
        }

        List<List<Integer>> validClusters = new ArrayList<>();
        for (List<Integer> cluster : clusters.values()) {
            if (cluster.size() >= MIN_WHEEL_CLUSTER_TRIANGLES) {
                validClusters.add(cluster);
            }
        }

        if (validClusters.size() != EXPECTED_WHEEL_COUNT) {
            return;
        }

        int pivotCount = validClusters.size();
        float[] pivotCenterX = new float[pivotCount];
        float[] pivotCenterY = new float[pivotCount];
        float[] pivotCenterZ = new float[pivotCount];
        float[] pivotAxisX = new float[pivotCount];
        float[] pivotAxisY = new float[pivotCount];
        float[] pivotAxisZ = new float[pivotCount];
        float[] pivotRadius = new float[pivotCount];
        int minClusterSize = Integer.MAX_VALUE;
        int maxClusterSize = 0;
        for (int cIdx = 0; cIdx < pivotCount; cIdx++) {
            List<Integer> cluster = validClusters.get(cIdx);
            float minX = Float.MAX_VALUE, maxX = -Float.MAX_VALUE;
            float minY = Float.MAX_VALUE, maxY = -Float.MAX_VALUE;
            float minZ = Float.MAX_VALUE, maxZ = -Float.MAX_VALUE;
            for (int localIdx : cluster) {
                Triangle t = triangles.get(candidates.get(localIdx));
                for (Vertex v : new Vertex[]{t.v1, t.v2, t.v3}) {
                    minX = Math.min(minX, v.x); maxX = Math.max(maxX, v.x);
                    minY = Math.min(minY, v.y); maxY = Math.max(maxY, v.y);
                    minZ = Math.min(minZ, v.z); maxZ = Math.max(maxZ, v.z);
                }
            }

            float ex = maxX - minX, ey = maxY - minY, ez = maxZ - minZ;
            float axisX = 0, axisY = 0, axisZ = 0;
            float radius;
            if (ex <= ey && ex <= ez) {
                axisX = 1f;
                radius = (ey + ez) / 4f;
            } else if (ey <= ex && ey <= ez) {
                axisY = 1f;
                radius = (ex + ez) / 4f;
            } else {
                axisZ = 1f;
                radius = (ex + ey) / 4f;
            }

            pivotCenterX[cIdx] = (minX + maxX) / 2f;
            pivotCenterY[cIdx] = (minY + maxY) / 2f;
            pivotCenterZ[cIdx] = (minZ + maxZ) / 2f;
            pivotAxisX[cIdx] = axisX;
            pivotAxisY[cIdx] = axisY;
            pivotAxisZ[cIdx] = axisZ;
            pivotRadius[cIdx] = radius;
            minClusterSize = Math.min(minClusterSize, cluster.size());
            maxClusterSize = Math.max(maxClusterSize, cluster.size());
        }

        float avgCenterZ = 0f;
        for (int i = 0; i < pivotCount; i++) {
            avgCenterZ += pivotCenterZ[i];
        }
        avgCenterZ /= pivotCount;

        List<WheelPivot> pivots = new ArrayList<>(pivotCount);
        for (int i = 0; i < pivotCount; i++) {
            pivots.add(new WheelPivot(
                    pivotCenterX[i], pivotCenterY[i], pivotCenterZ[i],
                    pivotAxisX[i], pivotAxisY[i], pivotAxisZ[i], pivotRadius[i],
                    pivotCenterZ[i] > avgCenterZ));
        }

        for (WheelPivot pivot : pivots) {
            if (pivot.radius < MIN_WHEEL_RADIUS || pivot.radius > MAX_WHEEL_RADIUS) {
                return;
            }
        }
        if (minClusterSize < maxClusterSize * MIN_CLUSTER_SIZE_RATIO) {
            return;
        }

        for (int group = 0; group < validClusters.size(); group++) {
            for (int localIdx : validClusters.get(group)) {
                triangles.get(candidates.get(localIdx)).wheelGroup = group;
            }
        }
        model.wheelPivots.addAll(pivots);
    }

    private static final int MIN_ROTOR_CLUSTER_TRIANGLES = 4;

    private static final float MIN_ROTOR_RADIUS = 0.05f;

    private static final float MAX_ROTOR_RADIUS = 10.0f;

    public static void resolveRotorGroups(ObjModel model) {
        List<Triangle> triangles = model.triangles;

        Map<Integer, List<Integer>> byNode = new HashMap<>();
        for (int i = 0; i < triangles.size(); i++) {
            Triangle t = triangles.get(i);
            if (t.rotorCandidate) {
                byNode.computeIfAbsent(t.rotorSourceNode, k -> new ArrayList<>()).add(i);
            }
        }
        if (byNode.isEmpty()) {
            return;
        }

        List<List<Integer>> validClusters = new ArrayList<>();
        for (List<Integer> cluster : byNode.values()) {
            if (cluster.size() >= MIN_ROTOR_CLUSTER_TRIANGLES) {
                validClusters.add(cluster);
            }
        }
        if (validClusters.isEmpty()) {
            return;
        }

        List<RotorPivot> pivots = new ArrayList<>(validClusters.size());
        List<List<Integer>> acceptedClusters = new ArrayList<>(validClusters.size());
        for (List<Integer> cluster : validClusters) {
            float minX = Float.MAX_VALUE, maxX = -Float.MAX_VALUE;
            float minY = Float.MAX_VALUE, maxY = -Float.MAX_VALUE;
            float minZ = Float.MAX_VALUE, maxZ = -Float.MAX_VALUE;

            float sumX = 0f, sumY = 0f, sumZ = 0f;
            int cornerCount = 0;
            for (int triIdx : cluster) {
                Triangle t = triangles.get(triIdx);
                for (Vertex v : new Vertex[]{t.v1, t.v2, t.v3}) {
                    minX = Math.min(minX, v.x); maxX = Math.max(maxX, v.x);
                    minY = Math.min(minY, v.y); maxY = Math.max(maxY, v.y);
                    minZ = Math.min(minZ, v.z); maxZ = Math.max(maxZ, v.z);
                    sumX += v.x; sumY += v.y; sumZ += v.z;
                    cornerCount++;
                }
            }

            float ex = maxX - minX, ey = maxY - minY, ez = maxZ - minZ;
            float axisX = 0, axisY = 0, axisZ = 0;
            float radius;
            if (ex <= ey && ex <= ez) {
                axisX = 1f;
                radius = (ey + ez) / 4f;
            } else if (ey <= ex && ey <= ez) {
                axisY = 1f;
                radius = (ex + ez) / 4f;
            } else {
                axisZ = 1f;
                radius = (ex + ey) / 4f;
            }

            if (radius < MIN_ROTOR_RADIUS || radius > MAX_ROTOR_RADIUS) {
                continue;
            }

            pivots.add(new RotorPivot(
                    sumX / cornerCount, sumY / cornerCount, sumZ / cornerCount,
                    axisX, axisY, axisZ));
            acceptedClusters.add(cluster);
        }

        if (pivots.isEmpty()) {
            return;
        }

        for (int group = 0; group < acceptedClusters.size(); group++) {
            for (int triIdx : acceptedClusters.get(group)) {
                triangles.get(triIdx).rotorGroup = group;
            }
        }
        model.rotorPivots.addAll(pivots);
    }

    private static int voxelCoord(float v) {
        return (int) Math.floor(v / WHEEL_CLUSTER_DISTANCE);
    }

    private static long packVoxel(int x, int y, int z) {
        long ox = x + 1_000_000L;
        long oy = y + 1_000_000L;
        long oz = z + 1_000_000L;
        return (ox << 42) | (oy << 21) | oz;
    }

    private static long voxelKey(float x, float y, float z) {
        return packVoxel(voxelCoord(x), voxelCoord(y), voxelCoord(z));
    }

    private static int find(int[] parent, int i) {
        while (parent[i] != i) {
            parent[i] = parent[parent[i]];
            i = parent[i];
        }
        return i;
    }

    private static void union(int[] parent, int a, int b) {
        int ra = find(parent, a);
        int rb = find(parent, b);
        if (ra != rb) {
            parent[ra] = rb;
        }
    }

    public static boolean isWheelCandidateName(String name) {
        if (name == null || name.isEmpty()) return false;
        String lower = name.toLowerCase(Locale.ROOT);
        if (lower.contains("steer")) return false;
        return lower.contains("wheel") || lower.contains("tire") || lower.contains("tyre");
    }

    public static boolean isRotorCandidateName(String name) {
        if (name == null || name.isEmpty()) return false;
        String lower = name.toLowerCase(Locale.ROOT);
        if (lower.contains("brake") || lower.contains("wiper")) return false;
        return lower.contains("rotor") || lower.contains("blade") || lower.contains("propeller")
                || lower.contains("screw") || lower.contains("elise");
    }

    public static boolean isPlateCandidateName(String name) {
        if (name == null || name.isEmpty()) return false;
        String lower = name.toLowerCase(Locale.ROOT);
        return lower.contains("plate") || lower.contains("license") || lower.contains("licence")
                || lower.contains("number_pl") || lower.contains("number_tex");
    }
}
