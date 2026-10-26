package com.examplebett.network;

import net.minecraft.network.FriendlyByteBuf;
import com.examplebett.AiObjectMod;
//? if >=1.20.5 {
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else
/*import net.minecraft.resources.ResourceLocation;*/

public record SetClientWheelieTriggerPayload(int entityId)
        //? if >=1.20.5
        implements CustomPacketPayload
{

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<SetClientWheelieTriggerPayload> TYPE =
            new CustomPacketPayload.Type<>(AiObjectMod.id("set_client_wheelie_trigger"));

    public static final StreamCodec<FriendlyByteBuf, SetClientWheelieTriggerPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SetClientWheelieTriggerPayload::entityId,
            SetClientWheelieTriggerPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*public static final ResourceLocation ID = AiObjectMod.id("set_client_wheelie_trigger");

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(entityId);
    }

    public static SetClientWheelieTriggerPayload read(FriendlyByteBuf buf) {
        return new SetClientWheelieTriggerPayload(buf.readVarInt());
    }
    *///?}
}
