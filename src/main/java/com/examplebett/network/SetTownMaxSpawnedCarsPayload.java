package com.examplebett.network;

import net.minecraft.network.FriendlyByteBuf;
import com.examplebett.AiObjectMod;
//? if >=1.20.5 {
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else
/*import net.minecraft.resources.ResourceLocation;*/

public record SetTownMaxSpawnedCarsPayload(int maxSpawnedCars)
        //? if >=1.20.5
        implements CustomPacketPayload
{

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<SetTownMaxSpawnedCarsPayload> TYPE =
            new CustomPacketPayload.Type<>(AiObjectMod.id("set_town_max_spawned_cars"));

    public static final StreamCodec<FriendlyByteBuf, SetTownMaxSpawnedCarsPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SetTownMaxSpawnedCarsPayload::maxSpawnedCars,
            SetTownMaxSpawnedCarsPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*public static final ResourceLocation ID = AiObjectMod.id("set_town_max_spawned_cars");

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(maxSpawnedCars);
    }

    public static SetTownMaxSpawnedCarsPayload read(FriendlyByteBuf buf) {
        return new SetTownMaxSpawnedCarsPayload(buf.readVarInt());
    }
    *///?}
}
