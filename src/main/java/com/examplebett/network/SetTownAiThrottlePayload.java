package com.examplebett.network;

import net.minecraft.network.FriendlyByteBuf;
import com.examplebett.AiObjectMod;
//? if >=1.20.5 {
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else
/*import net.minecraft.resources.ResourceLocation;*/

public record SetTownAiThrottlePayload(int intervalTicks)
        //? if >=1.20.5
        implements CustomPacketPayload
{

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<SetTownAiThrottlePayload> TYPE =
            new CustomPacketPayload.Type<>(AiObjectMod.id("set_town_ai_throttle"));

    public static final StreamCodec<FriendlyByteBuf, SetTownAiThrottlePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SetTownAiThrottlePayload::intervalTicks,
            SetTownAiThrottlePayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*public static final ResourceLocation ID = AiObjectMod.id("set_town_ai_throttle");

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(intervalTicks);
    }

    public static SetTownAiThrottlePayload read(FriendlyByteBuf buf) {
        return new SetTownAiThrottlePayload(buf.readVarInt());
    }
    *///?}
}
