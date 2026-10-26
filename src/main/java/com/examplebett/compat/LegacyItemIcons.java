//? if >=1.18 {
//?}
//? if <1.18 && >=1.17 {
/*
package com.examplebett.compat;

public final class LegacyItemIcons {

    public static void render(com.mojang.blaze3d.vertex.PoseStack poseStack, net.minecraft.world.item.ItemStack stack, int x, int y) {
        com.mojang.blaze3d.vertex.PoseStack modelView = com.mojang.blaze3d.systems.RenderSystem.getModelViewStack();
        modelView.pushPose();
        modelView.mulPoseMatrix(poseStack.last().pose());
        com.mojang.blaze3d.systems.RenderSystem.applyModelViewMatrix();
        net.minecraft.client.Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(stack, x, y);
        modelView.popPose();
        com.mojang.blaze3d.systems.RenderSystem.applyModelViewMatrix();
    }

    private LegacyItemIcons() {
    }
}
*/
//?}
//? if <1.17 && >=1.15 {
/*
package com.examplebett.compat;

public final class LegacyItemIcons {

    public static void render(com.mojang.blaze3d.vertex.PoseStack poseStack, net.minecraft.world.item.ItemStack stack, int x, int y) {
        com.mojang.blaze3d.systems.RenderSystem.pushMatrix();
        com.mojang.blaze3d.systems.RenderSystem.multMatrix(poseStack.last().pose());
        net.minecraft.client.Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(stack, x, y);
        com.mojang.blaze3d.systems.RenderSystem.popMatrix();
    }

    private LegacyItemIcons() {
    }
}
*/
//?}
//? if <1.15 {
/*
package com.examplebett.compat;

public final class LegacyItemIcons {

    public static void render(com.examplebett.compat.v114.PoseStack poseStack, net.minecraft.world.item.ItemStack stack, int x, int y) {
        com.mojang.blaze3d.platform.Lighting.turnOnGui();
        net.minecraft.client.Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(stack, x, y);
        com.mojang.blaze3d.platform.Lighting.turnOff();
    }

    private LegacyItemIcons() {
    }
}
*/
//?}
