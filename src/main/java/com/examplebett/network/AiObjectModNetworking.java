package com.examplebett.network;
import com.examplebett.compat.ModPlayer;
import com.examplebett.compat.ModText;

import com.examplebett.compat.ModNetworking;
import com.examplebett.entity.CarEntity;
import com.examplebett.entity.VehicleCategory;
import com.examplebett.item.AiObjectModItems;
import com.examplebett.waypoint.SignalGroupConfig;
import com.examplebett.waypoint.WaypointNetworkSavedData;
//? if >=1.20.5 {
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
//?}
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class AiObjectModNetworking {

    private static ServerLevel serverLevel(ServerPlayer player) {
        //? if >=1.20 {
        //? if >=1.21.6 {
        return player.level();
        //?} else
        /*return (ServerLevel) player.level();*/
        //?} else
        /*return (ServerLevel) player.getLevel();*/
    }

    public static void register() {

        //? if >=1.20.5 {
        ModNetworking.registerS2C(PlayCollisionSoundPayload.TYPE, PlayCollisionSoundPayload.STREAM_CODEC);
        //?}

        //? if >=1.20.5 {
        ModNetworking.registerS2C(SyncWheelDistancePayload.TYPE, SyncWheelDistancePayload.STREAM_CODEC);
        //?}

        //? if >=1.20.5 {
        ModNetworking.registerS2C(OpenSignalSettingsPayload.TYPE, OpenSignalSettingsPayload.STREAM_CODEC);
        //?}

        //? if >=1.20.5 {
        ModNetworking.registerS2C(SyncAmbientEngineVolumePayload.TYPE, SyncAmbientEngineVolumePayload.STREAM_CODEC);
        //?}

        //? if >=1.20.5 {
        ModNetworking.registerS2C(OpenRailwayConsistSettingsPayload.TYPE, OpenRailwayConsistSettingsPayload.STREAM_CODEC);
        //?}
        //? if >=1.20.5 {
        ModNetworking.registerS2C(ShowHudWarningPayload.TYPE, ShowHudWarningPayload.STREAM_CODEC);
        //?}

        //? if >=1.20.5 {
        ModNetworking.registerS2C(OpenRailwaySpawnSetupPayload.TYPE, OpenRailwaySpawnSetupPayload.STREAM_CODEC);
        //?}

        //? if >=1.20.5 {
        ModNetworking.registerS2C(GttScorePayload.TYPE, GttScorePayload.STREAM_CODEC);
        //?}

        //? if >=1.20.5 {
        ModNetworking.registerS2C(SlotSpinResultPayload.TYPE, SlotSpinResultPayload.STREAM_CODEC);
        //?}

        //? if >=1.20.5 {
        ModNetworking.registerS2C(SyncRailwayFormationOriginPayload.TYPE, SyncRailwayFormationOriginPayload.STREAM_CODEC);
        //?}
        //? if >=1.20.5 {
        ModNetworking.registerC2S(SetCarColorPayload.TYPE, SetCarColorPayload.STREAM_CODEC, AiObjectModNetworking::handleSetCarColor);
        //?} else
        /*ModNetworking.registerC2S(SetCarColorPayload.ID, SetCarColorPayload::read, AiObjectModNetworking::handleSetCarColor);*/

        //? if >=1.20.5 {
        ModNetworking.registerC2S(SetCarPlateTexturePayload.TYPE, SetCarPlateTexturePayload.STREAM_CODEC, AiObjectModNetworking::handleSetCarPlateTexture);
        //?} else
        /*ModNetworking.registerC2S(SetCarPlateTexturePayload.ID, SetCarPlateTexturePayload::read, AiObjectModNetworking::handleSetCarPlateTexture);*/

        //? if >=1.20.5 {
        ModNetworking.registerC2S(SetCarPlateTransformPayload.TYPE, SetCarPlateTransformPayload.STREAM_CODEC, AiObjectModNetworking::handleSetCarPlateTransform);
        //?} else
        /*ModNetworking.registerC2S(SetCarPlateTransformPayload.ID, SetCarPlateTransformPayload::read, AiObjectModNetworking::handleSetCarPlateTransform);*/

        //? if >=1.20.5 {
        ModNetworking.registerC2S(SetCarTireSmokeOffsetPayload.TYPE, SetCarTireSmokeOffsetPayload.STREAM_CODEC, AiObjectModNetworking::handleSetCarTireSmokeOffset);
        //?} else
        /*ModNetworking.registerC2S(SetCarTireSmokeOffsetPayload.ID, SetCarTireSmokeOffsetPayload::read, AiObjectModNetworking::handleSetCarTireSmokeOffset);*/

        //? if >=1.20.5 {
        ModNetworking.registerC2S(SetCarTireSmokeParticlePayload.TYPE, SetCarTireSmokeParticlePayload.STREAM_CODEC, AiObjectModNetworking::handleSetCarTireSmokeParticle);
        //?} else
        /*ModNetworking.registerC2S(SetCarTireSmokeParticlePayload.ID, SetCarTireSmokeParticlePayload::read, AiObjectModNetworking::handleSetCarTireSmokeParticle);*/

        //? if >=1.20.5 {
        ModNetworking.registerC2S(SetCarTireSmokePreviewPayload.TYPE, SetCarTireSmokePreviewPayload.STREAM_CODEC, AiObjectModNetworking::handleSetCarTireSmokePreview);
        //?} else
        /*ModNetworking.registerC2S(SetCarTireSmokePreviewPayload.ID, SetCarTireSmokePreviewPayload::read, AiObjectModNetworking::handleSetCarTireSmokePreview);*/

        //? if >=1.20.5 {
        ModNetworking.registerC2S(SetDetectedWheelFootprintPayload.TYPE, SetDetectedWheelFootprintPayload.STREAM_CODEC, AiObjectModNetworking::handleSetDetectedWheelFootprint);
        //?} else
        /*ModNetworking.registerC2S(SetDetectedWheelFootprintPayload.ID, SetDetectedWheelFootprintPayload::read, AiObjectModNetworking::handleSetDetectedWheelFootprint);*/

        //? if >=1.20.5 {
        ModNetworking.registerC2S(SetGhostRecordingPayload.TYPE, SetGhostRecordingPayload.STREAM_CODEC, AiObjectModNetworking::handleSetGhostRecording);
        //?} else
        /*ModNetworking.registerC2S(SetGhostRecordingPayload.ID, SetGhostRecordingPayload::read, AiObjectModNetworking::handleSetGhostRecording);*/

        //? if >=1.20.5 {
        ModNetworking.registerC2S(PlayGhostLapPayload.TYPE, PlayGhostLapPayload.STREAM_CODEC, AiObjectModNetworking::handlePlayGhostLap);
        //?} else
        /*ModNetworking.registerC2S(PlayGhostLapPayload.ID, PlayGhostLapPayload::read, AiObjectModNetworking::handlePlayGhostLap);*/

        //? if >=1.20.5 {
        ModNetworking.registerC2S(DeleteGhostLapPayload.TYPE, DeleteGhostLapPayload.STREAM_CODEC, AiObjectModNetworking::handleDeleteGhostLap);
        //?} else
        /*ModNetworking.registerC2S(DeleteGhostLapPayload.ID, DeleteGhostLapPayload::read, AiObjectModNetworking::handleDeleteGhostLap);*/

        //? if >=1.20.5 {
        ModNetworking.registerC2S(SetCarDriveModePayload.TYPE, SetCarDriveModePayload.STREAM_CODEC, AiObjectModNetworking::handleSetCarDriveMode);
        //?} else
        /*ModNetworking.registerC2S(SetCarDriveModePayload.ID, SetCarDriveModePayload::read, AiObjectModNetworking::handleSetCarDriveMode);*/

        //? if >=1.20.5 {
        ModNetworking.registerC2S(SetCarItemIconPayload.TYPE, SetCarItemIconPayload.STREAM_CODEC, AiObjectModNetworking::handleSetCarItemIcon);
        //?} else
        /*ModNetworking.registerC2S(SetCarItemIconPayload.ID, SetCarItemIconPayload::read, AiObjectModNetworking::handleSetCarItemIcon);*/

        //? if >=1.20.5 {
        ModNetworking.registerC2S(SetChaseModePayload.TYPE, SetChaseModePayload.STREAM_CODEC, AiObjectModNetworking::handleSetChaseMode);
        //?} else
        /*ModNetworking.registerC2S(SetChaseModePayload.ID, SetChaseModePayload::read, AiObjectModNetworking::handleSetChaseMode);*/

        //? if >=1.20.5 {
        ModNetworking.registerC2S(SetChaosModePayload.TYPE, SetChaosModePayload.STREAM_CODEC, AiObjectModNetworking::handleSetChaosMode);
        //?} else
        /*ModNetworking.registerC2S(SetChaosModePayload.ID, SetChaosModePayload::read, AiObjectModNetworking::handleSetChaosMode);*/

        //? if >=1.20.5 {
        ModNetworking.registerC2S(SetTownTrafficEnabledPayload.TYPE, SetTownTrafficEnabledPayload.STREAM_CODEC, AiObjectModNetworking::handleSetTownTrafficEnabled);
        //?} else
        /*ModNetworking.registerC2S(SetTownTrafficEnabledPayload.ID, SetTownTrafficEnabledPayload::read, AiObjectModNetworking::handleSetTownTrafficEnabled);*/

        //? if >=1.20.5 {
        ModNetworking.registerC2S(SetCarCustomTuningPayload.TYPE, SetCarCustomTuningPayload.STREAM_CODEC, AiObjectModNetworking::handleSetCarCustomTuning);
        //?} else
        /*ModNetworking.registerC2S(SetCarCustomTuningPayload.ID, SetCarCustomTuningPayload::read, AiObjectModNetworking::handleSetCarCustomTuning);*/

        //? if >=1.20.5 {
        ModNetworking.registerC2S(SetCarEnginePayload.TYPE, SetCarEnginePayload.STREAM_CODEC, AiObjectModNetworking::handleSetCarEngine);
        //?} else
        /*ModNetworking.registerC2S(SetCarEnginePayload.ID, SetCarEnginePayload::read, AiObjectModNetworking::handleSetCarEngine);*/

        //? if >=1.20.5 {
        ModNetworking.registerC2S(SetCarHornSirenPayload.TYPE, SetCarHornSirenPayload.STREAM_CODEC, AiObjectModNetworking::handleSetCarHornSiren);
        //?} else
        /*ModNetworking.registerC2S(SetCarHornSirenPayload.ID, SetCarHornSirenPayload::read, AiObjectModNetworking::handleSetCarHornSiren);*/

        //? if >=1.20.5 {
        ModNetworking.registerC2S(SetCarBombPayload.TYPE, SetCarBombPayload.STREAM_CODEC, AiObjectModNetworking::handleSetCarBomb);
        //?} else
        /*ModNetworking.registerC2S(SetCarBombPayload.ID, SetCarBombPayload::read, AiObjectModNetworking::handleSetCarBomb);*/

        //? if >=1.20.5 {
        ModNetworking.registerC2S(DetonateBombsPayload.TYPE, DetonateBombsPayload.STREAM_CODEC, AiObjectModNetworking::handleDetonateBombs);
        //?} else
        /*ModNetworking.registerC2S(DetonateBombsPayload.ID, DetonateBombsPayload::read, AiObjectModNetworking::handleDetonateBombs);*/

        //? if >=1.20.5 {
        ModNetworking.registerC2S(SetRailwayConsistPayload.TYPE, SetRailwayConsistPayload.STREAM_CODEC, AiObjectModNetworking::handleSetRailwayConsist);
        //?} else
        /*ModNetworking.registerC2S(SetRailwayConsistPayload.ID, SetRailwayConsistPayload::read, AiObjectModNetworking::handleSetRailwayConsist);*/

        //? if >=1.20.5 {
        ModNetworking.registerC2S(SetCarWheelAdjustPayload.TYPE, SetCarWheelAdjustPayload.STREAM_CODEC, AiObjectModNetworking::handleSetCarWheelAdjust);
        //?} else
        /*ModNetworking.registerC2S(SetCarWheelAdjustPayload.ID, SetCarWheelAdjustPayload::read, AiObjectModNetworking::handleSetCarWheelAdjust);*/

        //? if >=1.20.5 {
        ModNetworking.registerC2S(SetCarWheelColorPayload.TYPE, SetCarWheelColorPayload.STREAM_CODEC, AiObjectModNetworking::handleSetCarWheelColor);
        //?} else
        /*ModNetworking.registerC2S(SetCarWheelColorPayload.ID, SetCarWheelColorPayload::read, AiObjectModNetworking::handleSetCarWheelColor);*/

        //? if >=1.20.5 {
        ModNetworking.registerC2S(SetCarWheelWidthPayload.TYPE, SetCarWheelWidthPayload.STREAM_CODEC, AiObjectModNetworking::handleSetCarWheelWidth);
        //?} else
        /*ModNetworking.registerC2S(SetCarWheelWidthPayload.ID, SetCarWheelWidthPayload::read, AiObjectModNetworking::handleSetCarWheelWidth);*/

        //? if >=1.20.5 {
        ModNetworking.registerC2S(SetCarExtraRearAxlePayload.TYPE, SetCarExtraRearAxlePayload.STREAM_CODEC, AiObjectModNetworking::handleSetCarExtraRearAxle);
        //?} else
        /*ModNetworking.registerC2S(SetCarExtraRearAxlePayload.ID, SetCarExtraRearAxlePayload::read, AiObjectModNetworking::handleSetCarExtraRearAxle);*/

        //? if >=1.20.5 {
        ModNetworking.registerC2S(SetCarLipAdjustPayload.TYPE, SetCarLipAdjustPayload.STREAM_CODEC, AiObjectModNetworking::handleSetCarLipAdjust);
        //?} else
        /*ModNetworking.registerC2S(SetCarLipAdjustPayload.ID, SetCarLipAdjustPayload::read, AiObjectModNetworking::handleSetCarLipAdjust);*/

        //? if >=1.20.5 {
        ModNetworking.registerC2S(SetCarLimoAdjustPayload.TYPE, SetCarLimoAdjustPayload.STREAM_CODEC, AiObjectModNetworking::handleSetCarLimoAdjust);
        //?} else
        /*ModNetworking.registerC2S(SetCarLimoAdjustPayload.ID, SetCarLimoAdjustPayload::read, AiObjectModNetworking::handleSetCarLimoAdjust);*/

        //? if >=1.20.5 {
        ModNetworking.registerC2S(SetCarWidthAdjustPayload.TYPE, SetCarWidthAdjustPayload.STREAM_CODEC, AiObjectModNetworking::handleSetCarWidthAdjust);
        //?} else
        /*ModNetworking.registerC2S(SetCarWidthAdjustPayload.ID, SetCarWidthAdjustPayload::read, AiObjectModNetworking::handleSetCarWidthAdjust);*/

        //? if >=1.20.5 {
        ModNetworking.registerC2S(SetCarTiltPayload.TYPE, SetCarTiltPayload.STREAM_CODEC, AiObjectModNetworking::handleSetCarTilt);
        //?} else
        /*ModNetworking.registerC2S(SetCarTiltPayload.ID, SetCarTiltPayload::read, AiObjectModNetworking::handleSetCarTilt);*/

        //? if >=1.20.5 {
        ModNetworking.registerC2S(SetCarPassengerPositionAdjustPayload.TYPE, SetCarPassengerPositionAdjustPayload.STREAM_CODEC, AiObjectModNetworking::handleSetCarPassengerPositionAdjust);
        //?} else
        /*ModNetworking.registerC2S(SetCarPassengerPositionAdjustPayload.ID, SetCarPassengerPositionAdjustPayload::read, AiObjectModNetworking::handleSetCarPassengerPositionAdjust);*/

        //? if >=1.20.5 {
        ModNetworking.registerC2S(SetCarRearWingPayload.TYPE, SetCarRearWingPayload.STREAM_CODEC, AiObjectModNetworking::handleSetCarRearWing);
        //?} else
        /*ModNetworking.registerC2S(SetCarRearWingPayload.ID, SetCarRearWingPayload::read, AiObjectModNetworking::handleSetCarRearWing);*/

        //? if >=1.20.5 {
        ModNetworking.registerC2S(SetCarPoliceLightPayload.TYPE, SetCarPoliceLightPayload.STREAM_CODEC, AiObjectModNetworking::handleSetCarPoliceLight);
        //?} else
        /*ModNetworking.registerC2S(SetCarPoliceLightPayload.ID, SetCarPoliceLightPayload::read, AiObjectModNetworking::handleSetCarPoliceLight);*/

        //? if >=1.20.5 {
        ModNetworking.registerC2S(SetCarTaxiSignPayload.TYPE, SetCarTaxiSignPayload.STREAM_CODEC, AiObjectModNetworking::handleSetCarTaxiSign);
        //?} else
        /*ModNetworking.registerC2S(SetCarTaxiSignPayload.ID, SetCarTaxiSignPayload::read, AiObjectModNetworking::handleSetCarTaxiSign);*/

        //? if >=1.20.5 {
        ModNetworking.registerC2S(SetPoliceLightsOnPayload.TYPE, SetPoliceLightsOnPayload.STREAM_CODEC, AiObjectModNetworking::handleSetPoliceLightsOn);
        //?} else
        /*ModNetworking.registerC2S(SetPoliceLightsOnPayload.ID, SetPoliceLightsOnPayload::read, AiObjectModNetworking::handleSetPoliceLightsOn);*/

        //? if >=1.20.5 {
        ModNetworking.registerC2S(SetCarOtherPartPayload.TYPE, SetCarOtherPartPayload.STREAM_CODEC, AiObjectModNetworking::handleSetCarOtherPart);
        //?} else
        /*ModNetworking.registerC2S(SetCarOtherPartPayload.ID, SetCarOtherPartPayload::read, AiObjectModNetworking::handleSetCarOtherPart);*/

        //? if >=1.20.5 {
        ModNetworking.registerC2S(SetCarTextPartPayload.TYPE, SetCarTextPartPayload.STREAM_CODEC, AiObjectModNetworking::handleSetCarTextPart);
        //?} else
        /*ModNetworking.registerC2S(SetCarTextPartPayload.ID, SetCarTextPartPayload::read, AiObjectModNetworking::handleSetCarTextPart);*/

        //? if >=1.20.5 {
        ModNetworking.registerC2S(SetCarUnderNeonPayload.TYPE, SetCarUnderNeonPayload.STREAM_CODEC, AiObjectModNetworking::handleSetCarUnderNeon);
        //?} else
        /*ModNetworking.registerC2S(SetCarUnderNeonPayload.ID, SetCarUnderNeonPayload::read, AiObjectModNetworking::handleSetCarUnderNeon);*/

        //? if >=1.20.5 {
        ModNetworking.registerC2S(SetCarBrandLogoPayload.TYPE, SetCarBrandLogoPayload.STREAM_CODEC, AiObjectModNetworking::handleSetCarBrandLogo);
        //?} else
        /*ModNetworking.registerC2S(SetCarBrandLogoPayload.ID, SetCarBrandLogoPayload::read, AiObjectModNetworking::handleSetCarBrandLogo);*/

        //? if >=1.20.5 {
        ModNetworking.registerC2S(RequestAppCarSpawnPayload.TYPE, RequestAppCarSpawnPayload.STREAM_CODEC, AiObjectModNetworking::handleRequestAppCarSpawn);
        //?} else
        /*ModNetworking.registerC2S(RequestAppCarSpawnPayload.ID, RequestAppCarSpawnPayload::read, AiObjectModNetworking::handleRequestAppCarSpawn);*/

        //? if >=1.20.5 {
        ModNetworking.registerC2S(RequestAppItemPurchasePayload.TYPE, RequestAppItemPurchasePayload.STREAM_CODEC, AiObjectModNetworking::handleRequestAppItemPurchase);
        //?} else
        /*ModNetworking.registerC2S(RequestAppItemPurchasePayload.ID, RequestAppItemPurchasePayload::read, AiObjectModNetworking::handleRequestAppItemPurchase);*/

        //? if >=1.20.5 {
        ModNetworking.registerC2S(BuyUsedCarPayload.TYPE, BuyUsedCarPayload.STREAM_CODEC, AiObjectModNetworking::handleBuyUsedCar);
        //?} else
        /*ModNetworking.registerC2S(BuyUsedCarPayload.ID, BuyUsedCarPayload::read, AiObjectModNetworking::handleBuyUsedCar);*/

        //? if >=1.20.5 {
        ModNetworking.registerC2S(SetAppWeatherPayload.TYPE, SetAppWeatherPayload.STREAM_CODEC, AiObjectModNetworking::handleSetAppWeather);
        //?} else
        /*ModNetworking.registerC2S(SetAppWeatherPayload.ID, SetAppWeatherPayload::read, AiObjectModNetworking::handleSetAppWeather);*/

        //? if >=1.20.5 {
        ModNetworking.registerC2S(SetAppTimePayload.TYPE, SetAppTimePayload.STREAM_CODEC, AiObjectModNetworking::handleSetAppTime);
        //?} else
        /*ModNetworking.registerC2S(SetAppTimePayload.ID, SetAppTimePayload::read, AiObjectModNetworking::handleSetAppTime);*/

        //? if >=1.20.5 {
        ModNetworking.registerC2S(SetPhoneStylePayload.TYPE, SetPhoneStylePayload.STREAM_CODEC, AiObjectModNetworking::handleSetPhoneStyle);
        //?} else
        /*ModNetworking.registerC2S(SetPhoneStylePayload.ID, SetPhoneStylePayload::read, AiObjectModNetworking::handleSetPhoneStyle);*/

        //? if >=1.20.5 {
        ModNetworking.registerC2S(SetGttPayload.TYPE, SetGttPayload.STREAM_CODEC, AiObjectModNetworking::handleSetGtt);
        //?} else
        /*ModNetworking.registerC2S(SetGttPayload.ID, SetGttPayload::read, AiObjectModNetworking::handleSetGtt);*/

        //? if >=1.20.5 {
        ModNetworking.registerC2S(RequestEmergencyCallPayload.TYPE, RequestEmergencyCallPayload.STREAM_CODEC, AiObjectModNetworking::handleRequestEmergencyCall);
        //?} else
        /*ModNetworking.registerC2S(RequestEmergencyCallPayload.ID, RequestEmergencyCallPayload::read, AiObjectModNetworking::handleRequestEmergencyCall);*/

        //? if >=1.20.5 {
        ModNetworking.registerC2S(RequestSlotSpinPayload.TYPE, RequestSlotSpinPayload.STREAM_CODEC, AiObjectModNetworking::handleRequestSlotSpin);
        //?} else
        /*ModNetworking.registerC2S(RequestSlotSpinPayload.ID, RequestSlotSpinPayload::read, AiObjectModNetworking::handleRequestSlotSpin);*/

        //? if >=1.20.5 {
        ModNetworking.registerC2S(OpenMassiveAutoAppraisalPayload.TYPE, OpenMassiveAutoAppraisalPayload.STREAM_CODEC, AiObjectModNetworking::handleOpenMassiveAutoAppraisal);
        //?} else
        /*ModNetworking.registerC2S(OpenMassiveAutoAppraisalPayload.ID, OpenMassiveAutoAppraisalPayload::read, AiObjectModNetworking::handleOpenMassiveAutoAppraisal);*/

        //? if >=1.20.5 {
        ModNetworking.registerC2S(SetCarFrontBrakeCaliperPayload.TYPE, SetCarFrontBrakeCaliperPayload.STREAM_CODEC, AiObjectModNetworking::handleSetCarFrontBrakeCaliper);
        //?} else
        /*ModNetworking.registerC2S(SetCarFrontBrakeCaliperPayload.ID, SetCarFrontBrakeCaliperPayload::read, AiObjectModNetworking::handleSetCarFrontBrakeCaliper);*/

        //? if >=1.20.5 {
        ModNetworking.registerC2S(SetCarExhaustPayload.TYPE, SetCarExhaustPayload.STREAM_CODEC, AiObjectModNetworking::handleSetCarExhaust);
        //?} else
        /*ModNetworking.registerC2S(SetCarExhaustPayload.ID, SetCarExhaustPayload::read, AiObjectModNetworking::handleSetCarExhaust);*/

        //? if >=1.20.5 {
        ModNetworking.registerC2S(SetCarRearBrakeCaliperPayload.TYPE, SetCarRearBrakeCaliperPayload.STREAM_CODEC, AiObjectModNetworking::handleSetCarRearBrakeCaliper);
        //?} else
        /*ModNetworking.registerC2S(SetCarRearBrakeCaliperPayload.ID, SetCarRearBrakeCaliperPayload::read, AiObjectModNetworking::handleSetCarRearBrakeCaliper);*/

        //? if >=1.20.5 {
        ModNetworking.registerC2S(SetCarLiveryPayload.TYPE, SetCarLiveryPayload.STREAM_CODEC, AiObjectModNetworking::handleSetCarLivery);
        //?} else
        /*ModNetworking.registerC2S(SetCarLiveryPayload.ID, SetCarLiveryPayload::read, AiObjectModNetworking::handleSetCarLivery);*/

        //? if >=1.20.5 {
        ModNetworking.registerC2S(SetCarLightPayload.TYPE, SetCarLightPayload.STREAM_CODEC, AiObjectModNetworking::handleSetCarLight);
        //?} else
        /*ModNetworking.registerC2S(SetCarLightPayload.ID, SetCarLightPayload::read, AiObjectModNetworking::handleSetCarLight);*/

        //? if >=1.20.5 {
        ModNetworking.registerC2S(SetCarLightBlinkPayload.TYPE, SetCarLightBlinkPayload.STREAM_CODEC, AiObjectModNetworking::handleSetCarLightBlink);
        //?} else
        /*ModNetworking.registerC2S(SetCarLightBlinkPayload.ID, SetCarLightBlinkPayload::read, AiObjectModNetworking::handleSetCarLightBlink);*/

        //? if >=1.20.5 {
        ModNetworking.registerC2S(SetHitboxDisabledPayload.TYPE, SetHitboxDisabledPayload.STREAM_CODEC,
                (payload, player) -> CarEntity.setHitboxDisabled(payload.disabled()));
        //?} else
        /*ModNetworking.registerC2S(SetHitboxDisabledPayload.ID, SetHitboxDisabledPayload::read,
                (payload, player) -> CarEntity.setHitboxDisabled(payload.disabled()));*/

        //? if >=1.20.5 {
        ModNetworking.registerC2S(SetBreakVanillaBlocksPayload.TYPE, SetBreakVanillaBlocksPayload.STREAM_CODEC,
                (payload, player) -> CarEntity.setBreakVanillaBlocksEnabled(payload.enabled()));
        //?} else
        /*ModNetworking.registerC2S(SetBreakVanillaBlocksPayload.ID, SetBreakVanillaBlocksPayload::read,
                (payload, player) -> CarEntity.setBreakVanillaBlocksEnabled(payload.enabled()));*/

        //? if >=1.20.5 {
        ModNetworking.registerC2S(SetMobDamagePayload.TYPE, SetMobDamagePayload.STREAM_CODEC,
                (payload, player) -> CarEntity.setMobDamageEnabled(payload.enabled()));
        //?} else
        /*ModNetworking.registerC2S(SetMobDamagePayload.ID, SetMobDamagePayload::read,
                (payload, player) -> CarEntity.setMobDamageEnabled(payload.enabled()));*/

        //? if >=1.20.5 {
        ModNetworking.registerC2S(SetSecurityAlarmPayload.TYPE, SetSecurityAlarmPayload.STREAM_CODEC,
                (payload, player) -> CarEntity.setSecurityAlarmEnabled(payload.enabled()));
        //?} else
        /*ModNetworking.registerC2S(SetSecurityAlarmPayload.ID, SetSecurityAlarmPayload::read,
                (payload, player) -> CarEntity.setSecurityAlarmEnabled(payload.enabled()));*/

        //? if >=1.20.5 {
        ModNetworking.registerC2S(SetForceRotatePayload.TYPE, SetForceRotatePayload.STREAM_CODEC, AiObjectModNetworking::handleSetForceRotate);
        //?} else
        /*ModNetworking.registerC2S(SetForceRotatePayload.ID, SetForceRotatePayload::read, AiObjectModNetworking::handleSetForceRotate);*/

        //? if >=1.20.5 {
        ModNetworking.registerC2S(SetActionMultipliersPayload.TYPE, SetActionMultipliersPayload.STREAM_CODEC, AiObjectModNetworking::handleSetActionMultipliers);
        //?} else
        /*ModNetworking.registerC2S(SetActionMultipliersPayload.ID, SetActionMultipliersPayload::read, AiObjectModNetworking::handleSetActionMultipliers);*/

        //? if >=1.20.5 {
        ModNetworking.registerC2S(SetSparkPositionOffsetPayload.TYPE, SetSparkPositionOffsetPayload.STREAM_CODEC,
                (payload, player) -> CarEntity.setSparkPositionOffsetEnabled(payload.enabled()));
        //?} else
        /*ModNetworking.registerC2S(SetSparkPositionOffsetPayload.ID, SetSparkPositionOffsetPayload::read,
                (payload, player) -> CarEntity.setSparkPositionOffsetEnabled(payload.enabled()));*/

        //? if >=1.20.5 {
        ModNetworking.registerC2S(SetCarScalePayload.TYPE, SetCarScalePayload.STREAM_CODEC, AiObjectModNetworking::handleSetCarScale);
        //?} else
        /*ModNetworking.registerC2S(SetCarScalePayload.ID, SetCarScalePayload::read, AiObjectModNetworking::handleSetCarScale);*/

        //? if >=1.20.5 {
        ModNetworking.registerC2S(SetCarCameraPayload.TYPE, SetCarCameraPayload.STREAM_CODEC, AiObjectModNetworking::handleSetCarCamera);
        //?} else
        /*ModNetworking.registerC2S(SetCarCameraPayload.ID, SetCarCameraPayload::read, AiObjectModNetworking::handleSetCarCamera);*/

        //? if >=1.20.5 {
        ModNetworking.registerC2S(MountForCameraAdjustPayload.TYPE, MountForCameraAdjustPayload.STREAM_CODEC, AiObjectModNetworking::handleMountForCameraAdjust);
        //?} else
        /*ModNetworking.registerC2S(MountForCameraAdjustPayload.ID, MountForCameraAdjustPayload::read, AiObjectModNetworking::handleMountForCameraAdjust);*/

        //? if >=1.20.5 {
        ModNetworking.registerC2S(SetHornStatePayload.TYPE, SetHornStatePayload.STREAM_CODEC, AiObjectModNetworking::handleSetHornState);
        //?} else
        /*ModNetworking.registerC2S(SetHornStatePayload.ID, SetHornStatePayload::read, AiObjectModNetworking::handleSetHornState);*/

        //? if >=1.20.5 {
        ModNetworking.registerC2S(ToggleTrailerCouplingPayload.TYPE, ToggleTrailerCouplingPayload.STREAM_CODEC, AiObjectModNetworking::handleToggleTrailerCoupling);
        //?} else
        /*ModNetworking.registerC2S(ToggleTrailerCouplingPayload.ID, ToggleTrailerCouplingPayload::read, AiObjectModNetworking::handleToggleTrailerCoupling);*/

        //? if >=1.20.5 {
        ModNetworking.registerC2S(SetClientSlipReportPayload.TYPE, SetClientSlipReportPayload.STREAM_CODEC, AiObjectModNetworking::handleSetClientSlipReport);
        //?} else
        /*ModNetworking.registerC2S(SetClientSlipReportPayload.ID, SetClientSlipReportPayload::read, AiObjectModNetworking::handleSetClientSlipReport);*/

        //? if >=1.20.5 {
        ModNetworking.registerC2S(SetClientWheelieTriggerPayload.TYPE, SetClientWheelieTriggerPayload.STREAM_CODEC, AiObjectModNetworking::handleSetClientWheelieTrigger);
        //?} else
        /*ModNetworking.registerC2S(SetClientWheelieTriggerPayload.ID, SetClientWheelieTriggerPayload::read, AiObjectModNetworking::handleSetClientWheelieTrigger);*/

        //? if >=1.20.5 {
        ModNetworking.registerC2S(BoxCarPayload.TYPE, BoxCarPayload.STREAM_CODEC, AiObjectModNetworking::handleBoxCar);
        //?} else
        /*ModNetworking.registerC2S(BoxCarPayload.ID, BoxCarPayload::read, AiObjectModNetworking::handleBoxCar);*/

        //? if >=1.20.5 {
        ModNetworking.registerC2S(SetWaypointPenSpeedPayload.TYPE, SetWaypointPenSpeedPayload.STREAM_CODEC, AiObjectModNetworking::handleSetWaypointPenSpeed);
        //?} else
        /*ModNetworking.registerC2S(SetWaypointPenSpeedPayload.ID, SetWaypointPenSpeedPayload::read, AiObjectModNetworking::handleSetWaypointPenSpeed);*/

        //? if >=1.20.5 {
        ModNetworking.registerC2S(SetTownTrafficDisabledVehiclesPayload.TYPE, SetTownTrafficDisabledVehiclesPayload.STREAM_CODEC, AiObjectModNetworking::handleSetTownTrafficDisabledVehicles);
        //?} else
        /*ModNetworking.registerC2S(SetTownTrafficDisabledVehiclesPayload.ID, SetTownTrafficDisabledVehiclesPayload::read, AiObjectModNetworking::handleSetTownTrafficDisabledVehicles);*/

        //? if >=1.20.5 {
        ModNetworking.registerC2S(AddCustomTrafficCarPayload.TYPE, AddCustomTrafficCarPayload.STREAM_CODEC, AiObjectModNetworking::handleAddCustomTrafficCar);
        //?} else
        /*ModNetworking.registerC2S(AddCustomTrafficCarPayload.ID, AddCustomTrafficCarPayload::read, AiObjectModNetworking::handleAddCustomTrafficCar);*/

        //? if >=1.20.5 {
        ModNetworking.registerC2S(UpdateCustomTrafficCarPayload.TYPE, UpdateCustomTrafficCarPayload.STREAM_CODEC, AiObjectModNetworking::handleUpdateCustomTrafficCar);
        //?} else
        /*ModNetworking.registerC2S(UpdateCustomTrafficCarPayload.ID, UpdateCustomTrafficCarPayload::read, AiObjectModNetworking::handleUpdateCustomTrafficCar);*/

        //? if >=1.20.5 {
        ModNetworking.registerC2S(SetTownTrafficPlateIdsPayload.TYPE, SetTownTrafficPlateIdsPayload.STREAM_CODEC, AiObjectModNetworking::handleSetTownTrafficPlateIds);
        //?} else
        /*ModNetworking.registerC2S(SetTownTrafficPlateIdsPayload.ID, SetTownTrafficPlateIdsPayload::read, AiObjectModNetworking::handleSetTownTrafficPlateIds);*/

        //? if >=1.20.5 {
        ModNetworking.registerC2S(SetTownTrafficMovementModePayload.TYPE, SetTownTrafficMovementModePayload.STREAM_CODEC, AiObjectModNetworking::handleSetTownTrafficMovementMode);
        //?} else
        /*ModNetworking.registerC2S(SetTownTrafficMovementModePayload.ID, SetTownTrafficMovementModePayload::read, AiObjectModNetworking::handleSetTownTrafficMovementMode);*/

        //? if >=1.20.5 {
        ModNetworking.registerC2S(SetTownMaxSpawnedCarsPayload.TYPE, SetTownMaxSpawnedCarsPayload.STREAM_CODEC, AiObjectModNetworking::handleSetTownMaxSpawnedCars);
        //?} else
        /*ModNetworking.registerC2S(SetTownMaxSpawnedCarsPayload.ID, SetTownMaxSpawnedCarsPayload::read, AiObjectModNetworking::handleSetTownMaxSpawnedCars);*/

        //? if >=1.20.5 {
        ModNetworking.registerC2S(SetTownAiThrottlePayload.TYPE, SetTownAiThrottlePayload.STREAM_CODEC, AiObjectModNetworking::handleSetTownAiThrottle);
        //?} else
        /*ModNetworking.registerC2S(SetTownAiThrottlePayload.ID, SetTownAiThrottlePayload::read, AiObjectModNetworking::handleSetTownAiThrottle);*/

        //? if >=1.20.5 {
        ModNetworking.registerC2S(SetTownSpawnIntervalPayload.TYPE, SetTownSpawnIntervalPayload.STREAM_CODEC, AiObjectModNetworking::handleSetTownSpawnInterval);
        //?} else
        /*ModNetworking.registerC2S(SetTownSpawnIntervalPayload.ID, SetTownSpawnIntervalPayload::read, AiObjectModNetworking::handleSetTownSpawnInterval);*/

        //? if >=1.20.5 {
        ModNetworking.registerC2S(SetTownRecallDistantCarsPayload.TYPE, SetTownRecallDistantCarsPayload.STREAM_CODEC, AiObjectModNetworking::handleSetTownRecallDistantCars);
        //?} else
        /*ModNetworking.registerC2S(SetTownRecallDistantCarsPayload.ID, SetTownRecallDistantCarsPayload::read, AiObjectModNetworking::handleSetTownRecallDistantCars);*/

        //? if >=1.20.5 {
        ModNetworking.registerC2S(SetTownNightLightsPayload.TYPE, SetTownNightLightsPayload.STREAM_CODEC, AiObjectModNetworking::handleSetTownNightLights);
        //?} else
        /*ModNetworking.registerC2S(SetTownNightLightsPayload.ID, SetTownNightLightsPayload::read, AiObjectModNetworking::handleSetTownNightLights);*/

        //? if >=1.20.5 {
        ModNetworking.registerC2S(SetOneWayReversedPayload.TYPE, SetOneWayReversedPayload.STREAM_CODEC, AiObjectModNetworking::handleSetOneWayReversed);
        //?} else
        /*ModNetworking.registerC2S(SetOneWayReversedPayload.ID, SetOneWayReversedPayload::read, AiObjectModNetworking::handleSetOneWayReversed);*/

        //? if >=1.20.5 {
        ModNetworking.registerC2S(SetSignalNamePayload.TYPE, SetSignalNamePayload.STREAM_CODEC, AiObjectModNetworking::handleSetSignalName);
        //?} else
        /*ModNetworking.registerC2S(SetSignalNamePayload.ID, SetSignalNamePayload::read, AiObjectModNetworking::handleSetSignalName);*/

        //? if >=1.20.5 {
        ModNetworking.registerC2S(SetSignalDirectionPayload.TYPE, SetSignalDirectionPayload.STREAM_CODEC, AiObjectModNetworking::handleSetSignalDirection);
        //?} else
        /*ModNetworking.registerC2S(SetSignalDirectionPayload.ID, SetSignalDirectionPayload::read, AiObjectModNetworking::handleSetSignalDirection);*/

        //? if >=1.20.5 {
        ModNetworking.registerC2S(SetSignalTimingPayload.TYPE, SetSignalTimingPayload.STREAM_CODEC, AiObjectModNetworking::handleSetSignalTiming);
        //?} else
        /*ModNetworking.registerC2S(SetSignalTimingPayload.ID, SetSignalTimingPayload::read, AiObjectModNetworking::handleSetSignalTiming);*/

        //? if >=1.20.5 {
        ModNetworking.registerC2S(SetAmbientEngineVolumePayload.TYPE, SetAmbientEngineVolumePayload.STREAM_CODEC, AiObjectModNetworking::handleSetAmbientEngineVolume);
        //?} else
        /*ModNetworking.registerC2S(SetAmbientEngineVolumePayload.ID, SetAmbientEngineVolumePayload::read, AiObjectModNetworking::handleSetAmbientEngineVolume);*/

        //? if >=1.16 && <1.16.2 {
        /*final Set<UUID> stonecutter1161GreetedPlayers = new HashSet<>();
        net.fabricmc.fabric.api.event.server.ServerTickCallback.EVENT.register(server -> {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                if (stonecutter1161GreetedPlayers.add(player.getUUID())) {
                    WaypointNetworkSavedData data = WaypointNetworkSavedData.get(serverLevel(player));
                    SyncAmbientEngineVolumePayload syncPayload = SyncAmbientEngineVolumePayload.of(data);
                    ModNetworking.sendToClient(player, SyncAmbientEngineVolumePayload.ID, syncPayload, SyncAmbientEngineVolumePayload::write);
                }
            }
        });
        *///?}
        //? if <1.16 {
        /*net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayer player = handler.player;
            WaypointNetworkSavedData data = WaypointNetworkSavedData.get(serverLevel(player));
            SyncAmbientEngineVolumePayload syncPayload = SyncAmbientEngineVolumePayload.of(data);
            ModNetworking.sendToClient(player, SyncAmbientEngineVolumePayload.ID, syncPayload, SyncAmbientEngineVolumePayload::write);
        });
        *///?}
        //? if >=1.16.2 && <1.20.5 {
        /*net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayer player = handler.player;
            WaypointNetworkSavedData data = WaypointNetworkSavedData.get(serverLevel(player));
            SyncAmbientEngineVolumePayload syncPayload = SyncAmbientEngineVolumePayload.of(data);
            ModNetworking.sendToClient(player, SyncAmbientEngineVolumePayload.ID, syncPayload, SyncAmbientEngineVolumePayload::write);
        });
        *///?}
        //? if >=1.20.5 {
        net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayer player = handler.player;
            WaypointNetworkSavedData data = WaypointNetworkSavedData.get(serverLevel(player));
            SyncAmbientEngineVolumePayload syncPayload = SyncAmbientEngineVolumePayload.of(data);
            ModNetworking.sendToClient(player, syncPayload);
        });
        //?}

        //? if >=1.20.5 {
        ModNetworking.registerC2S(SetWheelRotationReportPayload.TYPE, SetWheelRotationReportPayload.STREAM_CODEC, AiObjectModNetworking::handleSetWheelRotationReport);
        //?} else
        /*ModNetworking.registerC2S(SetWheelRotationReportPayload.ID, SetWheelRotationReportPayload::read, AiObjectModNetworking::handleSetWheelRotationReport);*/

        //? if >=1.20.5 {
        ModNetworking.registerC2S(SetRailwayRouteVehiclePayload.TYPE, SetRailwayRouteVehiclePayload.STREAM_CODEC, AiObjectModNetworking::handleSetRailwayRouteVehicle);
        //?} else
        /*ModNetworking.registerC2S(SetRailwayRouteVehiclePayload.ID, SetRailwayRouteVehiclePayload::read, AiObjectModNetworking::handleSetRailwayRouteVehicle);*/

        //? if >=1.20.5 {
        ModNetworking.registerC2S(OpenRailwayFormationEditorPayload.TYPE, OpenRailwayFormationEditorPayload.STREAM_CODEC, AiObjectModNetworking::handleOpenRailwayFormationEditor);
        //?} else
        /*ModNetworking.registerC2S(OpenRailwayFormationEditorPayload.ID, OpenRailwayFormationEditorPayload::read, AiObjectModNetworking::handleOpenRailwayFormationEditor);*/

        //? if >=1.20.5 {
        ModNetworking.registerC2S(SetRailwayRouteCustomFormationPayload.TYPE, SetRailwayRouteCustomFormationPayload.STREAM_CODEC, AiObjectModNetworking::handleSetRailwayRouteCustomFormation);
        //?} else
        /*ModNetworking.registerC2S(SetRailwayRouteCustomFormationPayload.ID, SetRailwayRouteCustomFormationPayload::read, AiObjectModNetworking::handleSetRailwayRouteCustomFormation);*/
    }

    private static void applyToEditingSignal(WaypointNetworkSavedData data, UUID playerId, WaypointNetworkSavedData.PendingSignalPen updated) {
        Integer editingSignalId = data.getEditingSignalId(playerId);
        if (editingSignalId != null) {
            data.applyPenToExistingSignal(editingSignalId, updated);
        }
    }

    private static void handleBoxCar(BoxCarPayload payload, ServerPlayer player) {
        Entity entity = serverLevel(player).getEntity(payload.entityId());
        if (!(entity instanceof CarEntity car) || car.isGhost()) {
            return;
        }

        ItemStack stack = com.examplebett.item.VehicleItemIcons.createBoxedStack(car, payload.itemName().trim());

        if (!com.examplebett.compat.ModPlayer.getInventory(player).add(stack)) {
            player.drop(stack, false);
        }

        car.discardRailwayCoupledCars();

        car.discardCoupledTrailer();

        car.discard();
    }

    private static void handleSetRailwayConsist(SetRailwayConsistPayload payload, ServerPlayer player) {
        Entity entity = serverLevel(player).getEntity(payload.headEntityId());
        if (!(entity instanceof CarEntity car) || !car.hasVehicleCategory(VehicleCategory.RAILWAY)) {
            return;
        }
        java.util.List<String> middleModelIds = new java.util.ArrayList<>();
        if (!payload.middleModelIdsCsv().isEmpty()) {
            for (String modelId : payload.middleModelIdsCsv().split(",")) {
                if (!modelId.isEmpty()) {
                    middleModelIds.add(modelId);
                }
            }
        }
        car.applyRailwayConsist(middleModelIds, payload.tailModelId());
    }

    private static void handleSetRailwayRouteVehicle(SetRailwayRouteVehiclePayload payload, ServerPlayer player) {
        if (payload.modelId().isEmpty()) {
            return;
        }
        com.examplebett.waypoint.RailwayNetworkSavedData data = com.examplebett.waypoint.RailwayNetworkSavedData.get(serverLevel(player));
        if (!data.contains(payload.originNodeId())) {
            return;
        }
        data.setRouteConfig(new com.examplebett.waypoint.RailwayNetworkSavedData.RailwayRouteEntry(
                payload.originNodeId(),
                com.examplebett.waypoint.RailwayNetworkSavedData.RailwayRouteEntry.MODE_SINGLE,
                payload.modelId(),
                new CompoundTag()));
        ModPlayer.displayActionBar(player, ModText.translatable("gui.mineturismo.railway_spawn.vehicle_set"));
    }

    private static void handleOpenRailwayFormationEditor(OpenRailwayFormationEditorPayload payload, ServerPlayer player) {
        com.examplebett.waypoint.RailwayNetworkSavedData data = com.examplebett.waypoint.RailwayNetworkSavedData.get(serverLevel(player));
        if (!data.contains(payload.originNodeId())) {
            return;
        }
        SyncRailwayFormationOriginPayload syncPayload = new SyncRailwayFormationOriginPayload(payload.originNodeId());
        //? if >=1.20.5 {
        ModNetworking.sendToClient(player, syncPayload);
        //?} else
        /*ModNetworking.sendToClient(player, SyncRailwayFormationOriginPayload.ID, syncPayload, SyncRailwayFormationOriginPayload::write);*/
        player.openMenu(new net.minecraft.world.SimpleMenuProvider(
                (syncId, inventory, menuPlayer) -> new com.examplebett.inventory.RailwayFormationMenu(syncId, inventory, payload.originNodeId()),
                ModText.translatable("gui.mineturismo.railway_formation.title")));
    }

    private static void handleSetRailwayRouteCustomFormation(SetRailwayRouteCustomFormationPayload payload, ServerPlayer player) {
        if (payload.formationTag().isEmpty()) {
            return;
        }
        com.examplebett.waypoint.RailwayNetworkSavedData data = com.examplebett.waypoint.RailwayNetworkSavedData.get(serverLevel(player));
        if (!data.contains(payload.originNodeId())) {
            return;
        }
        data.setRouteConfig(new com.examplebett.waypoint.RailwayNetworkSavedData.RailwayRouteEntry(
                payload.originNodeId(),
                com.examplebett.waypoint.RailwayNetworkSavedData.RailwayRouteEntry.MODE_CUSTOM,
                "",
                payload.formationTag()));
        ModPlayer.displayActionBar(player, ModText.translatable("gui.mineturismo.railway_spawn.vehicle_set"));
    }

    private static void handleSetCarColor(SetCarColorPayload payload, ServerPlayer player) {
                    Entity entity = serverLevel(player).getEntity(payload.entityId());
            if (entity instanceof CarEntity car) {
                car.setColor(payload.color());
            }

    }

    private static void handleSetCarPlateTexture(SetCarPlateTexturePayload payload, ServerPlayer player) {
                    Entity entity = serverLevel(player).getEntity(payload.entityId());
            if (entity instanceof CarEntity car) {
                if (payload.front()) {
                    car.setFrontPlate(payload.textureId());
                } else {
                    car.setRearPlate(payload.textureId());
                }
            }

    }

    private static void handleSetCarPlateTransform(SetCarPlateTransformPayload payload, ServerPlayer player) {
                    Entity entity = serverLevel(player).getEntity(payload.entityId());
            if (entity instanceof CarEntity car) {
                if (payload.front()) {
                    car.setFrontPlateOffset(payload.x(), payload.y(), payload.z());
                    car.setFrontPlateScale(payload.scale());
                    car.setFrontPlateRotation(payload.yaw(), payload.pitch(), payload.roll());
                } else {
                    car.setRearPlateOffset(payload.x(), payload.y(), payload.z());
                    car.setRearPlateScale(payload.scale());
                    car.setRearPlateRotation(payload.yaw(), payload.pitch(), payload.roll());
                }
            }

    }

    private static void handleSetCarTireSmokeOffset(SetCarTireSmokeOffsetPayload payload, ServerPlayer player) {
                    Entity entity = serverLevel(player).getEntity(payload.entityId());
            if (entity instanceof CarEntity car) {
                car.setTireSmokeOffset(payload.x(), payload.y(), payload.z());
            }

    }

    private static void handleSetCarTireSmokeParticle(SetCarTireSmokeParticlePayload payload, ServerPlayer player) {
                    Entity entity = serverLevel(player).getEntity(payload.entityId());
            if (entity instanceof CarEntity car) {
                car.setTireSmokeParticle(payload.particleId());
            }

    }

    private static void handleSetCarTireSmokePreview(SetCarTireSmokePreviewPayload payload, ServerPlayer player) {
                    Entity entity = serverLevel(player).getEntity(payload.entityId());
            if (entity instanceof CarEntity car) {
                car.setTireSmokePreviewEnabled(payload.enabled());
            }

    }

    private static void handleSetDetectedWheelFootprint(SetDetectedWheelFootprintPayload payload, ServerPlayer player) {
                    Entity entity = serverLevel(player).getEntity(payload.entityId());
            if (entity instanceof CarEntity car) {
                car.setDetectedWheelFootprint(payload.trackHalfWidth(), payload.rearZ());
            }

    }

    private static void handleSetGhostRecording(SetGhostRecordingPayload payload, ServerPlayer player) {
                    Entity entity = serverLevel(player).getEntity(payload.entityId());
            if (entity instanceof CarEntity car) {
                if (payload.recording()) {
                    car.startRecording();
                } else {
                    car.stopRecording();
                }
            }

    }

    private static void handlePlayGhostLap(PlayGhostLapPayload payload, ServerPlayer player) {
                    Entity entity = serverLevel(player).getEntity(payload.entityId());
            if (entity instanceof CarEntity car) {
                car.spawnGhost(payload.loop());
            }

    }

    private static void handleDeleteGhostLap(DeleteGhostLapPayload payload, ServerPlayer player) {
                    Entity entity = serverLevel(player).getEntity(payload.entityId());
            if (entity instanceof CarEntity car) {
                car.deleteGhosts();
            }

    }

    private static void handleSetCarDriveMode(SetCarDriveModePayload payload, ServerPlayer player) {
                    Entity entity = serverLevel(player).getEntity(payload.entityId());
            if (entity instanceof CarEntity car) {
                CarEntity.DriveMode[] modes = CarEntity.DriveMode.values();
                int index = Math.max(0, Math.min(payload.driveMode(), modes.length - 1));
                car.setDriveMode(modes[index]);
            }

    }

    private static void handleSetCarItemIcon(SetCarItemIconPayload payload, ServerPlayer player) {
        Entity entity = serverLevel(player).getEntity(payload.entityId());
        if (entity instanceof CarEntity car) {
            car.setItemIcon(payload.icon());
        }
    }

    private static void handleSetChaseMode(SetChaseModePayload payload, ServerPlayer player) {
                    Entity entity = serverLevel(player).getEntity(payload.entityId());
            if (entity instanceof CarEntity car) {
                car.setChaseModeEnabled(payload.enabled());
            }

    }

    private static void handleSetChaosMode(SetChaosModePayload payload, ServerPlayer player) {
                    Entity entity = serverLevel(player).getEntity(payload.entityId());
            if (entity instanceof CarEntity car) {
                car.setChaosModeEnabled(payload.enabled());
            }

    }

    private static void handleSetTownTrafficEnabled(SetTownTrafficEnabledPayload payload, ServerPlayer player) {
                    WaypointNetworkSavedData data = WaypointNetworkSavedData.get(serverLevel(player));
            data.setTownTrafficEnabled(payload.enabled());

    }

    private static void handleSetCarCustomTuning(SetCarCustomTuningPayload payload, ServerPlayer player) {
                    Entity entity = serverLevel(player).getEntity(payload.entityId());
            if (entity instanceof CarEntity car) {
                car.setCustomTopSpeedKmh(payload.topSpeedKmh());
                car.setCustomAccelFraction(payload.accelFraction());
                car.setCustomBrakeFraction(payload.brakeFraction());
                car.setCustomHandlingFraction(payload.handlingFraction());
                car.setCustomTireSmokeAlways(payload.tireSmokeAlways());
                car.setDespawnEnabled(payload.despawnEnabled());
            }

    }

    private static void handleSetCarEngine(SetCarEnginePayload payload, ServerPlayer player) {
                    Entity entity = serverLevel(player).getEntity(payload.entityId());
            if (entity instanceof CarEntity car) {
                car.setEngineSoundId(payload.soundId());
                car.setEnginePitchIntensity(payload.pitchIntensity());
                car.setEngineTopSpeedPitch(payload.topSpeedPitch());
                car.setEngineLoopUseFraction(payload.loopUseFraction());
            }

    }

    private static void handleSetCarHornSiren(SetCarHornSirenPayload payload, ServerPlayer player) {
        Entity entity = serverLevel(player).getEntity(payload.entityId());
        if (entity instanceof CarEntity car) {
            car.setHornSoundId(payload.hornSoundId());
            car.setSirenSoundId(payload.sirenSoundId());
        }
    }

    private static void handleSetCarBomb(SetCarBombPayload payload, ServerPlayer player) {
        Entity entity = serverLevel(player).getEntity(payload.entityId());
        if (entity instanceof CarEntity car && !car.isBombBurning()) {
            car.setBombInstalled(payload.installed(), player.getUUID());
        }
    }

    private static void handleDetonateBombs(DetonateBombsPayload payload, ServerPlayer player) {
        java.util.List<CarEntity> armed = new java.util.ArrayList<>();

        //? if >=1.16 {
        Iterable<Entity> allEntities = serverLevel(player).getAllEntities();
        //?} else
        /*Iterable<Entity> allEntities = serverLevel(player).getEntities(null, mineturismo$e -> true);*/
        for (Entity entity : allEntities) {
            if (entity instanceof CarEntity car && car.isBombArmedBy(player.getUUID())) {
                armed.add(car);
            }
        }
        for (CarEntity car : armed) {
            car.detonateBomb();
        }
        if (armed.isEmpty()) {
            ShowHudWarningPayload warningPayload = new ShowHudWarningPayload("gui.mineturismo.detonator.no_bombs");
            //? if >=1.20.5 {
            ModNetworking.sendToClient(player, warningPayload);
            //?} else
            /*ModNetworking.sendToClient(player, ShowHudWarningPayload.ID, warningPayload, ShowHudWarningPayload::write);*/
        }
    }

    private static void handleSetCarWheelAdjust(SetCarWheelAdjustPayload payload, ServerPlayer player) {
                    Entity entity = serverLevel(player).getEntity(payload.entityId());
            if (entity instanceof CarEntity car) {
                car.setWheelAdjust(payload.frontZOffset(), payload.rearZOffset(), payload.trackHalfWidthOffset(),
                        payload.axleHeightOffset(), payload.bodyHeightOffset(), payload.camberDegrees(),
                        payload.wheelScale(), payload.wheelModelIdOverride());
            }

    }

    private static void handleSetCarWheelColor(SetCarWheelColorPayload payload, ServerPlayer player) {
                    Entity entity = serverLevel(player).getEntity(payload.entityId());
            if (entity instanceof CarEntity car) {
                car.setWheelColor(payload.wheelColorId());
            }

    }

    private static void handleSetCarWheelWidth(SetCarWheelWidthPayload payload, ServerPlayer player) {
                    Entity entity = serverLevel(player).getEntity(payload.entityId());
            if (entity instanceof CarEntity car) {
                car.setWheelWidthScale(payload.wheelWidthScale());
            }

    }

    private static void handleSetCarExtraRearAxle(SetCarExtraRearAxlePayload payload, ServerPlayer player) {
                    Entity entity = serverLevel(player).getEntity(payload.entityId());
            if (entity instanceof CarEntity car) {
                if (payload.axle() == 2) {
                    car.setExtraRearAxle2(payload.enabled(), payload.zOffset());
                } else {
                    car.setExtraRearAxle(payload.enabled(), payload.zOffset());
                }
            }

    }

    private static void handleSetCarLipAdjust(SetCarLipAdjustPayload payload, ServerPlayer player) {
                    Entity entity = serverLevel(player).getEntity(payload.entityId());
            if (entity instanceof CarEntity car) {
                car.setLipAdjust(payload.lipStretchAdjust(), payload.lipWidthAdjust(), payload.lipBandHeightCm(), payload.lipFrontExtendAdjust());
            }

    }

    private static void handleSetCarLimoAdjust(SetCarLimoAdjustPayload payload, ServerPlayer player) {
                    Entity entity = serverLevel(player).getEntity(payload.entityId());
            if (entity instanceof CarEntity car) {
                car.setLimoAdjust(payload.limoStretchCm(), payload.limoZoneCm(), payload.limoCenterOffsetCm());
            }

    }

    private static void handleSetCarWidthAdjust(SetCarWidthAdjustPayload payload, ServerPlayer player) {
                    Entity entity = serverLevel(player).getEntity(payload.entityId());
            if (entity instanceof CarEntity car) {
                car.setWidthAdjust(payload.widthStretchCm(), payload.widthZoneCm(), payload.widthCenterOffsetCm());
            }

    }

    private static void handleSetCarTilt(SetCarTiltPayload payload, ServerPlayer player) {
                    Entity entity = serverLevel(player).getEntity(payload.entityId());
            if (entity instanceof CarEntity car) {
                car.setTiltAdjust(payload.rollDegrees(), payload.pitchDegrees(), payload.heightCm());
            }

    }

    private static void handleSetCarPassengerPositionAdjust(SetCarPassengerPositionAdjustPayload payload, ServerPlayer player) {
                    Entity entity = serverLevel(player).getEntity(payload.entityId());
            if (entity instanceof CarEntity car) {
                car.setPassengerPositionAdjust(payload.x(), payload.y(), payload.z());
            }

    }

    private static void handleSetCarRearWing(SetCarRearWingPayload payload, ServerPlayer player) {
                    Entity entity = serverLevel(player).getEntity(payload.entityId());
            if (entity instanceof CarEntity car) {
                car.setRearWingEnabledIds(payload.enabledIds());
                car.setRearWingTransform(payload.partId(), new com.examplebett.render.PartTransforms.Transform12(
                        payload.x(), payload.y(), payload.z(), payload.angle(), payload.angleY(), payload.scale(),
                        payload.stretchXPos(), payload.stretchXNeg(), payload.stretchYPos(), payload.stretchYNeg(), payload.stretchZPos(), payload.stretchZNeg()));
            }

    }

    private static void handleSetCarPoliceLight(SetCarPoliceLightPayload payload, ServerPlayer player) {
                    Entity entity = serverLevel(player).getEntity(payload.entityId());
            if (entity instanceof CarEntity car) {
                car.setPoliceLightEnabledIds(payload.enabledIds());
                car.setPoliceLightTransform(payload.partId(), new com.examplebett.render.PartTransforms.Transform12(
                        payload.x(), payload.y(), payload.z(), payload.angle(), payload.angleY(), payload.scale(),
                        payload.stretchXPos(), payload.stretchXNeg(), payload.stretchYPos(), payload.stretchYNeg(), payload.stretchZPos(), payload.stretchZNeg()));
            }

    }

    private static void handleSetCarTaxiSign(SetCarTaxiSignPayload payload, ServerPlayer player) {
                    Entity entity = serverLevel(player).getEntity(payload.entityId());
            if (entity instanceof CarEntity car) {
                car.setTaxiSignEnabledIds(payload.enabledIds());
                car.setTaxiSignTransform(payload.partId(), new com.examplebett.render.PartTransforms.Transform12(
                        payload.x(), payload.y(), payload.z(), payload.angle(), payload.angleY(), payload.scale(),
                        payload.stretchXPos(), payload.stretchXNeg(), payload.stretchYPos(), payload.stretchYNeg(), payload.stretchZPos(), payload.stretchZNeg()));
            }
    }

    private static void handleSetCarOtherPart(SetCarOtherPartPayload payload, ServerPlayer player) {
                    Entity entity = serverLevel(player).getEntity(payload.entityId());
            if (entity instanceof CarEntity car) {
                car.setOtherPartEnabledIds(payload.enabledIds());
                car.setOtherPartTransform(payload.partId(), new com.examplebett.render.PartTransforms.Transform12(
                        payload.x(), payload.y(), payload.z(), payload.angle(), payload.angleY(), payload.scale(),
                        payload.stretchXPos(), payload.stretchXNeg(), payload.stretchYPos(), payload.stretchYNeg(), payload.stretchZPos(), payload.stretchZNeg()));
            }

    }

    private static void handleSetCarTextPart(SetCarTextPartPayload payload, ServerPlayer player) {
        Entity entity = serverLevel(player).getEntity(payload.entityId());
        if (entity instanceof CarEntity car) {
            com.examplebett.render.TextParts.Line line = com.examplebett.render.TextParts.decodeLine(payload.line());
            if (line != null) {
                car.setTextPart(line.slot(), line.value());
            }
        }
    }

    private static void handleSetCarUnderNeon(SetCarUnderNeonPayload payload, ServerPlayer player) {
        Entity entity = serverLevel(player).getEntity(payload.entityId());
        if (entity instanceof CarEntity car) {
            com.examplebett.render.UnderNeon.Line line = com.examplebett.render.UnderNeon.decodeLine(payload.line());
            if (line != null) {
                car.setUnderNeon(line.slot(), line.value());
            }
        }
    }

    private static void handleSetCarBrandLogo(SetCarBrandLogoPayload payload, ServerPlayer player) {
        Entity entity = serverLevel(player).getEntity(payload.entityId());
        if (entity instanceof CarEntity car) {
            com.examplebett.render.BrandLogos.Line line = com.examplebett.render.BrandLogos.decodeLine(payload.line());
            if (line != null) {
                car.setBrandLogo(line.slot(), line.value());
            }
        }
    }

    private static void handleRequestAppCarSpawn(RequestAppCarSpawnPayload payload, ServerPlayer player) {
        com.examplebett.item.SmartDispatchAppItem.callVehicle(serverLevel(player), player, payload.modelId());
    }

    private static void handleRequestAppItemPurchase(RequestAppItemPurchasePayload payload, ServerPlayer player) {
        com.examplebett.item.SmartDispatchAppItem.purchaseItem(serverLevel(player), player, payload.itemId());
    }

    private static void handleBuyUsedCar(BuyUsedCarPayload payload, ServerPlayer player) {
        com.examplebett.item.SmartDispatchAppItem.buyUsedCar(serverLevel(player), player, payload.listingId());
    }

    private static void handleSetAppTime(SetAppTimePayload payload, ServerPlayer player) {
        com.examplebett.item.SmartDispatchAppItem.setTime(serverLevel(player), player, payload.kind());
    }

    private static void handleSetPhoneStyle(SetPhoneStylePayload payload, ServerPlayer player) {
        com.examplebett.item.SmartDispatchAppItem.setPhoneStyle(player, payload.model(), payload.wallpaper());
    }

    private static void handleSetAppWeather(SetAppWeatherPayload payload, ServerPlayer player) {
        com.examplebett.item.SmartDispatchAppItem.setWeather(serverLevel(player), player, payload.kind());
    }

    private static void handleOpenMassiveAutoAppraisal(OpenMassiveAutoAppraisalPayload payload, ServerPlayer player) {
        if (!(player.getMainHandItem().getItem() instanceof com.examplebett.item.SmartDispatchAppItem)
                && !(player.getOffhandItem().getItem() instanceof com.examplebett.item.SmartDispatchAppItem)) {
            return;
        }
        player.openMenu(new net.minecraft.world.SimpleMenuProvider(
                (syncId, inventory, menuPlayer) -> new com.examplebett.inventory.MassiveAutoAppraisalMenu(syncId, inventory),
                ModText.translatable("gui.mineturismo.massive_auto.appraisal")));
    }

    private static void handleRequestEmergencyCall(RequestEmergencyCallPayload payload, ServerPlayer player) {
        com.examplebett.item.SmartDispatchAppItem.callEmergency(serverLevel(player), player);
    }

    private static void handleRequestSlotSpin(RequestSlotSpinPayload payload, ServerPlayer player) {
        com.examplebett.item.SmartDispatchAppItem.spinSlot(serverLevel(player), player);
    }

    private static void handleSetGtt(SetGttPayload payload, ServerPlayer player) {
        if (payload.active()) {
            com.examplebett.item.SmartDispatchAppItem.startGtt(serverLevel(player), player);
        } else {
            com.examplebett.item.SmartDispatchAppItem.stopGtt(serverLevel(player), player);
        }
    }

    private static void handleSetCarFrontBrakeCaliper(SetCarFrontBrakeCaliperPayload payload, ServerPlayer player) {
                    Entity entity = serverLevel(player).getEntity(payload.entityId());
            if (entity instanceof CarEntity car) {
                car.setFrontBrakeCaliperEnabledIds(payload.enabledIds());
                car.setFrontBrakeCaliperTransform(payload.partId(), new com.examplebett.render.PartTransforms.Transform6(
                        payload.x(), payload.y(), payload.z(), payload.angle(), payload.angleY(), payload.scale()));
            }

    }

    private static void handleSetCarExhaust(SetCarExhaustPayload payload, ServerPlayer player) {
                    Entity entity = serverLevel(player).getEntity(payload.entityId());
            if (entity instanceof CarEntity car) {
                car.setExhaustEnabledIds(payload.enabledIds());
                car.setExhaustTransform(payload.partId(), new com.examplebett.render.PartTransforms.Transform6(
                        payload.x(), payload.y(), payload.z(), payload.angle(), payload.angleY(), payload.scale()));
            }

    }

    private static void handleSetCarRearBrakeCaliper(SetCarRearBrakeCaliperPayload payload, ServerPlayer player) {
                    Entity entity = serverLevel(player).getEntity(payload.entityId());
            if (entity instanceof CarEntity car) {
                car.setRearBrakeCaliperEnabledIds(payload.enabledIds());
                car.setRearBrakeCaliperTransform(payload.partId(), new com.examplebett.render.PartTransforms.Transform6(
                        payload.x(), payload.y(), payload.z(), payload.angle(), payload.angleY(), payload.scale()));
            }

    }

    private static void handleSetPoliceLightsOn(SetPoliceLightsOnPayload payload, ServerPlayer player) {
                    Entity entity = serverLevel(player).getEntity(payload.entityId());
            if (entity instanceof CarEntity car) {
                car.setPoliceLightsOn(payload.on());
                car.setPoliceSirenOn(payload.siren());
            }

    }

    private static void handleSetCarLivery(SetCarLiveryPayload payload, ServerPlayer player) {
                    Entity entity = serverLevel(player).getEntity(payload.entityId());
            if (entity instanceof CarEntity car) {
                car.setLivery(payload.liveryId());
            }

    }

    private static void handleSetCarLight(SetCarLightPayload payload, ServerPlayer player) {
                    Entity entity = serverLevel(player).getEntity(payload.entityId());
            if (entity instanceof CarEntity car) {
                CarEntity.LightMode[] modes = CarEntity.LightMode.values();
                int ordinal = Math.max(0, Math.min(modes.length - 1, payload.lightMode()));
                car.setLightAdjust(payload.headlightX(), payload.headlightY(), payload.headlightZ(),
                        payload.taillightX(), payload.taillightY(), payload.taillightZ(), modes[ordinal],
                        payload.headlightColorId());
            }

    }

    private static void handleSetCarLightBlink(SetCarLightBlinkPayload payload, ServerPlayer player) {
                    Entity entity = serverLevel(player).getEntity(payload.entityId());
            if (entity instanceof CarEntity car) {
                car.setLightBlinkEnabled(payload.blinking());
            }

    }

    private static void handleSetForceRotate(SetForceRotatePayload payload, ServerPlayer player) {
                    CarEntity.setForceRotateSideEnabled(payload.sideEnabled());
            CarEntity.setForceRotateFrontEnabled(payload.frontEnabled());

    }

    private static void handleSetActionMultipliers(SetActionMultipliersPayload payload, ServerPlayer player) {
                    CarEntity.setTumbleHeightMultiplier(payload.heightMultiplier());
            CarEntity.setTumbleRotationMultiplier(payload.rotationMultiplier());
            CarEntity.setPushDistanceMultiplier(payload.pushDistanceMultiplier());
            CarEntity.setSuspensionIntensityMultiplier(payload.suspensionIntensityMultiplier());

    }

    private static void handleSetCarScale(SetCarScalePayload payload, ServerPlayer player) {
                    Entity entity = serverLevel(player).getEntity(payload.entityId());
            if (entity instanceof CarEntity car) {
                car.setCarScale(payload.carScale());
            }

    }

    private static void handleSetCarCamera(SetCarCameraPayload payload, ServerPlayer player) {
                    Entity entity = serverLevel(player).getEntity(payload.entityId());
            if (entity instanceof CarEntity car) {
                car.setCameraDistanceAdjust(payload.distanceAdjust());
                car.setCameraHeightAdjust(payload.heightAdjust());
            }

    }

    private static void handleMountForCameraAdjust(MountForCameraAdjustPayload payload, ServerPlayer player) {
                    Entity entity = serverLevel(player).getEntity(payload.entityId());
            if (entity instanceof CarEntity car && player.getVehicle() == null) {
                player.startRiding(car);
            }

    }

    private static void handleSetHornState(SetHornStatePayload payload, ServerPlayer player) {
                    Entity entity = serverLevel(player).getEntity(payload.entityId());
            if (entity instanceof CarEntity car) {
                car.setHornBroadcastActive(payload.pressed());
            }

    }

    private static void handleToggleTrailerCoupling(ToggleTrailerCouplingPayload payload, ServerPlayer player) {
        Entity entity = serverLevel(player).getEntity(payload.entityId());
        if (entity instanceof CarEntity car && car.getControllingPassenger() == player) {
            car.toggleTrailerCoupling(player);
        }
    }

    private static void handleSetClientSlipReport(SetClientSlipReportPayload payload, ServerPlayer player) {
                    Entity entity = serverLevel(player).getEntity(payload.entityId());
            if (entity instanceof CarEntity car) {
                car.reportClientSlip();
            }

    }

    private static void handleSetClientWheelieTrigger(SetClientWheelieTriggerPayload payload, ServerPlayer player) {
                    Entity entity = serverLevel(player).getEntity(payload.entityId());
            if (entity instanceof CarEntity car) {
                car.reportClientWheelieTrigger();
            }

    }

    private static void handleSetWaypointPenSpeed(SetWaypointPenSpeedPayload payload, ServerPlayer player) {
                    WaypointNetworkSavedData data = WaypointNetworkSavedData.get(serverLevel(player));
            data.setPenSpeedKmh(player.getUUID(), payload.speedKmh());

    }

    private static void handleSetTownTrafficDisabledVehicles(SetTownTrafficDisabledVehiclesPayload payload, ServerPlayer player) {
                    WaypointNetworkSavedData data = WaypointNetworkSavedData.get(serverLevel(player));
            Set<String> disabled = new HashSet<>();
            if (!payload.disabledModelIds().isEmpty()) {
                disabled.addAll(Arrays.asList(payload.disabledModelIds().split(",")));
            }
            data.setDisabledModelIds(disabled);
            Set<String> rare = new HashSet<>();
            if (!payload.rareModelIds().isEmpty()) {
                rare.addAll(Arrays.asList(payload.rareModelIds().split(",")));
            }
            data.setRareModelIds(rare);

    }

    private static void handleAddCustomTrafficCar(AddCustomTrafficCarPayload payload, ServerPlayer player) {
        int slot = payload.inventorySlot();
        if (slot < 0 || slot >= com.examplebett.compat.ModPlayer.getInventory(player).getContainerSize()) {
            return;
        }
        net.minecraft.world.item.ItemStack stack = com.examplebett.compat.ModPlayer.getInventory(player).getItem(slot);
        if (!com.examplebett.item.BoxedCarItem.hasCar(stack)) {
            return;
        }
        ServerLevel serverLevel = serverLevel(player);
        WaypointNetworkSavedData data = WaypointNetworkSavedData.get(serverLevel);

        if (data.addCustomTrafficCar(com.examplebett.item.BoxedCarItem.customizationTag(stack))) {
            syncWaypointSettings(serverLevel, data);
        }
    }

    private static void handleUpdateCustomTrafficCar(UpdateCustomTrafficCarPayload payload, ServerPlayer player) {
        ServerLevel serverLevel = serverLevel(player);
        WaypointNetworkSavedData data = WaypointNetworkSavedData.get(serverLevel);
        switch (payload.action()) {
            case UpdateCustomTrafficCarPayload.ACTION_REMOVE -> data.removeCustomTrafficCar(payload.id());
            case UpdateCustomTrafficCarPayload.ACTION_SET_NORMAL -> data.setCustomTrafficCarRare(payload.id(), false);
            case UpdateCustomTrafficCarPayload.ACTION_SET_RARE -> data.setCustomTrafficCarRare(payload.id(), true);
            default -> {
                return;
            }
        }
        syncWaypointSettings(serverLevel, data);
    }

    private static void syncWaypointSettings(ServerLevel serverLevel, WaypointNetworkSavedData data) {
        SyncAmbientEngineVolumePayload sync = SyncAmbientEngineVolumePayload.of(data);
        for (ServerPlayer levelPlayer : ModNetworking.worldPlayers(serverLevel)) {
            //? if >=1.20.5 {
            ModNetworking.sendToClient(levelPlayer, sync);
            //?} else
            /*ModNetworking.sendToClient(levelPlayer, SyncAmbientEngineVolumePayload.ID, sync, SyncAmbientEngineVolumePayload::write);*/
        }
    }

    private static void handleSetTownTrafficPlateIds(SetTownTrafficPlateIdsPayload payload, ServerPlayer player) {
                    WaypointNetworkSavedData data = WaypointNetworkSavedData.get(serverLevel(player));
            Set<String> plateIds = new HashSet<>();
            if (!payload.plateIds().isEmpty()) {
                plateIds.addAll(Arrays.asList(payload.plateIds().split(",")));
            }
            data.setTrafficPlateIds(plateIds);
            Set<String> exclusivePlateIds = new HashSet<>();
            if (!payload.exclusivePlateIds().isEmpty()) {
                exclusivePlateIds.addAll(Arrays.asList(payload.exclusivePlateIds().split(",")));
            }
            data.setExclusivePlateIds(exclusivePlateIds);

    }

    private static void handleSetTownTrafficMovementMode(SetTownTrafficMovementModePayload payload, ServerPlayer player) {
                    WaypointNetworkSavedData data = WaypointNetworkSavedData.get(serverLevel(player));
            data.setLegacySteeringEnabled(payload.useLegacySteering());

    }

    private static void handleSetTownMaxSpawnedCars(SetTownMaxSpawnedCarsPayload payload, ServerPlayer player) {
                    WaypointNetworkSavedData data = WaypointNetworkSavedData.get(serverLevel(player));
            data.setMaxSpawnedCars(payload.maxSpawnedCars());

    }

    private static void handleSetTownAiThrottle(SetTownAiThrottlePayload payload, ServerPlayer player) {
                    WaypointNetworkSavedData data = WaypointNetworkSavedData.get(serverLevel(player));
            data.setAiThrottleIntervalTicks(payload.intervalTicks());

    }

    private static void handleSetTownSpawnInterval(SetTownSpawnIntervalPayload payload, ServerPlayer player) {
                    WaypointNetworkSavedData data = WaypointNetworkSavedData.get(serverLevel(player));
            data.setSpawnIntervalTicks(payload.intervalTicks());

    }

    private static void handleSetTownRecallDistantCars(SetTownRecallDistantCarsPayload payload, ServerPlayer player) {
                    WaypointNetworkSavedData data = WaypointNetworkSavedData.get(serverLevel(player));
            data.setRecallDistantCarsEnabled(payload.enabled());

    }

    private static void handleSetTownNightLights(SetTownNightLightsPayload payload, ServerPlayer player) {
                    WaypointNetworkSavedData data = WaypointNetworkSavedData.get(serverLevel(player));
            data.setNightLightsEnabled(payload.enabled());

    }

    private static void handleSetOneWayReversed(SetOneWayReversedPayload payload, ServerPlayer player) {
                    WaypointNetworkSavedData data = WaypointNetworkSavedData.get(serverLevel(player));
            data.setOneWayReversed(player.getUUID(), payload.reversed());

    }

    private static void handleSetSignalName(SetSignalNamePayload payload, ServerPlayer player) {
                    WaypointNetworkSavedData data = WaypointNetworkSavedData.get(serverLevel(player));
            UUID playerId = player.getUUID();
            WaypointNetworkSavedData.PendingSignalPen pen = data.getPendingSignalPen(playerId);
            WaypointNetworkSavedData.PendingSignalPen updated = new WaypointNetworkSavedData.PendingSignalPen(
                    payload.intersectionName(), pen.direction(), pen.greenTicks(), pen.redTicks(), pen.phaseOffsetTicks());
            data.setPendingSignalPen(playerId, updated);
            applyToEditingSignal(data, playerId, updated);

    }

    private static void handleSetSignalDirection(SetSignalDirectionPayload payload, ServerPlayer player) {
                    WaypointNetworkSavedData data = WaypointNetworkSavedData.get(serverLevel(player));
            UUID playerId = player.getUUID();
            WaypointNetworkSavedData.PendingSignalPen pen = data.getPendingSignalPen(playerId);
            char direction = payload.direction().isEmpty() ? 'N' : payload.direction().charAt(0);
            WaypointNetworkSavedData.PendingSignalPen updated = new WaypointNetworkSavedData.PendingSignalPen(
                    pen.intersectionName(), direction, pen.greenTicks(), pen.redTicks(), pen.phaseOffsetTicks());
            data.setPendingSignalPen(playerId, updated);
            applyToEditingSignal(data, playerId, updated);

    }

    private static void handleSetSignalTiming(SetSignalTimingPayload payload, ServerPlayer player) {
                    WaypointNetworkSavedData data = WaypointNetworkSavedData.get(serverLevel(player));
            UUID playerId = player.getUUID();
            WaypointNetworkSavedData.PendingSignalPen pen = data.getPendingSignalPen(playerId);
            int greenTicks = Mth.clamp(payload.greenTicks(), SignalGroupConfig.MIN_PHASE_TICKS, SignalGroupConfig.MAX_PHASE_TICKS);
            int redTicks = Mth.clamp(payload.redTicks(), SignalGroupConfig.MIN_PHASE_TICKS, SignalGroupConfig.MAX_PHASE_TICKS);
            WaypointNetworkSavedData.PendingSignalPen updated = new WaypointNetworkSavedData.PendingSignalPen(
                    pen.intersectionName(), pen.direction(), greenTicks, redTicks, payload.phaseOffsetTicks());
            data.setPendingSignalPen(playerId, updated);
            applyToEditingSignal(data, playerId, updated);

    }

    private static void handleSetAmbientEngineVolume(SetAmbientEngineVolumePayload payload, ServerPlayer player) {
                    ServerLevel serverLevel = serverLevel(player);
            WaypointNetworkSavedData data = WaypointNetworkSavedData.get(serverLevel);
            data.setAmbientEngineVolumeMultiplier(payload.multiplier());

            SyncAmbientEngineVolumePayload sync = SyncAmbientEngineVolumePayload.of(data);
            for (ServerPlayer levelPlayer : ModNetworking.worldPlayers(serverLevel)) {
                //? if >=1.20.5 {
                ModNetworking.sendToClient(levelPlayer, sync);
                //?} else
                /*ModNetworking.sendToClient(levelPlayer, SyncAmbientEngineVolumePayload.ID, sync, SyncAmbientEngineVolumePayload::write);*/
            }

    }

    private static void handleSetWheelRotationReport(SetWheelRotationReportPayload payload, ServerPlayer player) {
                    Entity entity = serverLevel(player).getEntity(payload.entityId());
            if (entity instanceof CarEntity car) {

                SyncWheelDistancePayload sync = new SyncWheelDistancePayload(payload.entityId(), payload.distance());
                for (var trackingPlayer : ModNetworking.trackingPlayers(car)) {
                    //? if >=1.20.5 {
                    ModNetworking.sendToClient(trackingPlayer, sync);
                    //?} else
                    /*ModNetworking.sendToClient(trackingPlayer, SyncWheelDistancePayload.ID, sync, SyncWheelDistancePayload::write);*/
                }
            }

    }
}
