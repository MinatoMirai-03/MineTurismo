package com.examplebett.waypoint;

//? if >=1.16 {
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
//?}
import net.minecraft.nbt.CompoundTag;

public record SignalGroupConfig(String intersectionName, char direction, int greenTicks, int redTicks, int phaseOffsetTicks) {

    public static final int DEFAULT_GREEN_TICKS = 200;
    public static final int DEFAULT_RED_TICKS = 200;
    public static final int MIN_PHASE_TICKS = 20;
    public static final int MAX_PHASE_TICKS = 2400;

    //? if >=1.16 {
    public static final Codec<SignalGroupConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.optionalFieldOf("intersection_name", "").forGetter(SignalGroupConfig::intersectionName),
            Codec.STRING.optionalFieldOf("direction", "N").xmap(s -> s.isEmpty() ? 'N' : s.charAt(0), String::valueOf).forGetter(SignalGroupConfig::direction),
            Codec.INT.optionalFieldOf("green_ticks", DEFAULT_GREEN_TICKS).forGetter(SignalGroupConfig::greenTicks),
            Codec.INT.optionalFieldOf("red_ticks", DEFAULT_RED_TICKS).forGetter(SignalGroupConfig::redTicks),
            Codec.INT.optionalFieldOf("phase_offset_ticks", 0).forGetter(SignalGroupConfig::phaseOffsetTicks)
    ).apply(instance, SignalGroupConfig::new));
    //?}

    //? if <1.16 {
    /*public static SignalGroupConfig load(CompoundTag tag) {
        String intersectionName = tag.contains("intersection_name") ? tag.getString("intersection_name") : "";
        String directionStr = tag.contains("direction") ? tag.getString("direction") : "N";
        char direction = directionStr.isEmpty() ? 'N' : directionStr.charAt(0);
        int greenTicks = tag.contains("green_ticks") ? tag.getInt("green_ticks") : DEFAULT_GREEN_TICKS;
        int redTicks = tag.contains("red_ticks") ? tag.getInt("red_ticks") : DEFAULT_RED_TICKS;
        int phaseOffsetTicks = tag.contains("phase_offset_ticks") ? tag.getInt("phase_offset_ticks") : 0;
        return new SignalGroupConfig(intersectionName, direction, greenTicks, redTicks, phaseOffsetTicks);
    }

    public CompoundTag save(CompoundTag tag) {
        tag.putString("intersection_name", this.intersectionName);
        tag.putString("direction", String.valueOf(this.direction));
        tag.putInt("green_ticks", this.greenTicks);
        tag.putInt("red_ticks", this.redTicks);
        tag.putInt("phase_offset_ticks", this.phaseOffsetTicks);
        return tag;
    }*/
    //?}

    public String groupKey() {
        return this.intersectionName + "_" + this.direction;
    }

    public boolean isGreenAt(long gameTime) {
        long cycleLength = this.greenTicks + this.redTicks;
        if (cycleLength <= 0) {
            return true;
        }
        long phase = Math.floorMod(gameTime + this.phaseOffsetTicks, cycleLength);
        return phase < this.greenTicks;
    }
}
