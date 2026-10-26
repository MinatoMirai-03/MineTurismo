package com.examplebett.network;

import net.minecraft.network.FriendlyByteBuf;
import com.examplebett.AiObjectMod;
//? if >=1.20.5 {
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else
/*import net.minecraft.resources.ResourceLocation;*/

public record GttScorePayload(int score)
        //? if >=1.20.5
        implements CustomPacketPayload
{

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<GttScorePayload> TYPE =
            new CustomPacketPayload.Type<>(AiObjectMod.id("gtt_score"));

    public static final StreamCodec<FriendlyByteBuf, GttScorePayload> STREAM_CODEC = StreamCodec.of(
            (buf, value) -> buf.writeVarInt(value.score()),
            buf -> new GttScorePayload(buf.readVarInt())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*public static final ResourceLocation ID = AiObjectMod.id("gtt_score");

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(score);
    }

    public static GttScorePayload read(FriendlyByteBuf buf) {
        return new GttScorePayload(buf.readVarInt());
    }
    *///?}
}
