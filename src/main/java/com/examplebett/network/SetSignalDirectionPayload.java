package com.examplebett.network;

import net.minecraft.network.FriendlyByteBuf;
import com.examplebett.AiObjectMod;
//? if >=1.20.5 {
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else
/*import net.minecraft.resources.ResourceLocation;*/

public record SetSignalDirectionPayload(String direction)
        //? if >=1.20.5
        implements CustomPacketPayload
{

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<SetSignalDirectionPayload> TYPE =
            new CustomPacketPayload.Type<>(AiObjectMod.id("set_signal_direction"));

    public static final StreamCodec<FriendlyByteBuf, SetSignalDirectionPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, SetSignalDirectionPayload::direction,
            SetSignalDirectionPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*public static final ResourceLocation ID = AiObjectMod.id("set_signal_direction");

    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(direction);
    }

    public static SetSignalDirectionPayload read(FriendlyByteBuf buf) {
        return new SetSignalDirectionPayload(buf.readUtf());
    }
    *///?}
}
