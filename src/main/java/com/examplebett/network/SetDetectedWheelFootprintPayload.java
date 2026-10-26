package com.examplebett.network;

import net.minecraft.network.FriendlyByteBuf;
import com.examplebett.AiObjectMod;
//? if >=1.20.5 {
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else
/*import net.minecraft.resources.ResourceLocation;*/

public record SetDetectedWheelFootprintPayload(int entityId, float trackHalfWidth, float rearZ)
        //? if >=1.20.5
        implements CustomPacketPayload
{

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<SetDetectedWheelFootprintPayload> TYPE =
            new CustomPacketPayload.Type<>(AiObjectMod.id("set_detected_wheel_footprint"));

    public static final StreamCodec<FriendlyByteBuf, SetDetectedWheelFootprintPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SetDetectedWheelFootprintPayload::entityId,
            ByteBufCodecs.FLOAT, SetDetectedWheelFootprintPayload::trackHalfWidth,
            ByteBufCodecs.FLOAT, SetDetectedWheelFootprintPayload::rearZ,
            SetDetectedWheelFootprintPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*public static final ResourceLocation ID = AiObjectMod.id("set_detected_wheel_footprint");

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(entityId);
        buf.writeFloat(trackHalfWidth);
        buf.writeFloat(rearZ);
    }

    public static SetDetectedWheelFootprintPayload read(FriendlyByteBuf buf) {
        return new SetDetectedWheelFootprintPayload(buf.readVarInt(), buf.readFloat(), buf.readFloat());
    }
    *///?}
}
