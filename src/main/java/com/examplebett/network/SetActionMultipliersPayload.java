package com.examplebett.network;

import net.minecraft.network.FriendlyByteBuf;
import com.examplebett.AiObjectMod;
//? if >=1.20.5 {
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else
/*import net.minecraft.resources.ResourceLocation;*/

public record SetActionMultipliersPayload(float heightMultiplier, float rotationMultiplier, float pushDistanceMultiplier,
        float suspensionIntensityMultiplier)
        //? if >=1.20.5
        implements CustomPacketPayload
{

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<SetActionMultipliersPayload> TYPE =
            new CustomPacketPayload.Type<>(AiObjectMod.id("set_action_multipliers"));

    public static final StreamCodec<FriendlyByteBuf, SetActionMultipliersPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, SetActionMultipliersPayload::heightMultiplier,
            ByteBufCodecs.FLOAT, SetActionMultipliersPayload::rotationMultiplier,
            ByteBufCodecs.FLOAT, SetActionMultipliersPayload::pushDistanceMultiplier,
            ByteBufCodecs.FLOAT, SetActionMultipliersPayload::suspensionIntensityMultiplier,
            SetActionMultipliersPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*public static final ResourceLocation ID = AiObjectMod.id("set_action_multipliers");

    public void write(FriendlyByteBuf buf) {
        buf.writeFloat(heightMultiplier);
        buf.writeFloat(rotationMultiplier);
        buf.writeFloat(pushDistanceMultiplier);
        buf.writeFloat(suspensionIntensityMultiplier);
    }

    public static SetActionMultipliersPayload read(FriendlyByteBuf buf) {
        return new SetActionMultipliersPayload(buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat());
    }
    *///?}
}
