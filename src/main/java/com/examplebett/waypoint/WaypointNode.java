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

public record WaypointNode(int id, BlockPos pos, int[] links, float speedKmh, int[] oneWayLinks) {

    public static final float DEFAULT_SPEED_KMH = 45.0F;
    private static final int[] EMPTY_LINKS = new int[0];

    //? if >=1.16 {
    private static final Codec<int[]> INT_ARRAY_CODEC = Codec.INT_STREAM.xmap(IntStream::toArray, IntStream::of);

    public static final Codec<WaypointNode> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("id").forGetter(WaypointNode::id),
            BlockPos.CODEC.fieldOf("pos").forGetter(WaypointNode::pos),
            INT_ARRAY_CODEC.optionalFieldOf("links", EMPTY_LINKS).forGetter(WaypointNode::links),
            Codec.FLOAT.optionalFieldOf("speed_kmh", DEFAULT_SPEED_KMH).forGetter(WaypointNode::speedKmh),
            INT_ARRAY_CODEC.optionalFieldOf("one_way_links", EMPTY_LINKS).forGetter(WaypointNode::oneWayLinks)
    ).apply(instance, WaypointNode::new));
    //?}

    //? if <1.16 {
    /*public static WaypointNode load(CompoundTag tag) {
        int id = tag.getInt("id");
        BlockPos pos = new BlockPos(tag.getInt("pos_x"), tag.getInt("pos_y"), tag.getInt("pos_z"));
        int[] links = tag.contains("links") ? tag.getIntArray("links") : EMPTY_LINKS;
        float speedKmh = tag.contains("speed_kmh") ? tag.getFloat("speed_kmh") : DEFAULT_SPEED_KMH;
        int[] oneWayLinks = tag.contains("one_way_links") ? tag.getIntArray("one_way_links") : EMPTY_LINKS;
        return new WaypointNode(id, pos, links, speedKmh, oneWayLinks);
    }

    public CompoundTag save(CompoundTag tag) {
        tag.putInt("id", this.id);
        tag.putInt("pos_x", this.pos.getX());
        tag.putInt("pos_y", this.pos.getY());
        tag.putInt("pos_z", this.pos.getZ());
        tag.putIntArray("links", this.links);
        tag.putFloat("speed_kmh", this.speedKmh);
        tag.putIntArray("one_way_links", this.oneWayLinks);
        return tag;
    }*/
    //?}

    public WaypointNode withLinks(int[] newLinks) {
        return new WaypointNode(this.id, this.pos, newLinks, this.speedKmh, this.oneWayLinks);
    }

    public WaypointNode withOneWayLinks(int[] newOneWayLinks) {
        return new WaypointNode(this.id, this.pos, this.links, this.speedKmh, newOneWayLinks);
    }

    public boolean hasLink(int targetId) {
        for (int linkId : this.links) {
            if (linkId == targetId) {
                return true;
            }
        }
        return false;
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
