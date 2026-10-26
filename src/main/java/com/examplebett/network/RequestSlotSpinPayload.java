package com.examplebett.network;

import net.minecraft.network.FriendlyByteBuf;
import com.examplebett.AiObjectMod;
//? if >=1.20.5 {
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else
/*import net.minecraft.resources.ResourceLocation;*/

public record RequestSlotSpinPayload()
        //? if >=1.20.5
        implements CustomPacketPayload
{

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<RequestSlotSpinPayload> TYPE =
            new CustomPacketPayload.Type<>(AiObjectMod.id("request_slot_spin"));

    public static final StreamCodec<FriendlyByteBuf, RequestSlotSpinPayload> STREAM_CODEC = StreamCodec.of(
            (buf, value) -> {
            },
            buf -> new RequestSlotSpinPayload()
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*public static final ResourceLocation ID = AiObjectMod.id("request_slot_spin");

    public void write(FriendlyByteBuf buf) {
    }

    public static RequestSlotSpinPayload read(FriendlyByteBuf buf) {
        return new RequestSlotSpinPayload();
    }
    *///?}
}
