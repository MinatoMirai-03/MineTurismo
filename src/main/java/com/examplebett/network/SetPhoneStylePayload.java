package com.examplebett.network;

import net.minecraft.network.FriendlyByteBuf;
import com.examplebett.AiObjectMod;
//? if >=1.20.5 {
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else
/*import net.minecraft.resources.ResourceLocation;*/

public record SetPhoneStylePayload(String model, String wallpaper)
        //? if >=1.20.5
        implements CustomPacketPayload
{

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<SetPhoneStylePayload> TYPE =
            new CustomPacketPayload.Type<>(AiObjectMod.id("set_phone_style"));

    public static final StreamCodec<FriendlyByteBuf, SetPhoneStylePayload> STREAM_CODEC = StreamCodec.of(
            (buf, value) -> {
                buf.writeUtf(value.model());
                buf.writeUtf(value.wallpaper());
            },
            buf -> new SetPhoneStylePayload(buf.readUtf(64), buf.readUtf(64))
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*public static final ResourceLocation ID = AiObjectMod.id("set_phone_style");

    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(model);
        buf.writeUtf(wallpaper);
    }

    public static SetPhoneStylePayload read(FriendlyByteBuf buf) {
        return new SetPhoneStylePayload(buf.readUtf(64), buf.readUtf(64));
    }
    *///?}
}
