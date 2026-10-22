package com.examplebett.network;

import net.minecraft.network.FriendlyByteBuf;
import com.examplebett.AiObjectMod;
//? if >=1.20.5 {
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else
/*import net.minecraft.resources.ResourceLocation;*/

public record SetCarLipAdjustPayload(int entityId, float lipStretchAdjust, float lipWidthAdjust, float lipBandHeightCm, float lipFrontExtendAdjust)
        //? if >=1.20.5
        implements CustomPacketPayload
{

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<SetCarLipAdjustPayload> TYPE =
            new CustomPacketPayload.Type<>(AiObjectMod.id("set_car_lip_adjust"));

    public static final StreamCodec<FriendlyByteBuf, SetCarLipAdjustPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SetCarLipAdjustPayload::entityId,
            ByteBufCodecs.FLOAT, SetCarLipAdjustPayload::lipStretchAdjust,
            ByteBufCodecs.FLOAT, SetCarLipAdjustPayload::lipWidthAdjust,
            ByteBufCodecs.FLOAT, SetCarLipAdjustPayload::lipBandHeightCm,
            ByteBufCodecs.FLOAT, SetCarLipAdjustPayload::lipFrontExtendAdjust,
            SetCarLipAdjustPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*public static final ResourceLocation ID = AiObjectMod.id("set_car_lip_adjust");

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(entityId);
        buf.writeFloat(lipStretchAdjust);
        buf.writeFloat(lipWidthAdjust);
        buf.writeFloat(lipBandHeightCm);
        buf.writeFloat(lipFrontExtendAdjust);
    }

    public static SetCarLipAdjustPayload read(FriendlyByteBuf buf) {
        return new SetCarLipAdjustPayload(buf.readVarInt(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat());
    }
    *///?}
}
