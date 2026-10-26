package com.examplebett.waypoint;

import com.examplebett.AiObjectMod;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.LevelResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

final class WaypointRegionStorage {

    private static final int REGION_SIZE = 512;
    private static final String REGIONS_DIR_NAME = "mineturismo_waypoints_regions";

    private final Map<Long, Set<Integer>> nodeIdsByRegion = new HashMap<>();
    private final Map<Long, Set<Integer>> signalIdsByRegion = new HashMap<>();
    private final Map<Integer, Long> nodeRegionById = new HashMap<>();
    private final Map<Integer, Long> signalRegionById = new HashMap<>();

    record LoadResult(List<WaypointNode> nodes, List<TrafficSignalMarker> signals) {
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

    static long regionKeyOf(BlockPos pos) {
        int rx = Math.floorDiv(pos.getX(), REGION_SIZE);
        int rz = Math.floorDiv(pos.getZ(), REGION_SIZE);
        return ((long) rx << 32) | (rz & 0xFFFFFFFFL);
    }

    void trackNode(WaypointNode node) {
        long key = regionKeyOf(node.pos());
        this.nodeIdsByRegion.computeIfAbsent(key, k -> new HashSet<>()).add(node.id());
        this.nodeRegionById.put(node.id(), key);
    }

    void untrackNode(int id) {
        Long key = this.nodeRegionById.remove(id);
        if (key == null) {
            return;
        }
        Set<Integer> ids = this.nodeIdsByRegion.get(key);
        if (ids != null) {
            ids.remove(id);
            if (ids.isEmpty()) {
                this.nodeIdsByRegion.remove(key);
            }
        }
    }

    void trackSignal(TrafficSignalMarker signal) {
        long key = regionKeyOf(signal.pos());
        this.signalIdsByRegion.computeIfAbsent(key, k -> new HashSet<>()).add(signal.id());
        this.signalRegionById.put(signal.id(), key);
    }

    void untrackSignal(int id) {
        Long key = this.signalRegionById.remove(id);
        if (key == null) {
            return;
        }
        Set<Integer> ids = this.signalIdsByRegion.get(key);
        if (ids != null) {
            ids.remove(id);
            if (ids.isEmpty()) {
                this.signalIdsByRegion.remove(key);
            }
        }
    }

    void flush(ServerLevel level, Set<Long> regionKeys, Map<Integer, WaypointNode> nodes, Map<Integer, TrafficSignalMarker> signals) {
        if (regionKeys.isEmpty()) {
            return;
        }
        Path dir = regionsDir(level);
        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            AiObjectMod.LOGGER.error("Failed to create waypoint region directory {}", dir, e);
            return;
        }
        for (long key : regionKeys) {
            Path file = regionFile(dir, key);
            List<WaypointNode> regionNodes = new ArrayList<>();
            for (int id : this.nodeIdsByRegion.getOrDefault(key, Set.of())) {
                WaypointNode node = nodes.get(id);
                if (node != null) {
                    regionNodes.add(node);
                }
            }
            List<TrafficSignalMarker> regionSignals = new ArrayList<>();
            for (int id : this.signalIdsByRegion.getOrDefault(key, Set.of())) {
                TrafficSignalMarker signal = signals.get(id);
                if (signal != null) {
                    regionSignals.add(signal);
                }
            }
            if (regionNodes.isEmpty() && regionSignals.isEmpty()) {
                try {
                    Files.deleteIfExists(file);
                } catch (IOException e) {
                    AiObjectMod.LOGGER.error("Failed to delete empty waypoint region file {}", file, e);
                }
                continue;
            }
            WaypointRegionData data = new WaypointRegionData(regionNodes, regionSignals);

            //? if >=1.16 {
            CompoundTag tag = WaypointRegionData.CODEC.encodeStart(NbtOps.INSTANCE, data)
                    .resultOrPartial(msg -> AiObjectMod.LOGGER.error("Failed to encode waypoint region {}: {}", key, msg))
                    .map(CompoundTag.class::cast)
                    .orElse(null);
            //?}
            //? if <1.16 {
            /*CompoundTag tag = data.save(new CompoundTag());*/
            //?}
            if (tag == null) {
                continue;
            }
            try {
                writeCompressedCompat(tag, file);
            } catch (IOException e) {
                AiObjectMod.LOGGER.error("Failed to write waypoint region file {}", file, e);
            }
        }
    }

    LoadResult loadAll(ServerLevel level) {
        Path dir = regionsDir(level);
        List<WaypointNode> allNodes = new ArrayList<>();
        List<TrafficSignalMarker> allSignals = new ArrayList<>();
        if (!Files.isDirectory(dir)) {
            return new LoadResult(allNodes, allSignals);
        }
        List<Path> files;
        try (Stream<Path> stream = Files.list(dir)) {
            files = stream.filter(p -> p.getFileName().toString().endsWith(".dat")).toList();
        } catch (IOException e) {
            AiObjectMod.LOGGER.error("Failed to list waypoint region directory {}", dir, e);
            return new LoadResult(allNodes, allSignals);
        }
        for (Path file : files) {
            try {
                CompoundTag tag = readCompressedCompat(file);

                //? if >=1.16 {
                WaypointRegionData data = WaypointRegionData.CODEC.parse(NbtOps.INSTANCE, tag)
                        .resultOrPartial(msg -> AiObjectMod.LOGGER.error("Failed to parse waypoint region {}: {}", file, msg))
                        .orElse(null);
                //?}
                //? if <1.16 {
                /*WaypointRegionData data = WaypointRegionData.load(tag);*/
                //?}
                if (data == null) {
                    continue;
                }
                for (WaypointNode node : data.nodes()) {
                    this.trackNode(node);
                    allNodes.add(node);
                }
                for (TrafficSignalMarker signal : data.signals()) {
                    this.trackSignal(signal);
                    allSignals.add(signal);
                }
            } catch (IOException e) {
                AiObjectMod.LOGGER.error("Failed to read waypoint region file {}", file, e);
            }
        }
        return new LoadResult(allNodes, allSignals);
    }

    private static Path regionsDir(ServerLevel level) {

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
        return dimensionRoot.resolve("data").resolve(REGIONS_DIR_NAME);
    }

    private static Path regionFile(Path dir, long regionKey) {
        int rx = (int) (regionKey >> 32);
        int rz = (int) regionKey;
        return dir.resolve("region_" + rx + "_" + rz + ".dat");
    }
}
