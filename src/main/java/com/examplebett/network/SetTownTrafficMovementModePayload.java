package com.examplebett.network;

import net.minecraft.network.FriendlyByteBuf;
import com.examplebett.AiObjectMod;
//? if >=1.20.5 {
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else
/*import net.minecraft.resources.ResourceLocation;*/

public record SetTownTrafficMovementModePayload(boolean useLegacySteering)
        //? if >=1.20.5
        implements CustomPacketPayload
{

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<SetTownTrafficMovementModePayload> TYPE =
            new CustomPacketPayload.Type<>(AiObjectMod.id("set_town_traffic_movement_mode"));

    public static final StreamCodec<FriendlyByteBuf, SetTownTrafficMovementModePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, SetTownTrafficMovementModePayload::useLegacySteering,
            SetTownTrafficMovementModePayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*public static final ResourceLocation ID = AiObjectMod.id("set_town_traffic_movement_mode");

    public void write(FriendlyByteBuf buf) {
        buf.writeBoolean(useLegacySteering);
    }

    public static SetTownTrafficMovementModePayload read(FriendlyByteBuf buf) {
        return new SetTownTrafficMovementModePayload(buf.readBoolean());
    }
    *///?}
}
