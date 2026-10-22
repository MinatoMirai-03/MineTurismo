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

public class HornSoundInstance extends AbstractTickableSoundInstance {
    private static final int VOLUME_FADE_TICKS = 4;
    private static final float VOLUME_STEP = 1.0f / VOLUME_FADE_TICKS;

    private final CarEntity car;
    private boolean releasing;

    public HornSoundInstance(CarEntity car) {

        super(HornSirenSounds.hornById(car.getHornSoundId()).sound(), SoundSource.NEUTRAL);
        this.car = car;
        this.looping = true;
        this.delay = 0;
        this.volume = 0.0f;
        this.pitch = 1.0f;
        this.x = car.getX();
        this.y = car.getY();
        this.z = car.getZ();

        this.attenuation = SoundInstance.Attenuation.NONE;
    }

    public boolean isFor(CarEntity car) {
        return this.car == car;
    }

    @Override
    public boolean canStartSilent() {
        return true;
    }

    public void requestRelease() {
        this.releasing = true;
    }

    public void requestPlaying() {
        this.releasing = false;
    }

    @Override
    public void tick() {
        if (!this.car.isAlive()) {
            this.stop();
            return;
        }
        this.x = this.car.getX();
        this.y = this.car.getY();
        this.z = this.car.getZ();
        if (this.releasing) {
            this.volume = Math.max(0.0f, this.volume - VOLUME_STEP);
            if (this.volume <= 0.0f) {
                this.stop();
                return;
            }
        } else {
            this.volume = Math.min(1.0f, this.volume + VOLUME_STEP);
        }
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

public class HornSoundInstance extends AbstractTickableSoundInstance {
    private static final int VOLUME_FADE_TICKS = 4;
    private static final float VOLUME_STEP = 1.0f / VOLUME_FADE_TICKS;

    private final CarEntity car;
    private boolean releasing;

    public HornSoundInstance(CarEntity car) {

        super(HornSirenSounds.hornById(car.getHornSoundId()).sound(), SoundSource.NEUTRAL);
        this.car = car;
        this.looping = true;
        this.delay = 0;
        this.volume = 0.0f;
        this.pitch = 1.0f;
        this.x = (float) car.getX();
        this.y = (float) car.getY();
        this.z = (float) car.getZ();

        this.attenuation = SoundInstance.Attenuation.NONE;
    }

    public boolean isFor(CarEntity car) {
        return this.car == car;
    }

    @Override
    public boolean canStartSilent() {
        return true;
    }

    public void requestRelease() {
        this.releasing = true;
    }

    public void requestPlaying() {
        this.releasing = false;
    }

    @Override
    public void tick() {
        if (!this.car.isAlive()) {
            this.stop();
            return;
        }
        this.x = (float) this.car.getX();
        this.y = (float) this.car.getY();
        this.z = (float) this.car.getZ();
        if (this.releasing) {
            this.volume = Math.max(0.0f, this.volume - VOLUME_STEP);
            if (this.volume <= 0.0f) {
                this.stop();
                return;
            }
        } else {
            this.volume = Math.min(1.0f, this.volume + VOLUME_STEP);
        }
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

public class HornSoundInstance extends AbstractTickableSoundInstance {
    private static final int VOLUME_FADE_TICKS = 4;
    private static final float VOLUME_STEP = 1.0f / VOLUME_FADE_TICKS;

    private final CarEntity car;
    private boolean releasing;

    public HornSoundInstance(CarEntity car) {

        super(HornSirenSounds.hornById(car.getHornSoundId()).sound(), SoundSource.NEUTRAL);
        this.car = car;
        this.looping = true;
        this.delay = 0;
        this.volume = 0.0f;
        this.pitch = 1.0f;
        this.x = (float) com.examplebett.compat.ModEntity.getX(car);
        this.y = (float) com.examplebett.compat.ModEntity.getY(car);
        this.z = (float) com.examplebett.compat.ModEntity.getZ(car);

        this.attenuation = SoundInstance.Attenuation.NONE;
    }

    public boolean isFor(CarEntity car) {
        return this.car == car;
    }

    @Override
    public boolean canStartSilent() {
        return true;
    }

    public void requestRelease() {
        this.releasing = true;
    }

    public void requestPlaying() {
        this.releasing = false;
    }

    @Override
    public void tick() {
        if (!this.car.isAlive()) {
            this.stop();
            return;
        }
        this.x = (float) com.examplebett.compat.ModEntity.getX(this.car);
        this.y = (float) com.examplebett.compat.ModEntity.getY(this.car);
        this.z = (float) com.examplebett.compat.ModEntity.getZ(this.car);
        if (this.releasing) {
            this.volume = Math.max(0.0f, this.volume - VOLUME_STEP);
            if (this.volume <= 0.0f) {
                this.stop();
                return;
            }
        } else {
            this.volume = Math.min(1.0f, this.volume + VOLUME_STEP);
        }
    }
}
*/
//?}
