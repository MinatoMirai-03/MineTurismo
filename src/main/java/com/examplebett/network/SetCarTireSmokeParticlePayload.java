package com.examplebett.network;

import net.minecraft.network.FriendlyByteBuf;
import com.examplebett.AiObjectMod;
//? if >=1.20.5 {
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else
/*import net.minecraft.resources.ResourceLocation;*/

public record SetCarTireSmokeParticlePayload(int entityId, String particleId)
        //? if >=1.20.5
        implements CustomPacketPayload
{

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<SetCarTireSmokeParticlePayload> TYPE =
            new CustomPacketPayload.Type<>(AiObjectMod.id("set_car_tire_smoke_particle"));

    public static final StreamCodec<FriendlyByteBuf, SetCarTireSmokeParticlePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SetCarTireSmokeParticlePayload::entityId,
            ByteBufCodecs.STRING_UTF8, SetCarTireSmokeParticlePayload::particleId,
            SetCarTireSmokeParticlePayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*public static final ResourceLocation ID = AiObjectMod.id("set_car_tire_smoke_particle");

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(entityId);
        buf.writeUtf(particleId);
    }

    public static SetCarTireSmokeParticlePayload read(FriendlyByteBuf buf) {
        return new SetCarTireSmokeParticlePayload(buf.readVarInt(), buf.readUtf());
    }
    *///?}
}
