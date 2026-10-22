//? if >=1.18 {
//?} else {
/*
package com.examplebett.client.mixin;

import com.examplebett.client.MenuCameraOrbit;
import com.examplebett.client.gui.CarCameraOrbitScreen;
import com.examplebett.entity.CarEntity;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(Camera.class)
public abstract class CameraMixin {

    @Shadow
    public abstract Vec3 getPosition();

    @Shadow
    protected abstract void setPosition(Vec3 pos);

    @Shadow
    protected abstract void setRotation(float yRot, float xRot);

    @Unique
    private CarEntity mineturismo$riddenCar;

    @Unique
    private boolean mineturismo$orbitScreenActiveLastFrame;

    @Inject(method = "setup", at = @At("HEAD"))
    private void mineturismo$captureRiddenCar(BlockGetter level, Entity entity, boolean detached,
                                               boolean thirdPersonReverse, float partialTick, CallbackInfo ci) {
        Entity vehicle = entity.getVehicle();
        this.mineturismo$riddenCar = (vehicle instanceof CarEntity car) ? car : null;

        boolean orbitScreenNow = Minecraft.getInstance().screen instanceof CarCameraOrbitScreen;
        if (orbitScreenNow && !this.mineturismo$orbitScreenActiveLastFrame) {
            MenuCameraOrbit.begin(entity.getViewYRot(partialTick), entity.getViewXRot(partialTick));
        } else if (!orbitScreenNow && this.mineturismo$orbitScreenActiveLastFrame) {
            MenuCameraOrbit.end();
        }
        this.mineturismo$orbitScreenActiveLastFrame = orbitScreenNow;
    }

    @ModifyArg(method = "setup", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;getMaxZoom(D)D"))
    private double mineturismo$modifyThirdPersonDistance(double distance) {
        if (this.mineturismo$riddenCar == null) {
            return distance;
        }
        float heightOffset = this.mineturismo$riddenCar.getCameraHeight();
        if (heightOffset != 0f) {
            this.setPosition(this.getPosition().add(0.0, heightOffset, 0.0));
        }
        return this.mineturismo$riddenCar.getCameraDistance();
    }

    @ModifyArgs(method = "setup", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setRotation(FF)V", ordinal = 0))
    private void mineturismo$overrideMenuOrbitRotation(Args args) {
        if (MenuCameraOrbit.isActive()) {
            args.set(0, MenuCameraOrbit.yaw());
            args.set(1, MenuCameraOrbit.pitch());
        }
    }

    @Inject(method = "setup", at = @At("TAIL"))
    private void mineturismo$gttTopDownCamera(BlockGetter level, Entity entity, boolean detached,
                                              boolean thirdPersonReverse, float partialTick, CallbackInfo ci) {
        if (!com.examplebett.client.GttClient.isActive() || this.mineturismo$riddenCar == null) {
            return;
        }
        Vec3 carPos = com.examplebett.compat.ModVec3.position(this.mineturismo$riddenCar, partialTick);
        this.setPosition(carPos.add(0.0, com.examplebett.client.GttClient.CAMERA_HEIGHT, 0.0));
        this.setRotation(com.examplebett.client.GttClient.CAMERA_YAW, com.examplebett.client.GttClient.CAMERA_PITCH);
    }
}
*/
//?}
