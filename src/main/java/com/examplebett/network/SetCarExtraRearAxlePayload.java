package com.examplebett.network;

import net.minecraft.network.FriendlyByteBuf;
import com.examplebett.AiObjectMod;
//? if >=1.20.5 {
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else
/*import net.minecraft.resources.ResourceLocation;*/

public record SetCarExtraRearAxlePayload(int entityId, int axle, boolean enabled, float zOffset)
        //? if >=1.20.5
        implements CustomPacketPayload
{

    public SetCarExtraRearAxlePayload(int entityId, boolean enabled, float zOffset) {
        this(entityId, 1, enabled, zOffset);
    }

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<SetCarExtraRearAxlePayload> TYPE =
            new CustomPacketPayload.Type<>(AiObjectMod.id("set_car_extra_rear_axle"));

    public static final StreamCodec<FriendlyByteBuf, SetCarExtraRearAxlePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SetCarExtraRearAxlePayload::entityId,
            ByteBufCodecs.VAR_INT, SetCarExtraRearAxlePayload::axle,
            ByteBufCodecs.BOOL, SetCarExtraRearAxlePayload::enabled,
            ByteBufCodecs.FLOAT, SetCarExtraRearAxlePayload::zOffset,
            SetCarExtraRearAxlePayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*public static final ResourceLocation ID = AiObjectMod.id("set_car_extra_rear_axle");

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(entityId);
        buf.writeVarInt(axle);
        buf.writeBoolean(enabled);
        buf.writeFloat(zOffset);
    }

    public static SetCarExtraRearAxlePayload read(FriendlyByteBuf buf) {
        return new SetCarExtraRearAxlePayload(buf.readVarInt(), buf.readVarInt(), buf.readBoolean(), buf.readFloat());
    }
    *///?}
}
