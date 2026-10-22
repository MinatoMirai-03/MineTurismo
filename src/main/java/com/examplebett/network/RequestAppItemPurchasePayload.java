package com.examplebett.network;

import net.minecraft.network.FriendlyByteBuf;
import com.examplebett.AiObjectMod;
//? if >=1.20.5 {
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else
/*import net.minecraft.resources.ResourceLocation;*/

public record RequestAppItemPurchasePayload(String itemId)
        //? if >=1.20.5
        implements CustomPacketPayload
{

    private static final int MAX_ITEM_ID_LENGTH = 256;

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<RequestAppItemPurchasePayload> TYPE =
            new CustomPacketPayload.Type<>(AiObjectMod.id("request_app_item_purchase"));

    public static final StreamCodec<FriendlyByteBuf, RequestAppItemPurchasePayload> STREAM_CODEC = StreamCodec.of(
            (buf, value) -> buf.writeUtf(value.itemId(), MAX_ITEM_ID_LENGTH),
            buf -> new RequestAppItemPurchasePayload(buf.readUtf(MAX_ITEM_ID_LENGTH))
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*public static final ResourceLocation ID = AiObjectMod.id("request_app_item_purchase");

    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(itemId, MAX_ITEM_ID_LENGTH);
    }

    public static RequestAppItemPurchasePayload read(FriendlyByteBuf buf) {
        return new RequestAppItemPurchasePayload(buf.readUtf(MAX_ITEM_ID_LENGTH));
    }
    *///?}
}
