package com.examplebett.network;

import net.minecraft.network.FriendlyByteBuf;
import com.examplebett.AiObjectMod;
//? if >=1.20.5 {
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else
/*import net.minecraft.resources.ResourceLocation;*/

public record SetWaypointPenSpeedPayload(float speedKmh)
        //? if >=1.20.5
        implements CustomPacketPayload
{

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<SetWaypointPenSpeedPayload> TYPE =
            new CustomPacketPayload.Type<>(AiObjectMod.id("set_waypoint_pen_speed"));

    public static final StreamCodec<FriendlyByteBuf, SetWaypointPenSpeedPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, SetWaypointPenSpeedPayload::speedKmh,
            SetWaypointPenSpeedPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*public static final ResourceLocation ID = AiObjectMod.id("set_waypoint_pen_speed");

    public void write(FriendlyByteBuf buf) {
        buf.writeFloat(speedKmh);
    }

    public static SetWaypointPenSpeedPayload read(FriendlyByteBuf buf) {
        return new SetWaypointPenSpeedPayload(buf.readFloat());
    }
    *///?}
}
