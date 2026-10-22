package com.examplebett.network;

import net.minecraft.network.FriendlyByteBuf;
import com.examplebett.AiObjectMod;
//? if >=1.20.5 {
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else
/*import net.minecraft.resources.ResourceLocation;*/

public record SetCarLightPayload(int entityId, float headlightX, float headlightY, float headlightZ, float taillightX, float taillightY, float taillightZ, int lightMode, String headlightColorId)
        //? if >=1.20.5
        implements CustomPacketPayload
{

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<SetCarLightPayload> TYPE =
            new CustomPacketPayload.Type<>(AiObjectMod.id("set_car_light"));

    //? if >=1.21.5 {
    public static final StreamCodec<FriendlyByteBuf, SetCarLightPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SetCarLightPayload::entityId,
            ByteBufCodecs.FLOAT, SetCarLightPayload::headlightX,
            ByteBufCodecs.FLOAT, SetCarLightPayload::headlightY,
            ByteBufCodecs.FLOAT, SetCarLightPayload::headlightZ,
            ByteBufCodecs.FLOAT, SetCarLightPayload::taillightX,
            ByteBufCodecs.FLOAT, SetCarLightPayload::taillightY,
            ByteBufCodecs.FLOAT, SetCarLightPayload::taillightZ,
            ByteBufCodecs.VAR_INT, SetCarLightPayload::lightMode,
            ByteBufCodecs.STRING_UTF8, SetCarLightPayload::headlightColorId,
            SetCarLightPayload::new
    );
    //?} else
    /*public static final StreamCodec<FriendlyByteBuf, SetCarLightPayload> STREAM_CODEC = StreamCodec.of(
            (buf, value) -> {
                buf.writeVarInt(value.entityId());
                buf.writeFloat(value.headlightX());
                buf.writeFloat(value.headlightY());
                buf.writeFloat(value.headlightZ());
                buf.writeFloat(value.taillightX());
                buf.writeFloat(value.taillightY());
                buf.writeFloat(value.taillightZ());
                buf.writeVarInt(value.lightMode());
                buf.writeUtf(value.headlightColorId());
            },
            buf -> new SetCarLightPayload(buf.readVarInt(), buf.readFloat(), buf.readFloat(), buf.readFloat(),
                    buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readVarInt(), buf.readUtf())
    );*/

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*public static final ResourceLocation ID = AiObjectMod.id("set_car_light");

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(entityId);
        buf.writeFloat(headlightX);
        buf.writeFloat(headlightY);
        buf.writeFloat(headlightZ);
        buf.writeFloat(taillightX);
        buf.writeFloat(taillightY);
        buf.writeFloat(taillightZ);
        buf.writeVarInt(lightMode);
        buf.writeUtf(headlightColorId);
    }

    public static SetCarLightPayload read(FriendlyByteBuf buf) {
        return new SetCarLightPayload(buf.readVarInt(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readVarInt(), buf.readUtf());
    }
    *///?}
}
