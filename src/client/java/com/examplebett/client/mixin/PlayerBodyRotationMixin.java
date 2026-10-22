package com.examplebett.client.mixin;

//? if >=1.21.2 {
import com.examplebett.client.render.MotorcycleRiderState;
import com.examplebett.entity.CarEntity;
import com.examplebett.entity.VehicleCategory;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public class PlayerBodyRotationMixin {

    private static final float WHEELIE_PIVOT_Z_FACTOR = 0.85f;

    private static final float VANILLA_LEG_REACH_BLOCKS = 0.75f;

    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;F)V", at = @At("TAIL"))
    private void aiObjectMod$lockBodyRotForOpenCarPassenger(
            LivingEntity livingEntity, LivingEntityRenderState renderState, float partialTick, CallbackInfo ci) {

        boolean motorcycle = false;
        float leanRoll = 0f;
        float pivotDeltaY = 0f;
        boolean stationary = false;
        float legDropBlocks = 0f;
        float wheeliePitch = 0f;
        float wheeliePivotDeltaX = 0f;
        float wheeliePivotDeltaY = 0f;
        float wheeliePivotDeltaZ = 0f;
        if (livingEntity instanceof Player player && player.getVehicle() instanceof CarEntity car) {
            motorcycle = car.hasVehicleCategory(VehicleCategory.MOTORCYCLE);
            boolean openCarLike = car.hasVehicleCategory(VehicleCategory.OPEN_CAR) || motorcycle;
            if (openCarLike) {

                renderState.bodyRot = 360.0F - car.getYRot(partialTick);
            }
            if (motorcycle) {
                leanRoll = car.getBikeLeanRollDegrees();

                pivotDeltaY = (float) ((car.getY() + car.getScaledHitboxHeight() / 2.0) - player.getY());
                stationary = car.isBikeStationary();
                if (stationary) {

                    float gap = (float) (player.getY() - car.getBoundingBox().minY) - VANILLA_LEG_REACH_BLOCKS;
                    legDropBlocks = Math.max(0f, gap);
                }
            }

            if (openCarLike) {
                wheeliePitch = car.getWheeliePitchDegrees();
                if (wheeliePitch != 0f) {

                    float wheeliePivotZLocal = -car.getFootprintHalfLength() * WHEELIE_PIVOT_Z_FACTOR;
                    wheeliePivotDeltaX = 0f;
                    wheeliePivotDeltaY = (float) (car.getY() - player.getY());
                    wheeliePivotDeltaZ = -wheeliePivotZLocal;
                }
            }
        }
        if (renderState instanceof MotorcycleRiderState motorcycleState) {
            motorcycleState.aiObjectMod$setMotorcycleRiding(motorcycle, leanRoll, pivotDeltaY, stationary,
                    legDropBlocks, wheeliePitch, wheeliePivotDeltaX, wheeliePivotDeltaY, wheeliePivotDeltaZ);
        }
    }
}
//?}
