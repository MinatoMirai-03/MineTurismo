package com.examplebett.network;

import net.minecraft.network.FriendlyByteBuf;
import com.examplebett.AiObjectMod;
//? if >=1.20.5 {
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else
/*import net.minecraft.resources.ResourceLocation;*/

public record SetCarTextPartPayload(int entityId, String line)
        //? if >=1.20.5
        implements CustomPacketPayload
{

    private static final int MAX_LINE_LENGTH = 512;

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<SetCarTextPartPayload> TYPE =
            new CustomPacketPayload.Type<>(AiObjectMod.id("set_car_text_part"));

    public static final StreamCodec<FriendlyByteBuf, SetCarTextPartPayload> STREAM_CODEC = StreamCodec.of(
            (buf, value) -> {
                buf.writeVarInt(value.entityId());
                buf.writeUtf(value.line(), MAX_LINE_LENGTH);
            },
            buf -> new SetCarTextPartPayload(buf.readVarInt(), buf.readUtf(MAX_LINE_LENGTH))
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*public static final ResourceLocation ID = AiObjectMod.id("set_car_text_part");

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(entityId);
        buf.writeUtf(line, MAX_LINE_LENGTH);
    }

    public static SetCarTextPartPayload read(FriendlyByteBuf buf) {
        return new SetCarTextPartPayload(buf.readVarInt(), buf.readUtf(MAX_LINE_LENGTH));
    }
    *///?}
}
