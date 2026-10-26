package com.examplebett.network;

import net.minecraft.network.FriendlyByteBuf;
import com.examplebett.AiObjectMod;
//? if >=1.20.5 {
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else
/*import net.minecraft.resources.ResourceLocation;*/

public record SetAppWeatherPayload(int kind)
        //? if >=1.20.5
        implements CustomPacketPayload
{

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<SetAppWeatherPayload> TYPE =
            new CustomPacketPayload.Type<>(AiObjectMod.id("set_app_weather"));

    public static final StreamCodec<FriendlyByteBuf, SetAppWeatherPayload> STREAM_CODEC = StreamCodec.of(
            (buf, value) -> buf.writeVarInt(value.kind()),
            buf -> new SetAppWeatherPayload(buf.readVarInt())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*public static final ResourceLocation ID = AiObjectMod.id("set_app_weather");

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(kind);
    }

    public static SetAppWeatherPayload read(FriendlyByteBuf buf) {
        return new SetAppWeatherPayload(buf.readVarInt());
    }
    *///?}
}
