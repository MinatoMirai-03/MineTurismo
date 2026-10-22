package com.examplebett.network;

import net.minecraft.network.FriendlyByteBuf;
import com.examplebett.AiObjectMod;
//? if >=1.20.5 {
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else
/*import net.minecraft.resources.ResourceLocation;*/

public record RequestAppCarSpawnPayload(String modelId)
        //? if >=1.20.5
        implements CustomPacketPayload
{

    private static final int MAX_MODEL_ID_LENGTH = 128;

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<RequestAppCarSpawnPayload> TYPE =
            new CustomPacketPayload.Type<>(AiObjectMod.id("request_app_car_spawn"));

    public static final StreamCodec<FriendlyByteBuf, RequestAppCarSpawnPayload> STREAM_CODEC = StreamCodec.of(
            (buf, value) -> buf.writeUtf(value.modelId(), MAX_MODEL_ID_LENGTH),
            buf -> new RequestAppCarSpawnPayload(buf.readUtf(MAX_MODEL_ID_LENGTH))
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*public static final ResourceLocation ID = AiObjectMod.id("request_app_car_spawn");

    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(modelId, MAX_MODEL_ID_LENGTH);
    }

    public static RequestAppCarSpawnPayload read(FriendlyByteBuf buf) {
        return new RequestAppCarSpawnPayload(buf.readUtf(MAX_MODEL_ID_LENGTH));
    }
    *///?}
}
