package com.examplebett.network;

import net.minecraft.network.FriendlyByteBuf;
import com.examplebett.AiObjectMod;
//? if >=1.20.5 {
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else
/*import net.minecraft.resources.ResourceLocation;*/

public record SetSprintKeyHeldPayload(int entityId, boolean held)
        //? if >=1.20.5
        implements CustomPacketPayload
{

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<SetSprintKeyHeldPayload> TYPE =
            new CustomPacketPayload.Type<>(AiObjectMod.id("set_sprint_key_held"));

    public static final StreamCodec<FriendlyByteBuf, SetSprintKeyHeldPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SetSprintKeyHeldPayload::entityId,
            ByteBufCodecs.BOOL, SetSprintKeyHeldPayload::held,
            SetSprintKeyHeldPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*public static final ResourceLocation ID = AiObjectMod.id("set_sprint_key_held");

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(entityId);
        buf.writeBoolean(held);
    }

    public static SetSprintKeyHeldPayload read(FriendlyByteBuf buf) {
        return new SetSprintKeyHeldPayload(buf.readVarInt(), buf.readBoolean());
    }
    *///?}
}
