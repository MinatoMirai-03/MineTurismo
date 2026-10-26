package com.examplebett.sound;

import com.examplebett.AiObjectMod;
import com.examplebett.compat.ModRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class AiObjectModSounds {

    public static final SoundEvent ENGINE_LOOP = registerSoundEvent("engine_loop");
    public static final SoundEvent ENGINE_LOOP_2 = registerSoundEvent("engine_loop_2");
    public static final SoundEvent ENGINE_LOOP_3 = registerSoundEvent("engine_loop_3");
    public static final SoundEvent ENGINE_LOOP_4 = registerSoundEvent("engine_loop_4");
    public static final SoundEvent ENGINE_LOOP_5 = registerSoundEvent("engine_loop_5");
    public static final SoundEvent ENGINE_LOOP_6 = registerSoundEvent("engine_loop_6");
    public static final SoundEvent ENGINE_LOOP_7 = registerSoundEvent("engine_loop_7");
    public static final SoundEvent ENGINE_LOOP_8 = registerSoundEvent("engine_loop_8");
    public static final SoundEvent ENGINE_LOOP_9 = registerSoundEvent("engine_loop_9");
    public static final SoundEvent ENGINE_EV_MOTOR = registerSoundEvent("engine_ev_motor");
    public static final SoundEvent ENGINE_QUIET = registerSoundEvent("engine_quiet");
    public static final SoundEvent ENGINE_QUIET_2 = registerSoundEvent("engine_quiet_2");

    public static final SoundEvent ENGINE_EV_MOTOR_4 = registerSoundEvent("engine_ev_motor_4");
    public static final SoundEvent ENGINE_EV_MOTOR_5 = registerSoundEvent("engine_ev_motor_5");
    public static final SoundEvent ENGINE_EV_MOTOR_6 = registerSoundEvent("engine_ev_motor_6");
    public static final SoundEvent ENGINE_EV_MOTOR_7 = registerSoundEvent("engine_ev_motor_7");
    public static final SoundEvent ENGINE_RAILWAY_1 = registerSoundEvent("engine_railway_1");
    public static final SoundEvent ENGINE_RAILWAY_2 = registerSoundEvent("engine_railway_2");
    public static final SoundEvent ENGINE_HELICOPTER_1 = registerSoundEvent("engine_helicopter_1");
    public static final SoundEvent ENGINE_BIKE_1 = registerSoundEvent("engine_bike_1");
    public static final SoundEvent ENGINE_BIKE_2 = registerSoundEvent("engine_bike_2");
    public static final SoundEvent ENGINE_TRUCK_1 = registerSoundEvent("engine_truck_1");
    public static final SoundEvent ENGINE_TRUCK_2 = registerSoundEvent("engine_truck_2");
    public static final SoundEvent ENGINE_TRUCK_3 = registerSoundEvent("engine_truck_3");
    public static final SoundEvent ENGINE_TRUCK_4 = registerSoundEvent("engine_truck_4");
    public static final SoundEvent DOOR_LOCK_1 = registerSoundEvent("door_lock_1");
    public static final SoundEvent DOOR_LOCK_2 = registerSoundEvent("door_lock_2");
    public static final SoundEvent DOOR = registerSoundEvent("door");
    public static final SoundEvent HORN = registerSoundEvent("horn");
    public static final SoundEvent POLICE_SIREN = registerSoundEvent("police_siren");

    public static final SoundEvent HORN_DUAL = registerSoundEvent("horn_dual");
    public static final SoundEvent HORN_HIGH = registerSoundEvent("horn_high");
    public static final SoundEvent HORN_AIR = registerSoundEvent("horn_air");
    public static final SoundEvent SIREN_JP_POLICE = registerSoundEvent("siren_jp_police");
    public static final SoundEvent SIREN_JP_AMBULANCE = registerSoundEvent("siren_jp_ambulance");
    public static final SoundEvent SIREN_EU_TWO_TONE = registerSoundEvent("siren_eu_two_tone");

    public static final SoundEvent BOMB_BEEP = registerSoundEvent("bomb_beep");

    public static final SoundEvent ALARM_CHIRP = registerSoundEvent("alarm_chirp");
    public static final SoundEvent ALARM_SWEEP = registerSoundEvent("alarm_sweep");
    public static final SoundEvent CAR_COLLISION = registerSoundEvent("car_collision");

    public static final SoundEvent CAR_COLLISION_LOW = registerSoundEvent("car_collision_low");
    public static final SoundEvent TIRE_SLIP = registerSoundEvent("tire_slip");
    public static final SoundEvent BOX_BREAK = registerSoundEvent("box_break");

    public static final SoundEvent BARRICADE_BREAK = registerSoundEvent("barricade_break");
    public static final SoundEvent PASSENGER_TRANSPORT_DENIED = registerSoundEvent("passenger_transport_denied");

    public static final SoundEvent SLOT_START = registerSoundEvent("slot.start");
    public static final SoundEvent SLOT_TICK = registerSoundEvent("slot.tick");
    public static final SoundEvent SLOT_STOP = registerSoundEvent("slot.stop");
    public static final SoundEvent SLOT_LOSE = registerSoundEvent("slot.lose");
    public static final SoundEvent SLOT_WIN = registerSoundEvent("slot.win");
    public static final SoundEvent SLOT_JACKPOT = registerSoundEvent("slot.jackpot");

    private static final float SOUND_RANGE_BLOCKS = 64.0f;

    private static SoundEvent registerSoundEvent(String path) {
        ResourceLocation id = AiObjectMod.id(path);
        //? if >=1.19 {
        //? if >=1.19.3 {
        SoundEvent event = SoundEvent.createFixedRangeEvent(id, SOUND_RANGE_BLOCKS);
        //?} else {
        /*SoundEvent event = new SoundEvent(id, SOUND_RANGE_BLOCKS);*/
        //?}
        //?} else {
        /*SoundEvent event = new SoundEvent(id);*/
        //?}
        return Registry.register(ModRegistries.SOUND_EVENT, id, event);
    }

    public static void register() {

    }
}
