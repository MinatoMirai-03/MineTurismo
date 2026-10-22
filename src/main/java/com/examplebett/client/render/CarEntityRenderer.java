//? if >=1.18 {
//?}
//? if <1.18 && >=1.17 {
/*
//~ axis_mulpose_compat
package com.examplebett.client.render;

import com.examplebett.AiObjectMod;
import com.examplebett.entity.CarEntity;
import com.examplebett.render.CompiledCarModel;
import com.examplebett.render.ObjModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CarEntityRenderer extends EntityRenderer<CarEntity> {

    private static final double LOD_WHEEL_DISTANCE = 32.0;
    private static final double LOD_WHEEL_DISTANCE_SQ = LOD_WHEEL_DISTANCE * LOD_WHEEL_DISTANCE;

    private static final double BRAND_LOGO_FLAT_DISTANCE = 5.0;
    private static final double BRAND_LOGO_FLAT_DISTANCE_SQ = BRAND_LOGO_FLAT_DISTANCE * BRAND_LOGO_FLAT_DISTANCE;
    private static final double BRAND_LOGO_HIDE_DISTANCE = 48.0;
    private static final double BRAND_LOGO_HIDE_DISTANCE_SQ = BRAND_LOGO_HIDE_DISTANCE * BRAND_LOGO_HIDE_DISTANCE;

    private static final boolean[] WHEEL_IS_FRONT_4 = {true, true, false, false};
    private static final boolean[] WHEEL_IS_FRONT_6 = {true, true, false, false, false, false};
    private static final float[] WHEEL_CAMBER_SIGN_4 = {1f, -1f, 1f, -1f};
    private static final float[] WHEEL_CAMBER_SIGN_6 = {1f, -1f, 1f, -1f, 1f, -1f};

    private static final boolean[] WHEEL_IS_FRONT_8 = {true, true, false, false, false, false, false, false};
    private static final float[] WHEEL_CAMBER_SIGN_8 = {1f, -1f, 1f, -1f, 1f, -1f, 1f, -1f};

    private static final boolean[] WHEEL_IS_FRONT_2 = {true, false};
    private static final float[] WHEEL_CAMBER_SIGN_2 = {1f, -1f};
    private static final float[] WHEEL_OFFSET_Z_BUF = new float[8];
    private static final float[] WHEEL_OFFSET_X_BUF = new float[8];

    private static final Map<String, Set<String>> ENABLED_IDS_CACHE = new HashMap<>();

    private static Set<String> parseEnabledIds(String csv) {
        return ENABLED_IDS_CACHE.computeIfAbsent(csv, s -> new HashSet<>(java.util.Arrays.asList(s.split(","))));
    }

    private static final float WHEELIE_PIVOT_Z_FACTOR = 0.85f;

    private static final int BLINK_ON_TICKS = 10;
    private static final int BLINK_OFF_TICKS = 10;

    @FunctionalInterface
    private interface GeometryRenderer {
        void render(PoseStack.Pose pose, VertexConsumer consumer);
    }

    @FunctionalInterface
    private interface ModelDrawer {
        void draw(PoseStack poseStack, VertexConsumer consumer);
    }

    @FunctionalInterface
    private interface TextWrapper {
        VertexConsumer wrap(VertexConsumer out, PoseStack.Pose pose);
    }

    @FunctionalInterface
    private interface GeometrySubmitter {
        void submitCustomGeometry(PoseStack pose, RenderType type, GeometryRenderer renderer);

        default void submitWrappedText(PoseStack pose, net.minecraft.util.FormattedCharSequence text, float x, float y, int light,
                                       TextWrapper wrapper) {
            submitText(pose, text, x, y, 0xFFFFFFFF, light);
        }

        default void submitModel(PoseStack pose, RenderType type, ModelDrawer drawer) {

            throw new UnsupportedOperationException();
        }

        default GeometrySubmitter late() {
            return this;
        }

        default void submitText(PoseStack pose, net.minecraft.util.FormattedCharSequence text, float x, float y, int argb, int light) {
        }
    }

    private static GeometrySubmitter immediateSubmitter(MultiBufferSource bufferSource) {
        return new GeometrySubmitter() {
            @Override
            public void submitCustomGeometry(PoseStack pose, RenderType type, GeometryRenderer renderer) {
                renderer.render(pose.last(), bufferSource.getBuffer(type));
            }

            @Override
            public void submitModel(PoseStack pose, RenderType type, ModelDrawer drawer) {
                drawer.draw(pose, bufferSource.getBuffer(type));
            }

            @Override
            public void submitText(PoseStack pose, net.minecraft.util.FormattedCharSequence text, float x, float y, int argb, int light) {
                net.minecraft.client.Minecraft.getInstance().font.drawInBatch(text, x, y, argb, false, pose.last().pose(), bufferSource,
                        false, 0, light);
            }

            @Override
            public void submitWrappedText(PoseStack pose, net.minecraft.util.FormattedCharSequence text, float x, float y, int light,
                                          TextWrapper wrapper) {
                PoseStack.Pose last = pose.last();
                net.minecraft.client.Minecraft.getInstance().font.drawInBatch(text, x, y, 0xFFFFFFFF, false, PoseMatrices.identityMatrix(),
                        type -> wrapper.wrap(bufferSource.getBuffer(type), last), false, 0, light);
            }
        };
    }

    private final FigureModels figures;

    public CarEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.figures = new FigureModels(context);
    }

    @Override
    public ResourceLocation getTextureLocation(CarEntity entity) {
        return CompiledCarModel.FALLBACK_TEXTURE;
    }

    private void populateCarState(CarEntity entity, CarRenderState state, float partialTick) {
        state.lightCoords = this.getPackedLightCoords(entity, partialTick);

        state.yRot = entity.getYRot();

        float[] railwayOverride = entity.mineturismo$computeRailwayRenderOverride(partialTick);
        state.railwayPositionOverride = railwayOverride != null;
        if (railwayOverride != null) {
            state.railwayPosDeltaX = railwayOverride[0];
            state.railwayPosDeltaY = railwayOverride[1];
            state.railwayPosDeltaZ = railwayOverride[2];
            state.yRot = railwayOverride[3];
        }
        state.tumblePitchDegrees = entity.getTumblePitchDegrees();
        state.tumbleRollDegrees = entity.getTumbleRollDegrees();
        state.tumblePivotHeight = (float) (entity.getScaledHitboxHeight() / 2.0);
        state.wheeliePitchDegrees = entity.getWheeliePitchDegrees();

        state.wheeliePivotZ = -entity.getFootprintHalfLength() * WHEELIE_PIVOT_Z_FACTOR;
        state.tiltRollDegrees = entity.getTiltRollDegrees();
        state.tiltPitchDegrees = entity.getTiltPitchDegrees();
        state.tiltHeightCm = entity.getTiltHeightCm();
        state.suspensionPitchDegrees = entity.getSuspensionPitchDegrees();
        state.suspensionRollDegrees = entity.getSuspensionRollDegrees();
        state.bikeLeanRollDegrees = entity.getBikeLeanRollDegrees();
        state.showFakeRider = entity.shouldRenderFakeTrafficRider();
        if (state.showFakeRider) {
            state.fakeRiderUseAlex = (entity.getUUID().hashCode() & 1) == 0;
            state.fakeRiderX = entity.getPassengerXOffset();

            state.fakeRiderY = entity.getFakeRiderYOffset();
            state.fakeRiderZ = entity.getPassengerZOffset();
            state.fakeRiderIsMotorcycle = entity.hasVehicleCategory(com.examplebett.entity.VehicleCategory.MOTORCYCLE);
            state.fakeRiderBikeStationary = entity.isBikeStationary();
        }
        state.useLodWheel = this.entityRenderDispatcher.distanceToSqr(entity) > LOD_WHEEL_DISTANCE_SQ;

        state.wheelRotationDegrees = state.useLodWheel ? 0f : entity.getWheelRotationDegrees(partialTick);
        state.rearWheelExtraSpinDegrees = state.useLodWheel ? 0f : entity.getRearWheelExtraSpinDegrees(partialTick);
        state.frontWheelSteerDegrees = entity.getFrontWheelSteerDegrees(partialTick);
        state.rotorRotationDegrees = entity.getRotorRotationDegrees(partialTick);
        state.compiledModel = entity.getCompiledModel();
        state.wheelCompiledModel = entity.getWheelCompiledModel();
        state.wheelCompiledModelMirrored = entity.getWheelCompiledModelMirrored();
        state.wheelAttachment = entity.getWheelAttachment();
        state.wheelFrontZOffset = entity.getWheelFrontZOffset();
        state.wheelRearZOffset = entity.getWheelRearZOffset();
        state.wheelTrackHalfWidthOffset = entity.getWheelTrackHalfWidthOffset();
        state.wheelAxleHeightOffset = entity.getWheelAxleHeightOffset();
        state.extraRearAxleEnabled = entity.isExtraRearAxleEnabled();
        state.extraRearAxleZOffset = entity.getExtraRearAxleZOffset();
        state.extraRearAxle2Enabled = entity.isExtraRearAxle2Enabled();
        state.extraRearAxle2ZOffset = entity.getExtraRearAxle2ZOffset();
        state.bodyHeightOffset = entity.getBodyHeightOffset();
        state.wheelCamberDegrees = entity.getWheelCamberDegrees();
        state.wheelScale = entity.getWheelScale();
        state.wheelWidthScale = entity.getWheelWidthScale();
        state.lipStretchScale = entity.getLipStretchScale();
        state.lipWidthScale = entity.getLipWidthScale();
        state.lipBandHeightCm = entity.getLipBandHeightCm();
        state.lipFrontExtendScale = entity.getLipFrontExtendScale();
        state.limoStretchCm = entity.getLimoStretchCm();
        state.limoZoneCm = entity.getLimoZoneCm();
        state.limoCenterOffsetCm = entity.getLimoCenterOffsetCm();
        state.widthStretchCm = entity.getWidthStretchCm();
        state.widthZoneCm = entity.getWidthZoneCm();
        state.widthCenterOffsetCm = entity.getWidthCenterOffsetCm();
        state.carScale = entity.getCarScale();
        state.color = entity.getColor();

        boolean isMotorcycleNoFrontPlate = entity.hasVehicleCategory(com.examplebett.entity.VehicleCategory.MOTORCYCLE);
        state.frontPlateTexture = isMotorcycleNoFrontPlate ? null : PlateTextures.texture(entity.getFrontPlate());
        state.frontPlateAspect = PlateTextures.aspect(entity.getFrontPlate());
        var frontOffset = entity.getFrontPlateOffset();
        state.frontPlateX = (float) frontOffset.x;
        state.frontPlateY = (float) frontOffset.y;
        state.frontPlateZ = (float) frontOffset.z;
        state.frontPlateScale = entity.getFrontPlateScale();
        state.frontPlateYaw = entity.getFrontPlateYaw();
        state.frontPlatePitch = entity.getFrontPlatePitch();
        state.frontPlateRoll = entity.getFrontPlateRoll();

        state.rearPlateTexture = PlateTextures.texture(entity.getRearPlate());
        state.rearPlateAspect = PlateTextures.aspect(entity.getRearPlate());
        var rearOffset = entity.getRearPlateOffset();
        state.rearPlateX = (float) rearOffset.x;
        state.rearPlateY = (float) rearOffset.y;
        state.rearPlateZ = (float) rearOffset.z;
        state.rearPlateScale = entity.getRearPlateScale();
        state.rearPlateYaw = entity.getRearPlateYaw();
        state.rearPlatePitch = entity.getRearPlatePitch();
        state.rearPlateRoll = entity.getRearPlateRoll();

        state.lightsOn = entity.areLightsOn();

        state.headlightHidden = entity.hasVehicleCategory(com.examplebett.entity.VehicleCategory.TRAILER);
        state.lightsBlinking = entity.isLightBlinkEnabled();
        state.age = entity.tickCount;
        var headlightOffset = entity.getHeadlightOffset();
        state.headlightX = (float) headlightOffset.x;
        state.headlightY = (float) headlightOffset.y;
        state.headlightZ = (float) headlightOffset.z;
        var taillightOffset = entity.getTaillightOffset();
        state.taillightX = (float) taillightOffset.x;
        state.taillightY = (float) taillightOffset.y;
        state.taillightZ = (float) taillightOffset.z;
        var headlightColor = com.examplebett.entity.HeadlightColors.byId(entity.resolveHeadlightColorId());
        state.headlightColorR = headlightColor.r();
        state.headlightColorG = headlightColor.g();
        state.headlightColorB = headlightColor.b();

        state.rearWingEnabledIds = entity.getRearWingEnabledIds();
        state.rearWingTransformsRaw = entity.getRearWingTransformsRaw();

        state.otherPartEnabledIds = entity.getOtherPartEnabledIds();
        state.otherPartTransformsRaw = entity.getOtherPartTransformsRaw();

        state.textPartsRaw = entity.getTextPartsRaw();
        state.underNeonRaw = entity.getUnderNeonRaw();
        state.brandLogosRaw = entity.getBrandLogosRaw();
        double brandLogoDistanceSq = this.entityRenderDispatcher.distanceToSqr(entity);
        state.brandLogoDetail = brandLogoDistanceSq > BRAND_LOGO_HIDE_DISTANCE_SQ ? 0
                : brandLogoDistanceSq > BRAND_LOGO_FLAT_DISTANCE_SQ ? 1 : 2;

        state.frontBrakeCaliperEnabledIds = entity.getFrontBrakeCaliperEnabledIds();
        state.frontBrakeCaliperTransformsRaw = entity.getFrontBrakeCaliperTransformsRaw();

        state.rearBrakeCaliperEnabledIds = entity.getRearBrakeCaliperEnabledIds();
        state.rearBrakeCaliperTransformsRaw = entity.getRearBrakeCaliperTransformsRaw();

        state.exhaustEnabledId = entity.getExhaustEnabledIds();
        if (state.exhaustEnabledId.isEmpty()) {
            state.exhaustTarget = null;
            entity.clientExhaustOutlets = null;
        } else {
            state.exhaustTarget = entity;
            state.exhaustTransform = entity.getExhaustTransform(state.exhaustEnabledId);
        }

        state.towSelf = null;
        state.towComputeFront = false;
        state.towComputeRear = false;
        state.towRopeEnd = null;
        long towNow = entity.level().getGameTime();
        int towLeaderId = entity.getTowLeaderEntityId();
        if (towLeaderId != -1 && entity.level().getEntity(towLeaderId) instanceof CarEntity towLeader) {
            towLeader.clientTowRearHookWantedTime = towNow;
            state.towSelf = entity;
            state.towComputeFront = true;
            float[] leaderRear = towLeader.clientTowRearHook;
            if (leaderRear != null) {
                net.minecraft.world.phys.Vec3 leaderPos = towLeader.getPosition(partialTick);
                net.minecraft.world.phys.Vec3 selfPos = entity.getPosition(partialTick);
                state.towRopeEnd = new float[]{
                        (float) (leaderPos.x - selfPos.x) + leaderRear[0],
                        (float) (leaderPos.y - selfPos.y) + leaderRear[1],
                        (float) (leaderPos.z - selfPos.z) + leaderRear[2]};
            }
        } else {
            entity.clientTowFrontHook = null;
        }
        if (towNow - entity.clientTowRearHookWantedTime <= 2) {
            state.towSelf = entity;
            state.towComputeRear = true;
        }

        state.policeLightEnabledIds = entity.getPoliceLightEnabledIds();
        state.policeLightTransformsRaw = entity.getPoliceLightTransformsRaw();
        state.policeLightsOn = entity.isPoliceLightsOn();

        state.taxiSignEnabledIds = entity.getTaxiSignEnabledIds();
        state.taxiSignTransformsRaw = entity.getTaxiSignTransformsRaw();
    }

    @Override
    public void render(CarEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int light) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, light);
        CarRenderState state = new CarRenderState();
        populateCarState(entity, state, partialTick);
        state.isInvisible = entity.isInvisible();
        state.lightCoords = light;
        renderCar(state, poseStack, immediateSubmitter(bufferSource));
    }

    private void renderCar(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector) {

        if (state.isInvisible) {
            return;
        }

        if (state.compiledModel == null) {
            return;
        }

        float[] exhaustOrigin = (state.exhaustTarget != null || state.towSelf != null) ? PoseMatrices.capture(poseStack) : null;

        poseStack.pushPose();
        if (state.railwayPositionOverride) {

            poseStack.translate(state.railwayPosDeltaX, state.railwayPosDeltaY, state.railwayPosDeltaZ);
        }
        poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(state.yRot));

        boolean tumbling = state.tumblePitchDegrees != 0f || state.tumbleRollDegrees != 0f;
        if (tumbling) {
            poseStack.translate(0.0, state.tumblePivotHeight, 0.0);
        }
        if (state.tumblePitchDegrees != 0f) {
            poseStack.mulPose(com.mojang.math.Vector3f.XP.rotationDegrees(state.tumblePitchDegrees));
        }
        if (state.tumbleRollDegrees != 0f) {
            poseStack.mulPose(com.mojang.math.Vector3f.ZP.rotationDegrees(state.tumbleRollDegrees));
        }
        if (tumbling) {
            poseStack.translate(0.0, -state.tumblePivotHeight, 0.0);
        }

        boolean wheelieActive = state.wheeliePitchDegrees != 0f;
        if (wheelieActive) {
            poseStack.translate(0.0, 0.0, state.wheeliePivotZ);
            poseStack.mulPose(com.mojang.math.Vector3f.XP.rotationDegrees(-state.wheeliePitchDegrees));
            poseStack.translate(0.0, 0.0, -state.wheeliePivotZ);
        }

        boolean tilting = state.tiltPitchDegrees != 0f || state.tiltRollDegrees != 0f;
        if (tilting) {
            poseStack.translate(0.0, state.tumblePivotHeight, 0.0);
        }
        if (state.tiltPitchDegrees != 0f) {
            poseStack.mulPose(com.mojang.math.Vector3f.XP.rotationDegrees(state.tiltPitchDegrees));
        }
        if (state.tiltRollDegrees != 0f) {
            poseStack.mulPose(com.mojang.math.Vector3f.ZP.rotationDegrees(state.tiltRollDegrees));
        }
        if (tilting) {
            poseStack.translate(0.0, -state.tumblePivotHeight, 0.0);
        }
        if (state.tiltHeightCm != 0f) {
            poseStack.translate(0.0, state.tiltHeightCm / 100.0, 0.0);
        }

        if (state.bikeLeanRollDegrees != 0f) {
            poseStack.translate(0.0, state.tumblePivotHeight, 0.0);
            poseStack.mulPose(com.mojang.math.Vector3f.ZP.rotationDegrees(state.bikeLeanRollDegrees));
            poseStack.translate(0.0, -state.tumblePivotHeight, 0.0);
        }

        if (state.carScale != 1f) {
            poseStack.scale(state.carScale, state.carScale, state.carScale);
        }

        poseStack.pushPose();
        poseStack.translate(0.0, state.bodyHeightOffset, 0.0);

        boolean suspensionSwaying = state.suspensionPitchDegrees != 0f || state.suspensionRollDegrees != 0f;
        if (suspensionSwaying) {
            poseStack.translate(0.0, state.tumblePivotHeight, 0.0);
        }
        if (state.suspensionPitchDegrees != 0f) {
            poseStack.mulPose(com.mojang.math.Vector3f.XP.rotationDegrees(state.suspensionPitchDegrees));
        }
        if (state.suspensionRollDegrees != 0f) {
            poseStack.mulPose(com.mojang.math.Vector3f.ZP.rotationDegrees(state.suspensionRollDegrees));
        }
        if (suspensionSwaying) {
            poseStack.translate(0.0, -state.tumblePivotHeight, 0.0);
        }

        List<CompiledCarModel.TextureGroup> reflectiveGroups = new ArrayList<>();
        float[] embeddedExtraAxleZ = embeddedExtraAxleZ(state);
        for (CompiledCarModel.TextureGroup group : state.compiledModel.groups) {

            ResourceLocation texture = group.texture;
            boolean usesPaintMask = group.tintable && PaintTintTexture.hasPaint(group.texture);
            if (usesPaintMask) {
                texture = PaintTintTexture.resolve(group.texture, state.color);
            }
            texture = metalPaintTexture(group, texture, usesPaintMask, state.color);

            final int tint = (group.tintable && !usesPaintMask) ? state.color : 0xFFFFFF;

            RenderType renderType = groupRenderType(group, texture);

            boolean spinning = group.wheelGroup >= 0 && group.wheelGroup < state.compiledModel.wheelPivots.size();
            boolean rotorSpinning = group.rotorGroup >= 0 && group.rotorGroup < state.compiledModel.rotorPivots.size();

            boolean isBodyGroup = group.wheelGroup < 0 && group.rotorGroup < 0;
            BodyDeform deform = isBodyGroup ? BodyDeform.from(state) : BodyDeform.NONE;

            boolean reflective = com.examplebett.client.AiObjectModClient.isFakeReflectionEnabled()
                    && isBodyGroup && !group.translucent;

            poseStack.pushPose();
            applyGroupTransform(poseStack, state, group, spinning, rotorSpinning, 0f);
            collector.submitCustomGeometry(poseStack, renderType, (pose, vertexConsumer) ->
                    renderGroup(group.vertexData, pose, vertexConsumer, state.lightCoords, tint, deform, usesTriangles(group)));
            poseStack.popPose();

            if (spinning && embeddedExtraAxleZ.length > 0
                    && !state.compiledModel.wheelPivots.get(group.wheelGroup).front) {
                float pivotZ = state.compiledModel.wheelPivots.get(group.wheelGroup).centerZ;
                for (float axleZ : embeddedExtraAxleZ) {
                    poseStack.pushPose();
                    applyGroupTransform(poseStack, state, group, true, false, axleZ - pivotZ);
                    collector.submitCustomGeometry(poseStack, renderType, (pose, vertexConsumer) ->
                            renderGroup(group.vertexData, pose, vertexConsumer, state.lightCoords, tint, BodyDeform.NONE, usesTriangles(group)));
                    poseStack.popPose();
                }
            }
            if (reflective) {
                reflectiveGroups.add(group);
            }
        }

        for (CompiledCarModel.TextureGroup group : reflectiveGroups) {
            boolean spinning = group.wheelGroup >= 0 && group.wheelGroup < state.compiledModel.wheelPivots.size();
            boolean rotorSpinning = group.rotorGroup >= 0 && group.rotorGroup < state.compiledModel.rotorPivots.size();
            boolean isBodyGroup = group.wheelGroup < 0 && group.rotorGroup < 0;
            BodyDeform deform = isBodyGroup ? BodyDeform.from(state) : BodyDeform.NONE;
            RenderType reflectionType = RenderType.entityNoOutline(CompiledCarModel.FALLBACK_TEXTURE);
            poseStack.pushPose();
            applyGroupTransform(poseStack, state, group, spinning, rotorSpinning, 0f);
            collector.submitCustomGeometry(poseStack, reflectionType, (pose, vertexConsumer) ->
                    renderReflectionOverlay(group.vertexData, pose, vertexConsumer, state.lightCoords, deform));
            poseStack.popPose();
        }

        if (state.frontPlateTexture != null) {
            submitPlate(poseStack, collector, state.lightCoords, state.frontPlateTexture,
                    state.frontPlateX + widthShiftFor(state, state.frontPlateX), state.frontPlateY, state.frontPlateZ + limoShiftFor(state, state.frontPlateZ), state.frontPlateScale,
                    state.frontPlateYaw, state.frontPlatePitch, state.frontPlateRoll, state.frontPlateAspect);
        }
        if (state.rearPlateTexture != null) {

            submitPlate(poseStack, collector, state.lightCoords, state.rearPlateTexture,
                    state.rearPlateX + widthShiftFor(state, state.rearPlateX), state.rearPlateY, state.rearPlateZ + limoShiftFor(state, state.rearPlateZ), state.rearPlateScale,
                    state.rearPlateYaw + 180f, state.rearPlatePitch, state.rearPlateRoll, state.rearPlateAspect);
        }

        boolean blinkPhaseOn = !state.lightsBlinking
                || (state.age % (BLINK_ON_TICKS + BLINK_OFF_TICKS)) < BLINK_ON_TICKS;
        if (state.lightsOn && blinkPhaseOn) {

            for (int side = -1; side <= 1; side += 2) {
                float headlightX = state.headlightX * side;
                float taillightX = state.taillightX * side;
                if (!state.headlightHidden) {
                    submitGlow(poseStack, collector, HEADLIGHT_GLOW_TEXTURE,
                            headlightX + widthShiftFor(state, headlightX), state.headlightY, state.headlightZ + limoShiftFor(state, state.headlightZ), HEADLIGHT_GLOW_SIZE, 0f,
                            state.headlightColorR, state.headlightColorG, state.headlightColorB);
                }
                submitGlow(poseStack, collector, TAILLIGHT_GLOW_TEXTURE,
                        taillightX + widthShiftFor(state, taillightX), state.taillightY, state.taillightZ + limoShiftFor(state, state.taillightZ), TAILLIGHT_GLOW_SIZE, 180f,
                        1f, 1f, 1f);
            }
        }

        renderRearWing(state, poseStack, collector);
        renderPoliceLight(state, poseStack, collector);
        renderTaxiSign(state, poseStack, collector);
        renderOtherPart(state, poseStack, collector);
        renderTextParts(state, poseStack, collector);
        renderBrandLogos(state, poseStack, collector);
        if (exhaustOrigin != null && state.exhaustTarget != null) {
            computeExhaustOutlets(state, poseStack, exhaustOrigin);
        }
        if (exhaustOrigin != null && state.towSelf != null) {
            computeTowHooks(state, poseStack, exhaustOrigin);
        }

        poseStack.popPose();

        renderUnderNeon(state, poseStack, collector);
        renderWheelAttachment(state, poseStack, collector);
        renderBrakeCaliper(state, poseStack, collector, true, state.frontBrakeCaliperEnabledIds, state.frontBrakeCaliperTransformsRaw);
        renderBrakeCaliper(state, poseStack, collector, false, state.rearBrakeCaliperEnabledIds, state.rearBrakeCaliperTransformsRaw);

        renderFakeRider(state, poseStack, collector);

        poseStack.popPose();

        renderTowRope(state, poseStack, collector);
    }

    private static final ResourceLocation TOW_ROPE_TEXTURE = com.examplebett.AiObjectMod.id("textures/entity/tow_rope.png");

    private static final float TOW_ROPE_HALF_THICKNESS = 0.045f;
    private static final int TOW_ROPE_SEGMENTS = 24;

    private static void computeTowHooks(CarRenderState state, PoseStack poseStack, float[] origin) {
        float[] relative = PoseMatrices.relative(origin, PoseMatrices.capture(poseStack));
        if (state.towComputeFront) {
            state.towSelf.clientTowFrontHook = PoseMatrices.transformPosition(relative, 0f, state.frontPlateY,
                    state.frontPlateZ + limoShiftFor(state, state.frontPlateZ));
        }
        if (state.towComputeRear) {
            state.towSelf.clientTowRearHook = PoseMatrices.transformPosition(relative, 0f, state.rearPlateY,
                    state.rearPlateZ + limoShiftFor(state, state.rearPlateZ));
        }
    }

    private void renderTowRope(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector) {
        if (!state.towComputeFront || state.towRopeEnd == null || state.towSelf == null || state.towSelf.clientTowFrontHook == null) {
            return;
        }
        float[] a = state.towSelf.clientTowFrontHook;
        float[] b = state.towRopeEnd;
        float dx = b[0] - a[0];
        float dy = b[1] - a[1];
        float dz = b[2] - a[2];
        float straight = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (straight < 1.0E-3f) {
            return;
        }

        float ropeLength = (float) CarEntity.TOW_ROPE_LENGTH;
        float slack = Math.max(0f, ropeLength - straight);
        float sag = 0.08f + (float) Math.sqrt(slack * (ropeLength + straight)) * 0.35f;
        int light = state.lightCoords;
        collector.submitCustomGeometry(poseStack, RenderType.entityCutoutNoCull(TOW_ROPE_TEXTURE), (pose, consumer) -> {
            float[] prev = null;
            for (int i = 0; i <= TOW_ROPE_SEGMENTS; i++) {
                float t = (float) i / TOW_ROPE_SEGMENTS;
                float[] p = {a[0] + dx * t, a[1] + dy * t - sag * 4f * t * (1f - t), a[2] + dz * t};
                if (prev != null) {

                    float u0 = ((i - 1) % 2) * 0.5f;
                    addRopeSegment(consumer, pose, prev, p, u0, u0 + 0.5f, light);
                }
                prev = p;
            }
        });
    }

    private void addRopeSegment(VertexConsumer consumer, PoseStack.Pose pose, float[] p0, float[] p1, float u0, float u1, int light) {
        float dx = p1[0] - p0[0];
        float dy = p1[1] - p0[1];
        float dz = p1[2] - p0[2];
        float dirLen = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (dirLen < 1.0E-4f) {
            return;
        }
        dx /= dirLen;
        dy /= dirLen;
        dz /= dirLen;

        float sx = -dz;
        float sy = 0f;
        float sz = dx;
        float sideLen = (float) Math.sqrt(sx * sx + sz * sz);
        if (sideLen < 1.0E-3f) {
            sx = 1f;
            sz = 0f;
            sideLen = 1f;
        }
        sx = sx / sideLen * TOW_ROPE_HALF_THICKNESS;
        sz = sz / sideLen * TOW_ROPE_HALF_THICKNESS;

        float ux = sy * dz - sz * dy;
        float uy = sz * dx - sx * dz;
        float uz = sx * dy - sy * dx;
        float upLen = (float) Math.sqrt(ux * ux + uy * uy + uz * uz);
        if (upLen < 1.0E-8f) {
            return;
        }
        ux = ux / upLen * TOW_ROPE_HALF_THICKNESS;
        uy = uy / upLen * TOW_ROPE_HALF_THICKNESS;
        uz = uz / upLen * TOW_ROPE_HALF_THICKNESS;
        float[][] corners = {
                {sx + ux, sy + uy, sz + uz}, {-sx + ux, -sy + uy, -sz + uz},
                {-sx - ux, -sy - uy, -sz - uz}, {sx - ux, sy - uy, sz - uz}};
        for (int f = 0; f < 4; f++) {
            float[] c0 = corners[f];
            float[] c1 = corners[(f + 1) % 4];
            float v0 = f * 0.25f;
            float v1 = v0 + 0.25f;
            addPlateVertex(consumer, pose, p0[0] + c0[0], p0[1] + c0[1], p0[2] + c0[2], u0, v0, light);
            addPlateVertex(consumer, pose, p1[0] + c0[0], p1[1] + c0[1], p1[2] + c0[2], u1, v0, light);
            addPlateVertex(consumer, pose, p1[0] + c1[0], p1[1] + c1[1], p1[2] + c1[2], u1, v1, light);
            addPlateVertex(consumer, pose, p0[0] + c1[0], p0[1] + c1[1], p0[2] + c1[2], u0, v1, light);
        }
    }

    private static final float FAKE_RIDER_YAW_DEGREES = 180f;

    private static final float FAKE_RIDER_Y_ADJUST = 0.85f;

    private void renderFakeRider(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector) {
        if (!state.showFakeRider) {
            return;
        }
        RenderType renderType = RenderType.entityCutoutNoCull(FigureModels.fakeRiderTexture(state.fakeRiderUseAlex));
        poseStack.pushPose();
        poseStack.translate(state.fakeRiderX, state.fakeRiderY + FAKE_RIDER_Y_ADJUST, state.fakeRiderZ);
        poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(FAKE_RIDER_YAW_DEGREES));
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        collector.submitModel(poseStack, renderType, (modelPoseStack, vertexConsumer) ->
                this.figures.renderFakeRider(state.fakeRiderUseAlex, state.fakeRiderIsMotorcycle, state.fakeRiderBikeStationary,
                        state.bikeLeanRollDegrees, modelPoseStack, vertexConsumer, state.lightCoords));
        poseStack.popPose();
    }

    private static final float SEATED_PLAYER_SCALE = 0.9375f;

    private static final float SEATED_PLAYER_ORIGIN_HEIGHT = (12f + 2f) / 16f * SEATED_PLAYER_SCALE;

    private void renderSeatedPlayer(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector, boolean alex) {
        ResourceLocation texture = alex ? FigureModels.ALEX_SLIM_TEXTURE : FigureModels.STEVE_TEXTURE;
        poseStack.pushPose();
        poseStack.translate(0f, SEATED_PLAYER_ORIGIN_HEIGHT, 0f);
        poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(FAKE_RIDER_YAW_DEGREES));
        poseStack.scale(-SEATED_PLAYER_SCALE, -SEATED_PLAYER_SCALE, SEATED_PLAYER_SCALE);
        collector.submitModel(poseStack, RenderType.entityCutoutNoCull(texture), (modelPoseStack, vertexConsumer) ->
                this.figures.renderSeatedPlayer(alex, modelPoseStack, vertexConsumer, state.lightCoords));
        poseStack.popPose();
    }

    private static final float SITTING_WOLF_ORIGIN_HEIGHT = 1.501f;

    private void renderSittingWolf(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector) {
        poseStack.pushPose();
        poseStack.translate(0f, SITTING_WOLF_ORIGIN_HEIGHT, 0f);
        poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(FAKE_RIDER_YAW_DEGREES));
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        collector.submitModel(poseStack, RenderType.entityCutoutNoCull(FigureModels.WOLF_TEXTURE), (modelPoseStack, vertexConsumer) ->
                this.figures.renderSittingWolf(false, state.age, modelPoseStack, vertexConsumer, state.lightCoords));
        collector.submitModel(poseStack, RenderType.entityCutoutNoCull(FigureModels.WOLF_COLLAR_TEXTURE), (modelPoseStack, vertexConsumer) ->
                this.figures.renderSittingWolf(true, state.age, modelPoseStack, vertexConsumer, state.lightCoords));
        poseStack.popPose();
    }

    private record WheelPositions(int wheelCount, float axleHeight, boolean[] isFront, float[] camberSign) {
    }

    private record PartRef(String id, String modelId) {
    }

    private static boolean usesEmbeddedWheelPivots(CarRenderState state) {
        return state.wheelAttachment == null && state.compiledModel != null
                && state.compiledModel.wheelPivots.size() == 4;
    }

    private WheelPositions computeWheelPositions(CarRenderState state) {
        com.examplebett.render.WheelAttachment attachment = state.wheelAttachment;
        if (attachment == null) {
            return computeEmbeddedWheelPositions(state);
        }
        float frontZ = attachment.frontZ() + state.wheelFrontZOffset;
        float rearZ = attachment.rearZ() + state.wheelRearZOffset;
        float trackHalfWidth = attachment.trackHalfWidth() + state.wheelTrackHalfWidthOffset;
        float axleHeight = attachment.axleHeight() + state.wheelAxleHeightOffset;

        boolean hasRearAxle2 = attachment.hasRearAxle2() || state.extraRearAxleEnabled;
        float rearZ2Base = attachment.hasRearAxle2()
                ? attachment.rearZ2()
                : attachment.rearZ() - CarEntity.DEFAULT_EXTRA_REAR_AXLE_SPACING;
        float rearZ2 = hasRearAxle2 ? rearZ2Base + state.wheelRearZOffset + state.extraRearAxleZOffset : 0f;

        boolean hasRearAxle3 = state.extraRearAxle2Enabled;
        float rearZ3 = hasRearAxle3
                ? rearZ2Base - CarEntity.DEFAULT_EXTRA_REAR_AXLE_SPACING + state.wheelRearZOffset + state.extraRearAxle2ZOffset
                : 0f;

        frontZ += limoShiftFor(state, frontZ);
        rearZ += limoShiftFor(state, rearZ);
        if (hasRearAxle2) {
            rearZ2 += limoShiftFor(state, rearZ2);
        }
        if (hasRearAxle3) {
            rearZ3 += limoShiftFor(state, rearZ3);
        }

        float trackRightX = trackHalfWidth + widthShiftFor(state, trackHalfWidth);
        float trackLeftX = -trackHalfWidth + widthShiftFor(state, -trackHalfWidth);

        boolean singleWheelPerAxle = attachment.singleWheelPerAxle();

        int wheelCount = singleWheelPerAxle ? 2 : (hasRearAxle2 && hasRearAxle3 ? 8 : (hasRearAxle2 || hasRearAxle3 ? 6 : 4));
        float[] offsetZ = WHEEL_OFFSET_Z_BUF;
        float[] offsetX = WHEEL_OFFSET_X_BUF;
        if (singleWheelPerAxle) {
            offsetZ[0] = frontZ; offsetZ[1] = rearZ;
            offsetX[0] = 0f; offsetX[1] = 0f;
        } else {
            offsetZ[0] = frontZ; offsetZ[1] = frontZ; offsetZ[2] = rearZ; offsetZ[3] = rearZ;
            offsetX[0] = trackRightX; offsetX[1] = trackLeftX; offsetX[2] = trackRightX; offsetX[3] = trackLeftX;
            if (hasRearAxle2) {
                offsetZ[4] = rearZ2; offsetZ[5] = rearZ2;
                offsetX[4] = trackRightX; offsetX[5] = trackLeftX;
                if (hasRearAxle3) {
                    offsetZ[6] = rearZ3; offsetZ[7] = rearZ3;
                    offsetX[6] = trackRightX; offsetX[7] = trackLeftX;
                }
            } else if (hasRearAxle3) {
                offsetZ[4] = rearZ3; offsetZ[5] = rearZ3;
                offsetX[4] = trackRightX; offsetX[5] = trackLeftX;
            }
        }
        boolean[] isFront = singleWheelPerAxle ? WHEEL_IS_FRONT_2
                : (wheelCount == 8 ? WHEEL_IS_FRONT_8 : (wheelCount == 6 ? WHEEL_IS_FRONT_6 : WHEEL_IS_FRONT_4));

        float[] camberSign = singleWheelPerAxle ? WHEEL_CAMBER_SIGN_2
                : (wheelCount == 8 ? WHEEL_CAMBER_SIGN_8 : (wheelCount == 6 ? WHEEL_CAMBER_SIGN_6 : WHEEL_CAMBER_SIGN_4));
        return new WheelPositions(wheelCount, axleHeight, isFront, camberSign);
    }

    private static final float[] NO_EXTRA_AXLES = new float[0];

    private static float[] embeddedExtraAxleZ(CarRenderState state) {
        if ((!state.extraRearAxleEnabled && !state.extraRearAxle2Enabled) || !usesEmbeddedWheelPivots(state)) {
            return NO_EXTRA_AXLES;
        }
        float rearZ = 0f;
        int rearCount = 0;
        for (ObjModel.WheelPivot pivot : state.compiledModel.wheelPivots) {
            if (!pivot.front) {
                rearZ += pivot.centerZ;
                rearCount++;
            }
        }
        if (rearCount == 0) {
            return NO_EXTRA_AXLES;
        }
        rearZ /= rearCount;
        float rearZ2Base = rearZ - CarEntity.DEFAULT_EXTRA_REAR_AXLE_SPACING;
        float rearZ2 = rearZ2Base + state.extraRearAxleZOffset;
        float rearZ3 = rearZ2Base - CarEntity.DEFAULT_EXTRA_REAR_AXLE_SPACING + state.extraRearAxle2ZOffset;
        if (state.extraRearAxleEnabled && state.extraRearAxle2Enabled) {
            return new float[] { rearZ2, rearZ3 };
        }
        return new float[] { state.extraRearAxleEnabled ? rearZ2 : rearZ3 };
    }

    private WheelPositions computeEmbeddedWheelPositions(CarRenderState state) {
        if (!usesEmbeddedWheelPivots(state)) {
            return new WheelPositions(0, 0f, WHEEL_IS_FRONT_4, WHEEL_CAMBER_SIGN_4);
        }
        List<ObjModel.WheelPivot> pivots = state.compiledModel.wheelPivots;
        ObjModel.WheelPivot[] front = new ObjModel.WheelPivot[2];
        ObjModel.WheelPivot[] rear = new ObjModel.WheelPivot[2];
        int frontCount = 0;
        int rearCount = 0;
        float sumY = 0f;
        for (ObjModel.WheelPivot pivot : pivots) {
            if (pivot.front && frontCount < 2) {
                front[frontCount++] = pivot;
            } else if (!pivot.front && rearCount < 2) {
                rear[rearCount++] = pivot;
            }
            sumY += pivot.centerY;
        }
        if (frontCount != 2 || rearCount != 2) {
            return new WheelPositions(0, 0f, WHEEL_IS_FRONT_4, WHEEL_CAMBER_SIGN_4);
        }

        if (front[0].centerX < front[1].centerX) {
            ObjModel.WheelPivot t = front[0]; front[0] = front[1]; front[1] = t;
        }
        if (rear[0].centerX < rear[1].centerX) {
            ObjModel.WheelPivot t = rear[0]; rear[0] = rear[1]; rear[1] = t;
        }
        float[] extraAxleZ = embeddedExtraAxleZ(state);
        int wheelCount = 4 + extraAxleZ.length * 2;
        float[] offsetZ = WHEEL_OFFSET_Z_BUF;
        float[] offsetX = WHEEL_OFFSET_X_BUF;
        offsetZ[0] = embeddedWheelZ(state, front[0].centerZ, true); offsetZ[1] = embeddedWheelZ(state, front[1].centerZ, true);
        offsetZ[2] = embeddedWheelZ(state, rear[0].centerZ, false); offsetZ[3] = embeddedWheelZ(state, rear[1].centerZ, false);
        offsetX[0] = embeddedWheelX(state, front[0]); offsetX[1] = embeddedWheelX(state, front[1]);
        offsetX[2] = embeddedWheelX(state, rear[0]); offsetX[3] = embeddedWheelX(state, rear[1]);
        for (int a = 0; a < extraAxleZ.length; a++) {
            int i = 4 + a * 2;
            float axleZ = embeddedWheelZ(state, extraAxleZ[a], false);
            offsetZ[i] = axleZ; offsetZ[i + 1] = axleZ;
            offsetX[i] = offsetX[2]; offsetX[i + 1] = offsetX[3];
        }
        boolean[] isFront = wheelCount == 8 ? WHEEL_IS_FRONT_8 : (wheelCount == 6 ? WHEEL_IS_FRONT_6 : WHEEL_IS_FRONT_4);
        float[] camberSign = wheelCount == 8 ? WHEEL_CAMBER_SIGN_8 : (wheelCount == 6 ? WHEEL_CAMBER_SIGN_6 : WHEEL_CAMBER_SIGN_4);
        return new WheelPositions(wheelCount, sumY / pivots.size() + state.wheelAxleHeightOffset, isFront, camberSign);
    }

    private void renderBrakeCaliper(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector, boolean frontOnly,
                                     String enabledIdsCsv, String transformsRaw) {

        if ((state.wheelAttachment == null && !usesEmbeddedWheelPivots(state)) || enabledIdsCsv == null || enabledIdsCsv.isEmpty()) {
            return;
        }
        Set<String> enabledIds = parseEnabledIds(enabledIdsCsv);
        net.minecraft.server.packs.resources.ResourceManager resourceManager =
                net.minecraft.client.Minecraft.getInstance().getResourceManager();
        List<PartRef> parts = new ArrayList<>();
        if (frontOnly) {
            for (com.examplebett.render.FrontBrakeCaliperParts.Option option : com.examplebett.render.FrontBrakeCaliperParts.list(resourceManager)) {
                parts.add(new PartRef(option.id(), option.modelId()));
            }
        } else {
            for (com.examplebett.render.RearBrakeCaliperParts.Option option : com.examplebett.render.RearBrakeCaliperParts.list(resourceManager)) {
                parts.add(new PartRef(option.id(), option.modelId()));
            }
        }
        if (parts.isEmpty()) {
            return;
        }
        WheelPositions wp = computeWheelPositions(state);
        float[] offsetX = WHEEL_OFFSET_X_BUF;
        float[] offsetZ = WHEEL_OFFSET_Z_BUF;
        for (PartRef part : parts) {
            if (!enabledIds.contains(part.id())) {
                continue;
            }
            BrakeCaliperModel.Pair pair = BrakeCaliperModel.get(resourceManager, part.modelId());
            if (pair == null) {
                continue;
            }
            com.examplebett.render.PartTransforms.Transform6 t =
                    com.examplebett.render.PartTransforms.resolve6(transformsRaw, part.id(),
                            com.examplebett.entity.CarEntity.defaultBrakeCaliperTransform(part.id()));
            float localX = t.x();
            float localY = t.y();
            float localZ = t.z();
            float localAngle = t.angle();
            float localAngleY = t.angleY();
            float localScale = t.scale();
            for (int i = 0; i < wp.wheelCount(); i++) {
                if (wp.isFront()[i] != frontOnly) {
                    continue;
                }
                poseStack.pushPose();
                poseStack.translate(offsetX[i], wp.axleHeight(), offsetZ[i]);
                if (state.wheelCamberDegrees != 0f) {
                    poseStack.mulPose(com.mojang.math.Vector3f.ZP.rotationDegrees(wp.camberSign()[i] * state.wheelCamberDegrees));
                }
                if (frontOnly && state.frontWheelSteerDegrees != 0f) {

                    poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(state.frontWheelSteerDegrees));
                }
                boolean mirrored = offsetX[i] < 0;
                CompiledCarModel activeModel = mirrored ? pair.mirrored() : pair.normal();

                poseStack.translate(mirrored ? -localX : localX, localY, localZ);
                if (localAngleY != 0f) {
                    poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(localAngleY));
                }
                if (localAngle != 0f) {
                    poseStack.mulPose(com.mojang.math.Vector3f.XP.rotationDegrees(localAngle));
                }
                if (localScale != 1f) {
                    poseStack.scale(localScale, localScale, localScale);
                }
                for (CompiledCarModel.TextureGroup group : activeModel.groups) {
                    ResourceLocation texture = group.texture;
                    final int tint = group.tintable ? state.color : 0xFFFFFF;
                    RenderType renderType = groupRenderType(group, texture);
                    collector.submitCustomGeometry(poseStack, renderType, (pose, vertexConsumer) ->
                            renderGroup(group.vertexData, pose, vertexConsumer, state.lightCoords, tint, BodyDeform.NONE, usesTriangles(group)));
                }
                poseStack.popPose();
            }
        }
    }

    private void renderWheelAttachment(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector) {
        if (state.wheelCompiledModel == null || state.wheelAttachment == null) {
            return;
        }

        CompiledCarModel wheelModel = state.wheelCompiledModel;

        CompiledCarModel wheelModelMirrored =
                state.wheelCompiledModelMirrored != null ? state.wheelCompiledModelMirrored : wheelModel;
        if (state.useLodWheel) {
            net.minecraft.server.packs.resources.ResourceManager resourceManager =
                    net.minecraft.client.Minecraft.getInstance().getResourceManager();
            CompiledCarModel lodModel = LodWheelModel.get(resourceManager);
            if (lodModel != null) {
                wheelModel = lodModel;
                CompiledCarModel lodModelMirrored = LodWheelModel.getMirrored(resourceManager);
                wheelModelMirrored = lodModelMirrored != null ? lodModelMirrored : lodModel;
            }
        }
        WheelPositions wp = computeWheelPositions(state);
        int wheelCount = wp.wheelCount();
        float axleHeight = wp.axleHeight();
        float[] offsetZ = WHEEL_OFFSET_Z_BUF;
        float[] offsetX = WHEEL_OFFSET_X_BUF;
        boolean[] isFront = wp.isFront();
        float[] camberSign = wp.camberSign();

        for (int i = 0; i < wheelCount; i++) {
            poseStack.pushPose();
            poseStack.translate(offsetX[i], axleHeight, offsetZ[i]);

            if (state.wheelCamberDegrees != 0f) {
                poseStack.mulPose(com.mojang.math.Vector3f.ZP.rotationDegrees(camberSign[i] * state.wheelCamberDegrees));
            }
            if (isFront[i]) {
                poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(state.frontWheelSteerDegrees));
            }

            float rollDegrees = state.wheelRotationDegrees + (isFront[i] ? 0f : state.rearWheelExtraSpinDegrees);
            poseStack.mulPose(com.mojang.math.Vector3f.XP.rotationDegrees(rollDegrees));

            boolean mirrored = offsetX[i] < 0;
            CompiledCarModel activeWheelModel = mirrored ? wheelModelMirrored : wheelModel;

            float scaleX = state.wheelScale * state.wheelWidthScale;
            if (scaleX != 1f || state.wheelScale != 1f) {
                poseStack.scale(scaleX, state.wheelScale, state.wheelScale);
            }
            for (CompiledCarModel.TextureGroup group : activeWheelModel.groups) {
                ResourceLocation texture = group.texture;
                boolean usesPaintMask = group.tintable && PaintTintTexture.hasPaint(group.texture);
                if (usesPaintMask) {
                    texture = PaintTintTexture.resolve(group.texture, state.color);
                }
                texture = metalPaintTexture(group, texture, usesPaintMask, state.color);
                final int tint = (group.tintable && !usesPaintMask) ? state.color : 0xFFFFFF;
                RenderType renderType = groupRenderType(group, texture);
                collector.submitCustomGeometry(poseStack, renderType, (pose, vertexConsumer) ->
                        renderGroup(group.vertexData, pose, vertexConsumer, state.lightCoords, tint, BodyDeform.NONE, usesTriangles(group)));
            }
            poseStack.popPose();
        }
    }

    private void renderRearWing(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector) {
        if (state.rearWingEnabledIds == null || state.rearWingEnabledIds.isEmpty()) {
            return;
        }
        Set<String> enabledIds = parseEnabledIds(state.rearWingEnabledIds);
        net.minecraft.server.packs.resources.ResourceManager resourceManager =
                net.minecraft.client.Minecraft.getInstance().getResourceManager();
        com.examplebett.render.PartTransforms.Transform12 defaults = new com.examplebett.render.PartTransforms.Transform12(
                com.examplebett.entity.CarEntity.DEFAULT_REAR_WING_X, com.examplebett.entity.CarEntity.DEFAULT_REAR_WING_Y,
                com.examplebett.entity.CarEntity.DEFAULT_REAR_WING_Z, 0f, 0f, com.examplebett.entity.CarEntity.DEFAULT_REAR_WING_SCALE,
                1f, 1f, 1f, 1f, 1f, 1f);
        for (com.examplebett.render.RearWingParts.Option option : com.examplebett.render.RearWingParts.list(resourceManager)) {
            if (!enabledIds.contains(option.id())) {
                continue;
            }
            CompiledCarModel wingModel = RearWingModel.get(resourceManager, option.modelId());
            if (wingModel == null) {
                continue;
            }
            com.examplebett.render.PartTransforms.Transform12 t =
                    com.examplebett.render.PartTransforms.resolve12(state.rearWingTransformsRaw, option.id(), defaults);
            poseStack.pushPose();
            poseStack.translate(t.x() + widthShiftFor(state, t.x()), t.y(), t.z() + limoShiftFor(state, t.z()));
            if (t.angleY() != 0f) {
                poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(t.angleY()));
            }
            if (t.angle() != 0f) {
                poseStack.mulPose(com.mojang.math.Vector3f.XP.rotationDegrees(t.angle()));
            }

            float stretchScaleX = (t.stretchXPos() + t.stretchXNeg()) / 2f;
            float stretchScaleY = (t.stretchYPos() + t.stretchYNeg()) / 2f;
            float stretchScaleZ = (t.stretchZPos() + t.stretchZNeg()) / 2f;
            float offsetX = t.scale() * OTHER_PART_STRETCH_HALF_EXTENT * (t.stretchXPos() - t.stretchXNeg()) / 2f;
            float offsetY = t.scale() * OTHER_PART_STRETCH_HALF_EXTENT * (t.stretchYPos() - t.stretchYNeg()) / 2f;
            float offsetZ = t.scale() * OTHER_PART_STRETCH_HALF_EXTENT * (t.stretchZPos() - t.stretchZNeg()) / 2f;
            if (offsetX != 0f || offsetY != 0f || offsetZ != 0f) {
                poseStack.translate(offsetX, offsetY, offsetZ);
            }
            if (t.scale() != 1f || stretchScaleX != 1f || stretchScaleY != 1f || stretchScaleZ != 1f) {
                poseStack.scale(t.scale() * stretchScaleX, t.scale() * stretchScaleY, t.scale() * stretchScaleZ);
            }
            for (CompiledCarModel.TextureGroup group : wingModel.groups) {
                ResourceLocation texture = group.texture;
                final int tint = group.tintable ? state.color : 0xFFFFFF;
                RenderType renderType = groupRenderType(group, texture);
                collector.submitCustomGeometry(poseStack, renderType, (pose, vertexConsumer) ->
                        renderGroup(group.vertexData, pose, vertexConsumer, state.lightCoords, tint, BodyDeform.NONE, usesTriangles(group)));
            }
            poseStack.popPose();
        }
    }

    private static final float OTHER_PART_STRETCH_HALF_EXTENT = 0.3f;

    private void renderOtherPart(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector) {
        if (state.otherPartEnabledIds == null || state.otherPartEnabledIds.isEmpty()) {
            return;
        }
        Set<String> enabledIds = parseEnabledIds(state.otherPartEnabledIds);
        net.minecraft.server.packs.resources.ResourceManager resourceManager =
                net.minecraft.client.Minecraft.getInstance().getResourceManager();
        for (com.examplebett.render.OtherParts.Option option : com.examplebett.render.OtherParts.list(resourceManager)) {
            if (!enabledIds.contains(option.id())) {
                continue;
            }

            boolean spareTire = com.examplebett.render.OtherParts.SPARE_TIRE_ID.equals(option.id());

            boolean seatedPlayer = com.examplebett.render.OtherParts.isSeatedCreature(option.id());
            CompiledCarModel partModel = seatedPlayer
                    ? null
                    : spareTire
                            ? state.wheelCompiledModel
                            : OtherPartModel.get(resourceManager, option.modelId());
            if (partModel == null && !seatedPlayer) {
                continue;
            }
            com.examplebett.render.PartTransforms.Transform12 t =
                    com.examplebett.render.PartTransforms.resolve12(state.otherPartTransformsRaw, option.id(),
                            com.examplebett.entity.CarEntity.defaultOtherPartTransform(option.id()));
            poseStack.pushPose();
            poseStack.translate(t.x() + widthShiftFor(state, t.x()), t.y(), t.z() + limoShiftFor(state, t.z()));
            if (t.angleY() != 0f) {
                poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(t.angleY()));
            }
            if (t.angle() != 0f) {
                poseStack.mulPose(com.mojang.math.Vector3f.XP.rotationDegrees(t.angle()));
            }

            float stretchScaleX = (t.stretchXPos() + t.stretchXNeg()) / 2f;
            float stretchScaleY = (t.stretchYPos() + t.stretchYNeg()) / 2f;
            float stretchScaleZ = (t.stretchZPos() + t.stretchZNeg()) / 2f;
            float offsetX = t.scale() * OTHER_PART_STRETCH_HALF_EXTENT * (t.stretchXPos() - t.stretchXNeg()) / 2f;
            float offsetY = t.scale() * OTHER_PART_STRETCH_HALF_EXTENT * (t.stretchYPos() - t.stretchYNeg()) / 2f;
            float offsetZ = t.scale() * OTHER_PART_STRETCH_HALF_EXTENT * (t.stretchZPos() - t.stretchZNeg()) / 2f;
            if (offsetX != 0f || offsetY != 0f || offsetZ != 0f) {
                poseStack.translate(offsetX, offsetY, offsetZ);
            }
            if (t.scale() != 1f || stretchScaleX != 1f || stretchScaleY != 1f || stretchScaleZ != 1f) {
                poseStack.scale(t.scale() * stretchScaleX, t.scale() * stretchScaleY, t.scale() * stretchScaleZ);
            }
            if (spareTire) {

                poseStack.mulPose(com.mojang.math.Vector3f.ZP.rotationDegrees(90f));
                float scaleX = state.wheelScale * state.wheelWidthScale;
                if (scaleX != 1f || state.wheelScale != 1f) {
                    poseStack.scale(scaleX, state.wheelScale, state.wheelScale);
                }
            }
            if (seatedPlayer) {
                if (com.examplebett.render.OtherParts.SITTING_WOLF_ID.equals(option.id())) {
                    renderSittingWolf(state, poseStack, collector);
                } else {
                    renderSeatedPlayer(state, poseStack, collector,
                            com.examplebett.render.OtherParts.SITTING_ALEX_ID.equals(option.id()));
                }
                poseStack.popPose();
                continue;
            }

            boolean wholeVehicle = com.examplebett.render.OtherParts.R34_ID.equals(option.id());
            int partColor = wholeVehicle ? 0xFFFFFF : state.color;
            for (CompiledCarModel.TextureGroup group : partModel.groups) {
                ResourceLocation texture = group.texture;

                boolean usesPaintMask = group.tintable && PaintTintTexture.hasPaint(group.texture);
                if (usesPaintMask) {
                    texture = PaintTintTexture.resolve(group.texture, partColor);
                }
                texture = metalPaintTexture(group, texture, usesPaintMask, partColor);
                final int tint = (group.tintable && !usesPaintMask) ? partColor : 0xFFFFFF;
                RenderType renderType = groupRenderType(group, texture);
                collector.submitCustomGeometry(poseStack, renderType, (pose, vertexConsumer) ->
                        renderGroup(group.vertexData, pose, vertexConsumer, state.lightCoords, tint, BodyDeform.NONE, usesTriangles(group)));
            }
            poseStack.popPose();
        }
    }

    private static final float TEXT_PART_PIXEL_SIZE = 0.02f;

    private static final float TEXT_PART_LAYER_SPACING = 0.004f;
    private static final int TEXT_PART_MAX_LAYERS = 24;

    private static final float TEXT_PART_SIDE_SHADE = 0.55f;
    private static final int TEXT_PART_FULL_BRIGHT = 0xF000F0;

    private static final int TEXT_PART_CACHE_LIMIT = 256;
    private static final Map<String, com.examplebett.render.TextParts.Slot[]> TEXT_PARTS_CACHE = new HashMap<>();
    private static final Map<String, net.minecraft.util.FormattedCharSequence> TEXT_SEQUENCE_CACHE = new HashMap<>();

    private static com.examplebett.render.TextParts.Slot[] parseTextParts(String raw) {
        if (TEXT_PARTS_CACHE.size() > TEXT_PART_CACHE_LIMIT) {
            TEXT_PARTS_CACHE.clear();
        }
        return TEXT_PARTS_CACHE.computeIfAbsent(raw, com.examplebett.render.TextParts::parse);
    }

    private static net.minecraft.util.FormattedCharSequence textPartSequence(com.examplebett.render.TextParts.Slot slot) {
        if (TEXT_SEQUENCE_CACHE.size() > TEXT_PART_CACHE_LIMIT) {
            TEXT_SEQUENCE_CACHE.clear();
        }
        return TEXT_SEQUENCE_CACHE.computeIfAbsent(slot.font() + "\n" + slot.text(), key -> {
            net.minecraft.network.chat.MutableComponent component = com.examplebett.compat.ModText.literal(slot.text()).copy();
            if (!com.examplebett.render.TextParts.FONT_DEFAULT.equals(slot.font())) {
                ResourceLocation fontId = AiObjectMod.id("text_" + slot.font());

                component = component.withStyle(style -> style.withFont(fontId));
            }

            return component.getVisualOrderText();
        });
    }

    private static final ResourceLocation UNDER_NEON_TEXTURE =
            AiObjectMod.id("textures/entity/lights/under_neon_glow.png");
    private static final Map<String, com.examplebett.render.UnderNeon.Slot[]> UNDER_NEON_CACHE = new HashMap<>();

    private void renderUnderNeon(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector) {
        if (state.underNeonRaw == null || state.underNeonRaw.isEmpty()) {
            return;
        }
        if (UNDER_NEON_CACHE.size() > TEXT_PART_CACHE_LIMIT) {
            UNDER_NEON_CACHE.clear();
        }
        com.examplebett.render.UnderNeon.Slot[] slots = UNDER_NEON_CACHE.computeIfAbsent(state.underNeonRaw, com.examplebett.render.UnderNeon::parse);
        RenderType neonType = RenderType.itemEntityTranslucentCull(UNDER_NEON_TEXTURE);
        for (com.examplebett.render.UnderNeon.Slot slot : slots) {
            if (slot == null || !slot.enabled()) {
                continue;
            }
            float r = ((slot.color() >> 16) & 0xFF) / 255f;
            float g = ((slot.color() >> 8) & 0xFF) / 255f;
            float b = (slot.color() & 0xFF) / 255f;
            float alpha = slot.brightness();
            float hw = slot.width() / 2f;
            float hl = slot.length() / 2f;
            poseStack.pushPose();
            poseStack.translate(slot.x(), slot.y(), slot.z());
            poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(slot.yaw()));
            collector.late().submitCustomGeometry(poseStack, neonType, (pose, consumer) -> {
                addGlowVertex(consumer, pose, -hw, 0f, -hl, 0f, 0f, r, g, b, alpha);
                addGlowVertex(consumer, pose, -hw, 0f, hl, 0f, 1f, r, g, b, alpha);
                addGlowVertex(consumer, pose, hw, 0f, hl, 1f, 1f, r, g, b, alpha);
                addGlowVertex(consumer, pose, hw, 0f, -hl, 1f, 0f, r, g, b, alpha);

                addGlowVertex(consumer, pose, -hw, 0f, -hl, 0f, 0f, r, g, b, alpha);
                addGlowVertex(consumer, pose, hw, 0f, -hl, 1f, 0f, r, g, b, alpha);
                addGlowVertex(consumer, pose, hw, 0f, hl, 1f, 1f, r, g, b, alpha);
                addGlowVertex(consumer, pose, -hw, 0f, hl, 0f, 1f, r, g, b, alpha);
            });
            poseStack.popPose();
        }
    }

    private static final Map<String, com.examplebett.render.BrandLogos.Slot[]> BRAND_LOGO_CACHE = new HashMap<>();

    private static final float BRAND_LOGO_HEIGHT = 0.12f;

    private static final float BRAND_LOGO_RECESS_RATIO = 0.5f;

    private static final int BRAND_LOGO_GLINT_GRID = 16;
    private static final float BRAND_LOGO_GLINT_OFFSET = 0.0005f;
    private static final float BRAND_LOGO_GLINT_STRENGTH = 0.6f;

    private void renderBrandLogos(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector) {
        if (state.brandLogosRaw == null || state.brandLogosRaw.isEmpty() || state.brandLogoDetail == 0) {
            return;
        }
        boolean detailed = state.brandLogoDetail >= 2;
        if (BRAND_LOGO_CACHE.size() > TEXT_PART_CACHE_LIMIT) {
            BRAND_LOGO_CACHE.clear();
        }
        com.examplebett.render.BrandLogos.Slot[] slots = BRAND_LOGO_CACHE.computeIfAbsent(state.brandLogosRaw, com.examplebett.render.BrandLogos::parse);
        boolean glint = com.examplebett.client.AiObjectModClient.isFakeReflectionEnabled();
        for (com.examplebett.render.BrandLogos.Slot slot : slots) {
            if (slot == null || !slot.enabled()) {
                continue;
            }

            int light = slot.glow() ? TEXT_PART_FULL_BRIGHT : state.lightCoords;
            BrandLogoTextures.Design design = BrandLogoTextures.design(slot.logo());

            BrandLogoMesh.Mesh mesh = design.flat() ? null : BrandLogoMesh.get(design);
            float height = BRAND_LOGO_HEIGHT * slot.scale();
            float pixel = height / (mesh != null ? mesh.height() : design.height());
            float halfW = (mesh != null ? mesh.width() : design.width()) * pixel / 2f;
            float halfH = height / 2f;
            float depth = slot.depth();
            float[] levelZ = {0f, depth * (1f - BRAND_LOGO_RECESS_RATIO), depth};

            poseStack.pushPose();
            poseStack.translate(slot.x() + widthShiftFor(state, slot.x()), slot.y(), slot.z() + limoShiftFor(state, slot.z()));
            poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(slot.yaw()));
            poseStack.mulPose(com.mojang.math.Vector3f.XP.rotationDegrees(slot.pitch()));
            poseStack.mulPose(com.mojang.math.Vector3f.ZP.rotationDegrees(slot.roll()));
            if (design.flat()) {

                int layers = detailed
                        ? Math.min(TEXT_PART_MAX_LAYERS, Math.max(1, (int) Math.ceil(depth / TEXT_PART_LAYER_SPACING)))
                        : 0;
                collector.submitCustomGeometry(poseStack, RenderType.entityCutoutNoCull(design.texture()), (pose, consumer) -> {
                    for (int i = 0; i < layers; i++) {
                        addBrandLogoFace(consumer, pose, halfW, halfH, depth * i / layers, 1f, light, 1f, TEXT_PART_SIDE_SHADE);
                    }
                    addBrandLogoFace(consumer, pose, halfW, halfH, depth, 1f, light, 1f);
                });
                poseStack.popPose();
                continue;
            }
            collector.submitCustomGeometry(poseStack, RenderType.entityCutoutNoCull(design.texture()), (pose, consumer) -> {
                addBrandLogoFace(consumer, pose, halfW, halfH, levelZ[0], 1f, light, -1f);
                addBrandLogoFace(consumer, pose, halfW, halfH, levelZ[1], 1f, light, 1f);
                if (!detailed) {
                    return;
                }
                for (BrandLogoMesh.Wall wall : mesh.walls()) {
                    float x0 = -halfW + wall.x0() * pixel;
                    float y0 = halfH - wall.y0() * pixel;
                    float x1 = -halfW + wall.x1() * pixel;
                    float y1 = halfH - wall.y1() * pixel;
                    float zLow = levelZ[wall.lowLevel()];
                    float zHigh = levelZ[wall.highLevel()];

                    float nx = wall.nx();
                    float ny = -wall.ny();
                    addBrandLogoVertex(consumer, pose, x0, y0, zLow, wall.u0(), wall.v0(), light, 1f, nx, ny, 0f);
                    addBrandLogoVertex(consumer, pose, x1, y1, zLow, wall.u1(), wall.v1(), light, 1f, nx, ny, 0f);
                    addBrandLogoVertex(consumer, pose, x1, y1, zHigh, wall.u1(), wall.v1(), light, 1f, nx, ny, 0f);
                    addBrandLogoVertex(consumer, pose, x0, y0, zHigh, wall.u0(), wall.v0(), light, 1f, nx, ny, 0f);
                }
            });
            collector.submitCustomGeometry(poseStack, RenderType.entityCutoutNoCull(design.raisedTexture()), (pose, consumer) ->
                    addBrandLogoFace(consumer, pose, halfW, halfH, levelZ[2], 1f, light, 1f));
            if (glint && detailed) {
                collector.submitCustomGeometry(poseStack, RenderType.entityTranslucent(design.glintTexture()), (pose, consumer) ->
                        addBrandLogoGlint(consumer, pose, halfW, halfH, levelZ[2] + BRAND_LOGO_GLINT_OFFSET, light));
            }
            poseStack.popPose();
        }
    }

    private void addBrandLogoFace(VertexConsumer consumer, PoseStack.Pose pose, float halfW, float halfH, float z, float alpha, int light, float nz) {
        addBrandLogoFace(consumer, pose, halfW, halfH, z, alpha, light, nz, 1f);
    }

    private void addBrandLogoFace(VertexConsumer consumer, PoseStack.Pose pose, float halfW, float halfH, float z, float alpha, int light, float nz,
                                  float shade) {
        addBrandLogoVertex(consumer, pose, -halfW, -halfH, z, 0f, 1f, light, alpha, 0f, 0f, nz, shade);
        addBrandLogoVertex(consumer, pose, halfW, -halfH, z, 1f, 1f, light, alpha, 0f, 0f, nz, shade);
        addBrandLogoVertex(consumer, pose, halfW, halfH, z, 1f, 0f, light, alpha, 0f, 0f, nz, shade);
        addBrandLogoVertex(consumer, pose, -halfW, halfH, z, 0f, 0f, light, alpha, 0f, 0f, nz, shade);
    }

    private void addBrandLogoGlint(VertexConsumer consumer, PoseStack.Pose pose, float halfW, float halfH, float z, int light) {

        float[] camera = PoseMatrices.transformPosition(PoseMatrices.invert(PoseMatrices.capture(pose)), 0f, 0f, 0f);
        float toCameraX = camera[0];
        float toCameraY = camera[1];
        float toCameraZ = camera[2] - z;
        float toCameraLength = (float) Math.sqrt(toCameraX * toCameraX + toCameraY * toCameraY + toCameraZ * toCameraZ);
        if (toCameraLength < 1e-4f) {
            return;
        }
        float phase = 0.5f + 1.1f * (toCameraX / toCameraLength * 0.8f + toCameraY / toCameraLength * 0.6f);
        int blockLight = light & 0xFFFF;
        int skyLight = (light >> 16) & 0xFFFF;
        float strength = BRAND_LOGO_GLINT_STRENGTH * Math.max(blockLight, skyLight) / 240f;
        if (strength <= 0f) {
            return;
        }
        int n = BRAND_LOGO_GLINT_GRID;
        float[][] alpha = new float[n + 1][n + 1];
        boolean any = false;
        for (int j = 0; j <= n; j++) {
            for (int i = 0; i <= n; i++) {
                float u = (float) i / n;
                float v = (float) j / n;

                float s = (u * 0.8f + (1f - v) * 0.6f) / 1.4f;
                float value = Math.min(1f, glintBand(s, phase, 0.14f) + 0.6f * glintBand(s, phase - 0.24f, 0.05f)) * strength;
                alpha[j][i] = value;
                any |= value > 0.001f;
            }
        }
        if (!any) {
            return;
        }
        for (int j = 0; j < n; j++) {
            for (int i = 0; i < n; i++) {
                if (alpha[j][i] <= 0.001f && alpha[j][i + 1] <= 0.001f && alpha[j + 1][i] <= 0.001f && alpha[j + 1][i + 1] <= 0.001f) {
                    continue;
                }
                float u0 = (float) i / n;
                float u1 = (float) (i + 1) / n;
                float v0 = (float) j / n;
                float v1 = (float) (j + 1) / n;
                float x0 = -halfW + u0 * 2f * halfW;
                float x1 = -halfW + u1 * 2f * halfW;
                float y0 = halfH - v0 * 2f * halfH;
                float y1 = halfH - v1 * 2f * halfH;
                addBrandLogoVertex(consumer, pose, x0, y1, z, u0, v1, light, alpha[j + 1][i], 0f, 0f, 1f);
                addBrandLogoVertex(consumer, pose, x1, y1, z, u1, v1, light, alpha[j + 1][i + 1], 0f, 0f, 1f);
                addBrandLogoVertex(consumer, pose, x1, y0, z, u1, v0, light, alpha[j][i + 1], 0f, 0f, 1f);
                addBrandLogoVertex(consumer, pose, x0, y0, z, u0, v0, light, alpha[j][i], 0f, 0f, 1f);
            }
        }
    }

    private static float glintBand(float s, float center, float halfWidth) {
        return MetalTextConsumer.glintBand(s, center, halfWidth);
    }

    private void addBrandLogoVertex(VertexConsumer consumer, PoseStack.Pose pose, float x, float y, float z, float u, float v,
                                    int packedLight, float alpha, float nx, float ny, float nz) {
        addBrandLogoVertex(consumer, pose, x, y, z, u, v, packedLight, alpha, nx, ny, nz, 1f);
    }

    private void addBrandLogoVertex(VertexConsumer consumer, PoseStack.Pose pose, float x, float y, float z, float u, float v,
                                    int packedLight, float alpha, float nx, float ny, float nz, float shade) {
        consumer.vertex(pose.pose(), x, y, z)
                .color(shade, shade, shade, alpha)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(pose.normal(), nx, ny, nz)
                .endVertex();
    }

    private void renderTextParts(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector) {
        if (state.textPartsRaw == null || state.textPartsRaw.isEmpty()) {
            return;
        }
        net.minecraft.client.gui.Font font = net.minecraft.client.Minecraft.getInstance().font;
        for (com.examplebett.render.TextParts.Slot slot : parseTextParts(state.textPartsRaw)) {
            if (slot == null || !slot.visible()) {
                continue;
            }
            net.minecraft.util.FormattedCharSequence text = textPartSequence(slot);
            float left = -font.width(text) / 2f;
            float top = -font.lineHeight / 2f;
            int light = slot.glow() ? TEXT_PART_FULL_BRIGHT : state.lightCoords;
            int rgb = slot.color() & 0xFFFFFF;
            int sideRgb = ((int) (((rgb >> 16) & 0xFF) * TEXT_PART_SIDE_SHADE) << 16)
                    | ((int) (((rgb >> 8) & 0xFF) * TEXT_PART_SIDE_SHADE) << 8)
                    | (int) ((rgb & 0xFF) * TEXT_PART_SIDE_SHADE);
            float pixel = TEXT_PART_PIXEL_SIZE * slot.scale();

            float pixelX = pixel * slot.width();
            float pixelY = pixel * slot.height();

            poseStack.pushPose();
            poseStack.translate(slot.x() + widthShiftFor(state, slot.x()), slot.y(), slot.z() + limoShiftFor(state, slot.z()));
            poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(slot.yaw()));
            poseStack.mulPose(com.mojang.math.Vector3f.XP.rotationDegrees(slot.pitch()));
            poseStack.mulPose(com.mojang.math.Vector3f.ZP.rotationDegrees(slot.roll()));
            int layers = slot.depth() > 0f
                    ? Math.min(TEXT_PART_MAX_LAYERS, Math.max(1, (int) Math.ceil(slot.depth() / TEXT_PART_LAYER_SPACING)))
                    : 0;
            for (int i = layers; i >= 1; i--) {
                poseStack.pushPose();
                poseStack.translate(0f, 0f, -slot.depth() * i / layers);
                poseStack.scale(pixelX, -pixelY, pixel);
                collector.submitText(poseStack, text, left, top, 0xFF000000 | sideRgb, light);
                poseStack.popPose();
            }

            poseStack.scale(pixelX, -pixelY, pixel);
            boolean drawn = false;
            int metal = com.examplebett.client.gui.CarColorPalette.metalIndex(rgb);
            if (metal >= 0) {
                submitMetalText(poseStack, collector, font, text, left, top, rgb, metal, light, state.brandLogoDetail >= 2);
                drawn = true;
            }
            if (!drawn) {
                collector.submitText(poseStack, text, left, top, 0xFF000000 | rgb, light);
            }
            poseStack.popPose();
        }
    }

    private void submitMetalText(PoseStack poseStack, GeometrySubmitter collector, net.minecraft.client.gui.Font font,
                                 net.minecraft.util.FormattedCharSequence text, float left, float top, int rgb, int metal,
                                 int light, boolean band) {
        float width = Math.max(1f, font.width(text));
        float height = font.lineHeight;
        collector.submitWrappedText(poseStack, text, left, top, light, (out, pose) ->
                new MetalTextConsumer(out, pose, left, top, width, height, rgb, metal,
                        band ? MetalTextConsumer.phase(pose, left + width / 2f, top + height / 2f) : Float.NaN));
    }

    private static void computeExhaustOutlets(CarRenderState state, PoseStack poseStack, float[] origin) {
        int outlets = com.examplebett.render.ExhaustParts.outletsOf(state.exhaustEnabledId);
        com.examplebett.render.PartTransforms.Transform6 t = state.exhaustTransform;
        if (outlets == 0 || t == null) {
            state.exhaustTarget.clientExhaustOutlets = null;
            return;
        }
        float[] relative = PoseMatrices.relative(origin, PoseMatrices.capture(poseStack));
        float[] result = new float[outlets * 6];
        for (int i = 0; i < outlets; i++) {
            float side = i == 0 ? 1f : -1f;
            float localX = t.x() * side;
            float[] position = PoseMatrices.transformPosition(relative,
                    localX + widthShiftFor(state, localX), t.y(), t.z() + limoShiftFor(state, t.z()));

            float ax = (float) Math.toRadians(t.angle());
            float ay = (float) Math.toRadians(t.angleY() * side);
            float y1 = (float) Math.sin(ax);
            float z1 = -(float) Math.cos(ax);
            float x2 = z1 * (float) Math.sin(ay);
            float z2 = z1 * (float) Math.cos(ay);
            float[] direction = PoseMatrices.transformDirection(relative, x2, y1, z2);
            result[i * 6] = position[0];
            result[i * 6 + 1] = position[1];
            result[i * 6 + 2] = position[2];
            result[i * 6 + 3] = direction[0];
            result[i * 6 + 4] = direction[1];
            result[i * 6 + 5] = direction[2];
        }
        state.exhaustTarget.clientExhaustOutlets = result;
    }

    private void renderTaxiSign(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector) {
        if (state.taxiSignEnabledIds == null || state.taxiSignEnabledIds.isEmpty()) {
            return;
        }
        Set<String> enabledIds = parseEnabledIds(state.taxiSignEnabledIds);
        net.minecraft.server.packs.resources.ResourceManager resourceManager =
                net.minecraft.client.Minecraft.getInstance().getResourceManager();
        com.examplebett.render.PartTransforms.Transform12 defaults = new com.examplebett.render.PartTransforms.Transform12(
                com.examplebett.entity.CarEntity.DEFAULT_TAXI_SIGN_X, com.examplebett.entity.CarEntity.DEFAULT_TAXI_SIGN_Y,
                com.examplebett.entity.CarEntity.DEFAULT_TAXI_SIGN_Z, 0f, 0f, com.examplebett.entity.CarEntity.DEFAULT_TAXI_SIGN_SCALE,
                1f, 1f, 1f, 1f, 1f, 1f);
        for (com.examplebett.render.TaxiSignParts.Option option : com.examplebett.render.TaxiSignParts.list(resourceManager)) {
            if (!enabledIds.contains(option.id())) {
                continue;
            }
            CompiledCarModel signModel = TaxiSignModel.get(resourceManager, option.modelId());
            if (signModel == null) {
                continue;
            }
            com.examplebett.render.PartTransforms.Transform12 t =
                    com.examplebett.render.PartTransforms.resolve12(state.taxiSignTransformsRaw, option.id(), defaults);
            poseStack.pushPose();
            poseStack.translate(t.x() + widthShiftFor(state, t.x()), t.y(), t.z() + limoShiftFor(state, t.z()));
            if (t.angleY() != 0f) {
                poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(t.angleY()));
            }
            if (t.angle() != 0f) {
                poseStack.mulPose(com.mojang.math.Vector3f.XP.rotationDegrees(t.angle()));
            }

            float stretchScaleX = (t.stretchXPos() + t.stretchXNeg()) / 2f;
            float stretchScaleY = (t.stretchYPos() + t.stretchYNeg()) / 2f;
            float stretchScaleZ = (t.stretchZPos() + t.stretchZNeg()) / 2f;
            float offsetX = t.scale() * OTHER_PART_STRETCH_HALF_EXTENT * (t.stretchXPos() - t.stretchXNeg()) / 2f;
            float offsetY = t.scale() * OTHER_PART_STRETCH_HALF_EXTENT * (t.stretchYPos() - t.stretchYNeg()) / 2f;
            float offsetZ = t.scale() * OTHER_PART_STRETCH_HALF_EXTENT * (t.stretchZPos() - t.stretchZNeg()) / 2f;
            if (offsetX != 0f || offsetY != 0f || offsetZ != 0f) {
                poseStack.translate(offsetX, offsetY, offsetZ);
            }
            if (t.scale() != 1f || stretchScaleX != 1f || stretchScaleY != 1f || stretchScaleZ != 1f) {
                poseStack.scale(t.scale() * stretchScaleX, t.scale() * stretchScaleY, t.scale() * stretchScaleZ);
            }
            for (CompiledCarModel.TextureGroup group : signModel.groups) {
                ResourceLocation texture = group.texture;
                final int tint = group.tintable ? state.color : 0xFFFFFF;
                RenderType renderType = groupRenderType(group, texture);
                collector.submitCustomGeometry(poseStack, renderType, (pose, vertexConsumer) ->
                        renderGroup(group.vertexData, pose, vertexConsumer, state.lightCoords, tint, BodyDeform.NONE, usesTriangles(group)));
            }
            poseStack.popPose();
        }
    }

    private void renderPoliceLight(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector) {
        if (state.policeLightEnabledIds == null || state.policeLightEnabledIds.isEmpty()) {
            return;
        }
        Set<String> enabledIds = parseEnabledIds(state.policeLightEnabledIds);
        net.minecraft.server.packs.resources.ResourceManager resourceManager =
                net.minecraft.client.Minecraft.getInstance().getResourceManager();
        com.examplebett.render.PartTransforms.Transform12 defaults = new com.examplebett.render.PartTransforms.Transform12(
                com.examplebett.entity.CarEntity.DEFAULT_POLICE_LIGHT_X, com.examplebett.entity.CarEntity.DEFAULT_POLICE_LIGHT_Y,
                com.examplebett.entity.CarEntity.DEFAULT_POLICE_LIGHT_Z, 0f, 0f, com.examplebett.entity.CarEntity.DEFAULT_POLICE_LIGHT_SCALE,
                1f, 1f, 1f, 1f, 1f, 1f);
        for (com.examplebett.render.PoliceLightParts.Option option : com.examplebett.render.PoliceLightParts.list(resourceManager)) {
            if (!enabledIds.contains(option.id())) {
                continue;
            }

            CompiledCarModel lightModel = option.modelId() == null ? null : PoliceLightModel.get(resourceManager, option.modelId());
            if (lightModel == null && option.modelId() != null) {
                continue;
            }
            com.examplebett.render.PartTransforms.Transform12 t =
                    com.examplebett.render.PartTransforms.resolve12(state.policeLightTransformsRaw, option.id(), defaults);
            poseStack.pushPose();
            poseStack.translate(t.x() + widthShiftFor(state, t.x()), t.y(), t.z() + limoShiftFor(state, t.z()));
            if (t.angleY() != 0f) {
                poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(t.angleY()));
            }
            if (t.angle() != 0f) {
                poseStack.mulPose(com.mojang.math.Vector3f.XP.rotationDegrees(t.angle()));
            }

            float stretchScaleX = (t.stretchXPos() + t.stretchXNeg()) / 2f;
            float stretchScaleY = (t.stretchYPos() + t.stretchYNeg()) / 2f;
            float stretchScaleZ = (t.stretchZPos() + t.stretchZNeg()) / 2f;
            float offsetX = t.scale() * OTHER_PART_STRETCH_HALF_EXTENT * (t.stretchXPos() - t.stretchXNeg()) / 2f;
            float offsetY = t.scale() * OTHER_PART_STRETCH_HALF_EXTENT * (t.stretchYPos() - t.stretchYNeg()) / 2f;
            float offsetZ = t.scale() * OTHER_PART_STRETCH_HALF_EXTENT * (t.stretchZPos() - t.stretchZNeg()) / 2f;
            if (offsetX != 0f || offsetY != 0f || offsetZ != 0f) {
                poseStack.translate(offsetX, offsetY, offsetZ);
            }
            if (t.scale() != 1f || stretchScaleX != 1f || stretchScaleY != 1f || stretchScaleZ != 1f) {
                poseStack.scale(t.scale() * stretchScaleX, t.scale() * stretchScaleY, t.scale() * stretchScaleZ);
            }
            for (CompiledCarModel.TextureGroup group : lightModel == null ? List.<CompiledCarModel.TextureGroup>of() : lightModel.groups) {
                ResourceLocation texture = group.texture;
                final int tint = group.tintable ? state.color : 0xFFFFFF;
                RenderType renderType = groupRenderType(group, texture);
                collector.submitCustomGeometry(poseStack, renderType, (pose, vertexConsumer) ->
                        renderGroup(group.vertexData, pose, vertexConsumer, state.lightCoords, tint, BodyDeform.NONE, usesTriangles(group)));
            }
            if (state.policeLightsOn) {
                if (com.examplebett.render.PoliceLightParts.isEmergencyBeacon(option.id())) {
                    renderEmergencyBeaconLights(state, poseStack, collector);
                }
                if (com.examplebett.render.PoliceLightParts.JP_LIGHT_BAR_ID.equals(option.id())) {
                    renderJpLightBarLights(state, poseStack, collector);
                }
                if (com.examplebett.render.PoliceLightParts.hasUsLightBarLights(option.id())) {
                    renderUsLightBarLights(state, poseStack, collector, false);
                }
                if (com.examplebett.render.PoliceLightParts.isEuLightBar(option.id())) {
                    renderUsLightBarLights(state, poseStack, collector, true);
                }
                if (com.examplebett.render.PoliceLightParts.isEuPattern(option.id())) {
                    renderEuPatternLights(state, poseStack, collector,
                            com.examplebett.render.PoliceLightParts.isEuPatternLeftFirst(option.id()));
                }
                if (com.examplebett.render.PoliceLightParts.JP_PATTERN_ID.equals(option.id())) {
                    renderJpPatternLights(state, poseStack, collector);
                }
            }
            poseStack.popPose();
        }
    }

    private static final float ROTATING_LIGHT_ROTATE_DEG_PER_TICK = 15f;

    private static final int QUAD_FLASH_ON_TICKS = 3;
    private static final int QUAD_FLASH_GAP_TICKS = 3;
    private static final int QUAD_FLASH_GROUP_GAP_TICKS = 4;
    private static final int QUAD_FLASH_GROUP_SLOT_TICKS =
            2 * QUAD_FLASH_ON_TICKS + QUAD_FLASH_GAP_TICKS + QUAD_FLASH_GROUP_GAP_TICKS;
    private static final int QUAD_FLASH_CYCLE_TICKS = QUAD_FLASH_GROUP_SLOT_TICKS * 2;

    private record QuadFlashState(boolean groupATurn, boolean flashOn) {
    }

    private static QuadFlashState computeQuadFlashState(int age) {
        int cyclePos = age % QUAD_FLASH_CYCLE_TICKS;
        boolean groupATurn = cyclePos < QUAD_FLASH_GROUP_SLOT_TICKS;
        int localPhase = groupATurn ? cyclePos : cyclePos - QUAD_FLASH_GROUP_SLOT_TICKS;
        boolean flashOn = localPhase < QUAD_FLASH_ON_TICKS
                || (localPhase >= QUAD_FLASH_ON_TICKS + QUAD_FLASH_GAP_TICKS
                        && localPhase < 2 * QUAD_FLASH_ON_TICKS + QUAD_FLASH_GAP_TICKS);
        return new QuadFlashState(groupATurn, flashOn);
    }

    private static final float EMERGENCY_BEACON_RADIUS = 0.18f;

    private static final float EMERGENCY_BEACON_PIVOT_Z = -0.165f;

    private static final float EMERGENCY_BEACON_GLOW_SIZE = 0.36f;

    private static final float EMERGENCY_BEACON_SECOND_SET_OFFSET_DEG = 90f;

    private void renderEmergencyBeaconLights(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector) {
        float rotateDeg = (state.age * ROTATING_LIGHT_ROTATE_DEG_PER_TICK) % 360f;
        QuadFlashState flash = computeQuadFlashState(state.age);
        if (!flash.flashOn()) {
            return;
        }
        float setRotateDeg = flash.groupATurn() ? rotateDeg : rotateDeg + EMERGENCY_BEACON_SECOND_SET_OFFSET_DEG;
        submitRotatingLightGlow(poseStack, collector, 0f, 0f, EMERGENCY_BEACON_PIVOT_Z,
                EMERGENCY_BEACON_RADIUS, EMERGENCY_BEACON_GLOW_SIZE, setRotateDeg, 1f, 1f, 0f, 0f);
        submitRotatingLightGlow(poseStack, collector, 0f, 0f, EMERGENCY_BEACON_PIVOT_Z,
                EMERGENCY_BEACON_RADIUS, EMERGENCY_BEACON_GLOW_SIZE, setRotateDeg + 180f, 1f, 1f, 0f, 0f);
    }

    private void submitRotatingLightGlow(PoseStack poseStack, GeometrySubmitter collector,
                                          float pivotX, float pivotY, float pivotZ, float radius, float glowSize,
                                          float rotateDeg, float alpha, float tintR, float tintG, float tintB) {
        double rotateRad = Math.toRadians(rotateDeg);
        float dx = (float) (Math.sin(rotateRad) * radius);
        float dz = (float) (Math.cos(rotateRad) * radius);
        submitGlow(poseStack, collector, HEADLIGHT_GLOW_TEXTURE,
                pivotX + dx, pivotY, pivotZ + dz, glowSize, 0f,
                tintR, tintG, tintB, alpha);
    }

    private static final float LIGHT_BAR_ROTATOR_RADIUS = 0.15f;

    private static final float LIGHT_BAR_ROTATOR_GLOW_SIZE = 0.30f;

    private static final float[][] JP_LIGHT_BAR_ROTATOR_POSITIONS = {
            {-0.80f, 0.181f, -0.205f},
            {-0.35f, 0.179f, -0.015f},
            {0.35f, 0.177f, 0.011f},
            {0.80f, 0.173f, -0.205f},
    };

    private void renderJpLightBarLights(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector) {
        float rotateDeg = (state.age * ROTATING_LIGHT_ROTATE_DEG_PER_TICK) % 360f;
        QuadFlashState flash = computeQuadFlashState(state.age);
        if (!flash.flashOn()) {
            return;
        }

        for (int i = 0; i < JP_LIGHT_BAR_ROTATOR_POSITIONS.length; i++) {
            boolean isCenterSet = i == 1 || i == 2;
            if (isCenterSet != flash.groupATurn()) {
                continue;
            }
            float[] pos = JP_LIGHT_BAR_ROTATOR_POSITIONS[i];
            submitRotatingLightGlow(poseStack, collector, pos[0], pos[1], pos[2],
                    LIGHT_BAR_ROTATOR_RADIUS, LIGHT_BAR_ROTATOR_GLOW_SIZE, rotateDeg, 1f, 1f, 0f, 0f);
            submitRotatingLightGlow(poseStack, collector, pos[0], pos[1], pos[2],
                    LIGHT_BAR_ROTATOR_RADIUS, LIGHT_BAR_ROTATOR_GLOW_SIZE, rotateDeg + 180f, 1f, 1f, 0f, 0f);
        }
    }

    private static final float[][] US_LIGHT_BAR_ROTATOR_POSITIONS = {
            {-0.80f, 0.138f, 0.015f},
            {-0.35f, 0.138f, 0.015f},
            {0.35f, 0.138f, 0.015f},
            {0.80f, 0.138f, 0.015f},
    };

    private void renderUsLightBarLights(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector,
                                        boolean allBlue) {
        float rotateDeg = (state.age * ROTATING_LIGHT_ROTATE_DEG_PER_TICK) % 360f;
        QuadFlashState flash = computeQuadFlashState(state.age);
        if (!flash.flashOn()) {
            return;
        }

        for (int i = 0; i < US_LIGHT_BAR_ROTATOR_POSITIONS.length; i++) {
            boolean isCenterSet = i == 1 || i == 2;
            if (isCenterSet != flash.groupATurn()) {
                continue;
            }
            float[] pos = US_LIGHT_BAR_ROTATOR_POSITIONS[i];
            boolean isBlue = allBlue || i >= 2;
            float tintR = isBlue ? 0f : 1f;
            float tintB = isBlue ? 1f : 0f;
            submitRotatingLightGlow(poseStack, collector, pos[0], pos[1], pos[2],
                    LIGHT_BAR_ROTATOR_RADIUS, LIGHT_BAR_ROTATOR_GLOW_SIZE, rotateDeg, 1f, tintR, 0f, tintB);
            submitRotatingLightGlow(poseStack, collector, pos[0], pos[1], pos[2],
                    LIGHT_BAR_ROTATOR_RADIUS, LIGHT_BAR_ROTATOR_GLOW_SIZE, rotateDeg + 180f, 1f, tintR, 0f, tintB);
        }
    }

    private static final int EU_PATTERN_STEP_TICKS = 2;

    private static final int JP_PATTERN_DARK_TICKS = 10;

    private static final float[] SIDE_PAIR_PATTERN_X = {-0.475f, -0.325f, 0.325f, 0.475f};

    private static final boolean SIDE_PAIR_PATTERN_SINGLE_LIGHT = true;

    private void submitSidePairPatternGlow(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector,
                                           int index, float tintR, float tintB) {
        float x = SIDE_PAIR_PATTERN_X[index];
        if (SIDE_PAIR_PATTERN_SINGLE_LIGHT) {

            if (state.age % EU_PATTERN_STEP_TICKS >= EU_PATTERN_STEP_TICKS / 2) {
                return;
            }

            x = index < 2 ? (SIDE_PAIR_PATTERN_X[0] + SIDE_PAIR_PATTERN_X[1]) / 2f
                    : (SIDE_PAIR_PATTERN_X[2] + SIDE_PAIR_PATTERN_X[3]) / 2f;
        }
        float[] usPos = US_LIGHT_BAR_ROTATOR_POSITIONS[index];
        submitGlow(poseStack, collector, HEADLIGHT_GLOW_TEXTURE, x, usPos[1], usPos[2],
                LIGHT_BAR_ROTATOR_GLOW_SIZE, 0f, tintR, 0f, tintB, 1f);
    }

    private void renderEuPatternLights(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector,
                                       boolean leftFirst) {
        int step = (state.age / EU_PATTERN_STEP_TICKS) % 8;
        boolean firstSide = step < 4;
        boolean leftSide = firstSide == leftFirst;

        int outer = leftSide ? 0 : 3;
        int inner = leftSide ? 1 : 2;
        submitSidePairPatternGlow(state, poseStack, collector, (step % 2 == 0) ? outer : inner, 0f, 1f);
    }

    private void renderJpPatternLights(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector) {
        int flashTicks = EU_PATTERN_STEP_TICKS * 4;
        int pos = state.age % (flashTicks + JP_PATTERN_DARK_TICKS);
        if (pos >= flashTicks) {
            return;
        }
        boolean outerTurn = (pos / EU_PATTERN_STEP_TICKS) % 2 == 0;
        submitSidePairPatternGlow(state, poseStack, collector, outerTurn ? 0 : 1, 1f, 0f);
        submitSidePairPatternGlow(state, poseStack, collector, outerTurn ? 3 : 2, 1f, 0f);
    }

    private static void applyGroupTransform(PoseStack poseStack, CarRenderState state,
                                             CompiledCarModel.TextureGroup group, boolean spinning, boolean rotorSpinning,
                                             float extraZ) {

        if ((spinning || rotorSpinning)
                && (state.suspensionPitchDegrees != 0f || state.suspensionRollDegrees != 0f)) {
            poseStack.translate(0.0, state.tumblePivotHeight, 0.0);
            if (state.suspensionRollDegrees != 0f) {
                poseStack.mulPose(com.mojang.math.Vector3f.ZP.rotationDegrees(-state.suspensionRollDegrees));
            }
            if (state.suspensionPitchDegrees != 0f) {
                poseStack.mulPose(com.mojang.math.Vector3f.XP.rotationDegrees(-state.suspensionPitchDegrees));
            }
            poseStack.translate(0.0, -state.tumblePivotHeight, 0.0);
        }
        if (spinning) {
            ObjModel.WheelPivot pivot = state.compiledModel.wheelPivots.get(group.wheelGroup);

            if (state.bodyHeightOffset != 0f) {
                poseStack.translate(0.0, -state.bodyHeightOffset, 0.0);
            }

            float sideSign = embeddedWheelSideSign(pivot);
            poseStack.translate(embeddedWheelX(state, pivot), embeddedWheelY(state, pivot),
                    embeddedWheelZ(state, pivot.centerZ + extraZ, pivot.front));

            if (state.wheelCamberDegrees != 0f) {
                poseStack.mulPose(com.mojang.math.Vector3f.ZP.rotationDegrees(sideSign * state.wheelCamberDegrees));
            }

            if (pivot.front) {
                poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(state.frontWheelSteerDegrees));
            }

            float rollDegrees = state.wheelRotationDegrees + (pivot.front ? 0f : state.rearWheelExtraSpinDegrees);
            poseStack.mulPose(wheelAxisRotation(pivot, rollDegrees));

            float axialScale = state.wheelScale * state.wheelWidthScale;
            if (axialScale != 1f || state.wheelScale != 1f) {
                poseStack.scale(pivot.axisX >= 0.5f ? axialScale : state.wheelScale,
                        pivot.axisY >= 0.5f ? axialScale : state.wheelScale,
                        pivot.axisZ >= 0.5f ? axialScale : state.wheelScale);
            }
            poseStack.translate(-pivot.centerX, -pivot.centerY, -pivot.centerZ);
        } else if (rotorSpinning) {
            ObjModel.RotorPivot pivot = state.compiledModel.rotorPivots.get(group.rotorGroup);
            poseStack.translate(pivot.centerX, pivot.centerY, pivot.centerZ);
            poseStack.mulPose(rotorAxisRotation(pivot, state.rotorRotationDegrees));
            poseStack.translate(-pivot.centerX, -pivot.centerY, -pivot.centerZ);
        }
    }

    private static float embeddedWheelSideSign(ObjModel.WheelPivot pivot) {
        return pivot.centerX >= 0f ? 1f : -1f;
    }

    private static float embeddedWheelX(CarRenderState state, ObjModel.WheelPivot pivot) {
        float x = pivot.centerX + embeddedWheelSideSign(pivot) * state.wheelTrackHalfWidthOffset;
        return x + widthShiftFor(state, x);
    }

    private static float embeddedWheelY(CarRenderState state, ObjModel.WheelPivot pivot) {
        return pivot.centerY + state.wheelAxleHeightOffset;
    }

    private static float embeddedWheelZ(CarRenderState state, float baseZ, boolean front) {
        float z = baseZ + (front ? state.wheelFrontZOffset : state.wheelRearZOffset);
        return z + limoShiftFor(state, z);
    }

    private static com.mojang.math.Quaternion wheelAxisRotation(ObjModel.WheelPivot pivot, float degrees) {
        if (pivot.axisX >= 0.5f) {
            return com.mojang.math.Vector3f.XP.rotationDegrees(degrees);
        }
        if (pivot.axisY >= 0.5f) {
            return com.mojang.math.Vector3f.YP.rotationDegrees(degrees);
        }
        return com.mojang.math.Vector3f.ZP.rotationDegrees(degrees);
    }

    private static com.mojang.math.Quaternion rotorAxisRotation(ObjModel.RotorPivot pivot, float degrees) {
        if (pivot.axisX >= 0.5f) {
            return com.mojang.math.Vector3f.XP.rotationDegrees(degrees);
        }
        if (pivot.axisY >= 0.5f) {
            return com.mojang.math.Vector3f.YP.rotationDegrees(degrees);
        }
        return com.mojang.math.Vector3f.ZP.rotationDegrees(degrees);
    }

    private static final float PLATE_HALF_WIDTH = 0.45f;

    private void submitPlate(PoseStack poseStack, GeometrySubmitter collector, int packedLight, ResourceLocation texture,
                              float x, float y, float z, float scale, float yawDeg, float pitchDeg, float rollDeg, float aspect) {
        poseStack.pushPose();
        poseStack.translate(x, y, z);
        poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(yawDeg));
        poseStack.mulPose(com.mojang.math.Vector3f.XP.rotationDegrees(pitchDeg));
        poseStack.mulPose(com.mojang.math.Vector3f.ZP.rotationDegrees(rollDeg));
        RenderType plateType = RenderType.entityCutoutNoCull(texture);
        collector.submitCustomGeometry(poseStack, plateType, (pose, vertexConsumer) ->
                renderPlate(pose, vertexConsumer, packedLight, scale, aspect));
        poseStack.popPose();
    }

    private void renderPlate(PoseStack.Pose pose, VertexConsumer consumer, int packedLight, float scale, float aspect) {
        float halfW = PLATE_HALF_WIDTH * scale;
        float halfH = (PLATE_HALF_WIDTH / aspect) * scale;
        addPlateVertex(consumer, pose, -halfW, -halfH, 0f, 0f, 1f, packedLight);
        addPlateVertex(consumer, pose, halfW, -halfH, 0f, 1f, 1f, packedLight);
        addPlateVertex(consumer, pose, halfW, halfH, 0f, 1f, 0f, packedLight);
        addPlateVertex(consumer, pose, -halfW, halfH, 0f, 0f, 0f, packedLight);
    }

    private void addPlateVertex(VertexConsumer consumer, PoseStack.Pose pose, float x, float y, float z, float u, float v, int packedLight) {
        consumer.vertex(pose.pose(), x, y, z)
                .color(1f, 1f, 1f, 1f)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(pose.normal(), 0f, 1f, 0f)
                .endVertex();
    }

    private static final ResourceLocation HEADLIGHT_GLOW_TEXTURE =
            AiObjectMod.id("textures/entity/lights/headlight_glow.png");
    private static final ResourceLocation TAILLIGHT_GLOW_TEXTURE =
            AiObjectMod.id("textures/entity/lights/taillight_glow.png");
    private static final float HEADLIGHT_GLOW_SIZE = 0.35f;
    private static final float TAILLIGHT_GLOW_SIZE = 0.25f;

    private static final int FULL_BRIGHT_LIGHT = 0xF000F0;

    private void submitGlow(PoseStack poseStack, GeometrySubmitter collector, ResourceLocation texture,
                             float x, float y, float z, float halfSize, float yawDeg,
                             float tintR, float tintG, float tintB) {
        submitGlow(poseStack, collector, texture, x, y, z, halfSize, yawDeg, tintR, tintG, tintB, 1f);
    }

    private void submitGlow(PoseStack poseStack, GeometrySubmitter collector, ResourceLocation texture,
                             float x, float y, float z, float halfSize, float yawDeg,
                             float tintR, float tintG, float tintB, float alpha) {
        poseStack.pushPose();
        poseStack.translate(x, y, z);

        RenderType glowType = RenderType.itemEntityTranslucentCull(texture);
        submitGlowQuad(poseStack, collector, glowType, halfSize, yawDeg, 0f, tintR, tintG, tintB, alpha);
        submitGlowQuad(poseStack, collector, glowType, halfSize, yawDeg + 90f, 0f, tintR, tintG, tintB, alpha);
        submitGlowQuad(poseStack, collector, glowType, halfSize, yawDeg, 90f, tintR, tintG, tintB, alpha);
        poseStack.popPose();
    }

    private void submitGlowQuad(PoseStack poseStack, GeometrySubmitter collector, RenderType glowType,
                                 float halfSize, float yawDeg, float pitchDeg,
                                 float tintR, float tintG, float tintB, float alpha) {
        poseStack.pushPose();
        poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(yawDeg));
        poseStack.mulPose(com.mojang.math.Vector3f.XP.rotationDegrees(pitchDeg));
        collector.late().submitCustomGeometry(poseStack, glowType, (pose, vertexConsumer) ->
                renderGlow(pose, vertexConsumer, halfSize, tintR, tintG, tintB, alpha));
        poseStack.popPose();
    }

    private void renderGlow(PoseStack.Pose pose, VertexConsumer consumer, float halfSize,
                             float tintR, float tintG, float tintB, float alpha) {
        addGlowVertex(consumer, pose, -halfSize, -halfSize, 0f, 0f, 1f, tintR, tintG, tintB, alpha);
        addGlowVertex(consumer, pose, halfSize, -halfSize, 0f, 1f, 1f, tintR, tintG, tintB, alpha);
        addGlowVertex(consumer, pose, halfSize, halfSize, 0f, 1f, 0f, tintR, tintG, tintB, alpha);
        addGlowVertex(consumer, pose, -halfSize, halfSize, 0f, 0f, 0f, tintR, tintG, tintB, alpha);

        addGlowVertex(consumer, pose, -halfSize, -halfSize, 0f, 0f, 1f, tintR, tintG, tintB, alpha);
        addGlowVertex(consumer, pose, -halfSize, halfSize, 0f, 0f, 0f, tintR, tintG, tintB, alpha);
        addGlowVertex(consumer, pose, halfSize, halfSize, 0f, 1f, 0f, tintR, tintG, tintB, alpha);
        addGlowVertex(consumer, pose, halfSize, -halfSize, 0f, 1f, 1f, tintR, tintG, tintB, alpha);
    }

    private void addGlowVertex(VertexConsumer consumer, PoseStack.Pose pose, float x, float y, float z, float u, float v,
                                float tintR, float tintG, float tintB, float alpha) {
        consumer.vertex(pose.pose(), x, y, z)
                .color(tintR, tintG, tintB, alpha)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(FULL_BRIGHT_LIGHT)
                .normal(pose.normal(), 0f, 1f, 0f)
                .endVertex();
    }

    private record BodyDeform(boolean active, float bodyMinY,
                               float lipStretchScale, float lipWidthScale, float lipBandHeight, float lipFrontExtendScale,
                               float limoAnchorZ, float limoStretchM, float limoZoneM,
                               float widthAnchorX, float widthStretchM, float widthZoneM) {
        static final BodyDeform NONE = new BodyDeform(false, Float.NaN, 1f, 1f, 0f, 1f, Float.NaN, 0f, 0f, Float.NaN, 0f, 0f);

        static BodyDeform from(CarRenderState state) {
            boolean lipActive = !Float.isNaN(state.compiledModel.bodyMinY)
                    && (state.lipStretchScale != 1f || state.lipWidthScale != 1f || state.lipFrontExtendScale != 1f);
            boolean limoActive = !Float.isNaN(state.compiledModel.bodyCenterZ) && state.limoStretchCm != 0f;
            boolean widthActive = !Float.isNaN(state.compiledModel.bodyCenterX) && state.widthStretchCm != 0f;
            if (!lipActive && !limoActive && !widthActive) {
                return NONE;
            }

            float limoAnchorZ = state.compiledModel.bodyCenterZ + state.limoCenterOffsetCm / 100f;

            float widthAnchorX = state.compiledModel.bodyCenterX + state.widthCenterOffsetCm / 100f;
            return new BodyDeform(true, state.compiledModel.bodyMinY,
                    state.lipStretchScale, state.lipWidthScale, state.lipBandHeightCm / 100f, state.lipFrontExtendScale,
                    limoAnchorZ, state.limoStretchCm / 100f, state.limoZoneCm / 100f,
                    widthAnchorX, state.widthStretchCm / 100f, state.widthZoneCm / 100f);
        }
    }

    private static boolean usesTriangles(CompiledCarModel.TextureGroup group) {
        return !group.translucent && LegacyShaderFix.isAvailable();
    }

    private static final ResourceLocation[] METAL_PAINT_TEXTURES = {
            AiObjectMod.id("textures/entity/paint/chrome.png"),
            AiObjectMod.id("textures/entity/paint/gold.png"),
            AiObjectMod.id("textures/entity/paint/metallic_silver.png")
    };

    private static final ResourceLocation BRONZE_PAINT_TEXTURE = AiObjectMod.id("textures/entity/paint/bronze.png");

    private static ResourceLocation metalPaintTexture(CompiledCarModel.TextureGroup group, ResourceLocation texture, boolean usesPaintMask, int color) {
        if (!group.tintable || usesPaintMask || !CompiledCarModel.FALLBACK_TEXTURE.equals(texture)) {
            return texture;
        }
        int metal = com.examplebett.client.gui.CarColorPalette.metalIndex(color);
        if (metal >= 0) {
            return METAL_PAINT_TEXTURES[metal];
        }
        return (color & 0xFFFFFF) == com.examplebett.client.gui.CarColorPalette.EXTRA_RGB[com.examplebett.client.gui.CarColorPalette.EXTRA_BRONZE]
                ? BRONZE_PAINT_TEXTURE
                : texture;
    }

    private static RenderType groupRenderType(CompiledCarModel.TextureGroup group, ResourceLocation texture) {
        if (usesTriangles(group)) {
            return LegacyShaderFix.cutoutType(texture, group.noCull);
        }
        return group.translucent
                ? RenderType.itemEntityTranslucentCull(texture)
                : group.noCull
                        ? RenderType.entityCutoutNoCull(texture)
                        : RenderType.entityCutout(texture);
    }

    private void renderGroup(float[] data, PoseStack.Pose pose, VertexConsumer vertexConsumer, int packedLight, int color, BodyDeform deform, boolean triangles) {
        float tintR = ((color >> 16) & 0xFF) / 255f;
        float tintG = ((color >> 8) & 0xFF) / 255f;
        float tintB = (color & 0xFF) / 255f;
        if (triangles) {

            LegacyShaderFix.markBuffer(vertexConsumer);
        }

        int legacyStart = LegacyShaderFix.legacyBegin(vertexConsumer);
        for (int i = 0; i < data.length; i += 36) {
            addVertex(vertexConsumer, pose, data, i, packedLight, tintR, tintG, tintB, deform);
            addVertex(vertexConsumer, pose, data, i + 12, packedLight, tintR, tintG, tintB, deform);
            addVertex(vertexConsumer, pose, data, i + 24, packedLight, tintR, tintG, tintB, deform);
            if (!triangles) {
                addVertex(vertexConsumer, pose, data, i + 24, packedLight, tintR, tintG, tintB, deform);
            }
        }
        LegacyShaderFix.legacyEnd(vertexConsumer, legacyStart, data, pose, triangles);
    }

    private static float lipWeight(float y, float bodyMinY, float bandHeight) {
        if (bandHeight <= 0f) return 0f;
        float t = (bodyMinY + bandHeight - y) / bandHeight;
        t = Math.max(0f, Math.min(1f, t));
        return t * t * (3f - 2f * t);
    }

    private static float limoWeight(float z, float anchorZ, float zoneM) {
        if (zoneM <= 0f) return 0f;
        float t = (anchorZ - z) / zoneM;
        t = Math.max(0f, Math.min(1f, t));
        return t * t * (3f - 2f * t);
    }

    private static float limoShift(float z, float anchorZ, float zoneM, float stretchM) {
        return -limoWeight(z, anchorZ, zoneM) * stretchM;
    }

    private static float widthWeight(float x, float anchorX, float zoneM) {
        if (zoneM <= 0f) return 0f;
        float t = (anchorX - x) / zoneM;
        t = Math.max(0f, Math.min(1f, t));
        return t * t * (3f - 2f * t);
    }

    private static float widthShift(float x, float anchorX, float zoneM, float stretchM) {
        return -widthWeight(x, anchorX, zoneM) * stretchM;
    }

    private static float limoShiftFor(CarRenderState state, float z) {
        if (Float.isNaN(state.compiledModel.bodyCenterZ) || state.limoStretchCm == 0f) {
            return 0f;
        }
        float anchorZ = state.compiledModel.bodyCenterZ + state.limoCenterOffsetCm / 100f;
        return limoShift(z, anchorZ, state.limoZoneCm / 100f, state.limoStretchCm / 100f);
    }

    private static float widthShiftFor(CarRenderState state, float x) {
        if (Float.isNaN(state.compiledModel.bodyCenterX) || state.widthStretchCm == 0f) {
            return 0f;
        }
        float anchorX = state.compiledModel.bodyCenterX + state.widthCenterOffsetCm / 100f;
        return widthShift(x, anchorX, state.widthZoneCm / 100f, state.widthStretchCm / 100f);
    }

    private void addVertex(VertexConsumer consumer, PoseStack.Pose pose, float[] data, int offset, int packedLight, float tintR, float tintG, float tintB,
                            BodyDeform deform) {
        float x = data[offset];
        float y = data[offset + 1];
        float z = data[offset + 2];
        if (deform.active()) {
            float weight = lipWeight(y, deform.bodyMinY(), deform.lipBandHeight());
            if (weight > 0f) {
                y -= weight * (deform.lipStretchScale() - 1f) * deform.lipBandHeight();
                x += weight * x * (deform.lipWidthScale() - 1f);

                z += weight * Math.max(z, 0f) * (deform.lipFrontExtendScale() - 1f);
            }
            z += limoShift(z, deform.limoAnchorZ(), deform.limoZoneM(), deform.limoStretchM());
            x += widthShift(x, deform.widthAnchorX(), deform.widthZoneM(), deform.widthStretchM());
        }

        consumer.vertex(pose.pose(), x, y, z)
                .color(data[offset + 5] * tintR, data[offset + 6] * tintG, data[offset + 7] * tintB, data[offset + 8])
                .uv(data[offset + 3], data[offset + 4])
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(pose.normal(), data[offset + 9], data[offset + 10], data[offset + 11])
                .endVertex();
    }

    private static final float REFLECTION_STRENGTH = 0.45f;

    private static final float REFLECTION_ANGLE_LOW = 0.85f;
    private static final float REFLECTION_ANGLE_HIGH = 0.99f;

    private static final float REFLECTION_NORMAL_OFFSET = 0.001f;

    private static float fakeReflectionBoost(PoseStack.Pose pose, float x, float y, float z, float nx, float ny, float nz, int packedLight) {
        com.mojang.math.Vector3f normal = new com.mojang.math.Vector3f(nx, ny, nz);
        normal.transform(pose.normal());
        if (normal.dot(normal) < 1e-8f) {
            return 0f;
        }
        normal.normalize();

        com.mojang.math.Vector4f viewDirH = new com.mojang.math.Vector4f(x, y, z, 1f);
        viewDirH.transform(pose.pose());
        com.mojang.math.Vector3f viewDir = new com.mojang.math.Vector3f(viewDirH.x(), viewDirH.y(), viewDirH.z());
        if (viewDir.dot(viewDir) < 1e-8f) {
            return 0f;
        }
        viewDir.normalize();

        float vDotN = viewDir.dot(normal);
        com.mojang.math.Vector3f reflectDir = normal.copy();
        reflectDir.mul(-2f * vDotN);
        reflectDir.add(viewDir);

        float t = (reflectDir.y() - REFLECTION_ANGLE_LOW) / (REFLECTION_ANGLE_HIGH - REFLECTION_ANGLE_LOW);
        t = Math.max(0f, Math.min(1f, t));
        float highlight = t * t * (3f - 2f * t);

        int blockLight = packedLight & 0xFFFF;
        int skyLight = (packedLight >> 16) & 0xFFFF;
        float lightFactor = Math.max(blockLight, skyLight) / 240f;

        return highlight * lightFactor * REFLECTION_STRENGTH;
    }

    private void renderReflectionOverlay(float[] data, PoseStack.Pose pose, VertexConsumer vertexConsumer, int packedLight, BodyDeform deform) {
        for (int i = 0; i < data.length; i += 36) {
            addReflectionVertex(vertexConsumer, pose, data, i, packedLight, deform);
            addReflectionVertex(vertexConsumer, pose, data, i + 12, packedLight, deform);
            addReflectionVertex(vertexConsumer, pose, data, i + 24, packedLight, deform);
            addReflectionVertex(vertexConsumer, pose, data, i + 24, packedLight, deform);
        }
    }

    private void addReflectionVertex(VertexConsumer consumer, PoseStack.Pose pose, float[] data, int offset, int packedLight, BodyDeform deform) {
        float x = data[offset];
        float y = data[offset + 1];
        float z = data[offset + 2];
        if (deform.active()) {
            float weight = lipWeight(y, deform.bodyMinY(), deform.lipBandHeight());
            if (weight > 0f) {
                y -= weight * (deform.lipStretchScale() - 1f) * deform.lipBandHeight();
                x += weight * x * (deform.lipWidthScale() - 1f);
                z += weight * Math.max(z, 0f) * (deform.lipFrontExtendScale() - 1f);
            }
            z += limoShift(z, deform.limoAnchorZ(), deform.limoZoneM(), deform.limoStretchM());
            x += widthShift(x, deform.widthAnchorX(), deform.widthZoneM(), deform.widthStretchM());
        }

        float nx = data[offset + 9];
        float ny = data[offset + 10];
        float nz = data[offset + 11];
        float boost = fakeReflectionBoost(pose, x, y, z, nx, ny, nz, packedLight);
        x += nx * REFLECTION_NORMAL_OFFSET;
        y += ny * REFLECTION_NORMAL_OFFSET;
        z += nz * REFLECTION_NORMAL_OFFSET;

        consumer.vertex(pose.pose(), x, y, z)
                .color(1f, 1f, 1f, boost)
                .uv(data[offset + 3], data[offset + 4])
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(pose.normal(), nx, ny, nz)
                .endVertex();
    }
}
*/
//?}
//? if <1.17 && >=1.16.2 {
/*
//~ axis_mulpose_compat
package com.examplebett.client.render;

import com.examplebett.AiObjectMod;
import com.examplebett.entity.CarEntity;
import com.examplebett.render.CompiledCarModel;
import com.examplebett.render.ObjModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CarEntityRenderer extends EntityRenderer<CarEntity> {

    private static final double LOD_WHEEL_DISTANCE = 32.0;
    private static final double LOD_WHEEL_DISTANCE_SQ = LOD_WHEEL_DISTANCE * LOD_WHEEL_DISTANCE;

    private static final double BRAND_LOGO_FLAT_DISTANCE = 5.0;
    private static final double BRAND_LOGO_FLAT_DISTANCE_SQ = BRAND_LOGO_FLAT_DISTANCE * BRAND_LOGO_FLAT_DISTANCE;
    private static final double BRAND_LOGO_HIDE_DISTANCE = 48.0;
    private static final double BRAND_LOGO_HIDE_DISTANCE_SQ = BRAND_LOGO_HIDE_DISTANCE * BRAND_LOGO_HIDE_DISTANCE;

    private static final boolean[] WHEEL_IS_FRONT_4 = {true, true, false, false};
    private static final boolean[] WHEEL_IS_FRONT_6 = {true, true, false, false, false, false};
    private static final float[] WHEEL_CAMBER_SIGN_4 = {1f, -1f, 1f, -1f};
    private static final float[] WHEEL_CAMBER_SIGN_6 = {1f, -1f, 1f, -1f, 1f, -1f};

    private static final boolean[] WHEEL_IS_FRONT_8 = {true, true, false, false, false, false, false, false};
    private static final float[] WHEEL_CAMBER_SIGN_8 = {1f, -1f, 1f, -1f, 1f, -1f, 1f, -1f};

    private static final boolean[] WHEEL_IS_FRONT_2 = {true, false};
    private static final float[] WHEEL_CAMBER_SIGN_2 = {1f, -1f};
    private static final float[] WHEEL_OFFSET_Z_BUF = new float[8];
    private static final float[] WHEEL_OFFSET_X_BUF = new float[8];

    private static final Map<String, Set<String>> ENABLED_IDS_CACHE = new HashMap<>();

    private static Set<String> parseEnabledIds(String csv) {
        return ENABLED_IDS_CACHE.computeIfAbsent(csv, s -> new HashSet<>(java.util.Arrays.asList(s.split(","))));
    }

    private static final float WHEELIE_PIVOT_Z_FACTOR = 0.85f;

    private static final int BLINK_ON_TICKS = 10;
    private static final int BLINK_OFF_TICKS = 10;

    @FunctionalInterface
    private interface GeometryRenderer {
        void render(PoseStack.Pose pose, VertexConsumer consumer);
    }

    @FunctionalInterface
    private interface ModelDrawer {
        void draw(PoseStack poseStack, VertexConsumer consumer);
    }

    @FunctionalInterface
    private interface TextWrapper {
        VertexConsumer wrap(VertexConsumer out, PoseStack.Pose pose);
    }

    @FunctionalInterface
    private interface GeometrySubmitter {
        void submitCustomGeometry(PoseStack pose, RenderType type, GeometryRenderer renderer);

        default void submitWrappedText(PoseStack pose, net.minecraft.util.FormattedCharSequence text, float x, float y, int light,
                                       TextWrapper wrapper) {
            submitText(pose, text, x, y, 0xFFFFFFFF, light);
        }

        default void submitModel(PoseStack pose, RenderType type, ModelDrawer drawer) {

            throw new UnsupportedOperationException();
        }

        default GeometrySubmitter late() {
            return this;
        }

        default void submitText(PoseStack pose, net.minecraft.util.FormattedCharSequence text, float x, float y, int argb, int light) {
        }
    }

    private static GeometrySubmitter immediateSubmitter(MultiBufferSource bufferSource) {
        return new GeometrySubmitter() {
            @Override
            public void submitCustomGeometry(PoseStack pose, RenderType type, GeometryRenderer renderer) {
                renderer.render(pose.last(), bufferSource.getBuffer(type));
            }

            @Override
            public void submitModel(PoseStack pose, RenderType type, ModelDrawer drawer) {
                drawer.draw(pose, bufferSource.getBuffer(type));
            }

            @Override
            public void submitText(PoseStack pose, net.minecraft.util.FormattedCharSequence text, float x, float y, int argb, int light) {
                net.minecraft.client.Minecraft.getInstance().font.drawInBatch(text, x, y, argb, false, pose.last().pose(), bufferSource,
                        false, 0, light);
            }

            @Override
            public void submitWrappedText(PoseStack pose, net.minecraft.util.FormattedCharSequence text, float x, float y, int light,
                                          TextWrapper wrapper) {
                PoseStack.Pose last = pose.last();
                net.minecraft.client.Minecraft.getInstance().font.drawInBatch(text, x, y, 0xFFFFFFFF, false, PoseMatrices.identityMatrix(),
                        type -> wrapper.wrap(bufferSource.getBuffer(type), last), false, 0, light);
            }
        };
    }

    private final FigureModels figures;

    public CarEntityRenderer(net.minecraft.client.renderer.entity.EntityRenderDispatcher context) {
        super(context);
        this.figures = new FigureModels(context);
    }

    @Override
    public ResourceLocation getTextureLocation(CarEntity entity) {
        return CompiledCarModel.FALLBACK_TEXTURE;
    }

    private void populateCarState(CarEntity entity, CarRenderState state, float partialTick) {
        state.lightCoords = this.getPackedLightCoords(entity, partialTick);

        state.yRot = entity.getYRot();

        float[] railwayOverride = entity.mineturismo$computeRailwayRenderOverride(partialTick);
        state.railwayPositionOverride = railwayOverride != null;
        if (railwayOverride != null) {
            state.railwayPosDeltaX = railwayOverride[0];
            state.railwayPosDeltaY = railwayOverride[1];
            state.railwayPosDeltaZ = railwayOverride[2];
            state.yRot = railwayOverride[3];
        }
        state.tumblePitchDegrees = entity.getTumblePitchDegrees();
        state.tumbleRollDegrees = entity.getTumbleRollDegrees();
        state.tumblePivotHeight = (float) (entity.getScaledHitboxHeight() / 2.0);
        state.wheeliePitchDegrees = entity.getWheeliePitchDegrees();

        state.wheeliePivotZ = -entity.getFootprintHalfLength() * WHEELIE_PIVOT_Z_FACTOR;
        state.tiltRollDegrees = entity.getTiltRollDegrees();
        state.tiltPitchDegrees = entity.getTiltPitchDegrees();
        state.tiltHeightCm = entity.getTiltHeightCm();
        state.suspensionPitchDegrees = entity.getSuspensionPitchDegrees();
        state.suspensionRollDegrees = entity.getSuspensionRollDegrees();
        state.bikeLeanRollDegrees = entity.getBikeLeanRollDegrees();
        state.showFakeRider = entity.shouldRenderFakeTrafficRider();
        if (state.showFakeRider) {
            state.fakeRiderUseAlex = (entity.getUUID().hashCode() & 1) == 0;
            state.fakeRiderX = entity.getPassengerXOffset();

            state.fakeRiderY = entity.getFakeRiderYOffset();
            state.fakeRiderZ = entity.getPassengerZOffset();
            state.fakeRiderIsMotorcycle = entity.hasVehicleCategory(com.examplebett.entity.VehicleCategory.MOTORCYCLE);
            state.fakeRiderBikeStationary = entity.isBikeStationary();
        }
        state.useLodWheel = this.entityRenderDispatcher.distanceToSqr(entity) > LOD_WHEEL_DISTANCE_SQ;

        state.wheelRotationDegrees = state.useLodWheel ? 0f : entity.getWheelRotationDegrees(partialTick);
        state.rearWheelExtraSpinDegrees = state.useLodWheel ? 0f : entity.getRearWheelExtraSpinDegrees(partialTick);
        state.frontWheelSteerDegrees = entity.getFrontWheelSteerDegrees(partialTick);
        state.rotorRotationDegrees = entity.getRotorRotationDegrees(partialTick);
        state.compiledModel = entity.getCompiledModel();
        state.wheelCompiledModel = entity.getWheelCompiledModel();
        state.wheelCompiledModelMirrored = entity.getWheelCompiledModelMirrored();
        state.wheelAttachment = entity.getWheelAttachment();
        state.wheelFrontZOffset = entity.getWheelFrontZOffset();
        state.wheelRearZOffset = entity.getWheelRearZOffset();
        state.wheelTrackHalfWidthOffset = entity.getWheelTrackHalfWidthOffset();
        state.wheelAxleHeightOffset = entity.getWheelAxleHeightOffset();
        state.extraRearAxleEnabled = entity.isExtraRearAxleEnabled();
        state.extraRearAxleZOffset = entity.getExtraRearAxleZOffset();
        state.extraRearAxle2Enabled = entity.isExtraRearAxle2Enabled();
        state.extraRearAxle2ZOffset = entity.getExtraRearAxle2ZOffset();
        state.bodyHeightOffset = entity.getBodyHeightOffset();
        state.wheelCamberDegrees = entity.getWheelCamberDegrees();
        state.wheelScale = entity.getWheelScale();
        state.wheelWidthScale = entity.getWheelWidthScale();
        state.lipStretchScale = entity.getLipStretchScale();
        state.lipWidthScale = entity.getLipWidthScale();
        state.lipBandHeightCm = entity.getLipBandHeightCm();
        state.lipFrontExtendScale = entity.getLipFrontExtendScale();
        state.limoStretchCm = entity.getLimoStretchCm();
        state.limoZoneCm = entity.getLimoZoneCm();
        state.limoCenterOffsetCm = entity.getLimoCenterOffsetCm();
        state.widthStretchCm = entity.getWidthStretchCm();
        state.widthZoneCm = entity.getWidthZoneCm();
        state.widthCenterOffsetCm = entity.getWidthCenterOffsetCm();
        state.carScale = entity.getCarScale();
        state.color = entity.getColor();

        boolean isMotorcycleNoFrontPlate = entity.hasVehicleCategory(com.examplebett.entity.VehicleCategory.MOTORCYCLE);
        state.frontPlateTexture = isMotorcycleNoFrontPlate ? null : PlateTextures.texture(entity.getFrontPlate());
        state.frontPlateAspect = PlateTextures.aspect(entity.getFrontPlate());
        var frontOffset = entity.getFrontPlateOffset();
        state.frontPlateX = (float) frontOffset.x;
        state.frontPlateY = (float) frontOffset.y;
        state.frontPlateZ = (float) frontOffset.z;
        state.frontPlateScale = entity.getFrontPlateScale();
        state.frontPlateYaw = entity.getFrontPlateYaw();
        state.frontPlatePitch = entity.getFrontPlatePitch();
        state.frontPlateRoll = entity.getFrontPlateRoll();

        state.rearPlateTexture = PlateTextures.texture(entity.getRearPlate());
        state.rearPlateAspect = PlateTextures.aspect(entity.getRearPlate());
        var rearOffset = entity.getRearPlateOffset();
        state.rearPlateX = (float) rearOffset.x;
        state.rearPlateY = (float) rearOffset.y;
        state.rearPlateZ = (float) rearOffset.z;
        state.rearPlateScale = entity.getRearPlateScale();
        state.rearPlateYaw = entity.getRearPlateYaw();
        state.rearPlatePitch = entity.getRearPlatePitch();
        state.rearPlateRoll = entity.getRearPlateRoll();

        state.lightsOn = entity.areLightsOn();

        state.headlightHidden = entity.hasVehicleCategory(com.examplebett.entity.VehicleCategory.TRAILER);
        state.lightsBlinking = entity.isLightBlinkEnabled();
        state.age = entity.tickCount;
        var headlightOffset = entity.getHeadlightOffset();
        state.headlightX = (float) headlightOffset.x;
        state.headlightY = (float) headlightOffset.y;
        state.headlightZ = (float) headlightOffset.z;
        var taillightOffset = entity.getTaillightOffset();
        state.taillightX = (float) taillightOffset.x;
        state.taillightY = (float) taillightOffset.y;
        state.taillightZ = (float) taillightOffset.z;
        var headlightColor = com.examplebett.entity.HeadlightColors.byId(entity.resolveHeadlightColorId());
        state.headlightColorR = headlightColor.r();
        state.headlightColorG = headlightColor.g();
        state.headlightColorB = headlightColor.b();

        state.rearWingEnabledIds = entity.getRearWingEnabledIds();
        state.rearWingTransformsRaw = entity.getRearWingTransformsRaw();

        state.otherPartEnabledIds = entity.getOtherPartEnabledIds();
        state.otherPartTransformsRaw = entity.getOtherPartTransformsRaw();

        state.textPartsRaw = entity.getTextPartsRaw();
        state.underNeonRaw = entity.getUnderNeonRaw();
        state.brandLogosRaw = entity.getBrandLogosRaw();
        double brandLogoDistanceSq = this.entityRenderDispatcher.distanceToSqr(entity);
        state.brandLogoDetail = brandLogoDistanceSq > BRAND_LOGO_HIDE_DISTANCE_SQ ? 0
                : brandLogoDistanceSq > BRAND_LOGO_FLAT_DISTANCE_SQ ? 1 : 2;

        state.frontBrakeCaliperEnabledIds = entity.getFrontBrakeCaliperEnabledIds();
        state.frontBrakeCaliperTransformsRaw = entity.getFrontBrakeCaliperTransformsRaw();

        state.rearBrakeCaliperEnabledIds = entity.getRearBrakeCaliperEnabledIds();
        state.rearBrakeCaliperTransformsRaw = entity.getRearBrakeCaliperTransformsRaw();

        state.exhaustEnabledId = entity.getExhaustEnabledIds();
        if (state.exhaustEnabledId.isEmpty()) {
            state.exhaustTarget = null;
            entity.clientExhaustOutlets = null;
        } else {
            state.exhaustTarget = entity;
            state.exhaustTransform = entity.getExhaustTransform(state.exhaustEnabledId);
        }

        state.towSelf = null;
        state.towComputeFront = false;
        state.towComputeRear = false;
        state.towRopeEnd = null;
        long towNow = entity.level().getGameTime();
        int towLeaderId = entity.getTowLeaderEntityId();
        if (towLeaderId != -1 && entity.level().getEntity(towLeaderId) instanceof CarEntity towLeader) {
            towLeader.clientTowRearHookWantedTime = towNow;
            state.towSelf = entity;
            state.towComputeFront = true;
            float[] leaderRear = towLeader.clientTowRearHook;
            if (leaderRear != null) {
                net.minecraft.world.phys.Vec3 leaderPos = com.examplebett.compat.ModVec3.position(towLeader, partialTick);
                net.minecraft.world.phys.Vec3 selfPos = com.examplebett.compat.ModVec3.position(entity, partialTick);
                state.towRopeEnd = new float[]{
                        (float) (leaderPos.x - selfPos.x) + leaderRear[0],
                        (float) (leaderPos.y - selfPos.y) + leaderRear[1],
                        (float) (leaderPos.z - selfPos.z) + leaderRear[2]};
            }
        } else {
            entity.clientTowFrontHook = null;
        }
        if (towNow - entity.clientTowRearHookWantedTime <= 2) {
            state.towSelf = entity;
            state.towComputeRear = true;
        }

        state.policeLightEnabledIds = entity.getPoliceLightEnabledIds();
        state.policeLightTransformsRaw = entity.getPoliceLightTransformsRaw();
        state.policeLightsOn = entity.isPoliceLightsOn();

        state.taxiSignEnabledIds = entity.getTaxiSignEnabledIds();
        state.taxiSignTransformsRaw = entity.getTaxiSignTransformsRaw();
    }

    @Override
    public void render(CarEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int light) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, light);
        CarRenderState state = new CarRenderState();
        populateCarState(entity, state, partialTick);
        state.isInvisible = entity.isInvisible();
        state.lightCoords = light;
        renderCar(state, poseStack, immediateSubmitter(bufferSource));
    }

    private void renderCar(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector) {

        if (state.isInvisible) {
            return;
        }

        if (state.compiledModel == null) {
            return;
        }

        float[] exhaustOrigin = (state.exhaustTarget != null || state.towSelf != null) ? PoseMatrices.capture(poseStack) : null;

        poseStack.pushPose();
        if (state.railwayPositionOverride) {

            poseStack.translate(state.railwayPosDeltaX, state.railwayPosDeltaY, state.railwayPosDeltaZ);
        }
        poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(state.yRot));

        boolean tumbling = state.tumblePitchDegrees != 0f || state.tumbleRollDegrees != 0f;
        if (tumbling) {
            poseStack.translate(0.0, state.tumblePivotHeight, 0.0);
        }
        if (state.tumblePitchDegrees != 0f) {
            poseStack.mulPose(com.mojang.math.Vector3f.XP.rotationDegrees(state.tumblePitchDegrees));
        }
        if (state.tumbleRollDegrees != 0f) {
            poseStack.mulPose(com.mojang.math.Vector3f.ZP.rotationDegrees(state.tumbleRollDegrees));
        }
        if (tumbling) {
            poseStack.translate(0.0, -state.tumblePivotHeight, 0.0);
        }

        boolean wheelieActive = state.wheeliePitchDegrees != 0f;
        if (wheelieActive) {
            poseStack.translate(0.0, 0.0, state.wheeliePivotZ);
            poseStack.mulPose(com.mojang.math.Vector3f.XP.rotationDegrees(-state.wheeliePitchDegrees));
            poseStack.translate(0.0, 0.0, -state.wheeliePivotZ);
        }

        boolean tilting = state.tiltPitchDegrees != 0f || state.tiltRollDegrees != 0f;
        if (tilting) {
            poseStack.translate(0.0, state.tumblePivotHeight, 0.0);
        }
        if (state.tiltPitchDegrees != 0f) {
            poseStack.mulPose(com.mojang.math.Vector3f.XP.rotationDegrees(state.tiltPitchDegrees));
        }
        if (state.tiltRollDegrees != 0f) {
            poseStack.mulPose(com.mojang.math.Vector3f.ZP.rotationDegrees(state.tiltRollDegrees));
        }
        if (tilting) {
            poseStack.translate(0.0, -state.tumblePivotHeight, 0.0);
        }
        if (state.tiltHeightCm != 0f) {
            poseStack.translate(0.0, state.tiltHeightCm / 100.0, 0.0);
        }

        if (state.bikeLeanRollDegrees != 0f) {
            poseStack.translate(0.0, state.tumblePivotHeight, 0.0);
            poseStack.mulPose(com.mojang.math.Vector3f.ZP.rotationDegrees(state.bikeLeanRollDegrees));
            poseStack.translate(0.0, -state.tumblePivotHeight, 0.0);
        }

        if (state.carScale != 1f) {
            poseStack.scale(state.carScale, state.carScale, state.carScale);
        }

        poseStack.pushPose();
        poseStack.translate(0.0, state.bodyHeightOffset, 0.0);

        boolean suspensionSwaying = state.suspensionPitchDegrees != 0f || state.suspensionRollDegrees != 0f;
        if (suspensionSwaying) {
            poseStack.translate(0.0, state.tumblePivotHeight, 0.0);
        }
        if (state.suspensionPitchDegrees != 0f) {
            poseStack.mulPose(com.mojang.math.Vector3f.XP.rotationDegrees(state.suspensionPitchDegrees));
        }
        if (state.suspensionRollDegrees != 0f) {
            poseStack.mulPose(com.mojang.math.Vector3f.ZP.rotationDegrees(state.suspensionRollDegrees));
        }
        if (suspensionSwaying) {
            poseStack.translate(0.0, -state.tumblePivotHeight, 0.0);
        }

        List<CompiledCarModel.TextureGroup> reflectiveGroups = new ArrayList<>();
        float[] embeddedExtraAxleZ = embeddedExtraAxleZ(state);
        for (CompiledCarModel.TextureGroup group : state.compiledModel.groups) {

            ResourceLocation texture = group.texture;
            boolean usesPaintMask = group.tintable && PaintTintTexture.hasPaint(group.texture);
            if (usesPaintMask) {
                texture = PaintTintTexture.resolve(group.texture, state.color);
            }
            texture = metalPaintTexture(group, texture, usesPaintMask, state.color);

            final int tint = (group.tintable && !usesPaintMask) ? state.color : 0xFFFFFF;

            RenderType renderType = groupRenderType(group, texture);

            boolean spinning = group.wheelGroup >= 0 && group.wheelGroup < state.compiledModel.wheelPivots.size();
            boolean rotorSpinning = group.rotorGroup >= 0 && group.rotorGroup < state.compiledModel.rotorPivots.size();

            boolean isBodyGroup = group.wheelGroup < 0 && group.rotorGroup < 0;
            BodyDeform deform = isBodyGroup ? BodyDeform.from(state) : BodyDeform.NONE;

            boolean reflective = com.examplebett.client.AiObjectModClient.isFakeReflectionEnabled()
                    && isBodyGroup && !group.translucent;

            poseStack.pushPose();
            applyGroupTransform(poseStack, state, group, spinning, rotorSpinning, 0f);
            collector.submitCustomGeometry(poseStack, renderType, (pose, vertexConsumer) ->
                    renderGroup(group.vertexData, pose, vertexConsumer, state.lightCoords, tint, deform, usesTriangles(group)));
            poseStack.popPose();

            if (spinning && embeddedExtraAxleZ.length > 0
                    && !state.compiledModel.wheelPivots.get(group.wheelGroup).front) {
                float pivotZ = state.compiledModel.wheelPivots.get(group.wheelGroup).centerZ;
                for (float axleZ : embeddedExtraAxleZ) {
                    poseStack.pushPose();
                    applyGroupTransform(poseStack, state, group, true, false, axleZ - pivotZ);
                    collector.submitCustomGeometry(poseStack, renderType, (pose, vertexConsumer) ->
                            renderGroup(group.vertexData, pose, vertexConsumer, state.lightCoords, tint, BodyDeform.NONE, usesTriangles(group)));
                    poseStack.popPose();
                }
            }
            if (reflective) {
                reflectiveGroups.add(group);
            }
        }

        for (CompiledCarModel.TextureGroup group : reflectiveGroups) {
            boolean spinning = group.wheelGroup >= 0 && group.wheelGroup < state.compiledModel.wheelPivots.size();
            boolean rotorSpinning = group.rotorGroup >= 0 && group.rotorGroup < state.compiledModel.rotorPivots.size();
            boolean isBodyGroup = group.wheelGroup < 0 && group.rotorGroup < 0;
            BodyDeform deform = isBodyGroup ? BodyDeform.from(state) : BodyDeform.NONE;
            RenderType reflectionType = RenderType.entityNoOutline(CompiledCarModel.FALLBACK_TEXTURE);
            poseStack.pushPose();
            applyGroupTransform(poseStack, state, group, spinning, rotorSpinning, 0f);
            collector.submitCustomGeometry(poseStack, reflectionType, (pose, vertexConsumer) ->
                    renderReflectionOverlay(group.vertexData, pose, vertexConsumer, state.lightCoords, deform));
            poseStack.popPose();
        }

        if (state.frontPlateTexture != null) {
            submitPlate(poseStack, collector, state.lightCoords, state.frontPlateTexture,
                    state.frontPlateX + widthShiftFor(state, state.frontPlateX), state.frontPlateY, state.frontPlateZ + limoShiftFor(state, state.frontPlateZ), state.frontPlateScale,
                    state.frontPlateYaw, state.frontPlatePitch, state.frontPlateRoll, state.frontPlateAspect);
        }
        if (state.rearPlateTexture != null) {

            submitPlate(poseStack, collector, state.lightCoords, state.rearPlateTexture,
                    state.rearPlateX + widthShiftFor(state, state.rearPlateX), state.rearPlateY, state.rearPlateZ + limoShiftFor(state, state.rearPlateZ), state.rearPlateScale,
                    state.rearPlateYaw + 180f, state.rearPlatePitch, state.rearPlateRoll, state.rearPlateAspect);
        }

        boolean blinkPhaseOn = !state.lightsBlinking
                || (state.age % (BLINK_ON_TICKS + BLINK_OFF_TICKS)) < BLINK_ON_TICKS;
        if (state.lightsOn && blinkPhaseOn) {

            for (int side = -1; side <= 1; side += 2) {
                float headlightX = state.headlightX * side;
                float taillightX = state.taillightX * side;
                if (!state.headlightHidden) {
                    submitGlow(poseStack, collector, HEADLIGHT_GLOW_TEXTURE,
                            headlightX + widthShiftFor(state, headlightX), state.headlightY, state.headlightZ + limoShiftFor(state, state.headlightZ), HEADLIGHT_GLOW_SIZE, 0f,
                            state.headlightColorR, state.headlightColorG, state.headlightColorB);
                }
                submitGlow(poseStack, collector, TAILLIGHT_GLOW_TEXTURE,
                        taillightX + widthShiftFor(state, taillightX), state.taillightY, state.taillightZ + limoShiftFor(state, state.taillightZ), TAILLIGHT_GLOW_SIZE, 180f,
                        1f, 1f, 1f);
            }
        }

        renderRearWing(state, poseStack, collector);
        renderPoliceLight(state, poseStack, collector);
        renderTaxiSign(state, poseStack, collector);
        renderOtherPart(state, poseStack, collector);
        renderTextParts(state, poseStack, collector);
        renderBrandLogos(state, poseStack, collector);
        if (exhaustOrigin != null && state.exhaustTarget != null) {
            computeExhaustOutlets(state, poseStack, exhaustOrigin);
        }
        if (exhaustOrigin != null && state.towSelf != null) {
            computeTowHooks(state, poseStack, exhaustOrigin);
        }

        poseStack.popPose();

        renderUnderNeon(state, poseStack, collector);
        renderWheelAttachment(state, poseStack, collector);
        renderBrakeCaliper(state, poseStack, collector, true, state.frontBrakeCaliperEnabledIds, state.frontBrakeCaliperTransformsRaw);
        renderBrakeCaliper(state, poseStack, collector, false, state.rearBrakeCaliperEnabledIds, state.rearBrakeCaliperTransformsRaw);

        renderFakeRider(state, poseStack, collector);

        poseStack.popPose();

        renderTowRope(state, poseStack, collector);
    }

    private static final ResourceLocation TOW_ROPE_TEXTURE = com.examplebett.AiObjectMod.id("textures/entity/tow_rope.png");

    private static final float TOW_ROPE_HALF_THICKNESS = 0.045f;
    private static final int TOW_ROPE_SEGMENTS = 24;

    private static void computeTowHooks(CarRenderState state, PoseStack poseStack, float[] origin) {
        float[] relative = PoseMatrices.relative(origin, PoseMatrices.capture(poseStack));
        if (state.towComputeFront) {
            state.towSelf.clientTowFrontHook = PoseMatrices.transformPosition(relative, 0f, state.frontPlateY,
                    state.frontPlateZ + limoShiftFor(state, state.frontPlateZ));
        }
        if (state.towComputeRear) {
            state.towSelf.clientTowRearHook = PoseMatrices.transformPosition(relative, 0f, state.rearPlateY,
                    state.rearPlateZ + limoShiftFor(state, state.rearPlateZ));
        }
    }

    private void renderTowRope(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector) {
        if (!state.towComputeFront || state.towRopeEnd == null || state.towSelf == null || state.towSelf.clientTowFrontHook == null) {
            return;
        }
        float[] a = state.towSelf.clientTowFrontHook;
        float[] b = state.towRopeEnd;
        float dx = b[0] - a[0];
        float dy = b[1] - a[1];
        float dz = b[2] - a[2];
        float straight = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (straight < 1.0E-3f) {
            return;
        }

        float ropeLength = (float) CarEntity.TOW_ROPE_LENGTH;
        float slack = Math.max(0f, ropeLength - straight);
        float sag = 0.08f + (float) Math.sqrt(slack * (ropeLength + straight)) * 0.35f;
        int light = state.lightCoords;
        collector.submitCustomGeometry(poseStack, RenderType.entityCutoutNoCull(TOW_ROPE_TEXTURE), (pose, consumer) -> {
            float[] prev = null;
            for (int i = 0; i <= TOW_ROPE_SEGMENTS; i++) {
                float t = (float) i / TOW_ROPE_SEGMENTS;
                float[] p = {a[0] + dx * t, a[1] + dy * t - sag * 4f * t * (1f - t), a[2] + dz * t};
                if (prev != null) {

                    float u0 = ((i - 1) % 2) * 0.5f;
                    addRopeSegment(consumer, pose, prev, p, u0, u0 + 0.5f, light);
                }
                prev = p;
            }
        });
    }

    private void addRopeSegment(VertexConsumer consumer, PoseStack.Pose pose, float[] p0, float[] p1, float u0, float u1, int light) {
        float dx = p1[0] - p0[0];
        float dy = p1[1] - p0[1];
        float dz = p1[2] - p0[2];
        float dirLen = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (dirLen < 1.0E-4f) {
            return;
        }
        dx /= dirLen;
        dy /= dirLen;
        dz /= dirLen;

        float sx = -dz;
        float sy = 0f;
        float sz = dx;
        float sideLen = (float) Math.sqrt(sx * sx + sz * sz);
        if (sideLen < 1.0E-3f) {
            sx = 1f;
            sz = 0f;
            sideLen = 1f;
        }
        sx = sx / sideLen * TOW_ROPE_HALF_THICKNESS;
        sz = sz / sideLen * TOW_ROPE_HALF_THICKNESS;

        float ux = sy * dz - sz * dy;
        float uy = sz * dx - sx * dz;
        float uz = sx * dy - sy * dx;
        float upLen = (float) Math.sqrt(ux * ux + uy * uy + uz * uz);
        if (upLen < 1.0E-8f) {
            return;
        }
        ux = ux / upLen * TOW_ROPE_HALF_THICKNESS;
        uy = uy / upLen * TOW_ROPE_HALF_THICKNESS;
        uz = uz / upLen * TOW_ROPE_HALF_THICKNESS;
        float[][] corners = {
                {sx + ux, sy + uy, sz + uz}, {-sx + ux, -sy + uy, -sz + uz},
                {-sx - ux, -sy - uy, -sz - uz}, {sx - ux, sy - uy, sz - uz}};
        for (int f = 0; f < 4; f++) {
            float[] c0 = corners[f];
            float[] c1 = corners[(f + 1) % 4];
            float v0 = f * 0.25f;
            float v1 = v0 + 0.25f;
            addPlateVertex(consumer, pose, p0[0] + c0[0], p0[1] + c0[1], p0[2] + c0[2], u0, v0, light);
            addPlateVertex(consumer, pose, p1[0] + c0[0], p1[1] + c0[1], p1[2] + c0[2], u1, v0, light);
            addPlateVertex(consumer, pose, p1[0] + c1[0], p1[1] + c1[1], p1[2] + c1[2], u1, v1, light);
            addPlateVertex(consumer, pose, p0[0] + c1[0], p0[1] + c1[1], p0[2] + c1[2], u0, v1, light);
        }
    }

    private static final float FAKE_RIDER_YAW_DEGREES = 180f;

    private static final float FAKE_RIDER_Y_ADJUST = 0.85f;

    private void renderFakeRider(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector) {
        if (!state.showFakeRider) {
            return;
        }
        RenderType renderType = RenderType.entityCutoutNoCull(FigureModels.fakeRiderTexture(state.fakeRiderUseAlex));
        poseStack.pushPose();
        poseStack.translate(state.fakeRiderX, state.fakeRiderY + FAKE_RIDER_Y_ADJUST, state.fakeRiderZ);
        poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(FAKE_RIDER_YAW_DEGREES));
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        collector.submitModel(poseStack, renderType, (modelPoseStack, vertexConsumer) ->
                this.figures.renderFakeRider(state.fakeRiderUseAlex, state.fakeRiderIsMotorcycle, state.fakeRiderBikeStationary,
                        state.bikeLeanRollDegrees, modelPoseStack, vertexConsumer, state.lightCoords));
        poseStack.popPose();
    }

    private static final float SEATED_PLAYER_SCALE = 0.9375f;

    private static final float SEATED_PLAYER_ORIGIN_HEIGHT = (12f + 2f) / 16f * SEATED_PLAYER_SCALE;

    private void renderSeatedPlayer(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector, boolean alex) {
        ResourceLocation texture = alex ? FigureModels.ALEX_SLIM_TEXTURE : FigureModels.STEVE_TEXTURE;
        poseStack.pushPose();
        poseStack.translate(0f, SEATED_PLAYER_ORIGIN_HEIGHT, 0f);
        poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(FAKE_RIDER_YAW_DEGREES));
        poseStack.scale(-SEATED_PLAYER_SCALE, -SEATED_PLAYER_SCALE, SEATED_PLAYER_SCALE);
        collector.submitModel(poseStack, RenderType.entityCutoutNoCull(texture), (modelPoseStack, vertexConsumer) ->
                this.figures.renderSeatedPlayer(alex, modelPoseStack, vertexConsumer, state.lightCoords));
        poseStack.popPose();
    }

    private static final float SITTING_WOLF_ORIGIN_HEIGHT = 1.501f;

    private void renderSittingWolf(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector) {
        poseStack.pushPose();
        poseStack.translate(0f, SITTING_WOLF_ORIGIN_HEIGHT, 0f);
        poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(FAKE_RIDER_YAW_DEGREES));
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        collector.submitModel(poseStack, RenderType.entityCutoutNoCull(FigureModels.WOLF_TEXTURE), (modelPoseStack, vertexConsumer) ->
                this.figures.renderSittingWolf(false, state.age, modelPoseStack, vertexConsumer, state.lightCoords));
        collector.submitModel(poseStack, RenderType.entityCutoutNoCull(FigureModels.WOLF_COLLAR_TEXTURE), (modelPoseStack, vertexConsumer) ->
                this.figures.renderSittingWolf(true, state.age, modelPoseStack, vertexConsumer, state.lightCoords));
        poseStack.popPose();
    }

    private record WheelPositions(int wheelCount, float axleHeight, boolean[] isFront, float[] camberSign) {
    }

    private record PartRef(String id, String modelId) {
    }

    private static boolean usesEmbeddedWheelPivots(CarRenderState state) {
        return state.wheelAttachment == null && state.compiledModel != null
                && state.compiledModel.wheelPivots.size() == 4;
    }

    private WheelPositions computeWheelPositions(CarRenderState state) {
        com.examplebett.render.WheelAttachment attachment = state.wheelAttachment;
        if (attachment == null) {
            return computeEmbeddedWheelPositions(state);
        }
        float frontZ = attachment.frontZ() + state.wheelFrontZOffset;
        float rearZ = attachment.rearZ() + state.wheelRearZOffset;
        float trackHalfWidth = attachment.trackHalfWidth() + state.wheelTrackHalfWidthOffset;
        float axleHeight = attachment.axleHeight() + state.wheelAxleHeightOffset;

        boolean hasRearAxle2 = attachment.hasRearAxle2() || state.extraRearAxleEnabled;
        float rearZ2Base = attachment.hasRearAxle2()
                ? attachment.rearZ2()
                : attachment.rearZ() - CarEntity.DEFAULT_EXTRA_REAR_AXLE_SPACING;
        float rearZ2 = hasRearAxle2 ? rearZ2Base + state.wheelRearZOffset + state.extraRearAxleZOffset : 0f;

        boolean hasRearAxle3 = state.extraRearAxle2Enabled;
        float rearZ3 = hasRearAxle3
                ? rearZ2Base - CarEntity.DEFAULT_EXTRA_REAR_AXLE_SPACING + state.wheelRearZOffset + state.extraRearAxle2ZOffset
                : 0f;

        frontZ += limoShiftFor(state, frontZ);
        rearZ += limoShiftFor(state, rearZ);
        if (hasRearAxle2) {
            rearZ2 += limoShiftFor(state, rearZ2);
        }
        if (hasRearAxle3) {
            rearZ3 += limoShiftFor(state, rearZ3);
        }

        float trackRightX = trackHalfWidth + widthShiftFor(state, trackHalfWidth);
        float trackLeftX = -trackHalfWidth + widthShiftFor(state, -trackHalfWidth);

        boolean singleWheelPerAxle = attachment.singleWheelPerAxle();

        int wheelCount = singleWheelPerAxle ? 2 : (hasRearAxle2 && hasRearAxle3 ? 8 : (hasRearAxle2 || hasRearAxle3 ? 6 : 4));
        float[] offsetZ = WHEEL_OFFSET_Z_BUF;
        float[] offsetX = WHEEL_OFFSET_X_BUF;
        if (singleWheelPerAxle) {
            offsetZ[0] = frontZ; offsetZ[1] = rearZ;
            offsetX[0] = 0f; offsetX[1] = 0f;
        } else {
            offsetZ[0] = frontZ; offsetZ[1] = frontZ; offsetZ[2] = rearZ; offsetZ[3] = rearZ;
            offsetX[0] = trackRightX; offsetX[1] = trackLeftX; offsetX[2] = trackRightX; offsetX[3] = trackLeftX;
            if (hasRearAxle2) {
                offsetZ[4] = rearZ2; offsetZ[5] = rearZ2;
                offsetX[4] = trackRightX; offsetX[5] = trackLeftX;
                if (hasRearAxle3) {
                    offsetZ[6] = rearZ3; offsetZ[7] = rearZ3;
                    offsetX[6] = trackRightX; offsetX[7] = trackLeftX;
                }
            } else if (hasRearAxle3) {
                offsetZ[4] = rearZ3; offsetZ[5] = rearZ3;
                offsetX[4] = trackRightX; offsetX[5] = trackLeftX;
            }
        }
        boolean[] isFront = singleWheelPerAxle ? WHEEL_IS_FRONT_2
                : (wheelCount == 8 ? WHEEL_IS_FRONT_8 : (wheelCount == 6 ? WHEEL_IS_FRONT_6 : WHEEL_IS_FRONT_4));

        float[] camberSign = singleWheelPerAxle ? WHEEL_CAMBER_SIGN_2
                : (wheelCount == 8 ? WHEEL_CAMBER_SIGN_8 : (wheelCount == 6 ? WHEEL_CAMBER_SIGN_6 : WHEEL_CAMBER_SIGN_4));
        return new WheelPositions(wheelCount, axleHeight, isFront, camberSign);
    }

    private static final float[] NO_EXTRA_AXLES = new float[0];

    private static float[] embeddedExtraAxleZ(CarRenderState state) {
        if ((!state.extraRearAxleEnabled && !state.extraRearAxle2Enabled) || !usesEmbeddedWheelPivots(state)) {
            return NO_EXTRA_AXLES;
        }
        float rearZ = 0f;
        int rearCount = 0;
        for (ObjModel.WheelPivot pivot : state.compiledModel.wheelPivots) {
            if (!pivot.front) {
                rearZ += pivot.centerZ;
                rearCount++;
            }
        }
        if (rearCount == 0) {
            return NO_EXTRA_AXLES;
        }
        rearZ /= rearCount;
        float rearZ2Base = rearZ - CarEntity.DEFAULT_EXTRA_REAR_AXLE_SPACING;
        float rearZ2 = rearZ2Base + state.extraRearAxleZOffset;
        float rearZ3 = rearZ2Base - CarEntity.DEFAULT_EXTRA_REAR_AXLE_SPACING + state.extraRearAxle2ZOffset;
        if (state.extraRearAxleEnabled && state.extraRearAxle2Enabled) {
            return new float[] { rearZ2, rearZ3 };
        }
        return new float[] { state.extraRearAxleEnabled ? rearZ2 : rearZ3 };
    }

    private WheelPositions computeEmbeddedWheelPositions(CarRenderState state) {
        if (!usesEmbeddedWheelPivots(state)) {
            return new WheelPositions(0, 0f, WHEEL_IS_FRONT_4, WHEEL_CAMBER_SIGN_4);
        }
        List<ObjModel.WheelPivot> pivots = state.compiledModel.wheelPivots;
        ObjModel.WheelPivot[] front = new ObjModel.WheelPivot[2];
        ObjModel.WheelPivot[] rear = new ObjModel.WheelPivot[2];
        int frontCount = 0;
        int rearCount = 0;
        float sumY = 0f;
        for (ObjModel.WheelPivot pivot : pivots) {
            if (pivot.front && frontCount < 2) {
                front[frontCount++] = pivot;
            } else if (!pivot.front && rearCount < 2) {
                rear[rearCount++] = pivot;
            }
            sumY += pivot.centerY;
        }
        if (frontCount != 2 || rearCount != 2) {
            return new WheelPositions(0, 0f, WHEEL_IS_FRONT_4, WHEEL_CAMBER_SIGN_4);
        }

        if (front[0].centerX < front[1].centerX) {
            ObjModel.WheelPivot t = front[0]; front[0] = front[1]; front[1] = t;
        }
        if (rear[0].centerX < rear[1].centerX) {
            ObjModel.WheelPivot t = rear[0]; rear[0] = rear[1]; rear[1] = t;
        }
        float[] extraAxleZ = embeddedExtraAxleZ(state);
        int wheelCount = 4 + extraAxleZ.length * 2;
        float[] offsetZ = WHEEL_OFFSET_Z_BUF;
        float[] offsetX = WHEEL_OFFSET_X_BUF;
        offsetZ[0] = embeddedWheelZ(state, front[0].centerZ, true); offsetZ[1] = embeddedWheelZ(state, front[1].centerZ, true);
        offsetZ[2] = embeddedWheelZ(state, rear[0].centerZ, false); offsetZ[3] = embeddedWheelZ(state, rear[1].centerZ, false);
        offsetX[0] = embeddedWheelX(state, front[0]); offsetX[1] = embeddedWheelX(state, front[1]);
        offsetX[2] = embeddedWheelX(state, rear[0]); offsetX[3] = embeddedWheelX(state, rear[1]);
        for (int a = 0; a < extraAxleZ.length; a++) {
            int i = 4 + a * 2;
            float axleZ = embeddedWheelZ(state, extraAxleZ[a], false);
            offsetZ[i] = axleZ; offsetZ[i + 1] = axleZ;
            offsetX[i] = offsetX[2]; offsetX[i + 1] = offsetX[3];
        }
        boolean[] isFront = wheelCount == 8 ? WHEEL_IS_FRONT_8 : (wheelCount == 6 ? WHEEL_IS_FRONT_6 : WHEEL_IS_FRONT_4);
        float[] camberSign = wheelCount == 8 ? WHEEL_CAMBER_SIGN_8 : (wheelCount == 6 ? WHEEL_CAMBER_SIGN_6 : WHEEL_CAMBER_SIGN_4);
        return new WheelPositions(wheelCount, sumY / pivots.size() + state.wheelAxleHeightOffset, isFront, camberSign);
    }

    private void renderBrakeCaliper(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector, boolean frontOnly,
                                     String enabledIdsCsv, String transformsRaw) {

        if ((state.wheelAttachment == null && !usesEmbeddedWheelPivots(state)) || enabledIdsCsv == null || enabledIdsCsv.isEmpty()) {
            return;
        }
        Set<String> enabledIds = parseEnabledIds(enabledIdsCsv);
        net.minecraft.server.packs.resources.ResourceManager resourceManager =
                net.minecraft.client.Minecraft.getInstance().getResourceManager();
        List<PartRef> parts = new ArrayList<>();
        if (frontOnly) {
            for (com.examplebett.render.FrontBrakeCaliperParts.Option option : com.examplebett.render.FrontBrakeCaliperParts.list(resourceManager)) {
                parts.add(new PartRef(option.id(), option.modelId()));
            }
        } else {
            for (com.examplebett.render.RearBrakeCaliperParts.Option option : com.examplebett.render.RearBrakeCaliperParts.list(resourceManager)) {
                parts.add(new PartRef(option.id(), option.modelId()));
            }
        }
        if (parts.isEmpty()) {
            return;
        }
        WheelPositions wp = computeWheelPositions(state);
        float[] offsetX = WHEEL_OFFSET_X_BUF;
        float[] offsetZ = WHEEL_OFFSET_Z_BUF;
        for (PartRef part : parts) {
            if (!enabledIds.contains(part.id())) {
                continue;
            }
            BrakeCaliperModel.Pair pair = BrakeCaliperModel.get(resourceManager, part.modelId());
            if (pair == null) {
                continue;
            }
            com.examplebett.render.PartTransforms.Transform6 t =
                    com.examplebett.render.PartTransforms.resolve6(transformsRaw, part.id(),
                            com.examplebett.entity.CarEntity.defaultBrakeCaliperTransform(part.id()));
            float localX = t.x();
            float localY = t.y();
            float localZ = t.z();
            float localAngle = t.angle();
            float localAngleY = t.angleY();
            float localScale = t.scale();
            for (int i = 0; i < wp.wheelCount(); i++) {
                if (wp.isFront()[i] != frontOnly) {
                    continue;
                }
                poseStack.pushPose();
                poseStack.translate(offsetX[i], wp.axleHeight(), offsetZ[i]);
                if (state.wheelCamberDegrees != 0f) {
                    poseStack.mulPose(com.mojang.math.Vector3f.ZP.rotationDegrees(wp.camberSign()[i] * state.wheelCamberDegrees));
                }
                if (frontOnly && state.frontWheelSteerDegrees != 0f) {

                    poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(state.frontWheelSteerDegrees));
                }
                boolean mirrored = offsetX[i] < 0;
                CompiledCarModel activeModel = mirrored ? pair.mirrored() : pair.normal();

                poseStack.translate(mirrored ? -localX : localX, localY, localZ);
                if (localAngleY != 0f) {
                    poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(localAngleY));
                }
                if (localAngle != 0f) {
                    poseStack.mulPose(com.mojang.math.Vector3f.XP.rotationDegrees(localAngle));
                }
                if (localScale != 1f) {
                    poseStack.scale(localScale, localScale, localScale);
                }
                for (CompiledCarModel.TextureGroup group : activeModel.groups) {
                    ResourceLocation texture = group.texture;
                    final int tint = group.tintable ? state.color : 0xFFFFFF;
                    RenderType renderType = groupRenderType(group, texture);
                    collector.submitCustomGeometry(poseStack, renderType, (pose, vertexConsumer) ->
                            renderGroup(group.vertexData, pose, vertexConsumer, state.lightCoords, tint, BodyDeform.NONE, usesTriangles(group)));
                }
                poseStack.popPose();
            }
        }
    }

    private void renderWheelAttachment(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector) {
        if (state.wheelCompiledModel == null || state.wheelAttachment == null) {
            return;
        }

        CompiledCarModel wheelModel = state.wheelCompiledModel;

        CompiledCarModel wheelModelMirrored =
                state.wheelCompiledModelMirrored != null ? state.wheelCompiledModelMirrored : wheelModel;
        if (state.useLodWheel) {
            net.minecraft.server.packs.resources.ResourceManager resourceManager =
                    net.minecraft.client.Minecraft.getInstance().getResourceManager();
            CompiledCarModel lodModel = LodWheelModel.get(resourceManager);
            if (lodModel != null) {
                wheelModel = lodModel;
                CompiledCarModel lodModelMirrored = LodWheelModel.getMirrored(resourceManager);
                wheelModelMirrored = lodModelMirrored != null ? lodModelMirrored : lodModel;
            }
        }
        WheelPositions wp = computeWheelPositions(state);
        int wheelCount = wp.wheelCount();
        float axleHeight = wp.axleHeight();
        float[] offsetZ = WHEEL_OFFSET_Z_BUF;
        float[] offsetX = WHEEL_OFFSET_X_BUF;
        boolean[] isFront = wp.isFront();
        float[] camberSign = wp.camberSign();

        for (int i = 0; i < wheelCount; i++) {
            poseStack.pushPose();
            poseStack.translate(offsetX[i], axleHeight, offsetZ[i]);

            if (state.wheelCamberDegrees != 0f) {
                poseStack.mulPose(com.mojang.math.Vector3f.ZP.rotationDegrees(camberSign[i] * state.wheelCamberDegrees));
            }
            if (isFront[i]) {
                poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(state.frontWheelSteerDegrees));
            }

            float rollDegrees = state.wheelRotationDegrees + (isFront[i] ? 0f : state.rearWheelExtraSpinDegrees);
            poseStack.mulPose(com.mojang.math.Vector3f.XP.rotationDegrees(rollDegrees));

            boolean mirrored = offsetX[i] < 0;
            CompiledCarModel activeWheelModel = mirrored ? wheelModelMirrored : wheelModel;

            float scaleX = state.wheelScale * state.wheelWidthScale;
            if (scaleX != 1f || state.wheelScale != 1f) {
                poseStack.scale(scaleX, state.wheelScale, state.wheelScale);
            }
            for (CompiledCarModel.TextureGroup group : activeWheelModel.groups) {
                ResourceLocation texture = group.texture;
                boolean usesPaintMask = group.tintable && PaintTintTexture.hasPaint(group.texture);
                if (usesPaintMask) {
                    texture = PaintTintTexture.resolve(group.texture, state.color);
                }
                texture = metalPaintTexture(group, texture, usesPaintMask, state.color);
                final int tint = (group.tintable && !usesPaintMask) ? state.color : 0xFFFFFF;
                RenderType renderType = groupRenderType(group, texture);
                collector.submitCustomGeometry(poseStack, renderType, (pose, vertexConsumer) ->
                        renderGroup(group.vertexData, pose, vertexConsumer, state.lightCoords, tint, BodyDeform.NONE, usesTriangles(group)));
            }
            poseStack.popPose();
        }
    }

    private void renderRearWing(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector) {
        if (state.rearWingEnabledIds == null || state.rearWingEnabledIds.isEmpty()) {
            return;
        }
        Set<String> enabledIds = parseEnabledIds(state.rearWingEnabledIds);
        net.minecraft.server.packs.resources.ResourceManager resourceManager =
                net.minecraft.client.Minecraft.getInstance().getResourceManager();
        com.examplebett.render.PartTransforms.Transform12 defaults = new com.examplebett.render.PartTransforms.Transform12(
                com.examplebett.entity.CarEntity.DEFAULT_REAR_WING_X, com.examplebett.entity.CarEntity.DEFAULT_REAR_WING_Y,
                com.examplebett.entity.CarEntity.DEFAULT_REAR_WING_Z, 0f, 0f, com.examplebett.entity.CarEntity.DEFAULT_REAR_WING_SCALE,
                1f, 1f, 1f, 1f, 1f, 1f);
        for (com.examplebett.render.RearWingParts.Option option : com.examplebett.render.RearWingParts.list(resourceManager)) {
            if (!enabledIds.contains(option.id())) {
                continue;
            }
            CompiledCarModel wingModel = RearWingModel.get(resourceManager, option.modelId());
            if (wingModel == null) {
                continue;
            }
            com.examplebett.render.PartTransforms.Transform12 t =
                    com.examplebett.render.PartTransforms.resolve12(state.rearWingTransformsRaw, option.id(), defaults);
            poseStack.pushPose();
            poseStack.translate(t.x() + widthShiftFor(state, t.x()), t.y(), t.z() + limoShiftFor(state, t.z()));
            if (t.angleY() != 0f) {
                poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(t.angleY()));
            }
            if (t.angle() != 0f) {
                poseStack.mulPose(com.mojang.math.Vector3f.XP.rotationDegrees(t.angle()));
            }

            float stretchScaleX = (t.stretchXPos() + t.stretchXNeg()) / 2f;
            float stretchScaleY = (t.stretchYPos() + t.stretchYNeg()) / 2f;
            float stretchScaleZ = (t.stretchZPos() + t.stretchZNeg()) / 2f;
            float offsetX = t.scale() * OTHER_PART_STRETCH_HALF_EXTENT * (t.stretchXPos() - t.stretchXNeg()) / 2f;
            float offsetY = t.scale() * OTHER_PART_STRETCH_HALF_EXTENT * (t.stretchYPos() - t.stretchYNeg()) / 2f;
            float offsetZ = t.scale() * OTHER_PART_STRETCH_HALF_EXTENT * (t.stretchZPos() - t.stretchZNeg()) / 2f;
            if (offsetX != 0f || offsetY != 0f || offsetZ != 0f) {
                poseStack.translate(offsetX, offsetY, offsetZ);
            }
            if (t.scale() != 1f || stretchScaleX != 1f || stretchScaleY != 1f || stretchScaleZ != 1f) {
                poseStack.scale(t.scale() * stretchScaleX, t.scale() * stretchScaleY, t.scale() * stretchScaleZ);
            }
            for (CompiledCarModel.TextureGroup group : wingModel.groups) {
                ResourceLocation texture = group.texture;
                final int tint = group.tintable ? state.color : 0xFFFFFF;
                RenderType renderType = groupRenderType(group, texture);
                collector.submitCustomGeometry(poseStack, renderType, (pose, vertexConsumer) ->
                        renderGroup(group.vertexData, pose, vertexConsumer, state.lightCoords, tint, BodyDeform.NONE, usesTriangles(group)));
            }
            poseStack.popPose();
        }
    }

    private static final float OTHER_PART_STRETCH_HALF_EXTENT = 0.3f;

    private void renderOtherPart(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector) {
        if (state.otherPartEnabledIds == null || state.otherPartEnabledIds.isEmpty()) {
            return;
        }
        Set<String> enabledIds = parseEnabledIds(state.otherPartEnabledIds);
        net.minecraft.server.packs.resources.ResourceManager resourceManager =
                net.minecraft.client.Minecraft.getInstance().getResourceManager();
        for (com.examplebett.render.OtherParts.Option option : com.examplebett.render.OtherParts.list(resourceManager)) {
            if (!enabledIds.contains(option.id())) {
                continue;
            }

            boolean spareTire = com.examplebett.render.OtherParts.SPARE_TIRE_ID.equals(option.id());

            boolean seatedPlayer = com.examplebett.render.OtherParts.isSeatedCreature(option.id());
            CompiledCarModel partModel = seatedPlayer
                    ? null
                    : spareTire
                            ? state.wheelCompiledModel
                            : OtherPartModel.get(resourceManager, option.modelId());
            if (partModel == null && !seatedPlayer) {
                continue;
            }
            com.examplebett.render.PartTransforms.Transform12 t =
                    com.examplebett.render.PartTransforms.resolve12(state.otherPartTransformsRaw, option.id(),
                            com.examplebett.entity.CarEntity.defaultOtherPartTransform(option.id()));
            poseStack.pushPose();
            poseStack.translate(t.x() + widthShiftFor(state, t.x()), t.y(), t.z() + limoShiftFor(state, t.z()));
            if (t.angleY() != 0f) {
                poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(t.angleY()));
            }
            if (t.angle() != 0f) {
                poseStack.mulPose(com.mojang.math.Vector3f.XP.rotationDegrees(t.angle()));
            }

            float stretchScaleX = (t.stretchXPos() + t.stretchXNeg()) / 2f;
            float stretchScaleY = (t.stretchYPos() + t.stretchYNeg()) / 2f;
            float stretchScaleZ = (t.stretchZPos() + t.stretchZNeg()) / 2f;
            float offsetX = t.scale() * OTHER_PART_STRETCH_HALF_EXTENT * (t.stretchXPos() - t.stretchXNeg()) / 2f;
            float offsetY = t.scale() * OTHER_PART_STRETCH_HALF_EXTENT * (t.stretchYPos() - t.stretchYNeg()) / 2f;
            float offsetZ = t.scale() * OTHER_PART_STRETCH_HALF_EXTENT * (t.stretchZPos() - t.stretchZNeg()) / 2f;
            if (offsetX != 0f || offsetY != 0f || offsetZ != 0f) {
                poseStack.translate(offsetX, offsetY, offsetZ);
            }
            if (t.scale() != 1f || stretchScaleX != 1f || stretchScaleY != 1f || stretchScaleZ != 1f) {
                poseStack.scale(t.scale() * stretchScaleX, t.scale() * stretchScaleY, t.scale() * stretchScaleZ);
            }
            if (spareTire) {

                poseStack.mulPose(com.mojang.math.Vector3f.ZP.rotationDegrees(90f));
                float scaleX = state.wheelScale * state.wheelWidthScale;
                if (scaleX != 1f || state.wheelScale != 1f) {
                    poseStack.scale(scaleX, state.wheelScale, state.wheelScale);
                }
            }
            if (seatedPlayer) {
                if (com.examplebett.render.OtherParts.SITTING_WOLF_ID.equals(option.id())) {
                    renderSittingWolf(state, poseStack, collector);
                } else {
                    renderSeatedPlayer(state, poseStack, collector,
                            com.examplebett.render.OtherParts.SITTING_ALEX_ID.equals(option.id()));
                }
                poseStack.popPose();
                continue;
            }

            boolean wholeVehicle = com.examplebett.render.OtherParts.R34_ID.equals(option.id());
            int partColor = wholeVehicle ? 0xFFFFFF : state.color;
            for (CompiledCarModel.TextureGroup group : partModel.groups) {
                ResourceLocation texture = group.texture;

                boolean usesPaintMask = group.tintable && PaintTintTexture.hasPaint(group.texture);
                if (usesPaintMask) {
                    texture = PaintTintTexture.resolve(group.texture, partColor);
                }
                texture = metalPaintTexture(group, texture, usesPaintMask, partColor);
                final int tint = (group.tintable && !usesPaintMask) ? partColor : 0xFFFFFF;
                RenderType renderType = groupRenderType(group, texture);
                collector.submitCustomGeometry(poseStack, renderType, (pose, vertexConsumer) ->
                        renderGroup(group.vertexData, pose, vertexConsumer, state.lightCoords, tint, BodyDeform.NONE, usesTriangles(group)));
            }
            poseStack.popPose();
        }
    }

    private static final float TEXT_PART_PIXEL_SIZE = 0.02f;

    private static final float TEXT_PART_LAYER_SPACING = 0.004f;
    private static final int TEXT_PART_MAX_LAYERS = 24;

    private static final float TEXT_PART_SIDE_SHADE = 0.55f;
    private static final int TEXT_PART_FULL_BRIGHT = 0xF000F0;

    private static final int TEXT_PART_CACHE_LIMIT = 256;
    private static final Map<String, com.examplebett.render.TextParts.Slot[]> TEXT_PARTS_CACHE = new HashMap<>();
    private static final Map<String, net.minecraft.util.FormattedCharSequence> TEXT_SEQUENCE_CACHE = new HashMap<>();

    private static com.examplebett.render.TextParts.Slot[] parseTextParts(String raw) {
        if (TEXT_PARTS_CACHE.size() > TEXT_PART_CACHE_LIMIT) {
            TEXT_PARTS_CACHE.clear();
        }
        return TEXT_PARTS_CACHE.computeIfAbsent(raw, com.examplebett.render.TextParts::parse);
    }

    private static net.minecraft.util.FormattedCharSequence textPartSequence(com.examplebett.render.TextParts.Slot slot) {
        if (TEXT_SEQUENCE_CACHE.size() > TEXT_PART_CACHE_LIMIT) {
            TEXT_SEQUENCE_CACHE.clear();
        }
        return TEXT_SEQUENCE_CACHE.computeIfAbsent(slot.font() + "\n" + slot.text(), key -> {
            net.minecraft.network.chat.MutableComponent component = com.examplebett.compat.ModText.literal(slot.text()).copy();
            if (!com.examplebett.render.TextParts.FONT_DEFAULT.equals(slot.font())) {
                ResourceLocation fontId = AiObjectMod.id("text_" + slot.font());

                component = component.withStyle(style -> style.withFont(fontId));
            }

            return component.getVisualOrderText();
        });
    }

    private static final ResourceLocation UNDER_NEON_TEXTURE =
            AiObjectMod.id("textures/entity/lights/under_neon_glow.png");
    private static final Map<String, com.examplebett.render.UnderNeon.Slot[]> UNDER_NEON_CACHE = new HashMap<>();

    private void renderUnderNeon(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector) {
        if (state.underNeonRaw == null || state.underNeonRaw.isEmpty()) {
            return;
        }
        if (UNDER_NEON_CACHE.size() > TEXT_PART_CACHE_LIMIT) {
            UNDER_NEON_CACHE.clear();
        }
        com.examplebett.render.UnderNeon.Slot[] slots = UNDER_NEON_CACHE.computeIfAbsent(state.underNeonRaw, com.examplebett.render.UnderNeon::parse);
        RenderType neonType = RenderType.itemEntityTranslucentCull(UNDER_NEON_TEXTURE);
        for (com.examplebett.render.UnderNeon.Slot slot : slots) {
            if (slot == null || !slot.enabled()) {
                continue;
            }
            float r = ((slot.color() >> 16) & 0xFF) / 255f;
            float g = ((slot.color() >> 8) & 0xFF) / 255f;
            float b = (slot.color() & 0xFF) / 255f;
            float alpha = slot.brightness();
            float hw = slot.width() / 2f;
            float hl = slot.length() / 2f;
            poseStack.pushPose();
            poseStack.translate(slot.x(), slot.y(), slot.z());
            poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(slot.yaw()));
            collector.late().submitCustomGeometry(poseStack, neonType, (pose, consumer) -> {
                addGlowVertex(consumer, pose, -hw, 0f, -hl, 0f, 0f, r, g, b, alpha);
                addGlowVertex(consumer, pose, -hw, 0f, hl, 0f, 1f, r, g, b, alpha);
                addGlowVertex(consumer, pose, hw, 0f, hl, 1f, 1f, r, g, b, alpha);
                addGlowVertex(consumer, pose, hw, 0f, -hl, 1f, 0f, r, g, b, alpha);

                addGlowVertex(consumer, pose, -hw, 0f, -hl, 0f, 0f, r, g, b, alpha);
                addGlowVertex(consumer, pose, hw, 0f, -hl, 1f, 0f, r, g, b, alpha);
                addGlowVertex(consumer, pose, hw, 0f, hl, 1f, 1f, r, g, b, alpha);
                addGlowVertex(consumer, pose, -hw, 0f, hl, 0f, 1f, r, g, b, alpha);
            });
            poseStack.popPose();
        }
    }

    private static final Map<String, com.examplebett.render.BrandLogos.Slot[]> BRAND_LOGO_CACHE = new HashMap<>();

    private static final float BRAND_LOGO_HEIGHT = 0.12f;

    private static final float BRAND_LOGO_RECESS_RATIO = 0.5f;

    private static final int BRAND_LOGO_GLINT_GRID = 16;
    private static final float BRAND_LOGO_GLINT_OFFSET = 0.0005f;
    private static final float BRAND_LOGO_GLINT_STRENGTH = 0.6f;

    private void renderBrandLogos(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector) {
        if (state.brandLogosRaw == null || state.brandLogosRaw.isEmpty() || state.brandLogoDetail == 0) {
            return;
        }
        boolean detailed = state.brandLogoDetail >= 2;
        if (BRAND_LOGO_CACHE.size() > TEXT_PART_CACHE_LIMIT) {
            BRAND_LOGO_CACHE.clear();
        }
        com.examplebett.render.BrandLogos.Slot[] slots = BRAND_LOGO_CACHE.computeIfAbsent(state.brandLogosRaw, com.examplebett.render.BrandLogos::parse);
        boolean glint = com.examplebett.client.AiObjectModClient.isFakeReflectionEnabled();
        for (com.examplebett.render.BrandLogos.Slot slot : slots) {
            if (slot == null || !slot.enabled()) {
                continue;
            }

            int light = slot.glow() ? TEXT_PART_FULL_BRIGHT : state.lightCoords;
            BrandLogoTextures.Design design = BrandLogoTextures.design(slot.logo());

            BrandLogoMesh.Mesh mesh = design.flat() ? null : BrandLogoMesh.get(design);
            float height = BRAND_LOGO_HEIGHT * slot.scale();
            float pixel = height / (mesh != null ? mesh.height() : design.height());
            float halfW = (mesh != null ? mesh.width() : design.width()) * pixel / 2f;
            float halfH = height / 2f;
            float depth = slot.depth();
            float[] levelZ = {0f, depth * (1f - BRAND_LOGO_RECESS_RATIO), depth};

            poseStack.pushPose();
            poseStack.translate(slot.x() + widthShiftFor(state, slot.x()), slot.y(), slot.z() + limoShiftFor(state, slot.z()));
            poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(slot.yaw()));
            poseStack.mulPose(com.mojang.math.Vector3f.XP.rotationDegrees(slot.pitch()));
            poseStack.mulPose(com.mojang.math.Vector3f.ZP.rotationDegrees(slot.roll()));
            if (design.flat()) {

                int layers = detailed
                        ? Math.min(TEXT_PART_MAX_LAYERS, Math.max(1, (int) Math.ceil(depth / TEXT_PART_LAYER_SPACING)))
                        : 0;
                collector.submitCustomGeometry(poseStack, RenderType.entityCutoutNoCull(design.texture()), (pose, consumer) -> {
                    for (int i = 0; i < layers; i++) {
                        addBrandLogoFace(consumer, pose, halfW, halfH, depth * i / layers, 1f, light, 1f, TEXT_PART_SIDE_SHADE);
                    }
                    addBrandLogoFace(consumer, pose, halfW, halfH, depth, 1f, light, 1f);
                });
                poseStack.popPose();
                continue;
            }
            collector.submitCustomGeometry(poseStack, RenderType.entityCutoutNoCull(design.texture()), (pose, consumer) -> {
                addBrandLogoFace(consumer, pose, halfW, halfH, levelZ[0], 1f, light, -1f);
                addBrandLogoFace(consumer, pose, halfW, halfH, levelZ[1], 1f, light, 1f);
                if (!detailed) {
                    return;
                }
                for (BrandLogoMesh.Wall wall : mesh.walls()) {
                    float x0 = -halfW + wall.x0() * pixel;
                    float y0 = halfH - wall.y0() * pixel;
                    float x1 = -halfW + wall.x1() * pixel;
                    float y1 = halfH - wall.y1() * pixel;
                    float zLow = levelZ[wall.lowLevel()];
                    float zHigh = levelZ[wall.highLevel()];

                    float nx = wall.nx();
                    float ny = -wall.ny();
                    addBrandLogoVertex(consumer, pose, x0, y0, zLow, wall.u0(), wall.v0(), light, 1f, nx, ny, 0f);
                    addBrandLogoVertex(consumer, pose, x1, y1, zLow, wall.u1(), wall.v1(), light, 1f, nx, ny, 0f);
                    addBrandLogoVertex(consumer, pose, x1, y1, zHigh, wall.u1(), wall.v1(), light, 1f, nx, ny, 0f);
                    addBrandLogoVertex(consumer, pose, x0, y0, zHigh, wall.u0(), wall.v0(), light, 1f, nx, ny, 0f);
                }
            });
            collector.submitCustomGeometry(poseStack, RenderType.entityCutoutNoCull(design.raisedTexture()), (pose, consumer) ->
                    addBrandLogoFace(consumer, pose, halfW, halfH, levelZ[2], 1f, light, 1f));
            if (glint && detailed) {
                collector.submitCustomGeometry(poseStack, RenderType.entityTranslucent(design.glintTexture()), (pose, consumer) ->
                        addBrandLogoGlint(consumer, pose, halfW, halfH, levelZ[2] + BRAND_LOGO_GLINT_OFFSET, light));
            }
            poseStack.popPose();
        }
    }

    private void addBrandLogoFace(VertexConsumer consumer, PoseStack.Pose pose, float halfW, float halfH, float z, float alpha, int light, float nz) {
        addBrandLogoFace(consumer, pose, halfW, halfH, z, alpha, light, nz, 1f);
    }

    private void addBrandLogoFace(VertexConsumer consumer, PoseStack.Pose pose, float halfW, float halfH, float z, float alpha, int light, float nz,
                                  float shade) {
        addBrandLogoVertex(consumer, pose, -halfW, -halfH, z, 0f, 1f, light, alpha, 0f, 0f, nz, shade);
        addBrandLogoVertex(consumer, pose, halfW, -halfH, z, 1f, 1f, light, alpha, 0f, 0f, nz, shade);
        addBrandLogoVertex(consumer, pose, halfW, halfH, z, 1f, 0f, light, alpha, 0f, 0f, nz, shade);
        addBrandLogoVertex(consumer, pose, -halfW, halfH, z, 0f, 0f, light, alpha, 0f, 0f, nz, shade);
    }

    private void addBrandLogoGlint(VertexConsumer consumer, PoseStack.Pose pose, float halfW, float halfH, float z, int light) {

        float[] camera = PoseMatrices.transformPosition(PoseMatrices.invert(PoseMatrices.capture(pose)), 0f, 0f, 0f);
        float toCameraX = camera[0];
        float toCameraY = camera[1];
        float toCameraZ = camera[2] - z;
        float toCameraLength = (float) Math.sqrt(toCameraX * toCameraX + toCameraY * toCameraY + toCameraZ * toCameraZ);
        if (toCameraLength < 1e-4f) {
            return;
        }
        float phase = 0.5f + 1.1f * (toCameraX / toCameraLength * 0.8f + toCameraY / toCameraLength * 0.6f);
        int blockLight = light & 0xFFFF;
        int skyLight = (light >> 16) & 0xFFFF;
        float strength = BRAND_LOGO_GLINT_STRENGTH * Math.max(blockLight, skyLight) / 240f;
        if (strength <= 0f) {
            return;
        }
        int n = BRAND_LOGO_GLINT_GRID;
        float[][] alpha = new float[n + 1][n + 1];
        boolean any = false;
        for (int j = 0; j <= n; j++) {
            for (int i = 0; i <= n; i++) {
                float u = (float) i / n;
                float v = (float) j / n;

                float s = (u * 0.8f + (1f - v) * 0.6f) / 1.4f;
                float value = Math.min(1f, glintBand(s, phase, 0.14f) + 0.6f * glintBand(s, phase - 0.24f, 0.05f)) * strength;
                alpha[j][i] = value;
                any |= value > 0.001f;
            }
        }
        if (!any) {
            return;
        }
        for (int j = 0; j < n; j++) {
            for (int i = 0; i < n; i++) {
                if (alpha[j][i] <= 0.001f && alpha[j][i + 1] <= 0.001f && alpha[j + 1][i] <= 0.001f && alpha[j + 1][i + 1] <= 0.001f) {
                    continue;
                }
                float u0 = (float) i / n;
                float u1 = (float) (i + 1) / n;
                float v0 = (float) j / n;
                float v1 = (float) (j + 1) / n;
                float x0 = -halfW + u0 * 2f * halfW;
                float x1 = -halfW + u1 * 2f * halfW;
                float y0 = halfH - v0 * 2f * halfH;
                float y1 = halfH - v1 * 2f * halfH;
                addBrandLogoVertex(consumer, pose, x0, y1, z, u0, v1, light, alpha[j + 1][i], 0f, 0f, 1f);
                addBrandLogoVertex(consumer, pose, x1, y1, z, u1, v1, light, alpha[j + 1][i + 1], 0f, 0f, 1f);
                addBrandLogoVertex(consumer, pose, x1, y0, z, u1, v0, light, alpha[j][i + 1], 0f, 0f, 1f);
                addBrandLogoVertex(consumer, pose, x0, y0, z, u0, v0, light, alpha[j][i], 0f, 0f, 1f);
            }
        }
    }

    private static float glintBand(float s, float center, float halfWidth) {
        return MetalTextConsumer.glintBand(s, center, halfWidth);
    }

    private void addBrandLogoVertex(VertexConsumer consumer, PoseStack.Pose pose, float x, float y, float z, float u, float v,
                                    int packedLight, float alpha, float nx, float ny, float nz) {
        addBrandLogoVertex(consumer, pose, x, y, z, u, v, packedLight, alpha, nx, ny, nz, 1f);
    }

    private void addBrandLogoVertex(VertexConsumer consumer, PoseStack.Pose pose, float x, float y, float z, float u, float v,
                                    int packedLight, float alpha, float nx, float ny, float nz, float shade) {
        consumer.vertex(pose.pose(), x, y, z)
                .color(shade, shade, shade, alpha)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(pose.normal(), nx, ny, nz)
                .endVertex();
    }

    private void renderTextParts(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector) {
        if (state.textPartsRaw == null || state.textPartsRaw.isEmpty()) {
            return;
        }
        net.minecraft.client.gui.Font font = net.minecraft.client.Minecraft.getInstance().font;
        for (com.examplebett.render.TextParts.Slot slot : parseTextParts(state.textPartsRaw)) {
            if (slot == null || !slot.visible()) {
                continue;
            }
            net.minecraft.util.FormattedCharSequence text = textPartSequence(slot);
            float left = -font.width(text) / 2f;
            float top = -font.lineHeight / 2f;
            int light = slot.glow() ? TEXT_PART_FULL_BRIGHT : state.lightCoords;
            int rgb = slot.color() & 0xFFFFFF;
            int sideRgb = ((int) (((rgb >> 16) & 0xFF) * TEXT_PART_SIDE_SHADE) << 16)
                    | ((int) (((rgb >> 8) & 0xFF) * TEXT_PART_SIDE_SHADE) << 8)
                    | (int) ((rgb & 0xFF) * TEXT_PART_SIDE_SHADE);
            float pixel = TEXT_PART_PIXEL_SIZE * slot.scale();

            float pixelX = pixel * slot.width();
            float pixelY = pixel * slot.height();

            poseStack.pushPose();
            poseStack.translate(slot.x() + widthShiftFor(state, slot.x()), slot.y(), slot.z() + limoShiftFor(state, slot.z()));
            poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(slot.yaw()));
            poseStack.mulPose(com.mojang.math.Vector3f.XP.rotationDegrees(slot.pitch()));
            poseStack.mulPose(com.mojang.math.Vector3f.ZP.rotationDegrees(slot.roll()));
            int layers = slot.depth() > 0f
                    ? Math.min(TEXT_PART_MAX_LAYERS, Math.max(1, (int) Math.ceil(slot.depth() / TEXT_PART_LAYER_SPACING)))
                    : 0;
            for (int i = layers; i >= 1; i--) {
                poseStack.pushPose();
                poseStack.translate(0f, 0f, -slot.depth() * i / layers);
                poseStack.scale(pixelX, -pixelY, pixel);
                collector.submitText(poseStack, text, left, top, 0xFF000000 | sideRgb, light);
                poseStack.popPose();
            }

            poseStack.scale(pixelX, -pixelY, pixel);
            boolean drawn = false;
            int metal = com.examplebett.client.gui.CarColorPalette.metalIndex(rgb);
            if (metal >= 0) {
                submitMetalText(poseStack, collector, font, text, left, top, rgb, metal, light, state.brandLogoDetail >= 2);
                drawn = true;
            }
            if (!drawn) {
                collector.submitText(poseStack, text, left, top, 0xFF000000 | rgb, light);
            }
            poseStack.popPose();
        }
    }

    private void submitMetalText(PoseStack poseStack, GeometrySubmitter collector, net.minecraft.client.gui.Font font,
                                 net.minecraft.util.FormattedCharSequence text, float left, float top, int rgb, int metal,
                                 int light, boolean band) {
        float width = Math.max(1f, font.width(text));
        float height = font.lineHeight;
        collector.submitWrappedText(poseStack, text, left, top, light, (out, pose) ->
                new MetalTextConsumer(out, pose, left, top, width, height, rgb, metal,
                        band ? MetalTextConsumer.phase(pose, left + width / 2f, top + height / 2f) : Float.NaN));
    }

    private static void computeExhaustOutlets(CarRenderState state, PoseStack poseStack, float[] origin) {
        int outlets = com.examplebett.render.ExhaustParts.outletsOf(state.exhaustEnabledId);
        com.examplebett.render.PartTransforms.Transform6 t = state.exhaustTransform;
        if (outlets == 0 || t == null) {
            state.exhaustTarget.clientExhaustOutlets = null;
            return;
        }
        float[] relative = PoseMatrices.relative(origin, PoseMatrices.capture(poseStack));
        float[] result = new float[outlets * 6];
        for (int i = 0; i < outlets; i++) {
            float side = i == 0 ? 1f : -1f;
            float localX = t.x() * side;
            float[] position = PoseMatrices.transformPosition(relative,
                    localX + widthShiftFor(state, localX), t.y(), t.z() + limoShiftFor(state, t.z()));

            float ax = (float) Math.toRadians(t.angle());
            float ay = (float) Math.toRadians(t.angleY() * side);
            float y1 = (float) Math.sin(ax);
            float z1 = -(float) Math.cos(ax);
            float x2 = z1 * (float) Math.sin(ay);
            float z2 = z1 * (float) Math.cos(ay);
            float[] direction = PoseMatrices.transformDirection(relative, x2, y1, z2);
            result[i * 6] = position[0];
            result[i * 6 + 1] = position[1];
            result[i * 6 + 2] = position[2];
            result[i * 6 + 3] = direction[0];
            result[i * 6 + 4] = direction[1];
            result[i * 6 + 5] = direction[2];
        }
        state.exhaustTarget.clientExhaustOutlets = result;
    }

    private void renderTaxiSign(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector) {
        if (state.taxiSignEnabledIds == null || state.taxiSignEnabledIds.isEmpty()) {
            return;
        }
        Set<String> enabledIds = parseEnabledIds(state.taxiSignEnabledIds);
        net.minecraft.server.packs.resources.ResourceManager resourceManager =
                net.minecraft.client.Minecraft.getInstance().getResourceManager();
        com.examplebett.render.PartTransforms.Transform12 defaults = new com.examplebett.render.PartTransforms.Transform12(
                com.examplebett.entity.CarEntity.DEFAULT_TAXI_SIGN_X, com.examplebett.entity.CarEntity.DEFAULT_TAXI_SIGN_Y,
                com.examplebett.entity.CarEntity.DEFAULT_TAXI_SIGN_Z, 0f, 0f, com.examplebett.entity.CarEntity.DEFAULT_TAXI_SIGN_SCALE,
                1f, 1f, 1f, 1f, 1f, 1f);
        for (com.examplebett.render.TaxiSignParts.Option option : com.examplebett.render.TaxiSignParts.list(resourceManager)) {
            if (!enabledIds.contains(option.id())) {
                continue;
            }
            CompiledCarModel signModel = TaxiSignModel.get(resourceManager, option.modelId());
            if (signModel == null) {
                continue;
            }
            com.examplebett.render.PartTransforms.Transform12 t =
                    com.examplebett.render.PartTransforms.resolve12(state.taxiSignTransformsRaw, option.id(), defaults);
            poseStack.pushPose();
            poseStack.translate(t.x() + widthShiftFor(state, t.x()), t.y(), t.z() + limoShiftFor(state, t.z()));
            if (t.angleY() != 0f) {
                poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(t.angleY()));
            }
            if (t.angle() != 0f) {
                poseStack.mulPose(com.mojang.math.Vector3f.XP.rotationDegrees(t.angle()));
            }

            float stretchScaleX = (t.stretchXPos() + t.stretchXNeg()) / 2f;
            float stretchScaleY = (t.stretchYPos() + t.stretchYNeg()) / 2f;
            float stretchScaleZ = (t.stretchZPos() + t.stretchZNeg()) / 2f;
            float offsetX = t.scale() * OTHER_PART_STRETCH_HALF_EXTENT * (t.stretchXPos() - t.stretchXNeg()) / 2f;
            float offsetY = t.scale() * OTHER_PART_STRETCH_HALF_EXTENT * (t.stretchYPos() - t.stretchYNeg()) / 2f;
            float offsetZ = t.scale() * OTHER_PART_STRETCH_HALF_EXTENT * (t.stretchZPos() - t.stretchZNeg()) / 2f;
            if (offsetX != 0f || offsetY != 0f || offsetZ != 0f) {
                poseStack.translate(offsetX, offsetY, offsetZ);
            }
            if (t.scale() != 1f || stretchScaleX != 1f || stretchScaleY != 1f || stretchScaleZ != 1f) {
                poseStack.scale(t.scale() * stretchScaleX, t.scale() * stretchScaleY, t.scale() * stretchScaleZ);
            }
            for (CompiledCarModel.TextureGroup group : signModel.groups) {
                ResourceLocation texture = group.texture;
                final int tint = group.tintable ? state.color : 0xFFFFFF;
                RenderType renderType = groupRenderType(group, texture);
                collector.submitCustomGeometry(poseStack, renderType, (pose, vertexConsumer) ->
                        renderGroup(group.vertexData, pose, vertexConsumer, state.lightCoords, tint, BodyDeform.NONE, usesTriangles(group)));
            }
            poseStack.popPose();
        }
    }

    private void renderPoliceLight(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector) {
        if (state.policeLightEnabledIds == null || state.policeLightEnabledIds.isEmpty()) {
            return;
        }
        Set<String> enabledIds = parseEnabledIds(state.policeLightEnabledIds);
        net.minecraft.server.packs.resources.ResourceManager resourceManager =
                net.minecraft.client.Minecraft.getInstance().getResourceManager();
        com.examplebett.render.PartTransforms.Transform12 defaults = new com.examplebett.render.PartTransforms.Transform12(
                com.examplebett.entity.CarEntity.DEFAULT_POLICE_LIGHT_X, com.examplebett.entity.CarEntity.DEFAULT_POLICE_LIGHT_Y,
                com.examplebett.entity.CarEntity.DEFAULT_POLICE_LIGHT_Z, 0f, 0f, com.examplebett.entity.CarEntity.DEFAULT_POLICE_LIGHT_SCALE,
                1f, 1f, 1f, 1f, 1f, 1f);
        for (com.examplebett.render.PoliceLightParts.Option option : com.examplebett.render.PoliceLightParts.list(resourceManager)) {
            if (!enabledIds.contains(option.id())) {
                continue;
            }

            CompiledCarModel lightModel = option.modelId() == null ? null : PoliceLightModel.get(resourceManager, option.modelId());
            if (lightModel == null && option.modelId() != null) {
                continue;
            }
            com.examplebett.render.PartTransforms.Transform12 t =
                    com.examplebett.render.PartTransforms.resolve12(state.policeLightTransformsRaw, option.id(), defaults);
            poseStack.pushPose();
            poseStack.translate(t.x() + widthShiftFor(state, t.x()), t.y(), t.z() + limoShiftFor(state, t.z()));
            if (t.angleY() != 0f) {
                poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(t.angleY()));
            }
            if (t.angle() != 0f) {
                poseStack.mulPose(com.mojang.math.Vector3f.XP.rotationDegrees(t.angle()));
            }

            float stretchScaleX = (t.stretchXPos() + t.stretchXNeg()) / 2f;
            float stretchScaleY = (t.stretchYPos() + t.stretchYNeg()) / 2f;
            float stretchScaleZ = (t.stretchZPos() + t.stretchZNeg()) / 2f;
            float offsetX = t.scale() * OTHER_PART_STRETCH_HALF_EXTENT * (t.stretchXPos() - t.stretchXNeg()) / 2f;
            float offsetY = t.scale() * OTHER_PART_STRETCH_HALF_EXTENT * (t.stretchYPos() - t.stretchYNeg()) / 2f;
            float offsetZ = t.scale() * OTHER_PART_STRETCH_HALF_EXTENT * (t.stretchZPos() - t.stretchZNeg()) / 2f;
            if (offsetX != 0f || offsetY != 0f || offsetZ != 0f) {
                poseStack.translate(offsetX, offsetY, offsetZ);
            }
            if (t.scale() != 1f || stretchScaleX != 1f || stretchScaleY != 1f || stretchScaleZ != 1f) {
                poseStack.scale(t.scale() * stretchScaleX, t.scale() * stretchScaleY, t.scale() * stretchScaleZ);
            }
            for (CompiledCarModel.TextureGroup group : lightModel == null ? List.<CompiledCarModel.TextureGroup>of() : lightModel.groups) {
                ResourceLocation texture = group.texture;
                final int tint = group.tintable ? state.color : 0xFFFFFF;
                RenderType renderType = groupRenderType(group, texture);
                collector.submitCustomGeometry(poseStack, renderType, (pose, vertexConsumer) ->
                        renderGroup(group.vertexData, pose, vertexConsumer, state.lightCoords, tint, BodyDeform.NONE, usesTriangles(group)));
            }
            if (state.policeLightsOn) {
                if (com.examplebett.render.PoliceLightParts.isEmergencyBeacon(option.id())) {
                    renderEmergencyBeaconLights(state, poseStack, collector);
                }
                if (com.examplebett.render.PoliceLightParts.JP_LIGHT_BAR_ID.equals(option.id())) {
                    renderJpLightBarLights(state, poseStack, collector);
                }
                if (com.examplebett.render.PoliceLightParts.hasUsLightBarLights(option.id())) {
                    renderUsLightBarLights(state, poseStack, collector, false);
                }
                if (com.examplebett.render.PoliceLightParts.isEuLightBar(option.id())) {
                    renderUsLightBarLights(state, poseStack, collector, true);
                }
                if (com.examplebett.render.PoliceLightParts.isEuPattern(option.id())) {
                    renderEuPatternLights(state, poseStack, collector,
                            com.examplebett.render.PoliceLightParts.isEuPatternLeftFirst(option.id()));
                }
                if (com.examplebett.render.PoliceLightParts.JP_PATTERN_ID.equals(option.id())) {
                    renderJpPatternLights(state, poseStack, collector);
                }
            }
            poseStack.popPose();
        }
    }

    private static final float ROTATING_LIGHT_ROTATE_DEG_PER_TICK = 15f;

    private static final int QUAD_FLASH_ON_TICKS = 3;
    private static final int QUAD_FLASH_GAP_TICKS = 3;
    private static final int QUAD_FLASH_GROUP_GAP_TICKS = 4;
    private static final int QUAD_FLASH_GROUP_SLOT_TICKS =
            2 * QUAD_FLASH_ON_TICKS + QUAD_FLASH_GAP_TICKS + QUAD_FLASH_GROUP_GAP_TICKS;
    private static final int QUAD_FLASH_CYCLE_TICKS = QUAD_FLASH_GROUP_SLOT_TICKS * 2;

    private record QuadFlashState(boolean groupATurn, boolean flashOn) {
    }

    private static QuadFlashState computeQuadFlashState(int age) {
        int cyclePos = age % QUAD_FLASH_CYCLE_TICKS;
        boolean groupATurn = cyclePos < QUAD_FLASH_GROUP_SLOT_TICKS;
        int localPhase = groupATurn ? cyclePos : cyclePos - QUAD_FLASH_GROUP_SLOT_TICKS;
        boolean flashOn = localPhase < QUAD_FLASH_ON_TICKS
                || (localPhase >= QUAD_FLASH_ON_TICKS + QUAD_FLASH_GAP_TICKS
                        && localPhase < 2 * QUAD_FLASH_ON_TICKS + QUAD_FLASH_GAP_TICKS);
        return new QuadFlashState(groupATurn, flashOn);
    }

    private static final float EMERGENCY_BEACON_RADIUS = 0.18f;

    private static final float EMERGENCY_BEACON_PIVOT_Z = -0.165f;

    private static final float EMERGENCY_BEACON_GLOW_SIZE = 0.36f;

    private static final float EMERGENCY_BEACON_SECOND_SET_OFFSET_DEG = 90f;

    private void renderEmergencyBeaconLights(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector) {
        float rotateDeg = (state.age * ROTATING_LIGHT_ROTATE_DEG_PER_TICK) % 360f;
        QuadFlashState flash = computeQuadFlashState(state.age);
        if (!flash.flashOn()) {
            return;
        }
        float setRotateDeg = flash.groupATurn() ? rotateDeg : rotateDeg + EMERGENCY_BEACON_SECOND_SET_OFFSET_DEG;
        submitRotatingLightGlow(poseStack, collector, 0f, 0f, EMERGENCY_BEACON_PIVOT_Z,
                EMERGENCY_BEACON_RADIUS, EMERGENCY_BEACON_GLOW_SIZE, setRotateDeg, 1f, 1f, 0f, 0f);
        submitRotatingLightGlow(poseStack, collector, 0f, 0f, EMERGENCY_BEACON_PIVOT_Z,
                EMERGENCY_BEACON_RADIUS, EMERGENCY_BEACON_GLOW_SIZE, setRotateDeg + 180f, 1f, 1f, 0f, 0f);
    }

    private void submitRotatingLightGlow(PoseStack poseStack, GeometrySubmitter collector,
                                          float pivotX, float pivotY, float pivotZ, float radius, float glowSize,
                                          float rotateDeg, float alpha, float tintR, float tintG, float tintB) {
        double rotateRad = Math.toRadians(rotateDeg);
        float dx = (float) (Math.sin(rotateRad) * radius);
        float dz = (float) (Math.cos(rotateRad) * radius);
        submitGlow(poseStack, collector, HEADLIGHT_GLOW_TEXTURE,
                pivotX + dx, pivotY, pivotZ + dz, glowSize, 0f,
                tintR, tintG, tintB, alpha);
    }

    private static final float LIGHT_BAR_ROTATOR_RADIUS = 0.15f;

    private static final float LIGHT_BAR_ROTATOR_GLOW_SIZE = 0.30f;

    private static final float[][] JP_LIGHT_BAR_ROTATOR_POSITIONS = {
            {-0.80f, 0.181f, -0.205f},
            {-0.35f, 0.179f, -0.015f},
            {0.35f, 0.177f, 0.011f},
            {0.80f, 0.173f, -0.205f},
    };

    private void renderJpLightBarLights(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector) {
        float rotateDeg = (state.age * ROTATING_LIGHT_ROTATE_DEG_PER_TICK) % 360f;
        QuadFlashState flash = computeQuadFlashState(state.age);
        if (!flash.flashOn()) {
            return;
        }

        for (int i = 0; i < JP_LIGHT_BAR_ROTATOR_POSITIONS.length; i++) {
            boolean isCenterSet = i == 1 || i == 2;
            if (isCenterSet != flash.groupATurn()) {
                continue;
            }
            float[] pos = JP_LIGHT_BAR_ROTATOR_POSITIONS[i];
            submitRotatingLightGlow(poseStack, collector, pos[0], pos[1], pos[2],
                    LIGHT_BAR_ROTATOR_RADIUS, LIGHT_BAR_ROTATOR_GLOW_SIZE, rotateDeg, 1f, 1f, 0f, 0f);
            submitRotatingLightGlow(poseStack, collector, pos[0], pos[1], pos[2],
                    LIGHT_BAR_ROTATOR_RADIUS, LIGHT_BAR_ROTATOR_GLOW_SIZE, rotateDeg + 180f, 1f, 1f, 0f, 0f);
        }
    }

    private static final float[][] US_LIGHT_BAR_ROTATOR_POSITIONS = {
            {-0.80f, 0.138f, 0.015f},
            {-0.35f, 0.138f, 0.015f},
            {0.35f, 0.138f, 0.015f},
            {0.80f, 0.138f, 0.015f},
    };

    private void renderUsLightBarLights(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector,
                                        boolean allBlue) {
        float rotateDeg = (state.age * ROTATING_LIGHT_ROTATE_DEG_PER_TICK) % 360f;
        QuadFlashState flash = computeQuadFlashState(state.age);
        if (!flash.flashOn()) {
            return;
        }

        for (int i = 0; i < US_LIGHT_BAR_ROTATOR_POSITIONS.length; i++) {
            boolean isCenterSet = i == 1 || i == 2;
            if (isCenterSet != flash.groupATurn()) {
                continue;
            }
            float[] pos = US_LIGHT_BAR_ROTATOR_POSITIONS[i];
            boolean isBlue = allBlue || i >= 2;
            float tintR = isBlue ? 0f : 1f;
            float tintB = isBlue ? 1f : 0f;
            submitRotatingLightGlow(poseStack, collector, pos[0], pos[1], pos[2],
                    LIGHT_BAR_ROTATOR_RADIUS, LIGHT_BAR_ROTATOR_GLOW_SIZE, rotateDeg, 1f, tintR, 0f, tintB);
            submitRotatingLightGlow(poseStack, collector, pos[0], pos[1], pos[2],
                    LIGHT_BAR_ROTATOR_RADIUS, LIGHT_BAR_ROTATOR_GLOW_SIZE, rotateDeg + 180f, 1f, tintR, 0f, tintB);
        }
    }

    private static final int EU_PATTERN_STEP_TICKS = 2;

    private static final int JP_PATTERN_DARK_TICKS = 10;

    private static final float[] SIDE_PAIR_PATTERN_X = {-0.475f, -0.325f, 0.325f, 0.475f};

    private static final boolean SIDE_PAIR_PATTERN_SINGLE_LIGHT = true;

    private void submitSidePairPatternGlow(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector,
                                           int index, float tintR, float tintB) {
        float x = SIDE_PAIR_PATTERN_X[index];
        if (SIDE_PAIR_PATTERN_SINGLE_LIGHT) {

            if (state.age % EU_PATTERN_STEP_TICKS >= EU_PATTERN_STEP_TICKS / 2) {
                return;
            }

            x = index < 2 ? (SIDE_PAIR_PATTERN_X[0] + SIDE_PAIR_PATTERN_X[1]) / 2f
                    : (SIDE_PAIR_PATTERN_X[2] + SIDE_PAIR_PATTERN_X[3]) / 2f;
        }
        float[] usPos = US_LIGHT_BAR_ROTATOR_POSITIONS[index];
        submitGlow(poseStack, collector, HEADLIGHT_GLOW_TEXTURE, x, usPos[1], usPos[2],
                LIGHT_BAR_ROTATOR_GLOW_SIZE, 0f, tintR, 0f, tintB, 1f);
    }

    private void renderEuPatternLights(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector,
                                       boolean leftFirst) {
        int step = (state.age / EU_PATTERN_STEP_TICKS) % 8;
        boolean firstSide = step < 4;
        boolean leftSide = firstSide == leftFirst;

        int outer = leftSide ? 0 : 3;
        int inner = leftSide ? 1 : 2;
        submitSidePairPatternGlow(state, poseStack, collector, (step % 2 == 0) ? outer : inner, 0f, 1f);
    }

    private void renderJpPatternLights(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector) {
        int flashTicks = EU_PATTERN_STEP_TICKS * 4;
        int pos = state.age % (flashTicks + JP_PATTERN_DARK_TICKS);
        if (pos >= flashTicks) {
            return;
        }
        boolean outerTurn = (pos / EU_PATTERN_STEP_TICKS) % 2 == 0;
        submitSidePairPatternGlow(state, poseStack, collector, outerTurn ? 0 : 1, 1f, 0f);
        submitSidePairPatternGlow(state, poseStack, collector, outerTurn ? 3 : 2, 1f, 0f);
    }

    private static void applyGroupTransform(PoseStack poseStack, CarRenderState state,
                                             CompiledCarModel.TextureGroup group, boolean spinning, boolean rotorSpinning,
                                             float extraZ) {

        if ((spinning || rotorSpinning)
                && (state.suspensionPitchDegrees != 0f || state.suspensionRollDegrees != 0f)) {
            poseStack.translate(0.0, state.tumblePivotHeight, 0.0);
            if (state.suspensionRollDegrees != 0f) {
                poseStack.mulPose(com.mojang.math.Vector3f.ZP.rotationDegrees(-state.suspensionRollDegrees));
            }
            if (state.suspensionPitchDegrees != 0f) {
                poseStack.mulPose(com.mojang.math.Vector3f.XP.rotationDegrees(-state.suspensionPitchDegrees));
            }
            poseStack.translate(0.0, -state.tumblePivotHeight, 0.0);
        }
        if (spinning) {
            ObjModel.WheelPivot pivot = state.compiledModel.wheelPivots.get(group.wheelGroup);

            if (state.bodyHeightOffset != 0f) {
                poseStack.translate(0.0, -state.bodyHeightOffset, 0.0);
            }

            float sideSign = embeddedWheelSideSign(pivot);
            poseStack.translate(embeddedWheelX(state, pivot), embeddedWheelY(state, pivot),
                    embeddedWheelZ(state, pivot.centerZ + extraZ, pivot.front));

            if (state.wheelCamberDegrees != 0f) {
                poseStack.mulPose(com.mojang.math.Vector3f.ZP.rotationDegrees(sideSign * state.wheelCamberDegrees));
            }

            if (pivot.front) {
                poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(state.frontWheelSteerDegrees));
            }

            float rollDegrees = state.wheelRotationDegrees + (pivot.front ? 0f : state.rearWheelExtraSpinDegrees);
            poseStack.mulPose(wheelAxisRotation(pivot, rollDegrees));

            float axialScale = state.wheelScale * state.wheelWidthScale;
            if (axialScale != 1f || state.wheelScale != 1f) {
                poseStack.scale(pivot.axisX >= 0.5f ? axialScale : state.wheelScale,
                        pivot.axisY >= 0.5f ? axialScale : state.wheelScale,
                        pivot.axisZ >= 0.5f ? axialScale : state.wheelScale);
            }
            poseStack.translate(-pivot.centerX, -pivot.centerY, -pivot.centerZ);
        } else if (rotorSpinning) {
            ObjModel.RotorPivot pivot = state.compiledModel.rotorPivots.get(group.rotorGroup);
            poseStack.translate(pivot.centerX, pivot.centerY, pivot.centerZ);
            poseStack.mulPose(rotorAxisRotation(pivot, state.rotorRotationDegrees));
            poseStack.translate(-pivot.centerX, -pivot.centerY, -pivot.centerZ);
        }
    }

    private static float embeddedWheelSideSign(ObjModel.WheelPivot pivot) {
        return pivot.centerX >= 0f ? 1f : -1f;
    }

    private static float embeddedWheelX(CarRenderState state, ObjModel.WheelPivot pivot) {
        float x = pivot.centerX + embeddedWheelSideSign(pivot) * state.wheelTrackHalfWidthOffset;
        return x + widthShiftFor(state, x);
    }

    private static float embeddedWheelY(CarRenderState state, ObjModel.WheelPivot pivot) {
        return pivot.centerY + state.wheelAxleHeightOffset;
    }

    private static float embeddedWheelZ(CarRenderState state, float baseZ, boolean front) {
        float z = baseZ + (front ? state.wheelFrontZOffset : state.wheelRearZOffset);
        return z + limoShiftFor(state, z);
    }

    private static com.mojang.math.Quaternion wheelAxisRotation(ObjModel.WheelPivot pivot, float degrees) {
        if (pivot.axisX >= 0.5f) {
            return com.mojang.math.Vector3f.XP.rotationDegrees(degrees);
        }
        if (pivot.axisY >= 0.5f) {
            return com.mojang.math.Vector3f.YP.rotationDegrees(degrees);
        }
        return com.mojang.math.Vector3f.ZP.rotationDegrees(degrees);
    }

    private static com.mojang.math.Quaternion rotorAxisRotation(ObjModel.RotorPivot pivot, float degrees) {
        if (pivot.axisX >= 0.5f) {
            return com.mojang.math.Vector3f.XP.rotationDegrees(degrees);
        }
        if (pivot.axisY >= 0.5f) {
            return com.mojang.math.Vector3f.YP.rotationDegrees(degrees);
        }
        return com.mojang.math.Vector3f.ZP.rotationDegrees(degrees);
    }

    private static final float PLATE_HALF_WIDTH = 0.45f;

    private void submitPlate(PoseStack poseStack, GeometrySubmitter collector, int packedLight, ResourceLocation texture,
                              float x, float y, float z, float scale, float yawDeg, float pitchDeg, float rollDeg, float aspect) {
        poseStack.pushPose();
        poseStack.translate(x, y, z);
        poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(yawDeg));
        poseStack.mulPose(com.mojang.math.Vector3f.XP.rotationDegrees(pitchDeg));
        poseStack.mulPose(com.mojang.math.Vector3f.ZP.rotationDegrees(rollDeg));
        RenderType plateType = RenderType.entityCutoutNoCull(texture);
        collector.submitCustomGeometry(poseStack, plateType, (pose, vertexConsumer) ->
                renderPlate(pose, vertexConsumer, packedLight, scale, aspect));
        poseStack.popPose();
    }

    private void renderPlate(PoseStack.Pose pose, VertexConsumer consumer, int packedLight, float scale, float aspect) {
        float halfW = PLATE_HALF_WIDTH * scale;
        float halfH = (PLATE_HALF_WIDTH / aspect) * scale;
        addPlateVertex(consumer, pose, -halfW, -halfH, 0f, 0f, 1f, packedLight);
        addPlateVertex(consumer, pose, halfW, -halfH, 0f, 1f, 1f, packedLight);
        addPlateVertex(consumer, pose, halfW, halfH, 0f, 1f, 0f, packedLight);
        addPlateVertex(consumer, pose, -halfW, halfH, 0f, 0f, 0f, packedLight);
    }

    private void addPlateVertex(VertexConsumer consumer, PoseStack.Pose pose, float x, float y, float z, float u, float v, int packedLight) {
        consumer.vertex(pose.pose(), x, y, z)
                .color(1f, 1f, 1f, 1f)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(pose.normal(), 0f, 1f, 0f)
                .endVertex();
    }

    private static final ResourceLocation HEADLIGHT_GLOW_TEXTURE =
            AiObjectMod.id("textures/entity/lights/headlight_glow.png");
    private static final ResourceLocation TAILLIGHT_GLOW_TEXTURE =
            AiObjectMod.id("textures/entity/lights/taillight_glow.png");
    private static final float HEADLIGHT_GLOW_SIZE = 0.35f;
    private static final float TAILLIGHT_GLOW_SIZE = 0.25f;

    private static final int FULL_BRIGHT_LIGHT = 0xF000F0;

    private void submitGlow(PoseStack poseStack, GeometrySubmitter collector, ResourceLocation texture,
                             float x, float y, float z, float halfSize, float yawDeg,
                             float tintR, float tintG, float tintB) {
        submitGlow(poseStack, collector, texture, x, y, z, halfSize, yawDeg, tintR, tintG, tintB, 1f);
    }

    private void submitGlow(PoseStack poseStack, GeometrySubmitter collector, ResourceLocation texture,
                             float x, float y, float z, float halfSize, float yawDeg,
                             float tintR, float tintG, float tintB, float alpha) {
        poseStack.pushPose();
        poseStack.translate(x, y, z);

        RenderType glowType = RenderType.itemEntityTranslucentCull(texture);
        submitGlowQuad(poseStack, collector, glowType, halfSize, yawDeg, 0f, tintR, tintG, tintB, alpha);
        submitGlowQuad(poseStack, collector, glowType, halfSize, yawDeg + 90f, 0f, tintR, tintG, tintB, alpha);
        submitGlowQuad(poseStack, collector, glowType, halfSize, yawDeg, 90f, tintR, tintG, tintB, alpha);
        poseStack.popPose();
    }

    private void submitGlowQuad(PoseStack poseStack, GeometrySubmitter collector, RenderType glowType,
                                 float halfSize, float yawDeg, float pitchDeg,
                                 float tintR, float tintG, float tintB, float alpha) {
        poseStack.pushPose();
        poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(yawDeg));
        poseStack.mulPose(com.mojang.math.Vector3f.XP.rotationDegrees(pitchDeg));
        collector.late().submitCustomGeometry(poseStack, glowType, (pose, vertexConsumer) ->
                renderGlow(pose, vertexConsumer, halfSize, tintR, tintG, tintB, alpha));
        poseStack.popPose();
    }

    private void renderGlow(PoseStack.Pose pose, VertexConsumer consumer, float halfSize,
                             float tintR, float tintG, float tintB, float alpha) {
        addGlowVertex(consumer, pose, -halfSize, -halfSize, 0f, 0f, 1f, tintR, tintG, tintB, alpha);
        addGlowVertex(consumer, pose, halfSize, -halfSize, 0f, 1f, 1f, tintR, tintG, tintB, alpha);
        addGlowVertex(consumer, pose, halfSize, halfSize, 0f, 1f, 0f, tintR, tintG, tintB, alpha);
        addGlowVertex(consumer, pose, -halfSize, halfSize, 0f, 0f, 0f, tintR, tintG, tintB, alpha);

        addGlowVertex(consumer, pose, -halfSize, -halfSize, 0f, 0f, 1f, tintR, tintG, tintB, alpha);
        addGlowVertex(consumer, pose, -halfSize, halfSize, 0f, 0f, 0f, tintR, tintG, tintB, alpha);
        addGlowVertex(consumer, pose, halfSize, halfSize, 0f, 1f, 0f, tintR, tintG, tintB, alpha);
        addGlowVertex(consumer, pose, halfSize, -halfSize, 0f, 1f, 1f, tintR, tintG, tintB, alpha);
    }

    private void addGlowVertex(VertexConsumer consumer, PoseStack.Pose pose, float x, float y, float z, float u, float v,
                                float tintR, float tintG, float tintB, float alpha) {
        consumer.vertex(pose.pose(), x, y, z)
                .color(tintR, tintG, tintB, alpha)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(FULL_BRIGHT_LIGHT)
                .normal(pose.normal(), 0f, 1f, 0f)
                .endVertex();
    }

    private record BodyDeform(boolean active, float bodyMinY,
                               float lipStretchScale, float lipWidthScale, float lipBandHeight, float lipFrontExtendScale,
                               float limoAnchorZ, float limoStretchM, float limoZoneM,
                               float widthAnchorX, float widthStretchM, float widthZoneM) {
        static final BodyDeform NONE = new BodyDeform(false, Float.NaN, 1f, 1f, 0f, 1f, Float.NaN, 0f, 0f, Float.NaN, 0f, 0f);

        static BodyDeform from(CarRenderState state) {
            boolean lipActive = !Float.isNaN(state.compiledModel.bodyMinY)
                    && (state.lipStretchScale != 1f || state.lipWidthScale != 1f || state.lipFrontExtendScale != 1f);
            boolean limoActive = !Float.isNaN(state.compiledModel.bodyCenterZ) && state.limoStretchCm != 0f;
            boolean widthActive = !Float.isNaN(state.compiledModel.bodyCenterX) && state.widthStretchCm != 0f;
            if (!lipActive && !limoActive && !widthActive) {
                return NONE;
            }

            float limoAnchorZ = state.compiledModel.bodyCenterZ + state.limoCenterOffsetCm / 100f;

            float widthAnchorX = state.compiledModel.bodyCenterX + state.widthCenterOffsetCm / 100f;
            return new BodyDeform(true, state.compiledModel.bodyMinY,
                    state.lipStretchScale, state.lipWidthScale, state.lipBandHeightCm / 100f, state.lipFrontExtendScale,
                    limoAnchorZ, state.limoStretchCm / 100f, state.limoZoneCm / 100f,
                    widthAnchorX, state.widthStretchCm / 100f, state.widthZoneCm / 100f);
        }
    }

    private static boolean usesTriangles(CompiledCarModel.TextureGroup group) {
        return !group.translucent && LegacyShaderFix.isAvailable();
    }

    private static final ResourceLocation[] METAL_PAINT_TEXTURES = {
            AiObjectMod.id("textures/entity/paint/chrome.png"),
            AiObjectMod.id("textures/entity/paint/gold.png"),
            AiObjectMod.id("textures/entity/paint/metallic_silver.png")
    };

    private static final ResourceLocation BRONZE_PAINT_TEXTURE = AiObjectMod.id("textures/entity/paint/bronze.png");

    private static ResourceLocation metalPaintTexture(CompiledCarModel.TextureGroup group, ResourceLocation texture, boolean usesPaintMask, int color) {
        if (!group.tintable || usesPaintMask || !CompiledCarModel.FALLBACK_TEXTURE.equals(texture)) {
            return texture;
        }
        int metal = com.examplebett.client.gui.CarColorPalette.metalIndex(color);
        if (metal >= 0) {
            return METAL_PAINT_TEXTURES[metal];
        }
        return (color & 0xFFFFFF) == com.examplebett.client.gui.CarColorPalette.EXTRA_RGB[com.examplebett.client.gui.CarColorPalette.EXTRA_BRONZE]
                ? BRONZE_PAINT_TEXTURE
                : texture;
    }

    private static RenderType groupRenderType(CompiledCarModel.TextureGroup group, ResourceLocation texture) {
        if (usesTriangles(group)) {
            return LegacyShaderFix.cutoutType(texture, group.noCull);
        }
        return group.translucent
                ? RenderType.itemEntityTranslucentCull(texture)
                : group.noCull
                        ? RenderType.entityCutoutNoCull(texture)
                        : RenderType.entityCutout(texture);
    }

    private void renderGroup(float[] data, PoseStack.Pose pose, VertexConsumer vertexConsumer, int packedLight, int color, BodyDeform deform, boolean triangles) {
        float tintR = ((color >> 16) & 0xFF) / 255f;
        float tintG = ((color >> 8) & 0xFF) / 255f;
        float tintB = (color & 0xFF) / 255f;
        if (triangles) {

            LegacyShaderFix.markBuffer(vertexConsumer);
        }

        int legacyStart = LegacyShaderFix.legacyBegin(vertexConsumer);
        for (int i = 0; i < data.length; i += 36) {
            addVertex(vertexConsumer, pose, data, i, packedLight, tintR, tintG, tintB, deform);
            addVertex(vertexConsumer, pose, data, i + 12, packedLight, tintR, tintG, tintB, deform);
            addVertex(vertexConsumer, pose, data, i + 24, packedLight, tintR, tintG, tintB, deform);
            if (!triangles) {
                addVertex(vertexConsumer, pose, data, i + 24, packedLight, tintR, tintG, tintB, deform);
            }
        }
        LegacyShaderFix.legacyEnd(vertexConsumer, legacyStart, data, pose, triangles);
    }

    private static float lipWeight(float y, float bodyMinY, float bandHeight) {
        if (bandHeight <= 0f) return 0f;
        float t = (bodyMinY + bandHeight - y) / bandHeight;
        t = Math.max(0f, Math.min(1f, t));
        return t * t * (3f - 2f * t);
    }

    private static float limoWeight(float z, float anchorZ, float zoneM) {
        if (zoneM <= 0f) return 0f;
        float t = (anchorZ - z) / zoneM;
        t = Math.max(0f, Math.min(1f, t));
        return t * t * (3f - 2f * t);
    }

    private static float limoShift(float z, float anchorZ, float zoneM, float stretchM) {
        return -limoWeight(z, anchorZ, zoneM) * stretchM;
    }

    private static float widthWeight(float x, float anchorX, float zoneM) {
        if (zoneM <= 0f) return 0f;
        float t = (anchorX - x) / zoneM;
        t = Math.max(0f, Math.min(1f, t));
        return t * t * (3f - 2f * t);
    }

    private static float widthShift(float x, float anchorX, float zoneM, float stretchM) {
        return -widthWeight(x, anchorX, zoneM) * stretchM;
    }

    private static float limoShiftFor(CarRenderState state, float z) {
        if (Float.isNaN(state.compiledModel.bodyCenterZ) || state.limoStretchCm == 0f) {
            return 0f;
        }
        float anchorZ = state.compiledModel.bodyCenterZ + state.limoCenterOffsetCm / 100f;
        return limoShift(z, anchorZ, state.limoZoneCm / 100f, state.limoStretchCm / 100f);
    }

    private static float widthShiftFor(CarRenderState state, float x) {
        if (Float.isNaN(state.compiledModel.bodyCenterX) || state.widthStretchCm == 0f) {
            return 0f;
        }
        float anchorX = state.compiledModel.bodyCenterX + state.widthCenterOffsetCm / 100f;
        return widthShift(x, anchorX, state.widthZoneCm / 100f, state.widthStretchCm / 100f);
    }

    private void addVertex(VertexConsumer consumer, PoseStack.Pose pose, float[] data, int offset, int packedLight, float tintR, float tintG, float tintB,
                            BodyDeform deform) {
        float x = data[offset];
        float y = data[offset + 1];
        float z = data[offset + 2];
        if (deform.active()) {
            float weight = lipWeight(y, deform.bodyMinY(), deform.lipBandHeight());
            if (weight > 0f) {
                y -= weight * (deform.lipStretchScale() - 1f) * deform.lipBandHeight();
                x += weight * x * (deform.lipWidthScale() - 1f);

                z += weight * Math.max(z, 0f) * (deform.lipFrontExtendScale() - 1f);
            }
            z += limoShift(z, deform.limoAnchorZ(), deform.limoZoneM(), deform.limoStretchM());
            x += widthShift(x, deform.widthAnchorX(), deform.widthZoneM(), deform.widthStretchM());
        }

        consumer.vertex(pose.pose(), x, y, z)
                .color(data[offset + 5] * tintR, data[offset + 6] * tintG, data[offset + 7] * tintB, data[offset + 8])
                .uv(data[offset + 3], data[offset + 4])
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(pose.normal(), data[offset + 9], data[offset + 10], data[offset + 11])
                .endVertex();
    }

    private static final float REFLECTION_STRENGTH = 0.45f;

    private static final float REFLECTION_ANGLE_LOW = 0.85f;
    private static final float REFLECTION_ANGLE_HIGH = 0.99f;

    private static final float REFLECTION_NORMAL_OFFSET = 0.001f;

    private static float fakeReflectionBoost(PoseStack.Pose pose, float x, float y, float z, float nx, float ny, float nz, int packedLight) {
        com.mojang.math.Vector3f normal = new com.mojang.math.Vector3f(nx, ny, nz);
        normal.transform(pose.normal());
        if (normal.dot(normal) < 1e-8f) {
            return 0f;
        }
        normal.normalize();

        com.mojang.math.Vector4f viewDirH = new com.mojang.math.Vector4f(x, y, z, 1f);
        viewDirH.transform(pose.pose());
        com.mojang.math.Vector3f viewDir = new com.mojang.math.Vector3f(viewDirH.x(), viewDirH.y(), viewDirH.z());
        if (viewDir.dot(viewDir) < 1e-8f) {
            return 0f;
        }
        viewDir.normalize();

        float vDotN = viewDir.dot(normal);
        com.mojang.math.Vector3f reflectDir = normal.copy();
        reflectDir.mul(-2f * vDotN);
        reflectDir.add(viewDir);

        float t = (reflectDir.y() - REFLECTION_ANGLE_LOW) / (REFLECTION_ANGLE_HIGH - REFLECTION_ANGLE_LOW);
        t = Math.max(0f, Math.min(1f, t));
        float highlight = t * t * (3f - 2f * t);

        int blockLight = packedLight & 0xFFFF;
        int skyLight = (packedLight >> 16) & 0xFFFF;
        float lightFactor = Math.max(blockLight, skyLight) / 240f;

        return highlight * lightFactor * REFLECTION_STRENGTH;
    }

    private void renderReflectionOverlay(float[] data, PoseStack.Pose pose, VertexConsumer vertexConsumer, int packedLight, BodyDeform deform) {
        for (int i = 0; i < data.length; i += 36) {
            addReflectionVertex(vertexConsumer, pose, data, i, packedLight, deform);
            addReflectionVertex(vertexConsumer, pose, data, i + 12, packedLight, deform);
            addReflectionVertex(vertexConsumer, pose, data, i + 24, packedLight, deform);
            addReflectionVertex(vertexConsumer, pose, data, i + 24, packedLight, deform);
        }
    }

    private void addReflectionVertex(VertexConsumer consumer, PoseStack.Pose pose, float[] data, int offset, int packedLight, BodyDeform deform) {
        float x = data[offset];
        float y = data[offset + 1];
        float z = data[offset + 2];
        if (deform.active()) {
            float weight = lipWeight(y, deform.bodyMinY(), deform.lipBandHeight());
            if (weight > 0f) {
                y -= weight * (deform.lipStretchScale() - 1f) * deform.lipBandHeight();
                x += weight * x * (deform.lipWidthScale() - 1f);
                z += weight * Math.max(z, 0f) * (deform.lipFrontExtendScale() - 1f);
            }
            z += limoShift(z, deform.limoAnchorZ(), deform.limoZoneM(), deform.limoStretchM());
            x += widthShift(x, deform.widthAnchorX(), deform.widthZoneM(), deform.widthStretchM());
        }

        float nx = data[offset + 9];
        float ny = data[offset + 10];
        float nz = data[offset + 11];
        float boost = fakeReflectionBoost(pose, x, y, z, nx, ny, nz, packedLight);
        x += nx * REFLECTION_NORMAL_OFFSET;
        y += ny * REFLECTION_NORMAL_OFFSET;
        z += nz * REFLECTION_NORMAL_OFFSET;

        consumer.vertex(pose.pose(), x, y, z)
                .color(1f, 1f, 1f, boost)
                .uv(data[offset + 3], data[offset + 4])
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(pose.normal(), nx, ny, nz)
                .endVertex();
    }
}
*/
//?}
//? if <1.16.2 && >=1.16 {
/*
//~ axis_mulpose_compat
package com.examplebett.client.render;

import com.examplebett.AiObjectMod;
import com.examplebett.entity.CarEntity;
import com.examplebett.render.CompiledCarModel;
import com.examplebett.render.ObjModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CarEntityRenderer extends EntityRenderer<CarEntity> {

    private static final double LOD_WHEEL_DISTANCE = 32.0;
    private static final double LOD_WHEEL_DISTANCE_SQ = LOD_WHEEL_DISTANCE * LOD_WHEEL_DISTANCE;

    private static final double BRAND_LOGO_FLAT_DISTANCE = 5.0;
    private static final double BRAND_LOGO_FLAT_DISTANCE_SQ = BRAND_LOGO_FLAT_DISTANCE * BRAND_LOGO_FLAT_DISTANCE;
    private static final double BRAND_LOGO_HIDE_DISTANCE = 48.0;
    private static final double BRAND_LOGO_HIDE_DISTANCE_SQ = BRAND_LOGO_HIDE_DISTANCE * BRAND_LOGO_HIDE_DISTANCE;

    private static final boolean[] WHEEL_IS_FRONT_4 = {true, true, false, false};
    private static final boolean[] WHEEL_IS_FRONT_6 = {true, true, false, false, false, false};
    private static final float[] WHEEL_CAMBER_SIGN_4 = {1f, -1f, 1f, -1f};
    private static final float[] WHEEL_CAMBER_SIGN_6 = {1f, -1f, 1f, -1f, 1f, -1f};

    private static final boolean[] WHEEL_IS_FRONT_8 = {true, true, false, false, false, false, false, false};
    private static final float[] WHEEL_CAMBER_SIGN_8 = {1f, -1f, 1f, -1f, 1f, -1f, 1f, -1f};

    private static final boolean[] WHEEL_IS_FRONT_2 = {true, false};
    private static final float[] WHEEL_CAMBER_SIGN_2 = {1f, -1f};
    private static final float[] WHEEL_OFFSET_Z_BUF = new float[8];
    private static final float[] WHEEL_OFFSET_X_BUF = new float[8];

    private static final Map<String, Set<String>> ENABLED_IDS_CACHE = new HashMap<>();

    private static Set<String> parseEnabledIds(String csv) {
        return ENABLED_IDS_CACHE.computeIfAbsent(csv, s -> new HashSet<>(java.util.Arrays.asList(s.split(","))));
    }

    private static final float WHEELIE_PIVOT_Z_FACTOR = 0.85f;

    private static final int BLINK_ON_TICKS = 10;
    private static final int BLINK_OFF_TICKS = 10;

    @FunctionalInterface
    private interface GeometryRenderer {
        void render(PoseStack.Pose pose, VertexConsumer consumer);
    }

    @FunctionalInterface
    private interface ModelDrawer {
        void draw(PoseStack poseStack, VertexConsumer consumer);
    }

    @FunctionalInterface
    private interface TextWrapper {
        VertexConsumer wrap(VertexConsumer out, PoseStack.Pose pose);
    }

    @FunctionalInterface
    private interface GeometrySubmitter {
        void submitCustomGeometry(PoseStack pose, RenderType type, GeometryRenderer renderer);

        default void submitWrappedText(PoseStack pose, net.minecraft.network.chat.FormattedText text, float x, float y, int light,
                                       TextWrapper wrapper) {
            submitText(pose, text, x, y, 0xFFFFFFFF, light);
        }

        default void submitModel(PoseStack pose, RenderType type, ModelDrawer drawer) {

            throw new UnsupportedOperationException();
        }

        default GeometrySubmitter late() {
            return this;
        }

        default void submitText(PoseStack pose, net.minecraft.network.chat.FormattedText text, float x, float y, int argb, int light) {
        }
    }

    private static GeometrySubmitter immediateSubmitter(MultiBufferSource bufferSource) {
        return new GeometrySubmitter() {
            @Override
            public void submitCustomGeometry(PoseStack pose, RenderType type, GeometryRenderer renderer) {
                renderer.render(pose.last(), bufferSource.getBuffer(type));
            }

            @Override
            public void submitModel(PoseStack pose, RenderType type, ModelDrawer drawer) {
                drawer.draw(pose, bufferSource.getBuffer(type));
            }

            @Override
            public void submitText(PoseStack pose, net.minecraft.network.chat.FormattedText text, float x, float y, int argb, int light) {
                net.minecraft.client.Minecraft.getInstance().font.drawInBatch(text, x, y, argb, false, pose.last().pose(), bufferSource,
                        false, 0, light);
            }

            @Override
            public void submitWrappedText(PoseStack pose, net.minecraft.network.chat.FormattedText text, float x, float y, int light,
                                          TextWrapper wrapper) {
                PoseStack.Pose last = pose.last();
                net.minecraft.client.Minecraft.getInstance().font.drawInBatch(text, x, y, 0xFFFFFFFF, false, PoseMatrices.identityMatrix(),
                        type -> wrapper.wrap(bufferSource.getBuffer(type), last), false, 0, light);
            }
        };
    }

    private final FigureModels figures;

    public CarEntityRenderer(net.minecraft.client.renderer.entity.EntityRenderDispatcher context) {
        super(context);
        this.figures = new FigureModels(context);
    }

    @Override
    public ResourceLocation getTextureLocation(CarEntity entity) {
        return CompiledCarModel.FALLBACK_TEXTURE;
    }

    private void populateCarState(CarEntity entity, CarRenderState state, float partialTick) {
        state.lightCoords = this.getPackedLightCoords(entity, partialTick);

        state.yRot = entity.getYRot();

        float[] railwayOverride = entity.mineturismo$computeRailwayRenderOverride(partialTick);
        state.railwayPositionOverride = railwayOverride != null;
        if (railwayOverride != null) {
            state.railwayPosDeltaX = railwayOverride[0];
            state.railwayPosDeltaY = railwayOverride[1];
            state.railwayPosDeltaZ = railwayOverride[2];
            state.yRot = railwayOverride[3];
        }
        state.tumblePitchDegrees = entity.getTumblePitchDegrees();
        state.tumbleRollDegrees = entity.getTumbleRollDegrees();
        state.tumblePivotHeight = (float) (entity.getScaledHitboxHeight() / 2.0);
        state.wheeliePitchDegrees = entity.getWheeliePitchDegrees();

        state.wheeliePivotZ = -entity.getFootprintHalfLength() * WHEELIE_PIVOT_Z_FACTOR;
        state.tiltRollDegrees = entity.getTiltRollDegrees();
        state.tiltPitchDegrees = entity.getTiltPitchDegrees();
        state.tiltHeightCm = entity.getTiltHeightCm();
        state.suspensionPitchDegrees = entity.getSuspensionPitchDegrees();
        state.suspensionRollDegrees = entity.getSuspensionRollDegrees();
        state.bikeLeanRollDegrees = entity.getBikeLeanRollDegrees();
        state.showFakeRider = entity.shouldRenderFakeTrafficRider();
        if (state.showFakeRider) {
            state.fakeRiderUseAlex = (entity.getUUID().hashCode() & 1) == 0;
            state.fakeRiderX = entity.getPassengerXOffset();

            state.fakeRiderY = entity.getFakeRiderYOffset();
            state.fakeRiderZ = entity.getPassengerZOffset();
            state.fakeRiderIsMotorcycle = entity.hasVehicleCategory(com.examplebett.entity.VehicleCategory.MOTORCYCLE);
            state.fakeRiderBikeStationary = entity.isBikeStationary();
        }
        state.useLodWheel = this.entityRenderDispatcher.distanceToSqr(entity) > LOD_WHEEL_DISTANCE_SQ;

        state.wheelRotationDegrees = state.useLodWheel ? 0f : entity.getWheelRotationDegrees(partialTick);
        state.rearWheelExtraSpinDegrees = state.useLodWheel ? 0f : entity.getRearWheelExtraSpinDegrees(partialTick);
        state.frontWheelSteerDegrees = entity.getFrontWheelSteerDegrees(partialTick);
        state.rotorRotationDegrees = entity.getRotorRotationDegrees(partialTick);
        state.compiledModel = entity.getCompiledModel();
        state.wheelCompiledModel = entity.getWheelCompiledModel();
        state.wheelCompiledModelMirrored = entity.getWheelCompiledModelMirrored();
        state.wheelAttachment = entity.getWheelAttachment();
        state.wheelFrontZOffset = entity.getWheelFrontZOffset();
        state.wheelRearZOffset = entity.getWheelRearZOffset();
        state.wheelTrackHalfWidthOffset = entity.getWheelTrackHalfWidthOffset();
        state.wheelAxleHeightOffset = entity.getWheelAxleHeightOffset();
        state.extraRearAxleEnabled = entity.isExtraRearAxleEnabled();
        state.extraRearAxleZOffset = entity.getExtraRearAxleZOffset();
        state.extraRearAxle2Enabled = entity.isExtraRearAxle2Enabled();
        state.extraRearAxle2ZOffset = entity.getExtraRearAxle2ZOffset();
        state.bodyHeightOffset = entity.getBodyHeightOffset();
        state.wheelCamberDegrees = entity.getWheelCamberDegrees();
        state.wheelScale = entity.getWheelScale();
        state.wheelWidthScale = entity.getWheelWidthScale();
        state.lipStretchScale = entity.getLipStretchScale();
        state.lipWidthScale = entity.getLipWidthScale();
        state.lipBandHeightCm = entity.getLipBandHeightCm();
        state.lipFrontExtendScale = entity.getLipFrontExtendScale();
        state.limoStretchCm = entity.getLimoStretchCm();
        state.limoZoneCm = entity.getLimoZoneCm();
        state.limoCenterOffsetCm = entity.getLimoCenterOffsetCm();
        state.widthStretchCm = entity.getWidthStretchCm();
        state.widthZoneCm = entity.getWidthZoneCm();
        state.widthCenterOffsetCm = entity.getWidthCenterOffsetCm();
        state.carScale = entity.getCarScale();
        state.color = entity.getColor();

        boolean isMotorcycleNoFrontPlate = entity.hasVehicleCategory(com.examplebett.entity.VehicleCategory.MOTORCYCLE);
        state.frontPlateTexture = isMotorcycleNoFrontPlate ? null : PlateTextures.texture(entity.getFrontPlate());
        state.frontPlateAspect = PlateTextures.aspect(entity.getFrontPlate());
        var frontOffset = entity.getFrontPlateOffset();
        state.frontPlateX = (float) frontOffset.x;
        state.frontPlateY = (float) frontOffset.y;
        state.frontPlateZ = (float) frontOffset.z;
        state.frontPlateScale = entity.getFrontPlateScale();
        state.frontPlateYaw = entity.getFrontPlateYaw();
        state.frontPlatePitch = entity.getFrontPlatePitch();
        state.frontPlateRoll = entity.getFrontPlateRoll();

        state.rearPlateTexture = PlateTextures.texture(entity.getRearPlate());
        state.rearPlateAspect = PlateTextures.aspect(entity.getRearPlate());
        var rearOffset = entity.getRearPlateOffset();
        state.rearPlateX = (float) rearOffset.x;
        state.rearPlateY = (float) rearOffset.y;
        state.rearPlateZ = (float) rearOffset.z;
        state.rearPlateScale = entity.getRearPlateScale();
        state.rearPlateYaw = entity.getRearPlateYaw();
        state.rearPlatePitch = entity.getRearPlatePitch();
        state.rearPlateRoll = entity.getRearPlateRoll();

        state.lightsOn = entity.areLightsOn();

        state.headlightHidden = entity.hasVehicleCategory(com.examplebett.entity.VehicleCategory.TRAILER);
        state.lightsBlinking = entity.isLightBlinkEnabled();
        state.age = entity.tickCount;
        var headlightOffset = entity.getHeadlightOffset();
        state.headlightX = (float) headlightOffset.x;
        state.headlightY = (float) headlightOffset.y;
        state.headlightZ = (float) headlightOffset.z;
        var taillightOffset = entity.getTaillightOffset();
        state.taillightX = (float) taillightOffset.x;
        state.taillightY = (float) taillightOffset.y;
        state.taillightZ = (float) taillightOffset.z;
        var headlightColor = com.examplebett.entity.HeadlightColors.byId(entity.resolveHeadlightColorId());
        state.headlightColorR = headlightColor.r();
        state.headlightColorG = headlightColor.g();
        state.headlightColorB = headlightColor.b();

        state.rearWingEnabledIds = entity.getRearWingEnabledIds();
        state.rearWingTransformsRaw = entity.getRearWingTransformsRaw();

        state.otherPartEnabledIds = entity.getOtherPartEnabledIds();
        state.otherPartTransformsRaw = entity.getOtherPartTransformsRaw();

        state.textPartsRaw = entity.getTextPartsRaw();
        state.underNeonRaw = entity.getUnderNeonRaw();
        state.brandLogosRaw = entity.getBrandLogosRaw();
        double brandLogoDistanceSq = this.entityRenderDispatcher.distanceToSqr(entity);
        state.brandLogoDetail = brandLogoDistanceSq > BRAND_LOGO_HIDE_DISTANCE_SQ ? 0
                : brandLogoDistanceSq > BRAND_LOGO_FLAT_DISTANCE_SQ ? 1 : 2;

        state.frontBrakeCaliperEnabledIds = entity.getFrontBrakeCaliperEnabledIds();
        state.frontBrakeCaliperTransformsRaw = entity.getFrontBrakeCaliperTransformsRaw();

        state.rearBrakeCaliperEnabledIds = entity.getRearBrakeCaliperEnabledIds();
        state.rearBrakeCaliperTransformsRaw = entity.getRearBrakeCaliperTransformsRaw();

        state.exhaustEnabledId = entity.getExhaustEnabledIds();
        if (state.exhaustEnabledId.isEmpty()) {
            state.exhaustTarget = null;
            entity.clientExhaustOutlets = null;
        } else {
            state.exhaustTarget = entity;
            state.exhaustTransform = entity.getExhaustTransform(state.exhaustEnabledId);
        }

        state.towSelf = null;
        state.towComputeFront = false;
        state.towComputeRear = false;
        state.towRopeEnd = null;
        long towNow = entity.level().getGameTime();
        int towLeaderId = entity.getTowLeaderEntityId();
        if (towLeaderId != -1 && entity.level().getEntity(towLeaderId) instanceof CarEntity towLeader) {
            towLeader.clientTowRearHookWantedTime = towNow;
            state.towSelf = entity;
            state.towComputeFront = true;
            float[] leaderRear = towLeader.clientTowRearHook;
            if (leaderRear != null) {
                net.minecraft.world.phys.Vec3 leaderPos = com.examplebett.compat.ModVec3.position(towLeader, partialTick);
                net.minecraft.world.phys.Vec3 selfPos = com.examplebett.compat.ModVec3.position(entity, partialTick);
                state.towRopeEnd = new float[]{
                        (float) (leaderPos.x - selfPos.x) + leaderRear[0],
                        (float) (leaderPos.y - selfPos.y) + leaderRear[1],
                        (float) (leaderPos.z - selfPos.z) + leaderRear[2]};
            }
        } else {
            entity.clientTowFrontHook = null;
        }
        if (towNow - entity.clientTowRearHookWantedTime <= 2) {
            state.towSelf = entity;
            state.towComputeRear = true;
        }

        state.policeLightEnabledIds = entity.getPoliceLightEnabledIds();
        state.policeLightTransformsRaw = entity.getPoliceLightTransformsRaw();
        state.policeLightsOn = entity.isPoliceLightsOn();

        state.taxiSignEnabledIds = entity.getTaxiSignEnabledIds();
        state.taxiSignTransformsRaw = entity.getTaxiSignTransformsRaw();
    }

    @Override
    public void render(CarEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int light) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, light);
        CarRenderState state = new CarRenderState();
        populateCarState(entity, state, partialTick);
        state.isInvisible = entity.isInvisible();
        state.lightCoords = light;
        renderCar(state, poseStack, immediateSubmitter(bufferSource));
    }

    private void renderCar(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector) {

        if (state.isInvisible) {
            return;
        }

        if (state.compiledModel == null) {
            return;
        }

        float[] exhaustOrigin = (state.exhaustTarget != null || state.towSelf != null) ? PoseMatrices.capture(poseStack) : null;

        poseStack.pushPose();
        if (state.railwayPositionOverride) {

            poseStack.translate(state.railwayPosDeltaX, state.railwayPosDeltaY, state.railwayPosDeltaZ);
        }
        poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(state.yRot));

        boolean tumbling = state.tumblePitchDegrees != 0f || state.tumbleRollDegrees != 0f;
        if (tumbling) {
            poseStack.translate(0.0, state.tumblePivotHeight, 0.0);
        }
        if (state.tumblePitchDegrees != 0f) {
            poseStack.mulPose(com.mojang.math.Vector3f.XP.rotationDegrees(state.tumblePitchDegrees));
        }
        if (state.tumbleRollDegrees != 0f) {
            poseStack.mulPose(com.mojang.math.Vector3f.ZP.rotationDegrees(state.tumbleRollDegrees));
        }
        if (tumbling) {
            poseStack.translate(0.0, -state.tumblePivotHeight, 0.0);
        }

        boolean wheelieActive = state.wheeliePitchDegrees != 0f;
        if (wheelieActive) {
            poseStack.translate(0.0, 0.0, state.wheeliePivotZ);
            poseStack.mulPose(com.mojang.math.Vector3f.XP.rotationDegrees(-state.wheeliePitchDegrees));
            poseStack.translate(0.0, 0.0, -state.wheeliePivotZ);
        }

        boolean tilting = state.tiltPitchDegrees != 0f || state.tiltRollDegrees != 0f;
        if (tilting) {
            poseStack.translate(0.0, state.tumblePivotHeight, 0.0);
        }
        if (state.tiltPitchDegrees != 0f) {
            poseStack.mulPose(com.mojang.math.Vector3f.XP.rotationDegrees(state.tiltPitchDegrees));
        }
        if (state.tiltRollDegrees != 0f) {
            poseStack.mulPose(com.mojang.math.Vector3f.ZP.rotationDegrees(state.tiltRollDegrees));
        }
        if (tilting) {
            poseStack.translate(0.0, -state.tumblePivotHeight, 0.0);
        }
        if (state.tiltHeightCm != 0f) {
            poseStack.translate(0.0, state.tiltHeightCm / 100.0, 0.0);
        }

        if (state.bikeLeanRollDegrees != 0f) {
            poseStack.translate(0.0, state.tumblePivotHeight, 0.0);
            poseStack.mulPose(com.mojang.math.Vector3f.ZP.rotationDegrees(state.bikeLeanRollDegrees));
            poseStack.translate(0.0, -state.tumblePivotHeight, 0.0);
        }

        if (state.carScale != 1f) {
            poseStack.scale(state.carScale, state.carScale, state.carScale);
        }

        poseStack.pushPose();
        poseStack.translate(0.0, state.bodyHeightOffset, 0.0);

        boolean suspensionSwaying = state.suspensionPitchDegrees != 0f || state.suspensionRollDegrees != 0f;
        if (suspensionSwaying) {
            poseStack.translate(0.0, state.tumblePivotHeight, 0.0);
        }
        if (state.suspensionPitchDegrees != 0f) {
            poseStack.mulPose(com.mojang.math.Vector3f.XP.rotationDegrees(state.suspensionPitchDegrees));
        }
        if (state.suspensionRollDegrees != 0f) {
            poseStack.mulPose(com.mojang.math.Vector3f.ZP.rotationDegrees(state.suspensionRollDegrees));
        }
        if (suspensionSwaying) {
            poseStack.translate(0.0, -state.tumblePivotHeight, 0.0);
        }

        List<CompiledCarModel.TextureGroup> reflectiveGroups = new ArrayList<>();
        float[] embeddedExtraAxleZ = embeddedExtraAxleZ(state);
        for (CompiledCarModel.TextureGroup group : state.compiledModel.groups) {

            ResourceLocation texture = group.texture;
            boolean usesPaintMask = group.tintable && PaintTintTexture.hasPaint(group.texture);
            if (usesPaintMask) {
                texture = PaintTintTexture.resolve(group.texture, state.color);
            }
            texture = metalPaintTexture(group, texture, usesPaintMask, state.color);

            final int tint = (group.tintable && !usesPaintMask) ? state.color : 0xFFFFFF;

            RenderType renderType = groupRenderType(group, texture);

            boolean spinning = group.wheelGroup >= 0 && group.wheelGroup < state.compiledModel.wheelPivots.size();
            boolean rotorSpinning = group.rotorGroup >= 0 && group.rotorGroup < state.compiledModel.rotorPivots.size();

            boolean isBodyGroup = group.wheelGroup < 0 && group.rotorGroup < 0;
            BodyDeform deform = isBodyGroup ? BodyDeform.from(state) : BodyDeform.NONE;

            boolean reflective = com.examplebett.client.AiObjectModClient.isFakeReflectionEnabled()
                    && isBodyGroup && !group.translucent;

            poseStack.pushPose();
            applyGroupTransform(poseStack, state, group, spinning, rotorSpinning, 0f);
            collector.submitCustomGeometry(poseStack, renderType, (pose, vertexConsumer) ->
                    renderGroup(group.vertexData, pose, vertexConsumer, state.lightCoords, tint, deform, usesTriangles(group)));
            poseStack.popPose();

            if (spinning && embeddedExtraAxleZ.length > 0
                    && !state.compiledModel.wheelPivots.get(group.wheelGroup).front) {
                float pivotZ = state.compiledModel.wheelPivots.get(group.wheelGroup).centerZ;
                for (float axleZ : embeddedExtraAxleZ) {
                    poseStack.pushPose();
                    applyGroupTransform(poseStack, state, group, true, false, axleZ - pivotZ);
                    collector.submitCustomGeometry(poseStack, renderType, (pose, vertexConsumer) ->
                            renderGroup(group.vertexData, pose, vertexConsumer, state.lightCoords, tint, BodyDeform.NONE, usesTriangles(group)));
                    poseStack.popPose();
                }
            }
            if (reflective) {
                reflectiveGroups.add(group);
            }
        }

        for (CompiledCarModel.TextureGroup group : reflectiveGroups) {
            boolean spinning = group.wheelGroup >= 0 && group.wheelGroup < state.compiledModel.wheelPivots.size();
            boolean rotorSpinning = group.rotorGroup >= 0 && group.rotorGroup < state.compiledModel.rotorPivots.size();
            boolean isBodyGroup = group.wheelGroup < 0 && group.rotorGroup < 0;
            BodyDeform deform = isBodyGroup ? BodyDeform.from(state) : BodyDeform.NONE;
            RenderType reflectionType = RenderType.entityNoOutline(CompiledCarModel.FALLBACK_TEXTURE);
            poseStack.pushPose();
            applyGroupTransform(poseStack, state, group, spinning, rotorSpinning, 0f);
            collector.submitCustomGeometry(poseStack, reflectionType, (pose, vertexConsumer) ->
                    renderReflectionOverlay(group.vertexData, pose, vertexConsumer, state.lightCoords, deform));
            poseStack.popPose();
        }

        if (state.frontPlateTexture != null) {
            submitPlate(poseStack, collector, state.lightCoords, state.frontPlateTexture,
                    state.frontPlateX + widthShiftFor(state, state.frontPlateX), state.frontPlateY, state.frontPlateZ + limoShiftFor(state, state.frontPlateZ), state.frontPlateScale,
                    state.frontPlateYaw, state.frontPlatePitch, state.frontPlateRoll, state.frontPlateAspect);
        }
        if (state.rearPlateTexture != null) {

            submitPlate(poseStack, collector, state.lightCoords, state.rearPlateTexture,
                    state.rearPlateX + widthShiftFor(state, state.rearPlateX), state.rearPlateY, state.rearPlateZ + limoShiftFor(state, state.rearPlateZ), state.rearPlateScale,
                    state.rearPlateYaw + 180f, state.rearPlatePitch, state.rearPlateRoll, state.rearPlateAspect);
        }

        boolean blinkPhaseOn = !state.lightsBlinking
                || (state.age % (BLINK_ON_TICKS + BLINK_OFF_TICKS)) < BLINK_ON_TICKS;
        if (state.lightsOn && blinkPhaseOn) {

            for (int side = -1; side <= 1; side += 2) {
                float headlightX = state.headlightX * side;
                float taillightX = state.taillightX * side;
                if (!state.headlightHidden) {
                    submitGlow(poseStack, collector, HEADLIGHT_GLOW_TEXTURE,
                            headlightX + widthShiftFor(state, headlightX), state.headlightY, state.headlightZ + limoShiftFor(state, state.headlightZ), HEADLIGHT_GLOW_SIZE, 0f,
                            state.headlightColorR, state.headlightColorG, state.headlightColorB);
                }
                submitGlow(poseStack, collector, TAILLIGHT_GLOW_TEXTURE,
                        taillightX + widthShiftFor(state, taillightX), state.taillightY, state.taillightZ + limoShiftFor(state, state.taillightZ), TAILLIGHT_GLOW_SIZE, 180f,
                        1f, 1f, 1f);
            }
        }

        renderRearWing(state, poseStack, collector);
        renderPoliceLight(state, poseStack, collector);
        renderTaxiSign(state, poseStack, collector);
        renderOtherPart(state, poseStack, collector);
        renderTextParts(state, poseStack, collector);
        renderBrandLogos(state, poseStack, collector);
        if (exhaustOrigin != null && state.exhaustTarget != null) {
            computeExhaustOutlets(state, poseStack, exhaustOrigin);
        }
        if (exhaustOrigin != null && state.towSelf != null) {
            computeTowHooks(state, poseStack, exhaustOrigin);
        }

        poseStack.popPose();

        renderUnderNeon(state, poseStack, collector);
        renderWheelAttachment(state, poseStack, collector);
        renderBrakeCaliper(state, poseStack, collector, true, state.frontBrakeCaliperEnabledIds, state.frontBrakeCaliperTransformsRaw);
        renderBrakeCaliper(state, poseStack, collector, false, state.rearBrakeCaliperEnabledIds, state.rearBrakeCaliperTransformsRaw);

        renderFakeRider(state, poseStack, collector);

        poseStack.popPose();

        renderTowRope(state, poseStack, collector);
    }

    private static final ResourceLocation TOW_ROPE_TEXTURE = com.examplebett.AiObjectMod.id("textures/entity/tow_rope.png");

    private static final float TOW_ROPE_HALF_THICKNESS = 0.045f;
    private static final int TOW_ROPE_SEGMENTS = 24;

    private static void computeTowHooks(CarRenderState state, PoseStack poseStack, float[] origin) {
        float[] relative = PoseMatrices.relative(origin, PoseMatrices.capture(poseStack));
        if (state.towComputeFront) {
            state.towSelf.clientTowFrontHook = PoseMatrices.transformPosition(relative, 0f, state.frontPlateY,
                    state.frontPlateZ + limoShiftFor(state, state.frontPlateZ));
        }
        if (state.towComputeRear) {
            state.towSelf.clientTowRearHook = PoseMatrices.transformPosition(relative, 0f, state.rearPlateY,
                    state.rearPlateZ + limoShiftFor(state, state.rearPlateZ));
        }
    }

    private void renderTowRope(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector) {
        if (!state.towComputeFront || state.towRopeEnd == null || state.towSelf == null || state.towSelf.clientTowFrontHook == null) {
            return;
        }
        float[] a = state.towSelf.clientTowFrontHook;
        float[] b = state.towRopeEnd;
        float dx = b[0] - a[0];
        float dy = b[1] - a[1];
        float dz = b[2] - a[2];
        float straight = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (straight < 1.0E-3f) {
            return;
        }

        float ropeLength = (float) CarEntity.TOW_ROPE_LENGTH;
        float slack = Math.max(0f, ropeLength - straight);
        float sag = 0.08f + (float) Math.sqrt(slack * (ropeLength + straight)) * 0.35f;
        int light = state.lightCoords;
        collector.submitCustomGeometry(poseStack, RenderType.entityCutoutNoCull(TOW_ROPE_TEXTURE), (pose, consumer) -> {
            float[] prev = null;
            for (int i = 0; i <= TOW_ROPE_SEGMENTS; i++) {
                float t = (float) i / TOW_ROPE_SEGMENTS;
                float[] p = {a[0] + dx * t, a[1] + dy * t - sag * 4f * t * (1f - t), a[2] + dz * t};
                if (prev != null) {

                    float u0 = ((i - 1) % 2) * 0.5f;
                    addRopeSegment(consumer, pose, prev, p, u0, u0 + 0.5f, light);
                }
                prev = p;
            }
        });
    }

    private void addRopeSegment(VertexConsumer consumer, PoseStack.Pose pose, float[] p0, float[] p1, float u0, float u1, int light) {
        float dx = p1[0] - p0[0];
        float dy = p1[1] - p0[1];
        float dz = p1[2] - p0[2];
        float dirLen = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (dirLen < 1.0E-4f) {
            return;
        }
        dx /= dirLen;
        dy /= dirLen;
        dz /= dirLen;

        float sx = -dz;
        float sy = 0f;
        float sz = dx;
        float sideLen = (float) Math.sqrt(sx * sx + sz * sz);
        if (sideLen < 1.0E-3f) {
            sx = 1f;
            sz = 0f;
            sideLen = 1f;
        }
        sx = sx / sideLen * TOW_ROPE_HALF_THICKNESS;
        sz = sz / sideLen * TOW_ROPE_HALF_THICKNESS;

        float ux = sy * dz - sz * dy;
        float uy = sz * dx - sx * dz;
        float uz = sx * dy - sy * dx;
        float upLen = (float) Math.sqrt(ux * ux + uy * uy + uz * uz);
        if (upLen < 1.0E-8f) {
            return;
        }
        ux = ux / upLen * TOW_ROPE_HALF_THICKNESS;
        uy = uy / upLen * TOW_ROPE_HALF_THICKNESS;
        uz = uz / upLen * TOW_ROPE_HALF_THICKNESS;
        float[][] corners = {
                {sx + ux, sy + uy, sz + uz}, {-sx + ux, -sy + uy, -sz + uz},
                {-sx - ux, -sy - uy, -sz - uz}, {sx - ux, sy - uy, sz - uz}};
        for (int f = 0; f < 4; f++) {
            float[] c0 = corners[f];
            float[] c1 = corners[(f + 1) % 4];
            float v0 = f * 0.25f;
            float v1 = v0 + 0.25f;
            addPlateVertex(consumer, pose, p0[0] + c0[0], p0[1] + c0[1], p0[2] + c0[2], u0, v0, light);
            addPlateVertex(consumer, pose, p1[0] + c0[0], p1[1] + c0[1], p1[2] + c0[2], u1, v0, light);
            addPlateVertex(consumer, pose, p1[0] + c1[0], p1[1] + c1[1], p1[2] + c1[2], u1, v1, light);
            addPlateVertex(consumer, pose, p0[0] + c1[0], p0[1] + c1[1], p0[2] + c1[2], u0, v1, light);
        }
    }

    private static final float FAKE_RIDER_YAW_DEGREES = 180f;

    private static final float FAKE_RIDER_Y_ADJUST = 0.85f;

    private void renderFakeRider(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector) {
        if (!state.showFakeRider) {
            return;
        }
        RenderType renderType = RenderType.entityCutoutNoCull(FigureModels.fakeRiderTexture(state.fakeRiderUseAlex));
        poseStack.pushPose();
        poseStack.translate(state.fakeRiderX, state.fakeRiderY + FAKE_RIDER_Y_ADJUST, state.fakeRiderZ);
        poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(FAKE_RIDER_YAW_DEGREES));
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        collector.submitModel(poseStack, renderType, (modelPoseStack, vertexConsumer) ->
                this.figures.renderFakeRider(state.fakeRiderUseAlex, state.fakeRiderIsMotorcycle, state.fakeRiderBikeStationary,
                        state.bikeLeanRollDegrees, modelPoseStack, vertexConsumer, state.lightCoords));
        poseStack.popPose();
    }

    private static final float SEATED_PLAYER_SCALE = 0.9375f;

    private static final float SEATED_PLAYER_ORIGIN_HEIGHT = (12f + 2f) / 16f * SEATED_PLAYER_SCALE;

    private void renderSeatedPlayer(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector, boolean alex) {
        ResourceLocation texture = alex ? FigureModels.ALEX_SLIM_TEXTURE : FigureModels.STEVE_TEXTURE;
        poseStack.pushPose();
        poseStack.translate(0f, SEATED_PLAYER_ORIGIN_HEIGHT, 0f);
        poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(FAKE_RIDER_YAW_DEGREES));
        poseStack.scale(-SEATED_PLAYER_SCALE, -SEATED_PLAYER_SCALE, SEATED_PLAYER_SCALE);
        collector.submitModel(poseStack, RenderType.entityCutoutNoCull(texture), (modelPoseStack, vertexConsumer) ->
                this.figures.renderSeatedPlayer(alex, modelPoseStack, vertexConsumer, state.lightCoords));
        poseStack.popPose();
    }

    private static final float SITTING_WOLF_ORIGIN_HEIGHT = 1.501f;

    private void renderSittingWolf(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector) {
        poseStack.pushPose();
        poseStack.translate(0f, SITTING_WOLF_ORIGIN_HEIGHT, 0f);
        poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(FAKE_RIDER_YAW_DEGREES));
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        collector.submitModel(poseStack, RenderType.entityCutoutNoCull(FigureModels.WOLF_TEXTURE), (modelPoseStack, vertexConsumer) ->
                this.figures.renderSittingWolf(false, state.age, modelPoseStack, vertexConsumer, state.lightCoords));
        collector.submitModel(poseStack, RenderType.entityCutoutNoCull(FigureModels.WOLF_COLLAR_TEXTURE), (modelPoseStack, vertexConsumer) ->
                this.figures.renderSittingWolf(true, state.age, modelPoseStack, vertexConsumer, state.lightCoords));
        poseStack.popPose();
    }

    private record WheelPositions(int wheelCount, float axleHeight, boolean[] isFront, float[] camberSign) {
    }

    private record PartRef(String id, String modelId) {
    }

    private static boolean usesEmbeddedWheelPivots(CarRenderState state) {
        return state.wheelAttachment == null && state.compiledModel != null
                && state.compiledModel.wheelPivots.size() == 4;
    }

    private WheelPositions computeWheelPositions(CarRenderState state) {
        com.examplebett.render.WheelAttachment attachment = state.wheelAttachment;
        if (attachment == null) {
            return computeEmbeddedWheelPositions(state);
        }
        float frontZ = attachment.frontZ() + state.wheelFrontZOffset;
        float rearZ = attachment.rearZ() + state.wheelRearZOffset;
        float trackHalfWidth = attachment.trackHalfWidth() + state.wheelTrackHalfWidthOffset;
        float axleHeight = attachment.axleHeight() + state.wheelAxleHeightOffset;

        boolean hasRearAxle2 = attachment.hasRearAxle2() || state.extraRearAxleEnabled;
        float rearZ2Base = attachment.hasRearAxle2()
                ? attachment.rearZ2()
                : attachment.rearZ() - CarEntity.DEFAULT_EXTRA_REAR_AXLE_SPACING;
        float rearZ2 = hasRearAxle2 ? rearZ2Base + state.wheelRearZOffset + state.extraRearAxleZOffset : 0f;

        boolean hasRearAxle3 = state.extraRearAxle2Enabled;
        float rearZ3 = hasRearAxle3
                ? rearZ2Base - CarEntity.DEFAULT_EXTRA_REAR_AXLE_SPACING + state.wheelRearZOffset + state.extraRearAxle2ZOffset
                : 0f;

        frontZ += limoShiftFor(state, frontZ);
        rearZ += limoShiftFor(state, rearZ);
        if (hasRearAxle2) {
            rearZ2 += limoShiftFor(state, rearZ2);
        }
        if (hasRearAxle3) {
            rearZ3 += limoShiftFor(state, rearZ3);
        }

        float trackRightX = trackHalfWidth + widthShiftFor(state, trackHalfWidth);
        float trackLeftX = -trackHalfWidth + widthShiftFor(state, -trackHalfWidth);

        boolean singleWheelPerAxle = attachment.singleWheelPerAxle();

        int wheelCount = singleWheelPerAxle ? 2 : (hasRearAxle2 && hasRearAxle3 ? 8 : (hasRearAxle2 || hasRearAxle3 ? 6 : 4));
        float[] offsetZ = WHEEL_OFFSET_Z_BUF;
        float[] offsetX = WHEEL_OFFSET_X_BUF;
        if (singleWheelPerAxle) {
            offsetZ[0] = frontZ; offsetZ[1] = rearZ;
            offsetX[0] = 0f; offsetX[1] = 0f;
        } else {
            offsetZ[0] = frontZ; offsetZ[1] = frontZ; offsetZ[2] = rearZ; offsetZ[3] = rearZ;
            offsetX[0] = trackRightX; offsetX[1] = trackLeftX; offsetX[2] = trackRightX; offsetX[3] = trackLeftX;
            if (hasRearAxle2) {
                offsetZ[4] = rearZ2; offsetZ[5] = rearZ2;
                offsetX[4] = trackRightX; offsetX[5] = trackLeftX;
                if (hasRearAxle3) {
                    offsetZ[6] = rearZ3; offsetZ[7] = rearZ3;
                    offsetX[6] = trackRightX; offsetX[7] = trackLeftX;
                }
            } else if (hasRearAxle3) {
                offsetZ[4] = rearZ3; offsetZ[5] = rearZ3;
                offsetX[4] = trackRightX; offsetX[5] = trackLeftX;
            }
        }
        boolean[] isFront = singleWheelPerAxle ? WHEEL_IS_FRONT_2
                : (wheelCount == 8 ? WHEEL_IS_FRONT_8 : (wheelCount == 6 ? WHEEL_IS_FRONT_6 : WHEEL_IS_FRONT_4));

        float[] camberSign = singleWheelPerAxle ? WHEEL_CAMBER_SIGN_2
                : (wheelCount == 8 ? WHEEL_CAMBER_SIGN_8 : (wheelCount == 6 ? WHEEL_CAMBER_SIGN_6 : WHEEL_CAMBER_SIGN_4));
        return new WheelPositions(wheelCount, axleHeight, isFront, camberSign);
    }

    private static final float[] NO_EXTRA_AXLES = new float[0];

    private static float[] embeddedExtraAxleZ(CarRenderState state) {
        if ((!state.extraRearAxleEnabled && !state.extraRearAxle2Enabled) || !usesEmbeddedWheelPivots(state)) {
            return NO_EXTRA_AXLES;
        }
        float rearZ = 0f;
        int rearCount = 0;
        for (ObjModel.WheelPivot pivot : state.compiledModel.wheelPivots) {
            if (!pivot.front) {
                rearZ += pivot.centerZ;
                rearCount++;
            }
        }
        if (rearCount == 0) {
            return NO_EXTRA_AXLES;
        }
        rearZ /= rearCount;
        float rearZ2Base = rearZ - CarEntity.DEFAULT_EXTRA_REAR_AXLE_SPACING;
        float rearZ2 = rearZ2Base + state.extraRearAxleZOffset;
        float rearZ3 = rearZ2Base - CarEntity.DEFAULT_EXTRA_REAR_AXLE_SPACING + state.extraRearAxle2ZOffset;
        if (state.extraRearAxleEnabled && state.extraRearAxle2Enabled) {
            return new float[] { rearZ2, rearZ3 };
        }
        return new float[] { state.extraRearAxleEnabled ? rearZ2 : rearZ3 };
    }

    private WheelPositions computeEmbeddedWheelPositions(CarRenderState state) {
        if (!usesEmbeddedWheelPivots(state)) {
            return new WheelPositions(0, 0f, WHEEL_IS_FRONT_4, WHEEL_CAMBER_SIGN_4);
        }
        List<ObjModel.WheelPivot> pivots = state.compiledModel.wheelPivots;
        ObjModel.WheelPivot[] front = new ObjModel.WheelPivot[2];
        ObjModel.WheelPivot[] rear = new ObjModel.WheelPivot[2];
        int frontCount = 0;
        int rearCount = 0;
        float sumY = 0f;
        for (ObjModel.WheelPivot pivot : pivots) {
            if (pivot.front && frontCount < 2) {
                front[frontCount++] = pivot;
            } else if (!pivot.front && rearCount < 2) {
                rear[rearCount++] = pivot;
            }
            sumY += pivot.centerY;
        }
        if (frontCount != 2 || rearCount != 2) {
            return new WheelPositions(0, 0f, WHEEL_IS_FRONT_4, WHEEL_CAMBER_SIGN_4);
        }

        if (front[0].centerX < front[1].centerX) {
            ObjModel.WheelPivot t = front[0]; front[0] = front[1]; front[1] = t;
        }
        if (rear[0].centerX < rear[1].centerX) {
            ObjModel.WheelPivot t = rear[0]; rear[0] = rear[1]; rear[1] = t;
        }
        float[] extraAxleZ = embeddedExtraAxleZ(state);
        int wheelCount = 4 + extraAxleZ.length * 2;
        float[] offsetZ = WHEEL_OFFSET_Z_BUF;
        float[] offsetX = WHEEL_OFFSET_X_BUF;
        offsetZ[0] = embeddedWheelZ(state, front[0].centerZ, true); offsetZ[1] = embeddedWheelZ(state, front[1].centerZ, true);
        offsetZ[2] = embeddedWheelZ(state, rear[0].centerZ, false); offsetZ[3] = embeddedWheelZ(state, rear[1].centerZ, false);
        offsetX[0] = embeddedWheelX(state, front[0]); offsetX[1] = embeddedWheelX(state, front[1]);
        offsetX[2] = embeddedWheelX(state, rear[0]); offsetX[3] = embeddedWheelX(state, rear[1]);
        for (int a = 0; a < extraAxleZ.length; a++) {
            int i = 4 + a * 2;
            float axleZ = embeddedWheelZ(state, extraAxleZ[a], false);
            offsetZ[i] = axleZ; offsetZ[i + 1] = axleZ;
            offsetX[i] = offsetX[2]; offsetX[i + 1] = offsetX[3];
        }
        boolean[] isFront = wheelCount == 8 ? WHEEL_IS_FRONT_8 : (wheelCount == 6 ? WHEEL_IS_FRONT_6 : WHEEL_IS_FRONT_4);
        float[] camberSign = wheelCount == 8 ? WHEEL_CAMBER_SIGN_8 : (wheelCount == 6 ? WHEEL_CAMBER_SIGN_6 : WHEEL_CAMBER_SIGN_4);
        return new WheelPositions(wheelCount, sumY / pivots.size() + state.wheelAxleHeightOffset, isFront, camberSign);
    }

    private void renderBrakeCaliper(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector, boolean frontOnly,
                                     String enabledIdsCsv, String transformsRaw) {

        if ((state.wheelAttachment == null && !usesEmbeddedWheelPivots(state)) || enabledIdsCsv == null || enabledIdsCsv.isEmpty()) {
            return;
        }
        Set<String> enabledIds = parseEnabledIds(enabledIdsCsv);
        net.minecraft.server.packs.resources.ResourceManager resourceManager =
                net.minecraft.client.Minecraft.getInstance().getResourceManager();
        List<PartRef> parts = new ArrayList<>();
        if (frontOnly) {
            for (com.examplebett.render.FrontBrakeCaliperParts.Option option : com.examplebett.render.FrontBrakeCaliperParts.list(resourceManager)) {
                parts.add(new PartRef(option.id(), option.modelId()));
            }
        } else {
            for (com.examplebett.render.RearBrakeCaliperParts.Option option : com.examplebett.render.RearBrakeCaliperParts.list(resourceManager)) {
                parts.add(new PartRef(option.id(), option.modelId()));
            }
        }
        if (parts.isEmpty()) {
            return;
        }
        WheelPositions wp = computeWheelPositions(state);
        float[] offsetX = WHEEL_OFFSET_X_BUF;
        float[] offsetZ = WHEEL_OFFSET_Z_BUF;
        for (PartRef part : parts) {
            if (!enabledIds.contains(part.id())) {
                continue;
            }
            BrakeCaliperModel.Pair pair = BrakeCaliperModel.get(resourceManager, part.modelId());
            if (pair == null) {
                continue;
            }
            com.examplebett.render.PartTransforms.Transform6 t =
                    com.examplebett.render.PartTransforms.resolve6(transformsRaw, part.id(),
                            com.examplebett.entity.CarEntity.defaultBrakeCaliperTransform(part.id()));
            float localX = t.x();
            float localY = t.y();
            float localZ = t.z();
            float localAngle = t.angle();
            float localAngleY = t.angleY();
            float localScale = t.scale();
            for (int i = 0; i < wp.wheelCount(); i++) {
                if (wp.isFront()[i] != frontOnly) {
                    continue;
                }
                poseStack.pushPose();
                poseStack.translate(offsetX[i], wp.axleHeight(), offsetZ[i]);
                if (state.wheelCamberDegrees != 0f) {
                    poseStack.mulPose(com.mojang.math.Vector3f.ZP.rotationDegrees(wp.camberSign()[i] * state.wheelCamberDegrees));
                }
                if (frontOnly && state.frontWheelSteerDegrees != 0f) {

                    poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(state.frontWheelSteerDegrees));
                }
                boolean mirrored = offsetX[i] < 0;
                CompiledCarModel activeModel = mirrored ? pair.mirrored() : pair.normal();

                poseStack.translate(mirrored ? -localX : localX, localY, localZ);
                if (localAngleY != 0f) {
                    poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(localAngleY));
                }
                if (localAngle != 0f) {
                    poseStack.mulPose(com.mojang.math.Vector3f.XP.rotationDegrees(localAngle));
                }
                if (localScale != 1f) {
                    poseStack.scale(localScale, localScale, localScale);
                }
                for (CompiledCarModel.TextureGroup group : activeModel.groups) {
                    ResourceLocation texture = group.texture;
                    final int tint = group.tintable ? state.color : 0xFFFFFF;
                    RenderType renderType = groupRenderType(group, texture);
                    collector.submitCustomGeometry(poseStack, renderType, (pose, vertexConsumer) ->
                            renderGroup(group.vertexData, pose, vertexConsumer, state.lightCoords, tint, BodyDeform.NONE, usesTriangles(group)));
                }
                poseStack.popPose();
            }
        }
    }

    private void renderWheelAttachment(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector) {
        if (state.wheelCompiledModel == null || state.wheelAttachment == null) {
            return;
        }

        CompiledCarModel wheelModel = state.wheelCompiledModel;

        CompiledCarModel wheelModelMirrored =
                state.wheelCompiledModelMirrored != null ? state.wheelCompiledModelMirrored : wheelModel;
        if (state.useLodWheel) {
            net.minecraft.server.packs.resources.ResourceManager resourceManager =
                    net.minecraft.client.Minecraft.getInstance().getResourceManager();
            CompiledCarModel lodModel = LodWheelModel.get(resourceManager);
            if (lodModel != null) {
                wheelModel = lodModel;
                CompiledCarModel lodModelMirrored = LodWheelModel.getMirrored(resourceManager);
                wheelModelMirrored = lodModelMirrored != null ? lodModelMirrored : lodModel;
            }
        }
        WheelPositions wp = computeWheelPositions(state);
        int wheelCount = wp.wheelCount();
        float axleHeight = wp.axleHeight();
        float[] offsetZ = WHEEL_OFFSET_Z_BUF;
        float[] offsetX = WHEEL_OFFSET_X_BUF;
        boolean[] isFront = wp.isFront();
        float[] camberSign = wp.camberSign();

        for (int i = 0; i < wheelCount; i++) {
            poseStack.pushPose();
            poseStack.translate(offsetX[i], axleHeight, offsetZ[i]);

            if (state.wheelCamberDegrees != 0f) {
                poseStack.mulPose(com.mojang.math.Vector3f.ZP.rotationDegrees(camberSign[i] * state.wheelCamberDegrees));
            }
            if (isFront[i]) {
                poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(state.frontWheelSteerDegrees));
            }

            float rollDegrees = state.wheelRotationDegrees + (isFront[i] ? 0f : state.rearWheelExtraSpinDegrees);
            poseStack.mulPose(com.mojang.math.Vector3f.XP.rotationDegrees(rollDegrees));

            boolean mirrored = offsetX[i] < 0;
            CompiledCarModel activeWheelModel = mirrored ? wheelModelMirrored : wheelModel;

            float scaleX = state.wheelScale * state.wheelWidthScale;
            if (scaleX != 1f || state.wheelScale != 1f) {
                poseStack.scale(scaleX, state.wheelScale, state.wheelScale);
            }
            for (CompiledCarModel.TextureGroup group : activeWheelModel.groups) {
                ResourceLocation texture = group.texture;
                boolean usesPaintMask = group.tintable && PaintTintTexture.hasPaint(group.texture);
                if (usesPaintMask) {
                    texture = PaintTintTexture.resolve(group.texture, state.color);
                }
                texture = metalPaintTexture(group, texture, usesPaintMask, state.color);
                final int tint = (group.tintable && !usesPaintMask) ? state.color : 0xFFFFFF;
                RenderType renderType = groupRenderType(group, texture);
                collector.submitCustomGeometry(poseStack, renderType, (pose, vertexConsumer) ->
                        renderGroup(group.vertexData, pose, vertexConsumer, state.lightCoords, tint, BodyDeform.NONE, usesTriangles(group)));
            }
            poseStack.popPose();
        }
    }

    private void renderRearWing(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector) {
        if (state.rearWingEnabledIds == null || state.rearWingEnabledIds.isEmpty()) {
            return;
        }
        Set<String> enabledIds = parseEnabledIds(state.rearWingEnabledIds);
        net.minecraft.server.packs.resources.ResourceManager resourceManager =
                net.minecraft.client.Minecraft.getInstance().getResourceManager();
        com.examplebett.render.PartTransforms.Transform12 defaults = new com.examplebett.render.PartTransforms.Transform12(
                com.examplebett.entity.CarEntity.DEFAULT_REAR_WING_X, com.examplebett.entity.CarEntity.DEFAULT_REAR_WING_Y,
                com.examplebett.entity.CarEntity.DEFAULT_REAR_WING_Z, 0f, 0f, com.examplebett.entity.CarEntity.DEFAULT_REAR_WING_SCALE,
                1f, 1f, 1f, 1f, 1f, 1f);
        for (com.examplebett.render.RearWingParts.Option option : com.examplebett.render.RearWingParts.list(resourceManager)) {
            if (!enabledIds.contains(option.id())) {
                continue;
            }
            CompiledCarModel wingModel = RearWingModel.get(resourceManager, option.modelId());
            if (wingModel == null) {
                continue;
            }
            com.examplebett.render.PartTransforms.Transform12 t =
                    com.examplebett.render.PartTransforms.resolve12(state.rearWingTransformsRaw, option.id(), defaults);
            poseStack.pushPose();
            poseStack.translate(t.x() + widthShiftFor(state, t.x()), t.y(), t.z() + limoShiftFor(state, t.z()));
            if (t.angleY() != 0f) {
                poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(t.angleY()));
            }
            if (t.angle() != 0f) {
                poseStack.mulPose(com.mojang.math.Vector3f.XP.rotationDegrees(t.angle()));
            }

            float stretchScaleX = (t.stretchXPos() + t.stretchXNeg()) / 2f;
            float stretchScaleY = (t.stretchYPos() + t.stretchYNeg()) / 2f;
            float stretchScaleZ = (t.stretchZPos() + t.stretchZNeg()) / 2f;
            float offsetX = t.scale() * OTHER_PART_STRETCH_HALF_EXTENT * (t.stretchXPos() - t.stretchXNeg()) / 2f;
            float offsetY = t.scale() * OTHER_PART_STRETCH_HALF_EXTENT * (t.stretchYPos() - t.stretchYNeg()) / 2f;
            float offsetZ = t.scale() * OTHER_PART_STRETCH_HALF_EXTENT * (t.stretchZPos() - t.stretchZNeg()) / 2f;
            if (offsetX != 0f || offsetY != 0f || offsetZ != 0f) {
                poseStack.translate(offsetX, offsetY, offsetZ);
            }
            if (t.scale() != 1f || stretchScaleX != 1f || stretchScaleY != 1f || stretchScaleZ != 1f) {
                poseStack.scale(t.scale() * stretchScaleX, t.scale() * stretchScaleY, t.scale() * stretchScaleZ);
            }
            for (CompiledCarModel.TextureGroup group : wingModel.groups) {
                ResourceLocation texture = group.texture;
                final int tint = group.tintable ? state.color : 0xFFFFFF;
                RenderType renderType = groupRenderType(group, texture);
                collector.submitCustomGeometry(poseStack, renderType, (pose, vertexConsumer) ->
                        renderGroup(group.vertexData, pose, vertexConsumer, state.lightCoords, tint, BodyDeform.NONE, usesTriangles(group)));
            }
            poseStack.popPose();
        }
    }

    private static final float OTHER_PART_STRETCH_HALF_EXTENT = 0.3f;

    private void renderOtherPart(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector) {
        if (state.otherPartEnabledIds == null || state.otherPartEnabledIds.isEmpty()) {
            return;
        }
        Set<String> enabledIds = parseEnabledIds(state.otherPartEnabledIds);
        net.minecraft.server.packs.resources.ResourceManager resourceManager =
                net.minecraft.client.Minecraft.getInstance().getResourceManager();
        for (com.examplebett.render.OtherParts.Option option : com.examplebett.render.OtherParts.list(resourceManager)) {
            if (!enabledIds.contains(option.id())) {
                continue;
            }

            boolean spareTire = com.examplebett.render.OtherParts.SPARE_TIRE_ID.equals(option.id());

            boolean seatedPlayer = com.examplebett.render.OtherParts.isSeatedCreature(option.id());
            CompiledCarModel partModel = seatedPlayer
                    ? null
                    : spareTire
                            ? state.wheelCompiledModel
                            : OtherPartModel.get(resourceManager, option.modelId());
            if (partModel == null && !seatedPlayer) {
                continue;
            }
            com.examplebett.render.PartTransforms.Transform12 t =
                    com.examplebett.render.PartTransforms.resolve12(state.otherPartTransformsRaw, option.id(),
                            com.examplebett.entity.CarEntity.defaultOtherPartTransform(option.id()));
            poseStack.pushPose();
            poseStack.translate(t.x() + widthShiftFor(state, t.x()), t.y(), t.z() + limoShiftFor(state, t.z()));
            if (t.angleY() != 0f) {
                poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(t.angleY()));
            }
            if (t.angle() != 0f) {
                poseStack.mulPose(com.mojang.math.Vector3f.XP.rotationDegrees(t.angle()));
            }

            float stretchScaleX = (t.stretchXPos() + t.stretchXNeg()) / 2f;
            float stretchScaleY = (t.stretchYPos() + t.stretchYNeg()) / 2f;
            float stretchScaleZ = (t.stretchZPos() + t.stretchZNeg()) / 2f;
            float offsetX = t.scale() * OTHER_PART_STRETCH_HALF_EXTENT * (t.stretchXPos() - t.stretchXNeg()) / 2f;
            float offsetY = t.scale() * OTHER_PART_STRETCH_HALF_EXTENT * (t.stretchYPos() - t.stretchYNeg()) / 2f;
            float offsetZ = t.scale() * OTHER_PART_STRETCH_HALF_EXTENT * (t.stretchZPos() - t.stretchZNeg()) / 2f;
            if (offsetX != 0f || offsetY != 0f || offsetZ != 0f) {
                poseStack.translate(offsetX, offsetY, offsetZ);
            }
            if (t.scale() != 1f || stretchScaleX != 1f || stretchScaleY != 1f || stretchScaleZ != 1f) {
                poseStack.scale(t.scale() * stretchScaleX, t.scale() * stretchScaleY, t.scale() * stretchScaleZ);
            }
            if (spareTire) {

                poseStack.mulPose(com.mojang.math.Vector3f.ZP.rotationDegrees(90f));
                float scaleX = state.wheelScale * state.wheelWidthScale;
                if (scaleX != 1f || state.wheelScale != 1f) {
                    poseStack.scale(scaleX, state.wheelScale, state.wheelScale);
                }
            }
            if (seatedPlayer) {
                if (com.examplebett.render.OtherParts.SITTING_WOLF_ID.equals(option.id())) {
                    renderSittingWolf(state, poseStack, collector);
                } else {
                    renderSeatedPlayer(state, poseStack, collector,
                            com.examplebett.render.OtherParts.SITTING_ALEX_ID.equals(option.id()));
                }
                poseStack.popPose();
                continue;
            }

            boolean wholeVehicle = com.examplebett.render.OtherParts.R34_ID.equals(option.id());
            int partColor = wholeVehicle ? 0xFFFFFF : state.color;
            for (CompiledCarModel.TextureGroup group : partModel.groups) {
                ResourceLocation texture = group.texture;

                boolean usesPaintMask = group.tintable && PaintTintTexture.hasPaint(group.texture);
                if (usesPaintMask) {
                    texture = PaintTintTexture.resolve(group.texture, partColor);
                }
                texture = metalPaintTexture(group, texture, usesPaintMask, partColor);
                final int tint = (group.tintable && !usesPaintMask) ? partColor : 0xFFFFFF;
                RenderType renderType = groupRenderType(group, texture);
                collector.submitCustomGeometry(poseStack, renderType, (pose, vertexConsumer) ->
                        renderGroup(group.vertexData, pose, vertexConsumer, state.lightCoords, tint, BodyDeform.NONE, usesTriangles(group)));
            }
            poseStack.popPose();
        }
    }

    private static final float TEXT_PART_PIXEL_SIZE = 0.02f;

    private static final float TEXT_PART_LAYER_SPACING = 0.004f;
    private static final int TEXT_PART_MAX_LAYERS = 24;

    private static final float TEXT_PART_SIDE_SHADE = 0.55f;
    private static final int TEXT_PART_FULL_BRIGHT = 0xF000F0;

    private static final int TEXT_PART_CACHE_LIMIT = 256;
    private static final Map<String, com.examplebett.render.TextParts.Slot[]> TEXT_PARTS_CACHE = new HashMap<>();
    private static final Map<String, net.minecraft.network.chat.FormattedText> TEXT_SEQUENCE_CACHE = new HashMap<>();

    private static com.examplebett.render.TextParts.Slot[] parseTextParts(String raw) {
        if (TEXT_PARTS_CACHE.size() > TEXT_PART_CACHE_LIMIT) {
            TEXT_PARTS_CACHE.clear();
        }
        return TEXT_PARTS_CACHE.computeIfAbsent(raw, com.examplebett.render.TextParts::parse);
    }

    private static net.minecraft.network.chat.FormattedText textPartSequence(com.examplebett.render.TextParts.Slot slot) {
        if (TEXT_SEQUENCE_CACHE.size() > TEXT_PART_CACHE_LIMIT) {
            TEXT_SEQUENCE_CACHE.clear();
        }
        return TEXT_SEQUENCE_CACHE.computeIfAbsent(slot.font() + "\n" + slot.text(), key -> {
            net.minecraft.network.chat.MutableComponent component = com.examplebett.compat.ModText.literal(slot.text()).copy();
            if (!com.examplebett.render.TextParts.FONT_DEFAULT.equals(slot.font())) {
                ResourceLocation fontId = AiObjectMod.id("text_" + slot.font());

                component = component.withStyle(style -> style.withFont(fontId));
            }

            return component;
        });
    }

    private static final ResourceLocation UNDER_NEON_TEXTURE =
            AiObjectMod.id("textures/entity/lights/under_neon_glow.png");
    private static final Map<String, com.examplebett.render.UnderNeon.Slot[]> UNDER_NEON_CACHE = new HashMap<>();

    private void renderUnderNeon(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector) {
        if (state.underNeonRaw == null || state.underNeonRaw.isEmpty()) {
            return;
        }
        if (UNDER_NEON_CACHE.size() > TEXT_PART_CACHE_LIMIT) {
            UNDER_NEON_CACHE.clear();
        }
        com.examplebett.render.UnderNeon.Slot[] slots = UNDER_NEON_CACHE.computeIfAbsent(state.underNeonRaw, com.examplebett.render.UnderNeon::parse);
        RenderType neonType = RenderType.itemEntityTranslucentCull(UNDER_NEON_TEXTURE);
        for (com.examplebett.render.UnderNeon.Slot slot : slots) {
            if (slot == null || !slot.enabled()) {
                continue;
            }
            float r = ((slot.color() >> 16) & 0xFF) / 255f;
            float g = ((slot.color() >> 8) & 0xFF) / 255f;
            float b = (slot.color() & 0xFF) / 255f;
            float alpha = slot.brightness();
            float hw = slot.width() / 2f;
            float hl = slot.length() / 2f;
            poseStack.pushPose();
            poseStack.translate(slot.x(), slot.y(), slot.z());
            poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(slot.yaw()));
            collector.late().submitCustomGeometry(poseStack, neonType, (pose, consumer) -> {
                addGlowVertex(consumer, pose, -hw, 0f, -hl, 0f, 0f, r, g, b, alpha);
                addGlowVertex(consumer, pose, -hw, 0f, hl, 0f, 1f, r, g, b, alpha);
                addGlowVertex(consumer, pose, hw, 0f, hl, 1f, 1f, r, g, b, alpha);
                addGlowVertex(consumer, pose, hw, 0f, -hl, 1f, 0f, r, g, b, alpha);

                addGlowVertex(consumer, pose, -hw, 0f, -hl, 0f, 0f, r, g, b, alpha);
                addGlowVertex(consumer, pose, hw, 0f, -hl, 1f, 0f, r, g, b, alpha);
                addGlowVertex(consumer, pose, hw, 0f, hl, 1f, 1f, r, g, b, alpha);
                addGlowVertex(consumer, pose, -hw, 0f, hl, 0f, 1f, r, g, b, alpha);
            });
            poseStack.popPose();
        }
    }

    private static final Map<String, com.examplebett.render.BrandLogos.Slot[]> BRAND_LOGO_CACHE = new HashMap<>();

    private static final float BRAND_LOGO_HEIGHT = 0.12f;

    private static final float BRAND_LOGO_RECESS_RATIO = 0.5f;

    private static final int BRAND_LOGO_GLINT_GRID = 16;
    private static final float BRAND_LOGO_GLINT_OFFSET = 0.0005f;
    private static final float BRAND_LOGO_GLINT_STRENGTH = 0.6f;

    private void renderBrandLogos(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector) {
        if (state.brandLogosRaw == null || state.brandLogosRaw.isEmpty() || state.brandLogoDetail == 0) {
            return;
        }
        boolean detailed = state.brandLogoDetail >= 2;
        if (BRAND_LOGO_CACHE.size() > TEXT_PART_CACHE_LIMIT) {
            BRAND_LOGO_CACHE.clear();
        }
        com.examplebett.render.BrandLogos.Slot[] slots = BRAND_LOGO_CACHE.computeIfAbsent(state.brandLogosRaw, com.examplebett.render.BrandLogos::parse);
        boolean glint = com.examplebett.client.AiObjectModClient.isFakeReflectionEnabled();
        for (com.examplebett.render.BrandLogos.Slot slot : slots) {
            if (slot == null || !slot.enabled()) {
                continue;
            }

            int light = slot.glow() ? TEXT_PART_FULL_BRIGHT : state.lightCoords;
            BrandLogoTextures.Design design = BrandLogoTextures.design(slot.logo());

            BrandLogoMesh.Mesh mesh = design.flat() ? null : BrandLogoMesh.get(design);
            float height = BRAND_LOGO_HEIGHT * slot.scale();
            float pixel = height / (mesh != null ? mesh.height() : design.height());
            float halfW = (mesh != null ? mesh.width() : design.width()) * pixel / 2f;
            float halfH = height / 2f;
            float depth = slot.depth();
            float[] levelZ = {0f, depth * (1f - BRAND_LOGO_RECESS_RATIO), depth};

            poseStack.pushPose();
            poseStack.translate(slot.x() + widthShiftFor(state, slot.x()), slot.y(), slot.z() + limoShiftFor(state, slot.z()));
            poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(slot.yaw()));
            poseStack.mulPose(com.mojang.math.Vector3f.XP.rotationDegrees(slot.pitch()));
            poseStack.mulPose(com.mojang.math.Vector3f.ZP.rotationDegrees(slot.roll()));
            if (design.flat()) {

                int layers = detailed
                        ? Math.min(TEXT_PART_MAX_LAYERS, Math.max(1, (int) Math.ceil(depth / TEXT_PART_LAYER_SPACING)))
                        : 0;
                collector.submitCustomGeometry(poseStack, RenderType.entityCutoutNoCull(design.texture()), (pose, consumer) -> {
                    for (int i = 0; i < layers; i++) {
                        addBrandLogoFace(consumer, pose, halfW, halfH, depth * i / layers, 1f, light, 1f, TEXT_PART_SIDE_SHADE);
                    }
                    addBrandLogoFace(consumer, pose, halfW, halfH, depth, 1f, light, 1f);
                });
                poseStack.popPose();
                continue;
            }
            collector.submitCustomGeometry(poseStack, RenderType.entityCutoutNoCull(design.texture()), (pose, consumer) -> {
                addBrandLogoFace(consumer, pose, halfW, halfH, levelZ[0], 1f, light, -1f);
                addBrandLogoFace(consumer, pose, halfW, halfH, levelZ[1], 1f, light, 1f);
                if (!detailed) {
                    return;
                }
                for (BrandLogoMesh.Wall wall : mesh.walls()) {
                    float x0 = -halfW + wall.x0() * pixel;
                    float y0 = halfH - wall.y0() * pixel;
                    float x1 = -halfW + wall.x1() * pixel;
                    float y1 = halfH - wall.y1() * pixel;
                    float zLow = levelZ[wall.lowLevel()];
                    float zHigh = levelZ[wall.highLevel()];

                    float nx = wall.nx();
                    float ny = -wall.ny();
                    addBrandLogoVertex(consumer, pose, x0, y0, zLow, wall.u0(), wall.v0(), light, 1f, nx, ny, 0f);
                    addBrandLogoVertex(consumer, pose, x1, y1, zLow, wall.u1(), wall.v1(), light, 1f, nx, ny, 0f);
                    addBrandLogoVertex(consumer, pose, x1, y1, zHigh, wall.u1(), wall.v1(), light, 1f, nx, ny, 0f);
                    addBrandLogoVertex(consumer, pose, x0, y0, zHigh, wall.u0(), wall.v0(), light, 1f, nx, ny, 0f);
                }
            });
            collector.submitCustomGeometry(poseStack, RenderType.entityCutoutNoCull(design.raisedTexture()), (pose, consumer) ->
                    addBrandLogoFace(consumer, pose, halfW, halfH, levelZ[2], 1f, light, 1f));
            if (glint && detailed) {
                collector.submitCustomGeometry(poseStack, RenderType.entityTranslucent(design.glintTexture()), (pose, consumer) ->
                        addBrandLogoGlint(consumer, pose, halfW, halfH, levelZ[2] + BRAND_LOGO_GLINT_OFFSET, light));
            }
            poseStack.popPose();
        }
    }

    private void addBrandLogoFace(VertexConsumer consumer, PoseStack.Pose pose, float halfW, float halfH, float z, float alpha, int light, float nz) {
        addBrandLogoFace(consumer, pose, halfW, halfH, z, alpha, light, nz, 1f);
    }

    private void addBrandLogoFace(VertexConsumer consumer, PoseStack.Pose pose, float halfW, float halfH, float z, float alpha, int light, float nz,
                                  float shade) {
        addBrandLogoVertex(consumer, pose, -halfW, -halfH, z, 0f, 1f, light, alpha, 0f, 0f, nz, shade);
        addBrandLogoVertex(consumer, pose, halfW, -halfH, z, 1f, 1f, light, alpha, 0f, 0f, nz, shade);
        addBrandLogoVertex(consumer, pose, halfW, halfH, z, 1f, 0f, light, alpha, 0f, 0f, nz, shade);
        addBrandLogoVertex(consumer, pose, -halfW, halfH, z, 0f, 0f, light, alpha, 0f, 0f, nz, shade);
    }

    private void addBrandLogoGlint(VertexConsumer consumer, PoseStack.Pose pose, float halfW, float halfH, float z, int light) {

        float[] camera = PoseMatrices.transformPosition(PoseMatrices.invert(PoseMatrices.capture(pose)), 0f, 0f, 0f);
        float toCameraX = camera[0];
        float toCameraY = camera[1];
        float toCameraZ = camera[2] - z;
        float toCameraLength = (float) Math.sqrt(toCameraX * toCameraX + toCameraY * toCameraY + toCameraZ * toCameraZ);
        if (toCameraLength < 1e-4f) {
            return;
        }
        float phase = 0.5f + 1.1f * (toCameraX / toCameraLength * 0.8f + toCameraY / toCameraLength * 0.6f);
        int blockLight = light & 0xFFFF;
        int skyLight = (light >> 16) & 0xFFFF;
        float strength = BRAND_LOGO_GLINT_STRENGTH * Math.max(blockLight, skyLight) / 240f;
        if (strength <= 0f) {
            return;
        }
        int n = BRAND_LOGO_GLINT_GRID;
        float[][] alpha = new float[n + 1][n + 1];
        boolean any = false;
        for (int j = 0; j <= n; j++) {
            for (int i = 0; i <= n; i++) {
                float u = (float) i / n;
                float v = (float) j / n;

                float s = (u * 0.8f + (1f - v) * 0.6f) / 1.4f;
                float value = Math.min(1f, glintBand(s, phase, 0.14f) + 0.6f * glintBand(s, phase - 0.24f, 0.05f)) * strength;
                alpha[j][i] = value;
                any |= value > 0.001f;
            }
        }
        if (!any) {
            return;
        }
        for (int j = 0; j < n; j++) {
            for (int i = 0; i < n; i++) {
                if (alpha[j][i] <= 0.001f && alpha[j][i + 1] <= 0.001f && alpha[j + 1][i] <= 0.001f && alpha[j + 1][i + 1] <= 0.001f) {
                    continue;
                }
                float u0 = (float) i / n;
                float u1 = (float) (i + 1) / n;
                float v0 = (float) j / n;
                float v1 = (float) (j + 1) / n;
                float x0 = -halfW + u0 * 2f * halfW;
                float x1 = -halfW + u1 * 2f * halfW;
                float y0 = halfH - v0 * 2f * halfH;
                float y1 = halfH - v1 * 2f * halfH;
                addBrandLogoVertex(consumer, pose, x0, y1, z, u0, v1, light, alpha[j + 1][i], 0f, 0f, 1f);
                addBrandLogoVertex(consumer, pose, x1, y1, z, u1, v1, light, alpha[j + 1][i + 1], 0f, 0f, 1f);
                addBrandLogoVertex(consumer, pose, x1, y0, z, u1, v0, light, alpha[j][i + 1], 0f, 0f, 1f);
                addBrandLogoVertex(consumer, pose, x0, y0, z, u0, v0, light, alpha[j][i], 0f, 0f, 1f);
            }
        }
    }

    private static float glintBand(float s, float center, float halfWidth) {
        return MetalTextConsumer.glintBand(s, center, halfWidth);
    }

    private void addBrandLogoVertex(VertexConsumer consumer, PoseStack.Pose pose, float x, float y, float z, float u, float v,
                                    int packedLight, float alpha, float nx, float ny, float nz) {
        addBrandLogoVertex(consumer, pose, x, y, z, u, v, packedLight, alpha, nx, ny, nz, 1f);
    }

    private void addBrandLogoVertex(VertexConsumer consumer, PoseStack.Pose pose, float x, float y, float z, float u, float v,
                                    int packedLight, float alpha, float nx, float ny, float nz, float shade) {
        consumer.vertex(pose.pose(), x, y, z)
                .color(shade, shade, shade, alpha)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(pose.normal(), nx, ny, nz)
                .endVertex();
    }

    private void renderTextParts(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector) {
        if (state.textPartsRaw == null || state.textPartsRaw.isEmpty()) {
            return;
        }
        net.minecraft.client.gui.Font font = net.minecraft.client.Minecraft.getInstance().font;
        for (com.examplebett.render.TextParts.Slot slot : parseTextParts(state.textPartsRaw)) {
            if (slot == null || !slot.visible()) {
                continue;
            }
            net.minecraft.network.chat.FormattedText text = textPartSequence(slot);
            float left = -font.width(text) / 2f;
            float top = -font.lineHeight / 2f;
            int light = slot.glow() ? TEXT_PART_FULL_BRIGHT : state.lightCoords;
            int rgb = slot.color() & 0xFFFFFF;
            int sideRgb = ((int) (((rgb >> 16) & 0xFF) * TEXT_PART_SIDE_SHADE) << 16)
                    | ((int) (((rgb >> 8) & 0xFF) * TEXT_PART_SIDE_SHADE) << 8)
                    | (int) ((rgb & 0xFF) * TEXT_PART_SIDE_SHADE);
            float pixel = TEXT_PART_PIXEL_SIZE * slot.scale();

            float pixelX = pixel * slot.width();
            float pixelY = pixel * slot.height();

            poseStack.pushPose();
            poseStack.translate(slot.x() + widthShiftFor(state, slot.x()), slot.y(), slot.z() + limoShiftFor(state, slot.z()));
            poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(slot.yaw()));
            poseStack.mulPose(com.mojang.math.Vector3f.XP.rotationDegrees(slot.pitch()));
            poseStack.mulPose(com.mojang.math.Vector3f.ZP.rotationDegrees(slot.roll()));
            int layers = slot.depth() > 0f
                    ? Math.min(TEXT_PART_MAX_LAYERS, Math.max(1, (int) Math.ceil(slot.depth() / TEXT_PART_LAYER_SPACING)))
                    : 0;
            for (int i = layers; i >= 1; i--) {
                poseStack.pushPose();
                poseStack.translate(0f, 0f, -slot.depth() * i / layers);
                poseStack.scale(pixelX, -pixelY, pixel);
                collector.submitText(poseStack, text, left, top, 0xFF000000 | sideRgb, light);
                poseStack.popPose();
            }

            poseStack.scale(pixelX, -pixelY, pixel);
            boolean drawn = false;
            int metal = com.examplebett.client.gui.CarColorPalette.metalIndex(rgb);
            if (metal >= 0) {
                submitMetalText(poseStack, collector, font, text, left, top, rgb, metal, light, state.brandLogoDetail >= 2);
                drawn = true;
            }
            if (!drawn) {
                collector.submitText(poseStack, text, left, top, 0xFF000000 | rgb, light);
            }
            poseStack.popPose();
        }
    }

    private void submitMetalText(PoseStack poseStack, GeometrySubmitter collector, net.minecraft.client.gui.Font font,
                                 net.minecraft.network.chat.FormattedText text, float left, float top, int rgb, int metal,
                                 int light, boolean band) {
        float width = Math.max(1f, font.width(text));
        float height = font.lineHeight;
        collector.submitWrappedText(poseStack, text, left, top, light, (out, pose) ->
                new MetalTextConsumer(out, pose, left, top, width, height, rgb, metal,
                        band ? MetalTextConsumer.phase(pose, left + width / 2f, top + height / 2f) : Float.NaN));
    }

    private static void computeExhaustOutlets(CarRenderState state, PoseStack poseStack, float[] origin) {
        int outlets = com.examplebett.render.ExhaustParts.outletsOf(state.exhaustEnabledId);
        com.examplebett.render.PartTransforms.Transform6 t = state.exhaustTransform;
        if (outlets == 0 || t == null) {
            state.exhaustTarget.clientExhaustOutlets = null;
            return;
        }
        float[] relative = PoseMatrices.relative(origin, PoseMatrices.capture(poseStack));
        float[] result = new float[outlets * 6];
        for (int i = 0; i < outlets; i++) {
            float side = i == 0 ? 1f : -1f;
            float localX = t.x() * side;
            float[] position = PoseMatrices.transformPosition(relative,
                    localX + widthShiftFor(state, localX), t.y(), t.z() + limoShiftFor(state, t.z()));

            float ax = (float) Math.toRadians(t.angle());
            float ay = (float) Math.toRadians(t.angleY() * side);
            float y1 = (float) Math.sin(ax);
            float z1 = -(float) Math.cos(ax);
            float x2 = z1 * (float) Math.sin(ay);
            float z2 = z1 * (float) Math.cos(ay);
            float[] direction = PoseMatrices.transformDirection(relative, x2, y1, z2);
            result[i * 6] = position[0];
            result[i * 6 + 1] = position[1];
            result[i * 6 + 2] = position[2];
            result[i * 6 + 3] = direction[0];
            result[i * 6 + 4] = direction[1];
            result[i * 6 + 5] = direction[2];
        }
        state.exhaustTarget.clientExhaustOutlets = result;
    }

    private void renderTaxiSign(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector) {
        if (state.taxiSignEnabledIds == null || state.taxiSignEnabledIds.isEmpty()) {
            return;
        }
        Set<String> enabledIds = parseEnabledIds(state.taxiSignEnabledIds);
        net.minecraft.server.packs.resources.ResourceManager resourceManager =
                net.minecraft.client.Minecraft.getInstance().getResourceManager();
        com.examplebett.render.PartTransforms.Transform12 defaults = new com.examplebett.render.PartTransforms.Transform12(
                com.examplebett.entity.CarEntity.DEFAULT_TAXI_SIGN_X, com.examplebett.entity.CarEntity.DEFAULT_TAXI_SIGN_Y,
                com.examplebett.entity.CarEntity.DEFAULT_TAXI_SIGN_Z, 0f, 0f, com.examplebett.entity.CarEntity.DEFAULT_TAXI_SIGN_SCALE,
                1f, 1f, 1f, 1f, 1f, 1f);
        for (com.examplebett.render.TaxiSignParts.Option option : com.examplebett.render.TaxiSignParts.list(resourceManager)) {
            if (!enabledIds.contains(option.id())) {
                continue;
            }
            CompiledCarModel signModel = TaxiSignModel.get(resourceManager, option.modelId());
            if (signModel == null) {
                continue;
            }
            com.examplebett.render.PartTransforms.Transform12 t =
                    com.examplebett.render.PartTransforms.resolve12(state.taxiSignTransformsRaw, option.id(), defaults);
            poseStack.pushPose();
            poseStack.translate(t.x() + widthShiftFor(state, t.x()), t.y(), t.z() + limoShiftFor(state, t.z()));
            if (t.angleY() != 0f) {
                poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(t.angleY()));
            }
            if (t.angle() != 0f) {
                poseStack.mulPose(com.mojang.math.Vector3f.XP.rotationDegrees(t.angle()));
            }

            float stretchScaleX = (t.stretchXPos() + t.stretchXNeg()) / 2f;
            float stretchScaleY = (t.stretchYPos() + t.stretchYNeg()) / 2f;
            float stretchScaleZ = (t.stretchZPos() + t.stretchZNeg()) / 2f;
            float offsetX = t.scale() * OTHER_PART_STRETCH_HALF_EXTENT * (t.stretchXPos() - t.stretchXNeg()) / 2f;
            float offsetY = t.scale() * OTHER_PART_STRETCH_HALF_EXTENT * (t.stretchYPos() - t.stretchYNeg()) / 2f;
            float offsetZ = t.scale() * OTHER_PART_STRETCH_HALF_EXTENT * (t.stretchZPos() - t.stretchZNeg()) / 2f;
            if (offsetX != 0f || offsetY != 0f || offsetZ != 0f) {
                poseStack.translate(offsetX, offsetY, offsetZ);
            }
            if (t.scale() != 1f || stretchScaleX != 1f || stretchScaleY != 1f || stretchScaleZ != 1f) {
                poseStack.scale(t.scale() * stretchScaleX, t.scale() * stretchScaleY, t.scale() * stretchScaleZ);
            }
            for (CompiledCarModel.TextureGroup group : signModel.groups) {
                ResourceLocation texture = group.texture;
                final int tint = group.tintable ? state.color : 0xFFFFFF;
                RenderType renderType = groupRenderType(group, texture);
                collector.submitCustomGeometry(poseStack, renderType, (pose, vertexConsumer) ->
                        renderGroup(group.vertexData, pose, vertexConsumer, state.lightCoords, tint, BodyDeform.NONE, usesTriangles(group)));
            }
            poseStack.popPose();
        }
    }

    private void renderPoliceLight(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector) {
        if (state.policeLightEnabledIds == null || state.policeLightEnabledIds.isEmpty()) {
            return;
        }
        Set<String> enabledIds = parseEnabledIds(state.policeLightEnabledIds);
        net.minecraft.server.packs.resources.ResourceManager resourceManager =
                net.minecraft.client.Minecraft.getInstance().getResourceManager();
        com.examplebett.render.PartTransforms.Transform12 defaults = new com.examplebett.render.PartTransforms.Transform12(
                com.examplebett.entity.CarEntity.DEFAULT_POLICE_LIGHT_X, com.examplebett.entity.CarEntity.DEFAULT_POLICE_LIGHT_Y,
                com.examplebett.entity.CarEntity.DEFAULT_POLICE_LIGHT_Z, 0f, 0f, com.examplebett.entity.CarEntity.DEFAULT_POLICE_LIGHT_SCALE,
                1f, 1f, 1f, 1f, 1f, 1f);
        for (com.examplebett.render.PoliceLightParts.Option option : com.examplebett.render.PoliceLightParts.list(resourceManager)) {
            if (!enabledIds.contains(option.id())) {
                continue;
            }

            CompiledCarModel lightModel = option.modelId() == null ? null : PoliceLightModel.get(resourceManager, option.modelId());
            if (lightModel == null && option.modelId() != null) {
                continue;
            }
            com.examplebett.render.PartTransforms.Transform12 t =
                    com.examplebett.render.PartTransforms.resolve12(state.policeLightTransformsRaw, option.id(), defaults);
            poseStack.pushPose();
            poseStack.translate(t.x() + widthShiftFor(state, t.x()), t.y(), t.z() + limoShiftFor(state, t.z()));
            if (t.angleY() != 0f) {
                poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(t.angleY()));
            }
            if (t.angle() != 0f) {
                poseStack.mulPose(com.mojang.math.Vector3f.XP.rotationDegrees(t.angle()));
            }

            float stretchScaleX = (t.stretchXPos() + t.stretchXNeg()) / 2f;
            float stretchScaleY = (t.stretchYPos() + t.stretchYNeg()) / 2f;
            float stretchScaleZ = (t.stretchZPos() + t.stretchZNeg()) / 2f;
            float offsetX = t.scale() * OTHER_PART_STRETCH_HALF_EXTENT * (t.stretchXPos() - t.stretchXNeg()) / 2f;
            float offsetY = t.scale() * OTHER_PART_STRETCH_HALF_EXTENT * (t.stretchYPos() - t.stretchYNeg()) / 2f;
            float offsetZ = t.scale() * OTHER_PART_STRETCH_HALF_EXTENT * (t.stretchZPos() - t.stretchZNeg()) / 2f;
            if (offsetX != 0f || offsetY != 0f || offsetZ != 0f) {
                poseStack.translate(offsetX, offsetY, offsetZ);
            }
            if (t.scale() != 1f || stretchScaleX != 1f || stretchScaleY != 1f || stretchScaleZ != 1f) {
                poseStack.scale(t.scale() * stretchScaleX, t.scale() * stretchScaleY, t.scale() * stretchScaleZ);
            }
            for (CompiledCarModel.TextureGroup group : lightModel == null ? List.<CompiledCarModel.TextureGroup>of() : lightModel.groups) {
                ResourceLocation texture = group.texture;
                final int tint = group.tintable ? state.color : 0xFFFFFF;
                RenderType renderType = groupRenderType(group, texture);
                collector.submitCustomGeometry(poseStack, renderType, (pose, vertexConsumer) ->
                        renderGroup(group.vertexData, pose, vertexConsumer, state.lightCoords, tint, BodyDeform.NONE, usesTriangles(group)));
            }
            if (state.policeLightsOn) {
                if (com.examplebett.render.PoliceLightParts.isEmergencyBeacon(option.id())) {
                    renderEmergencyBeaconLights(state, poseStack, collector);
                }
                if (com.examplebett.render.PoliceLightParts.JP_LIGHT_BAR_ID.equals(option.id())) {
                    renderJpLightBarLights(state, poseStack, collector);
                }
                if (com.examplebett.render.PoliceLightParts.hasUsLightBarLights(option.id())) {
                    renderUsLightBarLights(state, poseStack, collector, false);
                }
                if (com.examplebett.render.PoliceLightParts.isEuLightBar(option.id())) {
                    renderUsLightBarLights(state, poseStack, collector, true);
                }
                if (com.examplebett.render.PoliceLightParts.isEuPattern(option.id())) {
                    renderEuPatternLights(state, poseStack, collector,
                            com.examplebett.render.PoliceLightParts.isEuPatternLeftFirst(option.id()));
                }
                if (com.examplebett.render.PoliceLightParts.JP_PATTERN_ID.equals(option.id())) {
                    renderJpPatternLights(state, poseStack, collector);
                }
            }
            poseStack.popPose();
        }
    }

    private static final float ROTATING_LIGHT_ROTATE_DEG_PER_TICK = 15f;

    private static final int QUAD_FLASH_ON_TICKS = 3;
    private static final int QUAD_FLASH_GAP_TICKS = 3;
    private static final int QUAD_FLASH_GROUP_GAP_TICKS = 4;
    private static final int QUAD_FLASH_GROUP_SLOT_TICKS =
            2 * QUAD_FLASH_ON_TICKS + QUAD_FLASH_GAP_TICKS + QUAD_FLASH_GROUP_GAP_TICKS;
    private static final int QUAD_FLASH_CYCLE_TICKS = QUAD_FLASH_GROUP_SLOT_TICKS * 2;

    private record QuadFlashState(boolean groupATurn, boolean flashOn) {
    }

    private static QuadFlashState computeQuadFlashState(int age) {
        int cyclePos = age % QUAD_FLASH_CYCLE_TICKS;
        boolean groupATurn = cyclePos < QUAD_FLASH_GROUP_SLOT_TICKS;
        int localPhase = groupATurn ? cyclePos : cyclePos - QUAD_FLASH_GROUP_SLOT_TICKS;
        boolean flashOn = localPhase < QUAD_FLASH_ON_TICKS
                || (localPhase >= QUAD_FLASH_ON_TICKS + QUAD_FLASH_GAP_TICKS
                        && localPhase < 2 * QUAD_FLASH_ON_TICKS + QUAD_FLASH_GAP_TICKS);
        return new QuadFlashState(groupATurn, flashOn);
    }

    private static final float EMERGENCY_BEACON_RADIUS = 0.18f;

    private static final float EMERGENCY_BEACON_PIVOT_Z = -0.165f;

    private static final float EMERGENCY_BEACON_GLOW_SIZE = 0.36f;

    private static final float EMERGENCY_BEACON_SECOND_SET_OFFSET_DEG = 90f;

    private void renderEmergencyBeaconLights(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector) {
        float rotateDeg = (state.age * ROTATING_LIGHT_ROTATE_DEG_PER_TICK) % 360f;
        QuadFlashState flash = computeQuadFlashState(state.age);
        if (!flash.flashOn()) {
            return;
        }
        float setRotateDeg = flash.groupATurn() ? rotateDeg : rotateDeg + EMERGENCY_BEACON_SECOND_SET_OFFSET_DEG;
        submitRotatingLightGlow(poseStack, collector, 0f, 0f, EMERGENCY_BEACON_PIVOT_Z,
                EMERGENCY_BEACON_RADIUS, EMERGENCY_BEACON_GLOW_SIZE, setRotateDeg, 1f, 1f, 0f, 0f);
        submitRotatingLightGlow(poseStack, collector, 0f, 0f, EMERGENCY_BEACON_PIVOT_Z,
                EMERGENCY_BEACON_RADIUS, EMERGENCY_BEACON_GLOW_SIZE, setRotateDeg + 180f, 1f, 1f, 0f, 0f);
    }

    private void submitRotatingLightGlow(PoseStack poseStack, GeometrySubmitter collector,
                                          float pivotX, float pivotY, float pivotZ, float radius, float glowSize,
                                          float rotateDeg, float alpha, float tintR, float tintG, float tintB) {
        double rotateRad = Math.toRadians(rotateDeg);
        float dx = (float) (Math.sin(rotateRad) * radius);
        float dz = (float) (Math.cos(rotateRad) * radius);
        submitGlow(poseStack, collector, HEADLIGHT_GLOW_TEXTURE,
                pivotX + dx, pivotY, pivotZ + dz, glowSize, 0f,
                tintR, tintG, tintB, alpha);
    }

    private static final float LIGHT_BAR_ROTATOR_RADIUS = 0.15f;

    private static final float LIGHT_BAR_ROTATOR_GLOW_SIZE = 0.30f;

    private static final float[][] JP_LIGHT_BAR_ROTATOR_POSITIONS = {
            {-0.80f, 0.181f, -0.205f},
            {-0.35f, 0.179f, -0.015f},
            {0.35f, 0.177f, 0.011f},
            {0.80f, 0.173f, -0.205f},
    };

    private void renderJpLightBarLights(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector) {
        float rotateDeg = (state.age * ROTATING_LIGHT_ROTATE_DEG_PER_TICK) % 360f;
        QuadFlashState flash = computeQuadFlashState(state.age);
        if (!flash.flashOn()) {
            return;
        }

        for (int i = 0; i < JP_LIGHT_BAR_ROTATOR_POSITIONS.length; i++) {
            boolean isCenterSet = i == 1 || i == 2;
            if (isCenterSet != flash.groupATurn()) {
                continue;
            }
            float[] pos = JP_LIGHT_BAR_ROTATOR_POSITIONS[i];
            submitRotatingLightGlow(poseStack, collector, pos[0], pos[1], pos[2],
                    LIGHT_BAR_ROTATOR_RADIUS, LIGHT_BAR_ROTATOR_GLOW_SIZE, rotateDeg, 1f, 1f, 0f, 0f);
            submitRotatingLightGlow(poseStack, collector, pos[0], pos[1], pos[2],
                    LIGHT_BAR_ROTATOR_RADIUS, LIGHT_BAR_ROTATOR_GLOW_SIZE, rotateDeg + 180f, 1f, 1f, 0f, 0f);
        }
    }

    private static final float[][] US_LIGHT_BAR_ROTATOR_POSITIONS = {
            {-0.80f, 0.138f, 0.015f},
            {-0.35f, 0.138f, 0.015f},
            {0.35f, 0.138f, 0.015f},
            {0.80f, 0.138f, 0.015f},
    };

    private void renderUsLightBarLights(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector,
                                        boolean allBlue) {
        float rotateDeg = (state.age * ROTATING_LIGHT_ROTATE_DEG_PER_TICK) % 360f;
        QuadFlashState flash = computeQuadFlashState(state.age);
        if (!flash.flashOn()) {
            return;
        }

        for (int i = 0; i < US_LIGHT_BAR_ROTATOR_POSITIONS.length; i++) {
            boolean isCenterSet = i == 1 || i == 2;
            if (isCenterSet != flash.groupATurn()) {
                continue;
            }
            float[] pos = US_LIGHT_BAR_ROTATOR_POSITIONS[i];
            boolean isBlue = allBlue || i >= 2;
            float tintR = isBlue ? 0f : 1f;
            float tintB = isBlue ? 1f : 0f;
            submitRotatingLightGlow(poseStack, collector, pos[0], pos[1], pos[2],
                    LIGHT_BAR_ROTATOR_RADIUS, LIGHT_BAR_ROTATOR_GLOW_SIZE, rotateDeg, 1f, tintR, 0f, tintB);
            submitRotatingLightGlow(poseStack, collector, pos[0], pos[1], pos[2],
                    LIGHT_BAR_ROTATOR_RADIUS, LIGHT_BAR_ROTATOR_GLOW_SIZE, rotateDeg + 180f, 1f, tintR, 0f, tintB);
        }
    }

    private static final int EU_PATTERN_STEP_TICKS = 2;

    private static final int JP_PATTERN_DARK_TICKS = 10;

    private static final float[] SIDE_PAIR_PATTERN_X = {-0.475f, -0.325f, 0.325f, 0.475f};

    private static final boolean SIDE_PAIR_PATTERN_SINGLE_LIGHT = true;

    private void submitSidePairPatternGlow(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector,
                                           int index, float tintR, float tintB) {
        float x = SIDE_PAIR_PATTERN_X[index];
        if (SIDE_PAIR_PATTERN_SINGLE_LIGHT) {

            if (state.age % EU_PATTERN_STEP_TICKS >= EU_PATTERN_STEP_TICKS / 2) {
                return;
            }

            x = index < 2 ? (SIDE_PAIR_PATTERN_X[0] + SIDE_PAIR_PATTERN_X[1]) / 2f
                    : (SIDE_PAIR_PATTERN_X[2] + SIDE_PAIR_PATTERN_X[3]) / 2f;
        }
        float[] usPos = US_LIGHT_BAR_ROTATOR_POSITIONS[index];
        submitGlow(poseStack, collector, HEADLIGHT_GLOW_TEXTURE, x, usPos[1], usPos[2],
                LIGHT_BAR_ROTATOR_GLOW_SIZE, 0f, tintR, 0f, tintB, 1f);
    }

    private void renderEuPatternLights(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector,
                                       boolean leftFirst) {
        int step = (state.age / EU_PATTERN_STEP_TICKS) % 8;
        boolean firstSide = step < 4;
        boolean leftSide = firstSide == leftFirst;

        int outer = leftSide ? 0 : 3;
        int inner = leftSide ? 1 : 2;
        submitSidePairPatternGlow(state, poseStack, collector, (step % 2 == 0) ? outer : inner, 0f, 1f);
    }

    private void renderJpPatternLights(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector) {
        int flashTicks = EU_PATTERN_STEP_TICKS * 4;
        int pos = state.age % (flashTicks + JP_PATTERN_DARK_TICKS);
        if (pos >= flashTicks) {
            return;
        }
        boolean outerTurn = (pos / EU_PATTERN_STEP_TICKS) % 2 == 0;
        submitSidePairPatternGlow(state, poseStack, collector, outerTurn ? 0 : 1, 1f, 0f);
        submitSidePairPatternGlow(state, poseStack, collector, outerTurn ? 3 : 2, 1f, 0f);
    }

    private static void applyGroupTransform(PoseStack poseStack, CarRenderState state,
                                             CompiledCarModel.TextureGroup group, boolean spinning, boolean rotorSpinning,
                                             float extraZ) {

        if ((spinning || rotorSpinning)
                && (state.suspensionPitchDegrees != 0f || state.suspensionRollDegrees != 0f)) {
            poseStack.translate(0.0, state.tumblePivotHeight, 0.0);
            if (state.suspensionRollDegrees != 0f) {
                poseStack.mulPose(com.mojang.math.Vector3f.ZP.rotationDegrees(-state.suspensionRollDegrees));
            }
            if (state.suspensionPitchDegrees != 0f) {
                poseStack.mulPose(com.mojang.math.Vector3f.XP.rotationDegrees(-state.suspensionPitchDegrees));
            }
            poseStack.translate(0.0, -state.tumblePivotHeight, 0.0);
        }
        if (spinning) {
            ObjModel.WheelPivot pivot = state.compiledModel.wheelPivots.get(group.wheelGroup);

            if (state.bodyHeightOffset != 0f) {
                poseStack.translate(0.0, -state.bodyHeightOffset, 0.0);
            }

            float sideSign = embeddedWheelSideSign(pivot);
            poseStack.translate(embeddedWheelX(state, pivot), embeddedWheelY(state, pivot),
                    embeddedWheelZ(state, pivot.centerZ + extraZ, pivot.front));

            if (state.wheelCamberDegrees != 0f) {
                poseStack.mulPose(com.mojang.math.Vector3f.ZP.rotationDegrees(sideSign * state.wheelCamberDegrees));
            }

            if (pivot.front) {
                poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(state.frontWheelSteerDegrees));
            }

            float rollDegrees = state.wheelRotationDegrees + (pivot.front ? 0f : state.rearWheelExtraSpinDegrees);
            poseStack.mulPose(wheelAxisRotation(pivot, rollDegrees));

            float axialScale = state.wheelScale * state.wheelWidthScale;
            if (axialScale != 1f || state.wheelScale != 1f) {
                poseStack.scale(pivot.axisX >= 0.5f ? axialScale : state.wheelScale,
                        pivot.axisY >= 0.5f ? axialScale : state.wheelScale,
                        pivot.axisZ >= 0.5f ? axialScale : state.wheelScale);
            }
            poseStack.translate(-pivot.centerX, -pivot.centerY, -pivot.centerZ);
        } else if (rotorSpinning) {
            ObjModel.RotorPivot pivot = state.compiledModel.rotorPivots.get(group.rotorGroup);
            poseStack.translate(pivot.centerX, pivot.centerY, pivot.centerZ);
            poseStack.mulPose(rotorAxisRotation(pivot, state.rotorRotationDegrees));
            poseStack.translate(-pivot.centerX, -pivot.centerY, -pivot.centerZ);
        }
    }

    private static float embeddedWheelSideSign(ObjModel.WheelPivot pivot) {
        return pivot.centerX >= 0f ? 1f : -1f;
    }

    private static float embeddedWheelX(CarRenderState state, ObjModel.WheelPivot pivot) {
        float x = pivot.centerX + embeddedWheelSideSign(pivot) * state.wheelTrackHalfWidthOffset;
        return x + widthShiftFor(state, x);
    }

    private static float embeddedWheelY(CarRenderState state, ObjModel.WheelPivot pivot) {
        return pivot.centerY + state.wheelAxleHeightOffset;
    }

    private static float embeddedWheelZ(CarRenderState state, float baseZ, boolean front) {
        float z = baseZ + (front ? state.wheelFrontZOffset : state.wheelRearZOffset);
        return z + limoShiftFor(state, z);
    }

    private static com.mojang.math.Quaternion wheelAxisRotation(ObjModel.WheelPivot pivot, float degrees) {
        if (pivot.axisX >= 0.5f) {
            return com.mojang.math.Vector3f.XP.rotationDegrees(degrees);
        }
        if (pivot.axisY >= 0.5f) {
            return com.mojang.math.Vector3f.YP.rotationDegrees(degrees);
        }
        return com.mojang.math.Vector3f.ZP.rotationDegrees(degrees);
    }

    private static com.mojang.math.Quaternion rotorAxisRotation(ObjModel.RotorPivot pivot, float degrees) {
        if (pivot.axisX >= 0.5f) {
            return com.mojang.math.Vector3f.XP.rotationDegrees(degrees);
        }
        if (pivot.axisY >= 0.5f) {
            return com.mojang.math.Vector3f.YP.rotationDegrees(degrees);
        }
        return com.mojang.math.Vector3f.ZP.rotationDegrees(degrees);
    }

    private static final float PLATE_HALF_WIDTH = 0.45f;

    private void submitPlate(PoseStack poseStack, GeometrySubmitter collector, int packedLight, ResourceLocation texture,
                              float x, float y, float z, float scale, float yawDeg, float pitchDeg, float rollDeg, float aspect) {
        poseStack.pushPose();
        poseStack.translate(x, y, z);
        poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(yawDeg));
        poseStack.mulPose(com.mojang.math.Vector3f.XP.rotationDegrees(pitchDeg));
        poseStack.mulPose(com.mojang.math.Vector3f.ZP.rotationDegrees(rollDeg));
        RenderType plateType = RenderType.entityCutoutNoCull(texture);
        collector.submitCustomGeometry(poseStack, plateType, (pose, vertexConsumer) ->
                renderPlate(pose, vertexConsumer, packedLight, scale, aspect));
        poseStack.popPose();
    }

    private void renderPlate(PoseStack.Pose pose, VertexConsumer consumer, int packedLight, float scale, float aspect) {
        float halfW = PLATE_HALF_WIDTH * scale;
        float halfH = (PLATE_HALF_WIDTH / aspect) * scale;
        addPlateVertex(consumer, pose, -halfW, -halfH, 0f, 0f, 1f, packedLight);
        addPlateVertex(consumer, pose, halfW, -halfH, 0f, 1f, 1f, packedLight);
        addPlateVertex(consumer, pose, halfW, halfH, 0f, 1f, 0f, packedLight);
        addPlateVertex(consumer, pose, -halfW, halfH, 0f, 0f, 0f, packedLight);
    }

    private void addPlateVertex(VertexConsumer consumer, PoseStack.Pose pose, float x, float y, float z, float u, float v, int packedLight) {
        consumer.vertex(pose.pose(), x, y, z)
                .color(1f, 1f, 1f, 1f)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(pose.normal(), 0f, 1f, 0f)
                .endVertex();
    }

    private static final ResourceLocation HEADLIGHT_GLOW_TEXTURE =
            AiObjectMod.id("textures/entity/lights/headlight_glow.png");
    private static final ResourceLocation TAILLIGHT_GLOW_TEXTURE =
            AiObjectMod.id("textures/entity/lights/taillight_glow.png");
    private static final float HEADLIGHT_GLOW_SIZE = 0.35f;
    private static final float TAILLIGHT_GLOW_SIZE = 0.25f;

    private static final int FULL_BRIGHT_LIGHT = 0xF000F0;

    private void submitGlow(PoseStack poseStack, GeometrySubmitter collector, ResourceLocation texture,
                             float x, float y, float z, float halfSize, float yawDeg,
                             float tintR, float tintG, float tintB) {
        submitGlow(poseStack, collector, texture, x, y, z, halfSize, yawDeg, tintR, tintG, tintB, 1f);
    }

    private void submitGlow(PoseStack poseStack, GeometrySubmitter collector, ResourceLocation texture,
                             float x, float y, float z, float halfSize, float yawDeg,
                             float tintR, float tintG, float tintB, float alpha) {
        poseStack.pushPose();
        poseStack.translate(x, y, z);

        RenderType glowType = RenderType.itemEntityTranslucentCull(texture);
        submitGlowQuad(poseStack, collector, glowType, halfSize, yawDeg, 0f, tintR, tintG, tintB, alpha);
        submitGlowQuad(poseStack, collector, glowType, halfSize, yawDeg + 90f, 0f, tintR, tintG, tintB, alpha);
        submitGlowQuad(poseStack, collector, glowType, halfSize, yawDeg, 90f, tintR, tintG, tintB, alpha);
        poseStack.popPose();
    }

    private void submitGlowQuad(PoseStack poseStack, GeometrySubmitter collector, RenderType glowType,
                                 float halfSize, float yawDeg, float pitchDeg,
                                 float tintR, float tintG, float tintB, float alpha) {
        poseStack.pushPose();
        poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(yawDeg));
        poseStack.mulPose(com.mojang.math.Vector3f.XP.rotationDegrees(pitchDeg));
        collector.late().submitCustomGeometry(poseStack, glowType, (pose, vertexConsumer) ->
                renderGlow(pose, vertexConsumer, halfSize, tintR, tintG, tintB, alpha));
        poseStack.popPose();
    }

    private void renderGlow(PoseStack.Pose pose, VertexConsumer consumer, float halfSize,
                             float tintR, float tintG, float tintB, float alpha) {
        addGlowVertex(consumer, pose, -halfSize, -halfSize, 0f, 0f, 1f, tintR, tintG, tintB, alpha);
        addGlowVertex(consumer, pose, halfSize, -halfSize, 0f, 1f, 1f, tintR, tintG, tintB, alpha);
        addGlowVertex(consumer, pose, halfSize, halfSize, 0f, 1f, 0f, tintR, tintG, tintB, alpha);
        addGlowVertex(consumer, pose, -halfSize, halfSize, 0f, 0f, 0f, tintR, tintG, tintB, alpha);

        addGlowVertex(consumer, pose, -halfSize, -halfSize, 0f, 0f, 1f, tintR, tintG, tintB, alpha);
        addGlowVertex(consumer, pose, -halfSize, halfSize, 0f, 0f, 0f, tintR, tintG, tintB, alpha);
        addGlowVertex(consumer, pose, halfSize, halfSize, 0f, 1f, 0f, tintR, tintG, tintB, alpha);
        addGlowVertex(consumer, pose, halfSize, -halfSize, 0f, 1f, 1f, tintR, tintG, tintB, alpha);
    }

    private void addGlowVertex(VertexConsumer consumer, PoseStack.Pose pose, float x, float y, float z, float u, float v,
                                float tintR, float tintG, float tintB, float alpha) {
        consumer.vertex(pose.pose(), x, y, z)
                .color(tintR, tintG, tintB, alpha)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(FULL_BRIGHT_LIGHT)
                .normal(pose.normal(), 0f, 1f, 0f)
                .endVertex();
    }

    private record BodyDeform(boolean active, float bodyMinY,
                               float lipStretchScale, float lipWidthScale, float lipBandHeight, float lipFrontExtendScale,
                               float limoAnchorZ, float limoStretchM, float limoZoneM,
                               float widthAnchorX, float widthStretchM, float widthZoneM) {
        static final BodyDeform NONE = new BodyDeform(false, Float.NaN, 1f, 1f, 0f, 1f, Float.NaN, 0f, 0f, Float.NaN, 0f, 0f);

        static BodyDeform from(CarRenderState state) {
            boolean lipActive = !Float.isNaN(state.compiledModel.bodyMinY)
                    && (state.lipStretchScale != 1f || state.lipWidthScale != 1f || state.lipFrontExtendScale != 1f);
            boolean limoActive = !Float.isNaN(state.compiledModel.bodyCenterZ) && state.limoStretchCm != 0f;
            boolean widthActive = !Float.isNaN(state.compiledModel.bodyCenterX) && state.widthStretchCm != 0f;
            if (!lipActive && !limoActive && !widthActive) {
                return NONE;
            }

            float limoAnchorZ = state.compiledModel.bodyCenterZ + state.limoCenterOffsetCm / 100f;

            float widthAnchorX = state.compiledModel.bodyCenterX + state.widthCenterOffsetCm / 100f;
            return new BodyDeform(true, state.compiledModel.bodyMinY,
                    state.lipStretchScale, state.lipWidthScale, state.lipBandHeightCm / 100f, state.lipFrontExtendScale,
                    limoAnchorZ, state.limoStretchCm / 100f, state.limoZoneCm / 100f,
                    widthAnchorX, state.widthStretchCm / 100f, state.widthZoneCm / 100f);
        }
    }

    private static boolean usesTriangles(CompiledCarModel.TextureGroup group) {
        return !group.translucent && LegacyShaderFix.isAvailable();
    }

    private static final ResourceLocation[] METAL_PAINT_TEXTURES = {
            AiObjectMod.id("textures/entity/paint/chrome.png"),
            AiObjectMod.id("textures/entity/paint/gold.png"),
            AiObjectMod.id("textures/entity/paint/metallic_silver.png")
    };

    private static final ResourceLocation BRONZE_PAINT_TEXTURE = AiObjectMod.id("textures/entity/paint/bronze.png");

    private static ResourceLocation metalPaintTexture(CompiledCarModel.TextureGroup group, ResourceLocation texture, boolean usesPaintMask, int color) {
        if (!group.tintable || usesPaintMask || !CompiledCarModel.FALLBACK_TEXTURE.equals(texture)) {
            return texture;
        }
        int metal = com.examplebett.client.gui.CarColorPalette.metalIndex(color);
        if (metal >= 0) {
            return METAL_PAINT_TEXTURES[metal];
        }
        return (color & 0xFFFFFF) == com.examplebett.client.gui.CarColorPalette.EXTRA_RGB[com.examplebett.client.gui.CarColorPalette.EXTRA_BRONZE]
                ? BRONZE_PAINT_TEXTURE
                : texture;
    }

    private static RenderType groupRenderType(CompiledCarModel.TextureGroup group, ResourceLocation texture) {
        if (usesTriangles(group)) {
            return LegacyShaderFix.cutoutType(texture, group.noCull);
        }
        return group.translucent
                ? RenderType.itemEntityTranslucentCull(texture)
                : group.noCull
                        ? RenderType.entityCutoutNoCull(texture)
                        : RenderType.entityCutout(texture);
    }

    private void renderGroup(float[] data, PoseStack.Pose pose, VertexConsumer vertexConsumer, int packedLight, int color, BodyDeform deform, boolean triangles) {
        float tintR = ((color >> 16) & 0xFF) / 255f;
        float tintG = ((color >> 8) & 0xFF) / 255f;
        float tintB = (color & 0xFF) / 255f;
        if (triangles) {

            LegacyShaderFix.markBuffer(vertexConsumer);
        }

        int legacyStart = LegacyShaderFix.legacyBegin(vertexConsumer);
        for (int i = 0; i < data.length; i += 36) {
            addVertex(vertexConsumer, pose, data, i, packedLight, tintR, tintG, tintB, deform);
            addVertex(vertexConsumer, pose, data, i + 12, packedLight, tintR, tintG, tintB, deform);
            addVertex(vertexConsumer, pose, data, i + 24, packedLight, tintR, tintG, tintB, deform);
            if (!triangles) {
                addVertex(vertexConsumer, pose, data, i + 24, packedLight, tintR, tintG, tintB, deform);
            }
        }
        LegacyShaderFix.legacyEnd(vertexConsumer, legacyStart, data, pose, triangles);
    }

    private static float lipWeight(float y, float bodyMinY, float bandHeight) {
        if (bandHeight <= 0f) return 0f;
        float t = (bodyMinY + bandHeight - y) / bandHeight;
        t = Math.max(0f, Math.min(1f, t));
        return t * t * (3f - 2f * t);
    }

    private static float limoWeight(float z, float anchorZ, float zoneM) {
        if (zoneM <= 0f) return 0f;
        float t = (anchorZ - z) / zoneM;
        t = Math.max(0f, Math.min(1f, t));
        return t * t * (3f - 2f * t);
    }

    private static float limoShift(float z, float anchorZ, float zoneM, float stretchM) {
        return -limoWeight(z, anchorZ, zoneM) * stretchM;
    }

    private static float widthWeight(float x, float anchorX, float zoneM) {
        if (zoneM <= 0f) return 0f;
        float t = (anchorX - x) / zoneM;
        t = Math.max(0f, Math.min(1f, t));
        return t * t * (3f - 2f * t);
    }

    private static float widthShift(float x, float anchorX, float zoneM, float stretchM) {
        return -widthWeight(x, anchorX, zoneM) * stretchM;
    }

    private static float limoShiftFor(CarRenderState state, float z) {
        if (Float.isNaN(state.compiledModel.bodyCenterZ) || state.limoStretchCm == 0f) {
            return 0f;
        }
        float anchorZ = state.compiledModel.bodyCenterZ + state.limoCenterOffsetCm / 100f;
        return limoShift(z, anchorZ, state.limoZoneCm / 100f, state.limoStretchCm / 100f);
    }

    private static float widthShiftFor(CarRenderState state, float x) {
        if (Float.isNaN(state.compiledModel.bodyCenterX) || state.widthStretchCm == 0f) {
            return 0f;
        }
        float anchorX = state.compiledModel.bodyCenterX + state.widthCenterOffsetCm / 100f;
        return widthShift(x, anchorX, state.widthZoneCm / 100f, state.widthStretchCm / 100f);
    }

    private void addVertex(VertexConsumer consumer, PoseStack.Pose pose, float[] data, int offset, int packedLight, float tintR, float tintG, float tintB,
                            BodyDeform deform) {
        float x = data[offset];
        float y = data[offset + 1];
        float z = data[offset + 2];
        if (deform.active()) {
            float weight = lipWeight(y, deform.bodyMinY(), deform.lipBandHeight());
            if (weight > 0f) {
                y -= weight * (deform.lipStretchScale() - 1f) * deform.lipBandHeight();
                x += weight * x * (deform.lipWidthScale() - 1f);

                z += weight * Math.max(z, 0f) * (deform.lipFrontExtendScale() - 1f);
            }
            z += limoShift(z, deform.limoAnchorZ(), deform.limoZoneM(), deform.limoStretchM());
            x += widthShift(x, deform.widthAnchorX(), deform.widthZoneM(), deform.widthStretchM());
        }

        consumer.vertex(pose.pose(), x, y, z)
                .color(data[offset + 5] * tintR, data[offset + 6] * tintG, data[offset + 7] * tintB, data[offset + 8])
                .uv(data[offset + 3], data[offset + 4])
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(pose.normal(), data[offset + 9], data[offset + 10], data[offset + 11])
                .endVertex();
    }

    private static final float REFLECTION_STRENGTH = 0.45f;

    private static final float REFLECTION_ANGLE_LOW = 0.85f;
    private static final float REFLECTION_ANGLE_HIGH = 0.99f;

    private static final float REFLECTION_NORMAL_OFFSET = 0.001f;

    private static float fakeReflectionBoost(PoseStack.Pose pose, float x, float y, float z, float nx, float ny, float nz, int packedLight) {
        com.mojang.math.Vector3f normal = new com.mojang.math.Vector3f(nx, ny, nz);
        normal.transform(pose.normal());
        if (normal.dot(normal) < 1e-8f) {
            return 0f;
        }
        normal.normalize();

        com.mojang.math.Vector4f viewDirH = new com.mojang.math.Vector4f(x, y, z, 1f);
        viewDirH.transform(pose.pose());
        com.mojang.math.Vector3f viewDir = new com.mojang.math.Vector3f(viewDirH.x(), viewDirH.y(), viewDirH.z());
        if (viewDir.dot(viewDir) < 1e-8f) {
            return 0f;
        }
        viewDir.normalize();

        float vDotN = viewDir.dot(normal);
        com.mojang.math.Vector3f reflectDir = normal.copy();
        reflectDir.mul(-2f * vDotN);
        reflectDir.add(viewDir);

        float t = (reflectDir.y() - REFLECTION_ANGLE_LOW) / (REFLECTION_ANGLE_HIGH - REFLECTION_ANGLE_LOW);
        t = Math.max(0f, Math.min(1f, t));
        float highlight = t * t * (3f - 2f * t);

        int blockLight = packedLight & 0xFFFF;
        int skyLight = (packedLight >> 16) & 0xFFFF;
        float lightFactor = Math.max(blockLight, skyLight) / 240f;

        return highlight * lightFactor * REFLECTION_STRENGTH;
    }

    private void renderReflectionOverlay(float[] data, PoseStack.Pose pose, VertexConsumer vertexConsumer, int packedLight, BodyDeform deform) {
        for (int i = 0; i < data.length; i += 36) {
            addReflectionVertex(vertexConsumer, pose, data, i, packedLight, deform);
            addReflectionVertex(vertexConsumer, pose, data, i + 12, packedLight, deform);
            addReflectionVertex(vertexConsumer, pose, data, i + 24, packedLight, deform);
            addReflectionVertex(vertexConsumer, pose, data, i + 24, packedLight, deform);
        }
    }

    private void addReflectionVertex(VertexConsumer consumer, PoseStack.Pose pose, float[] data, int offset, int packedLight, BodyDeform deform) {
        float x = data[offset];
        float y = data[offset + 1];
        float z = data[offset + 2];
        if (deform.active()) {
            float weight = lipWeight(y, deform.bodyMinY(), deform.lipBandHeight());
            if (weight > 0f) {
                y -= weight * (deform.lipStretchScale() - 1f) * deform.lipBandHeight();
                x += weight * x * (deform.lipWidthScale() - 1f);
                z += weight * Math.max(z, 0f) * (deform.lipFrontExtendScale() - 1f);
            }
            z += limoShift(z, deform.limoAnchorZ(), deform.limoZoneM(), deform.limoStretchM());
            x += widthShift(x, deform.widthAnchorX(), deform.widthZoneM(), deform.widthStretchM());
        }

        float nx = data[offset + 9];
        float ny = data[offset + 10];
        float nz = data[offset + 11];
        float boost = fakeReflectionBoost(pose, x, y, z, nx, ny, nz, packedLight);
        x += nx * REFLECTION_NORMAL_OFFSET;
        y += ny * REFLECTION_NORMAL_OFFSET;
        z += nz * REFLECTION_NORMAL_OFFSET;

        consumer.vertex(pose.pose(), x, y, z)
                .color(1f, 1f, 1f, boost)
                .uv(data[offset + 3], data[offset + 4])
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(pose.normal(), nx, ny, nz)
                .endVertex();
    }
}
*/
//?}
//? if <1.16 && >=1.15 {
/*
//~ axis_mulpose_compat
package com.examplebett.client.render;

import com.examplebett.AiObjectMod;
import com.examplebett.entity.CarEntity;
import com.examplebett.render.CompiledCarModel;
import com.examplebett.render.ObjModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CarEntityRenderer extends EntityRenderer<CarEntity> {

    private static final double LOD_WHEEL_DISTANCE = 32.0;
    private static final double LOD_WHEEL_DISTANCE_SQ = LOD_WHEEL_DISTANCE * LOD_WHEEL_DISTANCE;

    private static final double BRAND_LOGO_FLAT_DISTANCE = 5.0;
    private static final double BRAND_LOGO_FLAT_DISTANCE_SQ = BRAND_LOGO_FLAT_DISTANCE * BRAND_LOGO_FLAT_DISTANCE;
    private static final double BRAND_LOGO_HIDE_DISTANCE = 48.0;
    private static final double BRAND_LOGO_HIDE_DISTANCE_SQ = BRAND_LOGO_HIDE_DISTANCE * BRAND_LOGO_HIDE_DISTANCE;

    private static final boolean[] WHEEL_IS_FRONT_4 = {true, true, false, false};
    private static final boolean[] WHEEL_IS_FRONT_6 = {true, true, false, false, false, false};
    private static final float[] WHEEL_CAMBER_SIGN_4 = {1f, -1f, 1f, -1f};
    private static final float[] WHEEL_CAMBER_SIGN_6 = {1f, -1f, 1f, -1f, 1f, -1f};

    private static final boolean[] WHEEL_IS_FRONT_8 = {true, true, false, false, false, false, false, false};
    private static final float[] WHEEL_CAMBER_SIGN_8 = {1f, -1f, 1f, -1f, 1f, -1f, 1f, -1f};

    private static final boolean[] WHEEL_IS_FRONT_2 = {true, false};
    private static final float[] WHEEL_CAMBER_SIGN_2 = {1f, -1f};
    private static final float[] WHEEL_OFFSET_Z_BUF = new float[8];
    private static final float[] WHEEL_OFFSET_X_BUF = new float[8];

    private static final Map<String, Set<String>> ENABLED_IDS_CACHE = new HashMap<>();

    private static Set<String> parseEnabledIds(String csv) {
        return ENABLED_IDS_CACHE.computeIfAbsent(csv, s -> new HashSet<>(java.util.Arrays.asList(s.split(","))));
    }

    private static final float WHEELIE_PIVOT_Z_FACTOR = 0.85f;

    private static final int BLINK_ON_TICKS = 10;
    private static final int BLINK_OFF_TICKS = 10;

    @FunctionalInterface
    private interface GeometryRenderer {
        void render(PoseStack.Pose pose, VertexConsumer consumer);
    }

    @FunctionalInterface
    private interface ModelDrawer {
        void draw(PoseStack poseStack, VertexConsumer consumer);
    }

    @FunctionalInterface
    private interface TextWrapper {
        VertexConsumer wrap(VertexConsumer out, PoseStack.Pose pose);
    }

    @FunctionalInterface
    private interface GeometrySubmitter {
        void submitCustomGeometry(PoseStack pose, RenderType type, GeometryRenderer renderer);

        default void submitWrappedText(PoseStack pose, String text, float x, float y, int light,
                                       TextWrapper wrapper) {
            submitText(pose, text, x, y, 0xFFFFFFFF, light);
        }

        default void submitModel(PoseStack pose, RenderType type, ModelDrawer drawer) {

            throw new UnsupportedOperationException();
        }

        default GeometrySubmitter late() {
            return this;
        }

        default void submitText(PoseStack pose, String text, float x, float y, int argb, int light) {
        }
    }

    private static GeometrySubmitter immediateSubmitter(MultiBufferSource bufferSource) {
        return new GeometrySubmitter() {
            @Override
            public void submitCustomGeometry(PoseStack pose, RenderType type, GeometryRenderer renderer) {
                renderer.render(pose.last(), bufferSource.getBuffer(type));
            }

            @Override
            public void submitModel(PoseStack pose, RenderType type, ModelDrawer drawer) {
                drawer.draw(pose, bufferSource.getBuffer(type));
            }

            @Override
            public void submitText(PoseStack pose, String text, float x, float y, int argb, int light) {
                com.examplebett.compat.LegacyGui.currentFont().drawInBatch(text, x, y, argb, false, pose.last().pose(), bufferSource,
                        false, 0, light);
            }

            @Override
            public void submitWrappedText(PoseStack pose, String text, float x, float y, int light,
                                          TextWrapper wrapper) {
                PoseStack.Pose last = pose.last();
                com.examplebett.compat.LegacyGui.currentFont().drawInBatch(text, x, y, 0xFFFFFFFF, false, PoseMatrices.identityMatrix(),
                        type -> wrapper.wrap(bufferSource.getBuffer(type), last), false, 0, light);
            }
        };
    }

    private final FigureModels figures;

    public CarEntityRenderer(net.minecraft.client.renderer.entity.EntityRenderDispatcher context) {
        super(context);
        this.figures = new FigureModels(context);
    }

    @Override
    public ResourceLocation getTextureLocation(CarEntity entity) {
        return CompiledCarModel.FALLBACK_TEXTURE;
    }

    private void populateCarState(CarEntity entity, CarRenderState state, float partialTick) {
        state.lightCoords = this.getPackedLightCoords(entity, partialTick);

        state.yRot = entity.getYRot();

        float[] railwayOverride = entity.mineturismo$computeRailwayRenderOverride(partialTick);
        state.railwayPositionOverride = railwayOverride != null;
        if (railwayOverride != null) {
            state.railwayPosDeltaX = railwayOverride[0];
            state.railwayPosDeltaY = railwayOverride[1];
            state.railwayPosDeltaZ = railwayOverride[2];
            state.yRot = railwayOverride[3];
        }
        state.tumblePitchDegrees = entity.getTumblePitchDegrees();
        state.tumbleRollDegrees = entity.getTumbleRollDegrees();
        state.tumblePivotHeight = (float) (entity.getScaledHitboxHeight() / 2.0);
        state.wheeliePitchDegrees = entity.getWheeliePitchDegrees();

        state.wheeliePivotZ = -entity.getFootprintHalfLength() * WHEELIE_PIVOT_Z_FACTOR;
        state.tiltRollDegrees = entity.getTiltRollDegrees();
        state.tiltPitchDegrees = entity.getTiltPitchDegrees();
        state.tiltHeightCm = entity.getTiltHeightCm();
        state.suspensionPitchDegrees = entity.getSuspensionPitchDegrees();
        state.suspensionRollDegrees = entity.getSuspensionRollDegrees();
        state.bikeLeanRollDegrees = entity.getBikeLeanRollDegrees();
        state.showFakeRider = entity.shouldRenderFakeTrafficRider();
        if (state.showFakeRider) {
            state.fakeRiderUseAlex = (entity.getUUID().hashCode() & 1) == 0;
            state.fakeRiderX = entity.getPassengerXOffset();

            state.fakeRiderY = entity.getFakeRiderYOffset();
            state.fakeRiderZ = entity.getPassengerZOffset();
            state.fakeRiderIsMotorcycle = entity.hasVehicleCategory(com.examplebett.entity.VehicleCategory.MOTORCYCLE);
            state.fakeRiderBikeStationary = entity.isBikeStationary();
        }
        state.useLodWheel = this.entityRenderDispatcher.distanceToSqr(entity) > LOD_WHEEL_DISTANCE_SQ;

        state.wheelRotationDegrees = state.useLodWheel ? 0f : entity.getWheelRotationDegrees(partialTick);
        state.rearWheelExtraSpinDegrees = state.useLodWheel ? 0f : entity.getRearWheelExtraSpinDegrees(partialTick);
        state.frontWheelSteerDegrees = entity.getFrontWheelSteerDegrees(partialTick);
        state.rotorRotationDegrees = entity.getRotorRotationDegrees(partialTick);
        state.compiledModel = entity.getCompiledModel();
        state.wheelCompiledModel = entity.getWheelCompiledModel();
        state.wheelCompiledModelMirrored = entity.getWheelCompiledModelMirrored();
        state.wheelAttachment = entity.getWheelAttachment();
        state.wheelFrontZOffset = entity.getWheelFrontZOffset();
        state.wheelRearZOffset = entity.getWheelRearZOffset();
        state.wheelTrackHalfWidthOffset = entity.getWheelTrackHalfWidthOffset();
        state.wheelAxleHeightOffset = entity.getWheelAxleHeightOffset();
        state.extraRearAxleEnabled = entity.isExtraRearAxleEnabled();
        state.extraRearAxleZOffset = entity.getExtraRearAxleZOffset();
        state.extraRearAxle2Enabled = entity.isExtraRearAxle2Enabled();
        state.extraRearAxle2ZOffset = entity.getExtraRearAxle2ZOffset();
        state.bodyHeightOffset = entity.getBodyHeightOffset();
        state.wheelCamberDegrees = entity.getWheelCamberDegrees();
        state.wheelScale = entity.getWheelScale();
        state.wheelWidthScale = entity.getWheelWidthScale();
        state.lipStretchScale = entity.getLipStretchScale();
        state.lipWidthScale = entity.getLipWidthScale();
        state.lipBandHeightCm = entity.getLipBandHeightCm();
        state.lipFrontExtendScale = entity.getLipFrontExtendScale();
        state.limoStretchCm = entity.getLimoStretchCm();
        state.limoZoneCm = entity.getLimoZoneCm();
        state.limoCenterOffsetCm = entity.getLimoCenterOffsetCm();
        state.widthStretchCm = entity.getWidthStretchCm();
        state.widthZoneCm = entity.getWidthZoneCm();
        state.widthCenterOffsetCm = entity.getWidthCenterOffsetCm();
        state.carScale = entity.getCarScale();
        state.color = entity.getColor();

        boolean isMotorcycleNoFrontPlate = entity.hasVehicleCategory(com.examplebett.entity.VehicleCategory.MOTORCYCLE);
        state.frontPlateTexture = isMotorcycleNoFrontPlate ? null : PlateTextures.texture(entity.getFrontPlate());
        state.frontPlateAspect = PlateTextures.aspect(entity.getFrontPlate());
        var frontOffset = entity.getFrontPlateOffset();
        state.frontPlateX = (float) frontOffset.x;
        state.frontPlateY = (float) frontOffset.y;
        state.frontPlateZ = (float) frontOffset.z;
        state.frontPlateScale = entity.getFrontPlateScale();
        state.frontPlateYaw = entity.getFrontPlateYaw();
        state.frontPlatePitch = entity.getFrontPlatePitch();
        state.frontPlateRoll = entity.getFrontPlateRoll();

        state.rearPlateTexture = PlateTextures.texture(entity.getRearPlate());
        state.rearPlateAspect = PlateTextures.aspect(entity.getRearPlate());
        var rearOffset = entity.getRearPlateOffset();
        state.rearPlateX = (float) rearOffset.x;
        state.rearPlateY = (float) rearOffset.y;
        state.rearPlateZ = (float) rearOffset.z;
        state.rearPlateScale = entity.getRearPlateScale();
        state.rearPlateYaw = entity.getRearPlateYaw();
        state.rearPlatePitch = entity.getRearPlatePitch();
        state.rearPlateRoll = entity.getRearPlateRoll();

        state.lightsOn = entity.areLightsOn();

        state.headlightHidden = entity.hasVehicleCategory(com.examplebett.entity.VehicleCategory.TRAILER);
        state.lightsBlinking = entity.isLightBlinkEnabled();
        state.age = entity.tickCount;
        var headlightOffset = entity.getHeadlightOffset();
        state.headlightX = (float) headlightOffset.x;
        state.headlightY = (float) headlightOffset.y;
        state.headlightZ = (float) headlightOffset.z;
        var taillightOffset = entity.getTaillightOffset();
        state.taillightX = (float) taillightOffset.x;
        state.taillightY = (float) taillightOffset.y;
        state.taillightZ = (float) taillightOffset.z;
        var headlightColor = com.examplebett.entity.HeadlightColors.byId(entity.resolveHeadlightColorId());
        state.headlightColorR = headlightColor.r();
        state.headlightColorG = headlightColor.g();
        state.headlightColorB = headlightColor.b();

        state.rearWingEnabledIds = entity.getRearWingEnabledIds();
        state.rearWingTransformsRaw = entity.getRearWingTransformsRaw();

        state.otherPartEnabledIds = entity.getOtherPartEnabledIds();
        state.otherPartTransformsRaw = entity.getOtherPartTransformsRaw();

        state.textPartsRaw = entity.getTextPartsRaw();
        state.underNeonRaw = entity.getUnderNeonRaw();
        state.brandLogosRaw = entity.getBrandLogosRaw();
        double brandLogoDistanceSq = this.entityRenderDispatcher.distanceToSqr(entity);
        state.brandLogoDetail = brandLogoDistanceSq > BRAND_LOGO_HIDE_DISTANCE_SQ ? 0
                : brandLogoDistanceSq > BRAND_LOGO_FLAT_DISTANCE_SQ ? 1 : 2;

        state.frontBrakeCaliperEnabledIds = entity.getFrontBrakeCaliperEnabledIds();
        state.frontBrakeCaliperTransformsRaw = entity.getFrontBrakeCaliperTransformsRaw();

        state.rearBrakeCaliperEnabledIds = entity.getRearBrakeCaliperEnabledIds();
        state.rearBrakeCaliperTransformsRaw = entity.getRearBrakeCaliperTransformsRaw();

        state.exhaustEnabledId = entity.getExhaustEnabledIds();
        if (state.exhaustEnabledId.isEmpty()) {
            state.exhaustTarget = null;
            entity.clientExhaustOutlets = null;
        } else {
            state.exhaustTarget = entity;
            state.exhaustTransform = entity.getExhaustTransform(state.exhaustEnabledId);
        }

        state.towSelf = null;
        state.towComputeFront = false;
        state.towComputeRear = false;
        state.towRopeEnd = null;
        long towNow = entity.level().getGameTime();
        int towLeaderId = entity.getTowLeaderEntityId();
        if (towLeaderId != -1 && entity.level().getEntity(towLeaderId) instanceof CarEntity towLeader) {
            towLeader.clientTowRearHookWantedTime = towNow;
            state.towSelf = entity;
            state.towComputeFront = true;
            float[] leaderRear = towLeader.clientTowRearHook;
            if (leaderRear != null) {
                net.minecraft.world.phys.Vec3 leaderPos = com.examplebett.compat.ModVec3.position(towLeader, partialTick);
                net.minecraft.world.phys.Vec3 selfPos = com.examplebett.compat.ModVec3.position(entity, partialTick);
                state.towRopeEnd = new float[]{
                        (float) (leaderPos.x - selfPos.x) + leaderRear[0],
                        (float) (leaderPos.y - selfPos.y) + leaderRear[1],
                        (float) (leaderPos.z - selfPos.z) + leaderRear[2]};
            }
        } else {
            entity.clientTowFrontHook = null;
        }
        if (towNow - entity.clientTowRearHookWantedTime <= 2) {
            state.towSelf = entity;
            state.towComputeRear = true;
        }

        state.policeLightEnabledIds = entity.getPoliceLightEnabledIds();
        state.policeLightTransformsRaw = entity.getPoliceLightTransformsRaw();
        state.policeLightsOn = entity.isPoliceLightsOn();

        state.taxiSignEnabledIds = entity.getTaxiSignEnabledIds();
        state.taxiSignTransformsRaw = entity.getTaxiSignTransformsRaw();
    }

    @Override
    public void render(CarEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int light) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, light);
        CarRenderState state = new CarRenderState();
        populateCarState(entity, state, partialTick);
        state.isInvisible = entity.isInvisible();
        state.lightCoords = light;
        renderCar(state, poseStack, immediateSubmitter(bufferSource));
    }

    private void renderCar(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector) {

        if (state.isInvisible) {
            return;
        }

        if (state.compiledModel == null) {
            return;
        }

        float[] exhaustOrigin = (state.exhaustTarget != null || state.towSelf != null) ? PoseMatrices.capture(poseStack) : null;

        poseStack.pushPose();
        if (state.railwayPositionOverride) {

            poseStack.translate(state.railwayPosDeltaX, state.railwayPosDeltaY, state.railwayPosDeltaZ);
        }
        poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(state.yRot));

        boolean tumbling = state.tumblePitchDegrees != 0f || state.tumbleRollDegrees != 0f;
        if (tumbling) {
            poseStack.translate(0.0, state.tumblePivotHeight, 0.0);
        }
        if (state.tumblePitchDegrees != 0f) {
            poseStack.mulPose(com.mojang.math.Vector3f.XP.rotationDegrees(state.tumblePitchDegrees));
        }
        if (state.tumbleRollDegrees != 0f) {
            poseStack.mulPose(com.mojang.math.Vector3f.ZP.rotationDegrees(state.tumbleRollDegrees));
        }
        if (tumbling) {
            poseStack.translate(0.0, -state.tumblePivotHeight, 0.0);
        }

        boolean wheelieActive = state.wheeliePitchDegrees != 0f;
        if (wheelieActive) {
            poseStack.translate(0.0, 0.0, state.wheeliePivotZ);
            poseStack.mulPose(com.mojang.math.Vector3f.XP.rotationDegrees(-state.wheeliePitchDegrees));
            poseStack.translate(0.0, 0.0, -state.wheeliePivotZ);
        }

        boolean tilting = state.tiltPitchDegrees != 0f || state.tiltRollDegrees != 0f;
        if (tilting) {
            poseStack.translate(0.0, state.tumblePivotHeight, 0.0);
        }
        if (state.tiltPitchDegrees != 0f) {
            poseStack.mulPose(com.mojang.math.Vector3f.XP.rotationDegrees(state.tiltPitchDegrees));
        }
        if (state.tiltRollDegrees != 0f) {
            poseStack.mulPose(com.mojang.math.Vector3f.ZP.rotationDegrees(state.tiltRollDegrees));
        }
        if (tilting) {
            poseStack.translate(0.0, -state.tumblePivotHeight, 0.0);
        }
        if (state.tiltHeightCm != 0f) {
            poseStack.translate(0.0, state.tiltHeightCm / 100.0, 0.0);
        }

        if (state.bikeLeanRollDegrees != 0f) {
            poseStack.translate(0.0, state.tumblePivotHeight, 0.0);
            poseStack.mulPose(com.mojang.math.Vector3f.ZP.rotationDegrees(state.bikeLeanRollDegrees));
            poseStack.translate(0.0, -state.tumblePivotHeight, 0.0);
        }

        if (state.carScale != 1f) {
            poseStack.scale(state.carScale, state.carScale, state.carScale);
        }

        poseStack.pushPose();
        poseStack.translate(0.0, state.bodyHeightOffset, 0.0);

        boolean suspensionSwaying = state.suspensionPitchDegrees != 0f || state.suspensionRollDegrees != 0f;
        if (suspensionSwaying) {
            poseStack.translate(0.0, state.tumblePivotHeight, 0.0);
        }
        if (state.suspensionPitchDegrees != 0f) {
            poseStack.mulPose(com.mojang.math.Vector3f.XP.rotationDegrees(state.suspensionPitchDegrees));
        }
        if (state.suspensionRollDegrees != 0f) {
            poseStack.mulPose(com.mojang.math.Vector3f.ZP.rotationDegrees(state.suspensionRollDegrees));
        }
        if (suspensionSwaying) {
            poseStack.translate(0.0, -state.tumblePivotHeight, 0.0);
        }

        List<CompiledCarModel.TextureGroup> reflectiveGroups = new ArrayList<>();
        float[] embeddedExtraAxleZ = embeddedExtraAxleZ(state);
        for (CompiledCarModel.TextureGroup group : state.compiledModel.groups) {

            ResourceLocation texture = group.texture;
            boolean usesPaintMask = group.tintable && PaintTintTexture.hasPaint(group.texture);
            if (usesPaintMask) {
                texture = PaintTintTexture.resolve(group.texture, state.color);
            }
            texture = metalPaintTexture(group, texture, usesPaintMask, state.color);

            final int tint = (group.tintable && !usesPaintMask) ? state.color : 0xFFFFFF;

            RenderType renderType = groupRenderType(group, texture);

            boolean spinning = group.wheelGroup >= 0 && group.wheelGroup < state.compiledModel.wheelPivots.size();
            boolean rotorSpinning = group.rotorGroup >= 0 && group.rotorGroup < state.compiledModel.rotorPivots.size();

            boolean isBodyGroup = group.wheelGroup < 0 && group.rotorGroup < 0;
            BodyDeform deform = isBodyGroup ? BodyDeform.from(state) : BodyDeform.NONE;

            boolean reflective = com.examplebett.client.AiObjectModClient.isFakeReflectionEnabled()
                    && isBodyGroup && !group.translucent;

            poseStack.pushPose();
            applyGroupTransform(poseStack, state, group, spinning, rotorSpinning, 0f);
            collector.submitCustomGeometry(poseStack, renderType, (pose, vertexConsumer) ->
                    renderGroup(group.vertexData, pose, vertexConsumer, state.lightCoords, tint, deform, usesTriangles(group)));
            poseStack.popPose();

            if (spinning && embeddedExtraAxleZ.length > 0
                    && !state.compiledModel.wheelPivots.get(group.wheelGroup).front) {
                float pivotZ = state.compiledModel.wheelPivots.get(group.wheelGroup).centerZ;
                for (float axleZ : embeddedExtraAxleZ) {
                    poseStack.pushPose();
                    applyGroupTransform(poseStack, state, group, true, false, axleZ - pivotZ);
                    collector.submitCustomGeometry(poseStack, renderType, (pose, vertexConsumer) ->
                            renderGroup(group.vertexData, pose, vertexConsumer, state.lightCoords, tint, BodyDeform.NONE, usesTriangles(group)));
                    poseStack.popPose();
                }
            }
            if (reflective) {
                reflectiveGroups.add(group);
            }
        }

        for (CompiledCarModel.TextureGroup group : reflectiveGroups) {
            boolean spinning = group.wheelGroup >= 0 && group.wheelGroup < state.compiledModel.wheelPivots.size();
            boolean rotorSpinning = group.rotorGroup >= 0 && group.rotorGroup < state.compiledModel.rotorPivots.size();
            boolean isBodyGroup = group.wheelGroup < 0 && group.rotorGroup < 0;
            BodyDeform deform = isBodyGroup ? BodyDeform.from(state) : BodyDeform.NONE;
            RenderType reflectionType = RenderType.entityNoOutline(CompiledCarModel.FALLBACK_TEXTURE);
            poseStack.pushPose();
            applyGroupTransform(poseStack, state, group, spinning, rotorSpinning, 0f);
            collector.submitCustomGeometry(poseStack, reflectionType, (pose, vertexConsumer) ->
                    renderReflectionOverlay(group.vertexData, pose, vertexConsumer, state.lightCoords, deform));
            poseStack.popPose();
        }

        if (state.frontPlateTexture != null) {
            submitPlate(poseStack, collector, state.lightCoords, state.frontPlateTexture,
                    state.frontPlateX + widthShiftFor(state, state.frontPlateX), state.frontPlateY, state.frontPlateZ + limoShiftFor(state, state.frontPlateZ), state.frontPlateScale,
                    state.frontPlateYaw, state.frontPlatePitch, state.frontPlateRoll, state.frontPlateAspect);
        }
        if (state.rearPlateTexture != null) {

            submitPlate(poseStack, collector, state.lightCoords, state.rearPlateTexture,
                    state.rearPlateX + widthShiftFor(state, state.rearPlateX), state.rearPlateY, state.rearPlateZ + limoShiftFor(state, state.rearPlateZ), state.rearPlateScale,
                    state.rearPlateYaw + 180f, state.rearPlatePitch, state.rearPlateRoll, state.rearPlateAspect);
        }

        boolean blinkPhaseOn = !state.lightsBlinking
                || (state.age % (BLINK_ON_TICKS + BLINK_OFF_TICKS)) < BLINK_ON_TICKS;
        if (state.lightsOn && blinkPhaseOn) {

            for (int side = -1; side <= 1; side += 2) {
                float headlightX = state.headlightX * side;
                float taillightX = state.taillightX * side;
                if (!state.headlightHidden) {
                    submitGlow(poseStack, collector, HEADLIGHT_GLOW_TEXTURE,
                            headlightX + widthShiftFor(state, headlightX), state.headlightY, state.headlightZ + limoShiftFor(state, state.headlightZ), HEADLIGHT_GLOW_SIZE, 0f,
                            state.headlightColorR, state.headlightColorG, state.headlightColorB);
                }
                submitGlow(poseStack, collector, TAILLIGHT_GLOW_TEXTURE,
                        taillightX + widthShiftFor(state, taillightX), state.taillightY, state.taillightZ + limoShiftFor(state, state.taillightZ), TAILLIGHT_GLOW_SIZE, 180f,
                        1f, 1f, 1f);
            }
        }

        renderRearWing(state, poseStack, collector);
        renderPoliceLight(state, poseStack, collector);
        renderTaxiSign(state, poseStack, collector);
        renderOtherPart(state, poseStack, collector);
        renderTextParts(state, poseStack, collector);
        renderBrandLogos(state, poseStack, collector);
        if (exhaustOrigin != null && state.exhaustTarget != null) {
            computeExhaustOutlets(state, poseStack, exhaustOrigin);
        }
        if (exhaustOrigin != null && state.towSelf != null) {
            computeTowHooks(state, poseStack, exhaustOrigin);
        }

        poseStack.popPose();

        renderUnderNeon(state, poseStack, collector);
        renderWheelAttachment(state, poseStack, collector);
        renderBrakeCaliper(state, poseStack, collector, true, state.frontBrakeCaliperEnabledIds, state.frontBrakeCaliperTransformsRaw);
        renderBrakeCaliper(state, poseStack, collector, false, state.rearBrakeCaliperEnabledIds, state.rearBrakeCaliperTransformsRaw);

        renderFakeRider(state, poseStack, collector);

        poseStack.popPose();

        renderTowRope(state, poseStack, collector);
    }

    private static final ResourceLocation TOW_ROPE_TEXTURE = com.examplebett.AiObjectMod.id("textures/entity/tow_rope.png");

    private static final float TOW_ROPE_HALF_THICKNESS = 0.045f;
    private static final int TOW_ROPE_SEGMENTS = 24;

    private static void computeTowHooks(CarRenderState state, PoseStack poseStack, float[] origin) {
        float[] relative = PoseMatrices.relative(origin, PoseMatrices.capture(poseStack));
        if (state.towComputeFront) {
            state.towSelf.clientTowFrontHook = PoseMatrices.transformPosition(relative, 0f, state.frontPlateY,
                    state.frontPlateZ + limoShiftFor(state, state.frontPlateZ));
        }
        if (state.towComputeRear) {
            state.towSelf.clientTowRearHook = PoseMatrices.transformPosition(relative, 0f, state.rearPlateY,
                    state.rearPlateZ + limoShiftFor(state, state.rearPlateZ));
        }
    }

    private void renderTowRope(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector) {
        if (!state.towComputeFront || state.towRopeEnd == null || state.towSelf == null || state.towSelf.clientTowFrontHook == null) {
            return;
        }
        float[] a = state.towSelf.clientTowFrontHook;
        float[] b = state.towRopeEnd;
        float dx = b[0] - a[0];
        float dy = b[1] - a[1];
        float dz = b[2] - a[2];
        float straight = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (straight < 1.0E-3f) {
            return;
        }

        float ropeLength = (float) CarEntity.TOW_ROPE_LENGTH;
        float slack = Math.max(0f, ropeLength - straight);
        float sag = 0.08f + (float) Math.sqrt(slack * (ropeLength + straight)) * 0.35f;
        int light = state.lightCoords;
        collector.submitCustomGeometry(poseStack, RenderType.entityCutoutNoCull(TOW_ROPE_TEXTURE), (pose, consumer) -> {
            float[] prev = null;
            for (int i = 0; i <= TOW_ROPE_SEGMENTS; i++) {
                float t = (float) i / TOW_ROPE_SEGMENTS;
                float[] p = {a[0] + dx * t, a[1] + dy * t - sag * 4f * t * (1f - t), a[2] + dz * t};
                if (prev != null) {

                    float u0 = ((i - 1) % 2) * 0.5f;
                    addRopeSegment(consumer, pose, prev, p, u0, u0 + 0.5f, light);
                }
                prev = p;
            }
        });
    }

    private void addRopeSegment(VertexConsumer consumer, PoseStack.Pose pose, float[] p0, float[] p1, float u0, float u1, int light) {
        float dx = p1[0] - p0[0];
        float dy = p1[1] - p0[1];
        float dz = p1[2] - p0[2];
        float dirLen = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (dirLen < 1.0E-4f) {
            return;
        }
        dx /= dirLen;
        dy /= dirLen;
        dz /= dirLen;

        float sx = -dz;
        float sy = 0f;
        float sz = dx;
        float sideLen = (float) Math.sqrt(sx * sx + sz * sz);
        if (sideLen < 1.0E-3f) {
            sx = 1f;
            sz = 0f;
            sideLen = 1f;
        }
        sx = sx / sideLen * TOW_ROPE_HALF_THICKNESS;
        sz = sz / sideLen * TOW_ROPE_HALF_THICKNESS;

        float ux = sy * dz - sz * dy;
        float uy = sz * dx - sx * dz;
        float uz = sx * dy - sy * dx;
        float upLen = (float) Math.sqrt(ux * ux + uy * uy + uz * uz);
        if (upLen < 1.0E-8f) {
            return;
        }
        ux = ux / upLen * TOW_ROPE_HALF_THICKNESS;
        uy = uy / upLen * TOW_ROPE_HALF_THICKNESS;
        uz = uz / upLen * TOW_ROPE_HALF_THICKNESS;
        float[][] corners = {
                {sx + ux, sy + uy, sz + uz}, {-sx + ux, -sy + uy, -sz + uz},
                {-sx - ux, -sy - uy, -sz - uz}, {sx - ux, sy - uy, sz - uz}};
        for (int f = 0; f < 4; f++) {
            float[] c0 = corners[f];
            float[] c1 = corners[(f + 1) % 4];
            float v0 = f * 0.25f;
            float v1 = v0 + 0.25f;
            addPlateVertex(consumer, pose, p0[0] + c0[0], p0[1] + c0[1], p0[2] + c0[2], u0, v0, light);
            addPlateVertex(consumer, pose, p1[0] + c0[0], p1[1] + c0[1], p1[2] + c0[2], u1, v0, light);
            addPlateVertex(consumer, pose, p1[0] + c1[0], p1[1] + c1[1], p1[2] + c1[2], u1, v1, light);
            addPlateVertex(consumer, pose, p0[0] + c1[0], p0[1] + c1[1], p0[2] + c1[2], u0, v1, light);
        }
    }

    private static final float FAKE_RIDER_YAW_DEGREES = 180f;

    private static final float FAKE_RIDER_Y_ADJUST = 0.85f;

    private void renderFakeRider(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector) {
        if (!state.showFakeRider) {
            return;
        }
        RenderType renderType = RenderType.entityCutoutNoCull(FigureModels.fakeRiderTexture(state.fakeRiderUseAlex));
        poseStack.pushPose();
        poseStack.translate(state.fakeRiderX, state.fakeRiderY + FAKE_RIDER_Y_ADJUST, state.fakeRiderZ);
        poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(FAKE_RIDER_YAW_DEGREES));
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        collector.submitModel(poseStack, renderType, (modelPoseStack, vertexConsumer) ->
                this.figures.renderFakeRider(state.fakeRiderUseAlex, state.fakeRiderIsMotorcycle, state.fakeRiderBikeStationary,
                        state.bikeLeanRollDegrees, modelPoseStack, vertexConsumer, state.lightCoords));
        poseStack.popPose();
    }

    private static final float SEATED_PLAYER_SCALE = 0.9375f;

    private static final float SEATED_PLAYER_ORIGIN_HEIGHT = (12f + 2f) / 16f * SEATED_PLAYER_SCALE;

    private void renderSeatedPlayer(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector, boolean alex) {
        ResourceLocation texture = alex ? FigureModels.ALEX_SLIM_TEXTURE : FigureModels.STEVE_TEXTURE;
        poseStack.pushPose();
        poseStack.translate(0f, SEATED_PLAYER_ORIGIN_HEIGHT, 0f);
        poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(FAKE_RIDER_YAW_DEGREES));
        poseStack.scale(-SEATED_PLAYER_SCALE, -SEATED_PLAYER_SCALE, SEATED_PLAYER_SCALE);
        collector.submitModel(poseStack, RenderType.entityCutoutNoCull(texture), (modelPoseStack, vertexConsumer) ->
                this.figures.renderSeatedPlayer(alex, modelPoseStack, vertexConsumer, state.lightCoords));
        poseStack.popPose();
    }

    private static final float SITTING_WOLF_ORIGIN_HEIGHT = 1.501f;

    private void renderSittingWolf(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector) {
        poseStack.pushPose();
        poseStack.translate(0f, SITTING_WOLF_ORIGIN_HEIGHT, 0f);
        poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(FAKE_RIDER_YAW_DEGREES));
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        collector.submitModel(poseStack, RenderType.entityCutoutNoCull(FigureModels.WOLF_TEXTURE), (modelPoseStack, vertexConsumer) ->
                this.figures.renderSittingWolf(false, state.age, modelPoseStack, vertexConsumer, state.lightCoords));
        collector.submitModel(poseStack, RenderType.entityCutoutNoCull(FigureModels.WOLF_COLLAR_TEXTURE), (modelPoseStack, vertexConsumer) ->
                this.figures.renderSittingWolf(true, state.age, modelPoseStack, vertexConsumer, state.lightCoords));
        poseStack.popPose();
    }

    private record WheelPositions(int wheelCount, float axleHeight, boolean[] isFront, float[] camberSign) {
    }

    private record PartRef(String id, String modelId) {
    }

    private static boolean usesEmbeddedWheelPivots(CarRenderState state) {
        return state.wheelAttachment == null && state.compiledModel != null
                && state.compiledModel.wheelPivots.size() == 4;
    }

    private WheelPositions computeWheelPositions(CarRenderState state) {
        com.examplebett.render.WheelAttachment attachment = state.wheelAttachment;
        if (attachment == null) {
            return computeEmbeddedWheelPositions(state);
        }
        float frontZ = attachment.frontZ() + state.wheelFrontZOffset;
        float rearZ = attachment.rearZ() + state.wheelRearZOffset;
        float trackHalfWidth = attachment.trackHalfWidth() + state.wheelTrackHalfWidthOffset;
        float axleHeight = attachment.axleHeight() + state.wheelAxleHeightOffset;

        boolean hasRearAxle2 = attachment.hasRearAxle2() || state.extraRearAxleEnabled;
        float rearZ2Base = attachment.hasRearAxle2()
                ? attachment.rearZ2()
                : attachment.rearZ() - CarEntity.DEFAULT_EXTRA_REAR_AXLE_SPACING;
        float rearZ2 = hasRearAxle2 ? rearZ2Base + state.wheelRearZOffset + state.extraRearAxleZOffset : 0f;

        boolean hasRearAxle3 = state.extraRearAxle2Enabled;
        float rearZ3 = hasRearAxle3
                ? rearZ2Base - CarEntity.DEFAULT_EXTRA_REAR_AXLE_SPACING + state.wheelRearZOffset + state.extraRearAxle2ZOffset
                : 0f;

        frontZ += limoShiftFor(state, frontZ);
        rearZ += limoShiftFor(state, rearZ);
        if (hasRearAxle2) {
            rearZ2 += limoShiftFor(state, rearZ2);
        }
        if (hasRearAxle3) {
            rearZ3 += limoShiftFor(state, rearZ3);
        }

        float trackRightX = trackHalfWidth + widthShiftFor(state, trackHalfWidth);
        float trackLeftX = -trackHalfWidth + widthShiftFor(state, -trackHalfWidth);

        boolean singleWheelPerAxle = attachment.singleWheelPerAxle();

        int wheelCount = singleWheelPerAxle ? 2 : (hasRearAxle2 && hasRearAxle3 ? 8 : (hasRearAxle2 || hasRearAxle3 ? 6 : 4));
        float[] offsetZ = WHEEL_OFFSET_Z_BUF;
        float[] offsetX = WHEEL_OFFSET_X_BUF;
        if (singleWheelPerAxle) {
            offsetZ[0] = frontZ; offsetZ[1] = rearZ;
            offsetX[0] = 0f; offsetX[1] = 0f;
        } else {
            offsetZ[0] = frontZ; offsetZ[1] = frontZ; offsetZ[2] = rearZ; offsetZ[3] = rearZ;
            offsetX[0] = trackRightX; offsetX[1] = trackLeftX; offsetX[2] = trackRightX; offsetX[3] = trackLeftX;
            if (hasRearAxle2) {
                offsetZ[4] = rearZ2; offsetZ[5] = rearZ2;
                offsetX[4] = trackRightX; offsetX[5] = trackLeftX;
                if (hasRearAxle3) {
                    offsetZ[6] = rearZ3; offsetZ[7] = rearZ3;
                    offsetX[6] = trackRightX; offsetX[7] = trackLeftX;
                }
            } else if (hasRearAxle3) {
                offsetZ[4] = rearZ3; offsetZ[5] = rearZ3;
                offsetX[4] = trackRightX; offsetX[5] = trackLeftX;
            }
        }
        boolean[] isFront = singleWheelPerAxle ? WHEEL_IS_FRONT_2
                : (wheelCount == 8 ? WHEEL_IS_FRONT_8 : (wheelCount == 6 ? WHEEL_IS_FRONT_6 : WHEEL_IS_FRONT_4));

        float[] camberSign = singleWheelPerAxle ? WHEEL_CAMBER_SIGN_2
                : (wheelCount == 8 ? WHEEL_CAMBER_SIGN_8 : (wheelCount == 6 ? WHEEL_CAMBER_SIGN_6 : WHEEL_CAMBER_SIGN_4));
        return new WheelPositions(wheelCount, axleHeight, isFront, camberSign);
    }

    private static final float[] NO_EXTRA_AXLES = new float[0];

    private static float[] embeddedExtraAxleZ(CarRenderState state) {
        if ((!state.extraRearAxleEnabled && !state.extraRearAxle2Enabled) || !usesEmbeddedWheelPivots(state)) {
            return NO_EXTRA_AXLES;
        }
        float rearZ = 0f;
        int rearCount = 0;
        for (ObjModel.WheelPivot pivot : state.compiledModel.wheelPivots) {
            if (!pivot.front) {
                rearZ += pivot.centerZ;
                rearCount++;
            }
        }
        if (rearCount == 0) {
            return NO_EXTRA_AXLES;
        }
        rearZ /= rearCount;
        float rearZ2Base = rearZ - CarEntity.DEFAULT_EXTRA_REAR_AXLE_SPACING;
        float rearZ2 = rearZ2Base + state.extraRearAxleZOffset;
        float rearZ3 = rearZ2Base - CarEntity.DEFAULT_EXTRA_REAR_AXLE_SPACING + state.extraRearAxle2ZOffset;
        if (state.extraRearAxleEnabled && state.extraRearAxle2Enabled) {
            return new float[] { rearZ2, rearZ3 };
        }
        return new float[] { state.extraRearAxleEnabled ? rearZ2 : rearZ3 };
    }

    private WheelPositions computeEmbeddedWheelPositions(CarRenderState state) {
        if (!usesEmbeddedWheelPivots(state)) {
            return new WheelPositions(0, 0f, WHEEL_IS_FRONT_4, WHEEL_CAMBER_SIGN_4);
        }
        List<ObjModel.WheelPivot> pivots = state.compiledModel.wheelPivots;
        ObjModel.WheelPivot[] front = new ObjModel.WheelPivot[2];
        ObjModel.WheelPivot[] rear = new ObjModel.WheelPivot[2];
        int frontCount = 0;
        int rearCount = 0;
        float sumY = 0f;
        for (ObjModel.WheelPivot pivot : pivots) {
            if (pivot.front && frontCount < 2) {
                front[frontCount++] = pivot;
            } else if (!pivot.front && rearCount < 2) {
                rear[rearCount++] = pivot;
            }
            sumY += pivot.centerY;
        }
        if (frontCount != 2 || rearCount != 2) {
            return new WheelPositions(0, 0f, WHEEL_IS_FRONT_4, WHEEL_CAMBER_SIGN_4);
        }

        if (front[0].centerX < front[1].centerX) {
            ObjModel.WheelPivot t = front[0]; front[0] = front[1]; front[1] = t;
        }
        if (rear[0].centerX < rear[1].centerX) {
            ObjModel.WheelPivot t = rear[0]; rear[0] = rear[1]; rear[1] = t;
        }
        float[] extraAxleZ = embeddedExtraAxleZ(state);
        int wheelCount = 4 + extraAxleZ.length * 2;
        float[] offsetZ = WHEEL_OFFSET_Z_BUF;
        float[] offsetX = WHEEL_OFFSET_X_BUF;
        offsetZ[0] = embeddedWheelZ(state, front[0].centerZ, true); offsetZ[1] = embeddedWheelZ(state, front[1].centerZ, true);
        offsetZ[2] = embeddedWheelZ(state, rear[0].centerZ, false); offsetZ[3] = embeddedWheelZ(state, rear[1].centerZ, false);
        offsetX[0] = embeddedWheelX(state, front[0]); offsetX[1] = embeddedWheelX(state, front[1]);
        offsetX[2] = embeddedWheelX(state, rear[0]); offsetX[3] = embeddedWheelX(state, rear[1]);
        for (int a = 0; a < extraAxleZ.length; a++) {
            int i = 4 + a * 2;
            float axleZ = embeddedWheelZ(state, extraAxleZ[a], false);
            offsetZ[i] = axleZ; offsetZ[i + 1] = axleZ;
            offsetX[i] = offsetX[2]; offsetX[i + 1] = offsetX[3];
        }
        boolean[] isFront = wheelCount == 8 ? WHEEL_IS_FRONT_8 : (wheelCount == 6 ? WHEEL_IS_FRONT_6 : WHEEL_IS_FRONT_4);
        float[] camberSign = wheelCount == 8 ? WHEEL_CAMBER_SIGN_8 : (wheelCount == 6 ? WHEEL_CAMBER_SIGN_6 : WHEEL_CAMBER_SIGN_4);
        return new WheelPositions(wheelCount, sumY / pivots.size() + state.wheelAxleHeightOffset, isFront, camberSign);
    }

    private void renderBrakeCaliper(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector, boolean frontOnly,
                                     String enabledIdsCsv, String transformsRaw) {

        if ((state.wheelAttachment == null && !usesEmbeddedWheelPivots(state)) || enabledIdsCsv == null || enabledIdsCsv.isEmpty()) {
            return;
        }
        Set<String> enabledIds = parseEnabledIds(enabledIdsCsv);
        net.minecraft.server.packs.resources.ResourceManager resourceManager =
                net.minecraft.client.Minecraft.getInstance().getResourceManager();
        List<PartRef> parts = new ArrayList<>();
        if (frontOnly) {
            for (com.examplebett.render.FrontBrakeCaliperParts.Option option : com.examplebett.render.FrontBrakeCaliperParts.list(resourceManager)) {
                parts.add(new PartRef(option.id(), option.modelId()));
            }
        } else {
            for (com.examplebett.render.RearBrakeCaliperParts.Option option : com.examplebett.render.RearBrakeCaliperParts.list(resourceManager)) {
                parts.add(new PartRef(option.id(), option.modelId()));
            }
        }
        if (parts.isEmpty()) {
            return;
        }
        WheelPositions wp = computeWheelPositions(state);
        float[] offsetX = WHEEL_OFFSET_X_BUF;
        float[] offsetZ = WHEEL_OFFSET_Z_BUF;
        for (PartRef part : parts) {
            if (!enabledIds.contains(part.id())) {
                continue;
            }
            BrakeCaliperModel.Pair pair = BrakeCaliperModel.get(resourceManager, part.modelId());
            if (pair == null) {
                continue;
            }
            com.examplebett.render.PartTransforms.Transform6 t =
                    com.examplebett.render.PartTransforms.resolve6(transformsRaw, part.id(),
                            com.examplebett.entity.CarEntity.defaultBrakeCaliperTransform(part.id()));
            float localX = t.x();
            float localY = t.y();
            float localZ = t.z();
            float localAngle = t.angle();
            float localAngleY = t.angleY();
            float localScale = t.scale();
            for (int i = 0; i < wp.wheelCount(); i++) {
                if (wp.isFront()[i] != frontOnly) {
                    continue;
                }
                poseStack.pushPose();
                poseStack.translate(offsetX[i], wp.axleHeight(), offsetZ[i]);
                if (state.wheelCamberDegrees != 0f) {
                    poseStack.mulPose(com.mojang.math.Vector3f.ZP.rotationDegrees(wp.camberSign()[i] * state.wheelCamberDegrees));
                }
                if (frontOnly && state.frontWheelSteerDegrees != 0f) {

                    poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(state.frontWheelSteerDegrees));
                }
                boolean mirrored = offsetX[i] < 0;
                CompiledCarModel activeModel = mirrored ? pair.mirrored() : pair.normal();

                poseStack.translate(mirrored ? -localX : localX, localY, localZ);
                if (localAngleY != 0f) {
                    poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(localAngleY));
                }
                if (localAngle != 0f) {
                    poseStack.mulPose(com.mojang.math.Vector3f.XP.rotationDegrees(localAngle));
                }
                if (localScale != 1f) {
                    poseStack.scale(localScale, localScale, localScale);
                }
                for (CompiledCarModel.TextureGroup group : activeModel.groups) {
                    ResourceLocation texture = group.texture;
                    final int tint = group.tintable ? state.color : 0xFFFFFF;
                    RenderType renderType = groupRenderType(group, texture);
                    collector.submitCustomGeometry(poseStack, renderType, (pose, vertexConsumer) ->
                            renderGroup(group.vertexData, pose, vertexConsumer, state.lightCoords, tint, BodyDeform.NONE, usesTriangles(group)));
                }
                poseStack.popPose();
            }
        }
    }

    private void renderWheelAttachment(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector) {
        if (state.wheelCompiledModel == null || state.wheelAttachment == null) {
            return;
        }

        CompiledCarModel wheelModel = state.wheelCompiledModel;

        CompiledCarModel wheelModelMirrored =
                state.wheelCompiledModelMirrored != null ? state.wheelCompiledModelMirrored : wheelModel;
        if (state.useLodWheel) {
            net.minecraft.server.packs.resources.ResourceManager resourceManager =
                    net.minecraft.client.Minecraft.getInstance().getResourceManager();
            CompiledCarModel lodModel = LodWheelModel.get(resourceManager);
            if (lodModel != null) {
                wheelModel = lodModel;
                CompiledCarModel lodModelMirrored = LodWheelModel.getMirrored(resourceManager);
                wheelModelMirrored = lodModelMirrored != null ? lodModelMirrored : lodModel;
            }
        }
        WheelPositions wp = computeWheelPositions(state);
        int wheelCount = wp.wheelCount();
        float axleHeight = wp.axleHeight();
        float[] offsetZ = WHEEL_OFFSET_Z_BUF;
        float[] offsetX = WHEEL_OFFSET_X_BUF;
        boolean[] isFront = wp.isFront();
        float[] camberSign = wp.camberSign();

        for (int i = 0; i < wheelCount; i++) {
            poseStack.pushPose();
            poseStack.translate(offsetX[i], axleHeight, offsetZ[i]);

            if (state.wheelCamberDegrees != 0f) {
                poseStack.mulPose(com.mojang.math.Vector3f.ZP.rotationDegrees(camberSign[i] * state.wheelCamberDegrees));
            }
            if (isFront[i]) {
                poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(state.frontWheelSteerDegrees));
            }

            float rollDegrees = state.wheelRotationDegrees + (isFront[i] ? 0f : state.rearWheelExtraSpinDegrees);
            poseStack.mulPose(com.mojang.math.Vector3f.XP.rotationDegrees(rollDegrees));

            boolean mirrored = offsetX[i] < 0;
            CompiledCarModel activeWheelModel = mirrored ? wheelModelMirrored : wheelModel;

            float scaleX = state.wheelScale * state.wheelWidthScale;
            if (scaleX != 1f || state.wheelScale != 1f) {
                poseStack.scale(scaleX, state.wheelScale, state.wheelScale);
            }
            for (CompiledCarModel.TextureGroup group : activeWheelModel.groups) {
                ResourceLocation texture = group.texture;
                boolean usesPaintMask = group.tintable && PaintTintTexture.hasPaint(group.texture);
                if (usesPaintMask) {
                    texture = PaintTintTexture.resolve(group.texture, state.color);
                }
                texture = metalPaintTexture(group, texture, usesPaintMask, state.color);
                final int tint = (group.tintable && !usesPaintMask) ? state.color : 0xFFFFFF;
                RenderType renderType = groupRenderType(group, texture);
                collector.submitCustomGeometry(poseStack, renderType, (pose, vertexConsumer) ->
                        renderGroup(group.vertexData, pose, vertexConsumer, state.lightCoords, tint, BodyDeform.NONE, usesTriangles(group)));
            }
            poseStack.popPose();
        }
    }

    private void renderRearWing(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector) {
        if (state.rearWingEnabledIds == null || state.rearWingEnabledIds.isEmpty()) {
            return;
        }
        Set<String> enabledIds = parseEnabledIds(state.rearWingEnabledIds);
        net.minecraft.server.packs.resources.ResourceManager resourceManager =
                net.minecraft.client.Minecraft.getInstance().getResourceManager();
        com.examplebett.render.PartTransforms.Transform12 defaults = new com.examplebett.render.PartTransforms.Transform12(
                com.examplebett.entity.CarEntity.DEFAULT_REAR_WING_X, com.examplebett.entity.CarEntity.DEFAULT_REAR_WING_Y,
                com.examplebett.entity.CarEntity.DEFAULT_REAR_WING_Z, 0f, 0f, com.examplebett.entity.CarEntity.DEFAULT_REAR_WING_SCALE,
                1f, 1f, 1f, 1f, 1f, 1f);
        for (com.examplebett.render.RearWingParts.Option option : com.examplebett.render.RearWingParts.list(resourceManager)) {
            if (!enabledIds.contains(option.id())) {
                continue;
            }
            CompiledCarModel wingModel = RearWingModel.get(resourceManager, option.modelId());
            if (wingModel == null) {
                continue;
            }
            com.examplebett.render.PartTransforms.Transform12 t =
                    com.examplebett.render.PartTransforms.resolve12(state.rearWingTransformsRaw, option.id(), defaults);
            poseStack.pushPose();
            poseStack.translate(t.x() + widthShiftFor(state, t.x()), t.y(), t.z() + limoShiftFor(state, t.z()));
            if (t.angleY() != 0f) {
                poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(t.angleY()));
            }
            if (t.angle() != 0f) {
                poseStack.mulPose(com.mojang.math.Vector3f.XP.rotationDegrees(t.angle()));
            }

            float stretchScaleX = (t.stretchXPos() + t.stretchXNeg()) / 2f;
            float stretchScaleY = (t.stretchYPos() + t.stretchYNeg()) / 2f;
            float stretchScaleZ = (t.stretchZPos() + t.stretchZNeg()) / 2f;
            float offsetX = t.scale() * OTHER_PART_STRETCH_HALF_EXTENT * (t.stretchXPos() - t.stretchXNeg()) / 2f;
            float offsetY = t.scale() * OTHER_PART_STRETCH_HALF_EXTENT * (t.stretchYPos() - t.stretchYNeg()) / 2f;
            float offsetZ = t.scale() * OTHER_PART_STRETCH_HALF_EXTENT * (t.stretchZPos() - t.stretchZNeg()) / 2f;
            if (offsetX != 0f || offsetY != 0f || offsetZ != 0f) {
                poseStack.translate(offsetX, offsetY, offsetZ);
            }
            if (t.scale() != 1f || stretchScaleX != 1f || stretchScaleY != 1f || stretchScaleZ != 1f) {
                poseStack.scale(t.scale() * stretchScaleX, t.scale() * stretchScaleY, t.scale() * stretchScaleZ);
            }
            for (CompiledCarModel.TextureGroup group : wingModel.groups) {
                ResourceLocation texture = group.texture;
                final int tint = group.tintable ? state.color : 0xFFFFFF;
                RenderType renderType = groupRenderType(group, texture);
                collector.submitCustomGeometry(poseStack, renderType, (pose, vertexConsumer) ->
                        renderGroup(group.vertexData, pose, vertexConsumer, state.lightCoords, tint, BodyDeform.NONE, usesTriangles(group)));
            }
            poseStack.popPose();
        }
    }

    private static final float OTHER_PART_STRETCH_HALF_EXTENT = 0.3f;

    private void renderOtherPart(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector) {
        if (state.otherPartEnabledIds == null || state.otherPartEnabledIds.isEmpty()) {
            return;
        }
        Set<String> enabledIds = parseEnabledIds(state.otherPartEnabledIds);
        net.minecraft.server.packs.resources.ResourceManager resourceManager =
                net.minecraft.client.Minecraft.getInstance().getResourceManager();
        for (com.examplebett.render.OtherParts.Option option : com.examplebett.render.OtherParts.list(resourceManager)) {
            if (!enabledIds.contains(option.id())) {
                continue;
            }

            boolean spareTire = com.examplebett.render.OtherParts.SPARE_TIRE_ID.equals(option.id());

            boolean seatedPlayer = com.examplebett.render.OtherParts.isSeatedCreature(option.id());
            CompiledCarModel partModel = seatedPlayer
                    ? null
                    : spareTire
                            ? state.wheelCompiledModel
                            : OtherPartModel.get(resourceManager, option.modelId());
            if (partModel == null && !seatedPlayer) {
                continue;
            }
            com.examplebett.render.PartTransforms.Transform12 t =
                    com.examplebett.render.PartTransforms.resolve12(state.otherPartTransformsRaw, option.id(),
                            com.examplebett.entity.CarEntity.defaultOtherPartTransform(option.id()));
            poseStack.pushPose();
            poseStack.translate(t.x() + widthShiftFor(state, t.x()), t.y(), t.z() + limoShiftFor(state, t.z()));
            if (t.angleY() != 0f) {
                poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(t.angleY()));
            }
            if (t.angle() != 0f) {
                poseStack.mulPose(com.mojang.math.Vector3f.XP.rotationDegrees(t.angle()));
            }

            float stretchScaleX = (t.stretchXPos() + t.stretchXNeg()) / 2f;
            float stretchScaleY = (t.stretchYPos() + t.stretchYNeg()) / 2f;
            float stretchScaleZ = (t.stretchZPos() + t.stretchZNeg()) / 2f;
            float offsetX = t.scale() * OTHER_PART_STRETCH_HALF_EXTENT * (t.stretchXPos() - t.stretchXNeg()) / 2f;
            float offsetY = t.scale() * OTHER_PART_STRETCH_HALF_EXTENT * (t.stretchYPos() - t.stretchYNeg()) / 2f;
            float offsetZ = t.scale() * OTHER_PART_STRETCH_HALF_EXTENT * (t.stretchZPos() - t.stretchZNeg()) / 2f;
            if (offsetX != 0f || offsetY != 0f || offsetZ != 0f) {
                poseStack.translate(offsetX, offsetY, offsetZ);
            }
            if (t.scale() != 1f || stretchScaleX != 1f || stretchScaleY != 1f || stretchScaleZ != 1f) {
                poseStack.scale(t.scale() * stretchScaleX, t.scale() * stretchScaleY, t.scale() * stretchScaleZ);
            }
            if (spareTire) {

                poseStack.mulPose(com.mojang.math.Vector3f.ZP.rotationDegrees(90f));
                float scaleX = state.wheelScale * state.wheelWidthScale;
                if (scaleX != 1f || state.wheelScale != 1f) {
                    poseStack.scale(scaleX, state.wheelScale, state.wheelScale);
                }
            }
            if (seatedPlayer) {
                if (com.examplebett.render.OtherParts.SITTING_WOLF_ID.equals(option.id())) {
                    renderSittingWolf(state, poseStack, collector);
                } else {
                    renderSeatedPlayer(state, poseStack, collector,
                            com.examplebett.render.OtherParts.SITTING_ALEX_ID.equals(option.id()));
                }
                poseStack.popPose();
                continue;
            }

            boolean wholeVehicle = com.examplebett.render.OtherParts.R34_ID.equals(option.id());
            int partColor = wholeVehicle ? 0xFFFFFF : state.color;
            for (CompiledCarModel.TextureGroup group : partModel.groups) {
                ResourceLocation texture = group.texture;

                boolean usesPaintMask = group.tintable && PaintTintTexture.hasPaint(group.texture);
                if (usesPaintMask) {
                    texture = PaintTintTexture.resolve(group.texture, partColor);
                }
                texture = metalPaintTexture(group, texture, usesPaintMask, partColor);
                final int tint = (group.tintable && !usesPaintMask) ? partColor : 0xFFFFFF;
                RenderType renderType = groupRenderType(group, texture);
                collector.submitCustomGeometry(poseStack, renderType, (pose, vertexConsumer) ->
                        renderGroup(group.vertexData, pose, vertexConsumer, state.lightCoords, tint, BodyDeform.NONE, usesTriangles(group)));
            }
            poseStack.popPose();
        }
    }

    private static final float TEXT_PART_PIXEL_SIZE = 0.02f;

    private static final float TEXT_PART_LAYER_SPACING = 0.004f;
    private static final int TEXT_PART_MAX_LAYERS = 24;

    private static final float TEXT_PART_SIDE_SHADE = 0.55f;
    private static final int TEXT_PART_FULL_BRIGHT = 0xF000F0;

    private static final int TEXT_PART_CACHE_LIMIT = 256;
    private static final Map<String, com.examplebett.render.TextParts.Slot[]> TEXT_PARTS_CACHE = new HashMap<>();
    private static final Map<String, String> TEXT_SEQUENCE_CACHE = new HashMap<>();

    private static com.examplebett.render.TextParts.Slot[] parseTextParts(String raw) {
        if (TEXT_PARTS_CACHE.size() > TEXT_PART_CACHE_LIMIT) {
            TEXT_PARTS_CACHE.clear();
        }
        return TEXT_PARTS_CACHE.computeIfAbsent(raw, com.examplebett.render.TextParts::parse);
    }

    private static String textPartSequence(com.examplebett.render.TextParts.Slot slot) {
        if (TEXT_SEQUENCE_CACHE.size() > TEXT_PART_CACHE_LIMIT) {
            TEXT_SEQUENCE_CACHE.clear();
        }
        return TEXT_SEQUENCE_CACHE.computeIfAbsent(slot.font() + "\n" + slot.text(), key -> {
            net.minecraft.network.chat.Component component = com.examplebett.compat.ModText.literal(slot.text()).copy();
            if (!com.examplebett.render.TextParts.FONT_DEFAULT.equals(slot.font())) {
                ResourceLocation fontId = AiObjectMod.id("text_" + slot.font());

                component = component;
            }

            return component.getString();
        });
    }

    private static final ResourceLocation UNDER_NEON_TEXTURE =
            AiObjectMod.id("textures/entity/lights/under_neon_glow.png");
    private static final Map<String, com.examplebett.render.UnderNeon.Slot[]> UNDER_NEON_CACHE = new HashMap<>();

    private void renderUnderNeon(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector) {
        if (state.underNeonRaw == null || state.underNeonRaw.isEmpty()) {
            return;
        }
        if (UNDER_NEON_CACHE.size() > TEXT_PART_CACHE_LIMIT) {
            UNDER_NEON_CACHE.clear();
        }
        com.examplebett.render.UnderNeon.Slot[] slots = UNDER_NEON_CACHE.computeIfAbsent(state.underNeonRaw, com.examplebett.render.UnderNeon::parse);
        RenderType neonType = RenderType.entityTranslucentCull(UNDER_NEON_TEXTURE);
        for (com.examplebett.render.UnderNeon.Slot slot : slots) {
            if (slot == null || !slot.enabled()) {
                continue;
            }
            float r = ((slot.color() >> 16) & 0xFF) / 255f;
            float g = ((slot.color() >> 8) & 0xFF) / 255f;
            float b = (slot.color() & 0xFF) / 255f;
            float alpha = slot.brightness();
            float hw = slot.width() / 2f;
            float hl = slot.length() / 2f;
            poseStack.pushPose();
            poseStack.translate(slot.x(), slot.y(), slot.z());
            poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(slot.yaw()));
            collector.late().submitCustomGeometry(poseStack, neonType, (pose, consumer) -> {
                addGlowVertex(consumer, pose, -hw, 0f, -hl, 0f, 0f, r, g, b, alpha);
                addGlowVertex(consumer, pose, -hw, 0f, hl, 0f, 1f, r, g, b, alpha);
                addGlowVertex(consumer, pose, hw, 0f, hl, 1f, 1f, r, g, b, alpha);
                addGlowVertex(consumer, pose, hw, 0f, -hl, 1f, 0f, r, g, b, alpha);

                addGlowVertex(consumer, pose, -hw, 0f, -hl, 0f, 0f, r, g, b, alpha);
                addGlowVertex(consumer, pose, hw, 0f, -hl, 1f, 0f, r, g, b, alpha);
                addGlowVertex(consumer, pose, hw, 0f, hl, 1f, 1f, r, g, b, alpha);
                addGlowVertex(consumer, pose, -hw, 0f, hl, 0f, 1f, r, g, b, alpha);
            });
            poseStack.popPose();
        }
    }

    private static final Map<String, com.examplebett.render.BrandLogos.Slot[]> BRAND_LOGO_CACHE = new HashMap<>();

    private static final float BRAND_LOGO_HEIGHT = 0.12f;

    private static final float BRAND_LOGO_RECESS_RATIO = 0.5f;

    private static final int BRAND_LOGO_GLINT_GRID = 16;
    private static final float BRAND_LOGO_GLINT_OFFSET = 0.0005f;
    private static final float BRAND_LOGO_GLINT_STRENGTH = 0.6f;

    private void renderBrandLogos(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector) {
        if (state.brandLogosRaw == null || state.brandLogosRaw.isEmpty() || state.brandLogoDetail == 0) {
            return;
        }
        boolean detailed = state.brandLogoDetail >= 2;
        if (BRAND_LOGO_CACHE.size() > TEXT_PART_CACHE_LIMIT) {
            BRAND_LOGO_CACHE.clear();
        }
        com.examplebett.render.BrandLogos.Slot[] slots = BRAND_LOGO_CACHE.computeIfAbsent(state.brandLogosRaw, com.examplebett.render.BrandLogos::parse);
        boolean glint = com.examplebett.client.AiObjectModClient.isFakeReflectionEnabled();
        for (com.examplebett.render.BrandLogos.Slot slot : slots) {
            if (slot == null || !slot.enabled()) {
                continue;
            }

            int light = slot.glow() ? TEXT_PART_FULL_BRIGHT : state.lightCoords;
            BrandLogoTextures.Design design = BrandLogoTextures.design(slot.logo());

            BrandLogoMesh.Mesh mesh = design.flat() ? null : BrandLogoMesh.get(design);
            float height = BRAND_LOGO_HEIGHT * slot.scale();
            float pixel = height / (mesh != null ? mesh.height() : design.height());
            float halfW = (mesh != null ? mesh.width() : design.width()) * pixel / 2f;
            float halfH = height / 2f;
            float depth = slot.depth();
            float[] levelZ = {0f, depth * (1f - BRAND_LOGO_RECESS_RATIO), depth};

            poseStack.pushPose();
            poseStack.translate(slot.x() + widthShiftFor(state, slot.x()), slot.y(), slot.z() + limoShiftFor(state, slot.z()));
            poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(slot.yaw()));
            poseStack.mulPose(com.mojang.math.Vector3f.XP.rotationDegrees(slot.pitch()));
            poseStack.mulPose(com.mojang.math.Vector3f.ZP.rotationDegrees(slot.roll()));
            if (design.flat()) {

                int layers = detailed
                        ? Math.min(TEXT_PART_MAX_LAYERS, Math.max(1, (int) Math.ceil(depth / TEXT_PART_LAYER_SPACING)))
                        : 0;
                collector.submitCustomGeometry(poseStack, RenderType.entityCutoutNoCull(design.texture()), (pose, consumer) -> {
                    for (int i = 0; i < layers; i++) {
                        addBrandLogoFace(consumer, pose, halfW, halfH, depth * i / layers, 1f, light, 1f, TEXT_PART_SIDE_SHADE);
                    }
                    addBrandLogoFace(consumer, pose, halfW, halfH, depth, 1f, light, 1f);
                });
                poseStack.popPose();
                continue;
            }
            collector.submitCustomGeometry(poseStack, RenderType.entityCutoutNoCull(design.texture()), (pose, consumer) -> {
                addBrandLogoFace(consumer, pose, halfW, halfH, levelZ[0], 1f, light, -1f);
                addBrandLogoFace(consumer, pose, halfW, halfH, levelZ[1], 1f, light, 1f);
                if (!detailed) {
                    return;
                }
                for (BrandLogoMesh.Wall wall : mesh.walls()) {
                    float x0 = -halfW + wall.x0() * pixel;
                    float y0 = halfH - wall.y0() * pixel;
                    float x1 = -halfW + wall.x1() * pixel;
                    float y1 = halfH - wall.y1() * pixel;
                    float zLow = levelZ[wall.lowLevel()];
                    float zHigh = levelZ[wall.highLevel()];

                    float nx = wall.nx();
                    float ny = -wall.ny();
                    addBrandLogoVertex(consumer, pose, x0, y0, zLow, wall.u0(), wall.v0(), light, 1f, nx, ny, 0f);
                    addBrandLogoVertex(consumer, pose, x1, y1, zLow, wall.u1(), wall.v1(), light, 1f, nx, ny, 0f);
                    addBrandLogoVertex(consumer, pose, x1, y1, zHigh, wall.u1(), wall.v1(), light, 1f, nx, ny, 0f);
                    addBrandLogoVertex(consumer, pose, x0, y0, zHigh, wall.u0(), wall.v0(), light, 1f, nx, ny, 0f);
                }
            });
            collector.submitCustomGeometry(poseStack, RenderType.entityCutoutNoCull(design.raisedTexture()), (pose, consumer) ->
                    addBrandLogoFace(consumer, pose, halfW, halfH, levelZ[2], 1f, light, 1f));
            if (glint && detailed) {
                collector.submitCustomGeometry(poseStack, RenderType.entityTranslucent(design.glintTexture()), (pose, consumer) ->
                        addBrandLogoGlint(consumer, pose, halfW, halfH, levelZ[2] + BRAND_LOGO_GLINT_OFFSET, light));
            }
            poseStack.popPose();
        }
    }

    private void addBrandLogoFace(VertexConsumer consumer, PoseStack.Pose pose, float halfW, float halfH, float z, float alpha, int light, float nz) {
        addBrandLogoFace(consumer, pose, halfW, halfH, z, alpha, light, nz, 1f);
    }

    private void addBrandLogoFace(VertexConsumer consumer, PoseStack.Pose pose, float halfW, float halfH, float z, float alpha, int light, float nz,
                                  float shade) {
        addBrandLogoVertex(consumer, pose, -halfW, -halfH, z, 0f, 1f, light, alpha, 0f, 0f, nz, shade);
        addBrandLogoVertex(consumer, pose, halfW, -halfH, z, 1f, 1f, light, alpha, 0f, 0f, nz, shade);
        addBrandLogoVertex(consumer, pose, halfW, halfH, z, 1f, 0f, light, alpha, 0f, 0f, nz, shade);
        addBrandLogoVertex(consumer, pose, -halfW, halfH, z, 0f, 0f, light, alpha, 0f, 0f, nz, shade);
    }

    private void addBrandLogoGlint(VertexConsumer consumer, PoseStack.Pose pose, float halfW, float halfH, float z, int light) {

        float[] camera = PoseMatrices.transformPosition(PoseMatrices.invert(PoseMatrices.capture(pose)), 0f, 0f, 0f);
        float toCameraX = camera[0];
        float toCameraY = camera[1];
        float toCameraZ = camera[2] - z;
        float toCameraLength = (float) Math.sqrt(toCameraX * toCameraX + toCameraY * toCameraY + toCameraZ * toCameraZ);
        if (toCameraLength < 1e-4f) {
            return;
        }
        float phase = 0.5f + 1.1f * (toCameraX / toCameraLength * 0.8f + toCameraY / toCameraLength * 0.6f);
        int blockLight = light & 0xFFFF;
        int skyLight = (light >> 16) & 0xFFFF;
        float strength = BRAND_LOGO_GLINT_STRENGTH * Math.max(blockLight, skyLight) / 240f;
        if (strength <= 0f) {
            return;
        }
        int n = BRAND_LOGO_GLINT_GRID;
        float[][] alpha = new float[n + 1][n + 1];
        boolean any = false;
        for (int j = 0; j <= n; j++) {
            for (int i = 0; i <= n; i++) {
                float u = (float) i / n;
                float v = (float) j / n;

                float s = (u * 0.8f + (1f - v) * 0.6f) / 1.4f;
                float value = Math.min(1f, glintBand(s, phase, 0.14f) + 0.6f * glintBand(s, phase - 0.24f, 0.05f)) * strength;
                alpha[j][i] = value;
                any |= value > 0.001f;
            }
        }
        if (!any) {
            return;
        }
        for (int j = 0; j < n; j++) {
            for (int i = 0; i < n; i++) {
                if (alpha[j][i] <= 0.001f && alpha[j][i + 1] <= 0.001f && alpha[j + 1][i] <= 0.001f && alpha[j + 1][i + 1] <= 0.001f) {
                    continue;
                }
                float u0 = (float) i / n;
                float u1 = (float) (i + 1) / n;
                float v0 = (float) j / n;
                float v1 = (float) (j + 1) / n;
                float x0 = -halfW + u0 * 2f * halfW;
                float x1 = -halfW + u1 * 2f * halfW;
                float y0 = halfH - v0 * 2f * halfH;
                float y1 = halfH - v1 * 2f * halfH;
                addBrandLogoVertex(consumer, pose, x0, y1, z, u0, v1, light, alpha[j + 1][i], 0f, 0f, 1f);
                addBrandLogoVertex(consumer, pose, x1, y1, z, u1, v1, light, alpha[j + 1][i + 1], 0f, 0f, 1f);
                addBrandLogoVertex(consumer, pose, x1, y0, z, u1, v0, light, alpha[j][i + 1], 0f, 0f, 1f);
                addBrandLogoVertex(consumer, pose, x0, y0, z, u0, v0, light, alpha[j][i], 0f, 0f, 1f);
            }
        }
    }

    private static float glintBand(float s, float center, float halfWidth) {
        return MetalTextConsumer.glintBand(s, center, halfWidth);
    }

    private void addBrandLogoVertex(VertexConsumer consumer, PoseStack.Pose pose, float x, float y, float z, float u, float v,
                                    int packedLight, float alpha, float nx, float ny, float nz) {
        addBrandLogoVertex(consumer, pose, x, y, z, u, v, packedLight, alpha, nx, ny, nz, 1f);
    }

    private void addBrandLogoVertex(VertexConsumer consumer, PoseStack.Pose pose, float x, float y, float z, float u, float v,
                                    int packedLight, float alpha, float nx, float ny, float nz, float shade) {
        consumer.vertex(pose.pose(), x, y, z)
                .color(shade, shade, shade, alpha)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(pose.normal(), nx, ny, nz)
                .endVertex();
    }

    private void renderTextParts(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector) {
        if (state.textPartsRaw == null || state.textPartsRaw.isEmpty()) {
            return;
        }
        net.minecraft.client.gui.Font font = net.minecraft.client.Minecraft.getInstance().font;
        for (com.examplebett.render.TextParts.Slot slot : parseTextParts(state.textPartsRaw)) {
            if (slot == null || !slot.visible()) {
                continue;
            }
            String text = textPartSequence(slot);
            font = com.examplebett.compat.LegacyGui.textPartFont(slot.font());
            com.examplebett.compat.LegacyGui.setCurrentFont(font);
            float left = -com.examplebett.compat.LegacyGui.width(font, text) / 2f;
            float top = -font.lineHeight / 2f;
            int light = slot.glow() ? TEXT_PART_FULL_BRIGHT : state.lightCoords;
            int rgb = slot.color() & 0xFFFFFF;
            int sideRgb = ((int) (((rgb >> 16) & 0xFF) * TEXT_PART_SIDE_SHADE) << 16)
                    | ((int) (((rgb >> 8) & 0xFF) * TEXT_PART_SIDE_SHADE) << 8)
                    | (int) ((rgb & 0xFF) * TEXT_PART_SIDE_SHADE);
            float pixel = TEXT_PART_PIXEL_SIZE * slot.scale();

            float pixelX = pixel * slot.width();
            float pixelY = pixel * slot.height();

            poseStack.pushPose();
            poseStack.translate(slot.x() + widthShiftFor(state, slot.x()), slot.y(), slot.z() + limoShiftFor(state, slot.z()));
            poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(slot.yaw()));
            poseStack.mulPose(com.mojang.math.Vector3f.XP.rotationDegrees(slot.pitch()));
            poseStack.mulPose(com.mojang.math.Vector3f.ZP.rotationDegrees(slot.roll()));
            int layers = slot.depth() > 0f
                    ? Math.min(TEXT_PART_MAX_LAYERS, Math.max(1, (int) Math.ceil(slot.depth() / TEXT_PART_LAYER_SPACING)))
                    : 0;
            for (int i = layers; i >= 1; i--) {
                poseStack.pushPose();
                poseStack.translate(0f, 0f, -slot.depth() * i / layers);
                poseStack.scale(pixelX, -pixelY, pixel);
                collector.submitText(poseStack, text, left, top, 0xFF000000 | sideRgb, light);
                poseStack.popPose();
            }

            poseStack.scale(pixelX, -pixelY, pixel);
            boolean drawn = false;
            int metal = com.examplebett.client.gui.CarColorPalette.metalIndex(rgb);
            if (metal >= 0) {
                submitMetalText(poseStack, collector, font, text, left, top, rgb, metal, light, state.brandLogoDetail >= 2);
                drawn = true;
            }
            if (!drawn) {
                collector.submitText(poseStack, text, left, top, 0xFF000000 | rgb, light);
            }
            poseStack.popPose();
        }
    }

    private void submitMetalText(PoseStack poseStack, GeometrySubmitter collector, net.minecraft.client.gui.Font font,
                                 String text, float left, float top, int rgb, int metal,
                                 int light, boolean band) {
        float width = Math.max(1f, com.examplebett.compat.LegacyGui.width(font, text));
        float height = font.lineHeight;
        collector.submitWrappedText(poseStack, text, left, top, light, (out, pose) ->
                new MetalTextConsumer(out, pose, left, top, width, height, rgb, metal,
                        band ? MetalTextConsumer.phase(pose, left + width / 2f, top + height / 2f) : Float.NaN));
    }

    private static void computeExhaustOutlets(CarRenderState state, PoseStack poseStack, float[] origin) {
        int outlets = com.examplebett.render.ExhaustParts.outletsOf(state.exhaustEnabledId);
        com.examplebett.render.PartTransforms.Transform6 t = state.exhaustTransform;
        if (outlets == 0 || t == null) {
            state.exhaustTarget.clientExhaustOutlets = null;
            return;
        }
        float[] relative = PoseMatrices.relative(origin, PoseMatrices.capture(poseStack));
        float[] result = new float[outlets * 6];
        for (int i = 0; i < outlets; i++) {
            float side = i == 0 ? 1f : -1f;
            float localX = t.x() * side;
            float[] position = PoseMatrices.transformPosition(relative,
                    localX + widthShiftFor(state, localX), t.y(), t.z() + limoShiftFor(state, t.z()));

            float ax = (float) Math.toRadians(t.angle());
            float ay = (float) Math.toRadians(t.angleY() * side);
            float y1 = (float) Math.sin(ax);
            float z1 = -(float) Math.cos(ax);
            float x2 = z1 * (float) Math.sin(ay);
            float z2 = z1 * (float) Math.cos(ay);
            float[] direction = PoseMatrices.transformDirection(relative, x2, y1, z2);
            result[i * 6] = position[0];
            result[i * 6 + 1] = position[1];
            result[i * 6 + 2] = position[2];
            result[i * 6 + 3] = direction[0];
            result[i * 6 + 4] = direction[1];
            result[i * 6 + 5] = direction[2];
        }
        state.exhaustTarget.clientExhaustOutlets = result;
    }

    private void renderTaxiSign(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector) {
        if (state.taxiSignEnabledIds == null || state.taxiSignEnabledIds.isEmpty()) {
            return;
        }
        Set<String> enabledIds = parseEnabledIds(state.taxiSignEnabledIds);
        net.minecraft.server.packs.resources.ResourceManager resourceManager =
                net.minecraft.client.Minecraft.getInstance().getResourceManager();
        com.examplebett.render.PartTransforms.Transform12 defaults = new com.examplebett.render.PartTransforms.Transform12(
                com.examplebett.entity.CarEntity.DEFAULT_TAXI_SIGN_X, com.examplebett.entity.CarEntity.DEFAULT_TAXI_SIGN_Y,
                com.examplebett.entity.CarEntity.DEFAULT_TAXI_SIGN_Z, 0f, 0f, com.examplebett.entity.CarEntity.DEFAULT_TAXI_SIGN_SCALE,
                1f, 1f, 1f, 1f, 1f, 1f);
        for (com.examplebett.render.TaxiSignParts.Option option : com.examplebett.render.TaxiSignParts.list(resourceManager)) {
            if (!enabledIds.contains(option.id())) {
                continue;
            }
            CompiledCarModel signModel = TaxiSignModel.get(resourceManager, option.modelId());
            if (signModel == null) {
                continue;
            }
            com.examplebett.render.PartTransforms.Transform12 t =
                    com.examplebett.render.PartTransforms.resolve12(state.taxiSignTransformsRaw, option.id(), defaults);
            poseStack.pushPose();
            poseStack.translate(t.x() + widthShiftFor(state, t.x()), t.y(), t.z() + limoShiftFor(state, t.z()));
            if (t.angleY() != 0f) {
                poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(t.angleY()));
            }
            if (t.angle() != 0f) {
                poseStack.mulPose(com.mojang.math.Vector3f.XP.rotationDegrees(t.angle()));
            }

            float stretchScaleX = (t.stretchXPos() + t.stretchXNeg()) / 2f;
            float stretchScaleY = (t.stretchYPos() + t.stretchYNeg()) / 2f;
            float stretchScaleZ = (t.stretchZPos() + t.stretchZNeg()) / 2f;
            float offsetX = t.scale() * OTHER_PART_STRETCH_HALF_EXTENT * (t.stretchXPos() - t.stretchXNeg()) / 2f;
            float offsetY = t.scale() * OTHER_PART_STRETCH_HALF_EXTENT * (t.stretchYPos() - t.stretchYNeg()) / 2f;
            float offsetZ = t.scale() * OTHER_PART_STRETCH_HALF_EXTENT * (t.stretchZPos() - t.stretchZNeg()) / 2f;
            if (offsetX != 0f || offsetY != 0f || offsetZ != 0f) {
                poseStack.translate(offsetX, offsetY, offsetZ);
            }
            if (t.scale() != 1f || stretchScaleX != 1f || stretchScaleY != 1f || stretchScaleZ != 1f) {
                poseStack.scale(t.scale() * stretchScaleX, t.scale() * stretchScaleY, t.scale() * stretchScaleZ);
            }
            for (CompiledCarModel.TextureGroup group : signModel.groups) {
                ResourceLocation texture = group.texture;
                final int tint = group.tintable ? state.color : 0xFFFFFF;
                RenderType renderType = groupRenderType(group, texture);
                collector.submitCustomGeometry(poseStack, renderType, (pose, vertexConsumer) ->
                        renderGroup(group.vertexData, pose, vertexConsumer, state.lightCoords, tint, BodyDeform.NONE, usesTriangles(group)));
            }
            poseStack.popPose();
        }
    }

    private void renderPoliceLight(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector) {
        if (state.policeLightEnabledIds == null || state.policeLightEnabledIds.isEmpty()) {
            return;
        }
        Set<String> enabledIds = parseEnabledIds(state.policeLightEnabledIds);
        net.minecraft.server.packs.resources.ResourceManager resourceManager =
                net.minecraft.client.Minecraft.getInstance().getResourceManager();
        com.examplebett.render.PartTransforms.Transform12 defaults = new com.examplebett.render.PartTransforms.Transform12(
                com.examplebett.entity.CarEntity.DEFAULT_POLICE_LIGHT_X, com.examplebett.entity.CarEntity.DEFAULT_POLICE_LIGHT_Y,
                com.examplebett.entity.CarEntity.DEFAULT_POLICE_LIGHT_Z, 0f, 0f, com.examplebett.entity.CarEntity.DEFAULT_POLICE_LIGHT_SCALE,
                1f, 1f, 1f, 1f, 1f, 1f);
        for (com.examplebett.render.PoliceLightParts.Option option : com.examplebett.render.PoliceLightParts.list(resourceManager)) {
            if (!enabledIds.contains(option.id())) {
                continue;
            }

            CompiledCarModel lightModel = option.modelId() == null ? null : PoliceLightModel.get(resourceManager, option.modelId());
            if (lightModel == null && option.modelId() != null) {
                continue;
            }
            com.examplebett.render.PartTransforms.Transform12 t =
                    com.examplebett.render.PartTransforms.resolve12(state.policeLightTransformsRaw, option.id(), defaults);
            poseStack.pushPose();
            poseStack.translate(t.x() + widthShiftFor(state, t.x()), t.y(), t.z() + limoShiftFor(state, t.z()));
            if (t.angleY() != 0f) {
                poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(t.angleY()));
            }
            if (t.angle() != 0f) {
                poseStack.mulPose(com.mojang.math.Vector3f.XP.rotationDegrees(t.angle()));
            }

            float stretchScaleX = (t.stretchXPos() + t.stretchXNeg()) / 2f;
            float stretchScaleY = (t.stretchYPos() + t.stretchYNeg()) / 2f;
            float stretchScaleZ = (t.stretchZPos() + t.stretchZNeg()) / 2f;
            float offsetX = t.scale() * OTHER_PART_STRETCH_HALF_EXTENT * (t.stretchXPos() - t.stretchXNeg()) / 2f;
            float offsetY = t.scale() * OTHER_PART_STRETCH_HALF_EXTENT * (t.stretchYPos() - t.stretchYNeg()) / 2f;
            float offsetZ = t.scale() * OTHER_PART_STRETCH_HALF_EXTENT * (t.stretchZPos() - t.stretchZNeg()) / 2f;
            if (offsetX != 0f || offsetY != 0f || offsetZ != 0f) {
                poseStack.translate(offsetX, offsetY, offsetZ);
            }
            if (t.scale() != 1f || stretchScaleX != 1f || stretchScaleY != 1f || stretchScaleZ != 1f) {
                poseStack.scale(t.scale() * stretchScaleX, t.scale() * stretchScaleY, t.scale() * stretchScaleZ);
            }
            for (CompiledCarModel.TextureGroup group : lightModel == null ? List.<CompiledCarModel.TextureGroup>of() : lightModel.groups) {
                ResourceLocation texture = group.texture;
                final int tint = group.tintable ? state.color : 0xFFFFFF;
                RenderType renderType = groupRenderType(group, texture);
                collector.submitCustomGeometry(poseStack, renderType, (pose, vertexConsumer) ->
                        renderGroup(group.vertexData, pose, vertexConsumer, state.lightCoords, tint, BodyDeform.NONE, usesTriangles(group)));
            }
            if (state.policeLightsOn) {
                if (com.examplebett.render.PoliceLightParts.isEmergencyBeacon(option.id())) {
                    renderEmergencyBeaconLights(state, poseStack, collector);
                }
                if (com.examplebett.render.PoliceLightParts.JP_LIGHT_BAR_ID.equals(option.id())) {
                    renderJpLightBarLights(state, poseStack, collector);
                }
                if (com.examplebett.render.PoliceLightParts.hasUsLightBarLights(option.id())) {
                    renderUsLightBarLights(state, poseStack, collector, false);
                }
                if (com.examplebett.render.PoliceLightParts.isEuLightBar(option.id())) {
                    renderUsLightBarLights(state, poseStack, collector, true);
                }
                if (com.examplebett.render.PoliceLightParts.isEuPattern(option.id())) {
                    renderEuPatternLights(state, poseStack, collector,
                            com.examplebett.render.PoliceLightParts.isEuPatternLeftFirst(option.id()));
                }
                if (com.examplebett.render.PoliceLightParts.JP_PATTERN_ID.equals(option.id())) {
                    renderJpPatternLights(state, poseStack, collector);
                }
            }
            poseStack.popPose();
        }
    }

    private static final float ROTATING_LIGHT_ROTATE_DEG_PER_TICK = 15f;

    private static final int QUAD_FLASH_ON_TICKS = 3;
    private static final int QUAD_FLASH_GAP_TICKS = 3;
    private static final int QUAD_FLASH_GROUP_GAP_TICKS = 4;
    private static final int QUAD_FLASH_GROUP_SLOT_TICKS =
            2 * QUAD_FLASH_ON_TICKS + QUAD_FLASH_GAP_TICKS + QUAD_FLASH_GROUP_GAP_TICKS;
    private static final int QUAD_FLASH_CYCLE_TICKS = QUAD_FLASH_GROUP_SLOT_TICKS * 2;

    private record QuadFlashState(boolean groupATurn, boolean flashOn) {
    }

    private static QuadFlashState computeQuadFlashState(int age) {
        int cyclePos = age % QUAD_FLASH_CYCLE_TICKS;
        boolean groupATurn = cyclePos < QUAD_FLASH_GROUP_SLOT_TICKS;
        int localPhase = groupATurn ? cyclePos : cyclePos - QUAD_FLASH_GROUP_SLOT_TICKS;
        boolean flashOn = localPhase < QUAD_FLASH_ON_TICKS
                || (localPhase >= QUAD_FLASH_ON_TICKS + QUAD_FLASH_GAP_TICKS
                        && localPhase < 2 * QUAD_FLASH_ON_TICKS + QUAD_FLASH_GAP_TICKS);
        return new QuadFlashState(groupATurn, flashOn);
    }

    private static final float EMERGENCY_BEACON_RADIUS = 0.18f;

    private static final float EMERGENCY_BEACON_PIVOT_Z = -0.165f;

    private static final float EMERGENCY_BEACON_GLOW_SIZE = 0.36f;

    private static final float EMERGENCY_BEACON_SECOND_SET_OFFSET_DEG = 90f;

    private void renderEmergencyBeaconLights(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector) {
        float rotateDeg = (state.age * ROTATING_LIGHT_ROTATE_DEG_PER_TICK) % 360f;
        QuadFlashState flash = computeQuadFlashState(state.age);
        if (!flash.flashOn()) {
            return;
        }
        float setRotateDeg = flash.groupATurn() ? rotateDeg : rotateDeg + EMERGENCY_BEACON_SECOND_SET_OFFSET_DEG;
        submitRotatingLightGlow(poseStack, collector, 0f, 0f, EMERGENCY_BEACON_PIVOT_Z,
                EMERGENCY_BEACON_RADIUS, EMERGENCY_BEACON_GLOW_SIZE, setRotateDeg, 1f, 1f, 0f, 0f);
        submitRotatingLightGlow(poseStack, collector, 0f, 0f, EMERGENCY_BEACON_PIVOT_Z,
                EMERGENCY_BEACON_RADIUS, EMERGENCY_BEACON_GLOW_SIZE, setRotateDeg + 180f, 1f, 1f, 0f, 0f);
    }

    private void submitRotatingLightGlow(PoseStack poseStack, GeometrySubmitter collector,
                                          float pivotX, float pivotY, float pivotZ, float radius, float glowSize,
                                          float rotateDeg, float alpha, float tintR, float tintG, float tintB) {
        double rotateRad = Math.toRadians(rotateDeg);
        float dx = (float) (Math.sin(rotateRad) * radius);
        float dz = (float) (Math.cos(rotateRad) * radius);
        submitGlow(poseStack, collector, HEADLIGHT_GLOW_TEXTURE,
                pivotX + dx, pivotY, pivotZ + dz, glowSize, 0f,
                tintR, tintG, tintB, alpha);
    }

    private static final float LIGHT_BAR_ROTATOR_RADIUS = 0.15f;

    private static final float LIGHT_BAR_ROTATOR_GLOW_SIZE = 0.30f;

    private static final float[][] JP_LIGHT_BAR_ROTATOR_POSITIONS = {
            {-0.80f, 0.181f, -0.205f},
            {-0.35f, 0.179f, -0.015f},
            {0.35f, 0.177f, 0.011f},
            {0.80f, 0.173f, -0.205f},
    };

    private void renderJpLightBarLights(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector) {
        float rotateDeg = (state.age * ROTATING_LIGHT_ROTATE_DEG_PER_TICK) % 360f;
        QuadFlashState flash = computeQuadFlashState(state.age);
        if (!flash.flashOn()) {
            return;
        }

        for (int i = 0; i < JP_LIGHT_BAR_ROTATOR_POSITIONS.length; i++) {
            boolean isCenterSet = i == 1 || i == 2;
            if (isCenterSet != flash.groupATurn()) {
                continue;
            }
            float[] pos = JP_LIGHT_BAR_ROTATOR_POSITIONS[i];
            submitRotatingLightGlow(poseStack, collector, pos[0], pos[1], pos[2],
                    LIGHT_BAR_ROTATOR_RADIUS, LIGHT_BAR_ROTATOR_GLOW_SIZE, rotateDeg, 1f, 1f, 0f, 0f);
            submitRotatingLightGlow(poseStack, collector, pos[0], pos[1], pos[2],
                    LIGHT_BAR_ROTATOR_RADIUS, LIGHT_BAR_ROTATOR_GLOW_SIZE, rotateDeg + 180f, 1f, 1f, 0f, 0f);
        }
    }

    private static final float[][] US_LIGHT_BAR_ROTATOR_POSITIONS = {
            {-0.80f, 0.138f, 0.015f},
            {-0.35f, 0.138f, 0.015f},
            {0.35f, 0.138f, 0.015f},
            {0.80f, 0.138f, 0.015f},
    };

    private void renderUsLightBarLights(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector,
                                        boolean allBlue) {
        float rotateDeg = (state.age * ROTATING_LIGHT_ROTATE_DEG_PER_TICK) % 360f;
        QuadFlashState flash = computeQuadFlashState(state.age);
        if (!flash.flashOn()) {
            return;
        }

        for (int i = 0; i < US_LIGHT_BAR_ROTATOR_POSITIONS.length; i++) {
            boolean isCenterSet = i == 1 || i == 2;
            if (isCenterSet != flash.groupATurn()) {
                continue;
            }
            float[] pos = US_LIGHT_BAR_ROTATOR_POSITIONS[i];
            boolean isBlue = allBlue || i >= 2;
            float tintR = isBlue ? 0f : 1f;
            float tintB = isBlue ? 1f : 0f;
            submitRotatingLightGlow(poseStack, collector, pos[0], pos[1], pos[2],
                    LIGHT_BAR_ROTATOR_RADIUS, LIGHT_BAR_ROTATOR_GLOW_SIZE, rotateDeg, 1f, tintR, 0f, tintB);
            submitRotatingLightGlow(poseStack, collector, pos[0], pos[1], pos[2],
                    LIGHT_BAR_ROTATOR_RADIUS, LIGHT_BAR_ROTATOR_GLOW_SIZE, rotateDeg + 180f, 1f, tintR, 0f, tintB);
        }
    }

    private static final int EU_PATTERN_STEP_TICKS = 2;

    private static final int JP_PATTERN_DARK_TICKS = 10;

    private static final float[] SIDE_PAIR_PATTERN_X = {-0.475f, -0.325f, 0.325f, 0.475f};

    private static final boolean SIDE_PAIR_PATTERN_SINGLE_LIGHT = true;

    private void submitSidePairPatternGlow(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector,
                                           int index, float tintR, float tintB) {
        float x = SIDE_PAIR_PATTERN_X[index];
        if (SIDE_PAIR_PATTERN_SINGLE_LIGHT) {

            if (state.age % EU_PATTERN_STEP_TICKS >= EU_PATTERN_STEP_TICKS / 2) {
                return;
            }

            x = index < 2 ? (SIDE_PAIR_PATTERN_X[0] + SIDE_PAIR_PATTERN_X[1]) / 2f
                    : (SIDE_PAIR_PATTERN_X[2] + SIDE_PAIR_PATTERN_X[3]) / 2f;
        }
        float[] usPos = US_LIGHT_BAR_ROTATOR_POSITIONS[index];
        submitGlow(poseStack, collector, HEADLIGHT_GLOW_TEXTURE, x, usPos[1], usPos[2],
                LIGHT_BAR_ROTATOR_GLOW_SIZE, 0f, tintR, 0f, tintB, 1f);
    }

    private void renderEuPatternLights(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector,
                                       boolean leftFirst) {
        int step = (state.age / EU_PATTERN_STEP_TICKS) % 8;
        boolean firstSide = step < 4;
        boolean leftSide = firstSide == leftFirst;

        int outer = leftSide ? 0 : 3;
        int inner = leftSide ? 1 : 2;
        submitSidePairPatternGlow(state, poseStack, collector, (step % 2 == 0) ? outer : inner, 0f, 1f);
    }

    private void renderJpPatternLights(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector) {
        int flashTicks = EU_PATTERN_STEP_TICKS * 4;
        int pos = state.age % (flashTicks + JP_PATTERN_DARK_TICKS);
        if (pos >= flashTicks) {
            return;
        }
        boolean outerTurn = (pos / EU_PATTERN_STEP_TICKS) % 2 == 0;
        submitSidePairPatternGlow(state, poseStack, collector, outerTurn ? 0 : 1, 1f, 0f);
        submitSidePairPatternGlow(state, poseStack, collector, outerTurn ? 3 : 2, 1f, 0f);
    }

    private static void applyGroupTransform(PoseStack poseStack, CarRenderState state,
                                             CompiledCarModel.TextureGroup group, boolean spinning, boolean rotorSpinning,
                                             float extraZ) {

        if ((spinning || rotorSpinning)
                && (state.suspensionPitchDegrees != 0f || state.suspensionRollDegrees != 0f)) {
            poseStack.translate(0.0, state.tumblePivotHeight, 0.0);
            if (state.suspensionRollDegrees != 0f) {
                poseStack.mulPose(com.mojang.math.Vector3f.ZP.rotationDegrees(-state.suspensionRollDegrees));
            }
            if (state.suspensionPitchDegrees != 0f) {
                poseStack.mulPose(com.mojang.math.Vector3f.XP.rotationDegrees(-state.suspensionPitchDegrees));
            }
            poseStack.translate(0.0, -state.tumblePivotHeight, 0.0);
        }
        if (spinning) {
            ObjModel.WheelPivot pivot = state.compiledModel.wheelPivots.get(group.wheelGroup);

            if (state.bodyHeightOffset != 0f) {
                poseStack.translate(0.0, -state.bodyHeightOffset, 0.0);
            }

            float sideSign = embeddedWheelSideSign(pivot);
            poseStack.translate(embeddedWheelX(state, pivot), embeddedWheelY(state, pivot),
                    embeddedWheelZ(state, pivot.centerZ + extraZ, pivot.front));

            if (state.wheelCamberDegrees != 0f) {
                poseStack.mulPose(com.mojang.math.Vector3f.ZP.rotationDegrees(sideSign * state.wheelCamberDegrees));
            }

            if (pivot.front) {
                poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(state.frontWheelSteerDegrees));
            }

            float rollDegrees = state.wheelRotationDegrees + (pivot.front ? 0f : state.rearWheelExtraSpinDegrees);
            poseStack.mulPose(wheelAxisRotation(pivot, rollDegrees));

            float axialScale = state.wheelScale * state.wheelWidthScale;
            if (axialScale != 1f || state.wheelScale != 1f) {
                poseStack.scale(pivot.axisX >= 0.5f ? axialScale : state.wheelScale,
                        pivot.axisY >= 0.5f ? axialScale : state.wheelScale,
                        pivot.axisZ >= 0.5f ? axialScale : state.wheelScale);
            }
            poseStack.translate(-pivot.centerX, -pivot.centerY, -pivot.centerZ);
        } else if (rotorSpinning) {
            ObjModel.RotorPivot pivot = state.compiledModel.rotorPivots.get(group.rotorGroup);
            poseStack.translate(pivot.centerX, pivot.centerY, pivot.centerZ);
            poseStack.mulPose(rotorAxisRotation(pivot, state.rotorRotationDegrees));
            poseStack.translate(-pivot.centerX, -pivot.centerY, -pivot.centerZ);
        }
    }

    private static float embeddedWheelSideSign(ObjModel.WheelPivot pivot) {
        return pivot.centerX >= 0f ? 1f : -1f;
    }

    private static float embeddedWheelX(CarRenderState state, ObjModel.WheelPivot pivot) {
        float x = pivot.centerX + embeddedWheelSideSign(pivot) * state.wheelTrackHalfWidthOffset;
        return x + widthShiftFor(state, x);
    }

    private static float embeddedWheelY(CarRenderState state, ObjModel.WheelPivot pivot) {
        return pivot.centerY + state.wheelAxleHeightOffset;
    }

    private static float embeddedWheelZ(CarRenderState state, float baseZ, boolean front) {
        float z = baseZ + (front ? state.wheelFrontZOffset : state.wheelRearZOffset);
        return z + limoShiftFor(state, z);
    }

    private static com.mojang.math.Quaternion wheelAxisRotation(ObjModel.WheelPivot pivot, float degrees) {
        if (pivot.axisX >= 0.5f) {
            return com.mojang.math.Vector3f.XP.rotationDegrees(degrees);
        }
        if (pivot.axisY >= 0.5f) {
            return com.mojang.math.Vector3f.YP.rotationDegrees(degrees);
        }
        return com.mojang.math.Vector3f.ZP.rotationDegrees(degrees);
    }

    private static com.mojang.math.Quaternion rotorAxisRotation(ObjModel.RotorPivot pivot, float degrees) {
        if (pivot.axisX >= 0.5f) {
            return com.mojang.math.Vector3f.XP.rotationDegrees(degrees);
        }
        if (pivot.axisY >= 0.5f) {
            return com.mojang.math.Vector3f.YP.rotationDegrees(degrees);
        }
        return com.mojang.math.Vector3f.ZP.rotationDegrees(degrees);
    }

    private static final float PLATE_HALF_WIDTH = 0.45f;

    private void submitPlate(PoseStack poseStack, GeometrySubmitter collector, int packedLight, ResourceLocation texture,
                              float x, float y, float z, float scale, float yawDeg, float pitchDeg, float rollDeg, float aspect) {
        poseStack.pushPose();
        poseStack.translate(x, y, z);
        poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(yawDeg));
        poseStack.mulPose(com.mojang.math.Vector3f.XP.rotationDegrees(pitchDeg));
        poseStack.mulPose(com.mojang.math.Vector3f.ZP.rotationDegrees(rollDeg));
        RenderType plateType = RenderType.entityCutoutNoCull(texture);
        collector.submitCustomGeometry(poseStack, plateType, (pose, vertexConsumer) ->
                renderPlate(pose, vertexConsumer, packedLight, scale, aspect));
        poseStack.popPose();
    }

    private void renderPlate(PoseStack.Pose pose, VertexConsumer consumer, int packedLight, float scale, float aspect) {
        float halfW = PLATE_HALF_WIDTH * scale;
        float halfH = (PLATE_HALF_WIDTH / aspect) * scale;
        addPlateVertex(consumer, pose, -halfW, -halfH, 0f, 0f, 1f, packedLight);
        addPlateVertex(consumer, pose, halfW, -halfH, 0f, 1f, 1f, packedLight);
        addPlateVertex(consumer, pose, halfW, halfH, 0f, 1f, 0f, packedLight);
        addPlateVertex(consumer, pose, -halfW, halfH, 0f, 0f, 0f, packedLight);
    }

    private void addPlateVertex(VertexConsumer consumer, PoseStack.Pose pose, float x, float y, float z, float u, float v, int packedLight) {
        consumer.vertex(pose.pose(), x, y, z)
                .color(1f, 1f, 1f, 1f)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(pose.normal(), 0f, 1f, 0f)
                .endVertex();
    }

    private static final ResourceLocation HEADLIGHT_GLOW_TEXTURE =
            AiObjectMod.id("textures/entity/lights/headlight_glow.png");
    private static final ResourceLocation TAILLIGHT_GLOW_TEXTURE =
            AiObjectMod.id("textures/entity/lights/taillight_glow.png");
    private static final float HEADLIGHT_GLOW_SIZE = 0.35f;
    private static final float TAILLIGHT_GLOW_SIZE = 0.25f;

    private static final int FULL_BRIGHT_LIGHT = 0xF000F0;

    private void submitGlow(PoseStack poseStack, GeometrySubmitter collector, ResourceLocation texture,
                             float x, float y, float z, float halfSize, float yawDeg,
                             float tintR, float tintG, float tintB) {
        submitGlow(poseStack, collector, texture, x, y, z, halfSize, yawDeg, tintR, tintG, tintB, 1f);
    }

    private void submitGlow(PoseStack poseStack, GeometrySubmitter collector, ResourceLocation texture,
                             float x, float y, float z, float halfSize, float yawDeg,
                             float tintR, float tintG, float tintB, float alpha) {
        poseStack.pushPose();
        poseStack.translate(x, y, z);

        RenderType glowType = RenderType.entityTranslucentCull(texture);
        submitGlowQuad(poseStack, collector, glowType, halfSize, yawDeg, 0f, tintR, tintG, tintB, alpha);
        submitGlowQuad(poseStack, collector, glowType, halfSize, yawDeg + 90f, 0f, tintR, tintG, tintB, alpha);
        submitGlowQuad(poseStack, collector, glowType, halfSize, yawDeg, 90f, tintR, tintG, tintB, alpha);
        poseStack.popPose();
    }

    private void submitGlowQuad(PoseStack poseStack, GeometrySubmitter collector, RenderType glowType,
                                 float halfSize, float yawDeg, float pitchDeg,
                                 float tintR, float tintG, float tintB, float alpha) {
        poseStack.pushPose();
        poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(yawDeg));
        poseStack.mulPose(com.mojang.math.Vector3f.XP.rotationDegrees(pitchDeg));
        collector.late().submitCustomGeometry(poseStack, glowType, (pose, vertexConsumer) ->
                renderGlow(pose, vertexConsumer, halfSize, tintR, tintG, tintB, alpha));
        poseStack.popPose();
    }

    private void renderGlow(PoseStack.Pose pose, VertexConsumer consumer, float halfSize,
                             float tintR, float tintG, float tintB, float alpha) {
        addGlowVertex(consumer, pose, -halfSize, -halfSize, 0f, 0f, 1f, tintR, tintG, tintB, alpha);
        addGlowVertex(consumer, pose, halfSize, -halfSize, 0f, 1f, 1f, tintR, tintG, tintB, alpha);
        addGlowVertex(consumer, pose, halfSize, halfSize, 0f, 1f, 0f, tintR, tintG, tintB, alpha);
        addGlowVertex(consumer, pose, -halfSize, halfSize, 0f, 0f, 0f, tintR, tintG, tintB, alpha);

        addGlowVertex(consumer, pose, -halfSize, -halfSize, 0f, 0f, 1f, tintR, tintG, tintB, alpha);
        addGlowVertex(consumer, pose, -halfSize, halfSize, 0f, 0f, 0f, tintR, tintG, tintB, alpha);
        addGlowVertex(consumer, pose, halfSize, halfSize, 0f, 1f, 0f, tintR, tintG, tintB, alpha);
        addGlowVertex(consumer, pose, halfSize, -halfSize, 0f, 1f, 1f, tintR, tintG, tintB, alpha);
    }

    private void addGlowVertex(VertexConsumer consumer, PoseStack.Pose pose, float x, float y, float z, float u, float v,
                                float tintR, float tintG, float tintB, float alpha) {
        consumer.vertex(pose.pose(), x, y, z)
                .color(tintR, tintG, tintB, alpha)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(FULL_BRIGHT_LIGHT)
                .normal(pose.normal(), 0f, 1f, 0f)
                .endVertex();
    }

    private record BodyDeform(boolean active, float bodyMinY,
                               float lipStretchScale, float lipWidthScale, float lipBandHeight, float lipFrontExtendScale,
                               float limoAnchorZ, float limoStretchM, float limoZoneM,
                               float widthAnchorX, float widthStretchM, float widthZoneM) {
        static final BodyDeform NONE = new BodyDeform(false, Float.NaN, 1f, 1f, 0f, 1f, Float.NaN, 0f, 0f, Float.NaN, 0f, 0f);

        static BodyDeform from(CarRenderState state) {
            boolean lipActive = !Float.isNaN(state.compiledModel.bodyMinY)
                    && (state.lipStretchScale != 1f || state.lipWidthScale != 1f || state.lipFrontExtendScale != 1f);
            boolean limoActive = !Float.isNaN(state.compiledModel.bodyCenterZ) && state.limoStretchCm != 0f;
            boolean widthActive = !Float.isNaN(state.compiledModel.bodyCenterX) && state.widthStretchCm != 0f;
            if (!lipActive && !limoActive && !widthActive) {
                return NONE;
            }

            float limoAnchorZ = state.compiledModel.bodyCenterZ + state.limoCenterOffsetCm / 100f;

            float widthAnchorX = state.compiledModel.bodyCenterX + state.widthCenterOffsetCm / 100f;
            return new BodyDeform(true, state.compiledModel.bodyMinY,
                    state.lipStretchScale, state.lipWidthScale, state.lipBandHeightCm / 100f, state.lipFrontExtendScale,
                    limoAnchorZ, state.limoStretchCm / 100f, state.limoZoneCm / 100f,
                    widthAnchorX, state.widthStretchCm / 100f, state.widthZoneCm / 100f);
        }
    }

    private static boolean usesTriangles(CompiledCarModel.TextureGroup group) {
        return !group.translucent && LegacyShaderFix.isAvailable();
    }

    private static final ResourceLocation[] METAL_PAINT_TEXTURES = {
            AiObjectMod.id("textures/entity/paint/chrome.png"),
            AiObjectMod.id("textures/entity/paint/gold.png"),
            AiObjectMod.id("textures/entity/paint/metallic_silver.png")
    };

    private static final ResourceLocation BRONZE_PAINT_TEXTURE = AiObjectMod.id("textures/entity/paint/bronze.png");

    private static ResourceLocation metalPaintTexture(CompiledCarModel.TextureGroup group, ResourceLocation texture, boolean usesPaintMask, int color) {
        if (!group.tintable || usesPaintMask || !CompiledCarModel.FALLBACK_TEXTURE.equals(texture)) {
            return texture;
        }
        int metal = com.examplebett.client.gui.CarColorPalette.metalIndex(color);
        if (metal >= 0) {
            return METAL_PAINT_TEXTURES[metal];
        }
        return (color & 0xFFFFFF) == com.examplebett.client.gui.CarColorPalette.EXTRA_RGB[com.examplebett.client.gui.CarColorPalette.EXTRA_BRONZE]
                ? BRONZE_PAINT_TEXTURE
                : texture;
    }

    private static RenderType groupRenderType(CompiledCarModel.TextureGroup group, ResourceLocation texture) {
        if (usesTriangles(group)) {
            return LegacyShaderFix.cutoutType(texture, group.noCull);
        }
        return group.translucent
                ? RenderType.entityTranslucentCull(texture)
                : group.noCull
                        ? RenderType.entityCutoutNoCull(texture)
                        : RenderType.entityCutout(texture);
    }

    private void renderGroup(float[] data, PoseStack.Pose pose, VertexConsumer vertexConsumer, int packedLight, int color, BodyDeform deform, boolean triangles) {
        float tintR = ((color >> 16) & 0xFF) / 255f;
        float tintG = ((color >> 8) & 0xFF) / 255f;
        float tintB = (color & 0xFF) / 255f;
        if (triangles) {

            LegacyShaderFix.markBuffer(vertexConsumer);
        }

        int legacyStart = LegacyShaderFix.legacyBegin(vertexConsumer);
        for (int i = 0; i < data.length; i += 36) {
            addVertex(vertexConsumer, pose, data, i, packedLight, tintR, tintG, tintB, deform);
            addVertex(vertexConsumer, pose, data, i + 12, packedLight, tintR, tintG, tintB, deform);
            addVertex(vertexConsumer, pose, data, i + 24, packedLight, tintR, tintG, tintB, deform);
            if (!triangles) {
                addVertex(vertexConsumer, pose, data, i + 24, packedLight, tintR, tintG, tintB, deform);
            }
        }
        LegacyShaderFix.legacyEnd(vertexConsumer, legacyStart, data, pose, triangles);
    }

    private static float lipWeight(float y, float bodyMinY, float bandHeight) {
        if (bandHeight <= 0f) return 0f;
        float t = (bodyMinY + bandHeight - y) / bandHeight;
        t = Math.max(0f, Math.min(1f, t));
        return t * t * (3f - 2f * t);
    }

    private static float limoWeight(float z, float anchorZ, float zoneM) {
        if (zoneM <= 0f) return 0f;
        float t = (anchorZ - z) / zoneM;
        t = Math.max(0f, Math.min(1f, t));
        return t * t * (3f - 2f * t);
    }

    private static float limoShift(float z, float anchorZ, float zoneM, float stretchM) {
        return -limoWeight(z, anchorZ, zoneM) * stretchM;
    }

    private static float widthWeight(float x, float anchorX, float zoneM) {
        if (zoneM <= 0f) return 0f;
        float t = (anchorX - x) / zoneM;
        t = Math.max(0f, Math.min(1f, t));
        return t * t * (3f - 2f * t);
    }

    private static float widthShift(float x, float anchorX, float zoneM, float stretchM) {
        return -widthWeight(x, anchorX, zoneM) * stretchM;
    }

    private static float limoShiftFor(CarRenderState state, float z) {
        if (Float.isNaN(state.compiledModel.bodyCenterZ) || state.limoStretchCm == 0f) {
            return 0f;
        }
        float anchorZ = state.compiledModel.bodyCenterZ + state.limoCenterOffsetCm / 100f;
        return limoShift(z, anchorZ, state.limoZoneCm / 100f, state.limoStretchCm / 100f);
    }

    private static float widthShiftFor(CarRenderState state, float x) {
        if (Float.isNaN(state.compiledModel.bodyCenterX) || state.widthStretchCm == 0f) {
            return 0f;
        }
        float anchorX = state.compiledModel.bodyCenterX + state.widthCenterOffsetCm / 100f;
        return widthShift(x, anchorX, state.widthZoneCm / 100f, state.widthStretchCm / 100f);
    }

    private void addVertex(VertexConsumer consumer, PoseStack.Pose pose, float[] data, int offset, int packedLight, float tintR, float tintG, float tintB,
                            BodyDeform deform) {
        float x = data[offset];
        float y = data[offset + 1];
        float z = data[offset + 2];
        if (deform.active()) {
            float weight = lipWeight(y, deform.bodyMinY(), deform.lipBandHeight());
            if (weight > 0f) {
                y -= weight * (deform.lipStretchScale() - 1f) * deform.lipBandHeight();
                x += weight * x * (deform.lipWidthScale() - 1f);

                z += weight * Math.max(z, 0f) * (deform.lipFrontExtendScale() - 1f);
            }
            z += limoShift(z, deform.limoAnchorZ(), deform.limoZoneM(), deform.limoStretchM());
            x += widthShift(x, deform.widthAnchorX(), deform.widthZoneM(), deform.widthStretchM());
        }

        consumer.vertex(pose.pose(), x, y, z)
                .color(data[offset + 5] * tintR, data[offset + 6] * tintG, data[offset + 7] * tintB, data[offset + 8])
                .uv(data[offset + 3], data[offset + 4])
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(pose.normal(), data[offset + 9], data[offset + 10], data[offset + 11])
                .endVertex();
    }

    private static final float REFLECTION_STRENGTH = 0.45f;

    private static final float REFLECTION_ANGLE_LOW = 0.85f;
    private static final float REFLECTION_ANGLE_HIGH = 0.99f;

    private static final float REFLECTION_NORMAL_OFFSET = 0.001f;

    private static float fakeReflectionBoost(PoseStack.Pose pose, float x, float y, float z, float nx, float ny, float nz, int packedLight) {
        com.mojang.math.Vector3f normal = new com.mojang.math.Vector3f(nx, ny, nz);
        normal.transform(pose.normal());
        if (normal.dot(normal) < 1e-8f) {
            return 0f;
        }
        normal.normalize();

        com.mojang.math.Vector4f viewDirH = new com.mojang.math.Vector4f(x, y, z, 1f);
        viewDirH.transform(pose.pose());
        com.mojang.math.Vector3f viewDir = new com.mojang.math.Vector3f(viewDirH.x(), viewDirH.y(), viewDirH.z());
        if (viewDir.dot(viewDir) < 1e-8f) {
            return 0f;
        }
        viewDir.normalize();

        float vDotN = viewDir.dot(normal);
        com.mojang.math.Vector3f reflectDir = normal.copy();
        reflectDir.mul(-2f * vDotN);
        reflectDir.add(viewDir);

        float t = (reflectDir.y() - REFLECTION_ANGLE_LOW) / (REFLECTION_ANGLE_HIGH - REFLECTION_ANGLE_LOW);
        t = Math.max(0f, Math.min(1f, t));
        float highlight = t * t * (3f - 2f * t);

        int blockLight = packedLight & 0xFFFF;
        int skyLight = (packedLight >> 16) & 0xFFFF;
        float lightFactor = Math.max(blockLight, skyLight) / 240f;

        return highlight * lightFactor * REFLECTION_STRENGTH;
    }

    private void renderReflectionOverlay(float[] data, PoseStack.Pose pose, VertexConsumer vertexConsumer, int packedLight, BodyDeform deform) {
        for (int i = 0; i < data.length; i += 36) {
            addReflectionVertex(vertexConsumer, pose, data, i, packedLight, deform);
            addReflectionVertex(vertexConsumer, pose, data, i + 12, packedLight, deform);
            addReflectionVertex(vertexConsumer, pose, data, i + 24, packedLight, deform);
            addReflectionVertex(vertexConsumer, pose, data, i + 24, packedLight, deform);
        }
    }

    private void addReflectionVertex(VertexConsumer consumer, PoseStack.Pose pose, float[] data, int offset, int packedLight, BodyDeform deform) {
        float x = data[offset];
        float y = data[offset + 1];
        float z = data[offset + 2];
        if (deform.active()) {
            float weight = lipWeight(y, deform.bodyMinY(), deform.lipBandHeight());
            if (weight > 0f) {
                y -= weight * (deform.lipStretchScale() - 1f) * deform.lipBandHeight();
                x += weight * x * (deform.lipWidthScale() - 1f);
                z += weight * Math.max(z, 0f) * (deform.lipFrontExtendScale() - 1f);
            }
            z += limoShift(z, deform.limoAnchorZ(), deform.limoZoneM(), deform.limoStretchM());
            x += widthShift(x, deform.widthAnchorX(), deform.widthZoneM(), deform.widthStretchM());
        }

        float nx = data[offset + 9];
        float ny = data[offset + 10];
        float nz = data[offset + 11];
        float boost = fakeReflectionBoost(pose, x, y, z, nx, ny, nz, packedLight);
        x += nx * REFLECTION_NORMAL_OFFSET;
        y += ny * REFLECTION_NORMAL_OFFSET;
        z += nz * REFLECTION_NORMAL_OFFSET;

        consumer.vertex(pose.pose(), x, y, z)
                .color(1f, 1f, 1f, boost)
                .uv(data[offset + 3], data[offset + 4])
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(pose.normal(), nx, ny, nz)
                .endVertex();
    }
}
*/
//?}
//? if <1.15 {
/*
//~ axis_mulpose_compat
package com.examplebett.client.render;

import com.examplebett.AiObjectMod;
import com.examplebett.entity.CarEntity;
import com.examplebett.render.CompiledCarModel;
import com.examplebett.render.ObjModel;
import com.examplebett.compat.v114.PoseStack;
import com.examplebett.compat.v114.VertexConsumer;

import com.examplebett.compat.v114.RenderType;
import com.examplebett.compat.v114.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import com.examplebett.compat.v114.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CarEntityRenderer extends EntityRenderer<CarEntity> {

    private static final double LOD_WHEEL_DISTANCE = 32.0;
    private static final double LOD_WHEEL_DISTANCE_SQ = LOD_WHEEL_DISTANCE * LOD_WHEEL_DISTANCE;

    private static final double BRAND_LOGO_FLAT_DISTANCE = 5.0;
    private static final double BRAND_LOGO_FLAT_DISTANCE_SQ = BRAND_LOGO_FLAT_DISTANCE * BRAND_LOGO_FLAT_DISTANCE;
    private static final double BRAND_LOGO_HIDE_DISTANCE = 48.0;
    private static final double BRAND_LOGO_HIDE_DISTANCE_SQ = BRAND_LOGO_HIDE_DISTANCE * BRAND_LOGO_HIDE_DISTANCE;

    private static final boolean[] WHEEL_IS_FRONT_4 = {true, true, false, false};
    private static final boolean[] WHEEL_IS_FRONT_6 = {true, true, false, false, false, false};
    private static final float[] WHEEL_CAMBER_SIGN_4 = {1f, -1f, 1f, -1f};
    private static final float[] WHEEL_CAMBER_SIGN_6 = {1f, -1f, 1f, -1f, 1f, -1f};

    private static final boolean[] WHEEL_IS_FRONT_8 = {true, true, false, false, false, false, false, false};
    private static final float[] WHEEL_CAMBER_SIGN_8 = {1f, -1f, 1f, -1f, 1f, -1f, 1f, -1f};

    private static final boolean[] WHEEL_IS_FRONT_2 = {true, false};
    private static final float[] WHEEL_CAMBER_SIGN_2 = {1f, -1f};
    private static final float[] WHEEL_OFFSET_Z_BUF = new float[8];
    private static final float[] WHEEL_OFFSET_X_BUF = new float[8];

    private static final Map<String, Set<String>> ENABLED_IDS_CACHE = new HashMap<>();

    private static Set<String> parseEnabledIds(String csv) {
        return ENABLED_IDS_CACHE.computeIfAbsent(csv, s -> new HashSet<>(java.util.Arrays.asList(s.split(","))));
    }

    private static final float WHEELIE_PIVOT_Z_FACTOR = 0.85f;

    private static final int BLINK_ON_TICKS = 10;
    private static final int BLINK_OFF_TICKS = 10;

    @FunctionalInterface
    private interface GeometryRenderer {
        void render(PoseStack.Pose pose, VertexConsumer consumer);
    }

    @FunctionalInterface
    private interface ModelDrawer {
        void draw(PoseStack poseStack, VertexConsumer consumer);
    }

    @FunctionalInterface
    private interface TextWrapper {
        VertexConsumer wrap(VertexConsumer out, PoseStack.Pose pose);
    }

    @FunctionalInterface
    private interface GeometrySubmitter {
        void submitCustomGeometry(PoseStack pose, RenderType type, GeometryRenderer renderer);

        default void submitWrappedText(PoseStack pose, String text, float x, float y, int light,
                                       TextWrapper wrapper) {
            submitText(pose, text, x, y, 0xFFFFFFFF, light);
        }

        default void submitModel(PoseStack pose, RenderType type, ModelDrawer drawer) {

            throw new UnsupportedOperationException();
        }

        default GeometrySubmitter late() {
            return this;
        }

        default void submitText(PoseStack pose, String text, float x, float y, int argb, int light) {
        }
    }

    private static GeometrySubmitter immediateSubmitter(MultiBufferSource bufferSource) {
        return new GeometrySubmitter() {
            @Override
            public void submitCustomGeometry(PoseStack pose, RenderType type, GeometryRenderer renderer) {
                renderer.render(pose.last(), bufferSource.getBuffer(type));
            }

            @Override
            public void submitModel(PoseStack pose, RenderType type, ModelDrawer drawer) {
                drawer.draw(pose, bufferSource.getBuffer(type));
            }

            @Override
            public void submitText(PoseStack pose, String text, float x, float y, int argb, int light) {
                bufferSource.drawText(com.examplebett.compat.LegacyGui.currentFont(), text, x, y, argb, pose.last().pose());
            }

            @Override
            public void submitWrappedText(PoseStack pose, String text, float x, float y, int light,
                                          TextWrapper wrapper) {
                PoseStack.Pose last = pose.last();
                this.submitText(pose, text, x, y, 0xFFFFFFFF, light);
            }
        };
    }

    private final FigureModels figures;

    public CarEntityRenderer(net.minecraft.client.renderer.entity.EntityRenderDispatcher context) {
        super(context);
        this.figures = new FigureModels(context);
    }

    @Override
    public ResourceLocation getTextureLocation(CarEntity entity) {
        return CompiledCarModel.FALLBACK_TEXTURE;
    }

    private void populateCarState(CarEntity entity, CarRenderState state, float partialTick) {
        state.lightCoords = entity.getLightColor();

        state.yRot = entity.getYRot();

        float[] railwayOverride = entity.mineturismo$computeRailwayRenderOverride(partialTick);
        state.railwayPositionOverride = railwayOverride != null;
        if (railwayOverride != null) {
            state.railwayPosDeltaX = railwayOverride[0];
            state.railwayPosDeltaY = railwayOverride[1];
            state.railwayPosDeltaZ = railwayOverride[2];
            state.yRot = railwayOverride[3];
        }
        state.tumblePitchDegrees = entity.getTumblePitchDegrees();
        state.tumbleRollDegrees = entity.getTumbleRollDegrees();
        state.tumblePivotHeight = (float) (entity.getScaledHitboxHeight() / 2.0);
        state.wheeliePitchDegrees = entity.getWheeliePitchDegrees();

        state.wheeliePivotZ = -entity.getFootprintHalfLength() * WHEELIE_PIVOT_Z_FACTOR;
        state.tiltRollDegrees = entity.getTiltRollDegrees();
        state.tiltPitchDegrees = entity.getTiltPitchDegrees();
        state.tiltHeightCm = entity.getTiltHeightCm();
        state.suspensionPitchDegrees = entity.getSuspensionPitchDegrees();
        state.suspensionRollDegrees = entity.getSuspensionRollDegrees();
        state.bikeLeanRollDegrees = entity.getBikeLeanRollDegrees();
        state.showFakeRider = entity.shouldRenderFakeTrafficRider();
        if (state.showFakeRider) {
            state.fakeRiderUseAlex = (entity.getUUID().hashCode() & 1) == 0;
            state.fakeRiderX = entity.getPassengerXOffset();

            state.fakeRiderY = entity.getFakeRiderYOffset();
            state.fakeRiderZ = entity.getPassengerZOffset();
            state.fakeRiderIsMotorcycle = entity.hasVehicleCategory(com.examplebett.entity.VehicleCategory.MOTORCYCLE);
            state.fakeRiderBikeStationary = entity.isBikeStationary();
        }
        state.useLodWheel = this.entityRenderDispatcher.distanceToSqr(com.examplebett.compat.ModEntity.getX(entity), com.examplebett.compat.ModEntity.getY(entity), com.examplebett.compat.ModEntity.getZ(entity)) > LOD_WHEEL_DISTANCE_SQ;

        state.wheelRotationDegrees = state.useLodWheel ? 0f : entity.getWheelRotationDegrees(partialTick);
        state.rearWheelExtraSpinDegrees = state.useLodWheel ? 0f : entity.getRearWheelExtraSpinDegrees(partialTick);
        state.frontWheelSteerDegrees = entity.getFrontWheelSteerDegrees(partialTick);
        state.rotorRotationDegrees = entity.getRotorRotationDegrees(partialTick);
        state.compiledModel = entity.getCompiledModel();
        state.wheelCompiledModel = entity.getWheelCompiledModel();
        state.wheelCompiledModelMirrored = entity.getWheelCompiledModelMirrored();
        state.wheelAttachment = entity.getWheelAttachment();
        state.wheelFrontZOffset = entity.getWheelFrontZOffset();
        state.wheelRearZOffset = entity.getWheelRearZOffset();
        state.wheelTrackHalfWidthOffset = entity.getWheelTrackHalfWidthOffset();
        state.wheelAxleHeightOffset = entity.getWheelAxleHeightOffset();
        state.extraRearAxleEnabled = entity.isExtraRearAxleEnabled();
        state.extraRearAxleZOffset = entity.getExtraRearAxleZOffset();
        state.extraRearAxle2Enabled = entity.isExtraRearAxle2Enabled();
        state.extraRearAxle2ZOffset = entity.getExtraRearAxle2ZOffset();
        state.bodyHeightOffset = entity.getBodyHeightOffset();
        state.wheelCamberDegrees = entity.getWheelCamberDegrees();
        state.wheelScale = entity.getWheelScale();
        state.wheelWidthScale = entity.getWheelWidthScale();
        state.lipStretchScale = entity.getLipStretchScale();
        state.lipWidthScale = entity.getLipWidthScale();
        state.lipBandHeightCm = entity.getLipBandHeightCm();
        state.lipFrontExtendScale = entity.getLipFrontExtendScale();
        state.limoStretchCm = entity.getLimoStretchCm();
        state.limoZoneCm = entity.getLimoZoneCm();
        state.limoCenterOffsetCm = entity.getLimoCenterOffsetCm();
        state.widthStretchCm = entity.getWidthStretchCm();
        state.widthZoneCm = entity.getWidthZoneCm();
        state.widthCenterOffsetCm = entity.getWidthCenterOffsetCm();
        state.carScale = entity.getCarScale();
        state.color = entity.getColor();

        boolean isMotorcycleNoFrontPlate = entity.hasVehicleCategory(com.examplebett.entity.VehicleCategory.MOTORCYCLE);
        state.frontPlateTexture = isMotorcycleNoFrontPlate ? null : PlateTextures.texture(entity.getFrontPlate());
        state.frontPlateAspect = PlateTextures.aspect(entity.getFrontPlate());
        var frontOffset = entity.getFrontPlateOffset();
        state.frontPlateX = (float) frontOffset.x;
        state.frontPlateY = (float) frontOffset.y;
        state.frontPlateZ = (float) frontOffset.z;
        state.frontPlateScale = entity.getFrontPlateScale();
        state.frontPlateYaw = entity.getFrontPlateYaw();
        state.frontPlatePitch = entity.getFrontPlatePitch();
        state.frontPlateRoll = entity.getFrontPlateRoll();

        state.rearPlateTexture = PlateTextures.texture(entity.getRearPlate());
        state.rearPlateAspect = PlateTextures.aspect(entity.getRearPlate());
        var rearOffset = entity.getRearPlateOffset();
        state.rearPlateX = (float) rearOffset.x;
        state.rearPlateY = (float) rearOffset.y;
        state.rearPlateZ = (float) rearOffset.z;
        state.rearPlateScale = entity.getRearPlateScale();
        state.rearPlateYaw = entity.getRearPlateYaw();
        state.rearPlatePitch = entity.getRearPlatePitch();
        state.rearPlateRoll = entity.getRearPlateRoll();

        state.lightsOn = entity.areLightsOn();

        state.headlightHidden = entity.hasVehicleCategory(com.examplebett.entity.VehicleCategory.TRAILER);
        state.lightsBlinking = entity.isLightBlinkEnabled();
        state.age = entity.tickCount;
        var headlightOffset = entity.getHeadlightOffset();
        state.headlightX = (float) headlightOffset.x;
        state.headlightY = (float) headlightOffset.y;
        state.headlightZ = (float) headlightOffset.z;
        var taillightOffset = entity.getTaillightOffset();
        state.taillightX = (float) taillightOffset.x;
        state.taillightY = (float) taillightOffset.y;
        state.taillightZ = (float) taillightOffset.z;
        var headlightColor = com.examplebett.entity.HeadlightColors.byId(entity.resolveHeadlightColorId());
        state.headlightColorR = headlightColor.r();
        state.headlightColorG = headlightColor.g();
        state.headlightColorB = headlightColor.b();

        state.rearWingEnabledIds = entity.getRearWingEnabledIds();
        state.rearWingTransformsRaw = entity.getRearWingTransformsRaw();

        state.otherPartEnabledIds = entity.getOtherPartEnabledIds();
        state.otherPartTransformsRaw = entity.getOtherPartTransformsRaw();

        state.textPartsRaw = entity.getTextPartsRaw();
        state.underNeonRaw = entity.getUnderNeonRaw();
        state.brandLogosRaw = entity.getBrandLogosRaw();
        double brandLogoDistanceSq = this.entityRenderDispatcher.distanceToSqr(com.examplebett.compat.ModEntity.getX(entity), com.examplebett.compat.ModEntity.getY(entity), com.examplebett.compat.ModEntity.getZ(entity));
        state.brandLogoDetail = brandLogoDistanceSq > BRAND_LOGO_HIDE_DISTANCE_SQ ? 0
                : brandLogoDistanceSq > BRAND_LOGO_FLAT_DISTANCE_SQ ? 1 : 2;

        state.frontBrakeCaliperEnabledIds = entity.getFrontBrakeCaliperEnabledIds();
        state.frontBrakeCaliperTransformsRaw = entity.getFrontBrakeCaliperTransformsRaw();

        state.rearBrakeCaliperEnabledIds = entity.getRearBrakeCaliperEnabledIds();
        state.rearBrakeCaliperTransformsRaw = entity.getRearBrakeCaliperTransformsRaw();

        state.exhaustEnabledId = entity.getExhaustEnabledIds();
        if (state.exhaustEnabledId.isEmpty()) {
            state.exhaustTarget = null;
            entity.clientExhaustOutlets = null;
        } else {
            state.exhaustTarget = entity;
            state.exhaustTransform = entity.getExhaustTransform(state.exhaustEnabledId);
        }

        state.towSelf = null;
        state.towComputeFront = false;
        state.towComputeRear = false;
        state.towRopeEnd = null;
        long towNow = entity.level().getGameTime();
        int towLeaderId = entity.getTowLeaderEntityId();
        if (towLeaderId != -1 && entity.level().getEntity(towLeaderId) instanceof CarEntity towLeader) {
            towLeader.clientTowRearHookWantedTime = towNow;
            state.towSelf = entity;
            state.towComputeFront = true;
            float[] leaderRear = towLeader.clientTowRearHook;
            if (leaderRear != null) {
                net.minecraft.world.phys.Vec3 leaderPos = com.examplebett.compat.ModVec3.position(towLeader, partialTick);
                net.minecraft.world.phys.Vec3 selfPos = com.examplebett.compat.ModVec3.position(entity, partialTick);
                state.towRopeEnd = new float[]{
                        (float) (leaderPos.x - selfPos.x) + leaderRear[0],
                        (float) (leaderPos.y - selfPos.y) + leaderRear[1],
                        (float) (leaderPos.z - selfPos.z) + leaderRear[2]};
            }
        } else {
            entity.clientTowFrontHook = null;
        }
        if (towNow - entity.clientTowRearHookWantedTime <= 2) {
            state.towSelf = entity;
            state.towComputeRear = true;
        }

        state.policeLightEnabledIds = entity.getPoliceLightEnabledIds();
        state.policeLightTransformsRaw = entity.getPoliceLightTransformsRaw();
        state.policeLightsOn = entity.isPoliceLightsOn();

        state.taxiSignEnabledIds = entity.getTaxiSignEnabledIds();
        state.taxiSignTransformsRaw = entity.getTaxiSignTransformsRaw();
    }

    @Override
    public void render(CarEntity entity, double x, double y, double z, float entityYaw, float partialTick) {
        PoseStack poseStack = new PoseStack();
        poseStack.translate(x, y, z);
        MultiBufferSource bufferSource = new MultiBufferSource();
        this.mineturismo$render(entity, entityYaw, partialTick, poseStack, bufferSource, entity.getLightColor());
        bufferSource.endBatch();
        super.render(entity, x, y, z, entityYaw, partialTick);
    }

    private void mineturismo$render(CarEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int light) {
        CarRenderState state = new CarRenderState();
        populateCarState(entity, state, partialTick);
        state.isInvisible = entity.isInvisible();
        state.lightCoords = light;
        renderCar(state, poseStack, immediateSubmitter(bufferSource));
    }

    private void renderCar(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector) {

        if (state.isInvisible) {
            return;
        }

        if (state.compiledModel == null) {
            return;
        }

        float[] exhaustOrigin = (state.exhaustTarget != null || state.towSelf != null) ? PoseMatrices.capture(poseStack) : null;

        poseStack.pushPose();
        if (state.railwayPositionOverride) {

            poseStack.translate(state.railwayPosDeltaX, state.railwayPosDeltaY, state.railwayPosDeltaZ);
        }
        poseStack.mulPose(com.examplebett.compat.v114.PoseStack.yRotation(state.yRot));

        boolean tumbling = state.tumblePitchDegrees != 0f || state.tumbleRollDegrees != 0f;
        if (tumbling) {
            poseStack.translate(0.0, state.tumblePivotHeight, 0.0);
        }
        if (state.tumblePitchDegrees != 0f) {
            poseStack.mulPose(com.examplebett.compat.v114.PoseStack.xRotation(state.tumblePitchDegrees));
        }
        if (state.tumbleRollDegrees != 0f) {
            poseStack.mulPose(com.examplebett.compat.v114.PoseStack.zRotation(state.tumbleRollDegrees));
        }
        if (tumbling) {
            poseStack.translate(0.0, -state.tumblePivotHeight, 0.0);
        }

        boolean wheelieActive = state.wheeliePitchDegrees != 0f;
        if (wheelieActive) {
            poseStack.translate(0.0, 0.0, state.wheeliePivotZ);
            poseStack.mulPose(com.examplebett.compat.v114.PoseStack.xRotation(-state.wheeliePitchDegrees));
            poseStack.translate(0.0, 0.0, -state.wheeliePivotZ);
        }

        boolean tilting = state.tiltPitchDegrees != 0f || state.tiltRollDegrees != 0f;
        if (tilting) {
            poseStack.translate(0.0, state.tumblePivotHeight, 0.0);
        }
        if (state.tiltPitchDegrees != 0f) {
            poseStack.mulPose(com.examplebett.compat.v114.PoseStack.xRotation(state.tiltPitchDegrees));
        }
        if (state.tiltRollDegrees != 0f) {
            poseStack.mulPose(com.examplebett.compat.v114.PoseStack.zRotation(state.tiltRollDegrees));
        }
        if (tilting) {
            poseStack.translate(0.0, -state.tumblePivotHeight, 0.0);
        }
        if (state.tiltHeightCm != 0f) {
            poseStack.translate(0.0, state.tiltHeightCm / 100.0, 0.0);
        }

        if (state.bikeLeanRollDegrees != 0f) {
            poseStack.translate(0.0, state.tumblePivotHeight, 0.0);
            poseStack.mulPose(com.examplebett.compat.v114.PoseStack.zRotation(state.bikeLeanRollDegrees));
            poseStack.translate(0.0, -state.tumblePivotHeight, 0.0);
        }

        if (state.carScale != 1f) {
            poseStack.scale(state.carScale, state.carScale, state.carScale);
        }

        poseStack.pushPose();
        poseStack.translate(0.0, state.bodyHeightOffset, 0.0);

        boolean suspensionSwaying = state.suspensionPitchDegrees != 0f || state.suspensionRollDegrees != 0f;
        if (suspensionSwaying) {
            poseStack.translate(0.0, state.tumblePivotHeight, 0.0);
        }
        if (state.suspensionPitchDegrees != 0f) {
            poseStack.mulPose(com.examplebett.compat.v114.PoseStack.xRotation(state.suspensionPitchDegrees));
        }
        if (state.suspensionRollDegrees != 0f) {
            poseStack.mulPose(com.examplebett.compat.v114.PoseStack.zRotation(state.suspensionRollDegrees));
        }
        if (suspensionSwaying) {
            poseStack.translate(0.0, -state.tumblePivotHeight, 0.0);
        }

        List<CompiledCarModel.TextureGroup> reflectiveGroups = new ArrayList<>();
        float[] embeddedExtraAxleZ = embeddedExtraAxleZ(state);
        for (CompiledCarModel.TextureGroup group : state.compiledModel.groups) {

            ResourceLocation texture = group.texture;
            boolean usesPaintMask = group.tintable && PaintTintTexture.hasPaint(group.texture);
            if (usesPaintMask) {
                texture = PaintTintTexture.resolve(group.texture, state.color);
            }
            texture = metalPaintTexture(group, texture, usesPaintMask, state.color);

            final int tint = (group.tintable && !usesPaintMask) ? state.color : 0xFFFFFF;

            RenderType renderType = groupRenderType(group, texture);

            boolean spinning = group.wheelGroup >= 0 && group.wheelGroup < state.compiledModel.wheelPivots.size();
            boolean rotorSpinning = group.rotorGroup >= 0 && group.rotorGroup < state.compiledModel.rotorPivots.size();

            boolean isBodyGroup = group.wheelGroup < 0 && group.rotorGroup < 0;
            BodyDeform deform = isBodyGroup ? BodyDeform.from(state) : BodyDeform.NONE;

            boolean reflective = com.examplebett.client.AiObjectModClient.isFakeReflectionEnabled()
                    && isBodyGroup && !group.translucent;

            poseStack.pushPose();
            applyGroupTransform(poseStack, state, group, spinning, rotorSpinning, 0f);
            collector.submitCustomGeometry(poseStack, renderType, (pose, vertexConsumer) ->
                    renderGroup(group.vertexData, pose, vertexConsumer, state.lightCoords, tint, deform, usesTriangles(group)));
            poseStack.popPose();

            if (spinning && embeddedExtraAxleZ.length > 0
                    && !state.compiledModel.wheelPivots.get(group.wheelGroup).front) {
                float pivotZ = state.compiledModel.wheelPivots.get(group.wheelGroup).centerZ;
                for (float axleZ : embeddedExtraAxleZ) {
                    poseStack.pushPose();
                    applyGroupTransform(poseStack, state, group, true, false, axleZ - pivotZ);
                    collector.submitCustomGeometry(poseStack, renderType, (pose, vertexConsumer) ->
                            renderGroup(group.vertexData, pose, vertexConsumer, state.lightCoords, tint, BodyDeform.NONE, usesTriangles(group)));
                    poseStack.popPose();
                }
            }
            if (reflective) {
                reflectiveGroups.add(group);
            }
        }

        for (CompiledCarModel.TextureGroup group : reflectiveGroups) {
            boolean spinning = group.wheelGroup >= 0 && group.wheelGroup < state.compiledModel.wheelPivots.size();
            boolean rotorSpinning = group.rotorGroup >= 0 && group.rotorGroup < state.compiledModel.rotorPivots.size();
            boolean isBodyGroup = group.wheelGroup < 0 && group.rotorGroup < 0;
            BodyDeform deform = isBodyGroup ? BodyDeform.from(state) : BodyDeform.NONE;
            RenderType reflectionType = RenderType.entityNoOutline(CompiledCarModel.FALLBACK_TEXTURE);
            poseStack.pushPose();
            applyGroupTransform(poseStack, state, group, spinning, rotorSpinning, 0f);
            collector.submitCustomGeometry(poseStack, reflectionType, (pose, vertexConsumer) ->
                    renderReflectionOverlay(group.vertexData, pose, vertexConsumer, state.lightCoords, deform));
            poseStack.popPose();
        }

        if (state.frontPlateTexture != null) {
            submitPlate(poseStack, collector, state.lightCoords, state.frontPlateTexture,
                    state.frontPlateX + widthShiftFor(state, state.frontPlateX), state.frontPlateY, state.frontPlateZ + limoShiftFor(state, state.frontPlateZ), state.frontPlateScale,
                    state.frontPlateYaw, state.frontPlatePitch, state.frontPlateRoll, state.frontPlateAspect);
        }
        if (state.rearPlateTexture != null) {

            submitPlate(poseStack, collector, state.lightCoords, state.rearPlateTexture,
                    state.rearPlateX + widthShiftFor(state, state.rearPlateX), state.rearPlateY, state.rearPlateZ + limoShiftFor(state, state.rearPlateZ), state.rearPlateScale,
                    state.rearPlateYaw + 180f, state.rearPlatePitch, state.rearPlateRoll, state.rearPlateAspect);
        }

        boolean blinkPhaseOn = !state.lightsBlinking
                || (state.age % (BLINK_ON_TICKS + BLINK_OFF_TICKS)) < BLINK_ON_TICKS;
        if (state.lightsOn && blinkPhaseOn) {

            for (int side = -1; side <= 1; side += 2) {
                float headlightX = state.headlightX * side;
                float taillightX = state.taillightX * side;
                if (!state.headlightHidden) {
                    submitGlow(poseStack, collector, HEADLIGHT_GLOW_TEXTURE,
                            headlightX + widthShiftFor(state, headlightX), state.headlightY, state.headlightZ + limoShiftFor(state, state.headlightZ), HEADLIGHT_GLOW_SIZE, 0f,
                            state.headlightColorR, state.headlightColorG, state.headlightColorB);
                }
                submitGlow(poseStack, collector, TAILLIGHT_GLOW_TEXTURE,
                        taillightX + widthShiftFor(state, taillightX), state.taillightY, state.taillightZ + limoShiftFor(state, state.taillightZ), TAILLIGHT_GLOW_SIZE, 180f,
                        1f, 1f, 1f);
            }
        }

        renderRearWing(state, poseStack, collector);
        renderPoliceLight(state, poseStack, collector);
        renderTaxiSign(state, poseStack, collector);
        renderOtherPart(state, poseStack, collector);
        renderTextParts(state, poseStack, collector);
        renderBrandLogos(state, poseStack, collector);
        if (exhaustOrigin != null && state.exhaustTarget != null) {
            computeExhaustOutlets(state, poseStack, exhaustOrigin);
        }
        if (exhaustOrigin != null && state.towSelf != null) {
            computeTowHooks(state, poseStack, exhaustOrigin);
        }

        poseStack.popPose();

        renderUnderNeon(state, poseStack, collector);
        renderWheelAttachment(state, poseStack, collector);
        renderBrakeCaliper(state, poseStack, collector, true, state.frontBrakeCaliperEnabledIds, state.frontBrakeCaliperTransformsRaw);
        renderBrakeCaliper(state, poseStack, collector, false, state.rearBrakeCaliperEnabledIds, state.rearBrakeCaliperTransformsRaw);

        renderFakeRider(state, poseStack, collector);

        poseStack.popPose();

        renderTowRope(state, poseStack, collector);
    }

    private static final ResourceLocation TOW_ROPE_TEXTURE = com.examplebett.AiObjectMod.id("textures/entity/tow_rope.png");

    private static final float TOW_ROPE_HALF_THICKNESS = 0.045f;
    private static final int TOW_ROPE_SEGMENTS = 24;

    private static void computeTowHooks(CarRenderState state, PoseStack poseStack, float[] origin) {
        float[] relative = PoseMatrices.relative(origin, PoseMatrices.capture(poseStack));
        if (state.towComputeFront) {
            state.towSelf.clientTowFrontHook = PoseMatrices.transformPosition(relative, 0f, state.frontPlateY,
                    state.frontPlateZ + limoShiftFor(state, state.frontPlateZ));
        }
        if (state.towComputeRear) {
            state.towSelf.clientTowRearHook = PoseMatrices.transformPosition(relative, 0f, state.rearPlateY,
                    state.rearPlateZ + limoShiftFor(state, state.rearPlateZ));
        }
    }

    private void renderTowRope(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector) {
        if (!state.towComputeFront || state.towRopeEnd == null || state.towSelf == null || state.towSelf.clientTowFrontHook == null) {
            return;
        }
        float[] a = state.towSelf.clientTowFrontHook;
        float[] b = state.towRopeEnd;
        float dx = b[0] - a[0];
        float dy = b[1] - a[1];
        float dz = b[2] - a[2];
        float straight = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (straight < 1.0E-3f) {
            return;
        }

        float ropeLength = (float) CarEntity.TOW_ROPE_LENGTH;
        float slack = Math.max(0f, ropeLength - straight);
        float sag = 0.08f + (float) Math.sqrt(slack * (ropeLength + straight)) * 0.35f;
        int light = state.lightCoords;
        collector.submitCustomGeometry(poseStack, RenderType.entityCutoutNoCull(TOW_ROPE_TEXTURE), (pose, consumer) -> {
            float[] prev = null;
            for (int i = 0; i <= TOW_ROPE_SEGMENTS; i++) {
                float t = (float) i / TOW_ROPE_SEGMENTS;
                float[] p = {a[0] + dx * t, a[1] + dy * t - sag * 4f * t * (1f - t), a[2] + dz * t};
                if (prev != null) {

                    float u0 = ((i - 1) % 2) * 0.5f;
                    addRopeSegment(consumer, pose, prev, p, u0, u0 + 0.5f, light);
                }
                prev = p;
            }
        });
    }

    private void addRopeSegment(VertexConsumer consumer, PoseStack.Pose pose, float[] p0, float[] p1, float u0, float u1, int light) {
        float dx = p1[0] - p0[0];
        float dy = p1[1] - p0[1];
        float dz = p1[2] - p0[2];
        float dirLen = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (dirLen < 1.0E-4f) {
            return;
        }
        dx /= dirLen;
        dy /= dirLen;
        dz /= dirLen;

        float sx = -dz;
        float sy = 0f;
        float sz = dx;
        float sideLen = (float) Math.sqrt(sx * sx + sz * sz);
        if (sideLen < 1.0E-3f) {
            sx = 1f;
            sz = 0f;
            sideLen = 1f;
        }
        sx = sx / sideLen * TOW_ROPE_HALF_THICKNESS;
        sz = sz / sideLen * TOW_ROPE_HALF_THICKNESS;

        float ux = sy * dz - sz * dy;
        float uy = sz * dx - sx * dz;
        float uz = sx * dy - sy * dx;
        float upLen = (float) Math.sqrt(ux * ux + uy * uy + uz * uz);
        if (upLen < 1.0E-8f) {
            return;
        }
        ux = ux / upLen * TOW_ROPE_HALF_THICKNESS;
        uy = uy / upLen * TOW_ROPE_HALF_THICKNESS;
        uz = uz / upLen * TOW_ROPE_HALF_THICKNESS;
        float[][] corners = {
                {sx + ux, sy + uy, sz + uz}, {-sx + ux, -sy + uy, -sz + uz},
                {-sx - ux, -sy - uy, -sz - uz}, {sx - ux, sy - uy, sz - uz}};
        for (int f = 0; f < 4; f++) {
            float[] c0 = corners[f];
            float[] c1 = corners[(f + 1) % 4];
            float v0 = f * 0.25f;
            float v1 = v0 + 0.25f;
            addPlateVertex(consumer, pose, p0[0] + c0[0], p0[1] + c0[1], p0[2] + c0[2], u0, v0, light);
            addPlateVertex(consumer, pose, p1[0] + c0[0], p1[1] + c0[1], p1[2] + c0[2], u1, v0, light);
            addPlateVertex(consumer, pose, p1[0] + c1[0], p1[1] + c1[1], p1[2] + c1[2], u1, v1, light);
            addPlateVertex(consumer, pose, p0[0] + c1[0], p0[1] + c1[1], p0[2] + c1[2], u0, v1, light);
        }
    }

    private static final float FAKE_RIDER_YAW_DEGREES = 180f;

    private static final float FAKE_RIDER_Y_ADJUST = 0.85f;

    private void renderFakeRider(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector) {
        if (!state.showFakeRider) {
            return;
        }
        RenderType renderType = RenderType.entityCutoutNoCull(FigureModels.fakeRiderTexture(state.fakeRiderUseAlex));
        poseStack.pushPose();
        poseStack.translate(state.fakeRiderX, state.fakeRiderY + FAKE_RIDER_Y_ADJUST, state.fakeRiderZ);
        poseStack.mulPose(com.examplebett.compat.v114.PoseStack.yRotation(FAKE_RIDER_YAW_DEGREES));
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        collector.submitModel(poseStack, renderType, (modelPoseStack, vertexConsumer) ->
                this.figures.renderFakeRider(state.fakeRiderUseAlex, state.fakeRiderIsMotorcycle, state.fakeRiderBikeStationary,
                        state.bikeLeanRollDegrees, modelPoseStack, vertexConsumer, state.lightCoords));
        poseStack.popPose();
    }

    private static final float SEATED_PLAYER_SCALE = 0.9375f;

    private static final float SEATED_PLAYER_ORIGIN_HEIGHT = (12f + 2f) / 16f * SEATED_PLAYER_SCALE;

    private void renderSeatedPlayer(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector, boolean alex) {
        ResourceLocation texture = alex ? FigureModels.ALEX_SLIM_TEXTURE : FigureModels.STEVE_TEXTURE;
        poseStack.pushPose();
        poseStack.translate(0f, SEATED_PLAYER_ORIGIN_HEIGHT, 0f);
        poseStack.mulPose(com.examplebett.compat.v114.PoseStack.yRotation(FAKE_RIDER_YAW_DEGREES));
        poseStack.scale(-SEATED_PLAYER_SCALE, -SEATED_PLAYER_SCALE, SEATED_PLAYER_SCALE);
        collector.submitModel(poseStack, RenderType.entityCutoutNoCull(texture), (modelPoseStack, vertexConsumer) ->
                this.figures.renderSeatedPlayer(alex, modelPoseStack, vertexConsumer, state.lightCoords));
        poseStack.popPose();
    }

    private static final float SITTING_WOLF_ORIGIN_HEIGHT = 1.501f;

    private void renderSittingWolf(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector) {
        poseStack.pushPose();
        poseStack.translate(0f, SITTING_WOLF_ORIGIN_HEIGHT, 0f);
        poseStack.mulPose(com.examplebett.compat.v114.PoseStack.yRotation(FAKE_RIDER_YAW_DEGREES));
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        collector.submitModel(poseStack, RenderType.entityCutoutNoCull(FigureModels.WOLF_TEXTURE), (modelPoseStack, vertexConsumer) ->
                this.figures.renderSittingWolf(false, state.age, modelPoseStack, vertexConsumer, state.lightCoords));
        collector.submitModel(poseStack, RenderType.entityCutoutNoCull(FigureModels.WOLF_COLLAR_TEXTURE), (modelPoseStack, vertexConsumer) ->
                this.figures.renderSittingWolf(true, state.age, modelPoseStack, vertexConsumer, state.lightCoords));
        poseStack.popPose();
    }

    private record WheelPositions(int wheelCount, float axleHeight, boolean[] isFront, float[] camberSign) {
    }

    private record PartRef(String id, String modelId) {
    }

    private static boolean usesEmbeddedWheelPivots(CarRenderState state) {
        return state.wheelAttachment == null && state.compiledModel != null
                && state.compiledModel.wheelPivots.size() == 4;
    }

    private WheelPositions computeWheelPositions(CarRenderState state) {
        com.examplebett.render.WheelAttachment attachment = state.wheelAttachment;
        if (attachment == null) {
            return computeEmbeddedWheelPositions(state);
        }
        float frontZ = attachment.frontZ() + state.wheelFrontZOffset;
        float rearZ = attachment.rearZ() + state.wheelRearZOffset;
        float trackHalfWidth = attachment.trackHalfWidth() + state.wheelTrackHalfWidthOffset;
        float axleHeight = attachment.axleHeight() + state.wheelAxleHeightOffset;

        boolean hasRearAxle2 = attachment.hasRearAxle2() || state.extraRearAxleEnabled;
        float rearZ2Base = attachment.hasRearAxle2()
                ? attachment.rearZ2()
                : attachment.rearZ() - CarEntity.DEFAULT_EXTRA_REAR_AXLE_SPACING;
        float rearZ2 = hasRearAxle2 ? rearZ2Base + state.wheelRearZOffset + state.extraRearAxleZOffset : 0f;

        boolean hasRearAxle3 = state.extraRearAxle2Enabled;
        float rearZ3 = hasRearAxle3
                ? rearZ2Base - CarEntity.DEFAULT_EXTRA_REAR_AXLE_SPACING + state.wheelRearZOffset + state.extraRearAxle2ZOffset
                : 0f;

        frontZ += limoShiftFor(state, frontZ);
        rearZ += limoShiftFor(state, rearZ);
        if (hasRearAxle2) {
            rearZ2 += limoShiftFor(state, rearZ2);
        }
        if (hasRearAxle3) {
            rearZ3 += limoShiftFor(state, rearZ3);
        }

        float trackRightX = trackHalfWidth + widthShiftFor(state, trackHalfWidth);
        float trackLeftX = -trackHalfWidth + widthShiftFor(state, -trackHalfWidth);

        boolean singleWheelPerAxle = attachment.singleWheelPerAxle();

        int wheelCount = singleWheelPerAxle ? 2 : (hasRearAxle2 && hasRearAxle3 ? 8 : (hasRearAxle2 || hasRearAxle3 ? 6 : 4));
        float[] offsetZ = WHEEL_OFFSET_Z_BUF;
        float[] offsetX = WHEEL_OFFSET_X_BUF;
        if (singleWheelPerAxle) {
            offsetZ[0] = frontZ; offsetZ[1] = rearZ;
            offsetX[0] = 0f; offsetX[1] = 0f;
        } else {
            offsetZ[0] = frontZ; offsetZ[1] = frontZ; offsetZ[2] = rearZ; offsetZ[3] = rearZ;
            offsetX[0] = trackRightX; offsetX[1] = trackLeftX; offsetX[2] = trackRightX; offsetX[3] = trackLeftX;
            if (hasRearAxle2) {
                offsetZ[4] = rearZ2; offsetZ[5] = rearZ2;
                offsetX[4] = trackRightX; offsetX[5] = trackLeftX;
                if (hasRearAxle3) {
                    offsetZ[6] = rearZ3; offsetZ[7] = rearZ3;
                    offsetX[6] = trackRightX; offsetX[7] = trackLeftX;
                }
            } else if (hasRearAxle3) {
                offsetZ[4] = rearZ3; offsetZ[5] = rearZ3;
                offsetX[4] = trackRightX; offsetX[5] = trackLeftX;
            }
        }
        boolean[] isFront = singleWheelPerAxle ? WHEEL_IS_FRONT_2
                : (wheelCount == 8 ? WHEEL_IS_FRONT_8 : (wheelCount == 6 ? WHEEL_IS_FRONT_6 : WHEEL_IS_FRONT_4));

        float[] camberSign = singleWheelPerAxle ? WHEEL_CAMBER_SIGN_2
                : (wheelCount == 8 ? WHEEL_CAMBER_SIGN_8 : (wheelCount == 6 ? WHEEL_CAMBER_SIGN_6 : WHEEL_CAMBER_SIGN_4));
        return new WheelPositions(wheelCount, axleHeight, isFront, camberSign);
    }

    private static final float[] NO_EXTRA_AXLES = new float[0];

    private static float[] embeddedExtraAxleZ(CarRenderState state) {
        if ((!state.extraRearAxleEnabled && !state.extraRearAxle2Enabled) || !usesEmbeddedWheelPivots(state)) {
            return NO_EXTRA_AXLES;
        }
        float rearZ = 0f;
        int rearCount = 0;
        for (ObjModel.WheelPivot pivot : state.compiledModel.wheelPivots) {
            if (!pivot.front) {
                rearZ += pivot.centerZ;
                rearCount++;
            }
        }
        if (rearCount == 0) {
            return NO_EXTRA_AXLES;
        }
        rearZ /= rearCount;
        float rearZ2Base = rearZ - CarEntity.DEFAULT_EXTRA_REAR_AXLE_SPACING;
        float rearZ2 = rearZ2Base + state.extraRearAxleZOffset;
        float rearZ3 = rearZ2Base - CarEntity.DEFAULT_EXTRA_REAR_AXLE_SPACING + state.extraRearAxle2ZOffset;
        if (state.extraRearAxleEnabled && state.extraRearAxle2Enabled) {
            return new float[] { rearZ2, rearZ3 };
        }
        return new float[] { state.extraRearAxleEnabled ? rearZ2 : rearZ3 };
    }

    private WheelPositions computeEmbeddedWheelPositions(CarRenderState state) {
        if (!usesEmbeddedWheelPivots(state)) {
            return new WheelPositions(0, 0f, WHEEL_IS_FRONT_4, WHEEL_CAMBER_SIGN_4);
        }
        List<ObjModel.WheelPivot> pivots = state.compiledModel.wheelPivots;
        ObjModel.WheelPivot[] front = new ObjModel.WheelPivot[2];
        ObjModel.WheelPivot[] rear = new ObjModel.WheelPivot[2];
        int frontCount = 0;
        int rearCount = 0;
        float sumY = 0f;
        for (ObjModel.WheelPivot pivot : pivots) {
            if (pivot.front && frontCount < 2) {
                front[frontCount++] = pivot;
            } else if (!pivot.front && rearCount < 2) {
                rear[rearCount++] = pivot;
            }
            sumY += pivot.centerY;
        }
        if (frontCount != 2 || rearCount != 2) {
            return new WheelPositions(0, 0f, WHEEL_IS_FRONT_4, WHEEL_CAMBER_SIGN_4);
        }

        if (front[0].centerX < front[1].centerX) {
            ObjModel.WheelPivot t = front[0]; front[0] = front[1]; front[1] = t;
        }
        if (rear[0].centerX < rear[1].centerX) {
            ObjModel.WheelPivot t = rear[0]; rear[0] = rear[1]; rear[1] = t;
        }
        float[] extraAxleZ = embeddedExtraAxleZ(state);
        int wheelCount = 4 + extraAxleZ.length * 2;
        float[] offsetZ = WHEEL_OFFSET_Z_BUF;
        float[] offsetX = WHEEL_OFFSET_X_BUF;
        offsetZ[0] = embeddedWheelZ(state, front[0].centerZ, true); offsetZ[1] = embeddedWheelZ(state, front[1].centerZ, true);
        offsetZ[2] = embeddedWheelZ(state, rear[0].centerZ, false); offsetZ[3] = embeddedWheelZ(state, rear[1].centerZ, false);
        offsetX[0] = embeddedWheelX(state, front[0]); offsetX[1] = embeddedWheelX(state, front[1]);
        offsetX[2] = embeddedWheelX(state, rear[0]); offsetX[3] = embeddedWheelX(state, rear[1]);
        for (int a = 0; a < extraAxleZ.length; a++) {
            int i = 4 + a * 2;
            float axleZ = embeddedWheelZ(state, extraAxleZ[a], false);
            offsetZ[i] = axleZ; offsetZ[i + 1] = axleZ;
            offsetX[i] = offsetX[2]; offsetX[i + 1] = offsetX[3];
        }
        boolean[] isFront = wheelCount == 8 ? WHEEL_IS_FRONT_8 : (wheelCount == 6 ? WHEEL_IS_FRONT_6 : WHEEL_IS_FRONT_4);
        float[] camberSign = wheelCount == 8 ? WHEEL_CAMBER_SIGN_8 : (wheelCount == 6 ? WHEEL_CAMBER_SIGN_6 : WHEEL_CAMBER_SIGN_4);
        return new WheelPositions(wheelCount, sumY / pivots.size() + state.wheelAxleHeightOffset, isFront, camberSign);
    }

    private void renderBrakeCaliper(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector, boolean frontOnly,
                                     String enabledIdsCsv, String transformsRaw) {

        if ((state.wheelAttachment == null && !usesEmbeddedWheelPivots(state)) || enabledIdsCsv == null || enabledIdsCsv.isEmpty()) {
            return;
        }
        Set<String> enabledIds = parseEnabledIds(enabledIdsCsv);
        net.minecraft.server.packs.resources.ResourceManager resourceManager =
                net.minecraft.client.Minecraft.getInstance().getResourceManager();
        List<PartRef> parts = new ArrayList<>();
        if (frontOnly) {
            for (com.examplebett.render.FrontBrakeCaliperParts.Option option : com.examplebett.render.FrontBrakeCaliperParts.list(resourceManager)) {
                parts.add(new PartRef(option.id(), option.modelId()));
            }
        } else {
            for (com.examplebett.render.RearBrakeCaliperParts.Option option : com.examplebett.render.RearBrakeCaliperParts.list(resourceManager)) {
                parts.add(new PartRef(option.id(), option.modelId()));
            }
        }
        if (parts.isEmpty()) {
            return;
        }
        WheelPositions wp = computeWheelPositions(state);
        float[] offsetX = WHEEL_OFFSET_X_BUF;
        float[] offsetZ = WHEEL_OFFSET_Z_BUF;
        for (PartRef part : parts) {
            if (!enabledIds.contains(part.id())) {
                continue;
            }
            BrakeCaliperModel.Pair pair = BrakeCaliperModel.get(resourceManager, part.modelId());
            if (pair == null) {
                continue;
            }
            com.examplebett.render.PartTransforms.Transform6 t =
                    com.examplebett.render.PartTransforms.resolve6(transformsRaw, part.id(),
                            com.examplebett.entity.CarEntity.defaultBrakeCaliperTransform(part.id()));
            float localX = t.x();
            float localY = t.y();
            float localZ = t.z();
            float localAngle = t.angle();
            float localAngleY = t.angleY();
            float localScale = t.scale();
            for (int i = 0; i < wp.wheelCount(); i++) {
                if (wp.isFront()[i] != frontOnly) {
                    continue;
                }
                poseStack.pushPose();
                poseStack.translate(offsetX[i], wp.axleHeight(), offsetZ[i]);
                if (state.wheelCamberDegrees != 0f) {
                    poseStack.mulPose(com.examplebett.compat.v114.PoseStack.zRotation(wp.camberSign()[i] * state.wheelCamberDegrees));
                }
                if (frontOnly && state.frontWheelSteerDegrees != 0f) {

                    poseStack.mulPose(com.examplebett.compat.v114.PoseStack.yRotation(state.frontWheelSteerDegrees));
                }
                boolean mirrored = offsetX[i] < 0;
                CompiledCarModel activeModel = mirrored ? pair.mirrored() : pair.normal();

                poseStack.translate(mirrored ? -localX : localX, localY, localZ);
                if (localAngleY != 0f) {
                    poseStack.mulPose(com.examplebett.compat.v114.PoseStack.yRotation(localAngleY));
                }
                if (localAngle != 0f) {
                    poseStack.mulPose(com.examplebett.compat.v114.PoseStack.xRotation(localAngle));
                }
                if (localScale != 1f) {
                    poseStack.scale(localScale, localScale, localScale);
                }
                for (CompiledCarModel.TextureGroup group : activeModel.groups) {
                    ResourceLocation texture = group.texture;
                    final int tint = group.tintable ? state.color : 0xFFFFFF;
                    RenderType renderType = groupRenderType(group, texture);
                    collector.submitCustomGeometry(poseStack, renderType, (pose, vertexConsumer) ->
                            renderGroup(group.vertexData, pose, vertexConsumer, state.lightCoords, tint, BodyDeform.NONE, usesTriangles(group)));
                }
                poseStack.popPose();
            }
        }
    }

    private void renderWheelAttachment(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector) {
        if (state.wheelCompiledModel == null || state.wheelAttachment == null) {
            return;
        }

        CompiledCarModel wheelModel = state.wheelCompiledModel;

        CompiledCarModel wheelModelMirrored =
                state.wheelCompiledModelMirrored != null ? state.wheelCompiledModelMirrored : wheelModel;
        if (state.useLodWheel) {
            net.minecraft.server.packs.resources.ResourceManager resourceManager =
                    net.minecraft.client.Minecraft.getInstance().getResourceManager();
            CompiledCarModel lodModel = LodWheelModel.get(resourceManager);
            if (lodModel != null) {
                wheelModel = lodModel;
                CompiledCarModel lodModelMirrored = LodWheelModel.getMirrored(resourceManager);
                wheelModelMirrored = lodModelMirrored != null ? lodModelMirrored : lodModel;
            }
        }
        WheelPositions wp = computeWheelPositions(state);
        int wheelCount = wp.wheelCount();
        float axleHeight = wp.axleHeight();
        float[] offsetZ = WHEEL_OFFSET_Z_BUF;
        float[] offsetX = WHEEL_OFFSET_X_BUF;
        boolean[] isFront = wp.isFront();
        float[] camberSign = wp.camberSign();

        for (int i = 0; i < wheelCount; i++) {
            poseStack.pushPose();
            poseStack.translate(offsetX[i], axleHeight, offsetZ[i]);

            if (state.wheelCamberDegrees != 0f) {
                poseStack.mulPose(com.examplebett.compat.v114.PoseStack.zRotation(camberSign[i] * state.wheelCamberDegrees));
            }
            if (isFront[i]) {
                poseStack.mulPose(com.examplebett.compat.v114.PoseStack.yRotation(state.frontWheelSteerDegrees));
            }

            float rollDegrees = state.wheelRotationDegrees + (isFront[i] ? 0f : state.rearWheelExtraSpinDegrees);
            poseStack.mulPose(com.examplebett.compat.v114.PoseStack.xRotation(rollDegrees));

            boolean mirrored = offsetX[i] < 0;
            CompiledCarModel activeWheelModel = mirrored ? wheelModelMirrored : wheelModel;

            float scaleX = state.wheelScale * state.wheelWidthScale;
            if (scaleX != 1f || state.wheelScale != 1f) {
                poseStack.scale(scaleX, state.wheelScale, state.wheelScale);
            }
            for (CompiledCarModel.TextureGroup group : activeWheelModel.groups) {
                ResourceLocation texture = group.texture;
                boolean usesPaintMask = group.tintable && PaintTintTexture.hasPaint(group.texture);
                if (usesPaintMask) {
                    texture = PaintTintTexture.resolve(group.texture, state.color);
                }
                texture = metalPaintTexture(group, texture, usesPaintMask, state.color);
                final int tint = (group.tintable && !usesPaintMask) ? state.color : 0xFFFFFF;
                RenderType renderType = groupRenderType(group, texture);
                collector.submitCustomGeometry(poseStack, renderType, (pose, vertexConsumer) ->
                        renderGroup(group.vertexData, pose, vertexConsumer, state.lightCoords, tint, BodyDeform.NONE, usesTriangles(group)));
            }
            poseStack.popPose();
        }
    }

    private void renderRearWing(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector) {
        if (state.rearWingEnabledIds == null || state.rearWingEnabledIds.isEmpty()) {
            return;
        }
        Set<String> enabledIds = parseEnabledIds(state.rearWingEnabledIds);
        net.minecraft.server.packs.resources.ResourceManager resourceManager =
                net.minecraft.client.Minecraft.getInstance().getResourceManager();
        com.examplebett.render.PartTransforms.Transform12 defaults = new com.examplebett.render.PartTransforms.Transform12(
                com.examplebett.entity.CarEntity.DEFAULT_REAR_WING_X, com.examplebett.entity.CarEntity.DEFAULT_REAR_WING_Y,
                com.examplebett.entity.CarEntity.DEFAULT_REAR_WING_Z, 0f, 0f, com.examplebett.entity.CarEntity.DEFAULT_REAR_WING_SCALE,
                1f, 1f, 1f, 1f, 1f, 1f);
        for (com.examplebett.render.RearWingParts.Option option : com.examplebett.render.RearWingParts.list(resourceManager)) {
            if (!enabledIds.contains(option.id())) {
                continue;
            }
            CompiledCarModel wingModel = RearWingModel.get(resourceManager, option.modelId());
            if (wingModel == null) {
                continue;
            }
            com.examplebett.render.PartTransforms.Transform12 t =
                    com.examplebett.render.PartTransforms.resolve12(state.rearWingTransformsRaw, option.id(), defaults);
            poseStack.pushPose();
            poseStack.translate(t.x() + widthShiftFor(state, t.x()), t.y(), t.z() + limoShiftFor(state, t.z()));
            if (t.angleY() != 0f) {
                poseStack.mulPose(com.examplebett.compat.v114.PoseStack.yRotation(t.angleY()));
            }
            if (t.angle() != 0f) {
                poseStack.mulPose(com.examplebett.compat.v114.PoseStack.xRotation(t.angle()));
            }

            float stretchScaleX = (t.stretchXPos() + t.stretchXNeg()) / 2f;
            float stretchScaleY = (t.stretchYPos() + t.stretchYNeg()) / 2f;
            float stretchScaleZ = (t.stretchZPos() + t.stretchZNeg()) / 2f;
            float offsetX = t.scale() * OTHER_PART_STRETCH_HALF_EXTENT * (t.stretchXPos() - t.stretchXNeg()) / 2f;
            float offsetY = t.scale() * OTHER_PART_STRETCH_HALF_EXTENT * (t.stretchYPos() - t.stretchYNeg()) / 2f;
            float offsetZ = t.scale() * OTHER_PART_STRETCH_HALF_EXTENT * (t.stretchZPos() - t.stretchZNeg()) / 2f;
            if (offsetX != 0f || offsetY != 0f || offsetZ != 0f) {
                poseStack.translate(offsetX, offsetY, offsetZ);
            }
            if (t.scale() != 1f || stretchScaleX != 1f || stretchScaleY != 1f || stretchScaleZ != 1f) {
                poseStack.scale(t.scale() * stretchScaleX, t.scale() * stretchScaleY, t.scale() * stretchScaleZ);
            }
            for (CompiledCarModel.TextureGroup group : wingModel.groups) {
                ResourceLocation texture = group.texture;
                final int tint = group.tintable ? state.color : 0xFFFFFF;
                RenderType renderType = groupRenderType(group, texture);
                collector.submitCustomGeometry(poseStack, renderType, (pose, vertexConsumer) ->
                        renderGroup(group.vertexData, pose, vertexConsumer, state.lightCoords, tint, BodyDeform.NONE, usesTriangles(group)));
            }
            poseStack.popPose();
        }
    }

    private static final float OTHER_PART_STRETCH_HALF_EXTENT = 0.3f;

    private void renderOtherPart(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector) {
        if (state.otherPartEnabledIds == null || state.otherPartEnabledIds.isEmpty()) {
            return;
        }
        Set<String> enabledIds = parseEnabledIds(state.otherPartEnabledIds);
        net.minecraft.server.packs.resources.ResourceManager resourceManager =
                net.minecraft.client.Minecraft.getInstance().getResourceManager();
        for (com.examplebett.render.OtherParts.Option option : com.examplebett.render.OtherParts.list(resourceManager)) {
            if (!enabledIds.contains(option.id())) {
                continue;
            }

            boolean spareTire = com.examplebett.render.OtherParts.SPARE_TIRE_ID.equals(option.id());

            boolean seatedPlayer = com.examplebett.render.OtherParts.isSeatedCreature(option.id());
            CompiledCarModel partModel = seatedPlayer
                    ? null
                    : spareTire
                            ? state.wheelCompiledModel
                            : OtherPartModel.get(resourceManager, option.modelId());
            if (partModel == null && !seatedPlayer) {
                continue;
            }
            com.examplebett.render.PartTransforms.Transform12 t =
                    com.examplebett.render.PartTransforms.resolve12(state.otherPartTransformsRaw, option.id(),
                            com.examplebett.entity.CarEntity.defaultOtherPartTransform(option.id()));
            poseStack.pushPose();
            poseStack.translate(t.x() + widthShiftFor(state, t.x()), t.y(), t.z() + limoShiftFor(state, t.z()));
            if (t.angleY() != 0f) {
                poseStack.mulPose(com.examplebett.compat.v114.PoseStack.yRotation(t.angleY()));
            }
            if (t.angle() != 0f) {
                poseStack.mulPose(com.examplebett.compat.v114.PoseStack.xRotation(t.angle()));
            }

            float stretchScaleX = (t.stretchXPos() + t.stretchXNeg()) / 2f;
            float stretchScaleY = (t.stretchYPos() + t.stretchYNeg()) / 2f;
            float stretchScaleZ = (t.stretchZPos() + t.stretchZNeg()) / 2f;
            float offsetX = t.scale() * OTHER_PART_STRETCH_HALF_EXTENT * (t.stretchXPos() - t.stretchXNeg()) / 2f;
            float offsetY = t.scale() * OTHER_PART_STRETCH_HALF_EXTENT * (t.stretchYPos() - t.stretchYNeg()) / 2f;
            float offsetZ = t.scale() * OTHER_PART_STRETCH_HALF_EXTENT * (t.stretchZPos() - t.stretchZNeg()) / 2f;
            if (offsetX != 0f || offsetY != 0f || offsetZ != 0f) {
                poseStack.translate(offsetX, offsetY, offsetZ);
            }
            if (t.scale() != 1f || stretchScaleX != 1f || stretchScaleY != 1f || stretchScaleZ != 1f) {
                poseStack.scale(t.scale() * stretchScaleX, t.scale() * stretchScaleY, t.scale() * stretchScaleZ);
            }
            if (spareTire) {

                poseStack.mulPose(com.examplebett.compat.v114.PoseStack.zRotation(90f));
                float scaleX = state.wheelScale * state.wheelWidthScale;
                if (scaleX != 1f || state.wheelScale != 1f) {
                    poseStack.scale(scaleX, state.wheelScale, state.wheelScale);
                }
            }
            if (seatedPlayer) {
                if (com.examplebett.render.OtherParts.SITTING_WOLF_ID.equals(option.id())) {
                    renderSittingWolf(state, poseStack, collector);
                } else {
                    renderSeatedPlayer(state, poseStack, collector,
                            com.examplebett.render.OtherParts.SITTING_ALEX_ID.equals(option.id()));
                }
                poseStack.popPose();
                continue;
            }

            boolean wholeVehicle = com.examplebett.render.OtherParts.R34_ID.equals(option.id());
            int partColor = wholeVehicle ? 0xFFFFFF : state.color;
            for (CompiledCarModel.TextureGroup group : partModel.groups) {
                ResourceLocation texture = group.texture;

                boolean usesPaintMask = group.tintable && PaintTintTexture.hasPaint(group.texture);
                if (usesPaintMask) {
                    texture = PaintTintTexture.resolve(group.texture, partColor);
                }
                texture = metalPaintTexture(group, texture, usesPaintMask, partColor);
                final int tint = (group.tintable && !usesPaintMask) ? partColor : 0xFFFFFF;
                RenderType renderType = groupRenderType(group, texture);
                collector.submitCustomGeometry(poseStack, renderType, (pose, vertexConsumer) ->
                        renderGroup(group.vertexData, pose, vertexConsumer, state.lightCoords, tint, BodyDeform.NONE, usesTriangles(group)));
            }
            poseStack.popPose();
        }
    }

    private static final float TEXT_PART_PIXEL_SIZE = 0.02f;

    private static final float TEXT_PART_LAYER_SPACING = 0.004f;
    private static final int TEXT_PART_MAX_LAYERS = 24;

    private static final float TEXT_PART_SIDE_SHADE = 0.55f;
    private static final int TEXT_PART_FULL_BRIGHT = 0xF000F0;

    private static final int TEXT_PART_CACHE_LIMIT = 256;
    private static final Map<String, com.examplebett.render.TextParts.Slot[]> TEXT_PARTS_CACHE = new HashMap<>();
    private static final Map<String, String> TEXT_SEQUENCE_CACHE = new HashMap<>();

    private static com.examplebett.render.TextParts.Slot[] parseTextParts(String raw) {
        if (TEXT_PARTS_CACHE.size() > TEXT_PART_CACHE_LIMIT) {
            TEXT_PARTS_CACHE.clear();
        }
        return TEXT_PARTS_CACHE.computeIfAbsent(raw, com.examplebett.render.TextParts::parse);
    }

    private static String textPartSequence(com.examplebett.render.TextParts.Slot slot) {
        if (TEXT_SEQUENCE_CACHE.size() > TEXT_PART_CACHE_LIMIT) {
            TEXT_SEQUENCE_CACHE.clear();
        }
        return TEXT_SEQUENCE_CACHE.computeIfAbsent(slot.font() + "\n" + slot.text(), key -> {
            net.minecraft.network.chat.Component component = com.examplebett.compat.ModText.literal(slot.text()).copy();
            if (!com.examplebett.render.TextParts.FONT_DEFAULT.equals(slot.font())) {
                ResourceLocation fontId = AiObjectMod.id("text_" + slot.font());

                component = component;
            }

            return component.getString();
        });
    }

    private static final ResourceLocation UNDER_NEON_TEXTURE =
            AiObjectMod.id("textures/entity/lights/under_neon_glow.png");
    private static final Map<String, com.examplebett.render.UnderNeon.Slot[]> UNDER_NEON_CACHE = new HashMap<>();

    private void renderUnderNeon(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector) {
        if (state.underNeonRaw == null || state.underNeonRaw.isEmpty()) {
            return;
        }
        if (UNDER_NEON_CACHE.size() > TEXT_PART_CACHE_LIMIT) {
            UNDER_NEON_CACHE.clear();
        }
        com.examplebett.render.UnderNeon.Slot[] slots = UNDER_NEON_CACHE.computeIfAbsent(state.underNeonRaw, com.examplebett.render.UnderNeon::parse);
        RenderType neonType = RenderType.entityTranslucentCull(UNDER_NEON_TEXTURE);
        for (com.examplebett.render.UnderNeon.Slot slot : slots) {
            if (slot == null || !slot.enabled()) {
                continue;
            }
            float r = ((slot.color() >> 16) & 0xFF) / 255f;
            float g = ((slot.color() >> 8) & 0xFF) / 255f;
            float b = (slot.color() & 0xFF) / 255f;
            float alpha = slot.brightness();
            float hw = slot.width() / 2f;
            float hl = slot.length() / 2f;
            poseStack.pushPose();
            poseStack.translate(slot.x(), slot.y(), slot.z());
            poseStack.mulPose(com.examplebett.compat.v114.PoseStack.yRotation(slot.yaw()));
            collector.late().submitCustomGeometry(poseStack, neonType, (pose, consumer) -> {
                addGlowVertex(consumer, pose, -hw, 0f, -hl, 0f, 0f, r, g, b, alpha);
                addGlowVertex(consumer, pose, -hw, 0f, hl, 0f, 1f, r, g, b, alpha);
                addGlowVertex(consumer, pose, hw, 0f, hl, 1f, 1f, r, g, b, alpha);
                addGlowVertex(consumer, pose, hw, 0f, -hl, 1f, 0f, r, g, b, alpha);

                addGlowVertex(consumer, pose, -hw, 0f, -hl, 0f, 0f, r, g, b, alpha);
                addGlowVertex(consumer, pose, hw, 0f, -hl, 1f, 0f, r, g, b, alpha);
                addGlowVertex(consumer, pose, hw, 0f, hl, 1f, 1f, r, g, b, alpha);
                addGlowVertex(consumer, pose, -hw, 0f, hl, 0f, 1f, r, g, b, alpha);
            });
            poseStack.popPose();
        }
    }

    private static final Map<String, com.examplebett.render.BrandLogos.Slot[]> BRAND_LOGO_CACHE = new HashMap<>();

    private static final float BRAND_LOGO_HEIGHT = 0.12f;

    private static final float BRAND_LOGO_RECESS_RATIO = 0.5f;

    private static final int BRAND_LOGO_GLINT_GRID = 16;
    private static final float BRAND_LOGO_GLINT_OFFSET = 0.0005f;
    private static final float BRAND_LOGO_GLINT_STRENGTH = 0.6f;

    private void renderBrandLogos(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector) {
        if (state.brandLogosRaw == null || state.brandLogosRaw.isEmpty() || state.brandLogoDetail == 0) {
            return;
        }
        boolean detailed = state.brandLogoDetail >= 2;
        if (BRAND_LOGO_CACHE.size() > TEXT_PART_CACHE_LIMIT) {
            BRAND_LOGO_CACHE.clear();
        }
        com.examplebett.render.BrandLogos.Slot[] slots = BRAND_LOGO_CACHE.computeIfAbsent(state.brandLogosRaw, com.examplebett.render.BrandLogos::parse);
        boolean glint = com.examplebett.client.AiObjectModClient.isFakeReflectionEnabled();
        for (com.examplebett.render.BrandLogos.Slot slot : slots) {
            if (slot == null || !slot.enabled()) {
                continue;
            }

            int light = slot.glow() ? TEXT_PART_FULL_BRIGHT : state.lightCoords;
            BrandLogoTextures.Design design = BrandLogoTextures.design(slot.logo());

            BrandLogoMesh.Mesh mesh = design.flat() ? null : BrandLogoMesh.get(design);
            float height = BRAND_LOGO_HEIGHT * slot.scale();
            float pixel = height / (mesh != null ? mesh.height() : design.height());
            float halfW = (mesh != null ? mesh.width() : design.width()) * pixel / 2f;
            float halfH = height / 2f;
            float depth = slot.depth();
            float[] levelZ = {0f, depth * (1f - BRAND_LOGO_RECESS_RATIO), depth};

            poseStack.pushPose();
            poseStack.translate(slot.x() + widthShiftFor(state, slot.x()), slot.y(), slot.z() + limoShiftFor(state, slot.z()));
            poseStack.mulPose(com.examplebett.compat.v114.PoseStack.yRotation(slot.yaw()));
            poseStack.mulPose(com.examplebett.compat.v114.PoseStack.xRotation(slot.pitch()));
            poseStack.mulPose(com.examplebett.compat.v114.PoseStack.zRotation(slot.roll()));
            if (design.flat()) {

                int layers = detailed
                        ? Math.min(TEXT_PART_MAX_LAYERS, Math.max(1, (int) Math.ceil(depth / TEXT_PART_LAYER_SPACING)))
                        : 0;
                collector.submitCustomGeometry(poseStack, RenderType.entityCutoutNoCull(design.texture()), (pose, consumer) -> {
                    for (int i = 0; i < layers; i++) {
                        addBrandLogoFace(consumer, pose, halfW, halfH, depth * i / layers, 1f, light, 1f, TEXT_PART_SIDE_SHADE);
                    }
                    addBrandLogoFace(consumer, pose, halfW, halfH, depth, 1f, light, 1f);
                });
                poseStack.popPose();
                continue;
            }
            collector.submitCustomGeometry(poseStack, RenderType.entityCutoutNoCull(design.texture()), (pose, consumer) -> {
                addBrandLogoFace(consumer, pose, halfW, halfH, levelZ[0], 1f, light, -1f);
                addBrandLogoFace(consumer, pose, halfW, halfH, levelZ[1], 1f, light, 1f);
                if (!detailed) {
                    return;
                }
                for (BrandLogoMesh.Wall wall : mesh.walls()) {
                    float x0 = -halfW + wall.x0() * pixel;
                    float y0 = halfH - wall.y0() * pixel;
                    float x1 = -halfW + wall.x1() * pixel;
                    float y1 = halfH - wall.y1() * pixel;
                    float zLow = levelZ[wall.lowLevel()];
                    float zHigh = levelZ[wall.highLevel()];

                    float nx = wall.nx();
                    float ny = -wall.ny();
                    addBrandLogoVertex(consumer, pose, x0, y0, zLow, wall.u0(), wall.v0(), light, 1f, nx, ny, 0f);
                    addBrandLogoVertex(consumer, pose, x1, y1, zLow, wall.u1(), wall.v1(), light, 1f, nx, ny, 0f);
                    addBrandLogoVertex(consumer, pose, x1, y1, zHigh, wall.u1(), wall.v1(), light, 1f, nx, ny, 0f);
                    addBrandLogoVertex(consumer, pose, x0, y0, zHigh, wall.u0(), wall.v0(), light, 1f, nx, ny, 0f);
                }
            });
            collector.submitCustomGeometry(poseStack, RenderType.entityCutoutNoCull(design.raisedTexture()), (pose, consumer) ->
                    addBrandLogoFace(consumer, pose, halfW, halfH, levelZ[2], 1f, light, 1f));
            if (glint && detailed) {
                collector.submitCustomGeometry(poseStack, RenderType.entityTranslucent(design.glintTexture()), (pose, consumer) ->
                        addBrandLogoGlint(consumer, pose, halfW, halfH, levelZ[2] + BRAND_LOGO_GLINT_OFFSET, light));
            }
            poseStack.popPose();
        }
    }

    private void addBrandLogoFace(VertexConsumer consumer, PoseStack.Pose pose, float halfW, float halfH, float z, float alpha, int light, float nz) {
        addBrandLogoFace(consumer, pose, halfW, halfH, z, alpha, light, nz, 1f);
    }

    private void addBrandLogoFace(VertexConsumer consumer, PoseStack.Pose pose, float halfW, float halfH, float z, float alpha, int light, float nz,
                                  float shade) {
        addBrandLogoVertex(consumer, pose, -halfW, -halfH, z, 0f, 1f, light, alpha, 0f, 0f, nz, shade);
        addBrandLogoVertex(consumer, pose, halfW, -halfH, z, 1f, 1f, light, alpha, 0f, 0f, nz, shade);
        addBrandLogoVertex(consumer, pose, halfW, halfH, z, 1f, 0f, light, alpha, 0f, 0f, nz, shade);
        addBrandLogoVertex(consumer, pose, -halfW, halfH, z, 0f, 0f, light, alpha, 0f, 0f, nz, shade);
    }

    private void addBrandLogoGlint(VertexConsumer consumer, PoseStack.Pose pose, float halfW, float halfH, float z, int light) {

        float[] camera = PoseMatrices.transformPosition(PoseMatrices.invert(PoseMatrices.capture(pose)), 0f, 0f, 0f);
        float toCameraX = camera[0];
        float toCameraY = camera[1];
        float toCameraZ = camera[2] - z;
        float toCameraLength = (float) Math.sqrt(toCameraX * toCameraX + toCameraY * toCameraY + toCameraZ * toCameraZ);
        if (toCameraLength < 1e-4f) {
            return;
        }
        float phase = 0.5f + 1.1f * (toCameraX / toCameraLength * 0.8f + toCameraY / toCameraLength * 0.6f);
        int blockLight = light & 0xFFFF;
        int skyLight = (light >> 16) & 0xFFFF;
        float strength = BRAND_LOGO_GLINT_STRENGTH * Math.max(blockLight, skyLight) / 240f;
        if (strength <= 0f) {
            return;
        }
        int n = BRAND_LOGO_GLINT_GRID;
        float[][] alpha = new float[n + 1][n + 1];
        boolean any = false;
        for (int j = 0; j <= n; j++) {
            for (int i = 0; i <= n; i++) {
                float u = (float) i / n;
                float v = (float) j / n;

                float s = (u * 0.8f + (1f - v) * 0.6f) / 1.4f;
                float value = Math.min(1f, glintBand(s, phase, 0.14f) + 0.6f * glintBand(s, phase - 0.24f, 0.05f)) * strength;
                alpha[j][i] = value;
                any |= value > 0.001f;
            }
        }
        if (!any) {
            return;
        }
        for (int j = 0; j < n; j++) {
            for (int i = 0; i < n; i++) {
                if (alpha[j][i] <= 0.001f && alpha[j][i + 1] <= 0.001f && alpha[j + 1][i] <= 0.001f && alpha[j + 1][i + 1] <= 0.001f) {
                    continue;
                }
                float u0 = (float) i / n;
                float u1 = (float) (i + 1) / n;
                float v0 = (float) j / n;
                float v1 = (float) (j + 1) / n;
                float x0 = -halfW + u0 * 2f * halfW;
                float x1 = -halfW + u1 * 2f * halfW;
                float y0 = halfH - v0 * 2f * halfH;
                float y1 = halfH - v1 * 2f * halfH;
                addBrandLogoVertex(consumer, pose, x0, y1, z, u0, v1, light, alpha[j + 1][i], 0f, 0f, 1f);
                addBrandLogoVertex(consumer, pose, x1, y1, z, u1, v1, light, alpha[j + 1][i + 1], 0f, 0f, 1f);
                addBrandLogoVertex(consumer, pose, x1, y0, z, u1, v0, light, alpha[j][i + 1], 0f, 0f, 1f);
                addBrandLogoVertex(consumer, pose, x0, y0, z, u0, v0, light, alpha[j][i], 0f, 0f, 1f);
            }
        }
    }

    private static float glintBand(float s, float center, float halfWidth) {
        return MetalTextConsumer.glintBand(s, center, halfWidth);
    }

    private void addBrandLogoVertex(VertexConsumer consumer, PoseStack.Pose pose, float x, float y, float z, float u, float v,
                                    int packedLight, float alpha, float nx, float ny, float nz) {
        addBrandLogoVertex(consumer, pose, x, y, z, u, v, packedLight, alpha, nx, ny, nz, 1f);
    }

    private void addBrandLogoVertex(VertexConsumer consumer, PoseStack.Pose pose, float x, float y, float z, float u, float v,
                                    int packedLight, float alpha, float nx, float ny, float nz, float shade) {
        consumer.vertex(pose.pose(), x, y, z)
                .color(shade, shade, shade, alpha)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(pose.normal(), nx, ny, nz)
                .endVertex();
    }

    private void renderTextParts(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector) {
        if (state.textPartsRaw == null || state.textPartsRaw.isEmpty()) {
            return;
        }
        net.minecraft.client.gui.Font font = net.minecraft.client.Minecraft.getInstance().font;
        for (com.examplebett.render.TextParts.Slot slot : parseTextParts(state.textPartsRaw)) {
            if (slot == null || !slot.visible()) {
                continue;
            }
            String text = textPartSequence(slot);
            font = com.examplebett.compat.LegacyGui.textPartFont(slot.font());
            com.examplebett.compat.LegacyGui.setCurrentFont(font);
            float left = -com.examplebett.compat.LegacyGui.width(font, text) / 2f;
            float top = -font.lineHeight / 2f;
            int light = slot.glow() ? TEXT_PART_FULL_BRIGHT : state.lightCoords;
            int rgb = slot.color() & 0xFFFFFF;
            int sideRgb = ((int) (((rgb >> 16) & 0xFF) * TEXT_PART_SIDE_SHADE) << 16)
                    | ((int) (((rgb >> 8) & 0xFF) * TEXT_PART_SIDE_SHADE) << 8)
                    | (int) ((rgb & 0xFF) * TEXT_PART_SIDE_SHADE);
            float pixel = TEXT_PART_PIXEL_SIZE * slot.scale();

            float pixelX = pixel * slot.width();
            float pixelY = pixel * slot.height();

            poseStack.pushPose();
            poseStack.translate(slot.x() + widthShiftFor(state, slot.x()), slot.y(), slot.z() + limoShiftFor(state, slot.z()));
            poseStack.mulPose(com.examplebett.compat.v114.PoseStack.yRotation(slot.yaw()));
            poseStack.mulPose(com.examplebett.compat.v114.PoseStack.xRotation(slot.pitch()));
            poseStack.mulPose(com.examplebett.compat.v114.PoseStack.zRotation(slot.roll()));
            int layers = slot.depth() > 0f
                    ? Math.min(TEXT_PART_MAX_LAYERS, Math.max(1, (int) Math.ceil(slot.depth() / TEXT_PART_LAYER_SPACING)))
                    : 0;
            for (int i = layers; i >= 1; i--) {
                poseStack.pushPose();
                poseStack.translate(0f, 0f, -slot.depth() * i / layers);
                poseStack.scale(pixelX, -pixelY, pixel);
                collector.submitText(poseStack, text, left, top, 0xFF000000 | sideRgb, light);
                poseStack.popPose();
            }

            poseStack.scale(pixelX, -pixelY, pixel);
            boolean drawn = false;
            int metal = -1;
            if (metal >= 0) {
                submitMetalText(poseStack, collector, font, text, left, top, rgb, metal, light, state.brandLogoDetail >= 2);
                drawn = true;
            }
            if (!drawn) {
                collector.submitText(poseStack, text, left, top, 0xFF000000 | rgb, light);
            }
            poseStack.popPose();
        }
    }

    private void submitMetalText(PoseStack poseStack, GeometrySubmitter collector, net.minecraft.client.gui.Font font,
                                 String text, float left, float top, int rgb, int metal,
                                 int light, boolean band) {
        float width = Math.max(1f, com.examplebett.compat.LegacyGui.width(font, text));
        float height = font.lineHeight;
        collector.submitWrappedText(poseStack, text, left, top, light, (out, pose) -> out);
    }

    private static void computeExhaustOutlets(CarRenderState state, PoseStack poseStack, float[] origin) {
        int outlets = com.examplebett.render.ExhaustParts.outletsOf(state.exhaustEnabledId);
        com.examplebett.render.PartTransforms.Transform6 t = state.exhaustTransform;
        if (outlets == 0 || t == null) {
            state.exhaustTarget.clientExhaustOutlets = null;
            return;
        }
        float[] relative = PoseMatrices.relative(origin, PoseMatrices.capture(poseStack));
        float[] result = new float[outlets * 6];
        for (int i = 0; i < outlets; i++) {
            float side = i == 0 ? 1f : -1f;
            float localX = t.x() * side;
            float[] position = PoseMatrices.transformPosition(relative,
                    localX + widthShiftFor(state, localX), t.y(), t.z() + limoShiftFor(state, t.z()));

            float ax = (float) Math.toRadians(t.angle());
            float ay = (float) Math.toRadians(t.angleY() * side);
            float y1 = (float) Math.sin(ax);
            float z1 = -(float) Math.cos(ax);
            float x2 = z1 * (float) Math.sin(ay);
            float z2 = z1 * (float) Math.cos(ay);
            float[] direction = PoseMatrices.transformDirection(relative, x2, y1, z2);
            result[i * 6] = position[0];
            result[i * 6 + 1] = position[1];
            result[i * 6 + 2] = position[2];
            result[i * 6 + 3] = direction[0];
            result[i * 6 + 4] = direction[1];
            result[i * 6 + 5] = direction[2];
        }
        state.exhaustTarget.clientExhaustOutlets = result;
    }

    private void renderTaxiSign(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector) {
        if (state.taxiSignEnabledIds == null || state.taxiSignEnabledIds.isEmpty()) {
            return;
        }
        Set<String> enabledIds = parseEnabledIds(state.taxiSignEnabledIds);
        net.minecraft.server.packs.resources.ResourceManager resourceManager =
                net.minecraft.client.Minecraft.getInstance().getResourceManager();
        com.examplebett.render.PartTransforms.Transform12 defaults = new com.examplebett.render.PartTransforms.Transform12(
                com.examplebett.entity.CarEntity.DEFAULT_TAXI_SIGN_X, com.examplebett.entity.CarEntity.DEFAULT_TAXI_SIGN_Y,
                com.examplebett.entity.CarEntity.DEFAULT_TAXI_SIGN_Z, 0f, 0f, com.examplebett.entity.CarEntity.DEFAULT_TAXI_SIGN_SCALE,
                1f, 1f, 1f, 1f, 1f, 1f);
        for (com.examplebett.render.TaxiSignParts.Option option : com.examplebett.render.TaxiSignParts.list(resourceManager)) {
            if (!enabledIds.contains(option.id())) {
                continue;
            }
            CompiledCarModel signModel = TaxiSignModel.get(resourceManager, option.modelId());
            if (signModel == null) {
                continue;
            }
            com.examplebett.render.PartTransforms.Transform12 t =
                    com.examplebett.render.PartTransforms.resolve12(state.taxiSignTransformsRaw, option.id(), defaults);
            poseStack.pushPose();
            poseStack.translate(t.x() + widthShiftFor(state, t.x()), t.y(), t.z() + limoShiftFor(state, t.z()));
            if (t.angleY() != 0f) {
                poseStack.mulPose(com.examplebett.compat.v114.PoseStack.yRotation(t.angleY()));
            }
            if (t.angle() != 0f) {
                poseStack.mulPose(com.examplebett.compat.v114.PoseStack.xRotation(t.angle()));
            }

            float stretchScaleX = (t.stretchXPos() + t.stretchXNeg()) / 2f;
            float stretchScaleY = (t.stretchYPos() + t.stretchYNeg()) / 2f;
            float stretchScaleZ = (t.stretchZPos() + t.stretchZNeg()) / 2f;
            float offsetX = t.scale() * OTHER_PART_STRETCH_HALF_EXTENT * (t.stretchXPos() - t.stretchXNeg()) / 2f;
            float offsetY = t.scale() * OTHER_PART_STRETCH_HALF_EXTENT * (t.stretchYPos() - t.stretchYNeg()) / 2f;
            float offsetZ = t.scale() * OTHER_PART_STRETCH_HALF_EXTENT * (t.stretchZPos() - t.stretchZNeg()) / 2f;
            if (offsetX != 0f || offsetY != 0f || offsetZ != 0f) {
                poseStack.translate(offsetX, offsetY, offsetZ);
            }
            if (t.scale() != 1f || stretchScaleX != 1f || stretchScaleY != 1f || stretchScaleZ != 1f) {
                poseStack.scale(t.scale() * stretchScaleX, t.scale() * stretchScaleY, t.scale() * stretchScaleZ);
            }
            for (CompiledCarModel.TextureGroup group : signModel.groups) {
                ResourceLocation texture = group.texture;
                final int tint = group.tintable ? state.color : 0xFFFFFF;
                RenderType renderType = groupRenderType(group, texture);
                collector.submitCustomGeometry(poseStack, renderType, (pose, vertexConsumer) ->
                        renderGroup(group.vertexData, pose, vertexConsumer, state.lightCoords, tint, BodyDeform.NONE, usesTriangles(group)));
            }
            poseStack.popPose();
        }
    }

    private void renderPoliceLight(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector) {
        if (state.policeLightEnabledIds == null || state.policeLightEnabledIds.isEmpty()) {
            return;
        }
        Set<String> enabledIds = parseEnabledIds(state.policeLightEnabledIds);
        net.minecraft.server.packs.resources.ResourceManager resourceManager =
                net.minecraft.client.Minecraft.getInstance().getResourceManager();
        com.examplebett.render.PartTransforms.Transform12 defaults = new com.examplebett.render.PartTransforms.Transform12(
                com.examplebett.entity.CarEntity.DEFAULT_POLICE_LIGHT_X, com.examplebett.entity.CarEntity.DEFAULT_POLICE_LIGHT_Y,
                com.examplebett.entity.CarEntity.DEFAULT_POLICE_LIGHT_Z, 0f, 0f, com.examplebett.entity.CarEntity.DEFAULT_POLICE_LIGHT_SCALE,
                1f, 1f, 1f, 1f, 1f, 1f);
        for (com.examplebett.render.PoliceLightParts.Option option : com.examplebett.render.PoliceLightParts.list(resourceManager)) {
            if (!enabledIds.contains(option.id())) {
                continue;
            }

            CompiledCarModel lightModel = option.modelId() == null ? null : PoliceLightModel.get(resourceManager, option.modelId());
            if (lightModel == null && option.modelId() != null) {
                continue;
            }
            com.examplebett.render.PartTransforms.Transform12 t =
                    com.examplebett.render.PartTransforms.resolve12(state.policeLightTransformsRaw, option.id(), defaults);
            poseStack.pushPose();
            poseStack.translate(t.x() + widthShiftFor(state, t.x()), t.y(), t.z() + limoShiftFor(state, t.z()));
            if (t.angleY() != 0f) {
                poseStack.mulPose(com.examplebett.compat.v114.PoseStack.yRotation(t.angleY()));
            }
            if (t.angle() != 0f) {
                poseStack.mulPose(com.examplebett.compat.v114.PoseStack.xRotation(t.angle()));
            }

            float stretchScaleX = (t.stretchXPos() + t.stretchXNeg()) / 2f;
            float stretchScaleY = (t.stretchYPos() + t.stretchYNeg()) / 2f;
            float stretchScaleZ = (t.stretchZPos() + t.stretchZNeg()) / 2f;
            float offsetX = t.scale() * OTHER_PART_STRETCH_HALF_EXTENT * (t.stretchXPos() - t.stretchXNeg()) / 2f;
            float offsetY = t.scale() * OTHER_PART_STRETCH_HALF_EXTENT * (t.stretchYPos() - t.stretchYNeg()) / 2f;
            float offsetZ = t.scale() * OTHER_PART_STRETCH_HALF_EXTENT * (t.stretchZPos() - t.stretchZNeg()) / 2f;
            if (offsetX != 0f || offsetY != 0f || offsetZ != 0f) {
                poseStack.translate(offsetX, offsetY, offsetZ);
            }
            if (t.scale() != 1f || stretchScaleX != 1f || stretchScaleY != 1f || stretchScaleZ != 1f) {
                poseStack.scale(t.scale() * stretchScaleX, t.scale() * stretchScaleY, t.scale() * stretchScaleZ);
            }
            for (CompiledCarModel.TextureGroup group : lightModel == null ? List.<CompiledCarModel.TextureGroup>of() : lightModel.groups) {
                ResourceLocation texture = group.texture;
                final int tint = group.tintable ? state.color : 0xFFFFFF;
                RenderType renderType = groupRenderType(group, texture);
                collector.submitCustomGeometry(poseStack, renderType, (pose, vertexConsumer) ->
                        renderGroup(group.vertexData, pose, vertexConsumer, state.lightCoords, tint, BodyDeform.NONE, usesTriangles(group)));
            }
            if (state.policeLightsOn) {
                if (com.examplebett.render.PoliceLightParts.isEmergencyBeacon(option.id())) {
                    renderEmergencyBeaconLights(state, poseStack, collector);
                }
                if (com.examplebett.render.PoliceLightParts.JP_LIGHT_BAR_ID.equals(option.id())) {
                    renderJpLightBarLights(state, poseStack, collector);
                }
                if (com.examplebett.render.PoliceLightParts.hasUsLightBarLights(option.id())) {
                    renderUsLightBarLights(state, poseStack, collector, false);
                }
                if (com.examplebett.render.PoliceLightParts.isEuLightBar(option.id())) {
                    renderUsLightBarLights(state, poseStack, collector, true);
                }
                if (com.examplebett.render.PoliceLightParts.isEuPattern(option.id())) {
                    renderEuPatternLights(state, poseStack, collector,
                            com.examplebett.render.PoliceLightParts.isEuPatternLeftFirst(option.id()));
                }
                if (com.examplebett.render.PoliceLightParts.JP_PATTERN_ID.equals(option.id())) {
                    renderJpPatternLights(state, poseStack, collector);
                }
            }
            poseStack.popPose();
        }
    }

    private static final float ROTATING_LIGHT_ROTATE_DEG_PER_TICK = 15f;

    private static final int QUAD_FLASH_ON_TICKS = 3;
    private static final int QUAD_FLASH_GAP_TICKS = 3;
    private static final int QUAD_FLASH_GROUP_GAP_TICKS = 4;
    private static final int QUAD_FLASH_GROUP_SLOT_TICKS =
            2 * QUAD_FLASH_ON_TICKS + QUAD_FLASH_GAP_TICKS + QUAD_FLASH_GROUP_GAP_TICKS;
    private static final int QUAD_FLASH_CYCLE_TICKS = QUAD_FLASH_GROUP_SLOT_TICKS * 2;

    private record QuadFlashState(boolean groupATurn, boolean flashOn) {
    }

    private static QuadFlashState computeQuadFlashState(int age) {
        int cyclePos = age % QUAD_FLASH_CYCLE_TICKS;
        boolean groupATurn = cyclePos < QUAD_FLASH_GROUP_SLOT_TICKS;
        int localPhase = groupATurn ? cyclePos : cyclePos - QUAD_FLASH_GROUP_SLOT_TICKS;
        boolean flashOn = localPhase < QUAD_FLASH_ON_TICKS
                || (localPhase >= QUAD_FLASH_ON_TICKS + QUAD_FLASH_GAP_TICKS
                        && localPhase < 2 * QUAD_FLASH_ON_TICKS + QUAD_FLASH_GAP_TICKS);
        return new QuadFlashState(groupATurn, flashOn);
    }

    private static final float EMERGENCY_BEACON_RADIUS = 0.18f;

    private static final float EMERGENCY_BEACON_PIVOT_Z = -0.165f;

    private static final float EMERGENCY_BEACON_GLOW_SIZE = 0.36f;

    private static final float EMERGENCY_BEACON_SECOND_SET_OFFSET_DEG = 90f;

    private void renderEmergencyBeaconLights(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector) {
        float rotateDeg = (state.age * ROTATING_LIGHT_ROTATE_DEG_PER_TICK) % 360f;
        QuadFlashState flash = computeQuadFlashState(state.age);
        if (!flash.flashOn()) {
            return;
        }
        float setRotateDeg = flash.groupATurn() ? rotateDeg : rotateDeg + EMERGENCY_BEACON_SECOND_SET_OFFSET_DEG;
        submitRotatingLightGlow(poseStack, collector, 0f, 0f, EMERGENCY_BEACON_PIVOT_Z,
                EMERGENCY_BEACON_RADIUS, EMERGENCY_BEACON_GLOW_SIZE, setRotateDeg, 1f, 1f, 0f, 0f);
        submitRotatingLightGlow(poseStack, collector, 0f, 0f, EMERGENCY_BEACON_PIVOT_Z,
                EMERGENCY_BEACON_RADIUS, EMERGENCY_BEACON_GLOW_SIZE, setRotateDeg + 180f, 1f, 1f, 0f, 0f);
    }

    private void submitRotatingLightGlow(PoseStack poseStack, GeometrySubmitter collector,
                                          float pivotX, float pivotY, float pivotZ, float radius, float glowSize,
                                          float rotateDeg, float alpha, float tintR, float tintG, float tintB) {
        double rotateRad = Math.toRadians(rotateDeg);
        float dx = (float) (Math.sin(rotateRad) * radius);
        float dz = (float) (Math.cos(rotateRad) * radius);
        submitGlow(poseStack, collector, HEADLIGHT_GLOW_TEXTURE,
                pivotX + dx, pivotY, pivotZ + dz, glowSize, 0f,
                tintR, tintG, tintB, alpha);
    }

    private static final float LIGHT_BAR_ROTATOR_RADIUS = 0.15f;

    private static final float LIGHT_BAR_ROTATOR_GLOW_SIZE = 0.30f;

    private static final float[][] JP_LIGHT_BAR_ROTATOR_POSITIONS = {
            {-0.80f, 0.181f, -0.205f},
            {-0.35f, 0.179f, -0.015f},
            {0.35f, 0.177f, 0.011f},
            {0.80f, 0.173f, -0.205f},
    };

    private void renderJpLightBarLights(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector) {
        float rotateDeg = (state.age * ROTATING_LIGHT_ROTATE_DEG_PER_TICK) % 360f;
        QuadFlashState flash = computeQuadFlashState(state.age);
        if (!flash.flashOn()) {
            return;
        }

        for (int i = 0; i < JP_LIGHT_BAR_ROTATOR_POSITIONS.length; i++) {
            boolean isCenterSet = i == 1 || i == 2;
            if (isCenterSet != flash.groupATurn()) {
                continue;
            }
            float[] pos = JP_LIGHT_BAR_ROTATOR_POSITIONS[i];
            submitRotatingLightGlow(poseStack, collector, pos[0], pos[1], pos[2],
                    LIGHT_BAR_ROTATOR_RADIUS, LIGHT_BAR_ROTATOR_GLOW_SIZE, rotateDeg, 1f, 1f, 0f, 0f);
            submitRotatingLightGlow(poseStack, collector, pos[0], pos[1], pos[2],
                    LIGHT_BAR_ROTATOR_RADIUS, LIGHT_BAR_ROTATOR_GLOW_SIZE, rotateDeg + 180f, 1f, 1f, 0f, 0f);
        }
    }

    private static final float[][] US_LIGHT_BAR_ROTATOR_POSITIONS = {
            {-0.80f, 0.138f, 0.015f},
            {-0.35f, 0.138f, 0.015f},
            {0.35f, 0.138f, 0.015f},
            {0.80f, 0.138f, 0.015f},
    };

    private void renderUsLightBarLights(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector,
                                        boolean allBlue) {
        float rotateDeg = (state.age * ROTATING_LIGHT_ROTATE_DEG_PER_TICK) % 360f;
        QuadFlashState flash = computeQuadFlashState(state.age);
        if (!flash.flashOn()) {
            return;
        }

        for (int i = 0; i < US_LIGHT_BAR_ROTATOR_POSITIONS.length; i++) {
            boolean isCenterSet = i == 1 || i == 2;
            if (isCenterSet != flash.groupATurn()) {
                continue;
            }
            float[] pos = US_LIGHT_BAR_ROTATOR_POSITIONS[i];
            boolean isBlue = allBlue || i >= 2;
            float tintR = isBlue ? 0f : 1f;
            float tintB = isBlue ? 1f : 0f;
            submitRotatingLightGlow(poseStack, collector, pos[0], pos[1], pos[2],
                    LIGHT_BAR_ROTATOR_RADIUS, LIGHT_BAR_ROTATOR_GLOW_SIZE, rotateDeg, 1f, tintR, 0f, tintB);
            submitRotatingLightGlow(poseStack, collector, pos[0], pos[1], pos[2],
                    LIGHT_BAR_ROTATOR_RADIUS, LIGHT_BAR_ROTATOR_GLOW_SIZE, rotateDeg + 180f, 1f, tintR, 0f, tintB);
        }
    }

    private static final int EU_PATTERN_STEP_TICKS = 2;

    private static final int JP_PATTERN_DARK_TICKS = 10;

    private static final float[] SIDE_PAIR_PATTERN_X = {-0.475f, -0.325f, 0.325f, 0.475f};

    private static final boolean SIDE_PAIR_PATTERN_SINGLE_LIGHT = true;

    private void submitSidePairPatternGlow(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector,
                                           int index, float tintR, float tintB) {
        float x = SIDE_PAIR_PATTERN_X[index];
        if (SIDE_PAIR_PATTERN_SINGLE_LIGHT) {

            if (state.age % EU_PATTERN_STEP_TICKS >= EU_PATTERN_STEP_TICKS / 2) {
                return;
            }

            x = index < 2 ? (SIDE_PAIR_PATTERN_X[0] + SIDE_PAIR_PATTERN_X[1]) / 2f
                    : (SIDE_PAIR_PATTERN_X[2] + SIDE_PAIR_PATTERN_X[3]) / 2f;
        }
        float[] usPos = US_LIGHT_BAR_ROTATOR_POSITIONS[index];
        submitGlow(poseStack, collector, HEADLIGHT_GLOW_TEXTURE, x, usPos[1], usPos[2],
                LIGHT_BAR_ROTATOR_GLOW_SIZE, 0f, tintR, 0f, tintB, 1f);
    }

    private void renderEuPatternLights(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector,
                                       boolean leftFirst) {
        int step = (state.age / EU_PATTERN_STEP_TICKS) % 8;
        boolean firstSide = step < 4;
        boolean leftSide = firstSide == leftFirst;

        int outer = leftSide ? 0 : 3;
        int inner = leftSide ? 1 : 2;
        submitSidePairPatternGlow(state, poseStack, collector, (step % 2 == 0) ? outer : inner, 0f, 1f);
    }

    private void renderJpPatternLights(CarRenderState state, PoseStack poseStack, GeometrySubmitter collector) {
        int flashTicks = EU_PATTERN_STEP_TICKS * 4;
        int pos = state.age % (flashTicks + JP_PATTERN_DARK_TICKS);
        if (pos >= flashTicks) {
            return;
        }
        boolean outerTurn = (pos / EU_PATTERN_STEP_TICKS) % 2 == 0;
        submitSidePairPatternGlow(state, poseStack, collector, outerTurn ? 0 : 1, 1f, 0f);
        submitSidePairPatternGlow(state, poseStack, collector, outerTurn ? 3 : 2, 1f, 0f);
    }

    private static void applyGroupTransform(PoseStack poseStack, CarRenderState state,
                                             CompiledCarModel.TextureGroup group, boolean spinning, boolean rotorSpinning,
                                             float extraZ) {

        if ((spinning || rotorSpinning)
                && (state.suspensionPitchDegrees != 0f || state.suspensionRollDegrees != 0f)) {
            poseStack.translate(0.0, state.tumblePivotHeight, 0.0);
            if (state.suspensionRollDegrees != 0f) {
                poseStack.mulPose(com.examplebett.compat.v114.PoseStack.zRotation(-state.suspensionRollDegrees));
            }
            if (state.suspensionPitchDegrees != 0f) {
                poseStack.mulPose(com.examplebett.compat.v114.PoseStack.xRotation(-state.suspensionPitchDegrees));
            }
            poseStack.translate(0.0, -state.tumblePivotHeight, 0.0);
        }
        if (spinning) {
            ObjModel.WheelPivot pivot = state.compiledModel.wheelPivots.get(group.wheelGroup);

            if (state.bodyHeightOffset != 0f) {
                poseStack.translate(0.0, -state.bodyHeightOffset, 0.0);
            }

            float sideSign = embeddedWheelSideSign(pivot);
            poseStack.translate(embeddedWheelX(state, pivot), embeddedWheelY(state, pivot),
                    embeddedWheelZ(state, pivot.centerZ + extraZ, pivot.front));

            if (state.wheelCamberDegrees != 0f) {
                poseStack.mulPose(com.examplebett.compat.v114.PoseStack.zRotation(sideSign * state.wheelCamberDegrees));
            }

            if (pivot.front) {
                poseStack.mulPose(com.examplebett.compat.v114.PoseStack.yRotation(state.frontWheelSteerDegrees));
            }

            float rollDegrees = state.wheelRotationDegrees + (pivot.front ? 0f : state.rearWheelExtraSpinDegrees);
            poseStack.mulPose(wheelAxisRotation(pivot, rollDegrees));

            float axialScale = state.wheelScale * state.wheelWidthScale;
            if (axialScale != 1f || state.wheelScale != 1f) {
                poseStack.scale(pivot.axisX >= 0.5f ? axialScale : state.wheelScale,
                        pivot.axisY >= 0.5f ? axialScale : state.wheelScale,
                        pivot.axisZ >= 0.5f ? axialScale : state.wheelScale);
            }
            poseStack.translate(-pivot.centerX, -pivot.centerY, -pivot.centerZ);
        } else if (rotorSpinning) {
            ObjModel.RotorPivot pivot = state.compiledModel.rotorPivots.get(group.rotorGroup);
            poseStack.translate(pivot.centerX, pivot.centerY, pivot.centerZ);
            poseStack.mulPose(rotorAxisRotation(pivot, state.rotorRotationDegrees));
            poseStack.translate(-pivot.centerX, -pivot.centerY, -pivot.centerZ);
        }
    }

    private static float embeddedWheelSideSign(ObjModel.WheelPivot pivot) {
        return pivot.centerX >= 0f ? 1f : -1f;
    }

    private static float embeddedWheelX(CarRenderState state, ObjModel.WheelPivot pivot) {
        float x = pivot.centerX + embeddedWheelSideSign(pivot) * state.wheelTrackHalfWidthOffset;
        return x + widthShiftFor(state, x);
    }

    private static float embeddedWheelY(CarRenderState state, ObjModel.WheelPivot pivot) {
        return pivot.centerY + state.wheelAxleHeightOffset;
    }

    private static float embeddedWheelZ(CarRenderState state, float baseZ, boolean front) {
        float z = baseZ + (front ? state.wheelFrontZOffset : state.wheelRearZOffset);
        return z + limoShiftFor(state, z);
    }

    private static com.mojang.math.Quaternion wheelAxisRotation(ObjModel.WheelPivot pivot, float degrees) {
        if (pivot.axisX >= 0.5f) {
            return com.examplebett.compat.v114.PoseStack.xRotation(degrees);
        }
        if (pivot.axisY >= 0.5f) {
            return com.examplebett.compat.v114.PoseStack.yRotation(degrees);
        }
        return com.examplebett.compat.v114.PoseStack.zRotation(degrees);
    }

    private static com.mojang.math.Quaternion rotorAxisRotation(ObjModel.RotorPivot pivot, float degrees) {
        if (pivot.axisX >= 0.5f) {
            return com.examplebett.compat.v114.PoseStack.xRotation(degrees);
        }
        if (pivot.axisY >= 0.5f) {
            return com.examplebett.compat.v114.PoseStack.yRotation(degrees);
        }
        return com.examplebett.compat.v114.PoseStack.zRotation(degrees);
    }

    private static final float PLATE_HALF_WIDTH = 0.45f;

    private void submitPlate(PoseStack poseStack, GeometrySubmitter collector, int packedLight, ResourceLocation texture,
                              float x, float y, float z, float scale, float yawDeg, float pitchDeg, float rollDeg, float aspect) {
        poseStack.pushPose();
        poseStack.translate(x, y, z);
        poseStack.mulPose(com.examplebett.compat.v114.PoseStack.yRotation(yawDeg));
        poseStack.mulPose(com.examplebett.compat.v114.PoseStack.xRotation(pitchDeg));
        poseStack.mulPose(com.examplebett.compat.v114.PoseStack.zRotation(rollDeg));
        RenderType plateType = RenderType.entityCutoutNoCull(texture);
        collector.submitCustomGeometry(poseStack, plateType, (pose, vertexConsumer) ->
                renderPlate(pose, vertexConsumer, packedLight, scale, aspect));
        poseStack.popPose();
    }

    private void renderPlate(PoseStack.Pose pose, VertexConsumer consumer, int packedLight, float scale, float aspect) {
        float halfW = PLATE_HALF_WIDTH * scale;
        float halfH = (PLATE_HALF_WIDTH / aspect) * scale;
        addPlateVertex(consumer, pose, -halfW, -halfH, 0f, 0f, 1f, packedLight);
        addPlateVertex(consumer, pose, halfW, -halfH, 0f, 1f, 1f, packedLight);
        addPlateVertex(consumer, pose, halfW, halfH, 0f, 1f, 0f, packedLight);
        addPlateVertex(consumer, pose, -halfW, halfH, 0f, 0f, 0f, packedLight);
    }

    private void addPlateVertex(VertexConsumer consumer, PoseStack.Pose pose, float x, float y, float z, float u, float v, int packedLight) {
        consumer.vertex(pose.pose(), x, y, z)
                .color(1f, 1f, 1f, 1f)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(pose.normal(), 0f, 1f, 0f)
                .endVertex();
    }

    private static final ResourceLocation HEADLIGHT_GLOW_TEXTURE =
            AiObjectMod.id("textures/entity/lights/headlight_glow.png");
    private static final ResourceLocation TAILLIGHT_GLOW_TEXTURE =
            AiObjectMod.id("textures/entity/lights/taillight_glow.png");
    private static final float HEADLIGHT_GLOW_SIZE = 0.35f;
    private static final float TAILLIGHT_GLOW_SIZE = 0.25f;

    private static final int FULL_BRIGHT_LIGHT = 0xF000F0;

    private void submitGlow(PoseStack poseStack, GeometrySubmitter collector, ResourceLocation texture,
                             float x, float y, float z, float halfSize, float yawDeg,
                             float tintR, float tintG, float tintB) {
        submitGlow(poseStack, collector, texture, x, y, z, halfSize, yawDeg, tintR, tintG, tintB, 1f);
    }

    private void submitGlow(PoseStack poseStack, GeometrySubmitter collector, ResourceLocation texture,
                             float x, float y, float z, float halfSize, float yawDeg,
                             float tintR, float tintG, float tintB, float alpha) {
        poseStack.pushPose();
        poseStack.translate(x, y, z);

        RenderType glowType = RenderType.entityTranslucentCull(texture);
        submitGlowQuad(poseStack, collector, glowType, halfSize, yawDeg, 0f, tintR, tintG, tintB, alpha);
        submitGlowQuad(poseStack, collector, glowType, halfSize, yawDeg + 90f, 0f, tintR, tintG, tintB, alpha);
        submitGlowQuad(poseStack, collector, glowType, halfSize, yawDeg, 90f, tintR, tintG, tintB, alpha);
        poseStack.popPose();
    }

    private void submitGlowQuad(PoseStack poseStack, GeometrySubmitter collector, RenderType glowType,
                                 float halfSize, float yawDeg, float pitchDeg,
                                 float tintR, float tintG, float tintB, float alpha) {
        poseStack.pushPose();
        poseStack.mulPose(com.examplebett.compat.v114.PoseStack.yRotation(yawDeg));
        poseStack.mulPose(com.examplebett.compat.v114.PoseStack.xRotation(pitchDeg));
        collector.late().submitCustomGeometry(poseStack, glowType, (pose, vertexConsumer) ->
                renderGlow(pose, vertexConsumer, halfSize, tintR, tintG, tintB, alpha));
        poseStack.popPose();
    }

    private void renderGlow(PoseStack.Pose pose, VertexConsumer consumer, float halfSize,
                             float tintR, float tintG, float tintB, float alpha) {
        addGlowVertex(consumer, pose, -halfSize, -halfSize, 0f, 0f, 1f, tintR, tintG, tintB, alpha);
        addGlowVertex(consumer, pose, halfSize, -halfSize, 0f, 1f, 1f, tintR, tintG, tintB, alpha);
        addGlowVertex(consumer, pose, halfSize, halfSize, 0f, 1f, 0f, tintR, tintG, tintB, alpha);
        addGlowVertex(consumer, pose, -halfSize, halfSize, 0f, 0f, 0f, tintR, tintG, tintB, alpha);

        addGlowVertex(consumer, pose, -halfSize, -halfSize, 0f, 0f, 1f, tintR, tintG, tintB, alpha);
        addGlowVertex(consumer, pose, -halfSize, halfSize, 0f, 0f, 0f, tintR, tintG, tintB, alpha);
        addGlowVertex(consumer, pose, halfSize, halfSize, 0f, 1f, 0f, tintR, tintG, tintB, alpha);
        addGlowVertex(consumer, pose, halfSize, -halfSize, 0f, 1f, 1f, tintR, tintG, tintB, alpha);
    }

    private void addGlowVertex(VertexConsumer consumer, PoseStack.Pose pose, float x, float y, float z, float u, float v,
                                float tintR, float tintG, float tintB, float alpha) {
        consumer.vertex(pose.pose(), x, y, z)
                .color(tintR, tintG, tintB, alpha)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(FULL_BRIGHT_LIGHT)
                .normal(pose.normal(), 0f, 1f, 0f)
                .endVertex();
    }

    private record BodyDeform(boolean active, float bodyMinY,
                               float lipStretchScale, float lipWidthScale, float lipBandHeight, float lipFrontExtendScale,
                               float limoAnchorZ, float limoStretchM, float limoZoneM,
                               float widthAnchorX, float widthStretchM, float widthZoneM) {
        static final BodyDeform NONE = new BodyDeform(false, Float.NaN, 1f, 1f, 0f, 1f, Float.NaN, 0f, 0f, Float.NaN, 0f, 0f);

        static BodyDeform from(CarRenderState state) {
            boolean lipActive = !Float.isNaN(state.compiledModel.bodyMinY)
                    && (state.lipStretchScale != 1f || state.lipWidthScale != 1f || state.lipFrontExtendScale != 1f);
            boolean limoActive = !Float.isNaN(state.compiledModel.bodyCenterZ) && state.limoStretchCm != 0f;
            boolean widthActive = !Float.isNaN(state.compiledModel.bodyCenterX) && state.widthStretchCm != 0f;
            if (!lipActive && !limoActive && !widthActive) {
                return NONE;
            }

            float limoAnchorZ = state.compiledModel.bodyCenterZ + state.limoCenterOffsetCm / 100f;

            float widthAnchorX = state.compiledModel.bodyCenterX + state.widthCenterOffsetCm / 100f;
            return new BodyDeform(true, state.compiledModel.bodyMinY,
                    state.lipStretchScale, state.lipWidthScale, state.lipBandHeightCm / 100f, state.lipFrontExtendScale,
                    limoAnchorZ, state.limoStretchCm / 100f, state.limoZoneCm / 100f,
                    widthAnchorX, state.widthStretchCm / 100f, state.widthZoneCm / 100f);
        }
    }

    private static boolean usesTriangles(CompiledCarModel.TextureGroup group) {
        return !group.translucent && LegacyShaderFix.isAvailable();
    }

    private static final ResourceLocation[] METAL_PAINT_TEXTURES = {
            AiObjectMod.id("textures/entity/paint/chrome.png"),
            AiObjectMod.id("textures/entity/paint/gold.png"),
            AiObjectMod.id("textures/entity/paint/metallic_silver.png")
    };

    private static final ResourceLocation BRONZE_PAINT_TEXTURE = AiObjectMod.id("textures/entity/paint/bronze.png");

    private static ResourceLocation metalPaintTexture(CompiledCarModel.TextureGroup group, ResourceLocation texture, boolean usesPaintMask, int color) {
        if (!group.tintable || usesPaintMask || !CompiledCarModel.FALLBACK_TEXTURE.equals(texture)) {
            return texture;
        }
        int metal = com.examplebett.client.gui.CarColorPalette.metalIndex(color);
        if (metal >= 0) {
            return METAL_PAINT_TEXTURES[metal];
        }
        return (color & 0xFFFFFF) == com.examplebett.client.gui.CarColorPalette.EXTRA_RGB[com.examplebett.client.gui.CarColorPalette.EXTRA_BRONZE]
                ? BRONZE_PAINT_TEXTURE
                : texture;
    }

    private static RenderType groupRenderType(CompiledCarModel.TextureGroup group, ResourceLocation texture) {
        if (usesTriangles(group)) {
            return LegacyShaderFix.cutoutType(texture, group.noCull);
        }
        return group.translucent
                ? RenderType.entityTranslucentCull(texture)
                : group.noCull
                        ? RenderType.entityCutoutNoCull(texture)
                        : RenderType.entityCutout(texture);
    }

    private void renderGroup(float[] data, PoseStack.Pose pose, VertexConsumer vertexConsumer, int packedLight, int color, BodyDeform deform, boolean triangles) {
        float tintR = ((color >> 16) & 0xFF) / 255f;
        float tintG = ((color >> 8) & 0xFF) / 255f;
        float tintB = (color & 0xFF) / 255f;
        if (triangles) {

            LegacyShaderFix.markBuffer(vertexConsumer);
        }

        int legacyStart = LegacyShaderFix.legacyBegin(vertexConsumer);
        for (int i = 0; i < data.length; i += 36) {
            addVertex(vertexConsumer, pose, data, i, packedLight, tintR, tintG, tintB, deform);
            addVertex(vertexConsumer, pose, data, i + 12, packedLight, tintR, tintG, tintB, deform);
            addVertex(vertexConsumer, pose, data, i + 24, packedLight, tintR, tintG, tintB, deform);
            if (!triangles) {
                addVertex(vertexConsumer, pose, data, i + 24, packedLight, tintR, tintG, tintB, deform);
            }
        }
        LegacyShaderFix.legacyEnd(vertexConsumer, legacyStart, data, pose, triangles);
    }

    private static float lipWeight(float y, float bodyMinY, float bandHeight) {
        if (bandHeight <= 0f) return 0f;
        float t = (bodyMinY + bandHeight - y) / bandHeight;
        t = Math.max(0f, Math.min(1f, t));
        return t * t * (3f - 2f * t);
    }

    private static float limoWeight(float z, float anchorZ, float zoneM) {
        if (zoneM <= 0f) return 0f;
        float t = (anchorZ - z) / zoneM;
        t = Math.max(0f, Math.min(1f, t));
        return t * t * (3f - 2f * t);
    }

    private static float limoShift(float z, float anchorZ, float zoneM, float stretchM) {
        return -limoWeight(z, anchorZ, zoneM) * stretchM;
    }

    private static float widthWeight(float x, float anchorX, float zoneM) {
        if (zoneM <= 0f) return 0f;
        float t = (anchorX - x) / zoneM;
        t = Math.max(0f, Math.min(1f, t));
        return t * t * (3f - 2f * t);
    }

    private static float widthShift(float x, float anchorX, float zoneM, float stretchM) {
        return -widthWeight(x, anchorX, zoneM) * stretchM;
    }

    private static float limoShiftFor(CarRenderState state, float z) {
        if (Float.isNaN(state.compiledModel.bodyCenterZ) || state.limoStretchCm == 0f) {
            return 0f;
        }
        float anchorZ = state.compiledModel.bodyCenterZ + state.limoCenterOffsetCm / 100f;
        return limoShift(z, anchorZ, state.limoZoneCm / 100f, state.limoStretchCm / 100f);
    }

    private static float widthShiftFor(CarRenderState state, float x) {
        if (Float.isNaN(state.compiledModel.bodyCenterX) || state.widthStretchCm == 0f) {
            return 0f;
        }
        float anchorX = state.compiledModel.bodyCenterX + state.widthCenterOffsetCm / 100f;
        return widthShift(x, anchorX, state.widthZoneCm / 100f, state.widthStretchCm / 100f);
    }

    private void addVertex(VertexConsumer consumer, PoseStack.Pose pose, float[] data, int offset, int packedLight, float tintR, float tintG, float tintB,
                            BodyDeform deform) {
        float x = data[offset];
        float y = data[offset + 1];
        float z = data[offset + 2];
        if (deform.active()) {
            float weight = lipWeight(y, deform.bodyMinY(), deform.lipBandHeight());
            if (weight > 0f) {
                y -= weight * (deform.lipStretchScale() - 1f) * deform.lipBandHeight();
                x += weight * x * (deform.lipWidthScale() - 1f);

                z += weight * Math.max(z, 0f) * (deform.lipFrontExtendScale() - 1f);
            }
            z += limoShift(z, deform.limoAnchorZ(), deform.limoZoneM(), deform.limoStretchM());
            x += widthShift(x, deform.widthAnchorX(), deform.widthZoneM(), deform.widthStretchM());
        }

        consumer.vertex(pose.pose(), x, y, z)
                .color(data[offset + 5] * tintR, data[offset + 6] * tintG, data[offset + 7] * tintB, data[offset + 8])
                .uv(data[offset + 3], data[offset + 4])
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(pose.normal(), data[offset + 9], data[offset + 10], data[offset + 11])
                .endVertex();
    }

    private static final float REFLECTION_STRENGTH = 0.45f;

    private static final float REFLECTION_ANGLE_LOW = 0.85f;
    private static final float REFLECTION_ANGLE_HIGH = 0.99f;

    private static final float REFLECTION_NORMAL_OFFSET = 0.001f;

    private static float fakeReflectionBoost(PoseStack.Pose pose, float x, float y, float z, float nx, float ny, float nz, int packedLight) {
        com.mojang.math.Vector3f normal = new com.mojang.math.Vector3f(nx, ny, nz);
        pose.normal().transform(normal);
        if (normal.dot(normal) < 1e-8f) {
            return 0f;
        }
        normal.normalize();

        com.mojang.math.Vector4f viewDirH = new com.mojang.math.Vector4f(x, y, z, 1f);
        pose.pose().transform(viewDirH);
        com.mojang.math.Vector3f viewDir = new com.mojang.math.Vector3f(viewDirH.x(), viewDirH.y(), viewDirH.z());
        if (viewDir.dot(viewDir) < 1e-8f) {
            return 0f;
        }
        viewDir.normalize();

        float vDotN = viewDir.dot(normal);
        com.mojang.math.Vector3f reflectDir = new com.mojang.math.Vector3f(normal);
        reflectDir.mul(-2f * vDotN);
        reflectDir.add(viewDir.x(), viewDir.y(), viewDir.z());

        float t = (reflectDir.y() - REFLECTION_ANGLE_LOW) / (REFLECTION_ANGLE_HIGH - REFLECTION_ANGLE_LOW);
        t = Math.max(0f, Math.min(1f, t));
        float highlight = t * t * (3f - 2f * t);

        int blockLight = packedLight & 0xFFFF;
        int skyLight = (packedLight >> 16) & 0xFFFF;
        float lightFactor = Math.max(blockLight, skyLight) / 240f;

        return highlight * lightFactor * REFLECTION_STRENGTH;
    }

    private void renderReflectionOverlay(float[] data, PoseStack.Pose pose, VertexConsumer vertexConsumer, int packedLight, BodyDeform deform) {
        for (int i = 0; i < data.length; i += 36) {
            addReflectionVertex(vertexConsumer, pose, data, i, packedLight, deform);
            addReflectionVertex(vertexConsumer, pose, data, i + 12, packedLight, deform);
            addReflectionVertex(vertexConsumer, pose, data, i + 24, packedLight, deform);
            addReflectionVertex(vertexConsumer, pose, data, i + 24, packedLight, deform);
        }
    }

    private void addReflectionVertex(VertexConsumer consumer, PoseStack.Pose pose, float[] data, int offset, int packedLight, BodyDeform deform) {
        float x = data[offset];
        float y = data[offset + 1];
        float z = data[offset + 2];
        if (deform.active()) {
            float weight = lipWeight(y, deform.bodyMinY(), deform.lipBandHeight());
            if (weight > 0f) {
                y -= weight * (deform.lipStretchScale() - 1f) * deform.lipBandHeight();
                x += weight * x * (deform.lipWidthScale() - 1f);
                z += weight * Math.max(z, 0f) * (deform.lipFrontExtendScale() - 1f);
            }
            z += limoShift(z, deform.limoAnchorZ(), deform.limoZoneM(), deform.limoStretchM());
            x += widthShift(x, deform.widthAnchorX(), deform.widthZoneM(), deform.widthStretchM());
        }

        float nx = data[offset + 9];
        float ny = data[offset + 10];
        float nz = data[offset + 11];
        float boost = fakeReflectionBoost(pose, x, y, z, nx, ny, nz, packedLight);
        x += nx * REFLECTION_NORMAL_OFFSET;
        y += ny * REFLECTION_NORMAL_OFFSET;
        z += nz * REFLECTION_NORMAL_OFFSET;

        consumer.vertex(pose.pose(), x, y, z)
                .color(1f, 1f, 1f, boost)
                .uv(data[offset + 3], data[offset + 4])
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(pose.normal(), nx, ny, nz)
                .endVertex();
    }
}
*/
//?}
