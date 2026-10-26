package com.examplebett.network;

import net.minecraft.network.FriendlyByteBuf;
import com.examplebett.AiObjectMod;
//? if >=1.20.5 {
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else
/*import net.minecraft.resources.ResourceLocation;*/

public record SetCarWheelAdjustPayload(int entityId, float frontZOffset, float rearZOffset, float trackHalfWidthOffset, float axleHeightOffset, float bodyHeightOffset, float camberDegrees, float wheelScale, String wheelModelIdOverride)
        //? if >=1.20.5
        implements CustomPacketPayload
{

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<SetCarWheelAdjustPayload> TYPE =
            new CustomPacketPayload.Type<>(AiObjectMod.id("set_car_wheel_adjust"));

    //? if >=1.21.5 {
    public static final StreamCodec<FriendlyByteBuf, SetCarWheelAdjustPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SetCarWheelAdjustPayload::entityId,
            ByteBufCodecs.FLOAT, SetCarWheelAdjustPayload::frontZOffset,
            ByteBufCodecs.FLOAT, SetCarWheelAdjustPayload::rearZOffset,
            ByteBufCodecs.FLOAT, SetCarWheelAdjustPayload::trackHalfWidthOffset,
            ByteBufCodecs.FLOAT, SetCarWheelAdjustPayload::axleHeightOffset,
            ByteBufCodecs.FLOAT, SetCarWheelAdjustPayload::bodyHeightOffset,
            ByteBufCodecs.FLOAT, SetCarWheelAdjustPayload::camberDegrees,
            ByteBufCodecs.FLOAT, SetCarWheelAdjustPayload::wheelScale,
            ByteBufCodecs.STRING_UTF8, SetCarWheelAdjustPayload::wheelModelIdOverride,
            SetCarWheelAdjustPayload::new
    );
    //?} else
    /*public static final StreamCodec<FriendlyByteBuf, SetCarWheelAdjustPayload> STREAM_CODEC = StreamCodec.of(
            (buf, value) -> {
                buf.writeVarInt(value.entityId());
                buf.writeFloat(value.frontZOffset());
                buf.writeFloat(value.rearZOffset());
                buf.writeFloat(value.trackHalfWidthOffset());
                buf.writeFloat(value.axleHeightOffset());
                buf.writeFloat(value.bodyHeightOffset());
                buf.writeFloat(value.camberDegrees());
                buf.writeFloat(value.wheelScale());
                buf.writeUtf(value.wheelModelIdOverride());
            },
            buf -> new SetCarWheelAdjustPayload(buf.readVarInt(), buf.readFloat(), buf.readFloat(),
                    buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readUtf())
    );*/

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*public static final ResourceLocation ID = AiObjectMod.id("set_car_wheel_adjust");

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(entityId);
        buf.writeFloat(frontZOffset);
        buf.writeFloat(rearZOffset);
        buf.writeFloat(trackHalfWidthOffset);
        buf.writeFloat(axleHeightOffset);
        buf.writeFloat(bodyHeightOffset);
        buf.writeFloat(camberDegrees);
        buf.writeFloat(wheelScale);
        buf.writeUtf(wheelModelIdOverride);
    }

    public static SetCarWheelAdjustPayload read(FriendlyByteBuf buf) {
        return new SetCarWheelAdjustPayload(buf.readVarInt(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readUtf());
    }
    *///?}
}
