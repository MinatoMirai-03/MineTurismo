package com.examplebett.network;

import net.minecraft.network.FriendlyByteBuf;
import com.examplebett.AiObjectMod;
//? if >=1.20.5 {
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else
/*import net.minecraft.resources.ResourceLocation;*/

public record SetCarLimoAdjustPayload(int entityId, float limoStretchCm, float limoZoneCm, float limoCenterOffsetCm)
        //? if >=1.20.5
        implements CustomPacketPayload
{

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<SetCarLimoAdjustPayload> TYPE =
            new CustomPacketPayload.Type<>(AiObjectMod.id("set_car_limo_adjust"));

    public static final StreamCodec<FriendlyByteBuf, SetCarLimoAdjustPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SetCarLimoAdjustPayload::entityId,
            ByteBufCodecs.FLOAT, SetCarLimoAdjustPayload::limoStretchCm,
            ByteBufCodecs.FLOAT, SetCarLimoAdjustPayload::limoZoneCm,
            ByteBufCodecs.FLOAT, SetCarLimoAdjustPayload::limoCenterOffsetCm,
            SetCarLimoAdjustPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*public static final ResourceLocation ID = AiObjectMod.id("set_car_limo_adjust");

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(entityId);
        buf.writeFloat(limoStretchCm);
        buf.writeFloat(limoZoneCm);
        buf.writeFloat(limoCenterOffsetCm);
    }

    public static SetCarLimoAdjustPayload read(FriendlyByteBuf buf) {
        return new SetCarLimoAdjustPayload(buf.readVarInt(), buf.readFloat(), buf.readFloat(), buf.readFloat());
    }
    *///?}
}
