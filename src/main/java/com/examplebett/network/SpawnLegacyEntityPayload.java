//? if <1.17 {
/*
package com.examplebett.network;

import com.examplebett.AiObjectMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public final class SpawnLegacyEntityPayload {
    public static final ResourceLocation ID = AiObjectMod.id("spawn_legacy_entity");

    public final ResourceLocation typeId;
    public final int entityId;
    public final UUID uuid;
    public final double x;
    public final double y;
    public final double z;
    public final float yRot;

    public SpawnLegacyEntityPayload(ResourceLocation typeId, int entityId, UUID uuid, double x, double y, double z, float yRot) {
        this.typeId = typeId;
        this.entityId = entityId;
        this.uuid = uuid;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yRot = yRot;
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeResourceLocation(this.typeId);
        buf.writeVarInt(this.entityId);
        buf.writeUUID(this.uuid);
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
        buf.writeFloat(this.yRot);
    }

    public static SpawnLegacyEntityPayload read(FriendlyByteBuf buf) {
        return new SpawnLegacyEntityPayload(buf.readResourceLocation(), buf.readVarInt(), buf.readUUID(),
                buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readFloat());
    }
}
*/
//?}
