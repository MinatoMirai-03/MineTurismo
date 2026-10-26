package com.examplebett.network;

import net.minecraft.network.FriendlyByteBuf;
import com.examplebett.AiObjectMod;
//? if >=1.20.5 {
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else
/*import net.minecraft.resources.ResourceLocation;*/

public record SetForceRotatePayload(boolean sideEnabled, boolean frontEnabled)
        //? if >=1.20.5
        implements CustomPacketPayload
{

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<SetForceRotatePayload> TYPE =
            new CustomPacketPayload.Type<>(AiObjectMod.id("set_force_rotate"));

    public static final StreamCodec<FriendlyByteBuf, SetForceRotatePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, SetForceRotatePayload::sideEnabled,
            ByteBufCodecs.BOOL, SetForceRotatePayload::frontEnabled,
            SetForceRotatePayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*public static final ResourceLocation ID = AiObjectMod.id("set_force_rotate");

    public void write(FriendlyByteBuf buf) {
        buf.writeBoolean(sideEnabled);
        buf.writeBoolean(frontEnabled);
    }

    public static SetForceRotatePayload read(FriendlyByteBuf buf) {
        return new SetForceRotatePayload(buf.readBoolean(), buf.readBoolean());
    }
    *///?}
}
