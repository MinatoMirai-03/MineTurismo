package com.examplebett.client.mixin;

//? if >=1.21.2 {
import com.examplebett.client.render.MotorcycleRiderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(HumanoidRenderState.class)
public class HumanoidRenderStateMotorcycleMixin implements MotorcycleRiderState {
    @Unique
    private boolean aiObjectMod$ridingMotorcycle;
    @Unique
    private float aiObjectMod$bikeLeanRoll;
    @Unique
    private float aiObjectMod$pivotDeltaY;
    @Unique
    private boolean aiObjectMod$stationary;
    @Unique
    private float aiObjectMod$stationaryLegDropBlocks;
    @Unique
    private float aiObjectMod$wheeliePitch;
    @Unique
    private float aiObjectMod$wheeliePivotDeltaX;
    @Unique
    private float aiObjectMod$wheeliePivotDeltaY;
    @Unique
    private float aiObjectMod$wheeliePivotDeltaZ;

    @Override
    public boolean aiObjectMod$isRidingMotorcycle() {
        return this.aiObjectMod$ridingMotorcycle;
    }

    @Override
    public float aiObjectMod$getBikeLeanRoll() {
        return this.aiObjectMod$bikeLeanRoll;
    }

    @Override
    public float aiObjectMod$getPivotDeltaY() {
        return this.aiObjectMod$pivotDeltaY;
    }

    @Override
    public boolean aiObjectMod$isBikeStationary() {
        return this.aiObjectMod$stationary;
    }

    @Override
    public float aiObjectMod$getStationaryLegDropBlocks() {
        return this.aiObjectMod$stationaryLegDropBlocks;
    }

    @Override
    public float aiObjectMod$getWheeliePitch() {
        return this.aiObjectMod$wheeliePitch;
    }

    @Override
    public float aiObjectMod$getWheeliePivotDeltaX() {
        return this.aiObjectMod$wheeliePivotDeltaX;
    }

    @Override
    public float aiObjectMod$getWheeliePivotDeltaY() {
        return this.aiObjectMod$wheeliePivotDeltaY;
    }

    @Override
    public float aiObjectMod$getWheeliePivotDeltaZ() {
        return this.aiObjectMod$wheeliePivotDeltaZ;
    }

    @Override
    public void aiObjectMod$setMotorcycleRiding(boolean riding, float leanRollDegrees, float pivotDeltaY, boolean stationary,
                                                 float stationaryLegDropBlocks, float wheeliePitchDegrees,
                                                 float wheeliePivotDeltaX, float wheeliePivotDeltaY, float wheeliePivotDeltaZ) {
        this.aiObjectMod$ridingMotorcycle = riding;
        this.aiObjectMod$bikeLeanRoll = leanRollDegrees;
        this.aiObjectMod$pivotDeltaY = pivotDeltaY;
        this.aiObjectMod$stationary = stationary;
        this.aiObjectMod$stationaryLegDropBlocks = stationaryLegDropBlocks;
        this.aiObjectMod$wheeliePitch = wheeliePitchDegrees;
        this.aiObjectMod$wheeliePivotDeltaX = wheeliePivotDeltaX;
        this.aiObjectMod$wheeliePivotDeltaY = wheeliePivotDeltaY;
        this.aiObjectMod$wheeliePivotDeltaZ = wheeliePivotDeltaZ;
    }
}
//?}
