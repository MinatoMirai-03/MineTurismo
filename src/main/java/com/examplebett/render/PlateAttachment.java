package com.examplebett.render;

public record PlateAttachment(
        float frontX, float frontY, float frontZ, float frontScale, float frontYaw, float frontPitch, float frontRoll,
        float rearX, float rearY, float rearZ, float rearScale, float rearYaw, float rearPitch, float rearRoll,
        String frontPlateId, String rearPlateId
) {
}
