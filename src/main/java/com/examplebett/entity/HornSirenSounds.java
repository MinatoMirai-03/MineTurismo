package com.examplebett.entity;
import com.examplebett.compat.ModText;

import com.examplebett.sound.AiObjectModSounds;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;

import java.util.List;

public final class HornSirenSounds {

    public record HornOption(String id, SoundEvent sound, int broadcastIntervalTicks, Component displayName) {
    }

    public record SirenOption(String id, SoundEvent sound, int crossfadeLoopTicks, Component displayName) {
    }

    public static final String NONE_ID = "none";
    public static final String DEFAULT_HORN_ID = "horn";
    public static final String DEFAULT_SIREN_ID = "police_siren";

    public static final List<HornOption> HORNS = List.of(
            new HornOption("horn", AiObjectModSounds.HORN, 19, ModText.translatable("gui.mineturismo.horn_siren.horn_standard")),
            new HornOption("horn_dual", AiObjectModSounds.HORN_DUAL, 19, ModText.translatable("gui.mineturismo.horn_siren.horn_dual")),
            new HornOption("horn_high", AiObjectModSounds.HORN_HIGH, 19, ModText.translatable("gui.mineturismo.horn_siren.horn_high")),
            new HornOption("horn_air", AiObjectModSounds.HORN_AIR, 19, ModText.translatable("gui.mineturismo.horn_siren.horn_air")),

            new HornOption(NONE_ID, null, 0, ModText.translatable("gui.mineturismo.horn_siren.none"))
    );

    public static final List<SirenOption> SIRENS = List.of(

            new SirenOption("police_siren", AiObjectModSounds.POLICE_SIREN, 76, ModText.translatable("gui.mineturismo.horn_siren.siren_standard")),
            new SirenOption("siren_jp_police", AiObjectModSounds.SIREN_JP_POLICE, 0, ModText.translatable("gui.mineturismo.horn_siren.siren_jp_police")),
            new SirenOption("siren_jp_ambulance", AiObjectModSounds.SIREN_JP_AMBULANCE, 0, ModText.translatable("gui.mineturismo.horn_siren.siren_jp_ambulance")),
            new SirenOption("siren_eu_two_tone", AiObjectModSounds.SIREN_EU_TWO_TONE, 0, ModText.translatable("gui.mineturismo.horn_siren.siren_eu_two_tone")),

            new SirenOption(NONE_ID, null, 0, ModText.translatable("gui.mineturismo.horn_siren.none"))
    );

    public static HornOption hornById(String id) {
        for (HornOption option : HORNS) {
            if (option.id().equals(id)) {
                return option;
            }
        }
        return HORNS.get(0);
    }

    public static SirenOption sirenById(String id) {
        for (SirenOption option : SIRENS) {
            if (option.id().equals(id)) {
                return option;
            }
        }
        return SIRENS.get(0);
    }

    private HornSirenSounds() {
    }
}
