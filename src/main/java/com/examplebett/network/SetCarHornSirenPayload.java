package com.examplebett.network;

import net.minecraft.network.FriendlyByteBuf;
import com.examplebett.AiObjectMod;
//? if >=1.20.5 {
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else
/*import net.minecraft.resources.ResourceLocation;*/

public record SetCarHornSirenPayload(int entityId, String hornSoundId, String sirenSoundId)
        //? if >=1.20.5
        implements CustomPacketPayload
{

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<SetCarHornSirenPayload> TYPE =
            new CustomPacketPayload.Type<>(AiObjectMod.id("set_car_horn_siren"));

    public static final StreamCodec<FriendlyByteBuf, SetCarHornSirenPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SetCarHornSirenPayload::entityId,
            ByteBufCodecs.STRING_UTF8, SetCarHornSirenPayload::hornSoundId,
            ByteBufCodecs.STRING_UTF8, SetCarHornSirenPayload::sirenSoundId,
            SetCarHornSirenPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*public static final ResourceLocation ID = AiObjectMod.id("set_car_horn_siren");

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(entityId);
        buf.writeUtf(hornSoundId);
        buf.writeUtf(sirenSoundId);
    }

    public static SetCarHornSirenPayload read(FriendlyByteBuf buf) {
        return new SetCarHornSirenPayload(buf.readVarInt(), buf.readUtf(), buf.readUtf());
    }
    *///?}
}
