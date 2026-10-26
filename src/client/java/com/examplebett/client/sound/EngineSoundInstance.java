package com.examplebett.client.sound;

import com.examplebett.entity.CarEntity;
import com.examplebett.entity.EngineSounds;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;

public class EngineSoundInstance extends AbstractTickableSoundInstance {
    private static final float IDLE_PITCH_RATIO = 0.5f;

    private static final float IDLE_VOLUME_FACTOR = 0.45f;

    private static final float IDLE_VOLUME_FALL_PER_TICK = 0.02f;
    private static final float IDLE_VOLUME_RAMP_SPEED_FRACTION = 0.12f;

    public enum Layer { NORMAL, HYBRID_LOW, HYBRID_HIGH }

    public static final String HYBRID_LOW_SOUND_ID = "engine_quiet_2";

    private static final float HYBRID_CROSSFADE_START = 0.20f;
    private static final float HYBRID_CROSSFADE_END = 0.60f;

    private static final float HYBRID_CROSSFADE_STEP_PER_TICK = 0.015f;

    private static String soundIdFor(CarEntity car, Layer layer) {
        return layer == Layer.HYBRID_LOW ? HYBRID_LOW_SOUND_ID : car.getEngineSoundId();
    }

    private float hybridLayerFactor(float speedFraction) {
        if (this.layer == Layer.NORMAL) {
            return 1.0f;
        }
        float raw = Mth.clamp((speedFraction - HYBRID_CROSSFADE_START) / (HYBRID_CROSSFADE_END - HYBRID_CROSSFADE_START), 0f, 1f);
        float target = raw * raw * (3f - 2f * raw);
        if (!this.hybridCrossfadeInitialized) {
            this.hybridCrossfade = target;
            this.hybridCrossfadeInitialized = true;
        } else {
            float delta = target - this.hybridCrossfade;
            this.hybridCrossfade += Mth.clamp(delta, -HYBRID_CROSSFADE_STEP_PER_TICK, HYBRID_CROSSFADE_STEP_PER_TICK);
        }
        double angle = this.hybridCrossfade * (Math.PI / 2.0);
        return (float) (this.layer == Layer.HYBRID_LOW ? Math.cos(angle) : Math.sin(angle));
    }

    private static final int SEAM_DIP_WINDOW_TICKS = 4;
    private static final float SEAM_DIP_AMOUNT = 0.25f;

    private static final int LOOP_RESTART_FADE_TICKS = 20;

    private final CarEntity car;
    private final String soundId;
    private final int loopDurationTicks;
    private final int useDurationTicks;
    private final float baseVolume;
    private final boolean ambient;
    private int ticksAlive;
    private float idleVolumeFactor = 1.0f;
    private final Layer layer;

    private float hybridCrossfade;
    private boolean hybridCrossfadeInitialized;

    public EngineSoundInstance(CarEntity car) {
        this(car, 1.0f, false);
    }

    public EngineSoundInstance(CarEntity car, float baseVolume, boolean ambient) {
        this(car, baseVolume, ambient, Layer.NORMAL);
    }

    public EngineSoundInstance(CarEntity car, float baseVolume, boolean ambient, Layer layer) {

        //? if >=1.19 {
        super(EngineSounds.byId(soundIdFor(car, layer)).sound(), SoundSource.NEUTRAL, SoundInstance.createUnseededRandom());
        //?} else
        /*super(EngineSounds.byId(soundIdFor(car, layer)).sound(), SoundSource.NEUTRAL);*/
        this.car = car;
        this.ambient = ambient;
        this.layer = layer;
        this.soundId = soundIdFor(car, layer);
        this.loopDurationTicks = EngineSounds.byId(this.soundId).loopDurationTicks();

        this.useDurationTicks = layer == Layer.HYBRID_LOW ? this.loopDurationTicks : car.getEngineLoopUseTicks(this.loopDurationTicks);
        this.baseVolume = baseVolume;
        this.looping = true;
        this.delay = 0;

        this.volume = (this.layer != Layer.NORMAL || this.useDurationTicks < this.loopDurationTicks) ? 0.0f : baseVolume;
        this.pitch = 1.0f;
        this.x = car.getX();
        this.y = car.getY();
        this.z = car.getZ();
        if (ambient) {
            this.attenuation = SoundInstance.Attenuation.LINEAR;
        } else {

            this.attenuation = SoundInstance.Attenuation.NONE;
        }
    }

