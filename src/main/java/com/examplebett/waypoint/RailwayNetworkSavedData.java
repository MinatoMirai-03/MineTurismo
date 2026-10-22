package com.examplebett.waypoint;

import com.examplebett.AiObjectMod;
//? if >=1.16 {
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
//?}
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.LevelResource;
//? if >=1.21.5 {
import net.minecraft.world.level.saveddata.SavedDataType;
//?}

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class RailwayNetworkSavedData extends SavedData {

    private static final String ROUTES_FILE_NAME = "mineturismo_railway_routes.dat";

    public record RailwayRouteEntry(int originNodeId, String mode, String modelId, CompoundTag formationTag) {
        public static final String MODE_SINGLE = "single";
        public static final String MODE_CUSTOM = "custom";
    }

    private static final int[] EMPTY_LINKS = new int[0];

    //? if >=1.16 {
    public static final Codec<RailwayNetworkSavedData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RailwayNode.CODEC.listOf().optionalFieldOf("nodes", List.of())
                    .forGetter(data -> List.copyOf(data.nodes.values())),
            Codec.INT.fieldOf("next_id").forGetter(data -> data.nextId),

            RailwayStopMarker.CODEC.listOf().optionalFieldOf("stops", List.of())
                    .forGetter(data -> List.copyOf(data.stops.values())),
            Codec.INT.optionalFieldOf("next_stop_id", 0).forGetter(data -> data.nextStopId)
    ).apply(instance, RailwayNetworkSavedData::new));
    //?}

    //? if >=1.21.5 && <26.1 {
    public static final SavedDataType<RailwayNetworkSavedData> TYPE = new SavedDataType<>(
            "mineturismo_railway", RailwayNetworkSavedData::new, CODEC, DataFixTypes.LEVEL
    );
    //?}
    //? if >=26.1 {
    /*public static final SavedDataType<RailwayNetworkSavedData> TYPE = new SavedDataType<>(
            com.examplebett.AiObjectMod.id("railway"), RailwayNetworkSavedData::new, CODEC, DataFixTypes.LEVEL
    );*/
    //?}
    //? if >=1.20.5 && <1.21.5 {
    /*private static final SavedData.Factory<RailwayNetworkSavedData> FACTORY = new SavedData.Factory<>(
            RailwayNetworkSavedData::new,
            (tag, registries) -> CODEC.parse(NbtOps.INSTANCE, tag).result().orElseGet(RailwayNetworkSavedData::new),
            DataFixTypes.LEVEL
    );*/
    //?}
    //? if >=1.20.2 && <1.20.5 {
    /*private static final SavedData.Factory<RailwayNetworkSavedData> FACTORY = new SavedData.Factory<>(
            RailwayNetworkSavedData::new,
            tag -> CODEC.parse(NbtOps.INSTANCE, tag).result().orElseGet(RailwayNetworkSavedData::new),
            DataFixTypes.LEVEL
    );*/
    //?}

    private final Map<Integer, RailwayNode> nodes = new LinkedHashMap<>();
    private final Map<Integer, Set<Integer>> incomingOneWayLinks = new HashMap<>();
    private final SpatialIndex nodeIndex = new SpatialIndex();
    private int nextId;

    private final Map<Integer, RailwayStopMarker> stops = new LinkedHashMap<>();
    private int nextStopId;

    private ServerLevel level;
    private boolean routesLoaded = false;
    private final Map<Integer, RailwayRouteEntry> routeConfigs = new LinkedHashMap<>();

    private final Map<UUID, Integer> chainByPlayer = new HashMap<>();

    private final Map<Integer, Integer> activeTrainByRoute = new HashMap<>();

    private final Map<Integer, Integer> routeCooldownByRoute = new HashMap<>();

    public RailwayNetworkSavedData() {
        //? if <1.17 {
        /*super("mineturismo_railway");*/
        //?}
        this.nextId = 0;
    }

    private RailwayNetworkSavedData(List<RailwayNode> nodeList, int nextId, List<RailwayStopMarker> stopList, int nextStopId) {
        //? if <1.17 {
        /*super("mineturismo_railway");*/
        //?}
        this.nextId = nextId;
        this.nextStopId = nextStopId;
        for (RailwayStopMarker stop : stopList) {
            this.stops.put(stop.id(), stop);
        }
        for (RailwayNode node : nodeList) {
            this.nodes.put(node.id(), node);
            this.nodeIndex.add(node.id(), node.pos());
        }
        for (RailwayNode node : nodeList) {
            for (int targetId : node.oneWayLinks()) {
                this.incomingOneWayLinks.computeIfAbsent(targetId, k -> new HashSet<>()).add(node.id());
            }
        }
    }

    public static RailwayNetworkSavedData get(ServerLevel level) {
    //? if >=1.21.5 {
    RailwayNetworkSavedData data = level.getDataStorage().computeIfAbsent(TYPE);
    //?}
    //? if >=1.20.2 && <1.21.5 {
    /*RailwayNetworkSavedData data = level.getDataStorage().computeIfAbsent(FACTORY, "mineturismo_railway");*/
    //?}
    //? if >=1.17 && <1.20.2 {
    /*RailwayNetworkSavedData data = level.getDataStorage().computeIfAbsent(
            tag -> CODEC.parse(NbtOps.INSTANCE, tag).result().orElseGet(RailwayNetworkSavedData::new),
            RailwayNetworkSavedData::new,
            "mineturismo_railway");*/
    //?}
    //? if <1.17 {
    /*RailwayNetworkSavedData data = level.getDataStorage().computeIfAbsent(
            RailwayNetworkSavedData::new, "mineturismo_railway");*/
    //?}
        data.level = level;
        data.ensureRoutesLoaded(level);
        return data;
    }

    //? if >=1.20.5 && <1.21.5 {
    public CompoundTag save(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
        return (CompoundTag) CODEC.encodeStart(NbtOps.INSTANCE, this).result().orElseThrow();
    }
    //?}
    //? if <1.20.5 && >=1.16 {
    /*public CompoundTag save(CompoundTag tag) {
        return (CompoundTag) CODEC.encodeStart(NbtOps.INSTANCE, this).result().orElseThrow();
    }*/
    //?}
    //? if <1.16 {
    /*public CompoundTag save(CompoundTag tag) {
        ListTag nodesList = new ListTag();
        for (RailwayNode node : this.nodes.values()) {
            nodesList.add(node.save(new CompoundTag()));
        }
        tag.put("nodes", nodesList);
        tag.putInt("next_id", this.nextId);
        ListTag stopsList = new ListTag();
        for (RailwayStopMarker stop : this.stops.values()) {
            stopsList.add(stop.save(new CompoundTag()));
        }
        tag.put("stops", stopsList);
        tag.putInt("next_stop_id", this.nextStopId);
        return tag;
    }*/
    //?}

    //? if <1.17 && >=1.16 {
    /*public void load(CompoundTag tag) {
        RailwayNetworkSavedData decoded = CODEC.parse(NbtOps.INSTANCE, tag).result().orElseGet(RailwayNetworkSavedData::new);
        this.nextId = decoded.nextId;
        this.nextStopId = decoded.nextStopId;
        this.stops.putAll(decoded.stops);
        for (RailwayNode node : decoded.nodes.values()) {
            this.nodes.put(node.id(), node);
            this.nodeIndex.add(node.id(), node.pos());
        }
        for (RailwayNode node : decoded.nodes.values()) {
            for (int targetId : node.oneWayLinks()) {
                this.incomingOneWayLinks.computeIfAbsent(targetId, k -> new HashSet<>()).add(node.id());
            }
        }
    }*/
    //?}
    //? if <1.16 {
    /*public void load(CompoundTag tag) {
        this.nextId = tag.getInt("next_id");
        this.nextStopId = tag.getInt("next_stop_id");
        ListTag stopsList = tag.getList("stops", 10);
        for (int i = 0; i < stopsList.size(); i++) {
            RailwayStopMarker stop = RailwayStopMarker.load(stopsList.getCompound(i));
            this.stops.put(stop.id(), stop);
        }
        ListTag nodesList = tag.getList("nodes", 10);
        for (int i = 0; i < nodesList.size(); i++) {
            RailwayNode node = RailwayNode.load(nodesList.getCompound(i));
            this.nodes.put(node.id(), node);
            this.nodeIndex.add(node.id(), node.pos());
        }
        for (RailwayNode node : this.nodes.values()) {
            for (int targetId : node.oneWayLinks()) {
                this.incomingOneWayLinks.computeIfAbsent(targetId, k -> new HashSet<>()).add(node.id());
            }
        }
    }*/
    //?}

    public Map<Integer, RailwayNode> getNodes() {
        return this.nodes;
    }

    public RailwayNode findNear(BlockPos targetPos, double radius) {
        double radiusSq = radius * radius;
        RailwayNode closest = null;
        double closestDistSq = Double.MAX_VALUE;
        for (int id : this.nodeIndex.candidatesNear(targetPos, radius)) {
            RailwayNode node = this.nodes.get(id);
            if (node == null) {
                continue;
            }
            double distSq = node.pos().distSqr(targetPos);
            if (distSq <= radiusSq && distSq < closestDistSq) {
                closest = node;
                closestDistSq = distSq;
            }
        }
        return closest;
    }

    public List<RailwayNode> findNodesWithinRadius(BlockPos origin, double radius) {
        double radiusSq = radius * radius;
        List<RailwayNode> result = new ArrayList<>();
        for (int id : this.nodeIndex.candidatesNear(origin, radius)) {
            RailwayNode node = this.nodes.get(id);
            if (node != null && node.pos().distSqr(origin) <= radiusSq) {
                result.add(node);
            }
        }
        return result;
    }

    public int addWaypoint(BlockPos pos, float speedKmh) {
        int id = this.nextId++;
        RailwayNode node = new RailwayNode(id, pos, EMPTY_LINKS, speedKmh);
        this.nodes.put(id, node);
        this.nodeIndex.add(id, pos);
        this.setDirty();
        return id;
    }

    public void removeWaypoint(int id) {
        RailwayNode removed = this.nodes.remove(id);
        if (removed == null) {
            return;
        }
        this.nodeIndex.remove(id);

        Set<Integer> incomingIds = this.incomingOneWayLinks.remove(id);
        if (incomingIds != null) {
            for (int sourceId : incomingIds) {
                RailwayNode source = this.nodes.get(sourceId);
                if (source != null) {
                    this.nodes.put(sourceId, source.withOneWayLinks(withoutValue(source.oneWayLinks(), id)));
                }
            }
        }
        for (int targetId : removed.oneWayLinks()) {
            Set<Integer> incoming = this.incomingOneWayLinks.get(targetId);
            if (incoming != null) {
                incoming.remove(id);
                if (incoming.isEmpty()) {
                    this.incomingOneWayLinks.remove(targetId);
                }
            }
        }

        this.stops.values().removeIf(stop -> stop.touchesNode(id));
        this.setDirty();

        if (this.routeConfigs.remove(id) != null) {
            this.saveRoutesFile();
        }
        Integer activeEntityId = this.activeTrainByRoute.get(id);
        if (activeEntityId != null && this.level != null) {
            net.minecraft.world.entity.Entity active = this.level.getEntity(activeEntityId);

            if (active instanceof com.examplebett.entity.CarEntity car) {
                car.discard();
            }
            this.activeTrainByRoute.remove(id);
        }
        this.routeCooldownByRoute.remove(id);
    }

    public void linkOneWay(int fromId, int toId) {
        if (fromId == toId) {
            return;
        }
        RailwayNode from = this.nodes.get(fromId);
        RailwayNode to = this.nodes.get(toId);
        if (from == null || to == null) {
            return;
        }
        if (!from.hasOneWayLink(toId)) {
            this.nodes.put(fromId, from.withOneWayLinks(withAppended(from.oneWayLinks(), toId)));
            this.incomingOneWayLinks.computeIfAbsent(toId, k -> new HashSet<>()).add(fromId);
        }
        this.setDirty();
    }

    public boolean contains(int id) {
        return this.nodes.containsKey(id);
    }

    public Set<Integer> getIncomingOneWayLinks(int id) {
        Set<Integer> incoming = this.incomingOneWayLinks.get(id);
        return incoming != null ? incoming : Set.of();
    }

    public int addStop(BlockPos pos, int edgeFromId, int edgeToId, double stopDistanceFromEnd) {
        int id = this.nextStopId++;
        this.stops.put(id, new RailwayStopMarker(id, pos, edgeFromId, edgeToId, stopDistanceFromEnd));
        this.setDirty();
        return id;
    }

    public void removeStop(int id) {
        if (this.stops.remove(id) != null) {
            this.setDirty();
        }
    }

    public RailwayStopMarker findStopNear(BlockPos targetPos, double radius) {
        double radiusSq = radius * radius;
        RailwayStopMarker closest = null;
        double closestDistSq = Double.MAX_VALUE;
        for (RailwayStopMarker stop : this.stops.values()) {
            double distSq = stop.pos().distSqr(targetPos);
            if (distSq <= radiusSq && distSq < closestDistSq) {
                closest = stop;
                closestDistSq = distSq;
            }
        }
        return closest;
    }

    public List<RailwayStopMarker> findStopsWithinRadius(BlockPos origin, double radius) {
        double radiusSq = radius * radius;
        List<RailwayStopMarker> result = new ArrayList<>();
        for (RailwayStopMarker stop : this.stops.values()) {
            if (stop.pos().distSqr(origin) <= radiusSq) {
                result.add(stop);
            }
        }
        return result;
    }

    public List<RailwayStopMarker> findStopsOnEdge(int fromId, int toId) {
        List<RailwayStopMarker> result = new ArrayList<>();
        for (RailwayStopMarker stop : this.stops.values()) {
            if (stop.edgeFromId() == fromId && stop.edgeToId() == toId) {
                result.add(stop);
            }
        }
        return result;
    }

    private static int[] withoutValue(int[] array, int value) {
        int count = 0;
        for (int v : array) {
            if (v != value) {
                count++;
            }
        }
        if (count == array.length) {
            return array;
        }
        int[] result = new int[count];
        int idx = 0;
        for (int v : array) {
            if (v != value) {
                result[idx++] = v;
            }
        }
        return result;
    }

    private static int[] withAppended(int[] array, int value) {
        int[] result = new int[array.length + 1];
        System.arraycopy(array, 0, result, 0, array.length);
        result[array.length] = value;
        return result;
    }

    public Integer getChainWaypoint(UUID playerId) {
        return this.chainByPlayer.get(playerId);
    }

    public void setChainWaypoint(UUID playerId, int id) {
        this.chainByPlayer.put(playerId, id);
    }

    public void clearChainWaypoint(UUID playerId) {
        this.chainByPlayer.remove(playerId);
    }

    public void clearChainWaypointIfMatches(UUID playerId, int id) {
        Integer current = this.chainByPlayer.get(playerId);
        if (current != null && current == id) {
            this.chainByPlayer.remove(playerId);
        }
    }

    public RailwayRouteEntry getRouteConfig(int originNodeId) {
        return this.routeConfigs.get(originNodeId);
    }

    public Set<Integer> getRouteOriginIds() {
        return this.routeConfigs.keySet();
    }

    public void setRouteConfig(RailwayRouteEntry entry) {
        this.routeConfigs.put(entry.originNodeId(), entry);
        this.saveRoutesFile();
    }

    public void removeRouteConfig(int originNodeId) {
        if (this.routeConfigs.remove(originNodeId) != null) {
            this.saveRoutesFile();
        }
    }

    public Integer getActiveTrainEntityId(int originNodeId) {
        return this.activeTrainByRoute.get(originNodeId);
    }

    public void setActiveTrainEntityId(int originNodeId, int entityId) {
        this.activeTrainByRoute.put(originNodeId, entityId);
    }

    public void clearActiveTrainIfMatches(int originNodeId, int entityId) {
        Integer current = this.activeTrainByRoute.get(originNodeId);
        if (current != null && current == entityId) {
            this.activeTrainByRoute.remove(originNodeId);
        }
    }

    public int getRouteCooldown(int originNodeId) {
        return this.routeCooldownByRoute.getOrDefault(originNodeId, 0);
    }

    public void setRouteCooldown(int originNodeId, int ticks) {
        this.routeCooldownByRoute.put(originNodeId, ticks);
    }

    private static void writeCompressedCompat(CompoundTag tag, Path file) throws IOException {
        //? if >=1.20.3 {
        NbtIo.writeCompressed(tag, file);
        //?}
        //? if >=1.16.2 && <1.20.3 {
        /*NbtIo.writeCompressed(tag, file.toFile());*/
        //?}
        //? if <1.16.2 {
        /*try (java.io.OutputStream out = java.nio.file.Files.newOutputStream(file)) {
            NbtIo.writeCompressed(tag, out);
        }*/
        //?}
    }

    private static CompoundTag readCompressedCompat(Path file) throws IOException {
        //? if >=1.20.3 {
        return NbtIo.readCompressed(file, NbtAccounter.unlimitedHeap());
        //?}
        //? if >=1.16.2 && <1.20.3 {
        /*return NbtIo.readCompressed(file.toFile());*/
        //?}
        //? if <1.16.2 {
        /*try (java.io.InputStream in = java.nio.file.Files.newInputStream(file)) {
            return NbtIo.readCompressed(in);
        }*/
        //?}
    }

    private static Path routesFile(ServerLevel level) {

        //? if >=1.18 {
        Path dimensionRoot = DimensionType.getStorageFolder(level.dimension(), level.getServer().getWorldPath(LevelResource.ROOT));
        //?}
        //? if <1.18 && >=1.16 {
        /*Path dimensionRoot = DimensionType.getStorageFolder(level.dimension(), level.getServer().getWorldPath(LevelResource.ROOT).toFile()).toPath();*/
        //?}
        //? if <1.16 {
        /*net.minecraft.world.level.dimension.DimensionType dimType = level.getDimension().getType();
        java.io.File worldRoot = level.getServer().getFile("");
        Path dimensionRoot = dimType.getStorageFolder(worldRoot).toPath();*/
        //?}
        return dimensionRoot.resolve("data").resolve(ROUTES_FILE_NAME);
    }

    private static String rawStringOr(CompoundTag tag, String key, String fallback) {
        //? if >=1.21.5 {
        return tag.getStringOr(key, fallback);
        //?} else
        /*return tag.contains(key) ? tag.getString(key) : fallback;*/
    }

    private static int rawIntOr(CompoundTag tag, String key, int fallback) {
        //? if >=1.21.5 {
        return tag.getInt(key).orElse(fallback);
        //?} else
        /*return tag.contains(key) ? tag.getInt(key) : fallback;*/
    }

    private static CompoundTag rawCompoundOr(CompoundTag tag, String key) {
        //? if >=1.21.5 {
        return tag.getCompound(key).orElse(new CompoundTag());
        //?} else
        /*return tag.contains(key) ? tag.getCompound(key) : new CompoundTag();*/
    }

    private void ensureRoutesLoaded(ServerLevel level) {
        if (this.routesLoaded) {
            return;
        }
        this.routesLoaded = true;
        Path file = routesFile(level);
        if (!Files.isRegularFile(file)) {
            return;
        }
        try {
            CompoundTag root = readCompressedCompat(file);
            int count = rawIntOr(root, "RouteCount", 0);
            for (int i = 0; i < count; i++) {
                CompoundTag entryTag = rawCompoundOr(root, "Route" + i);
                int originNodeId = rawIntOr(entryTag, "Origin", -1);
                if (originNodeId < 0) {
                    continue;
                }
                String mode = rawStringOr(entryTag, "Mode", RailwayRouteEntry.MODE_SINGLE);
                String modelId = rawStringOr(entryTag, "ModelId", "");
                CompoundTag formationTag = rawCompoundOr(entryTag, "Formation");
                this.routeConfigs.put(originNodeId, new RailwayRouteEntry(originNodeId, mode, modelId, formationTag));
            }
        } catch (IOException e) {
            AiObjectMod.LOGGER.error("Failed to read railway routes file {}", file, e);
        }
    }

    private void saveRoutesFile() {
        if (this.level == null) {
            return;
        }
        Path file = routesFile(this.level);
        try {
            Files.createDirectories(file.getParent());
        } catch (IOException e) {
            AiObjectMod.LOGGER.error("Failed to create railway routes directory {}", file.getParent(), e);
            return;
        }
        CompoundTag root = new CompoundTag();
        int count = 0;
        for (RailwayRouteEntry entry : this.routeConfigs.values()) {
            CompoundTag entryTag = new CompoundTag();
            entryTag.putInt("Origin", entry.originNodeId());
            entryTag.putString("Mode", entry.mode());
            entryTag.putString("ModelId", entry.modelId());
            if (!entry.formationTag().isEmpty()) {
                entryTag.put("Formation", entry.formationTag());
            }
            root.put("Route" + count, entryTag);
            count++;
        }
        root.putInt("RouteCount", count);
        try {
            writeCompressedCompat(root, file);
        } catch (IOException e) {
            AiObjectMod.LOGGER.error("Failed to write railway routes file {}", file, e);
        }
    }
}
