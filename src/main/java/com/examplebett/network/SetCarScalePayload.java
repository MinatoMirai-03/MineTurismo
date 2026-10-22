package com.examplebett.network;

import net.minecraft.network.FriendlyByteBuf;
import com.examplebett.AiObjectMod;
//? if >=1.20.5 {
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else
/*import net.minecraft.resources.ResourceLocation;*/

public record SetCarScalePayload(int entityId, float carScale)
        //? if >=1.20.5
        implements CustomPacketPayload
{

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<SetCarScalePayload> TYPE =
            new CustomPacketPayload.Type<>(AiObjectMod.id("set_car_scale"));

    public static final StreamCodec<FriendlyByteBuf, SetCarScalePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SetCarScalePayload::entityId,
            ByteBufCodecs.FLOAT, SetCarScalePayload::carScale,
            SetCarScalePayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*public static final ResourceLocation ID = AiObjectMod.id("set_car_scale");

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(entityId);
        buf.writeFloat(carScale);
    }

    public static SetCarScalePayload read(FriendlyByteBuf buf) {
        return new SetCarScalePayload(buf.readVarInt(), buf.readFloat());
    }
    *///?}
}
