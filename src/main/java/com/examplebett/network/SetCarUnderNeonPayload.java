package com.examplebett.network;

import net.minecraft.network.FriendlyByteBuf;
import com.examplebett.AiObjectMod;
//? if >=1.20.5 {
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else
/*import net.minecraft.resources.ResourceLocation;*/

public record SetCarUnderNeonPayload(int entityId, String line)
        //? if >=1.20.5
        implements CustomPacketPayload
{

    private static final int MAX_LINE_LENGTH = 256;

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<SetCarUnderNeonPayload> TYPE =
            new CustomPacketPayload.Type<>(AiObjectMod.id("set_car_under_neon"));

    public static final StreamCodec<FriendlyByteBuf, SetCarUnderNeonPayload> STREAM_CODEC = StreamCodec.of(
            (buf, value) -> {
                buf.writeVarInt(value.entityId());
                buf.writeUtf(value.line(), MAX_LINE_LENGTH);
            },
            buf -> new SetCarUnderNeonPayload(buf.readVarInt(), buf.readUtf(MAX_LINE_LENGTH))
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*public static final ResourceLocation ID = AiObjectMod.id("set_car_under_neon");

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(entityId);
        buf.writeUtf(line, MAX_LINE_LENGTH);
    }

    public static SetCarUnderNeonPayload read(FriendlyByteBuf buf) {
        return new SetCarUnderNeonPayload(buf.readVarInt(), buf.readUtf(MAX_LINE_LENGTH));
    }
    *///?}
}
