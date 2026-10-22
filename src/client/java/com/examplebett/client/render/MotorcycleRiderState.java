package com.examplebett.client.render;

public interface MotorcycleRiderState {
    boolean aiObjectMod$isRidingMotorcycle();

    float aiObjectMod$getBikeLeanRoll();

    float aiObjectMod$getPivotDeltaY();

    boolean aiObjectMod$isBikeStationary();

    float aiObjectMod$getStationaryLegDropBlocks();

    float aiObjectMod$getWheeliePitch();

    float aiObjectMod$getWheeliePivotDeltaX();

    float aiObjectMod$getWheeliePivotDeltaY();

    float aiObjectMod$getWheeliePivotDeltaZ();

    void aiObjectMod$setMotorcycleRiding(boolean riding, float leanRollDegrees, float pivotDeltaY, boolean stationary,
                                          float stationaryLegDropBlocks, float wheeliePitchDegrees,
                                          float wheeliePivotDeltaX, float wheeliePivotDeltaY, float wheeliePivotDeltaZ);
}
