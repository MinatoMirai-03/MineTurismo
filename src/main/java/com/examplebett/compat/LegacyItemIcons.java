//? if >=1.18 {
//?}
//? if <1.18 && >=1.15 {
/*
package com.examplebett.compat;

public final class LegacyItemIcons {

    public static void render(com.mojang.blaze3d.vertex.PoseStack poseStack, net.minecraft.world.item.ItemStack stack, int x, int y) {
        net.minecraft.client.Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(stack, x, y);
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
        net.minecraft.client.Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(stack, x, y);
    }

    private LegacyItemIcons() {
    }
}
*/
//?}
