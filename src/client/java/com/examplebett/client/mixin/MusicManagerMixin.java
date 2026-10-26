package com.examplebett.client.mixin;

import com.examplebett.client.sound.ChorusMusicPlayer;
import net.minecraft.client.sounds.MusicManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MusicManager.class)
public abstract class MusicManagerMixin {

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void mineturismo$pauseForChorusMusic(CallbackInfo ci) {
        if (ChorusMusicPlayer.isOverridingVanilla()) {
            ci.cancel();
        }
    }
}
