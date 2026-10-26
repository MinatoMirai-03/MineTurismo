package com.examplebett.entity;
import com.examplebett.compat.ModText;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.chat.Component;

import java.util.List;

public final class TireSmokeParticles {

    public record Option(String id, SimpleParticleType particleType, Component displayName) {
    }

    private static final List<Option> OPTIONS_MUTABLE = new java.util.ArrayList<>(List.of(
            new Option("smoke", ParticleTypes.SMOKE, ModText.translatable("gui.mineturismo.tire_smoke_edit.particle_smoke")),
            new Option("large_smoke", ParticleTypes.LARGE_SMOKE, ModText.translatable("gui.mineturismo.tire_smoke_edit.particle_large_smoke")),
            new Option("campfire_smoke", ParticleTypes.CAMPFIRE_COSY_SMOKE, ModText.translatable("gui.mineturismo.tire_smoke_edit.particle_campfire_smoke")),
            new Option("cloud", ParticleTypes.CLOUD, ModText.translatable("gui.mineturismo.tire_smoke_edit.particle_cloud")),
            new Option("poof", ParticleTypes.POOF, ModText.translatable("gui.mineturismo.tire_smoke_edit.particle_poof"))
    ));

    static {
        //? if >=1.16 {
        OPTIONS_MUTABLE.add(new Option("white_ash", ParticleTypes.WHITE_ASH, ModText.translatable("gui.mineturismo.tire_smoke_edit.particle_white_ash")));
        //?} else {
        /*OPTIONS_MUTABLE.add(new Option("white_ash", ParticleTypes.MYCELIUM, ModText.translatable("gui.mineturismo.tire_smoke_edit.particle_white_ash")));*/
        //?}
        //? if >=1.17 {
        OPTIONS_MUTABLE.add(new Option("snow", ParticleTypes.SNOWFLAKE, ModText.translatable("gui.mineturismo.tire_smoke_edit.particle_snow")));
        //?} else {
        /*OPTIONS_MUTABLE.add(new Option("snow", ParticleTypes.FIREWORK, ModText.translatable("gui.mineturismo.tire_smoke_edit.particle_snow")));*/
        //?}
    }
    public static final List<Option> OPTIONS = List.copyOf(OPTIONS_MUTABLE);

    public static final String DEFAULT_ID = "poof";

    public static SimpleParticleType byId(String id) {
        for (Option option : OPTIONS) {
            if (option.id().equals(id)) {
                return option.particleType();
            }
        }
        return ParticleTypes.POOF;
    }

    private TireSmokeParticles() {
    }
}
