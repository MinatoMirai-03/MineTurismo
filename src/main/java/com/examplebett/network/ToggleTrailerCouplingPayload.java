package com.examplebett.network;

import net.minecraft.network.FriendlyByteBuf;
import com.examplebett.AiObjectMod;
//? if >=1.20.5 {
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else
/*import net.minecraft.resources.ResourceLocation;*/

public record ToggleTrailerCouplingPayload(int entityId)
        //? if >=1.20.5
        implements CustomPacketPayload
{

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<ToggleTrailerCouplingPayload> TYPE =
            new CustomPacketPayload.Type<>(AiObjectMod.id("toggle_trailer_coupling"));

    public static final StreamCodec<FriendlyByteBuf, ToggleTrailerCouplingPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ToggleTrailerCouplingPayload::entityId,
            ToggleTrailerCouplingPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*public static final ResourceLocation ID = AiObjectMod.id("toggle_trailer_coupling");

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(entityId);
    }

    public static ToggleTrailerCouplingPayload read(FriendlyByteBuf buf) {
        return new ToggleTrailerCouplingPayload(buf.readVarInt());
    }
    *///?}
}
