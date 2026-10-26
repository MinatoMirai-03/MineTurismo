package com.examplebett.network;

import net.minecraft.network.FriendlyByteBuf;
import com.examplebett.AiObjectMod;
//? if >=1.20.5 {
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else
/*import net.minecraft.resources.ResourceLocation;*/

public record SetMobDamagePayload(boolean enabled)
        //? if >=1.20.5
        implements CustomPacketPayload
{

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<SetMobDamagePayload> TYPE =
            new CustomPacketPayload.Type<>(AiObjectMod.id("set_mob_damage"));

    public static final StreamCodec<FriendlyByteBuf, SetMobDamagePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, SetMobDamagePayload::enabled,
            SetMobDamagePayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*public static final ResourceLocation ID = AiObjectMod.id("set_mob_damage");

    public void write(FriendlyByteBuf buf) {
        buf.writeBoolean(enabled);
    }

    public static SetMobDamagePayload read(FriendlyByteBuf buf) {
        return new SetMobDamagePayload(buf.readBoolean());
    }
    *///?}
}
