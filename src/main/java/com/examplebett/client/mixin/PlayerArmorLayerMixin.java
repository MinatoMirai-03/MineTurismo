//? if >=1.18 {
//?}
//? if <1.18 && >=1.16 {
/*
package com.examplebett.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidArmorLayer.class)
public class PlayerArmorLayerMixin {
    @Inject(
            method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void aiObjectMod$hideArmorWhileInvisiblePassenger(
            PoseStack poseStack,
            MultiBufferSource multiBufferSource,
            int packedLight,
            LivingEntity entity,
            float limbSwing,
            float limbSwingAmount,
            float ageInTicks,
            float netHeadYaw,
            float headPitch,
            float partialTick,
            CallbackInfo ci) {
        if (entity instanceof Player && entity.isInvisible() && entity.isPassenger()) {
            ci.cancel();
        }
    }
}
*/
//?}
//? if <1.16 && >=1.15 {
/*
package com.examplebett.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.AbstractArmorLayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractArmorLayer.class)
public class PlayerArmorLayerMixin {
    @Inject(
            method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void aiObjectMod$hideArmorWhileInvisiblePassenger(
            PoseStack poseStack,
            MultiBufferSource multiBufferSource,
            int packedLight,
            LivingEntity entity,
            float limbSwing,
            float limbSwingAmount,
            float ageInTicks,
            float netHeadYaw,
            float headPitch,
            float partialTick,
            CallbackInfo ci) {
        if (entity instanceof Player && entity.isInvisible() && entity.isPassenger()) {
            ci.cancel();
        }
    }
}
*/
//?}
//? if <1.15 {
/*
package com.examplebett.client.mixin;

import net.minecraft.client.renderer.entity.layers.AbstractArmorLayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractArmorLayer.class)
public class PlayerArmorLayerMixin {
    @Inject(
            method = "render(Lnet/minecraft/world/entity/LivingEntity;FFFFFFF)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void aiObjectMod$hideArmorWhileInvisiblePassenger(
            LivingEntity entity,
            float limbSwing,
            float limbSwingAmount,
            float ageInTicks,
            float netHeadYaw,
            float headPitch,
            float scale,
            float partialTick,
            CallbackInfo ci) {
        if (entity instanceof Player && entity.isInvisible() && entity.isPassenger()) {
            ci.cancel();
        }
    }
}
*/
//?}
