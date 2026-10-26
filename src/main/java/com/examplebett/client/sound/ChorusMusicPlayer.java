//? if >=1.18 {
//?}
//? if <1.18 {
/*
package com.examplebett.client.sound;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;

public final class ChorusMusicPlayer {

    private static ChorusMusicTracks.Track current;
    private static SoundInstance instance;
    private static boolean loop;

    private ChorusMusicPlayer() {
    }

    public static ChorusMusicTracks.Track current() {
        return current;
    }

    public static boolean isLoop() {
        return loop;
    }

    public static void toggleLoop() {
        loop = !loop;
    }

    public static boolean isOverridingVanilla() {
        return current != null;
    }

    public static void play(ChorusMusicTracks.Track track) {
        Minecraft mc = Minecraft.getInstance();

        mc.getMusicManager().stopPlaying();
        if (instance != null) {
            mc.getSoundManager().stop(instance);
        }
        current = track;

        instance = SimpleSoundInstance.forMusic(new SoundEvent(track.soundId()));
        mc.getSoundManager().play(instance);
    }

    public static void stop() {
        Minecraft mc = Minecraft.getInstance();
        if (instance != null) {
            mc.getSoundManager().stop(instance);
        }
        instance = null;
        current = null;
        mc.getMusicManager().stopPlaying();
    }

    public static void tick(Minecraft mc) {
        if (current == null || instance == null) {
            return;
        }
        if (mc.level == null) {

            mc.getSoundManager().stop(instance);
            instance = null;
            current = null;
            return;
        }
        if (!mc.getSoundManager().isActive(instance)) {
            if (loop) {
                play(current);
            } else {
                instance = null;
                current = null;
            }
        }
    }
}
*/
//?}
