package com.examplebett.compat;

public final class LegacyItemIcons {

    //? if >=1.19.4 && <1.20 {
    /*public static void render(com.mojang.blaze3d.vertex.PoseStack poseStack, net.minecraft.world.item.ItemStack stack, int x, int y) {
        net.minecraft.client.Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(poseStack, stack, x, y);
    }*/
    //?}
    //? if <1.19.4 {
    /*public static void render(com.mojang.blaze3d.vertex.PoseStack poseStack, net.minecraft.world.item.ItemStack stack, int x, int y) {
        net.minecraft.client.Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(stack, x, y);
    }*/
    //?}

    private LegacyItemIcons() {
    }
}
