package com.examplebett.network;

import net.minecraft.network.FriendlyByteBuf;
import com.examplebett.AiObjectMod;
//? if >=1.20.5 {
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else
/*import net.minecraft.resources.ResourceLocation;*/

public record SetGttPayload(boolean active)
        //? if >=1.20.5
        implements CustomPacketPayload
{

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<SetGttPayload> TYPE =
            new CustomPacketPayload.Type<>(AiObjectMod.id("set_gtt"));

    public static final StreamCodec<FriendlyByteBuf, SetGttPayload> STREAM_CODEC = StreamCodec.of(
            (buf, value) -> buf.writeBoolean(value.active()),
            buf -> new SetGttPayload(buf.readBoolean())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*public static final ResourceLocation ID = AiObjectMod.id("set_gtt");

    public void write(FriendlyByteBuf buf) {
        buf.writeBoolean(active);
    }

    public static SetGttPayload read(FriendlyByteBuf buf) {
        return new SetGttPayload(buf.readBoolean());
    }
    *///?}
}
