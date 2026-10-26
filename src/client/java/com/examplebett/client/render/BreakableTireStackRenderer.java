//~ axis_mulpose_compat
package com.examplebett.client.render;

import com.examplebett.entity.BreakableTireStackEntity;
import com.examplebett.render.CompiledCarModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
//? if >=1.19.3 {
import com.mojang.math.Axis;
//?}
//? if >=1.21.11 {
/*import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;*/
//?} else
import net.minecraft.client.renderer.RenderType;
//? if >=1.21.9 {
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.CameraRenderState;
//?} else
/*import net.minecraft.client.renderer.MultiBufferSource;*/
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

//? if >=1.21.2 {
public class BreakableTireStackRenderer<T extends BreakableTireStackEntity> extends EntityRenderer<T, BreakableTireStackRenderState> {
//?} else
/*public class BreakableTireStackRenderer<T extends BreakableTireStackEntity> extends EntityRenderer<T> {*/

    @FunctionalInterface
    private interface GeometryRenderer {
        void render(PoseStack.Pose pose, VertexConsumer consumer);
    }

    @FunctionalInterface
    private interface GeometrySubmitter {
        void submitCustomGeometry(PoseStack pose, RenderType type, GeometryRenderer renderer);
    }

    public BreakableTireStackRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    //? if <1.21.2 {
    /*
    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return CompiledCarModel.FALLBACK_TEXTURE;
    }
    */
    //?}

    //? if >=1.21.2 {
    @Override
    public BreakableTireStackRenderState createRenderState() {
        return new BreakableTireStackRenderState();
    }

    @Override
    public void extractRenderState(T entity, BreakableTireStackRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        populateState(entity, state, partialTick);
    }
    //?}

    private void populateState(T entity, BreakableTireStackRenderState state, float partialTick) {
        state.lightCoords = this.getPackedLightCoords(entity, partialTick);

        //? if >=1.21.2 {
        state.yRot = entity.getYRot(partialTick);
        state.xRot = entity.getXRot(partialTick);
        //?} else {
        /*state.yRot = entity.getYRot();
        state.xRot = entity.getXRot();*/
        //?}
        state.pivotY = entity.getRenderPivotHeight();
        state.compiledModel = entity.getCompiledModel();
    }

    //? if >=1.21.2 {

    //? if >=1.21.9 {
    @Override
    public void submit(BreakableTireStackRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraRenderState) {
        super.submit(state, poseStack, collector, cameraRenderState);
        renderTireStack(state, poseStack, (pose, type, renderer) -> collector.submitCustomGeometry(pose, type, renderer::render));
    }
    //?} else {
    /*@Override
    public void render(BreakableTireStackRenderState state, PoseStack poseStack, MultiBufferSource bufferSource, int light) {
        super.render(state, poseStack, bufferSource, light);
        renderTireStack(state, poseStack, (pose, type, renderer) -> renderer.render(pose.last(), bufferSource.getBuffer(type)));
    }*/
    //?}
    //?} else {
    /*@Override
    public void render(T entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int light) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, light);
        BreakableTireStackRenderState state = new BreakableTireStackRenderState();
        populateState(entity, state, partialTick);
        state.lightCoords = light;
        renderTireStack(state, poseStack, (pose, type, renderer) -> renderer.render(pose.last(), bufferSource.getBuffer(type)));
    }*/
    //?}

    private void renderTireStack(BreakableTireStackRenderState state, PoseStack poseStack, GeometrySubmitter collector) {
        if (state.compiledModel == null) {
            return;
        }

        poseStack.pushPose();

        poseStack.mulPose(Axis.YP.rotationDegrees(state.yRot));
        poseStack.translate(0.0, state.pivotY, 0.0);
        poseStack.mulPose(Axis.XP.rotationDegrees(state.xRot));
        poseStack.translate(0.0, -state.pivotY, 0.0);

        for (CompiledCarModel.TextureGroup group : state.compiledModel.groups) {
            ResourceLocation texture = group.texture;
            RenderType renderType = group.translucent
                    ? RenderType.itemEntityTranslucentCull(texture)
                    : group.noCull
                            ? RenderType.entityCutoutNoCull(texture)
                            : RenderType.entityCutout(texture);
            collector.submitCustomGeometry(poseStack, renderType, (pose, vertexConsumer) ->
                    renderGroup(group.vertexData, pose, vertexConsumer, state.lightCoords));
        }

        poseStack.popPose();
    }

    private void renderGroup(float[] data, PoseStack.Pose pose, VertexConsumer vertexConsumer, int packedLight) {
        for (int i = 0; i < data.length; i += 36) {
            addVertex(vertexConsumer, pose, data, i, packedLight);
            addVertex(vertexConsumer, pose, data, i + 12, packedLight);
            addVertex(vertexConsumer, pose, data, i + 24, packedLight);
            addVertex(vertexConsumer, pose, data, i + 24, packedLight);
        }
    }

    private void addVertex(VertexConsumer consumer, PoseStack.Pose pose, float[] data, int offset, int packedLight) {
        //? if >=1.20.5 {
        //? if >=1.21 {
        consumer.addVertex(pose, data[offset], data[offset + 1], data[offset + 2])
                .setColor(data[offset + 5], data[offset + 6], data[offset + 7], data[offset + 8])
                .setUv(data[offset + 3], data[offset + 4])
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(packedLight)
                .setNormal(pose, data[offset + 9], data[offset + 10], data[offset + 11]);
        //?} else {
        /*consumer.vertex(pose, data[offset], data[offset + 1], data[offset + 2])
                .color(data[offset + 5], data[offset + 6], data[offset + 7], data[offset + 8])
                .uv(data[offset + 3], data[offset + 4])
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(pose, data[offset + 9], data[offset + 10], data[offset + 11])
                .endVertex();*/
        //?}
        //?} else {
        /*consumer.vertex(pose.pose(), data[offset], data[offset + 1], data[offset + 2])
                .color(data[offset + 5], data[offset + 6], data[offset + 7], data[offset + 8])
                .uv(data[offset + 3], data[offset + 4])
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(pose.normal(), data[offset + 9], data[offset + 10], data[offset + 11])
                .endVertex();*/
        //?}
    }
}
