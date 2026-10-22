package com.examplebett.network;

import net.minecraft.network.FriendlyByteBuf;
import com.examplebett.AiObjectMod;
//? if >=1.20.5 {
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else
/*import net.minecraft.resources.ResourceLocation;*/

public record SetCarWheelWidthPayload(int entityId, float wheelWidthScale)
        //? if >=1.20.5
        implements CustomPacketPayload
{

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<SetCarWheelWidthPayload> TYPE =
            new CustomPacketPayload.Type<>(AiObjectMod.id("set_car_wheel_width"));

    public static final StreamCodec<FriendlyByteBuf, SetCarWheelWidthPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SetCarWheelWidthPayload::entityId,
            ByteBufCodecs.FLOAT, SetCarWheelWidthPayload::wheelWidthScale,
            SetCarWheelWidthPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*public static final ResourceLocation ID = AiObjectMod.id("set_car_wheel_width");

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(entityId);
        buf.writeFloat(wheelWidthScale);
    }

    public static SetCarWheelWidthPayload read(FriendlyByteBuf buf) {
        return new SetCarWheelWidthPayload(buf.readVarInt(), buf.readFloat());
    }
    *///?}
}
