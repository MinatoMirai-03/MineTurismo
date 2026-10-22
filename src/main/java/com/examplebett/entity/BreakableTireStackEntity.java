package com.examplebett.entity;

import com.examplebett.compat.ModEntity;
import com.examplebett.render.CompiledCarModel;
import com.examplebett.render.MtlMaterial;
import com.examplebett.render.ObjModel;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
//? if >=1.19 {
import net.minecraft.util.RandomSource;
//?}
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
//? if >=1.21.6 {
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
//?} else
/*import net.minecraft.nbt.CompoundTag;*/
import net.minecraft.world.phys.Vec3;

import java.util.Map;

public class BreakableTireStackEntity extends Entity {

    //? if >=1.18 && <1.20 {
    /*
    public Level level() {
        return this.getLevel();
    }
    public boolean onGround() {
        return this.isOnGround();
    }
    */
    //?}
    //? if <1.18 && >=1.16 {
    /*
    public Level level() {
        return this.level;
    }
    public boolean onGround() {
        return this.isOnGround();
    }
    */
    //?}

    //? if <1.16 {
    /*
    public Level level() {
        return this.level;
    }
    public boolean onGround() {
        return this.onGround;
    }
    */
    //?}

    //? if <1.17 {
    /*
    public float getYRot() {
        return this.yRot;
    }
    public void setYRot(float yRot) {
        this.yRot = yRot;
    }
    public float getXRot() {
        return this.xRot;
    }
    public void setXRot(float xRot) {
        this.xRot = xRot;
    }
    public boolean isRemoved() {
        return this.removed;
    }
    public void discard() {
        this.remove();
    }
    */
    //?}

    private final String modelId;

    private ObjModel model;
    private Map<String, MtlMaterial> materials;
    private CompiledCarModel compiledModel;

    private int launchCooldown;

    private boolean tumbling;
    private float yawSpinPerTick;
    private float pitchSpinPerTick;

    private int despawnTicksLeft = -1;

    public BreakableTireStackEntity(EntityType<? extends BreakableTireStackEntity> entityType, Level level) {
        this(entityType, level, "breakable_tire_stack");
    }

    public BreakableTireStackEntity(EntityType<? extends BreakableTireStackEntity> entityType, Level level, String modelId) {
        super(entityType, level);
        this.modelId = modelId;
    }

    public String getModelId() {
        return this.modelId;
    }

    public float getRenderPivotHeight() {
        return 0f;
    }

    public ObjModel getModel() {
        return this.model;
    }

    public Map<String, MtlMaterial> getMaterials() {
        return this.materials;
    }

    public void setModel(ObjModel model) {
        this.model = model;
    }

    public void setMaterials(Map<String, MtlMaterial> materials) {
        this.materials = materials;
    }

    public CompiledCarModel getCompiledModel() {
        return this.compiledModel;
    }

    public void setCompiledModel(CompiledCarModel compiledModel) {
        this.compiledModel = compiledModel;
    }

    public int getLaunchCooldown() {
        return this.launchCooldown;
    }

    public void setLaunchCooldown(int launchCooldown) {
        this.launchCooldown = launchCooldown;
    }

    public void onLaunched(ServerLevel level, Vec3 velocity) {
        this.setDeltaMovement(velocity);
        this.setLaunchCooldown(10);
    }

    //? if >=1.19 {
    protected void startTumble(RandomSource random, float minDegPerTick, float maxDegPerTick) {
    //?} else
    /*protected void startTumble(java.util.Random random, float minDegPerTick, float maxDegPerTick) {*/
        this.tumbling = true;
        float spinRange = maxDegPerTick - minDegPerTick;
        this.yawSpinPerTick = (minDegPerTick + random.nextFloat() * spinRange) * (random.nextBoolean() ? 1f : -1f);
        this.pitchSpinPerTick = (minDegPerTick + random.nextFloat() * spinRange) * (random.nextBoolean() ? 1f : -1f);
    }

    protected void playBreakSound(ServerLevel level, SoundEvent sound, float volume) {
        float pitch = 0.9f + level.random.nextFloat() * 0.2f;
        level.playSound(null, ModEntity.getX(this), ModEntity.getY(this), ModEntity.getZ(this), sound, SoundSource.NEUTRAL, volume, pitch);
    }

    protected void startDespawnTimer(int ticks) {
        this.despawnTicksLeft = ticks;
    }

    @Override
    //? if >=1.20.5 {
    protected void defineSynchedData(SynchedEntityData.Builder builder) {

    }
    //?} else
    /*protected void defineSynchedData() {

    }*/

    @Override
    //? if >=1.21.6 {
    protected void readAdditionalSaveData(ValueInput input) {

    }
    //?} else
    /*public void readAdditionalSaveData(CompoundTag tag) {

    }*/

    @Override
    //? if >=1.21.6 {
    protected void addAdditionalSaveData(ValueOutput output) {

    }
    //?} else
    /*public void addAdditionalSaveData(CompoundTag tag) {

    }*/

    @Override
    public void tick() {
        super.tick();
        if (this.isRemoved()) {
            return;
        }
        if (this.launchCooldown > 0) {
            this.launchCooldown--;
        }

        if (this.tumbling && !this.onGround()) {
            this.yRotO = this.getYRot();
            this.xRotO = this.getXRot();
            this.setYRot(this.getYRot() + this.yawSpinPerTick);
            this.setXRot(this.getXRot() + this.pitchSpinPerTick);
        }

        if (!this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.04, 0.0));
        }
        this.move(MoverType.SELF, this.getDeltaMovement());

        Vec3 motion = this.getDeltaMovement();
        double drag = this.onGround() ? 0.6 : 0.98;
        this.setDeltaMovement(motion.x * drag, motion.y * 0.98, motion.z * drag);

        //? if >=1.17 {
        double mineturismo$horizSqr = this.getDeltaMovement().horizontalDistanceSqr();
        //?} else
        /*Vec3 mineturismo$motion = this.getDeltaMovement();
        double mineturismo$horizSqr = mineturismo$motion.x * mineturismo$motion.x + mineturismo$motion.z * mineturismo$motion.z;*/
        if (this.onGround() && mineturismo$horizSqr < 1.0E-5) {
            this.setDeltaMovement(0.0, this.getDeltaMovement().y, 0.0);
        }
        if (this.despawnTicksLeft >= 0) {
            this.despawnTicksLeft--;
            if (this.despawnTicksLeft <= 0) {
                this.discard();
            }
        }
    }

    //? if <1.19.3 {
    /*
    @Override
    public net.minecraft.network.protocol.Packet<?> getAddEntityPacket() {
        return new net.minecraft.network.protocol.game.ClientboundAddEntityPacket(this);
    }
    */
    //?}

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    //? if >=1.21.2 {
    public boolean hurtServer(ServerLevel level, DamageSource damageSource, float amount) {
    //?} else
    /*public boolean hurt(DamageSource damageSource, float amount) {*/

        if (!this.isRemoved()) {
            this.discard();
        }
        return true;
    }
}
