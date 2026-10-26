//? if >=1.18 {
//?}
//? if <1.18 && >=1.16 {
/*
package com.examplebett.client.sound;

import com.examplebett.entity.CarEntity;
import com.examplebett.sound.AiObjectModSounds;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;

public class TireSlipSoundInstance extends AbstractTickableSoundInstance {

    private static final double SUSTAIN_DECAY = 0.94;
    private static final float SUSTAIN_STOP_THRESHOLD = 0.02f;

    private final CarEntity car;
    private float sustainedVolume;

    public TireSlipSoundInstance(CarEntity car) {

        super(AiObjectModSounds.TIRE_SLIP, SoundSource.NEUTRAL);
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

    @Override
    public void tick() {
        if (!this.car.isAlive()) {
            this.stop();
            return;
        }
        this.x = this.car.getX();
        this.y = this.car.getY();
        this.z = this.car.getZ();

        this.sustainedVolume = Math.max(this.car.getClientSlideIntensity(), this.sustainedVolume * (float) SUSTAIN_DECAY);
        if (this.sustainedVolume < SUSTAIN_STOP_THRESHOLD) {
            this.volume = 0.0f;
            this.stop();
            return;
        }
        this.volume = this.sustainedVolume;
    }
}
*/
//?}
//? if <1.16 && >=1.15 {
/*
package com.examplebett.client.sound;

import com.examplebett.entity.CarEntity;
import com.examplebett.sound.AiObjectModSounds;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;

public class TireSlipSoundInstance extends AbstractTickableSoundInstance {

    private static final double SUSTAIN_DECAY = 0.94;
    private static final float SUSTAIN_STOP_THRESHOLD = 0.02f;

    private final CarEntity car;
    private float sustainedVolume;

    public TireSlipSoundInstance(CarEntity car) {

        super(AiObjectModSounds.TIRE_SLIP, SoundSource.NEUTRAL);
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

    @Override
    public void tick() {
        if (!this.car.isAlive()) {
            this.stop();
            return;
        }
        this.x = (float) this.car.getX();
        this.y = (float) this.car.getY();
        this.z = (float) this.car.getZ();

        this.sustainedVolume = Math.max(this.car.getClientSlideIntensity(), this.sustainedVolume * (float) SUSTAIN_DECAY);
        if (this.sustainedVolume < SUSTAIN_STOP_THRESHOLD) {
            this.volume = 0.0f;
            this.stop();
            return;
        }
        this.volume = this.sustainedVolume;
    }
}
*/
//?}
//? if <1.15 {
/*
package com.examplebett.client.sound;

import com.examplebett.entity.CarEntity;
import com.examplebett.sound.AiObjectModSounds;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;

public class TireSlipSoundInstance extends AbstractTickableSoundInstance {

    private static final double SUSTAIN_DECAY = 0.94;
    private static final float SUSTAIN_STOP_THRESHOLD = 0.02f;

    private final CarEntity car;
    private float sustainedVolume;

    public TireSlipSoundInstance(CarEntity car) {

        super(AiObjectModSounds.TIRE_SLIP, SoundSource.NEUTRAL);
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

    @Override
    public void tick() {
        if (!this.car.isAlive()) {
            this.stop();
            return;
        }
        this.x = (float) com.examplebett.compat.ModEntity.getX(this.car);
        this.y = (float) com.examplebett.compat.ModEntity.getY(this.car);
        this.z = (float) com.examplebett.compat.ModEntity.getZ(this.car);

        this.sustainedVolume = Math.max(this.car.getClientSlideIntensity(), this.sustainedVolume * (float) SUSTAIN_DECAY);
        if (this.sustainedVolume < SUSTAIN_STOP_THRESHOLD) {
            this.volume = 0.0f;
            this.stop();
            return;
        }
        this.volume = this.sustainedVolume;
    }
}
*/
//?}
