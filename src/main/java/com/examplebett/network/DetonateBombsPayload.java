package com.examplebett.network;

import net.minecraft.network.FriendlyByteBuf;
import com.examplebett.AiObjectMod;
//? if >=1.20.5 {
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else
/*import net.minecraft.resources.ResourceLocation;*/

public record DetonateBombsPayload()
        //? if >=1.20.5
        implements CustomPacketPayload
{

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<DetonateBombsPayload> TYPE =
            new CustomPacketPayload.Type<>(AiObjectMod.id("detonate_bombs"));

    public static final StreamCodec<FriendlyByteBuf, DetonateBombsPayload> STREAM_CODEC = StreamCodec.of(
            (buf, value) -> {
            },
            buf -> new DetonateBombsPayload()
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*public static final ResourceLocation ID = AiObjectMod.id("detonate_bombs");

    public void write(FriendlyByteBuf buf) {
    }

    public static DetonateBombsPayload read(FriendlyByteBuf buf) {
        return new DetonateBombsPayload();
    }
    *///?}
}
