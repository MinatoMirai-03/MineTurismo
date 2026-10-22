package com.examplebett.network;

import net.minecraft.network.FriendlyByteBuf;
import com.examplebett.AiObjectMod;
//? if >=1.20.5 {
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else
/*import net.minecraft.resources.ResourceLocation;*/

public record SetRailwayRouteVehiclePayload(int originNodeId, String modelId)
        //? if >=1.20.5
        implements CustomPacketPayload
{

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<SetRailwayRouteVehiclePayload> TYPE =
            new CustomPacketPayload.Type<>(AiObjectMod.id("set_railway_route_vehicle"));

    public static final StreamCodec<FriendlyByteBuf, SetRailwayRouteVehiclePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SetRailwayRouteVehiclePayload::originNodeId,
            ByteBufCodecs.STRING_UTF8, SetRailwayRouteVehiclePayload::modelId,
            SetRailwayRouteVehiclePayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*public static final ResourceLocation ID = AiObjectMod.id("set_railway_route_vehicle");

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(originNodeId);
        buf.writeUtf(modelId);
    }

    public static SetRailwayRouteVehiclePayload read(FriendlyByteBuf buf) {
        return new SetRailwayRouteVehiclePayload(buf.readVarInt(), buf.readUtf());
    }
    *///?}
}
