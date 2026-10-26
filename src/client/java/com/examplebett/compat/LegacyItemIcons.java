package com.examplebett.compat;

public final class LegacyItemIcons {

    //? if >=1.19.4 && <1.20 {
    /*public static void render(com.mojang.blaze3d.vertex.PoseStack poseStack, net.minecraft.world.item.ItemStack stack, int x, int y) {
        net.minecraft.client.Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(poseStack, stack, x, y);
    }*/
    //?}

    //? if <1.19.4 {
    /*public static void render(com.mojang.blaze3d.vertex.PoseStack poseStack, net.minecraft.world.item.ItemStack stack, int x, int y) {
        com.mojang.blaze3d.vertex.PoseStack modelView = com.mojang.blaze3d.systems.RenderSystem.getModelViewStack();
        modelView.pushPose();
        modelView.mulPoseMatrix(poseStack.last().pose());
        com.mojang.blaze3d.systems.RenderSystem.applyModelViewMatrix();
        net.minecraft.client.Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(stack, x, y);
        modelView.popPose();
        com.mojang.blaze3d.systems.RenderSystem.applyModelViewMatrix();
    }*/
    //?}

    private LegacyItemIcons() {
    }
}
