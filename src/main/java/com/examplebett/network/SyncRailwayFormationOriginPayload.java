package com.examplebett.network;

import net.minecraft.network.FriendlyByteBuf;
import com.examplebett.AiObjectMod;
//? if >=1.20.5 {
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else
/*import net.minecraft.resources.ResourceLocation;*/

public record SyncRailwayFormationOriginPayload(int originNodeId)
        //? if >=1.20.5
        implements CustomPacketPayload
{

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<SyncRailwayFormationOriginPayload> TYPE =
            new CustomPacketPayload.Type<>(AiObjectMod.id("sync_railway_formation_origin"));

    public static final StreamCodec<FriendlyByteBuf, SyncRailwayFormationOriginPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SyncRailwayFormationOriginPayload::originNodeId,
            SyncRailwayFormationOriginPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*public static final ResourceLocation ID = AiObjectMod.id("sync_railway_formation_origin");

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(originNodeId);
    }

    public static SyncRailwayFormationOriginPayload read(FriendlyByteBuf buf) {
        return new SyncRailwayFormationOriginPayload(buf.readVarInt());
    }
    *///?}
}
