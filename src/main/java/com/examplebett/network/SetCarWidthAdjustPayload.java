package com.examplebett.network;

import net.minecraft.network.FriendlyByteBuf;
import com.examplebett.AiObjectMod;
//? if >=1.20.5 {
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else
/*import net.minecraft.resources.ResourceLocation;*/

public record SetCarWidthAdjustPayload(int entityId, float widthStretchCm, float widthZoneCm, float widthCenterOffsetCm)
        //? if >=1.20.5
        implements CustomPacketPayload
{

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<SetCarWidthAdjustPayload> TYPE =
            new CustomPacketPayload.Type<>(AiObjectMod.id("set_car_width_adjust"));

    public static final StreamCodec<FriendlyByteBuf, SetCarWidthAdjustPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SetCarWidthAdjustPayload::entityId,
            ByteBufCodecs.FLOAT, SetCarWidthAdjustPayload::widthStretchCm,
            ByteBufCodecs.FLOAT, SetCarWidthAdjustPayload::widthZoneCm,
            ByteBufCodecs.FLOAT, SetCarWidthAdjustPayload::widthCenterOffsetCm,
            SetCarWidthAdjustPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*public static final ResourceLocation ID = AiObjectMod.id("set_car_width_adjust");

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(entityId);
        buf.writeFloat(widthStretchCm);
        buf.writeFloat(widthZoneCm);
        buf.writeFloat(widthCenterOffsetCm);
    }

    public static SetCarWidthAdjustPayload read(FriendlyByteBuf buf) {
        return new SetCarWidthAdjustPayload(buf.readVarInt(), buf.readFloat(), buf.readFloat(), buf.readFloat());
    }
    *///?}
}
