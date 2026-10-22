package com.examplebett.network;

import net.minecraft.network.FriendlyByteBuf;
import com.examplebett.AiObjectMod;
//? if >=1.20.5 {
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else
/*import net.minecraft.resources.ResourceLocation;*/

public record SetCarBrandLogoPayload(int entityId, String line)
        //? if >=1.20.5
        implements CustomPacketPayload
{

    private static final int MAX_LINE_LENGTH = 320;

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<SetCarBrandLogoPayload> TYPE =
            new CustomPacketPayload.Type<>(AiObjectMod.id("set_car_brand_logo"));

    public static final StreamCodec<FriendlyByteBuf, SetCarBrandLogoPayload> STREAM_CODEC = StreamCodec.of(
            (buf, value) -> {
                buf.writeVarInt(value.entityId());
                buf.writeUtf(value.line(), MAX_LINE_LENGTH);
            },
            buf -> new SetCarBrandLogoPayload(buf.readVarInt(), buf.readUtf(MAX_LINE_LENGTH))
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*public static final ResourceLocation ID = AiObjectMod.id("set_car_brand_logo");

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(entityId);
        buf.writeUtf(line, MAX_LINE_LENGTH);
    }

    public static SetCarBrandLogoPayload read(FriendlyByteBuf buf) {
        return new SetCarBrandLogoPayload(buf.readVarInt(), buf.readUtf(MAX_LINE_LENGTH));
    }
    *///?}
}
