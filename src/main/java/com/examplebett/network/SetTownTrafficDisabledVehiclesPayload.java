package com.examplebett.network;

import net.minecraft.network.FriendlyByteBuf;
import com.examplebett.AiObjectMod;
//? if >=1.20.5 {
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else
/*import net.minecraft.resources.ResourceLocation;*/

public record SetTownTrafficDisabledVehiclesPayload(String disabledModelIds, String rareModelIds)
        //? if >=1.20.5
        implements CustomPacketPayload
{

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<SetTownTrafficDisabledVehiclesPayload> TYPE =
            new CustomPacketPayload.Type<>(AiObjectMod.id("set_town_traffic_disabled_vehicles"));

    public static final StreamCodec<FriendlyByteBuf, SetTownTrafficDisabledVehiclesPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, SetTownTrafficDisabledVehiclesPayload::disabledModelIds,
            ByteBufCodecs.STRING_UTF8, SetTownTrafficDisabledVehiclesPayload::rareModelIds,
            SetTownTrafficDisabledVehiclesPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*public static final ResourceLocation ID = AiObjectMod.id("set_town_traffic_disabled_vehicles");

    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(disabledModelIds);
        buf.writeUtf(rareModelIds);
    }

    public static SetTownTrafficDisabledVehiclesPayload read(FriendlyByteBuf buf) {
        return new SetTownTrafficDisabledVehiclesPayload(buf.readUtf(), buf.readUtf());
    }
    *///?}
}
