package com.examplebett.network;

import net.minecraft.network.FriendlyByteBuf;
import com.examplebett.AiObjectMod;
//? if >=1.20.5 {
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else
/*import net.minecraft.resources.ResourceLocation;*/

public record UpdateCustomTrafficCarPayload(String id, int action)
        //? if >=1.20.5
        implements CustomPacketPayload
{

    public static final int ACTION_REMOVE = 0;
    public static final int ACTION_SET_NORMAL = 1;
    public static final int ACTION_SET_RARE = 2;

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<UpdateCustomTrafficCarPayload> TYPE =
            new CustomPacketPayload.Type<>(AiObjectMod.id("update_custom_traffic_car"));

    public static final StreamCodec<FriendlyByteBuf, UpdateCustomTrafficCarPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, UpdateCustomTrafficCarPayload::id,
            ByteBufCodecs.VAR_INT, UpdateCustomTrafficCarPayload::action,
            UpdateCustomTrafficCarPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*public static final ResourceLocation ID = AiObjectMod.id("update_custom_traffic_car");

    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(id);
        buf.writeVarInt(action);
    }

    public static UpdateCustomTrafficCarPayload read(FriendlyByteBuf buf) {
        return new UpdateCustomTrafficCarPayload(buf.readUtf(), buf.readVarInt());
    }
    *///?}
}
