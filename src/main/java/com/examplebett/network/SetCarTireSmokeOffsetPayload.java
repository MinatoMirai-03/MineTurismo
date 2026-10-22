package com.examplebett.network;

import net.minecraft.network.FriendlyByteBuf;
import com.examplebett.AiObjectMod;
//? if >=1.20.5 {
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else
/*import net.minecraft.resources.ResourceLocation;*/

public record SetCarTireSmokeOffsetPayload(int entityId, float x, float y, float z)
        //? if >=1.20.5
        implements CustomPacketPayload
{

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<SetCarTireSmokeOffsetPayload> TYPE =
            new CustomPacketPayload.Type<>(AiObjectMod.id("set_car_tire_smoke_offset"));

    public static final StreamCodec<FriendlyByteBuf, SetCarTireSmokeOffsetPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SetCarTireSmokeOffsetPayload::entityId,
            ByteBufCodecs.FLOAT, SetCarTireSmokeOffsetPayload::x,
            ByteBufCodecs.FLOAT, SetCarTireSmokeOffsetPayload::y,
            ByteBufCodecs.FLOAT, SetCarTireSmokeOffsetPayload::z,
            SetCarTireSmokeOffsetPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*public static final ResourceLocation ID = AiObjectMod.id("set_car_tire_smoke_offset");

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(entityId);
        buf.writeFloat(x);
        buf.writeFloat(y);
        buf.writeFloat(z);
    }

    public static SetCarTireSmokeOffsetPayload read(FriendlyByteBuf buf) {
        return new SetCarTireSmokeOffsetPayload(buf.readVarInt(), buf.readFloat(), buf.readFloat(), buf.readFloat());
    }
    *///?}
}
