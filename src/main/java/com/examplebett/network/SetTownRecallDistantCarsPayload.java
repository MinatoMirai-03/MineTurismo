package com.examplebett.network;

import net.minecraft.network.FriendlyByteBuf;
import com.examplebett.AiObjectMod;
//? if >=1.20.5 {
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else
/*import net.minecraft.resources.ResourceLocation;*/

public record SetTownRecallDistantCarsPayload(boolean enabled)
        //? if >=1.20.5
        implements CustomPacketPayload
{

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<SetTownRecallDistantCarsPayload> TYPE =
            new CustomPacketPayload.Type<>(AiObjectMod.id("set_town_recall_distant_cars"));

    public static final StreamCodec<FriendlyByteBuf, SetTownRecallDistantCarsPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, SetTownRecallDistantCarsPayload::enabled,
            SetTownRecallDistantCarsPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*public static final ResourceLocation ID = AiObjectMod.id("set_town_recall_distant_cars");

    public void write(FriendlyByteBuf buf) {
        buf.writeBoolean(enabled);
    }

    public static SetTownRecallDistantCarsPayload read(FriendlyByteBuf buf) {
        return new SetTownRecallDistantCarsPayload(buf.readBoolean());
    }
    *///?}
}
