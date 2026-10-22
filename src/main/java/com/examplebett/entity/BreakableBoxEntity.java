package com.examplebett.entity;

import com.examplebett.sound.AiObjectModSounds;
import net.minecraft.server.level.ServerLevel;
//? if >=1.19 {
import net.minecraft.util.RandomSource;
//?}
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class BreakableBoxEntity extends BreakableTireStackEntity {

    private static final double SCATTER_ANGLE_DEGREES = 55.0;
    private static final double SCATTER_SPEED_MIN = 0.35;
    private static final double SCATTER_SPEED_MAX = 0.65;

    private static final float SPIN_DEGREES_PER_TICK_MIN = 8.0f;
    private static final float SPIN_DEGREES_PER_TICK_MAX = 20.0f;

    private static final float BREAK_SOUND_VOLUME = 0.8f * 2f / 3f;

    private static final float RENDER_PIVOT_HEIGHT = 0.372f;

    private static final int DESPAWN_TICKS = 100;

    public BreakableBoxEntity(EntityType<? extends BreakableBoxEntity> entityType, Level level, String modelId) {
        super(entityType, level, modelId);
        this.setNoGravity(true);
    }

    @Override
    public float getRenderPivotHeight() {
        return RENDER_PIVOT_HEIGHT;
    }

    @Override
    public void onLaunched(ServerLevel level, Vec3 velocity) {
        this.playBreakSound(level, AiObjectModSounds.BOX_BREAK, BREAK_SOUND_VOLUME);
        this.setNoGravity(false);
        //? if >=1.19 {
        RandomSource random = level.random;
        //?} else
        /*java.util.Random random = level.random;*/
        double angleRad = Math.toRadians((random.nextDouble() * 2.0 - 1.0) * SCATTER_ANGLE_DEGREES);
        double speedScale = SCATTER_SPEED_MIN + random.nextDouble() * (SCATTER_SPEED_MAX - SCATTER_SPEED_MIN);
        double cos = Math.cos(angleRad);
        double sin = Math.sin(angleRad);
        double scatteredX = (velocity.x * cos - velocity.z * sin) * speedScale;
        double scatteredZ = (velocity.x * sin + velocity.z * cos) * speedScale;
        super.onLaunched(level, new Vec3(scatteredX, velocity.y * speedScale, scatteredZ));

        this.startTumble(random, SPIN_DEGREES_PER_TICK_MIN, SPIN_DEGREES_PER_TICK_MAX);
        this.startDespawnTimer(DESPAWN_TICKS);
    }

    @Override
    //? if >=1.21.2 {
    public boolean hurtServer(ServerLevel level, DamageSource damageSource, float amount) {

        this.playBreakSound(level, AiObjectModSounds.BOX_BREAK, BREAK_SOUND_VOLUME);
        return super.hurtServer(level, damageSource, amount);
    }
    //?} else
    /*public boolean hurt(DamageSource damageSource, float amount) {
        this.playBreakSound((ServerLevel) this.level(), AiObjectModSounds.BOX_BREAK, BREAK_SOUND_VOLUME);
        return super.hurt(damageSource, amount);
    }*/
}
