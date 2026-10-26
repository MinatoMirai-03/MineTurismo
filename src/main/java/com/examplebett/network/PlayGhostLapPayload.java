package com.examplebett.network;

import net.minecraft.network.FriendlyByteBuf;
import com.examplebett.AiObjectMod;
//? if >=1.20.5 {
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else
/*import net.minecraft.resources.ResourceLocation;*/

public record PlayGhostLapPayload(int entityId, boolean loop)
        //? if >=1.20.5
        implements CustomPacketPayload
{

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<PlayGhostLapPayload> TYPE =
            new CustomPacketPayload.Type<>(AiObjectMod.id("play_ghost_lap"));

    public static final StreamCodec<FriendlyByteBuf, PlayGhostLapPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, PlayGhostLapPayload::entityId,
            ByteBufCodecs.BOOL, PlayGhostLapPayload::loop,
            PlayGhostLapPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*public static final ResourceLocation ID = AiObjectMod.id("play_ghost_lap");

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(entityId);
        buf.writeBoolean(loop);
    }

    public static PlayGhostLapPayload read(FriendlyByteBuf buf) {
        return new PlayGhostLapPayload(buf.readVarInt(), buf.readBoolean());
    }
    *///?}
}
