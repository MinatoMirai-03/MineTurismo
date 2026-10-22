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

    //? if >=1.21.11 {
    /*@Shadow
    public abstract Vec3 position();*/
    //?} else {
    @Shadow
    public abstract Vec3 getPosition();
    //?}

    @Shadow
    protected abstract void setPosition(Vec3 pos);

    @Shadow
    protected abstract void setRotation(float yRot, float xRot);

    @Unique
    private Vec3 mineturismo$currentPosition() {
        //? if >=1.21.11 {
        /*return this.position();*/
        //?} else {
        return this.getPosition();
        //?}
    }

    @Unique
    private CarEntity mineturismo$riddenCar;

    @Unique
    private boolean mineturismo$orbitScreenActiveLastFrame;

    //? if >=26.1 {
    /*@Shadow
    private Entity entity;

    @Inject(method = "alignWithEntity", at = @At("HEAD"))
    private void mineturismo$captureRiddenCar(float partialTick, CallbackInfo ci) {
        this.mineturismo$captureRiddenCarImpl(this.entity, partialTick);
    }*/
    //?}

    //? if >=1.21.11 && <26.1 {
    /*@Inject(method = "setup", at = @At("HEAD"))
    private void mineturismo$captureRiddenCar(net.minecraft.world.level.Level level, Entity entity, boolean detached,
                                               boolean thirdPersonReverse, float partialTick, CallbackInfo ci) {
        this.mineturismo$captureRiddenCarImpl(entity, partialTick);
    }*/
    //?}
    //? if <1.21.11 {
    @Inject(method = "setup", at = @At("HEAD"))
    private void mineturismo$captureRiddenCar(BlockGetter level, Entity entity, boolean detached,
                                               boolean thirdPersonReverse, float partialTick, CallbackInfo ci) {
        this.mineturismo$captureRiddenCarImpl(entity, partialTick);
    }
    //?}

    //? if >=26.1 {
    /*@Inject(method = "alignWithEntity", at = @At("TAIL"))
    private void mineturismo$gttTopDownCamera(float partialTick, CallbackInfo ci) {
        this.mineturismo$applyGttCamera(partialTick);
    }*/
    //?}
    //? if >=1.21.11 && <26.1 {
    /*@Inject(method = "setup", at = @At("TAIL"))
    private void mineturismo$gttTopDownCamera(net.minecraft.world.level.Level level, Entity entity, boolean detached,
                                              boolean thirdPersonReverse, float partialTick, CallbackInfo ci) {
        this.mineturismo$applyGttCamera(partialTick);
    }*/
    //?}
    //? if <1.21.11 {
    @Inject(method = "setup", at = @At("TAIL"))
    private void mineturismo$gttTopDownCamera(BlockGetter level, Entity entity, boolean detached,
                                              boolean thirdPersonReverse, float partialTick, CallbackInfo ci) {
        this.mineturismo$applyGttCamera(partialTick);
    }
    //?}

    @Unique
    private void mineturismo$applyGttCamera(float partialTick) {
        if (!com.examplebett.client.GttClient.isActive() || this.mineturismo$riddenCar == null) {
            return;
        }
        Vec3 carPos = this.mineturismo$riddenCar.getPosition(partialTick);
        this.setPosition(carPos.add(0.0, com.examplebett.client.GttClient.CAMERA_HEIGHT, 0.0));
        this.setRotation(com.examplebett.client.GttClient.CAMERA_YAW, com.examplebett.client.GttClient.CAMERA_PITCH);
    }

    @Unique
    private void mineturismo$captureRiddenCarImpl(Entity entity, float partialTick) {
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

    //? if >=26.1 {
    /*@ModifyArg(method = "alignWithEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;getMaxZoom(F)F"))
    private float mineturismo$modifyThirdPersonDistance(float distance) {
        if (this.mineturismo$riddenCar == null) {
            return distance;
        }
        float heightOffset = this.mineturismo$riddenCar.getCameraHeight();
        if (heightOffset != 0f) {
            this.setPosition(this.mineturismo$currentPosition().add(0.0, heightOffset, 0.0));
        }
        return this.mineturismo$riddenCar.getCameraDistance();
    }*/
    //?}
    //? if >=1.21 && <26.1 {
    @ModifyArg(method = "setup", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;getMaxZoom(F)F"))
    private float mineturismo$modifyThirdPersonDistance(float distance) {
        if (this.mineturismo$riddenCar == null) {
            return distance;
        }
        float heightOffset = this.mineturismo$riddenCar.getCameraHeight();
        if (heightOffset != 0f) {
            this.setPosition(this.mineturismo$currentPosition().add(0.0, heightOffset, 0.0));
        }
        return this.mineturismo$riddenCar.getCameraDistance();
    }
    //?}
    //? if <1.21 {
    /*@ModifyArg(method = "setup", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;getMaxZoom(D)D"))
    private double mineturismo$modifyThirdPersonDistance(double distance) {
        if (this.mineturismo$riddenCar == null) {
            return distance;
        }
        float heightOffset = this.mineturismo$riddenCar.getCameraHeight();
        if (heightOffset != 0f) {
            this.setPosition(this.mineturismo$currentPosition().add(0.0, heightOffset, 0.0));
        }
        return this.mineturismo$riddenCar.getCameraDistance();
    }*/
    //?}

    //? if >=26.1 {
    /*@ModifyArgs(method = "alignWithEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setRotation(FF)V", ordinal = 1))
    private void mineturismo$overrideMenuOrbitRotation(Args args) {
        if (MenuCameraOrbit.isActive()) {
            args.set(0, MenuCameraOrbit.yaw());
            args.set(1, MenuCameraOrbit.pitch());
        }
    }*/
    //?}
    //? if >=1.21.2 && <26.1 {
    @ModifyArgs(method = "setup", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setRotation(FF)V", ordinal = 1))
    private void mineturismo$overrideMenuOrbitRotation(Args args) {
        if (MenuCameraOrbit.isActive()) {
            args.set(0, MenuCameraOrbit.yaw());
            args.set(1, MenuCameraOrbit.pitch());
        }
    }
    //?}
    //? if >=1.18 && <1.21.2 {
    /*@ModifyArgs(method = "setup", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setRotation(FF)V", ordinal = 0))
    private void mineturismo$overrideMenuOrbitRotation(Args args) {
        if (MenuCameraOrbit.isActive()) {
            args.set(0, MenuCameraOrbit.yaw());
            args.set(1, MenuCameraOrbit.pitch());
        }
    }*/
    //?}
}
