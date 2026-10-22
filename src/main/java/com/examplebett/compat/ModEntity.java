package com.examplebett.compat;

import net.minecraft.world.entity.Entity;

public final class ModEntity {

    public static double getX(Entity entity) {
        //? if >=1.15 {
        return entity.getX();
        //?} else
        /*return entity.x;*/
    }

    public static double getY(Entity entity) {
        //? if >=1.15 {
        return entity.getY();
        //?} else
        /*return entity.y;*/
    }

    public static double getZ(Entity entity) {
        //? if >=1.15 {
        return entity.getZ();
        //?} else
        /*return entity.z;*/
    }

    private ModEntity() {
    }
}
