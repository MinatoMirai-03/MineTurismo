package com.examplebett.network;

import net.minecraft.network.FriendlyByteBuf;
import com.examplebett.AiObjectMod;
//? if >=1.20.5 {
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else
/*import net.minecraft.resources.ResourceLocation;*/

public record SetChaseModePayload(int entityId, boolean enabled)
        //? if >=1.20.5
        implements CustomPacketPayload
{

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<SetChaseModePayload> TYPE =
            new CustomPacketPayload.Type<>(AiObjectMod.id("set_chase_mode"));

    public static final StreamCodec<FriendlyByteBuf, SetChaseModePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SetChaseModePayload::entityId,
            ByteBufCodecs.BOOL, SetChaseModePayload::enabled,
            SetChaseModePayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*public static final ResourceLocation ID = AiObjectMod.id("set_chase_mode");

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(entityId);
        buf.writeBoolean(enabled);
    }

    public static SetChaseModePayload read(FriendlyByteBuf buf) {
        return new SetChaseModePayload(buf.readVarInt(), buf.readBoolean());
    }
    *///?}
}
