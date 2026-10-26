package com.examplebett.network;

import net.minecraft.network.FriendlyByteBuf;
import com.examplebett.AiObjectMod;
//? if >=1.20.5 {
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else
/*import net.minecraft.resources.ResourceLocation;*/

public record BuyUsedCarPayload(String listingId)
        //? if >=1.20.5
        implements CustomPacketPayload
{

    private static final int MAX_ID_LENGTH = 128;

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<BuyUsedCarPayload> TYPE =
            new CustomPacketPayload.Type<>(AiObjectMod.id("buy_used_car"));

    public static final StreamCodec<FriendlyByteBuf, BuyUsedCarPayload> STREAM_CODEC = StreamCodec.of(
            (buf, value) -> buf.writeUtf(value.listingId(), MAX_ID_LENGTH),
            buf -> new BuyUsedCarPayload(buf.readUtf(MAX_ID_LENGTH))
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*public static final ResourceLocation ID = AiObjectMod.id("buy_used_car");

    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(listingId, MAX_ID_LENGTH);
    }

    public static BuyUsedCarPayload read(FriendlyByteBuf buf) {
        return new BuyUsedCarPayload(buf.readUtf(MAX_ID_LENGTH));
    }
    *///?}
}
