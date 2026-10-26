package com.examplebett.waypoint;

//? if >=1.16 {
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
//?}
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;

public record TrafficSignalMarker(int id, BlockPos pos, int edgeFromId, int edgeToId, String groupKey,
                                   double stopDistanceFromEnd) {

    //? if >=1.16 {
    public static final Codec<TrafficSignalMarker> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("id").forGetter(TrafficSignalMarker::id),
            BlockPos.CODEC.fieldOf("pos").forGetter(TrafficSignalMarker::pos),
            Codec.INT.fieldOf("edge_from_id").forGetter(TrafficSignalMarker::edgeFromId),
            Codec.INT.fieldOf("edge_to_id").forGetter(TrafficSignalMarker::edgeToId),
            Codec.STRING.fieldOf("group_key").forGetter(TrafficSignalMarker::groupKey),
            Codec.DOUBLE.fieldOf("stop_distance_from_end").forGetter(TrafficSignalMarker::stopDistanceFromEnd)
    ).apply(instance, TrafficSignalMarker::new));
    //?}

    //? if <1.16 {
    /*public static TrafficSignalMarker load(CompoundTag tag) {
        return new TrafficSignalMarker(
                tag.getInt("id"),
                new BlockPos(tag.getInt("pos_x"), tag.getInt("pos_y"), tag.getInt("pos_z")),
                tag.getInt("edge_from_id"),
                tag.getInt("edge_to_id"),
                tag.getString("group_key"),
                tag.getDouble("stop_distance_from_end")
        );
    }

    public CompoundTag save(CompoundTag tag) {
        tag.putInt("id", this.id);
        tag.putInt("pos_x", this.pos.getX());
        tag.putInt("pos_y", this.pos.getY());
        tag.putInt("pos_z", this.pos.getZ());
        tag.putInt("edge_from_id", this.edgeFromId);
        tag.putInt("edge_to_id", this.edgeToId);
        tag.putString("group_key", this.groupKey);
        tag.putDouble("stop_distance_from_end", this.stopDistanceFromEnd);
        return tag;
    }*/
    //?}

    public TrafficSignalMarker withGroupKey(String newGroupKey) {
        return new TrafficSignalMarker(this.id, this.pos, this.edgeFromId, this.edgeToId, newGroupKey, this.stopDistanceFromEnd);
    }
}
