package com.examplebett.network;

import net.minecraft.network.FriendlyByteBuf;
import com.examplebett.AiObjectMod;
//? if >=1.20.5 {
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else
/*import net.minecraft.resources.ResourceLocation;*/

public record OpenRailwayFormationEditorPayload(int originNodeId)
        //? if >=1.20.5
        implements CustomPacketPayload
{

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<OpenRailwayFormationEditorPayload> TYPE =
            new CustomPacketPayload.Type<>(AiObjectMod.id("open_railway_formation_editor"));

    public static final StreamCodec<FriendlyByteBuf, OpenRailwayFormationEditorPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, OpenRailwayFormationEditorPayload::originNodeId,
            OpenRailwayFormationEditorPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*public static final ResourceLocation ID = AiObjectMod.id("open_railway_formation_editor");

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(originNodeId);
    }

    public static OpenRailwayFormationEditorPayload read(FriendlyByteBuf buf) {
        return new OpenRailwayFormationEditorPayload(buf.readVarInt());
    }
    *///?}
}
