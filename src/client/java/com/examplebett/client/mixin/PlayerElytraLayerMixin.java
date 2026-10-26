package com.examplebett.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
//? if >=1.21.9 {
import net.minecraft.client.renderer.SubmitNodeCollector;
//?}
//? if <1.21.9 {
/*import net.minecraft.client.renderer.MultiBufferSource;*/
//?}
//? if >=1.21.2 {
import net.minecraft.client.renderer.entity.layers.WingsLayer;
//?} else {
/*import net.minecraft.client.renderer.entity.layers.ElytraLayer;*/
//?}
//? if >=1.21.9 {
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
//?}
//? if >=1.21.2 && <1.21.9 {
/*import net.minecraft.client.renderer.entity.state.PlayerRenderState;*/
//?}
//? if >=1.21.2 {
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
//?}
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//? if >=1.21.2 {
@Mixin(WingsLayer.class)
//?} else {
/*@Mixin(ElytraLayer.class)*/
//?}
public class PlayerElytraLayerMixin {
    //? if >=1.21.9 {
    @Inject(
            method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/HumanoidRenderState;FF)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void aiObjectMod$hideElytraWhileInvisiblePassenger(
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            int packedLight,
            HumanoidRenderState state,
            float limbSwing,
            float partialTick,
            CallbackInfo ci) {
        if (state instanceof AvatarRenderState && state.isInvisible && state.isPassenger) {
            ci.cancel();
        }
    }
    //?}
    //? if >=1.21.2 && <1.21.9 {
    /*@Inject(
            method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/state/HumanoidRenderState;FF)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void aiObjectMod$hideElytraWhileInvisiblePassenger(
            PoseStack poseStack,
            MultiBufferSource multiBufferSource,
            int packedLight,
            HumanoidRenderState state,
            float limbSwing,
            float partialTick,
            CallbackInfo ci) {
        if (state instanceof PlayerRenderState && state.isInvisible && state.isPassenger) {
            ci.cancel();
        }
    }*/
    //?}
    //? if <1.21.2 {
    /*@Inject(
            method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void aiObjectMod$hideElytraWhileInvisiblePassenger(
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
    }*/
    //?}
}
