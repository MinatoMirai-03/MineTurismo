package com.examplebett.network;

import net.minecraft.network.FriendlyByteBuf;
import com.examplebett.AiObjectMod;
//? if >=1.20.5 {
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else
/*import net.minecraft.resources.ResourceLocation;*/

public record SetCarItemIconPayload(int entityId, int icon)
        //? if >=1.20.5
        implements CustomPacketPayload
{

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<SetCarItemIconPayload> TYPE =
            new CustomPacketPayload.Type<>(AiObjectMod.id("set_car_item_icon"));

    public static final StreamCodec<FriendlyByteBuf, SetCarItemIconPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SetCarItemIconPayload::entityId,
            ByteBufCodecs.VAR_INT, SetCarItemIconPayload::icon,
            SetCarItemIconPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*public static final ResourceLocation ID = AiObjectMod.id("set_car_item_icon");

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(entityId);
        buf.writeVarInt(icon);
    }

    public static SetCarItemIconPayload read(FriendlyByteBuf buf) {
        return new SetCarItemIconPayload(buf.readVarInt(), buf.readVarInt());
    }
    *///?}
}
