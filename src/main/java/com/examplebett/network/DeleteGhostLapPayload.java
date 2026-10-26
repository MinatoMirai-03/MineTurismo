package com.examplebett.network;

import net.minecraft.network.FriendlyByteBuf;
import com.examplebett.AiObjectMod;
//? if >=1.20.5 {
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else
/*import net.minecraft.resources.ResourceLocation;*/

public record DeleteGhostLapPayload(int entityId)
        //? if >=1.20.5
        implements CustomPacketPayload
{

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<DeleteGhostLapPayload> TYPE =
            new CustomPacketPayload.Type<>(AiObjectMod.id("delete_ghost_lap"));

    public static final StreamCodec<FriendlyByteBuf, DeleteGhostLapPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, DeleteGhostLapPayload::entityId,
            DeleteGhostLapPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*public static final ResourceLocation ID = AiObjectMod.id("delete_ghost_lap");

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(entityId);
    }

    public static DeleteGhostLapPayload read(FriendlyByteBuf buf) {
        return new DeleteGhostLapPayload(buf.readVarInt());
    }
    *///?}
}
