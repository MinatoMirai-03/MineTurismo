package com.examplebett.network;

import net.minecraft.network.FriendlyByteBuf;
import com.examplebett.AiObjectMod;
//? if >=1.20.5 {
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else
/*import net.minecraft.resources.ResourceLocation;*/

public record OpenMassiveAutoAppraisalPayload()
        //? if >=1.20.5
        implements CustomPacketPayload
{

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<OpenMassiveAutoAppraisalPayload> TYPE =
            new CustomPacketPayload.Type<>(AiObjectMod.id("open_massive_auto_appraisal"));

    public static final StreamCodec<FriendlyByteBuf, OpenMassiveAutoAppraisalPayload> STREAM_CODEC = StreamCodec.of(
            (buf, value) -> {
            },
            buf -> new OpenMassiveAutoAppraisalPayload()
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*public static final ResourceLocation ID = AiObjectMod.id("open_massive_auto_appraisal");

    public void write(FriendlyByteBuf buf) {
    }

    public static OpenMassiveAutoAppraisalPayload read(FriendlyByteBuf buf) {
        return new OpenMassiveAutoAppraisalPayload();
    }
    *///?}
}
