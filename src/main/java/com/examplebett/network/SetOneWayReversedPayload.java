package com.examplebett.network;

import net.minecraft.network.FriendlyByteBuf;
import com.examplebett.AiObjectMod;
//? if >=1.20.5 {
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else
/*import net.minecraft.resources.ResourceLocation;*/

public record SetOneWayReversedPayload(boolean reversed)
        //? if >=1.20.5
        implements CustomPacketPayload
{

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<SetOneWayReversedPayload> TYPE =
            new CustomPacketPayload.Type<>(AiObjectMod.id("set_one_way_reversed"));

    public static final StreamCodec<FriendlyByteBuf, SetOneWayReversedPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, SetOneWayReversedPayload::reversed,
            SetOneWayReversedPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*public static final ResourceLocation ID = AiObjectMod.id("set_one_way_reversed");

    public void write(FriendlyByteBuf buf) {
        buf.writeBoolean(reversed);
    }

    public static SetOneWayReversedPayload read(FriendlyByteBuf buf) {
        return new SetOneWayReversedPayload(buf.readBoolean());
    }
    *///?}
}
