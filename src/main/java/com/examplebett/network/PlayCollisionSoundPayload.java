package com.examplebett.network;

import net.minecraft.network.FriendlyByteBuf;
import com.examplebett.AiObjectMod;
//? if >=1.20.5 {
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else
/*import net.minecraft.resources.ResourceLocation;*/

public record PlayCollisionSoundPayload(double x, double y, double z, float volume, float pitch, boolean lowSpeed)
        //? if >=1.20.5
        implements CustomPacketPayload
{

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<PlayCollisionSoundPayload> TYPE =
            new CustomPacketPayload.Type<>(AiObjectMod.id("play_collision_sound"));

    public static final StreamCodec<FriendlyByteBuf, PlayCollisionSoundPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.DOUBLE, PlayCollisionSoundPayload::x,
            ByteBufCodecs.DOUBLE, PlayCollisionSoundPayload::y,
            ByteBufCodecs.DOUBLE, PlayCollisionSoundPayload::z,
            ByteBufCodecs.FLOAT, PlayCollisionSoundPayload::volume,
            ByteBufCodecs.FLOAT, PlayCollisionSoundPayload::pitch,
            ByteBufCodecs.BOOL, PlayCollisionSoundPayload::lowSpeed,
            PlayCollisionSoundPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*public static final ResourceLocation ID = AiObjectMod.id("play_collision_sound");

    public void write(FriendlyByteBuf buf) {
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
        buf.writeFloat(volume);
        buf.writeFloat(pitch);
        buf.writeBoolean(lowSpeed);
    }

    public static PlayCollisionSoundPayload read(FriendlyByteBuf buf) {
        return new PlayCollisionSoundPayload(buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readFloat(), buf.readFloat(), buf.readBoolean());
    }
    *///?}
}
