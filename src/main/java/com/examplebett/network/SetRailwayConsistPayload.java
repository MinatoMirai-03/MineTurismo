package com.examplebett.network;

import net.minecraft.network.FriendlyByteBuf;
import com.examplebett.AiObjectMod;
//? if >=1.20.5 {
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else
/*import net.minecraft.resources.ResourceLocation;*/

public record SetRailwayConsistPayload(int headEntityId, String middleModelIdsCsv, String tailModelId)
        //? if >=1.20.5
        implements CustomPacketPayload
{

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<SetRailwayConsistPayload> TYPE =
            new CustomPacketPayload.Type<>(AiObjectMod.id("set_railway_consist"));

    public static final StreamCodec<FriendlyByteBuf, SetRailwayConsistPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SetRailwayConsistPayload::headEntityId,
            ByteBufCodecs.STRING_UTF8, SetRailwayConsistPayload::middleModelIdsCsv,
            ByteBufCodecs.STRING_UTF8, SetRailwayConsistPayload::tailModelId,
            SetRailwayConsistPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*public static final ResourceLocation ID = AiObjectMod.id("set_railway_consist");

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(headEntityId);
        buf.writeUtf(middleModelIdsCsv);
        buf.writeUtf(tailModelId);
    }

    public static SetRailwayConsistPayload read(FriendlyByteBuf buf) {
        return new SetRailwayConsistPayload(buf.readVarInt(), buf.readUtf(), buf.readUtf());
    }
    *///?}
}
