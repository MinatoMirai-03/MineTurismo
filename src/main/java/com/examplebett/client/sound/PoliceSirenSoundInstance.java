//? if >=1.18 {
//?}
//? if <1.18 && >=1.16 {
/*
package com.examplebett.client.sound;

import com.examplebett.entity.CarEntity;
import com.examplebett.entity.HornSirenSounds;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;

public class PoliceSirenSoundInstance extends AbstractTickableSoundInstance {

    private static final int FADE_TICKS = 20;
    private static final float RELEASE_VOLUME_STEP = 1.0f / FADE_TICKS;

    private final CarEntity car;
    private final String sirenSoundId;

    private final int loopDurationTicks;
    private int ticksAlive;
    private boolean releasing;
    private float releaseVolume = 1.0f;

    public PoliceSirenSoundInstance(CarEntity car) {
        this(car, HornSirenSounds.sirenById(car.getSirenSoundId()));
    }

    private PoliceSirenSoundInstance(CarEntity car, HornSirenSounds.SirenOption siren) {

        super(siren.sound(), SoundSource.NEUTRAL);
        this.car = car;
        this.sirenSoundId = siren.id();
        this.loopDurationTicks = siren.crossfadeLoopTicks();
        this.looping = true;
        this.delay = 0;

        this.volume = 0.0f;
        this.pitch = 1.0f;
        this.x = car.getX();
        this.y = soundY(car);
        this.z = car.getZ();

        this.attenuation = SoundInstance.Attenuation.LINEAR;
    }

    private static double soundY(CarEntity car) {
        return car.getY() + (com.examplebett.client.GttClient.isActive() ? com.examplebett.client.GttClient.CAMERA_HEIGHT : 0.0);
    }

    public boolean isFor(CarEntity car) {
        return this.car == car;
    }

    public boolean isSoundOutdated() {
        return !this.sirenSoundId.equals(this.car.getSirenSoundId());
    }

    @Override
    public boolean canStartSilent() {
        return true;
    }

    public void requestRelease() {
        this.releasing = true;
    }

    public boolean isReadyForSuccessor() {
        if (!this.releasing && this.loopDurationTicks > 0) {
            int fadeTicks = Math.min(FADE_TICKS, this.loopDurationTicks / 2);
            if (this.ticksAlive >= this.loopDurationTicks - fadeTicks) {
                return true;
            }
        }
        return this.isStopped();
    }

    @Override
    public void tick() {
        if (!this.car.isAlive()) {
            this.stop();
            return;
        }
        if (!this.releasing && this.loopDurationTicks > 0 && this.ticksAlive >= this.loopDurationTicks) {
            this.stop();
            return;
        }
        this.x = this.car.getX();
        this.y = soundY(this.car);
        this.z = this.car.getZ();
        float loopFactor = this.loopRestartFadeFactor();
        if (this.releasing) {
            this.releaseVolume = Math.max(0.0f, this.releaseVolume - RELEASE_VOLUME_STEP);
            this.volume = loopFactor * this.releaseVolume;
            if (this.releaseVolume <= 0.0f) {
                this.stop();
                return;
            }
        } else {
            this.volume = loopFactor;
        }
        this.ticksAlive++;
    }

    private float loopRestartFadeFactor() {
        if (this.loopDurationTicks <= 0) {
            float fadeIn = Mth.clamp((float) this.ticksAlive / FADE_TICKS, 0f, 1f);
            return (float) Math.sin(fadeIn * (Math.PI / 2.0));
        }
        int fadeTicks = Math.min(FADE_TICKS, this.loopDurationTicks / 2);
        if (fadeTicks <= 0) {
            return 1f;
        }
        float fadeIn = Mth.clamp((float) this.ticksAlive / fadeTicks, 0f, 1f);
        float ticksUntilStop = (this.loopDurationTicks - 1) - this.ticksAlive;
        float fadeOut = Mth.clamp(ticksUntilStop / fadeTicks, 0f, 1f);
        float fraction = Math.min(fadeIn, fadeOut);
        return (float) Math.sin(fraction * (Math.PI / 2.0));
    }
}
*/
//?}
//? if <1.16 && >=1.15 {
/*
package com.examplebett.client.sound;

import com.examplebett.entity.CarEntity;
import com.examplebett.entity.HornSirenSounds;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;

public class PoliceSirenSoundInstance extends AbstractTickableSoundInstance {

    private static final int FADE_TICKS = 20;
    private static final float RELEASE_VOLUME_STEP = 1.0f / FADE_TICKS;

    private final CarEntity car;
    private final String sirenSoundId;

    private final int loopDurationTicks;
    private int ticksAlive;
    private boolean releasing;
    private float releaseVolume = 1.0f;

    public PoliceSirenSoundInstance(CarEntity car) {
        this(car, HornSirenSounds.sirenById(car.getSirenSoundId()));
    }

    private PoliceSirenSoundInstance(CarEntity car, HornSirenSounds.SirenOption siren) {

        super(siren.sound(), SoundSource.NEUTRAL);
        this.car = car;
        this.sirenSoundId = siren.id();
        this.loopDurationTicks = siren.crossfadeLoopTicks();
        this.looping = true;
        this.delay = 0;

        this.volume = 0.0f;
        this.pitch = 1.0f;
        this.x = (float) car.getX();
        this.y = (float) soundY(car);
        this.z = (float) car.getZ();

        this.attenuation = SoundInstance.Attenuation.LINEAR;
    }

    private static double soundY(CarEntity car) {
        return car.getY() + (com.examplebett.client.GttClient.isActive() ? com.examplebett.client.GttClient.CAMERA_HEIGHT : 0.0);
    }

    public boolean isFor(CarEntity car) {
        return this.car == car;
    }

    public boolean isSoundOutdated() {
        return !this.sirenSoundId.equals(this.car.getSirenSoundId());
    }

    @Override
    public boolean canStartSilent() {
        return true;
    }

    public void requestRelease() {
        this.releasing = true;
    }

    public boolean isReadyForSuccessor() {
        if (!this.releasing && this.loopDurationTicks > 0) {
            int fadeTicks = Math.min(FADE_TICKS, this.loopDurationTicks / 2);
            if (this.ticksAlive >= this.loopDurationTicks - fadeTicks) {
                return true;
            }
        }
        return this.isStopped();
    }

    @Override
    public void tick() {
        if (!this.car.isAlive()) {
            this.stop();
            return;
        }
        if (!this.releasing && this.loopDurationTicks > 0 && this.ticksAlive >= this.loopDurationTicks) {
            this.stop();
            return;
        }
        this.x = (float) this.car.getX();
        this.y = (float) soundY(this.car);
        this.z = (float) this.car.getZ();
        float loopFactor = this.loopRestartFadeFactor();
        if (this.releasing) {
            this.releaseVolume = Math.max(0.0f, this.releaseVolume - RELEASE_VOLUME_STEP);
            this.volume = loopFactor * this.releaseVolume;
            if (this.releaseVolume <= 0.0f) {
                this.stop();
                return;
            }
        } else {
            this.volume = loopFactor;
        }
        this.ticksAlive++;
    }

    private float loopRestartFadeFactor() {
        if (this.loopDurationTicks <= 0) {
            float fadeIn = Mth.clamp((float) this.ticksAlive / FADE_TICKS, 0f, 1f);
            return (float) Math.sin(fadeIn * (Math.PI / 2.0));
        }
        int fadeTicks = Math.min(FADE_TICKS, this.loopDurationTicks / 2);
        if (fadeTicks <= 0) {
            return 1f;
        }
        float fadeIn = Mth.clamp((float) this.ticksAlive / fadeTicks, 0f, 1f);
        float ticksUntilStop = (this.loopDurationTicks - 1) - this.ticksAlive;
        float fadeOut = Mth.clamp(ticksUntilStop / fadeTicks, 0f, 1f);
        float fraction = Math.min(fadeIn, fadeOut);
        return (float) Math.sin(fraction * (Math.PI / 2.0));
    }
}
*/
//?}
//? if <1.15 {
/*
package com.examplebett.client.sound;

import com.examplebett.entity.CarEntity;
import com.examplebett.entity.HornSirenSounds;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;

public class PoliceSirenSoundInstance extends AbstractTickableSoundInstance {

    private static final int FADE_TICKS = 20;
    private static final float RELEASE_VOLUME_STEP = 1.0f / FADE_TICKS;

    private final CarEntity car;
    private final String sirenSoundId;

    private final int loopDurationTicks;
    private int ticksAlive;
    private boolean releasing;
    private float releaseVolume = 1.0f;

    public PoliceSirenSoundInstance(CarEntity car) {
        this(car, HornSirenSounds.sirenById(car.getSirenSoundId()));
    }

    private PoliceSirenSoundInstance(CarEntity car, HornSirenSounds.SirenOption siren) {

        super(siren.sound(), SoundSource.NEUTRAL);
        this.car = car;
        this.sirenSoundId = siren.id();
        this.loopDurationTicks = siren.crossfadeLoopTicks();
        this.looping = true;
        this.delay = 0;

        this.volume = 0.0f;
        this.pitch = 1.0f;
        this.x = (float) com.examplebett.compat.ModEntity.getX(car);
        this.y = (float) soundY(car);
        this.z = (float) com.examplebett.compat.ModEntity.getZ(car);

        this.attenuation = SoundInstance.Attenuation.LINEAR;
    }

    private static double soundY(CarEntity car) {
        return com.examplebett.compat.ModEntity.getY(car) + (com.examplebett.client.GttClient.isActive() ? com.examplebett.client.GttClient.CAMERA_HEIGHT : 0.0);
    }

    public boolean isFor(CarEntity car) {
        return this.car == car;
    }

    public boolean isSoundOutdated() {
        return !this.sirenSoundId.equals(this.car.getSirenSoundId());
    }

    @Override
    public boolean canStartSilent() {
        return true;
    }

    public void requestRelease() {
        this.releasing = true;
    }

    public boolean isReadyForSuccessor() {
        if (!this.releasing && this.loopDurationTicks > 0) {
            int fadeTicks = Math.min(FADE_TICKS, this.loopDurationTicks / 2);
            if (this.ticksAlive >= this.loopDurationTicks - fadeTicks) {
                return true;
            }
        }
        return this.isStopped();
    }

    @Override
    public void tick() {
        if (!this.car.isAlive()) {
            this.stop();
            return;
        }
        if (!this.releasing && this.loopDurationTicks > 0 && this.ticksAlive >= this.loopDurationTicks) {
            this.stop();
            return;
        }
        this.x = (float) com.examplebett.compat.ModEntity.getX(this.car);
        this.y = (float) soundY(this.car);
        this.z = (float) com.examplebett.compat.ModEntity.getZ(this.car);
        float loopFactor = this.loopRestartFadeFactor();
        if (this.releasing) {
            this.releaseVolume = Math.max(0.0f, this.releaseVolume - RELEASE_VOLUME_STEP);
            this.volume = loopFactor * this.releaseVolume;
            if (this.releaseVolume <= 0.0f) {
                this.stop();
                return;
            }
        } else {
            this.volume = loopFactor;
        }
        this.ticksAlive++;
    }

    private float loopRestartFadeFactor() {
        if (this.loopDurationTicks <= 0) {
            float fadeIn = Mth.clamp((float) this.ticksAlive / FADE_TICKS, 0f, 1f);
            return (float) Math.sin(fadeIn * (Math.PI / 2.0));
        }
        int fadeTicks = Math.min(FADE_TICKS, this.loopDurationTicks / 2);
        if (fadeTicks <= 0) {
            return 1f;
        }
        float fadeIn = Mth.clamp((float) this.ticksAlive / fadeTicks, 0f, 1f);
        float ticksUntilStop = (this.loopDurationTicks - 1) - this.ticksAlive;
        float fadeOut = Mth.clamp(ticksUntilStop / fadeTicks, 0f, 1f);
        float fraction = Math.min(fadeIn, fadeOut);
        return (float) Math.sin(fraction * (Math.PI / 2.0));
    }
}
*/
//?}
