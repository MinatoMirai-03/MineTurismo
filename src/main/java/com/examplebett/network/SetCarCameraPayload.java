package com.examplebett.network;

import net.minecraft.network.FriendlyByteBuf;
import com.examplebett.AiObjectMod;
//? if >=1.20.5 {
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else
/*import net.minecraft.resources.ResourceLocation;*/

public record SetCarCameraPayload(int entityId, float distanceAdjust, float heightAdjust)
        //? if >=1.20.5
        implements CustomPacketPayload
{

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<SetCarCameraPayload> TYPE =
            new CustomPacketPayload.Type<>(AiObjectMod.id("set_car_camera"));

    public static final StreamCodec<FriendlyByteBuf, SetCarCameraPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SetCarCameraPayload::entityId,
            ByteBufCodecs.FLOAT, SetCarCameraPayload::distanceAdjust,
            ByteBufCodecs.FLOAT, SetCarCameraPayload::heightAdjust,
            SetCarCameraPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*public static final ResourceLocation ID = AiObjectMod.id("set_car_camera");

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(entityId);
        buf.writeFloat(distanceAdjust);
        buf.writeFloat(heightAdjust);
    }

    public static SetCarCameraPayload read(FriendlyByteBuf buf) {
        return new SetCarCameraPayload(buf.readVarInt(), buf.readFloat(), buf.readFloat());
    }
    *///?}
}
