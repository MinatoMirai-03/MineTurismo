package com.examplebett.network;

import net.minecraft.network.FriendlyByteBuf;
import com.examplebett.AiObjectMod;
//? if >=1.20.5 {
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else
/*import net.minecraft.resources.ResourceLocation;*/

public record SetCarTiltPayload(int entityId, float rollDegrees, float pitchDegrees, float heightCm)
        //? if >=1.20.5
        implements CustomPacketPayload
{

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<SetCarTiltPayload> TYPE =
            new CustomPacketPayload.Type<>(AiObjectMod.id("set_car_tilt"));

    public static final StreamCodec<FriendlyByteBuf, SetCarTiltPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SetCarTiltPayload::entityId,
            ByteBufCodecs.FLOAT, SetCarTiltPayload::rollDegrees,
            ByteBufCodecs.FLOAT, SetCarTiltPayload::pitchDegrees,
            ByteBufCodecs.FLOAT, SetCarTiltPayload::heightCm,
            SetCarTiltPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*public static final ResourceLocation ID = AiObjectMod.id("set_car_tilt");

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(entityId);
        buf.writeFloat(rollDegrees);
        buf.writeFloat(pitchDegrees);
        buf.writeFloat(heightCm);
    }

    public static SetCarTiltPayload read(FriendlyByteBuf buf) {
        return new SetCarTiltPayload(buf.readVarInt(), buf.readFloat(), buf.readFloat(), buf.readFloat());
    }
    *///?}
}
