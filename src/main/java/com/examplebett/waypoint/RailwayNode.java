package com.examplebett.waypoint;

//? if >=1.16 {
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
//?}
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;

//? if >=1.16 {
import java.util.stream.IntStream;
//?}

public record RailwayNode(int id, BlockPos pos, int[] oneWayLinks, float speedKmh) {

    public static final float DEFAULT_SPEED_KMH = 45.0F;
    private static final int[] EMPTY_LINKS = new int[0];

    //? if >=1.16 {
    private static final Codec<int[]> INT_ARRAY_CODEC = Codec.INT_STREAM.xmap(IntStream::toArray, IntStream::of);

    public static final Codec<RailwayNode> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("id").forGetter(RailwayNode::id),
            BlockPos.CODEC.fieldOf("pos").forGetter(RailwayNode::pos),
            INT_ARRAY_CODEC.optionalFieldOf("one_way_links", EMPTY_LINKS).forGetter(RailwayNode::oneWayLinks),
            Codec.FLOAT.optionalFieldOf("speed_kmh", DEFAULT_SPEED_KMH).forGetter(RailwayNode::speedKmh)
    ).apply(instance, RailwayNode::new));
    //?}

    //? if <1.16 {
    /*public static RailwayNode load(CompoundTag tag) {
        int id = tag.getInt("id");
        BlockPos pos = new BlockPos(tag.getInt("pos_x"), tag.getInt("pos_y"), tag.getInt("pos_z"));
        int[] oneWayLinks = tag.contains("one_way_links") ? tag.getIntArray("one_way_links") : EMPTY_LINKS;
        float speedKmh = tag.contains("speed_kmh") ? tag.getFloat("speed_kmh") : DEFAULT_SPEED_KMH;
        return new RailwayNode(id, pos, oneWayLinks, speedKmh);
    }

    public CompoundTag save(CompoundTag tag) {
        tag.putInt("id", this.id);
        tag.putInt("pos_x", this.pos.getX());
        tag.putInt("pos_y", this.pos.getY());
        tag.putInt("pos_z", this.pos.getZ());
        tag.putIntArray("one_way_links", this.oneWayLinks);
        tag.putFloat("speed_kmh", this.speedKmh);
        return tag;
    }*/
    //?}

    public RailwayNode withOneWayLinks(int[] newOneWayLinks) {
        return new RailwayNode(this.id, this.pos, newOneWayLinks, this.speedKmh);
    }

    public boolean hasOneWayLink(int targetId) {
        for (int linkId : this.oneWayLinks) {
            if (linkId == targetId) {
                return true;
            }
        }
        return false;
    }
}
