package com.examplebett.network;

import net.minecraft.network.FriendlyByteBuf;
import com.examplebett.AiObjectMod;
//? if >=1.20.5 {
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else
/*import net.minecraft.resources.ResourceLocation;*/

public record SetCarPlateTexturePayload(int entityId, boolean front, String textureId)
        //? if >=1.20.5
        implements CustomPacketPayload
{

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<SetCarPlateTexturePayload> TYPE =
            new CustomPacketPayload.Type<>(AiObjectMod.id("set_car_plate_texture"));

    public static final StreamCodec<FriendlyByteBuf, SetCarPlateTexturePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SetCarPlateTexturePayload::entityId,
            ByteBufCodecs.BOOL, SetCarPlateTexturePayload::front,
            ByteBufCodecs.STRING_UTF8, SetCarPlateTexturePayload::textureId,
            SetCarPlateTexturePayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*public static final ResourceLocation ID = AiObjectMod.id("set_car_plate_texture");

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(entityId);
        buf.writeBoolean(front);
        buf.writeUtf(textureId);
    }

    public static SetCarPlateTexturePayload read(FriendlyByteBuf buf) {
        return new SetCarPlateTexturePayload(buf.readVarInt(), buf.readBoolean(), buf.readUtf());
    }
    *///?}
}
