package com.examplebett.render;
import com.examplebett.compat.ModText;

import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public final class ExhaustParts {

    public record Option(String id, int outlets, String particle, Component displayName) {
    }

    public static final String SINGLE_ID = "single";
    public static final String DUAL_ID = "dual";

    public static final String PARTICLE_FLAME = "flame";

    public static final List<String> EXTRA_PARTICLES = buildExtraParticles();

    private static List<String> buildExtraParticles() {
        List<String> particles = new ArrayList<>();

        particles.add("soul_fire_flame");
        particles.addAll(List.of(
                "lava", "fishing", "dolphin", "smoke", "campfire_cosy_smoke", "falling_dust", "witch", "flash", "explosion"));
        return List.copyOf(particles);
    }

    public static final List<Option> OPTIONS = buildOptions();

    private static List<Option> buildOptions() {
        List<Option> options = new ArrayList<>();
        options.add(new Option(SINGLE_ID, 1, PARTICLE_FLAME, ModText.translatable("gui.mineturismo.exhaust_adjust.part_single")));
        options.add(new Option(DUAL_ID, 2, PARTICLE_FLAME, ModText.translatable("gui.mineturismo.exhaust_adjust.part_dual")));
        for (String particle : EXTRA_PARTICLES) {
            Component particleName = ModText.translatable("gui.mineturismo.exhaust_adjust.particle_" + particle);
            options.add(new Option(SINGLE_ID + "_" + particle, 1, particle,
                    ModText.translatable("gui.mineturismo.exhaust_adjust.part_single_with", particleName)));
            options.add(new Option(DUAL_ID + "_" + particle, 2, particle,
                    ModText.translatable("gui.mineturismo.exhaust_adjust.part_dual_with", particleName)));
        }
        return List.copyOf(options);
    }

    public static final float DEFAULT_Y = 0.35f;
    public static final float DEFAULT_Z = -2.2f;
    public static final float DEFAULT_SINGLE_X = 0.4f;
    public static final float DEFAULT_DUAL_X = 0.5f;

    public static PartTransforms.Transform6 defaultTransform(String id) {
        float x = outletsOf(id) == 2 ? DEFAULT_DUAL_X : DEFAULT_SINGLE_X;
        return new PartTransforms.Transform6(x, DEFAULT_Y, DEFAULT_Z, 0f, 0f, 1f);
    }

    public static Option byId(String id) {
        for (Option option : OPTIONS) {
            if (option.id().equals(id)) {
                return option;
            }
        }
        return null;
    }

    public static int outletsOf(String id) {
        Option option = byId(id);
        return option != null ? option.outlets() : 0;
    }

    public static String particleOf(String id) {
        Option option = byId(id);
        return option != null ? option.particle() : PARTICLE_FLAME;
    }

    private ExhaustParts() {
    }
}
