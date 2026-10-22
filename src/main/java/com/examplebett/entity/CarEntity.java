package com.examplebett.entity;

import com.examplebett.AiObjectMod;
import com.examplebett.block.AiObjectModBlocks;
import com.examplebett.compat.ModEntity;
import com.examplebett.compat.ModNetworking;
import com.examplebett.compat.ModRegistries;
import com.examplebett.item.AiObjectModItems;
import com.examplebett.network.OpenRailwayConsistSettingsPayload;
import com.examplebett.network.PlayCollisionSoundPayload;
import com.examplebett.network.ShowHudWarningPayload;
import com.examplebett.network.SyncWheelDistancePayload;
import com.examplebett.render.CompiledCarModel;
import com.examplebett.render.MtlMaterial;
import com.examplebett.render.ObjModel;
import com.examplebett.render.PartTransforms;
import com.examplebett.sound.AiObjectModSounds;
import com.examplebett.util.AiCarPool;
import com.examplebett.waypoint.RailwayNetworkSavedData;
import com.examplebett.waypoint.RailwayNode;
import com.examplebett.waypoint.SignalGroupConfig;
import com.examplebett.waypoint.TrafficSignalMarker;
import com.examplebett.waypoint.WaypointNetworkSavedData;
import com.examplebett.waypoint.WaypointNode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
//? if >=1.18.2 {
import net.minecraft.tags.TagKey;
//?}
import net.minecraft.util.Mth;
//? if >=1.19 {
import net.minecraft.util.RandomSource;
//?}
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
//? if >=1.21.2 {
import net.minecraft.world.entity.player.Input;
//?}
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

//? if >=1.21.6 {
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
//?} else {
/*import com.examplebett.compat.ValueInput;
import com.examplebett.compat.ValueOutput;*/
//?}
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class CarEntity extends Mob {

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

    //? if <1.21.5 {
    /*
    public boolean isLocalInstanceAuthoritative() {
        return this.isControlledByLocalInstance();
    }
    */
    //?}

    //? if >=1.21.5 {
    @Override
    protected boolean isLocalClientAuthoritative() {
        if (this.mineturismo$isNpcDrivenWithPassenger()) {
            return false;
        }
        return super.isLocalClientAuthoritative();
    }

    @Override
    public boolean isClientAuthoritative() {
        if (this.mineturismo$isNpcDrivenWithPassenger()) {
            return false;
        }
        return super.isClientAuthoritative();
    }
    //?} else {
    /*@Override
    public boolean isControlledByLocalInstance() {
        if (this.mineturismo$isNpcDrivenWithPassenger()) {
            return false;
        }
        return super.isControlledByLocalInstance();
    }*/
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
    public Entity getFirstPassenger() {
        java.util.List<Entity> mineturismo$passengers = this.getPassengers();
        return mineturismo$passengers.isEmpty() ? null : mineturismo$passengers.get(0);
    }
    */
    //?}

    public static final String EXTERNAL_MODEL_PREFIX = "external:";

    //? if >=1.17 {
    private static final SimpleParticleType SPARK_PARTICLE = ParticleTypes.ELECTRIC_SPARK;
    //?} else
    /*private static final SimpleParticleType SPARK_PARTICLE = ParticleTypes.CRIT;*/

    //? if >=1.18.2 {
    private static final TagKey<Block> CAR_BREAKABLE = TagKey.create(ModRegistries.BLOCK_KEY, AiObjectMod.id("car_breakable"));
    //?}

    private static boolean isCarBreakable(BlockState state) {
        //? if >=1.18.2 {
        return state.is(CAR_BREAKABLE);
        //?}
        //? if <1.18.2 && >=1.16 {
        /*return state.is(com.examplebett.block.AiObjectModBlocks.CAR_BREAKABLE_GLASS);*/
        //?}

        //? if <1.16 {
        /*return state.getBlock() == com.examplebett.block.AiObjectModBlocks.CAR_BREAKABLE_GLASS;*/
        //?}
    }

    private static final double BREAK_SPEED_THRESHOLD = 0.12;

    private static final double COLLISION_LENGTH_MARGIN = 1.5;
    private static final double COLLISION_WIDTH_MARGIN = 0.5;

    private static final double BREAKABLE_LAUNCH_SPEED_MULTIPLIER = 2.2;
    private static final double BREAKABLE_LAUNCH_UPWARD_BASE = 0.35;

    private static final double BREAKABLE_LAUNCH_VERTICAL_MARGIN = 4.0;

    private static final double CAR_PUSH_SPEED_MULTIPLIER = 2.4;
    private static final double CAR_PUSH_HIGH_SPEED_THRESHOLD = 2.0;
    private static final double CAR_PUSH_HIGH_SPEED_BOOST = 1.8;
    private static final double CAR_PUSH_MAX_SLIDE = 40.0;

    private static final double CAR_PUSH_LAUNCH_UPWARD_THRESHOLD = 1.5;
    private static final double CAR_PUSH_LAUNCH_UPWARD_FACTOR = 0.25;

    private static final double CAR_PUSH_SPIN_FACTOR = 8.0;
    private static final double CAR_PUSH_SPIN_DECAY = 0.85;

    private static final double CAR_TUMBLE_MIN_SPEED_BLOCKS_PER_TICK = 160.0 / 3.6 / 20.0;
    private static final double CAR_TUMBLE_CHANCE = 0.6;

    private static final double CAR_TUMBLE_FRONT_FLIP_MAX_CHANCE = 0.15;

    private static final double CAR_TUMBLE_FRONT_FLIP_FIXED_ROTATION_DEGREES = 180.0;
    private static final double CAR_TUMBLE_TARGET_ROTATION_MIN_DEGREES = 180.0;
    private static final double CAR_TUMBLE_TARGET_ROTATION_MAX_DEGREES = 360.0;

    private static final double CAR_TUMBLE_AIR_DECAY = 0.90;
    private static final double CAR_TUMBLE_GROUND_DECAY = 0.55;
    private static final double CAR_TUMBLE_SETTLE_ANGULAR_EPSILON = 1.0;

    private static final double CAR_TUMBLE_HORIZONTAL_SLIDE_SCALE = 0.5;

    private static final double CAR_TUMBLE_LAUNCH_UPWARD_VELOCITY = 0.7;

    private static final int GLASS_BREAK_SOUND_CHANCE = 3;
    private static final int CAR_PUSH_COOLDOWN_TICKS = 10;

    public static final float ACTION_MULTIPLIER_MIN = 0.25f;
    public static final float ACTION_MULTIPLIER_MAX = 3.0f;

    public static final float PUSH_DISTANCE_MULTIPLIER_MIN = 0.25f;
    public static final float PUSH_DISTANCE_MULTIPLIER_MAX = 10.0f;
    private static final float DEFAULT_PUSH_DISTANCE_MULTIPLIER = 3.0f;

    public static final float SUSPENSION_INTENSITY_MULTIPLIER_MIN = 0.0f;
    public static final float SUSPENSION_INTENSITY_MULTIPLIER_MAX = 10.0f;
    private static final float DEFAULT_SUSPENSION_INTENSITY_MULTIPLIER = 1.0f;

    private static final int SECURITY_ALARM_DURATION_TICKS = 200;
    private static final int SECURITY_ALARM_BLINK_INTERVAL_TICKS = 10;

    private static final int SECURITY_ALARM_VARIANT_COUNT = 3;
    private static final int SECURITY_ALARM_CHIRP_TICKS = 40;
    private static final int SECURITY_ALARM_SWEEP_TICKS = 44;

    private static final float MOB_DAMAGE_REFERENCE_DAMAGE = 30f;

    private static final double WALL_HIT_INTENDED_SPEED_THRESHOLD = 0.02;
    private static final int WALL_HIT_COOLDOWN_TICKS = 8;

    private static final double WALL_HIT_SPEED_RETENTION = 2.0 / 3.0;

    private static final int PUDDLE_SPLASH_COOLDOWN_TICKS = 6;

    private static final int CHASE_SPAWN_INTERVAL_MIN_TICKS = 160;
    private static final int CHASE_SPAWN_INTERVAL_MAX_TICKS = 300;
    private static final int CHASE_MAX_CONCURRENT_PER_TARGET = 2;
    private static final double CHASE_SPAWN_DISTANCE_MIN = 25.0;
    private static final double CHASE_SPAWN_DISTANCE_MAX = 45.0;
    private static final double CHASE_SPAWN_LATERAL_SPREAD = 12.0;

    private static final int CHASE_LIFETIME_TIMEOUT_TICKS = 600;

    private static final double CHASE_DODGE_TRIGGER_DISTANCE = 6.0;
    private static final double CHASE_DODGE_RELEASE_DISTANCE = 10.0;
    private static final double CHASE_TARGET_ANGLE_DEADZONE_DEGREES = 5.0;
    private static final double CHASE_SPIN_OUT_TRIGGER_DISTANCE = 12.0;
    private static final double CHASE_SPIN_OUT_CHANCE_PER_TICK = 0.01;
    private static final int CHASE_SPIN_OUT_MIN_TICKS = 15;
    private static final int CHASE_SPIN_OUT_MAX_TICKS = 25;

    private static final int CHAOS_SPAWN_INTERVAL_MIN_TICKS = 40;
    private static final int CHAOS_SPAWN_INTERVAL_MAX_TICKS = 80;
    private static final int CHAOS_MAX_CONCURRENT_PER_TARGET = 1;
    private static final double CHAOS_SPAWN_DISTANCE_MIN = 60.0;
    private static final double CHAOS_SPAWN_DISTANCE_MAX = 80.0;

    private static final double CHAOS_SPAWN_LATERAL_SPREAD = 0.0;

    private static final float CHAOS_CHARGE_TOP_SPEED_KMH = 150.0F;

    private static final int CHAOS_LIFETIME_TIMEOUT_TICKS = 300;

    private static final double CHAOS_MISS_DISTANCE_INCREASE_EPSILON = 0.01;

    private static final int CHAOS_MISS_EXPLOSION_DELAY_TICKS = 6;

    private static final double CHAOS_SPINNING_CHANCE = 0.35;
    private static final double CHAOS_TUMBLING_CHANCE = 0.35;

    private static final double CHAOS_SPIN_BASE_DEGREES_PER_TICK = 12.0;

    private static final double CHAOS_TUMBLE_DEGREES_PER_TICK = 28.0;

    private static final double CHAOS_TUMBLE_NO_LIMIT_DEGREES = 1.0E9;

    private static final double CHAOS_TUMBLE_SPARK_CHANCE_PER_TICK = 0.03;

    private static final double CHAOS_HITBOX_SHRINK = 0.3;

    private static final double CHAOS_HITBOX_ADDITIONAL_SCALE = 2.0 / 3.0;

    private static final double TOWN_SPAWN_SEARCH_RADIUS = 48.0;
    private static final double TOWN_WAYPOINT_ARRIVAL_DISTANCE = 2.0;

    private static final float NPC_STEP_UP_INCREMENT = 0.1F;

    private static final double TOWN_LANE_OFFSET = 1.5;

    private static final double TOWN_STEER_LOOKAHEAD_DISTANCE = 4.0;

    private static final float TOWN_HEADING_SMOOTHING = 0.35F;

    private static final double TOWN_LEGACY_TARGET_ANGLE_DEADZONE_DEGREES = 5.0;
    private static final double TOWN_LEGACY_STEER_FULL_ANGLE_DEGREES = 45.0;
    private static final float TOWN_LEGACY_CORNER_SLOWDOWN_FRACTION = 0.7F;

    private static final double TOWN_DESPAWN_DISTANCE = 96.0;

    private static final int TOWN_LIFETIME_TIMEOUT_TICKS = 24000;

    private static final double TOWN_AI_THROTTLE_DISTANCE = 48.0;

    private static final double TOWN_FOLLOW_LOOKAHEAD_DISTANCE = 12.0;

    private static final double TOWN_FOLLOW_LANE_HALF_WIDTH = 1.5;

    private static final double TOWN_FOLLOW_STOP_GAP = 2.0;

    private static final double TOWN_FOLLOW_MIN_HEADING_ALIGNMENT = 0.5;

    private static final double TOWN_FOLLOW_BRAKING_DISTANCE = 8.0;

    private static final int TOWN_FOLLOW_CHECK_INTERVAL_TICKS = 3;

    private static final double TOWN_SIGNAL_STOP_MARGIN = 1.0;
    private static final double TOWN_SIGNAL_BRAKING_DISTANCE = 10.0;

    private static final EntityDataAccessor<String> MODEL_ID = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.INT);
    private static final int DEFAULT_COLOR = 0xFFFFFF;

    private static final EntityDataAccessor<String> FRONT_PLATE = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Float> FRONT_PLATE_X = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> FRONT_PLATE_Y = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> FRONT_PLATE_Z = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> FRONT_PLATE_SCALE = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.FLOAT);

    private static final EntityDataAccessor<Float> FRONT_PLATE_YAW = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> FRONT_PLATE_PITCH = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> FRONT_PLATE_ROLL = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<String> REAR_PLATE = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Float> REAR_PLATE_X = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> REAR_PLATE_Y = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> REAR_PLATE_Z = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> REAR_PLATE_SCALE = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> REAR_PLATE_YAW = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> REAR_PLATE_PITCH = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> REAR_PLATE_ROLL = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.FLOAT);

    private static final float FALLBACK_FRONT_Z = 2.3f;
    private static final float FALLBACK_REAR_Z = -2.3f;
    private static final float FALLBACK_PLATE_Y = 0.45f;
    public static final float FALLBACK_PLATE_SCALE = 0.4f;

    public static final float EUROPEAN_DEFAULT_PLATE_SCALE = FALLBACK_PLATE_SCALE + 5 * 0.05f;

    public static final float ITALY_FRONT_PLATE_SCALE = EUROPEAN_DEFAULT_PLATE_SCALE * 0.7f;
    private static final float FALLBACK_PLATE_ROTATION = 0f;

    private static final java.util.Set<String> EUROPEAN_PLATE_IDS = java.util.Set.of(
            "southkorea", "germany", "france", "italy", "italy_glossy",
            "beijing_green", "beijing_blue", "unitedkingdom", "unitedkingdom_front", "russia",
            "southkorea_yellow", "blank_europe", "blank_brazil", "blank_italy",
            "singapore", "malaysia", "hongkong", "sweden", "india", "dubai",
            "blank_europe_yellow", "blank_singapore", "blank_malaysia", "blank_hongkong", "blank_china");

    private static boolean isEuropeanPlate(String id) {
        return EUROPEAN_PLATE_IDS.contains(id) || com.examplebett.render.AddonPlateDesigns.isEuropean(id);
    }

    private com.examplebett.render.PlateAttachment plateAttachment;

    public com.examplebett.render.PlateAttachment getPlateAttachment() {
        return this.plateAttachment;
    }

    public void setPlateAttachment(com.examplebett.render.PlateAttachment plateAttachment) {
        this.plateAttachment = plateAttachment;
    }

    private static final EntityDataAccessor<Float> TIRE_SMOKE_X = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> TIRE_SMOKE_Y = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> TIRE_SMOKE_Z = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<String> TIRE_SMOKE_PARTICLE = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.STRING);

    private static final EntityDataAccessor<Boolean> TIRE_SMOKE_PREVIEW = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Float> DETECTED_WHEEL_TRACK_HALF_WIDTH = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DETECTED_WHEEL_REAR_Z = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.FLOAT);

    private static final EntityDataAccessor<Float> HEADLIGHT_X = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> HEADLIGHT_Y = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> HEADLIGHT_Z = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> TAILLIGHT_X = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> TAILLIGHT_Y = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> TAILLIGHT_Z = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.FLOAT);

    private static final EntityDataAccessor<Integer> LIGHT_MODE = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<String> HEADLIGHT_COLOR = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.STRING);

    private static final EntityDataAccessor<Boolean> LIGHT_BLINK = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Boolean> TOWN_NIGHT_LIGHTS_ACTIVE = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Boolean> IS_TOWN_CAR = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Boolean> CHAUFFEUR_ACTIVE = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Boolean> RAILWAY_ROUTE_ACTIVE_SYNCED = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.BOOLEAN);

    private static final float FALLBACK_HEADLIGHT_X = 0.6f;
    private static final float FALLBACK_HEADLIGHT_Y = 0.4f;
    private static final float FALLBACK_HEADLIGHT_Z = 2.2f;
    private static final float FALLBACK_TAILLIGHT_X = 0.6f;
    private static final float FALLBACK_TAILLIGHT_Y = 0.4f;
    private static final float FALLBACK_TAILLIGHT_Z = -2.2f;

    private com.examplebett.render.LightAttachment lightAttachment;

    public com.examplebett.render.LightAttachment getLightAttachment() {
        return this.lightAttachment;
    }

    public void setLightAttachment(com.examplebett.render.LightAttachment lightAttachment) {
        this.lightAttachment = lightAttachment;
    }

    private java.util.Set<VehicleCategory> vehicleCategories;

    public java.util.Set<VehicleCategory> getVehicleCategories() {
        return this.vehicleCategories;
    }

    public void setVehicleCategories(java.util.Set<VehicleCategory> vehicleCategories) {
        this.vehicleCategories = vehicleCategories;
    }

    public java.util.Set<VehicleCategory> resolveVehicleCategories() {
        if (this.vehicleCategories != null) {
            return this.vehicleCategories;
        }
        net.minecraft.server.packs.resources.ResourceManager resourceManager =
                this.level().getServer() != null
                        ? com.examplebett.compat.ModResources.getServerResourceManager(this.level().getServer())
                        : null;
        return resourceManager != null
                ? VehicleCategoryFootprint.get(resourceManager, this.getModelId())
                : java.util.Set.of();
    }

    public boolean hasVehicleCategory(VehicleCategory category) {
        return this.resolveVehicleCategories().contains(category);
    }

    public enum LightMode {
        AUTO,
        FORCE_ON,
        FORCE_OFF
    }

    private static final EntityDataAccessor<Float> WHEEL_FRONT_Z_OFFSET = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> WHEEL_REAR_Z_OFFSET = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> WHEEL_TRACK_HALF_WIDTH_OFFSET = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> WHEEL_AXLE_HEIGHT_OFFSET = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.FLOAT);

    private static final EntityDataAccessor<Float> BODY_HEIGHT_OFFSET = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.FLOAT);

    private static final EntityDataAccessor<Float> WHEEL_CAMBER_DEGREES = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.FLOAT);

    private static final EntityDataAccessor<Float> WHEEL_SCALE = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.FLOAT);

    private static final EntityDataAccessor<Float> WHEEL_WIDTH_SCALE = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.FLOAT);

    private static final EntityDataAccessor<Boolean> EXTRA_REAR_AXLE_ENABLED = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> EXTRA_REAR_AXLE_Z_OFFSET = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.FLOAT);

    private static final EntityDataAccessor<Boolean> EXTRA_REAR_AXLE_2_ENABLED = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> EXTRA_REAR_AXLE_2_Z_OFFSET = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.FLOAT);

    public static final float DEFAULT_EXTRA_REAR_AXLE_SPACING = 0.8f;

    private static final EntityDataAccessor<Float> LIP_STRETCH = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.FLOAT);

    private static final EntityDataAccessor<Float> LIP_WIDTH = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.FLOAT);

    private static final EntityDataAccessor<Float> LIP_BAND_HEIGHT_CM = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.FLOAT);
    public static final float DEFAULT_LIP_BAND_HEIGHT_CM = 20f;

    private static final EntityDataAccessor<Float> LIP_FRONT_EXTEND = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.FLOAT);

    private static final EntityDataAccessor<Float> LIMO_STRETCH_CM = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.FLOAT);

    private static final EntityDataAccessor<Float> LIMO_ZONE_CM = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.FLOAT);
    public static final float DEFAULT_LIMO_ZONE_CM = 100f;

    private static final EntityDataAccessor<Float> LIMO_CENTER_OFFSET_CM = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.FLOAT);

    private static final EntityDataAccessor<Float> WIDTH_STRETCH_CM = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> WIDTH_ZONE_CM = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.FLOAT);
    public static final float DEFAULT_WIDTH_ZONE_CM = 100f;
    private static final EntityDataAccessor<Float> WIDTH_CENTER_OFFSET_CM = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.FLOAT);

    private static final EntityDataAccessor<Float> PASSENGER_X_ADJUST = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> PASSENGER_Y_ADJUST = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> PASSENGER_Z_ADJUST = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.FLOAT);

    private static final EntityDataAccessor<String> REAR_WING_ENABLED_IDS = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.STRING);

    private static final EntityDataAccessor<String> REAR_WING_TRANSFORMS = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.STRING);

    public static final float DEFAULT_REAR_WING_X = -0.02f;
    public static final float DEFAULT_REAR_WING_Y = 1.23f;
    public static final float DEFAULT_REAR_WING_Z = -2.05f;
    public static final float DEFAULT_REAR_WING_SCALE = 1.35f;

    private static final EntityDataAccessor<String> POLICE_LIGHT_ENABLED_IDS = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.STRING);

    private static final EntityDataAccessor<String> POLICE_LIGHT_TRANSFORMS = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.STRING);

    public static final float DEFAULT_POLICE_LIGHT_X = 0f;
    public static final float DEFAULT_POLICE_LIGHT_Y = 0.73f;
    public static final float DEFAULT_POLICE_LIGHT_Z = -0.3f;
    public static final float DEFAULT_POLICE_LIGHT_SCALE = 0.62f;

    private static final EntityDataAccessor<String> TAXI_SIGN_ENABLED_IDS = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.STRING);

    private static final EntityDataAccessor<String> TAXI_SIGN_TRANSFORMS = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.STRING);

    public static final float DEFAULT_TAXI_SIGN_X = 0f;
    public static final float DEFAULT_TAXI_SIGN_Y = 0.75f;
    public static final float DEFAULT_TAXI_SIGN_Z = -0.25f;
    public static final float DEFAULT_TAXI_SIGN_SCALE = 0.2f;

    private static final EntityDataAccessor<String> OTHER_PART_ENABLED_IDS = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.STRING);

    private static final EntityDataAccessor<String> OTHER_PART_TRANSFORMS = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.STRING);
    public static final float DEFAULT_OTHER_PART_X = 0f;
    public static final float DEFAULT_OTHER_PART_Y = 0f;
    public static final float DEFAULT_OTHER_PART_Z = 0f;
    public static final float DEFAULT_OTHER_PART_SCALE = 1f;
    public static final float DEFAULT_SEATED_PLAYER_ANGLE_Y = 180f;
    public static final float DEFAULT_OTHER_PART_STRETCH = 1f;

    public static final float DEFAULT_OTHER_PART_CARDBOARD_BOX_SCALE = 0.5f;

    public static final float DEFAULT_OTHER_PART_R34_Y = 1.0f;

    private static final EntityDataAccessor<String> FRONT_BRAKE_CALIPER_ENABLED_IDS = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.STRING);

    private static final EntityDataAccessor<String> FRONT_BRAKE_CALIPER_TRANSFORMS = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> REAR_BRAKE_CALIPER_ENABLED_IDS = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> REAR_BRAKE_CALIPER_TRANSFORMS = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.STRING);

    private static final EntityDataAccessor<String> TEXT_PARTS = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.STRING);

    private static final EntityDataAccessor<String> UNDER_NEON = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.STRING);

    private static final EntityDataAccessor<String> BRAND_LOGOS = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.STRING);

    private static final EntityDataAccessor<String> EXHAUST_ENABLED_IDS = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> EXHAUST_TRANSFORMS = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.STRING);

    private static final EntityDataAccessor<Integer> TOW_LEADER_ID = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Integer> TRAILER_HEAD_ENTITY_ID = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> TRAILER_HITCH_BEHIND_SYNCED = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> TRAILER_KINGPIN_AHEAD_SYNCED = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.FLOAT);

    public static final int TRAILER_CARGO_SIZE = 27;

    private static final double TRAILER_CARGO_REACH_SQR = 12.0 * 12.0;
    private final net.minecraft.world.SimpleContainer trailerCargo = new net.minecraft.world.SimpleContainer(TRAILER_CARGO_SIZE) {
        @Override
        public boolean stillValid(Player player) {
            return CarEntity.this.isAlive() && player.distanceToSqr(CarEntity.this) < TRAILER_CARGO_REACH_SQR;
        }
    };
    public static final float DEFAULT_BRAKE_CALIPER_X = 0f;
    public static final float DEFAULT_BRAKE_CALIPER_Y = 0f;
    public static final float DEFAULT_BRAKE_CALIPER_Z = -0.17f;
    public static final float DEFAULT_BRAKE_CALIPER_SCALE = 1f;

    public static final String BRAKE_CALIPER_REVERSED_ID_SUFFIX = "_reversed";

    public static PartTransforms.Transform6 defaultBrakeCaliperTransform(String partId) {
        if (partId != null && partId.endsWith(BRAKE_CALIPER_REVERSED_ID_SUFFIX)) {
            return new PartTransforms.Transform6(DEFAULT_BRAKE_CALIPER_X, -DEFAULT_BRAKE_CALIPER_Y,
                    -DEFAULT_BRAKE_CALIPER_Z, 90f, 0f, DEFAULT_BRAKE_CALIPER_SCALE);
        }
        return new PartTransforms.Transform6(DEFAULT_BRAKE_CALIPER_X, DEFAULT_BRAKE_CALIPER_Y,
                DEFAULT_BRAKE_CALIPER_Z, -90f, 0f, DEFAULT_BRAKE_CALIPER_SCALE);
    }

    private static final EntityDataAccessor<Boolean> POLICE_LIGHTS_ON = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Boolean> POLICE_SIREN_ON = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<String> WHEEL_MODEL_ID_OVERRIDE = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.STRING);

    private static final EntityDataAccessor<String> WHEEL_COLOR_OVERRIDE = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.STRING);

    private static final EntityDataAccessor<Float> CAR_SCALE = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.FLOAT);
    private static final float CAR_SCALE_MIN = 0.25f;
    private static final float CAR_SCALE_MAX = 10.0f;

    private static final EntityDataAccessor<Float> CAMERA_DISTANCE_ADJUST = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.FLOAT);

    private static final EntityDataAccessor<Float> CAMERA_HEIGHT_ADJUST = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.FLOAT);

    private static final EntityDataAccessor<String> LIVERY_OVERRIDE = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.STRING);

    private static final EntityDataAccessor<String> ENGINE_SOUND_ID = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.STRING);

    private static final EntityDataAccessor<String> HORN_SOUND_ID = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> SIREN_SOUND_ID = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.STRING);

    private static final EntityDataAccessor<Boolean> BOMB_INSTALLED = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> ENGINE_PITCH_INTENSITY = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.FLOAT);
    private static final float DEFAULT_ENGINE_PITCH_INTENSITY = 1.0f;

    private static final EntityDataAccessor<Float> ENGINE_TOP_SPEED_PITCH = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.FLOAT);
    private static final float DEFAULT_ENGINE_TOP_SPEED_PITCH = 1.0f;
    public static final float ENGINE_TOP_SPEED_PITCH_MIN = 0.5f;
    public static final float ENGINE_TOP_SPEED_PITCH_MAX = 2.0f;

    private static final EntityDataAccessor<Float> ENGINE_LOOP_USE_FRACTION = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.FLOAT);
    private static final float DEFAULT_ENGINE_LOOP_USE_FRACTION = 1.0f;

    public static final float ENGINE_LOOP_USE_FRACTION_MIN = 0.25f;

    private static final EntityDataAccessor<String> RAILWAY_MIDDLE_MODEL_IDS = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> RAILWAY_TAIL_MODEL_ID = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Boolean> RAILWAY_CONFIGURED = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<String> RAILWAY_COUPLED_ENTITY_IDS = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.STRING);

    private static final EntityDataAccessor<Boolean> RAILWAY_FOLLOWER = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Integer> RAILWAY_HEAD_ENTITY_ID = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> RAILWAY_TRAIL_DISTANCE_SYNCED = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> RAILWAY_HALF_LENGTH_SYNCED = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.FLOAT);

    private static final EntityDataAccessor<Boolean> IS_RECORDING = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_GHOST = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.BOOLEAN);

    public enum DriveMode {
        MODERN,
        CLASSIC,
        HYPER,
        DRIFT,
        CUSTOM
    }

    private static final EntityDataAccessor<Integer> DRIVE_MODE = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Boolean> CHASE_MODE = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Boolean> CHAOS_MODE = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Float> CUSTOM_TOP_SPEED_KMH = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> CUSTOM_ACCEL_FRACTION = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> CUSTOM_BRAKE_FRACTION = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> CUSTOM_HANDLING_FRACTION = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> CUSTOM_TIRE_SMOKE_ALWAYS = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Boolean> DESPAWN_ENABLED = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Integer> ITEM_ICON = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.INT);
    public static final int ITEM_ICON_VEHICLE = 0;
    public static final int ITEM_ICON_KEY = 1;
    public static final int ITEM_ICON_BOXED = 2;

    public static final int ITEM_ICON_SMART_KEY = 3;

    public static final int PLACED_FROM_NONE = 0;
    public static final int PLACED_FROM_SPAWN_ITEM = 1;
    public static final int PLACED_FROM_BOXED_CAR = 2;
    private int placedFromItem = PLACED_FROM_NONE;

    private static final EntityDataAccessor<Float> TUMBLE_PITCH = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> TUMBLE_ROLL = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.FLOAT);

    private static final EntityDataAccessor<Float> WHEELIE_PITCH = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.FLOAT);

    private static final EntityDataAccessor<Float> BIKE_LEAN_ROLL = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.FLOAT);

    private static final EntityDataAccessor<Boolean> BIKE_STATIONARY = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Float> TILT_ROLL = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> TILT_PITCH = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> TILT_HEIGHT_CM = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.FLOAT);

    private static final EntityDataAccessor<Float> SUSPENSION_PITCH = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> SUSPENSION_ROLL = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.FLOAT);

    private static final EntityDataAccessor<Boolean> SECURITY_ALARM_ACTIVE = SynchedEntityData.defineId(CarEntity.class, EntityDataSerializers.BOOLEAN);
    public static final float CUSTOM_TOP_SPEED_KMH_MIN = 0.5F;
    public static final float CUSTOM_TOP_SPEED_KMH_MAX = 531.0F;
    private static final float DEFAULT_CUSTOM_TOP_SPEED_KMH = 150.0F;
    private static final float DEFAULT_CUSTOM_FRACTION = 0.5F;
    private static final float DEFAULT_TIRE_SMOKE_X = 0.6f;
    private static final float DEFAULT_TIRE_SMOKE_Y = 0.3f;
    private static final float DEFAULT_TIRE_SMOKE_Z = -2.3f;

    private static final float TIRE_SMOKE_TRACK_WIDTH_FACTOR = 0.6f;
    private static final float TIRE_SMOKE_REAR_Z_FACTOR = 1.0222f;

    private static final float TIRE_SMOKE_TURN_THRESHOLD = 45.0f;
    private static final int TIRE_SMOKE_WINDOW_TICKS = 6;

    private static final int TIRE_SMOKE_PREVIEW_INTERVAL_TICKS = 10;
    private int tireSmokePreviewTickCounter;

    public static WrenchMenuOpener wrenchMenuOpener;

    public static WrenchMenuOpener wheelWrenchMenuOpener;

    public static WrenchMenuOpener debugWrenchMenuOpener;

    public interface WrenchMenuOpener {
        void open(CarEntity car, Player player);
    }

    public static java.util.function.BooleanSupplier clientDescendKeyHeld;

    public static java.util.function.BooleanSupplier clientForwardKeyHeld;
    public static java.util.function.BooleanSupplier clientBackKeyHeld;

    public static java.util.function.Supplier<net.minecraft.server.packs.resources.ResourceManager> clientDimensionsResourceManager = () -> null;

    private ObjModel model;
    private Map<String, MtlMaterial> materials;
    private CompiledCarModel compiledModel;

    private static final double CLIENT_SLIDE_LATERAL_THRESHOLD = 0.05;
    private static final double CLIENT_SLIDE_LATERAL_FULL = 0.5;
    private static final float CLIENT_SLIDE_YAW_RATE_THRESHOLD = 3.0f;
    private static final float CLIENT_SLIDE_YAW_RATE_FULL = 20.0f;

    private static final float HANDBRAKE_BRAKING_SLIP_MIN_SPEED = 0.03f;

    private static final float TURN_SLIP_VOLUME_FACTOR = 0.50f;

    private static final int STEER_HOLD_TICKS_THRESHOLD = 20;
    private double clientSlidePrevX;
    private double clientSlidePrevZ;
    private float clientSlidePrevYaw;
    private boolean clientSlidePrevInitialized;
    private float clientSlideIntensity;
    private int steerHoldDirection;
    private int steerHoldTicks;

    private double driftSmokePrevX;
    private double driftSmokePrevZ;
    private float driftSmokePrevHeading;
    private boolean driftSmokePrevInitialized;

    private boolean hornBroadcastActive;
    private int hornBroadcastCooldownTicks;

    private static final int TIRE_SLIP_BROADCAST_INTERVAL_TICKS = 57;
    private int tireSlipBroadcastCooldownTicks;

    private int engineBroadcastCooldownTicks;

    private static final int CLIENT_SLIP_REPORT_TIMEOUT_TICKS = 4;
    private int clientSlipReportTicksRemaining;

    private static final int SERVER_SLIP_WINDOW_TICKS = 6;
    private double serverSlideWindowX;
    private double serverSlideWindowZ;
    private float serverSlideWindowYaw;
    private int serverSlideWindowAge;
    private boolean serverSlideWindowInitialized;

    private static final double SERVER_SMOKE_LATERAL_THRESHOLD = 0.2;
    private static final float SERVER_SMOKE_YAW_RATE_THRESHOLD = 10.0f;

    private record SlipMeasurement(double lateralSpeed, float yawRate) {
    }

    private SlipMeasurement measureServerSideSlip() {
        double curX = ModEntity.getX(this);
        double curZ = ModEntity.getZ(this);
        float curYaw = this.getYRot();
        if (!this.serverSlideWindowInitialized) {
            this.serverSlideWindowX = curX;
            this.serverSlideWindowZ = curZ;
            this.serverSlideWindowYaw = curYaw;
            this.serverSlideWindowAge = 0;
            this.serverSlideWindowInitialized = true;
            return new SlipMeasurement(0.0, 0.0f);
        }
        double dx = curX - this.serverSlideWindowX;
        double dz = curZ - this.serverSlideWindowZ;

        float yawRad = (float) Math.toRadians(-curYaw);
        double forwardX = -Mth.sin(yawRad);
        double forwardZ = Mth.cos(yawRad);
        double rightX = forwardZ;
        double rightZ = -forwardX;

        double lateralSpeed = Math.abs(dx * rightX + dz * rightZ) / SERVER_SLIP_WINDOW_TICKS;
        float yawRate = Math.abs(Mth.wrapDegrees(curYaw - this.serverSlideWindowYaw)) / SERVER_SLIP_WINDOW_TICKS;
        this.serverSlideWindowAge++;
        if (this.serverSlideWindowAge >= SERVER_SLIP_WINDOW_TICKS) {
            this.serverSlideWindowX = curX;
            this.serverSlideWindowZ = curZ;
            this.serverSlideWindowYaw = curYaw;
            this.serverSlideWindowAge = 0;
        }
        return new SlipMeasurement(lateralSpeed, yawRate);
    }

    public void reportClientSlip() {
        if (!this.level().isClientSide()) {
            this.clientSlipReportTicksRemaining = CLIENT_SLIP_REPORT_TIMEOUT_TICKS;
        }
    }

    public void reportClientWheelieTrigger() {
        if (!this.level().isClientSide() && this.hasVehicleCategory(VehicleCategory.MUSCLE_CAR)) {
            this.wheelieHoldTicksRemaining = WHEELIE_HOLD_TICKS;
        }
    }

    public boolean consumeClientSlipFlag() {
        boolean value = this.clientSlipThisTick;
        this.clientSlipThisTick = false;
        return value;
    }

    public boolean consumeClientWheelieTriggerFlag() {
        boolean value = this.clientWheelieTriggerThisTick;
        this.clientWheelieTriggerThisTick = false;
        return value;
    }

    private void updateServerSideSlipBroadcast() {

        if (this.clientSlipReportTicksRemaining > 0) {
            this.clientSlipReportTicksRemaining--;
        }
        boolean clientReportedSlip = this.clientSlipReportTicksRemaining > 0;
        SlipMeasurement slip = this.measureServerSideSlip();

        boolean slipSoundWorthy = !this.hasVehicleCategory(VehicleCategory.HELICOPTER)
                && (clientReportedSlip
                || slip.lateralSpeed() >= CLIENT_SLIDE_LATERAL_THRESHOLD
                || slip.yawRate() >= CLIENT_SLIDE_YAW_RATE_THRESHOLD);

        boolean smokeWorthy = clientReportedSlip;
        if (slipSoundWorthy) {
            if (this.tireSlipBroadcastCooldownTicks <= 0) {

                boolean driftOrHandbrake = this.getDriveMode() == DriveMode.DRIFT || this.handbrakeHeld || clientReportedSlip;
                this.level().playSound(null, ModEntity.getX(this), ModEntity.getY(this), ModEntity.getZ(this),
                        AiObjectModSounds.TIRE_SLIP, SoundSource.NEUTRAL,
                        driftOrHandbrake ? 1.0f : TURN_SLIP_VOLUME_FACTOR, 1.0f);
                this.tireSlipBroadcastCooldownTicks = TIRE_SLIP_BROADCAST_INTERVAL_TICKS;
            } else {
                this.tireSlipBroadcastCooldownTicks--;
            }
        } else {
            this.tireSlipBroadcastCooldownTicks = 0;
        }
        if (smokeWorthy) {
            this.spawnTireSmoke(DRIFT_RESIDUAL_SMOKE_PARTICLE_COUNT);
        }
    }

    private void updateEngineSoundBroadcast() {
        if (this.engineBroadcastCooldownTicks > 0) {
            this.engineBroadcastCooldownTicks--;
            return;
        }
        EngineSounds.Option engineSound = EngineSounds.byId(this.getEngineSoundId());
        this.level().playSound(null, ModEntity.getX(this), ModEntity.getY(this), ModEntity.getZ(this),
                engineSound.sound(), SoundSource.NEUTRAL, 1.0f, 1.0f);
        this.engineBroadcastCooldownTicks = engineSound.loopDurationTicks();
    }

    public void setHornBroadcastActive(boolean active) {
        if (this.level().isClientSide()) {
            return;
        }
        this.hornBroadcastActive = active;
        if (active) {
            this.hornBroadcastCooldownTicks = 0;
        }
    }

    public void updateClientSlideIntensity(boolean isLocallyDriven) {
        double curX = ModEntity.getX(this);
        double curZ = ModEntity.getZ(this);
        float curYaw = this.getYRot();
        this.updateSteerHoldTracking();
        if (!this.clientSlidePrevInitialized) {
            this.clientSlidePrevX = curX;
            this.clientSlidePrevZ = curZ;
            this.clientSlidePrevYaw = curYaw;
            this.clientSlidePrevInitialized = true;
            this.clientSlideIntensity = 0.0f;
            return;
        }
        double dx = curX - this.clientSlidePrevX;
        double dz = curZ - this.clientSlidePrevZ;

        float yawRad = (float) Math.toRadians(-curYaw);
        double forwardX = -Mth.sin(yawRad);
        double forwardZ = Mth.cos(yawRad);

        double rightX = forwardZ;
        double rightZ = -forwardX;
        double lateralSpeed = Math.abs(dx * rightX + dz * rightZ);
        float yawRate = Math.abs(Mth.wrapDegrees(curYaw - this.clientSlidePrevYaw));

        double lateralFactor = Mth.clamp(
                (lateralSpeed - CLIENT_SLIDE_LATERAL_THRESHOLD) / (CLIENT_SLIDE_LATERAL_FULL - CLIENT_SLIDE_LATERAL_THRESHOLD), 0.0, 1.0);
        double yawFactor = Mth.clamp(
                (yawRate - CLIENT_SLIDE_YAW_RATE_THRESHOLD) / (CLIENT_SLIDE_YAW_RATE_FULL - CLIENT_SLIDE_YAW_RATE_THRESHOLD), 0.0, 1.0);
        float rawIntensity = (float) Math.max(lateralFactor, yawFactor);

        boolean driftInstantSlip = (this.getDriveMode() == DriveMode.DRIFT || this.handbrakeHeld || this.reverseDriftAutoThisTick) && this.steerHoldDirection != 0;

        boolean handbrakeBrakingSlip = this.handbrakeHeld && !this.rawForwardHeld && !this.rawBackwardHeld
                && Math.abs(this.classicSpeed) > HANDBRAKE_BRAKING_SLIP_MIN_SPEED;

        if (driftInstantSlip || handbrakeBrakingSlip) {
            this.clientSlideIntensity = 1.0f;
        } else if (isLocallyDriven && this.steerHoldTicks < STEER_HOLD_TICKS_THRESHOLD) {
            this.clientSlideIntensity = 0.0f;
        } else {
            this.clientSlideIntensity = rawIntensity * TURN_SLIP_VOLUME_FACTOR;
        }

        this.clientSlidePrevX = curX;
        this.clientSlidePrevZ = curZ;
        this.clientSlidePrevYaw = curYaw;
    }

    private void updateSteerHoldTracking() {
        int direction = this.steerInput > 0.3f ? 1 : (this.steerInput < -0.3f ? -1 : 0);
        if (direction != 0 && direction == this.steerHoldDirection) {
            this.steerHoldTicks++;
        } else {
            this.steerHoldDirection = direction;
            this.steerHoldTicks = direction != 0 ? 1 : 0;
        }
    }

    public float getClientSlideIntensity() {
        if (this.hasVehicleCategory(VehicleCategory.HELICOPTER)) {
            return 0.0f;
        }
        return this.clientSlideIntensity;
    }

    private com.examplebett.render.WheelAttachment wheelAttachment;

    private float wheelStyleDefaultWidthScale = 1f;
    private CompiledCarModel wheelCompiledModel;

    private CompiledCarModel wheelCompiledModelMirrored;

    private String loadedWheelModelId = null;

    private String loadedWheelColorId = null;

    private com.examplebett.render.LiveryOptions liveryOptions;

    private String loadedLiveryId = null;
    private float drivingYaw;
    private float drivingYawWindow;
    private int drivingYawWindowAge;

    private float wheelRot;
    private float wheelRotO;
    private double wheelPrevX;
    private double wheelPrevZ;
    private boolean wheelPrevInitialized;

    private float burnoutSpinRot;
    private float burnoutSpinRotO;
    private float burnoutSpinRate;

    private double breakCheckPrevX;
    private double breakCheckPrevZ;
    private boolean breakCheckPrevInitialized;

    private double recentImpactSpeed;

    private double collisionSlideX;
    private double collisionSlideZ;
    private int collisionPushCooldown;

    private double collisionSpin;

    private double tumbleAngularVelocity;
    private boolean tumbleUsesRollAxis;

    private double tumbleTargetRotationDegrees;

    private boolean chaseCarActive;
    private CarEntity chaseTargetCar;

    private enum ChaseState {
        CHARGE,
        SPIN_OUT
    }

    private ChaseState chaseState = ChaseState.CHARGE;
    private int chaseStateTimer;
    private int chaseLifetimeTicks;

    private boolean chaseWasClose;

    private float aiSteerDir;
    private float aiThrottleDir;

    private int chaseSpawnCooldown;

    private boolean gttActive;

    private int gttScore;
    private int gttNoHitTicks;

    private int gttBumpCooldown;

    private boolean responderActive;
    private Player responderTarget;
    private int responderTicks;

    private boolean chaosCarActive;
    private CarEntity chaosTargetCar;

    private enum ChaosVariant {
        STRAIGHT,
        SPINNING,
        TUMBLING
    }

    private ChaosVariant chaosVariant = ChaosVariant.STRAIGHT;

    private float chaosTravelHeading;

    private float chaosTumbleSign = 1.0F;

    private float chaosSpinSign = 1.0F;
    private double chaosSpinRateDegreesPerTick = CHAOS_SPIN_BASE_DEGREES_PER_TICK;
    private int chaosLifetimeTicks;

    private double chaosPrevDistance;
    private boolean chaosPrevDistanceInitialized;

    private boolean chaosMissed;
    private int chaosMissDelayTicks;

    private int chaosSpawnCooldown;

    private boolean townCarActive;

    private int townCurrentNodeId = -1;

    private int townPreviousNodeId = -1;

    private final java.util.Deque<Integer> townRecentNodeIds = new java.util.ArrayDeque<>();
    private static final int TOWN_RECENT_NODE_HISTORY = 3;
    private int townLifetimeTicks;

    private boolean townUseLegacySteering;

    private float townLegacyTopSpeedKmh = WaypointNode.DEFAULT_SPEED_KMH;

    private int townAiThrottleTicks;

    private int townFollowCheckTicks;
    private double townCachedAheadCarGap = Double.MAX_VALUE;

    private boolean railwayRouteActive;

    private int railwayRouteOriginId = -1;

    private java.util.List<RailwayNode> railwayRouteNodes;

    private int railwayRouteIndex;

    private int railwayStopWaitTicks;

    private int railwayServedStopId = -1;

    private boolean chauffeurActive;

    private java.util.List<Integer> chauffeurRoute;

    private int chauffeurRouteIndex;

    private java.util.UUID chauffeurPayerId;

    private int securityAlarmTicksRemaining;
    private boolean securityAlarmPreviousPhaseOn;

    private int securityAlarmVariant;
    private int securityAlarmSoundTicks;

    private static final int RECALL_INVISIBLE_TICKS = 6;
    private int recallInvisibleTicks;

    private CarEntity townCoupledTrailer;

    private boolean townTrailer;

    private int wallHitParticleCooldown;

    private int puddleSplashCooldown;

    private float steerInput;

    private boolean handbrakeHeld;

    private boolean sprintKeyHeld;

    private boolean rawForwardHeld;
    private boolean rawBackwardHeld;
    private float wheelSteer;
    private float wheelSteerO;

    private static final float MAX_FRONT_WHEEL_STEER_DEGREES = 30.0f;

    private static final float FRONT_WHEEL_STEER_SPEED_DEG_PER_TICK = 10.0f;

    private float bikeLeanPrevYaw;
    private boolean bikeLeanPrevYawInitialized;

    private double bikeLeanPrevX;
    private double bikeLeanPrevZ;
    private boolean bikeLeanPrevPosInitialized;

    private int bikeStationaryLowMovementTicks;
    private int bikeStationaryHighMovementTicks;

    private static final float MAX_BIKE_LEAN_DEGREES = 25.0f;

    private static final float BIKE_LEAN_SPEED_DEG_PER_TICK = 6.0f;

    private static final float BIKE_LEAN_SIGN = -1.0f;

    private static final float BIKE_LEAN_YAW_RATE_DEADZONE_DEG = 0.05f;

    private static final double BIKE_STATIONARY_SPEED_THRESHOLD = 0.005;

    private static final int BIKE_STATIONARY_ENTER_TICKS = 6;
    private static final int BIKE_STATIONARY_EXIT_TICKS = 2;

    private static final float PARKED_LEAN_DEGREES = 15.0f;

    private double suspensionPrevX;
    private double suspensionPrevZ;
    private boolean suspensionPrevPosInitialized;
    private float suspensionPrevForwardSpeed;
    private float suspensionPrevYaw;
    private boolean suspensionPrevYawInitialized;

    private int suspensionSettleTicksRemaining = SUSPENSION_SETTLE_TICKS;

    private boolean suspensionPendingCatchup;

    private int suspensionFrozenTicks;

    private static final int SUSPENSION_SPEED_WINDOW_TICKS = 5;
    private final float[] suspensionSpeedWindow = new float[SUSPENSION_SPEED_WINDOW_TICKS];
    private int suspensionSpeedWindowIndex;
    private int suspensionSpeedWindowCount;
    private static final int SUSPENSION_MAX_FROZEN_TICKS = 3;

    private float suspensionSmoothedForwardSpeed;
    private float suspensionSmoothedYawRate;

    private float suspensionSmoothedAccel;

    private float suspensionPitchHeld;
    private float suspensionPitchTargetPrev;

    private static final float MAX_SUSPENSION_PITCH_DEGREES = 3.0f;

    private static final float MAX_SUSPENSION_ROLL_DEGREES = 5.0f;

    private static final float SUSPENSION_ANGLE_SPEED_DEG_PER_TICK = 1.4f;

    private static final float SUSPENSION_SPEED_SMOOTHING = 0.4f;
    private static final float SUSPENSION_YAW_RATE_SMOOTHING = 0.4f;

    private static final float SUSPENSION_ACCEL_SMOOTHING = 0.2f;

    private static final float SUSPENSION_PITCH_GAIN_DEG = 45.0f;

    private static final float SUSPENSION_PITCH_WASHOUT_DECAY = 0.85f;

    private static final float SUSPENSION_SUPERCAR_FACTOR = 0.1f;
    private static final float SUSPENSION_SPORTSCAR_FACTOR = 0.67f;

    private static final float SUSPENSION_ROLL_GAIN = 0.6f;

    private static final float SUSPENSION_ACCEL_DEADZONE = 0.006f;

    private static final float SUSPENSION_STATIONARY_SPEED_THRESHOLD = 0.02f;

    private static final float SUSPENSION_YAW_RATE_DEADZONE_DEG = 0.05f;

    private static final float SUSPENSION_ROLL_SIGN = 1.0f;

    private static final int SUSPENSION_SETTLE_TICKS = 5;

    private static final float ROTOR_SPIN_DEGREES_PER_TICK = 54.0f;
    private float rotorRot;
    private float rotorRotO;

    private final List<float[]> recordedFrames = new ArrayList<>();

    private boolean tireSmokeFiredThisTick;

    private boolean clientSlipThisTick;

    private static final class PendingSmokeSpawn {
        int ticksRemaining;
        final net.minecraft.core.particles.ParticleOptions particleType;
        final double worldX;
        final double worldY;
        final double worldZ;
        final int particleCount;

        PendingSmokeSpawn(int ticksRemaining, net.minecraft.core.particles.ParticleOptions particleType,
                double worldX, double worldY, double worldZ, int particleCount) {
            this.ticksRemaining = ticksRemaining;
            this.particleType = particleType;
            this.worldX = worldX;
            this.worldY = worldY;
            this.worldZ = worldZ;
            this.particleCount = particleCount;
        }
    }

    private static final int SERVER_SMOKE_BROADCAST_DELAY_TICKS = 4;
    private final List<PendingSmokeSpawn> pendingServerSmokeSpawns = new ArrayList<>();

    private void tickPendingServerSmokeSpawns(ServerLevel serverLevel) {
        if (this.pendingServerSmokeSpawns.isEmpty()) {
            return;
        }
        Iterator<PendingSmokeSpawn> iterator = this.pendingServerSmokeSpawns.iterator();
        while (iterator.hasNext()) {
            PendingSmokeSpawn pending = iterator.next();
            if (pending.ticksRemaining <= 0) {
                serverLevel.sendParticles(pending.particleType, pending.worldX, pending.worldY, pending.worldZ,
                        pending.particleCount, 0.0, 0.0, 0.0, 0.09f);
                iterator.remove();
            } else {
                pending.ticksRemaining--;
            }
        }
    }

    private float[][] ghostFrames = new float[0][];
    private int ghostFrameIndex;
    private boolean ghostLoop;
    private boolean ghostPlaying;

    private final List<UUID> spawnedGhostIds = new ArrayList<>();

    private final List<UUID> railwayCoupledIds = new ArrayList<>();

    private final java.util.ArrayDeque<float[]> railwayTrajectoryBuffer = new java.util.ArrayDeque<>();
    private float railwayTrajectoryDistance;

    private UUID railwayHeadUuid;
    private float railwayTrailDistance;

    private static final float RAILWAY_COUPLING_GAP = 0.0f;

    private static final float RAILWAY_BUFFER_MAX_DISTANCE = 512f;

    private final java.util.ArrayDeque<float[]> clientRailwayTrajectoryBuffer = new java.util.ArrayDeque<>();
    private float clientRailwayTrajectoryDistance;

    private static final double AIRCRAFT_VERTICAL_SPEED = 0.3;

    private record DriveTuning(float maxForwardSpeed, float maxReverseSpeed, float acceleration, float braking, float maxTurnSpeedDeg) {
    }

    private static final DriveTuning CLASSIC_TUNING = new DriveTuning(0.8F, -0.15F, 0.02F, 0.04F, 4.0F);

    private static final float HYPER_MAX_FORWARD_SPEED = 300.0F / 3.6F / 20.0F;
    private static final DriveTuning HYPER_TUNING = new DriveTuning(
            HYPER_MAX_FORWARD_SPEED, -0.8F, HYPER_MAX_FORWARD_SPEED / 30.0F, HYPER_MAX_FORWARD_SPEED / 20.0F, 4.0F);

    private static final DriveTuning DRIFT_TUNING = new DriveTuning(
            CLASSIC_TUNING.maxForwardSpeed() * 1.1F, CLASSIC_TUNING.maxReverseSpeed() * 1.1F,
            CLASSIC_TUNING.acceleration(), CLASSIC_TUNING.braking(), CLASSIC_TUNING.maxTurnSpeedDeg() * 2.0F);

    private static final float DRIFT_SLIDE_MAX_TURN_DEG_PER_TICK = 2.0F;

    private static final float DRIFT_SLIDE_RECOVERY_BOOST = 3.0F;

    private static final float DRIFT_SLIDE_THROTTLE_RECOVERY_MULTIPLIER = 2.0F;

    private static final float DRIFT_SLIDE_KICK_DEGREES = 15.0F;

    private static final int DRIFT_STEER_RAMP_TICKS = 10;

    private static final int STEER_RAMP_TICKS = 3;

    private static final float DRIFT_YAW_RATE_DECAY_FACTOR = 0.92F;

    private static final float DRIFT_YAW_RATE_STOP_EPSILON = 0.05F;

    private static final float DRIFT_RECOVERY_GAP_EPSILON = 0.1F;

    private static final float COUNTER_STEER_CATCH_MULTIPLIER = 5.0F;

    private static final float REVERSE_DRIFT_KICK_MULTIPLIER = 2.2F;
    private static final float REVERSE_DRIFT_RECOVERY_SLOWDOWN = 0.35F;

    private static final float REVERSE_DRIFT_TURN_RATE_MULTIPLIER = 2.4F;

    private static final int REVERSE_DRIFT_STEER_RAMP_TICKS = 10;

    private static final float REVERSE_MIN_TOP_SPEED = 30.0F / 3.6F / 20.0F;

    private static final float REVERSE_AUTO_DRIFT_MIN_SPEED = 0.41F;

    private boolean reverseDriftAutoThisTick;

    private boolean reverseAutoDriftBlocked;
    private int reverseAutoDriftLastSteerDir;

    private static final float HEADING_RECONCILE_THRESHOLD_DEG = 1.0F;
    private static final float HEADING_RECONCILE_MAX_CORRECTION_DEG_PER_TICK = 2.0F;

    private static final float HANDBRAKE_DECEL_FRACTION = 0.15F;

    private static final float HANDBRAKE_REV_RAMP_UP = 0.15F;
    private static final float HANDBRAKE_REV_RAMP_DOWN = 0.1F;

    private static final float BURNOUT_SPIN_MAX_DEG_PER_TICK = 130.0F;
    private static final float BURNOUT_SPIN_RAMP_UP = 12.0F;
    private static final float BURNOUT_SPIN_RAMP_DOWN = 8.0F;

    private static final float WHEELIE_TRIGGER_REV_THRESHOLD = 0.999F;
    private static final int WHEELIE_HOLD_TICKS = 40;
    private static final float WHEELIE_MAX_PITCH_DEG = 20.0F;
    private static final float WHEELIE_RISE_STEP_DEG = 4.0F;

    private static final float WHEELIE_FALL_STEP_DEG = 4.0F;

    private static final float CUSTOM_ACCEL_TIME_TICKS_AT_MIN = 80.0F;
    private static final float CUSTOM_ACCEL_TIME_TICKS_AT_MAX = 6.0F;
    private static final float CUSTOM_BRAKE_TIME_TICKS_AT_MIN = 60.0F;
    private static final float CUSTOM_BRAKE_TIME_TICKS_AT_MAX = 4.0F;
    private static final float CUSTOM_HANDLING_TURN_DEG_AT_MIN = 2.0F;
    private static final float CUSTOM_HANDLING_TURN_DEG_AT_MAX = 12.0F;

    private DriveTuning customTuning() {

        float topSpeedKmh = this.townCarActive ? this.townLegacyTopSpeedKmh : this.getCustomTopSpeedKmh();
        float maxForward = topSpeedKmh / 3.6F / 20.0F;

        float maxReverse = maxForward * (CLASSIC_TUNING.maxReverseSpeed() / CLASSIC_TUNING.maxForwardSpeed());
        float accelTicks = Mth.lerp(this.getCustomAccelFraction(), CUSTOM_ACCEL_TIME_TICKS_AT_MIN, CUSTOM_ACCEL_TIME_TICKS_AT_MAX);
        float brakeTicks = Mth.lerp(this.getCustomBrakeFraction(), CUSTOM_BRAKE_TIME_TICKS_AT_MIN, CUSTOM_BRAKE_TIME_TICKS_AT_MAX);
        float turnDeg = Mth.lerp(this.getCustomHandlingFraction(), CUSTOM_HANDLING_TURN_DEG_AT_MIN, CUSTOM_HANDLING_TURN_DEG_AT_MAX);
        return new DriveTuning(maxForward, maxReverse, maxForward / accelTicks, maxForward / brakeTicks, turnDeg);
    }

    private float classicHeading;
    private float classicSpeed;

    private boolean classicHeadingInitialized;

    private float driftVelocityHeading;
    private boolean driftVelocityHeadingInitialized;

    private int driftLastSteerDir;

    private int driftSteerHoldTicks;

    private float driftYawRate;

    private int steerRampLastDir;
    private int steerRampHoldTicks;

    private float handbrakeRevIntensity;

    private boolean prevWheelieHandbrakeHeld;

    private float prevWheelieClassicSpeed;

    private boolean clientWheelieTriggerThisTick;

    private int wheelieHoldTicksRemaining;

    public CarEntity(EntityType<? extends CarEntity> entityType, Level level) {
        super(entityType, level);
        //? if <1.20.5 {
        /*this.applyLegacyStepHeight();*/
        //?}
    }

    //? if <1.19.4 {
    /*private void applyLegacyStepHeight() {
        this.maxUpStep = 1.05f;
    }*/
    //?}
    //? if >=1.19.4 {
    //? if <1.20.5 {
    /*private void applyLegacyStepHeight() {
        this.setMaxUpStep(1.05f);
    }*/
    //?}
    //?}

    public CarEntity(Level level, double x, double y, double z, String modelId) {
        this(AiObjectModEntities.CAR_ENTITY, level);
        this.setPos(x, y, z);
        this.setModelId(modelId);
        this.applyDefaultPlateDesigns();
        this.applyDefaultPartPresets();
        this.applyDefaultOtherParts();
        this.applyDefaultCategoryTuning();
    }

    private void applyDefaultCategoryTuning() {
        if (!(this.level() instanceof ServerLevel)) {
            return;
        }
        boolean appliedCustomTuning = false;
        if (this.hasVehicleCategory(VehicleCategory.EV)) {
            this.setCustomTopSpeedKmh(75.0F);
            float defaultAccelTicks = Mth.lerp(DEFAULT_CUSTOM_FRACTION, CUSTOM_ACCEL_TIME_TICKS_AT_MIN, CUSTOM_ACCEL_TIME_TICKS_AT_MAX);
            float evAccelTicks = defaultAccelTicks * 0.90F;
            float evAccelFraction = (CUSTOM_ACCEL_TIME_TICKS_AT_MIN - evAccelTicks)
                    / (CUSTOM_ACCEL_TIME_TICKS_AT_MIN - CUSTOM_ACCEL_TIME_TICKS_AT_MAX);
            this.setCustomAccelFraction(evAccelFraction);
            this.setEngineSoundId("engine_quiet_2");
            appliedCustomTuning = true;
        }
        if (this.hasVehicleCategory(VehicleCategory.SUPERCAR)) {
            this.setCustomTopSpeedKmh(180.0F);
            this.setEngineSoundId("engine_loop_6");
            appliedCustomTuning = true;
        }
        if (this.hasVehicleCategory(VehicleCategory.SPORTSCAR)) {
            this.setCustomTopSpeedKmh(80.0F);
            this.setEngineSoundId("engine_loop_3");
            appliedCustomTuning = true;
        }
        if (this.hasVehicleCategory(VehicleCategory.HELICOPTER)) {
            this.setCustomTopSpeedKmh(230.0F);
            this.setEngineSoundId("engine_helicopter_1");

            this.setCameraDistanceAdjust(3.75f);
            this.setCameraHeightAdjust(1.25f);
            appliedCustomTuning = true;
        }
        if (this.hasVehicleCategory(VehicleCategory.RAILWAY)) {

            this.setCameraDistanceAdjust(10.0f - DEFAULT_CAMERA_DISTANCE);
            this.setCameraHeightAdjust(2.0f);

            this.setWheelAdjust(0f, 0f, 0f, 0f, 0f, 0f, 0f, com.examplebett.render.WheelAttachment.NONE_STYLE);

            this.setCustomTopSpeedKmh(70.0F);
            this.setCustomAccelFraction(0.0F);
            this.setCustomHandlingFraction(0.0F);
            this.setCustomBrakeFraction(0.0F);
            this.setEngineSoundId("engine_railway_1");
            appliedCustomTuning = true;
        }
        if (this.hasVehicleCategory(VehicleCategory.MUSCLE_CAR)) {

            this.setEngineSoundId("engine_loop_9");
        }
        if (this.hasVehicleCategory(VehicleCategory.MOTORCYCLE)) {

            this.setEngineSoundId("engine_bike_1");
        }
        if (this.hasVehicleCategory(VehicleCategory.TRAILER_HEAD)) {

            this.setEngineSoundId("engine_truck_3");
            this.setCameraDistanceAdjust(10.0f - DEFAULT_CAMERA_DISTANCE);
            this.setCameraHeightAdjust(2.0f);
        }

        if (appliedCustomTuning) {
            this.setDriveMode(DriveMode.CUSTOM);
        }
    }

    private static final String GCLASS_MODEL_ID = "mercedes_benz_gclass";

    private static final String CONTAINER_TRAILER_MODEL_ID = "container_trailer";
    private static final float CONTAINER_TRAILER_EXTRA_AXLE_Z_OFFSET = -0.38f;

    private void applyDefaultOtherParts() {
        if (CONTAINER_TRAILER_MODEL_ID.equals(this.getModelId())) {
            this.setExtraRearAxle(true, CONTAINER_TRAILER_EXTRA_AXLE_Z_OFFSET);
            return;
        }
        if (!GCLASS_MODEL_ID.equals(this.getModelId())) {
            return;
        }
        this.setOtherPartEnabledIds("black_square");
        this.setOtherPartTransform("black_square", new PartTransforms.Transform12(0f, 1.3f, 1f, 93f, 0f, 1f, 2.6f, 2.6f, 1f, 1f, 5.45f, 3.4f));
    }

    private void applyDefaultPlateDesigns() {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        PlateAttachmentFootprint.Footprint footprint =
                PlateAttachmentFootprint.get(com.examplebett.compat.ModResources.getServerResourceManager(serverLevel.getServer()), this.getModelId());
        if (footprint == null) {
            return;
        }
        if (!footprint.frontPlateId().isEmpty()) {
            this.setFrontPlate(footprint.frontPlateId());
        }
        if (!footprint.rearPlateId().isEmpty()) {
            this.setRearPlate(footprint.rearPlateId());
        }
    }

    private void applyDefaultPartPresets() {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        CarPartPresets.Preset preset =
                CarPartPresets.get(com.examplebett.compat.ModResources.getServerResourceManager(serverLevel.getServer()), this.getModelId());
        if (preset == null) {
            return;
        }
        if (!preset.textPartsRaw().isEmpty()) {
            this.entityData.set(TEXT_PARTS, preset.textPartsRaw());
        }
        if (!preset.brandLogosRaw().isEmpty()) {
            this.entityData.set(BRAND_LOGOS, preset.brandLogosRaw());
        }
    }

    @Override
    //? if >=1.20.5 {
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        defineFields(builder);
    }
    //?} else
    /*protected void defineSynchedData() {
        super.defineSynchedData();
        defineFields(this.entityData);
    }*/

    //? if >=1.20.5 {
    private static void defineFields(SynchedEntityData.Builder builder) {
        builder.define(MODEL_ID, "");
        builder.define(COLOR, DEFAULT_COLOR);
        builder.define(FRONT_PLATE, "");
        builder.define(FRONT_PLATE_X, 0f);
        builder.define(FRONT_PLATE_Y, 0f);
        builder.define(FRONT_PLATE_Z, 0f);
        builder.define(FRONT_PLATE_SCALE, 0f);
        builder.define(FRONT_PLATE_YAW, 0f);
        builder.define(FRONT_PLATE_PITCH, 0f);
        builder.define(FRONT_PLATE_ROLL, 0f);
        builder.define(REAR_PLATE, "");
        builder.define(REAR_PLATE_X, 0f);
        builder.define(REAR_PLATE_Y, 0f);
        builder.define(REAR_PLATE_Z, 0f);
        builder.define(REAR_PLATE_SCALE, 0f);
        builder.define(REAR_PLATE_YAW, 0f);
        builder.define(REAR_PLATE_PITCH, 0f);
        builder.define(REAR_PLATE_ROLL, 0f);
        builder.define(TIRE_SMOKE_X, DEFAULT_TIRE_SMOKE_X);
        builder.define(TIRE_SMOKE_Y, DEFAULT_TIRE_SMOKE_Y);
        builder.define(TIRE_SMOKE_Z, DEFAULT_TIRE_SMOKE_Z);
        builder.define(TIRE_SMOKE_PARTICLE, TireSmokeParticles.DEFAULT_ID);
        builder.define(TIRE_SMOKE_PREVIEW, false);
        builder.define(DETECTED_WHEEL_TRACK_HALF_WIDTH, Float.NaN);
        builder.define(DETECTED_WHEEL_REAR_Z, Float.NaN);
        builder.define(HEADLIGHT_X, 0f);
        builder.define(HEADLIGHT_Y, 0f);
        builder.define(HEADLIGHT_Z, 0f);
        builder.define(TAILLIGHT_X, 0f);
        builder.define(TAILLIGHT_Y, 0f);
        builder.define(TAILLIGHT_Z, 0f);
        builder.define(LIGHT_MODE, LightMode.AUTO.ordinal());

        builder.define(HEADLIGHT_COLOR, "");
        builder.define(LIGHT_BLINK, false);
        builder.define(TOWN_NIGHT_LIGHTS_ACTIVE, false);
        builder.define(IS_TOWN_CAR, false);
        builder.define(CHAUFFEUR_ACTIVE, false);
        builder.define(RAILWAY_ROUTE_ACTIVE_SYNCED, false);
        builder.define(WHEEL_FRONT_Z_OFFSET, 0f);
        builder.define(WHEEL_REAR_Z_OFFSET, 0f);
        builder.define(WHEEL_TRACK_HALF_WIDTH_OFFSET, 0f);
        builder.define(WHEEL_AXLE_HEIGHT_OFFSET, 0f);
        builder.define(BODY_HEIGHT_OFFSET, 0f);
        builder.define(WHEEL_CAMBER_DEGREES, 0f);
        builder.define(WHEEL_SCALE, 0f);
        builder.define(WHEEL_WIDTH_SCALE, 0f);
        builder.define(EXTRA_REAR_AXLE_ENABLED, false);
        builder.define(EXTRA_REAR_AXLE_Z_OFFSET, 0f);
        builder.define(EXTRA_REAR_AXLE_2_ENABLED, false);
        builder.define(EXTRA_REAR_AXLE_2_Z_OFFSET, 0f);
        builder.define(LIP_STRETCH, 0f);
        builder.define(LIP_WIDTH, 0f);
        builder.define(LIP_BAND_HEIGHT_CM, DEFAULT_LIP_BAND_HEIGHT_CM);
        builder.define(LIP_FRONT_EXTEND, 0f);
        builder.define(LIMO_STRETCH_CM, 0f);

        builder.define(LIMO_ZONE_CM, 0f);
        builder.define(LIMO_CENTER_OFFSET_CM, 0f);

        builder.define(WIDTH_STRETCH_CM, 0f);
        builder.define(WIDTH_ZONE_CM, 0f);
        builder.define(WIDTH_CENTER_OFFSET_CM, 0f);
        builder.define(PASSENGER_X_ADJUST, 0f);
        builder.define(PASSENGER_Y_ADJUST, 0f);
        builder.define(PASSENGER_Z_ADJUST, 0f);
        builder.define(REAR_WING_ENABLED_IDS, "");
        builder.define(REAR_WING_TRANSFORMS, "");
        builder.define(POLICE_LIGHT_ENABLED_IDS, "");
        builder.define(POLICE_LIGHT_TRANSFORMS, "");
        builder.define(TAXI_SIGN_ENABLED_IDS, "");
        builder.define(TAXI_SIGN_TRANSFORMS, "");
        builder.define(OTHER_PART_ENABLED_IDS, "");
        builder.define(OTHER_PART_TRANSFORMS, "");
        builder.define(FRONT_BRAKE_CALIPER_ENABLED_IDS, "");
        builder.define(FRONT_BRAKE_CALIPER_TRANSFORMS, "");
        builder.define(REAR_BRAKE_CALIPER_ENABLED_IDS, "");
        builder.define(REAR_BRAKE_CALIPER_TRANSFORMS, "");
        builder.define(EXHAUST_ENABLED_IDS, "");
        builder.define(EXHAUST_TRANSFORMS, "");
        builder.define(TEXT_PARTS, "");
        builder.define(UNDER_NEON, "");
        builder.define(BRAND_LOGOS, "");
        builder.define(TOW_LEADER_ID, -1);
        builder.define(TRAILER_HEAD_ENTITY_ID, -1);
        builder.define(TRAILER_HITCH_BEHIND_SYNCED, 0f);
        builder.define(TRAILER_KINGPIN_AHEAD_SYNCED, 0f);
        builder.define(POLICE_LIGHTS_ON, false);
        builder.define(POLICE_SIREN_ON, false);
        builder.define(WHEEL_MODEL_ID_OVERRIDE, "");
        builder.define(WHEEL_COLOR_OVERRIDE, "");
        builder.define(CAR_SCALE, 0f);
        builder.define(CAMERA_DISTANCE_ADJUST, 0f);
        builder.define(CAMERA_HEIGHT_ADJUST, 0f);
        builder.define(LIVERY_OVERRIDE, "");
        builder.define(ENGINE_SOUND_ID, EngineSounds.DEFAULT_ID);
        builder.define(HORN_SOUND_ID, HornSirenSounds.DEFAULT_HORN_ID);
        builder.define(SIREN_SOUND_ID, HornSirenSounds.DEFAULT_SIREN_ID);
        builder.define(BOMB_INSTALLED, false);
        builder.define(ENGINE_PITCH_INTENSITY, DEFAULT_ENGINE_PITCH_INTENSITY);
        builder.define(ENGINE_TOP_SPEED_PITCH, DEFAULT_ENGINE_TOP_SPEED_PITCH);
        builder.define(ENGINE_LOOP_USE_FRACTION, DEFAULT_ENGINE_LOOP_USE_FRACTION);
        builder.define(RAILWAY_MIDDLE_MODEL_IDS, "");
        builder.define(RAILWAY_TAIL_MODEL_ID, "");
        builder.define(RAILWAY_CONFIGURED, false);
        builder.define(RAILWAY_COUPLED_ENTITY_IDS, "");
        builder.define(RAILWAY_FOLLOWER, false);
        builder.define(RAILWAY_HEAD_ENTITY_ID, -1);
        builder.define(RAILWAY_TRAIL_DISTANCE_SYNCED, 0f);
        builder.define(RAILWAY_HALF_LENGTH_SYNCED, 0f);
        builder.define(IS_RECORDING, false);
        builder.define(IS_GHOST, false);
        builder.define(DRIVE_MODE, DriveMode.CLASSIC.ordinal());
        builder.define(CHASE_MODE, false);
        builder.define(CHAOS_MODE, false);
        builder.define(CUSTOM_TOP_SPEED_KMH, DEFAULT_CUSTOM_TOP_SPEED_KMH);
        builder.define(CUSTOM_ACCEL_FRACTION, DEFAULT_CUSTOM_FRACTION);
        builder.define(CUSTOM_BRAKE_FRACTION, DEFAULT_CUSTOM_FRACTION);
        builder.define(CUSTOM_HANDLING_FRACTION, DEFAULT_CUSTOM_FRACTION);
        builder.define(CUSTOM_TIRE_SMOKE_ALWAYS, false);
        builder.define(DESPAWN_ENABLED, true);
        builder.define(ITEM_ICON, ITEM_ICON_VEHICLE);
        builder.define(TUMBLE_PITCH, 0f);
        builder.define(TUMBLE_ROLL, 0f);
        builder.define(WHEELIE_PITCH, 0f);
        builder.define(BIKE_LEAN_ROLL, 0f);
        builder.define(BIKE_STATIONARY, false);
        builder.define(TILT_ROLL, 0f);
        builder.define(TILT_PITCH, 0f);
        builder.define(TILT_HEIGHT_CM, 0f);
        builder.define(SUSPENSION_PITCH, 0f);
        builder.define(SUSPENSION_ROLL, 0f);
        builder.define(SECURITY_ALARM_ACTIVE, false);
    }
    //?} else
    /*private static void defineFields(SynchedEntityData builder) {
        builder.define(MODEL_ID, "");
        builder.define(COLOR, DEFAULT_COLOR);
        builder.define(FRONT_PLATE, "");
        builder.define(FRONT_PLATE_X, 0f);
        builder.define(FRONT_PLATE_Y, 0f);
        builder.define(FRONT_PLATE_Z, 0f);
        builder.define(FRONT_PLATE_SCALE, 0f);
        builder.define(FRONT_PLATE_YAW, 0f);
        builder.define(FRONT_PLATE_PITCH, 0f);
        builder.define(FRONT_PLATE_ROLL, 0f);
        builder.define(REAR_PLATE, "");
        builder.define(REAR_PLATE_X, 0f);
        builder.define(REAR_PLATE_Y, 0f);
        builder.define(REAR_PLATE_Z, 0f);
        builder.define(REAR_PLATE_SCALE, 0f);
        builder.define(REAR_PLATE_YAW, 0f);
        builder.define(REAR_PLATE_PITCH, 0f);
        builder.define(REAR_PLATE_ROLL, 0f);
        builder.define(TIRE_SMOKE_X, DEFAULT_TIRE_SMOKE_X);
        builder.define(TIRE_SMOKE_Y, DEFAULT_TIRE_SMOKE_Y);
        builder.define(TIRE_SMOKE_Z, DEFAULT_TIRE_SMOKE_Z);
        builder.define(TIRE_SMOKE_PARTICLE, TireSmokeParticles.DEFAULT_ID);
        builder.define(TIRE_SMOKE_PREVIEW, false);
        builder.define(DETECTED_WHEEL_TRACK_HALF_WIDTH, Float.NaN);
        builder.define(DETECTED_WHEEL_REAR_Z, Float.NaN);
        builder.define(HEADLIGHT_X, 0f);
        builder.define(HEADLIGHT_Y, 0f);
        builder.define(HEADLIGHT_Z, 0f);
        builder.define(TAILLIGHT_X, 0f);
        builder.define(TAILLIGHT_Y, 0f);
        builder.define(TAILLIGHT_Z, 0f);
        builder.define(LIGHT_MODE, LightMode.AUTO.ordinal());

        builder.define(HEADLIGHT_COLOR, "");
        builder.define(LIGHT_BLINK, false);
        builder.define(TOWN_NIGHT_LIGHTS_ACTIVE, false);
        builder.define(IS_TOWN_CAR, false);
        builder.define(CHAUFFEUR_ACTIVE, false);
        builder.define(RAILWAY_ROUTE_ACTIVE_SYNCED, false);
        builder.define(WHEEL_FRONT_Z_OFFSET, 0f);
        builder.define(WHEEL_REAR_Z_OFFSET, 0f);
        builder.define(WHEEL_TRACK_HALF_WIDTH_OFFSET, 0f);
        builder.define(WHEEL_AXLE_HEIGHT_OFFSET, 0f);
        builder.define(BODY_HEIGHT_OFFSET, 0f);
        builder.define(WHEEL_CAMBER_DEGREES, 0f);
        builder.define(WHEEL_SCALE, 0f);
        builder.define(WHEEL_WIDTH_SCALE, 0f);
        builder.define(EXTRA_REAR_AXLE_ENABLED, false);
        builder.define(EXTRA_REAR_AXLE_Z_OFFSET, 0f);
        builder.define(EXTRA_REAR_AXLE_2_ENABLED, false);
        builder.define(EXTRA_REAR_AXLE_2_Z_OFFSET, 0f);
        builder.define(LIP_STRETCH, 0f);
        builder.define(LIP_WIDTH, 0f);
        builder.define(LIP_BAND_HEIGHT_CM, DEFAULT_LIP_BAND_HEIGHT_CM);
        builder.define(LIP_FRONT_EXTEND, 0f);
        builder.define(LIMO_STRETCH_CM, 0f);
        builder.define(LIMO_ZONE_CM, DEFAULT_LIMO_ZONE_CM);
        builder.define(LIMO_CENTER_OFFSET_CM, 0f);
        builder.define(WIDTH_STRETCH_CM, 0f);
        builder.define(WIDTH_ZONE_CM, 0f);
        builder.define(WIDTH_CENTER_OFFSET_CM, 0f);
        builder.define(PASSENGER_X_ADJUST, 0f);
        builder.define(PASSENGER_Y_ADJUST, 0f);
        builder.define(PASSENGER_Z_ADJUST, 0f);
        builder.define(REAR_WING_ENABLED_IDS, "");
        builder.define(REAR_WING_TRANSFORMS, "");
        builder.define(POLICE_LIGHT_ENABLED_IDS, "");
        builder.define(POLICE_LIGHT_TRANSFORMS, "");
        builder.define(TAXI_SIGN_ENABLED_IDS, "");
        builder.define(TAXI_SIGN_TRANSFORMS, "");
        builder.define(OTHER_PART_ENABLED_IDS, "");
        builder.define(OTHER_PART_TRANSFORMS, "");
        builder.define(FRONT_BRAKE_CALIPER_ENABLED_IDS, "");
        builder.define(FRONT_BRAKE_CALIPER_TRANSFORMS, "");
        builder.define(REAR_BRAKE_CALIPER_ENABLED_IDS, "");
        builder.define(REAR_BRAKE_CALIPER_TRANSFORMS, "");
        builder.define(EXHAUST_ENABLED_IDS, "");
        builder.define(EXHAUST_TRANSFORMS, "");
        builder.define(TEXT_PARTS, "");
        builder.define(UNDER_NEON, "");
        builder.define(BRAND_LOGOS, "");
        builder.define(TOW_LEADER_ID, -1);
        builder.define(TRAILER_HEAD_ENTITY_ID, -1);
        builder.define(TRAILER_HITCH_BEHIND_SYNCED, 0f);
        builder.define(TRAILER_KINGPIN_AHEAD_SYNCED, 0f);
        builder.define(POLICE_LIGHTS_ON, false);
        builder.define(POLICE_SIREN_ON, false);
        builder.define(WHEEL_MODEL_ID_OVERRIDE, "");
        builder.define(WHEEL_COLOR_OVERRIDE, "");
        builder.define(CAR_SCALE, 0f);
        builder.define(CAMERA_DISTANCE_ADJUST, 0f);
        builder.define(CAMERA_HEIGHT_ADJUST, 0f);
        builder.define(LIVERY_OVERRIDE, "");
        builder.define(ENGINE_SOUND_ID, EngineSounds.DEFAULT_ID);
        builder.define(HORN_SOUND_ID, HornSirenSounds.DEFAULT_HORN_ID);
        builder.define(SIREN_SOUND_ID, HornSirenSounds.DEFAULT_SIREN_ID);
        builder.define(BOMB_INSTALLED, false);
        builder.define(ENGINE_PITCH_INTENSITY, DEFAULT_ENGINE_PITCH_INTENSITY);
        builder.define(ENGINE_TOP_SPEED_PITCH, DEFAULT_ENGINE_TOP_SPEED_PITCH);
        builder.define(ENGINE_LOOP_USE_FRACTION, DEFAULT_ENGINE_LOOP_USE_FRACTION);
        builder.define(RAILWAY_MIDDLE_MODEL_IDS, "");
        builder.define(RAILWAY_TAIL_MODEL_ID, "");
        builder.define(RAILWAY_CONFIGURED, false);
        builder.define(RAILWAY_COUPLED_ENTITY_IDS, "");
        builder.define(RAILWAY_FOLLOWER, false);
        builder.define(RAILWAY_HEAD_ENTITY_ID, -1);
        builder.define(RAILWAY_TRAIL_DISTANCE_SYNCED, 0f);
        builder.define(RAILWAY_HALF_LENGTH_SYNCED, 0f);
        builder.define(IS_RECORDING, false);
        builder.define(IS_GHOST, false);
        builder.define(DRIVE_MODE, DriveMode.CLASSIC.ordinal());
        builder.define(CHASE_MODE, false);
        builder.define(CHAOS_MODE, false);
        builder.define(CUSTOM_TOP_SPEED_KMH, DEFAULT_CUSTOM_TOP_SPEED_KMH);
        builder.define(CUSTOM_ACCEL_FRACTION, DEFAULT_CUSTOM_FRACTION);
        builder.define(CUSTOM_BRAKE_FRACTION, DEFAULT_CUSTOM_FRACTION);
        builder.define(CUSTOM_HANDLING_FRACTION, DEFAULT_CUSTOM_FRACTION);
        builder.define(CUSTOM_TIRE_SMOKE_ALWAYS, false);
        builder.define(DESPAWN_ENABLED, true);
        builder.define(ITEM_ICON, ITEM_ICON_VEHICLE);
        builder.define(TUMBLE_PITCH, 0f);
        builder.define(TUMBLE_ROLL, 0f);
        builder.define(WHEELIE_PITCH, 0f);
        builder.define(BIKE_LEAN_ROLL, 0f);
        builder.define(BIKE_STATIONARY, false);
        builder.define(TILT_ROLL, 0f);
        builder.define(TILT_PITCH, 0f);
        builder.define(TILT_HEIGHT_CM, 0f);
        builder.define(SUSPENSION_PITCH, 0f);
        builder.define(SUSPENSION_ROLL, 0f);
        builder.define(SECURITY_ALARM_ACTIVE, false);
    }*/

    public float getTumblePitchDegrees() {
        return this.entityData.get(TUMBLE_PITCH);
    }

    public float getTumbleRollDegrees() {
        return this.entityData.get(TUMBLE_ROLL);
    }

    public float getWheeliePitchDegrees() {
        return this.entityData.get(WHEELIE_PITCH);
    }

    public float getTiltRollDegrees() {
        return this.entityData.get(TILT_ROLL);
    }

    public float getTiltPitchDegrees() {
        return this.entityData.get(TILT_PITCH);
    }

    public float getTiltHeightCm() {
        return this.entityData.get(TILT_HEIGHT_CM);
    }

    public void setTiltAdjust(float rollDegrees, float pitchDegrees, float heightCm) {
        this.entityData.set(TILT_ROLL, rollDegrees);
        this.entityData.set(TILT_PITCH, pitchDegrees);
        this.entityData.set(TILT_HEIGHT_CM, heightCm);
    }

    public float getSuspensionPitchDegrees() {
        return this.entityData.get(SUSPENSION_PITCH);
    }

    public float getSuspensionRollDegrees() {
        return this.entityData.get(SUSPENSION_ROLL);
    }

    public String getModelId() {
        return this.entityData.get(MODEL_ID);
    }

    public void setModelId(String modelId) {
        this.entityData.set(MODEL_ID, modelId);
    }

    public int getColor() {
        return this.entityData.get(COLOR);
    }

    public void setColor(int color) {
        this.entityData.set(COLOR, color);
    }

    public String getFrontPlate() {
        return this.entityData.get(FRONT_PLATE);
    }

    public void setFrontPlate(String textureId) {
        this.entityData.set(FRONT_PLATE, textureId);
    }

    public float getFrontPlateXAdjust() {
        return this.entityData.get(FRONT_PLATE_X);
    }

    public float getFrontPlateYAdjust() {
        return this.entityData.get(FRONT_PLATE_Y);
    }

    public float getFrontPlateZAdjust() {
        return this.entityData.get(FRONT_PLATE_Z);
    }

    public float getFrontPlateScaleAdjust() {
        return this.entityData.get(FRONT_PLATE_SCALE);
    }

    public float getFrontPlateYawAdjust() {
        return this.entityData.get(FRONT_PLATE_YAW);
    }

    public float getFrontPlatePitchAdjust() {
        return this.entityData.get(FRONT_PLATE_PITCH);
    }

    public float getFrontPlateRollAdjust() {
        return this.entityData.get(FRONT_PLATE_ROLL);
    }

    public Vec3 getFrontPlateOffset() {
        float[] base = this.resolvePlateBase(true);
        return new Vec3(base[0] + this.getFrontPlateXAdjust(), base[1] + this.getFrontPlateYAdjust(), base[2] + this.getFrontPlateZAdjust());
    }

    public void setFrontPlateOffset(float x, float y, float z) {
        this.entityData.set(FRONT_PLATE_X, x);
        this.entityData.set(FRONT_PLATE_Y, y);
        this.entityData.set(FRONT_PLATE_Z, z);
    }

    public float getFrontPlateScale() {
        return this.resolvePlateBase(true)[3] + this.getFrontPlateScaleAdjust();
    }

    public void setFrontPlateScale(float scale) {
        this.entityData.set(FRONT_PLATE_SCALE, scale);
    }

    public float getFrontPlateYaw() {
        return this.resolvePlateBase(true)[4] + this.getFrontPlateYawAdjust();
    }

    public float getFrontPlatePitch() {
        return this.resolvePlateBase(true)[5] + this.getFrontPlatePitchAdjust();
    }

    public float getFrontPlateRoll() {
        return this.resolvePlateBase(true)[6] + this.getFrontPlateRollAdjust();
    }

    public void setFrontPlateRotation(float yaw, float pitch, float roll) {
        this.entityData.set(FRONT_PLATE_YAW, yaw);
        this.entityData.set(FRONT_PLATE_PITCH, pitch);
        this.entityData.set(FRONT_PLATE_ROLL, roll);
    }

    public String getRearPlate() {
        return this.entityData.get(REAR_PLATE);
    }

    public void setRearPlate(String textureId) {
        this.entityData.set(REAR_PLATE, textureId);
    }

    public float getRearPlateXAdjust() {
        return this.entityData.get(REAR_PLATE_X);
    }

    public float getRearPlateYAdjust() {
        return this.entityData.get(REAR_PLATE_Y);
    }

    public float getRearPlateZAdjust() {
        return this.entityData.get(REAR_PLATE_Z);
    }

    public float getRearPlateScaleAdjust() {
        return this.entityData.get(REAR_PLATE_SCALE);
    }

    public float getRearPlateYawAdjust() {
        return this.entityData.get(REAR_PLATE_YAW);
    }

    public float getRearPlatePitchAdjust() {
        return this.entityData.get(REAR_PLATE_PITCH);
    }

    public float getRearPlateRollAdjust() {
        return this.entityData.get(REAR_PLATE_ROLL);
    }

    public Vec3 getRearPlateOffset() {
        float[] base = this.resolvePlateBase(false);
        return new Vec3(base[0] + this.getRearPlateXAdjust(), base[1] + this.getRearPlateYAdjust(), base[2] + this.getRearPlateZAdjust());
    }

    public void setRearPlateOffset(float x, float y, float z) {
        this.entityData.set(REAR_PLATE_X, x);
        this.entityData.set(REAR_PLATE_Y, y);
        this.entityData.set(REAR_PLATE_Z, z);
    }

    public float getRearPlateScale() {
        return this.resolvePlateBase(false)[3] + this.getRearPlateScaleAdjust();
    }

    public void setRearPlateScale(float scale) {
        this.entityData.set(REAR_PLATE_SCALE, scale);
    }

    public float getRearPlateYaw() {
        return this.resolvePlateBase(false)[4] + this.getRearPlateYawAdjust();
    }

    public float getRearPlatePitch() {
        return this.resolvePlateBase(false)[5] + this.getRearPlatePitchAdjust();
    }

    public float getRearPlateRoll() {
        return this.resolvePlateBase(false)[6] + this.getRearPlateRollAdjust();
    }

    private float[] resolvePlateBase(boolean front) {
        com.examplebett.render.PlateAttachment attachment = this.plateAttachment;
        if (attachment != null) {
            return front
                    ? new float[]{attachment.frontX(), attachment.frontY(), attachment.frontZ(), attachment.frontScale(), attachment.frontYaw(), attachment.frontPitch(), attachment.frontRoll()}
                    : new float[]{attachment.rearX(), attachment.rearY(), attachment.rearZ(), attachment.rearScale(), attachment.rearYaw(), attachment.rearPitch(), attachment.rearRoll()};
        }
        net.minecraft.server.packs.resources.ResourceManager resourceManager =
                this.level() instanceof ServerLevel serverLevel
                        ? com.examplebett.compat.ModResources.getServerResourceManager(serverLevel.getServer())
                        : clientDimensionsResourceManager.get();
        PlateAttachmentFootprint.Footprint footprint = resourceManager != null
                ? PlateAttachmentFootprint.get(resourceManager, this.getModelId())
                : null;
        if (footprint != null) {
            return front
                    ? new float[]{footprint.frontX(), footprint.frontY(), footprint.frontZ(), footprint.frontScale(), footprint.frontYaw(), footprint.frontPitch(), footprint.frontRoll()}
                    : new float[]{footprint.rearX(), footprint.rearY(), footprint.rearZ(), footprint.rearScale(), footprint.rearYaw(), footprint.rearPitch(), footprint.rearRoll()};
        }
        return front
                ? new float[]{0f, FALLBACK_PLATE_Y, FALLBACK_FRONT_Z, FALLBACK_PLATE_SCALE, FALLBACK_PLATE_ROTATION, FALLBACK_PLATE_ROTATION, FALLBACK_PLATE_ROTATION}
                : new float[]{0f, FALLBACK_PLATE_Y, FALLBACK_REAR_Z, FALLBACK_PLATE_SCALE, FALLBACK_PLATE_ROTATION, FALLBACK_PLATE_ROTATION, FALLBACK_PLATE_ROTATION};
    }

    public void setRearPlateRotation(float yaw, float pitch, float roll) {
        this.entityData.set(REAR_PLATE_YAW, yaw);
        this.entityData.set(REAR_PLATE_PITCH, pitch);
        this.entityData.set(REAR_PLATE_ROLL, roll);
    }

    public Vec3 getTireSmokeOffset() {
        return new Vec3(this.entityData.get(TIRE_SMOKE_X), this.entityData.get(TIRE_SMOKE_Y), this.entityData.get(TIRE_SMOKE_Z));
    }

    public void setTireSmokeOffset(float x, float y, float z) {
        this.entityData.set(TIRE_SMOKE_X, x);
        this.entityData.set(TIRE_SMOKE_Y, y);
        this.entityData.set(TIRE_SMOKE_Z, z);
    }

    public String getTireSmokeParticle() {
        return this.entityData.get(TIRE_SMOKE_PARTICLE);
    }

    public void setTireSmokeParticle(String particleId) {
        this.entityData.set(TIRE_SMOKE_PARTICLE, particleId);
    }

    public boolean isTireSmokePreviewEnabled() {
        return this.entityData.get(TIRE_SMOKE_PREVIEW);
    }

    public void setTireSmokePreviewEnabled(boolean enabled) {
        this.entityData.set(TIRE_SMOKE_PREVIEW, enabled);
        this.tireSmokePreviewTickCounter = 0;
    }

    public float getHeadlightXAdjust() {
        return this.entityData.get(HEADLIGHT_X);
    }

    public float getHeadlightYAdjust() {
        return this.entityData.get(HEADLIGHT_Y);
    }

    public float getHeadlightZAdjust() {
        return this.entityData.get(HEADLIGHT_Z);
    }

    public float getTaillightXAdjust() {
        return this.entityData.get(TAILLIGHT_X);
    }

    public float getTaillightYAdjust() {
        return this.entityData.get(TAILLIGHT_Y);
    }

    public float getTaillightZAdjust() {
        return this.entityData.get(TAILLIGHT_Z);
    }

    public Vec3 getHeadlightOffset() {
        Vec3 base = this.resolveLightBase(true);
        return new Vec3(base.x + this.getHeadlightXAdjust(), base.y + this.getHeadlightYAdjust(), base.z + this.getHeadlightZAdjust());
    }

    public Vec3 getTaillightOffset() {
        Vec3 base = this.resolveLightBase(false);
        return new Vec3(base.x + this.getTaillightXAdjust(), base.y + this.getTaillightYAdjust(), base.z + this.getTaillightZAdjust());
    }

    private Vec3 resolveLightBase(boolean headlight) {
        com.examplebett.render.LightAttachment attachment = this.lightAttachment;
        if (attachment != null) {
            return headlight
                    ? new Vec3(attachment.headlightX(), attachment.headlightY(), attachment.headlightZ())
                    : new Vec3(attachment.taillightX(), attachment.taillightY(), attachment.taillightZ());
        }
        net.minecraft.server.packs.resources.ResourceManager resourceManager =
                this.level() instanceof ServerLevel serverLevel
                        ? com.examplebett.compat.ModResources.getServerResourceManager(serverLevel.getServer())
                        : clientDimensionsResourceManager.get();
        LightAttachmentFootprint.Footprint footprint = resourceManager != null
                ? LightAttachmentFootprint.get(resourceManager, this.getModelId())
                : null;
        if (footprint != null) {
            return headlight
                    ? new Vec3(footprint.headlightX(), footprint.headlightY(), footprint.headlightZ())
                    : new Vec3(footprint.taillightX(), footprint.taillightY(), footprint.taillightZ());
        }
        return headlight
                ? new Vec3(FALLBACK_HEADLIGHT_X, FALLBACK_HEADLIGHT_Y, FALLBACK_HEADLIGHT_Z)
                : new Vec3(FALLBACK_TAILLIGHT_X, FALLBACK_TAILLIGHT_Y, FALLBACK_TAILLIGHT_Z);
    }

    public LightMode getLightMode() {
        int ordinal = this.entityData.get(LIGHT_MODE);
        LightMode[] values = LightMode.values();
        return ordinal >= 0 && ordinal < values.length ? values[ordinal] : LightMode.AUTO;
    }

    public String getHeadlightColorId() {
        return this.entityData.get(HEADLIGHT_COLOR);
    }

    public String resolveHeadlightColorId() {
        String override = this.getHeadlightColorId();
        if (override != null && !override.isEmpty()) {
            return override;
        }
        com.examplebett.render.LightAttachment attachment = this.lightAttachment;
        if (attachment != null && attachment.headlightColorId() != null && !attachment.headlightColorId().isEmpty()) {
            return attachment.headlightColorId();
        }
        return HeadlightColors.DEFAULT_ID;
    }

    public boolean isLightBlinkEnabled() {
        return this.entityData.get(LIGHT_BLINK);
    }

    public void setLightBlinkEnabled(boolean enabled) {
        this.entityData.set(LIGHT_BLINK, enabled);
    }

    public void setLightAdjust(float headlightXAdjust, float headlightYAdjust, float headlightZAdjust,
                                float taillightXAdjust, float taillightYAdjust, float taillightZAdjust, LightMode mode,
                                String headlightColorId) {
        this.entityData.set(HEADLIGHT_X, headlightXAdjust);
        this.entityData.set(HEADLIGHT_Y, headlightYAdjust);
        this.entityData.set(HEADLIGHT_Z, headlightZAdjust);
        this.entityData.set(TAILLIGHT_X, taillightXAdjust);
        this.entityData.set(TAILLIGHT_Y, taillightYAdjust);
        this.entityData.set(TAILLIGHT_Z, taillightZAdjust);
        this.entityData.set(LIGHT_MODE, mode.ordinal());
        this.entityData.set(HEADLIGHT_COLOR, headlightColorId);
    }

    public boolean areLightsOn() {

        if (this.entityData.get(SECURITY_ALARM_ACTIVE)) {
            return (this.tickCount / SECURITY_ALARM_BLINK_INTERVAL_TICKS) % 2 == 0;
        }
        LightMode mode = this.getLightMode();
        if (mode == LightMode.FORCE_ON) {
            return true;
        }
        if (mode == LightMode.FORCE_OFF) {
            return false;
        }

        //? if >=26.1 {
        /*long timeOfDay = this.level().getOverworldClockTime() % 24000L;*/
        //?}
        //? if >=1.21.11 && <26.1 {
        /*long timeOfDay = this.level().getDayTime() % 24000L;*/
        //?}
        //? if >=1.16.2 && <1.21.11 {
        long timeOfDay = this.level().dayTime() % 24000L;
        //?}
        //? if <1.16.2 {
        /*long timeOfDay = this.level().getDayTime() % 24000L;*/
        //?}
        boolean isNight = timeOfDay >= 13000L && timeOfDay < 23000L;
        if (!isNight) {
            return false;
        }

        if (this.entityData.get(TOWN_NIGHT_LIGHTS_ACTIVE)) {
            return true;
        }
        return this.getControllingPassenger() != null;
    }

    public float getWheelFrontZOffset() {
        return this.entityData.get(WHEEL_FRONT_Z_OFFSET);
    }

    public float getWheelRearZOffset() {
        return this.entityData.get(WHEEL_REAR_Z_OFFSET);
    }

    public float getWheelTrackHalfWidthOffset() {
        return this.entityData.get(WHEEL_TRACK_HALF_WIDTH_OFFSET);
    }

    public float getWheelAxleHeightOffset() {
        return this.entityData.get(WHEEL_AXLE_HEIGHT_OFFSET);
    }

    public float getBodyHeightAdjust() {
        return this.entityData.get(BODY_HEIGHT_OFFSET);
    }

    public float getBodyHeightOffset() {
        return this.resolveBodyHeightBase() + this.getBodyHeightAdjust();
    }

    private float resolveBodyHeightBase() {
        com.examplebett.render.WheelAttachment attachment = this.wheelAttachment;
        if (attachment != null) {
            return attachment.bodyHeight();
        }
        net.minecraft.server.packs.resources.ResourceManager resourceManager =
                this.level() instanceof ServerLevel serverLevel
                        ? com.examplebett.compat.ModResources.getServerResourceManager(serverLevel.getServer())
                        : clientDimensionsResourceManager.get();
        WheelAttachmentFootprint.Footprint footprint = resourceManager != null
                ? WheelAttachmentFootprint.get(resourceManager, this.getModelId())
                : null;
        return footprint != null ? footprint.bodyHeight() : 0f;
    }

    public String getWheelModelIdOverride() {
        return this.entityData.get(WHEEL_MODEL_ID_OVERRIDE);
    }

    public String getWheelColorOverride() {
        return this.entityData.get(WHEEL_COLOR_OVERRIDE);
    }

    public float getWheelCamberDegrees() {
        return this.entityData.get(WHEEL_CAMBER_DEGREES);
    }

    public float getWheelScaleAdjust() {
        return this.entityData.get(WHEEL_SCALE);
    }

    public float getWheelScale() {
        float base = this.wheelAttachment != null ? this.wheelAttachment.wheelScale() : 1f;
        return base + this.getWheelScaleAdjust();
    }

    public float getWheelWidthScaleAdjust() {
        return this.entityData.get(WHEEL_WIDTH_SCALE);
    }

    public void setWheelStyleDefaultWidthScale(float wheelStyleDefaultWidthScale) {
        this.wheelStyleDefaultWidthScale = wheelStyleDefaultWidthScale;
    }

    public float getWheelWidthScale() {
        return this.wheelStyleDefaultWidthScale + this.getWheelWidthScaleAdjust();
    }

    public void setWheelWidthScale(float wheelWidthScaleAdjust) {
        this.entityData.set(WHEEL_WIDTH_SCALE, wheelWidthScaleAdjust);
    }

    public boolean isExtraRearAxleEnabled() {
        return this.entityData.get(EXTRA_REAR_AXLE_ENABLED);
    }

    public float getExtraRearAxleZOffset() {
        return this.entityData.get(EXTRA_REAR_AXLE_Z_OFFSET);
    }

    public void setExtraRearAxle(boolean enabled, float zOffset) {
        this.entityData.set(EXTRA_REAR_AXLE_ENABLED, enabled);
        this.entityData.set(EXTRA_REAR_AXLE_Z_OFFSET, zOffset);
    }

    public boolean isExtraRearAxle2Enabled() {
        return this.entityData.get(EXTRA_REAR_AXLE_2_ENABLED);
    }

    public float getExtraRearAxle2ZOffset() {
        return this.entityData.get(EXTRA_REAR_AXLE_2_Z_OFFSET);
    }

    public void setExtraRearAxle2(boolean enabled, float zOffset) {
        this.entityData.set(EXTRA_REAR_AXLE_2_ENABLED, enabled);
        this.entityData.set(EXTRA_REAR_AXLE_2_Z_OFFSET, zOffset);
    }

    public float getLipStretchAdjust() {
        return this.entityData.get(LIP_STRETCH);
    }

    public float getLipWidthAdjust() {
        return this.entityData.get(LIP_WIDTH);
    }

    public float getLipStretchScale() {
        return 1f + this.getLipStretchAdjust();
    }

    public float getLipWidthScale() {
        return 1f + this.getLipWidthAdjust();
    }

    public float getLipBandHeightCm() {
        return this.entityData.get(LIP_BAND_HEIGHT_CM);
    }

    public float getLipFrontExtendAdjust() {
        return this.entityData.get(LIP_FRONT_EXTEND);
    }

    public float getLipFrontExtendScale() {
        return 1f + this.getLipFrontExtendAdjust();
    }

    public void setLipAdjust(float lipStretchAdjust, float lipWidthAdjust, float lipBandHeightCm, float lipFrontExtendAdjust) {
        this.entityData.set(LIP_STRETCH, lipStretchAdjust);
        this.entityData.set(LIP_WIDTH, lipWidthAdjust);
        this.entityData.set(LIP_BAND_HEIGHT_CM, lipBandHeightCm);
        this.entityData.set(LIP_FRONT_EXTEND, lipFrontExtendAdjust);
    }

    public float getLimoStretchAdjust() {
        return this.entityData.get(LIMO_STRETCH_CM);
    }

    public float getLimoZoneAdjust() {
        return this.entityData.get(LIMO_ZONE_CM);
    }

    public float getLimoCenterOffsetAdjust() {
        return this.entityData.get(LIMO_CENTER_OFFSET_CM);
    }

    public float getLimoStretchCm() {
        float base = this.wheelAttachment != null ? this.wheelAttachment.limoStretchCm() : 0f;
        return base + this.getLimoStretchAdjust();
    }

    public float getLimoZoneCm() {
        float base = this.wheelAttachment != null ? this.wheelAttachment.limoZoneCm() : DEFAULT_LIMO_ZONE_CM;
        return base + this.getLimoZoneAdjust();
    }

    public float getLimoCenterOffsetCm() {
        float base = this.wheelAttachment != null ? this.wheelAttachment.limoCenterOffsetCm() : 0f;
        return base + this.getLimoCenterOffsetAdjust();
    }

    public void setLimoAdjust(float limoStretchCm, float limoZoneCm, float limoCenterOffsetCm) {
        this.entityData.set(LIMO_STRETCH_CM, limoStretchCm);
        this.entityData.set(LIMO_ZONE_CM, limoZoneCm);
        this.entityData.set(LIMO_CENTER_OFFSET_CM, limoCenterOffsetCm);
    }

    public float getWidthStretchAdjust() {
        return this.entityData.get(WIDTH_STRETCH_CM);
    }

    public float getWidthZoneAdjust() {
        return this.entityData.get(WIDTH_ZONE_CM);
    }

    public float getWidthCenterOffsetAdjust() {
        return this.entityData.get(WIDTH_CENTER_OFFSET_CM);
    }

    public float getWidthStretchCm() {
        float base = this.wheelAttachment != null ? this.wheelAttachment.widthStretchCm() : 0f;
        return base + this.getWidthStretchAdjust();
    }

    public float getWidthZoneCm() {
        float base = this.wheelAttachment != null ? this.wheelAttachment.widthZoneCm() : DEFAULT_WIDTH_ZONE_CM;
        return base + this.getWidthZoneAdjust();
    }

    public float getWidthCenterOffsetCm() {
        float base = this.wheelAttachment != null ? this.wheelAttachment.widthCenterOffsetCm() : 0f;
        return base + this.getWidthCenterOffsetAdjust();
    }

    public void setWidthAdjust(float widthStretchCm, float widthZoneCm, float widthCenterOffsetCm) {
        this.entityData.set(WIDTH_STRETCH_CM, widthStretchCm);
        this.entityData.set(WIDTH_ZONE_CM, widthZoneCm);
        this.entityData.set(WIDTH_CENTER_OFFSET_CM, widthCenterOffsetCm);
    }

    public float getPassengerXAdjust() {
        return this.entityData.get(PASSENGER_X_ADJUST);
    }

    public float getPassengerYAdjust() {
        return this.entityData.get(PASSENGER_Y_ADJUST);
    }

    public float getPassengerZAdjust() {
        return this.entityData.get(PASSENGER_Z_ADJUST);
    }

    public float getPassengerXOffset() {
        float base = this.wheelAttachment != null ? this.wheelAttachment.passengerX() : 0f;
        return base + this.getPassengerXAdjust();
    }

    public float getPassengerYOffset() {
        float base = this.wheelAttachment != null ? this.wheelAttachment.passengerY() : 0f;
        return base + this.getPassengerYAdjust();
    }

    public float getPassengerZOffset() {
        float base = this.wheelAttachment != null ? this.wheelAttachment.passengerZ() : 0f;
        return base + this.getPassengerZAdjust();
    }

    private boolean mineturismo$fakeRiderDebugLogged = false;

    public float getFakeRiderYOffset() {
        float hitboxHeight = (float) this.scaledHitboxHeight();
        float baseScale = this.getBaseCarScale();
        float bodyHeight = this.getBodyHeightOffset();
        float passengerY = this.getPassengerYOffset();
        float result = hitboxHeight + (float) (PASSENGER_HEIGHT_OFFSET_PER_BASE_SCALE * baseScale) + bodyHeight + passengerY;
        if (!this.mineturismo$fakeRiderDebugLogged) {
            this.mineturismo$fakeRiderDebugLogged = true;
            AiObjectMod.LOGGER.debug(
                    "fake rider debug [{}] scaledHitboxHeight={} baseCarScale={} bodyHeightOffset={} passengerYOffset={} result={}",
                    this.getModelId(), hitboxHeight, baseScale, bodyHeight, passengerY, result);
        }
        return result;
    }

    public void setPassengerPositionAdjust(float x, float y, float z) {
        this.entityData.set(PASSENGER_X_ADJUST, x);
        this.entityData.set(PASSENGER_Y_ADJUST, y);
        this.entityData.set(PASSENGER_Z_ADJUST, z);
    }

    public String getRearWingEnabledIds() {
        return this.entityData.get(REAR_WING_ENABLED_IDS);
    }

    public boolean isRearWingPartEnabled(String id) {
        String csv = this.getRearWingEnabledIds();
        if (csv.isEmpty()) {
            return false;
        }
        for (String part : csv.split(",")) {
            if (part.equals(id)) {
                return true;
            }
        }
        return false;
    }

    public PartTransforms.Transform12 getRearWingTransform(String partId) {
        return PartTransforms.resolve12(this.entityData.get(REAR_WING_TRANSFORMS), partId,
                new PartTransforms.Transform12(DEFAULT_REAR_WING_X, DEFAULT_REAR_WING_Y, DEFAULT_REAR_WING_Z, 0f, 0f, DEFAULT_REAR_WING_SCALE,
                        DEFAULT_OTHER_PART_STRETCH, DEFAULT_OTHER_PART_STRETCH, DEFAULT_OTHER_PART_STRETCH,
                        DEFAULT_OTHER_PART_STRETCH, DEFAULT_OTHER_PART_STRETCH, DEFAULT_OTHER_PART_STRETCH));
    }

    public void setRearWingEnabledIds(String enabledIds) {
        this.entityData.set(REAR_WING_ENABLED_IDS, enabledIds);
    }

    public void setRearWingTransform(String partId, PartTransforms.Transform12 t) {
        this.entityData.set(REAR_WING_TRANSFORMS, PartTransforms.put12(this.entityData.get(REAR_WING_TRANSFORMS), partId, t));
    }

    public String getRearWingTransformsRaw() {
        return this.entityData.get(REAR_WING_TRANSFORMS);
    }

    public String getPoliceLightEnabledIds() {
        return this.entityData.get(POLICE_LIGHT_ENABLED_IDS);
    }

    public boolean isPoliceLightPartEnabled(String id) {
        String csv = this.getPoliceLightEnabledIds();
        if (csv.isEmpty()) {
            return false;
        }
        for (String part : csv.split(",")) {
            if (part.equals(id)) {
                return true;
            }
        }
        return false;
    }

    public PartTransforms.Transform12 getPoliceLightTransform(String partId) {
        return PartTransforms.resolve12(this.entityData.get(POLICE_LIGHT_TRANSFORMS), partId,
                new PartTransforms.Transform12(DEFAULT_POLICE_LIGHT_X, DEFAULT_POLICE_LIGHT_Y, DEFAULT_POLICE_LIGHT_Z, 0f, 0f, DEFAULT_POLICE_LIGHT_SCALE,
                        DEFAULT_OTHER_PART_STRETCH, DEFAULT_OTHER_PART_STRETCH, DEFAULT_OTHER_PART_STRETCH,
                        DEFAULT_OTHER_PART_STRETCH, DEFAULT_OTHER_PART_STRETCH, DEFAULT_OTHER_PART_STRETCH));
    }

    public void setPoliceLightEnabledIds(String enabledIds) {
        this.entityData.set(POLICE_LIGHT_ENABLED_IDS, enabledIds);
    }

    public void setPoliceLightTransform(String partId, PartTransforms.Transform12 t) {
        this.entityData.set(POLICE_LIGHT_TRANSFORMS, PartTransforms.put12(this.entityData.get(POLICE_LIGHT_TRANSFORMS), partId, t));
    }

    public String getPoliceLightTransformsRaw() {
        return this.entityData.get(POLICE_LIGHT_TRANSFORMS);
    }

    public String getTaxiSignEnabledIds() {
        return this.entityData.get(TAXI_SIGN_ENABLED_IDS);
    }

    public boolean isTaxiSignPartEnabled(String id) {
        String csv = this.getTaxiSignEnabledIds();
        if (csv.isEmpty()) {
            return false;
        }
        for (String part : csv.split(",")) {
            if (part.equals(id)) {
                return true;
            }
        }
        return false;
    }

    public PartTransforms.Transform12 getTaxiSignTransform(String partId) {
        return PartTransforms.resolve12(this.entityData.get(TAXI_SIGN_TRANSFORMS), partId,
                new PartTransforms.Transform12(DEFAULT_TAXI_SIGN_X, DEFAULT_TAXI_SIGN_Y, DEFAULT_TAXI_SIGN_Z, 0f, 0f, DEFAULT_TAXI_SIGN_SCALE,
                        DEFAULT_OTHER_PART_STRETCH, DEFAULT_OTHER_PART_STRETCH, DEFAULT_OTHER_PART_STRETCH,
                        DEFAULT_OTHER_PART_STRETCH, DEFAULT_OTHER_PART_STRETCH, DEFAULT_OTHER_PART_STRETCH));
    }

    public void setTaxiSignEnabledIds(String enabledIds) {
        this.entityData.set(TAXI_SIGN_ENABLED_IDS, enabledIds);
    }

    public void setTaxiSignTransform(String partId, PartTransforms.Transform12 t) {
        this.entityData.set(TAXI_SIGN_TRANSFORMS, PartTransforms.put12(this.entityData.get(TAXI_SIGN_TRANSFORMS), partId, t));
    }

    public String getTaxiSignTransformsRaw() {
        return this.entityData.get(TAXI_SIGN_TRANSFORMS);
    }

    public String getOtherPartEnabledIds() {
        return this.entityData.get(OTHER_PART_ENABLED_IDS);
    }

    public boolean isOtherPartEnabled(String id) {
        String csv = this.getOtherPartEnabledIds();
        if (csv.isEmpty()) {
            return false;
        }
        for (String part : csv.split(",")) {
            if (part.equals(id)) {
                return true;
            }
        }
        return false;
    }

    public PartTransforms.Transform12 getOtherPartTransform(String partId) {
        return PartTransforms.resolve12(this.entityData.get(OTHER_PART_TRANSFORMS), partId, defaultOtherPartTransform(partId));
    }

    public static PartTransforms.Transform12 defaultOtherPartTransform(String partId) {
        float y = DEFAULT_OTHER_PART_Y;
        float scale = DEFAULT_OTHER_PART_SCALE;
        int boxIndex = com.examplebett.render.OtherParts.cardboardBoxIndex(partId);
        if (boxIndex >= 0) {
            scale = DEFAULT_OTHER_PART_CARDBOARD_BOX_SCALE;
            y += boxIndex * com.examplebett.render.OtherParts.CARDBOARD_BOX_HEIGHT * scale;
        } else if (com.examplebett.render.OtherParts.R34_ID.equals(partId)) {
            y += DEFAULT_OTHER_PART_R34_Y;
        }

        float angleY = com.examplebett.render.OtherParts.isSeatedCreature(partId) ? DEFAULT_SEATED_PLAYER_ANGLE_Y : 0f;
        return new PartTransforms.Transform12(DEFAULT_OTHER_PART_X, y, DEFAULT_OTHER_PART_Z, 0f, angleY, scale,
                DEFAULT_OTHER_PART_STRETCH, DEFAULT_OTHER_PART_STRETCH, DEFAULT_OTHER_PART_STRETCH,
                DEFAULT_OTHER_PART_STRETCH, DEFAULT_OTHER_PART_STRETCH, DEFAULT_OTHER_PART_STRETCH);
    }

    public void setOtherPartEnabledIds(String enabledIds) {
        this.entityData.set(OTHER_PART_ENABLED_IDS, enabledIds);
    }

    public void setOtherPartTransform(String partId, PartTransforms.Transform12 t) {
        this.entityData.set(OTHER_PART_TRANSFORMS, PartTransforms.put12(this.entityData.get(OTHER_PART_TRANSFORMS), partId, t));
    }

    public String getOtherPartTransformsRaw() {
        return this.entityData.get(OTHER_PART_TRANSFORMS);
    }

    public String getTextPartsRaw() {
        return this.entityData.get(TEXT_PARTS);
    }

    public com.examplebett.render.TextParts.Slot getTextPart(int slot) {
        com.examplebett.render.TextParts.Slot saved = com.examplebett.render.TextParts.parse(this.getTextPartsRaw())[slot];
        return saved != null ? saved : this.getTextPartDefault(slot);
    }

    public void setTextPart(int slot, com.examplebett.render.TextParts.Slot value) {
        if (slot < 0 || slot >= com.examplebett.render.TextParts.MAX_SLOTS || value == null) {
            return;
        }
        this.entityData.set(TEXT_PARTS, com.examplebett.render.TextParts.put(this.getTextPartsRaw(), slot, value));
    }

    public com.examplebett.render.TextParts.Slot getTextPartDefault(int slot) {
        float rearZ = -this.getFootprintHalfLength() - 0.02f;
        float y = 0.9f - (slot % 5) * 0.2f;
        return new com.examplebett.render.TextParts.Slot(false, "", "gothic", com.examplebett.render.TextParts.DEFAULT_COLOR,
                false, 0f, 0f, y, rearZ, 0f, 180f, 0f, 1f, 1f, 1f);
    }

    public String getUnderNeonRaw() {
        return this.entityData.get(UNDER_NEON);
    }

    public com.examplebett.render.UnderNeon.Slot getUnderNeon(int slot) {
        com.examplebett.render.UnderNeon.Slot saved = com.examplebett.render.UnderNeon.parse(this.getUnderNeonRaw())[slot];
        return saved != null ? saved : this.getUnderNeonDefault(slot);
    }

    public void setUnderNeon(int slot, com.examplebett.render.UnderNeon.Slot value) {
        if (slot < 0 || slot >= com.examplebett.render.UnderNeon.MAX_SLOTS || value == null) {
            return;
        }
        this.entityData.set(UNDER_NEON, com.examplebett.render.UnderNeon.put(this.getUnderNeonRaw(), slot, value));
    }

    public com.examplebett.render.UnderNeon.Slot getUnderNeonDefault(int slot) {
        float halfWidth = this.resolveFootprintWidthLength()[0] / 2f;
        float halfLength = this.getFootprintHalfLength();
        float y = 0.03f;
        float x = 0f;
        float z = 0f;
        float width;
        float length;
        switch (slot) {
            case com.examplebett.render.UnderNeon.SLOT_FRONT:
                z = halfLength * 0.6f;
                width = halfWidth * 2.2f;
                length = halfLength * 0.8f;
                break;
            case com.examplebett.render.UnderNeon.SLOT_REAR:
                z = -halfLength * 0.6f;
                width = halfWidth * 2.2f;
                length = halfLength * 0.8f;
                break;
            case com.examplebett.render.UnderNeon.SLOT_LEFT:
                x = halfWidth * 0.6f;
                width = halfWidth * 1.2f;
                length = halfLength * 1.8f;
                break;
            default:
                x = -halfWidth * 0.6f;
                width = halfWidth * 1.2f;
                length = halfLength * 1.8f;
                break;
        }
        return new com.examplebett.render.UnderNeon.Slot(false, com.examplebett.render.UnderNeon.DEFAULT_COLOR, 0.8f,
                x, y, z, 0f, width, length).withSize(width, length);
    }

    public String getBrandLogosRaw() {
        return this.entityData.get(BRAND_LOGOS);
    }

    public com.examplebett.render.BrandLogos.Slot getBrandLogo(int slot) {
        com.examplebett.render.BrandLogos.Slot saved = com.examplebett.render.BrandLogos.parse(this.getBrandLogosRaw())[slot];
        return saved != null ? saved : this.getBrandLogoDefault(slot);
    }

    public void setBrandLogo(int slot, com.examplebett.render.BrandLogos.Slot value) {
        if (slot < 0 || slot >= com.examplebett.render.BrandLogos.MAX_SLOTS || value == null) {
            return;
        }
        this.entityData.set(BRAND_LOGOS, com.examplebett.render.BrandLogos.put(this.getBrandLogosRaw(), slot, value));
    }

    private static final float BRAND_LOGO_DEFAULT_ABOVE_PLATE = 0.2f;

    private static final float BRAND_LOGO_DEFAULT_OTHER_STEP = 0.3f;

    public com.examplebett.render.BrandLogos.Slot getBrandLogoDefault(int slot) {
        boolean other = com.examplebett.render.BrandLogos.isOtherSlot(slot);
        boolean front = slot == com.examplebett.render.BrandLogos.SLOT_FRONT;
        Vec3 plate = front ? this.getFrontPlateOffset() : this.getRearPlateOffset();
        float yaw = front ? this.getFrontPlateYaw() : Mth.wrapDegrees(this.getRearPlateYaw() + 180f);
        float pitch = front ? this.getFrontPlatePitch() : this.getRearPlatePitch();
        float roll = front ? this.getFrontPlateRoll() : this.getRearPlateRoll();
        float above = BRAND_LOGO_DEFAULT_ABOVE_PLATE
                + (other ? (slot - com.examplebett.render.BrandLogos.FIRST_OTHER_SLOT + 1) * BRAND_LOGO_DEFAULT_OTHER_STEP : 0f);
        return new com.examplebett.render.BrandLogos.Slot(false,
                other ? com.examplebett.render.BrandLogos.DEFAULT_OTHER_LOGO : com.examplebett.render.BrandLogos.DEFAULT_LOGO,
                (float) plate.x, (float) plate.y + above, (float) plate.z,
                pitch, yaw, roll, 1f, com.examplebett.render.BrandLogos.DEFAULT_DEPTH, false);
    }

    public String getFrontBrakeCaliperEnabledIds() {
        return this.entityData.get(FRONT_BRAKE_CALIPER_ENABLED_IDS);
    }

    public PartTransforms.Transform6 getFrontBrakeCaliperTransform(String partId) {
        return PartTransforms.resolve6(this.entityData.get(FRONT_BRAKE_CALIPER_TRANSFORMS), partId, defaultBrakeCaliperTransform(partId));
    }

    public void setFrontBrakeCaliperEnabledIds(String enabledIds) {
        this.entityData.set(FRONT_BRAKE_CALIPER_ENABLED_IDS, enabledIds);
    }

    public void setFrontBrakeCaliperTransform(String partId, PartTransforms.Transform6 t) {
        this.entityData.set(FRONT_BRAKE_CALIPER_TRANSFORMS, PartTransforms.put6(this.entityData.get(FRONT_BRAKE_CALIPER_TRANSFORMS), partId, t));
    }

    public String getFrontBrakeCaliperTransformsRaw() {
        return this.entityData.get(FRONT_BRAKE_CALIPER_TRANSFORMS);
    }

    public String getRearBrakeCaliperEnabledIds() {
        return this.entityData.get(REAR_BRAKE_CALIPER_ENABLED_IDS);
    }

    public PartTransforms.Transform6 getRearBrakeCaliperTransform(String partId) {
        return PartTransforms.resolve6(this.entityData.get(REAR_BRAKE_CALIPER_TRANSFORMS), partId, defaultBrakeCaliperTransform(partId));
    }

    public void setRearBrakeCaliperEnabledIds(String enabledIds) {
        this.entityData.set(REAR_BRAKE_CALIPER_ENABLED_IDS, enabledIds);
    }

    public void setRearBrakeCaliperTransform(String partId, PartTransforms.Transform6 t) {
        this.entityData.set(REAR_BRAKE_CALIPER_TRANSFORMS, PartTransforms.put6(this.entityData.get(REAR_BRAKE_CALIPER_TRANSFORMS), partId, t));
    }

    public String getRearBrakeCaliperTransformsRaw() {
        return this.entityData.get(REAR_BRAKE_CALIPER_TRANSFORMS);
    }

    public String getExhaustEnabledIds() {
        return this.entityData.get(EXHAUST_ENABLED_IDS);
    }

    public void setExhaustEnabledIds(String enabledIds) {
        this.entityData.set(EXHAUST_ENABLED_IDS, enabledIds);
    }

    public PartTransforms.Transform6 getExhaustTransform(String partId) {
        return PartTransforms.resolve6(this.entityData.get(EXHAUST_TRANSFORMS), partId,
                com.examplebett.render.ExhaustParts.defaultTransform(partId));
    }

    public void setExhaustTransform(String partId, PartTransforms.Transform6 t) {
        this.entityData.set(EXHAUST_TRANSFORMS, PartTransforms.put6(this.entityData.get(EXHAUST_TRANSFORMS), partId, t));
    }

    public float[] clientExhaustOutlets;

    private double exhaustSmoothedSpeed;
    private double exhaustPreviousSmoothedSpeed;
    private int exhaustAcceleratingTicks;

    private int exhaustPendingPops;
    private int exhaustPopCooldown;

    private static final double EXHAUST_LIFT_OFF_MIN_SPEED_KMH = 30.0;
    private static final double EXHAUST_RANDOM_MIN_SPEED_KMH = 20.0;

    private static final float EXHAUST_RANDOM_POP_CHANCE = 1f / 60f;

    private static final int EXHAUST_LIFT_OFF_MIN_ACCEL_TICKS = 6;

    private static final float EXHAUST_REV_TRIGGER = 0.7f;
    private static final float EXHAUST_REV_RANDOM_POP_CHANCE = 1f / 25f;
    private float exhaustPreviousRev;
    private boolean exhaustRevArmed;

    private void tickExhaustAfterfire() {
        double dx = ModEntity.getX(this) - this.xo;
        double dz = ModEntity.getZ(this) - this.zo;
        double speedKmh = Math.sqrt(dx * dx + dz * dz) * 20.0 * 3.6;
        this.exhaustPreviousSmoothedSpeed = this.exhaustSmoothedSpeed;
        this.exhaustSmoothedSpeed += (speedKmh - this.exhaustSmoothedSpeed) * 0.3;
        float rev = this.handbrakeRevIntensity;
        boolean revReleased = false;
        if (rev >= EXHAUST_REV_TRIGGER) {
            this.exhaustRevArmed = true;
        } else if (this.exhaustRevArmed && rev < this.exhaustPreviousRev) {
            this.exhaustRevArmed = false;
            revReleased = true;
        }
        this.exhaustPreviousRev = rev;

        if (this.getExhaustEnabledIds().isEmpty() || this.clientExhaustOutlets == null) {
            this.exhaustPendingPops = 0;
            return;
        }
        if (revReleased) {
            this.exhaustPendingPops = 2 + this.random.nextInt(3);
            this.exhaustPopCooldown = 0;
        }

        boolean decelerating = this.exhaustSmoothedSpeed < this.exhaustPreviousSmoothedSpeed - 0.15;
        if (!decelerating) {
            this.exhaustAcceleratingTicks++;
        } else {
            if (this.exhaustAcceleratingTicks >= EXHAUST_LIFT_OFF_MIN_ACCEL_TICKS
                    && this.exhaustSmoothedSpeed >= EXHAUST_LIFT_OFF_MIN_SPEED_KMH) {
                this.exhaustPendingPops = 2 + this.random.nextInt(3);
                this.exhaustPopCooldown = 0;
            }
            this.exhaustAcceleratingTicks = 0;
        }

        if (this.exhaustPopCooldown > 0) {
            this.exhaustPopCooldown--;
        }
        if (this.exhaustPendingPops > 0 && this.exhaustPopCooldown == 0) {
            this.exhaustPendingPops--;
            this.exhaustPopCooldown = 2 + this.random.nextInt(4);
            this.spawnExhaustPop(1.0f);
        } else if (this.exhaustSmoothedSpeed >= EXHAUST_RANDOM_MIN_SPEED_KMH
                && this.random.nextFloat() < EXHAUST_RANDOM_POP_CHANCE) {
            this.spawnExhaustPop(0.7f);
        } else if (rev >= EXHAUST_REV_TRIGGER && this.random.nextFloat() < EXHAUST_REV_RANDOM_POP_CHANCE) {
            this.spawnExhaustPop(0.7f);
        }
    }

    public void spawnExhaustPop(float intensity) {
        this.spawnExhaustPop(intensity, true);
    }

    public void spawnExhaustPop(float intensity, boolean withSound) {
        float[] outlets = this.clientExhaustOutlets;
        if (outlets == null || !this.level().isClientSide()) {
            return;
        }
        String enabledId = this.getExhaustEnabledIds();
        float flameScale = enabledId.isEmpty() ? 1f : Math.max(0.1f, this.getExhaustTransform(enabledId).scale());
        String particle = com.examplebett.render.ExhaustParts.particleOf(enabledId);
        Vec3 carMotion = this.getDeltaMovement();
        for (int i = 0; i + 5 < outlets.length; i += 6) {
            double x = ModEntity.getX(this) + outlets[i];
            double y = ModEntity.getY(this) + outlets[i + 1];
            double z = ModEntity.getZ(this) + outlets[i + 2];
            float dirX = outlets[i + 3];
            float dirY = outlets[i + 4];
            float dirZ = outlets[i + 5];
            int count = Math.max(2, Math.round(6 * intensity * flameScale));
            switch (particle) {

                //? if >=1.16 {
                case "soul_fire_flame" -> {
                    this.spawnExhaustParticles(net.minecraft.core.particles.ParticleTypes.SOUL_FIRE_FLAME, count, x, y, z, dirX, dirY, dirZ, flameScale, carMotion);
                    this.spawnExhaustParticles(net.minecraft.core.particles.ParticleTypes.SMOKE, 1, x, y, z, dirX, dirY, dirZ, 0.4f, carMotion);
                }
                //?} else {
                /*case "soul_fire_flame" -> {
                    this.spawnExhaustParticles(new net.minecraft.core.particles.DustParticleOptions(0.35f, 0.85f, 1.0f, 1.0f), count, x, y, z, dirX, dirY, dirZ, flameScale, carMotion);
                    this.spawnExhaustParticles(net.minecraft.core.particles.ParticleTypes.SMOKE, 1, x, y, z, dirX, dirY, dirZ, 0.4f, carMotion);
                }*/
                //?}
                case "lava" -> {
                    this.spawnExhaustParticles(net.minecraft.core.particles.ParticleTypes.LAVA, Math.max(1, count / 2), x, y, z, dirX, dirY, dirZ, flameScale, carMotion);
                    this.spawnExhaustParticles(net.minecraft.core.particles.ParticleTypes.SMOKE, 1, x, y, z, dirX, dirY, dirZ, 0.4f, carMotion);
                }
                case "fishing" -> this.spawnExhaustParticles(net.minecraft.core.particles.ParticleTypes.FISHING, count, x, y, z, dirX, dirY, dirZ, flameScale, carMotion);
                case "dolphin" -> this.spawnExhaustParticles(net.minecraft.core.particles.ParticleTypes.DOLPHIN, count * 2, x, y, z, dirX, dirY, dirZ, flameScale, carMotion);
                case "smoke" -> this.spawnExhaustParticles(net.minecraft.core.particles.ParticleTypes.SMOKE, count, x, y, z, dirX, dirY, dirZ, flameScale, carMotion);
                case "campfire_cosy_smoke" -> this.spawnExhaustParticles(net.minecraft.core.particles.ParticleTypes.CAMPFIRE_COSY_SMOKE, Math.max(1, count / 3), x, y, z, dirX, dirY, dirZ, flameScale * 0.4f, carMotion);

                case "falling_dust" -> this.spawnExhaustParticles(new net.minecraft.core.particles.BlockParticleOption(
                                net.minecraft.core.particles.ParticleTypes.FALLING_DUST, net.minecraft.world.level.block.Blocks.COAL_BLOCK.defaultBlockState()),
                        count, x, y, z, dirX, dirY, dirZ, flameScale, carMotion);
                case "witch" -> this.spawnExhaustParticles(net.minecraft.core.particles.ParticleTypes.WITCH, count * 2, x, y, z, dirX, dirY, dirZ, flameScale, carMotion);

                case "flash" -> this.spawnExhaustParticles(
                        //? if >=1.21.9 {
                        net.minecraft.core.particles.ColorParticleOption.create(net.minecraft.core.particles.ParticleTypes.FLASH, 0xFFFFA040),
                        //?} else
                        /*net.minecraft.core.particles.ParticleTypes.FLASH,*/
                        1, x, y, z, dirX, dirY, dirZ, 0f, carMotion);

                case "explosion" -> this.level().addParticle(net.minecraft.core.particles.ParticleTypes.EXPLOSION, x, y, z,
                        Math.max(0.0, 1.0 - flameScale * 0.5), 0.0, 0.0);
                default -> {
                    this.spawnExhaustParticles(net.minecraft.core.particles.ParticleTypes.FLAME, count, x, y, z, dirX, dirY, dirZ, flameScale, carMotion);
                    this.spawnExhaustParticles(net.minecraft.core.particles.ParticleTypes.SMOKE, 1, x, y, z, dirX, dirY, dirZ, 0.4f, carMotion);
                }
            }

            if (withSound && i == 0) {
                this.level().playLocalSound(x, y, z, net.minecraft.sounds.SoundEvents.FIREWORK_ROCKET_BLAST,
                        net.minecraft.sounds.SoundSource.NEUTRAL, 0.5f + 0.4f * intensity,
                        0.6f + this.random.nextFloat() * 0.3f, false);
            }
        }
    }

    private void spawnExhaustParticles(net.minecraft.core.particles.ParticleOptions particle, int count,
            double x, double y, double z, float dirX, float dirY, float dirZ, float strength, Vec3 carMotion) {
        for (int n = 0; n < count; n++) {
            double speed = (0.12 + this.random.nextDouble() * 0.12) * strength;
            double spread = 0.04 * strength;
            this.level().addParticle(particle, x, y, z,
                    carMotion.x * 0.5 + dirX * speed + (this.random.nextDouble() - 0.5) * spread,
                    dirY * speed + (this.random.nextDouble() - 0.5) * spread,
                    carMotion.z * 0.5 + dirZ * speed + (this.random.nextDouble() - 0.5) * spread);
        }
    }

    private UUID towLeaderUuid;

    public static final double TOW_ROPE_LENGTH = 3.0;

    private static final double TOW_ATTACH_MAX_DISTANCE = 16.0;

    private static final float TOW_HEADING_SMOOTHING = 0.25f;

    private static final java.util.Map<UUID, Integer> TOW_PENDING_BY_PLAYER = new java.util.HashMap<>();

    public float[] clientTowFrontHook;
    public float[] clientTowRearHook;

    public long clientTowRearHookWantedTime = Long.MIN_VALUE;

    public int getTowLeaderEntityId() {
        return this.entityData.get(TOW_LEADER_ID);
    }

    private void handleTowRopeUse(ServerLevel serverLevel, Player player, InteractionHand hand) {
        UUID playerId = player.getUUID();
        Integer pendingId = TOW_PENDING_BY_PLAYER.get(playerId);
        if (pendingId == null && this.towLeaderUuid != null) {
            this.setTowLeader(null);
            giveTowRopeBack(player);
            com.examplebett.compat.ModPlayer.displayActionBar(player, com.examplebett.compat.ModText.translatable("item.mineturismo.tow_rope.removed"));
            return;
        }
        CarEntity follower = this.findTowFollower(serverLevel);
        if (pendingId == null && follower != null) {
            follower.setTowLeader(null);
            giveTowRopeBack(player);
            com.examplebett.compat.ModPlayer.displayActionBar(player, com.examplebett.compat.ModText.translatable("item.mineturismo.tow_rope.removed"));
            return;
        }
        if (pendingId != null) {
            TOW_PENDING_BY_PLAYER.remove(playerId);
            Entity pendingEntity = serverLevel.getEntity(pendingId);
            if (pendingEntity == this) {
                com.examplebett.compat.ModPlayer.displayActionBar(player, com.examplebett.compat.ModText.translatable("item.mineturismo.tow_rope.cancelled"));
                return;
            }
            if (!(pendingEntity instanceof CarEntity towed) || !towed.isAlive()
                    || towed.distanceTo(this) > TOW_ATTACH_MAX_DISTANCE) {
                com.examplebett.compat.ModPlayer.displayActionBar(player, com.examplebett.compat.ModText.translatable("item.mineturismo.tow_rope.too_far"));
                return;
            }
            if (towed.towLeaderUuid != null || follower != null) {
                com.examplebett.compat.ModPlayer.displayActionBar(player, com.examplebett.compat.ModText.translatable("item.mineturismo.tow_rope.already_towing"));
                return;
            }
            if (this.isTowedThrough(serverLevel, towed)) {
                com.examplebett.compat.ModPlayer.displayActionBar(player, com.examplebett.compat.ModText.translatable("item.mineturismo.tow_rope.cycle"));
                return;
            }
            towed.setTowLeader(this);
            if (!com.examplebett.compat.ModPlayer.hasInstabuild(player)) {
                player.getItemInHand(hand).shrink(1);
            }
            com.examplebett.compat.ModPlayer.displayActionBar(player, com.examplebett.compat.ModText.translatable("item.mineturismo.tow_rope.attached"));
            return;
        }
        TOW_PENDING_BY_PLAYER.put(playerId, this.getId());
        com.examplebett.compat.ModPlayer.displayActionBar(player, com.examplebett.compat.ModText.translatable("item.mineturismo.tow_rope.selected"));
    }

    private void setTowLeader(CarEntity leader) {
        this.towLeaderUuid = leader != null ? leader.getUUID() : null;
        this.entityData.set(TOW_LEADER_ID, leader != null ? leader.getId() : -1);
    }

    private static void giveTowRopeBack(Player player) {
        if (com.examplebett.compat.ModPlayer.hasInstabuild(player)) {
            return;
        }
        ItemStack rope = new ItemStack(AiObjectModItems.TOW_ROPE);
        if (!com.examplebett.compat.ModPlayer.getInventory(player).add(rope)) {
            player.drop(rope, false);
        }
    }

    private static void dropTowRope(ServerLevel serverLevel, Entity at) {
        serverLevel.addFreshEntity(new net.minecraft.world.entity.item.ItemEntity(serverLevel,
                ModEntity.getX(at), ModEntity.getY(at) + 0.5, ModEntity.getZ(at), new ItemStack(AiObjectModItems.TOW_ROPE)));
    }

    private CarEntity findTowFollower(ServerLevel serverLevel) {
        double r = TOW_ATTACH_MAX_DISTANCE * 4;
        AABB box = new AABB(ModEntity.getX(this) - r, ModEntity.getY(this) - r, ModEntity.getZ(this) - r,
                ModEntity.getX(this) + r, ModEntity.getY(this) + r, ModEntity.getZ(this) + r);
        UUID myId = this.getUUID();
        for (CarEntity other : serverLevel.getEntitiesOfClass(CarEntity.class, box, c -> myId.equals(c.towLeaderUuid))) {
            return other;
        }
        return null;
    }

    private boolean isTowedThrough(ServerLevel serverLevel, CarEntity target) {
        CarEntity current = this;
        for (int depth = 0; depth < 32 && current.towLeaderUuid != null; depth++) {
            if (current.towLeaderUuid.equals(target.getUUID())) {
                return true;
            }
            Entity next = serverLevel.getEntity(current.towLeaderUuid);
            if (!(next instanceof CarEntity nextCar)) {
                return false;
            }
            current = nextCar;
        }
        return false;
    }

    private void updateTow(ServerLevel serverLevel) {
        if (this.towLeaderUuid == null) {
            if (this.entityData.get(TOW_LEADER_ID) != -1) {
                this.entityData.set(TOW_LEADER_ID, -1);
            }
            return;
        }
        Entity leaderEntity = serverLevel.getEntity(this.towLeaderUuid);
        if (!(leaderEntity instanceof CarEntity leader) || !leader.isAlive()) {

            this.entityData.set(TOW_LEADER_ID, -1);
            return;
        }
        this.entityData.set(TOW_LEADER_ID, leader.getId());
        if (this.getControllingPassenger() instanceof Player || this.townCarActive || this.railwayRouteActive
                || this.chaseCarActive || this.chaosCarActive || this.responderActive) {
            return;
        }
        double dx = ModEntity.getX(leader) - ModEntity.getX(this);
        double dz = ModEntity.getZ(leader) - ModEntity.getZ(this);
        double dist = Math.sqrt(dx * dx + dz * dz);
        double maxDist = this.resolveFootprintWidthLength()[1] / 2.0 + leader.resolveFootprintWidthLength()[1] / 2.0 + TOW_ROPE_LENGTH;
        if (dist <= maxDist || dist < 1.0E-4) {
            return;
        }
        double excess = dist - maxDist;
        double dirX = dx / dist;
        double dirZ = dz / dist;

        this.move(MoverType.SELF, new Vec3(dirX * excess, 0.0, dirZ * excess));
        float targetHeading = (float) Math.toDegrees(Math.atan2(-dirX, dirZ));
        float heading = this.classicHeadingInitialized
                ? Mth.rotLerp(TOW_HEADING_SMOOTHING, this.classicHeading, targetHeading)
                : targetHeading;
        this.classicHeading = heading;
        this.classicHeadingInitialized = true;
        this.setRot(-heading, 0.0F);
    }

    private UUID trailerHeadUuid;

    private static final float TRAILER_HITCH_BEHIND_FRACTION = 0.25f;
    private static final float TRAILER_KINGPIN_SETBACK = 1.2f;

    private static final double TRAILER_COUPLE_MAX_DISTANCE = 4.0;
    private static final float TRAILER_COUPLE_MAX_YAW_DIFF = 90.0f;

    public int getTrailerHeadEntityId() {
        return this.entityData.get(TRAILER_HEAD_ENTITY_ID);
    }

    private boolean isCoupledTrailer() {
        return this.trailerHeadUuid != null || this.entityData.get(TRAILER_HEAD_ENTITY_ID) >= 0;
    }

    private static double[] mineturismo$forwardOf(float yawDegrees) {
        float yawRad = (float) Math.toRadians(yawDegrees);
        return new double[]{Mth.sin(yawRad), Mth.cos(yawRad)};
    }

    private float trailerHitchBehindDistance() {
        return this.resolveFootprintWidthLength()[1] * TRAILER_HITCH_BEHIND_FRACTION;
    }

    private float trailerKingpinAheadDistance() {
        return Math.max(0.5f, this.resolveFootprintWidthLength()[1] / 2.0f - TRAILER_KINGPIN_SETBACK);
    }

    public CarEntity findCoupledTrailer() {
        int myId = this.getId();
        double r = 24.0;
        AABB box = new AABB(ModEntity.getX(this) - r, ModEntity.getY(this) - r, ModEntity.getZ(this) - r,
                ModEntity.getX(this) + r, ModEntity.getY(this) + r, ModEntity.getZ(this) + r);
        for (CarEntity other : this.level().getEntitiesOfClass(CarEntity.class, box,
                c -> c != this && c.entityData.get(TRAILER_HEAD_ENTITY_ID) == myId)) {
            return other;
        }
        return null;
    }

    public CarEntity findCouplableTrailer() {
        if (!this.hasVehicleCategory(VehicleCategory.TRAILER_HEAD)) {
            return null;
        }
        double[] forward = mineturismo$forwardOf(this.getYRot());
        float hitchBehind = this.trailerHitchBehindDistance();
        double hitchX = ModEntity.getX(this) - forward[0] * hitchBehind;
        double hitchZ = ModEntity.getZ(this) - forward[1] * hitchBehind;
        double r = 24.0;
        AABB box = new AABB(hitchX - r, ModEntity.getY(this) - 4.0, hitchZ - r, hitchX + r, ModEntity.getY(this) + 4.0, hitchZ + r);
        CarEntity best = null;
        double bestDistSq = TRAILER_COUPLE_MAX_DISTANCE * TRAILER_COUPLE_MAX_DISTANCE;
        for (CarEntity other : this.level().getEntitiesOfClass(CarEntity.class, box,
                c -> c != this && c.isAlive() && !c.isCoupledTrailer() && c.hasVehicleCategory(VehicleCategory.TRAILER))) {
            if (Math.abs(Mth.wrapDegrees(other.getYRot() - this.getYRot())) > TRAILER_COUPLE_MAX_YAW_DIFF) {
                continue;
            }
            double[] otherForward = mineturismo$forwardOf(other.getYRot());
            float kingpinAhead = other.trailerKingpinAheadDistance();
            double kingpinX = ModEntity.getX(other) + otherForward[0] * kingpinAhead;
            double kingpinZ = ModEntity.getZ(other) + otherForward[1] * kingpinAhead;
            double dx = kingpinX - hitchX;
            double dz = kingpinZ - hitchZ;
            double distSq = dx * dx + dz * dz;
            double centerAlong = (ModEntity.getX(other) - ModEntity.getX(this)) * forward[0]
                    + (ModEntity.getZ(other) - ModEntity.getZ(this)) * forward[1];
            if (distSq <= bestDistSq && centerAlong < 0) {
                best = other;
                bestDistSq = distSq;
            }
        }
        return best;
    }

    public void toggleTrailerCoupling(Player player) {
        if (!this.hasVehicleCategory(VehicleCategory.TRAILER_HEAD)) {
            return;
        }
        CarEntity coupled = this.findCoupledTrailer();
        if (coupled != null) {
            coupled.setTrailerHead(null);
            com.examplebett.compat.ModPlayer.displayActionBar(player, com.examplebett.compat.ModText.translatable("gui.mineturismo.trailer.uncoupled"));
            return;
        }
        CarEntity trailer = this.findCouplableTrailer();
        if (trailer == null) {
            return;
        }
        trailer.setTrailerHead(this);
        trailer.updateTrailerFollow(this);
        com.examplebett.compat.ModPlayer.displayActionBar(player, com.examplebett.compat.ModText.translatable("gui.mineturismo.trailer.coupled"));
    }

    private void setTrailerHead(CarEntity head) {
        this.trailerHeadUuid = head != null ? head.getUUID() : null;
        this.entityData.set(TRAILER_HEAD_ENTITY_ID, head != null ? head.getId() : -1);
        if (head == null) {

            this.setDeltaMovement(Vec3.ZERO);
            this.classicSpeed = 0.0F;
        }
    }

    private void updateTrailerFollow(CarEntity head) {
        double[] headForward = mineturismo$forwardOf(head.getYRot());
        float hitchBehind = head.trailerHitchBehindDistance();
        float kingpinAhead = this.trailerKingpinAheadDistance();
        double hitchX = ModEntity.getX(head) - headForward[0] * hitchBehind;
        double hitchZ = ModEntity.getZ(head) - headForward[1] * hitchBehind;
        double dx = ModEntity.getX(this) - hitchX;
        double dz = ModEntity.getZ(this) - hitchZ;
        double len = Math.sqrt(dx * dx + dz * dz);
        if (len < 1.0E-4) {
            dx = -headForward[0];
            dz = -headForward[1];
            len = 1.0;
        }
        dx /= len;
        dz /= len;

        float newYaw = (float) Math.toDegrees(Math.atan2(-dx, -dz));
        this.setPos(hitchX + dx * kingpinAhead, ModEntity.getY(head), hitchZ + dz * kingpinAhead);
        this.setRot(newYaw, 0f);
        this.yRotO = this.yBodyRot = this.yHeadRot = newYaw;

        this.classicHeading = -newYaw;
        this.classicHeadingInitialized = true;
        this.setDeltaMovement(Vec3.ZERO);

        this.entityData.set(TRAILER_HITCH_BEHIND_SYNCED, hitchBehind);
        this.entityData.set(TRAILER_KINGPIN_AHEAD_SYNCED, kingpinAhead);
    }

    private void mineturismo$writeCoupledTrailerTag(CompoundTag tag) {
        tag.remove("TrailerHeadUuid");
        if (!(this.level() instanceof ServerLevel) || !this.hasVehicleCategory(VehicleCategory.TRAILER_HEAD)) {
            return;
        }
        CarEntity trailer = this.findCoupledTrailer();
        if (trailer != null) {
            tag.put("CoupledTrailer", trailer.exportCustomizationTag());
        }
    }

    private CarEntity mineturismo$readCoupledTrailerTag(CompoundTag tag) {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return null;
        }
        CompoundTag trailerTag = mineturismo$rawCompoundOr(tag, "CoupledTrailer");
        String modelId = mineturismo$rawStringOr(trailerTag, "ModelId", "");
        if (modelId.isEmpty()) {
            return null;
        }
        double[] forward = mineturismo$forwardOf(this.getYRot());
        CarEntity trailer = new CarEntity(serverLevel, ModEntity.getX(this), ModEntity.getY(this), ModEntity.getZ(this), modelId);
        trailer.setRot(this.getYRot(), 0f);
        trailer.importCustomizationTag(trailerTag);

        float distance = this.trailerHitchBehindDistance() + trailer.trailerKingpinAheadDistance();
        trailer.setPos(ModEntity.getX(this) - forward[0] * distance, ModEntity.getY(this), ModEntity.getZ(this) - forward[1] * distance);
        trailer.setTrailerHead(this);
        trailer.updateTrailerFollow(this);
        serverLevel.addFreshEntity(trailer);
        return trailer;
    }

    public void discardCoupledTrailer() {
        if (!(this.level() instanceof ServerLevel) || !this.hasVehicleCategory(VehicleCategory.TRAILER_HEAD)) {
            return;
        }
        CarEntity trailer = this.findCoupledTrailer();
        if (trailer != null) {
            trailer.discard();
        }
    }

    private void tickTrailerCoupling(ServerLevel serverLevel) {
        if (this.trailerHeadUuid == null) {
            if (this.entityData.get(TRAILER_HEAD_ENTITY_ID) != -1) {
                this.entityData.set(TRAILER_HEAD_ENTITY_ID, -1);
            }
            return;
        }
        Entity headEntity = serverLevel.getEntity(this.trailerHeadUuid);
        if (!(headEntity instanceof CarEntity head) || !head.isAlive()) {
            this.entityData.set(TRAILER_HEAD_ENTITY_ID, -1);
            return;
        }
        this.entityData.set(TRAILER_HEAD_ENTITY_ID, head.getId());
        this.updateTrailerFollow(head);
    }

    private float[] mineturismo$computeTrailerRenderOverride(float partialTick) {
        int headId = this.entityData.get(TRAILER_HEAD_ENTITY_ID);
        if (headId < 0) {
            return null;
        }
        Entity headEntity = this.level().getEntity(headId);
        if (!(headEntity instanceof CarEntity head)) {
            return null;
        }

        //? if >=1.21.2 {
        float headYaw = head.getYRot(partialTick);
        //?} else
        /*float headYaw = Mth.rotLerp(partialTick, head.yRotO, head.getYRot());*/
        //? if >=1.16.2 {
        Vec3 headPos = head.getPosition(partialTick);
        Vec3 vanillaPos = this.getPosition(partialTick);
        //?} else {
        /*Vec3 headPos = new Vec3(
                Mth.lerp(partialTick, head.xo, ModEntity.getX(head)),
                Mth.lerp(partialTick, head.yo, ModEntity.getY(head)),
                Mth.lerp(partialTick, head.zo, ModEntity.getZ(head)));
        Vec3 vanillaPos = new Vec3(
                Mth.lerp(partialTick, this.xo, ModEntity.getX(this)),
                Mth.lerp(partialTick, this.yo, ModEntity.getY(this)),
                Mth.lerp(partialTick, this.zo, ModEntity.getZ(this)));*/
        //?}
        double[] headForward = mineturismo$forwardOf(headYaw);
        float hitchBehind = this.entityData.get(TRAILER_HITCH_BEHIND_SYNCED);
        float kingpinAhead = this.entityData.get(TRAILER_KINGPIN_AHEAD_SYNCED);
        double hitchX = headPos.x - headForward[0] * hitchBehind;
        double hitchZ = headPos.z - headForward[1] * hitchBehind;
        double dx = vanillaPos.x - hitchX;
        double dz = vanillaPos.z - hitchZ;
        double len = Math.sqrt(dx * dx + dz * dz);
        if (len < 1.0E-4) {
            return null;
        }
        dx /= len;
        dz /= len;
        float yaw = (float) Math.toDegrees(Math.atan2(-dx, -dz));
        return new float[]{
                (float) (hitchX + dx * kingpinAhead - vanillaPos.x),
                (float) (headPos.y - vanillaPos.y),
                (float) (hitchZ + dz * kingpinAhead - vanillaPos.z),
                yaw};
    }

    public boolean isPoliceLightsOn() {
        return this.entityData.get(POLICE_LIGHTS_ON);
    }

    public void setPoliceLightsOn(boolean on) {
        this.entityData.set(POLICE_LIGHTS_ON, on);
    }

    public boolean isPoliceSirenOn() {
        return this.entityData.get(POLICE_SIREN_ON);
    }

    public void setPoliceSirenOn(boolean on) {
        this.entityData.set(POLICE_SIREN_ON, on);
    }

    public String getHornSoundId() {
        return this.entityData.get(HORN_SOUND_ID);
    }

    public void setHornSoundId(String id) {

        this.entityData.set(HORN_SOUND_ID, HornSirenSounds.hornById(id).id());
    }

    public String getSirenSoundId() {
        return this.entityData.get(SIREN_SOUND_ID);
    }

    public void setSirenSoundId(String id) {
        this.entityData.set(SIREN_SOUND_ID, HornSirenSounds.sirenById(id).id());
    }

    private static final int[] BOMB_SLOW_BEEP_TICKS = {0, 20, 40};
    private static final int BOMB_RAPID_START_TICK = 44;
    private static final int BOMB_RAPID_INTERVAL_TICKS = 2;
    private static final int BOMB_EXPLODE_TICK = 60;
    private static final int BOMB_BURN_TICKS = 100;
    private static final int BOMB_FIRE_SPOT_COUNT = 7;

    private UUID bombOwnerUuid;

    private int bombTicks = -1;
    private final List<Vec3> bombFireSpots = new ArrayList<>();

    public boolean isBombInstalled() {
        return this.entityData.get(BOMB_INSTALLED);
    }

    public void setBombInstalled(boolean installed, UUID owner) {
        this.entityData.set(BOMB_INSTALLED, installed);
        this.bombOwnerUuid = installed ? owner : null;
    }

    public boolean isBombArmedBy(UUID player) {
        return this.isBombInstalled() && this.bombTicks < 0 && player.equals(this.bombOwnerUuid);
    }

    public void detonateBomb() {
        if (this.bombTicks >= 0) {
            return;
        }
        this.bombTicks = 0;
        this.entityData.set(BOMB_INSTALLED, false);
    }

    public boolean isBombBurning() {
        return this.bombTicks >= BOMB_EXPLODE_TICK;
    }

    private void tickBomb(ServerLevel serverLevel) {
        if (this.bombTicks < 0) {
            return;
        }
        double x = ModEntity.getX(this);
        double y = ModEntity.getY(this);
        double z = ModEntity.getZ(this);
        boolean beep = false;
        for (int beepTick : BOMB_SLOW_BEEP_TICKS) {
            beep |= this.bombTicks == beepTick;
        }

        beep |= this.bombTicks >= BOMB_RAPID_START_TICK && this.bombTicks <= BOMB_EXPLODE_TICK - 4
                && (this.bombTicks - BOMB_RAPID_START_TICK) % BOMB_RAPID_INTERVAL_TICKS == 0;
        if (beep) {
            serverLevel.playSound(null, x, y, z, AiObjectModSounds.BOMB_BEEP, SoundSource.NEUTRAL, 1.0f, 1.0f);
        }
        if (this.bombTicks == BOMB_EXPLODE_TICK) {
            this.ejectPassengers();
            this.spawnAiCarExplosion(serverLevel, x, y + 0.5, z);
            this.bombFireSpots.clear();

            this.bombFireSpots.add(new Vec3(x, y + 0.9, z));
            this.bombFireSpots.add(new Vec3(x + (this.random.nextDouble() - 0.5), y + 0.7, z + (this.random.nextDouble() - 0.5)));
            for (int i = 0; i < BOMB_FIRE_SPOT_COUNT; i++) {
                double angle = this.random.nextDouble() * Math.PI * 2.0;
                double distance = 1.8 + this.random.nextDouble() * 2.5;
                this.bombFireSpots.add(new Vec3(x + Math.cos(angle) * distance, y + 0.1, z + Math.sin(angle) * distance));
            }
        }
        if (this.isBombBurning()) {
            this.setDeltaMovement(Vec3.ZERO);
            int burnTick = this.bombTicks - BOMB_EXPLODE_TICK;

            int flameCount = burnTick > BOMB_BURN_TICKS - 20 ? 1 : 3;
            for (Vec3 spot : this.bombFireSpots) {
                serverLevel.sendParticles(ParticleTypes.FLAME, spot.x, spot.y + 0.3, spot.z, flameCount, 0.25, 0.3, 0.25, 0.01);
                if (burnTick % 3 == 0) {
                    serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE, spot.x, spot.y + 0.8, spot.z, 1, 0.2, 0.2, 0.2, 0.01);
                }
            }
            if (burnTick % 20 == 0) {
                serverLevel.playSound(null, x, y, z, SoundEvents.FIRE_AMBIENT, SoundSource.NEUTRAL, 1.5f, 1.0f);
            }
            if (burnTick >= BOMB_BURN_TICKS) {
                for (Vec3 spot : this.bombFireSpots) {
                    serverLevel.sendParticles(ParticleTypes.POOF, spot.x, spot.y + 0.3, spot.z, 4, 0.2, 0.2, 0.2, 0.02);
                }
                serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE, x, y + 0.8, z, 20, 1.0, 0.5, 1.0, 0.02);
                this.discard();
                return;
            }
        }
        this.bombTicks++;
    }

    public float getCarScaleAdjust() {
        return this.entityData.get(CAR_SCALE);
    }

    public void setCarScale(float carScaleAdjust) {
        this.entityData.set(CAR_SCALE, carScaleAdjust);
    }

    private float getBaseCarScale() {
        if (this.wheelAttachment != null) {
            return this.wheelAttachment.carScale();
        }
        net.minecraft.server.packs.resources.ResourceManager resourceManager =
                this.level() instanceof ServerLevel serverLevel
                        ? com.examplebett.compat.ModResources.getServerResourceManager(serverLevel.getServer())
                        : clientDimensionsResourceManager.get();
        return resourceManager != null ? CarFootprint.getCarScale(resourceManager, this.getModelId()) : 1f;
    }

    public float getCarScale() {
        return Mth.clamp(this.getBaseCarScale() + this.getCarScaleAdjust(), CAR_SCALE_MIN, CAR_SCALE_MAX);
    }

    public void setWheelAdjust(float frontZOffset, float rearZOffset, float trackHalfWidthOffset,
                                float axleHeightOffset, float bodyHeightOffset, float camberDegrees,
                                float wheelScaleAdjust, String wheelModelIdOverride) {
        this.entityData.set(WHEEL_FRONT_Z_OFFSET, frontZOffset);
        this.entityData.set(WHEEL_REAR_Z_OFFSET, rearZOffset);
        this.entityData.set(WHEEL_TRACK_HALF_WIDTH_OFFSET, trackHalfWidthOffset);
        this.entityData.set(WHEEL_AXLE_HEIGHT_OFFSET, axleHeightOffset);
        this.entityData.set(BODY_HEIGHT_OFFSET, bodyHeightOffset);
        this.entityData.set(WHEEL_CAMBER_DEGREES, camberDegrees);
        this.entityData.set(WHEEL_SCALE, wheelScaleAdjust);
        this.entityData.set(WHEEL_MODEL_ID_OVERRIDE, wheelModelIdOverride);
    }

    public void setWheelColor(String wheelColorOverride) {
        this.entityData.set(WHEEL_COLOR_OVERRIDE, wheelColorOverride);
    }

    public static final float DEFAULT_CAMERA_DISTANCE = 4.0f;

    private com.examplebett.render.CameraAttachment cameraAttachment;

    public com.examplebett.render.CameraAttachment getCameraAttachment() {
        return this.cameraAttachment;
    }

    public void setCameraAttachment(com.examplebett.render.CameraAttachment cameraAttachment) {
        this.cameraAttachment = cameraAttachment;
    }

    public float getCameraDistanceAdjust() {
        return this.entityData.get(CAMERA_DISTANCE_ADJUST);
    }

    public void setCameraDistanceAdjust(float distanceAdjust) {
        this.entityData.set(CAMERA_DISTANCE_ADJUST, distanceAdjust);
    }

    public float getCameraDistance() {
        float base = this.cameraAttachment != null ? this.cameraAttachment.distance() : DEFAULT_CAMERA_DISTANCE;
        return base + this.getCameraDistanceAdjust();
    }

    public float getCameraHeightAdjust() {
        return this.entityData.get(CAMERA_HEIGHT_ADJUST);
    }

    public void setCameraHeightAdjust(float heightAdjust) {
        this.entityData.set(CAMERA_HEIGHT_ADJUST, heightAdjust);
    }

    public float getCameraHeight() {
        float base = this.cameraAttachment != null ? this.cameraAttachment.height() : 0f;
        return base + this.getCameraHeightAdjust();
    }

    public String getLiveryOverride() {
        return this.entityData.get(LIVERY_OVERRIDE);
    }

    public void setLivery(String liveryId) {
        this.entityData.set(LIVERY_OVERRIDE, liveryId);
    }

    public String getEngineSoundId() {
        return this.entityData.get(ENGINE_SOUND_ID);
    }

    public void setEngineSoundId(String soundId) {
        this.entityData.set(ENGINE_SOUND_ID, soundId);
    }

    public float getEnginePitchIntensity() {
        return this.entityData.get(ENGINE_PITCH_INTENSITY);
    }

    public void setEnginePitchIntensity(float intensity) {
        this.entityData.set(ENGINE_PITCH_INTENSITY, Mth.clamp(intensity, 0.0f, 1.0f));
    }

    public float getEngineTopSpeedPitch() {
        return this.entityData.get(ENGINE_TOP_SPEED_PITCH);
    }

    public void setEngineTopSpeedPitch(float pitch) {
        this.entityData.set(ENGINE_TOP_SPEED_PITCH, Mth.clamp(pitch, ENGINE_TOP_SPEED_PITCH_MIN, ENGINE_TOP_SPEED_PITCH_MAX));
    }

    public float getEngineLoopUseFraction() {
        return this.entityData.get(ENGINE_LOOP_USE_FRACTION);
    }

    public void setEngineLoopUseFraction(float fraction) {
        this.entityData.set(ENGINE_LOOP_USE_FRACTION, Mth.clamp(fraction, ENGINE_LOOP_USE_FRACTION_MIN, 1.0f));
    }

    public int getEngineLoopUseTicks(int fullDurationTicks) {
        float fraction = this.getEngineLoopUseFraction();
        if (fraction >= 1.0f) {
            return fullDurationTicks;
        }
        return Math.max(1, Math.round(fullDurationTicks * fraction));
    }

    public String getRailwayMiddleModelIds() {
        return this.entityData.get(RAILWAY_MIDDLE_MODEL_IDS);
    }

    public void setRailwayMiddleModelIds(String csv) {
        this.entityData.set(RAILWAY_MIDDLE_MODEL_IDS, csv);
    }

    public String getRailwayTailModelId() {
        return this.entityData.get(RAILWAY_TAIL_MODEL_ID);
    }

    public void setRailwayTailModelId(String modelId) {
        this.entityData.set(RAILWAY_TAIL_MODEL_ID, modelId);
    }

    public boolean isRailwayConfigured() {
        return this.entityData.get(RAILWAY_CONFIGURED);
    }

    public List<Integer> getRailwayCoupledEntityIds() {
        String csv = this.entityData.get(RAILWAY_COUPLED_ENTITY_IDS);
        if (csv.isEmpty()) {
            return List.of();
        }
        List<Integer> ids = new ArrayList<>();
        for (String part : csv.split(",")) {
            try {
                ids.add(Integer.parseInt(part));
            } catch (NumberFormatException ignored) {
            }
        }
        return ids;
    }

    public boolean isRailwayHead() {
        return !this.entityData.get(RAILWAY_COUPLED_ENTITY_IDS).isEmpty();
    }

    private boolean isInSameRailwayConsist(CarEntity other) {

        if ((this.hasVehicleCategory(VehicleCategory.TRAILER_HEAD) && other.hasVehicleCategory(VehicleCategory.TRAILER))
                || (this.hasVehicleCategory(VehicleCategory.TRAILER) && other.hasVehicleCategory(VehicleCategory.TRAILER_HEAD))
                || this.entityData.get(TRAILER_HEAD_ENTITY_ID) == other.getId()
                || other.entityData.get(TRAILER_HEAD_ENTITY_ID) == this.getId()
                || (this.trailerHeadUuid != null && this.trailerHeadUuid.equals(other.getUUID()))
                || (other.trailerHeadUuid != null && other.trailerHeadUuid.equals(this.getUUID()))) {
            return true;
        }
        if (this.railwayHeadUuid != null && this.railwayHeadUuid.equals(other.getUUID())) {
            return true;
        }
        if (other.railwayHeadUuid != null && other.railwayHeadUuid.equals(this.getUUID())) {
            return true;
        }
        return this.railwayHeadUuid != null && this.railwayHeadUuid.equals(other.railwayHeadUuid);
    }

    private void mineturismo$reduceRailwayFollowerInterpolationDelay() {

        //? if >=26.3 {
        /*if (this.getInterpolation() instanceof net.minecraft.world.entity.LinearInterpolationHandler linear) {
            linear.setInterpolationLength(1);
        }
        *///?} else if >=1.21.5 {
        this.getInterpolation().setInterpolationLength(1);
        //?}
    }

    //? if >=26.3 {
    /*@Override
    protected net.minecraft.world.entity.InterpolationHandler createInterpolationHandler() {
        return net.minecraft.world.entity.LinearInterpolationHandler.create(this);
    }
    *///?}

    //? if <1.20.2 {
    /*@Override
    public void lerpTo(double x, double y, double z, float yRot, float xRot, int steps, boolean teleport) {
        super.lerpTo(x, y, z, yRot, xRot, this.entityData.get(RAILWAY_FOLLOWER) || this.entityData.get(TRAILER_HEAD_ENTITY_ID) >= 0 ? 1 : steps, teleport);
    }*/
    //?}
    //? if >=1.20.2 {
    //? if <1.21.5 {
    /*@Override
    public void lerpTo(double x, double y, double z, float yRot, float xRot, int steps) {
        super.lerpTo(x, y, z, yRot, xRot, this.entityData.get(RAILWAY_FOLLOWER) || this.entityData.get(TRAILER_HEAD_ENTITY_ID) >= 0 ? 1 : steps);
    }*/
    //?}
    //?}

    public void discardRailwayCoupledCars() {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        for (UUID id : this.railwayCoupledIds) {
            Entity entity = serverLevel.getEntity(id);
            if (entity instanceof CarEntity coupled) {
                coupled.discard();
            }
        }
        this.railwayCoupledIds.clear();
    }

    public void applyRailwayConsist(List<String> middleModelIds, String tailModelId) {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        for (UUID id : this.railwayCoupledIds) {
            Entity entity = serverLevel.getEntity(id);
            if (entity instanceof CarEntity coupled) {
                coupled.discard();
            }
        }
        this.railwayCoupledIds.clear();

        List<String> orderedModelIds = new ArrayList<>(middleModelIds);
        if (!tailModelId.isEmpty()) {
            orderedModelIds.add(tailModelId);
        }

        net.minecraft.server.packs.resources.ResourceManager resourceManager =
                com.examplebett.compat.ModResources.getServerResourceManager(serverLevel.getServer());

        float yawRad = (float) Math.toRadians(this.getYRot());
        double rearX = -Mth.sin(yawRad);
        double rearZ = -Mth.cos(yawRad);
        float cumulativeTrailDistance = CarFootprint.getLength(resourceManager, this.getModelId()) * this.getCarScale() / 2.0f;

        for (String modelId : orderedModelIds) {
            float length = CarFootprint.getLength(resourceManager, modelId) * CarFootprint.getCarScale(resourceManager, modelId);
            cumulativeTrailDistance += RAILWAY_COUPLING_GAP;
            float centerTrailDistance = cumulativeTrailDistance + length / 2.0f;
            double spawnX = ModEntity.getX(this) + rearX * centerTrailDistance;
            double spawnZ = ModEntity.getZ(this) + rearZ * centerTrailDistance;
            CarEntity coupled = new CarEntity(serverLevel, spawnX, ModEntity.getY(this), spawnZ, modelId);
            coupled.setRot(this.getYRot(), 0f);
            coupled.railwayHeadUuid = this.getUUID();
            coupled.railwayTrailDistance = centerTrailDistance;
            coupled.entityData.set(RAILWAY_FOLLOWER, true);
            coupled.entityData.set(RAILWAY_HEAD_ENTITY_ID, this.getId());
            coupled.entityData.set(RAILWAY_TRAIL_DISTANCE_SYNCED, centerTrailDistance);
            coupled.entityData.set(RAILWAY_HALF_LENGTH_SYNCED, length / 2.0f);
            serverLevel.addFreshEntity(coupled);
            this.railwayCoupledIds.add(coupled.getUUID());

            cumulativeTrailDistance = centerTrailDistance + length / 2.0f;
        }

        this.railwayTrajectoryBuffer.clear();
        this.railwayTrajectoryDistance = 0f;
        float seedBackDistance = cumulativeTrailDistance + 5.0f;
        this.railwayTrajectoryBuffer.addLast(new float[]{
                (float) (ModEntity.getX(this) + rearX * seedBackDistance), (float) ModEntity.getY(this),
                (float) (ModEntity.getZ(this) + rearZ * seedBackDistance), -seedBackDistance});
        this.railwayTrajectoryBuffer.addLast(new float[]{
                (float) ModEntity.getX(this), (float) ModEntity.getY(this), (float) ModEntity.getZ(this), 0f});

        this.entityData.set(RAILWAY_MIDDLE_MODEL_IDS, String.join(",", middleModelIds));
        this.entityData.set(RAILWAY_TAIL_MODEL_ID, tailModelId);
        this.mineturismo$syncRailwayCoupledEntityIds(serverLevel);
    }

    private void updateRailwayConsist(ServerLevel serverLevel) {
        this.recordRailwayTrajectorySample();

        net.minecraft.server.packs.resources.ResourceManager resourceManager =
                com.examplebett.compat.ModResources.getServerResourceManager(serverLevel.getServer());
        for (UUID id : this.railwayCoupledIds) {
            Entity entity = serverLevel.getEntity(id);
            if (!(entity instanceof CarEntity coupled)) {

                continue;
            }
            float halfLength = CarFootprint.getLength(resourceManager, coupled.getModelId())
                    * CarFootprint.getCarScale(resourceManager, coupled.getModelId()) / 2.0f;
            float[] front = this.sampleRailwayTrajectory(coupled.railwayTrailDistance - halfLength);
            float[] rear = this.sampleRailwayTrajectory(coupled.railwayTrailDistance + halfLength);
            if (front == null || rear == null) {

                continue;
            }
            double newX = (front[0] + rear[0]) / 2.0;
            double newY = (front[1] + rear[1]) / 2.0;
            double newZ = (front[2] + rear[2]) / 2.0;
            double dirX = front[0] - rear[0];
            double dirZ = front[2] - rear[2];

            float newYaw = (Math.abs(dirX) > 1.0E-5 || Math.abs(dirZ) > 1.0E-5)
                    ? (float) Math.toDegrees(Math.atan2(dirX, dirZ))
                    : coupled.getYRot();
            coupled.setPos(newX, newY, newZ);
            coupled.setRot(newYaw, 0f);
            coupled.yRotO = coupled.yBodyRot = coupled.yHeadRot = newYaw;
        }
    }

    private void recordRailwayTrajectorySample() {
        float x = (float) ModEntity.getX(this);
        float y = (float) ModEntity.getY(this);
        float z = (float) ModEntity.getZ(this);
        float[] last = this.railwayTrajectoryBuffer.peekLast();
        if (last != null && last[0] == x && last[1] == y && last[2] == z) {
            return;
        }
        if (last != null) {
            double dx = x - last[0];
            double dy = y - last[1];
            double dz = z - last[2];
            this.railwayTrajectoryDistance += (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
        }
        this.railwayTrajectoryBuffer.addLast(new float[]{x, y, z, this.railwayTrajectoryDistance});
        while (this.railwayTrajectoryBuffer.size() > 1
                && this.railwayTrajectoryBuffer.peekFirst()[3] < this.railwayTrajectoryDistance - RAILWAY_BUFFER_MAX_DISTANCE) {
            this.railwayTrajectoryBuffer.removeFirst();
        }
    }

    private float[] sampleRailwayTrajectory(float trailDistance) {
        if (this.railwayTrajectoryBuffer.isEmpty()) {
            return null;
        }
        float target = this.railwayTrajectoryDistance - trailDistance;
        float[] first = this.railwayTrajectoryBuffer.peekFirst();
        if (target < first[3]) {

            return null;
        }
        float[] previous = first;
        for (float[] sample : this.railwayTrajectoryBuffer) {
            if (sample[3] >= target) {
                if (sample[3] == previous[3]) {
                    return new float[]{sample[0], sample[1], sample[2]};
                }
                float t = (target - previous[3]) / (sample[3] - previous[3]);
                return new float[]{
                        Mth.lerp(t, previous[0], sample[0]),
                        Mth.lerp(t, previous[1], sample[1]),
                        Mth.lerp(t, previous[2], sample[2])};
            }
            previous = sample;
        }
        return new float[]{previous[0], previous[1], previous[2]};
    }

    private void mineturismo$recordClientRailwayTrajectorySample() {
        float x = (float) ModEntity.getX(this);
        float y = (float) ModEntity.getY(this);
        float z = (float) ModEntity.getZ(this);
        float[] last = this.clientRailwayTrajectoryBuffer.peekLast();
        if (last != null && last[0] == x && last[1] == y && last[2] == z) {
            return;
        }
        if (last != null) {
            double dx = x - last[0];
            double dy = y - last[1];
            double dz = z - last[2];
            this.clientRailwayTrajectoryDistance += (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
        }
        this.clientRailwayTrajectoryBuffer.addLast(new float[]{x, y, z, this.clientRailwayTrajectoryDistance});
        while (this.clientRailwayTrajectoryBuffer.size() > 1
                && this.clientRailwayTrajectoryBuffer.peekFirst()[3] < this.clientRailwayTrajectoryDistance - RAILWAY_BUFFER_MAX_DISTANCE) {
            this.clientRailwayTrajectoryBuffer.removeFirst();
        }
    }

    private float[] mineturismo$sampleClientRailwayTrajectory(float liveDistance, float trailDistance) {
        if (this.clientRailwayTrajectoryBuffer.isEmpty()) {
            return null;
        }
        float target = liveDistance - trailDistance;
        float[] first = this.clientRailwayTrajectoryBuffer.peekFirst();
        if (target < first[3]) {
            return null;
        }
        float[] previous = first;
        for (float[] sample : this.clientRailwayTrajectoryBuffer) {
            if (sample[3] >= target) {
                if (sample[3] == previous[3]) {
                    return new float[]{sample[0], sample[1], sample[2]};
                }
                float t = (target - previous[3]) / (sample[3] - previous[3]);
                return new float[]{
                        Mth.lerp(t, previous[0], sample[0]),
                        Mth.lerp(t, previous[1], sample[1]),
                        Mth.lerp(t, previous[2], sample[2])};
            }
            previous = sample;
        }
        return new float[]{previous[0], previous[1], previous[2]};
    }

    public float[] mineturismo$computeRailwayRenderOverride(float partialTick) {
        if (this.level().isClientSide() && this.entityData.get(TRAILER_HEAD_ENTITY_ID) >= 0) {
            return this.mineturismo$computeTrailerRenderOverride(partialTick);
        }
        if (!this.level().isClientSide() || !this.entityData.get(RAILWAY_FOLLOWER)) {
            return null;
        }
        int headId = this.entityData.get(RAILWAY_HEAD_ENTITY_ID);
        if (headId < 0) {
            return null;
        }
        Entity headEntity = this.level().getEntity(headId);
        if (!(headEntity instanceof CarEntity head) || head.clientRailwayTrajectoryBuffer.isEmpty()) {
            return null;
        }

        float liveDistance;
        Iterator<float[]> it = head.clientRailwayTrajectoryBuffer.descendingIterator();
        float[] latest = it.next();
        if (it.hasNext()) {
            float[] secondLatest = it.next();
            liveDistance = Mth.lerp(partialTick, secondLatest[3], latest[3]);
        } else {
            liveDistance = latest[3];
        }
        float halfLength = this.entityData.get(RAILWAY_HALF_LENGTH_SYNCED);
        float trailDistance = this.entityData.get(RAILWAY_TRAIL_DISTANCE_SYNCED);
        float[] front = head.mineturismo$sampleClientRailwayTrajectory(liveDistance, trailDistance - halfLength);
        float[] rear = head.mineturismo$sampleClientRailwayTrajectory(liveDistance, trailDistance + halfLength);
        if (front == null || rear == null) {
            return null;
        }
        double centerX = (front[0] + rear[0]) / 2.0;
        double centerY = (front[1] + rear[1]) / 2.0;
        double centerZ = (front[2] + rear[2]) / 2.0;
        double dirX = front[0] - rear[0];
        double dirZ = front[2] - rear[2];

        //? if >=1.21.2 {
        float fallbackYaw = this.getYRot(partialTick);
        //?} else
        /*float fallbackYaw = this.getYRot();*/
        float yaw = (Math.abs(dirX) > 1.0E-5 || Math.abs(dirZ) > 1.0E-5)
                ? (float) Math.toDegrees(Math.atan2(dirX, dirZ))
                : fallbackYaw;

        //? if >=1.16.2 {
        Vec3 vanillaPos = this.getPosition(partialTick);
        //?} else {
        /*Vec3 vanillaPos = new Vec3(
                Mth.lerp(partialTick, this.xo, ModEntity.getX(this)),
                Mth.lerp(partialTick, this.yo, ModEntity.getY(this)),
                Mth.lerp(partialTick, this.zo, ModEntity.getZ(this)));*/
        //?}
        return new float[]{
                (float) (centerX - vanillaPos.x),
                (float) (centerY - vanillaPos.y),
                (float) (centerZ - vanillaPos.z),
                yaw};
    }

    private void mineturismo$syncRailwayCoupledEntityIds(ServerLevel serverLevel) {
        StringBuilder csv = new StringBuilder();
        for (UUID id : this.railwayCoupledIds) {
            Entity entity = serverLevel.getEntity(id);
            if (entity == null) {
                continue;
            }
            if (csv.length() > 0) {
                csv.append(',');
            }
            csv.append(entity.getId());
        }
        this.entityData.set(RAILWAY_COUPLED_ENTITY_IDS, csv.toString());
    }

    private static void mineturismo$packUuidInto(int[] dest, int offset, UUID id) {
        long most = id.getMostSignificantBits();
        long least = id.getLeastSignificantBits();
        dest[offset] = (int) (most >>> 32);
        dest[offset + 1] = (int) most;
        dest[offset + 2] = (int) (least >>> 32);
        dest[offset + 3] = (int) least;
    }

    private static UUID mineturismo$unpackUuidAt(int[] src, int offset) {
        long most = ((long) src[offset] << 32) | (src[offset + 1] & 0xFFFFFFFFL);
        long least = ((long) src[offset + 2] << 32) | (src[offset + 3] & 0xFFFFFFFFL);
        return new UUID(most, least);
    }

    private static String mineturismo$rawStringOr(CompoundTag tag, String key, String fallback) {
        //? if >=1.21.5 {
        return tag.getStringOr(key, fallback);
        //?} else
        /*return tag.contains(key) ? tag.getString(key) : fallback;*/
    }

    private static int mineturismo$rawIntOr(CompoundTag tag, String key, int fallback) {
        //? if >=1.21.5 {
        return tag.getInt(key).orElse(fallback);
        //?} else
        /*return tag.contains(key) ? tag.getInt(key) : fallback;*/
    }

    private static CompoundTag mineturismo$rawCompoundOr(CompoundTag tag, String key) {
        //? if >=1.21.5 {
        return tag.getCompound(key).orElse(new CompoundTag());
        //?} else
        /*return tag.contains(key) ? tag.getCompound(key) : new CompoundTag();*/
    }

    private void mineturismo$writeRailwayCarsTag(CompoundTag tag) {
        if (this.railwayCoupledIds.isEmpty() || !(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        int count = 0;
        for (UUID id : this.railwayCoupledIds) {
            Entity entity = serverLevel.getEntity(id);
            if (entity instanceof CarEntity coupled) {
                tag.put("RailwayCar" + count, coupled.exportCustomizationTag());
                count++;
            }
        }
        if (count > 0) {
            tag.putInt("RailwayCarCount", count);
        }
    }

    private void mineturismo$readRailwayCarsTag(CompoundTag tag) {
        this.railwayCoupledIds.clear();
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        net.minecraft.server.packs.resources.ResourceManager resourceManager =
                com.examplebett.compat.ModResources.getServerResourceManager(serverLevel.getServer());
        int count = mineturismo$rawIntOr(tag, "RailwayCarCount", 0);
        if (count == 0) {
            this.mineturismo$syncRailwayCoupledEntityIds(serverLevel);
            return;
        }

        float yawRad = (float) Math.toRadians(this.getYRot());
        double rearX = -Mth.sin(yawRad);
        double rearZ = -Mth.cos(yawRad);
        float cumulativeTrailDistance = CarFootprint.getLength(resourceManager, this.getModelId()) * this.getCarScale() / 2.0f;

        for (int i = 0; i < count; i++) {
            CompoundTag childTag = mineturismo$rawCompoundOr(tag, "RailwayCar" + i);
            String modelId = mineturismo$rawStringOr(childTag, "ModelId", "");
            if (modelId.isEmpty()) {
                continue;
            }
            float length = CarFootprint.getLength(resourceManager, modelId) * CarFootprint.getCarScale(resourceManager, modelId);
            cumulativeTrailDistance += RAILWAY_COUPLING_GAP;
            float centerTrailDistance = cumulativeTrailDistance + length / 2.0f;
            double spawnX = ModEntity.getX(this) + rearX * centerTrailDistance;
            double spawnZ = ModEntity.getZ(this) + rearZ * centerTrailDistance;
            CarEntity coupled = new CarEntity(serverLevel, spawnX, ModEntity.getY(this), spawnZ, modelId);
            coupled.setRot(this.getYRot(), 0f);

            coupled.importCustomizationTag(childTag);
            coupled.railwayHeadUuid = this.getUUID();
            coupled.railwayTrailDistance = centerTrailDistance;
            coupled.entityData.set(RAILWAY_FOLLOWER, true);
            coupled.entityData.set(RAILWAY_HEAD_ENTITY_ID, this.getId());
            coupled.entityData.set(RAILWAY_TRAIL_DISTANCE_SYNCED, centerTrailDistance);
            coupled.entityData.set(RAILWAY_HALF_LENGTH_SYNCED, length / 2.0f);
            serverLevel.addFreshEntity(coupled);
            this.railwayCoupledIds.add(coupled.getUUID());
            cumulativeTrailDistance = centerTrailDistance + length / 2.0f;
        }

        this.railwayTrajectoryBuffer.clear();
        this.railwayTrajectoryDistance = 0f;
        float seedBackDistance = cumulativeTrailDistance + 5.0f;
        this.railwayTrajectoryBuffer.addLast(new float[]{
                (float) (ModEntity.getX(this) + rearX * seedBackDistance), (float) ModEntity.getY(this),
                (float) (ModEntity.getZ(this) + rearZ * seedBackDistance), -seedBackDistance});
        this.railwayTrajectoryBuffer.addLast(new float[]{
                (float) ModEntity.getX(this), (float) ModEntity.getY(this), (float) ModEntity.getZ(this), 0f});

        this.mineturismo$syncRailwayCoupledEntityIds(serverLevel);
    }

    public boolean isRecording() {
        return this.entityData.get(IS_RECORDING);
    }

    public boolean isGhost() {
        return this.entityData.get(IS_GHOST);
    }

    private void setGhost(boolean ghost) {
        this.entityData.set(IS_GHOST, ghost);
    }

    public boolean hasRecording() {
        return !this.recordedFrames.isEmpty();
    }

    public DriveMode getDriveMode() {
        int ordinal = this.entityData.get(DRIVE_MODE);
        DriveMode[] values = DriveMode.values();
        return values[Mth.clamp(ordinal, 0, values.length - 1)];
    }

    public boolean isHeadingBasedDriveMode() {
        return this.getDriveMode() != DriveMode.MODERN;
    }

    public boolean isHandbrakeHeld() {
        return this.handbrakeHeld;
    }

    public float getHandbrakeRevIntensity() {
        return this.handbrakeRevIntensity;
    }

    public void setDriveMode(DriveMode mode) {
        if (mode != DriveMode.MODERN && this.getDriveMode() == DriveMode.MODERN) {
            this.classicHeading = -this.getYRot();
            this.classicSpeed = 0f;
            this.classicHeadingInitialized = true;
        }
        this.entityData.set(DRIVE_MODE, mode.ordinal());
    }

    public boolean isChaseModeEnabled() {
        return this.entityData.get(CHASE_MODE);
    }

    public void setChaseModeEnabled(boolean enabled) {
        this.entityData.set(CHASE_MODE, enabled);
    }

    public boolean isChaosModeEnabled() {
        return this.entityData.get(CHAOS_MODE);
    }

    public void setChaosModeEnabled(boolean enabled) {
        this.entityData.set(CHAOS_MODE, enabled);
    }

    public boolean isTownCar() {
        return this.entityData.get(IS_TOWN_CAR);
    }

    public boolean shouldRenderFakeTrafficRider() {
        return this.isTownCar() && this.hasOpenCarLikePassengerBehavior() && this.getPassengers().isEmpty();
    }

    public float getCustomTopSpeedKmh() {
        return this.entityData.get(CUSTOM_TOP_SPEED_KMH);
    }

    public void setCustomTopSpeedKmh(float kmh) {
        this.entityData.set(CUSTOM_TOP_SPEED_KMH, Mth.clamp(kmh, CUSTOM_TOP_SPEED_KMH_MIN, CUSTOM_TOP_SPEED_KMH_MAX));
    }

    public float getCustomAccelFraction() {
        return this.entityData.get(CUSTOM_ACCEL_FRACTION);
    }

    public void setCustomAccelFraction(float fraction) {
        this.entityData.set(CUSTOM_ACCEL_FRACTION, Mth.clamp(fraction, 0.0F, 1.0F));
    }

    public float getCustomBrakeFraction() {
        return this.entityData.get(CUSTOM_BRAKE_FRACTION);
    }

    public void setCustomBrakeFraction(float fraction) {
        this.entityData.set(CUSTOM_BRAKE_FRACTION, Mth.clamp(fraction, 0.0F, 1.0F));
    }

    public float getCustomHandlingFraction() {
        return this.entityData.get(CUSTOM_HANDLING_FRACTION);
    }

    public void setCustomHandlingFraction(float fraction) {
        this.entityData.set(CUSTOM_HANDLING_FRACTION, Mth.clamp(fraction, 0.0F, 1.0F));
    }

    public boolean isCustomTireSmokeAlways() {
        return this.entityData.get(CUSTOM_TIRE_SMOKE_ALWAYS);
    }

    public void setCustomTireSmokeAlways(boolean always) {
        this.entityData.set(CUSTOM_TIRE_SMOKE_ALWAYS, always);
    }

    public boolean isDespawnEnabled() {
        return this.entityData.get(DESPAWN_ENABLED);
    }

    public int getItemIcon() {
        return this.entityData.get(ITEM_ICON);
    }

    public void setItemIcon(int icon) {
        this.entityData.set(ITEM_ICON, Mth.clamp(icon, ITEM_ICON_VEHICLE, ITEM_ICON_SMART_KEY));
    }

    public int getPlacedFromItem() {
        return this.placedFromItem;
    }

    public void setPlacedFromItem(int placedFromItem) {
        this.placedFromItem = placedFromItem;
    }

    public void setDespawnEnabled(boolean enabled) {
        this.entityData.set(DESPAWN_ENABLED, enabled);
    }

    @Override
    public boolean isPersistenceRequired() {

        return this.railwayHeadUuid != null || this.trailerHeadUuid != null
                || !this.isDespawnEnabled() || this.isChaseModeEnabled() || this.isChaosModeEnabled()
                || this.chaseCarActive || this.chaosCarActive || this.responderActive
                || this.townCarActive || this.railwayRouteActive || super.isPersistenceRequired();
    }

    @Override

    //? if >=1.17 {
    //? if >=1.21.2 {
    public void onRemoval(Entity.RemovalReason reason) {
    //?} else
    /*public void remove(Entity.RemovalReason reason) {*/
        this.mineturismo$onRemoved(reason.shouldDestroy());
        //? if >=1.21.2 {
        super.onRemoval(reason);
        //?} else
        /*super.remove(reason);*/
    }
    //?} else
    /*public void remove() {

        this.mineturismo$onRemoved(true);
        super.remove();
    }*/

    private void mineturismo$onRemoved(boolean destroy) {
        if (this.townCarActive && this.level() instanceof ServerLevel serverLevel) {
            WaypointNetworkSavedData.get(serverLevel).untrackActiveTownCar(this.getId());
        }
        if (this.railwayRouteActive && this.level() instanceof ServerLevel railwayRouteServerLevel) {
            RailwayNetworkSavedData.get(railwayRouteServerLevel).clearActiveTrainIfMatches(this.railwayRouteOriginId, this.getId());
        }

        if (destroy && !this.railwayCoupledIds.isEmpty()
                && this.level() instanceof ServerLevel railwayServerLevel) {
            for (UUID mineturismo$coupledId : this.railwayCoupledIds) {
                Entity mineturismo$coupledEntity = railwayServerLevel.getEntity(mineturismo$coupledId);
                if (mineturismo$coupledEntity instanceof CarEntity mineturismo$coupled) {
                    mineturismo$coupled.discard();
                }
            }
            this.railwayCoupledIds.clear();
        }

        if (destroy && this.level() instanceof ServerLevel) {
            if (this.townCoupledTrailer != null) {

                if (!this.townCoupledTrailer.isRemoved()) {
                    this.townCoupledTrailer.discard();
                }
                this.townCoupledTrailer = null;
            } else {
                CarEntity mineturismo$coupledTrailer = this.findCoupledTrailer();
                if (mineturismo$coupledTrailer != null) {
                    mineturismo$coupledTrailer.setTrailerHead(null);
                }
            }
        }

        if (destroy && this.level() instanceof ServerLevel towServerLevel) {
            if (this.towLeaderUuid != null) {
                this.towLeaderUuid = null;
                dropTowRope(towServerLevel, this);
            }
            CarEntity towFollower = this.findTowFollower(towServerLevel);
            if (towFollower != null) {
                towFollower.setTowLeader(null);
                dropTowRope(towServerLevel, towFollower);
            }
        }

        if (destroy && this.level() instanceof ServerLevel && !this.trailerCargo.isEmpty()) {
            net.minecraft.world.Containers.dropContents(this.level(), this, this.trailerCargo);
        }
    }

    //? if >=1.17 {
    @Override
    public boolean shouldBeSaved() {

        if (this.railwayHeadUuid != null || this.townTrailer
                || this.chaseCarActive || this.chaosCarActive || this.townCarActive || this.railwayRouteActive) {
            return false;
        }
        return super.shouldBeSaved();
    }
    //?}

    public void startRecording() {
        this.recordedFrames.clear();
        this.entityData.set(IS_RECORDING, true);
    }

    public void stopRecording() {
        this.entityData.set(IS_RECORDING, false);
    }

    public void spawnGhost(boolean loop) {
        if (!(this.level() instanceof ServerLevel serverLevel) || this.recordedFrames.isEmpty()) {
            return;
        }
        float[] firstFrame = this.recordedFrames.get(0);
        CarEntity ghost = new CarEntity(serverLevel, firstFrame[0], firstFrame[1], firstFrame[2], this.getModelId());
        ghost.setColor(this.getColor());
        ghost.setFrontPlate(this.getFrontPlate());

        ghost.setFrontPlateOffset(this.getFrontPlateXAdjust(), this.getFrontPlateYAdjust(), this.getFrontPlateZAdjust());
        ghost.setFrontPlateScale(this.getFrontPlateScaleAdjust());
        ghost.setFrontPlateRotation(this.getFrontPlateYawAdjust(), this.getFrontPlatePitchAdjust(), this.getFrontPlateRollAdjust());
        ghost.setRearPlate(this.getRearPlate());
        ghost.setRearPlateOffset(this.getRearPlateXAdjust(), this.getRearPlateYAdjust(), this.getRearPlateZAdjust());
        ghost.setRearPlateScale(this.getRearPlateScaleAdjust());
        ghost.setRearPlateRotation(this.getRearPlateYawAdjust(), this.getRearPlatePitchAdjust(), this.getRearPlateRollAdjust());
        Vec3 tireSmoke = this.getTireSmokeOffset();
        ghost.setTireSmokeOffset((float) tireSmoke.x, (float) tireSmoke.y, (float) tireSmoke.z);
        ghost.setTireSmokeParticle(this.getTireSmokeParticle());
        ghost.setLivery(this.getLiveryOverride());
        ghost.setGhost(true);
        ghost.setRot(firstFrame[3], firstFrame[4]);
        ghost.ghostFrames = this.recordedFrames.toArray(new float[0][]);
        ghost.ghostFrameIndex = 0;
        ghost.ghostLoop = loop;
        ghost.ghostPlaying = true;
        serverLevel.addFreshEntity(ghost);
        this.spawnedGhostIds.add(ghost.getUUID());
    }

    public void deleteGhosts() {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        for (UUID id : this.spawnedGhostIds) {
            Entity entity = serverLevel.getEntity(id);
            if (entity instanceof CarEntity ghost && ghost.isGhost()) {
                ghost.discard();
            }
        }
        this.spawnedGhostIds.clear();
    }

    private void tickGhostPlayback() {
        if (this.ghostFrames.length == 0) {
            this.ghostPlaying = false;
            this.discard();
            return;
        }
        float[] frame = this.ghostFrames[this.ghostFrameIndex];
        this.setPos(frame[0], frame[1], frame[2]);
        this.setRot(frame[3], frame[4]);
        this.yRotO = this.yBodyRot = this.yHeadRot = frame[3];
        if (frame[5] >= 0.5f) {
            this.spawnTireSmoke();
        }
        this.tickGhostWheelDistanceBroadcast();
        this.ghostFrameIndex++;
        if (this.ghostFrameIndex >= this.ghostFrames.length) {
            if (this.ghostLoop) {
                this.ghostFrameIndex = 0;
            } else {
                this.ghostPlaying = false;
                this.discard();
            }
        }
    }

    private double ghostWheelPrevX;
    private double ghostWheelPrevZ;
    private boolean ghostWheelPrevInitialized;
    private float ghostWheelDistanceTraveled;
    private int ghostWheelBroadcastCooldownTicks;
    private static final int GHOST_WHEEL_BROADCAST_INTERVAL_TICKS = 2;

    private void tickGhostWheelDistanceBroadcast() {
        if (!(this.level() instanceof ServerLevel)) {
            return;
        }
        double curX = ModEntity.getX(this);
        double curZ = ModEntity.getZ(this);
        if (!this.ghostWheelPrevInitialized) {
            this.ghostWheelPrevX = curX;
            this.ghostWheelPrevZ = curZ;
            this.ghostWheelPrevInitialized = true;
            return;
        }
        double dx = curX - this.ghostWheelPrevX;
        double dz = curZ - this.ghostWheelPrevZ;
        this.ghostWheelPrevX = curX;
        this.ghostWheelPrevZ = curZ;

        float yawRad = (float) Math.toRadians(this.getYRot());
        double forwardX = -Mth.sin(yawRad);
        double forwardZ = Mth.cos(yawRad);
        double signedDistance = -(dx * forwardX + dz * forwardZ);
        this.ghostWheelDistanceTraveled += (float) signedDistance;
        if (this.ghostWheelBroadcastCooldownTicks > 0) {
            this.ghostWheelBroadcastCooldownTicks--;
            return;
        }
        this.ghostWheelBroadcastCooldownTicks = GHOST_WHEEL_BROADCAST_INTERVAL_TICKS;
        SyncWheelDistancePayload payload = new SyncWheelDistancePayload(this.getId(), this.ghostWheelDistanceTraveled);
        for (ServerPlayer trackingPlayer : ModNetworking.trackingPlayers(this)) {
            //? if >=1.20.5 {
            ModNetworking.sendToClient(trackingPlayer, payload);
            //?} else
            /*ModNetworking.sendToClient(trackingPlayer, SyncWheelDistancePayload.ID, payload, SyncWheelDistancePayload::write);*/
        }
    }

    public void setModel(ObjModel model) {
        this.model = model;
    }

    public ObjModel getModel() {
        return this.model;
    }

    public void setMaterials(Map<String, MtlMaterial> materials) {
        this.materials = materials;
    }

    public Map<String, MtlMaterial> getMaterials() {
        return this.materials;
    }

    public void setCompiledModel(CompiledCarModel compiledModel) {
        this.compiledModel = compiledModel;
    }

    public CompiledCarModel getCompiledModel() {
        return this.compiledModel;
    }

    public void setWheelAttachment(com.examplebett.render.WheelAttachment wheelAttachment) {
        this.wheelAttachment = wheelAttachment;
    }

    public com.examplebett.render.WheelAttachment getWheelAttachment() {
        return this.wheelAttachment;
    }

    public boolean hasWheelPositions() {
        return this.wheelAttachment != null
                || (this.compiledModel != null && this.compiledModel.wheelPivots.size() == 4);
    }

    public void setDetectedWheelFootprint(float trackHalfWidth, float rearZ) {
        this.entityData.set(DETECTED_WHEEL_TRACK_HALF_WIDTH, trackHalfWidth);
        this.entityData.set(DETECTED_WHEEL_REAR_Z, rearZ);
    }

    public boolean hasDetectedWheelFootprint() {
        return !Float.isNaN(this.entityData.get(DETECTED_WHEEL_TRACK_HALF_WIDTH));
    }

    public float getDetectedWheelTrackHalfWidth() {
        return this.entityData.get(DETECTED_WHEEL_TRACK_HALF_WIDTH);
    }

    public float getDetectedWheelRearZ() {
        return this.entityData.get(DETECTED_WHEEL_REAR_Z);
    }

    public void setWheelCompiledModel(CompiledCarModel wheelCompiledModel) {
        this.wheelCompiledModel = wheelCompiledModel;
    }

    public CompiledCarModel getWheelCompiledModel() {
        return this.wheelCompiledModel;
    }

    public void setWheelCompiledModelMirrored(CompiledCarModel wheelCompiledModelMirrored) {
        this.wheelCompiledModelMirrored = wheelCompiledModelMirrored;
    }

    public CompiledCarModel getWheelCompiledModelMirrored() {
        return this.wheelCompiledModelMirrored;
    }

    public void setLoadedWheelModelId(String loadedWheelModelId) {
        this.loadedWheelModelId = loadedWheelModelId;
    }

    public String getLoadedWheelModelId() {
        return this.loadedWheelModelId;
    }

    public void setLoadedWheelColorId(String loadedWheelColorId) {
        this.loadedWheelColorId = loadedWheelColorId;
    }

    public String getLoadedWheelColorId() {
        return this.loadedWheelColorId;
    }

    public void setLiveryOptions(com.examplebett.render.LiveryOptions liveryOptions) {
        this.liveryOptions = liveryOptions;
    }

    public com.examplebett.render.LiveryOptions getLiveryOptions() {
        return this.liveryOptions;
    }

    public void setLoadedLiveryId(String loadedLiveryId) {
        this.loadedLiveryId = loadedLiveryId;
    }

    public String getLoadedLiveryId() {
        return this.loadedLiveryId;
    }

    //? if >=1.16 {
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.6)

                //? if >=1.20.5 {
                .add(Attributes.STEP_HEIGHT, 1.05);
                //?} else
                /*;*/
    }
    //?}

    //? if <1.16 {
    /*@Override
    protected void registerAttributes() {
        super.registerAttributes();
        (this.getAttributes().getInstance(net.minecraft.world.entity.monster.SharedMonsterAttributes.MAX_HEALTH) != null
                ? this.getAttributes().getInstance(net.minecraft.world.entity.monster.SharedMonsterAttributes.MAX_HEALTH)
                : this.getAttributes().registerAttribute(net.minecraft.world.entity.monster.SharedMonsterAttributes.MAX_HEALTH))
                .setBaseValue(20.0);
        (this.getAttributes().getInstance(net.minecraft.world.entity.monster.SharedMonsterAttributes.MOVEMENT_SPEED) != null
                ? this.getAttributes().getInstance(net.minecraft.world.entity.monster.SharedMonsterAttributes.MOVEMENT_SPEED)
                : this.getAttributes().registerAttribute(net.minecraft.world.entity.monster.SharedMonsterAttributes.MOVEMENT_SPEED))
                .setBaseValue(0.6);
    }*/
    //?}

    @Override
    protected void registerGoals() {

    }

    @Override
    public HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }

    @Override
    public LivingEntity getControllingPassenger() {
        Entity passenger = this.getFirstPassenger();
        return passenger instanceof LivingEntity livingEntity ? livingEntity : null;
    }

    //? if >=1.20 {
    @Override
    protected Vec3 getRiddenInput(Player player, Vec3 vec3) {
        return this.computeRiddenInputVector(player);
    }
    //?}

    private Vec3 computeRiddenInputVector(Player player) {
        float xxa;
        float zza;
        boolean jumpHeld;
        boolean descendHeld;
        if (player instanceof ServerPlayer serverPlayer) {

            //? if >=1.21.2 {
            Input input = serverPlayer.getLastClientInput();
            xxa = input.left() == input.right() ? 0.0F : (input.left() ? 1.0F : -1.0F);
            zza = input.forward() == input.backward() ? 0.0F : (input.forward() ? 1.0F : -1.0F);
            jumpHeld = input.jump();

            descendHeld = input.sprint();
            this.rawForwardHeld = input.forward();
            this.rawBackwardHeld = input.backward();
            //?} else {
            /*xxa = serverPlayer.xxa;
            zza = serverPlayer.zza;
            jumpHeld = serverPlayer.jumping;
            descendHeld = serverPlayer.isSprinting();
            this.rawForwardHeld = zza > 0;
            this.rawBackwardHeld = zza < 0;*/
            //?}
        } else {

            xxa = player.xxa;
            zza = player.zza;
            //? if >=1.21.6 {
            jumpHeld = player.isJumping();
            //?} else
            /*jumpHeld = player.jumping;*/
            descendHeld = clientDescendKeyHeld != null && clientDescendKeyHeld.getAsBoolean();
            this.rawForwardHeld = clientForwardKeyHeld != null && clientForwardKeyHeld.getAsBoolean();
            this.rawBackwardHeld = clientBackKeyHeld != null && clientBackKeyHeld.getAsBoolean();
        }

        float vertical = jumpHeld == descendHeld ? 0.0F : (jumpHeld ? 1.0F : -1.0F);

        this.steerInput = xxa;

        this.handbrakeHeld = jumpHeld && !this.isAircraft();
        this.sprintKeyHeld = descendHeld;
        if (this.isHeadingBasedDriveMode()) {

            return new Vec3(xxa, vertical, zza);
        }

        float f = xxa * 0.5F;
        float g = zza;
        if (g <= 0.0F) {
            g *= 0.25F;
        }
        return new Vec3(f, vertical, g);
    }

    //? if >=1.20 {
    @Override
    protected void tickRidden(Player player, Vec3 vec3) {
        super.tickRidden(player, vec3);
        this.tickRiddenSteering(player, vec3.z > 0.01F);
    }
    //?}

    private boolean hasOpenCarLikePassengerBehavior() {
        return this.hasVehicleCategory(VehicleCategory.OPEN_CAR) || this.hasVehicleCategory(VehicleCategory.MOTORCYCLE);
    }

    private void lockPassengerBodyRotationIfOpenCar() {
        if (!this.hasOpenCarLikePassengerBehavior()) {
            return;
        }

        for (Entity passenger : this.getPassengers()) {
            if (passenger instanceof Player player) {
                player.setYBodyRot(this.getYRot());
            }
        }
    }

    @Override
    public void onPassengerTurned(Entity entityToUpdate) {
        if (this.hasOpenCarLikePassengerBehavior() && entityToUpdate instanceof Player player) {
            player.setYBodyRot(this.getYRot());
            return;
        }
        super.onPassengerTurned(entityToUpdate);
    }

    private void tickRiddenSteering(Player player, boolean movingForward) {
        if (this.isHeadingBasedDriveMode()) {

            return;
        }

        //? if >=1.17 {
        float mineturismo$playerYRot = player.getYRot();
        float mineturismo$playerXRot = player.getXRot();
        //?} else
        /*float mineturismo$playerYRot = player.yRot;
        float mineturismo$playerXRot = player.xRot;*/
        this.updateTireSmokeWindow(mineturismo$playerYRot, movingForward);

        this.drivingYaw = mineturismo$playerYRot;

        this.setRot(-mineturismo$playerYRot, mineturismo$playerXRot * 0.5F);
        this.yRotO = this.yBodyRot = this.yHeadRot = this.getYRot();
    }

    private void updateTireSmokeWindow(float currentHeading, boolean movingForward) {
        if (movingForward) {
            this.drivingYawWindowAge++;
            float windowDelta = Mth.abs(Mth.wrapDegrees(currentHeading - this.drivingYawWindow));
            if (windowDelta >= TIRE_SMOKE_TURN_THRESHOLD) {
                this.spawnTireSmoke();
            }
            if (this.drivingYawWindowAge >= TIRE_SMOKE_WINDOW_TICKS) {
                this.drivingYawWindow = currentHeading;
                this.drivingYawWindowAge = 0;
            }
        } else {
            this.drivingYawWindow = currentHeading;
            this.drivingYawWindowAge = 0;
        }
    }

    private static final int TIRE_SMOKE_PARTICLE_COUNT = 12;

    private static final int DRIFT_RESIDUAL_SMOKE_PARTICLE_COUNT = 3;

    private void spawnTireSmoke() {
        this.spawnTireSmoke(TIRE_SMOKE_PARTICLE_COUNT);
    }

    private void spawnTireSmoke(int particleCount) {
        this.spawnTireSmoke(particleCount, false);
    }

    private void spawnTireSmoke(int particleCount, boolean front) {

        this.tireSmokeFiredThisTick = true;
        if (this.level().isClientSide()) {

            this.clientSlipThisTick = true;
        }

        Vec3 offset = this.getTireSmokeOffset();

        com.examplebett.render.WheelAttachment attachment = this.wheelAttachment;
        double localRearX;
        double localRearZ;
        if (attachment != null) {
            localRearX = attachment.trackHalfWidth() + this.getWheelTrackHalfWidthOffset();
            localRearZ = (front ? attachment.rearZ() : attachment.frontZ()) + this.getWheelFrontZOffset();
        } else {
            WheelAttachmentFootprint.Footprint footprint = this.level() instanceof ServerLevel footprintLevel
                    ? WheelAttachmentFootprint.get(com.examplebett.compat.ModResources.getServerResourceManager(footprintLevel.getServer()), this.getModelId())
                    : null;
            if (footprint != null) {
                localRearX = footprint.trackHalfWidth() + this.getWheelTrackHalfWidthOffset();
                localRearZ = (front ? footprint.rearZ() : footprint.frontZ()) + this.getWheelFrontZOffset();
            } else if (this.hasDetectedWheelFootprint()) {

                localRearX = this.getDetectedWheelTrackHalfWidth() + this.getWheelTrackHalfWidthOffset();
                double detectedRearZ = this.getDetectedWheelRearZ() + this.getWheelFrontZOffset();
                localRearZ = front ? -detectedRearZ : detectedRearZ;
            } else if (offset.x != DEFAULT_TIRE_SMOKE_X || offset.z != DEFAULT_TIRE_SMOKE_Z) {

                localRearX = offset.x;
                localRearZ = front ? -offset.z : offset.z;
            } else {

                float[] footprintWidthLength = this.resolveFootprintWidthLength();
                float halfWidth = footprintWidthLength[0] / 2f;
                float halfLength = footprintWidthLength[1] / 2f;
                localRearX = halfWidth * TIRE_SMOKE_TRACK_WIDTH_FACTOR;
                localRearZ = front ? halfLength * TIRE_SMOKE_REAR_Z_FACTOR : -halfLength * TIRE_SMOKE_REAR_Z_FACTOR;
            }
        }
        var particleType = TireSmokeParticles.byId(this.getTireSmokeParticle());

        float yawRad = (float) Math.toRadians(
                (this.isHeadingBasedDriveMode() && this.level().isClientSide())
                        ? -this.classicHeading : this.getYRot());
        float sin = Mth.sin(yawRad);
        float cos = Mth.cos(yawRad);
        float speed = 0.09f;
        for (int side = -1; side <= 1; side += 2) {
            double localX = localRearX * side;
            double worldX = ModEntity.getX(this) + (localX * cos - localRearZ * sin);
            double worldY = ModEntity.getY(this) + offset.y;
            double worldZ = ModEntity.getZ(this) + (localRearZ * cos + localX * sin);
            if (this.level() instanceof ServerLevel) {

                this.pendingServerSmokeSpawns.add(new PendingSmokeSpawn(
                        SERVER_SMOKE_BROADCAST_DELAY_TICKS, particleType, worldX, worldY, worldZ, particleCount));
            } else {

                for (int i = 0; i < particleCount; i++) {
                    double vx = (this.random.nextDouble() - 0.5) * 2.0 * speed;
                    double vy = (this.random.nextDouble() - 0.5) * 2.0 * speed;
                    double vz = (this.random.nextDouble() - 0.5) * 2.0 * speed;
                    this.level().addParticle(particleType, worldX, worldY, worldZ, vx, vy, vz);
                }
            }
        }
    }

    @Override
    public boolean canAddPassenger(Entity passenger) {

        int maxPassengers = this.hasVehicleCategory(VehicleCategory.PASSENGER_TRANSPORT)
                || this.hasVehicleCategory(VehicleCategory.RAILWAY) ? 10 : 1;
        return this.getPassengers().size() < maxPassengers;
    }

    @Override
    protected void addPassenger(Entity entity) {
        super.addPassenger(entity);
        if (entity instanceof Player player) {

            if (this.getControllingPassenger() == player) {

                //? if >=1.17 {
                float mineturismo$addPassengerPlayerYRot = player.getYRot();
                //?} else
                /*float mineturismo$addPassengerPlayerYRot = player.yRot;*/
                this.drivingYaw = mineturismo$addPassengerPlayerYRot;
                this.drivingYawWindow = this.isHeadingBasedDriveMode() ? this.classicHeading : mineturismo$addPassengerPlayerYRot;
                this.drivingYawWindowAge = 0;
            }

            if (!this.level().isClientSide()) {
                if (!this.hasOpenCarLikePassengerBehavior()) {
                    player.setInvisible(true);
                }
                this.level().playSound(null, ModEntity.getX(this), ModEntity.getY(this), ModEntity.getZ(this),
                        AiObjectModSounds.DOOR, SoundSource.NEUTRAL, 1.0f, 1.0f);

                if (this.getControllingPassenger() == player
                        && this.hasVehicleCategory(VehicleCategory.RAILWAY)
                        && !this.isRailwayConfigured()
                        && !this.townCarActive && !this.railwayRouteActive && !this.chauffeurActive
                        && this.railwayHeadUuid == null) {
                    this.entityData.set(RAILWAY_CONFIGURED, true);
                    if (player instanceof ServerPlayer serverPlayer) {
                        OpenRailwayConsistSettingsPayload mineturismo$openPayload =
                                new OpenRailwayConsistSettingsPayload(this.getId());
                        //? if >=1.20.5 {
                        ModNetworking.sendToClient(serverPlayer, mineturismo$openPayload);
                        //?} else
                        /*ModNetworking.sendToClient(serverPlayer, OpenRailwayConsistSettingsPayload.ID, mineturismo$openPayload, OpenRailwayConsistSettingsPayload::write);*/
                    }
                }
            }
        }
    }

    @Override
    protected void removePassenger(Entity entity) {

        boolean wasControlling = entity == this.getControllingPassenger();
        super.removePassenger(entity);
        if (entity instanceof Player player && !this.level().isClientSide()) {
            player.setInvisible(false);
            this.level().playSound(null, ModEntity.getX(this), ModEntity.getY(this), ModEntity.getZ(this),
                    AiObjectModSounds.DOOR, SoundSource.NEUTRAL, 1.0f, 1.0f);
            if (wasControlling) {

                this.hornBroadcastActive = false;

                this.tireSlipBroadcastCooldownTicks = 0;
                this.clientSlipReportTicksRemaining = 0;
                this.serverSlideWindowInitialized = false;
            }

            if (this.chauffeurActive && player.getUUID().equals(this.chauffeurPayerId)) {
                mineturismo$consumeOneEmerald(player);
            }

            if (this.chauffeurActive && this.getPassengers().isEmpty()) {
                this.mineturismo$setChauffeurActive(false);
                this.chauffeurRoute = null;
                this.chauffeurRouteIndex = 0;
                this.chauffeurPayerId = null;
            }
        }
    }

    //? if >=1.20 {
    @Override
    protected float getRiddenSpeed(Player player) {
        return (float) this.getAttributeValue(Attributes.MOVEMENT_SPEED);
    }
    //?}

    @Override
    public void travel(Vec3 travelVector) {
        if (this.ghostPlaying) {

            if (this.level() instanceof ServerLevel) {
                this.tickGhostPlayback();
            }
            return;
        }
        if (this.isCoupledTrailer()) {

            return;
        }
        if (this.railwayHeadUuid != null) {

            return;
        }

        boolean playerControlled = (this.getControllingPassenger() instanceof Player) && !this.mineturismo$isNpcDrivenWithPassenger()
                && !this.hasVehicleCategory(VehicleCategory.TRAILER);

        boolean ballistic = ((this.chaosCarActive
                && (this.chaosVariant == ChaosVariant.SPINNING || this.chaosVariant == ChaosVariant.TUMBLING))
                || (this.townCarActive && !this.townUseLegacySteering)
                || this.railwayRouteActive)
                && !playerControlled;

        boolean aiControlled = (this.chaseCarActive || this.responderActive || (this.chaosCarActive && !ballistic)
                || (this.townCarActive && this.townUseLegacySteering)) && !playerControlled;
        boolean simulatingDrive = playerControlled || aiControlled;
        boolean aircraft = this.isAircraft();

        this.setNoGravity(aircraft && playerControlled);

        super.travel(simulatingDrive || ballistic ? Vec3.ZERO : travelVector);
        if (ballistic) {
            if (!this.townCarActive && !this.railwayRouteActive) {

                float yawRad = (float) Math.toRadians(this.chaosTravelHeading);
                double forwardX = -Mth.sin(yawRad);
                double forwardZ = Mth.cos(yawRad);
                double speedBlocksPerTick = CHAOS_CHARGE_TOP_SPEED_KMH / 3.6 / 20.0;
                Vec3 motion = this.getDeltaMovement();
                this.setDeltaMovement(forwardX * speedBlocksPerTick, motion.y, forwardZ * speedBlocksPerTick);
            }

        } else if (simulatingDrive) {
            Vec3 inputVector;
            if (aiControlled) {
                inputVector = new Vec3(this.aiSteerDir, 0.0, this.aiThrottleDir);
            } else {

                //? if >=1.20 {
                inputVector = travelVector;
                //?} else {
                /*inputVector = this.getControllingPassenger() instanceof Player player
                        ? this.computeRiddenInputVector(player) : travelVector;*/
                //?}
            }
            double worldX;
            double worldZ;
            if (this.isHeadingBasedDriveMode()) {

                Vec3 horizontal = this.updateClassicDrive(inputVector);
                worldX = horizontal.x;
                worldZ = horizontal.z;
            } else {

                float yawRad = (float) Math.toRadians(this.drivingYaw);
                float sin = (float) Math.sin(yawRad);
                float cos = (float) Math.cos(yawRad);
                double speedAttr = this.getAttributeValue(Attributes.MOVEMENT_SPEED);
                worldX = (inputVector.x * cos - inputVector.z * sin) * speedAttr;
                worldZ = (inputVector.z * cos + inputVector.x * sin) * speedAttr;
            }
            Vec3 motion = this.getDeltaMovement();

            double worldY = aircraft ? inputVector.y * AIRCRAFT_VERTICAL_SPEED : motion.y;
            this.setDeltaMovement(worldX, worldY, worldZ);
        }
    }

    private DriveTuning currentHeadingBasedTuning() {
        return switch (this.getDriveMode()) {
            case HYPER -> HYPER_TUNING;
            case DRIFT -> DRIFT_TUNING;
            case CUSTOM -> this.customTuning();
            default -> CLASSIC_TUNING;
        };
    }

    public float getTopForwardSpeedBlocksPerTick() {
        if (this.isHeadingBasedDriveMode()) {
            return this.currentHeadingBasedTuning().maxForwardSpeed();
        }
        return (float) this.getAttributeValue(Attributes.MOVEMENT_SPEED);
    }

    private boolean isDriftSlipping() {
        double curX = ModEntity.getX(this);
        double curZ = ModEntity.getZ(this);
        float curHeading = this.classicHeading;
        if (!this.driftSmokePrevInitialized) {
            this.driftSmokePrevX = curX;
            this.driftSmokePrevZ = curZ;
            this.driftSmokePrevHeading = curHeading;
            this.driftSmokePrevInitialized = true;
            return false;
        }
        double dx = curX - this.driftSmokePrevX;
        double dz = curZ - this.driftSmokePrevZ;
        float headingRad = (float) Math.toRadians(curHeading);
        double forwardX = -Mth.sin(headingRad);
        double forwardZ = Mth.cos(headingRad);
        double rightX = forwardZ;
        double rightZ = -forwardX;
        double lateralSpeed = Math.abs(dx * rightX + dz * rightZ);
        float yawRate = Math.abs(Mth.wrapDegrees(curHeading - this.driftSmokePrevHeading));
        this.driftSmokePrevX = curX;
        this.driftSmokePrevZ = curZ;
        this.driftSmokePrevHeading = curHeading;
        return lateralSpeed >= CLIENT_SLIDE_LATERAL_THRESHOLD || yawRate >= CLIENT_SLIDE_YAW_RATE_THRESHOLD;
    }

    private Vec3 updateClassicDrive(Vec3 travelVector) {

        float incomingSyncedHeadingDeg = -this.getYRot();
        if (!this.classicHeadingInitialized) {
            this.classicHeading = -this.getYRot();
            this.classicHeadingInitialized = true;
        }
        DriveTuning tuning = this.currentHeadingBasedTuning();

        float reverseFloor = Math.min(REVERSE_MIN_TOP_SPEED, tuning.maxForwardSpeed());
        if (tuning.maxReverseSpeed() > -reverseFloor) {
            tuning = new DriveTuning(tuning.maxForwardSpeed(), -reverseFloor,
                    tuning.acceleration(), tuning.braking(), tuning.maxTurnSpeedDeg());
        }
        boolean inputForward = travelVector.z > 0.5;
        boolean inputBackward = travelVector.z < -0.5;
        boolean inputLeft = travelVector.x > 0.5;
        boolean inputRight = travelVector.x < -0.5;

        int steerDirNow = inputLeft ? -1 : (inputRight ? 1 : 0);

        if (steerDirNow == 0 || this.classicSpeed == 0.0F) {
            this.steerRampHoldTicks = 0;
            this.steerRampLastDir = 0;
        } else if (steerDirNow == this.steerRampLastDir) {
            this.steerRampHoldTicks = Math.min(this.steerRampHoldTicks + 1, STEER_RAMP_TICKS);
        } else {
            this.steerRampHoldTicks = 1;
            this.steerRampLastDir = steerDirNow;
        }

        if (steerDirNow == 0) {
            this.reverseAutoDriftBlocked = false;
        } else if (steerDirNow != this.reverseAutoDriftLastSteerDir) {
            this.reverseAutoDriftBlocked = this.classicSpeed > -REVERSE_AUTO_DRIFT_MIN_SPEED;
        }
        this.reverseAutoDriftLastSteerDir = steerDirNow;
        boolean reverseDriftAuto = this.classicSpeed <= -REVERSE_AUTO_DRIFT_MIN_SPEED
                && steerDirNow != 0 && !inputForward && !this.reverseAutoDriftBlocked;
        this.reverseDriftAutoThisTick = reverseDriftAuto;
        boolean reverseDriftSteer = reverseDriftAuto;

        boolean stationaryBurnout = this.classicSpeed == 0.0F && this.rawForwardHeld && this.rawBackwardHeld;

        boolean driftPhysicsActive = this.getDriveMode() == DriveMode.DRIFT || this.handbrakeHeld || reverseDriftAuto;

        boolean driftHeadingGapRemains = this.driftVelocityHeadingInitialized
                && Math.abs(Mth.wrapDegrees(this.classicHeading - this.driftVelocityHeading)) > DRIFT_RECOVERY_GAP_EPSILON;
        boolean driftRecoveryActive = driftPhysicsActive || driftHeadingGapRemains;

        if (this.handbrakeHeld && !(inputForward && this.classicSpeed != 0.0F)) {

            float handbrakeDecel = tuning.maxForwardSpeed() * HANDBRAKE_DECEL_FRACTION;
            if (this.classicSpeed > 0.0F) {
                this.classicSpeed = Math.max(this.classicSpeed - handbrakeDecel, 0.0F);
            } else if (this.classicSpeed < 0.0F) {
                this.classicSpeed = Math.min(this.classicSpeed + handbrakeDecel, 0.0F);
            }
        } else if (stationaryBurnout) {

        } else if (inputForward) {

            this.classicSpeed = Math.min(this.classicSpeed + tuning.acceleration(), tuning.maxForwardSpeed());
        } else if (inputBackward) {
            this.classicSpeed = Math.max(this.classicSpeed - tuning.acceleration(), tuning.maxReverseSpeed());
        } else if (this.classicSpeed > 0.0F) {
            this.classicSpeed = Math.max(this.classicSpeed - tuning.braking(), 0.0F);
        } else if (this.classicSpeed < 0.0F) {
            this.classicSpeed = Math.min(this.classicSpeed + tuning.braking(), 0.0F);
        }

        float revTarget = (this.handbrakeHeld && this.rawForwardHeld) ? 1.0F : 0.0F;
        if (this.handbrakeRevIntensity < revTarget) {
            this.handbrakeRevIntensity = Math.min(this.handbrakeRevIntensity + HANDBRAKE_REV_RAMP_UP, revTarget);
        } else if (this.handbrakeRevIntensity > revTarget) {
            this.handbrakeRevIntensity = Math.max(this.handbrakeRevIntensity - HANDBRAKE_REV_RAMP_DOWN, revTarget);
        }

        boolean wheelieHandbrakeJustReleased = this.prevWheelieHandbrakeHeld && !this.handbrakeHeld;
        if (wheelieHandbrakeJustReleased && this.rawForwardHeld && this.prevWheelieClassicSpeed == 0.0F
                && this.handbrakeRevIntensity >= WHEELIE_TRIGGER_REV_THRESHOLD
                && this.hasVehicleCategory(VehicleCategory.MUSCLE_CAR)) {
            this.clientWheelieTriggerThisTick = true;
        }
        this.prevWheelieHandbrakeHeld = this.handbrakeHeld;
        this.prevWheelieClassicSpeed = this.classicSpeed;

        float burnoutSpinTarget = stationaryBurnout ? BURNOUT_SPIN_MAX_DEG_PER_TICK : 0.0F;
        if (this.burnoutSpinRate < burnoutSpinTarget) {
            this.burnoutSpinRate = Math.min(this.burnoutSpinRate + BURNOUT_SPIN_RAMP_UP, burnoutSpinTarget);
        } else if (this.burnoutSpinRate > burnoutSpinTarget) {
            this.burnoutSpinRate = Math.max(this.burnoutSpinRate - BURNOUT_SPIN_RAMP_DOWN, burnoutSpinTarget);
        }
        this.burnoutSpinRot += this.burnoutSpinRate;
        if (stationaryBurnout) {

            this.spawnTireSmoke(TIRE_SMOKE_PARTICLE_COUNT, false);
        }

        if (this.classicSpeed != 0.0F && (inputLeft || inputRight)) {
            float speedFraction = Math.abs(this.classicSpeed) / tuning.maxForwardSpeed();

            float turnRateDeg = driftPhysicsActive ? DRIFT_TUNING.maxTurnSpeedDeg() : tuning.maxTurnSpeedDeg();
            if (reverseDriftSteer) {

                turnRateDeg *= REVERSE_DRIFT_TURN_RATE_MULTIPLIER;
            }
            float turn = turnRateDeg * (0.4F + 0.6F * speedFraction) * Math.signum(this.classicSpeed);
            if (driftPhysicsActive) {

                int steerRampTicks = reverseDriftSteer ? REVERSE_DRIFT_STEER_RAMP_TICKS : DRIFT_STEER_RAMP_TICKS;
                if (steerDirNow == this.driftLastSteerDir) {
                    this.driftSteerHoldTicks = Math.min(this.driftSteerHoldTicks + 1, steerRampTicks);
                } else {
                    this.driftSteerHoldTicks = 1;
                }
                turn *= (float) this.driftSteerHoldTicks / steerRampTicks;
            } else {

                turn *= (float) this.steerRampHoldTicks / STEER_RAMP_TICKS;
            }
            if (inputLeft) {
                this.classicHeading -= turn;
                if (driftPhysicsActive) {
                    this.driftYawRate = -turn;
                }
            }
            if (inputRight) {
                this.classicHeading += turn;
                if (driftPhysicsActive) {
                    this.driftYawRate = turn;
                }
            }
        } else if (driftRecoveryActive && this.driftYawRate != 0.0F) {

            this.classicHeading += this.driftYawRate;
            this.driftYawRate *= DRIFT_YAW_RATE_DECAY_FACTOR;
            if (Math.abs(this.driftYawRate) < DRIFT_YAW_RATE_STOP_EPSILON) {
                this.driftYawRate = 0.0F;
            }
        }

        boolean alwaysSmokeOnTurn = driftPhysicsActive
                || (this.getDriveMode() == DriveMode.CUSTOM && this.isCustomTireSmokeAlways());

        boolean driftSlipping = driftRecoveryActive && this.isDriftSlipping();
        if (!driftRecoveryActive) {
            this.driftSmokePrevInitialized = false;
        }
        if (alwaysSmokeOnTurn && (inputLeft || inputRight) && this.classicSpeed != 0.0F) {

            this.spawnTireSmoke(TIRE_SMOKE_PARTICLE_COUNT, reverseDriftSteer);
        } else {
            this.updateTireSmokeWindow(this.classicHeading, this.classicSpeed > 0.0F);
            if (driftSlipping) {

                this.spawnTireSmoke(DRIFT_RESIDUAL_SMOKE_PARTICLE_COUNT, reverseDriftSteer);
            }
        }

        this.setRot(-this.classicHeading, 0.0F);
        this.yRotO = this.yBodyRot = this.yHeadRot = this.getYRot();

        if (driftRecoveryActive) {
            if (!this.driftVelocityHeadingInitialized) {
                this.driftVelocityHeading = this.classicHeading;
                this.driftVelocityHeadingInitialized = true;
            }
            float counterGap = Mth.wrapDegrees(this.classicHeading - this.driftVelocityHeading);

            boolean counterSteerCatch = this.getDriveMode() != DriveMode.DRIFT
                    && steerDirNow != 0
                    && Math.abs(counterGap) > DRIFT_RECOVERY_GAP_EPSILON
                    && Math.signum(this.driftYawRate) == -Math.signum(counterGap);
            if (counterSteerCatch) {

                float driftSpeedFraction = Math.abs(this.classicSpeed) / tuning.maxForwardSpeed();
                float counterCatchUp = DRIFT_SLIDE_MAX_TURN_DEG_PER_TICK
                        * (1.0F + (1.0F - driftSpeedFraction) * DRIFT_SLIDE_RECOVERY_BOOST);
                if (inputForward) {
                    counterCatchUp *= DRIFT_SLIDE_THROTTLE_RECOVERY_MULTIPLIER;
                }
                counterCatchUp *= COUNTER_STEER_CATCH_MULTIPLIER;
                this.driftVelocityHeading += Mth.clamp(counterGap, -counterCatchUp, counterCatchUp);
                this.driftYawRate = 0.0F;
                this.driftLastSteerDir = steerDirNow;
            } else if (this.classicSpeed == 0.0F) {

                this.driftVelocityHeading = this.classicHeading;
                this.driftYawRate = 0.0F;
                this.driftLastSteerDir = 0;
                this.driftSteerHoldTicks = 0;
            } else {
                float driftSpeedFraction = Math.abs(this.classicSpeed) / tuning.maxForwardSpeed();

                if (steerDirNow != 0 && steerDirNow != this.driftLastSteerDir) {

                    float kickMultiplier = reverseDriftSteer ? REVERSE_DRIFT_KICK_MULTIPLIER : 1.0F;
                    this.driftVelocityHeading -= Math.signum(this.driftYawRate) * DRIFT_SLIDE_KICK_DEGREES * kickMultiplier * (0.4F + 0.6F * driftSpeedFraction);
                }
                this.driftLastSteerDir = steerDirNow;

                float maxCatchUp = DRIFT_SLIDE_MAX_TURN_DEG_PER_TICK
                        * (1.0F + (1.0F - driftSpeedFraction) * DRIFT_SLIDE_RECOVERY_BOOST);
                if (inputForward) {
                    maxCatchUp *= DRIFT_SLIDE_THROTTLE_RECOVERY_MULTIPLIER;
                }
                if (reverseDriftSteer) {

                    maxCatchUp *= REVERSE_DRIFT_RECOVERY_SLOWDOWN;
                }
                float headingGap = Mth.wrapDegrees(this.classicHeading - this.driftVelocityHeading);
                this.driftVelocityHeading += Mth.clamp(headingGap, -maxCatchUp, maxCatchUp);
            }
        } else {
            this.driftVelocityHeading = this.classicHeading;
            this.driftVelocityHeadingInitialized = true;

            this.driftYawRate = 0.0F;
        }

        if (this.getControllingPassenger() instanceof Player && !this.isLocalInstanceAuthoritative()) {
            float reconcileGap = Mth.wrapDegrees(incomingSyncedHeadingDeg - this.classicHeading);
            if (Math.abs(reconcileGap) > HEADING_RECONCILE_THRESHOLD_DEG) {
                float correction = Mth.clamp(reconcileGap,
                        -HEADING_RECONCILE_MAX_CORRECTION_DEG_PER_TICK, HEADING_RECONCILE_MAX_CORRECTION_DEG_PER_TICK);
                this.classicHeading += correction;
                this.driftVelocityHeading += correction;
            }
        }

        float yawRad = (float) Math.toRadians(this.driftVelocityHeading);
        float sin = (float) Math.sin(yawRad);
        float cos = (float) Math.cos(yawRad);
        return new Vec3(-sin * this.classicSpeed, 0.0, cos * this.classicSpeed);
    }

    private static final double VEHICLE_HITBOX_HEIGHT = 1.5;

    private void updateFootprintBoundingBox() {
        float[] footprint = resolveFootprintWidthLength();
        float width = footprint[0];
        float length = footprint[1];

        float absYaw = Math.abs(Mth.wrapDegrees(this.getYRot()));
        boolean lengthAlongZ = absYaw <= 45.0f || absYaw >= 135.0f;
        double halfX = (lengthAlongZ ? width : length) / 2.0;
        double halfZ = (lengthAlongZ ? length : width) / 2.0;

        double x = ModEntity.getX(this);

        double y = ModEntity.getY(this);
        double z = ModEntity.getZ(this);
        this.setBoundingBox(new AABB(x - halfX, y, z - halfZ, x + halfX, y + this.scaledHitboxHeight(), z + halfZ));
    }

    private float[] resolveFootprintWidthLength() {
        net.minecraft.server.packs.resources.ResourceManager resourceManager =
                this.level() instanceof ServerLevel serverLevel
                        ? com.examplebett.compat.ModResources.getServerResourceManager(serverLevel.getServer())
                        : clientDimensionsResourceManager.get();
        float carScale = this.getCarScale();
        if (resourceManager == null) {
            return new float[]{2.0f * carScale, 4.5f * carScale};
        }
        return new float[]{
                CarFootprint.getWidth(resourceManager, this.getModelId()) * carScale,
                CarFootprint.getLength(resourceManager, this.getModelId()) * carScale
        };
    }

    private double scaledHitboxHeight() {
        net.minecraft.server.packs.resources.ResourceManager resourceManager =
                this.level() instanceof ServerLevel serverLevel
                        ? com.examplebett.compat.ModResources.getServerResourceManager(serverLevel.getServer())
                        : clientDimensionsResourceManager.get();
        float carScale = this.getCarScale();
        if (resourceManager == null) {
            return VEHICLE_HITBOX_HEIGHT * carScale;
        }
        return CarFootprint.getHeight(resourceManager, this.getModelId()) * carScale;
    }

    public double getScaledHitboxHeight() {
        return this.scaledHitboxHeight();
    }

    public float getFootprintHalfLength() {
        return this.resolveFootprintWidthLength()[1] / 2f;
    }

    private void tryDetectWallImpact(ServerLevel serverLevel, double actualSpeed) {
        if (this.wallHitParticleCooldown > 0) {
            this.wallHitParticleCooldown--;
        }
        if (!(this.getControllingPassenger() instanceof Player)) {
            return;
        }
        if (actualSpeed < WALL_HIT_INTENDED_SPEED_THRESHOLD || this.wallHitParticleCooldown > 0) {
            return;
        }

        float[] footprint = resolveFootprintWidthLength();
        float width = footprint[0];
        float length = footprint[1];
        float yawRad = (float) Math.toRadians(this.getYRot());
        double forwardX = -Mth.sin(yawRad);
        double forwardZ = Mth.cos(yawRad);
        double rightX = forwardZ;
        double rightZ = -forwardX;

        double frontCenterX = ModEntity.getX(this) + forwardX * (length / 2.0 + COLLISION_LENGTH_MARGIN);
        double frontCenterZ = ModEntity.getZ(this) + forwardZ * (length / 2.0 + COLLISION_LENGTH_MARGIN);
        double frontY = ModEntity.getY(this) + this.getBodyHeightOffset() + this.scaledHitboxHeight() / 2.0;
        double halfWidth = width / 2.0;

        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        BlockState hitState = null;
        BlockPos hitPos = null;
        for (double widthFraction : new double[]{0.0, -0.9, 0.9}) {
            double checkX = frontCenterX + rightX * (halfWidth * widthFraction);
            double checkZ = frontCenterZ + rightZ * (halfWidth * widthFraction);
            //? if >=1.19.4 {
            mutable.set(BlockPos.containing(checkX, frontY, checkZ));
            //?} else
            /*mutable.set(new BlockPos(checkX, frontY, checkZ));*/
            BlockState state = serverLevel.getBlockState(mutable);
            if (!state.isAir() && !isCarBreakable(state)
                    && !state.getCollisionShape(serverLevel, mutable).isEmpty()) {
                hitState = state;
                hitPos = mutable.immutable();
                break;
            }
        }
        if (hitState == null) {
            return;
        }

        double blockCenterX = hitPos.getX() + 0.5;
        double blockCenterZ = hitPos.getZ() + 0.5;
        double towardCarX = ModEntity.getX(this) - blockCenterX;
        double towardCarZ = ModEntity.getZ(this) - blockCenterZ;
        Vec3 towardCar = new Vec3(towardCarX, 0.2, towardCarZ);

        //? if >=1.17 {
        boolean mineturismo$towardCarTooShort = towardCar.horizontalDistanceSqr() < 1.0E-6;
        //?} else
        /*boolean mineturismo$towardCarTooShort = towardCarX * towardCarX + towardCarZ * towardCarZ < 1.0E-6;*/
        if (mineturismo$towardCarTooShort) {
            towardCar = new Vec3(-forwardX, 0.2, -forwardZ);
        }

        Vec3 towardCarNorm = towardCar.normalize();
        double spawnX = blockCenterX + towardCarNorm.x * 0.6;
        double spawnZ = blockCenterZ + towardCarNorm.z * 0.6;

        this.spawnImpactSparks(serverLevel, spawnX, frontY, spawnZ, actualSpeed, hitState, towardCar,
                collisionSoundFor(actualSpeed, false));
        this.wallHitParticleCooldown = WALL_HIT_COOLDOWN_TICKS;

        this.classicSpeed *= WALL_HIT_SPEED_RETENTION;
    }

    private void spawnImpactSparks(ServerLevel serverLevel, double x, double y, double z,
                                    double intensity, BlockState debrisState) {
        this.spawnImpactSparks(serverLevel, x, y, z, intensity, debrisState, null);
    }

    private static float collisionSoundVolume(double intensity) {
        return (float) Mth.clamp(0.6 + intensity * 0.08, 0.6, 1.5);
    }

    private static final double COLLISION_LOW_SPEED_BLOCKS_PER_TICK = 20.0 / 3.6 / 20.0;

    private static final double SIDE_IMPACT_FRONT_BACK_FRACTION = 0.5;

    private static net.minecraft.sounds.SoundEvent collisionSoundFor(double speedBlocksPerTick, boolean sideImpact) {
        return (speedBlocksPerTick <= COLLISION_LOW_SPEED_BLOCKS_PER_TICK || sideImpact)
                ? AiObjectModSounds.CAR_COLLISION_LOW : AiObjectModSounds.CAR_COLLISION;
    }

    private void spawnImpactSparks(ServerLevel serverLevel, double x, double y, double z,
                                    double intensity, BlockState debrisState, Vec3 biasDirection) {
        this.spawnImpactSparks(serverLevel, x, y, z, intensity, debrisState, biasDirection, AiObjectModSounds.CAR_COLLISION);
    }

    private void spawnImpactSparks(ServerLevel serverLevel, double x, double y, double z,
                                    double intensity, BlockState debrisState, Vec3 biasDirection, net.minecraft.sounds.SoundEvent collisionSound) {

        float collisionVolume = collisionSoundVolume(intensity);
        float collisionPitch = 0.9f + serverLevel.getRandom().nextFloat() * 0.2f;
        serverLevel.playSound(null, x, y, z, collisionSound, SoundSource.NEUTRAL,
                collisionVolume, collisionPitch);

        int sparkCount = (int) Mth.clamp(10.0 + intensity * 12.0, 10.0, 40.0);
        int emberCount = (int) Mth.clamp(4.0 + intensity * 5.0, 4.0, 16.0);
        int flameCount = (int) Mth.clamp(2.0 + intensity * 2.0, 2.0, 6.0);
        int debrisCount = debrisState != null ? (int) Mth.clamp(8.0 + intensity * 8.0, 8.0, 24.0) : 0;

        if (biasDirection == null) {

            serverLevel.sendParticles(SPARK_PARTICLE, x, y, z, sparkCount, 0.45, 0.4, 0.45, 0.3);
            serverLevel.sendParticles(ParticleTypes.LAVA, x, y, z, emberCount, 0.4, 0.35, 0.4, 0.0);
            serverLevel.sendParticles(ParticleTypes.FLAME, x, y, z, flameCount, 0.35, 0.3, 0.35, 0.03);
            if (debrisState != null) {
                serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, debrisState),
                        x, y, z, debrisCount, 0.4, 0.35, 0.4, 0.25);
            }
            return;
        }

        Vec3 dir = biasDirection.normalize();
        this.spawnDirectionalBurst(serverLevel, SPARK_PARTICLE, x, y, z, sparkCount, dir, 0.9 + intensity * 0.12);
        this.spawnDirectionalBurst(serverLevel, ParticleTypes.LAVA, x, y, z, emberCount, dir, 0.75 + intensity * 0.1);
        this.spawnDirectionalBurst(serverLevel, ParticleTypes.FLAME, x, y, z, flameCount, dir, 0.6 + intensity * 0.08);
        if (debrisState != null) {
            this.spawnDirectionalBurst(serverLevel, new BlockParticleOption(ParticleTypes.BLOCK, debrisState),
                    x, y, z, debrisCount, dir, 0.75 + intensity * 0.1);
        }
    }

    private void spawnDirectionalBurst(ServerLevel serverLevel, net.minecraft.core.particles.ParticleOptions type,
                                        double x, double y, double z, int count, Vec3 direction, double baseSpeed) {
        //? if >=1.19 {
        net.minecraft.util.RandomSource random = serverLevel.getRandom();
        //?} else
        /*java.util.Random random = serverLevel.getRandom();*/

        boolean offsetPosition = CarEntity.isSparkPositionOffsetEnabled();
        double horizontalLen = Math.hypot(direction.x, direction.z);
        double lateralX = horizontalLen > 1.0E-4 ? -direction.z / horizontalLen : 1.0;
        double lateralZ = horizontalLen > 1.0E-4 ? direction.x / horizontalLen : 0.0;
        for (int i = 0; i < count; i++) {
            double lateralSpread = 2.2;
            double forwardJitter = 0.25;
            double lateralAmount = (random.nextDouble() - 0.5) * lateralSpread;
            double forwardFactor = 0.7 + (random.nextDouble() - 0.5) * forwardJitter;
            double vx = direction.x * forwardFactor + lateralX * lateralAmount;
            double vz = direction.z * forwardFactor + lateralZ * lateralAmount;
            double vy = 0.35 + random.nextDouble() * 0.55;
            double speedScale = baseSpeed * (0.6 + random.nextDouble() * 0.9);
            double posLateral = 0.0;
            double posForward = 0.0;
            double posUp = 0.0;
            if (offsetPosition) {

                double fanT = count > 1 ? ((double) i / (count - 1)) - 0.5 : 0.0;
                posLateral = fanT * 2.6 + (random.nextDouble() - 0.5) * 0.5;
                posForward = random.nextDouble() * 0.3;
                posUp = random.nextDouble() * 0.9;
            }
            double posX = x + lateralX * posLateral + direction.x * posForward + (random.nextDouble() - 0.5) * 0.15;
            double posY = y + posUp + (random.nextDouble() - 0.5) * 0.15;
            double posZ = z + lateralZ * posLateral + direction.z * posForward + (random.nextDouble() - 0.5) * 0.15;
            serverLevel.sendParticles(type, posX, posY, posZ, 0,
                    vx * speedScale, vy * speedScale, vz * speedScale, 0.0);
        }
    }

    private void tryBreakCollidingBlocks(ServerLevel serverLevel) {
        double curX = ModEntity.getX(this);
        double curZ = ModEntity.getZ(this);
        if (!this.breakCheckPrevInitialized) {
            this.breakCheckPrevX = curX;
            this.breakCheckPrevZ = curZ;
            this.breakCheckPrevInitialized = true;
            return;
        }
        double speed = Math.hypot(curX - this.breakCheckPrevX, curZ - this.breakCheckPrevZ);
        this.breakCheckPrevX = curX;
        this.breakCheckPrevZ = curZ;

        this.tryDetectWallImpact(serverLevel, speed);

        this.recentImpactSpeed = Math.max(speed, this.recentImpactSpeed * 0.85);
        double effectiveSpeed = this.recentImpactSpeed;

        if (effectiveSpeed < BREAK_SPEED_THRESHOLD) {
            return;
        }

        float[] footprint = resolveFootprintWidthLength();
        float width = footprint[0];
        float length = footprint[1];
        float halfWidth = width / 2f;
        float halfLength = length / 2f;

        double centerX = ModEntity.getX(this);
        double centerZ = ModEntity.getZ(this);

        float yawRad = (float) Math.toRadians(this.getYRot());
        double forwardX = -Mth.sin(yawRad);
        double forwardZ = Mth.cos(yawRad);
        double rightX = forwardZ;
        double rightZ = -forwardX;

        double maxExtent = Math.sqrt((double) halfWidth * halfWidth + (double) halfLength * halfLength);

        double scanHalfWidth = halfWidth + COLLISION_WIDTH_MARGIN;
        double scanHalfLength = halfLength + COLLISION_LENGTH_MARGIN;
        double scanMaxExtent = Math.sqrt(scanHalfWidth * scanHalfWidth + scanHalfLength * scanHalfLength);
        int minBX = Mth.floor(centerX - scanMaxExtent);
        int maxBX = Mth.floor(centerX + scanMaxExtent);
        int minBZ = Mth.floor(centerZ - scanMaxExtent);
        int maxBZ = Mth.floor(centerZ + scanMaxExtent);
        int minBY = Mth.floor(ModEntity.getY(this));
        int maxBY = Mth.floor(ModEntity.getY(this) + this.getBbHeight());

        this.trySplashWaterPuddles(serverLevel, effectiveSpeed, minBX, maxBX, minBZ, maxBZ, minBY, maxBY,
                centerX, centerZ, forwardX, forwardZ, rightX, rightZ, scanHalfWidth, scanHalfLength);

        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        for (int bx = minBX; bx <= maxBX; bx++) {
            for (int bz = minBZ; bz <= maxBZ; bz++) {
                double dx = bx + 0.5 - centerX;
                double dz = bz + 0.5 - centerZ;

                double alongLength = dx * forwardX + dz * forwardZ;
                double alongWidth = dx * rightX + dz * rightZ;
                if (Math.abs(alongLength) > scanHalfLength || Math.abs(alongWidth) > scanHalfWidth) {
                    continue;
                }
                for (int by = minBY; by <= maxBY; by++) {
                    mutable.set(bx, by, bz);
                    BlockState state = serverLevel.getBlockState(mutable);
                    if (state.isAir() || !isCarBreakable(state)) {
                        continue;
                    }
                    boolean isDedicatedBreakableGlass = state.is(AiObjectModBlocks.CAR_BREAKABLE_GLASS);

                    if (!isDedicatedBreakableGlass && !breakVanillaBlocksEnabled) {
                        continue;
                    }

                    this.spawnImpactSparks(serverLevel,
                            mutable.getX() + 0.5, mutable.getY() + 0.5, mutable.getZ() + 0.5,
                            effectiveSpeed, state);
                    serverLevel.destroyBlock(mutable, true, this);
                    if (isDedicatedBreakableGlass) {
                        this.scheduleGlassShatterSpread(mutable, effectiveSpeed);
                    }
                }
            }
        }

        this.tryLaunchCollidingBreakables(serverLevel, effectiveSpeed, centerX, centerZ,
                forwardX, forwardZ, rightX, rightZ, halfWidth, halfLength, maxExtent);
        this.tryPushCollidingCars(serverLevel, effectiveSpeed, centerX, centerZ,
                forwardX, forwardZ, rightX, rightZ, halfWidth, halfLength, maxExtent);

        if (mobDamageEnabled) {
            this.tryDamageCollidingMobs(serverLevel, effectiveSpeed, centerX, centerZ,
                    forwardX, forwardZ, rightX, rightZ, halfWidth, halfLength, maxExtent);
        }
    }

    private static final double GLASS_SPREAD_FULL_SPEED = 0.9;
    private final java.util.Map<Long, Integer> pendingGlassShatters = new java.util.HashMap<>();

    private void scheduleGlassShatterSpread(BlockPos origin, double speed) {

        double t = Mth.clamp((speed - BREAK_SPEED_THRESHOLD) / (GLASS_SPREAD_FULL_SPEED - BREAK_SPEED_THRESHOLD), 0.0, 1.0);
        double nearChance = 0.25 + 0.5 * t;
        double farChance = 0.4 * t;
        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                for (int dz = -2; dz <= 2; dz++) {
                    int ring = Math.max(Math.abs(dx), Math.max(Math.abs(dy), Math.abs(dz)));
                    if (ring == 0) {
                        continue;
                    }
                    double chance = ring == 1 ? nearChance : farChance;
                    if (this.random.nextDouble() >= chance) {
                        continue;
                    }
                    long key = origin.offset(dx, dy, dz).asLong();

                    int delay = ring == 1 ? 2 + this.random.nextInt(2) : 4 + this.random.nextInt(3);
                    this.pendingGlassShatters.merge(key, delay, Math::min);
                }
            }
        }
    }

    private void tickPendingGlassShatters(ServerLevel serverLevel) {
        if (this.pendingGlassShatters.isEmpty()) {
            return;
        }
        java.util.Iterator<java.util.Map.Entry<Long, Integer>> it = this.pendingGlassShatters.entrySet().iterator();
        while (it.hasNext()) {
            java.util.Map.Entry<Long, Integer> entry = it.next();
            int remaining = entry.getValue() - 1;
            if (remaining > 0) {
                entry.setValue(remaining);
                continue;
            }
            it.remove();
            BlockPos pos = BlockPos.of(entry.getKey());

            if (serverLevel.getBlockState(pos).getBlock() == AiObjectModBlocks.CAR_BREAKABLE_GLASS) {
                serverLevel.destroyBlock(pos, true);
            }
        }
    }

    private void trySplashWaterPuddles(ServerLevel serverLevel, double effectiveSpeed,
                                        int minBX, int maxBX, int minBZ, int maxBZ, int minBY, int maxBY,
                                        double centerX, double centerZ,
                                        double forwardX, double forwardZ, double rightX, double rightZ,
                                        double scanHalfWidth, double scanHalfLength) {
        if (this.puddleSplashCooldown > 0) {
            this.puddleSplashCooldown--;
            return;
        }
        boolean splashed = false;
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        for (int bx = minBX; bx <= maxBX; bx++) {
            for (int bz = minBZ; bz <= maxBZ; bz++) {
                double dx = bx + 0.5 - centerX;
                double dz = bz + 0.5 - centerZ;
                double alongLength = dx * forwardX + dz * forwardZ;
                double alongWidth = dx * rightX + dz * rightZ;
                if (Math.abs(alongLength) > scanHalfLength || Math.abs(alongWidth) > scanHalfWidth) {
                    continue;
                }
                for (int by = minBY; by <= maxBY; by++) {
                    mutable.set(bx, by, bz);
                    if (serverLevel.getBlockState(mutable).is(AiObjectModBlocks.WATER_PUDDLE)) {
                        this.spawnWaterSplash(serverLevel,
                                mutable.getX() + 0.5, mutable.getY() + 0.15, mutable.getZ() + 0.5, effectiveSpeed);
                        splashed = true;
                    }
                }
            }
        }
        if (splashed) {
            this.puddleSplashCooldown = PUDDLE_SPLASH_COOLDOWN_TICKS;
        }
    }

    private void spawnWaterSplash(ServerLevel serverLevel, double x, double y, double z, double intensity) {
        float volume = (float) Mth.clamp(0.7 + intensity * 0.15, 0.7, 1.8);
        float pitch = 0.8f + serverLevel.getRandom().nextFloat() * 0.3f;
        serverLevel.playSound(null, x, y, z, SoundEvents.GENERIC_SPLASH, SoundSource.NEUTRAL, volume, pitch);

        int splashCount = (int) Mth.clamp(30.0 + intensity * 30.0, 30.0, 100.0);
        int dropletCount = (int) Mth.clamp(10.0 + intensity * 12.0, 10.0, 32.0);
        int bubbleCount = (int) Mth.clamp(6.0 + intensity * 6.0, 6.0, 18.0);

        serverLevel.sendParticles(ParticleTypes.SPLASH, x, y, z, splashCount, 0.5, 0.2, 0.5, 0.6);
        serverLevel.sendParticles(ParticleTypes.FALLING_WATER, x, y + 0.15, z, dropletCount, 0.45, 0.35, 0.45, 0.12);
        serverLevel.sendParticles(ParticleTypes.BUBBLE_POP, x, y, z, bubbleCount, 0.35, 0.15, 0.35, 0.1);
    }

    private void tryDamageCollidingMobs(ServerLevel serverLevel, double speed, double centerX, double centerZ,
                                         double forwardX, double forwardZ, double rightX, double rightZ,
                                         float halfWidth, float halfLength, double maxExtent) {
        AABB searchBox = new AABB(
                centerX - maxExtent, ModEntity.getY(this) - 1.0, centerZ - maxExtent,
                centerX + maxExtent, ModEntity.getY(this) + this.getBbHeight() + 1.0, centerZ + maxExtent);
        List<Mob> nearby = serverLevel.getEntitiesOfClass(Mob.class, searchBox);
        if (nearby.isEmpty()) {
            return;
        }
        float damage = (float) (speed / CLASSIC_TUNING.maxForwardSpeed() * MOB_DAMAGE_REFERENCE_DAMAGE);
        for (Mob target : nearby) {
            if (this.getPassengers().contains(target)) {
                continue;
            }
            double dx = ModEntity.getX(target) - centerX;
            double dz = ModEntity.getZ(target) - centerZ;
            double alongLength = dx * forwardX + dz * forwardZ;
            double alongWidth = dx * rightX + dz * rightZ;
            if (Math.abs(alongLength) > halfLength || Math.abs(alongWidth) > halfWidth) {
                continue;
            }
            //? if >=1.19.4 {
            DamageSource genericDamage = serverLevel.damageSources().generic();
            //?} else
            /*DamageSource genericDamage = DamageSource.GENERIC;*/
            //? if >=1.21.2 {
            target.hurtServer(serverLevel, genericDamage, damage);
            //?} else
            /*target.hurt(genericDamage, damage);*/
        }
    }

    private void tryLaunchCollidingBreakables(ServerLevel serverLevel, double speed, double centerX, double centerZ,
                                               double forwardX, double forwardZ, double rightX, double rightZ,
                                               float halfWidth, float halfLength, double maxExtent) {
        AABB searchBox = new AABB(
                centerX - maxExtent, ModEntity.getY(this) - 1.0, centerZ - maxExtent,
                centerX + maxExtent, ModEntity.getY(this) + this.getBbHeight() + BREAKABLE_LAUNCH_VERTICAL_MARGIN, centerZ + maxExtent);
        List<BreakableTireStackEntity> nearby =
                serverLevel.getEntitiesOfClass(BreakableTireStackEntity.class, searchBox);
        for (BreakableTireStackEntity target : nearby) {
            if (target.getLaunchCooldown() > 0) {
                continue;
            }
            double dx = ModEntity.getX(target) - centerX;
            double dz = ModEntity.getZ(target) - centerZ;
            double alongLength = dx * forwardX + dz * forwardZ;
            double alongWidth = dx * rightX + dz * rightZ;
            if (Math.abs(alongLength) > halfLength || Math.abs(alongWidth) > halfWidth) {
                continue;
            }

            double launchSpeed = speed * BREAKABLE_LAUNCH_SPEED_MULTIPLIER;
            double sideScatter = alongWidth >= 0 ? 1.0 : -1.0;
            double launchX = forwardX * launchSpeed + rightX * sideScatter * launchSpeed * 0.3;
            double launchZ = forwardZ * launchSpeed + rightZ * sideScatter * launchSpeed * 0.3;
            target.onLaunched(serverLevel,
                    new Vec3(launchX, BREAKABLE_LAUNCH_UPWARD_BASE + speed * 0.5, launchZ));
        }
    }

    private void tryPushCollidingCars(ServerLevel serverLevel, double speed, double centerX, double centerZ,
                                       double forwardX, double forwardZ, double rightX, double rightZ,
                                       float halfWidth, float halfLength, double maxExtent) {
        double searchMargin = maxExtent + 3.0;
        AABB searchBox = new AABB(
                centerX - searchMargin, ModEntity.getY(this) - 1.0, centerZ - searchMargin,
                centerX + searchMargin, ModEntity.getY(this) + this.getBbHeight() + 1.0, centerZ + searchMargin);

        boolean thisPlayerDriven = (this.getControllingPassenger() instanceof Player) && !this.mineturismo$isNpcDrivenWithPassenger();
        List<CarEntity> nearby = serverLevel.getEntitiesOfClass(CarEntity.class, searchBox,
                other -> other != this && !this.isInSameRailwayConsist(other) && (thisPlayerDriven
                        || ((other.getControllingPassenger() instanceof Player) && !other.mineturismo$isNpcDrivenWithPassenger())));
        for (CarEntity other : nearby) {
            if (other.collisionPushCooldown > 0) {
                continue;
            }
            float otherCarScale = other.getCarScale();
            float otherWidth = CarFootprint.getWidth(com.examplebett.compat.ModResources.getServerResourceManager(serverLevel.getServer()), other.getModelId()) * otherCarScale;
            float otherLength = CarFootprint.getLength(com.examplebett.compat.ModResources.getServerResourceManager(serverLevel.getServer()), other.getModelId()) * otherCarScale;
            double dx = ModEntity.getX(other) - centerX;
            double dz = ModEntity.getZ(other) - centerZ;
            double alongLength = dx * forwardX + dz * forwardZ;
            double alongWidth = dx * rightX + dz * rightZ;
            double combinedHalfLength = halfLength + otherLength / 2.0 + COLLISION_LENGTH_MARGIN;
            double combinedHalfWidth = halfWidth + otherWidth / 2.0 + COLLISION_WIDTH_MARGIN;
            if (Math.abs(alongLength) > combinedHalfLength || Math.abs(alongWidth) > combinedHalfWidth) {
                continue;
            }

            double speedBeyondThreshold = Math.max(0.0, speed - CAR_PUSH_HIGH_SPEED_THRESHOLD);
            double pushMagnitude = speed * CAR_PUSH_SPEED_MULTIPLIER
                    + speedBeyondThreshold * speedBeyondThreshold * CAR_PUSH_HIGH_SPEED_BOOST;

            pushMagnitude = Math.min(pushMagnitude, CAR_PUSH_MAX_SLIDE) * pushDistanceMultiplier;

            double dist = Math.hypot(dx, dz);
            double pushDirX;
            double pushDirZ;
            if (dist > 1.0E-4) {
                pushDirX = dx / dist;
                pushDirZ = dz / dist;
            } else {
                pushDirX = forwardX;
                pushDirZ = forwardZ;
            }
            double pushX = pushDirX * pushMagnitude;
            double pushZ = pushDirZ * pushMagnitude;
            other.collisionSlideX += pushX;
            other.collisionSlideZ += pushZ;

            double verticalBeyond = Math.max(0.0, speed - CAR_PUSH_LAUNCH_UPWARD_THRESHOLD);
            if (verticalBeyond > 0.0) {
                double upwardBoost = verticalBeyond * CAR_PUSH_LAUNCH_UPWARD_FACTOR;
                other.setDeltaMovement(other.getDeltaMovement().add(0.0, upwardBoost, 0.0));
            }

            float otherYawRad = (float) Math.toRadians(other.getYRot());
            double otherForwardX = -Mth.sin(otherYawRad);
            double otherForwardZ = Mth.cos(otherYawRad);
            double otherHalfLength = otherLength / 2.0;
            double towardImpactX = centerX - ModEntity.getX(other);
            double towardImpactZ = centerZ - ModEntity.getZ(other);
            double frontBackOffset = towardImpactX * otherForwardX + towardImpactZ * otherForwardZ;
            double frontBackFraction = otherHalfLength > 1.0E-4
                    ? Mth.clamp(frontBackOffset / otherHalfLength, -1.0, 1.0)
                    : 0.0;
            double lateralSign = alongWidth >= 0.0 ? 1.0 : -1.0;

            double otherRightX = otherForwardZ;
            double otherRightZ = -otherForwardX;
            double flightForwardComponent = pushDirX * otherForwardX + pushDirZ * otherForwardZ;
            double flightLateralComponent = pushDirX * otherRightX + pushDirZ * otherRightZ;
            double flightForwardSign = flightForwardComponent >= 0.0 ? 1.0 : -1.0;
            double flightLateralSign = flightLateralComponent >= 0.0 ? 1.0 : -1.0;

            boolean forceRotate = (forceRotateSideEnabled || forceRotateFrontEnabled)
                    && other.getControllingPassenger() == null;
            boolean tumbleTriggered = forceRotate
                    || (speed >= CAR_TUMBLE_MIN_SPEED_BLOCKS_PER_TICK
                            && serverLevel.getRandom().nextDouble() < CAR_TUMBLE_CHANCE);
            boolean useFrontFlip;
            if (forceRotate && forceRotateFrontEnabled) {
                useFrontFlip = true;
            } else if (forceRotate && forceRotateSideEnabled) {
                useFrontFlip = false;
            } else if (tumbleTriggered) {
                double endFactor = Math.abs(frontBackFraction);
                useFrontFlip = serverLevel.getRandom().nextDouble()
                        < CAR_TUMBLE_FRONT_FLIP_MAX_CHANCE * endFactor;
            } else {
                useFrontFlip = false;
            }

            if (!tumbleTriggered) {
                other.collisionSpin += frontBackFraction * lateralSign * pushMagnitude * CAR_PUSH_SPIN_FACTOR;
            }

            if (tumbleTriggered) {

                double targetRotationDegrees = (useFrontFlip
                        ? CAR_TUMBLE_FRONT_FLIP_FIXED_ROTATION_DEGREES
                        : CAR_TUMBLE_TARGET_ROTATION_MIN_DEGREES
                                + serverLevel.getRandom().nextDouble()
                                * (CAR_TUMBLE_TARGET_ROTATION_MAX_DEGREES - CAR_TUMBLE_TARGET_ROTATION_MIN_DEGREES))
                        * tumbleRotationMultiplier;
                double tumbleMagnitude = targetRotationDegrees * (1.0 - CAR_TUMBLE_AIR_DECAY);

                double tumbleSign = useFrontFlip ? flightForwardSign : flightLateralSign;

                other.tumbleAngularVelocity = -tumbleSign * tumbleMagnitude;
                other.tumbleUsesRollAxis = !useFrontFlip;
                other.tumbleTargetRotationDegrees = targetRotationDegrees;

                other.collisionSlideX *= CAR_TUMBLE_HORIZONTAL_SLIDE_SCALE;
                other.collisionSlideZ *= CAR_TUMBLE_HORIZONTAL_SLIDE_SCALE;

                Vec3 tumbleMotion = other.getDeltaMovement();
                double tumbleUpwardVelocity = Math.max(tumbleMotion.y,
                        CAR_TUMBLE_LAUNCH_UPWARD_VELOCITY * tumbleHeightMultiplier);
                other.setDeltaMovement(tumbleMotion.x, tumbleUpwardVelocity, tumbleMotion.z);
            }

            double impactX = (centerX + ModEntity.getX(other)) / 2.0;
            double impactZ = (centerZ + ModEntity.getZ(other)) / 2.0;
            double impactY = ModEntity.getY(this) + this.getBodyHeightOffset() + this.scaledHitboxHeight() / 2.0;

            double thisFrontBackFraction = halfLength > 1.0E-4 ? Mth.clamp(alongLength / halfLength, -1.0, 1.0) : 0.0;
            boolean sideImpact = Math.abs(frontBackFraction) < SIDE_IMPACT_FRONT_BACK_FRACTION
                    || Math.abs(thisFrontBackFraction) < SIDE_IMPACT_FRONT_BACK_FRACTION;
            boolean lowCollisionSound = collisionSoundFor(speed, sideImpact) == AiObjectModSounds.CAR_COLLISION_LOW;
            this.spawnImpactSparks(serverLevel, impactX, impactY, impactZ, pushMagnitude, null, null,
                    collisionSoundFor(speed, sideImpact));
            if (serverLevel.getRandom().nextInt(GLASS_BREAK_SOUND_CHANCE) == 0) {
                float glassPitch = 0.9f + serverLevel.getRandom().nextFloat() * 0.2f;
                serverLevel.playSound(null, impactX, impactY, impactZ, SoundEvents.GLASS_BREAK, SoundSource.NEUTRAL,
                        1.0f, glassPitch);
            }

            float notifySoundVolume = collisionSoundVolume(pushMagnitude);
            float notifySoundPitch = 0.9f + serverLevel.getRandom().nextFloat() * 0.2f;
            PlayCollisionSoundPayload collisionSoundPayload =
                    new PlayCollisionSoundPayload(impactX, impactY, impactZ, notifySoundVolume, notifySoundPitch, lowCollisionSound);
            if (this.getControllingPassenger() instanceof ServerPlayer thisDriver) {
                //? if >=1.20.5 {
                ModNetworking.sendToClient(thisDriver, collisionSoundPayload);
                //?} else
                /*ModNetworking.sendToClient(thisDriver, PlayCollisionSoundPayload.ID, collisionSoundPayload, PlayCollisionSoundPayload::write);*/
            }
            if (other.getControllingPassenger() instanceof ServerPlayer otherDriver) {
                //? if >=1.20.5 {
                ModNetworking.sendToClient(otherDriver, collisionSoundPayload);
                //?} else
                /*ModNetworking.sendToClient(otherDriver, PlayCollisionSoundPayload.ID, collisionSoundPayload, PlayCollisionSoundPayload::write);*/
            }

            if (securityAlarmEnabled && other.getPassengers().isEmpty()) {

                if (other.securityAlarmTicksRemaining <= 0) {
                    other.securityAlarmVariant = other.random.nextInt(SECURITY_ALARM_VARIANT_COUNT);
                    other.securityAlarmSoundTicks = 0;
                }
                other.securityAlarmTicksRemaining = SECURITY_ALARM_DURATION_TICKS;
            }

            other.collisionPushCooldown = CAR_PUSH_COOLDOWN_TICKS;
        }
    }

    private void applyCollisionSlide() {
        if (this.collisionPushCooldown > 0) {
            this.collisionPushCooldown--;
        }
        if (Math.abs(this.collisionSlideX) < 1.0E-4 && Math.abs(this.collisionSlideZ) < 1.0E-4) {
            this.collisionSlideX = 0.0;
            this.collisionSlideZ = 0.0;
        } else {
            this.move(MoverType.SELF, new Vec3(this.collisionSlideX, 0.0, this.collisionSlideZ));
            this.collisionSlideX *= 0.75;
            this.collisionSlideZ *= 0.75;
        }

        if (Math.abs(this.collisionSpin) < 1.0E-3) {
            this.collisionSpin = 0.0;
        } else {

            this.classicHeading -= (float) this.collisionSpin;
            this.setYRot(this.getYRot() + (float) this.collisionSpin);
            this.yRotO = this.yBodyRot = this.yHeadRot = this.getYRot();
            this.collisionSpin *= CAR_PUSH_SPIN_DECAY;
        }

        boolean tumbling = Math.abs(this.tumbleAngularVelocity) > 1.0E-3
                || this.getTumblePitchDegrees() != 0.0f || this.getTumbleRollDegrees() != 0.0f;
        if (tumbling) {
            if (this.onGround() && Math.abs(this.tumbleAngularVelocity) < CAR_TUMBLE_SETTLE_ANGULAR_EPSILON) {

                this.tumbleAngularVelocity = 0.0;
                this.entityData.set(TUMBLE_PITCH, 0f);
                this.entityData.set(TUMBLE_ROLL, 0f);
            } else {
                EntityDataAccessor<Float> axis = this.tumbleUsesRollAxis ? TUMBLE_ROLL : TUMBLE_PITCH;
                float newAngle = this.entityData.get(axis) + (float) this.tumbleAngularVelocity;

                float rotationLimit = (float) this.tumbleTargetRotationDegrees;
                if (Math.abs(newAngle) >= rotationLimit) {
                    newAngle = Math.signum(newAngle) * rotationLimit;
                    this.tumbleAngularVelocity = 0.0;
                } else {
                    this.tumbleAngularVelocity *= this.onGround() ? CAR_TUMBLE_GROUND_DECAY : CAR_TUMBLE_AIR_DECAY;
                }
                this.entityData.set(axis, newAngle);
            }
        }
    }

    private void updateWheelieState() {

        if (this.handbrakeHeld) {
            this.wheelieHoldTicksRemaining = 0;
        }
        if (this.wheelieHoldTicksRemaining > 0) {
            this.wheelieHoldTicksRemaining--;
        }
        boolean motorcycleWheelieActive = this.sprintKeyHeld && !this.isBikeStationary()
                && this.hasVehicleCategory(VehicleCategory.MOTORCYCLE);

        boolean muscleCarSimpleWheelieActive = this.sprintKeyHeld
                && this.hasVehicleCategory(VehicleCategory.MUSCLE_CAR);
        float wheelieTarget = this.wheelieHoldTicksRemaining > 0 || motorcycleWheelieActive || muscleCarSimpleWheelieActive
                ? WHEELIE_MAX_PITCH_DEG : 0.0F;
        float currentPitch = this.entityData.get(WHEELIE_PITCH);
        if (currentPitch < wheelieTarget) {
            currentPitch = Math.min(currentPitch + WHEELIE_RISE_STEP_DEG, wheelieTarget);
        } else if (currentPitch > wheelieTarget) {
            currentPitch = Math.max(currentPitch - WHEELIE_FALL_STEP_DEG, wheelieTarget);
        }
        if (currentPitch != this.entityData.get(WHEELIE_PITCH)) {
            this.entityData.set(WHEELIE_PITCH, currentPitch);
        }
    }

    private void updateBikeLeanState() {
        if (!this.hasVehicleCategory(VehicleCategory.MOTORCYCLE)) {
            this.bikeLeanPrevYawInitialized = false;
            this.bikeLeanPrevPosInitialized = false;
            this.bikeStationaryLowMovementTicks = 0;
            this.bikeStationaryHighMovementTicks = 0;
            if (this.entityData.get(BIKE_LEAN_ROLL) != 0f) {
                this.entityData.set(BIKE_LEAN_ROLL, 0f);
            }
            if (this.entityData.get(BIKE_STATIONARY)) {
                this.entityData.set(BIKE_STATIONARY, false);
            }
            return;
        }
        float currentYaw = this.getYRot();
        float yawRate = this.bikeLeanPrevYawInitialized ? Mth.wrapDegrees(currentYaw - this.bikeLeanPrevYaw) : 0.0F;
        this.bikeLeanPrevYaw = currentYaw;
        this.bikeLeanPrevYawInitialized = true;

        double curX = ModEntity.getX(this);
        double curZ = ModEntity.getZ(this);
        boolean belowThreshold = true;
        if (this.bikeLeanPrevPosInitialized) {
            double dx = curX - this.bikeLeanPrevX;
            double dz = curZ - this.bikeLeanPrevZ;
            belowThreshold = (dx * dx + dz * dz) < BIKE_STATIONARY_SPEED_THRESHOLD * BIKE_STATIONARY_SPEED_THRESHOLD;
        }
        this.bikeLeanPrevX = curX;
        this.bikeLeanPrevZ = curZ;
        this.bikeLeanPrevPosInitialized = true;

        boolean stationary = this.entityData.get(BIKE_STATIONARY);
        if (belowThreshold) {
            this.bikeStationaryHighMovementTicks = 0;
            this.bikeStationaryLowMovementTicks++;
            if (!stationary && this.bikeStationaryLowMovementTicks >= BIKE_STATIONARY_ENTER_TICKS) {
                stationary = true;
            }
        } else {
            this.bikeStationaryLowMovementTicks = 0;
            this.bikeStationaryHighMovementTicks++;
            if (stationary && this.bikeStationaryHighMovementTicks >= BIKE_STATIONARY_EXIT_TICKS) {
                stationary = false;
            }
        }
        if (this.entityData.get(BIKE_STATIONARY) != stationary) {
            this.entityData.set(BIKE_STATIONARY, stationary);
        }

        float leanTarget;
        if (stationary) {

            leanTarget = PARKED_LEAN_DEGREES * BIKE_LEAN_SIGN;
        } else if (yawRate > BIKE_LEAN_YAW_RATE_DEADZONE_DEG) {
            leanTarget = MAX_BIKE_LEAN_DEGREES * BIKE_LEAN_SIGN;
        } else if (yawRate < -BIKE_LEAN_YAW_RATE_DEADZONE_DEG) {
            leanTarget = -MAX_BIKE_LEAN_DEGREES * BIKE_LEAN_SIGN;
        } else {
            leanTarget = 0.0F;
        }
        float currentLean = this.entityData.get(BIKE_LEAN_ROLL);
        float leanDelta = leanTarget - currentLean;
        float newLean;
        if (Mth.abs(leanDelta) <= BIKE_LEAN_SPEED_DEG_PER_TICK) {
            newLean = leanTarget;
        } else {
            newLean = currentLean + Math.signum(leanDelta) * BIKE_LEAN_SPEED_DEG_PER_TICK;
        }
        if (newLean != currentLean) {
            this.entityData.set(BIKE_LEAN_ROLL, newLean);
        }
    }

    private void updateSuspensionSwayState() {
        if (this.hasVehicleCategory(VehicleCategory.MOTORCYCLE)) {
            this.suspensionPrevPosInitialized = false;
            this.suspensionPrevYawInitialized = false;
            this.suspensionSettleTicksRemaining = SUSPENSION_SETTLE_TICKS;
            this.suspensionPendingCatchup = false;
            this.suspensionSmoothedForwardSpeed = 0.0F;
            this.suspensionSmoothedYawRate = 0.0F;
            this.suspensionSmoothedAccel = 0.0F;
            if (this.entityData.get(SUSPENSION_PITCH) != 0f) {
                this.entityData.set(SUSPENSION_PITCH, 0f);
            }
            if (this.entityData.get(SUSPENSION_ROLL) != 0f) {
                this.entityData.set(SUSPENSION_ROLL, 0f);
            }
            return;
        }

        double curX = ModEntity.getX(this);
        double curZ = ModEntity.getZ(this);
        float currentYaw = this.getYRot();

        float yawRad = (float) Math.toRadians(currentYaw);
        double forwardX = this.isHeadingBasedDriveMode() ? Mth.sin(yawRad) : -Mth.sin(yawRad);
        double forwardZ = Mth.cos(yawRad);

        boolean settling = this.suspensionSettleTicksRemaining > 0;
        if (settling) {
            this.suspensionSettleTicksRemaining--;
        }

        float rawForwardSpeed = 0.0F;
        if (this.suspensionPrevPosInitialized && !settling) {
            double dx = curX - this.suspensionPrevX;
            double dz = curZ - this.suspensionPrevZ;
            rawForwardSpeed = (float) (dx * forwardX + dz * forwardZ);
        }
        this.suspensionPrevX = curX;
        this.suspensionPrevZ = curZ;
        this.suspensionPrevPosInitialized = true;

        float rawYawRate = (this.suspensionPrevYawInitialized && !settling)
                ? Mth.wrapDegrees(currentYaw - this.suspensionPrevYaw) : 0.0F;
        this.suspensionPrevYaw = currentYaw;
        this.suspensionPrevYawInitialized = true;

        boolean frozenCandidate = !settling && rawForwardSpeed == 0.0F
                && Mth.abs(this.suspensionSmoothedForwardSpeed) >= SUSPENSION_STATIONARY_SPEED_THRESHOLD;

        this.suspensionFrozenTicks = frozenCandidate ? this.suspensionFrozenTicks + 1 : 0;
        boolean looksFrozen = frozenCandidate && this.suspensionFrozenTicks <= SUSPENSION_MAX_FROZEN_TICKS;
        if (frozenCandidate && !looksFrozen) {
            this.suspensionPendingCatchup = false;
        }
        if (looksFrozen) {
            this.suspensionPendingCatchup = true;
        } else if (this.suspensionPendingCatchup) {
            rawForwardSpeed *= 0.5F;
            rawYawRate *= 0.5F;
            this.suspensionPendingCatchup = false;
        }

        if (!looksFrozen) {

            this.suspensionSpeedWindow[this.suspensionSpeedWindowIndex] = rawForwardSpeed;
            this.suspensionSpeedWindowIndex = (this.suspensionSpeedWindowIndex + 1) % SUSPENSION_SPEED_WINDOW_TICKS;
            this.suspensionSpeedWindowCount = Math.min(this.suspensionSpeedWindowCount + 1, SUSPENSION_SPEED_WINDOW_TICKS);
            float windowSum = 0.0F;
            for (int i = 0; i < this.suspensionSpeedWindowCount; i++) {
                windowSum += this.suspensionSpeedWindow[i];
            }
            float windowAvgSpeed = windowSum / this.suspensionSpeedWindowCount;
            this.suspensionSmoothedForwardSpeed += (windowAvgSpeed - this.suspensionSmoothedForwardSpeed) * SUSPENSION_SPEED_SMOOTHING;
            this.suspensionSmoothedYawRate += (rawYawRate - this.suspensionSmoothedYawRate) * SUSPENSION_YAW_RATE_SMOOTHING;
        }
        float forwardSpeed = this.suspensionSmoothedForwardSpeed;
        float yawRate = this.suspensionSmoothedYawRate;

        boolean wasNearStationary = Mth.abs(this.suspensionPrevForwardSpeed) < SUSPENSION_STATIONARY_SPEED_THRESHOLD;

        float rawAccel = forwardSpeed - this.suspensionPrevForwardSpeed;
        this.suspensionPrevForwardSpeed = forwardSpeed;
        this.suspensionSmoothedAccel += (rawAccel - this.suspensionSmoothedAccel) * SUSPENSION_ACCEL_SMOOTHING;
        float accel = this.suspensionSmoothedAccel;

        float intensity = getSuspensionIntensityMultiplier();

        if (this.hasVehicleCategory(VehicleCategory.SUPERCAR)) {
            intensity *= SUSPENSION_SUPERCAR_FACTOR;
        } else if (this.hasVehicleCategory(VehicleCategory.SPORTSCAR)) {
            intensity *= SUSPENSION_SPORTSCAR_FACTOR;
        }
        float maxPitch = MAX_SUSPENSION_PITCH_DEGREES * intensity;
        float maxRoll = MAX_SUSPENSION_ROLL_DEGREES * intensity;

        float rawPitchTarget = wasNearStationary || Mth.abs(accel) < SUSPENSION_ACCEL_DEADZONE ? 0.0F
                : Mth.clamp(-accel * SUSPENSION_PITCH_GAIN_DEG * intensity, -maxPitch, maxPitch);

        float held = this.suspensionPitchHeld * SUSPENSION_PITCH_WASHOUT_DECAY;
        float targetDelta = rawPitchTarget - this.suspensionPitchTargetPrev;
        if (rawPitchTarget != 0.0F && held * rawPitchTarget >= 0.0F
                && Mth.abs(rawPitchTarget) > Mth.abs(this.suspensionPitchTargetPrev)
                && targetDelta * rawPitchTarget > 0.0F) {
            held += targetDelta;
        }
        if (Mth.abs(rawPitchTarget) < Mth.abs(held) || held * rawPitchTarget < 0.0F) {
            held = rawPitchTarget;
        }
        this.suspensionPitchHeld = held;
        this.suspensionPitchTargetPrev = rawPitchTarget;
        float pitchTarget = Mth.clamp(held, -maxPitch, maxPitch);
        float rollTarget = Mth.abs(yawRate) < SUSPENSION_YAW_RATE_DEADZONE_DEG ? 0.0F
                : Mth.clamp(yawRate * SUSPENSION_ROLL_GAIN * SUSPENSION_ROLL_SIGN * intensity, -maxRoll, maxRoll);

        float currentPitch = this.entityData.get(SUSPENSION_PITCH);
        float newPitch = stepToward(currentPitch, pitchTarget, SUSPENSION_ANGLE_SPEED_DEG_PER_TICK);
        if (newPitch != currentPitch) {
            this.entityData.set(SUSPENSION_PITCH, newPitch);
        }

        float currentRoll = this.entityData.get(SUSPENSION_ROLL);
        float newRoll = stepToward(currentRoll, rollTarget, SUSPENSION_ANGLE_SPEED_DEG_PER_TICK);
        if (newRoll != currentRoll) {
            this.entityData.set(SUSPENSION_ROLL, newRoll);
        }
    }

    private static float stepToward(float current, float target, float maxDelta) {
        float delta = target - current;
        if (Mth.abs(delta) <= maxDelta) {
            return target;
        }
        return current + Math.signum(delta) * maxDelta;
    }

    private void updateChaseSpawning(ServerLevel serverLevel) {
        if (this.chaseSpawnCooldown > 0) {
            this.chaseSpawnCooldown--;
            return;
        }
        AABB searchBox = this.getBoundingBox().inflate(CHASE_SPAWN_DISTANCE_MAX + CHASE_SPAWN_LATERAL_SPREAD);
        int activeCount = serverLevel.getEntitiesOfClass(CarEntity.class, searchBox,
                other -> other.chaseCarActive && other.chaseTargetCar == this).size();
        if (activeCount < CHASE_MAX_CONCURRENT_PER_TARGET) {
            this.spawnChaseCar(serverLevel);
        }
        this.chaseSpawnCooldown = CHASE_SPAWN_INTERVAL_MIN_TICKS
                + this.random.nextInt(CHASE_SPAWN_INTERVAL_MAX_TICKS - CHASE_SPAWN_INTERVAL_MIN_TICKS + 1);
    }

    private void spawnChaseCar(ServerLevel serverLevel) {

        float yawRad = (float) Math.toRadians(this.getYRot());
        double forwardX = Mth.sin(yawRad);
        double forwardZ = Mth.cos(yawRad);
        double rightX = forwardZ;
        double rightZ = -forwardX;
        double distance = CHASE_SPAWN_DISTANCE_MIN
                + this.random.nextDouble() * (CHASE_SPAWN_DISTANCE_MAX - CHASE_SPAWN_DISTANCE_MIN);
        double lateral = (this.random.nextDouble() - 0.5) * 2.0 * CHASE_SPAWN_LATERAL_SPREAD;
        double x = ModEntity.getX(this) + forwardX * distance + rightX * lateral;
        double z = ModEntity.getZ(this) + forwardZ * distance + rightZ * lateral;

        double y = ModEntity.getY(this);

        CarEntity chaseCar = null;
        if (this.gttActive) {

            chaseCar = VehiclePresets.create(serverLevel, x, y, z, VehiclePresets.FBI_CHALLENGER);
            if (chaseCar != null) {
                chaseCar.setPoliceLightsOn(true);
                chaseCar.setPoliceSirenOn(true);
            }
        }
        if (chaseCar == null) {
            String modelId = AiCarPool.pickRandom(excludeHelicopterModelId(serverLevel), this.random);
            chaseCar = new CarEntity(serverLevel, x, y, z, modelId);
        }

        chaseCar.setDriveMode(DriveMode.CLASSIC);
        chaseCar.chaseCarActive = true;
        chaseCar.chaseTargetCar = this;
        chaseCar.chaseState = ChaseState.CHARGE;

        double towardTargetX = ModEntity.getX(this) - x;
        double towardTargetZ = ModEntity.getZ(this) - z;
        float initialHeading = (float) Math.toDegrees(Math.atan2(-towardTargetX, towardTargetZ));
        chaseCar.classicHeading = initialHeading;
        chaseCar.classicHeadingInitialized = true;
        chaseCar.setRot(-initialHeading, 0.0F);

        serverLevel.addFreshEntity(chaseCar);
    }

    private static final int GTT_FIRST_CHASE_DELAY_TICKS = 40;

    public boolean isGttActive() {
        return this.gttActive;
    }

    private static final int GTT_POINTS_POLICE_DESTROYED = 500;
    private static final int GTT_POINTS_POLICE_WRECKED_BY_PLAYER = -500;
    private static final int GTT_POINTS_BUMP = -100;
    private static final int GTT_POINTS_NO_HIT = 100;
    private static final int GTT_NO_HIT_BONUS_TICKS = 200;

    private static final double GTT_WRECK_IMPACT_SPEED = 0.6;

    private static final int GTT_BUMP_COOLDOWN_TICKS = 20;

    public void startGtt() {
        this.gttActive = true;
        this.chaseSpawnCooldown = GTT_FIRST_CHASE_DELAY_TICKS;
        this.gttScore = 0;
        this.gttNoHitTicks = 0;
        this.sendGttScore();
    }

    private void addGttScoreIfActive(int delta) {
        if (this.gttActive) {
            this.addGttScore(delta);
        }
    }

    private void addGttScore(int delta) {
        this.gttScore += delta;
        this.sendGttScore();
    }

    private void sendGttScore() {
        if (this.getControllingPassenger() instanceof net.minecraft.server.level.ServerPlayer player) {
            com.examplebett.network.GttScorePayload payload = new com.examplebett.network.GttScorePayload(this.gttScore);
            //? if >=1.20.5 {
            com.examplebett.compat.ModNetworking.sendToClient(player, payload);
            //?} else
            /*com.examplebett.compat.ModNetworking.sendToClient(player, com.examplebett.network.GttScorePayload.ID, payload, com.examplebett.network.GttScorePayload::write);*/
        }
    }

    private static final double RESPONDER_STOP_DISTANCE = 7.0;

    private static final double RESPONDER_SLOW_DISTANCE = 18.0;

    private static final int RESPONDER_TIMEOUT_TICKS = 1200;

    public void startResponding(Player target) {
        this.responderActive = true;
        this.responderTarget = target;
        this.responderTicks = 0;
        this.setDriveMode(DriveMode.CLASSIC);
        this.setPoliceLightsOn(true);
        this.setPoliceSirenOn(true);

        double towardX = ModEntity.getX(target) - ModEntity.getX(this);
        double towardZ = ModEntity.getZ(target) - ModEntity.getZ(this);
        float initialHeading = (float) Math.toDegrees(Math.atan2(-towardX, towardZ));
        this.classicHeading = initialHeading;
        this.classicHeadingInitialized = true;
        this.setRot(-initialHeading, 0.0F);
    }

    private void finishResponding() {
        this.responderActive = false;
        this.responderTarget = null;
        this.aiThrottleDir = 0.0F;
        this.aiSteerDir = 0.0F;
        this.handbrakeHeld = false;

        this.setPoliceSirenOn(false);
    }

    private static double horizontalLength(Vec3 v) {
        return Math.sqrt(v.x * v.x + v.z * v.z);
    }

    private void updateResponderAi(ServerLevel serverLevel) {
        this.responderTicks++;

        if (this.responderTarget == null || !this.responderTarget.isAlive() || this.responderTicks > RESPONDER_TIMEOUT_TICKS) {
            this.finishResponding();
            return;
        }
        double dx = ModEntity.getX(this.responderTarget) - ModEntity.getX(this);
        double dz = ModEntity.getZ(this.responderTarget) - ModEntity.getZ(this);
        double distance = Math.sqrt(dx * dx + dz * dz);
        if (distance < RESPONDER_STOP_DISTANCE) {

            this.aiThrottleDir = 0.0F;
            this.aiSteerDir = 0.0F;
            this.handbrakeHeld = true;
            if (horizontalLength(this.getDeltaMovement()) < 0.02) {
                this.finishResponding();
            }
            return;
        }
        this.handbrakeHeld = false;
        this.aiThrottleDir = distance < RESPONDER_SLOW_DISTANCE ? 0.5F : 1.0F;

        float headingRad = (float) Math.toRadians(this.classicHeading);
        double forwardX = -Mth.sin(headingRad);
        double forwardZ = Mth.cos(headingRad);
        double dirX = dx / distance;
        double dirZ = dz / distance;
        double angleDeg = Math.toDegrees(Math.atan2(forwardX * dirZ - forwardZ * dirX, dirX * forwardX + dirZ * forwardZ));
        if (angleDeg > CHASE_TARGET_ANGLE_DEADZONE_DEGREES) {
            this.aiSteerDir = -1.0F;
        } else if (angleDeg < -CHASE_TARGET_ANGLE_DEADZONE_DEGREES) {
            this.aiSteerDir = 1.0F;
        } else {
            this.aiSteerDir = 0.0F;
        }
    }

    public void stopGtt(ServerLevel serverLevel) {
        if (!this.gttActive) {
            return;
        }
        this.gttActive = false;
        AABB searchBox = this.getBoundingBox().inflate(CHASE_SPAWN_DISTANCE_MAX * 4);
        for (CarEntity chaser : serverLevel.getEntitiesOfClass(CarEntity.class, searchBox,
                other -> other.chaseCarActive && other.chaseTargetCar == this)) {
            chaser.discard();
        }
    }

    private void updateChaseAi(ServerLevel serverLevel) {
        if (this.chaseTargetCar == null || this.chaseTargetCar.isRemoved()
                || !(this.chaseTargetCar.getControllingPassenger() instanceof Player)) {

            this.discard();
            return;
        }

        this.chaseLifetimeTicks++;
        if (this.chaseLifetimeTicks >= CHASE_LIFETIME_TIMEOUT_TICKS) {

            this.chaseTargetCar.addGttScoreIfActive(GTT_POINTS_POLICE_DESTROYED);
            this.despawnWithExplosion(serverLevel);
            return;
        }
        if (this.gttBumpCooldown > 0) {
            this.gttBumpCooldown--;
        }

        double dx = ModEntity.getX(chaseTargetCar) - ModEntity.getX(this);
        double dz = ModEntity.getZ(chaseTargetCar) - ModEntity.getZ(this);
        double distance = Math.sqrt(dx * dx + dz * dz);

        if (this.chaseState == ChaseState.SPIN_OUT) {
            this.chaseStateTimer--;
            if (this.chaseStateTimer <= 0) {
                this.chaseState = ChaseState.CHARGE;
            }
            this.handbrakeHeld = true;
            this.aiThrottleDir = 1.0F;

        } else {
            this.handbrakeHeld = false;
            this.aiThrottleDir = 1.0F;
            if (distance > 1.0E-3) {

                float headingRad = (float) Math.toRadians(this.classicHeading);
                double forwardX = -Mth.sin(headingRad);
                double forwardZ = Mth.cos(headingRad);
                double dirX = dx / distance;
                double dirZ = dz / distance;
                double forwardComponent = dirX * forwardX + dirZ * forwardZ;

                double turnRightComponent = forwardX * dirZ - forwardZ * dirX;
                double angleDeg = Math.toDegrees(Math.atan2(turnRightComponent, forwardComponent));
                if (angleDeg > CHASE_TARGET_ANGLE_DEADZONE_DEGREES) {
                    this.aiSteerDir = -1.0F;
                } else if (angleDeg < -CHASE_TARGET_ANGLE_DEADZONE_DEGREES) {
                    this.aiSteerDir = 1.0F;
                } else {
                    this.aiSteerDir = 0.0F;
                }
            }
            if (distance < CHASE_SPIN_OUT_TRIGGER_DISTANCE && this.random.nextDouble() < CHASE_SPIN_OUT_CHANCE_PER_TICK) {
                this.chaseState = ChaseState.SPIN_OUT;
                this.chaseStateTimer = CHASE_SPIN_OUT_MIN_TICKS
                        + this.random.nextInt(CHASE_SPIN_OUT_MAX_TICKS - CHASE_SPIN_OUT_MIN_TICKS + 1);
                this.aiSteerDir = this.random.nextBoolean() ? 1.0F : -1.0F;
            }
        }

        if (this.getBoundingBox().intersects(this.chaseTargetCar.getBoundingBox())) {
            if (this.chaseTargetCar.isGttActive()) {

                this.chaseTargetCar.gttNoHitTicks = 0;
                double impact = horizontalLength(this.getDeltaMovement().subtract(this.chaseTargetCar.getDeltaMovement()));
                if (impact >= GTT_WRECK_IMPACT_SPEED) {
                    this.chaseTargetCar.addGttScore(GTT_POINTS_POLICE_WRECKED_BY_PLAYER);
                    this.despawnWithExplosion(serverLevel);
                    return;
                }
                if (this.gttBumpCooldown <= 0) {
                    this.chaseTargetCar.addGttScore(GTT_POINTS_BUMP);
                    this.gttBumpCooldown = GTT_BUMP_COOLDOWN_TICKS;
                }
                this.chaseWasClose = false;
            } else {
                this.despawnWithExplosion(serverLevel);
                return;
            }
        }

        if (distance < CHASE_DODGE_TRIGGER_DISTANCE && this.gttBumpCooldown <= 0) {
            this.chaseWasClose = true;
        } else if (this.chaseWasClose && distance > CHASE_DODGE_RELEASE_DISTANCE) {

            this.chaseTargetCar.addGttScoreIfActive(GTT_POINTS_POLICE_DESTROYED);
            this.despawnWithExplosion(serverLevel);
        }
    }

    private void despawnWithExplosion(ServerLevel serverLevel) {
        this.spawnAiCarExplosion(serverLevel, ModEntity.getX(this), ModEntity.getY(this) + 0.5, ModEntity.getZ(this));
        this.discard();
    }

    private void spawnAiCarExplosion(ServerLevel serverLevel, double x, double y, double z) {
        serverLevel.sendParticles(ParticleTypes.EXPLOSION_EMITTER, x, y, z, 1, 0.0, 0.0, 0.0, 0.0);
        int directionCount = 8;
        for (int i = 0; i < directionCount; i++) {
            double angle = (Math.PI * 2.0 * i) / directionCount;
            Vec3 direction = new Vec3(Math.cos(angle), 0.3, Math.sin(angle));
            this.spawnDirectionalBurst(serverLevel, ParticleTypes.LARGE_SMOKE, x, y, z, 12, direction, 0.5);
            this.spawnDirectionalBurst(serverLevel, ParticleTypes.FLAME, x, y, z, 8, direction, 1.0);
        }

        //? if >=1.20.5 {
        //? if >=1.21 {
        serverLevel.playSound(null, x, y, z, SoundEvents.GENERIC_EXPLODE, SoundSource.NEUTRAL, 2.0f, 0.9f);
        //?} else
        /*serverLevel.playSound(null, x, y, z, SoundEvents.GENERIC_EXPLODE.value(), SoundSource.NEUTRAL, 2.0f, 0.9f);*/
        //?} else
        /*serverLevel.playSound(null, x, y, z, SoundEvents.GENERIC_EXPLODE, SoundSource.NEUTRAL, 2.0f, 0.9f);*/
    }

    private void updateChaosSpawning(ServerLevel serverLevel) {
        if (this.chaosSpawnCooldown > 0) {
            this.chaosSpawnCooldown--;
            return;
        }
        AABB searchBox = this.getBoundingBox().inflate(CHAOS_SPAWN_DISTANCE_MAX + CHAOS_SPAWN_LATERAL_SPREAD);
        int activeCount = serverLevel.getEntitiesOfClass(CarEntity.class, searchBox,
                other -> other.chaosCarActive && other.chaosTargetCar == this).size();
        if (activeCount < CHAOS_MAX_CONCURRENT_PER_TARGET) {
            this.spawnChaosCar(serverLevel);
        }
        this.chaosSpawnCooldown = CHAOS_SPAWN_INTERVAL_MIN_TICKS
                + this.random.nextInt(CHAOS_SPAWN_INTERVAL_MAX_TICKS - CHAOS_SPAWN_INTERVAL_MIN_TICKS + 1);
    }

    private void spawnChaosCar(ServerLevel serverLevel) {

        float yawRad = (float) Math.toRadians(this.getYRot());
        double forwardX = Mth.sin(yawRad);
        double forwardZ = Mth.cos(yawRad);
        double rightX = forwardZ;
        double rightZ = -forwardX;
        double distance = CHAOS_SPAWN_DISTANCE_MIN
                + this.random.nextDouble() * (CHAOS_SPAWN_DISTANCE_MAX - CHAOS_SPAWN_DISTANCE_MIN);
        double lateral = (this.random.nextDouble() - 0.5) * 2.0 * CHAOS_SPAWN_LATERAL_SPREAD;
        double x = ModEntity.getX(this) + forwardX * distance + rightX * lateral;
        double z = ModEntity.getZ(this) + forwardZ * distance + rightZ * lateral;

        double y = ModEntity.getY(this);

        String modelId = AiCarPool.pickRandom(excludeHelicopterModelId(serverLevel), this.random);
        CarEntity chaosCar = new CarEntity(serverLevel, x, y, z, modelId);

        chaosCar.setDriveMode(DriveMode.CUSTOM);
        chaosCar.setCustomTopSpeedKmh(CHAOS_CHARGE_TOP_SPEED_KMH);

        chaosCar.setCustomAccelFraction(0.9F);
        chaosCar.chaosCarActive = true;
        chaosCar.chaosTargetCar = this;
        chaosCar.aiSteerDir = 0.0F;
        chaosCar.aiThrottleDir = 1.0F;

        double variantRoll = this.random.nextDouble();
        if (variantRoll < CHAOS_SPINNING_CHANCE) {
            chaosCar.chaosVariant = ChaosVariant.SPINNING;
            boolean spinsRight = this.random.nextBoolean();
            chaosCar.chaosSpinSign = spinsRight ? 1.0F : -1.0F;

            chaosCar.chaosSpinRateDegreesPerTick = this.random.nextBoolean()
                    ? CHAOS_SPIN_BASE_DEGREES_PER_TICK * 2.0
                    : CHAOS_SPIN_BASE_DEGREES_PER_TICK;
        } else if (variantRoll < CHAOS_SPINNING_CHANCE + CHAOS_TUMBLING_CHANCE) {
            chaosCar.chaosVariant = ChaosVariant.TUMBLING;
        }

        double towardTargetX = ModEntity.getX(this) - x;
        double towardTargetZ = ModEntity.getZ(this) - z;
        float initialHeading = (float) Math.toDegrees(Math.atan2(-towardTargetX, towardTargetZ));
        chaosCar.chaosTravelHeading = initialHeading;

        float visualHeading = initialHeading;
        if (chaosCar.chaosVariant == ChaosVariant.TUMBLING) {
            boolean leansRight = this.random.nextBoolean();
            visualHeading += leansRight ? 90.0F : -90.0F;

            chaosCar.chaosTumbleSign = leansRight ? -1.0F : 1.0F;
        }
        chaosCar.classicHeading = visualHeading;
        chaosCar.classicHeadingInitialized = true;
        chaosCar.setRot(-visualHeading, 0.0F);

        serverLevel.addFreshEntity(chaosCar);
    }

    private static AABB shrinkTowardCenter(AABB box, double scale) {
        double xReduce = box.getXsize() * (1.0 - scale) / 2.0;
        double yReduce = box.getYsize() * (1.0 - scale) / 2.0;
        double zReduce = box.getZsize() * (1.0 - scale) / 2.0;

        //? if >=1.17 {
        return box.deflate(xReduce, yReduce, zReduce);
        //?} else
        /*return new AABB(box.minX + xReduce, box.minY + yReduce, box.minZ + zReduce,
                box.maxX - xReduce, box.maxY - yReduce, box.maxZ - zReduce);*/
    }

    private void updateChaosTumble() {
        this.tumbleUsesRollAxis = true;
        this.tumbleTargetRotationDegrees = CHAOS_TUMBLE_NO_LIMIT_DEGREES;
        this.tumbleAngularVelocity = this.chaosTumbleSign * CHAOS_TUMBLE_DEGREES_PER_TICK;
    }

    private void updateChaosAi(ServerLevel serverLevel) {
        if (this.chaosTargetCar == null || this.chaosTargetCar.isRemoved()
                || !(this.chaosTargetCar.getControllingPassenger() instanceof Player)) {

            this.discard();
            return;
        }

        this.chaosLifetimeTicks++;
        if (this.chaosLifetimeTicks >= CHAOS_LIFETIME_TIMEOUT_TICKS) {
            this.despawnWithExplosion(serverLevel);
            return;
        }

        double dx = ModEntity.getX(chaosTargetCar) - ModEntity.getX(this);
        double dz = ModEntity.getZ(chaosTargetCar) - ModEntity.getZ(this);
        double distance = Math.sqrt(dx * dx + dz * dz);

        switch (this.chaosVariant) {
            case SPINNING -> {

                this.collisionSpin = this.chaosSpinSign * this.chaosSpinRateDegreesPerTick;
            }
            case TUMBLING -> {

                this.updateChaosTumble();

                if (this.random.nextDouble() < CHAOS_TUMBLE_SPARK_CHANCE_PER_TICK) {
                    Vec3 sparkDir = new Vec3(this.random.nextDouble() - 0.5, 0.4, this.random.nextDouble() - 0.5).normalize();
                    this.spawnDirectionalBurst(serverLevel, SPARK_PARTICLE,
                            ModEntity.getX(this), ModEntity.getY(this) + 0.3, ModEntity.getZ(this), 6, sparkDir, 0.4);
                }
            }
            case STRAIGHT -> {

                this.handbrakeHeld = false;
                this.aiSteerDir = 0.0F;
                this.aiThrottleDir = 1.0F;
            }
        }

        AABB chaosHitBox = shrinkTowardCenter(
                this.getBoundingBox().deflate(CHAOS_HITBOX_SHRINK), CHAOS_HITBOX_ADDITIONAL_SCALE);
        if (chaosHitBox.intersects(this.chaosTargetCar.getBoundingBox())) {
            this.despawnWithExplosion(serverLevel);
            return;
        }

        if (!this.chaosMissed
                && this.chaosPrevDistanceInitialized
                && distance > this.chaosPrevDistance + CHAOS_MISS_DISTANCE_INCREASE_EPSILON) {
            this.chaosMissed = true;
            this.chaosMissDelayTicks = CHAOS_MISS_EXPLOSION_DELAY_TICKS;
        }
        this.chaosPrevDistance = distance;
        this.chaosPrevDistanceInitialized = true;

        if (this.chaosMissed) {
            this.chaosMissDelayTicks--;
            if (this.chaosMissDelayTicks <= 0) {
                this.despawnWithExplosion(serverLevel);
            }
        }
    }

    static void trySpawnTownTrafficNear(ServerLevel serverLevel, WaypointNetworkSavedData data, BlockPos origin) {

        int activeCount = data.countActiveTownCarsNear(origin, TOWN_DESPAWN_DISTANCE);
        if (activeCount >= data.getMaxSpawnedCars()) {
            return;
        }

        List<WaypointNode> candidates = new ArrayList<>();
        for (WaypointNode node : data.findNodesWithinRadius(origin, TOWN_SPAWN_SEARCH_RADIUS)) {
            if (!forwardTargets(node).isEmpty()) {
                candidates.add(node);
            }
        }
        if (candidates.isEmpty()) {

            return;
        }

        //? if >=1.19 {
        RandomSource random = serverLevel.getRandom();
        //?} else
        /*java.util.Random random = serverLevel.getRandom();*/
        WaypointNode spawnNode = candidates.get(random.nextInt(candidates.size()));
        List<Integer> spawnTargets = forwardTargets(spawnNode);
        int firstTargetId = spawnTargets.get(random.nextInt(spawnTargets.size()));
        WaypointNode firstTarget = data.getNodes().get(firstTargetId);
        if (firstTarget == null) {
            return;
        }
        spawnTownCar(serverLevel, data, spawnNode, firstTarget, data.getDisabledModelIds(), data.getRareModelIds());
    }

    private static final String ITALY_FRONT_PLATE_ID = "italy_glossy";
    private static final String ITALY_REAR_PLATE_ID = "italy";
    private static final String UK_FRONT_PLATE_ID = "unitedkingdom_front";
    private static final String UK_REAR_PLATE_ID = "unitedkingdom";

    private static List<String[]> buildTrafficPlateChoicePool(Set<String> trafficPlateIds) {
        boolean italyPair = trafficPlateIds.contains(ITALY_FRONT_PLATE_ID) && trafficPlateIds.contains(ITALY_REAR_PLATE_ID);
        boolean ukPair = trafficPlateIds.contains(UK_FRONT_PLATE_ID) && trafficPlateIds.contains(UK_REAR_PLATE_ID);
        List<String[]> pool = new ArrayList<>(trafficPlateIds.size());
        for (String id : trafficPlateIds) {
            if (italyPair && (id.equals(ITALY_FRONT_PLATE_ID) || id.equals(ITALY_REAR_PLATE_ID))) {
                pool.add(new String[]{ITALY_FRONT_PLATE_ID, ITALY_REAR_PLATE_ID});
            } else if (ukPair && (id.equals(UK_FRONT_PLATE_ID) || id.equals(UK_REAR_PLATE_ID))) {
                pool.add(new String[]{UK_FRONT_PLATE_ID, UK_REAR_PLATE_ID});
            } else {
                pool.add(new String[]{id, id});
            }
        }
        return pool;
    }

    private static void spawnTownCar(ServerLevel serverLevel, WaypointNetworkSavedData data, WaypointNode spawnNode, WaypointNode firstTarget,
                                      Set<String> disabledModelIds, Set<String> rareModelIds) {
        //? if >=1.19 {
        RandomSource random = serverLevel.getRandom();
        //?} else
        /*java.util.Random random = serverLevel.getRandom();*/

        java.util.function.Predicate<String> excludeModelId = excludeHelicopterModelId(serverLevel);
        List<WaypointNetworkSavedData.CustomTrafficCar> customCars = new ArrayList<>();
        double customWeight = 0.0;
        for (WaypointNetworkSavedData.CustomTrafficCar candidate : data.getCustomTrafficCars()) {
            String candidateModelId = candidate.modelId();

            if (!candidateModelId.isEmpty() && AiCarPool.spawnItemFor(candidateModelId) != null
                    && !excludeModelId.test(candidateModelId)) {
                customCars.add(candidate);
                customWeight += candidate.rare() ? AiCarPool.RARE_SPAWN_WEIGHT : 1.0;
            }
        }
        WaypointNetworkSavedData.CustomTrafficCar customCar = null;
        if (customWeight > 0.0) {
            double roll = random.nextDouble() * (customWeight + AiCarPool.allowedTotalWeight(excludeModelId, disabledModelIds, rareModelIds));
            for (WaypointNetworkSavedData.CustomTrafficCar candidate : customCars) {
                roll -= candidate.rare() ? AiCarPool.RARE_SPAWN_WEIGHT : 1.0;
                if (roll < 0.0) {
                    customCar = candidate;
                    break;
                }
            }
        }
        String modelId = customCar != null ? customCar.modelId()
                : AiCarPool.pickRandomAllowed(excludeModelId, random, disabledModelIds, rareModelIds);
        if (modelId == null) {
            return;
        }

        Vec3 travelDir = edgeDirection(spawnNode, firstTarget);
        boolean oneWayEdge = isOneWayEdge(spawnNode, firstTarget);
        Vec3 spawnPos = travelLinePoint(spawnNode, travelDir, oneWayEdge);
        double x = spawnPos.x;
        double y = spawnNode.pos().getY();
        double z = spawnPos.z;

        CarEntity townCar = new CarEntity(serverLevel, x, y, z, modelId);

        CompoundTag customTrailerTag = null;
        if (customCar != null) {

            CompoundTag customTag = customCar.tag().copy();
            if (customTag.contains("CoupledTrailer")) {
                customTrailerTag = new CompoundTag();
                customTrailerTag.put("CoupledTrailer", customTag.get("CoupledTrailer").copy());
            }
            customTag.remove("CoupledTrailer");
            customTag.remove("ChaseMode");
            customTag.remove("ChaosMode");
            townCar.importCustomizationTag(customTag);
        } else {

            String trafficLiveryId = TrafficLiveryPool.pickRandom(
                    com.examplebett.compat.ModResources.getServerResourceManager(serverLevel.getServer()), random, modelId);
            if (trafficLiveryId != null) {
                townCar.setLivery(trafficLiveryId);
            } else {

                townCar.setColor(TrafficColorPool.pickRandomRgb(random));
            }
        }

        Set<String> trafficPlateIds = data.getTrafficPlateIds();

        Set<String> exclusivePlateIds = data.getExclusivePlateIds();
        if (customCar == null && !trafficPlateIds.isEmpty()) {
            PlateAttachmentFootprint.Footprint footprint =
                    PlateAttachmentFootprint.get(com.examplebett.compat.ModResources.getServerResourceManager(serverLevel.getServer()), modelId);

            List<String[]> plateChoicePool = buildTrafficPlateChoicePool(trafficPlateIds);
            String[] randomChoice = plateChoicePool.get(random.nextInt(plateChoicePool.size()));
            boolean frontDefaultInList = footprint != null && !footprint.frontPlateId().isEmpty()
                    && (trafficPlateIds.contains(footprint.frontPlateId()) || exclusivePlateIds.contains(footprint.frontPlateId()));
            boolean rearDefaultInList = footprint != null && !footprint.rearPlateId().isEmpty()
                    && (trafficPlateIds.contains(footprint.rearPlateId()) || exclusivePlateIds.contains(footprint.rearPlateId()));
            townCar.setFrontPlate(frontDefaultInList ? footprint.frontPlateId() : randomChoice[0]);
            townCar.setRearPlate(rearDefaultInList ? footprint.rearPlateId() : randomChoice[1]);

            if (!frontDefaultInList) {
                float baseScale = footprint != null ? footprint.frontScale() : FALLBACK_PLATE_SCALE;
                float targetScale = randomChoice[0].equals("italy_glossy") ? ITALY_FRONT_PLATE_SCALE
                        : isEuropeanPlate(randomChoice[0]) ? EUROPEAN_DEFAULT_PLATE_SCALE : FALLBACK_PLATE_SCALE;
                townCar.setFrontPlateScale(targetScale - baseScale);
            }
            if (!rearDefaultInList) {
                float baseScale = footprint != null ? footprint.rearScale() : FALLBACK_PLATE_SCALE;
                float targetScale = randomChoice[1].equals("italy_glossy") ? ITALY_FRONT_PLATE_SCALE
                        : isEuropeanPlate(randomChoice[1]) ? EUROPEAN_DEFAULT_PLATE_SCALE : FALLBACK_PLATE_SCALE;
                townCar.setRearPlateScale(targetScale - baseScale);
            }
        }

        townCar.setDriveMode(DriveMode.CUSTOM);
        townCar.townCarActive = true;
        townCar.entityData.set(IS_TOWN_CAR, true);

        townCar.entityData.set(LIGHT_MODE, LightMode.AUTO.ordinal());
        townCar.townPreviousNodeId = spawnNode.id();
        townCar.townCurrentNodeId = firstTarget.id();

        Vec3 firstSteerTarget = travelLinePoint(firstTarget, travelDir, oneWayEdge);
        double towardTargetX = firstSteerTarget.x - x;
        double towardTargetZ = firstSteerTarget.z - z;
        float initialHeading = (float) Math.toDegrees(Math.atan2(-towardTargetX, towardTargetZ));
        townCar.classicHeading = initialHeading;
        townCar.classicHeadingInitialized = true;
        townCar.setRot(-initialHeading, 0.0F);

        serverLevel.addFreshEntity(townCar);
        data.trackActiveTownCar(townCar.getId(), townCar.blockPosition());
        if (customTrailerTag != null) {

            CarEntity trailer = townCar.mineturismo$readCoupledTrailerTag(customTrailerTag);
            if (trailer != null) {
                trailer.townTrailer = true;
                townCar.townCoupledTrailer = trailer;
            }
        }
    }

    private boolean recallNearPlayer(ServerLevel serverLevel, WaypointNetworkSavedData data, Player nearestPlayer) {
        BlockPos origin = nearestPlayer.blockPosition();

        List<WaypointNode> candidates = new ArrayList<>();
        for (WaypointNode node : data.findNodesWithinRadius(origin, TOWN_SPAWN_SEARCH_RADIUS)) {
            if (!forwardTargets(node).isEmpty()) {
                candidates.add(node);
            }
        }
        if (candidates.isEmpty()) {
            return false;
        }

        //? if >=1.19 {
        RandomSource random = serverLevel.getRandom();
        //?} else
        /*java.util.Random random = serverLevel.getRandom();*/
        WaypointNode fromNode = candidates.get(random.nextInt(candidates.size()));
        List<Integer> recallTargets = forwardTargets(fromNode);
        int targetId = recallTargets.get(random.nextInt(recallTargets.size()));
        WaypointNode targetNode = data.getNodes().get(targetId);
        if (targetNode == null) {
            return false;
        }

        Vec3 travelDir = edgeDirection(fromNode, targetNode);
        boolean oneWayEdge = isOneWayEdge(fromNode, targetNode);
        Vec3 newPos = travelLinePoint(fromNode, travelDir, oneWayEdge);
        this.setPos(newPos.x, fromNode.pos().getY(), newPos.z);
        this.setDeltaMovement(Vec3.ZERO);

        this.setInvisible(true);
        this.recallInvisibleTicks = RECALL_INVISIBLE_TICKS;
        data.updateActiveTownCarPosition(this.getId(), this.blockPosition());

        this.townPreviousNodeId = fromNode.id();
        this.townCurrentNodeId = targetNode.id();
        this.townRecentNodeIds.clear();

        Vec3 firstSteerTarget = travelLinePoint(targetNode, travelDir, oneWayEdge);
        float heading = (float) Math.toDegrees(Math.atan2(-(firstSteerTarget.x - newPos.x), firstSteerTarget.z - newPos.z));
        this.classicHeading = heading;
        this.classicHeadingInitialized = true;
        this.setRot(-heading, 0.0F);

        if (this.townCoupledTrailer != null && !this.townCoupledTrailer.isRemoved()) {

            double[] forward = mineturismo$forwardOf(this.getYRot());
            float distance = this.trailerHitchBehindDistance() + this.townCoupledTrailer.trailerKingpinAheadDistance();
            this.townCoupledTrailer.setPos(ModEntity.getX(this) - forward[0] * distance, ModEntity.getY(this),
                    ModEntity.getZ(this) - forward[1] * distance);
            this.townCoupledTrailer.updateTrailerFollow(this);
            this.townCoupledTrailer.setDeltaMovement(Vec3.ZERO);
            this.townCoupledTrailer.setInvisible(true);
            this.townCoupledTrailer.recallInvisibleTicks = RECALL_INVISIBLE_TICKS;
        }

        return true;
    }

    private static List<Integer> forwardTargets(WaypointNode node) {
        List<Integer> targets = new ArrayList<>(node.links().length + node.oneWayLinks().length);
        for (int linkId : node.links()) {
            targets.add(linkId);
        }
        for (int oneWayTargetId : node.oneWayLinks()) {
            if (!targets.contains(oneWayTargetId)) {
                targets.add(oneWayTargetId);
            }
        }
        return targets;
    }

    private static Vec3 edgeDirection(WaypointNode from, WaypointNode to) {
        double dx = to.pos().getX() - from.pos().getX();
        double dz = to.pos().getZ() - from.pos().getZ();
        Vec3 direction = new Vec3(dx, 0.0, dz);
        return direction.lengthSqr() > 1.0e-6 ? direction.normalize() : new Vec3(0.0, 0.0, 1.0);
    }

    private static Vec3 laneOffsetPoint(WaypointNode node, Vec3 travelDir) {
        double rightX = travelDir.z;
        double rightZ = -travelDir.x;
        return new Vec3(
                node.pos().getX() + 0.5 + rightX * TOWN_LANE_OFFSET,
                node.pos().getY(),
                node.pos().getZ() + 0.5 + rightZ * TOWN_LANE_OFFSET);
    }

    private static boolean isOneWayEdge(WaypointNode from, WaypointNode to) {
        return from != null && from.hasOneWayLink(to.id());
    }

    private static Vec3 travelLinePoint(WaypointNode node, Vec3 travelDir, boolean oneWayEdge) {
        if (oneWayEdge) {
            return new Vec3(node.pos().getX() + 0.5, node.pos().getY(), node.pos().getZ() + 0.5);
        }
        return laneOffsetPoint(node, travelDir);
    }

    private void updateTownTrafficAi(ServerLevel serverLevel) {
        this.townLifetimeTicks++;

        Player nearestPlayer = serverLevel.getNearestPlayer(this, -1.0);
        double nearestPlayerDistSq = nearestPlayer != null ? nearestPlayer.distanceToSqr(this) : Double.MAX_VALUE;
        boolean tooFarFromPlayers = nearestPlayer == null
                || nearestPlayerDistSq > TOWN_DESPAWN_DISTANCE * TOWN_DESPAWN_DISTANCE;
        if ((this.townLifetimeTicks >= TOWN_LIFETIME_TIMEOUT_TICKS || tooFarFromPlayers) && !this.chauffeurActive) {

            this.discard();
            return;
        }

        WaypointNetworkSavedData data = WaypointNetworkSavedData.get(serverLevel);

        data.updateActiveTownCarPosition(this.getId(), this.blockPosition());

        this.entityData.set(TOWN_NIGHT_LIGHTS_ACTIVE, data.isNightLightsEnabled());

        boolean inThrottleZone = nearestPlayerDistSq > TOWN_AI_THROTTLE_DISTANCE * TOWN_AI_THROTTLE_DISTANCE;

        if (inThrottleZone && data.isRecallDistantCarsEnabled() && !this.chauffeurActive
                && this.recallNearPlayer(serverLevel, data, nearestPlayer)) {
            this.townAiThrottleTicks = 0;
            return;
        }

        int throttleInterval = data.getAiThrottleIntervalTicks();
        if (throttleInterval > 0 && inThrottleZone) {
            this.townAiThrottleTicks++;
            if (this.townAiThrottleTicks < throttleInterval) {
                return;
            }
            this.townAiThrottleTicks = 0;
        } else {
            this.townAiThrottleTicks = 0;
        }

        this.townUseLegacySteering = data.isLegacySteeringEnabled();

        WaypointNode target = data.getNodes().get(this.townCurrentNodeId);
        if (target == null) {

            this.discard();
            return;
        }

        WaypointNode from = data.getNodes().get(this.townPreviousNodeId);
        Vec3 travelDir = from != null ? edgeDirection(from, target) : new Vec3(0.0, 0.0, 1.0);
        boolean oneWayEdge = isOneWayEdge(from, target);

        Vec3 lineEnd = travelLinePoint(target, travelDir, oneWayEdge);

        double endDx = lineEnd.x - ModEntity.getX(this);
        double endDz = lineEnd.z - ModEntity.getZ(this);
        double distanceToEnd = Math.sqrt(endDx * endDx + endDz * endDz);
        if (distanceToEnd < TOWN_WAYPOINT_ARRIVAL_DISTANCE) {
            this.advanceToNextWaypoint(data, target);
            return;
        }

        double effectiveSpeedKmh = target.speedKmh();
        this.townFollowCheckTicks++;
        if (this.townFollowCheckTicks >= TOWN_FOLLOW_CHECK_INTERVAL_TICKS) {
            this.townFollowCheckTicks = 0;
            this.townCachedAheadCarGap = this.distanceToCarAhead(serverLevel, travelDir);
        }
        double aheadCarGap = this.townCachedAheadCarGap;
        effectiveSpeedKmh = Math.min(effectiveSpeedKmh,
                rampedSpeedKmh(aheadCarGap, target.speedKmh(), TOWN_FOLLOW_STOP_GAP, TOWN_FOLLOW_BRAKING_DISTANCE));
        double signalStopDistance = this.distanceToRedSignalStop(serverLevel, data, from, target, distanceToEnd);
        effectiveSpeedKmh = Math.min(effectiveSpeedKmh,
                rampedSpeedKmh(signalStopDistance, target.speedKmh(), TOWN_SIGNAL_STOP_MARGIN, TOWN_SIGNAL_BRAKING_DISTANCE));

        if (this.townUseLegacySteering) {
            this.updateTownTrafficLegacySteering(endDx, endDz, distanceToEnd, effectiveSpeedKmh);
            return;
        }

        Vec3 aimPoint;
        if (from != null) {
            Vec3 lineStart = travelLinePoint(from, travelDir, oneWayEdge);
            aimPoint = lookaheadPointOnSegment(lineStart, lineEnd, ModEntity.getX(this), ModEntity.getZ(this), TOWN_STEER_LOOKAHEAD_DISTANCE);
        } else {
            aimPoint = lineEnd;
        }

        double aimDx = aimPoint.x - ModEntity.getX(this);
        double aimDz = aimPoint.z - ModEntity.getZ(this);
        double aimDist = Math.sqrt(aimDx * aimDx + aimDz * aimDz);
        double speedBlocksPerTick = effectiveSpeedKmh / 3.6 / 20.0;
        double moveDirX = aimDist > 1.0E-4 ? aimDx / aimDist : travelDir.x;
        double moveDirZ = aimDist > 1.0E-4 ? aimDz / aimDist : travelDir.z;

        Vec3 motion = this.getDeltaMovement();
        this.setDeltaMovement(moveDirX * speedBlocksPerTick, motion.y, moveDirZ * speedBlocksPerTick);

        float rawHeading = (float) Math.toDegrees(Math.atan2(-moveDirX, moveDirZ));
        float heading = this.classicHeadingInitialized
                ? Mth.rotLerp(TOWN_HEADING_SMOOTHING, this.classicHeading, rawHeading)
                : rawHeading;
        this.classicHeading = heading;
        this.classicHeadingInitialized = true;
        this.setRot(-heading, 0.0F);
    }

    private static final double RAILWAY_WAYPOINT_ARRIVAL_DISTANCE = 2.0;
    private static final double RAILWAY_DESPAWN_DISTANCE = 96.0;

    private static final int RAILWAY_STOP_WAIT_TICKS = 200;

    private static final double RAILWAY_STOP_ARRIVAL_MARGIN = 0.4;

    private static final double RAILWAY_STOP_BRAKING_DISTANCE = 16.0;

    private static final double RAILWAY_STOP_CRAWL_SPEED_KMH = 4.0;

    private static final double RAILWAY_STOP_PASSED_DISTANCE = 1.0;

    public void startRailwayRoute(RailwayNetworkSavedData data, int originId, List<RailwayNode> path, int startIndex) {
        this.setDriveMode(DriveMode.CUSTOM);
        this.railwayRouteActive = true;
        this.entityData.set(RAILWAY_ROUTE_ACTIVE_SYNCED, true);
        this.railwayRouteOriginId = originId;
        this.railwayRouteNodes = path;
        this.railwayRouteIndex = startIndex + 1;

        this.faceRailwayHeading(path.get(startIndex).pos(), path.get(startIndex + 1).pos());

        if (!this.railwayCoupledIds.isEmpty()) {
            this.seedRailwayTrajectoryFromPath(path, startIndex);
        }

        data.setActiveTrainEntityId(originId, this.getId());
    }

    private void seedRailwayTrajectoryFromPath(List<RailwayNode> path, int startIndex) {
        RailwayNode spawnNode = path.get(startIndex);
        this.railwayTrajectoryBuffer.clear();
        this.railwayTrajectoryDistance = 0f;
        this.railwayTrajectoryBuffer.addFirst(new float[]{
                spawnNode.pos().getX() + 0.5f, spawnNode.pos().getY(), spawnNode.pos().getZ() + 0.5f, 0f});
        float cumulative = 0f;
        for (int index = startIndex; index > 0; index--) {
            BlockPos current = path.get(index).pos();
            BlockPos previous = path.get(index - 1).pos();
            double dx = (previous.getX() + 0.5) - (current.getX() + 0.5);
            double dy = previous.getY() - current.getY();
            double dz = (previous.getZ() + 0.5) - (current.getZ() + 0.5);
            cumulative += (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
            this.railwayTrajectoryBuffer.addFirst(new float[]{
                    previous.getX() + 0.5f, previous.getY(), previous.getZ() + 0.5f, -cumulative});
        }
    }

    public void faceRailwayHeading(BlockPos from, BlockPos to) {
        double towardTargetX = (to.getX() + 0.5) - (from.getX() + 0.5);
        double towardTargetZ = (to.getZ() + 0.5) - (from.getZ() + 0.5);
        float heading = (float) Math.toDegrees(Math.atan2(-towardTargetX, towardTargetZ));
        this.classicHeading = heading;
        this.classicHeadingInitialized = true;
        this.setRot(-heading, 0.0F);
    }

    private void updateRailwayRouteAi(ServerLevel serverLevel) {
        Player nearestPlayer = serverLevel.getNearestPlayer(this, -1.0);
        double nearestPlayerDistSq = nearestPlayer != null ? nearestPlayer.distanceToSqr(this) : Double.MAX_VALUE;
        if (nearestPlayer == null || nearestPlayerDistSq > RAILWAY_DESPAWN_DISTANCE * RAILWAY_DESPAWN_DISTANCE) {
            this.discard();
            return;
        }
        if (this.railwayRouteIndex >= this.railwayRouteNodes.size()) {
            this.discard();
            return;
        }

        if (this.railwayStopWaitTicks > 0) {
            this.railwayStopWaitTicks--;
            Vec3 waitMotion = this.getDeltaMovement();
            this.setDeltaMovement(0.0, waitMotion.y, 0.0);
            return;
        }

        RailwayNode target = this.railwayRouteNodes.get(this.railwayRouteIndex);
        double targetX = target.pos().getX() + 0.5;
        double targetZ = target.pos().getZ() + 0.5;
        double endDx = targetX - ModEntity.getX(this);
        double endDz = targetZ - ModEntity.getZ(this);
        double distanceToEnd = Math.sqrt(endDx * endDx + endDz * endDz);

        double effectiveSpeedKmh = target.speedKmh();
        boolean stopPending = false;
        if (this.railwayRouteIndex > 0) {
            RailwayNode from = this.railwayRouteNodes.get(this.railwayRouteIndex - 1);
            com.examplebett.waypoint.RailwayStopMarker stop =
                    this.nextRailwayStopOnEdge(RailwayNetworkSavedData.get(serverLevel), from, target, distanceToEnd);
            if (stop != null) {
                double stopDistance = distanceToEnd - stop.stopDistanceFromEnd();
                if (stopDistance <= RAILWAY_STOP_ARRIVAL_MARGIN) {
                    this.railwayStopWaitTicks = RAILWAY_STOP_WAIT_TICKS;
                    this.railwayServedStopId = stop.id();
                    Vec3 stopMotion = this.getDeltaMovement();
                    this.setDeltaMovement(0.0, stopMotion.y, 0.0);
                    return;
                }

                effectiveSpeedKmh = Math.min(effectiveSpeedKmh, Math.max(RAILWAY_STOP_CRAWL_SPEED_KMH,
                        rampedSpeedKmh(stopDistance, target.speedKmh(), RAILWAY_STOP_ARRIVAL_MARGIN, RAILWAY_STOP_BRAKING_DISTANCE)));
                stopPending = true;
            }
        }

        if (distanceToEnd < RAILWAY_WAYPOINT_ARRIVAL_DISTANCE && !stopPending) {
            this.railwayRouteIndex++;
            this.railwayServedStopId = -1;
            if (this.railwayRouteIndex >= this.railwayRouteNodes.size()) {
                this.discard();
            }
            return;
        }

        double speedBlocksPerTick = effectiveSpeedKmh / 3.6 / 20.0;
        double moveDirX = distanceToEnd > 1.0E-4 ? endDx / distanceToEnd : 0.0;
        double moveDirZ = distanceToEnd > 1.0E-4 ? endDz / distanceToEnd : 1.0;
        Vec3 motion = this.getDeltaMovement();
        this.setDeltaMovement(moveDirX * speedBlocksPerTick, motion.y, moveDirZ * speedBlocksPerTick);

        float heading = (float) Math.toDegrees(Math.atan2(-moveDirX, moveDirZ));
        this.classicHeading = heading;
        this.classicHeadingInitialized = true;
        this.setRot(-heading, 0.0F);
    }

    private com.examplebett.waypoint.RailwayStopMarker nextRailwayStopOnEdge(RailwayNetworkSavedData data,
            RailwayNode from, RailwayNode target, double distanceToEnd) {
        com.examplebett.waypoint.RailwayStopMarker nearest = null;
        double nearestDistance = Double.MAX_VALUE;
        for (com.examplebett.waypoint.RailwayStopMarker stop : data.findStopsOnEdge(from.id(), target.id())) {
            if (stop.id() == this.railwayServedStopId) {
                continue;
            }
            double remaining = distanceToEnd - stop.stopDistanceFromEnd();
            if (remaining < -RAILWAY_STOP_PASSED_DISTANCE || remaining >= nearestDistance) {
                continue;
            }
            nearest = stop;
            nearestDistance = remaining;
        }
        return nearest;
    }

    private void updateTownTrafficLegacySteering(double endDx, double endDz, double distanceToEnd,
                                                   double effectiveSpeedKmh) {
        this.townLegacyTopSpeedKmh = (float) effectiveSpeedKmh;
        this.handbrakeHeld = false;
        this.aiThrottleDir = 1.0F;

        float headingRad = (float) Math.toRadians(this.classicHeading);
        double forwardX = -Mth.sin(headingRad);
        double forwardZ = Mth.cos(headingRad);
        double dirX = endDx / distanceToEnd;
        double dirZ = endDz / distanceToEnd;
        double forwardComponent = dirX * forwardX + dirZ * forwardZ;
        double turnRightComponent = forwardX * dirZ - forwardZ * dirX;
        double angleDeg = Math.toDegrees(Math.atan2(turnRightComponent, forwardComponent));
        if (Math.abs(angleDeg) <= TOWN_LEGACY_TARGET_ANGLE_DEADZONE_DEGREES) {
            this.aiSteerDir = 0.0F;
        } else {

            float magnitude = (float) Mth.clamp(Math.abs(angleDeg) / TOWN_LEGACY_STEER_FULL_ANGLE_DEGREES, 0.0, 1.0);
            this.aiSteerDir = angleDeg > 0 ? -magnitude : magnitude;

            float speedFactor = 1.0F - TOWN_LEGACY_CORNER_SLOWDOWN_FRACTION * magnitude;
            this.townLegacyTopSpeedKmh = (float) (effectiveSpeedKmh * speedFactor);
        }
    }

    private static Vec3 lookaheadPointOnSegment(Vec3 lineStart, Vec3 lineEnd, double carX, double carZ,
                                                 double lookaheadDistance) {
        double segX = lineEnd.x - lineStart.x;
        double segZ = lineEnd.z - lineStart.z;
        double segLenSq = segX * segX + segZ * segZ;
        if (segLenSq < 1.0E-6) {
            return lineEnd;
        }
        double segLen = Math.sqrt(segLenSq);
        double carRelX = carX - lineStart.x;
        double carRelZ = carZ - lineStart.z;
        double projT = Mth.clamp((carRelX * segX + carRelZ * segZ) / segLenSq, 0.0, 1.0);
        double aimT = Mth.clamp(projT + lookaheadDistance / segLen, 0.0, 1.0);
        return new Vec3(lineStart.x + segX * aimT, lineEnd.y, lineStart.z + segZ * aimT);
    }

    private double distanceToCarAhead(ServerLevel serverLevel, Vec3 travelDir) {
        double forwardX = travelDir.x;
        double forwardZ = travelDir.z;
        double rightX = travelDir.z;
        double rightZ = -travelDir.x;
        double searchMargin = TOWN_FOLLOW_LOOKAHEAD_DISTANCE + 6.0;
        AABB searchBox = new AABB(
                ModEntity.getX(this) - searchMargin, ModEntity.getY(this) - 2.0, ModEntity.getZ(this) - searchMargin,
                ModEntity.getX(this) + searchMargin, ModEntity.getY(this) + 2.0, ModEntity.getZ(this) + searchMargin);
        List<CarEntity> candidates = serverLevel.getEntitiesOfClass(CarEntity.class, searchBox,
                other -> other != this && (other.townCarActive || other.getControllingPassenger() instanceof Player));
        if (candidates.isEmpty()) {
            return Double.MAX_VALUE;
        }
        float thisLength = CarFootprint.getLength(com.examplebett.compat.ModResources.getServerResourceManager(serverLevel.getServer()), this.getModelId()) * this.getCarScale();
        double nearestGap = Double.MAX_VALUE;
        for (CarEntity other : candidates) {
            double dx = ModEntity.getX(other) - ModEntity.getX(this);
            double dz = ModEntity.getZ(other) - ModEntity.getZ(this);
            double along = dx * forwardX + dz * forwardZ;
            if (along <= 0.0 || along > TOWN_FOLLOW_LOOKAHEAD_DISTANCE + thisLength) {
                continue;
            }
            double across = dx * rightX + dz * rightZ;
            if (Math.abs(across) > TOWN_FOLLOW_LANE_HALF_WIDTH) {
                continue;
            }

            float otherHeadingRad = (float) Math.toRadians(other.classicHeading);
            double otherForwardX = -Mth.sin(otherHeadingRad);
            double otherForwardZ = Mth.cos(otherHeadingRad);
            double headingAlignment = otherForwardX * forwardX + otherForwardZ * forwardZ;
            if (headingAlignment < TOWN_FOLLOW_MIN_HEADING_ALIGNMENT) {
                continue;
            }
            float otherLength = CarFootprint.getLength(com.examplebett.compat.ModResources.getServerResourceManager(serverLevel.getServer()), other.getModelId()) * other.getCarScale();
            double gap = along - (thisLength + otherLength) / 2.0;
            if (gap < nearestGap) {
                nearestGap = gap;
            }
        }
        return nearestGap;
    }

    private static double rampedSpeedKmh(double distanceToStop, double fullSpeedKmh, double stopMargin, double brakingDistance) {
        if (distanceToStop >= Double.MAX_VALUE) {
            return fullSpeedKmh;
        }
        if (distanceToStop <= stopMargin) {
            return 0.0;
        }
        if (distanceToStop >= stopMargin + brakingDistance) {
            return fullSpeedKmh;
        }
        double t = (distanceToStop - stopMargin) / brakingDistance;
        return fullSpeedKmh * t;
    }

    private double distanceToRedSignalStop(ServerLevel serverLevel, WaypointNetworkSavedData data,
                                            WaypointNode from, WaypointNode target, double distanceToEnd) {
        if (from == null) {
            return Double.MAX_VALUE;
        }
        TrafficSignalMarker signal = data.findSignalOnEdge(from.id(), target.id());
        if (signal == null) {
            return Double.MAX_VALUE;
        }
        SignalGroupConfig group = data.getSignalGroup(signal.groupKey());
        if (group == null || group.isGreenAt(serverLevel.getGameTime())) {
            return Double.MAX_VALUE;
        }
        double remainingToStop = distanceToEnd - signal.stopDistanceFromEnd();
        return remainingToStop > 0.0 ? remainingToStop : Double.MAX_VALUE;
    }

    private void advanceToNextWaypoint(WaypointNetworkSavedData data, WaypointNode arrivedAt) {

        if (this.chauffeurActive) {

            if (this.chauffeurRouteIndex >= this.chauffeurRoute.size() - 1) {

                List<Integer> continuation = this.mineturismo$planChauffeurRoute(data);
                if (continuation != null) {
                    this.chauffeurRoute = continuation;
                    this.chauffeurRouteIndex = 0;
                    this.townPreviousNodeId = arrivedAt.id();
                    return;
                }
                this.endChauffeurTrip();
            } else {
                this.chauffeurRouteIndex++;
                this.townPreviousNodeId = arrivedAt.id();
                this.townCurrentNodeId = this.chauffeurRoute.get(this.chauffeurRouteIndex);
                return;
            }
        }
        if (arrivedAt.links().length == 0 && arrivedAt.oneWayLinks().length == 0) {

            this.discard();
            return;
        }

        List<Integer> options = forwardTargets(arrivedAt);
        List<Integer> filtered = new ArrayList<>(options);
        filtered.removeAll(this.townRecentNodeIds);
        if (filtered.isEmpty() && options.size() > 1) {
            filtered = new ArrayList<>(options);
            filtered.remove(Integer.valueOf(this.townPreviousNodeId));
        }
        if (filtered.isEmpty()) {
            filtered = options;
        }
        int nextId = filtered.get(this.random.nextInt(filtered.size()));

        this.townPreviousNodeId = arrivedAt.id();
        this.townRecentNodeIds.addLast(arrivedAt.id());
        while (this.townRecentNodeIds.size() > TOWN_RECENT_NODE_HISTORY) {
            this.townRecentNodeIds.removeFirst();
        }
        this.townCurrentNodeId = nextId;

        WaypointNode nextNode = data.getNodes().get(nextId);
        if (nextNode != null) {
            AiObjectMod.LOGGER.debug(
                    "[TownTraffic advance] car={} tick={} carPos=({}, {}, {}) arrivedAt={} pos=({}, {}, {}) -> next={} pos=({}, {}, {}) options={}",
                    this.getId(), this.tickCount,
                    String.format("%.1f", ModEntity.getX(this)), String.format("%.1f", ModEntity.getY(this)), String.format("%.1f", ModEntity.getZ(this)),
                    arrivedAt.id(), arrivedAt.pos().getX(), arrivedAt.pos().getY(), arrivedAt.pos().getZ(),
                    nextId, nextNode.pos().getX(), nextNode.pos().getY(), nextNode.pos().getZ(),
                    filtered.size());
        }
    }

    private void endChauffeurTrip() {
        this.ejectPassengers();
        this.mineturismo$setChauffeurActive(false);
        this.chauffeurRoute = null;
        this.chauffeurRouteIndex = 0;
        this.chauffeurPayerId = null;
    }

    private void mineturismo$setChauffeurActive(boolean active) {
        this.chauffeurActive = active;
        this.entityData.set(CHAUFFEUR_ACTIVE, active);
    }

    private boolean mineturismo$isChauffeurActive() {
        return this.entityData.get(CHAUFFEUR_ACTIVE);
    }

    private boolean mineturismo$isNpcDrivenWithPassenger() {
        return this.mineturismo$isChauffeurActive()
                || this.entityData.get(RAILWAY_ROUTE_ACTIVE_SYNCED)
                || this.entityData.get(RAILWAY_FOLLOWER)
                || this.entityData.get(TRAILER_HEAD_ENTITY_ID) >= 0;
    }

    @Override
    public void tick() {
        super.tick();
        //? if <1.20 {
        /*if (this.getControllingPassenger() instanceof Player player) {
            this.tickRiddenSteering(player, this.rawForwardHeld);
        }*/
        //?}
        if ((this.entityData.get(RAILWAY_FOLLOWER) || this.entityData.get(TRAILER_HEAD_ENTITY_ID) >= 0) && this.level().isClientSide()) {

            this.mineturismo$reduceRailwayFollowerInterpolationDelay();
        }

        if (this.level().isClientSide()) {
            this.tickExhaustAfterfire();
        }
        if (this.level().isClientSide() && this.isRailwayHead()) {

            this.mineturismo$recordClientRailwayTrajectorySample();
        }
        this.lockPassengerBodyRotationIfOpenCar();

        this.updateFootprintBoundingBox();
        if (this.level() instanceof ServerLevel && !this.getPassengers().isEmpty()) {

            this.updateServerSideSlipBroadcast();
            this.updateEngineSoundBroadcast();
        }
        if (this.isRecording() && this.level() instanceof ServerLevel) {
            this.recordedFrames.add(new float[]{
                    (float) ModEntity.getX(this), (float) ModEntity.getY(this), (float) ModEntity.getZ(this),
                    this.getYRot(), this.getXRot(),
                    this.tireSmokeFiredThisTick ? 1f : 0f});
        }
        if (!this.railwayCoupledIds.isEmpty() && this.level() instanceof ServerLevel railwayServerLevel) {

            this.updateRailwayConsist(railwayServerLevel);
            this.mineturismo$syncRailwayCoupledEntityIds(railwayServerLevel);
        }
        if (this.railwayHeadUuid != null && this.entityData.get(RAILWAY_HEAD_ENTITY_ID) < 0
                && this.level() instanceof ServerLevel railwayFollowerServerLevel) {

            Entity headEntity = railwayFollowerServerLevel.getEntity(this.railwayHeadUuid);
            if (headEntity != null) {
                this.entityData.set(RAILWAY_HEAD_ENTITY_ID, headEntity.getId());
            }
        }
        if (this.level() instanceof ServerLevel serverLevel) {
            this.tickPendingServerSmokeSpawns(serverLevel);
            this.tickPendingGlassShatters(serverLevel);

            if (!hitboxDisabled) {
                this.tryBreakCollidingBlocks(serverLevel);
            }
            this.applyCollisionSlide();
            this.updateBikeLeanState();
            this.updateWheelieState();
            this.updateSuspensionSwayState();
            this.tickBomb(serverLevel);
            if (this.isRemoved()) {
                return;
            }
            HornSirenSounds.HornOption horn = HornSirenSounds.hornById(this.getHornSoundId());

            if (this.hornBroadcastActive && horn.sound() != null) {
                if (this.hornBroadcastCooldownTicks <= 0) {
                    serverLevel.playSound(null, ModEntity.getX(this), ModEntity.getY(this), ModEntity.getZ(this),
                            horn.sound(), SoundSource.NEUTRAL, 1.0f, 1.0f);
                    this.hornBroadcastCooldownTicks = horn.broadcastIntervalTicks();
                } else {
                    this.hornBroadcastCooldownTicks--;
                }
            }

            if (this.recallInvisibleTicks > 0) {
                this.recallInvisibleTicks--;
                if (this.recallInvisibleTicks == 0) {
                    this.setInvisible(false);
                }
            }

            if (this.securityAlarmTicksRemaining > 0) {
                this.securityAlarmTicksRemaining--;
                boolean phaseOn = (this.tickCount / SECURITY_ALARM_BLINK_INTERVAL_TICKS) % 2 == 0;
                if (this.securityAlarmVariant == 0) {

                    if (phaseOn && !this.securityAlarmPreviousPhaseOn && horn.sound() != null) {
                        serverLevel.playSound(null, ModEntity.getX(this), ModEntity.getY(this), ModEntity.getZ(this),
                                horn.sound(), SoundSource.NEUTRAL, 1.0f, 1.0f);
                    }
                } else if (--this.securityAlarmSoundTicks <= 0) {
                    boolean chirp = this.securityAlarmVariant == 1;
                    serverLevel.playSound(null, ModEntity.getX(this), ModEntity.getY(this), ModEntity.getZ(this),
                            chirp ? AiObjectModSounds.ALARM_CHIRP : AiObjectModSounds.ALARM_SWEEP,
                            SoundSource.NEUTRAL, 1.0f, 1.0f);
                    this.securityAlarmSoundTicks = chirp ? SECURITY_ALARM_CHIRP_TICKS : SECURITY_ALARM_SWEEP_TICKS;
                }
                this.securityAlarmPreviousPhaseOn = phaseOn;
                this.entityData.set(SECURITY_ALARM_ACTIVE, this.securityAlarmTicksRemaining > 0);
            } else if (this.entityData.get(SECURITY_ALARM_ACTIVE)) {
                this.entityData.set(SECURITY_ALARM_ACTIVE, false);
            }

            this.tickTrailerCoupling(serverLevel);
            this.updateTow(serverLevel);

            if (this.gttActive && !(this.getControllingPassenger() instanceof Player)) {
                this.stopGtt(serverLevel);
            }
            if (this.gttActive) {
                this.gttNoHitTicks++;
                if (this.gttNoHitTicks >= GTT_NO_HIT_BONUS_TICKS) {
                    this.gttNoHitTicks = 0;
                    this.addGttScore(GTT_POINTS_NO_HIT);
                }
            }
            if (this.responderActive) {
                this.updateResponderAi(serverLevel);
            } else if (this.chaseCarActive) {
                this.updateChaseAi(serverLevel);
            } else if (this.chaosCarActive) {
                this.updateChaosAi(serverLevel);
            } else if (this.townCarActive) {
                this.updateTownTrafficAi(serverLevel);
            } else if (this.railwayRouteActive) {
                this.updateRailwayRouteAi(serverLevel);
            } else if (this.getControllingPassenger() instanceof Player) {

                if (this.isChaseModeEnabled() || this.gttActive) {
                    this.updateChaseSpawning(serverLevel);
                }
                if (this.isChaosModeEnabled()) {
                    this.updateChaosSpawning(serverLevel);
                }
            }
        }
        this.tireSmokeFiredThisTick = false;
        if (this.isTireSmokePreviewEnabled()) {

            if (this.isHeadingBasedDriveMode() && !this.classicHeadingInitialized) {
                this.classicHeading = -this.getYRot();
                this.classicHeadingInitialized = true;
            }
            if (this.tireSmokePreviewTickCounter <= 0) {
                this.spawnTireSmoke();
                this.tireSmokePreviewTickCounter = TIRE_SMOKE_PREVIEW_INTERVAL_TICKS;
            } else {
                this.tireSmokePreviewTickCounter--;
            }
        }
        this.wheelRotO = this.wheelRot;
        this.burnoutSpinRotO = this.burnoutSpinRot;
        this.rotorRotO = this.rotorRot;

        if (!(this.getControllingPassenger() instanceof Player)) {

            this.steerInput = 0.0F;
        }
        this.wheelSteerO = this.wheelSteer;
        float targetSteer = this.steerInput * MAX_FRONT_WHEEL_STEER_DEGREES;
        float steerDelta = targetSteer - this.wheelSteer;
        if (Mth.abs(steerDelta) <= FRONT_WHEEL_STEER_SPEED_DEG_PER_TICK) {
            this.wheelSteer = targetSteer;
        } else {
            this.wheelSteer += Math.signum(steerDelta) * FRONT_WHEEL_STEER_SPEED_DEG_PER_TICK;
        }

        if (this.compiledModel != null && !this.compiledModel.rotorPivots.isEmpty()
                && this.hasVehicleCategory(VehicleCategory.HELICOPTER)) {
            this.rotorRot += ROTOR_SPIN_DEGREES_PER_TICK;
        }

        if (!this.wheelPrevInitialized) {

            this.wheelPrevX = ModEntity.getX(this);
            this.wheelPrevZ = ModEntity.getZ(this);
            this.wheelPrevInitialized = true;
            return;
        }

        double dx = ModEntity.getX(this) - this.wheelPrevX;
        double dz = ModEntity.getZ(this) - this.wheelPrevZ;
        this.wheelPrevX = ModEntity.getX(this);
        this.wheelPrevZ = ModEntity.getZ(this);

        double signedDistance;
        if (this.isHeadingBasedDriveMode() && this.isLocalInstanceAuthoritative()) {

            signedDistance = this.classicSpeed;
        } else {

            float yawRad = (float) Math.toRadians(this.getYRot());

            double forwardX = this.isHeadingBasedDriveMode() ? Mth.sin(yawRad) : -Mth.sin(yawRad);
            double forwardZ = Mth.cos(yawRad);
            signedDistance = dx * forwardX + dz * forwardZ;
        }

        if (this.level().isClientSide()) {

            this.wheelDistanceTraveled += (float) signedDistance;
        }

        float radius = this.resolveWheelRadius();

        if (radius <= 0.01f) {
            return;
        }
        float degreesPerMeter = (float) (360.0 / (2.0 * Math.PI * radius));
        this.wheelRot += (float) signedDistance * degreesPerMeter;

        this.wheelRotLocalUpdateMs = System.currentTimeMillis();
    }

    private float resolveWheelRadius() {
        CompiledCarModel model = this.compiledModel;

        float radius = model != null ? model.averageWheelRadius() * this.getWheelScale() : 0f;
        if (radius <= 0.01f && this.wheelAttachment != null) {

            radius = this.wheelAttachment.wheelRadius() * this.getWheelScale();
        }
        return radius;
    }

    private float wheelDistanceTraveled;

    public float getWheelDistanceTraveled() {
        return this.wheelDistanceTraveled;
    }

    private static final long WHEEL_ROTATION_LOCAL_STALE_MS = 500;
    private long wheelRotLocalUpdateMs = -1;

    private float networkWheelDistanceTraveled;
    private long wheelRotNetworkUpdateMs = -1;

    public void setNetworkWheelDistance(float distance) {
        this.networkWheelDistanceTraveled = distance;
        this.wheelRotNetworkUpdateMs = System.currentTimeMillis();
    }

    public float getWheelRotationDegrees(float partialTick) {
        boolean localFresh = this.wheelRotLocalUpdateMs >= 0
                && (System.currentTimeMillis() - this.wheelRotLocalUpdateMs) < WHEEL_ROTATION_LOCAL_STALE_MS;
        if (localFresh) {
            return Mth.lerp(partialTick, this.wheelRotO, this.wheelRot);
        }
        float radius = this.resolveWheelRadius();
        if (radius <= 0.01f) {
            return 0f;
        }
        float degreesPerMeter = (float) (360.0 / (2.0 * Math.PI * radius));
        return this.networkWheelDistanceTraveled * degreesPerMeter;
    }

    public float getFrontWheelSteerDegrees(float partialTick) {
        return Mth.lerp(partialTick, this.wheelSteerO, this.wheelSteer);
    }

    public float getBikeLeanRollDegrees() {
        return this.entityData.get(BIKE_LEAN_ROLL);
    }

    public boolean isBikeStationary() {
        return this.entityData.get(BIKE_STATIONARY);
    }

    public float getRearWheelExtraSpinDegrees(float partialTick) {
        return Mth.lerp(partialTick, this.burnoutSpinRotO, this.burnoutSpinRot);
    }

    public float getRotorRotationDegrees(float partialTick) {
        return Mth.lerp(partialTick, this.rotorRotO, this.rotorRot);
    }

    public boolean isAircraft() {
        return this.hasVehicleCategory(VehicleCategory.HELICOPTER);
    }

    private static java.util.function.Predicate<String> excludeHelicopterModelId(ServerLevel serverLevel) {
        net.minecraft.server.packs.resources.ResourceManager resourceManager = com.examplebett.compat.ModResources.getServerResourceManager(serverLevel.getServer());
        return modelId -> {
            java.util.Set<VehicleCategory> categories = VehicleCategoryFootprint.get(resourceManager, modelId);

            return categories.contains(VehicleCategory.HELICOPTER) || categories.contains(VehicleCategory.RAILWAY)
                    || categories.contains(VehicleCategory.TRAILER);
        };
    }

    @Override
    //? if >=1.21.6 {
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.readCustomizationData(input);
        this.placedFromItem = input.getIntOr("PlacedFromItem", PLACED_FROM_NONE);
        this.trailerCargo.clearContent();
        net.minecraft.world.ContainerHelper.loadAllItems(input.childOrEmpty("TrailerCargo"), this.trailerCargo.getItems());
    }
    //?} else {
    /*
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        ValueInput input = new ValueInput(tag);
        this.readCustomizationData(input);
        this.placedFromItem = input.getIntOr("PlacedFromItem", PLACED_FROM_NONE);
        com.examplebett.compat.ModContainers.load(tag, "TrailerCargo", this.trailerCargo, this);
    }
    */
    //?}

    public void readCustomizationData(ValueInput input) {
        input.getString("ModelId").ifPresent(this::setModelId);
        this.setColor(input.getIntOr("Color", DEFAULT_COLOR));
        this.setFrontPlate(input.getStringOr("FrontPlate", ""));
        this.setFrontPlateOffset(
                input.getFloatOr("FrontPlateX", 0f),
                input.getFloatOr("FrontPlateY", 0f),
                input.getFloatOr("FrontPlateZ", 0f));
        this.setFrontPlateScale(input.getFloatOr("FrontPlateScale", 0f));
        this.setFrontPlateRotation(
                input.getFloatOr("FrontPlateYaw", 0f),
                input.getFloatOr("FrontPlatePitch", 0f),
                input.getFloatOr("FrontPlateRoll", 0f));
        this.setRearPlate(input.getStringOr("RearPlate", ""));
        this.setRearPlateOffset(
                input.getFloatOr("RearPlateX", 0f),
                input.getFloatOr("RearPlateY", 0f),
                input.getFloatOr("RearPlateZ", 0f));
        this.setRearPlateScale(input.getFloatOr("RearPlateScale", 0f));
        this.setRearPlateRotation(
                input.getFloatOr("RearPlateYaw", 0f),
                input.getFloatOr("RearPlatePitch", 0f),
                input.getFloatOr("RearPlateRoll", 0f));
        this.setTireSmokeOffset(
                input.getFloatOr("TireSmokeX", DEFAULT_TIRE_SMOKE_X),
                input.getFloatOr("TireSmokeY", DEFAULT_TIRE_SMOKE_Y),
                input.getFloatOr("TireSmokeZ", DEFAULT_TIRE_SMOKE_Z));
        this.setTireSmokeParticle(input.getStringOr("TireSmokeParticle", TireSmokeParticles.DEFAULT_ID));
        LightMode[] lightModes = LightMode.values();
        int savedLightMode = input.getIntOr("LightMode", LightMode.AUTO.ordinal());
        this.setLightAdjust(
                input.getFloatOr("HeadlightX", 0f),
                input.getFloatOr("HeadlightY", 0f),
                input.getFloatOr("HeadlightZ", 0f),
                input.getFloatOr("TaillightX", 0f),
                input.getFloatOr("TaillightY", 0f),
                input.getFloatOr("TaillightZ", 0f),
                lightModes[Mth.clamp(savedLightMode, 0, lightModes.length - 1)],
                input.getStringOr("HeadlightColor", ""));
        this.setLightBlinkEnabled(input.getBooleanOr("LightBlink", false));
        this.setWheelAdjust(
                input.getFloatOr("WheelFrontZOffset", 0f),
                input.getFloatOr("WheelRearZOffset", 0f),
                input.getFloatOr("WheelTrackHalfWidthOffset", 0f),
                input.getFloatOr("WheelAxleHeightOffset", 0f),
                input.getFloatOr("BodyHeightOffset", 0f),
                input.getFloatOr("WheelCamberDegrees", 0f),
                input.getFloatOr("WheelScale", 0f),
                input.getStringOr("WheelModelIdOverride", ""));
        this.setWheelWidthScale(input.getFloatOr("WheelWidthScale", 0f));
        this.setExtraRearAxle(input.getBooleanOr("ExtraRearAxleEnabled", false),
                input.getFloatOr("ExtraRearAxleZOffset", 0f));
        this.setExtraRearAxle2(input.getBooleanOr("ExtraRearAxle2Enabled", false),
                input.getFloatOr("ExtraRearAxle2ZOffset", 0f));
        this.setLipAdjust(input.getFloatOr("LipStretch", 0f), input.getFloatOr("LipWidth", 0f),
                input.getFloatOr("LipBandHeightCm", DEFAULT_LIP_BAND_HEIGHT_CM),
                input.getFloatOr("LipFrontExtend", 0f));
        this.setLimoAdjust(input.getFloatOr("LimoStretchCm", 0f),
                input.getFloatOr("LimoZoneCm", 0f),
                input.getFloatOr("LimoCenterOffsetCm", 0f));
        this.setWidthAdjust(input.getFloatOr("WidthStretchCm", 0f),
                input.getFloatOr("WidthZoneCm", 0f),
                input.getFloatOr("WidthCenterOffsetCm", 0f));
        this.setTiltAdjust(input.getFloatOr("TiltRollDegrees", 0f),
                input.getFloatOr("TiltPitchDegrees", 0f),
                input.getFloatOr("TiltHeightCm", 0f));
        this.setPassengerPositionAdjust(input.getFloatOr("PassengerXAdjust", 0f),
                input.getFloatOr("PassengerYAdjust", 0f),
                input.getFloatOr("PassengerZAdjust", 0f));
        this.setRearWingEnabledIds(input.getStringOr("RearWingEnabledIds", ""));
        this.entityData.set(REAR_WING_TRANSFORMS, input.getStringOr("RearWingTransforms", ""));
        this.setPoliceLightEnabledIds(input.getStringOr("PoliceLightEnabledIds", ""));
        this.entityData.set(POLICE_LIGHT_TRANSFORMS, input.getStringOr("PoliceLightTransforms", ""));
        this.setTaxiSignEnabledIds(input.getStringOr("TaxiSignEnabledIds", ""));
        this.entityData.set(TAXI_SIGN_TRANSFORMS, input.getStringOr("TaxiSignTransforms", ""));
        this.setOtherPartEnabledIds(input.getStringOr("OtherPartEnabledIds", ""));
        this.entityData.set(OTHER_PART_TRANSFORMS, input.getStringOr("OtherPartTransforms", ""));

        this.entityData.set(TEXT_PARTS, com.examplebett.render.TextParts.serialize(
                com.examplebett.render.TextParts.parse(input.getStringOr("TextParts", ""))));
        this.entityData.set(UNDER_NEON, com.examplebett.render.UnderNeon.serialize(
                com.examplebett.render.UnderNeon.parse(input.getStringOr("UnderNeon", ""))));
        this.entityData.set(BRAND_LOGOS, com.examplebett.render.BrandLogos.serialize(
                com.examplebett.render.BrandLogos.parse(input.getStringOr("BrandLogos", ""))));
        this.setFrontBrakeCaliperEnabledIds(input.getStringOr("FrontBrakeCaliperEnabledIds", ""));
        this.entityData.set(FRONT_BRAKE_CALIPER_TRANSFORMS, input.getStringOr("FrontBrakeCaliperTransforms", ""));
        this.setRearBrakeCaliperEnabledIds(input.getStringOr("RearBrakeCaliperEnabledIds", ""));
        this.entityData.set(REAR_BRAKE_CALIPER_TRANSFORMS, input.getStringOr("RearBrakeCaliperTransforms", ""));
        this.setExhaustEnabledIds(input.getStringOr("ExhaustEnabledIds", ""));
        this.entityData.set(EXHAUST_TRANSFORMS, input.getStringOr("ExhaustTransforms", ""));
        String mineturismo$towLeader = input.getStringOr("TowLeaderUuid", "");
        this.towLeaderUuid = mineturismo$towLeader.isEmpty() ? null : UUID.fromString(mineturismo$towLeader);

        String mineturismo$trailerHead = input.getStringOr("TrailerHeadUuid", "");
        this.trailerHeadUuid = mineturismo$trailerHead.isEmpty() ? null : UUID.fromString(mineturismo$trailerHead);
        this.setPoliceLightsOn(input.getBooleanOr("PoliceLightsOn", false));
        this.setPoliceSirenOn(input.getBooleanOr("PoliceSirenOn", false));
        this.setWheelColor(input.getStringOr("WheelColorOverride", ""));
        this.setCarScale(input.getFloatOr("CarScale", 0f));
        this.setCameraDistanceAdjust(input.getFloatOr("CameraDistanceAdjust", 0f));
        this.setCameraHeightAdjust(input.getFloatOr("CameraHeightAdjust", 0f));
        this.setLivery(input.getStringOr("Livery", ""));

        input.getString("EngineSound").ifPresent(this::setEngineSoundId);
        this.setHornSoundId(input.getStringOr("HornSound", HornSirenSounds.DEFAULT_HORN_ID));
        this.setSirenSoundId(input.getStringOr("SirenSound", HornSirenSounds.DEFAULT_SIREN_ID));
        String mineturismo$bombOwner = input.getStringOr("BombOwner", "");
        this.setBombInstalled(input.getBooleanOr("BombInstalled", false) && !mineturismo$bombOwner.isEmpty(),
                mineturismo$bombOwner.isEmpty() ? null : UUID.fromString(mineturismo$bombOwner));
        this.setEnginePitchIntensity(input.getFloatOr("EnginePitchIntensity", this.getEnginePitchIntensity()));
        this.setEngineTopSpeedPitch(input.getFloatOr("EngineTopSpeedPitch", this.getEngineTopSpeedPitch()));
        this.setEngineLoopUseFraction(input.getFloatOr("EngineLoopUseFraction", this.getEngineLoopUseFraction()));
        DriveMode[] driveModes = DriveMode.values();
        int savedDriveMode = input.getIntOr("DriveMode", DriveMode.CLASSIC.ordinal());
        this.setDriveMode(driveModes[Mth.clamp(savedDriveMode, 0, driveModes.length - 1)]);

        this.setChaseModeEnabled(input.getBooleanOr("ChaseMode", false));
        this.setChaosModeEnabled(input.getBooleanOr("ChaosMode", false));
        this.setCustomTopSpeedKmh(input.getFloatOr("CustomTopSpeedKmh", DEFAULT_CUSTOM_TOP_SPEED_KMH));
        this.setCustomAccelFraction(input.getFloatOr("CustomAccelFraction", DEFAULT_CUSTOM_FRACTION));
        this.setCustomBrakeFraction(input.getFloatOr("CustomBrakeFraction", DEFAULT_CUSTOM_FRACTION));
        this.setCustomHandlingFraction(input.getFloatOr("CustomHandlingFraction", DEFAULT_CUSTOM_FRACTION));
        this.setCustomTireSmokeAlways(input.getBooleanOr("CustomTireSmokeAlways", false));
        this.setDespawnEnabled(input.getBooleanOr("DespawnEnabled", true));
        this.setItemIcon(input.getIntOr("ItemIcon", ITEM_ICON_VEHICLE));
        this.entityData.set(RAILWAY_MIDDLE_MODEL_IDS, input.getStringOr("RailwayMiddleModelIds", ""));
        this.entityData.set(RAILWAY_TAIL_MODEL_ID, input.getStringOr("RailwayTailModelId", ""));
        this.entityData.set(RAILWAY_CONFIGURED, input.getBooleanOr("RailwayConfigured", false));

        int[] mineturismo$packedHeadUuid = input.getIntArray("RailwayHeadUuid").orElse(new int[0]);
        this.railwayHeadUuid = mineturismo$packedHeadUuid.length == 4
                ? mineturismo$unpackUuidAt(mineturismo$packedHeadUuid, 0) : null;
        this.railwayTrailDistance = input.getFloatOr("RailwayTrailDistance", 0f);
        this.entityData.set(RAILWAY_FOLLOWER, this.railwayHeadUuid != null);

        this.entityData.set(RAILWAY_TRAIL_DISTANCE_SYNCED, this.railwayTrailDistance);
        if (this.railwayHeadUuid != null && this.level() instanceof ServerLevel mineturismo$railwayLoadLevel) {
            net.minecraft.server.packs.resources.ResourceManager mineturismo$railwayLoadResourceManager =
                    com.examplebett.compat.ModResources.getServerResourceManager(mineturismo$railwayLoadLevel.getServer());
            float mineturismo$railwayLoadHalfLength = CarFootprint.getLength(mineturismo$railwayLoadResourceManager, this.getModelId())
                    * CarFootprint.getCarScale(mineturismo$railwayLoadResourceManager, this.getModelId()) / 2.0f;
            this.entityData.set(RAILWAY_HALF_LENGTH_SYNCED, mineturismo$railwayLoadHalfLength);
        }

        this.railwayCoupledIds.clear();
        int[] mineturismo$packedCoupledIds = input.getIntArray("RailwayCoupledUuids").orElse(new int[0]);
        for (int mineturismo$i = 0; mineturismo$i + 3 < mineturismo$packedCoupledIds.length; mineturismo$i += 4) {
            this.railwayCoupledIds.add(mineturismo$unpackUuidAt(mineturismo$packedCoupledIds, mineturismo$i));
        }
        this.recordedFrames.clear();
        int[] packedFrames = input.getIntArray("GhostLapFrames").orElse(new int[0]);
        for (int i = 0; i + 5 < packedFrames.length; i += 6) {
            this.recordedFrames.add(new float[]{
                    Float.intBitsToFloat(packedFrames[i]),
                    Float.intBitsToFloat(packedFrames[i + 1]),
                    Float.intBitsToFloat(packedFrames[i + 2]),
                    Float.intBitsToFloat(packedFrames[i + 3]),
                    Float.intBitsToFloat(packedFrames[i + 4]),
                    Float.intBitsToFloat(packedFrames[i + 5])});
        }
    }

    @Override
    //? if >=1.21.6 {
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        this.writeCustomizationData(output);
        output.putInt("PlacedFromItem", this.placedFromItem);
        if (!this.trailerCargo.isEmpty()) {
            net.minecraft.world.ContainerHelper.saveAllItems(output.child("TrailerCargo"), this.trailerCargo.getItems());
        }
    }
    //?} else {
    /*
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        ValueOutput output = new ValueOutput(tag);
        this.writeCustomizationData(output);
        output.putInt("PlacedFromItem", this.placedFromItem);
        com.examplebett.compat.ModContainers.save(tag, "TrailerCargo", this.trailerCargo, this);
    }
    */
    //?}

    public void writeCustomizationData(ValueOutput output) {
        output.putString("ModelId", this.getModelId());
        output.putInt("Color", this.getColor());
        output.putString("FrontPlate", this.getFrontPlate());
        output.putFloat("FrontPlateX", this.getFrontPlateXAdjust());
        output.putFloat("FrontPlateY", this.getFrontPlateYAdjust());
        output.putFloat("FrontPlateZ", this.getFrontPlateZAdjust());
        output.putFloat("FrontPlateScale", this.getFrontPlateScaleAdjust());
        output.putFloat("FrontPlateYaw", this.getFrontPlateYawAdjust());
        output.putFloat("FrontPlatePitch", this.getFrontPlatePitchAdjust());
        output.putFloat("FrontPlateRoll", this.getFrontPlateRollAdjust());
        output.putString("RearPlate", this.getRearPlate());
        output.putFloat("RearPlateX", this.getRearPlateXAdjust());
        output.putFloat("RearPlateY", this.getRearPlateYAdjust());
        output.putFloat("RearPlateZ", this.getRearPlateZAdjust());
        output.putFloat("RearPlateScale", this.getRearPlateScaleAdjust());
        output.putFloat("RearPlateYaw", this.getRearPlateYawAdjust());
        output.putFloat("RearPlatePitch", this.getRearPlatePitchAdjust());
        output.putFloat("RearPlateRoll", this.getRearPlateRollAdjust());
        Vec3 tireSmokeOffset = this.getTireSmokeOffset();
        output.putFloat("TireSmokeX", (float) tireSmokeOffset.x);
        output.putFloat("TireSmokeY", (float) tireSmokeOffset.y);
        output.putFloat("TireSmokeZ", (float) tireSmokeOffset.z);
        output.putString("TireSmokeParticle", this.getTireSmokeParticle());
        output.putFloat("HeadlightX", this.getHeadlightXAdjust());
        output.putFloat("HeadlightY", this.getHeadlightYAdjust());
        output.putFloat("HeadlightZ", this.getHeadlightZAdjust());
        output.putFloat("TaillightX", this.getTaillightXAdjust());
        output.putFloat("TaillightY", this.getTaillightYAdjust());
        output.putFloat("TaillightZ", this.getTaillightZAdjust());
        output.putInt("LightMode", this.getLightMode().ordinal());
        output.putString("HeadlightColor", this.getHeadlightColorId());
        output.putBoolean("LightBlink", this.isLightBlinkEnabled());
        output.putFloat("WheelFrontZOffset", this.getWheelFrontZOffset());
        output.putFloat("WheelRearZOffset", this.getWheelRearZOffset());
        output.putFloat("WheelTrackHalfWidthOffset", this.getWheelTrackHalfWidthOffset());
        output.putFloat("WheelAxleHeightOffset", this.getWheelAxleHeightOffset());
        output.putFloat("BodyHeightOffset", this.getBodyHeightAdjust());
        output.putFloat("WheelCamberDegrees", this.getWheelCamberDegrees());
        output.putFloat("WheelScale", this.getWheelScaleAdjust());
        output.putFloat("WheelWidthScale", this.getWheelWidthScaleAdjust());
        output.putBoolean("ExtraRearAxleEnabled", this.isExtraRearAxleEnabled());
        output.putFloat("ExtraRearAxleZOffset", this.getExtraRearAxleZOffset());
        output.putBoolean("ExtraRearAxle2Enabled", this.isExtraRearAxle2Enabled());
        output.putFloat("ExtraRearAxle2ZOffset", this.getExtraRearAxle2ZOffset());
        output.putFloat("LipStretch", this.getLipStretchAdjust());
        output.putFloat("LipWidth", this.getLipWidthAdjust());
        output.putFloat("LipBandHeightCm", this.getLipBandHeightCm());
        output.putFloat("LipFrontExtend", this.getLipFrontExtendAdjust());
        output.putFloat("LimoStretchCm", this.getLimoStretchAdjust());
        output.putFloat("LimoZoneCm", this.getLimoZoneAdjust());
        output.putFloat("LimoCenterOffsetCm", this.getLimoCenterOffsetAdjust());
        output.putFloat("WidthStretchCm", this.getWidthStretchAdjust());
        output.putFloat("WidthZoneCm", this.getWidthZoneAdjust());
        output.putFloat("WidthCenterOffsetCm", this.getWidthCenterOffsetAdjust());
        output.putFloat("TiltRollDegrees", this.getTiltRollDegrees());
        output.putFloat("TiltPitchDegrees", this.getTiltPitchDegrees());
        output.putFloat("TiltHeightCm", this.getTiltHeightCm());
        output.putFloat("PassengerXAdjust", this.getPassengerXAdjust());
        output.putFloat("PassengerYAdjust", this.getPassengerYAdjust());
        output.putFloat("PassengerZAdjust", this.getPassengerZAdjust());
        output.putString("RearWingEnabledIds", this.getRearWingEnabledIds());
        output.putString("RearWingTransforms", this.entityData.get(REAR_WING_TRANSFORMS));
        output.putString("PoliceLightEnabledIds", this.getPoliceLightEnabledIds());
        output.putString("PoliceLightTransforms", this.entityData.get(POLICE_LIGHT_TRANSFORMS));
        output.putString("TaxiSignEnabledIds", this.getTaxiSignEnabledIds());
        output.putString("TaxiSignTransforms", this.entityData.get(TAXI_SIGN_TRANSFORMS));
        output.putString("OtherPartEnabledIds", this.getOtherPartEnabledIds());
        output.putString("OtherPartTransforms", this.entityData.get(OTHER_PART_TRANSFORMS));
        output.putString("TextParts", this.getTextPartsRaw());
        output.putString("UnderNeon", this.getUnderNeonRaw());
        output.putString("BrandLogos", this.getBrandLogosRaw());
        output.putString("FrontBrakeCaliperEnabledIds", this.getFrontBrakeCaliperEnabledIds());
        output.putString("FrontBrakeCaliperTransforms", this.entityData.get(FRONT_BRAKE_CALIPER_TRANSFORMS));
        output.putString("RearBrakeCaliperEnabledIds", this.getRearBrakeCaliperEnabledIds());
        output.putString("RearBrakeCaliperTransforms", this.entityData.get(REAR_BRAKE_CALIPER_TRANSFORMS));
        output.putString("ExhaustEnabledIds", this.getExhaustEnabledIds());
        output.putString("ExhaustTransforms", this.entityData.get(EXHAUST_TRANSFORMS));
        output.putString("TowLeaderUuid", this.towLeaderUuid != null ? this.towLeaderUuid.toString() : "");
        output.putString("TrailerHeadUuid", this.trailerHeadUuid != null ? this.trailerHeadUuid.toString() : "");
        output.putBoolean("PoliceLightsOn", this.isPoliceLightsOn());
        output.putBoolean("PoliceSirenOn", this.isPoliceSirenOn());
        output.putString("WheelModelIdOverride", this.getWheelModelIdOverride());
        output.putString("WheelColorOverride", this.getWheelColorOverride());
        output.putFloat("CarScale", this.getCarScaleAdjust());
        output.putFloat("CameraDistanceAdjust", this.getCameraDistanceAdjust());
        output.putFloat("CameraHeightAdjust", this.getCameraHeightAdjust());
        output.putString("Livery", this.getLiveryOverride());
        output.putString("EngineSound", this.getEngineSoundId());
        output.putString("HornSound", this.getHornSoundId());
        output.putString("SirenSound", this.getSirenSoundId());
        output.putBoolean("BombInstalled", this.isBombInstalled());
        output.putString("BombOwner", this.bombOwnerUuid != null ? this.bombOwnerUuid.toString() : "");
        output.putFloat("EnginePitchIntensity", this.getEnginePitchIntensity());
        output.putFloat("EngineTopSpeedPitch", this.getEngineTopSpeedPitch());
        output.putFloat("EngineLoopUseFraction", this.getEngineLoopUseFraction());
        output.putInt("DriveMode", this.getDriveMode().ordinal());
        output.putBoolean("ChaseMode", this.isChaseModeEnabled());
        output.putBoolean("ChaosMode", this.isChaosModeEnabled());
        output.putFloat("CustomTopSpeedKmh", this.getCustomTopSpeedKmh());
        output.putFloat("CustomAccelFraction", this.getCustomAccelFraction());
        output.putFloat("CustomBrakeFraction", this.getCustomBrakeFraction());
        output.putFloat("CustomHandlingFraction", this.getCustomHandlingFraction());
        output.putBoolean("CustomTireSmokeAlways", this.isCustomTireSmokeAlways());
        output.putBoolean("DespawnEnabled", this.isDespawnEnabled());
        output.putInt("ItemIcon", this.getItemIcon());
        output.putString("RailwayMiddleModelIds", this.getRailwayMiddleModelIds());
        output.putString("RailwayTailModelId", this.getRailwayTailModelId());
        output.putBoolean("RailwayConfigured", this.isRailwayConfigured());
        if (this.railwayHeadUuid != null) {
            int[] mineturismo$packedHeadUuid = new int[4];
            mineturismo$packUuidInto(mineturismo$packedHeadUuid, 0, this.railwayHeadUuid);
            output.putIntArray("RailwayHeadUuid", mineturismo$packedHeadUuid);
        } else {
            output.putIntArray("RailwayHeadUuid", new int[0]);
        }
        output.putFloat("RailwayTrailDistance", this.railwayTrailDistance);
        int[] mineturismo$packedCoupledIds = new int[this.railwayCoupledIds.size() * 4];
        for (int mineturismo$i = 0; mineturismo$i < this.railwayCoupledIds.size(); mineturismo$i++) {
            mineturismo$packUuidInto(mineturismo$packedCoupledIds, mineturismo$i * 4, this.railwayCoupledIds.get(mineturismo$i));
        }
        output.putIntArray("RailwayCoupledUuids", mineturismo$packedCoupledIds);

        int[] packedFrames = new int[this.recordedFrames.size() * 6];
        for (int i = 0; i < this.recordedFrames.size(); i++) {
            float[] frame = this.recordedFrames.get(i);
            for (int j = 0; j < 6; j++) {
                packedFrames[i * 6 + j] = Float.floatToRawIntBits(frame[j]);
            }
        }
        output.putIntArray("GhostLapFrames", packedFrames);
    }

    public CompoundTag exportCustomizationTag() {
        CompoundTag mineturismo$tag;
        //? if >=1.21.6 {
        TagValueOutput output = TagValueOutput.createWithoutContext(ProblemReporter.DISCARDING);
        this.writeCustomizationData(output);
        mineturismo$tag = output.buildResult();
        //?} else {
        /*
        mineturismo$tag = new CompoundTag();
        this.writeCustomizationData(new ValueOutput(mineturismo$tag));
        */
        //?}
        this.mineturismo$writeRailwayCarsTag(mineturismo$tag);
        this.mineturismo$writeCoupledTrailerTag(mineturismo$tag);
        return mineturismo$tag;
    }

    public void importCustomizationTag(CompoundTag tag) {
        //? if >=1.21.6 {
        ValueInput input = TagValueInput.create(ProblemReporter.DISCARDING, this.registryAccess(), tag);
        this.readCustomizationData(input);
        //?} else
        /*this.readCustomizationData(new ValueInput(tag));*/
        this.mineturismo$readRailwayCarsTag(tag);
        this.mineturismo$readCoupledTrailerTag(tag);
    }

    private static final double PASSENGER_HEIGHT_OFFSET_PER_BASE_SCALE = 0.5 - 2.0;

    private static final double PLAYER_VEHICLE_ATTACHMENT_Y = 0.6;

    private static double mineturismo$vehicleAttachmentY(Entity passenger) {
        return passenger instanceof Player ? PLAYER_VEHICLE_ATTACHMENT_Y : 0.0;
    }

    private Vec3 resolvePassengerLocalOffset() {
        float yawRad = (float) Math.toRadians(this.getYRot());
        float forwardX = -Mth.sin(yawRad);
        float forwardZ = Mth.cos(yawRad);
        float rightX = forwardZ;
        float rightZ = -forwardX;
        float localX = this.getPassengerXOffset();
        float localZ = this.getPassengerZOffset();
        return new Vec3(rightX * localX + forwardX * localZ, this.getPassengerYOffset(), rightZ * localX + forwardZ * localZ);
    }

    //? if >=1.20.2 {
    //? if >=1.20.5 {

    private boolean mineturismo$realSeatDebugLogged = false;

    @Override
    protected Vec3 getPassengerAttachmentPoint(Entity entity, EntityDimensions dimensions, float partialTick) {

        Vec3 passengerLocalOffset = this.resolvePassengerLocalOffset();
        Vec3 base = super.getPassengerAttachmentPoint(entity, dimensions, partialTick);
        float baseScale = this.getBaseCarScale();
        float bodyHeight = this.getBodyHeightOffset();
        Vec3 result = base.add(passengerLocalOffset.x,
                PASSENGER_HEIGHT_OFFSET_PER_BASE_SCALE * baseScale + bodyHeight + passengerLocalOffset.y,
                passengerLocalOffset.z);
        if (!this.mineturismo$realSeatDebugLogged) {
            this.mineturismo$realSeatDebugLogged = true;
            AiObjectMod.LOGGER.debug(
                    "real seat debug [{}] dimensionsHeight={} superY={} baseCarScale={} bodyHeightOffset={} passengerYOffset={} resultY={}",
                    this.getModelId(), dimensions.height(), base.y, baseScale, bodyHeight, passengerLocalOffset.y, result.y);
        }
        return result;
    }
    //?} else
    /*@Override
    protected org.joml.Vector3f getPassengerAttachmentPoint(Entity entity, EntityDimensions dimensions, float partialTick) {
        Vec3 passengerLocalOffset = this.resolvePassengerLocalOffset();
        return super.getPassengerAttachmentPoint(entity, dimensions, partialTick)
                .add((float) passengerLocalOffset.x,
                        (float) (PASSENGER_HEIGHT_OFFSET_PER_BASE_SCALE * this.getBaseCarScale() + this.getBodyHeightOffset() + passengerLocalOffset.y),
                        (float) passengerLocalOffset.z);
    }*/
    //?}

    //? if <1.20 {
    /*
    @Override
    public void positionRider(Entity entity) {
        if (this.hasPassenger(entity)) {

            Vec3 passengerLocalOffset = this.resolvePassengerLocalOffset();
            entity.setPos(
                    ModEntity.getX(this) + passengerLocalOffset.x,
                    ModEntity.getY(this) + this.getBbHeight() + PASSENGER_HEIGHT_OFFSET_PER_BASE_SCALE * this.getBaseCarScale() + this.getBodyHeightOffset() + passengerLocalOffset.y
                            - mineturismo$vehicleAttachmentY(entity),
                    ModEntity.getZ(this) + passengerLocalOffset.z);
        }
    }
    */
    //?}

    //? if >=1.20 && <1.20.2 {
    /*
    @Override
    protected void positionRider(Entity entity, Entity.MoveFunction moveFunction) {

        Vec3 passengerLocalOffset = this.resolvePassengerLocalOffset();
        moveFunction.accept(entity,
                this.getX() + passengerLocalOffset.x,
                this.getY() + this.getBbHeight() + PASSENGER_HEIGHT_OFFSET_PER_BASE_SCALE * this.getBaseCarScale() + this.getBodyHeightOffset() + passengerLocalOffset.y
                        - mineturismo$vehicleAttachmentY(entity),
                this.getZ() + passengerLocalOffset.z);
    }
    */
    //?}

    //? if >=1.17 {
    private static boolean mineturismo$isItem(ItemStack stack, Item item) {
        return stack.is(item);
    }
    //?} else
    /*private static boolean mineturismo$isItem(ItemStack stack, Item item) {
        return stack.getItem() == item;
    }*/

    //? if >=1.16 {
    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {

        if (this.isBombBurning()) {
            return InteractionResult.FAIL;
        }

        if (mineturismo$isItem(player.getItemInHand(hand), AiObjectModItems.TOW_ROPE)) {
            if (this.level() instanceof ServerLevel towServerLevel && !this.isGhost()) {
                this.handleTowRopeUse(towServerLevel, player, hand);
            }
            return InteractionResult.SUCCESS;
        }
        if (mineturismo$isItem(player.getItemInHand(hand), AiObjectModItems.CUSTOM_WRENCH)) {

            if (this.isGhost()) {
                return InteractionResult.SUCCESS;
            }

            if (!this.level().isClientSide()) {
                player.startRiding(this);
            }
            if (this.level().isClientSide() && wrenchMenuOpener != null) {
                wrenchMenuOpener.open(this, player);
            }
            return InteractionResult.SUCCESS;
        }
        if (mineturismo$isItem(player.getItemInHand(hand), AiObjectModItems.WHEEL_WRENCH)) {
            if (this.isGhost()) {
                return InteractionResult.SUCCESS;
            }
            if (!this.level().isClientSide()) {
                player.startRiding(this);
            }
            if (this.level().isClientSide() && wheelWrenchMenuOpener != null) {
                wheelWrenchMenuOpener.open(this, player);
            }
            return InteractionResult.SUCCESS;
        }
        if (mineturismo$isItem(player.getItemInHand(hand), AiObjectModItems.DEBUG_WRENCH)) {
            if (this.isGhost()) {
                return InteractionResult.SUCCESS;
            }
            if (this.level().isClientSide() && debugWrenchMenuOpener != null) {
                debugWrenchMenuOpener.open(this, player);
            }
            return InteractionResult.SUCCESS;
        }

        if (this.hasVehicleCategory(VehicleCategory.TRAILER) && !this.isGhost()) {
            if (!this.level().isClientSide()) {
                player.openMenu(new net.minecraft.world.SimpleMenuProvider(
                        (containerId, inventory, menuPlayer) -> net.minecraft.world.inventory.ChestMenu.threeRows(containerId, inventory, this.trailerCargo),
                        this.getName()));
            }
            return InteractionResult.SUCCESS;
        }
        if (!this.level().isClientSide() && this.canAddPassenger(player)) {

            AiObjectMod.LOGGER.debug(
                    "[railway-board] car={} modelId={} passengersEmpty={} isTownCar={} railwayRouteActive={} railwayHeadUuid={} hasRAILWAY={} hasPASSENGER_TRANSPORT={}",
                    this.getId(), this.getModelId(), this.getPassengers().isEmpty(), this.isTownCar(),
                    this.railwayRouteActive, this.railwayHeadUuid,
                    this.hasVehicleCategory(VehicleCategory.RAILWAY), this.hasVehicleCategory(VehicleCategory.PASSENGER_TRANSPORT));
            if (this.getPassengers().isEmpty()
                    && (this.isTownCar() || this.railwayRouteActive || this.railwayHeadUuid != null)
                    && this.level() instanceof ServerLevel serverLevel) {
                if (this.hasVehicleCategory(VehicleCategory.RAILWAY)) {

                    if (!mineturismo$hasEmerald(player)) {
                        if (player instanceof ServerPlayer serverPlayer) {
                            ShowHudWarningPayload warningPayload =
                                    new ShowHudWarningPayload("gui.mineturismo.passenger_transport.emerald_required");
                            //? if >=1.20.5 {
                            ModNetworking.sendToClient(serverPlayer, warningPayload);
                            //?} else
                            /*ModNetworking.sendToClient(serverPlayer, ShowHudWarningPayload.ID, warningPayload, ShowHudWarningPayload::write);*/
                        }
                        return InteractionResult.SUCCESS;
                    }
                    player.startRiding(this);
                    mineturismo$consumeOneEmerald(player);
                    return InteractionResult.SUCCESS;
                }
                if (this.hasVehicleCategory(VehicleCategory.PASSENGER_TRANSPORT)) {

                    return this.mineturismo$tryBoardPassengerTransport(serverLevel, player);
                }

                this.mineturismo$detachFromTraffic(serverLevel);
            }
            player.startRiding(this);
        }
        return InteractionResult.SUCCESS;
    }
    //?}
    //? if <1.16 {
    /*@Override
    protected boolean mobInteract(Player player, InteractionHand hand) {
        if (mineturismo$isItem(player.getItemInHand(hand), AiObjectModItems.CUSTOM_WRENCH)) {
            if (this.isGhost()) {
                return true;
            }
            if (!this.level().isClientSide()) {
                player.startRiding(this);
            }
            if (this.level().isClientSide() && wrenchMenuOpener != null) {
                wrenchMenuOpener.open(this, player);
            }
            return true;
        }
        if (mineturismo$isItem(player.getItemInHand(hand), AiObjectModItems.WHEEL_WRENCH)) {
            if (this.isGhost()) {
                return true;
            }
            if (!this.level().isClientSide()) {
                player.startRiding(this);
            }
            if (this.level().isClientSide() && wheelWrenchMenuOpener != null) {
                wheelWrenchMenuOpener.open(this, player);
            }
            return true;
        }
        if (mineturismo$isItem(player.getItemInHand(hand), AiObjectModItems.DEBUG_WRENCH)) {
            if (this.isGhost()) {
                return true;
            }
            if (this.level().isClientSide() && debugWrenchMenuOpener != null) {
                debugWrenchMenuOpener.open(this, player);
            }
            return true;
        }
        if (!this.level().isClientSide() && this.canAddPassenger(player)) {
            AiObjectMod.LOGGER.debug(
                    "[railway-board] car={} modelId={} passengersEmpty={} isTownCar={} railwayRouteActive={} railwayHeadUuid={} hasRAILWAY={} hasPASSENGER_TRANSPORT={}",
                    this.getId(), this.getModelId(), this.getPassengers().isEmpty(), this.isTownCar(),
                    this.railwayRouteActive, this.railwayHeadUuid,
                    this.hasVehicleCategory(VehicleCategory.RAILWAY), this.hasVehicleCategory(VehicleCategory.PASSENGER_TRANSPORT));
            if (this.getPassengers().isEmpty()
                    && (this.isTownCar() || this.railwayRouteActive || this.railwayHeadUuid != null)
                    && this.level() instanceof ServerLevel serverLevel) {
                if (this.hasVehicleCategory(VehicleCategory.RAILWAY)) {
                    if (!mineturismo$hasEmerald(player)) {
                        if (player instanceof ServerPlayer serverPlayer) {
                            ShowHudWarningPayload warningPayload =
                                    new ShowHudWarningPayload("gui.mineturismo.passenger_transport.emerald_required");
                            ModNetworking.sendToClient(serverPlayer, ShowHudWarningPayload.ID, warningPayload, ShowHudWarningPayload::write);
                        }
                        return true;
                    }
                    player.startRiding(this);
                    mineturismo$consumeOneEmerald(player);
                    return true;
                }
                if (this.hasVehicleCategory(VehicleCategory.PASSENGER_TRANSPORT)) {
                    return this.mineturismo$tryBoardPassengerTransport(serverLevel, player);
                }
                this.mineturismo$detachFromTraffic(serverLevel);
            }
            player.startRiding(this);
        }
        return true;
    }*/
    //?}

    private void mineturismo$detachFromTraffic(ServerLevel serverLevel) {
        this.townCarActive = false;
        this.entityData.set(IS_TOWN_CAR, false);
        this.setDriveMode(DriveMode.CLASSIC);
        WaypointNetworkSavedData.get(serverLevel).untrackActiveTownCar(this.getId());
    }

    //? if >=1.16 {
    private InteractionResult mineturismo$tryBoardPassengerTransport(ServerLevel serverLevel, Player player) {
        WaypointNetworkSavedData data = WaypointNetworkSavedData.get(serverLevel);
        List<Integer> route = this.mineturismo$planChauffeurRoute(data);

        AiObjectMod.LOGGER.debug(
                "[PassengerTransport] board attempt: car={} townCurrentNodeId={} routeFound={} routeLen={}",
                this.getId(), this.townCurrentNodeId, route != null, route != null ? route.size() : -1);
        if (route == null) {

            this.mineturismo$detachFromTraffic(serverLevel);
            player.startRiding(this);
            return InteractionResult.SUCCESS;
        }
        if (!mineturismo$hasEmerald(player)) {
            AiObjectMod.LOGGER.debug("[PassengerTransport] board denied: no emerald, car={}", this.getId());
            if (player instanceof ServerPlayer serverPlayer) {
                ShowHudWarningPayload warningPayload =
                        new ShowHudWarningPayload("gui.mineturismo.passenger_transport.emerald_required");
                //? if >=1.20.5 {
                ModNetworking.sendToClient(serverPlayer, warningPayload);
                //?} else
                /*ModNetworking.sendToClient(serverPlayer, ShowHudWarningPayload.ID, warningPayload, ShowHudWarningPayload::write);*/
            }
            return InteractionResult.SUCCESS;
        }
        player.startRiding(this);
        this.mineturismo$setChauffeurActive(true);
        AiObjectMod.LOGGER.debug("[PassengerTransport] chauffeur trip started: car={} routeLen={} driveMode={}",
                this.getId(), route.size(), this.getDriveMode());
        this.chauffeurRoute = route;
        this.chauffeurRouteIndex = 0;
        this.chauffeurPayerId = player.getUUID();
        return InteractionResult.SUCCESS;
    }
    //?}
    //? if <1.16 {
    /*private boolean mineturismo$tryBoardPassengerTransport(ServerLevel serverLevel, Player player) {
        WaypointNetworkSavedData data = WaypointNetworkSavedData.get(serverLevel);
        List<Integer> route = this.mineturismo$planChauffeurRoute(data);
        AiObjectMod.LOGGER.debug(
                "[PassengerTransport] board attempt: car={} townCurrentNodeId={} routeFound={} routeLen={}",
                this.getId(), this.townCurrentNodeId, route != null, route != null ? route.size() : -1);
        if (route == null) {
            this.mineturismo$detachFromTraffic(serverLevel);
            player.startRiding(this);
            return true;
        }
        if (!mineturismo$hasEmerald(player)) {
            AiObjectMod.LOGGER.debug("[PassengerTransport] board denied: no emerald, car={}", this.getId());
            if (player instanceof ServerPlayer serverPlayer) {
                ShowHudWarningPayload warningPayload =
                        new ShowHudWarningPayload("gui.mineturismo.passenger_transport.emerald_required");
                ModNetworking.sendToClient(serverPlayer, ShowHudWarningPayload.ID, warningPayload, ShowHudWarningPayload::write);
            }
            return true;
        }
        player.startRiding(this);
        this.mineturismo$setChauffeurActive(true);
        AiObjectMod.LOGGER.debug("[PassengerTransport] chauffeur trip started: car={} routeLen={} driveMode={}",
                this.getId(), route.size(), this.getDriveMode());
        this.chauffeurRoute = route;
        this.chauffeurRouteIndex = 0;
        this.chauffeurPayerId = player.getUUID();
        return true;
    }*/
    //?}

    private List<Integer> mineturismo$planChauffeurRoute(WaypointNetworkSavedData data) {
        WaypointNode start = data.getNodes().get(this.townCurrentNodeId);
        if (start == null) {
            return null;
        }
        Map<Integer, Integer> parent = new java.util.HashMap<>();
        java.util.Set<Integer> visited = new java.util.HashSet<>();
        java.util.Deque<Integer> queue = new java.util.ArrayDeque<>();
        List<Integer> order = new ArrayList<>();
        visited.add(start.id());
        queue.add(start.id());
        while (!queue.isEmpty()) {
            int currentId = queue.poll();
            order.add(currentId);
            WaypointNode currentNode = data.getNodes().get(currentId);
            if (currentNode == null) {
                continue;
            }
            for (int nextId : forwardTargets(currentNode)) {
                if (visited.add(nextId)) {
                    parent.put(nextId, currentId);
                    queue.add(nextId);
                }
            }
        }
        order.remove(0);
        if (order.isEmpty()) {
            return null;
        }
        int minHopSkip = Math.min(order.size() - 1, TOWN_CHAUFFEUR_MIN_HOP_SKIP);
        List<Integer> hopFiltered = order.subList(minHopSkip, order.size());

        List<Integer> candidates = new ArrayList<>();
        for (int candidateId : hopFiltered) {
            WaypointNode candidateNode = data.getNodes().get(candidateId);
            if (candidateNode == null) {
                continue;
            }
            double dx = candidateNode.pos().getX() - start.pos().getX();
            double dz = candidateNode.pos().getZ() - start.pos().getZ();
            if (Math.sqrt(dx * dx + dz * dz) >= TOWN_CHAUFFEUR_MIN_DISTANCE) {
                candidates.add(candidateId);
            }
        }
        if (candidates.isEmpty()) {
            candidates = hopFiltered;
        }
        int destId = candidates.get(this.random.nextInt(candidates.size()));

        java.util.LinkedList<Integer> path = new java.util.LinkedList<>();
        int cursor = destId;
        while (cursor != start.id()) {
            path.addFirst(cursor);
            cursor = parent.get(cursor);
        }
        path.addFirst(start.id());
        return path;
    }

    private static final int TOWN_CHAUFFEUR_MIN_HOP_SKIP = 5;

    private static final double TOWN_CHAUFFEUR_MIN_DISTANCE = 80.0;

    private static boolean mineturismo$hasEmerald(Player player) {
        return com.examplebett.compat.ModPlayer.getInventory(player).countItem(net.minecraft.world.item.Items.EMERALD) > 0;
    }

    private static void mineturismo$consumeOneEmerald(Player player) {
        net.minecraft.world.entity.player.Inventory inventory = com.examplebett.compat.ModPlayer.getInventory(player);
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (mineturismo$isItem(stack, net.minecraft.world.item.Items.EMERALD)) {
                stack.shrink(1);
                return;
            }
        }
    }

    private static volatile boolean hitboxDisabled = false;

    public static boolean isHitboxDisabled() {
        return hitboxDisabled;
    }

    public static void setHitboxDisabled(boolean disabled) {
        hitboxDisabled = disabled;
        DebugSettingsStore.save();
    }

    private static volatile boolean breakVanillaBlocksEnabled = false;
    private static volatile boolean mobDamageEnabled = false;

    public static boolean isBreakVanillaBlocksEnabled() {
        return breakVanillaBlocksEnabled;
    }

    public static void setBreakVanillaBlocksEnabled(boolean enabled) {
        breakVanillaBlocksEnabled = enabled;
        DebugSettingsStore.save();
    }

    public static boolean isMobDamageEnabled() {
        return mobDamageEnabled;
    }

    public static void setMobDamageEnabled(boolean enabled) {
        mobDamageEnabled = enabled;
        DebugSettingsStore.save();
    }

    private static volatile boolean securityAlarmEnabled = true;

    private static volatile boolean forceRotateSideEnabled = false;
    private static volatile boolean forceRotateFrontEnabled = false;

    private static volatile float tumbleHeightMultiplier = 1.0f;
    private static volatile float tumbleRotationMultiplier = 1.0f;
    private static volatile float pushDistanceMultiplier = DEFAULT_PUSH_DISTANCE_MULTIPLIER;
    private static volatile float suspensionIntensityMultiplier = DEFAULT_SUSPENSION_INTENSITY_MULTIPLIER;

    private static volatile boolean sparkPositionOffsetEnabled = false;

    public static boolean isSecurityAlarmEnabled() {
        return securityAlarmEnabled;
    }

    public static void setSecurityAlarmEnabled(boolean enabled) {
        securityAlarmEnabled = enabled;
        DebugSettingsStore.save();
    }

    public static boolean isForceRotateSideEnabled() {
        return forceRotateSideEnabled;
    }

    public static void setForceRotateSideEnabled(boolean enabled) {
        forceRotateSideEnabled = enabled;
        DebugSettingsStore.save();
    }

    public static boolean isForceRotateFrontEnabled() {
        return forceRotateFrontEnabled;
    }

    public static void setForceRotateFrontEnabled(boolean enabled) {
        forceRotateFrontEnabled = enabled;
        DebugSettingsStore.save();
    }

    public static float getTumbleHeightMultiplier() {
        return tumbleHeightMultiplier;
    }

    public static void setTumbleHeightMultiplier(float multiplier) {
        tumbleHeightMultiplier = Mth.clamp(multiplier, ACTION_MULTIPLIER_MIN, ACTION_MULTIPLIER_MAX);
        DebugSettingsStore.save();
    }

    public static float getTumbleRotationMultiplier() {
        return tumbleRotationMultiplier;
    }

    public static void setTumbleRotationMultiplier(float multiplier) {
        tumbleRotationMultiplier = Mth.clamp(multiplier, ACTION_MULTIPLIER_MIN, ACTION_MULTIPLIER_MAX);
        DebugSettingsStore.save();
    }

    public static float getPushDistanceMultiplier() {
        return pushDistanceMultiplier;
    }

    public static void setPushDistanceMultiplier(float multiplier) {
        pushDistanceMultiplier = Mth.clamp(multiplier, PUSH_DISTANCE_MULTIPLIER_MIN, PUSH_DISTANCE_MULTIPLIER_MAX);
        DebugSettingsStore.save();
    }

    public static float getSuspensionIntensityMultiplier() {
        return suspensionIntensityMultiplier;
    }

    public static void setSuspensionIntensityMultiplier(float multiplier) {
        suspensionIntensityMultiplier = Mth.clamp(multiplier, SUSPENSION_INTENSITY_MULTIPLIER_MIN, SUSPENSION_INTENSITY_MULTIPLIER_MAX);
        DebugSettingsStore.save();
    }

    public static boolean isSparkPositionOffsetEnabled() {
        return sparkPositionOffsetEnabled;
    }

    public static void setSparkPositionOffsetEnabled(boolean enabled) {
        sparkPositionOffsetEnabled = enabled;
        DebugSettingsStore.save();
    }

    @Override
    public void move(MoverType type, Vec3 movement) {
        boolean npcActive = (this.chaseCarActive || this.chaosCarActive || this.townCarActive || this.railwayRouteActive
                || this.responderActive)
                && (!(this.getControllingPassenger() instanceof Player) || this.mineturismo$isNpcDrivenWithPassenger());
        if (npcActive) {
            super.move(type, new Vec3(0.0, movement.y, 0.0));
            double newX = ModEntity.getX(this) + movement.x;
            double newZ = ModEntity.getZ(this) + movement.z;
            this.setPos(newX, this.resolveNpcStepUpY(newX, newZ), newZ);
            return;
        }
        super.move(type, movement);
    }

    private double resolveNpcStepUpY(double newX, double newZ) {
        double baseY = ModEntity.getY(this);

        //? if >=1.16.2 {
        AABB testBox = this.getDimensions(this.getPose()).makeBoundingBox(newX, baseY, newZ);
        //?} else {
        /*net.minecraft.world.entity.EntityDimensions npcStepDimensions = this.getDimensions(this.getPose());
        double npcStepHalfWidth = npcStepDimensions.width / 2.0;
        AABB testBox = new AABB(newX - npcStepHalfWidth, baseY, newZ - npcStepHalfWidth,
                newX + npcStepHalfWidth, baseY + npcStepDimensions.height, newZ + npcStepHalfWidth);*/
        //?}
        if (this.level().noCollision(this, testBox)) {
            return baseY;
        }
        //? if >=1.19.4 {
        float maxStep = this.maxUpStep();
        //?} else
        /*float maxStep = this.maxUpStep;*/
        for (float step = NPC_STEP_UP_INCREMENT; step <= maxStep + 1.0E-4F; step += NPC_STEP_UP_INCREMENT) {
            AABB raised = testBox.move(0.0, step, 0.0);
            if (this.level().noCollision(this, raised)) {
                return baseY + step;
            }
        }
        return baseY;
    }

    //? if >=1.21.6 {
    @Override
    public boolean canBeCollidedWith(Entity entity) {
        if (hitboxDisabled) {
            return false;
        }

        boolean thisNpc = this.chaseCarActive || this.chaosCarActive || this.townCarActive || this.railwayRouteActive;
        if (thisNpc && entity instanceof CarEntity other
                && (other.chaseCarActive || other.chaosCarActive || other.townCarActive || other.railwayRouteActive)) {
            return false;
        }

        if (entity instanceof CarEntity other && this.isInSameRailwayConsist(other)) {
            return false;
        }
        return true;
    }
    //?}
    //? if >=1.16.2 && <1.21.6 {
    /*@Override
    public boolean canBeCollidedWith() {
        return !hitboxDisabled;
    }*/
    //?}
    //? if <1.16.2 {
    /*public boolean canBeCollidedWith() {
        return !hitboxDisabled;
    }*/
    //?}

    @Override
    public boolean isPushable() {

        return !hitboxDisabled;
    }

    @Override
    //? if >=1.21.2 {
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
    //?} else
    /*public boolean hurt(DamageSource source, float amount) {*/
        if (source.getEntity() instanceof Player player) {

            if (!this.level().isClientSide() && !this.isGhost() && !this.isRemoved()
                    && !com.examplebett.compat.ModPlayer.hasInstabuild(player)) {
                ItemStack drop = com.examplebett.item.VehicleItemIcons.dropStackFor(this);
                if (!drop.isEmpty()) {
                    this.level().addFreshEntity(new net.minecraft.world.entity.item.ItemEntity(this.level(),
                            ModEntity.getX(this), ModEntity.getY(this) + 0.5, ModEntity.getZ(this), drop));
                }

                if (this.getPlacedFromItem() == PLACED_FROM_BOXED_CAR) {
                    this.discardCoupledTrailer();
                }
            }
            this.discard();
            return true;
        }
        return false;
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    static {
        DebugSettingsStore.load();
    }
}
