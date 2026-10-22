package com.examplebett.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
//? if >=1.21.9 {
import net.minecraft.client.renderer.SubmitNodeCollector;
//?}
//? if <1.21.9 {
/*import net.minecraft.client.renderer.MultiBufferSource;*/
//?}
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
//? if >=1.17 {
import net.minecraft.client.renderer.entity.layers.PlayerItemInHandLayer;
//?}
//? if >=1.21.9 {
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
//?}
//? if >=1.21.2 && <1.21.9 {
/*import net.minecraft.client.renderer.entity.state.PlayerRenderState;*/
//?}
//? if >=1.21.4 {
import net.minecraft.client.renderer.item.ItemStackRenderState;
//?}
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//? if >=1.17 {
@Mixin(PlayerItemInHandLayer.class)
//?} else {
/*@Mixin(ItemInHandLayer.class)*/
//?}
public class PlayerItemInHandLayerMixin {
    //? if >=1.21.11 {
    /*@Inject(
            method = "submitArmWithItem(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;Lnet/minecraft/client/renderer/item/ItemStackRenderState;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/HumanoidArm;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;I)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void aiObjectMod$hideHeldItemWhileInvisiblePassenger(
            AvatarRenderState state,
            ItemStackRenderState itemStackRenderState,
            net.minecraft.world.item.ItemStack itemStack,
            HumanoidArm humanoidArm,
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            int packedLight,
            CallbackInfo ci) {
        if (state.isInvisible && state.isPassenger) {
            ci.cancel();
        }
    }*/
    //?}
    //? if >=1.21.9 && <1.21.11 {
    @Inject(
            method = "submitArmWithItem(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;Lnet/minecraft/client/renderer/item/ItemStackRenderState;Lnet/minecraft/world/entity/HumanoidArm;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;I)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void aiObjectMod$hideHeldItemWhileInvisiblePassenger(
            AvatarRenderState state,
            ItemStackRenderState itemStackRenderState,
            HumanoidArm humanoidArm,
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            int packedLight,
            CallbackInfo ci) {
        if (state.isInvisible && state.isPassenger) {
            ci.cancel();
        }
    }
    //?}
    //? if >=1.21.4 && <1.21.9 {
    /*@Inject(
            method = "renderArmWithItem(Lnet/minecraft/client/renderer/entity/state/PlayerRenderState;Lnet/minecraft/client/renderer/item/ItemStackRenderState;Lnet/minecraft/world/entity/HumanoidArm;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private void aiObjectMod$hideHeldItemWhileInvisiblePassenger(
            PlayerRenderState state,
            ItemStackRenderState itemStackRenderState,
            HumanoidArm humanoidArm,
            PoseStack poseStack,
            MultiBufferSource multiBufferSource,
            int packedLight,
            CallbackInfo ci) {
        if (state.isInvisible && state.isPassenger) {
            ci.cancel();
        }
    }*/
    //?}
    //? if >=1.21.2 && <1.21.4 {
    /*@Inject(
            method = "renderArmWithItem(Lnet/minecraft/client/renderer/entity/state/PlayerRenderState;Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;Lnet/minecraft/world/entity/HumanoidArm;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void aiObjectMod$hideHeldItemWhileInvisiblePassenger(
            PlayerRenderState state,
            net.minecraft.client.resources.model.BakedModel bakedModel,
            net.minecraft.world.item.ItemStack itemStack,
            net.minecraft.world.item.ItemDisplayContext itemDisplayContext,
            HumanoidArm humanoidArm,
            PoseStack poseStack,
            MultiBufferSource multiBufferSource,
            int packedLight,
            CallbackInfo ci) {
        if (state.isInvisible && state.isPassenger) {
            ci.cancel();
        }
    }*/
    //?}
    //? if >=1.19.4 && <1.21.2 {
    /*@Inject(
            method = "renderArmWithItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;Lnet/minecraft/world/entity/HumanoidArm;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void aiObjectMod$hideHeldItemWhileInvisiblePassenger(
            LivingEntity entity,
            net.minecraft.world.item.ItemStack itemStack,
            net.minecraft.world.item.ItemDisplayContext itemDisplayContext,
            HumanoidArm humanoidArm,
            PoseStack poseStack,
            MultiBufferSource multiBufferSource,
            int packedLight,
            CallbackInfo ci) {
        if (entity.isInvisible() && entity.isPassenger()) {
            ci.cancel();
        }
    }*/
    //?}
    //? if >=1.17 && <1.19.4 {
    /*@Inject(
            method = "renderArmWithItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/client/renderer/block/model/ItemTransforms$TransformType;Lnet/minecraft/world/entity/HumanoidArm;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void aiObjectMod$hideHeldItemWhileInvisiblePassenger(
            LivingEntity entity,
            net.minecraft.world.item.ItemStack itemStack,
            net.minecraft.client.renderer.block.model.ItemTransforms.TransformType transformType,
            HumanoidArm humanoidArm,
            PoseStack poseStack,
            MultiBufferSource multiBufferSource,
            int packedLight,
            CallbackInfo ci) {
        if (entity.isInvisible() && entity.isPassenger()) {
            ci.cancel();
        }
    }*/
    //?}
    //? if >=1.15 && <1.17 {
    /*@Inject(
            method = "renderArmWithItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/client/renderer/block/model/ItemTransforms$TransformType;Lnet/minecraft/world/entity/HumanoidArm;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void aiObjectMod$hideHeldItemWhileInvisiblePassenger(
            LivingEntity entity,
            net.minecraft.world.item.ItemStack itemStack,
            net.minecraft.client.renderer.block.model.ItemTransforms.TransformType transformType,
            HumanoidArm humanoidArm,
            PoseStack poseStack,
            MultiBufferSource multiBufferSource,
            int packedLight,
            CallbackInfo ci) {
        if (entity instanceof Player && entity.isInvisible() && entity.isPassenger()) {
            ci.cancel();
        }
    }*/
    //?}
    //? if <1.15 {
    /*@Inject(
            method = "renderArmWithItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/client/renderer/block/model/ItemTransforms$TransformType;Lnet/minecraft/world/entity/HumanoidArm;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void aiObjectMod$hideHeldItemWhileInvisiblePassenger(
            LivingEntity entity,
            net.minecraft.world.item.ItemStack itemStack,
            net.minecraft.client.renderer.block.model.ItemTransforms.TransformType transformType,
            HumanoidArm humanoidArm,
            CallbackInfo ci) {
        if (entity instanceof Player && entity.isInvisible() && entity.isPassenger()) {
            ci.cancel();
        }
    }*/
    //?}
}
