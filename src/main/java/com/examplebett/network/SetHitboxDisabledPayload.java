package com.examplebett.network;

import net.minecraft.network.FriendlyByteBuf;
import com.examplebett.AiObjectMod;
//? if >=1.20.5 {
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else
/*import net.minecraft.resources.ResourceLocation;*/

public record SetHitboxDisabledPayload(boolean disabled)
        //? if >=1.20.5
        implements CustomPacketPayload
{

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<SetHitboxDisabledPayload> TYPE =
            new CustomPacketPayload.Type<>(AiObjectMod.id("set_hitbox_disabled"));

    public static final StreamCodec<FriendlyByteBuf, SetHitboxDisabledPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, SetHitboxDisabledPayload::disabled,
            SetHitboxDisabledPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*public static final ResourceLocation ID = AiObjectMod.id("set_hitbox_disabled");

    public void write(FriendlyByteBuf buf) {
        buf.writeBoolean(disabled);
    }

    public static SetHitboxDisabledPayload read(FriendlyByteBuf buf) {
        return new SetHitboxDisabledPayload(buf.readBoolean());
    }
    *///?}
}
