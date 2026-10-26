package com.examplebett.network;

import net.minecraft.network.FriendlyByteBuf;
import com.examplebett.AiObjectMod;
//? if >=1.20.5 {
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else
/*import net.minecraft.resources.ResourceLocation;*/

public record SyncAmbientEngineVolumePayload(float multiplier, String settings)
        //? if >=1.20.5
        implements CustomPacketPayload
{

    public static SyncAmbientEngineVolumePayload of(com.examplebett.waypoint.WaypointNetworkSavedData data) {
        return new SyncAmbientEngineVolumePayload(data.getAmbientEngineVolumeMultiplier(), data.encodeClientSettings());
    }

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<SyncAmbientEngineVolumePayload> TYPE =
            new CustomPacketPayload.Type<>(AiObjectMod.id("sync_ambient_engine_volume"));

    public static final StreamCodec<FriendlyByteBuf, SyncAmbientEngineVolumePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, SyncAmbientEngineVolumePayload::multiplier,
            ByteBufCodecs.STRING_UTF8, SyncAmbientEngineVolumePayload::settings,
            SyncAmbientEngineVolumePayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*public static final ResourceLocation ID = AiObjectMod.id("sync_ambient_engine_volume");

    public void write(FriendlyByteBuf buf) {
        buf.writeFloat(multiplier);
        buf.writeUtf(settings);
    }

    public static SyncAmbientEngineVolumePayload read(FriendlyByteBuf buf) {
        return new SyncAmbientEngineVolumePayload(buf.readFloat(), buf.readUtf());
    }
    *///?}
}
