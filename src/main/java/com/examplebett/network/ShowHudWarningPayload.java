package com.examplebett.network;

import net.minecraft.network.FriendlyByteBuf;
import com.examplebett.AiObjectMod;
//? if >=1.20.5 {
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else
/*import net.minecraft.resources.ResourceLocation;*/

public record ShowHudWarningPayload(String messageKey)
        //? if >=1.20.5
        implements CustomPacketPayload
{

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<ShowHudWarningPayload> TYPE =
            new CustomPacketPayload.Type<>(AiObjectMod.id("show_hud_warning"));

    public static final StreamCodec<FriendlyByteBuf, ShowHudWarningPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, ShowHudWarningPayload::messageKey,
            ShowHudWarningPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*public static final ResourceLocation ID = AiObjectMod.id("show_hud_warning");

    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(messageKey);
    }

    public static ShowHudWarningPayload read(FriendlyByteBuf buf) {
        return new ShowHudWarningPayload(buf.readUtf());
    }
    *///?}
}
