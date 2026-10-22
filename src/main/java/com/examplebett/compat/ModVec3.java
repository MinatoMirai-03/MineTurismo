package com.examplebett.compat;

import net.minecraft.world.phys.Vec3;

public final class ModVec3 {

    public static double horizontalLength(Vec3 v) {
        return Math.sqrt(v.x * v.x + v.z * v.z);
    }

    //? if >=1.15 {
    public static Vec3 position(net.minecraft.world.entity.Entity entity, float partialTick) {
        return new Vec3(entity.xo + (entity.getX() - entity.xo) * partialTick,
                entity.yo + (entity.getY() - entity.yo) * partialTick,
                entity.zo + (entity.getZ() - entity.zo) * partialTick);
    }
    //?} else {
    /*public static Vec3 position(net.minecraft.world.entity.Entity entity, float partialTick) {
        return new Vec3(entity.xo + (entity.x - entity.xo) * partialTick,
                entity.yo + (entity.y - entity.yo) * partialTick,
                entity.zo + (entity.z - entity.zo) * partialTick);
    }
    *///?}

    private ModVec3() {
    }
}
