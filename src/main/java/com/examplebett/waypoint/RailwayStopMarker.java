package com.examplebett.waypoint;

//? if >=1.16 {
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
//?}
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;

public record RailwayStopMarker(int id, BlockPos pos, int edgeFromId, int edgeToId, double stopDistanceFromEnd) {

    //? if >=1.16 {
    public static final Codec<RailwayStopMarker> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("id").forGetter(RailwayStopMarker::id),
            BlockPos.CODEC.fieldOf("pos").forGetter(RailwayStopMarker::pos),
            Codec.INT.fieldOf("edge_from_id").forGetter(RailwayStopMarker::edgeFromId),
            Codec.INT.fieldOf("edge_to_id").forGetter(RailwayStopMarker::edgeToId),
            Codec.DOUBLE.fieldOf("stop_distance_from_end").forGetter(RailwayStopMarker::stopDistanceFromEnd)
    ).apply(instance, RailwayStopMarker::new));
    //?}

    //? if <1.16 {
    /*public static RailwayStopMarker load(CompoundTag tag) {
        return new RailwayStopMarker(
                tag.getInt("id"),
                new BlockPos(tag.getInt("pos_x"), tag.getInt("pos_y"), tag.getInt("pos_z")),
                tag.getInt("edge_from_id"),
                tag.getInt("edge_to_id"),
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
        tag.putDouble("stop_distance_from_end", this.stopDistanceFromEnd);
        return tag;
    }*/
    //?}

    public boolean touchesNode(int nodeId) {
        return this.edgeFromId == nodeId || this.edgeToId == nodeId;
    }
}
