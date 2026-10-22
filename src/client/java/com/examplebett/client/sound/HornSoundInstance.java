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

        //? if >=1.19 {
        super(HornSirenSounds.hornById(car.getHornSoundId()).sound(), SoundSource.NEUTRAL, SoundInstance.createUnseededRandom());
        //?} else
        /*super(HornSirenSounds.hornById(car.getHornSoundId()).sound(), SoundSource.NEUTRAL);*/
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
