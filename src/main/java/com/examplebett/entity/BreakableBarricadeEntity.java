package com.examplebett.entity;

import com.examplebett.sound.AiObjectModSounds;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class BreakableBarricadeEntity extends BreakableConeEntity {

    private static final float RENDER_PIVOT_HEIGHT = 0.5f;

    public BreakableBarricadeEntity(EntityType<? extends BreakableBarricadeEntity> entityType, Level level, String modelId) {
        super(entityType, level, modelId);
    }

    @Override
    public float getRenderPivotHeight() {
        return RENDER_PIVOT_HEIGHT;
    }

    @Override
    protected net.minecraft.sounds.SoundEvent breakSound() {
        return AiObjectModSounds.BARRICADE_BREAK;
    }
}
