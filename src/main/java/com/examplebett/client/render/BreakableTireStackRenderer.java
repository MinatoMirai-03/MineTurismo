//? if >=1.18 {
//?}
//? if <1.18 && >=1.17 {
/*
//~ axis_mulpose_compat
package com.examplebett.client.render;

import com.examplebett.entity.BreakableTireStackEntity;
import com.examplebett.render.CompiledCarModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class BreakableTireStackRenderer<T extends BreakableTireStackEntity> extends EntityRenderer<T> {

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

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return CompiledCarModel.FALLBACK_TEXTURE;
    }

    private void populateState(T entity, BreakableTireStackRenderState state, float partialTick) {
        state.lightCoords = this.getPackedLightCoords(entity, partialTick);

        state.yRot = entity.getYRot();
        state.xRot = entity.getXRot();
        state.pivotY = entity.getRenderPivotHeight();
        state.compiledModel = entity.getCompiledModel();
    }

    @Override
    public void render(T entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int light) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, light);
        BreakableTireStackRenderState state = new BreakableTireStackRenderState();
        populateState(entity, state, partialTick);
        state.lightCoords = light;
        renderTireStack(state, poseStack, (pose, type, renderer) -> renderer.render(pose.last(), bufferSource.getBuffer(type)));
    }

    private void renderTireStack(BreakableTireStackRenderState state, PoseStack poseStack, GeometrySubmitter collector) {
        if (state.compiledModel == null) {
            return;
        }

        poseStack.pushPose();

        poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(state.yRot));
        poseStack.translate(0.0, state.pivotY, 0.0);
        poseStack.mulPose(com.mojang.math.Vector3f.XP.rotationDegrees(state.xRot));
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
        consumer.vertex(pose.pose(), data[offset], data[offset + 1], data[offset + 2])
                .color(data[offset + 5], data[offset + 6], data[offset + 7], data[offset + 8])
                .uv(data[offset + 3], data[offset + 4])
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(pose.normal(), data[offset + 9], data[offset + 10], data[offset + 11])
                .endVertex();
    }
}
*/
//?}
//? if <1.17 && >=1.16 {
/*
//~ axis_mulpose_compat
package com.examplebett.client.render;

import com.examplebett.entity.BreakableTireStackEntity;
import com.examplebett.render.CompiledCarModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class BreakableTireStackRenderer<T extends BreakableTireStackEntity> extends EntityRenderer<T> {

    @FunctionalInterface
    private interface GeometryRenderer {
        void render(PoseStack.Pose pose, VertexConsumer consumer);
    }

    @FunctionalInterface
    private interface GeometrySubmitter {
        void submitCustomGeometry(PoseStack pose, RenderType type, GeometryRenderer renderer);
    }

    public BreakableTireStackRenderer(net.minecraft.client.renderer.entity.EntityRenderDispatcher context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return CompiledCarModel.FALLBACK_TEXTURE;
    }

    private void populateState(T entity, BreakableTireStackRenderState state, float partialTick) {
        state.lightCoords = this.getPackedLightCoords(entity, partialTick);

        state.yRot = entity.getYRot();
        state.xRot = entity.getXRot();
        state.pivotY = entity.getRenderPivotHeight();
        state.compiledModel = entity.getCompiledModel();
    }

    @Override
    public void render(T entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int light) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, light);
        BreakableTireStackRenderState state = new BreakableTireStackRenderState();
        populateState(entity, state, partialTick);
        state.lightCoords = light;
        renderTireStack(state, poseStack, (pose, type, renderer) -> renderer.render(pose.last(), bufferSource.getBuffer(type)));
    }

    private void renderTireStack(BreakableTireStackRenderState state, PoseStack poseStack, GeometrySubmitter collector) {
        if (state.compiledModel == null) {
            return;
        }

        poseStack.pushPose();

        poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(state.yRot));
        poseStack.translate(0.0, state.pivotY, 0.0);
        poseStack.mulPose(com.mojang.math.Vector3f.XP.rotationDegrees(state.xRot));
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
        consumer.vertex(pose.pose(), data[offset], data[offset + 1], data[offset + 2])
                .color(data[offset + 5], data[offset + 6], data[offset + 7], data[offset + 8])
                .uv(data[offset + 3], data[offset + 4])
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(pose.normal(), data[offset + 9], data[offset + 10], data[offset + 11])
                .endVertex();
    }
}
*/
//?}
//? if <1.16 && >=1.15 {
/*
//~ axis_mulpose_compat
package com.examplebett.client.render;

import com.examplebett.entity.BreakableTireStackEntity;
import com.examplebett.render.CompiledCarModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class BreakableTireStackRenderer<T extends BreakableTireStackEntity> extends EntityRenderer<T> {

    @FunctionalInterface
    private interface GeometryRenderer {
        void render(PoseStack.Pose pose, VertexConsumer consumer);
    }

    @FunctionalInterface
    private interface GeometrySubmitter {
        void submitCustomGeometry(PoseStack pose, RenderType type, GeometryRenderer renderer);
    }

    public BreakableTireStackRenderer(net.minecraft.client.renderer.entity.EntityRenderDispatcher context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return CompiledCarModel.FALLBACK_TEXTURE;
    }

    private void populateState(T entity, BreakableTireStackRenderState state, float partialTick) {
        state.lightCoords = this.getPackedLightCoords(entity, partialTick);

        state.yRot = entity.getYRot();
        state.xRot = entity.getXRot();
        state.pivotY = entity.getRenderPivotHeight();
        state.compiledModel = entity.getCompiledModel();
    }

    @Override
    public void render(T entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int light) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, light);
        BreakableTireStackRenderState state = new BreakableTireStackRenderState();
        populateState(entity, state, partialTick);
        state.lightCoords = light;
        renderTireStack(state, poseStack, (pose, type, renderer) -> renderer.render(pose.last(), bufferSource.getBuffer(type)));
    }

    private void renderTireStack(BreakableTireStackRenderState state, PoseStack poseStack, GeometrySubmitter collector) {
        if (state.compiledModel == null) {
            return;
        }

        poseStack.pushPose();

        poseStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(state.yRot));
        poseStack.translate(0.0, state.pivotY, 0.0);
        poseStack.mulPose(com.mojang.math.Vector3f.XP.rotationDegrees(state.xRot));
        poseStack.translate(0.0, -state.pivotY, 0.0);

        for (CompiledCarModel.TextureGroup group : state.compiledModel.groups) {
            ResourceLocation texture = group.texture;
            RenderType renderType = group.translucent
                    ? RenderType.entityTranslucentCull(texture)
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
        consumer.vertex(pose.pose(), data[offset], data[offset + 1], data[offset + 2])
                .color(data[offset + 5], data[offset + 6], data[offset + 7], data[offset + 8])
                .uv(data[offset + 3], data[offset + 4])
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(pose.normal(), data[offset + 9], data[offset + 10], data[offset + 11])
                .endVertex();
    }
}
*/
//?}
//? if <1.15 {
/*
//~ axis_mulpose_compat
package com.examplebett.client.render;

import com.examplebett.entity.BreakableTireStackEntity;
import com.examplebett.render.CompiledCarModel;
import com.examplebett.compat.v114.PoseStack;
import com.examplebett.compat.v114.VertexConsumer;

import com.examplebett.compat.v114.RenderType;
import com.examplebett.compat.v114.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import com.examplebett.compat.v114.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class BreakableTireStackRenderer<T extends BreakableTireStackEntity> extends EntityRenderer<T> {

    @FunctionalInterface
    private interface GeometryRenderer {
        void render(PoseStack.Pose pose, VertexConsumer consumer);
    }

    @FunctionalInterface
    private interface GeometrySubmitter {
        void submitCustomGeometry(PoseStack pose, RenderType type, GeometryRenderer renderer);
    }

    public BreakableTireStackRenderer(net.minecraft.client.renderer.entity.EntityRenderDispatcher context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return CompiledCarModel.FALLBACK_TEXTURE;
    }

    private void populateState(T entity, BreakableTireStackRenderState state, float partialTick) {
        state.lightCoords = entity.getLightColor();

        state.yRot = entity.getYRot();
        state.xRot = entity.getXRot();
        state.pivotY = entity.getRenderPivotHeight();
        state.compiledModel = entity.getCompiledModel();
    }

    @Override
    public void render(T entity, double x, double y, double z, float entityYaw, float partialTick) {
        PoseStack poseStack = new PoseStack();
        poseStack.translate(x, y, z);
        MultiBufferSource bufferSource = new MultiBufferSource();
        this.mineturismo$render(entity, entityYaw, partialTick, poseStack, bufferSource, entity.getLightColor());
        bufferSource.endBatch();
        super.render(entity, x, y, z, entityYaw, partialTick);
    }

    private void mineturismo$render(T entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int light) {
        BreakableTireStackRenderState state = new BreakableTireStackRenderState();
        populateState(entity, state, partialTick);
        state.lightCoords = light;
        renderTireStack(state, poseStack, (pose, type, renderer) -> renderer.render(pose.last(), bufferSource.getBuffer(type)));
    }

    private void renderTireStack(BreakableTireStackRenderState state, PoseStack poseStack, GeometrySubmitter collector) {
        if (state.compiledModel == null) {
            return;
        }

        poseStack.pushPose();

        poseStack.mulPose(com.examplebett.compat.v114.PoseStack.yRotation(state.yRot));
        poseStack.translate(0.0, state.pivotY, 0.0);
        poseStack.mulPose(com.examplebett.compat.v114.PoseStack.xRotation(state.xRot));
        poseStack.translate(0.0, -state.pivotY, 0.0);

        for (CompiledCarModel.TextureGroup group : state.compiledModel.groups) {
            ResourceLocation texture = group.texture;
            RenderType renderType = group.translucent
                    ? RenderType.entityTranslucentCull(texture)
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
        consumer.vertex(pose.pose(), data[offset], data[offset + 1], data[offset + 2])
                .color(data[offset + 5], data[offset + 6], data[offset + 7], data[offset + 8])
                .uv(data[offset + 3], data[offset + 4])
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(pose.normal(), data[offset + 9], data[offset + 10], data[offset + 11])
                .endVertex();
    }
}
*/
//?}
