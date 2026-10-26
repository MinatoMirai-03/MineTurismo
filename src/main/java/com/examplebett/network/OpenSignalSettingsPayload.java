package com.examplebett.network;

import net.minecraft.network.FriendlyByteBuf;
import com.examplebett.AiObjectMod;
//? if >=1.20.5 {
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else
/*import net.minecraft.resources.ResourceLocation;*/

public record OpenSignalSettingsPayload(int signalId, String intersectionName, String direction, int greenTicks, int redTicks, int phaseOffsetTicks)
        //? if >=1.20.5
        implements CustomPacketPayload
{

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<OpenSignalSettingsPayload> TYPE =
            new CustomPacketPayload.Type<>(AiObjectMod.id("open_signal_settings"));

    public static final StreamCodec<FriendlyByteBuf, OpenSignalSettingsPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, OpenSignalSettingsPayload::signalId,
            ByteBufCodecs.STRING_UTF8, OpenSignalSettingsPayload::intersectionName,
            ByteBufCodecs.STRING_UTF8, OpenSignalSettingsPayload::direction,
            ByteBufCodecs.VAR_INT, OpenSignalSettingsPayload::greenTicks,
            ByteBufCodecs.VAR_INT, OpenSignalSettingsPayload::redTicks,
            ByteBufCodecs.VAR_INT, OpenSignalSettingsPayload::phaseOffsetTicks,
            OpenSignalSettingsPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*public static final ResourceLocation ID = AiObjectMod.id("open_signal_settings");

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(signalId);
        buf.writeUtf(intersectionName);
        buf.writeUtf(direction);
        buf.writeVarInt(greenTicks);
        buf.writeVarInt(redTicks);
        buf.writeVarInt(phaseOffsetTicks);
    }

    public static OpenSignalSettingsPayload read(FriendlyByteBuf buf) {
        return new OpenSignalSettingsPayload(buf.readVarInt(), buf.readUtf(), buf.readUtf(), buf.readVarInt(), buf.readVarInt(), buf.readVarInt());
    }
    *///?}
}
