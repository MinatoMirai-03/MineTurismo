package com.examplebett.network;

import net.minecraft.network.FriendlyByteBuf;
import com.examplebett.AiObjectMod;
//? if >=1.20.5 {
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else
/*import net.minecraft.resources.ResourceLocation;*/

public record SetCarDriveModePayload(int entityId, int driveMode)
        //? if >=1.20.5
        implements CustomPacketPayload
{

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<SetCarDriveModePayload> TYPE =
            new CustomPacketPayload.Type<>(AiObjectMod.id("set_car_drive_mode"));

    public static final StreamCodec<FriendlyByteBuf, SetCarDriveModePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SetCarDriveModePayload::entityId,
            ByteBufCodecs.VAR_INT, SetCarDriveModePayload::driveMode,
            SetCarDriveModePayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*public static final ResourceLocation ID = AiObjectMod.id("set_car_drive_mode");

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(entityId);
        buf.writeVarInt(driveMode);
    }

    public static SetCarDriveModePayload read(FriendlyByteBuf buf) {
        return new SetCarDriveModePayload(buf.readVarInt(), buf.readVarInt());
    }
    *///?}
}
