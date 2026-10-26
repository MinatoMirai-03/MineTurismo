package com.examplebett.network;

import net.minecraft.network.FriendlyByteBuf;
import com.examplebett.AiObjectMod;
//? if >=1.20.5 {
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else
/*import net.minecraft.resources.ResourceLocation;*/

public record SetHornStatePayload(int entityId, boolean pressed)
        //? if >=1.20.5
        implements CustomPacketPayload
{

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<SetHornStatePayload> TYPE =
            new CustomPacketPayload.Type<>(AiObjectMod.id("set_horn_state"));

    public static final StreamCodec<FriendlyByteBuf, SetHornStatePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SetHornStatePayload::entityId,
            ByteBufCodecs.BOOL, SetHornStatePayload::pressed,
            SetHornStatePayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*public static final ResourceLocation ID = AiObjectMod.id("set_horn_state");

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(entityId);
        buf.writeBoolean(pressed);
    }

    public static SetHornStatePayload read(FriendlyByteBuf buf) {
        return new SetHornStatePayload(buf.readVarInt(), buf.readBoolean());
    }
    *///?}
}
