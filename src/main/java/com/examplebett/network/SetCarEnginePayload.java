package com.examplebett.network;

import net.minecraft.network.FriendlyByteBuf;
import com.examplebett.AiObjectMod;
//? if >=1.20.5 {
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else
/*import net.minecraft.resources.ResourceLocation;*/

public record SetCarEnginePayload(int entityId, String soundId, float pitchIntensity, float topSpeedPitch, float loopUseFraction)
        //? if >=1.20.5
        implements CustomPacketPayload
{

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<SetCarEnginePayload> TYPE =
            new CustomPacketPayload.Type<>(AiObjectMod.id("set_car_engine"));

    public static final StreamCodec<FriendlyByteBuf, SetCarEnginePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SetCarEnginePayload::entityId,
            ByteBufCodecs.STRING_UTF8, SetCarEnginePayload::soundId,
            ByteBufCodecs.FLOAT, SetCarEnginePayload::pitchIntensity,
            ByteBufCodecs.FLOAT, SetCarEnginePayload::topSpeedPitch,
            ByteBufCodecs.FLOAT, SetCarEnginePayload::loopUseFraction,
            SetCarEnginePayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*public static final ResourceLocation ID = AiObjectMod.id("set_car_engine");

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(entityId);
        buf.writeUtf(soundId);
        buf.writeFloat(pitchIntensity);
        buf.writeFloat(topSpeedPitch);
        buf.writeFloat(loopUseFraction);
    }

    public static SetCarEnginePayload read(FriendlyByteBuf buf) {
        return new SetCarEnginePayload(buf.readVarInt(), buf.readUtf(), buf.readFloat(), buf.readFloat(), buf.readFloat());
    }
    *///?}
}
