package com.examplebett.network;

import net.minecraft.network.FriendlyByteBuf;
import com.examplebett.AiObjectMod;
//? if >=1.20.5 {
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else
/*import net.minecraft.resources.ResourceLocation;*/

public record SetCarPlateTransformPayload(int entityId, boolean front, float x, float y, float z, float scale, float yaw, float pitch, float roll)
        //? if >=1.20.5
        implements CustomPacketPayload
{

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<SetCarPlateTransformPayload> TYPE =
            new CustomPacketPayload.Type<>(AiObjectMod.id("set_car_plate_transform"));

    //? if >=1.21.5 {
    public static final StreamCodec<FriendlyByteBuf, SetCarPlateTransformPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SetCarPlateTransformPayload::entityId,
            ByteBufCodecs.BOOL, SetCarPlateTransformPayload::front,
            ByteBufCodecs.FLOAT, SetCarPlateTransformPayload::x,
            ByteBufCodecs.FLOAT, SetCarPlateTransformPayload::y,
            ByteBufCodecs.FLOAT, SetCarPlateTransformPayload::z,
            ByteBufCodecs.FLOAT, SetCarPlateTransformPayload::scale,
            ByteBufCodecs.FLOAT, SetCarPlateTransformPayload::yaw,
            ByteBufCodecs.FLOAT, SetCarPlateTransformPayload::pitch,
            ByteBufCodecs.FLOAT, SetCarPlateTransformPayload::roll,
            SetCarPlateTransformPayload::new
    );
    //?} else
    /*public static final StreamCodec<FriendlyByteBuf, SetCarPlateTransformPayload> STREAM_CODEC = StreamCodec.of(
            (buf, value) -> {
                buf.writeVarInt(value.entityId());
                buf.writeBoolean(value.front());
                buf.writeFloat(value.x());
                buf.writeFloat(value.y());
                buf.writeFloat(value.z());
                buf.writeFloat(value.scale());
                buf.writeFloat(value.yaw());
                buf.writeFloat(value.pitch());
                buf.writeFloat(value.roll());
            },
            buf -> new SetCarPlateTransformPayload(buf.readVarInt(), buf.readBoolean(), buf.readFloat(), buf.readFloat(),
                    buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat())
    );*/

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*public static final ResourceLocation ID = AiObjectMod.id("set_car_plate_transform");

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(entityId);
        buf.writeBoolean(front);
        buf.writeFloat(x);
        buf.writeFloat(y);
        buf.writeFloat(z);
        buf.writeFloat(scale);
        buf.writeFloat(yaw);
        buf.writeFloat(pitch);
        buf.writeFloat(roll);
    }

    public static SetCarPlateTransformPayload read(FriendlyByteBuf buf) {
        return new SetCarPlateTransformPayload(buf.readVarInt(), buf.readBoolean(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat());
    }
    *///?}
}
