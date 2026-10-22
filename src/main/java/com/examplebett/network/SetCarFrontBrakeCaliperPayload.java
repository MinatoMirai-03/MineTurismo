package com.examplebett.network;

import net.minecraft.network.FriendlyByteBuf;
import com.examplebett.AiObjectMod;
//? if >=1.20.5 {
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else
/*import net.minecraft.resources.ResourceLocation;*/

public record SetCarFrontBrakeCaliperPayload(int entityId, String enabledIds, String partId, float x, float y, float z, float angle, float angleY, float scale)
        //? if >=1.20.5
        implements CustomPacketPayload
{

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<SetCarFrontBrakeCaliperPayload> TYPE =
            new CustomPacketPayload.Type<>(AiObjectMod.id("set_car_front_brake_caliper"));

    public static final StreamCodec<FriendlyByteBuf, SetCarFrontBrakeCaliperPayload> STREAM_CODEC = StreamCodec.of(
            (buf, value) -> {
                buf.writeVarInt(value.entityId());
                buf.writeUtf(value.enabledIds());
                buf.writeUtf(value.partId());
                buf.writeFloat(value.x());
                buf.writeFloat(value.y());
                buf.writeFloat(value.z());
                buf.writeFloat(value.angle());
                buf.writeFloat(value.angleY());
                buf.writeFloat(value.scale());
            },
            buf -> new SetCarFrontBrakeCaliperPayload(buf.readVarInt(), buf.readUtf(), buf.readUtf(), buf.readFloat(),
                    buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*public static final ResourceLocation ID = AiObjectMod.id("set_car_front_brake_caliper");

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(entityId);
        buf.writeUtf(enabledIds);
        buf.writeUtf(partId);
        buf.writeFloat(x);
        buf.writeFloat(y);
        buf.writeFloat(z);
        buf.writeFloat(angle);
        buf.writeFloat(angleY);
        buf.writeFloat(scale);
    }

    public static SetCarFrontBrakeCaliperPayload read(FriendlyByteBuf buf) {
        return new SetCarFrontBrakeCaliperPayload(buf.readVarInt(), buf.readUtf(), buf.readUtf(), buf.readFloat(),
                buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat());
    }
    *///?}
}
