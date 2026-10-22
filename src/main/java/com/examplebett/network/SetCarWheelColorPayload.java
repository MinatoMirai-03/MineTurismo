package com.examplebett.network;

import net.minecraft.network.FriendlyByteBuf;
import com.examplebett.AiObjectMod;
//? if >=1.20.5 {
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else
/*import net.minecraft.resources.ResourceLocation;*/

public record SetCarWheelColorPayload(int entityId, String wheelColorId)
        //? if >=1.20.5
        implements CustomPacketPayload
{

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<SetCarWheelColorPayload> TYPE =
            new CustomPacketPayload.Type<>(AiObjectMod.id("set_car_wheel_color"));

    public static final StreamCodec<FriendlyByteBuf, SetCarWheelColorPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SetCarWheelColorPayload::entityId,
            ByteBufCodecs.STRING_UTF8, SetCarWheelColorPayload::wheelColorId,
            SetCarWheelColorPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*public static final ResourceLocation ID = AiObjectMod.id("set_car_wheel_color");

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(entityId);
        buf.writeUtf(wheelColorId);
    }

    public static SetCarWheelColorPayload read(FriendlyByteBuf buf) {
        return new SetCarWheelColorPayload(buf.readVarInt(), buf.readUtf());
    }
    *///?}
}
