package com.examplebett.waypoint;

//? if >=1.16 {
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
//?}
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

import java.util.ArrayList;
import java.util.List;

record WaypointRegionData(List<WaypointNode> nodes, List<TrafficSignalMarker> signals) {

    //? if >=1.16 {
    static final Codec<WaypointRegionData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            WaypointNode.CODEC.listOf().optionalFieldOf("nodes", List.of()).forGetter(WaypointRegionData::nodes),
            TrafficSignalMarker.CODEC.listOf().optionalFieldOf("signals", List.of()).forGetter(WaypointRegionData::signals)
    ).apply(instance, WaypointRegionData::new));
    //?}

    //? if <1.16 {
    /*static WaypointRegionData load(CompoundTag tag) {
        List<WaypointNode> nodes = new ArrayList<>();
        ListTag nodesList = tag.getList("nodes", 10);
        for (int i = 0; i < nodesList.size(); i++) {
            nodes.add(WaypointNode.load(nodesList.getCompound(i)));
        }
        List<TrafficSignalMarker> signals = new ArrayList<>();
        ListTag signalsList = tag.getList("signals", 10);
        for (int i = 0; i < signalsList.size(); i++) {
            signals.add(TrafficSignalMarker.load(signalsList.getCompound(i)));
        }
        return new WaypointRegionData(nodes, signals);
    }

    CompoundTag save(CompoundTag tag) {
        ListTag nodesList = new ListTag();
        for (WaypointNode node : this.nodes) {
            nodesList.add(node.save(new CompoundTag()));
        }
        tag.put("nodes", nodesList);
        ListTag signalsList = new ListTag();
        for (TrafficSignalMarker signal : this.signals) {
            signalsList.add(signal.save(new CompoundTag()));
        }
        tag.put("signals", signalsList);
        return tag;
    }*/
    //?}
}
