//? if >=1.18 {
//?}
//? if <1.18 && >=1.15 {
/*
package com.examplebett.client.mixin;

import com.examplebett.entity.CarEntity;
import com.examplebett.entity.VehicleCategory;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public class PlayerBikeLeanMixin {
    private static final float WHEELIE_PIVOT_Z_FACTOR = 0.85f;

    @Inject(method = "setupRotations(Lnet/minecraft/world/entity/LivingEntity;Lcom/mojang/blaze3d/vertex/PoseStack;FFF)V", at = @At("TAIL"))
    private void aiObjectMod$applyBikeBodyRotAndLean(LivingEntity entity, PoseStack poseStack, float ageInTicks,
                                                       float rotationYaw, float partialTick, CallbackInfo ci) {
        if (!(entity instanceof Player player) || !(player.getVehicle() instanceof CarEntity car)) {
            return;
        }
        boolean motorcycle = car.hasVehicleCategory(VehicleCategory.MOTORCYCLE);
        boolean openCarLike = motorcycle || car.hasVehicleCategory(VehicleCategory.OPEN_CAR);
        if (!openCarLike) {
            return;
        }
        poseStack.mulPose(Vector3f.YP.rotationDegrees(-(180f - rotationYaw)));
        float correctedBodyRot = 360f - car.getYRot();
        poseStack.mulPose(Vector3f.YP.rotationDegrees(180f - correctedBodyRot));

        if (motorcycle) {
            float roll = car.getBikeLeanRollDegrees();
            if (roll != 0f) {
                float pivotDeltaY = (float) ((car.getY() + car.getScaledHitboxHeight() / 2.0) - player.getY());
                poseStack.translate(0.0, pivotDeltaY, 0.0);
                poseStack.mulPose(Vector3f.ZP.rotationDegrees(-roll));
                poseStack.translate(0.0, -pivotDeltaY, 0.0);
            }
        }
        float wheeliePitch = car.getWheeliePitchDegrees();
        if (wheeliePitch != 0f) {
            float wheeliePivotZLocal = -car.getFootprintHalfLength() * WHEELIE_PIVOT_Z_FACTOR;
            float dx = 0f;
            float dy = (float) (car.getY() - player.getY());
            float dz = -wheeliePivotZLocal;
            poseStack.translate(dx, dy, dz);
            poseStack.mulPose(Vector3f.XP.rotationDegrees(wheeliePitch));
            poseStack.translate(-dx, -dy, -dz);
        }
    }
}
*/
//?}
//? if <1.15 {
/*
package com.examplebett.client.mixin;

import com.examplebett.compat.ModEntity;
import com.examplebett.entity.CarEntity;
import com.examplebett.entity.VehicleCategory;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public class PlayerBikeLeanMixin {
    private static final float WHEELIE_PIVOT_Z_FACTOR = 0.85f;

    @Inject(method = "setupRotations(Lnet/minecraft/world/entity/LivingEntity;FFF)V", at = @At("TAIL"))
    private void aiObjectMod$applyBikeBodyRotAndLean(LivingEntity entity, float ageInTicks, float rotationYaw,
                                                       float partialTick, CallbackInfo ci) {
        if (!(entity instanceof Player player) || !(player.getVehicle() instanceof CarEntity car)) {
            return;
        }
        boolean motorcycle = car.hasVehicleCategory(VehicleCategory.MOTORCYCLE);
        boolean openCarLike = motorcycle || car.hasVehicleCategory(VehicleCategory.OPEN_CAR);
        if (!openCarLike) {
            return;
        }
        GlStateManager.rotatef(-(180f - rotationYaw), 0f, 1f, 0f);
        float correctedBodyRot = 360f - car.getYRot();
        GlStateManager.rotatef(180f - correctedBodyRot, 0f, 1f, 0f);

        if (motorcycle) {
            float roll = car.getBikeLeanRollDegrees();
            if (roll != 0f) {
                float pivotDeltaY = (float) ((ModEntity.getY(car) + car.getScaledHitboxHeight() / 2.0) - ModEntity.getY(player));
                GlStateManager.translatef(0f, pivotDeltaY, 0f);
                GlStateManager.rotatef(-roll, 0f, 0f, 1f);
                GlStateManager.translatef(0f, -pivotDeltaY, 0f);
            }
        }
        float wheeliePitch = car.getWheeliePitchDegrees();
        if (wheeliePitch != 0f) {
            float wheeliePivotZLocal = -car.getFootprintHalfLength() * WHEELIE_PIVOT_Z_FACTOR;
            float dx = 0f;
            float dy = (float) (ModEntity.getY(car) - ModEntity.getY(player));
            float dz = -wheeliePivotZLocal;
            GlStateManager.translatef(dx, dy, dz);
            GlStateManager.rotatef(wheeliePitch, 1f, 0f, 0f);
            GlStateManager.translatef(-dx, -dy, -dz);
        }
    }
}
*/
//?}
