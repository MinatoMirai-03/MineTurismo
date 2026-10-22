package com.examplebett.network;

import net.minecraft.network.FriendlyByteBuf;
import com.examplebett.AiObjectMod;
//? if >=1.20.5 {
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else
/*import net.minecraft.resources.ResourceLocation;*/

public record SetTownTrafficPlateIdsPayload(String plateIds, String exclusivePlateIds)
        //? if >=1.20.5
        implements CustomPacketPayload
{

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<SetTownTrafficPlateIdsPayload> TYPE =
            new CustomPacketPayload.Type<>(AiObjectMod.id("set_town_traffic_plate_ids"));

    public static final StreamCodec<FriendlyByteBuf, SetTownTrafficPlateIdsPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, SetTownTrafficPlateIdsPayload::plateIds,
            ByteBufCodecs.STRING_UTF8, SetTownTrafficPlateIdsPayload::exclusivePlateIds,
            SetTownTrafficPlateIdsPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*public static final ResourceLocation ID = AiObjectMod.id("set_town_traffic_plate_ids");

    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(plateIds);
        buf.writeUtf(exclusivePlateIds);
    }

    public static SetTownTrafficPlateIdsPayload read(FriendlyByteBuf buf) {
        return new SetTownTrafficPlateIdsPayload(buf.readUtf(), buf.readUtf());
    }
    *///?}
}
