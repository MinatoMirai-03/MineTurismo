package com.examplebett.waypoint;

import com.examplebett.AiObjectMod;
//? if >=1.16 {
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
//?}
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
//? if >=1.21.5 {
import net.minecraft.world.level.saveddata.SavedDataType;
//?}

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class WaypointNetworkSavedData extends SavedData {

    private record TrafficPlateIdSets(List<String> poolIds, List<String> exclusiveIds, List<CustomTrafficCar> customCars) {
        static final TrafficPlateIdSets EMPTY = new TrafficPlateIdSets(List.of(), List.of(), List.of());

        //? if >=1.16 {
        static final Codec<TrafficPlateIdSets> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.listOf().optionalFieldOf("pool", List.of()).forGetter(TrafficPlateIdSets::poolIds),
                Codec.STRING.listOf().optionalFieldOf("exclusive", List.of()).forGetter(TrafficPlateIdSets::exclusiveIds),
                CustomTrafficCar.CODEC.listOf().optionalFieldOf("custom_cars", List.of()).forGetter(TrafficPlateIdSets::customCars)
        ).apply(instance, TrafficPlateIdSets::new));
        //?}
    }

    public record CustomTrafficCar(String id, CompoundTag tag, boolean rare) {
        //? if >=1.16 {
        static final Codec<CustomTrafficCar> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("id").forGetter(CustomTrafficCar::id),
                CompoundTag.CODEC.fieldOf("tag").forGetter(CustomTrafficCar::tag),
                Codec.BOOL.optionalFieldOf("rare", false).forGetter(CustomTrafficCar::rare)
        ).apply(instance, CustomTrafficCar::new));
        //?}

        public String modelId() {
            //? if >=1.21.5 {
            return this.tag.getStringOr("ModelId", "");
            //?} else
            /*return this.tag.contains("ModelId") ? this.tag.getString("ModelId") : "";*/
        }
    }

    public static final int MAX_CUSTOM_TRAFFIC_CARS = 64;

    public static final int MIN_MAX_SPAWNED_CARS = 1;
    public static final int MAX_MAX_SPAWNED_CARS = 100;
    private static final int DEFAULT_MAX_SPAWNED_CARS = 3;
    public static final int MIN_AI_THROTTLE_INTERVAL_TICKS = 0;
    public static final int MAX_AI_THROTTLE_INTERVAL_TICKS = 20;
    private static final int DEFAULT_AI_THROTTLE_INTERVAL_TICKS = 4;

    public static final int MIN_SPAWN_INTERVAL_TICKS = 20;
    public static final int MAX_SPAWN_INTERVAL_TICKS = 600;
    private static final int DEFAULT_SPAWN_INTERVAL_TICKS = 150;

    public static final float MIN_AMBIENT_ENGINE_VOLUME_MULTIPLIER = 0.5f;
    public static final float MAX_AMBIENT_ENGINE_VOLUME_MULTIPLIER = 20.0f;
    public static final float DEFAULT_AMBIENT_ENGINE_VOLUME_MULTIPLIER = 5.0f;

    //? if >=1.16 {
    public static final Codec<WaypointNetworkSavedData> CODEC = RecordCodecBuilder.create(instance -> instance.group(

            WaypointNode.CODEC.listOf().optionalFieldOf("nodes", List.of()).forGetter(data -> List.of()),
            Codec.INT.fieldOf("next_id").forGetter(data -> data.nextId),
            Codec.STRING.listOf().optionalFieldOf("disabled_models", List.of()).forGetter(data -> List.copyOf(data.disabledModelIds)),
            Codec.STRING.listOf().optionalFieldOf("rare_models", List.of()).forGetter(data -> List.copyOf(data.rareModelIds)),
            TrafficPlateIdSets.CODEC.optionalFieldOf("traffic_plate_ids", TrafficPlateIdSets.EMPTY)
                    .forGetter(data -> new TrafficPlateIdSets(List.copyOf(data.trafficPlateIds), List.copyOf(data.exclusivePlateIds),
                            List.copyOf(data.customTrafficCars))),
            Codec.BOOL.optionalFieldOf("use_legacy_steering", false).forGetter(data -> data.useLegacySteering),
            Codec.BOOL.optionalFieldOf("town_traffic_enabled", true).forGetter(data -> data.townTrafficEnabled),
            Codec.INT.optionalFieldOf("max_spawned_cars", DEFAULT_MAX_SPAWNED_CARS).forGetter(data -> data.maxSpawnedCars),
            Codec.INT.optionalFieldOf("ai_throttle_interval_ticks", DEFAULT_AI_THROTTLE_INTERVAL_TICKS).forGetter(data -> data.aiThrottleIntervalTicks),
            Codec.INT.optionalFieldOf("spawn_interval_ticks", DEFAULT_SPAWN_INTERVAL_TICKS).forGetter(data -> data.spawnIntervalTicks),
            Codec.BOOL.optionalFieldOf("recall_distant_cars_enabled", false).forGetter(data -> data.recallDistantCarsEnabled),
            Codec.BOOL.optionalFieldOf("night_lights_enabled", true).forGetter(data -> data.nightLightsEnabled),

            TrafficSignalMarker.CODEC.listOf().optionalFieldOf("signals", List.of()).forGetter(data -> List.of()),
            Codec.INT.optionalFieldOf("next_signal_id", 0).forGetter(data -> data.nextSignalId),
            SignalGroupConfig.CODEC.listOf().optionalFieldOf("signal_groups", List.of()).forGetter(data -> List.copyOf(data.signalGroups.values())),
            Codec.FLOAT.optionalFieldOf("ambient_engine_volume_multiplier", DEFAULT_AMBIENT_ENGINE_VOLUME_MULTIPLIER).forGetter(data -> data.ambientEngineVolumeMultiplier)
    ).apply(instance, WaypointNetworkSavedData::new));
    //?}

    //? if >=1.21.5 && <26.1 {
    public static final SavedDataType<WaypointNetworkSavedData> TYPE = new SavedDataType<>(
            "mineturismo_waypoints", WaypointNetworkSavedData::new, CODEC, DataFixTypes.LEVEL
    );
    //?}
    //? if >=26.1 {
    /*public static final SavedDataType<WaypointNetworkSavedData> TYPE = new SavedDataType<>(
            com.examplebett.AiObjectMod.id("waypoints"), WaypointNetworkSavedData::new, CODEC, DataFixTypes.LEVEL
    );*/
    //?}
    //? if >=1.20.5 && <1.21.5 {
    /*private static final SavedData.Factory<WaypointNetworkSavedData> FACTORY = new SavedData.Factory<>(
            WaypointNetworkSavedData::new,
            (tag, registries) -> CODEC.parse(NbtOps.INSTANCE, tag).result().orElseGet(WaypointNetworkSavedData::new),
            DataFixTypes.LEVEL
    );*/
    //?}
    //? if >=1.20.2 && <1.20.5 {
    /*private static final SavedData.Factory<WaypointNetworkSavedData> FACTORY = new SavedData.Factory<>(
            WaypointNetworkSavedData::new,
            tag -> CODEC.parse(NbtOps.INSTANCE, tag).result().orElseGet(WaypointNetworkSavedData::new),
            DataFixTypes.LEVEL
    );*/
    //?}

    private final WaypointRegionStorage regionStorage = new WaypointRegionStorage();

    private boolean regionsLoaded = false;

    private ServerLevel level;

    private List<WaypointNode> legacyNodesForMigration = List.of();
    private List<TrafficSignalMarker> legacySignalsForMigration = List.of();
    private final Map<Integer, WaypointNode> nodes = new LinkedHashMap<>();

    private final Map<Integer, Set<Integer>> incomingOneWayLinks = new HashMap<>();
    private final Map<UUID, Integer> chainByPlayer = new HashMap<>();
    private final Map<UUID, Float> penSpeedByPlayer = new HashMap<>();
    private final Map<UUID, Integer> spawnCooldownByPlayer = new HashMap<>();

    private boolean isNewlyCreated = false;
    private final Set<String> disabledModelIds = new HashSet<>();

    private final Set<String> rareModelIds = new HashSet<>();

    private final Set<String> trafficPlateIds = new HashSet<>();

    private final Set<String> exclusivePlateIds = new HashSet<>();

    private final List<CustomTrafficCar> customTrafficCars = new ArrayList<>();

    private final Map<Integer, TrafficSignalMarker> signals = new LinkedHashMap<>();
    private final Map<String, SignalGroupConfig> signalGroups = new LinkedHashMap<>();

    private final SpatialIndex nodeIndex = new SpatialIndex();
    private final SpatialIndex signalIndex = new SpatialIndex();

    private final Map<Long, TrafficSignalMarker> signalsByEdge = new HashMap<>();

    private final Map<String, Integer> signalGroupRefCounts = new HashMap<>();

    private final SpatialIndex activeTownCarIndex = new SpatialIndex();
    private final Map<Integer, BlockPos> activeTownCarPositions = new HashMap<>();

    private final Map<UUID, Boolean> oneWayReversedByPlayer = new HashMap<>();

    private final Map<UUID, PendingSignalPen> pendingSignalByPlayer = new HashMap<>();

    private final Map<UUID, Integer> editingSignalIdByPlayer = new HashMap<>();
    private int nextId;
    private int nextSignalId;

    private boolean useLegacySteering;

    private boolean townTrafficEnabled;

    private int maxSpawnedCars = DEFAULT_MAX_SPAWNED_CARS;

    private int aiThrottleIntervalTicks = DEFAULT_AI_THROTTLE_INTERVAL_TICKS;

    private int spawnIntervalTicks = DEFAULT_SPAWN_INTERVAL_TICKS;

    private boolean recallDistantCarsEnabled;

    private boolean nightLightsEnabled = true;
    private float ambientEngineVolumeMultiplier = DEFAULT_AMBIENT_ENGINE_VOLUME_MULTIPLIER;

    public WaypointNetworkSavedData() {
        //? if <1.17 {
        /*super("mineturismo_waypoints");*/
        //?}
        this.nextId = 0;
        this.isNewlyCreated = true;
        this.townTrafficEnabled = true;
    }

    private WaypointNetworkSavedData(List<WaypointNode> legacyNodeList, int nextId, List<String> disabledModelIdList,
                                      List<String> rareModelIdList, TrafficPlateIdSets trafficPlateIdSets,
                                      boolean useLegacySteering, boolean townTrafficEnabled,
                                      int maxSpawnedCars, int aiThrottleIntervalTicks, int spawnIntervalTicks,
                                      boolean recallDistantCarsEnabled, boolean nightLightsEnabled,
                                      List<TrafficSignalMarker> legacySignalList, int nextSignalId,
                                      List<SignalGroupConfig> signalGroupList, float ambientEngineVolumeMultiplier) {
        //? if <1.17 {
        /*super("mineturismo_waypoints");*/
        //?}

        this.legacyNodesForMigration = legacyNodeList;
        this.legacySignalsForMigration = legacySignalList;
        this.nextId = nextId;
        this.disabledModelIds.addAll(disabledModelIdList);
        this.rareModelIds.addAll(rareModelIdList);
        this.trafficPlateIds.addAll(trafficPlateIdSets.poolIds());
        this.exclusivePlateIds.addAll(trafficPlateIdSets.exclusiveIds());
        this.customTrafficCars.addAll(trafficPlateIdSets.customCars());
        this.useLegacySteering = useLegacySteering;
        this.townTrafficEnabled = townTrafficEnabled;
        this.maxSpawnedCars = maxSpawnedCars;
        this.aiThrottleIntervalTicks = aiThrottleIntervalTicks;
        this.spawnIntervalTicks = spawnIntervalTicks;
        this.recallDistantCarsEnabled = recallDistantCarsEnabled;
        this.nightLightsEnabled = nightLightsEnabled;
        this.nextSignalId = nextSignalId;
        for (SignalGroupConfig groupConfig : signalGroupList) {
            this.signalGroups.put(groupConfig.groupKey(), groupConfig);
        }
        this.ambientEngineVolumeMultiplier = ambientEngineVolumeMultiplier;
    }

    private void ensureRegionsLoaded(ServerLevel level) {
        if (this.regionsLoaded) {
            return;
        }
        this.regionsLoaded = true;

        if (!this.legacyNodesForMigration.isEmpty() || !this.legacySignalsForMigration.isEmpty()) {
            AiObjectMod.LOGGER.info("Migrating {} waypoint node(s) and {} signal(s) to per-region storage",
                    this.legacyNodesForMigration.size(), this.legacySignalsForMigration.size());
            this.populateFrom(this.legacyNodesForMigration, this.legacySignalsForMigration);
            Set<Long> touchedRegions = new HashSet<>();
            for (WaypointNode node : this.legacyNodesForMigration) {
                this.regionStorage.trackNode(node);
                touchedRegions.add(WaypointRegionStorage.regionKeyOf(node.pos()));
            }
            for (TrafficSignalMarker signal : this.legacySignalsForMigration) {
                this.regionStorage.trackSignal(signal);
                touchedRegions.add(WaypointRegionStorage.regionKeyOf(signal.pos()));
            }
            this.regionStorage.flush(level, touchedRegions, this.nodes, this.signals);
            this.legacyNodesForMigration = List.of();
            this.legacySignalsForMigration = List.of();

            this.setDirty();
            //? if >=1.21.2 {
            level.getDataStorage().saveAndJoin();
            //?} else
            /*level.getDataStorage().save();*/
            return;
        }

        WaypointRegionStorage.LoadResult loaded = this.regionStorage.loadAll(level);
        this.populateFrom(loaded.nodes(), loaded.signals());
    }

    private void populateFrom(List<WaypointNode> nodeList, List<TrafficSignalMarker> signalList) {
        for (WaypointNode node : nodeList) {
            this.nodes.put(node.id(), node);
            this.nodeIndex.add(node.id(), node.pos());
        }

        for (WaypointNode node : nodeList) {
            for (int targetId : node.oneWayLinks()) {
                this.incomingOneWayLinks.computeIfAbsent(targetId, k -> new HashSet<>()).add(node.id());
            }
        }
        for (TrafficSignalMarker signal : signalList) {
            this.signals.put(signal.id(), signal);
            this.signalIndex.add(signal.id(), signal.pos());
            this.signalsByEdge.put(edgeKey(signal.edgeFromId(), signal.edgeToId()), signal);
            this.signalGroupRefCounts.merge(signal.groupKey(), 1, Integer::sum);
        }
    }

    private static long edgeKey(int edgeFromId, int edgeToId) {
        return ((long) edgeFromId << 32) | (edgeToId & 0xFFFFFFFFL);
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

    public record PendingSignalPen(String intersectionName, char direction, int greenTicks, int redTicks, int phaseOffsetTicks) {
        public static final PendingSignalPen DEFAULT = new PendingSignalPen("", 'N',
                SignalGroupConfig.DEFAULT_GREEN_TICKS, SignalGroupConfig.DEFAULT_RED_TICKS, 0);

        public String groupKey() {
            return this.intersectionName + "_" + this.direction;
        }
    }

    public static WaypointNetworkSavedData get(ServerLevel level) {
    //? if >=1.21.5 {
    WaypointNetworkSavedData data = level.getDataStorage().computeIfAbsent(TYPE);
    //?}
    //? if >=1.20.2 && <1.21.5 {
    /*WaypointNetworkSavedData data = level.getDataStorage().computeIfAbsent(FACTORY, "mineturismo_waypoints");*/
    //?}
    //? if >=1.17 && <1.20.2 {
    /*WaypointNetworkSavedData data = level.getDataStorage().computeIfAbsent(
            tag -> CODEC.parse(NbtOps.INSTANCE, tag).result().orElseGet(WaypointNetworkSavedData::new),
            WaypointNetworkSavedData::new,
            "mineturismo_waypoints");*/
    //?}
    //? if <1.17 {
    /*WaypointNetworkSavedData data = level.getDataStorage().computeIfAbsent(
            WaypointNetworkSavedData::new, "mineturismo_waypoints");*/
    //?}
        data.level = level;
        if (data.isNewlyCreated) {

            com.examplebett.entity.TownTrafficPresetPool.getDefault(com.examplebett.compat.ModResources.getServerResourceManager(level.getServer()))
                    .ifPresent(preset -> {
                        data.disabledModelIds.addAll(
                                com.examplebett.entity.TownTrafficPresetPool.computeDisabledIds(preset));
                        data.rareModelIds.addAll(preset.rareModelIds());
                        data.setDirty();
                    });
            data.isNewlyCreated = false;
        }
        data.ensureRegionsLoaded(level);
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
        tag.putInt("next_id", this.nextId);
        saveStringSet(tag, "disabled_models", this.disabledModelIds);
        saveStringSet(tag, "rare_models", this.rareModelIds);
        saveStringSet(tag, "traffic_plate_ids_pool", this.trafficPlateIds);
        saveStringSet(tag, "traffic_plate_ids_exclusive", this.exclusivePlateIds);
        tag.putBoolean("use_legacy_steering", this.useLegacySteering);
        tag.putBoolean("town_traffic_enabled", this.townTrafficEnabled);
        tag.putInt("max_spawned_cars", this.maxSpawnedCars);
        tag.putInt("ai_throttle_interval_ticks", this.aiThrottleIntervalTicks);
        tag.putInt("spawn_interval_ticks", this.spawnIntervalTicks);
        tag.putBoolean("recall_distant_cars_enabled", this.recallDistantCarsEnabled);
        tag.putBoolean("night_lights_enabled", this.nightLightsEnabled);
        tag.putInt("next_signal_id", this.nextSignalId);
        ListTag signalGroupsList = new ListTag();
        for (SignalGroupConfig config : this.signalGroups.values()) {
            signalGroupsList.add(config.save(new CompoundTag()));
        }
        tag.put("signal_groups", signalGroupsList);
        tag.putFloat("ambient_engine_volume_multiplier", this.ambientEngineVolumeMultiplier);
        return tag;
    }*/
    //?}

    //? if <1.17 && >=1.16 {
    /*public void load(CompoundTag tag) {

        this.isNewlyCreated = false;
        WaypointNetworkSavedData decoded = CODEC.parse(NbtOps.INSTANCE, tag).result().orElseGet(WaypointNetworkSavedData::new);
        this.legacyNodesForMigration = decoded.legacyNodesForMigration;
        this.legacySignalsForMigration = decoded.legacySignalsForMigration;
        this.nextId = decoded.nextId;
        this.disabledModelIds.clear();
        this.disabledModelIds.addAll(decoded.disabledModelIds);
        this.rareModelIds.clear();
        this.rareModelIds.addAll(decoded.rareModelIds);
        this.trafficPlateIds.clear();
        this.trafficPlateIds.addAll(decoded.trafficPlateIds);
        this.exclusivePlateIds.clear();
        this.exclusivePlateIds.addAll(decoded.exclusivePlateIds);
        this.useLegacySteering = decoded.useLegacySteering;
        this.townTrafficEnabled = decoded.townTrafficEnabled;
        this.maxSpawnedCars = decoded.maxSpawnedCars;
        this.aiThrottleIntervalTicks = decoded.aiThrottleIntervalTicks;
        this.spawnIntervalTicks = decoded.spawnIntervalTicks;
        this.recallDistantCarsEnabled = decoded.recallDistantCarsEnabled;
        this.nightLightsEnabled = decoded.nightLightsEnabled;
        this.nextSignalId = decoded.nextSignalId;
        this.signalGroups.clear();
        this.signalGroups.putAll(decoded.signalGroups);
        this.ambientEngineVolumeMultiplier = decoded.ambientEngineVolumeMultiplier;
    }*/
    //?}
    //? if <1.16 {
    /*public void load(CompoundTag tag) {

        this.isNewlyCreated = false;
        this.nextId = tag.getInt("next_id");
        this.disabledModelIds.clear();
        this.disabledModelIds.addAll(loadStringSet(tag, "disabled_models"));
        this.rareModelIds.clear();
        this.rareModelIds.addAll(loadStringSet(tag, "rare_models"));
        this.trafficPlateIds.clear();
        this.trafficPlateIds.addAll(loadStringSet(tag, "traffic_plate_ids_pool"));
        this.exclusivePlateIds.clear();
        this.exclusivePlateIds.addAll(loadStringSet(tag, "traffic_plate_ids_exclusive"));
        this.useLegacySteering = tag.getBoolean("use_legacy_steering");
        this.townTrafficEnabled = !tag.contains("town_traffic_enabled") || tag.getBoolean("town_traffic_enabled");
        this.maxSpawnedCars = tag.contains("max_spawned_cars") ? tag.getInt("max_spawned_cars") : DEFAULT_MAX_SPAWNED_CARS;
        this.aiThrottleIntervalTicks = tag.contains("ai_throttle_interval_ticks") ? tag.getInt("ai_throttle_interval_ticks") : DEFAULT_AI_THROTTLE_INTERVAL_TICKS;
        this.spawnIntervalTicks = tag.contains("spawn_interval_ticks") ? tag.getInt("spawn_interval_ticks") : DEFAULT_SPAWN_INTERVAL_TICKS;
        this.recallDistantCarsEnabled = tag.getBoolean("recall_distant_cars_enabled");
        this.nightLightsEnabled = !tag.contains("night_lights_enabled") || tag.getBoolean("night_lights_enabled");
        this.nextSignalId = tag.getInt("next_signal_id");
        this.signalGroups.clear();
        ListTag signalGroupsList = tag.getList("signal_groups", 10);
        for (int i = 0; i < signalGroupsList.size(); i++) {
            SignalGroupConfig config = SignalGroupConfig.load(signalGroupsList.getCompound(i));
            this.signalGroups.put(config.groupKey(), config);
        }
        this.ambientEngineVolumeMultiplier = tag.contains("ambient_engine_volume_multiplier") ? tag.getFloat("ambient_engine_volume_multiplier") : DEFAULT_AMBIENT_ENGINE_VOLUME_MULTIPLIER;
    }

    private static void saveStringSet(CompoundTag tag, String key, Set<String> values) {
        ListTag list = new ListTag();
        for (String value : values) {
            list.add(net.minecraft.nbt.StringTag.valueOf(value));
        }
        tag.put(key, list);
    }

    private static List<String> loadStringSet(CompoundTag tag, String key) {
        List<String> result = new ArrayList<>();
        ListTag list = tag.getList(key, 8);
        for (int i = 0; i < list.size(); i++) {
            result.add(list.getString(i));
        }
        return result;
    }*/
    //?}

    public Map<Integer, WaypointNode> getNodes() {
        return this.nodes;
    }

    public WaypointNode findNear(BlockPos targetPos, double radius) {
        double radiusSq = radius * radius;
        WaypointNode closest = null;
        double closestDistSq = Double.MAX_VALUE;
        for (int id : this.nodeIndex.candidatesNear(targetPos, radius)) {
            WaypointNode node = this.nodes.get(id);
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

    public List<WaypointNode> findNodesWithinRadius(BlockPos origin, double radius) {
        double radiusSq = radius * radius;
        List<WaypointNode> result = new ArrayList<>();
        for (int id : this.nodeIndex.candidatesNear(origin, radius)) {
            WaypointNode node = this.nodes.get(id);
            if (node != null && node.pos().distSqr(origin) <= radiusSq) {
                result.add(node);
            }
        }
        return result;
    }

    public int addWaypoint(BlockPos pos, float speedKmh) {
        int id = this.nextId++;
        WaypointNode node = new WaypointNode(id, pos, new int[0], speedKmh, new int[0]);
        this.nodes.put(id, node);
        this.nodeIndex.add(id, pos);
        this.regionStorage.trackNode(node);
        this.regionStorage.flush(this.level, Set.of(WaypointRegionStorage.regionKeyOf(pos)), this.nodes, this.signals);
        this.setDirty();
        return id;
    }

    public void removeWaypoint(int id) {
        WaypointNode removed = this.nodes.remove(id);
        if (removed == null) {
            return;
        }
        this.nodeIndex.remove(id);
        Set<Long> touchedRegions = new HashSet<>();
        touchedRegions.add(WaypointRegionStorage.regionKeyOf(removed.pos()));
        this.regionStorage.untrackNode(id);

        for (int neighborId : removed.links()) {
            WaypointNode neighbor = this.nodes.get(neighborId);
            if (neighbor != null) {
                WaypointNode updatedNeighbor = neighbor.withLinks(withoutValue(neighbor.links(), id));
                this.nodes.put(neighborId, updatedNeighbor);
                touchedRegions.add(WaypointRegionStorage.regionKeyOf(updatedNeighbor.pos()));
            }
        }

        Set<Integer> incomingIds = this.incomingOneWayLinks.remove(id);
        if (incomingIds != null) {
            for (int sourceId : incomingIds) {
                WaypointNode source = this.nodes.get(sourceId);
                if (source != null) {
                    WaypointNode updatedSource = source.withOneWayLinks(withoutValue(source.oneWayLinks(), id));
                    this.nodes.put(sourceId, updatedSource);
                    touchedRegions.add(WaypointRegionStorage.regionKeyOf(updatedSource.pos()));
                }
                Long signalRegion = this.removeSignalOnEdge(sourceId, id);
                if (signalRegion != null) {
                    touchedRegions.add(signalRegion);
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
            Long signalRegion = this.removeSignalOnEdge(id, targetId);
            if (signalRegion != null) {
                touchedRegions.add(signalRegion);
            }
        }

        this.regionStorage.flush(this.level, touchedRegions, this.nodes, this.signals);
        this.setDirty();
    }

    private Long removeSignalOnEdge(int edgeFromId, int edgeToId) {
        TrafficSignalMarker signal = this.signalsByEdge.remove(edgeKey(edgeFromId, edgeToId));
        if (signal != null) {
            this.signals.remove(signal.id());
            this.signalIndex.remove(signal.id());
            this.regionStorage.untrackSignal(signal.id());
            this.decrementGroupRef(signal.groupKey());
            return WaypointRegionStorage.regionKeyOf(signal.pos());
        }
        return null;
    }

    public void link(int fromId, int toId) {
        if (fromId == toId) {
            return;
        }
        WaypointNode from = this.nodes.get(fromId);
        WaypointNode to = this.nodes.get(toId);
        if (from == null || to == null) {
            return;
        }
        Set<Long> touchedRegions = new HashSet<>();
        if (!from.hasLink(toId)) {
            WaypointNode updatedFrom = from.withLinks(withAppended(from.links(), toId));
            this.nodes.put(fromId, updatedFrom);
            touchedRegions.add(WaypointRegionStorage.regionKeyOf(updatedFrom.pos()));
        }
        if (!to.hasLink(fromId)) {
            WaypointNode updatedTo = to.withLinks(withAppended(to.links(), fromId));
            this.nodes.put(toId, updatedTo);
            touchedRegions.add(WaypointRegionStorage.regionKeyOf(updatedTo.pos()));
        }
        this.regionStorage.flush(this.level, touchedRegions, this.nodes, this.signals);
        this.setDirty();
    }

    public void linkOneWay(int fromId, int toId) {
        if (fromId == toId) {
            return;
        }
        WaypointNode from = this.nodes.get(fromId);
        WaypointNode to = this.nodes.get(toId);
        if (from == null || to == null) {
            return;
        }
        if (!from.hasOneWayLink(toId)) {
            WaypointNode updatedFrom = from.withOneWayLinks(withAppended(from.oneWayLinks(), toId));
            this.nodes.put(fromId, updatedFrom);
            this.incomingOneWayLinks.computeIfAbsent(toId, k -> new HashSet<>()).add(fromId);
            this.regionStorage.flush(this.level, Set.of(WaypointRegionStorage.regionKeyOf(updatedFrom.pos())), this.nodes, this.signals);
        }
        this.setDirty();
    }

    public boolean contains(int id) {
        return this.nodes.containsKey(id);
    }

    public Set<Integer> getIncomingOneWayLinks(int nodeId) {
        return this.incomingOneWayLinks.getOrDefault(nodeId, Set.of());
    }

    public Map<Integer, TrafficSignalMarker> getSignals() {
        return this.signals;
    }

    public TrafficSignalMarker findSignalNear(BlockPos targetPos, double radius) {
        double radiusSq = radius * radius;
        TrafficSignalMarker closest = null;
        double closestDistSq = Double.MAX_VALUE;
        for (int id : this.signalIndex.candidatesNear(targetPos, radius)) {
            TrafficSignalMarker signal = this.signals.get(id);
            if (signal == null) {
                continue;
            }
            double distSq = signal.pos().distSqr(targetPos);
            if (distSq <= radiusSq && distSq < closestDistSq) {
                closest = signal;
                closestDistSq = distSq;
            }
        }
        return closest;
    }

    public List<TrafficSignalMarker> findSignalsWithinRadius(BlockPos origin, double radius) {
        double radiusSq = radius * radius;
        List<TrafficSignalMarker> result = new ArrayList<>();
        for (int id : this.signalIndex.candidatesNear(origin, radius)) {
            TrafficSignalMarker signal = this.signals.get(id);
            if (signal != null && signal.pos().distSqr(origin) <= radiusSq) {
                result.add(signal);
            }
        }
        return result;
    }

    public TrafficSignalMarker findSignalOnEdge(int edgeFromId, int edgeToId) {
        return this.signalsByEdge.get(edgeKey(edgeFromId, edgeToId));
    }

    public int addSignal(BlockPos pos, int edgeFromId, int edgeToId, double stopDistanceFromEnd, PendingSignalPen pen) {
        int id = this.nextSignalId++;
        PendingSignalPen effectivePen = pen.intersectionName().isEmpty()
                ? new PendingSignalPen("signal" + id, pen.direction(), pen.greenTicks(), pen.redTicks(), pen.phaseOffsetTicks())
                : pen;
        String groupKey = effectivePen.groupKey();
        TrafficSignalMarker signal = new TrafficSignalMarker(id, pos, edgeFromId, edgeToId, groupKey, stopDistanceFromEnd);
        this.signals.put(id, signal);
        this.signalIndex.add(id, pos);
        this.signalsByEdge.put(edgeKey(edgeFromId, edgeToId), signal);
        this.regionStorage.trackSignal(signal);
        this.signalGroups.put(groupKey, new SignalGroupConfig(effectivePen.intersectionName(), effectivePen.direction(),
                effectivePen.greenTicks(), effectivePen.redTicks(), effectivePen.phaseOffsetTicks()));
        this.incrementGroupRef(groupKey);
        this.regionStorage.flush(this.level, Set.of(WaypointRegionStorage.regionKeyOf(pos)), this.nodes, this.signals);
        this.setDirty();
        return id;
    }

    public void removeSignal(int id) {
        TrafficSignalMarker removed = this.signals.remove(id);
        if (removed != null) {
            this.signalIndex.remove(id);
            this.signalsByEdge.remove(edgeKey(removed.edgeFromId(), removed.edgeToId()));
            this.regionStorage.untrackSignal(id);
            this.decrementGroupRef(removed.groupKey());
            this.regionStorage.flush(this.level, Set.of(WaypointRegionStorage.regionKeyOf(removed.pos())), this.nodes, this.signals);
            this.setDirty();
        }
    }

    public void applyPenToExistingSignal(int signalId, PendingSignalPen pen) {
        TrafficSignalMarker marker = this.signals.get(signalId);
        if (marker == null || pen.intersectionName().isEmpty()) {

            return;
        }
        String newGroupKey = pen.groupKey();
        this.signalGroups.put(newGroupKey, new SignalGroupConfig(pen.intersectionName(), pen.direction(),
                pen.greenTicks(), pen.redTicks(), pen.phaseOffsetTicks()));
        if (!marker.groupKey().equals(newGroupKey)) {
            String oldGroupKey = marker.groupKey();
            TrafficSignalMarker updated = marker.withGroupKey(newGroupKey);
            this.signals.put(signalId, updated);

            this.signalsByEdge.put(edgeKey(updated.edgeFromId(), updated.edgeToId()), updated);
            this.incrementGroupRef(newGroupKey);
            this.decrementGroupRef(oldGroupKey);

            this.regionStorage.flush(this.level, Set.of(WaypointRegionStorage.regionKeyOf(updated.pos())), this.nodes, this.signals);
        }
        this.setDirty();
    }

    private void incrementGroupRef(String groupKey) {
        this.signalGroupRefCounts.merge(groupKey, 1, Integer::sum);
    }

    private void decrementGroupRef(String groupKey) {
        Integer count = this.signalGroupRefCounts.get(groupKey);
        if (count == null) {
            return;
        }
        if (count <= 1) {
            this.signalGroupRefCounts.remove(groupKey);
            this.signalGroups.remove(groupKey);
        } else {
            this.signalGroupRefCounts.put(groupKey, count - 1);
        }
    }

    public SignalGroupConfig getSignalGroup(String groupKey) {
        return this.signalGroups.get(groupKey);
    }

    public void trackActiveTownCar(int carEntityId, BlockPos pos) {
        this.activeTownCarIndex.add(carEntityId, pos);
        this.activeTownCarPositions.put(carEntityId, pos);
    }

    public void updateActiveTownCarPosition(int carEntityId, BlockPos pos) {
        this.activeTownCarIndex.update(carEntityId, pos);
        this.activeTownCarPositions.put(carEntityId, pos);
    }

    public void untrackActiveTownCar(int carEntityId) {
        this.activeTownCarIndex.remove(carEntityId);
        this.activeTownCarPositions.remove(carEntityId);
    }

    public int countActiveTownCarsNear(BlockPos origin, double radius) {
        double radiusSq = radius * radius;
        int count = 0;
        for (int id : this.activeTownCarIndex.candidatesNear(origin, radius)) {
            BlockPos pos = this.activeTownCarPositions.get(id);
            if (pos != null && pos.distSqr(origin) <= radiusSq) {
                count++;
            }
        }
        return count;
    }

    public Integer getEditingSignalId(UUID playerId) {
        return this.editingSignalIdByPlayer.get(playerId);
    }

    public void setEditingSignalId(UUID playerId, int signalId) {
        this.editingSignalIdByPlayer.put(playerId, signalId);
    }

    public void clearEditingSignalId(UUID playerId) {
        this.editingSignalIdByPlayer.remove(playerId);
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

    public float getPenSpeedKmh(UUID playerId) {
        return this.penSpeedByPlayer.getOrDefault(playerId, WaypointNode.DEFAULT_SPEED_KMH);
    }

    public void setPenSpeedKmh(UUID playerId, float speedKmh) {
        this.penSpeedByPlayer.put(playerId, speedKmh);
    }

    public boolean isOneWayReversed(UUID playerId) {
        return this.oneWayReversedByPlayer.getOrDefault(playerId, false);
    }

    public void setOneWayReversed(UUID playerId, boolean reversed) {
        this.oneWayReversedByPlayer.put(playerId, reversed);
    }

    public PendingSignalPen getPendingSignalPen(UUID playerId) {
        return this.pendingSignalByPlayer.getOrDefault(playerId, PendingSignalPen.DEFAULT);
    }

    public void setPendingSignalPen(UUID playerId, PendingSignalPen pen) {
        this.pendingSignalByPlayer.put(playerId, pen);
    }

    public String encodeClientSettings() {
        StringBuilder sb = new StringBuilder();
        sb.append("disabled=").append(String.join(",", this.disabledModelIds)).append('\n');
        sb.append("rare=").append(String.join(",", this.rareModelIds)).append('\n');
        sb.append("plates=").append(String.join(",", this.getTrafficPlateIds())).append('\n');
        sb.append("exclusivePlates=").append(String.join(",", this.getExclusivePlateIds())).append('\n');
        sb.append("townTraffic=").append(this.isTownTrafficEnabled()).append('\n');
        sb.append("legacySteering=").append(this.isLegacySteeringEnabled()).append('\n');
        sb.append("maxCars=").append(this.getMaxSpawnedCars()).append('\n');
        sb.append("aiThrottle=").append(this.getAiThrottleIntervalTicks()).append('\n');
        sb.append("spawnInterval=").append(this.getSpawnIntervalTicks()).append('\n');
        sb.append("recall=").append(this.isRecallDistantCarsEnabled()).append('\n');
        sb.append("nightLights=").append(this.isNightLightsEnabled()).append('\n');

        List<String> customCars = new ArrayList<>();
        for (CustomTrafficCar car : this.customTrafficCars) {
            customCars.add(car.id() + ":" + car.modelId() + ":" + car.rare());
        }
        sb.append("customCars=").append(String.join(",", customCars)).append('\n');
        return sb.toString();
    }

    public List<CustomTrafficCar> getCustomTrafficCars() {
        return java.util.Collections.unmodifiableList(this.customTrafficCars);
    }

    public boolean addCustomTrafficCar(CompoundTag tag) {
        if (this.customTrafficCars.size() >= MAX_CUSTOM_TRAFFIC_CARS) {
            return false;
        }
        String id = UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        this.customTrafficCars.add(new CustomTrafficCar(id, tag.copy(), false));
        this.setDirty();
        return true;
    }

    public void removeCustomTrafficCar(String id) {
        if (this.customTrafficCars.removeIf(car -> car.id().equals(id))) {
            this.setDirty();
        }
    }

    public void setCustomTrafficCarRare(String id, boolean rare) {
        for (int i = 0; i < this.customTrafficCars.size(); i++) {
            CustomTrafficCar car = this.customTrafficCars.get(i);
            if (car.id().equals(id) && car.rare() != rare) {
                this.customTrafficCars.set(i, new CustomTrafficCar(car.id(), car.tag(), rare));
                this.setDirty();
            }
        }
    }

    public Set<String> getDisabledModelIds() {
        return this.disabledModelIds;
    }

    public void setDisabledModelIds(Set<String> modelIds) {
        this.disabledModelIds.clear();
        this.disabledModelIds.addAll(modelIds);
        this.setDirty();
    }

    public Set<String> getRareModelIds() {
        return this.rareModelIds;
    }

    public void setRareModelIds(Set<String> modelIds) {
        this.rareModelIds.clear();
        this.rareModelIds.addAll(modelIds);
        this.setDirty();
    }

    public Set<String> getTrafficPlateIds() {
        return this.trafficPlateIds;
    }

    public void setTrafficPlateIds(Set<String> plateIds) {
        this.trafficPlateIds.clear();
        this.trafficPlateIds.addAll(plateIds);
        this.setDirty();
    }

    public Set<String> getExclusivePlateIds() {
        return this.exclusivePlateIds;
    }

    public void setExclusivePlateIds(Set<String> plateIds) {
        this.exclusivePlateIds.clear();
        this.exclusivePlateIds.addAll(plateIds);
        this.setDirty();
    }

    public boolean isLegacySteeringEnabled() {
        return this.useLegacySteering;
    }

    public void setLegacySteeringEnabled(boolean enabled) {
        this.useLegacySteering = enabled;
        this.setDirty();
    }

    public boolean isTownTrafficEnabled() {
        return this.townTrafficEnabled;
    }

    public void setTownTrafficEnabled(boolean enabled) {
        this.townTrafficEnabled = enabled;
        this.setDirty();
    }

    public int getMaxSpawnedCars() {
        return this.maxSpawnedCars;
    }

    public void setMaxSpawnedCars(int maxSpawnedCars) {
        this.maxSpawnedCars = Mth.clamp(maxSpawnedCars, MIN_MAX_SPAWNED_CARS, MAX_MAX_SPAWNED_CARS);
        this.setDirty();
    }

    public int getAiThrottleIntervalTicks() {
        return this.aiThrottleIntervalTicks;
    }

    public void setAiThrottleIntervalTicks(int aiThrottleIntervalTicks) {
        this.aiThrottleIntervalTicks = Mth.clamp(aiThrottleIntervalTicks, MIN_AI_THROTTLE_INTERVAL_TICKS, MAX_AI_THROTTLE_INTERVAL_TICKS);
        this.setDirty();
    }

    public int getSpawnIntervalTicks() {
        return this.spawnIntervalTicks;
    }

    public void setSpawnIntervalTicks(int spawnIntervalTicks) {
        this.spawnIntervalTicks = Mth.clamp(spawnIntervalTicks, MIN_SPAWN_INTERVAL_TICKS, MAX_SPAWN_INTERVAL_TICKS);
        this.setDirty();
    }

    public boolean isRecallDistantCarsEnabled() {
        return this.recallDistantCarsEnabled;
    }

    public void setRecallDistantCarsEnabled(boolean enabled) {
        this.recallDistantCarsEnabled = enabled;
        this.setDirty();
    }

    public boolean isNightLightsEnabled() {
        return this.nightLightsEnabled;
    }

    public void setNightLightsEnabled(boolean enabled) {
        this.nightLightsEnabled = enabled;
        this.setDirty();
    }

    public float getAmbientEngineVolumeMultiplier() {
        return this.ambientEngineVolumeMultiplier;
    }

    public void setAmbientEngineVolumeMultiplier(float multiplier) {
        this.ambientEngineVolumeMultiplier = Mth.clamp(multiplier,
                MIN_AMBIENT_ENGINE_VOLUME_MULTIPLIER, MAX_AMBIENT_ENGINE_VOLUME_MULTIPLIER);
        this.setDirty();
    }

    public int getSpawnCooldown(UUID playerId) {
        return this.spawnCooldownByPlayer.getOrDefault(playerId, 0);
    }

    public void setSpawnCooldown(UUID playerId, int ticks) {
        this.spawnCooldownByPlayer.put(playerId, ticks);
    }
}
