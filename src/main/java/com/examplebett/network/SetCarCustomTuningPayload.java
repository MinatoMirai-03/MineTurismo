package com.examplebett.network;

import net.minecraft.network.FriendlyByteBuf;
import com.examplebett.AiObjectMod;
//? if >=1.20.5 {
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else
/*import net.minecraft.resources.ResourceLocation;*/

public record SetCarCustomTuningPayload(int entityId, float topSpeedKmh, float accelFraction, float brakeFraction, float handlingFraction, boolean tireSmokeAlways, boolean despawnEnabled)
        //? if >=1.20.5
        implements CustomPacketPayload
{

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<SetCarCustomTuningPayload> TYPE =
            new CustomPacketPayload.Type<>(AiObjectMod.id("set_car_custom_tuning"));

    //? if >=1.21.2 {
    public static final StreamCodec<FriendlyByteBuf, SetCarCustomTuningPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SetCarCustomTuningPayload::entityId,
            ByteBufCodecs.FLOAT, SetCarCustomTuningPayload::topSpeedKmh,
            ByteBufCodecs.FLOAT, SetCarCustomTuningPayload::accelFraction,
            ByteBufCodecs.FLOAT, SetCarCustomTuningPayload::brakeFraction,
            ByteBufCodecs.FLOAT, SetCarCustomTuningPayload::handlingFraction,
            ByteBufCodecs.BOOL, SetCarCustomTuningPayload::tireSmokeAlways,
            ByteBufCodecs.BOOL, SetCarCustomTuningPayload::despawnEnabled,
            SetCarCustomTuningPayload::new
    );
    //?} else
    /*public static final StreamCodec<FriendlyByteBuf, SetCarCustomTuningPayload> STREAM_CODEC = StreamCodec.of(
            (buf, value) -> {
                buf.writeVarInt(value.entityId());
                buf.writeFloat(value.topSpeedKmh());
                buf.writeFloat(value.accelFraction());
                buf.writeFloat(value.brakeFraction());
                buf.writeFloat(value.handlingFraction());
                buf.writeBoolean(value.tireSmokeAlways());
                buf.writeBoolean(value.despawnEnabled());
            },
            buf -> new SetCarCustomTuningPayload(buf.readVarInt(), buf.readFloat(), buf.readFloat(),
                    buf.readFloat(), buf.readFloat(), buf.readBoolean(), buf.readBoolean())
    );*/

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*public static final ResourceLocation ID = AiObjectMod.id("set_car_custom_tuning");

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(entityId);
        buf.writeFloat(topSpeedKmh);
        buf.writeFloat(accelFraction);
        buf.writeFloat(brakeFraction);
        buf.writeFloat(handlingFraction);
        buf.writeBoolean(tireSmokeAlways);
        buf.writeBoolean(despawnEnabled);
    }

    public static SetCarCustomTuningPayload read(FriendlyByteBuf buf) {
        return new SetCarCustomTuningPayload(buf.readVarInt(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readBoolean(), buf.readBoolean());
    }
    *///?}
}
