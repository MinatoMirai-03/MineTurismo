package com.examplebett.network;

import net.minecraft.network.FriendlyByteBuf;
import com.examplebett.AiObjectMod;
//? if >=1.20.5 {
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else
/*import net.minecraft.resources.ResourceLocation;*/

public record SetGhostRecordingPayload(int entityId, boolean recording)
        //? if >=1.20.5
        implements CustomPacketPayload
{

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<SetGhostRecordingPayload> TYPE =
            new CustomPacketPayload.Type<>(AiObjectMod.id("set_ghost_recording"));

    public static final StreamCodec<FriendlyByteBuf, SetGhostRecordingPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SetGhostRecordingPayload::entityId,
            ByteBufCodecs.BOOL, SetGhostRecordingPayload::recording,
            SetGhostRecordingPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*public static final ResourceLocation ID = AiObjectMod.id("set_ghost_recording");

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(entityId);
        buf.writeBoolean(recording);
    }

    public static SetGhostRecordingPayload read(FriendlyByteBuf buf) {
        return new SetGhostRecordingPayload(buf.readVarInt(), buf.readBoolean());
    }
    *///?}
}
