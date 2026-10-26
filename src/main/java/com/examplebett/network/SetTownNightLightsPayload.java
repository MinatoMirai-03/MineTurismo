package com.examplebett.network;

import net.minecraft.network.FriendlyByteBuf;
import com.examplebett.AiObjectMod;
//? if >=1.20.5 {
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else
/*import net.minecraft.resources.ResourceLocation;*/

public record SetTownNightLightsPayload(boolean enabled)
        //? if >=1.20.5
        implements CustomPacketPayload
{

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<SetTownNightLightsPayload> TYPE =
            new CustomPacketPayload.Type<>(AiObjectMod.id("set_town_night_lights"));

    public static final StreamCodec<FriendlyByteBuf, SetTownNightLightsPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, SetTownNightLightsPayload::enabled,
            SetTownNightLightsPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*public static final ResourceLocation ID = AiObjectMod.id("set_town_night_lights");

    public void write(FriendlyByteBuf buf) {
        buf.writeBoolean(enabled);
    }

    public static SetTownNightLightsPayload read(FriendlyByteBuf buf) {
        return new SetTownNightLightsPayload(buf.readBoolean());
    }
    *///?}
}
