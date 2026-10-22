//? if >=1.18 {
//?}
//? if <1.18 && >=1.17 {
/*
package com.examplebett.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

import net.minecraft.client.model.PlayerModel;

public final class FigureModels {

    private static final float FAKE_RIDER_LEG_XROT = (float) Math.toRadians(-75.0);

    private static final float FAKE_RIDER_BIKE_LEG_XROT_RETAIN_FRACTION = 0.3F;
    private static final float FAKE_RIDER_ARM_XROT = (float) Math.toRadians(-60.0);
    private static final float FAKE_RIDER_ARM_ZROT = (float) Math.toRadians(8.0);

    public static final ResourceLocation STEVE_TEXTURE = vanillaId("textures/entity/steve.png");
    public static final ResourceLocation ALEX_WIDE_TEXTURE = null;
    public static final ResourceLocation ALEX_SLIM_TEXTURE = vanillaId("textures/entity/alex.png");
    public static final ResourceLocation WOLF_TEXTURE = vanillaId("textures/entity/wolf/wolf_tame.png");
    public static final ResourceLocation WOLF_COLLAR_TEXTURE = vanillaId("textures/entity/wolf/wolf_collar.png");

    private static final int WOLF_COLLAR_COLOR = 0xFFB02E26;

    private final PlayerModel fakeRiderWide;
    private final PlayerModel fakeRiderSlim;
    private final PlayerModel seatedWide;
    private final PlayerModel seatedSlim;

    private final net.minecraft.client.model.WolfModel wolf;

    private net.minecraft.world.entity.animal.Wolf wolfEntity;
    private net.minecraft.world.level.Level wolfLevel;

    public FigureModels(EntityRendererProvider.Context context) {
        this.fakeRiderWide = new PlayerModel(context.bakeLayer(net.minecraft.client.model.geom.ModelLayers.PLAYER), false);
        this.fakeRiderSlim = new PlayerModel(context.bakeLayer(net.minecraft.client.model.geom.ModelLayers.PLAYER_SLIM), true);
        this.seatedWide = new PlayerModel(context.bakeLayer(net.minecraft.client.model.geom.ModelLayers.PLAYER), false);
        this.seatedSlim = new PlayerModel(context.bakeLayer(net.minecraft.client.model.geom.ModelLayers.PLAYER_SLIM), true);

        this.wolf = new net.minecraft.client.model.WolfModel(context.bakeLayer(net.minecraft.client.model.geom.ModelLayers.WOLF));
    }

    private static ResourceLocation vanillaId(String path) {
        return new ResourceLocation("minecraft", path);
    }

    public static ResourceLocation fakeRiderTexture(boolean alex) {
        if (!alex) {
            return STEVE_TEXTURE;
        }
        return ALEX_WIDE_TEXTURE != null ? ALEX_WIDE_TEXTURE : ALEX_SLIM_TEXTURE;
    }

    public void renderFakeRider(boolean alex, boolean motorcycle, boolean bikeStationary, float bikeLeanRollDegrees,
                                PoseStack poseStack, VertexConsumer consumer, int light) {
        PlayerModel model = alex && ALEX_WIDE_TEXTURE == null ? this.fakeRiderSlim : this.fakeRiderWide;
        model.head.xRot = 0f;
        model.head.yRot = 0f;
        model.body.xRot = 0f;
        model.rightLeg.yRot = 0f;
        model.rightLeg.zRot = 0f;
        model.leftLeg.yRot = 0f;
        if (motorcycle) {

            model.rightLeg.xRot = FAKE_RIDER_LEG_XROT * FAKE_RIDER_BIKE_LEG_XROT_RETAIN_FRACTION;
            if (bikeStationary) {
                model.leftLeg.xRot = 0f;
                model.leftLeg.zRot = (float) Math.toRadians(bikeLeanRollDegrees);
            } else {
                model.leftLeg.xRot = FAKE_RIDER_LEG_XROT * FAKE_RIDER_BIKE_LEG_XROT_RETAIN_FRACTION;
                model.leftLeg.zRot = 0f;
            }
        } else {
            model.rightLeg.xRot = FAKE_RIDER_LEG_XROT;
            model.leftLeg.xRot = FAKE_RIDER_LEG_XROT;
            model.leftLeg.zRot = 0f;
        }
        model.rightArm.xRot = FAKE_RIDER_ARM_XROT;
        model.rightArm.yRot = 0f;
        model.rightArm.zRot = FAKE_RIDER_ARM_ZROT;
        model.leftArm.xRot = FAKE_RIDER_ARM_XROT;
        model.leftArm.yRot = 0f;
        model.leftArm.zRot = -FAKE_RIDER_ARM_ZROT;
        renderPlayer(model, poseStack, consumer, light);
    }

    public void renderSeatedPlayer(boolean alex, PoseStack poseStack, VertexConsumer consumer, int light) {
        PlayerModel model = alex ? this.seatedSlim : this.seatedWide;
        model.head.xRot = 0f;
        model.head.yRot = 0f;
        model.head.zRot = 0f;
        model.body.xRot = 0f;
        model.body.yRot = 0f;
        model.body.zRot = 0f;
        model.rightArm.xRot = -0.62831855f;
        model.rightArm.yRot = 0f;
        model.rightArm.zRot = 0f;
        model.leftArm.xRot = -0.62831855f;
        model.leftArm.yRot = 0f;
        model.leftArm.zRot = 0f;
        model.rightLeg.xRot = -1.4137167f;
        model.rightLeg.yRot = 0.31415927f;
        model.rightLeg.zRot = 0.07853982f;
        model.leftLeg.xRot = -1.4137167f;
        model.leftLeg.yRot = -0.31415927f;
        model.leftLeg.zRot = -0.07853982f;
        renderPlayer(model, poseStack, consumer, light);
    }

    public void renderSittingWolf(boolean collar, float ageInTicks, PoseStack poseStack, VertexConsumer consumer, int light) {
        if (!this.prepareWolfEntity()) {
            return;
        }
        this.wolf.prepareMobModel(this.wolfEntity, 0f, 0f, 0f);
        this.wolf.setupAnim(this.wolfEntity, 0f, 0f, ageInTicks, 0f, 0f);
        int color = collar ? WOLF_COLLAR_COLOR : 0xFFFFFFFF;
        this.wolf.renderToBuffer(poseStack, consumer, light, OverlayTexture.NO_OVERLAY,
                (color >> 16 & 0xFF) / 255f, (color >> 8 & 0xFF) / 255f, (color & 0xFF) / 255f, 1f);
    }

    private boolean prepareWolfEntity() {
        net.minecraft.world.level.Level level = net.minecraft.client.Minecraft.getInstance().level;
        if (level == null) {
            return false;
        }
        if (this.wolfEntity == null || this.wolfLevel != level) {
            this.wolfEntity = new net.minecraft.world.entity.animal.Wolf(net.minecraft.world.entity.EntityType.WOLF, level);
            this.wolfLevel = level;
            setTame(this.wolfEntity);
            this.wolfEntity.setInSittingPose(true);
        }
        return true;
    }

    private static void setTame(net.minecraft.world.entity.animal.Wolf wolf) {
        wolf.setTame(true);
    }

    private static void renderPlayer(PlayerModel model, PoseStack poseStack, VertexConsumer consumer, int light) {

        model.renderToBuffer(poseStack, consumer, light, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
    }
}
*/
//?}
//? if <1.17 && >=1.16 {
/*
package com.examplebett.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

import net.minecraft.client.model.PlayerModel;

public final class FigureModels {

    private static final float FAKE_RIDER_LEG_XROT = (float) Math.toRadians(-75.0);

    private static final float FAKE_RIDER_BIKE_LEG_XROT_RETAIN_FRACTION = 0.3F;
    private static final float FAKE_RIDER_ARM_XROT = (float) Math.toRadians(-60.0);
    private static final float FAKE_RIDER_ARM_ZROT = (float) Math.toRadians(8.0);

    public static final ResourceLocation STEVE_TEXTURE = vanillaId("textures/entity/steve.png");
    public static final ResourceLocation ALEX_WIDE_TEXTURE = null;
    public static final ResourceLocation ALEX_SLIM_TEXTURE = vanillaId("textures/entity/alex.png");
    public static final ResourceLocation WOLF_TEXTURE = vanillaId("textures/entity/wolf/wolf_tame.png");
    public static final ResourceLocation WOLF_COLLAR_TEXTURE = vanillaId("textures/entity/wolf/wolf_collar.png");

    private static final int WOLF_COLLAR_COLOR = 0xFFB02E26;

    private final PlayerModel fakeRiderWide;
    private final PlayerModel fakeRiderSlim;
    private final PlayerModel seatedWide;
    private final PlayerModel seatedSlim;

    private final net.minecraft.client.model.WolfModel wolf;

    private net.minecraft.world.entity.animal.Wolf wolfEntity;
    private net.minecraft.world.level.Level wolfLevel;

    public FigureModels(net.minecraft.client.renderer.entity.EntityRenderDispatcher context) {
        this.fakeRiderWide = new PlayerModel(0.0F, false);
        this.fakeRiderSlim = new PlayerModel(0.0F, true);
        this.seatedWide = new PlayerModel(0.0F, false);
        this.seatedSlim = new PlayerModel(0.0F, true);

        this.wolf = new net.minecraft.client.model.WolfModel();
    }

    private static ResourceLocation vanillaId(String path) {
        return new ResourceLocation("minecraft", path);
    }

    public static ResourceLocation fakeRiderTexture(boolean alex) {
        if (!alex) {
            return STEVE_TEXTURE;
        }
        return ALEX_WIDE_TEXTURE != null ? ALEX_WIDE_TEXTURE : ALEX_SLIM_TEXTURE;
    }

    public void renderFakeRider(boolean alex, boolean motorcycle, boolean bikeStationary, float bikeLeanRollDegrees,
                                PoseStack poseStack, VertexConsumer consumer, int light) {
        PlayerModel model = alex && ALEX_WIDE_TEXTURE == null ? this.fakeRiderSlim : this.fakeRiderWide;
        model.head.xRot = 0f;
        model.head.yRot = 0f;
        model.body.xRot = 0f;
        model.rightLeg.yRot = 0f;
        model.rightLeg.zRot = 0f;
        model.leftLeg.yRot = 0f;
        if (motorcycle) {

            model.rightLeg.xRot = FAKE_RIDER_LEG_XROT * FAKE_RIDER_BIKE_LEG_XROT_RETAIN_FRACTION;
            if (bikeStationary) {
                model.leftLeg.xRot = 0f;
                model.leftLeg.zRot = (float) Math.toRadians(bikeLeanRollDegrees);
            } else {
                model.leftLeg.xRot = FAKE_RIDER_LEG_XROT * FAKE_RIDER_BIKE_LEG_XROT_RETAIN_FRACTION;
                model.leftLeg.zRot = 0f;
            }
        } else {
            model.rightLeg.xRot = FAKE_RIDER_LEG_XROT;
            model.leftLeg.xRot = FAKE_RIDER_LEG_XROT;
            model.leftLeg.zRot = 0f;
        }
        model.rightArm.xRot = FAKE_RIDER_ARM_XROT;
        model.rightArm.yRot = 0f;
        model.rightArm.zRot = FAKE_RIDER_ARM_ZROT;
        model.leftArm.xRot = FAKE_RIDER_ARM_XROT;
        model.leftArm.yRot = 0f;
        model.leftArm.zRot = -FAKE_RIDER_ARM_ZROT;
        renderPlayer(model, poseStack, consumer, light);
    }

    public void renderSeatedPlayer(boolean alex, PoseStack poseStack, VertexConsumer consumer, int light) {
        PlayerModel model = alex ? this.seatedSlim : this.seatedWide;
        model.head.xRot = 0f;
        model.head.yRot = 0f;
        model.head.zRot = 0f;
        model.body.xRot = 0f;
        model.body.yRot = 0f;
        model.body.zRot = 0f;
        model.rightArm.xRot = -0.62831855f;
        model.rightArm.yRot = 0f;
        model.rightArm.zRot = 0f;
        model.leftArm.xRot = -0.62831855f;
        model.leftArm.yRot = 0f;
        model.leftArm.zRot = 0f;
        model.rightLeg.xRot = -1.4137167f;
        model.rightLeg.yRot = 0.31415927f;
        model.rightLeg.zRot = 0.07853982f;
        model.leftLeg.xRot = -1.4137167f;
        model.leftLeg.yRot = -0.31415927f;
        model.leftLeg.zRot = -0.07853982f;
        renderPlayer(model, poseStack, consumer, light);
    }

    public void renderSittingWolf(boolean collar, float ageInTicks, PoseStack poseStack, VertexConsumer consumer, int light) {
        if (!this.prepareWolfEntity()) {
            return;
        }
        this.wolf.prepareMobModel(this.wolfEntity, 0f, 0f, 0f);
        this.wolf.setupAnim(this.wolfEntity, 0f, 0f, ageInTicks, 0f, 0f);
        int color = collar ? WOLF_COLLAR_COLOR : 0xFFFFFFFF;
        this.wolf.renderToBuffer(poseStack, consumer, light, OverlayTexture.NO_OVERLAY,
                (color >> 16 & 0xFF) / 255f, (color >> 8 & 0xFF) / 255f, (color & 0xFF) / 255f, 1f);
    }

    private boolean prepareWolfEntity() {
        net.minecraft.world.level.Level level = net.minecraft.client.Minecraft.getInstance().level;
        if (level == null) {
            return false;
        }
        if (this.wolfEntity == null || this.wolfLevel != level) {
            this.wolfEntity = new net.minecraft.world.entity.animal.Wolf(net.minecraft.world.entity.EntityType.WOLF, level);
            this.wolfLevel = level;
            setTame(this.wolfEntity);
            this.wolfEntity.setInSittingPose(true);
        }
        return true;
    }

    private static void setTame(net.minecraft.world.entity.animal.Wolf wolf) {
        wolf.setTame(true);
    }

    private static void renderPlayer(PlayerModel model, PoseStack poseStack, VertexConsumer consumer, int light) {

        model.renderToBuffer(poseStack, consumer, light, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
    }
}
*/
//?}
//? if <1.16 && >=1.15 {
/*
package com.examplebett.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

import net.minecraft.client.model.PlayerModel;

public final class FigureModels {

    private static final float FAKE_RIDER_LEG_XROT = (float) Math.toRadians(-75.0);

    private static final float FAKE_RIDER_BIKE_LEG_XROT_RETAIN_FRACTION = 0.3F;
    private static final float FAKE_RIDER_ARM_XROT = (float) Math.toRadians(-60.0);
    private static final float FAKE_RIDER_ARM_ZROT = (float) Math.toRadians(8.0);

    public static final ResourceLocation STEVE_TEXTURE = vanillaId("textures/entity/steve.png");
    public static final ResourceLocation ALEX_WIDE_TEXTURE = null;
    public static final ResourceLocation ALEX_SLIM_TEXTURE = vanillaId("textures/entity/alex.png");
    public static final ResourceLocation WOLF_TEXTURE = vanillaId("textures/entity/wolf/wolf_tame.png");
    public static final ResourceLocation WOLF_COLLAR_TEXTURE = vanillaId("textures/entity/wolf/wolf_collar.png");

    private static final int WOLF_COLLAR_COLOR = 0xFFB02E26;

    private final PlayerModel fakeRiderWide;
    private final PlayerModel fakeRiderSlim;
    private final PlayerModel seatedWide;
    private final PlayerModel seatedSlim;

    private final net.minecraft.client.model.WolfModel wolf;

    private net.minecraft.world.entity.animal.Wolf wolfEntity;
    private net.minecraft.world.level.Level wolfLevel;

    public FigureModels(net.minecraft.client.renderer.entity.EntityRenderDispatcher context) {
        this.fakeRiderWide = new PlayerModel(0.0F, false);
        this.fakeRiderSlim = new PlayerModel(0.0F, true);
        this.seatedWide = new PlayerModel(0.0F, false);
        this.seatedSlim = new PlayerModel(0.0F, true);

        this.wolf = new net.minecraft.client.model.WolfModel();
    }

    private static ResourceLocation vanillaId(String path) {
        return new ResourceLocation("minecraft", path);
    }

    public static ResourceLocation fakeRiderTexture(boolean alex) {
        if (!alex) {
            return STEVE_TEXTURE;
        }
        return ALEX_WIDE_TEXTURE != null ? ALEX_WIDE_TEXTURE : ALEX_SLIM_TEXTURE;
    }

    public void renderFakeRider(boolean alex, boolean motorcycle, boolean bikeStationary, float bikeLeanRollDegrees,
                                PoseStack poseStack, VertexConsumer consumer, int light) {
        PlayerModel model = alex && ALEX_WIDE_TEXTURE == null ? this.fakeRiderSlim : this.fakeRiderWide;
        model.head.xRot = 0f;
        model.head.yRot = 0f;
        model.body.xRot = 0f;
        model.rightLeg.yRot = 0f;
        model.rightLeg.zRot = 0f;
        model.leftLeg.yRot = 0f;
        if (motorcycle) {

            model.rightLeg.xRot = FAKE_RIDER_LEG_XROT * FAKE_RIDER_BIKE_LEG_XROT_RETAIN_FRACTION;
            if (bikeStationary) {
                model.leftLeg.xRot = 0f;
                model.leftLeg.zRot = (float) Math.toRadians(bikeLeanRollDegrees);
            } else {
                model.leftLeg.xRot = FAKE_RIDER_LEG_XROT * FAKE_RIDER_BIKE_LEG_XROT_RETAIN_FRACTION;
                model.leftLeg.zRot = 0f;
            }
        } else {
            model.rightLeg.xRot = FAKE_RIDER_LEG_XROT;
            model.leftLeg.xRot = FAKE_RIDER_LEG_XROT;
            model.leftLeg.zRot = 0f;
        }
        model.rightArm.xRot = FAKE_RIDER_ARM_XROT;
        model.rightArm.yRot = 0f;
        model.rightArm.zRot = FAKE_RIDER_ARM_ZROT;
        model.leftArm.xRot = FAKE_RIDER_ARM_XROT;
        model.leftArm.yRot = 0f;
        model.leftArm.zRot = -FAKE_RIDER_ARM_ZROT;
        renderPlayer(model, poseStack, consumer, light);
    }

    public void renderSeatedPlayer(boolean alex, PoseStack poseStack, VertexConsumer consumer, int light) {
        PlayerModel model = alex ? this.seatedSlim : this.seatedWide;
        model.head.xRot = 0f;
        model.head.yRot = 0f;
        model.head.zRot = 0f;
        model.body.xRot = 0f;
        model.body.yRot = 0f;
        model.body.zRot = 0f;
        model.rightArm.xRot = -0.62831855f;
        model.rightArm.yRot = 0f;
        model.rightArm.zRot = 0f;
        model.leftArm.xRot = -0.62831855f;
        model.leftArm.yRot = 0f;
        model.leftArm.zRot = 0f;
        model.rightLeg.xRot = -1.4137167f;
        model.rightLeg.yRot = 0.31415927f;
        model.rightLeg.zRot = 0.07853982f;
        model.leftLeg.xRot = -1.4137167f;
        model.leftLeg.yRot = -0.31415927f;
        model.leftLeg.zRot = -0.07853982f;
        renderPlayer(model, poseStack, consumer, light);
    }

    public void renderSittingWolf(boolean collar, float ageInTicks, PoseStack poseStack, VertexConsumer consumer, int light) {
        if (!this.prepareWolfEntity()) {
            return;
        }
        this.wolf.prepareMobModel(this.wolfEntity, 0f, 0f, 0f);
        this.wolf.setupAnim(this.wolfEntity, 0f, 0f, ageInTicks, 0f, 0f);
        int color = collar ? WOLF_COLLAR_COLOR : 0xFFFFFFFF;
        this.wolf.renderToBuffer(poseStack, consumer, light, OverlayTexture.NO_OVERLAY,
                (color >> 16 & 0xFF) / 255f, (color >> 8 & 0xFF) / 255f, (color & 0xFF) / 255f, 1f);
    }

    private boolean prepareWolfEntity() {
        net.minecraft.world.level.Level level = net.minecraft.client.Minecraft.getInstance().level;
        if (level == null) {
            return false;
        }
        if (this.wolfEntity == null || this.wolfLevel != level) {
            this.wolfEntity = new net.minecraft.world.entity.animal.Wolf(net.minecraft.world.entity.EntityType.WOLF, level);
            this.wolfLevel = level;
            setTame(this.wolfEntity);
            this.wolfEntity.setSitting(true);
        }
        return true;
    }

    private static void setTame(net.minecraft.world.entity.animal.Wolf wolf) {
        wolf.setTame(true);
    }

    private static void renderPlayer(PlayerModel model, PoseStack poseStack, VertexConsumer consumer, int light) {

        model.renderToBuffer(poseStack, consumer, light, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
    }
}
*/
//?}
//? if <1.15 {
/*
package com.examplebett.client.render;

import com.examplebett.compat.v114.PoseStack;
import com.examplebett.compat.v114.VertexConsumer;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.WolfModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.level.Level;

public final class FigureModels {
    private static final float FAKE_RIDER_LEG_XROT = (float) Math.toRadians(-75.0);
    private static final float FAKE_RIDER_BIKE_LEG_XROT_RETAIN_FRACTION = 0.3F;
    private static final float FAKE_RIDER_ARM_XROT = (float) Math.toRadians(-60.0);
    private static final float FAKE_RIDER_ARM_ZROT = (float) Math.toRadians(8.0);
    private static final float MODEL_SCALE = 0.0625f;

    public static final ResourceLocation STEVE_TEXTURE = new ResourceLocation("minecraft", "textures/entity/steve.png");
    public static final ResourceLocation ALEX_WIDE_TEXTURE = null;
    public static final ResourceLocation ALEX_SLIM_TEXTURE = new ResourceLocation("minecraft", "textures/entity/alex.png");
    public static final ResourceLocation WOLF_TEXTURE = new ResourceLocation("minecraft", "textures/entity/wolf/wolf_tame.png");
    public static final ResourceLocation WOLF_COLLAR_TEXTURE = new ResourceLocation("minecraft", "textures/entity/wolf/wolf_collar.png");
    private static final int WOLF_COLLAR_COLOR = 0xFFB02E26;

    private final PlayerModel<?> fakeRiderWide = new PlayerModel<>(0.0F, false);
    private final PlayerModel<?> fakeRiderSlim = new PlayerModel<>(0.0F, true);
    private final PlayerModel<?> seatedWide = new PlayerModel<>(0.0F, false);
    private final PlayerModel<?> seatedSlim = new PlayerModel<>(0.0F, true);
    private final WolfModel<Wolf> wolf = new WolfModel<>();
    private Wolf wolfEntity;
    private Level wolfLevel;

    public FigureModels(EntityRenderDispatcher context) {
    }

    public static ResourceLocation fakeRiderTexture(boolean alex) {
        return alex ? ALEX_SLIM_TEXTURE : STEVE_TEXTURE;
    }

    public void renderFakeRider(boolean alex, boolean motorcycle, boolean bikeStationary, float bikeLeanRollDegrees,
                                PoseStack poseStack, VertexConsumer consumer, int light) {
        PlayerModel<?> model = alex ? this.fakeRiderSlim : this.fakeRiderWide;
        model.head.xRot = 0f;
        model.head.yRot = 0f;
        model.body.xRot = 0f;
        model.rightLeg.yRot = 0f;
        model.rightLeg.zRot = 0f;
        model.leftLeg.yRot = 0f;
        if (motorcycle) {
            model.rightLeg.xRot = FAKE_RIDER_LEG_XROT * FAKE_RIDER_BIKE_LEG_XROT_RETAIN_FRACTION;
            if (bikeStationary) {
                model.leftLeg.xRot = 0f;
                model.leftLeg.zRot = (float) Math.toRadians(bikeLeanRollDegrees);
            } else {
                model.leftLeg.xRot = FAKE_RIDER_LEG_XROT * FAKE_RIDER_BIKE_LEG_XROT_RETAIN_FRACTION;
                model.leftLeg.zRot = 0f;
            }
        } else {
            model.rightLeg.xRot = FAKE_RIDER_LEG_XROT;
            model.leftLeg.xRot = FAKE_RIDER_LEG_XROT;
            model.leftLeg.zRot = 0f;
        }
        model.rightArm.xRot = FAKE_RIDER_ARM_XROT;
        model.rightArm.yRot = 0f;
        model.rightArm.zRot = FAKE_RIDER_ARM_ZROT;
        model.leftArm.xRot = FAKE_RIDER_ARM_XROT;
        model.leftArm.yRot = 0f;
        model.leftArm.zRot = -FAKE_RIDER_ARM_ZROT;
        renderPlayer(model, poseStack, consumer, light);
    }

    public void renderSeatedPlayer(boolean alex, PoseStack poseStack, VertexConsumer consumer, int light) {
        PlayerModel<?> model = alex ? this.seatedSlim : this.seatedWide;
        model.head.xRot = 0f;
        model.head.yRot = 0f;
        model.head.zRot = 0f;
        model.body.xRot = 0f;
        model.body.yRot = 0f;
        model.body.zRot = 0f;
        model.rightArm.xRot = -0.62831855f;
        model.rightArm.yRot = 0f;
        model.rightArm.zRot = 0f;
        model.leftArm.xRot = -0.62831855f;
        model.leftArm.yRot = 0f;
        model.leftArm.zRot = 0f;
        model.rightLeg.xRot = -1.4137167f;
        model.rightLeg.yRot = 0.31415927f;
        model.rightLeg.zRot = 0.07853982f;
        model.leftLeg.xRot = -1.4137167f;
        model.leftLeg.yRot = -0.31415927f;
        model.leftLeg.zRot = -0.07853982f;
        renderPlayer(model, poseStack, consumer, light);
    }

    public void renderSittingWolf(boolean collar, float ageInTicks, PoseStack poseStack, VertexConsumer consumer, int light) {
        Level level = net.minecraft.client.Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        if (this.wolfEntity == null || this.wolfLevel != level) {
            this.wolfEntity = new Wolf(net.minecraft.world.entity.EntityType.WOLF, level);
            this.wolfLevel = level;
            this.wolfEntity.setTame(true);
            this.wolfEntity.setSitting(true);
        }
        int color = collar ? WOLF_COLLAR_COLOR : 0xFFFFFFFF;
        Wolf entity = this.wolfEntity;
        WolfModel<Wolf> model = this.wolf;
        consumer.drawGl(poseStack.last().pose(), light,
                (color >> 16 & 0xFF) / 255f, (color >> 8 & 0xFF) / 255f, (color & 0xFF) / 255f, () -> {
                    model.prepareMobModel(entity, 0f, 0f, 0f);
                    model.render(entity, 0f, 0f, ageInTicks, 0f, 0f, MODEL_SCALE);
                });
    }

    private static void renderPlayer(PlayerModel<?> model, PoseStack poseStack, VertexConsumer consumer, int light) {
        consumer.drawGl(poseStack.last().pose(), light, 1f, 1f, 1f, () -> {
            model.hat.copyFrom(model.head);
            model.jacket.copyFrom(model.body);
            model.leftSleeve.copyFrom(model.leftArm);
            model.rightSleeve.copyFrom(model.rightArm);
            model.leftPants.copyFrom(model.leftLeg);
            model.rightPants.copyFrom(model.rightLeg);
            model.head.render(MODEL_SCALE);
            model.body.render(MODEL_SCALE);
            model.rightArm.render(MODEL_SCALE);
            model.leftArm.render(MODEL_SCALE);
            model.rightLeg.render(MODEL_SCALE);
            model.leftLeg.render(MODEL_SCALE);
            model.hat.render(MODEL_SCALE);
            model.jacket.render(MODEL_SCALE);
            model.leftSleeve.render(MODEL_SCALE);
            model.rightSleeve.render(MODEL_SCALE);
            model.leftPants.render(MODEL_SCALE);
            model.rightPants.render(MODEL_SCALE);
        });
    }
}
*/
//?}
