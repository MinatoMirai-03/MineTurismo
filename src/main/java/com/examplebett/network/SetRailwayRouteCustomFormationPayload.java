package com.examplebett.network;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import com.examplebett.AiObjectMod;
//? if >=1.20.5 {
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else
/*import net.minecraft.resources.ResourceLocation;*/

public record SetRailwayRouteCustomFormationPayload(int originNodeId, CompoundTag formationTag)
        //? if >=1.20.5
        implements CustomPacketPayload
{

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<SetRailwayRouteCustomFormationPayload> TYPE =
            new CustomPacketPayload.Type<>(AiObjectMod.id("set_railway_route_custom_formation"));

    public static final StreamCodec<FriendlyByteBuf, SetRailwayRouteCustomFormationPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SetRailwayRouteCustomFormationPayload::originNodeId,
            ByteBufCodecs.COMPOUND_TAG, SetRailwayRouteCustomFormationPayload::formationTag,
            SetRailwayRouteCustomFormationPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*public static final ResourceLocation ID = AiObjectMod.id("set_railway_route_custom_formation");

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(originNodeId);
        buf.writeNbt(formationTag);
    }

    public static SetRailwayRouteCustomFormationPayload read(FriendlyByteBuf buf) {
        return new SetRailwayRouteCustomFormationPayload(buf.readVarInt(), buf.readNbt());
    }
    *///?}
}
