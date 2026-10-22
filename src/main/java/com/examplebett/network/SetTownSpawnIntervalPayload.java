package com.examplebett.network;

import net.minecraft.network.FriendlyByteBuf;
import com.examplebett.AiObjectMod;
//? if >=1.20.5 {
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else
/*import net.minecraft.resources.ResourceLocation;*/

public record SetTownSpawnIntervalPayload(int intervalTicks)
        //? if >=1.20.5
        implements CustomPacketPayload
{

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<SetTownSpawnIntervalPayload> TYPE =
            new CustomPacketPayload.Type<>(AiObjectMod.id("set_town_spawn_interval"));

    public static final StreamCodec<FriendlyByteBuf, SetTownSpawnIntervalPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SetTownSpawnIntervalPayload::intervalTicks,
            SetTownSpawnIntervalPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*public static final ResourceLocation ID = AiObjectMod.id("set_town_spawn_interval");

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(intervalTicks);
    }

    public static SetTownSpawnIntervalPayload read(FriendlyByteBuf buf) {
        return new SetTownSpawnIntervalPayload(buf.readVarInt());
    }
    *///?}
}
