package com.examplebett.network;

import net.minecraft.network.FriendlyByteBuf;
import com.examplebett.AiObjectMod;
//? if >=1.20.5 {
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else
/*import net.minecraft.resources.ResourceLocation;*/

public record SetCarLiveryPayload(int entityId, String liveryId)
        //? if >=1.20.5
        implements CustomPacketPayload
{

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<SetCarLiveryPayload> TYPE =
            new CustomPacketPayload.Type<>(AiObjectMod.id("set_car_livery"));

    public static final StreamCodec<FriendlyByteBuf, SetCarLiveryPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SetCarLiveryPayload::entityId,
            ByteBufCodecs.STRING_UTF8, SetCarLiveryPayload::liveryId,
            SetCarLiveryPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*public static final ResourceLocation ID = AiObjectMod.id("set_car_livery");

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(entityId);
        buf.writeUtf(liveryId);
    }

    public static SetCarLiveryPayload read(FriendlyByteBuf buf) {
        return new SetCarLiveryPayload(buf.readVarInt(), buf.readUtf());
    }
    *///?}
}
