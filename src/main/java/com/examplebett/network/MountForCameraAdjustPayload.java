package com.examplebett.network;

import net.minecraft.network.FriendlyByteBuf;
import com.examplebett.AiObjectMod;
//? if >=1.20.5 {
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else
/*import net.minecraft.resources.ResourceLocation;*/

public record MountForCameraAdjustPayload(int entityId)
        //? if >=1.20.5
        implements CustomPacketPayload
{

    //? if >=1.20.5 {
    public static final CustomPacketPayload.Type<MountForCameraAdjustPayload> TYPE =
            new CustomPacketPayload.Type<>(AiObjectMod.id("mount_for_camera_adjust"));

    public static final StreamCodec<FriendlyByteBuf, MountForCameraAdjustPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, MountForCameraAdjustPayload::entityId,
            MountForCameraAdjustPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?} else {
    /*public static final ResourceLocation ID = AiObjectMod.id("mount_for_camera_adjust");

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(entityId);
    }

    public static MountForCameraAdjustPayload read(FriendlyByteBuf buf) {
        return new MountForCameraAdjustPayload(buf.readVarInt());
    }
    *///?}
}
