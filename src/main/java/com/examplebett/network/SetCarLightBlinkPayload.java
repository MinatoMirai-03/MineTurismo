package com.examplebett.network;

import net.minecraft.network.FriendlyByteBuf;
import com.examplebett.AiObjectMod;
//? if >=1.20.5 {
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else
/*import net.minecraft.resources.ResourceLocation;*/

public record SetCarLightBlinkPayload(int entityId, boolean blinking)
        //? if >=1.20.5
        implements CustomPacketPayload
{

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<SetCarLightBlinkPayload> TYPE =
            new CustomPacketPayload.Type<>(AiObjectMod.id("set_car_light_blink"));

    public static final StreamCodec<FriendlyByteBuf, SetCarLightBlinkPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SetCarLightBlinkPayload::entityId,
            ByteBufCodecs.BOOL, SetCarLightBlinkPayload::blinking,
            SetCarLightBlinkPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*public static final ResourceLocation ID = AiObjectMod.id("set_car_light_blink");

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(entityId);
        buf.writeBoolean(blinking);
    }

    public static SetCarLightBlinkPayload read(FriendlyByteBuf buf) {
        return new SetCarLightBlinkPayload(buf.readVarInt(), buf.readBoolean());
    }
    *///?}
}
