package com.examplebett.network;

import net.minecraft.network.FriendlyByteBuf;
import com.examplebett.AiObjectMod;
//? if >=1.20.5 {
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else
/*import net.minecraft.resources.ResourceLocation;*/

public record AddCustomTrafficCarPayload(int inventorySlot)
        //? if >=1.20.5
        implements CustomPacketPayload
{

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<AddCustomTrafficCarPayload> TYPE =
            new CustomPacketPayload.Type<>(AiObjectMod.id("add_custom_traffic_car"));

    public static final StreamCodec<FriendlyByteBuf, AddCustomTrafficCarPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, AddCustomTrafficCarPayload::inventorySlot,
            AddCustomTrafficCarPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*public static final ResourceLocation ID = AiObjectMod.id("add_custom_traffic_car");

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(inventorySlot);
    }

    public static AddCustomTrafficCarPayload read(FriendlyByteBuf buf) {
        return new AddCustomTrafficCarPayload(buf.readVarInt());
    }
    *///?}
}
