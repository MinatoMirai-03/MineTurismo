package com.examplebett.network;

import net.minecraft.network.FriendlyByteBuf;
import com.examplebett.AiObjectMod;
//? if >=1.20.5 {
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else
/*import net.minecraft.resources.ResourceLocation;*/

public record SetSignalNamePayload(String intersectionName)
        //? if >=1.20.5
        implements CustomPacketPayload
{

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<SetSignalNamePayload> TYPE =
            new CustomPacketPayload.Type<>(AiObjectMod.id("set_signal_name"));

    public static final StreamCodec<FriendlyByteBuf, SetSignalNamePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, SetSignalNamePayload::intersectionName,
            SetSignalNamePayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*public static final ResourceLocation ID = AiObjectMod.id("set_signal_name");

    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(intersectionName);
    }

    public static SetSignalNamePayload read(FriendlyByteBuf buf) {
        return new SetSignalNamePayload(buf.readUtf());
    }
    *///?}
}
