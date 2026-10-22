package com.examplebett.network;

import net.minecraft.network.FriendlyByteBuf;
import com.examplebett.AiObjectMod;
//? if >=1.20.5 {
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else
/*import net.minecraft.resources.ResourceLocation;*/

public record SetCarPassengerPositionAdjustPayload(int entityId, float x, float y, float z)
        //? if >=1.20.5
        implements CustomPacketPayload
{

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<SetCarPassengerPositionAdjustPayload> TYPE =
            new CustomPacketPayload.Type<>(AiObjectMod.id("set_car_passenger_position_adjust"));

    public static final StreamCodec<FriendlyByteBuf, SetCarPassengerPositionAdjustPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SetCarPassengerPositionAdjustPayload::entityId,
            ByteBufCodecs.FLOAT, SetCarPassengerPositionAdjustPayload::x,
            ByteBufCodecs.FLOAT, SetCarPassengerPositionAdjustPayload::y,
            ByteBufCodecs.FLOAT, SetCarPassengerPositionAdjustPayload::z,
            SetCarPassengerPositionAdjustPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*public static final ResourceLocation ID = AiObjectMod.id("set_car_passenger_position_adjust");

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(entityId);
        buf.writeFloat(x);
        buf.writeFloat(y);
        buf.writeFloat(z);
    }

    public static SetCarPassengerPositionAdjustPayload read(FriendlyByteBuf buf) {
        return new SetCarPassengerPositionAdjustPayload(buf.readVarInt(), buf.readFloat(), buf.readFloat(), buf.readFloat());
    }
    *///?}
}
