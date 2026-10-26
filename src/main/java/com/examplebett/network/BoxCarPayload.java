package com.examplebett.network;

import com.examplebett.AiObjectMod;
import net.minecraft.network.FriendlyByteBuf;
//? if >=1.20.5 {
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else
/*import net.minecraft.resources.ResourceLocation;*/

public record BoxCarPayload(int entityId, String itemName)
        //? if >=1.20.5
        implements CustomPacketPayload
{

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<BoxCarPayload> TYPE =
            new CustomPacketPayload.Type<>(AiObjectMod.id("box_car"));

    public static final StreamCodec<FriendlyByteBuf, BoxCarPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, BoxCarPayload::entityId,
            ByteBufCodecs.STRING_UTF8, BoxCarPayload::itemName,
            BoxCarPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*public static final ResourceLocation ID = AiObjectMod.id("box_car");

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(entityId);
        buf.writeUtf(itemName);
    }

    public static BoxCarPayload read(FriendlyByteBuf buf) {
        return new BoxCarPayload(buf.readVarInt(), buf.readUtf());
    }
    *///?}
}
