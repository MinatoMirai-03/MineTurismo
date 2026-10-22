package com.examplebett.network;

import net.minecraft.network.FriendlyByteBuf;
import com.examplebett.AiObjectMod;
//? if >=1.20.5 {
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else
/*import net.minecraft.resources.ResourceLocation;*/

public record SetSignalTimingPayload(int greenTicks, int redTicks, int phaseOffsetTicks)
        //? if >=1.20.5
        implements CustomPacketPayload
{

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<SetSignalTimingPayload> TYPE =
            new CustomPacketPayload.Type<>(AiObjectMod.id("set_signal_timing"));

    public static final StreamCodec<FriendlyByteBuf, SetSignalTimingPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SetSignalTimingPayload::greenTicks,
            ByteBufCodecs.VAR_INT, SetSignalTimingPayload::redTicks,
            ByteBufCodecs.VAR_INT, SetSignalTimingPayload::phaseOffsetTicks,
            SetSignalTimingPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*public static final ResourceLocation ID = AiObjectMod.id("set_signal_timing");

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(greenTicks);
        buf.writeVarInt(redTicks);
        buf.writeVarInt(phaseOffsetTicks);
    }

    public static SetSignalTimingPayload read(FriendlyByteBuf buf) {
        return new SetSignalTimingPayload(buf.readVarInt(), buf.readVarInt(), buf.readVarInt());
    }
    *///?}
}
