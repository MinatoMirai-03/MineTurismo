package com.examplebett.network;

import net.minecraft.network.FriendlyByteBuf;
import com.examplebett.AiObjectMod;
//? if >=1.20.5 {
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else
/*import net.minecraft.resources.ResourceLocation;*/

public record SetClientSlipReportPayload(int entityId)
        //? if >=1.20.5
        implements CustomPacketPayload
{

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<SetClientSlipReportPayload> TYPE =
            new CustomPacketPayload.Type<>(AiObjectMod.id("set_client_slip_report"));

    public static final StreamCodec<FriendlyByteBuf, SetClientSlipReportPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SetClientSlipReportPayload::entityId,
            SetClientSlipReportPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*public static final ResourceLocation ID = AiObjectMod.id("set_client_slip_report");

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(entityId);
    }

    public static SetClientSlipReportPayload read(FriendlyByteBuf buf) {
        return new SetClientSlipReportPayload(buf.readVarInt());
    }
    *///?}
}
