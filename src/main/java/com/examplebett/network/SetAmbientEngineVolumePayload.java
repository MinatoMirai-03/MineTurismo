package com.examplebett.network;

import net.minecraft.network.FriendlyByteBuf;
import com.examplebett.AiObjectMod;
//? if >=1.20.5 {
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else
/*import net.minecraft.resources.ResourceLocation;*/

public record SetAmbientEngineVolumePayload(float multiplier)
        //? if >=1.20.5
        implements CustomPacketPayload
{

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<SetAmbientEngineVolumePayload> TYPE =
            new CustomPacketPayload.Type<>(AiObjectMod.id("set_ambient_engine_volume"));

    public static final StreamCodec<FriendlyByteBuf, SetAmbientEngineVolumePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, SetAmbientEngineVolumePayload::multiplier,
            SetAmbientEngineVolumePayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*public static final ResourceLocation ID = AiObjectMod.id("set_ambient_engine_volume");

    public void write(FriendlyByteBuf buf) {
        buf.writeFloat(multiplier);
    }

    public static SetAmbientEngineVolumePayload read(FriendlyByteBuf buf) {
        return new SetAmbientEngineVolumePayload(buf.readFloat());
    }
    *///?}
}
