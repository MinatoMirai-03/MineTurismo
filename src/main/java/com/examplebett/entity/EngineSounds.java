package com.examplebett.entity;
import com.examplebett.compat.ModText;

import com.examplebett.sound.AiObjectModSounds;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;

import java.util.List;

public final class EngineSounds {

    public record Option(String id, SoundEvent sound, int loopDurationTicks, Component displayName) {
    }

    public static final List<Option> OPTIONS = List.of(
            new Option("engine_loop", AiObjectModSounds.ENGINE_LOOP, 160, ModText.translatable("gui.mineturismo.engine.sound_1")),
            new Option("engine_loop_2", AiObjectModSounds.ENGINE_LOOP_2, 160, ModText.translatable("gui.mineturismo.engine.sound_2")),
            new Option("engine_loop_3", AiObjectModSounds.ENGINE_LOOP_3, 160, ModText.translatable("gui.mineturismo.engine.sound_3")),

            new Option("engine_loop_4", AiObjectModSounds.ENGINE_LOOP_4, 128, ModText.translatable("gui.mineturismo.engine.sound_4")),
            new Option("engine_loop_5", AiObjectModSounds.ENGINE_LOOP_5, 51, ModText.translatable("gui.mineturismo.engine.sound_5")),
            new Option("engine_loop_6", AiObjectModSounds.ENGINE_LOOP_6, 59, ModText.translatable("gui.mineturismo.engine.sound_6")),
            new Option("engine_loop_7", AiObjectModSounds.ENGINE_LOOP_7, 101, ModText.translatable("gui.mineturismo.engine.sound_7")),
            new Option("engine_loop_8", AiObjectModSounds.ENGINE_LOOP_8, 160, ModText.translatable("gui.mineturismo.engine.sound_8")),
            new Option("engine_loop_9", AiObjectModSounds.ENGINE_LOOP_9, 109, ModText.translatable("gui.mineturismo.engine.sound_9")),
            new Option("engine_ev_motor", AiObjectModSounds.ENGINE_EV_MOTOR, 160, ModText.translatable("gui.mineturismo.engine.sound_ev_motor")),
            new Option("engine_quiet", AiObjectModSounds.ENGINE_QUIET, 160, ModText.translatable("gui.mineturismo.engine.sound_quiet")),
            new Option("engine_quiet_2", AiObjectModSounds.ENGINE_QUIET_2, 160, ModText.translatable("gui.mineturismo.engine.sound_quiet_2")),

            new Option("engine_ev_motor_4", AiObjectModSounds.ENGINE_EV_MOTOR_4, 160, ModText.translatable("gui.mineturismo.engine.sound_ev_motor_4")),
            new Option("engine_ev_motor_5", AiObjectModSounds.ENGINE_EV_MOTOR_5, 160, ModText.translatable("gui.mineturismo.engine.sound_ev_motor_5")),
            new Option("engine_ev_motor_6", AiObjectModSounds.ENGINE_EV_MOTOR_6, 160, ModText.translatable("gui.mineturismo.engine.sound_ev_motor_6")),
            new Option("engine_ev_motor_7", AiObjectModSounds.ENGINE_EV_MOTOR_7, 160, ModText.translatable("gui.mineturismo.engine.sound_ev_motor_7")),
            new Option("engine_railway_1", AiObjectModSounds.ENGINE_RAILWAY_1, 160, ModText.translatable("gui.mineturismo.engine.sound_railway_1")),
            new Option("engine_railway_2", AiObjectModSounds.ENGINE_RAILWAY_2, 160, ModText.translatable("gui.mineturismo.engine.sound_railway_2")),
            new Option("engine_helicopter_1", AiObjectModSounds.ENGINE_HELICOPTER_1, 160, ModText.translatable("gui.mineturismo.engine.sound_helicopter_1")),
            new Option("engine_bike_1", AiObjectModSounds.ENGINE_BIKE_1, 160, ModText.translatable("gui.mineturismo.engine.sound_bike_1")),
            new Option("engine_bike_2", AiObjectModSounds.ENGINE_BIKE_2, 160, ModText.translatable("gui.mineturismo.engine.sound_bike_2")),
            new Option("engine_truck_1", AiObjectModSounds.ENGINE_TRUCK_1, 160, ModText.translatable("gui.mineturismo.engine.sound_truck_1")),
            new Option("engine_truck_2", AiObjectModSounds.ENGINE_TRUCK_2, 160, ModText.translatable("gui.mineturismo.engine.sound_truck_2")),
            new Option("engine_truck_3", AiObjectModSounds.ENGINE_TRUCK_3, 160, ModText.translatable("gui.mineturismo.engine.sound_truck_3")),
            new Option("engine_truck_4", AiObjectModSounds.ENGINE_TRUCK_4, 160, ModText.translatable("gui.mineturismo.engine.sound_truck_4"))
    );

    public static final String DEFAULT_ID = "engine_loop";

    private static final java.util.Set<String> SEAMLESS_IDS =
            java.util.Set.of("engine_loop_4", "engine_loop_5", "engine_loop_6", "engine_loop_7");

    public static boolean isSeamless(String id) {
        return SEAMLESS_IDS.contains(id);
    }

    public static Option byId(String id) {
        for (Option option : OPTIONS) {
            if (option.id().equals(id)) {
                return option;
            }
        }
        return OPTIONS.get(0);
    }

    private EngineSounds() {
    }
}
