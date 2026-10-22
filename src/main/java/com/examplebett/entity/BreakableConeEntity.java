package com.examplebett.entity;

import com.examplebett.sound.AiObjectModSounds;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class BreakableConeEntity extends BreakableTireStackEntity {

    private static final float SPIN_DEGREES_PER_TICK_MIN = 8.0f;
    private static final float SPIN_DEGREES_PER_TICK_MAX = 20.0f;

    private static final float BREAK_SOUND_VOLUME = 0.8f * 2f / 3f;

    private static final float RENDER_PIVOT_HEIGHT = 0.401f;

    private static final int DESPAWN_TICKS = 100;

    public BreakableConeEntity(EntityType<? extends BreakableConeEntity> entityType, Level level, String modelId) {
        super(entityType, level, modelId);
    }

    @Override
    public float getRenderPivotHeight() {
        return RENDER_PIVOT_HEIGHT;
    }

    protected net.minecraft.sounds.SoundEvent breakSound() {
        return AiObjectModSounds.BOX_BREAK;
    }

    @Override
    public void onLaunched(ServerLevel level, Vec3 velocity) {
        this.playBreakSound(level, this.breakSound(), BREAK_SOUND_VOLUME);
        super.onLaunched(level, velocity);
        this.startTumble(level.random, SPIN_DEGREES_PER_TICK_MIN, SPIN_DEGREES_PER_TICK_MAX);
        this.startDespawnTimer(DESPAWN_TICKS);
    }

    @Override
    //? if >=1.21.2 {
    public boolean hurtServer(ServerLevel level, DamageSource damageSource, float amount) {
        this.playBreakSound(level, this.breakSound(), BREAK_SOUND_VOLUME);
        return super.hurtServer(level, damageSource, amount);
    }
    //?} else
    /*public boolean hurt(DamageSource damageSource, float amount) {
        this.playBreakSound((ServerLevel) this.level(), this.breakSound(), BREAK_SOUND_VOLUME);
        return super.hurt(damageSource, amount);
    }*/
}
