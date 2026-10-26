package com.examplebett.client.mixin;

//? if >=1.21.2 {
import com.examplebett.client.render.MotorcycleRiderState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public class PlayerBikeLeanMixin {
    @Inject(method = "setupRotations(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;FF)V", at = @At("TAIL"))
    private void aiObjectMod$applyBikeLean(LivingEntityRenderState renderState, PoseStack poseStack, float ageInTicks, float rotationYaw, CallbackInfo ci) {
        if (!(renderState instanceof MotorcycleRiderState motorcycleState)) {
            return;
        }
        if (motorcycleState.aiObjectMod$isRidingMotorcycle()) {
            float roll = motorcycleState.aiObjectMod$getBikeLeanRoll();
            if (roll != 0f) {

                float pivotDeltaY = motorcycleState.aiObjectMod$getPivotDeltaY();
                poseStack.translate(0.0, pivotDeltaY, 0.0);

                poseStack.mulPose(Axis.ZP.rotationDegrees(-roll));
                poseStack.translate(0.0, -pivotDeltaY, 0.0);
            }
        }
        float wheeliePitch = motorcycleState.aiObjectMod$getWheeliePitch();
        if (wheeliePitch != 0f) {

            float dx = motorcycleState.aiObjectMod$getWheeliePivotDeltaX();
            float dy = motorcycleState.aiObjectMod$getWheeliePivotDeltaY();
            float dz = motorcycleState.aiObjectMod$getWheeliePivotDeltaZ();
            poseStack.translate(dx, dy, dz);
            poseStack.mulPose(Axis.XP.rotationDegrees(wheeliePitch));
            poseStack.translate(-dx, -dy, -dz);
        }
    }
}
//?}

//? if >=1.19.3 && <1.20.5 {
/*
import com.examplebett.entity.CarEntity;
import com.examplebett.entity.VehicleCategory;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
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
        poseStack.mulPose(Axis.YP.rotationDegrees(-(180f - rotationYaw)));
        float correctedBodyRot = 360f - car.getYRot();
        poseStack.mulPose(Axis.YP.rotationDegrees(180f - correctedBodyRot));

        if (motorcycle) {
            float roll = car.getBikeLeanRollDegrees();
            if (roll != 0f) {
                float pivotDeltaY = (float) ((car.getY() + car.getScaledHitboxHeight() / 2.0) - player.getY());
                poseStack.translate(0.0, pivotDeltaY, 0.0);
                poseStack.mulPose(Axis.ZP.rotationDegrees(-roll));
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
            poseStack.mulPose(Axis.XP.rotationDegrees(wheeliePitch));
            poseStack.translate(-dx, -dy, -dz);
        }
    }
}
*/
//?}
//? if >=1.18 && <1.19.3 {
/*
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
//? if >=1.20.5 && <1.21.2 {
/*
import com.examplebett.entity.CarEntity;
import com.examplebett.entity.VehicleCategory;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
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

    @Inject(method = "setupRotations(Lnet/minecraft/world/entity/LivingEntity;Lcom/mojang/blaze3d/vertex/PoseStack;FFFF)V", at = @At("TAIL"))
    private void aiObjectMod$applyBikeBodyRotAndLean(LivingEntity entity, PoseStack poseStack, float ageInTicks,
                                                       float rotationYaw, float shakeAngle, float partialTick, CallbackInfo ci) {
        if (!(entity instanceof Player player) || !(player.getVehicle() instanceof CarEntity car)) {
            return;
        }
        boolean motorcycle = car.hasVehicleCategory(VehicleCategory.MOTORCYCLE);
        boolean openCarLike = motorcycle || car.hasVehicleCategory(VehicleCategory.OPEN_CAR);
        if (!openCarLike) {
            return;
        }
        poseStack.mulPose(Axis.YP.rotationDegrees(-(180f - rotationYaw)));
        float correctedBodyRot = 360f - car.getYRot();
        poseStack.mulPose(Axis.YP.rotationDegrees(180f - correctedBodyRot));

        if (motorcycle) {
            float roll = car.getBikeLeanRollDegrees();
            if (roll != 0f) {
                float pivotDeltaY = (float) ((car.getY() + car.getScaledHitboxHeight() / 2.0) - player.getY());
                poseStack.translate(0.0, pivotDeltaY, 0.0);
                poseStack.mulPose(Axis.ZP.rotationDegrees(-roll));
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
            poseStack.mulPose(Axis.XP.rotationDegrees(wheeliePitch));
            poseStack.translate(-dx, -dy, -dz);
        }
    }
}
*/
//?}
