package com.examplebett.render;

import java.util.List;

public record WheelAttachment(String wheelModelId, List<String> wheelStyles, float frontZ, float rearZ,
                               float trackHalfWidth, float axleHeight, float wheelRadius, float bodyHeight,
                               float rearZ2, float wheelScale, float wheelWidthScale, float carScale,
                               float widthStretchCm, float widthZoneCm, float widthCenterOffsetCm,
                               float limoStretchCm, float limoZoneCm, float limoCenterOffsetCm,
                               float passengerX, float passengerY, float passengerZ,
                               boolean singleWheelPerAxle) {

    public static final String NONE_STYLE = "none";

    public boolean hasRearAxle2() {
        return !Float.isNaN(this.rearZ2);
    }
}
