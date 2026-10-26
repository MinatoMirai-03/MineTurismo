package com.examplebett.compat;

//? if >=1.17 && <1.18 {
/*public final class GuiTextures {
    private GuiTextures() {
    }

    public static void blit(GuiGraphics g, net.minecraft.resources.ResourceLocation texture, int x, int y, float u, float v,
            int width, int height, int regionWidth, int regionHeight, int textureWidth, int textureHeight) {
        com.mojang.blaze3d.systems.RenderSystem.setShader(net.minecraft.client.renderer.GameRenderer::getPositionTexShader);
        com.mojang.blaze3d.systems.RenderSystem.setShaderTexture(0, texture);
        com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        com.mojang.blaze3d.systems.RenderSystem.enableBlend();
        com.mojang.blaze3d.systems.RenderSystem.defaultBlendFunc();
        net.minecraft.client.gui.GuiComponent.blit(g.pose(), x, y, width, height, u, v, regionWidth, regionHeight, textureWidth, textureHeight);
        com.mojang.blaze3d.systems.RenderSystem.disableBlend();
    }
}
*///?}
//? if >=1.16 && <1.17 {
/*public final class GuiTextures {
    private GuiTextures() {
    }

    public static void blit(GuiGraphics g, net.minecraft.resources.ResourceLocation texture, int x, int y, float u, float v,
            int width, int height, int regionWidth, int regionHeight, int textureWidth, int textureHeight) {
        net.minecraft.client.Minecraft.getInstance().getTextureManager().bind(texture);
        com.mojang.blaze3d.systems.RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        com.mojang.blaze3d.systems.RenderSystem.enableBlend();
        com.mojang.blaze3d.systems.RenderSystem.defaultBlendFunc();
        net.minecraft.client.gui.GuiComponent.blit(g.pose(), x, y, width, height, u, v, regionWidth, regionHeight, textureWidth, textureHeight);
        com.mojang.blaze3d.systems.RenderSystem.disableBlend();
    }
}
*///?}
//? if <1.15 {
/*public final class GuiTextures {
    private GuiTextures() {
    }

    public static void blit(GuiGraphics g, net.minecraft.resources.ResourceLocation texture, int x, int y, float u, float v,
            int width, int height, int regionWidth, int regionHeight, int textureWidth, int textureHeight) {
        net.minecraft.client.Minecraft.getInstance().getTextureManager().bind(texture);
        com.mojang.blaze3d.platform.GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        com.mojang.blaze3d.platform.GlStateManager.enableBlend();
        com.mojang.blaze3d.platform.GlStateManager.blendFuncSeparate(770, 771, 1, 0);
        net.minecraft.client.gui.GuiComponent.blit(x, y, width, height, u, v, regionWidth, regionHeight, textureWidth, textureHeight);
        com.mojang.blaze3d.platform.GlStateManager.disableBlend();
    }
}
*///?}
//? if >=1.15 && <1.16 {
/*public final class GuiTextures {
    private GuiTextures() {
    }

    public static void blit(GuiGraphics g, net.minecraft.resources.ResourceLocation texture, int x, int y, float u, float v,
            int width, int height, int regionWidth, int regionHeight, int textureWidth, int textureHeight) {
        net.minecraft.client.Minecraft.getInstance().getTextureManager().bind(texture);
        com.mojang.blaze3d.systems.RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        com.mojang.blaze3d.systems.RenderSystem.enableBlend();
        com.mojang.blaze3d.systems.RenderSystem.defaultBlendFunc();
        net.minecraft.client.gui.GuiComponent.blit(x, y, width, height, u, v, regionWidth, regionHeight, textureWidth, textureHeight);
        com.mojang.blaze3d.systems.RenderSystem.disableBlend();
    }
}
*///?}
