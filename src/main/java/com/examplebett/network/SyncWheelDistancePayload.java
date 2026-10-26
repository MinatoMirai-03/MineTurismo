package com.examplebett.network;

import net.minecraft.network.FriendlyByteBuf;
import com.examplebett.AiObjectMod;
//? if >=1.20.5 {
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else
/*import net.minecraft.resources.ResourceLocation;*/

public record SyncWheelDistancePayload(int entityId, float distance)
        //? if >=1.20.5
        implements CustomPacketPayload
{

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<SyncWheelDistancePayload> TYPE =
            new CustomPacketPayload.Type<>(AiObjectMod.id("sync_wheel_distance"));

    public static final StreamCodec<FriendlyByteBuf, SyncWheelDistancePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SyncWheelDistancePayload::entityId,
            ByteBufCodecs.FLOAT, SyncWheelDistancePayload::distance,
            SyncWheelDistancePayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*public static final ResourceLocation ID = AiObjectMod.id("sync_wheel_distance");

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(entityId);
        buf.writeFloat(distance);
    }

    public static SyncWheelDistancePayload read(FriendlyByteBuf buf) {
        return new SyncWheelDistancePayload(buf.readVarInt(), buf.readFloat());
    }
    *///?}
}
