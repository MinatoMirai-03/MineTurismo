//? if >=1.18 {
//?} else {
/*
package com.examplebett.client.mixin;

import com.examplebett.client.sound.EngineSoundInstance;
import com.examplebett.compat.ModEntity;
import com.examplebett.entity.CarEntity;
import com.examplebett.entity.EngineSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Set;

@Mixin(value = SoundManager.class, priority = 2000)
public class EngineBroadcastMuteMixin {

    private static final double OWN_CAR_POSITION_EPSILON = 4.0;

    private static final Set<ResourceLocation> ENGINE_SOUND_LOCATIONS = buildEngineSoundLocations();

    private static Set<ResourceLocation> buildEngineSoundLocations() {
        Set<ResourceLocation> locations = new HashSet<>();
        for (EngineSounds.Option option : EngineSounds.OPTIONS) {
            locations.add(option.sound().getLocation());
        }
        return locations;
    }

    @Inject(method = "play", at = @At("HEAD"), cancellable = true)
    private void mineturismo$handleEngineBroadcast(SoundInstance sound, CallbackInfo ci) {
        if (shouldMute(sound)) {
            ci.cancel();
        }
    }

    private boolean shouldMute(SoundInstance sound) {
        if (sound instanceof EngineSoundInstance) {
            return false;
        }
        if (!ENGINE_SOUND_LOCATIONS.contains(sound.getLocation())) {
            return false;
        }
        Minecraft client = Minecraft.getInstance();
        Entity vehicle = client.player != null ? client.player.getVehicle() : null;
        ResourceLocation drivingCarSoundLocation = vehicle instanceof CarEntity drivingCarForSound
                ? EngineSounds.byId(drivingCarForSound.getEngineSoundId()).sound().getLocation() : null;
        boolean isSameEngineSound = drivingCarSoundLocation != null && drivingCarSoundLocation.equals(sound.getLocation());
        if (vehicle instanceof CarEntity drivingCar
                && isSameEngineSound
                && isNear(sound, drivingCar)) {
            return true;
        }
        if (sound instanceof AbstractSoundInstanceAccessor accessor) {
            accessor.mineturismo$setAttenuation(SoundInstance.Attenuation.NONE);
        }
        return false;
    }

    private static boolean isNear(SoundInstance sound, CarEntity car) {
        double dx = sound.getX() - ModEntity.getX(car);
        double dy = sound.getY() - ModEntity.getY(car);
        double dz = sound.getZ() - ModEntity.getZ(car);
        return dx * dx + dy * dy + dz * dz <= OWN_CAR_POSITION_EPSILON * OWN_CAR_POSITION_EPSILON;
    }
}
*/
//?}
