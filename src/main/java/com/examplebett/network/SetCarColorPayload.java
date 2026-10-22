package com.examplebett.network;

import net.minecraft.network.FriendlyByteBuf;
import com.examplebett.AiObjectMod;
//? if >=1.20.5 {
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else
/*import net.minecraft.resources.ResourceLocation;*/

public record SetCarColorPayload(int entityId, int color)
        //? if >=1.20.5
        implements CustomPacketPayload
{

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<SetCarColorPayload> TYPE =
            new CustomPacketPayload.Type<>(AiObjectMod.id("set_car_color"));

    public static final StreamCodec<FriendlyByteBuf, SetCarColorPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SetCarColorPayload::entityId,
            //? if >=1.21.6 {
            ByteBufCodecs.RGB_COLOR, SetCarColorPayload::color,
            //?} else
            /*ByteBufCodecs.INT, SetCarColorPayload::color,*/
            SetCarColorPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*public static final ResourceLocation ID = AiObjectMod.id("set_car_color");

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(entityId);
        buf.writeInt(color);
    }

    public static SetCarColorPayload read(FriendlyByteBuf buf) {
        return new SetCarColorPayload(buf.readVarInt(), buf.readInt());
    }
    *///?}
}
