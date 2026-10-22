package com.examplebett.effect;

import com.examplebett.AiObjectMod;
import com.examplebett.compat.ModEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public final class SonicBoomEnergy {

    private static final int COLOR = 0x29DFEB;

    private static final double MAX_BEAM_LENGTH = 16.0;

    //? if >=1.20.5 {
    public static net.minecraft.core.Holder<MobEffect> EFFECT;
    //?} else {
    /*public static MobEffect EFFECT;*/
    //?}

    private static final class SonicBoomEffect extends MobEffect {
        SonicBoomEffect() {
            super(MobEffectCategory.BENEFICIAL, COLOR);
        }
    }

    private SonicBoomEnergy() {
    }

    public static void register() {
        //? if >=1.20.5 {
        EFFECT = net.minecraft.core.Registry.registerForHolder(net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT,
                AiObjectMod.id("sonic_boom"), new SonicBoomEffect());
        //?} else if >=1.19.3 {
        /*EFFECT = net.minecraft.core.Registry.register(net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT,
                AiObjectMod.id("sonic_boom"), new SonicBoomEffect());*/
        //?} else {
        /*EFFECT = net.minecraft.core.Registry.register(net.minecraft.core.Registry.MOB_EFFECT,
                AiObjectMod.id("sonic_boom"), new SonicBoomEffect());*/
        //?}
        net.fabricmc.fabric.api.event.player.AttackEntityCallback.EVENT.register((player, level, hand, target, hitResult) -> {
            if (!level.isClientSide() && target instanceof LivingEntity && player.hasEffect(EFFECT)
                    && level instanceof ServerLevel) {
                spawnBeam((ServerLevel) level, player, target);
            }
            return InteractionResult.PASS;
        });
    }

    private static void spawnBeam(ServerLevel level, LivingEntity attacker, net.minecraft.world.entity.Entity target) {
        Vec3 from = new Vec3(ModEntity.getX(attacker), ModEntity.getY(attacker) + attacker.getEyeHeight(), ModEntity.getZ(attacker));
        Vec3 to = new Vec3(ModEntity.getX(target), ModEntity.getY(target) + target.getBbHeight() * 0.5, ModEntity.getZ(target));
        Vec3 delta = to.subtract(from);
        double length = Math.min(MAX_BEAM_LENGTH, delta.length());
        Vec3 dir = delta.normalize();
        for (int i = 1; i <= Math.max(1, (int) Math.ceil(length)); i++) {
            Vec3 p = from.add(dir.scale(Math.min(i, length)));
            //? if >=1.19 {
            level.sendParticles(ParticleTypes.SONIC_BOOM, p.x, p.y, p.z, 1, 0.0, 0.0, 0.0, 0.0);
            //?} else {
            /*level.sendParticles(ParticleTypes.END_ROD, p.x, p.y, p.z, 4, 0.15, 0.15, 0.15, 0.02);*/
            //?}
        }
        //? if >=1.19 {
        level.playSound(null, ModEntity.getX(attacker), ModEntity.getY(attacker), ModEntity.getZ(attacker), SoundEvents.WARDEN_SONIC_BOOM,
                SoundSource.PLAYERS, 0.6f, 1.2f);
        //?} else {
        /*level.playSound(null, ModEntity.getX(attacker), ModEntity.getY(attacker), ModEntity.getZ(attacker), SoundEvents.FIREWORK_ROCKET_LARGE_BLAST,
                SoundSource.PLAYERS, 0.8f, 0.6f);*/
        //?}
    }
}
