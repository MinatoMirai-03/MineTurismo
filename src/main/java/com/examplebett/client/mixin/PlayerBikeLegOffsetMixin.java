//? if >=1.18 {
//?}
//? if <1.18 && >=1.15 {
/*
package com.examplebett.client.mixin;

import com.examplebett.entity.CarEntity;
import com.examplebett.entity.VehicleCategory;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidModel.class)
public abstract class PlayerBikeLegOffsetMixin {
    @Shadow
    public ModelPart rightLeg;
    @Shadow
    public ModelPart leftLeg;

    private static final float BIKE_LEG_XROT_RETAIN_FRACTION = 0.3F;
    private static final float VANILLA_LEG_REACH_BLOCKS = 0.75f;

    @Inject(method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V", at = @At("TAIL"))
    private void aiObjectMod$lowerLegsOnMotorcycle(LivingEntity entity, float limbSwing, float limbSwingAmount,
                                                     float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        if (!(entity instanceof Player player) || !(player.getVehicle() instanceof CarEntity car)) {
            return;
        }
        if (!car.hasVehicleCategory(VehicleCategory.MOTORCYCLE)) {
            return;
        }
        this.rightLeg.xRot *= BIKE_LEG_XROT_RETAIN_FRACTION;
        if (car.isBikeStationary()) {
            this.leftLeg.xRot = 0.0F;
            this.leftLeg.zRot = (float) Math.toRadians(car.getBikeLeanRollDegrees());
            float gap = (float) (player.getY() - car.getBoundingBox().minY) - VANILLA_LEG_REACH_BLOCKS;
            float legDropBlocks = Math.max(0f, gap);
            this.leftLeg.y += legDropBlocks * 16f;
        } else {
            this.leftLeg.xRot *= BIKE_LEG_XROT_RETAIN_FRACTION;
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
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidModel.class)
public abstract class PlayerBikeLegOffsetMixin {
    @Shadow
    public ModelPart rightLeg;
    @Shadow
    public ModelPart leftLeg;

    private static final float BIKE_LEG_XROT_RETAIN_FRACTION = 0.3F;
    private static final float VANILLA_LEG_REACH_BLOCKS = 0.75f;

    @Inject(method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFFF)V", at = @At("TAIL"))
    private void aiObjectMod$lowerLegsOnMotorcycle(LivingEntity entity, float limbSwing, float limbSwingAmount,
                                                     float ageInTicks, float netHeadYaw, float headPitch,
                                                     float scale, CallbackInfo ci) {
        if (!(entity instanceof Player player) || !(player.getVehicle() instanceof CarEntity car)) {
            return;
        }
        if (!car.hasVehicleCategory(VehicleCategory.MOTORCYCLE)) {
            return;
        }
        this.rightLeg.xRot *= BIKE_LEG_XROT_RETAIN_FRACTION;
        if (car.isBikeStationary()) {
            this.leftLeg.xRot = 0.0F;
            this.leftLeg.zRot = (float) Math.toRadians(car.getBikeLeanRollDegrees());
            float gap = (float) (ModEntity.getY(player) - car.getBoundingBox().minY) - VANILLA_LEG_REACH_BLOCKS;
            float legDropBlocks = Math.max(0f, gap);
            this.leftLeg.y += legDropBlocks * 16f;
        } else {
            this.leftLeg.xRot *= BIKE_LEG_XROT_RETAIN_FRACTION;
        }
    }
}
*/
//?}
