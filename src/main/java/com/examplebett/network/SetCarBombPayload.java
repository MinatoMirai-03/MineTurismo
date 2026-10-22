package com.examplebett.network;

import net.minecraft.network.FriendlyByteBuf;
import com.examplebett.AiObjectMod;
//? if >=1.20.5 {
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else
/*import net.minecraft.resources.ResourceLocation;*/

public record SetCarBombPayload(int entityId, boolean installed)
        //? if >=1.20.5
        implements CustomPacketPayload
{

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<SetCarBombPayload> TYPE =
            new CustomPacketPayload.Type<>(AiObjectMod.id("set_car_bomb"));

    public static final StreamCodec<FriendlyByteBuf, SetCarBombPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SetCarBombPayload::entityId,
            ByteBufCodecs.BOOL, SetCarBombPayload::installed,
            SetCarBombPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*public static final ResourceLocation ID = AiObjectMod.id("set_car_bomb");

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(entityId);
        buf.writeBoolean(installed);
    }

    public static SetCarBombPayload read(FriendlyByteBuf buf) {
        return new SetCarBombPayload(buf.readVarInt(), buf.readBoolean());
    }
    *///?}
}
