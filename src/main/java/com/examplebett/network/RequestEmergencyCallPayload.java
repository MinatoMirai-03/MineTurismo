package com.examplebett.network;

import net.minecraft.network.FriendlyByteBuf;
import com.examplebett.AiObjectMod;
//? if >=1.20.5 {
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else
/*import net.minecraft.resources.ResourceLocation;*/

public record RequestEmergencyCallPayload()
        //? if >=1.20.5
        implements CustomPacketPayload
{

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<RequestEmergencyCallPayload> TYPE =
            new CustomPacketPayload.Type<>(AiObjectMod.id("request_emergency_call"));

    public static final StreamCodec<FriendlyByteBuf, RequestEmergencyCallPayload> STREAM_CODEC = StreamCodec.of(
            (buf, value) -> {
            },
            buf -> new RequestEmergencyCallPayload()
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*public static final ResourceLocation ID = AiObjectMod.id("request_emergency_call");

    public void write(FriendlyByteBuf buf) {
    }

    public static RequestEmergencyCallPayload read(FriendlyByteBuf buf) {
        return new RequestEmergencyCallPayload();
    }
    *///?}
}
