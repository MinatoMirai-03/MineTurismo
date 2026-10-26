package com.examplebett.network;

import net.minecraft.network.FriendlyByteBuf;
import com.examplebett.AiObjectMod;
//? if >=1.20.5 {
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else
/*import net.minecraft.resources.ResourceLocation;*/

public record SetAppTimePayload(int kind)
        //? if >=1.20.5
        implements CustomPacketPayload
{

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<SetAppTimePayload> TYPE =
            new CustomPacketPayload.Type<>(AiObjectMod.id("set_app_time"));

    public static final StreamCodec<FriendlyByteBuf, SetAppTimePayload> STREAM_CODEC = StreamCodec.of(
            (buf, value) -> buf.writeVarInt(value.kind()),
            buf -> new SetAppTimePayload(buf.readVarInt())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*public static final ResourceLocation ID = AiObjectMod.id("set_app_time");

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(kind);
    }

    public static SetAppTimePayload read(FriendlyByteBuf buf) {
        return new SetAppTimePayload(buf.readVarInt());
    }
    *///?}
}
