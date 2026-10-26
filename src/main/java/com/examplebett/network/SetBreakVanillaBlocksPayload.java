package com.examplebett.network;

import net.minecraft.network.FriendlyByteBuf;
import com.examplebett.AiObjectMod;
//? if >=1.20.5 {
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else
/*import net.minecraft.resources.ResourceLocation;*/

public record SetBreakVanillaBlocksPayload(boolean enabled)
        //? if >=1.20.5
        implements CustomPacketPayload
{

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<SetBreakVanillaBlocksPayload> TYPE =
            new CustomPacketPayload.Type<>(AiObjectMod.id("set_break_vanilla_blocks"));

    public static final StreamCodec<FriendlyByteBuf, SetBreakVanillaBlocksPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, SetBreakVanillaBlocksPayload::enabled,
            SetBreakVanillaBlocksPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*public static final ResourceLocation ID = AiObjectMod.id("set_break_vanilla_blocks");

    public void write(FriendlyByteBuf buf) {
        buf.writeBoolean(enabled);
    }

    public static SetBreakVanillaBlocksPayload read(FriendlyByteBuf buf) {
        return new SetBreakVanillaBlocksPayload(buf.readBoolean());
    }
    *///?}
}
