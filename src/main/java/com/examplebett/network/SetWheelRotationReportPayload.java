package com.examplebett.network;

import net.minecraft.network.FriendlyByteBuf;
import com.examplebett.AiObjectMod;
//? if >=1.20.5 {
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else
/*import net.minecraft.resources.ResourceLocation;*/

public record SetWheelRotationReportPayload(int entityId, float distance)
        //? if >=1.20.5
        implements CustomPacketPayload
{

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<SetWheelRotationReportPayload> TYPE =
            new CustomPacketPayload.Type<>(AiObjectMod.id("set_wheel_rotation_report"));

    public static final StreamCodec<FriendlyByteBuf, SetWheelRotationReportPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SetWheelRotationReportPayload::entityId,
            ByteBufCodecs.FLOAT, SetWheelRotationReportPayload::distance,
            SetWheelRotationReportPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*public static final ResourceLocation ID = AiObjectMod.id("set_wheel_rotation_report");

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(entityId);
        buf.writeFloat(distance);
    }

    public static SetWheelRotationReportPayload read(FriendlyByteBuf buf) {
        return new SetWheelRotationReportPayload(buf.readVarInt(), buf.readFloat());
    }
    *///?}
}