    public boolean matchesCurrentSoundId() {

        return this.layer == Layer.HYBRID_LOW
                || (this.soundId.equals(this.car.getEngineSoundId())
                && (this.layer == Layer.HYBRID_HIGH) == this.car.hasVehicleCategory(com.examplebett.entity.VehicleCategory.HYBRID));
    }

    @Override
    public boolean canStartSilent() {
        return true;
    }

    public boolean isReadyForSuccessor() {
        if (this.useDurationTicks < this.loopDurationTicks) {
            int fadeTicks = Math.min(LOOP_RESTART_FADE_TICKS, this.useDurationTicks / 2);
            if (this.ticksAlive >= this.useDurationTicks - fadeTicks) {
                return true;
            }
        }
        return this.isStopped();
    }

    @Override
    public void tick() {
        if (!this.car.isAlive() || (!this.ambient && this.car.getControllingPassenger() == null)) {
            this.stop();
            return;
        }

        if (this.useDurationTicks < this.loopDurationTicks && this.ticksAlive >= this.useDurationTicks) {
            this.stop();
            return;
        }
        this.x = this.car.getX();
        this.y = this.car.getY();
        this.z = this.car.getZ();
        double speed = this.car.getDeltaMovement().horizontalDistance();
        float topSpeed = this.car.getTopForwardSpeedBlocksPerTick();
        float speedFraction = topSpeed > 0f ? Mth.clamp((float) speed / topSpeed, 0f, 1f) : 0f;

        speedFraction = Math.max(speedFraction, this.car.getHandbrakeRevIntensity());

        float targetIdleFactor = Mth.lerp(Mth.clamp(speedFraction / IDLE_VOLUME_RAMP_SPEED_FRACTION, 0f, 1f),
                IDLE_VOLUME_FACTOR, 1.0f);

        if (this.ticksAlive == 0 || targetIdleFactor > this.idleVolumeFactor) {
            this.idleVolumeFactor = targetIdleFactor;
        } else {
            this.idleVolumeFactor = Math.max(targetIdleFactor, this.idleVolumeFactor - IDLE_VOLUME_FALL_PER_TICK);
        }
        float idleVolumeFactor = this.idleVolumeFactor;
        float volumeFade = this.useDurationTicks < this.loopDurationTicks ? this.loopRestartFadeFactor() : 1.0f;
        this.volume = this.baseVolume * volumeFade * idleVolumeFactor * this.hybridLayerFactor(speedFraction);
        float topSpeedPitch = this.car.getEngineTopSpeedPitch();
        float idlePitch = topSpeedPitch * IDLE_PITCH_RATIO;
        float basePitch = Mth.lerp(speedFraction, idlePitch, topSpeedPitch);
        float modulatedPitch = basePitch - SEAM_DIP_AMOUNT * this.seamDipFactor();
        this.pitch = Mth.lerp(this.car.getEnginePitchIntensity(), 1.0f, modulatedPitch);
        this.ticksAlive++;
    }

    private float loopRestartFadeFactor() {
        int fadeTicks = Math.min(LOOP_RESTART_FADE_TICKS, this.useDurationTicks / 2);
        if (fadeTicks <= 0) {
            return 1f;
        }
        float fadeIn = Mth.clamp((float) this.ticksAlive / fadeTicks, 0f, 1f);
        float ticksUntilStop = (this.useDurationTicks - 1) - this.ticksAlive;
        float fadeOut = Mth.clamp(ticksUntilStop / fadeTicks, 0f, 1f);
        float fraction = Math.min(fadeIn, fadeOut);

        return (float) Math.sin(fraction * (Math.PI / 2.0));
    }

    private float seamDipFactor() {

        if (EngineSounds.isSeamless(this.soundId) && this.useDurationTicks >= this.loopDurationTicks) {
            return 0f;
        }

        int period = Math.min(this.useDurationTicks, this.loopDurationTicks);
        int posInLoop = this.ticksAlive % period;
        int distanceToSeam = Math.min(posInLoop, period - posInLoop);
        if (distanceToSeam > SEAM_DIP_WINDOW_TICKS) {
            return 0f;
        }
        return 1f - (float) distanceToSeam / SEAM_DIP_WINDOW_TICKS;
    }
}
