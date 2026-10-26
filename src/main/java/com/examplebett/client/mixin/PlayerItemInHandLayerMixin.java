//? if >=1.18 {
//?}
//? if <1.18 && >=1.17 {
/*
package com.examplebett.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.layers.PlayerItemInHandLayer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerItemInHandLayer.class)
public class PlayerItemInHandLayerMixin {
    @Inject(
            method = "renderArmWithItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/client/renderer/block/model/ItemTransforms$TransformType;Lnet/minecraft/world/entity/HumanoidArm;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void aiObjectMod$hideHeldItemWhileInvisiblePassenger(
            LivingEntity entity,
            ItemStack itemStack,
            ItemTransforms.TransformType transformType,
            HumanoidArm humanoidArm,
            PoseStack poseStack,
            MultiBufferSource multiBufferSource,
            int packedLight,
            CallbackInfo ci) {
        if (entity.isInvisible() && entity.isPassenger()) {
            ci.cancel();
        }
    }
}
*/
//?}
//? if <1.17 && >=1.15 {
/*
package com.examplebett.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandLayer.class)
public class PlayerItemInHandLayerMixin {
    @Inject(
            method = "renderArmWithItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/client/renderer/block/model/ItemTransforms$TransformType;Lnet/minecraft/world/entity/HumanoidArm;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void aiObjectMod$hideHeldItemWhileInvisiblePassenger(
            LivingEntity entity,
            ItemStack itemStack,
            ItemTransforms.TransformType transformType,
            HumanoidArm humanoidArm,
            PoseStack poseStack,
            MultiBufferSource multiBufferSource,
            int packedLight,
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

import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandLayer.class)
public class PlayerItemInHandLayerMixin {
    @Inject(
            method = "renderArmWithItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/client/renderer/block/model/ItemTransforms$TransformType;Lnet/minecraft/world/entity/HumanoidArm;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void aiObjectMod$hideHeldItemWhileInvisiblePassenger(
            LivingEntity entity,
            ItemStack itemStack,
            ItemTransforms.TransformType transformType,
            HumanoidArm humanoidArm,
            CallbackInfo ci) {
        if (entity instanceof Player && entity.isInvisible() && entity.isPassenger()) {
            ci.cancel();
        }
    }
}
*/
//?}
